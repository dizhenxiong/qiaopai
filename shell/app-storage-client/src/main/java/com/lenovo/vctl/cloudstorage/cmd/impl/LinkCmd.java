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
 *  generate a link using the given objectId
 *  parameters like this
 *  curl -k -X  POST  "https://oss.lenovows.com/object/link" -d"api_key=test&user_key=1313560910041203809&oid=1313486206368200974&expiration=2012-01-01T00:00:00+08:00"
 * @author songkun1
 *
 */
public class LinkCmd extends AbstractCmd {

	public LinkCmd(String... params) throws Exception  {
		super(new HttpPost(Constants.CLOUDSTORAGE_LINK_OBJECT_URL), null, null, params);
	}
	public LinkCmd(String objectId, String name, String size) throws Exception  {
		this(Constants.CLOUDSTORAGE_API_KEY, Constants.CLOUDSTORAGE_USER_KEY, objectId, name, size);
	}
	
	@Override
	public void generateEntity(InputStream is, String... params) throws Exception{
		if(params.length != 5) throw new CloudStorageException("LinkCmd parameters error");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("api_key", params[0]));
		nameValuePairs.add(new BasicNameValuePair("user_key", params[1]));
		nameValuePairs.add(new BasicNameValuePair("oid", params[2]));
		nameValuePairs.add(new BasicNameValuePair("name", params[3]));
		nameValuePairs.add(new BasicNameValuePair("size", params[4]));
		nameValuePairs.add(new BasicNameValuePair("expiration", Constants.EXPIRATIONDATE));
		try {
			this.entity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new CloudStorageException(e);
		} 

	}

	
	

}
