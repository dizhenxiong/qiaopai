package com.lenovo.vctl.cloudstorage.ftp.util;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.time.DateFormatUtils;

import com.lenovo.vctl.cloudstorage.core.Store;
/**
 * @author songkun1
 */
public final class FileUtil {
    private static java.util.Map fileNameMap = Collections.synchronizedMap(new LRUMap(10));
    private static String ipString = null;
    static{
			ipString = Integer.toHexString(new Random().nextInt());
			if(ipString == null || ipString.equals("")){
				ipString = "ffff";
			}
    }
    public static String getFilePath(String from) {
    	if(from.equals(Store.ICON)){
    		return Store.ICON_PATH;
    	}else if(from.equals(Store.UPDATE)){
    		return Store.UPDATE_PATH;
    	}
        Date date = new Date();
        String sDate = DateFormatUtils.format(date, "yyyy/MMdd/HHmm");
        return sDate;
    }

    public synchronized static String getFileName(String virtualPath) {
        Long currentValue = null;
        if (fileNameMap.containsKey(virtualPath)) {
            currentValue = (Long) fileNameMap.get(virtualPath);
            if (currentValue == null) { //LRU容易出去
                currentValue = System.nanoTime();
            }
            currentValue = currentValue + 1l;
            fileNameMap.put(virtualPath, currentValue);

        } else {
            currentValue = System.nanoTime();
            fileNameMap.put(virtualPath, currentValue);
        }
        	
        return ipString +"_"+ Long.toHexString(currentValue);
        
        
    }

    public static void main(String[] args) {
//    	try {
//    		String ip = InetAddress.getLocalHost().getHostAddress();
//    		System.out.println(ip+" "+ip2int(ip));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    System.out.println(FileUtil.getFileName("/a/b"));
    }
    
    private static int ip2int(String ipStr) throws Exception{
    	int ipNum = 0;
    	if(ipStr == null || ipStr.trim().equals(""))return ipNum;
    	String[] tmp = ipStr.split("\\.");
    	if(tmp != null && tmp.length == 4){
    		for(int i=0; i<4; i++){
    			int num = Integer.parseInt(tmp[i]);
    			ipNum = (ipNum << 8) + num;
    		}
    		return ipNum;
    	}
    	throw new Exception("ip is not vaild");
    }
    

}
