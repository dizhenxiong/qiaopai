package com.lenovo.vctl.cloudstorage.core;

import java.io.BufferedInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lenovo.vctl.cloudstorage.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.lenovo.vctl.cloudstorage.cmd.impl.v2.DeleteCmd;
import com.lenovo.vctl.cloudstorage.cmd.impl.v2.UpdateCmd;
import com.lenovo.vctl.cloudstorage.cmd.invoker.GenericInvoker;
import com.lenovo.vctl.cloudstorage.cmd.invoker.Invoker;
import com.lenovo.vctl.cloudstorage.ftp.FtpItem;
import com.lenovo.vctl.cloudstorage.ftp.extend.UploadFtp;
import com.lenovo.vctl.cloudstorage.ftp.util.FtpUtil;
import com.lenovo.vctl.cloudstorage.model.RecordObject;
import com.lenovo.vctl.cloudstorage.token.TokenUtil;

//import com.lenovo.vctl.apps.cloudstorage.model.StorageObject;

public class Store {
	public static final String ICON = "icon";
	public static final String UPDATE = "update";
	public static final String PIC = "pic";
	public static final String VIDEO = "video";
	
	public static final String ICON_PATH = "icons";
	public static final String UPDATE_PATH = "files";
	
//	private final Map header = new HashMap();
	private static Invoker invoker = new GenericInvoker();
//	static{
//		header.put("Authorization", "lws_token e28c95b8d7a8b1c9245361221acf658:z3ZTQ91%2B%2F9DeTKRaCeh7LypJ7nglF2mocklsT8gGWZKf3P16hKbC2XV1KbLe48RG4Wp88E5udcC6k5BfGjpLjg6VFi9q%2Bxfoa0imDHkeEQ6Z3Hjx%2FjgaWFGIqM4%2Bnw%3D%3D");
//	}
	private static final Log log = LogFactory.getLog(Store.class);
	private final static Store store = new Store();
	public static Store getInstance(){
		return store;
	}
	private Store(){};
	
