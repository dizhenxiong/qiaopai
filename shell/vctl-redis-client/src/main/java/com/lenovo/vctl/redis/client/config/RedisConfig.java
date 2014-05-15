package com.lenovo.vctl.redis.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import pl.quaternion.SentinelBasedJedisPoolWrapper;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.lenovo.vctl.redis.client.config.model.DatasourceItem;
import com.lenovo.vctl.redis.client.config.model.KeyPatternItem;
import com.lenovo.vctl.redis.client.config.model.RegionItem;
import com.lenovo.vctl.redis.client.config.model.SentinelItem;

public class RedisConfig {
	private static Logger logger = Logger.getLogger(RedisConfig.class);
	
    public static String CONFIG_FILE = "/redis_client.xml";
    public static String DASNAME_SEPARATOR = ","; // Datasource 名字的分隔符
    private static RedisConfig config;
    
    private Map<String, DatasourceItem> redisItemMap = new HashMap<String, DatasourceItem>();
    private Map<String, RegionItem> regionItemMap = new HashMap<String, RegionItem>();
    private List<String> redisSourceNames = new ArrayList<String>();
    private Map<String, SentinelBasedJedisPoolWrapper> redisSourceMap = new HashMap<String, SentinelBasedJedisPoolWrapper>();
    private RegionItem defaultRegionItem = null;

