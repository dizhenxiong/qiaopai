package com.lenovo.vctl.cloudstorage.cmd.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.lenovo.vctl.cloudstorage.Constants;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;
import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

/**
 * remove the object associated with the given oid
 * parameters should like this
 * curl -k -X POST "https://oss.lenovows.com/object/remove" -d"api_key=test&user_key=1313560910041203809&oid=1313488086319200975"
 * @author songkun1
 *
 */
public class RemoveCmd extends AbstractCmd {


	public RemoveCmd(String... params)throws Exception {
		super(new HttpPost(Constants.CLOUDSTORAGE_REMOVE_OBJECT_URL), null, null, params);
	}

	public RemoveCmd(String oid)throws Exception {
		this(Constants.CLOUDSTORAGE_API_KEY, Constants.CLOUDSTORAGE_USER_KEY, oid);
	}
	
	@Override
	public void generateEntity(InputStream is, String... params) throws Exception{
		if(params.length != 3) throw new CloudStorageException("RemoveCmd parameters error");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("api_key", params[0]));
		nameValuePairs.add(new BasicNameValuePair("user_key", params[1]));
		nameValuePairs.add(new BasicNameValuePair("oid", params[2]));
		try {
			this.entity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			throw new CloudStorageException(e);
		} 
	}
	

}
