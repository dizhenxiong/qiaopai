package com.lenovo.vctl.cloudstorage.cmd.impl.v2;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.codehaus.jackson.node.BooleanNode;


import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;
import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

public class DeleteCmd extends AbstractCmd{
	private static final Log log = LogFactory.getLog(DeleteCmd.class);
	public DeleteCmd(String objectKey, Map<String, String> headers) {
		super(new HttpDelete(objectKey), headers);
	}

	@Override
	public void generateEntity(InputStream is, String... params)
			throws CloudStorageException {
	}
	
	public boolean isCmdExecuteSuccess() {
		if(log.isDebugEnabled())
			log.debug("result = " + result.asBoolean());
		if(result == null)return false;
		return result.asBoolean();
	}
	
	protected void processResult(HttpResponse response){
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			this.result = BooleanNode.valueOf(true);
		}else{
			this.result = BooleanNode.valueOf(false);
		}
	}

}
