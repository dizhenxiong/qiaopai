package com.lenovo.vctl.cloudstorage.cmd.impl.v2;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.node.BooleanNode;

import com.lenovo.vctl.apps.commons.cloudstorage.constant.Constants;
import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;

public class UpdateCmd extends AbstractCmd {
	private static final Log log = LogFactory.getLog(UpdateCmd.class);
	public UpdateCmd(String objectKey, InputStream is, Map<String, String> headers) throws Exception  {
		super(new HttpPut(MessageFormat.format(Constants.CLOUDSTORAGE_UPDATE_OBJECT_URL,  objectKey.startsWith("/")? objectKey.substring(1):objectKey)), is, headers);
	}
	
	
	@Override
	public void generateEntity(InputStream is, String... params) throws Exception{
		byte[] b = new byte[is.available()];
		is.read(b);
		is.close();
		ByteArrayEntity bre = new ByteArrayEntity(b);
		bre.setContentType("binary/octet-stream");
		((HttpPut) httpRequestBase).setEntity(bre);
	}
	
	protected void processResult(HttpResponse response){
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			this.result = BooleanNode.valueOf(true);
		}else{
			this.result = BooleanNode.valueOf(false);
		}
	}
	
	public boolean isCmdExecuteSuccess() {
		if(log.isDebugEnabled())
			log.debug("result = " + result.asBoolean());
		if(result == null)return false;
		return result.asBoolean();
	}

}