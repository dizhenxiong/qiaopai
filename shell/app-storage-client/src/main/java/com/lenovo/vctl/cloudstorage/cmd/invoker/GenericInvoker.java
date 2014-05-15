package com.lenovo.vctl.cloudstorage.cmd.invoker;

import com.lenovo.vctl.cloudstorage.cmd.ICmd;

public class GenericInvoker implements Invoker {
	
	public GenericInvoker(){
	}
	public boolean invoke(ICmd cmd) throws Exception{
		while(cmd != null){
			cmd.execute();
			if(!cmd.isCmdExecuteSuccess()){
				return false;
			}
			cmd = cmd.getNextCmd();
			
		}
		return true;
	}
	
	
}
