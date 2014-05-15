package com.lenovo.vctl.cloudstorage.cmd.invoker;


import com.lenovo.vctl.cloudstorage.cmd.ICmd;


public interface Invoker{
	public boolean invoke(ICmd cmd) throws Exception;
	
}