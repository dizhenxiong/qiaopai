/**
 * 
 */
package com.lenovo.vctl.dal.id;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author allenshen
 * date: 2012-6-25 上午9:54:18
 * Copyright © 2012 lenovo. All Rights Reserved
 *
 */
public class TimeIdHelperBak {

	
	private static final int TIME_BIT_MOVE = 22;
	
	
	
	public static final long TIME_BIT = Long.MAX_VALUE << (18 + 4);

	public static final long IDC_ID_BIT = 15L << 18;

	public static final long SEQ_BIT = ~(Long.MAX_VALUE << 18);

	private static final int DEFAULT_IDC = 6;

	private static int DEFAULT_ID_INTEVAL = 5;

	private static AtomicLong sid = new AtomicLong(0l);

	public static boolean isUuidAfterUpdate(long id) {
		return TimeIdHelperBak.isValidId(id) && id > 3342818919841793l;   
																		// 2011-08-05
																		// 00:00:00
	}

	/**
	 * is valid id
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isValidId(long id) {
		return (id > 3000000000000000L) && (id < 4500000000000000L);
	}

	/**
	 * get unix time from id (Accurate to seconds)
	 * 
	 * @param id
	 * @return
	 */
	public static long getTimeFromId(long id) {
		return getTimeNumberFromId(id) + 515483463;
	}

	/**
	 * get time number from id
	 * 
	 * @param id
	 * @return
	 */
	public static long getTimeNumberFromId(long id) {
		return id >> (18 + 4);
	}

	/**
	 * get idc from id
	 * 
	 * @param id
	 * @return
	 */
	public static long getIdcIdFromId(long id) {
		return (id & IDC_ID_BIT) >> 18;
	}

	/**
	 * get seq from id
	 * 
	 * @param id
	 * @return
	 */
	public static long getSeqFromId(long id) {
		return id & SEQ_BIT;
	}

	/**
	 * get date time from id
	 * 
	 * @param id
	 * @return
	 */
	public static Date getDateFromId(long id) {
		return new Date(getTimeFromId(id) * 1000);
	}

	/**
	 * get id by date
	 * 
	 * @param date
	 * @return
	 */
	public static long getIdByDate(Date date) {
		long uuid = date.getTime() / 1000;

		uuid -= 515483463;
		uuid <<= 22;
		uuid += DEFAULT_IDC << 18;

		long sidValue = sid.addAndGet(DEFAULT_ID_INTEVAL);
		uuid += sidValue % (1 << 18);
		return uuid;
	}
	
	
	
	/**
	 * get id by date
	 * 
	 * @param date
	 * @return
	 */
	public static long getIdByDate(Date date, int idc) {
		
		
		long uuid = date.getTime() / 1000;

		uuid -= 515483463;
		uuid <<= 22;
		uuid += DEFAULT_IDC << 18;

		long sidValue = sid.addAndGet(DEFAULT_ID_INTEVAL);
		uuid += sidValue % (1 << 18);
		return uuid;
	}
	
	

	public static void main1(String[] args) {
		// long id = 3374709054121351l;
		// long id = 3379782484330149l;
		// long id = 3363475030378149l; //10.1 3363475030378149
		// long id = 3374709054211749l; //11.1 3374709054211749
		// long id = 3100365840449541l;
		// System.out.println(getTimeFromId(id) * 1000);
		// System.out.println(new Date(UuidHelper.getTimeFromId(id) * 1000));
		// SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		// System.out.println(format.format(UuidHelper.getTimeFromId(id) *
		// 1000));
		for (int i = 0; i < 100; i++) {
			long id = getIdByDate(new Date());
			System.out.println(id);
			//System.out.println(getDateFromId(id));
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println(~25);
		
	}
	
	
}
