package com.lenovo.vctl.redis.client.route.strategy;

public interface HashFunction {
	public Long hash(Object key);
}
