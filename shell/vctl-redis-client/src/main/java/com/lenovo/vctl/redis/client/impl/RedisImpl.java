package com.lenovo.vctl.redis.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.lenovo.vctl.redis.client.Redis;
import com.lenovo.vctl.redis.client.config.helper.RedisConfigHelper;
import com.lenovo.vctl.redis.client.exception.StrategyException;
import com.lenovo.vctl.redis.client.route.RoutingService;
import com.lenovo.vctl.redis.client.route.strategy.IStrategy;
import com.lenovo.vctl.redis.client.util.RedisUtil;

public class RedisImpl implements Redis {

    private static Logger log = Logger.getLogger(RedisImpl.class);
    
    private String region;
    private IStrategy strategy;
	private JedisPool defaultPool;
    
	public RedisImpl(JedisPool pool) {
		super();
		this.defaultPool = pool;
	}

	public RedisImpl() {
		super();
	}

	public RedisImpl(String region, RoutingService instance, JedisPool defaultPool) {
		this.defaultPool = defaultPool;
		this.region = region;
		try {
			this.strategy = instance.getRoutingStrategy(region);
		} catch (StrategyException e) {
			e.printStackTrace();
			log.error("getRoutingStrategy error. region : " + region);
		}
	}

	public Boolean exists(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return false;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.exists(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String get(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.get(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}

	public List<String> mget(String... keys) {
		boolean isBroken = false;
		List<String> result = new ArrayList<String>();
		
		Map<JedisPool, List<String>> pools = getPools(keys);
		if (MapUtils.isEmpty(pools)) return result;
		
		Map<String, String> resultMap = new HashMap<String, String>();
		for(Entry<JedisPool, List<String>> entry : pools.entrySet()) {
			if (null == entry || null == entry.getKey() || CollectionUtils.isEmpty(entry.getValue())) continue;
			
			JedisPool pool = entry.getKey();
			Jedis jedis = pool.getResource();
			try {
				List<String> curKeys = entry.getValue();
				List<String> curValues = jedis.mget(RedisUtil.keyEncode(region, curKeys).toArray(new String[0]));
				for(int i=0; i<curKeys.size(); i++){
					resultMap.put(curKeys.get(i), curValues.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
				pool.returnBrokenResource(jedis);
				isBroken = true;
				return null;
			} finally {
				if (!isBroken) {
					pool.returnResource(jedis);
				}
			}
		}
		
		//Convert map to list(keep order)
		for (String key : keys) {
			result.add(resultMap.get(key));
		}
		
		return result;
	}
	
	//get all value of regexp key from all redis server
	public List<String> getAll(String regexp) {
		boolean isBroken = false;
		List<String> values = new ArrayList<String>();
		
		List<JedisPool> pools = getRegionAllPools();
		if (CollectionUtils.isEmpty(pools)) return values;
		
		//get all keys from each pool
		for (JedisPool pool : pools) {
			Jedis jedis = pool.getResource();
			try {
				Set<String> keys = jedis.keys(RedisUtil.keyEncode(region, regexp));
				if (CollectionUtils.isEmpty(keys)) continue;
				
				//get all value of keys
				values.addAll(jedis.mget(keys.toArray(new String[0])));
			} catch (Exception e) {
				e.printStackTrace();
				pool.returnBrokenResource(jedis);
				isBroken = true;
				return null;
			} finally {
				if (!isBroken) {
					pool.returnResource(jedis);
				}
			}
		}
		
		return values;
	}

	public void set(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			jedis.set(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String getSet(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.getSet(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long expire(String key, Integer time) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.expire(RedisUtil.keyEncode(region, key), time);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long expireAt(String key, Long time) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.expireAt(RedisUtil.keyEncode(region, key), time);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}

	public Long incr(String key, Long inc) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			if (null == inc) {
				return jedis.incr(RedisUtil.keyEncode(region, key));
			}
			else {
				return jedis.incrBy(RedisUtil.keyEncode(region, key), inc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}

	public Long decr(String key, Long decr) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			if (null == decr) {
				return jedis.decr(RedisUtil.keyEncode(region, key));
			}
			else {
				return jedis.decrBy(RedisUtil.keyEncode(region, key), decr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long del(String ...keys) {
		Map<JedisPool, List<String>> pools = getPools(keys);
		if (MapUtils.isEmpty(pools)) return null;
		
		boolean isBroken = false;
		Long result = null;
		for(Entry<JedisPool, List<String>> entry : pools.entrySet()) {
			if (null == entry || null == entry.getKey() || CollectionUtils.isEmpty(entry.getValue())) continue;
			
			JedisPool pool = entry.getKey();
			Jedis jedis = pool.getResource();
			try {
				result = jedis.del((String[]) RedisUtil.keyEncode(region, entry.getValue()).toArray(new String[0]));
			} catch (Exception e) {
				e.printStackTrace();
				pool.returnBrokenResource(jedis);
				isBroken = true;
				return null;
			} finally {
				if (!isBroken) {
					pool.returnResource(jedis);
				}
			}
		}
		
		return result;
	}
	
	//Keys
	public Set<String> keys(String keyPattern) {
		JedisPool pool = getPool(keyPattern);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.keys(RedisUtil.keyEncode(region, keyPattern));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String type(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.type(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//Set
	public Long sAdd(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.sadd(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long scard(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.scard(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String sPop(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.spop(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}

	public Set<String> sMembers(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.smembers(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public void sRem(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			jedis.srem(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public boolean sIsMember(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return false;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.sismember(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return false;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Set<String> sUnion(String ...keys) {
		boolean isBroken = false;
		Set<String> result = new HashSet<String>();
		
		Map<JedisPool, List<String>> pools = getPools(keys);
		if (MapUtils.isEmpty(pools)) return result;
		
		for(Entry<JedisPool, List<String>> entry : pools.entrySet()) {
			if (null == entry || null == entry.getKey() || CollectionUtils.isEmpty(entry.getValue())) continue;
			
			JedisPool pool = entry.getKey();
			Jedis jedis = pool.getResource();
			try {
				result = jedis.sunion((String[]) RedisUtil.keyEncode(region, entry.getValue()).toArray(new String[0]));
			} catch (Exception e) {
				e.printStackTrace();
				pool.returnBrokenResource(jedis);
				isBroken = true;
				return null;
			} finally {
				if (!isBroken) {
					pool.returnResource(jedis);
				}
			}
		}
		
		return result;
	}
	
	//sorted Set
	public Long zAdd(String key, double score, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zadd(RedisUtil.keyEncode(region, key), score, value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//批量添加，values 必须有序
	public Long zAdds(String key, Map<Double, String> values) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zadd(RedisUtil.keyEncode(region, key), values);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long zcard(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zcard(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Set<String> zRange(String key, int start, int end) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrange(RedisUtil.keyEncode(region, key), start, end);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Set<String> zRangeByScore(final String key, final double min,
		    final double max, final int offset, final int count) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrangeByScore(RedisUtil.keyEncode(region, key),min, max, offset, count);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long zRank(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrank(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long zRem(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrem(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long zRemRange(String key, int start, int end) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zremrangeByRank(RedisUtil.keyEncode(region, key), start, end);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Set<String> zRevRange(String key, int start, int end) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrevrange(RedisUtil.keyEncode(region, key), start, end);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Set<String> zRevrangeByScore(final String key, final double max,
		    final double min, final int offset, final int count) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrevrangeByScore(RedisUtil.keyEncode(region, key), max, min, offset, count);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Double zScore(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zscore(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//只能是单个redis情况下使用
	public Long zUnionStore(String dstkey, String ...keys) {
		JedisPool pool = getPool(dstkey);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			List<String> sKeys = new ArrayList<String>();
			CollectionUtils.addAll(sKeys, keys);
			return jedis.zunionstore(RedisUtil.keyEncode(region, dstkey), ((String[])RedisUtil.keyEncode(region, sKeys).toArray(new String[0])));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long zSize(String key, Double min, Double max) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.zcount(RedisUtil.keyEncode(region, key), min, max);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//List
	public String lIndex(String key, int index) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.lindex(RedisUtil.keyEncode(region, key), index);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long lLen(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.llen(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String lPop(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.lpop(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long lPush(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.lpush(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public List<String> lRange(String key, int start, int end) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.lrange(RedisUtil.keyEncode(region, key), start, end);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long lRem(String key, int count, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.lrem(RedisUtil.keyEncode(region, key), count, value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String lSet(String key, int index, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.lset(RedisUtil.keyEncode(region, key), index, value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String lTrim(String key, int start, int end) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.ltrim(RedisUtil.keyEncode(region, key), start, end);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String rPop(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.rpop(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//只能是单个redis情况下使用
	public String rPopLPush(String srckey, String dstkey) {
		JedisPool pool = getPool(srckey);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.rpoplpush(RedisUtil.keyEncode(region, srckey), RedisUtil.keyEncode(region, dstkey));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long rPush(String key, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.rpush(RedisUtil.keyEncode(region, key), value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//Map
	public Long hDel(String key, String field) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hdel(RedisUtil.keyEncode(region, key), field);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public boolean hExists(String key, String field) {
		JedisPool pool = getPool(key);
		if (null == pool) return false;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hexists(RedisUtil.keyEncode(region, key), field);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return false;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String hGet(String key, String field) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hget(RedisUtil.keyEncode(region, key), field);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public List<String> hmGet(String key, String... fields) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hmget(RedisUtil.keyEncode(region, key), fields);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Map<String, String> hGetAll(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hgetAll(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long hIncrBy(String key, String field, Long value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hincrBy(RedisUtil.keyEncode(region, key), field, value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Set<String> hKeys(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hkeys(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long hLen(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hlen(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public List<String> hMGet(String key, String... fields) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hmget(RedisUtil.keyEncode(region, key), fields);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public String hMSet(String key, Map<String, String> kvalues) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hmset(RedisUtil.keyEncode(region, key), kvalues);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public Long hSet(String key, String field, String value) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hset(RedisUtil.keyEncode(region, key), field, value);
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	public List<String> hVals(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.hvals(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	//get pool according to strategy
	private JedisPool getPool(String key) {
		JedisPool pool = this.defaultPool;
		
		if (null == this.strategy) {
			return pool;
		}
		
		if (StringUtils.isEmpty(key)) {
			return pool;
		}
		
		try {
			Object obj = this.strategy.playStrategy(this.region, key);
			if (null == obj) {
				log.error("get JedisPool failed. region : "+ region +", strategy : " + strategy + " key : " + key);
				return pool;
			}
			
			String poolName = (String) obj;
			pool = RedisConfigHelper.getRedisSource(poolName);
			if (null == pool) {
				pool = this.defaultPool;
			}
		} catch (StrategyException e) {
			e.printStackTrace();
		}
		
		return pool;
	}
	
	/**
	 * 获取某个region下所有的Redis连接池
	 * @return
	 */
	private List<JedisPool> getRegionAllPools() {
		List<JedisPool> pools = new ArrayList<JedisPool>();
		if (null == this.strategy) {
			return pools;
		}
		if (StringUtils.isEmpty(this.region)) {
			return pools;
		}
		
		try {
			List<String> dsNames = this.strategy.getAllResouceName(this.region);
			if (CollectionUtils.isNotEmpty(dsNames)) {
				for(String dsName : dsNames) {
					JedisPool pool = RedisConfigHelper.getRedisSource(dsName);
					if (null == pool) continue;
					
					pools.add(pool);
				}
			}
		} catch (StrategyException e) {
			e.printStackTrace();
		}
		
		return pools;
	}
	
	private Map<JedisPool, List<String>> getPools(String ...keys) {
		Map<JedisPool, List<String>> pools = new HashMap<JedisPool, List<String>>();
		for(String key : keys) {
			JedisPool pool = getPool(key);
			if (null == pool) continue;
			
			List<String> ks = pools.get(pool);
			if (CollectionUtils.isEmpty(ks)) {
				ks = new ArrayList<String>();
				pools.put(pool, ks);
			}
			ks.add(key);
		}
		
		return pools;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public IStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}

	public JedisPool getDefaultPool() {
		return defaultPool;
	}

	public void setDefaultPool(JedisPool defaultPool) {
		this.defaultPool = defaultPool;
	}

	@Override
	public String sRandmember(String key) {
		JedisPool pool = getPool(key);
		if (null == pool) return null;
		
		boolean isBroken = false;
		Jedis jedis = pool.getResource();
		try {
			return jedis.srandmember(RedisUtil.keyEncode(region, key));
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			isBroken = true;
			return null;
		} finally {
			if (!isBroken) {
				pool.returnResource(jedis);
			}
		}
	}
	
	
}
