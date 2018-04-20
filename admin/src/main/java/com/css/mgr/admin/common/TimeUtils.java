package com.css.mgr.admin.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	/**
	 * 得到秒级的当前时间
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(new Date());
	}
	public static String getBkTimestamp() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
		return formater.format(new Date());
	}
	public static String getBkDate() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		return formater.format(new Date());
	}
	/**
	 * 指定日期格式，获取当前时间<br>
	 * 默认：yyyy-MM-dd
	 * @return
	 */
	public static String getCurrentDate(String dateType) {
		if (dateType == null || "".equals(dateType)) {
			dateType = "yyyy-MM-dd";
		}
		SimpleDateFormat formater = new SimpleDateFormat(dateType);
		return formater.format(new Date());
	}
	/**
	 * 获取当前时间|yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		return getCurrentDate(null);
	}
	/**
	 * 指定日期格式转换日期<br>
	 * 默认：yyyy-MM-dd
	 * @return
	 */
	public static String convertCurrentDate(Date date, String dateType) {
		if (dateType == null || "".equals(dateType)) {
			dateType = "yyyy-MM-dd";
		}
		SimpleDateFormat formater = new SimpleDateFormat(dateType);
		return formater.format(date);
	}
	/**
	 * 指定日期格式转换日期|yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String convertCurrentDate(Date date){
		return convertCurrentDate(date,null);
	}
	
	/**
	 * 日期格式转换,格式为yyyy-MM-dd HH:mm:ss,返回String类型
	 * 
	 * @return
	 */
	public static String convertTimestamp(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date);
	}

	/**
	 * 日期格式转换,格式为yyyy-MM-dd HH:mm:ss,返回String类型
	 * 
	 * @return
	 */
	public static Date getTimestamp(String date) throws Exception {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.parse(date);
	}

	/**
	 * 得到当前时间,格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getDaystamp() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(new Date());
	}

	/**
	 * 日期类型转换,格式为yyyy-MM-dd，返回Date类型
	 * 
	 * @return
	 */
	public static Date getDaystamp(String date) throws Exception {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		return formater.parse(date);
	}
	public static Date get0Daystamp(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}
	public static Date get24Daystamp() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,23);
		cal.set(Calendar.MINUTE,59);
		cal.set(Calendar.SECOND,59);
		cal.set(Calendar.MILLISECOND,59);
		return cal.getTime();
	}
	/**
	 * 日期类型转换,格式为yyyy-MM-dd，返回String类型
	 * 
	 * @return
	 */
	public static String convertDaystamp(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		return formater.format(date);
	}

	/**
	 * 日期类型转换,格式为yyyy，返回String类型
	 * 
	 * @return
	 */
	public static String convertYearstamp(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy");
		return formater.format(date);
	}

	/**
	 * 得到分级的当前时间<br>
	 * 默认：yyyy-MM-dd HH:mm
	 * @return
	 */
	public static String getMinutestamp() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formater.format(new Date());
	}

	/**
	 * 日期类型转换，返回String类型<br>
	 * 格式为yyyy-MM-dd HH:mm
	 * @return
	 */
	public static String convertMinutestamp(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formater.format(date);
	}

	/**
	 * 得到当前时间之前time毫秒的时间值，返回秒级别<br>
	 * 格式为yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String getBeforTimestamp(long time) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt = new Date();
		dt.setTime(dt.getTime() - time);
		return formater.format(dt);
	}

	/**
	 * 得到当前时间之后time毫秒的时间,返回秒级别<br>
	 * 格式为yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String getAfterTimestamp(long time) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt = new Date();
		dt.setTime(dt.getTime() + time);
		return formater.format(dt);
	}

	/**
	 * 判断是否超时，limit对比当前时间超时time毫秒返回true
	 * 
	 * @param time  超时毫秒
	 * @param limit  比较的时间
	 * @return
	 */
	public static boolean getTimestamp(long time, long limit) {
		Date dt = new Date();
		dt.setTime(dt.getTime() - time);
		if (dt.getTime() >= limit) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取锁定time毫秒时间，返回格式"xx时：xx分：xx秒"<br>
	 * 如：128小时23分00秒
	 * @param time毫秒
	 * @return
	 */
	public static String getTime(long time) {
		Date dt = new Date();
		dt.setTime(dt.getTime() - time);
		Long locktime = dt.getTime() / 1000;
		if (locktime > 0L) {
			String display = String.format("%02d时:%02d分:%02d秒",
					locktime / 3600, (locktime % 3600) / 60, (locktime % 60));
			return display;
		} else {
			return "";
		}
	}

	/**
	 * 获取当前时间里的小时,返回整型
	 * 
	 * @param time
	 * @return
	 */
	public static int getHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取当前时间里的分，返回整型
	 * 
	 * @return
	 */
	public static int getMin() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * 获取当前时间里的秒，返回整型
	 * 
	 * @return
	 */
	public static int getSec() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal.get(Calendar.SECOND);
	}

	/**
	 * @param date 转换前的时间
	 * @param pattern 转换格式
	 * */
	public static String formatDate(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * @param strDate 转换前的时间
	 * @param pattern 转换格式
	 * */
	public static Date formatDate(String strDate, String pattern)
			throws ParseException {
		if (strDate == null || "".equals(strDate)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(strDate);
	}
}
