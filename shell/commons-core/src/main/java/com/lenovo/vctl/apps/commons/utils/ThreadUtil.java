package com.lenovo.vctl.apps.commons.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadUtil {
	
	private static final Log log = LogFactory.getLog(ThreadUtil.class) ;
	
	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			log.warn("Interrupted. ", e) ;
		}
	}
}