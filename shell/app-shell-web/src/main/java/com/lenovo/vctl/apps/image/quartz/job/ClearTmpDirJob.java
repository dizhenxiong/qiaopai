package com.lenovo.vctl.apps.image.quartz.job;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lenovo.vctl.cloudstorage.core.Store;


public class ClearTmpDirJob{
	private static final Logger log = LoggerFactory.getLogger(ClearTmpDirJob.class);
	public void work()throws Exception {
		boolean flag = Store.getInstance().clearTmpDir();
		log.info("ClearTmpDirJob state = "  + (flag ? " ok ": " error ") ) ;
	}
	
}
