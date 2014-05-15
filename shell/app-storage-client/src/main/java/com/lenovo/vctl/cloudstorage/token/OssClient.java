package com.lenovo.vctl.cloudstorage.token;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class OssClient {
//	private final static String APP_ID = "003f21c3ad6e9c00";
//	private final static String APP_KEY_ID = "ce28c95b8d7a8b1c9245361221acf658"; 
//	private final static String APP_KEY = "935da8e9de5496db6cb13a86e9839044";
//	private final static String USER_SLUG = "songkun1@lenovo.com";
//	private final static long EXPIRATION_SECOND = 30 * 24 * 60 * 60*300l; // second
	private final static String POST_OBJECT_URL = "http://cos.lenovows.com/v1/object/003f21c3ad7b3c01:video_call/";
	
	public Object upload(String objectKey, String fileLocation, String contentType){
	     Object object = null;
		 String token = TokenUtil.generate();
		 HttpClient httpclient = new DefaultHttpClient();
//		 HttpHost proxy = new HttpHost("10.100.1.212", 3125);
//		 httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
		 
		 HttpEntity resEntity = null;

         try {
        	//String url = POST_OBJECT_URL + objectKey+ "?key_id=" + Client.APP_KEY_ID + "&token=" + token;
        	String url = POST_OBJECT_URL + objectKey;
            HttpPut httpput = new HttpPut(url);

        	System.out.println("Authorization:"+token);
            httpput.addHeader("Authorization", token);
            File file = new File(fileLocation);


            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType(contentType);
            reqEntity.setChunked(true);

            httpput.setEntity(reqEntity);

            System.out.println("executing request " + httpput.getRequestLine());
            HttpResponse response = httpclient.execute(httpput);
            resEntity = response.getEntity();
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
            	StringWriter writer = new StringWriter();
            	IOUtils.copy(resEntity.getContent(), writer, "utf-8");
            	String objectStr = writer.toString();
            	// JSONObject objectJson = JSONObject.fromString(objectStr);
            	// object = new Object(objectJson);
                System.out.println("Response content length: " + resEntity.getContentLength());
                //System.out.println("Chunked?: " + resEntity.isChunked());
                //System.out.println("object location?: " + object.getLocation());
                //System.out.println("object thumb location?: " + object.getThumbLocation("144x144"));
                //System.out.println("object thumb location?: " + object.getThumbLocation("200"));
                //System.out.println("object thumb location?: " + object.getThumbLocation("640"));
            }
            else{
            	
            }
          //  EntityUtils.consume(resEntity);
           
        }catch(Exception e){
        	System.out.println("post error:" + e.getMessage());
        	
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
        	if(resEntity!=null)
				try {
					EntityUtils.consume(resEntity);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            httpclient.getConnectionManager().shutdown();
            
        }
		 return object;
	}

	 public static void main(String[] args) throws Exception {
		 String objectKey = "test8881"; 
		 String fileLocation = "D:/pic/sk1.jpg";
		 String contentType = "image/jpg";
		 OssClient client = new OssClient();
		 client.upload(objectKey, fileLocation, contentType);
	 }
		
}
