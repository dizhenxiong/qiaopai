package com.lenovo.vctl.apps.commons.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;


/**
 * User: caiyingjie
 * Date: 2008-12-26
 * Time: 14:09:16
 * Version: $Id: TimeUtil.java 74781 2009-08-14 09:02:26Z caiyingjie $
 */
public class TimeUtil {
// -------------------------- STATIC METHODS --------------------------

    /**
     * 判断是不是在同一个小时
     *
     * @param firTime 第一个时间点
     * @param secTime 第二个时间点
     * @return 是不是在同一个小时
     */
    public static boolean sameHour(long firTime, long secTime) {
        return getHour(new Date(firTime)) == getHour(new Date(secTime));
    }

    /**
     * 取得指定时间的小时数
     *
     * @param date 指定时间
     * @return 小时数
     */
    public static int getHour(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 某一时间是不是标注时间的同一天
     *
     * @param currentDate 标准时间
     * @param judgeDate   被判断时间
     * @return 判断结果
     */
    public static boolean isToday(Date currentDate, Date judgeDate) {
        return judgeDate.compareTo(getDayBegin(currentDate)) >= 0 && judgeDate.compareTo(getDayEnd(currentDate)) < 0;
    }

    public static Date getDayEnd(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date nextDayBegin = getDayBegin(calendar.getTime());
        return new Date(nextDayBegin.getTime() - 1);
    }

    public static Date getDayBegin(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Long getHourBegin(Long current) {
    	GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(current);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    /**
     * 某一时间是不是标准时间的昨天
     *
     * @param currentDate 标准时间
     * @param judgeDate   被判断时间
     * @return 判断结果
     */
    public static boolean isYesterday(Date currentDate, Date judgeDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);

        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        return judgeDate.compareTo(getDayBegin(yesterday)) >= 0 && judgeDate.compareTo(getDayEnd(yesterday)) < 0;
    }

    /**
     * 某一时间是不是标准时间的同一周
     *
     * @param currentDate 标准时间
     * @param judgeDate   被判断时间
     * @return 判断结果
     */
    public static boolean isThisWeek(Date currentDate, Date judgeDate) {
        return judgeDate.compareTo(getWeekBegin(currentDate)) >= 0 && judgeDate.compareTo(getWeekEnd(currentDate)) < 0;
    }

    public static Date getWeekEnd(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_MONTH, 1);
        Date nextWeekBegin = getDayBegin(calendar.getTime());
        return new Date(nextWeekBegin.getTime() - 1);
    }

    public static Date getWeekBegin(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 某一时间是不是标准时间的上一周
     *
     * @param currentDate 标准时间
     * @param judgeDate   被判断时间
     * @return 判断结果
     */
    public static boolean isLastWeek(Date currentDate, Date judgeDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);

        calendar.add(Calendar.WEEK_OF_MONTH, -1);
        Date lastWeek = calendar.getTime();

        return judgeDate.compareTo(getWeekBegin(lastWeek)) >= 0 && judgeDate.compareTo(getWeekEnd(lastWeek)) < 0;
    }

    /**
     * 某一时间是不是标准时间的同一月
     *
     * @param currentDate 标准时间
     * @param judgeDate   被判断时间
     * @return 判断结果
     */
    public static boolean isThisMonth(Date currentDate, Date judgeDate) {
        return judgeDate.compareTo(getMonthBegin(currentDate)) >= 0 && judgeDate.compareTo(getMonthEnd(currentDate)) < 0;
    }

    public static Date getMonthEnd(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        Date nextMonthBegin = getMonthBegin(calendar.getTime());
        return new Date(nextMonthBegin.getTime() - 1);
    }

    public static Date getMonthBegin(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 某一时间是不是标准时间的上一月
     *
     * @param currentDate 标准时间
     * @param judgeDate   被判断时间
     * @return 判断结果
     */
    public static boolean isLastMonth(Date currentDate, Date judgeDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);

        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();

        return judgeDate.compareTo(getMonthBegin(lastMonth)) >= 0 && judgeDate.compareTo(getMonthEnd(lastMonth)) < 0;
    }
    
    /**
     * 获取date所属年份的第一天起始时间
     * @param date
     * @return
     */
    public static Date getYearBegin(Date date){
    	 GregorianCalendar calendar = new GregorianCalendar();
         calendar.setTime(date);
         calendar.set(Calendar.MONTH, 0);
         calendar.set(Calendar.DAY_OF_MONTH, 1);
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         return calendar.getTime();
    }
    
    /**
     * 获取date所属年份的最后一天结束时间
     * @param date
     * @return
     */
    public static Date getYearEnd(Date date){
   	 GregorianCalendar calendar = new GregorianCalendar();
   	 calendar.setTime(date);
     calendar.set(Calendar.MONTH, 12);
     Date nextMonthBegin = getMonthBegin(calendar.getTime());
     return new Date(nextMonthBegin.getTime() - 1);
   }
    
    
    /**
     * 获取指定年份的第一天时间
     * @param year
     * @return
     */
    public static Date getYearBegin(Integer year){
   	   GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, 1, 0);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
   }
    
