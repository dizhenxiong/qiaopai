package com.lenovo.vctl.cloudstorage.cmd.impl;

import java.io.InputStream;
import java.text.MessageFormat;

import com.lenovo.vctl.cloudstorage.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.codehaus.jackson.node.BooleanNode;
import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;
/**
 * push a object to the cloud
 * @author songkun1
 *
 */
public class WriteCmd extends AbstractCmd {
	private final static Log log = LogFactory.getLog(WriteCmd.class);
//	private InputStream is;
//	private String name;
	public WriteCmd(InputStream is, String sdnServer, String oid, String sdnKey)throws Exception {
		super(new HttpPost(MessageFormat.format(Constants.CLOUDSTORAGE_WRITE_OBJECT_URL, sdnServer, oid, sdnKey)), is, null);
//		this.is = is;
	}
	@Override
	public void generateEntity(InputStream is, String... params)
			throws Exception {
		MultipartEntity entity = new MultipartEntity(); 
		InputStreamBody isBody = new InputStreamBody(is, "");
        entity.addPart("file", isBody); 
        this.entity = entity;
	}
	@Override
	public void execute() throws Exception{
		super.execute();
	}
	/**
	 * write Cmd would not return a json body but we can get a result from http header named x-lenovows-status
	 */
	protected void processResult(HttpResponse response){
		String status = response.getFirstHeader("x-lenovows-status").getValue();
		if(status.startsWith("code="+Constants.CLOUDSTORAGE_SUCCESS_CODE)){
			this.result = BooleanNode.valueOf(true);
		}else{
			this.result = BooleanNode.valueOf(false);
		}
	}
	
	public boolean isCmdExecuteSuccess() {
		if(log.isDebugEnabled())
			log.debug("result = " + result.asBoolean());
		return result.asBoolean();
	}
	
}
