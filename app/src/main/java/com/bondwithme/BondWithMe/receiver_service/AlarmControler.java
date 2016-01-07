package com.bondwithme.BondWithMe.receiver_service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmControler {

    public final static String FIVE_MINUTE_ACTION = "com.bondwithme.BondWithMe.android.five_minute_action";
    public final static String TWO_DAY_ACTION = "com.bondwithme.BondWithMe.android.two_day_action";
    public final static String THREE_DAY_ACTION = "com.bondwithme.BondWithMe.android.three_day_action";
    public final static String FIVE_DAY_ACTION = "com.bondwithme.BondWithMe.android.five_day_action";
    private static AlarmControler instance;

    private AlarmControler() {

    }

    public static AlarmControler getInstance() {
        if (instance == null) {
            instance = new AlarmControler();
        }
        return instance;
    }

    public void createAllTasks(Context mContext) {
        /**5分钟*/
//        createClock(mContext, 15 * 1000, FIVE_MINUTE_ACTION, null);
        createClock(mContext, 5 * 60 * 1000, FIVE_MINUTE_ACTION, null);
        /**2天*/
//        createClock(mContext, 20 * 1000, TWO_DAY_ACTION, null);
        createClock(mContext, 2 * 24 * 60 * 60 * 1000, TWO_DAY_ACTION, null);
//        createClock(mContext, 25 * 1000, THREE_DAY_ACTION, null);
        createClock(mContext, 3 * 24 * 60 * 60 * 1000, THREE_DAY_ACTION, null);
//        createClock(mContext, 30 * 1000, FIVE_DAY_ACTION, null);
        createClock(mContext, 5 * 24 * 60 * 60 * 1000, FIVE_DAY_ACTION, null);
    }

    public void cancleAllTasks(Context mContext) {
        cancleClock(mContext, FIVE_MINUTE_ACTION, null);
        cancleClock(mContext, TWO_DAY_ACTION, null);
        cancleClock(mContext, THREE_DAY_ACTION, null);
        cancleClock(mContext, FIVE_DAY_ACTION, null);
    }

    /**
     * 构建闹钟
     *
     * @param mContext
     * @param interval 延时时间，毫秒
     * @param action
     * @param extras
     */
    public void createClock(Context mContext, long interval, String action, Bundle extras) {
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setAction(action);
        if (extras != null) {
            intent.putExtras(extras);
        }
        PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 5秒后发送广播，只发送一次
        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendIntent);
    }

    /**
     * 销毁闹钟
     *
     * @param mContext
     * @param action
     * @param extras
     */
    public void cancleClock(Context mContext, String action, Bundle extras) {
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setAction(action);
        if (extras != null) {
            intent.putExtras(extras);
        }
        PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendIntent);
    }

}
