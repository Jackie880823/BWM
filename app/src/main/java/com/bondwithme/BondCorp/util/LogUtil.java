package com.bondwithme.BondCorp.util;

/**
 * Created by wing on 15/6/18.
 */
import android.util.Log;

public class LogUtil {
    /**debug 输出控制等级 TODO release 把值设为0*/
//    private static final int DEBUG_LEVEL = 0;// 0:none;1:error;2:error and warning// 3:e,w,d;4:e,w,d,i;5:all
    private static final int DEBUG_LEVEL = 5;// 0:none;1:error;2:error and warning// 3:e,w,d;4:e,w,d,i;5:all

    public static void e(String tag, String msg) {
        if (DEBUG_LEVEL >= 1) {
            Log.e(tag, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (DEBUG_LEVEL >= 2) {
            Log.w(tag, msg);
        }

    }

    public static void d(String tag, String msg) {
        if (DEBUG_LEVEL >= 3) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG_LEVEL >= 4) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG_LEVEL >= 5) {
            Log.v(tag, msg);
        }
    }


    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG_LEVEL >= 1) {
            Log.e(tag, msg,tr);
        }

    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG_LEVEL >= 2) {
            Log.w(tag, msg,tr);
        }

    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG_LEVEL >= 3) {
            Log.d(tag, msg,tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG_LEVEL >= 4) {
            Log.i(tag, msg,tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG_LEVEL >= 5) {
            Log.v(tag, msg,tr);
        }
    }
}