package com.lenovo.vctl.redis.client.route.strategy;

import java.util.List;

import com.lenovo.vctl.redis.client.exception.StrategyException;


/**
 * 策略接口定义
 * 
 * 
 * @author allenshen
 * date: Dec 16, 2008 3:34:12 PM
 * Copyright 2008 Sohu.com Inc. All Rights Reserved.
 */
public interface IStrategy {
    /**
     * 根据Region的名字和CACHE中Key来决定用�?��CACHE(memcachedInstance)
     * 
     * @param regionName
     * @param key
     * @return
     */
    public Object playStrategy(String regionName, Object key) throws StrategyException;
    
    /**
     * 获取某个region下所有的resouce列表
     * @param regionName
     * @return
     * @throws com.lenovo.vctl.redis.client.exception.StrategyException
     */
    public List<String> getAllResouceName(String regionName) throws StrategyException;
}
