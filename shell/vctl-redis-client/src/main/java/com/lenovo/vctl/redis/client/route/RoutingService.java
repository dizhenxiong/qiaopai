package com.lenovo.vctl.redis.client.route;

import com.lenovo.vctl.redis.client.exception.StrategyException;
import com.lenovo.vctl.redis.client.route.strategy.IStrategy;

public interface RoutingService {
    public abstract boolean setRoutingStrategy(String regionName, Object key) throws StrategyException;
    
    public IStrategy getRoutingStrategy(String regionName) throws StrategyException;
}