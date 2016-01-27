package com.bondwithme.BondCorp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bondwithme.BondCorp.Constant;

/**
 * SharedPreferences工具适配
 */
public class PreferencesUtil {

    /**
     * 保存SharedPreferences数据库中的整型值
     *
     * @param context
     * @param key
     * @return
     */
    public static void saveValue(Context context, String key, int value) {
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            Editor edit = pre.edit();
            edit.putInt(key, value);
            edit.commit();
        }
    }

    /**
     * 保存SharedPreferences数据库中的long
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static void saveValue(Context context, String key, long value) {
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            Editor edit = pre.edit();
            edit.putLong(key, value);
            edit.commit();
        }
    }

    /**
     * 保存SharedPreferences数据库中的float
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static void saveValue(Context context, String key, float value) {
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            Editor edit = pre.edit();
            edit.putFloat(key, value);
            edit.commit();
        }
    }

    /**
     * 保存SharedPreferences数据库中的String
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static void saveValue(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            Editor edit = pre.edit();
            edit.putString(key, value);
            edit.commit();
        }
    }

    /**
     * 保存SharedPreferences数据库中的boolean
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static void saveValue(Context context, String key, boolean value) {
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            Editor edit = pre.edit();
            edit.putBoolean(key, value);
            edit.commit();
        }
    }

    /** TODO 如下定义各种默认SharedPreferences获取数据函数 */

    /**
     * 获取SharedPreferences数据库中的整型值
     *
     * @param context
     * @param key
     * @param defaule
     * @return
     */
    public static int getValue(Context context, String key, int defaule) {

        int value = defaule;
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            value = pre.getInt(key, defaule);
        }
        return value;
    }

    /**
     * 获取SharedPreferences数据库中的long
     *
     * @param context
     * @param key
     * @param defaule
     * @return
     */
    public static long getValue(Context context, String key, long defaule) {

        long value = defaule;
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            value = pre.getLong(key, defaule);
        }
        return value;
    }

    /**
     * 获取SharedPreferences数据库中的float
     *
     * @param context
     * @param key
     * @param defaule
     * @return
     */
    public static float getValue(Context context, String key, float defaule) {

        float value = defaule;
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            value = pre.getFloat(key, defaule);
        }
        return value;
    }

    /**
     * 获取SharedPreferences数据库中的String
     *
     * @param context
     * @param key
     * @param defaule
     * @return
     */
    public static String getValue(Context context, String key, String defaule) {

        String value = defaule;
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            value = pre.getString(key, defaule);
        }
        return value;
    }

    /**
     * 获取SharedPreferences数据库中的boolean
     *
     * @param context
     * @param key
     * @param defaule
     * @return
     */
    public static boolean getValue(Context context, String key, boolean defaule) {

        boolean value = defaule;
        if (context != null) {
            SharedPreferences pre = context.getSharedPreferences(Constant.PREFERRENCE_NAME, Context.MODE_PRIVATE);
            value = pre.getBoolean(key, defaule);
        }
        return value;
    }
}