    private RedisConfig() {
        try {
            this.init(getClass().getResourceAsStream(CONFIG_FILE));
            this.initDefaultRegion(getClass().getResourceAsStream(CONFIG_FILE));
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace(System.out);
            } else {
                logger.error("init config file " + CONFIG_FILE + " error: " + e.getMessage());
            }
        }
    }

    public static RedisConfig getInstance() {
        if (config == null) {
            synchronized (RedisConfig.class) {
                if (config == null) {
                    config = new RedisConfig();
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            config.close();
                        }
                    });
                }
            }
        }

        return config;
    }

    private void init(InputStream configFile) {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("root", ArrayList.class);

        // 初始化redis source配置
        digester.addObjectCreate("root/cache/", ArrayList.class.getName());
        digester.addObjectCreate("root/cache/datasource", DatasourceItem.class);
        digester.addSetProperties("root/cache/datasource");
        digester.addSetNext("root/cache/datasource", "add");
        
        digester.addObjectCreate("root/cache/datasource/sentinel", SentinelItem.class);
        digester.addSetProperties("root/cache/datasource/sentinel");
        digester.addSetNext("root/cache/datasource/sentinel", "addSentinelItem");

        digester.addSetNext("root/cache/", "add");

        // 初始化region配置
        digester.addObjectCreate("root/regions/", ArrayList.class.getName());
        digester.addObjectCreate("root/regions/region", RegionItem.class);
        digester.addSetProperties("root/regions/region");
        digester.addSetNext("root/regions/region", "add");

        digester.addObjectCreate("root/regions/region/keyPattern", KeyPatternItem.class);
        digester.addSetProperties("root/regions/region/keyPattern");
        digester.addSetNext("root/regions/region/keyPattern", "addKeyPatternItem");

        digester.addSetNext("root/regions/", "add");
        
        try {
            Object root = digester.parse(configFile);
            if (root != null && root instanceof ArrayList) {
                List<List> list = (List) root;
                if (CollectionUtils.isEmpty(list)) {
                    return;
                }
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    List arrayList = (List) iterator.next();
                    if (CollectionUtils.isNotEmpty(arrayList)) {
                        for (Iterator itemIterator = arrayList.iterator(); itemIterator.hasNext();) {
                            Object item = itemIterator.next();
                            if (item != null) {
                                if (item instanceof DatasourceItem) {
                                    addDatasourceItem((DatasourceItem) item);
                                    continue;
                                }
                                if (item instanceof RegionItem) {
                                    addRegionItem((RegionItem) item);
                                }
                            }
                        }
                    }
                }
            }
            
            //init redis pool
            initRedisPools();
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace(System.err);
            } else {
                logger.error("init error: " + e.getMessage());
            }
        } catch (SAXException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace(System.err);
            } else {
                logger.error("init parse fail : " + e.getMessage());
            }
        }
    }

    private void initDefaultRegion(InputStream configFile) {
        Digester digester = new Digester();
        digester.setValidating(false);
        
        digester.addObjectCreate("root/default/region", RegionItem.class);
        digester.addSetProperties("root/default/region");

        digester.addObjectCreate("root/default/region/keyPattern", KeyPatternItem.class);
        digester.addSetProperties("root/default/region/keyPattern");
        digester.addSetNext("root/default/region/keyPattern", "addKeyPatternItem");

        try {
            Object root = digester.parse(configFile);
                if (root instanceof RegionItem) {
                    this.defaultRegionItem = (RegionItem) root;
                }

        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace(System.err);
            } else {
                logger.error("initDefaultRegion error: " + e.getMessage());
            }
        } catch (SAXException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace(System.err);
            } else {
                logger.error("initDefaultRegion parse fail : " + e.getMessage());
            }
        }
    }

    //Init redis source
    private void initRedisPools() {
    	if (MapUtils.isEmpty(redisItemMap)) return;

    	for(Entry<String, DatasourceItem> source : redisItemMap.entrySet()) {
    		DatasourceItem datasourceItem = source.getValue();
    		
	        JedisPoolConfig cf = new JedisPoolConfig();
			cf.setMaxActive(datasourceItem.getMaxActive());
			cf.setMaxIdle(datasourceItem.getMaxIdle());
			cf.setMaxWait(datasourceItem.getMaxWait());
			
			int database = Protocol.DEFAULT_DATABASE;
			int timeOut = datasourceItem.getTimeout();
			if (datasourceItem.getTimeout() <= 0)
				timeOut = Protocol.DEFAULT_TIMEOUT;
			
			SentinelBasedJedisPoolWrapper pool = null;
			if (MapUtils.isEmpty(datasourceItem.getSentinelMap())) {
				pool = new SentinelBasedJedisPoolWrapper(cf, timeOut, null, database, datasourceItem.getServer(), datasourceItem.getPort());
			}
			else {
				pool = new SentinelBasedJedisPoolWrapper(cf, timeOut, null, database, datasourceItem.getName(), datasourceItem.getSentinelMap().keySet());
			}
			
    		redisSourceMap.put(datasourceItem.getName(), pool);
    	}
    }
    
    /**
     * 
     * @param achememcachedItemdItem
     */
    private void addDatasourceItem(DatasourceItem datasourceItem) {
        if (datasourceItem != null && StringUtils.isNotBlank(datasourceItem.getName())) {
            if (!redisItemMap.containsKey(datasourceItem.getName())) {
            	redisItemMap.put(datasourceItem.getName(), datasourceItem);
                
        		
        		redisSourceNames.add(datasourceItem.getName());
                
            } else {
                logger.error("same name <" + datasourceItem.getName() + "> memcacheditem have exist!");
            }

        }
    }

    /**
     * 
     * @param addRegionItem
     */
    private void addRegionItem(RegionItem regionItem) {
        if (regionItem != null && StringUtils.isNotBlank(regionItem.getName())) {
            if (!regionItemMap.containsKey(regionItem.getName())) {
                regionItemMap.put(regionItem.getName(), regionItem);
            } else {
                logger.error("same name <" + regionItem.getName() + "> RegionItem have exist!");
            }

        }
    }
    
    /**
     * 
     * @param regionName
     * @return
     */
    public RegionItem getRegionItem(String regionName) {
        return this.regionItemMap.get(regionName);
    }
    
    /**
     * 
     * @param redisItemName
     * @return
     */
    public DatasourceItem getRedisItem(String redisItemName) {
        return this.redisItemMap.get(redisItemName);
    }

    public JedisPool getRedisSource(String name) {
    	SentinelBasedJedisPoolWrapper poolWrapper = redisSourceMap.get(name);
    	if (null == poolWrapper) return null;
    	
    	return poolWrapper.getJedisPool();
    }
    
    public Map<String, SentinelBasedJedisPoolWrapper> getRedisSourceMap() {
		return redisSourceMap;
	}

	public void setRedisSourceMap(
			Map<String, SentinelBasedJedisPoolWrapper> redisSourceMap) {
		this.redisSourceMap = redisSourceMap;
	}

	public List<String> getSourceItemNames() {
    	return redisSourceNames;
    }
    
    public void close() {
        if (MapUtils.isNotEmpty(redisSourceMap)) {
            Collection<SentinelBasedJedisPoolWrapper> c = redisSourceMap.values();
            for (Iterator<SentinelBasedJedisPoolWrapper> iterator = c.iterator(); iterator.hasNext();) {
            	SentinelBasedJedisPoolWrapper source = iterator.next();
                if (source != null) {
                    try {
                    	source.destroy();
                    } catch (Exception e) {
                        ;
                    }
                }
            }
        }
    }

    public RegionItem getDefaultRegionItem() {
        return defaultRegionItem;
    }
    
}
