package com.bondwithme.BondWithMe.Tranck;

import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christepherzhang on 15/9/23.
 */
public class MyAppsFlyer {

    private static App appContext = App.getContextInstance();
    private static Map<String,Object> baseEvent = new HashMap<>();


    static
    {
        baseEvent.put(AFInAppEventParameterName.PARAM_1, AppInfoUtil.getAppPackageName(appContext));
        baseEvent.put(AFInAppEventParameterName.PARAM_2, AppInfoUtil.getAppVersionName(appContext));
        baseEvent.put(AFInAppEventParameterName.PARAM_3, android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL);
        baseEvent.put(AFInAppEventParameterName.PARAM_4, android.os.Build.VERSION.RELEASE);
    }

    /**
     * 初始化AppFlyer
     */
    public static void appsFlyerInit()
    {
        //XQz4cyuPQxsCQooPANw76W
        AppsFlyerLib.setAppsFlyerKey("XQz4cyuPQxsCQooPANw76W");
        AppsFlyerLib.sendTracking(App.getContextInstance());

    }

    /**
     * 游客统计
     */
    public static void appsFlyerGuest()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.REGSITRATION_METHOD, "Guest");
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.COMPLETE_REGISTRATION, baseEvent);
    }


    /**
     * 用户名
     * 注册
     */
    public static void appsFlyerRegistrationUsername()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.REGSITRATION_METHOD, Constant.TYPE_USERNAME);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.COMPLETE_REGISTRATION, baseEvent);
    }

    /**
     * 手机号
     * 注册
     */
    public static void appsFlyerRegistrationPhone()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.REGSITRATION_METHOD, Constant.TYPE_PHONE);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.COMPLETE_REGISTRATION, baseEvent);
    }

    /**
     * facebook
     * 注册
     */
    public static void appsFlyerRegistrationFacebook()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.REGSITRATION_METHOD, Constant.TYPE_FACEBOOK);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.COMPLETE_REGISTRATION, baseEvent);
    }


    /**
     * 用户名
     * 登录
     */
    public static void appsFlyerLoginUsername()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.DESCRIPTION, Constant.TYPE_USERNAME);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.LOGIN, baseEvent);
    }

    /**
     * 手机号
     * 登录
     */
    private static void appsFlyerLoginPhone()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.DESCRIPTION, Constant.TYPE_PHONE);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.LOGIN, baseEvent);
    }

    /**
     * facebook
     * 登录
     */
    private static void appsFlyerLoginFacebook()
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.DESCRIPTION, Constant.TYPE_FACEBOOK);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.LOGIN, baseEvent);
    }


    public static void appsFlyer5MainPager(String strTab)
    {
        AppsFlyerLib.setCustomerUserId(SystemUtil.getDeviceId(appContext));
        baseEvent.put(AFInAppEventParameterName.CONTENT_TYPE, strTab);
        AppsFlyerLib.trackEvent(appContext, AFInAppEventType.CONTENT_VIEW, baseEvent);
    }




    public static void doLoginTrack()
    {
        if(!TextUtils.isEmpty(App.getLoginedUser().getUser_login_type())) {
            switch (App.getLoginedUser().getUser_login_type()) {
                case Constant.TYPE_USERNAME:
                    appsFlyerLoginUsername();
                    break;

                case Constant.TYPE_PHONE:
                    appsFlyerLoginPhone();
                    break;

                case Constant.TYPE_FACEBOOK:
                    appsFlyerLoginFacebook();
                    break;
            }
        }
    }
}
