package com.lenovo.vctl.redis.client.test;

import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.lenovo.vctl.redis.client.util.RedisUtil;

@Ignore
public class RedisSentinelTest {

	@Test
	public void testSentinel() throws InterruptedException {
		RedisUtil.plus("sentinel", "sentinel", 1, 10000000L);
		
		for (int i = 0; i < 1000; i++) {
			try {
				RedisUtil.plus("sentinel", "sentinel", 1);
				String value = RedisUtil.get("sentinel", "sentinel");
				System.out.print("." + value);
				Thread.sleep(1000);
			} catch (JedisConnectionException e) {
				System.out.print("x");
				Thread.sleep(1000);
			}
		}
	}
}
