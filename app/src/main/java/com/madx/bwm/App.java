package com.madx.bwm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.IntentCompat;

import com.android.volley.ext.tools.HttpTools;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.madx.bwm.db.SQLiteHelperOrm;
import com.madx.bwm.entity.AppTokenEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.ui.LoginActivity;
import com.madx.bwm.util.AppInfoUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.NotificationUtil;
import com.madx.bwm.util.PreferencesUtil;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by wing on 15/3/21.
 */
public class App extends MultiDexApplication {

    private static UserEntity user;
    private static App appContext;
    private SQLiteHelperOrm databaseHelper = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        /**异常处理*/
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        /**网络工具初始*/
        HttpTools.init(this);
        /**baidu map*/
        SDKInitializer.initialize(getApplicationContext());

    }

    public static App getContextInstance() {
        return appContext;
    }

    public static void changeLoginedUser(UserEntity user) {
        if (appContext != null) {
            App.user = user;
            PreferencesUtil.saveValue(appContext, Constant.LOGIN_USER, new Gson().toJson(user));
        }
    }

    public static void changeLoginedUser(UserEntity user, AppTokenEntity tokenEntity) {
        if (appContext != null) {

            initToken(user.getUser_login_id(), tokenEntity);

            changeLoginedUser(user);

        }
    }

    public static void initToken(String user_login_id, AppTokenEntity tokenEntity) {
        if (tokenEntity != null) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Charset", "UTF-8");
            headers.put("X_BWM_TOKEN", tokenEntity.getUser_token());
            headers.put("X_BWM_USERLOGINID", user_login_id);
            headers.put("X_BWM_DEVID", AppInfoUtil.getDeviceUUID(appContext));
            HttpTools.initHeaders(headers);
            //how to use
//            Map<String, String> headers = HttpTools.getHeaders();
//            headers.put("Content-Type","application/json");
//            HttpTools.initHeaders(headers);
            PreferencesUtil.saveValue(appContext, Constant.HTTP_TOKEN, new Gson().toJson(tokenEntity));
        }
    }

    public static UserEntity getLoginedUser() {
        if (appContext != null && (user == null)) {
            user = new Gson().fromJson(PreferencesUtil.getValue(appContext, "user", null), UserEntity.class);
        }
        //test,18682116784
//        String userString = "{'bondwithme_id':'80000698','linked':'0','owner_user_id':'0','sys_gender':'F','user_active_date':'2015-04-06 10:34:39','user_country_code':'86','user_creation_date':'2015-03-10 11:16:21','user_default_group':'0','user_emoticon':'','user_fullname':'Wing','user_gender':'M'," +
//                "'user_given_name':'Wing','user_id':'698','user_latitude':'','user_location_name':'','user_login_id':'8618682116784','user_login_type':'phone','user_longitude':'','user_password':'25d55ad283aa400af464c76d713c07ad','user_phone':'18682116784','user_photo':'','user_status':'active','user_surname':'Zhong','user_tnc_read':'1'}";
//        user = new Gson().fromJson(userString,UserEntity.class);

        return user;
    }

    public static void logout(Activity context) {


        //反注册推送
        NotificationUtil.unRegisterPush(context,user.getUser_id());
        user = null;
        if (context != null) {
            FileUtil.clearCache(context);
            PreferencesUtil.saveValue(context, "user", null);
            Intent intent = new Intent(context, LoginActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            context.startActivity(mainIntent);
            /**销毁推送id*/
            PreferencesUtil.saveValue(context, Constant.GCM_PREF_REG_ID, "");
            PreferencesUtil.saveValue(context, Constant.GCM_PREF_APP_VERSION, "");
            PreferencesUtil.saveValue(context, Constant.JPUSH_PREF_REG_ID, "");
            PreferencesUtil.saveValue(context, Constant.JPUSH_PREF_APP_VERSION, "");
            NotificationUtil.clearNotification(context);
            //默认tab
            PreferencesUtil.saveValue(context, "lastLeaveIndex", -1);
            context.finish();
        }
    }

    public static void exit(Activity context) {
        if (context != null) {
            FileUtil.clearCache(context);
            context.finish();
        }
    }

    public SQLiteHelperOrm getDBHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this,
                    SQLiteHelperOrm.class);
        }
        return databaseHelper;
    }

    /**
     * 完全退出app
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        FileUtil.clearCache(this);
        System.exit(0);
    }
}
