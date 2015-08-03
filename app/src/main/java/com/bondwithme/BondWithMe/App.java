package com.bondwithme.BondWithMe;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.baidu.mapapi.SDKInitializer;
import com.bondwithme.BondWithMe.db.SQLiteHelperOrm;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.start.StartActivity;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.NotificationUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.PushApi;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wing on 15/3/21.
 */
public class App extends MultiDexApplication {

    private static UserEntity user;
    private static App appContext;
    private SQLiteHelperOrm databaseHelper = null;
    private static MyDialog updateDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        /**图片工具*/
        BitmapTools.init(this);
        /**异常处理*/
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        /**网络工具初始*/
        HttpTools.init(this);
        //TODO for baidu not support 64 bit cpu
        /**baidu map*/
        if (System.getProperty("os.arch").contains("64")) {
            //64bit cpu
        } else {
            //32 bit cpu
            SDKInitializer.initialize(getApplicationContext());
        }

        /** 设置从歌地图获取位置，当这次设置获取不到位置说明谷歌地图这次无法使用，应用中调用地图时会默认启动百度地图 */
        LocationUtil.setRequestLocationUpdates(this);

        /** 初始化第三方 Universal Image Loader图片处理类 */
        UniversalImageLoaderUtil.initImageLoader(App.this);
    }


    public static App getContextInstance() {
        return appContext;
    }

    public static void userLoginSuccessed(Activity context, UserEntity user, AppTokenEntity tokenEntity) {

        changeLoginedUser(user, tokenEntity);
        PushApi.initPushApi(context);
        goMain(context);

    }

    static boolean needUpdate;

    public static void checkVerSion(final Activity context) {
        needUpdate = false;
        Map params = new HashMap();
        params.put("os", "android");
        new HttpTools(context).get("http://dev.bondwith.me/bondwithme/index.php/api/appVersion", params, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
//                if (!needUpdate) {
//                    goMain(context);
//                }
            }

            @Override
            public void onResult(String response) {
                LogUtil.e("", "response===========" + response);
                try {
                    JSONObject object = new JSONObject(response);
//                    if (!("" + AppInfoUtil.getAppVersionCode(context)).equals(object.get("app_latest_version")) ) {
                    if (!("" + AppInfoUtil.getAppVersionCode(context)).equals(object.get("app_latest_version")) && object.get("app_major_update") == "1") {
                        //must update app
                        needUpdate = true;
                        showUpdateDialog(context);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private static void showUpdateDialog(final Activity content) {

        if (updateDialog == null) {
            LayoutInflater factory = LayoutInflater.from(content);
            updateDialog = new MyDialog(content, R.string.text_tips_title, R.string.update_message);
            updateDialog.setCanceledOnTouchOutside(false);
            updateDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDialog.dismiss();
                    appContext.exit(content);
                }
            });
            updateDialog.setButtonAccept(R.string.update, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDialog.dismiss();
                    // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
                    DownloadManager.Request req = new DownloadManager.Request(Uri.parse("http://bondwith.me/download.php"));

                    // 通过setAllowedNetworkTypes方法可以设置允许在何种网络下下载，
                    // 也可以使用setAllowedOverRoaming方法，它更加灵活
                    req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

                    // 此方法表示在下载过程中通知栏会一直显示该下载，在下载完成后仍然会显示，
                    // 直到用户点击该通知或者消除该通知。还有其他参数可供选择
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    // 设置下载文件存放的路径，同样你可以选择以下方法存放在你想要的位置。
                    req.setDestinationInExternalFilesDir(content, Environment.DIRECTORY_DOWNLOADS, content.getString(R.string.title_download_task));

                    // 设置一些基本显示信息
                    req.setTitle(content.getString(R.string.download_apk_content_title));
                    req.setDescription(content.getString(R.string.download_apk_content_description));
                    req.setMimeType("application/vnd.android.package-archive");

                    // Ok go!
                    DownloadManager dm = (DownloadManager) content.getSystemService(Context.DOWNLOAD_SERVICE);
                    long downloadId = dm.enqueue(req);

                    appContext.exit(content);
                }
            });
        }
        if (!updateDialog.isShowing())
            updateDialog.show();
    }

    private static void goMain(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);
        context.finish();
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
            //异常情况，重新初始token
            if (user != null && HttpTools.getHeaders() != null && TextUtils.isEmpty(HttpTools.getHeaders().get("X_BWM_TOKEN"))) {
                initToken(user.getUser_login_id(), new Gson().fromJson(PreferencesUtil.getValue(appContext, Constant.HTTP_TOKEN, ""), AppTokenEntity.class));
            }
        }
        //test,18682116784
//        String userString = "{'bondwithme_id':'80000698','linked':'0','owner_user_id':'0','sys_gender':'F','user_active_date':'2015-04-06 10:34:39','user_country_code':'86','user_creation_date':'2015-03-10 11:16:21','user_default_group':'0','user_emoticon':'','user_fullname':'Wing','user_gender':'M'," +
//                "'user_given_name':'Wing','user_id':'698','user_latitude':'','user_location_name':'','user_login_id':'8618682116784','user_login_type':'phone','user_longitude':'','user_password':'25d55ad283aa400af464c76d713c07ad','user_phone':'18682116784','user_photo':'','user_status':'active','user_surname':'Zhong','user_tnc_read':'1'}";
//        user = new Gson().fromJson(userString,UserEntity.class);

        return user;
    }

    public static void logout(Activity context) {

        if (context != null) {
            LoginManager.getInstance().logOut();//清除Facebook授权缓存
            FileUtil.clearCache(context);
            PreferencesUtil.saveValue(context, "user", null);

            //反注册推送
            clearPush(context);
            //默认tab
            PreferencesUtil.saveValue(context, "lastLeaveIndex", -1);

            //to Login activity
            Intent intent = new Intent(context, StartActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            context.startActivity(mainIntent);

            user = null;
            context.finish();
        }
    }

    private static void clearPush(Context context) {
        //反注册推送
        NotificationUtil.unRegisterPush(context, user.getUser_id());
        /**销毁推送id*/
        PreferencesUtil.saveValue(context, Constant.GCM_PREF_REG_ID, "");
        PreferencesUtil.saveValue(context, Constant.GCM_PREF_APP_VERSION, "");
        PreferencesUtil.saveValue(context, Constant.JPUSH_PREF_REG_ID, "");
        PreferencesUtil.saveValue(context, Constant.JPUSH_PREF_APP_VERSION, "");
        NotificationUtil.clearNotification(context);
    }

    public void exit() {
        exit(null);
    }

    public void exit(Activity context) {
        if (context != null && !context.isFinishing()) {
            context.finish();
        }
        onTerminate();
    }

    public SQLiteHelperOrm getDBHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, SQLiteHelperOrm.class);
        }
        return databaseHelper;
    }



    /**
     * /**
     * 完全退出app，应用销毁执行(不能保证一定)
     */
    @Override
    public void onTerminate() {
        new HttpTools(this).cancelAllRequest();
        FileUtil.clearCache(this);
        super.onTerminate();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
