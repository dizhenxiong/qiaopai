package com.lenovo.vctl.redis.client.config.model;

import java.io.Serializable;

public class SentinelItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7038091699628263835L;
	private String name;
	private String server;
	private String port;
	private int timeout;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	} 
	
	
}
