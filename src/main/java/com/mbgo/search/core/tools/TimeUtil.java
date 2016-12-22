package com.mbgo.search.core.tools;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 时间工具类
 * @author QuYachu
 *
 */
public class TimeUtil {
	
	public static String dateFormatStr= "yyyy-MM-dd HH:mm:ss";
	private static final Logger logger = Logger.getLogger(TimeUtil.class);
	
	/**
	 *  获取当前时间的字符串  格式 为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getNowDateStr(){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
		return sdf.format(new Date());
	}

	/**
	 *  获取时间的字符串 格式 为"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getStrFromDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
		return sdf.format(date);
	}
	
	/**
	 * 将时间转换成指定格式的字符日期
	 * @param date
	 * @param formate
	 * @return
	 */
	public static String getStrDateByFormate(Date date, String formate) {
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		return sdf.format(date);
	}
	
	/**
	 * 通过字符串获取 时间  格式 为"yyyy-MM-dd HH:mm:ss"
	 * @param dateStr
	 * @return
	 */
	public static Date getDateFromStr(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
		Date resultDate = null;
		try {
			resultDate = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error("dateStr = "+dateStr+" can't change to Date!");
			logger.error(e.getMessage());
			resultDate = new Date();
		}
		return resultDate;
	}
	
	/**
	 * 将一个"yyyyMMddHHmm"字符串转成日期格式
	 */
	public static Date getDateByString(String str){
		Date time = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");
			time = sf.parse(str);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return time;
	}
	
	/**
	 * 将一个"yyyyMMddHHmm"字符串转成日期格式
	 */
	public static Date getDateByString(String str, String formate){
		Date time = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(formate);
			time = sf.parse(str);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return time;
	}
	
	/**
	 * 今天的"yyMMdd"日期字符串
	 */	
	public static String getTodayStr(){
		SimpleDateFormat sf = new SimpleDateFormat("yyMMdd");
		Date now=new Date();
		return sf.format(now);
	}
	
	/**
	 * 获取大于或者小于指定日期的日期
	 * @param date 指定日期
	 * @param day 1表示明天|-1表示昨天
	 * @return 
	 */
	public static Date getBeforeDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.DATE, day);
		
		return cal.getTime();
	}
	
	/**
	 * 获取大于或者小于当前日期的日期
	 * @param day
	 * @return
	 */
	public static Date getBeforeDay(int day) {
		return getBeforeDay(new Date(), day);
	}
	
	/**
	 * 获取Timestamp
	 * @param date
	 * @return
	 */
	public static Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}
	
	public static void main(String[] args) {
		System.out.println(getStrDateByFormate(new Date(), "HH"));
	}
}
