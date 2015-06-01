package com.madx.bwm.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.List;

/**
 * Created by wing on 15/3/22.
 */
public class SystemUtil {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            /**提示安装google 服务*/
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, context,
//                       PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i("AppInfoUtil", "This device is not supported.");
//                context.finish();
//            }
            return false;
        }
        return true;
    }

    /**
     * 判断根据包名判断应用是否安装
     * @param targetPackage
     * @return
     */
    public static boolean isPackageExists(Context context,String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }


}
