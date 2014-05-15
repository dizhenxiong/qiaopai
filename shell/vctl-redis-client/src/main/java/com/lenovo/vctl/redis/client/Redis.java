package com.lenovo.vctl.redis.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Redis {
	public Boolean exists(String key);
	public String get(String key);
	//设置key值并返回旧值
	public String getSet(String key, String value);
	public List<String> mget(String... keys);
	public List<String> getAll(String regexp);
	public void set(String key, String value);
	//只能是单个redis情况下使用
	public Long del(String ...keys);
	public Long incr(String key, Long inc);
	public Long decr(String key, Long dec);
	
	public Long expire(String key, Integer time) ;
	public Long expireAt(String key, Long time) ;
	
	//Key
	public Set<String> keys(String keyPattern);
	public String type(String key);
	
	//Set
	public Long sAdd(String key, String value) ;
	
	public Long scard(String key) ;
	
	public String sPop(String key) ;

	public Set<String> sMembers(String key) ;
	
	public void sRem(String key, String value) ;
	
	public boolean sIsMember(String key, String value) ;
	
	//只能是单个redis情况下使用
	public Set<String> sUnion(String ...keys) ;
	
	//sorted Set
	public Long zAdd(String key, double score, String value) ;
	
	public Long zAdds(String key, Map<Double, String> values) ;
	
	public Long zcard(String key) ;
	
	public Set<String> zRange(String key, int start, int end) ;
	
	public Set<String> zRangeByScore(final String key, final double min,
		    final double max, final int offset, final int count) ;
	
	public Long zRank(String key, String value) ;
	
	public Long zRem(String key, String value) ;
	
	public Long zRemRange(String key, int start, int end) ;
	public Set<String> zRevrangeByScore(final String key, final double max,
		    final double min, final int offset, final int count);
	public Set<String> zRevRange(String key, int start, int end) ;
	
	public Double zScore(String key, String value) ;
	
	public Long zSize(String key, Double min, Double max);
	
	//只能是单个redis情况下使用
	public Long zUnionStore(String dstkey, String ...keys) ;
	
	//List
	public String lIndex(String key, int index) ;
	
	public Long lLen(String key) ;
	
	public String lPop(String key) ;
	
	public Long lPush(String key, String value) ;
	
	public List<String> lRange(String key, int start, int end) ;
	
	public Long lRem(String key, int count, String value) ;
	
	public String lSet(String key, int index, String value) ;
	
	public String lTrim(String key, int start, int end) ;
	
	public String rPop(String key) ;
	
	//只能是单个redis情况下使用
	public String rPopLPush(String srckey, String dstkey) ;
	
	public Long rPush(String key, String value) ;
	
	//Map
	public Long hDel(String key, String field) ;
	
	public boolean hExists(String key, String field) ;
	
	public String hGet(String key, String field) ;
	
	public List<String> hmGet(String key, String... fields);
	
	public Map<String, String> hGetAll(String key) ;
	
	public Long hIncrBy(String key, String field, Long value) ;
	
	public Set<String> hKeys(String key) ;
	
	public Long hLen(String key) ;
	
	public List<String> hMGet(String key, String... fields) ;
	
	public String hMSet(String key, Map<String, String> kvalues) ;
	
	public Long hSet(String key, String field, String value) ;
	
	public List<String> hVals(String key) ;
	
	public String sRandmember(String key);
}
