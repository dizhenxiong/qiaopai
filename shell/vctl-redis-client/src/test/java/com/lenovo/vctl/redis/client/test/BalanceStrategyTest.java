package com.lenovo.vctl.redis.client.test;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.lenovo.vctl.redis.client.util.RedisUtil;

@Ignore
public class BalanceStrategyTest {

	@Test
	public void testStrategy() {
//		String regionName = "redis_balance";
		String regionName = "redis_client_df";
		
		String[] keys = {"aaa", "sip_bbb", "sip_ccc", "xxwef", "34634", "xxx", "uheht", "l;;ok", "bdd", "wowevineirn"};
		RedisUtil.get(regionName, keys[0]);
		
		long start = System.currentTimeMillis();
		for (int i=1;i<=1000;i++) {
			for(String key : keys) {
				RedisUtil.set(regionName, key, key);
				Assert.assertEquals(key, RedisUtil.get(regionName, key));
			}
		}
		System.out.println("done. use time : " + (System.currentTimeMillis()-start));
	}
}
