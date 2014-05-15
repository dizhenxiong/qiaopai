package com.lenovo.vctl.redis.client.route.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lenovo.vctl.redis.client.config.helper.RedisConfigHelper;
import com.lenovo.vctl.redis.client.exception.StrategyException;
import com.lenovo.vctl.redis.client.route.strategy.IStrategy;


public class BalanceStrategy implements IStrategy {
    private static Logger logger = Logger.getLogger(BalanceStrategy.class);

    public Object playStrategy(String regionName, Object key) throws StrategyException {
        if (StringUtils.isEmpty(regionName)) {
            throw new StrategyException("region name must have a value");
        }

        if (key == null) {
            throw new StrategyException("key must have a value");
        }
        
        //using all redis source name for consistent hash
        List<String> sourceItemNames = getAllResouceName(regionName);
        if (CollectionUtils.isEmpty(sourceItemNames)) {
        	throw new StrategyException("redis source is empty");
        }
        
        ConsistentHash<String> hash = new ConsistentHash<String>(new HashFunctionImpl(), sourceItemNames.size(), sourceItemNames);
        String sourceName = hash.get(key);
        if (logger.isDebugEnabled()) {
        	logger.info("key : " + key + ", was assign to redis : " + sourceName);
        }
        
        return sourceName;
    }

	@Override
	public List<String> getAllResouceName(String regionName)
			throws StrategyException {
		List<String> poolNames = new ArrayList<String>();
		
		//get all redis items as resouce name
		List<String> sourceItemNames = RedisConfigHelper.getSourceItemNames();
		if (CollectionUtils.isEmpty(sourceItemNames)) {
			return poolNames;
		}
		
		poolNames.addAll(sourceItemNames);
		
		return poolNames;
	}
}
