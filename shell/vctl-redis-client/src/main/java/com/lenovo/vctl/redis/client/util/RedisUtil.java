package com.lenovo.vctl.redis.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lenovo.vctl.redis.client.Redis;
import com.lenovo.vctl.redis.client.impl.RedisFactoryImpl;

public class RedisUtil {
    private static Log log = LogFactory.getLog(RedisUtil.class);
    private final static String DELIMITER = "_";
    private final static String REGION_DEFAULT = "default";
    private static Map<String, String> shortNameMap = new ConcurrentHashMap<String, String>();
    
    public static boolean exist(String regionName, String key) {
    	if (log.isDebugEnabled()) 
			log.debug("redisUtil exist, regionName : " + regionName + ", key : " + key);
    	
    	Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return false;
		}
		
		return redis.exists(key);
    }
    
	public static String get(String key) {
		return get(REGION_DEFAULT, key);
	}
	
	public static String get(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil get, regionName : " + regionName + ", key : " + key);
		
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.get(key);
	}
	
	public static Long incr(String key) {
		return incr(REGION_DEFAULT, key);
	}
	
	public static Long incr(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil incr, regionName : " + regionName + ", key : " + key);
		
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.incr(key, null);
	}
	
	public static List<String> mget(String regionName, String... keys) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil mget, regionName : " + regionName + ", keys : " + keys);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.mget(keys);
	}
	
	/**
	 * 通过正则表达式，获取全部值，不保证顺序
	 * @param regionName
	 * @param regexp
	 * @return
	 */
	public static List<String> getAll(String regionName, String regexp) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil getAll, regionName : " + regionName + ", regexp : " + regexp);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.getAll(regexp);
	}
	
	public static void set(String key, String value) {
		set(REGION_DEFAULT, key, value);
	}
	
	public static void set(String regionName, String key, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil set, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return;
		}
		
		redis.set(key, value);
	}
	
	/**
	 * 设置redis key值，并返回老值，如果老值不存在，返回null
	 * @param regionName
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getSet(String regionName, String key, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil getSet, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.getSet(key, value);
	}
	
	public static Long del(String key) {
		return del(REGION_DEFAULT, key);
	} 
	
	public static Long del(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil del, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.del(key);
	} 
	
	public static Long plus(String key, Integer inc) {
		return plus(REGION_DEFAULT, key, inc);
	}
	
	public static Long plus(String regionName, String key, Integer inc) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil plus, regionName : " + regionName + ", key : " + key + ", inc : " + inc);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.incr(key, Long.valueOf(inc));
	}
	
	public static Long plus(String key, Integer inc, Long expireAt) {
		return plus(REGION_DEFAULT, key, inc, expireAt);
	}
	
	public static Long plus(String regionName, String key, Integer inc, Long expireAt) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil plus, regionName : " + regionName + ", key : " + key + ", inc : " + inc + ", expireAt : " + expireAt);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("redis not exist.");
			return null;
		}
		
		Long result = redis.incr(key, Long.valueOf(inc));
		//第一次设置过期时间
		if (null != expireAt && result.equals(Long.valueOf(inc))) { 
			redis.expireAt(key, expireAt / 1000);
		}
		
		return result;
	}
	
	public static Long decs(String key) {
		return decs(REGION_DEFAULT, key);
	}
	
	public static Long decs(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil decs, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.decr(key, null);
	}
	

	public static Long expire(String key, Integer seconds) {
		return expire(REGION_DEFAULT, key, seconds);
	}
	
	public static Long expire(String regionName, String key, Integer seconds) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil expire, regionName : " + regionName + ", key : " + key + ", seconds : " + seconds);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("redis not exist.");
			return null;
		}
		
		return redis.expire(key, seconds);
	}

	public static String keyEncode(String regionName, String sKey) {
        return (new StringBuilder()).append(shortRegionName(regionName)).append(DELIMITER).append(sKey).toString();
	}
	
	public static List<String> keyEncode(String regionName, List<String> sKeys) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil keyEncode, regionName : " + regionName + ", sKeys : " + sKeys);
		List<String> result = new ArrayList<String>();
		if (CollectionUtils.isEmpty(sKeys)) return result;
		
		for(String sKey : sKeys) {
			result.add(keyEncode(regionName, sKey));
		}
		
		return result;
	}
	
	public static String[] keyEncode(String regionName, String... sKeys) {
		List<String> result = keyEncode(regionName, Arrays.asList(sKeys));
		return result.toArray(new String[0]);
	}
	
	public static String keyDecode(String regionName, String keyEncode) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil keyDecode, regionName : " + regionName + ", keyEncode : " + keyEncode);
        String shortName = shortRegionName(regionName);
        String sResult = keyEncode.substring(0, Math.min(shortName.length(), keyEncode.length()));
        if (shortName.equals(sResult)) {
            return keyEncode.substring(shortName.length() + DELIMITER.length());
        } else {
            log.error("don't decode keyEncode " + keyEncode);
            return null;
        }
	}
	
	
	public static String shortRegionName(String regionName) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil shortRegionName, regionName : " + regionName);
	    String shortName = shortNameMap.get(regionName);
	    if (shortName != null) {
	        return shortName;
	    } else {
	        int iPos = regionName.lastIndexOf(".");
	        if (iPos > 0) {
	            String sTemp = regionName.substring(iPos + 1);
	            shortNameMap.put(regionName, sTemp);
	            return sTemp;
	        }
	    }
	    return regionName;
	
	}
	
	//Hash
	public static Long hFieldPlus(String key, String field, Long inc) {
		return hFieldPlus(REGION_DEFAULT, key, field, inc);
	}
	
	public static Long hFieldPlus(String regionName, String key, String field, Long inc) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hFieldPlus, regionName : " + regionName + ", key : " + key + ", field : " + field + ", inc : " + inc);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		Long incBy = (null == inc) ? 1L : inc; 
		return redis.hIncrBy(key, field, incBy);
	}
	
	public static String hGet(String key, String field) {
		return hGet(REGION_DEFAULT, key, field);
	}
	
	public static String hGet(String regionName, String key, String field) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hGet, regionName : " + regionName + ", key : " + key + ", field : " + field);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.hGet(key, field);
	}
	
	public static Long hDel(String key, String field) {
		return hDel(REGION_DEFAULT, key, field);
	}
	
	public static List<String> hmGet(String regionName, String key, String... fields) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hGet, regionName : " + regionName + ", key : " + key + ", fields : " + fields);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.hmGet(key, fields);
	}
	
	public static Long hDel(String regionName, String key, String field) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hDel, regionName : " + regionName + ", key : " + key + ", field : " + field);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.hDel(key, field);
	}
	
	public static void hSet(String key, String field, String value) {
		hSet(REGION_DEFAULT, key, field, value);
	}
	
	public static void hSet(String regionName, String key, String field, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hSet, regionName : " + regionName + ", key : " + key + ", field : " + field + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return;
		}
		
		redis.hSet(key, field, value);
	}
	
	public static Set<String> hKeys(String key) {
		return hKeys(REGION_DEFAULT, key);
	}
	
	public static Set<String> hKeys(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hKeys, regionName : " + regionName + ", key : " + key);
		Set<String> result = new HashSet<String>();
		
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return result;
		}
		
		result = redis.hKeys(key);
		return result;
	}
	
	public static List<String> hVals(String key) {
		return hVals(REGION_DEFAULT, key);
	}
	
	public static List<String> hVals(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hVals, regionName : " + regionName + ", key : " + key);
		List<String> result = new ArrayList<String>();
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return result;
		}
		
		result = redis.hVals(key);
		return result;
	}
	
	public static Map<String, String> hGetAll(String key) {
		return hGetAll(REGION_DEFAULT, key);
	}
	
	public static Map<String, String> hGetAll(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil hGetAll, regionName : " + regionName + ", key : " + key);
		Map<String, String> result = new HashMap<String, String>();
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return result;
		}
		
		result = redis.hGetAll(key);
		return result;
	}
	
	//sorted sets
	public static void zPush(String regionName, String key, Long score, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zPush, regionName : " + regionName + ", key : " + key + ", score : " + score + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return;
		}
		
		redis.zAdd(key, score, value);
	}
	
	public static void zPush(String regionName, String key, Map<Double, String> values) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zPush, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return;
		}
		
		redis.zAdds(key, values);
	}
	
	public static void zDel(String regionName, String key, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zDel, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return;
		}
		
		redis.zRem(key, value);
	}
	
	public static Set<String> zRange(String regionName, String key, int start, int end) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zRange, regionName : " + regionName + ", key : " + key + ", start : " + start + ", end : " + end);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return new HashSet<String>();
		}
		
		return redis.zRange(key, start, end);
	}
	
	public static Set<String> zRangeByScore(String regionName, String key, Long min, Long max, int offset, int count) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zRangeByScore, regionName : " + regionName + ", key : " + key + ", min : " + min + ", max : " + max + ", offset : " + offset + ", count : " + count);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return new HashSet<String>();
		}
		
		return redis.zRangeByScore(key, min, max, offset, count);
	}
	
	public static Set<String> zRevRange(String regionName, String key, int start, int end) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zRevRange, regionName : " + regionName + ", key : " + key + ", start : " + start + ", end : " + end);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return new HashSet<String>();
		}
		
		return redis.zRevRange(key, start, end);
	}
	
	public static Set<String> zRevRangeByScore(String regionName, String key, Long max, Long min, int offset, int count) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zRevrangeByScore, regionName : " + regionName + ", key : " + key + ", min : " + min + ", max : " + max + ", offset : " + offset + ", count : " + count);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return new HashSet<String>();
		}
		
		return redis.zRevrangeByScore(key, max, min, offset, count);
	}
	
	public static Long zSize(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zSize, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return 0L;
		}
		
		return redis.zSize(key, Double.MIN_VALUE, Double.MAX_VALUE);
	}
	
	public static Long zRemRange(String regionName, String key, int start, int end) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zRemRange, regionName : " + regionName + ", key : " + key + ", start : " + start + ", end : " + end);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return 0L;
		}
		
		return redis.zRemRange(key, start, end);
	}
	
	public static Double zScore(String regionName, String key ,String member) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil zSize, regionName : " + regionName + ", key : " + key+", member:"+member);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return 0d;
		}
		
		return redis.zScore(key, member);
	}
	
	//List
	//取index位置string值，0开始
	public static String lIndex(String regionName, String key, int index) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lIndex, regionName : " + regionName + ", key : " + key + ", index : " + index);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lIndex(key, index);
	}
	
	//长度
	public static Long lLen(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lLen, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lLen(key);
	}
	
	//取第一个并从列表删除
	public static String lPop(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lPop, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lPop(key);
	}
	
	//添加到开头
	public static Long lPush(String regionName, String key, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lPush, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lPush(key, value);
	}
	
	//截取
	public static List<String> lRange(String regionName, String key, int start, int end) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lRange, regionName : " + regionName + ", key : " + key + ", start : " + start + ", end : " + end);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return new ArrayList<String>();
		}
		
		return redis.lRange(key, start, end);
	}
	
	//删除count个value（从左往右）
	public static Long lRem(String regionName, String key, int count, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lRem, regionName : " + regionName + ", key : " + key + ", count : " + count + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lRem(key, count, value);
	}
	
	//在index位置插入一个值
	public static String lSet(String regionName, String key, int index, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lSet, regionName : " + regionName + ", key : " + key + ", index : " + index + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lSet(key, index, value);
	}
	
	//截取(0开始，包含start，end)
	public static String lTrim(String regionName, String key, int start, int end) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil lTrim, regionName : " + regionName + ", key : " + key + ", start : " + start + ", end : " + end);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.lTrim(key, start, end);
	}
	
	//取出第一个
	public static String rPop(String regionName, String key) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil rPop, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.rPop(key);
	}
	
	//添加到末尾
	public static Long rPush(String regionName, String key, String value) {
		if (log.isDebugEnabled()) 
			log.debug("redisUtil rPush, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.rPush(key, value);
	}
	
	
	public static Long sAdd(String regionName, String key, String value){
		if (log.isDebugEnabled()) 
			log.debug("redisUtil sAdd, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.sAdd(key, value);
	}
	
	public static Set<String> sMembers(String regionName, String key){
		if (log.isDebugEnabled()) 
			log.debug("redisUtil sMembers, regionName : " + regionName + ", key : " + key );
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null == redis) {
			log.error("can not get redis. name : " + regionName);
			return null;
		}
		
		return redis.sMembers(key);
	}
	
	public static void sRem(String regionName, String key, String value){
		if (log.isDebugEnabled()) 
			log.debug("redisUtil sRem, regionName : " + regionName + ", key : " + key + ", value : " + value);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null != redis) {
			redis.sRem(key, value);
		}else{
			log.error("can not get redis. name : " + regionName);
		}
	}
	
	public static Long sCard(String regionName, String key){
		if (log.isDebugEnabled())
			log.debug("redisUtil sCard, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null != redis) {
			return redis.scard(key);
		}else{
			log.error("can not get redis. name : " + regionName);
			return 0l;
		}
	}
	
	public static String sRandmember(String regionName, String key){
		if (log.isDebugEnabled())
			log.debug("redisUtil sCard, regionName : " + regionName + ", key : " + key);
		Redis redis = RedisFactoryImpl.getInstance().getRedis(regionName);
		if (null != redis) {
			return redis.sRandmember(key);
		}else{
			log.error("can not get redis. name : " + regionName);
			return null;
		}
	}
	
}
