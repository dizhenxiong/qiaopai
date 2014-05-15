package com.lenovo.vctl.cloudstorage.cmd.impl;

import java.io.File;
import java.io.InputStream;

import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

/**
 * composite storecmd used to store a object into the cloud, this cmd inlcudes create, write, commit, link the four cmds 
 * @author songkun1
 *
 */
public class StoreCmd extends CreateCmd {
	private InputStream is;
	private String name;
	private String type;
	private String size;
	
	
	public StoreCmd(InputStream is, String objectKey, String type, String size, String peer, String option)throws Exception {
		super(objectKey, type, size, peer, option);
		this.is = is;
		this.name = objectKey;
		this.type = type;
		this.size= size;
	}
	@Override
	public void execute() throws Exception{
		super.execute();
		//create cmd exec success will add new Cmd to them
		if(isCmdExecuteSuccess()){
			try {
				WriteCmd writeCmd = new WriteCmd(is, getResult().get("sdn_server").asText(), getResult().get("oid").asText(), getResult().get("sdn_key").asText());
				setNextCmd(writeCmd);
				CommitCmd commitCmd = new CommitCmd(getResult().get("oid").asText(), this.name, this.type, this.size);
				writeCmd.setNextCmd(commitCmd);
				LinkCmd linkCmd = new LinkCmd(getResult().get("oid").asText(), this.name, this.size);
				commitCmd.setNextCmd(linkCmd);
			} catch (Exception e) {
				throw new CloudStorageException("commitCmd create excpetion in storage cmd execute method.");
			}
		}
	}
}
