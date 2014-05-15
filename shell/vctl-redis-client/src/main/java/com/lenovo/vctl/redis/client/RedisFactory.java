package com.lenovo.vctl.redis.client;

public interface RedisFactory {
	public Redis getRedis(String name);
	
	public void removeRedis(String name);
}
