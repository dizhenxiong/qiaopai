package com.lenovo.vctl.cloudstorage.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lenovo.vctl.cloudstorage.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.JsonNode;
import com.lenovo.vctl.cloudstorage.exception.CloudStorageException;
import com.lenovo.vctl.cloudstorage.util.JacksonUtil;

public abstract class AbstractCmd implements ICmd {
	
	private static final Log log = LogFactory.getLog(AbstractCmd.class);

	protected HttpClient client;
	protected HttpRequestBase httpRequestBase;
	protected HttpEntity entity;
	protected ICmd nextCmd;
	protected JsonNode result;
	protected String reqUri;
	
	public AbstractCmd(HttpGet get, Map<String, String> headers) {
		// TODO Auto-generated constructor stub
		this.httpRequestBase = get;
		this.client = createDefaultHttpClient();
		setHeader(headers);
	}
	
	public AbstractCmd(HttpPost post, InputStream is, Map<String, String> headers, String... params) throws Exception{
		this.client = createDefaultHttpClient();
		this.httpRequestBase = post;
		try {
			generateEntity(is, params);
			setHeader(headers);	
		} catch (CloudStorageException e) {
			throw e;
		}
	}
	
	
	
	public AbstractCmd(HttpDelete delete, Map<String, String> headers){
		this.httpRequestBase = delete;
		this.client = createDefaultHttpClient();
		setHeader(headers);
	}
	
	public AbstractCmd(HttpPut put, InputStream is, Map<String, String> headers, String... params)throws Exception{
		this.httpRequestBase = put;
		this.client = createDefaultHttpClient();
		try {
			generateEntity(is, params);
			setHeader(headers);
		} catch (CloudStorageException e) {
			throw e;
		}	
		
	}
	
	public void execute() throws Exception{
		if(this.client != null){
			if(httpRequestBase != null){
				try {
					this.reqUri = httpRequestBase.getRequestLine().getUri();
					HttpResponse response = client.execute(httpRequestBase);
					processResult(response);
				} catch (Exception e) {
					log.error("cmd execute error " + this.httpRequestBase.getURI().toString(), e);
				}finally{
					client.getConnectionManager().shutdown();
				}
			}else{
				log.error("httpRequestBase = null");
			}
		
		}else{
			log.error("cmd = " + this.getClass() + "client = null" );
		}
		
		
	}
	
	public boolean isCmdExecuteSuccess() {
		if(log.isDebugEnabled())
			log.debug("check cmd execute "  + (result.get("code").asText()).equals(Constants.CLOUDSTORAGE_SUCCESS_CODE));
		if(result != null && (result.get("code").asText()).equals(Constants.CLOUDSTORAGE_SUCCESS_CODE)){
			if(log.isDebugEnabled())
				log.debug("isCmdExecuteSuccess return true" );
			return true;
		}
		return false;
	}

	private  static HttpClient createDefaultHttpClient(){
			HttpContext localContext = new BasicHttpContext();
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);	
			ClientConnectionManager connman = new ThreadSafeClientConnManager();
			HttpClient base =  new DefaultHttpClient(connman, params);

//		    try {
//		        SSLContext ctx = SSLContext.getInstance("TLS");
//		        X509TrustManager tm = new X509TrustManager() {
//		            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
//		 
//		            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
//		 
//		            public X509Certificate[] getAcceptedIssuers() {
//		                return null;
//		            }
//		        };
//		        ctx.init(null, new TrustManager[]{tm}, null);
//		        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
//		        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		        ClientConnectionManager ccm = base.getConnectionManager();
//		        SchemeRegistry sr = ccm.getSchemeRegistry();
//		        sr.register(new Scheme("https", ssf, 443));
//		        return new DefaultHttpClient(ccm, base.getParams());
//		    } catch (Exception ex) {
//		        return null;
//		    }
		return base;
	}
	
	protected static HttpEntity generateHttpEntity(Map<String, String> params){
		if(params != null && params.size() > 0){
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
			Entry<String, String> e = null;
			while(iterator.hasNext()){
				e = iterator.next();
				if(e != null){
					pairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
				}
			}
			try {
				return new UrlEncodedFormEntity(pairs);
			} catch (Exception e2) {
				log.error("generateHttpEntity error  " , e2);
			}
		}
		return null;
	}
	
    public JsonNode getResult(){
		return result;
	}
	
	public abstract void generateEntity(InputStream is, String ... params) throws Exception;

	public ICmd getNextCmd() {
		return nextCmd;
	}

	public void setNextCmd(ICmd nextCmd) {
		this.nextCmd = nextCmd;
	}

	protected void processResult(HttpResponse response){
		StringBuffer sb = new StringBuffer();
		if(response != null && HttpStatus.SC_OK == response.getStatusLine().getStatusCode() && response.getEntity() != null){
			try {
				InputStream is = response.getEntity().getContent();
				if(is != null){
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String tmp = null;
					while((tmp = br.readLine()) != null){
						sb.append(tmp);
					}
				}
		
			} catch (IOException e) {
				log.error("isCmdExcuteSuccess method error ", e);
			}
			
		}
		if(log.isDebugEnabled())
			log.debug("response = " + sb.toString());
		this.result = JacksonUtil.String2Json(sb.toString());
	}	
	
	
	
//	protected String getToken(){
//		try {
//			String key = CryptUtil.initKey();
//			String token = CryptUtil.encryptBASE64(CryptUtil.encrypt("token{app_id:003f142a4aeea000;expiration:1000000000;user_slug:songkun1@lenovo.com}".getBytes(), key));
//			return token;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return "";
//	}
	private void setHeader(Map<String, String> headers){
		if(headers != null && headers.size() > 0){
			Iterator<Entry<String, String>> entryIterator = headers.entrySet().iterator();
			while(entryIterator.hasNext()){
				Entry<String, String> e = entryIterator.next();
				httpRequestBase.setHeader(e.getKey(), e.getValue());
			}
		}
			
	}
	
	
	public String getUri(){
		return this.reqUri;
	}
	
	
//	public static void main(String[] args){
//		InputStream is = AbstractCmd.createInputStream("http://p3.ifaceshow.com/a/2013/0423/1530/a7354062_1298dce6a8345_src.jpg");
//		System.out.println(is);
//	}
	
}
