package pl.quaternion;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

public class SentinelBasedJedisPoolWrapper extends Pool<Jedis> {
	private static Logger logger = Logger.getLogger(SentinelBasedJedisPoolWrapper.class);
	private class HostAndPort {
		String host;
		int port;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HostAndPort) {
				final HostAndPort that = (HostAndPort) obj;
				return this.port == that.port && this.host.equals(that.host);
			}
			return false;
		}

		@Override
		public String toString() {
			return host + ":" + port;
		}
	}

	public class JedisPubSubAdapter extends JedisPubSub {

		@Override
		public void onMessage(String channel, String message) {

		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {

		}

		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {

		}

		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {

		}

		@Override
		public void onSubscribe(String channel, int subscribedChannels) {

		}

		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {

		}
	}

	private final Config poolConfig;

	private final int timeout;
	private final String password;
	private final int database;

	private volatile JedisPool poolToUse;

	private volatile HostAndPort currentHostMaster;

	/**
	 * generate JedisPool without sentinel
	 * no sentinel, not need to subscribe the message
	 * 
	 * @param poolConfig
	 * @param timeout
	 * @param password
	 * @param database
	 * @param host
	 * @param port
	 */
	public SentinelBasedJedisPoolWrapper(final Config poolConfig, int timeout,
			final String password, int database, String host, int port) {
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		this.database = database;
		
		HostAndPort nonSentinelMaster = toHostAndPort(host, port);
		initPool(nonSentinelMaster);
	}
	
	/**
	 * generate JedisPool with sentinels
	 * @param poolConfig
	 * @param timeout
	 * @param password
	 * @param database
	 * @param masterName
	 * @param sentinels
	 */
	public SentinelBasedJedisPoolWrapper(final Config poolConfig, int timeout,
			final String password, int database, String masterName,
			Set<String> sentinels) {
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		this.database = database;

		final HostAndPort master = initSentinels(sentinels, masterName);
		initPool(master);
	}

	public void destroy() {
		// TODO clean sentinels connections
		poolToUse.destroy();
	}

	public HostAndPort getCurrentHostMaster() {
		return currentHostMaster;
	}

	public Jedis getResource() {
		return poolToUse.getResource();
	}
	
	public JedisPool getJedisPool() {
		return poolToUse;
	}

	private void initPool(HostAndPort master) {
		if (!master.equals(currentHostMaster)) {
			currentHostMaster = master;
			if (logger.isDebugEnabled()) {
				logger.debug("Created pool: " + master);
			}
			poolToUse = new JedisPool(poolConfig, master.host, master.port,
					timeout, password, database);
		}
	}

	private HostAndPort initSentinels(Set<String> sentinels,
			final String masterName) {
		HostAndPort master = null;
		final boolean running = true;
		outer: while (running) {
			for (String sentinel : sentinels) {
				final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
						.split(":")));
				try {
					final Jedis jedis = new Jedis(hap.host, hap.port);
					if (master == null) {
						List<String> masterAddr = jedis.sentinelGetMasterAddrByName(masterName);
						if (CollectionUtils.isEmpty(masterAddr)) throw new JedisConnectionException("masterAddr empty. master name :" + masterName);
						
						master = toHostAndPort(masterAddr);
						jedis.disconnect();
						break outer;
					}
				} catch (JedisConnectionException e) {
					logger.error("Cannot connect to sentinel running @ " + hap
							+ "master name : "+ masterName +". Trying next one.");
				}
			}
			try {
				logger.error("All sentinels down, cannot determinate where is "
						+ masterName + " master is running... sleeping 1000ms.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Got master running at " + master
					+ ". Starting sentinel listeners.");
		}
		for (String sentinel : sentinels) {
			final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
					.split(":")));

			new Thread() {
				public void run() {
					boolean running = true;
					while (running) {
						final Jedis jedis = new Jedis(hap.host, hap.port);
						try {
							jedis.subscribe(new JedisPubSubAdapter() {
								@Override
								public void onMessage(String channel,
										String message) {
									// System.out.println(channel + ": " +
									// message);
									// +switch-master: mymaster 127.0.0.1 6379
									// 127.0.0.1 6380
									logger.error("Sentinel " + hap + " published: "
												+ message + ".");
									final String[] switchMasterMsg = message
											.split(" ");
									if (masterName.equals(switchMasterMsg[0])) {
										initPool(toHostAndPort(Arrays.asList(
												switchMasterMsg[3],
												switchMasterMsg[4])));
									}
								}
							}, "+switch-master");
						} catch (JedisConnectionException e) {
							logger.error("Lost connection to " + hap
									+ ". Sleeping 5000ms.");
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				};
			}.start();
		}

		return master;
	}

	public void returnBrokenResource(final Jedis resource) {
		poolToUse.returnBrokenResource(resource);
	}

	public void returnResource(final Jedis resource) {
		poolToUse.returnResource(resource);
	}

	public void returnResourceObject(final Object resource) {
		poolToUse.returnResourceObject(resource);
	}

	private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
		final HostAndPort hap = new HostAndPort();
		hap.host = getMasterAddrByNameResult.get(0);
		hap.port = Integer.parseInt(getMasterAddrByNameResult.get(1));
		return hap;
	}
	
	private HostAndPort toHostAndPort(String host, int port) {
		final HostAndPort hap = new HostAndPort();
		hap.host = host;
		hap.port = port;
		return hap;
	}
}