	public List<RecordObject> createFiles(InputStream[] iss, String[] subNames, String[] suffixNames, String from, long userId, long lenovoId,  boolean needStore2Cloud) throws Exception{
		try {
			vaildCreateParams(iss, subNames, suffixNames, from, userId);
		} catch (Exception e) {
			throw new Exception("storefile params error!");
		}
		
		BufferedInputStream[] biss = new BufferedInputStream[iss.length];
		for(int i=0; i<iss.length; i++){
			biss[i] = new BufferedInputStream(iss[i]);
			biss[i].mark(Integer.MAX_VALUE);
		}
		List<RecordObject> l = new ArrayList<RecordObject>();
		String[] ftpUrls = null;
		try {
			ftpUrls = FtpUtil.createFile(biss, from, subNames, suffixNames);
			if(ftpUrls != null){
				String[] cloudUrls = new String[ftpUrls.length];
				if(needStore2Cloud){
					cloudUrls = storeToCloud(biss, ftpUrls, lenovoId);
				}	
				if(cloudUrls != null && cloudUrls.length == ftpUrls.length){
					for(int i=0; i< iss.length; i++){
						RecordObject o = new RecordObject("", cloudUrls[i], ftpUrls[i], userId);// todo
						l.add(o);
					}
				}else{
					throw new Exception("file store to cloud exception 1");
				}
			}
		} catch (Exception e) {
			log.error("store file ftp error  ",e);
		}finally{
			try{
				for(InputStream is: biss){
					if(is != null) is.close();
				}
			}catch(Exception e){}
		}
		return l;

	}
	

	
	public List<RecordObject> createFiles(InputStream[] iss, String[] subNames, String suffixName, String from, long userId, long lenovoId,  boolean needStore2Cloud) throws Exception{
		vaildCreateParams(iss, subNames, suffixName, from, userId);
		BufferedInputStream[] biss = new BufferedInputStream[iss.length];
		for(int i=0; i<iss.length; i++){
			biss[i] = new BufferedInputStream(iss[i]);
			biss[i].mark(Integer.MAX_VALUE);
		}
		List<RecordObject> l = new ArrayList<RecordObject>();
		String[] ftpUrls = null;
		try {
			ftpUrls = FtpUtil.createFile(biss, from, subNames, suffixName);
			if(ftpUrls != null){
				String[] cloudUrls = new String[ftpUrls.length];
				if(needStore2Cloud){
					cloudUrls = storeToCloud(biss, ftpUrls, lenovoId);
				}	
				if(cloudUrls != null && cloudUrls.length == ftpUrls.length){
					for(int i=0; i< iss.length; i++){
						RecordObject o = new RecordObject("", cloudUrls[i], ftpUrls[i], userId);// todo
						l.add(o);
					}
				}else{
					throw new Exception("file store to cloud exception 1");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("store file ftp error  ",e);
		}finally{
			try{
				for(InputStream is: biss){
					if(is != null) is.close();
				}
			}catch(Exception e){}
		}
		return l;

	}
	
	public List<RecordObject> createFiles(InputStream[] iss, String[] subNames, String[] suffixNames, String from, long userId, long lenovoId) throws Exception{
		return createFiles(iss, subNames, suffixNames, from, userId, lenovoId, true);
	}
	

	public List<RecordObject> storeFiles(String dir, InputStream[] iss, String prefixName, String[] subNames, String[] suffixNames, String from, long userId, long lenovoId,  boolean needStore2Cloud)
			throws Exception {
		try {
			vaildStoreParams(dir, iss, prefixName, subNames, suffixNames, from, userId);
		} catch (Exception e) {
			throw new Exception("storefile params error!");
		}
		BufferedInputStream[] biss = new BufferedInputStream[iss.length];
		for(int i=0; i<iss.length; i++){
			biss[i] = new BufferedInputStream(iss[i]);
			biss[i].mark(Integer.MAX_VALUE);
		}
		List<RecordObject> l = new ArrayList<RecordObject>();
		String[] ftpUrls = null;
		ftpUrls = FtpUtil.storeFile(dir, biss, from, prefixName, subNames, suffixNames);
		if(ftpUrls != null){
			String[] cloudUrls = new String[ftpUrls.length];
			if( needStore2Cloud ){
				cloudUrls = storeToCloud(biss, ftpUrls, lenovoId);
			}
			if(cloudUrls != null && cloudUrls.length == ftpUrls.length){
				for(int i=0; i < iss.length; i++){
					RecordObject o = new RecordObject("", cloudUrls[i], ftpUrls[i], userId);// todo
					l.add(o);
				}
			}else{
				throw new Exception("file store to cloud exception 2");
			}
		}
		
		try{
			for(InputStream is : biss){
				if(is != null)is.close();
			}
		}catch(Exception e){}
		
		return l;
	}
	/**
	 *  dir is a sub dir
	 * @param dir
	 * @param is
	 * @param from
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String storeUpdateFiletoFtp(String ip, String userName, String passwd, int port, String domain, String path, InputStream is, String name)throws Exception{
		
		if(ip == null || userName == null || passwd == null || port <=0 || domain == null || path == null || is == null || name ==null)
			throw new Exception(" sotre update file to ftp params error!");
			UploadFtp client =  FtpUtil.createFtpClient(ip, userName, passwd, port, domain, path);           
	        String lResult = null;
			try {
				String filePath = path;
				filePath = StringUtils.removeStart(filePath, "/");
				lResult = null;
				if (client != null) {
				    if (FtpUtil.createDir(filePath, client)){          
				            try {
				                if (!client.storeFile(filePath + "/" + name, is)){
				                } else {
				                    lResult = new StringBuilder().append("http://").append(domain).append("/").append(filePath).append("/").append(name).toString();
				                }
				            } catch (IOException ex) {
				                ex.printStackTrace(System.err);
				            } 

//	                }
				    }
				} else {
				    throw new IOException("don't connected store server: store server is null");
				}
			} catch (Exception e) {
				throw e;
			}finally{
				FtpUtil.closeFtp(null, client);
			}
	        return lResult;
	}
	
	

	public List<RecordObject> storeFiles(String dir, InputStream[] iss, String prefixName, String[] subNames, String suffixName, String from, long userId, long lenovoId, boolean needStore2Cloud)
			throws Exception {

		try {
			vaildStoreParams(dir, iss, prefixName, subNames, suffixName, from, userId);
		} catch (Exception e) {
			throw new Exception("storefile params error!");
		}
		BufferedInputStream[] biss = new BufferedInputStream[iss.length];
		for(int i=0; i<iss.length; i++){
			biss[i] = new BufferedInputStream(iss[i]);
			biss[i].mark(Integer.MAX_VALUE);
		}
		List<RecordObject> l = new ArrayList<RecordObject>();
		String[] ftpUrls = null;
		ftpUrls = FtpUtil.storeFile(dir, biss, from, prefixName, subNames, suffixName);
		if(ftpUrls != null){
			String[] cloudUrls = new String[ftpUrls.length];
			if( needStore2Cloud ){
				cloudUrls = storeToCloud(biss, ftpUrls, lenovoId);
			}
			if(cloudUrls != null && cloudUrls.length == ftpUrls.length){
				for(int i=0; i < iss.length; i++){
					RecordObject o = new RecordObject("", cloudUrls[i], ftpUrls[i], userId);// todo
					l.add(o);
				}
			}else{
				throw new Exception("file store to cloud exception 2");
			}
		}
		try{
			for(InputStream is : biss){
				if(is != null)is.close();
			}
		}catch(Exception e){}
		return l;
	}
	
	public List<RecordObject> storeFiles(String srcUrl, InputStream[] iss, String[] subNames, String suffixName, String from, long userId, long lenovoId, boolean needStore2Cloud)
			throws Exception {

		
		if(srcUrl == null || srcUrl.trim().equals(""))throw new Exception("srcUrl is invalide");
		String prefixName = srcUrl.substring(srcUrl.lastIndexOf("/")+1);
		String dir = srcUrl.replace(prefixName, "");
		prefixName = prefixName.substring(0, prefixName.lastIndexOf("_"));
		BufferedInputStream[] biss = new BufferedInputStream[iss.length];
		for(int i=0; i<iss.length; i++){
			biss[i] = new BufferedInputStream(iss[i]);
			biss[i].mark(Integer.MAX_VALUE);
		}
		List<RecordObject> l = new ArrayList<RecordObject>();
		String[] ftpUrls = null;
		ftpUrls = FtpUtil.storeFile(dir, biss, from, prefixName, subNames, suffixName);
		if(ftpUrls != null){
			String[] cloudUrls = new String[ftpUrls.length];
			if( needStore2Cloud ){
				cloudUrls = storeToCloud(biss, ftpUrls, lenovoId);
			}
			if(cloudUrls != null && cloudUrls.length == ftpUrls.length){
				for(int i=0; i < iss.length; i++){
					RecordObject o = new RecordObject("", cloudUrls[i], ftpUrls[i], userId);// todo
					l.add(o);
				}
			}else{
				throw new Exception("file store to cloud exception 2");
			}
		}
		try{
			for(InputStream is : biss){
				if(is != null)is.close();
			}
		}catch(Exception e){}
		return l;
	}
	
	
	public List<RecordObject> storeFiles(String dir, InputStream[] iss, String prefixName, String[] subNames, String[] suffixNames, String from, long userId, long lenovoId) throws Exception{
		return storeFiles(dir, iss, prefixName, subNames, suffixNames, from , userId, lenovoId, true);
	}
	public static String[] storeToCloud(InputStream[] iss, String[] urls, long lenovoId) throws Exception{
		if(iss == null || urls == null || iss.length != urls.length)return null;
		String[] cloudUrls = new String[iss.length];
		for(int i=0; i<iss.length; i++){
			if(urls[i] == null || urls[i].isEmpty()) {
				cloudUrls[i] = null;
				continue;
			}
			iss[i].reset();
//				CreateCmd cmd = new CreateCmd(getCloudObjectKey(urls[i]), iss[i], header);
			Map header = new HashMap();
			header.put("Authorization", TokenUtil.generate());
			UpdateCmd cmd = new UpdateCmd(getCloudObjectKey(urls[i]), iss[i], header);
			if(invoker.invoke(cmd)){
				cloudUrls[i] = cmd.getUri();
			}else{
				throw new Exception("store to cloud error");
			}
			
		} 
		return cloudUrls;
	} 
	
	
	
	
	private static String getCloudObjectKey(String url){
		if(url == null || url.isEmpty() || url.indexOf(Constants.FTPSTORAGE_DOMAIN) < 0) return null;
		return url.replaceAll("http://", "").replaceAll("\\."+ Constants.FTPSTORAGE_DOMAIN, "");
	}
	

	private static String getCloudUrlFromFtpUrl(String ftpUrl){
		if(ftpUrl == null || ftpUrl.isEmpty()) return null;
		return MessageFormat.format(Constants.CLOUDSTORAGE_UPDATE_OBJECT_URL, getCloudObjectKey(ftpUrl));
	}
	private static String getFtpUrlFromCloudUrl(String cloudUrl){
		if(cloudUrl == null || cloudUrl.isEmpty() || cloudUrl.indexOf(Constants.CLOUDSTORAGE_DOMAIN) < 0) return null;
		String url = cloudUrl.replaceAll("http://", "").replaceAll(Constants.CLOUD_URL_DOMAIN, "");
		return new StringBuffer().append("http://").append(url.substring(0, 2)).append(".").append(Constants.FTPSTORAGE_DOMAIN).append(url.substring(2)).toString();
	}
	public boolean clearTmpDir(){
		try {
			return FtpUtil.clearTmpDir();
		} catch (Exception e) {
			log.error("clearTmpDir error : ", e);
			return false;
		}
	}
	
	
	public boolean getFile(String httpUrlAddress, OutputStream os){
		return FtpUtil.getFtpFile(httpUrlAddress, os);
	}
	
	
	private void vaildCreateParams(InputStream[] iss, String[] subNames, String[] suffixNames, String from, long userId) throws Exception{
		if(iss == null  || iss.length == 0 || subNames == null || suffixNames == null || from == null || userId <= 0) throw new Exception("storage params error");
	}
	private void vaildCreateParams(InputStream[] iss, String[] subNames, String suffixName, String from, long userId) throws Exception{
		if(iss == null  || iss.length == 0 || subNames == null || suffixName == null || from == null || userId <= 0) throw new Exception("storage params error");
	}
	private void vaildStoreParams(String dir, InputStream[] iss, String prefixName, String[] subNames, String[] suffixNames, String from, long userId) throws Exception{
		if(dir == null || dir.isEmpty() || prefixName== null || prefixName.isEmpty() || iss == null  || iss.length == 0 || subNames == null || suffixNames == null || from == null || userId <= 0) throw new Exception("storage params error");
	}
	
	private void vaildStoreParams(String dir, InputStream[] iss, String prefixName, String[] subNames, String suffixName, String from, long userId) throws Exception{
		if(dir == null || dir.isEmpty() || prefixName== null || prefixName.isEmpty() || iss == null  || iss.length == 0 || subNames == null || suffixName == null || from == null || userId <= 0) throw new Exception("storage params error");
	}
	
	public boolean deleteFiles(String[] urls){
		if(urls == null || urls.length <= 0) return true;
		boolean result = true;
		for(String url : urls){
			if(url == null || url.isEmpty()){
				continue;
			}
			String cloudUrl = null;
			String ftpUrl = null;
			if(url.startsWith("http：//"))url = new StringBuffer().append("http://").append(url).toString();
			if(url.indexOf(Constants.CLOUDSTORAGE_DOMAIN) > 0){//云存储的url
				cloudUrl = url;
				ftpUrl = getFtpUrlFromCloudUrl(url);
			}else{
				ftpUrl = url;
				cloudUrl = getCloudUrlFromFtpUrl(url);
			}
			if(cloudUrl != null && !cloudUrl.isEmpty()){
				Map header = new HashMap();
				header.put("Authorization", TokenUtil.generate());
				DeleteCmd cmd = new DeleteCmd(cloudUrl, header);
				try {
					result = invoker.invoke(cmd) && result;
				} catch (Exception e) {
					log.error("delete url = " + url + " error", e);
					result = false;
				}
			}
			if(ftpUrl != null && !ftpUrl.isEmpty()){
				result = FtpUtil.removeFile(ftpUrl) && result;
			}
			
	
		
		}
		return result;
		
	}

	public static void main(String[] args) {
//		String url = "http://p1.ifaceshow.com/a/2012/1219/1919/abc.jpg";
//		System.out.println(Store.getCloudUrlFromFtpUrl(url));
//		String url_2 = "http://oss.lenovows.com/object/003adbb8a3338401:test/p1/a/2012/1219/1919/abc.jpg";
//		System.out.println(Store.getFtpUrlFromCloudUrl(url_2));
//		
////		
//
//		try {
//			List<RecordObject> l = Store.getInstance().createFiles(new InputStream[]{new FileInputStream("/data/songkun/123.jpg")}, new String[]{null}, new String[]{"jpg"}, "pic", 1, 1);
//			if(l != null && l.size() > 0){
//				System.out.println(""+l.get(0).getCloudUrl());
//			}
//			System.out.println(l.size());
//		}  catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			UpdateCmd cmd = new UpdateCmd(getCloudObjectKey("http://p1.ifaceshow.com/a/2012/1219/1919/abc.jpg"), new FileInputStream("d:/pic/123.jpg"), header);
//			cmd.execute();
//			System.out.println(cmd.isCmdExecuteSuccess());
//			System.out.println(cmd.getResult());
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Delete
		
//		System.out.println(Store.getInstance().deleteFiles(new String[]{"http://oss.lenovows.com/object/003adbb8a3338401:test/p3/a/2012/1108/1814/a64cbbe_fedbdd2e753e_0.jpg"}));
	
		String srcUrl = "http://dev.ifaceshow.com/a/2013/0410/1616/65e6502c_528878cf27d66_src.jpg";
		String prefixName = srcUrl.substring(srcUrl.lastIndexOf("/")+1);
		String dir = srcUrl.replace(prefixName, "");
		prefixName = prefixName.substring(0, prefixName.lastIndexOf("_"));
		System.out.println(prefixName);

		System.out.println(dir);
	}
	

	
}
