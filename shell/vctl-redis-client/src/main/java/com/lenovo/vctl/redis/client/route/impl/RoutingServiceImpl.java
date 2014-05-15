package com.lenovo.vctl.redis.client.route.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lenovo.vctl.redis.client.config.helper.RedisConfigHelper;
import com.lenovo.vctl.redis.client.exception.StrategyException;
import com.lenovo.vctl.redis.client.route.ContextHolder;
import com.lenovo.vctl.redis.client.route.RoutingService;
import com.lenovo.vctl.redis.client.route.strategy.IStrategy;


public class RoutingServiceImpl implements RoutingService {
    private static Logger logger = Logger.getLogger(RoutingServiceImpl.class);
    private static Map<String, IStrategy> objectStrategy = new ConcurrentHashMap<String, IStrategy>();
    private static RoutingService routingService = null;

    public static RoutingService getInstance() {
        if (routingService == null) {
            synchronized (RoutingServiceImpl.class) {
                routingService = new RoutingServiceImpl();
            }
        }
        return routingService;
    }

    private RoutingServiceImpl() {

    }

    /* (non-Javadoc)
     * @see com.sohu.sns.dal.cache.route.RoutingService#setRoutingStrategy(java.lang.String, java.lang.Object)
     */
    public boolean setRoutingStrategy(String regionName, Object key) throws StrategyException {
        IStrategy strategy = getRoutingStrategy(regionName);

        if (strategy != null) {
            ContextHolder.setRedisName(String.valueOf(strategy.playStrategy(regionName, key)));
        }
        return false;
    }

    
    public IStrategy getRoutingStrategy(String regionName) throws StrategyException {
        IStrategy strategy = objectStrategy.get(regionName);
        if (strategy == null) {
            // 根据配置判断对象是否有独立策略配置
            // 有：初始化并存储到Map
            // 无：把全局策略存储到Map
            String className = null;

            try {
                className = RedisConfigHelper.getStrategyClassName(regionName);
                if (StringUtils.isNotEmpty(className)) {
                    strategy = (IStrategy) Class.forName(className).newInstance();
                    objectStrategy.put(className, strategy);
                } else {
                    logger.error(StringUtils.defaultIfEmpty(regionName, "")
                            + " region strategyClass property must have value, please set in redis_client.xml");
                }

            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace(System.err);
                } else {
                    logger.error("don't instance " + StringUtils.defaultIfEmpty(className, ""));
                }
            }

        }
        
        return strategy;
    }
}
