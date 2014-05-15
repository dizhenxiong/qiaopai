package com.lenovo.vctl.storage.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ChunkedInputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;


import com.lenovo.vctl.cloudstorage.cmd.AbstractCmd;
import com.lenovo.vctl.cloudstorage.cmd.impl.v2.CreateCmd;
import com.lenovo.vctl.cloudstorage.cmd.impl.v2.DeleteCmd;
import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;

public class Test extends Thread{
	
	private Object lock = new Object();
	public void run(){
		if(Thread.currentThread().getName().equals("t_1")){
			synchronized(lock){
				try {
					System.out.println("wait");
					lock.wait();
					System.out.println("wait1");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("wait3");
			}
		}
		if(Thread.currentThread().getName().equals("t_2")){
			synchronized(lock){
				try {
					System.out.println("notify");
					lock.notify();
					System.out.println("notify2");
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("notify3");
			}
		}
	
	}
	public static void main(String[] args) {
		Test t = new Test();
		
		Thread t1 = new Thread(t);
		t1.setName("t_1");
		Thread t2 = new Thread(t);
		t2.setName("t_2");
		t1.start();
		t2.start();
	}

}
