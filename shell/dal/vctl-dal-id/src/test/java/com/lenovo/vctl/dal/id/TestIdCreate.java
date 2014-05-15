package com.lenovo.vctl.dal.id;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.time.DateUtils;


import junit.framework.TestCase;

public class TestIdCreate extends TestCase {
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testIdcreatorFactory() throws Exception {
		
		for (int i = 0; i <100; i++) {
			IdCreator idCreator = IdCreatorFactory.getTimeIdCreator(15);
			//System.out.println(idCreator.nextId("test_key1"));
			
			Long id = idCreator.nextId("test_key1");
			assertNotNull(id);
			
			Date date = TimeIdHelper.getDateFromId(id);
			long idSeq = TimeIdHelper.getSeqFromId(id);
			long idc = TimeIdHelper.getIdcIdFromId(id);
			assertTrue(DateUtils.isSameDay(date, new Date()));
			assertEquals(idc, 15l);
			
		}
	}
	
	
}
