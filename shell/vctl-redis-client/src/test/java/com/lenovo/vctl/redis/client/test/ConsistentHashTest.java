package com.lenovo.vctl.redis.client.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.lenovo.vctl.redis.client.route.strategy.HashFunction;
import com.lenovo.vctl.redis.client.route.strategy.impl.ConsistentHash;
import com.lenovo.vctl.redis.client.route.strategy.impl.HashFunctionImpl;

@Ignore
public class ConsistentHashTest {
	
	@Test
	public void testNull() {
		
	}
	
	@Test
	public void testDistributeHash() {
		HashFunction hashFunc = new HashFunctionImpl();
		Collection<Map<String, String>> hosts = new ArrayList<Map<String, String>>();
		for(int i=1; i<=10; i++) {
			Map<String, String> h = new HashMap<String, String>();
			h.put("192.168.0." + i, String.valueOf(i));
			
			hosts.add(h);
		}
		ConsistentHash<Map<String, String>> hash = new ConsistentHash<Map<String, String>>(hashFunc, 10, hosts);
		
		long start = System.currentTimeMillis();
		
		Map<String, Long> distribute = new HashMap<String, Long>();
		for (int i=1; i<=100000; i++) {
			Map<String, String> node = hash.get(i);
			String value = node.values().iterator().next();
			Long cnt = distribute.get(value);
			if (null == cnt) {
				cnt = 0L;
			}
			cnt = cnt + 1;
			distribute.put(value, cnt);
//			System.out.println("get node : " + node.values().iterator().next());
		}
		
		System.out.println("use time : " + (System.currentTimeMillis() - start));
		System.out.println("distribute : " + distribute.toString());
	}

	@Test
	public void testLocateHash() {
		HashFunction hashFunc = new HashFunctionImpl();
		Collection<Map<String, String>> hosts = new ArrayList<Map<String, String>>();
		for(int i=1; i<=10; i++) {
			Map<String, String> h = new HashMap<String, String>();
			h.put("192.168.0." + i, String.valueOf(i));
			
			hosts.add(h);
		}
		ConsistentHash<Map<String, String>> hash = new ConsistentHash<Map<String, String>>(hashFunc, 10, hosts);
		
		long start = System.currentTimeMillis();
		
		String[] keys = {"aaa", "sip_bbb", "sip_ccc", "xxwef", "34634", "xxx", "uheht", "l;;ok", "bdd", "wowevineirn"};
		Map<String, Long> distribute = new HashMap<String, Long>();
		for (String key : keys) {
			Map<String, String> node = hash.get(key);
			String value = node.values().iterator().next();
			Long cnt = distribute.get(value);
			if (null == cnt) {
				cnt = 0L;
			}
			cnt = cnt + 1;
			distribute.put(value, cnt);
			System.out.println("location, key : " + key + ", node : " + node.values().iterator().next());
		}
		
		System.out.println("use time : " + (System.currentTimeMillis() - start));
		System.out.println("distribute : " + distribute.toString());
		
		
		//remove node
		System.out.println("remove node...");
		Map<String, String> removeNode = hosts.iterator().next();
		hash.remove(removeNode);
		distribute = new HashMap<String, Long>();
		for (String key : keys) {
			Map<String, String> node = hash.get(key);
			String value = node.values().iterator().next();
			Long cnt = distribute.get(value);
			if (null == cnt) {
				cnt = 0L;
			}
			cnt = cnt + 1;
			distribute.put(value, cnt);
			System.out.println("location, key : " + key + ", node : " + node.values().iterator().next());
		}
		System.out.println("distribute : " + distribute.toString());
		
		//add node
		System.out.println("add node...");
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("192.168.0.11", "11");
		hash.add(newNode);
		
		distribute = new HashMap<String, Long>();
		for (String key : keys) {
			Map<String, String> node = hash.get(key);
			String value = node.values().iterator().next();
			Long cnt = distribute.get(value);
			if (null == cnt) {
				cnt = 0L;
			}
			cnt = cnt + 1;
			distribute.put(value, cnt);
			System.out.println("location, key : " + key + ", node : " + node.values().iterator().next());
		}
		System.out.println("distribute : " + distribute.toString());
		
		//add removed node
		System.out.println("add back removed node...");
		hash.add(newNode);
		
		distribute = new HashMap<String, Long>();
		for (String key : keys) {
			Map<String, String> node = hash.get(key);
			String value = node.values().iterator().next();
			Long cnt = distribute.get(value);
			if (null == cnt) {
				cnt = 0L;
			}
			cnt = cnt + 1;
			distribute.put(value, cnt);
			System.out.println("location, key : " + key + ", node : " + node.values().iterator().next());
		}
		System.out.println("distribute : " + distribute.toString());
		
		//remove added node
		hash.remove(newNode);
		distribute = new HashMap<String, Long>();
		for (String key : keys) {
			Map<String, String> node = hash.get(key);
			String value = node.values().iterator().next();
			Long cnt = distribute.get(value);
			if (null == cnt) {
				cnt = 0L;
			}
			cnt = cnt + 1;
			distribute.put(value, cnt);
			System.out.println("location, key : " + key + ", node : " + node.values().iterator().next());
		}
		System.out.println("distribute : " + distribute.toString());
	}
}
