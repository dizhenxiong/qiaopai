package com.lenovo.vctl.vctl_ftp_client;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import com.lenovo.vctl.ftp.util.FtpUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
	public static void main(String[] args) {
		try {
			System.out.println(assembleTmpFiles("http://tmp.ifaceshow.com/5/123_100",2));	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//
	static String assembleTmpFiles(String fileDir, int totalPiece) throws Exception{

		Vector<InputStream> v = new Vector<InputStream>();
		if(!fileDir.endsWith("/")) fileDir = fileDir + "/";
		String[] names = FtpUtil.listFileNamesByDir(fileDir);
		if(names != null && names.length == totalPiece){
			for(int i=0; i<names.length; i++){
				System.out.println("1234567890==========="+names.length);
				InputStream is = FtpUtil.retrieveFileStream(fileDir+names[i]); //+totalPiece+FtpUtil.SPLITER+i
				if(is != null){
					v.add(is);
				}
			}
			System.out.println(v.size()+"=====================================================");
			InputStream[] iss = {new SequenceInputStream(v.elements())};
			return FtpUtil.createFile(iss, "video",new String[]{null}, "txt")[0];
		}
		return null;
	}
}
