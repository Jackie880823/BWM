package com.madx.bwm.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * 日期工具类 主要负责处理时间java.util.Date与String类型的转换
 * 
 * @author wangtao
 */
public class DateUtils {
	private static Logger log = Logger.getLogger(DateUtils.class.toString());
	private static DateFormat defaultDateFormat = null;

	/**
	 * 默认的格式化格式为 yyyy-MM-dd HH:mm:ss
	 */
	// private static SimpleDateFormat defaultDateFormat = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取系统日期格式，不带时间
	 * 
	 * @param context
	 * @return
	 */
	public static DateFormat getSystemDateFormatNoTime(Context context) {
		if (defaultDateFormat == null) {
			final String formatDate = Settings.System.getString(
					context.getContentResolver(), Settings.System.DATE_FORMAT);

			if (TextUtils.isEmpty(formatDate)) {
				return android.text.format.DateFormat
						.getMediumDateFormat(context.getApplicationContext());
			} else {
				Locale locale = context.getResources().getConfiguration().locale;
				return new SimpleDateFormat(formatDate, locale);
			}
		}
		return defaultDateFormat;
	}

	/**
	 * 解析字符串,返回日期
	 * 
	 * @param
	 * @return 返回日期
	 */
	public static Date parse(String value) {
		try {
			return defaultDateFormat.parse(value);
		} catch (Exception e) {
			try {
				defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				return defaultDateFormat.parse(value);
			} catch (Exception ex) {
				try {
					defaultDateFormat = new SimpleDateFormat("HH:mm:ss");
					return defaultDateFormat.parse(value);
				} catch (Exception exc) {
					// exc.printStackTrace();
					log.warning("不能格式化指定的值: " + value);
				}
			}
			return null;
		}
	}

//	 //获取当前日期-时间
//	public static String getCurDateTime(Context context) {
//		DateFormat format = getSystemDateFormatNoTime(context);
//		try {
//			return format.format(new Date());
//		} catch (Exception e) {
//			return null;
//		}
//	}

	public static int getYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}

	public static int getMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getDay() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/***
	 * //86400=60*60*24
	 * @param timestamp
	 * @return
	 */
	public static long getDayByTimestamp(long timestamp){
		return timestamp/86400;//86400=60*60*24
	}

	/**
	 * 根据系统格式返回时间
	 * 
	 * @return
	 */
	public static String getTime(Context context) {
		Calendar cal = Calendar.getInstance();
		int hour = 0;
		int minute = cal.get(Calendar.MINUTE);
		String suffix = "";
		int am_pm = cal.get(Calendar.AM_PM);
		if (android.text.format.DateFormat.is24HourFormat(context)) {
			hour = cal.get(Calendar.HOUR_OF_DAY);
		} else {
			hour = cal.get(Calendar.HOUR);
			if (am_pm == Calendar.AM) {
				suffix = "AM";
			} else {
				suffix = "PM";
			}
		}
		String timeString;
		if (hour < 10) {
			if (minute < 10) {
				timeString = "0" + hour + ":0" + minute + suffix;
			} else
				timeString = "0" + hour + ":" + minute + suffix;
		} else {
			if (minute < 10) {
				timeString = hour + ":0" + minute + suffix;
			} else
				timeString = hour + ":" + minute + suffix;
		}
		return timeString;
	}

	// public static long date2long(String datestr, String format) {
	// try {
	// SimpleDateFormat sdf = new SimpleDateFormat(format);
	// Date date = sdf.parse(datestr);
	// return date.getTime();
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return 0;
	// }

	/**
	 * only to second
	 * 
	 * @return
	 */
	public static Long getUnixTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 根据系统格式返回unix timestamp中的日期,不包括时间(如2011-9-12)
	 * 
	 * @return
	 */
	public static String long2date(Context context, long datelong) {
		//根据系统日期格式
//		final DateFormat formater = getSystemDateFormatNoTime(context);
		SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date(datelong * 1000);
		return formater.format(date);
	}

	/**
	 * 根据系统格式返回unix timestamp中的时间,不包括日期(如12:23:14)
	 * 
	 * @return
	 */
	public static String long2time(Context context, long datelong) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(datelong * 1000);

		int hour = 0;
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		String suffix = "";
		int am_pm = cal.get(Calendar.AM_PM);
		if (android.text.format.DateFormat.is24HourFormat(context)) {
			// if(formatTime.equals("24")){
			hour = cal.get(Calendar.HOUR_OF_DAY);
		} else {
			// if(formatTime.equals("12")){
			hour = cal.get(Calendar.HOUR);
			if (am_pm == Calendar.AM) {
				suffix = " AM";
			} else {
				suffix = " PM";
			}
		}
		String dayString;
		String minuteString;
		// String secondString;
		if (hour < 10)
			dayString = "0" + hour;
		else {
			dayString = "" + hour;
		}
		if (minute < 10)
			minuteString = "0" + minute;
		else {
			minuteString = "" + minute;
		}
		// if (second < 10)
		// secondString = "0" + second;
		// else {
		// secondString = "" + second;
		// }
		// return dayString + ":" + minuteString + ":" + secondString + suffix;
		return dayString + ":" + minuteString + suffix;
	}
	
	
	public static Long hexString2Long(String date16String){
		return Long.parseLong(date16String, 16);
	}

}