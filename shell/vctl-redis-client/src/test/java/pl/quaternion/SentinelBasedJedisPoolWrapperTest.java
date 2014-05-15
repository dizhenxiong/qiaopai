package pl.quaternion;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Ignore
public class SentinelBasedJedisPoolWrapperTest {

	@Test
	public void testX() throws Exception {
		final Set<String> sentinels = new HashSet<String>();
		final Config config = new Config();
		config.testOnReturn = true;
		config.testOnBorrow = true;

		sentinels.add("redis.vctal.lenovo.com:26379");
		sentinels.add("redis.vctal.lenovo.com:26380");
		sentinels.add("redis.vctal.lenovo.com:26381");

		SentinelBasedJedisPoolWrapper pool = new SentinelBasedJedisPoolWrapper(
				config, 90000, null, 0, "mymaster", sentinels);

		Jedis j = pool.getResource();
		j.flushAll();
		pool.returnResource(j);

		for (int i = 0; i < 1000; i++) {
			try {
				j = pool.getResource();
				j.set("KEY: " + i, "" + i);
				System.out.print(".");
				Thread.sleep(500);
				pool.returnResource(j);
			} catch (JedisConnectionException e) {
				System.out.print("x");
				i--;
				Thread.sleep(1000);
			}
		}

		pool.destroy();
	}
}
