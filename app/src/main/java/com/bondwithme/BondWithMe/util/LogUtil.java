package com.bondwithme.BondWithMe.util;

/**
 * Created by wing on 15/6/18.
 */
import android.util.Log;

public class LogUtil {
    /**debug 输出控制等级 */
    private static boolean DEBUG = Log.isLoggable("LogUtil", Log.VERBOSE);

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }

    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }


    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(tag, msg,tr);
        }

    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.w(tag, msg,tr);
        }

    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(tag, msg,tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.i(tag, msg,tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.v(tag, msg,tr);
        }
    }
}