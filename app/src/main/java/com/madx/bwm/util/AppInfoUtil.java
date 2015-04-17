package com.madx.bwm.util;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by wing on 15/3/22.
 */
public class AppInfoUtil {

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static String getDeviceUUID(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


}
