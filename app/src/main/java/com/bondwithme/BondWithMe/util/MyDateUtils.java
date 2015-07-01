package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.bondwithme.BondWithMe.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * 日期工具类 主要负责处理时间java.util.Date与String类型的转换
 * 
 * @author wangtao
 */
public class MyDateUtils extends android.text.format.DateUtils {
	private static Logger log = Logger.getLogger(DateUtils.class.toString());
	private static SimpleDateFormat defaultDateFormat = null;

    public static String formatTimeStampString(Context context, long when, boolean fullFormat) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();
        // Basic settings for formatDateTime() we want for all cases.
        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |
                DateUtils.FORMAT_ABBREV_ALL |
                DateUtils.FORMAT_CAP_AMPM;
        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            if(now.yearDay - then.yearDay == 1) {
                // 一天前的更新
                format_flags |= DateUtils.FORMAT_SHOW_TIME;
                String result = context.getString(R.string.yesterday_ago) + DateUtils.formatDateTime(context, when, format_flags);
                return result;
            }
            format_flags |= DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;
        } else {
            // Otherwise, if the message is from today, show the time.
            int hour = now.hour  - then.hour;
            int minute = now.minute - then.minute;
            if(hour < 1 && minute <= 1){
                // 1分之内的更新
                return context.getString(R.string.just_now);
            } else {
                // 当天1分钟之后的更新
                format_flags |= DateUtils.FORMAT_SHOW_TIME;
            }
//            format_flags |= DateUtils.FORMAT_SHOW_TIME;
        }
        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        if (fullFormat) {
            format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        }
        return DateUtils.formatDateTime(context, when, format_flags);
    }

    public static String EventformatTimeStampString(Context context, long when, boolean fullFormat) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();
        // Basic settings for formatDateTime() we want for all cases.
        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |
                DateUtils.FORMAT_ABBREV_ALL |
                DateUtils.FORMAT_CAP_AMPM;
        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            if(now.yearDay - then.yearDay == 1) {
                // 一天前的更新
                format_flags |= DateUtils.FORMAT_SHOW_TIME;
                String result = context.getString(R.string.yesterday_ago) + DateUtils.formatDateTime(context, when, format_flags);
                return result;
            }
            format_flags |= DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;
        } else {
            // Otherwise, if the message is from today, show the time.
            int hour = now.hour  - then.hour;
            int minute = now.minute - then.minute;
            if(hour < 1 && minute <= 1){
                // 1分之内的更新
//                return context.getString(R.string.just_now);
            } else {
                // 当天1分钟之后的更新
                format_flags |= DateUtils.FORMAT_SHOW_TIME;
            }
//            format_flags |= DateUtils.FORMAT_SHOW_TIME;
        }
        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        if (fullFormat) {
            format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        }
        return DateUtils.formatDateTime(context, when, format_flags);
    }
    private static SimpleDateFormat getDefaultDateFormat(){
        if(defaultDateFormat==null){
            defaultDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        }
        return defaultDateFormat;
    }

    private static String formatDate2Default(Date date){
        getDefaultDateFormat().setTimeZone(TimeZone.getDefault());
        return defaultDateFormat.format(date);
    }

    /**
     * Default Date format like yyy-MM-dd HH:mm:ss
     * @param dateString
     * @return
     */
    public static String getLocalDateString4DefaultFromUTC(String dateString){
        return formatDate2Default(new Date(dateString2Timestamp(dateString).getTime() + TimeZone.getDefault().getRawOffset()));
    }
    /**
     * show Date time format is auto by current time
     * @param context
     * @param dateString
     * @return
     */
    public static String getLocalDateStringFromUTC(Context context, String dateString){
        if(TextUtils.isEmpty(dateString))
            return null;
        return formatTimeStampString(context, dateString2Timestamp(dateString).getTime() + TimeZone.getDefault().getRawOffset(), false);
    }
    //event 专用
    public static String getEventLocalDateStringFromUTC(Context context,String dateString){
        if(TextUtils.isEmpty(dateString))
            return null;
        return EventformatTimeStampString(context, dateString2Timestamp(dateString).getTime() + TimeZone.getDefault().getRawOffset(), true);
//        return formatTimeStampString(context, dateString2Timestamp(dateString).getTime() + TimeZone.getDefault().getRawOffset(), true);

    }

    public static String getLocalDateStringFromLocal(Context context,  long timestamp){
        return  formatTimeStampString(context, timestamp, true);
    }
    public static String getEventLocalDateStringFromLocal(Context context,  long timestamp){
        return  EventformatTimeStampString(context, timestamp, true);
    }

    public static String getUTCDateString4DefaultFromLocal(long timestamp){
        return formatDate2Default(new Date(formatTimestamp2UTC(timestamp)));
    }
    public static String getEventUTCDateString4DefaultFromLocal(long timestamp){
        return formatDate2Default(new Date(formatTimestamp2UTC(timestamp)));
    }

    /**
     * Default Date format like yyy-MM-dd HH:mm:ss
     * @param timestamp
     * @return
     */
    public static String getUTCDateString4DefaultFromUTC(long timestamp){
        return formatDate2Default(new Date(timestamp));
    }

    public static String getUTCDateStringFromUTC(Context context, long timestamp){
        return formatTimeStampString(context, formatTimestamp2Local(timestamp), true);
    }

    public static Timestamp dateString2Timestamp(String date){
        return Timestamp.valueOf(date);
    }


    public static long formatTimestamp2UTC(long timestamp){
        return timestamp-TimeZone.getDefault().getRawOffset();
    }

    public static long formatTimestamp2Local(long timestamp){
        return timestamp+TimeZone.getDefault().getRawOffset();
    }

    public static boolean isBeforeDate(long timestamp){
        return timestamp<System.currentTimeMillis();
    }



}