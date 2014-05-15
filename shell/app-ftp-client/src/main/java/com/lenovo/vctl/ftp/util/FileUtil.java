package com.lenovo.vctl.ftp.util;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.time.DateFormatUtils;
/**
 * @author songkun1
 */
public final class FileUtil {
    private static java.util.Map fileNameMap = Collections.synchronizedMap(new LRUMap(10));
    private static int ip = 0;
    static{
    	try {
			ip = ip2int(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
//			e.printStackTrace();
		}
    }
    public static String getFilePath() {
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
        return Integer.toHexString(ip) +"_"+ Long.toHexString(currentValue);
    }

    public static void main(String[] args) {
//    	try {
//    		String ip = InetAddress.getLocalHost().getHostAddress();
//    		System.out.println(ip+" "+ip2int(ip));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    	
    	System.out.println(getFileName(getFilePath()));
    }
    
    private static int ip2int(String ipStr) throws Exception{
    	int ipNum = 0;
    	if(ipStr == null || ipStr.trim().equals(""))throw new Exception("ip is null");
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