    /**
     * 获取指定年份的第一天时间
     * @param year
     * @return
     */
	public static Date getYearEnd(Integer year) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(year, 13, 0);
		Date nextMonthBegin = getMonthBegin(calendar.getTime());
		return new Date(nextMonthBegin.getTime() - 1);
	}
    
    
    
    
    /**
	 * 获取最近4年的年份
	 * @param yearCount 获取年份数
	 * @param isContainsCurrentYear 是否包含当前时间的年份
	 * @return
	 */
	public static List<Integer> getLastYearList(int yearCount,boolean isContainsCurrentYear){
		List<Integer> yearList = new ArrayList<Integer>();
		Calendar c = Calendar.getInstance();
		int thisYear = c.get(Calendar.YEAR);
		for (int i = 0; i <= yearCount; i++) {
			if(i == 0 && !isContainsCurrentYear){
				continue;
			}
			if(isContainsCurrentYear && i == yearCount){
				break;
			}
			yearList.add(thisYear - i);
		}
		return yearList;
	}
	
	
	  /**
	 * 获取最近n年的年份
	 * @param yearCount 获取年份数
	 * @param isContainsCurrentYear 是否包含当前时间的年份
	 * @return
	 */
	public static List<Integer> getLastYearList(Integer yearEnd,int yearCount){
		List<Integer> yearList = new ArrayList<Integer>();
		if(null == yearEnd){
			return yearList;
		}
		Integer thisYear = yearEnd;
		for (int i = 0; i < yearCount; i++) {
			yearList.add(thisYear - i);
		}
		return yearList;
	}
	
	/**
	 * 获取最近n年的年份
	 * @param yearEnd 获取年份数
	 * @param yearCount 是否包含当前时间的年份
	 * @return 年份值,索引
	 */
	public static Map<Integer,Integer> getLastYearIndexMap(Integer yearEnd,int yearCount){
		Map<Integer,Integer> yearMap = new HashMap<Integer, Integer>();
		if(null == yearEnd){
			return yearMap;
		}
		Integer thisYear = yearEnd;
		for (int i = 0; i < yearCount; i++) {
			yearMap.put(thisYear - i , i );
		}
		return yearMap;
	}
	
	/**
	 * 根据起始时间和结束时间获取期间的日期列表(包含起始时间和结束时间)
	 * @param start 起始日期
	 * @param end 结束日期
	 * @return
	 */
	public static List<Long> getDayTimeByStartAndEndDate4TimeMillis(Date start,Date end){
		if(null == start && null == end ){
			return new LinkedList<Long>();
		}
		List<Long> dayList = new LinkedList<Long>();
		if(null == end){
			dayList.add(start.getTime());
			return dayList;
		}
		if(null == start){
			dayList.add(end.getTime());
			return dayList;
		}
		if(start.after(end)){
			dayList.add(start.getTime());
			return dayList;
		}
		
		while (start.before(end) || start.equals(end)){
			dayList.add(start.getTime());
			start = DateUtils.addDays(start, 1);
		}
		return dayList;
	}
	
	
	/**
	 * 根据起始时间和结束时间获取期间的日期列表(包含起始时间和结束时间)
	 * @param start 起始日期
	 * @param end 结束日期
	 * @return
	 */
	public static List<Integer> getDayTimeByStartAndEndDate4YyyyMMdd(Date start,Date end){
		if(null == start && null == end ){
			return new LinkedList<Integer>();
		}
		List<Integer> dayList = new LinkedList<Integer>();
		if(null == end){
			dayList.add(CalendarUtil.convertTimeMillis2yyyyMMdd(start.getTime()));
			return dayList;
		}
		if(null == start){
			dayList.add(CalendarUtil.convertTimeMillis2yyyyMMdd(end.getTime()));
			return dayList;
		}
		if(start.after(end)){
			dayList.add(CalendarUtil.convertTimeMillis2yyyyMMdd(start.getTime()));
			return dayList;
		}
		
		while (start.before(end) || start.equals(end)){
			dayList.add(CalendarUtil.convertTimeMillis2yyyyMMdd(start.getTime()));
			start = DateUtils.addDays(start, 1);
		}
		return dayList;
	}
	
	/**
	 * 根据起始时间和结束时间获取期间的日期列表(包含起始时间和结束时间)
	 * @param start 起始日期
	 * @param end 结束日期
	 * @return
	 */
    public static List<Date> getDayTimeByStartAndEndDate4Date(Date start,Date end){
    	if(null == start && null == end ){
			return new LinkedList<Date>();
		}
		List<Date> dayList = new LinkedList<Date>();
		if(null == end){
			dayList.add(start);
			return dayList;
		}
		if(null == start){
			dayList.add(end);
			return dayList;
		}
		
		if(start.after(end)){
			dayList.add(start);
			return dayList;
		}
		
		while (start.before(end) || start.equals(end)){
			dayList.add(start);
			start = DateUtils.addDays(start, 1);
		}
		return dayList;
	}
    
    /**
	 * 根据起始时间和结束时间获取期间的int 类型的天的列表(包含起始时间和结束时间)
	 * @param start 起始日期 
	 * @param end 结束日期
	 * 1：如果起始时间 和结束时间相同则返回 同一天。
	 * 2：如果起始和结束时间为空则返回当天；
	 * 3：如果有起始无结束，则结束时间默认为今天。
	 * 4：如果有结束无起始，则时间为结束的那一天。
	 * @return
	 */
    public static List<Integer> getDayTimeByStartAndEndDate4Integer(Long start,Long end){
    	DateFormat yyyyMMddFormat = new SimpleDateFormat("yyyyMMdd");
    	List<Integer> dayList = new ArrayList<Integer>();
    	if(null == start && null == end){
    		Date today = new Date();
    		Integer todayInt = Integer.valueOf(yyyyMMddFormat.format(today));
    		dayList.add(todayInt);
			return dayList;
		}
    	if(null == start && null != end){
    		dayList.add(Integer.valueOf(yyyyMMddFormat.format(new Date(end))));
    		return dayList;
    	}
    	if(null == end){
    		end = System.currentTimeMillis();
    	}
		Date startDate = new Date(start); 
		Date endDate = new Date(end); 
		Integer startDay = Integer.valueOf(yyyyMMddFormat.format(startDate));
		Integer endDay = Integer.valueOf(yyyyMMddFormat.format(endDate));
		if(startDay.equals(endDay)){
			dayList.add(startDay);
			return dayList;
		}
		while (startDate.before(endDate) || start.equals(end)){
			dayList.add(startDay);
			startDate = DateUtils.addDays(startDate, 1);
			startDay ++;
		}
		return dayList;
	}
	

    /**
     * 根据毫秒数获取所属年份
     * @param dateTimeMillis
     * @return
     */
    public static Integer getYear(Long dateTimeMillis){
    	if(null == dateTimeMillis){
    		return null;
    	}
    	 GregorianCalendar calendar = new GregorianCalendar();
    	 calendar.setTimeInMillis(dateTimeMillis);
    	return calendar.get(Calendar.YEAR);
    }
    
	/**
	 * 根据毫秒数获取Date
	 * 
	 * @param dateTimeMillis
	 * @return
	 */
	public static Date getDateByTimeMillis(Long dateTimeMillis) {
		if (null == dateTimeMillis) {
			return null;
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(dateTimeMillis);
		return calendar.getTime();
	}
    
	
	/**
	 * 获取需要计算的年份倒序排列
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Integer> getYearListDesc(Date start,Date end){
		if(null == start && null == end){
			return new LinkedList<Integer>();
		}
		List<Integer> yearList = new LinkedList<Integer>();
		if(null == start){
			yearList.add(getYear(end.getTime()));
			return yearList;
		}
		if(null == end){
			yearList.add(getYear(start.getTime()));
			return yearList;
		}
		
		if(end.before(start)){
			yearList.add(getYear(start.getTime()));
			return yearList;
		}
		int yearStart = getYear(start.getTime());
		int yearEnd = getYear(end.getTime());
		
		while(yearEnd >= yearStart){
			yearList.add(yearEnd);
			yearEnd --;
		}
		
		return yearList;
	}

	public static Long getSameDayInLastYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 取离当前时间最近的固定时间点：比如将一小时分为60份，当前时间为1分35秒，则离当前时间最近的固定时间点为1分钟
	 * @param current ：当前时间
	 * @param perTrunck : 每份为多少秒
	 * @return
	 */
	public static Long getLatestHourFixedTimePoint(Long current, Integer perTrunck) {
		Long hourBegin = getHourBegin(current);
		Long diftimes = current - hourBegin;
		Long per = Long.valueOf(perTrunck*1000);
		Long fixDiftimes = (diftimes/per) * per;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(hourBegin);
		calendar.add(Calendar.MILLISECOND, fixDiftimes.intValue());
		
		return calendar.getTimeInMillis();
	}
	
}
