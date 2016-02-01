package com.madxstudio.co8.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.madxstudio.co8.Constant;

/**
 * Created by wing on 15/3/22.
 */
public class AppInfoUtil {

    private final static String TAG = "AppInfoUtil";
    public static final String APP_NAME = "BWM";

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static String getDeviceUUID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's version name from the {@code PackageManager}.
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's version name from the {@code PackageManager}.
     */
    public static String getAppPackageName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getGCMRegistrationId(Context context) {
        String registrationId = PreferencesUtil.getValue(context, Constant.GCM_PREF_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = PreferencesUtil.getValue(context, Constant.GCM_PREF_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = AppInfoUtil.getAppVersionCode(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static String getJpushRegistrationId(Context context) {
        String registrationId = PreferencesUtil.getValue(context, Constant.JPUSH_PREF_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = PreferencesUtil.getValue(context, Constant.JPUSH_PREF_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = AppInfoUtil.getAppVersionCode(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    public static void storeRegistrationId(Context context, String regId, boolean isGCM) {
        int appVersion = getAppVersionCode(context);
        if (isGCM) {
            PreferencesUtil.saveValue(context, Constant.GCM_PREF_REG_ID, regId);
            PreferencesUtil.saveValue(context, Constant.GCM_PREF_APP_VERSION, appVersion);
        } else {
            PreferencesUtil.saveValue(context, Constant.JPUSH_PREF_REG_ID, regId);
            PreferencesUtil.saveValue(context, Constant.JPUSH_PREF_APP_VERSION, appVersion);
        }
    }



}
