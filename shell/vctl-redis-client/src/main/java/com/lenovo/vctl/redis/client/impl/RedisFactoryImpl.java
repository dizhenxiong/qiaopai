package com.lenovo.vctl.redis.client.impl;
import redis.clients.jedis.JedisPool;

import com.lenovo.vctl.redis.client.Redis;
import com.lenovo.vctl.redis.client.RedisFactory;
import com.lenovo.vctl.redis.client.config.helper.RedisConfigHelper;
import com.lenovo.vctl.redis.client.route.impl.RoutingServiceImpl;

public class RedisFactoryImpl implements RedisFactory{
	private static RedisFactory redisFactory;

	private JedisPool defaultPool; // 非动态

	private RedisFactoryImpl() {
		init();
	}

	private void init() {
		this.defaultPool = RedisConfigHelper.getRedisSource("default");
	}

	public static RedisFactory getInstance() {
		if (redisFactory == null) {
			synchronized (RedisFactoryImpl.class) {
				if (redisFactory == null) {
					redisFactory = new RedisFactoryImpl();
				}
			}
		}
		return redisFactory;
	}

	public Redis getRedis(String resource) {
		return new RedisImpl(resource, RoutingServiceImpl.getInstance(), this.defaultPool);
	}
	
	public void removeRedis(String name) {
		// TODO Auto-generated method stub
	}

}
