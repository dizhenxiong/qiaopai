package com.lenovo.vctl.redis.client.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.lenovo.vctl.redis.client.Redis;
import com.lenovo.vctl.redis.client.impl.RedisFactoryImpl;
import com.lenovo.vctl.redis.client.util.RedisUtil;


@Ignore
public class RedisTest {

    private static final ExecutorService executors = Executors.newFixedThreadPool(3);
    
    @Test
    public void testDel() {
    	Long result = RedisUtil.del("wefwef");
    	System.out.println(result);
    }
    
    @Test
    public void TestJedis() {
    	long start = System.currentTimeMillis();
    	
    	//Init redis source
        JedisPoolConfig cf = new JedisPoolConfig();
		cf.setMaxActive(18);
		cf.setMaxIdle(18);
		cf.setMaxWait(-1);
		JedisPool pool = new JedisPool(cf, "redis.vctal.lenovo.com", 6379);
		
		Jedis client = pool.getResource();
		String obj = client.get("nuan");
    	System.out.println("TestJedis get value, use time : " + (System.currentTimeMillis() - start));
		
    	for (int j=1; j<=3; j++) {
    		start = System.currentTimeMillis();
			for (int i=1; i<=20000; i++) {
				try {
					client = pool.getResource();
					obj = client.get("nuan");
				}
				finally {
					pool.returnResource(client);
				}
			}
			System.out.println("TestJedis get from key nuan : " + obj + ", use time : " + (System.currentTimeMillis() - start));
    	}
    }
    
    @Test
    public void TestRedis() {
    	long start = System.currentTimeMillis();
    	Redis client = RedisFactoryImpl.getInstance().getRedis("redis_client_df");
    	client.set("nuan", "123");
    	System.out.println("set value, use time : " + (System.currentTimeMillis() - start));
    	
    	String obj = client.get("nuan");
    	System.out.println("get from key nuan : " + obj + ", use time : " + (System.currentTimeMillis() - start));
    	
    	for (int j=1; j<=3; j++) {
	    	start = System.currentTimeMillis();
	    	for (int i=1; i<=200000; i++) {
	    		client = RedisFactoryImpl.getInstance().getRedis("redis_client_df");
	    		obj = client.get("nuan");
	    	}
	    	System.out.println("get from key nuan : " + obj + ", use time : " + (System.currentTimeMillis() - start));
    	}
    }
    
    @Test
    public void testPlus() {
    	RedisUtil.del("default", "wefwef");
    	
    	RedisUtil.plus("default", "wefwef", 1, 10000000L);
    	String result = RedisUtil.get("default", "wefwef");
    	Assert.assertEquals("1", result);
    	
    	RedisUtil.plus("default", "wefwef", 1, 10000000L);
    	result = RedisUtil.get("default", "wefwef");
    	Assert.assertEquals("2", result);
    }
    
	@Test
	public void TestMap() {
		
		Runnable task = new Runnable() {
			public void run() {
				Redis client = RedisFactoryImpl.getInstance().getRedis(null);
				for (int i=1; i<=50000; i++) {
					client.hSet("nuan_map", "task_1_"+i, "test"+i);
					System.out.println("wef");
				}
				System.out.println("done task");
			}
			
		};
		executors.execute(task);
		
		Runnable task2 = new Runnable() {
			public void run() {
				Redis client = RedisFactoryImpl.getInstance().getRedis(null);
				for (int i=1; i<=50000; i++) {
					client.hSet("nuan_map", "task_2_"+i, "test"+i);
				}
				System.out.println("done task2");
			}
			
		};
		executors.execute(task2);

		Runnable task3 = new Runnable() {
			public void run() {
				Redis client = RedisFactoryImpl.getInstance().getRedis(null);
				for (int i=1; i<=50000; i++) {
					client.hSet("nuan_map", "task_3_"+i, "test"+i);
				}
				System.out.println("done task3");
			}
			
		};
		executors.execute(task3);
		
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMGet() {
		String region = "redis_client_df";
		
		RedisUtil.set(region, "001", "001"); //存6379
		RedisUtil.set(region, "008", "008"); //存6380
		
		List<String> results = RedisUtil.mget(region, "001", "008");
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		Assert.assertEquals("001", results.get(0));
		Assert.assertEquals("008", results.get(1));
		
		results = RedisUtil.mget(region, "001", "009", "008", "000");
		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());
		Assert.assertEquals("001", results.get(0));
		Assert.assertNull(results.get(1));
		Assert.assertEquals("008", results.get(2));
		Assert.assertNull(results.get(3));
	}
	
