package com.lenovo.vctl.redis.client.config.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class DatasourceItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6548768493657025956L;
    private String name;      
    private int timeout;      
    private int port;         
    private String server;    
    private int maxActive;   
    private int maxIdle;      
    private int maxWait;      
    
    private Map<String, SentinelItem> sentinelMap = new HashMap<String, SentinelItem>();
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public int getMaxActive() {
        return maxActive;
    }
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }
    public int getMaxIdle() {
        return maxIdle;
    }
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }
    public int getMaxWait() {
        return maxWait;
    }
    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }
	public Map<String, SentinelItem> getSentinelMap() {
		return sentinelMap;
	}
	public void setSentinelMap(Map<String, SentinelItem> sentinelMap) {
		this.sentinelMap = sentinelMap;
	}
	
	public void addSentinelItem(SentinelItem item) {
        if (item != null && StringUtils.isNotEmpty(item.getServer()) && null != item.getPort()) {
        	String key = item.getServer() + ":" + item.getPort();
            if (!sentinelMap.containsKey(key)) {
            	sentinelMap.put(key, item);
            }
        }
    }
	
}
