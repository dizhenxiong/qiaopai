package com.lenovo.vctl.cloudstorage.cmd.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.lenovo.vctl.apps.commons.cloudstorage.constant.Constants;
import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;
import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

/**
 * commit cmd 
 * parameters should like this
 * curl -k -X POST "https://oss.lenovows.com/object/commit" -d"api_key=test&user_key=1313560910041203809&oid=1313486206368200974&type=jpg&size=1024&last_modified_time=2012-01-01T00:00:00+08:00"
 * @author songkun1
 *
 */
public class CommitCmd extends AbstractCmd {

	public CommitCmd(String... params) throws Exception {
		super(new HttpPost(Constants.CLOUDSTORAGE_COMMIT_OBJECT_URL), null, null, params);
	}
	public CommitCmd(String oid, String name, String type, String size) throws Exception  {
		this(Constants.CLOUDSTORAGE_API_KEY, Constants.CLOUDSTORAGE_USER_KEY, oid, name, type, size);
	}
	
	@Override
	public void generateEntity(InputStream is, String... params) throws CloudStorageException{
		if(params.length != 6) throw new CloudStorageException("CommitCmd parameters error");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("api_key", params[0]));
		nameValuePairs.add(new BasicNameValuePair("user_key", params[1]));
		nameValuePairs.add(new BasicNameValuePair("oid", params[2]));
		nameValuePairs.add(new BasicNameValuePair("name", params[3]));
		nameValuePairs.add(new BasicNameValuePair("type", params[4]));
		nameValuePairs.add(new BasicNameValuePair("size", params[5]));
		nameValuePairs.add(new BasicNameValuePair("last_modified_time", new Date().toString()));
		try {
			this.entity  = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			throw new CloudStorageException(e);
		} 
	}


}