	@Test
	public void testhMGet() {
		String region = "redis_client_df";
		String key = "testMap";
		
		RedisUtil.hSet(region, key, "001", "value001"); //存6379
		RedisUtil.hSet(region, key, "002", "value002"); //存6379
		RedisUtil.hSet(region, key, "005", "value005"); //存6379
		RedisUtil.hSet(region, key, "009", "value009"); //存6379
		
		List<String> results = RedisUtil.hmGet(region, key, "001", "002", "005", "009");
		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());
		Assert.assertEquals("value001", results.get(0));
		Assert.assertEquals("value002", results.get(1));
		Assert.assertEquals("value005", results.get(2));
		Assert.assertEquals("value009", results.get(3));
		
		results = RedisUtil.hmGet(region, key, "001", "002", "003", "008");
		Assert.assertEquals(4, results.size());
		Assert.assertEquals("value001", results.get(0));
		Assert.assertEquals("value002", results.get(1));
		Assert.assertNull(results.get(2));
		Assert.assertNull(results.get(3));
	}
	
	@Test
	public void testGetAll() {
		String region = "redis_client_df";
		
		RedisUtil.del(region, "001");
		RedisUtil.del(region, "002");
		RedisUtil.del(region, "003");
		RedisUtil.del(region, "005");
		RedisUtil.del(region, "006");
		RedisUtil.del(region, "009");
		
		RedisUtil.set(region, "001", "{\"userMobile\":\"18210600891\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");
		RedisUtil.set(region, "002", "{\"userMobile\":\"18210600892\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");
		RedisUtil.set(region, "003", "{\"userMobile\":\"18210600892\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");

		List<String> results = RedisUtil.getAll(region, "*");
		Assert.assertEquals(3, results.size());
		
		RedisUtil.set(region, "005", "{\"userMobile\":\"18210600895\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");
		RedisUtil.set(region, "006", "{\"userMobile\":\"18210600896\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");
		RedisUtil.set(region, "009", "{\"userMobile\":\"18210600899\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");
		
		results = RedisUtil.getAll(region, "*");
		Assert.assertEquals(6, results.size());
		
	}
	
	@Test
	public void testGetAllLoad() {
		String region = "redis_client_df";
		
		for (int i=1; i<=1000000; i++) {
			RedisUtil.set(region, String.valueOf(i), "{\"userMobile\":\""+ i +"\",\"presenceInfoMap\":{\"003cceb8215e-68c2-4f29-86e2-b186f78d5d03003e\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platformInfoMap\":{\"0\":{\"instanceId\":\"\u003cceb8215e-68c2-4f29-86e2-b186f78d5d03\u003e\",\"platform\":0,\"channel\":null,\"extraInfo\":\"\",\"expire\":0,\"createAt\":1366197944925}},\"updateAt\":1366197944925}},\"updateAt\":1366197944925}");
		}
		
		Long start = System.currentTimeMillis();
		List<String> results = RedisUtil.getAll(region, "*");
		System.out.println("size : " + results.size());
		System.out.println("get all using time : " + (System.currentTimeMillis()-start));
//		Assert.assertEquals(1000000, results.size());
	}
	
	@Test
	public void testNull() {
		
		List<String> result = new ArrayList<String>();
		result.add("wef");
		result.add("kkk");
		String[] test = result.toArray(new String[0]);
		for (String k : test) {
			System.out.println(k);
		}
	}
}
