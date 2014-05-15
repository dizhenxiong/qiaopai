package com.lenovo.vctl.storage.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * Example how to use unbuffered chunk-encoded POST request.
 */
public class ClientChunkEncodedPost {

    public static void main(String[] args) throws Exception {

//        HttpClient httpclient = new DefaultHttpClient();
//        try {
//        	HttpPut post = new HttpPut("http://oss.lenovows.com/object/sk4/a/1/123456/1231111.jpg");
//        	post.addHeader("Authorization", "lws_token 003abfc0b51e7000:1gBD6ZEG4MvTV9d3KZR9IZSMe5w7khYep%2Be%2FO9nujBmZiHQ7PTsgky%2FM%2BPu%2B8KphWEhODZnmFW65mV5Dtvp6jx0YtpsZLr8bADRRNo9uEtxeUQdGcyTDptgwmdn%2FUfEW8o7Je6N6ZimkFGva7ZUQqhK08UQmuIr13WoX8qvW");
////        	post.addHeader("Accept-Encoding", "chunked");
//        	File f = new File("d:/pic/123.jpg");
//        	FileEntity reqEntity = new FileEntity(f);
////        	InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream("/data/songkun/123.jpg"), -1);
//            reqEntity.setContentType("binary/octet-stream");
//            post.setEntity(reqEntity);
//            System.out.println("executing request " + post.getRequestLine());
//            HttpResponse response = httpclient.execute(post);
//
//            System.out.println("----------------------------------------");
//            System.out.println(response.getStatusLine());
//            InputStream is = response.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            Header[] h = post.getAllHeaders();
//            for(int i=0; i<h.length; i++){
//                System.out.println("header = " + h[i].getName() + "_" + h[i].getValue());
//            }
//
////            System.out.println(post.getAllHeaders());
//            String tmp = null;
//            while((tmp = reader.readLine()) != null){
//            	System.out.println("tmp = " +tmp);
//            }
////            if (resEntity != null) {
////                System.out.println("Response content length: " + resEntity.getContentLength());
////                System.out.println("Chunked?: " + resEntity.isChunked());
////            }
////            EntityUtils.consume(resEntity);
//        } finally {
//            // When HttpClient instance is no longer needed,
//            // shut down the connection manager to ensure
//            // immediate deallocation of all system resources
//            httpclient.getConnectionManager().shutdown();
//        }
    	
    	
    	
	      
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpDelete httppost = new HttpDelete("http://oss.lenovows.com/object/003adbb8a3338401:test/p2/a/2012/1108/1811/a64cbbe_feac9535b804_0.jpg");
            httppost.addHeader("Authorization", "lws_token 003adbb8a2b96401:PQHXrIPHSJ9Qf58fCxsRp%2F1u%2FEvn%2FqoqVbIMv%2BwgAk6shprWHERM9Pvzg%2BwGVSzH6BOcHhvXf21k%2FzxeC0ILseZCVUd7oQbehxMBKusvxQ%3D%3D");
//          File file = new File("D:\\pic\\123.jpg");
//          InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
//          reqEntity.setContentType("binary/octet-stream");
//          reqEntity.setChunked(true);
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");
//            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                System.out.println("Chunked?: " + resEntity.isChunked());
            }
            
          InputStream is = response.getEntity().getContent();
          BufferedReader reader = new BufferedReader(new InputStreamReader(is));
          String tmp = null;
          while((tmp = reader.readLine()) != null){
          	System.out.println("tmp = " +tmp);
          }
            EntityUtils.consume(resEntity);
            
            
        }catch(Exception e){} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
    

}