package com.lenovo.vctl.cloudstorage.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.lenovo.vctl.cloudstorage.ftp.extend.UploadFtp;
import com.lenovo.vctl.cloudstorage.ftp.pool.FTPClientPoolableFactory;


public class FtpConfig {
	private static String CONFIG_FILE = "ftpserver.xml";

	private static Logger logger = Logger.getLogger(FtpConfig.class);
	private static final Map<String, FromItem> fromItemMap = new HashMap<String, FromItem>();
	private FromItem defaultFromItem = null;
	private static final int defaultMaxActive = 8;
	private static final int defaultMaxIdle = 1;
	private static final long defaultMaxWait = GenericObjectPool.DEFAULT_MAX_WAIT; //无限的等待时间
	private static final long defaultMinEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
	private static final Map<String, GenericObjectPool> ftpPools = new HashMap<String, GenericObjectPool>(); 
	private static final FtpConfig config = new FtpConfig();
	

	
	public static FtpConfig getInstance() {
		return config;
	}

	private FtpConfig() {
		try {
			this.init(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE));
			// this.datasourceAssign();
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace(System.out);
			} else {
				logger.error("init config file " + CONFIG_FILE + " error: " + e.getMessage());
			}
		}
	}

	/**
	 * 解析Object的配置
	 * 
	 * @param groupConfigFile
	 */
	private void init(InputStream groupConfigFile) {
		Digester digester = new Digester();
		digester.setValidating(false);

		// 对象
		digester.addObjectCreate("root", ArrayList.class);

		digester.addObjectCreate("root/default", FromItem.class);
		digester.addSetNext("root/default", "add");
		digester.addSetProperties("root/default");

		digester.addObjectCreate("root/default/ftp", FtpItem.class);
		digester.addSetNext("root/default/ftp", "addFtpItem");
		digester.addSetProperties("root/default/ftp");

		digester.addObjectCreate("root/from", FromItem.class);
		digester.addSetNext("root/from", "add");
		digester.addSetProperties("root/from");

		digester.addObjectCreate("root/from/ftp", FtpItem.class);
		digester.addSetNext("root/from/ftp", "addFtpItem");
		digester.addSetProperties("root/from/ftp");

		try {
			Object objectList = digester.parse(groupConfigFile);
			if (objectList != null && objectList instanceof ArrayList) {
				List fromItems = (List) objectList;
				for (Iterator iterator = fromItems.iterator(); iterator.hasNext();) {
					FromItem fromItem = (FromItem) iterator.next();
					if (fromItem != null) {
						if (StringUtils.isEmpty(fromItem.getName())) {
							logger.error("From tag must have value of name property");
							continue;
						}
						if (!StringUtils.equalsIgnoreCase("default", fromItem.getName())) {
							if (fromItemMap.containsKey(fromItem.getName())) {
								logger.error("Duplicate " + fromItem.getName() + " From Tag - This From may already exist more than two in " + CONFIG_FILE
										+ ". Please check");
							} else {
								fromItemMap.put(fromItem.getName(), fromItem);
								GenericObjectPool<UploadFtp> pool = new GenericObjectPool<UploadFtp>(new FTPClientPoolableFactory(fromItem));
								pool.setMaxActive(fromItem.getMaxActive() == -1? defaultMaxActive: fromItem.getMaxActive());
								logger.info("fromItem.getMaxActive() = " + fromItem.getMaxActive());
								pool.setMaxIdle(fromItem.getMaxIdle() == -1 ? defaultMaxIdle : fromItem.getMaxIdle());
								pool.setMaxWait(fromItem.getMaxWait() == -1 ? defaultMaxWait : fromItem.getMaxWait());
								pool.setMinEvictableIdleTimeMillis(fromItem.getMinEvictableIdleTimeMillis() == -1? defaultMinEvictableIdleTimeMillis : fromItem.getMinEvictableIdleTimeMillis());
								ftpPools.put(fromItem.getName(), pool);
							}
						} else {
							if (defaultFromItem != null) {
								logger.error("Duplicate default From Tag - This From may already exist more than two in " + CONFIG_FILE + ". Please check");
							} else {
								defaultFromItem = fromItem;
								GenericObjectPool<UploadFtp> pool = new GenericObjectPool<UploadFtp>(new FTPClientPoolableFactory(defaultFromItem));
								pool.setMaxActive(defaultFromItem.getMaxActive() == -1? defaultMaxActive: defaultFromItem.getMaxActive());
								pool.setMaxIdle(defaultFromItem.getMaxIdle() == -1 ? defaultMaxIdle : defaultFromItem.getMaxIdle());
								pool.setMaxWait(defaultFromItem.getMaxWait() == -1 ? defaultMaxWait : defaultFromItem.getMaxWait());
								pool.setMinEvictableIdleTimeMillis(defaultFromItem.getMinEvictableIdleTimeMillis() == -1? defaultMinEvictableIdleTimeMillis : defaultFromItem.getMinEvictableIdleTimeMillis());
								ftpPools.put("default", pool);
							}
						}
					}
				}
			}
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

	/**
	 * return FromItem by fromItemName, if fromItemName not exist!, return
	 * defaultFromItem
	 * 
	 * @param fromItemName
	 * @return
	 */
	public FromItem getFromItem(String fromItemName) {
		if (StringUtils.isEmpty(fromItemName)) {
			return defaultFromItem;
		}
		FromItem fromItem = fromItemMap.get(fromItemName);
		return fromItem == null ? defaultFromItem : fromItem;
	}

	
	public GenericObjectPool getObjectPool(String fromItemName){
		GenericObjectPool defaultPool = ftpPools.get("default");
		if (StringUtils.isEmpty(fromItemName)) {
			return defaultPool;
		}
		GenericObjectPool pool = ftpPools.get(fromItemName);
		if(pool != null)return pool;
		else return defaultPool;
	}
	
	/**
	 * 
	 * @param fromItemName
	 * @return
	 */
	public boolean isExist(String fromItemName) {
		return StringUtils.isBlank(fromItemName) ? false : fromItemMap.containsKey(fromItemName);
	}

	public static void main(String[] args) {
		FromItem fromItem = FtpConfig.getInstance().getFromItem(null);
	}

	/**
	 * return FromItem by fromItemName, if fromItemName not exist!, return
	 * defaultFromItem
	 * 
	 * @param fromItemName
	 * @return
	 */
	public String getAlias(String from) {
		String alias;
		if (StringUtils.isEmpty(from)) {
			alias = defaultFromItem.getAlias();
		} else {
			FromItem fromItem = fromItemMap.get(from);
			fromItem = fromItem == null ? defaultFromItem : fromItem;
			alias = fromItem == null ? "" : fromItem.getAlias();
		}
		return alias == null ? from : alias;
	}

	public Map<String, FromItem> getFromItemMap() {
		return this.fromItemMap;
	}

	public FromItem getDefaultFromItem() {
		return defaultFromItem;
	}

}
