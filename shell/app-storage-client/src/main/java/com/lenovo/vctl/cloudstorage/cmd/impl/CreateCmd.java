package com.lenovo.vctl.cloudstorage.cmd.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lenovo.vctl.cloudstorage.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;
import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

/**
 * 
 *  create object 
 *  parameters like this 
 *  api_key=test&user_key=1313560910041203809&object_key=abc123&type=jpg&size=1024&peer=211.151.77.148&option=0&last_modified_time=2012-01-01T00:00:00+08:00"
 * @author songkun1
 *
 */
public class CreateCmd extends AbstractCmd {

	private static final Log log = LogFactory.getLog(CreateCmd.class);

	public CreateCmd(String... params) throws Exception  {
		super(new HttpPost(Constants.CLOUDSTORAGE_CREATE_OBJECT_URL), null, null, params);
	}
	public CreateCmd(String objectKey, String type, String size, String peer, String option) throws Exception {
		this(Constants.CLOUDSTORAGE_API_KEY, Constants.CLOUDSTORAGE_USER_KEY, objectKey, type, size, peer, option, (new Date()).toString());
	}
	
	@Override
	public void generateEntity(InputStream is, String... params) throws CloudStorageException{
		if(params.length != 8) throw new CloudStorageException("CreateCmd parameters error");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("api_key", params[0]));
		nameValuePairs.add(new BasicNameValuePair("user_key", params[1]));
		nameValuePairs.add(new BasicNameValuePair("object_key", params[2]));
		nameValuePairs.add(new BasicNameValuePair("type", params[3]));
		nameValuePairs.add(new BasicNameValuePair("size", params[4]));
		nameValuePairs.add(new BasicNameValuePair("peer", params[5]));
		nameValuePairs.add(new BasicNameValuePair("option", params[6]));
		nameValuePairs.add(new BasicNameValuePair("last_modified_time", params[7]));
		try {
			this.entity  = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			throw new CloudStorageException(e);
		} 
	}
	
}
