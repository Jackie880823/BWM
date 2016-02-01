package com.madxstudio.co8.Tranck;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.util.AppInfoUtil;
import com.madxstudio.co8.util.PreferencesUtil;

import org.piwik.sdk.Piwik;
import org.piwik.sdk.Tracker;

import java.net.MalformedURLException;

/**
 * Created by christepherzhang on 15/8/27.
 */
public class MyPiwik {

    private static Tracker piwikTracker;
    private static App appContext = App.getContextInstance();

    private static org.piwik.sdk.Piwik getGlobalSettings(){
        return org.piwik.sdk.Piwik.getInstance(appContext);
    }

    public static Tracker getTracker() {
        if (piwikTracker != null) {
            return piwikTracker;
        }

        try {
            piwikTracker = getGlobalSettings().newTracker(getTrackerUrl(), getSiteId(), getAuthToken());
        } catch (MalformedURLException e) {
            return null;
        }

        return piwikTracker;

    }

    private static String getTrackerUrl() {
        return Constant.TRACKER_URL;
    }

    /**
     * AuthToken is deprecated in Piwik >= 2.8.0 due to security reasons.
     * @return token or null
     */
    private static String getAuthToken() {
        return null;
//        return "3bde48623ab1cea339c606abd09debd7";
    }


    private static Integer getSiteId() {
        return Constant.TRACKER_SITE_ID;
    }

    public static void piwikUser()
    {
        Piwik.getInstance(appContext).setAppOptOut(false);//启动piwik

        getGlobalSettings().setDryRun(false);//设置sent to Piwik

        getTracker()
                .setUserId(Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID))
                .setDispatchInterval(120)
                .setSessionTimeout(30);

        getTracker()
                .setUserCustomVariable(1, "App Name", AppInfoUtil.getAppPackageName(appContext))
                .setUserCustomVariable(2, "App Version", AppInfoUtil.getAppVersionName(appContext))
                .setUserCustomVariable(3, "Model", android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL)
                .setUserCustomVariable(4, "OS Version", android.os.Build.VERSION.RELEASE)
                .trackScreenView("");
    }


    private static  final  String META_DATA_APP_STORE = "UMENG_CHANNEL";

    public static void piwikGuest()
    {
        Piwik.getInstance(appContext).setAppOptOut(false);//启动piwik

        getGlobalSettings().setDryRun(false);//设置sent to Piwik

        getTracker()
                .setUserId("guest")
                .setDispatchInterval(120)
                .setSessionTimeout(30);

        getTracker()
                .setUserCustomVariable(1, "App Name", AppInfoUtil.getAppPackageName(appContext))
                .setUserCustomVariable(2, "App Version", AppInfoUtil.getAppVersionName(appContext))
                .setUserCustomVariable(3, "Model", android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL)
                .setUserCustomVariable(4, "OS Version", android.os.Build.VERSION.RELEASE)
                .trackAppDownload();

        //计算平台下载次数
        if (TextUtils.isEmpty(PreferencesUtil.getValue(appContext, Constant.HAS_DOWNLOAD, null))) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = appContext.getPackageManager()
                        .getApplicationInfo(appContext.getPackageName(),
                                PackageManager.GET_META_DATA);
                String msg = appInfo.metaData.getString(META_DATA_APP_STORE);
                getTracker()
                        .setScreenCustomVariable(1, "appstore", msg)
                        .trackScreenView("");
                Log.d("", "msg------" + msg);
                PreferencesUtil.saveValue(appContext, Constant.HAS_DOWNLOAD, Constant.HAS_DOWNLOAD);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            getTracker().trackScreenView("");
        }
    }

    public static void piwikNewUser()
    {

        Piwik.getInstance(appContext).setAppOptOut(false);//启动piwik

        getGlobalSettings().setDryRun(false);//设置sent to Piwik

        getTracker()
                .setUserId(Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID))
                .setDispatchInterval(0)
                .setSessionTimeout(0);

        getTracker()
                .setUserCustomVariable(1, "App Name", AppInfoUtil.getAppPackageName(appContext))
                .setUserCustomVariable(2, "App Version", AppInfoUtil.getAppVersionName(appContext))
                .setUserCustomVariable(3, "Model", android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL)
                .setUserCustomVariable(4, "OS Version", android.os.Build.VERSION.RELEASE)
                .trackEvent("Clicks", "Button", "new user");
    }

}
