package com.lenovo.vctl.redis.client.config.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPool;

import com.lenovo.vctl.redis.client.config.RedisConfig;
import com.lenovo.vctl.redis.client.config.model.KeyPatternItem;
import com.lenovo.vctl.redis.client.config.model.RegionItem;

public class RedisConfigHelper {
	private static Logger logger = Logger.getLogger(RedisConfigHelper.class);

	/**
     * 给出Region对应的策略表达式Map
     * 
     * @param regionName
     * @return
     */
    public static Map<String, KeyPatternItem> getKeyKeyPatternItems(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            if (regionItem == null) {
                logger.error("don't find the " + regionName + " RegionItem in " + RedisConfig.CONFIG_FILE);
                return null;
            }
            return regionItem.getKeyPatternMap();
        } else {
            logger.error("region name must have a value");

        }
        return null;
    }

    /**
     * 给出Region对应的策略表达式Map
     * 
     * @param regionName
     * @return
     */
    public static Map<String, KeyPatternItem> getDefaultKeyKeyPatternItems() {
        RegionItem regionItem = RedisConfig.getInstance().getDefaultRegionItem();
        if (regionItem == null) {
            logger.error("cannot find the default RegionItem in " + RedisConfig.CONFIG_FILE);
            return null;
        }
        return regionItem.getKeyPatternMap();
    }

    /**
     * 通过名字给出RedisSource
     * 
     * @param name
     * @return
     */
    public static JedisPool getRedisSource(String name) {
        return StringUtils.isNotEmpty(name) ? RedisConfig.getInstance().getRedisSource(name) : null;
    }

    public static List<String> getSourceItemNames() {
    	return RedisConfig.getInstance().getSourceItemNames();
    }
    
    public static String getStrategyClassName(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            if (regionItem != null) {
                return regionItem.getStrategyClass();
            } else {
                logger.error("don't find regionName config: " + regionName);
                return null;
            }
        } else {
            logger.error("regionName is null or empty");
            return null;
        }
    }

    /**
     * 
     * @param regionName
     * @return
     */
    public static String getListenerClass(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            if (regionItem != null) {
                return regionItem.getListenerClass();
            } else {
                logger.error("don't find regionName config: " + regionName);
                return null;
            }
        } else {
            logger.error("regionName is null or empty");
            return null;
        }
    }

    /**
     * 
     * @param regionName
     * @return
     */
    public static int getListLimitLen(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            if (regionItem != null) {
                return regionItem.getLimitLen();
            } else {
                logger.error("don't find regionName config: " + regionName);
                return 0;
            }
        } else {
            logger.error("regionName is null or empty");
            return 0;
        }
    }    
    /**
     * 检查一个Region是否在配置文件中存在
     * 
     * @param regionName
     * @return
     */
    public static boolean isExistRegion(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            return regionItem == null ? false : true;
        }
        return false;
    }

    /**
     * 检查一个Region是否支持本地缓存
     * 
     * @param regionName
     * @return
     */
    public static boolean isLocalCache(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            return regionItem == null ? false : regionItem.isLocalCache();
        }
        return false;
    }

    /**
     * 检查一个Region是否支持远程缓存
     * 
     * @param regionName
     * @return
     */
    public static boolean isRemoteCache(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            return regionItem == null ? false : regionItem.isRemoteCache();
        }
        return false;
    }    
    /**
     * 检查一个Region是否支持系列化类消息
     * 
     * @param regionName
     * @return
     */
    public static boolean isHasClassInfo(String regionName) {
        if (StringUtils.isNotEmpty(regionName)) {
            RegionItem regionItem = RedisConfig.getInstance().getRegionItem(regionName);
            return regionItem == null ? true : regionItem.isHasClassInfo();
        }
        return true;
    }
	
	
}
