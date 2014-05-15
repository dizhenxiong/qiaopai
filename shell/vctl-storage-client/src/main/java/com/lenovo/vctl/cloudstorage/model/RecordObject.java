package com.lenovo.vctl.cloudstorage.model;

import java.io.Serializable;

public class RecordObject implements Serializable{
	
	private long id; //primary key
	private String oid;
	private String cloudUrl; // 云存储url
	private String ftpUrl; // ftpurl
	private long uid;//上传该文件的user
	
	public RecordObject(){}
	
	public RecordObject(String oid, String cloudUrl, String ftpUrl, long uid){
		this.oid = oid;
		this.cloudUrl = cloudUrl;
		this.ftpUrl = ftpUrl;
		this.uid = uid;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getCloudUrl() {
		return cloudUrl;
	}
	public void setCloudUrl(String cloudUrl) {
		this.cloudUrl = cloudUrl;
	}
	public String getFtpUrl() {
		return ftpUrl;
	}
	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	
	
}
