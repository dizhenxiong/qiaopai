package com.lenovo.vctl.cloudstorage.cmd;


import org.codehaus.jackson.JsonNode;

import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

public interface ICmd {
	public void execute() throws Exception;
	public boolean isCmdExecuteSuccess();
	public void setNextCmd(ICmd cmd);
	public ICmd getNextCmd();
	public JsonNode getResult();
}
