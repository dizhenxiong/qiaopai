package com.lenovo.vctl.cloudstorage.ftp.extend;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import com.lenovo.vctl.cloudstorage.ftp.FtpItem;



public class UploadFtp extends FTPClient {
	
    private FtpItem ftpItem;
    
    private boolean isFromPool;

    public FtpItem getFtpItem() {
        return ftpItem;
    }

    public void setFtpItem(FtpItem ftpItem) {
        this.ftpItem = ftpItem;
    }
     
    
    
    
    public boolean isFromPool() {
		return isFromPool;
	}

	public void setFromPool(boolean isFromPool) {
		this.isFromPool = isFromPool;
	}

	public void connect() throws SocketException, IOException{
    	if(ftpItem != null){
    		
    		connect(ftpItem.getIp(), ftpItem.getPort());
    	}


    }
    
    
    public void login() throws IOException{
      	if(ftpItem != null){
      		login(ftpItem.getUser(), ftpItem.getPasswd());
        	this.setFileType(FTP.BINARY_FILE_TYPE);//用于传递二进制文件
    	}
      	
      	
    }

	@Override
	public String toString() {
		return "UploadFtp [ftpItem=" + ftpItem + ", isFromPool=" + isFromPool
				+ ", _replyCode=" + _replyCode + ", _replyLines=" + _replyLines
				+ ", _newReplyString=" + _newReplyString + ", _replyString="
				+ _replyString + ", _controlEncoding=" + _controlEncoding
				+ ", strictMultilineParsing=" + strictMultilineParsing
				+ ", _controlInput_=" + _controlInput_ + ", _controlOutput_="
				+ _controlOutput_ + ", _commandSupport_=" + _commandSupport_
				+ ", _timeout_=" + _timeout_ + ", _socket_=" + _socket_
				+ ", _defaultPort_=" + _defaultPort_ + ", _input_=" + _input_
				+ ", _output_=" + _output_ + ", _socketFactory_="
				+ _socketFactory_ + ", _serverSocketFactory_="
				+ _serverSocketFactory_ + ", connectTimeout=" + connectTimeout
				+ "]";
	}

	public static void main(String[] args) {
		FtpItem item = new FtpItem();
		item.setIp("127.0.0.1");
		item.setPort(21);
		UploadFtp ftp = new UploadFtp();
		ftp.ftpItem = item;
		try {
			ftp.connect();
			ftp.login();
			FileInputStream fis = new FileInputStream("d:/1393925129860.amr");
			boolean flag = ftp.storeFile("abbb", fis);
			System.out.println(flag);
//			ftp.finalize();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    
    
}
