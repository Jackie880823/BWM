package com.bondwithme.BondWithMe;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.baidu.mapapi.SDKInitializer;
import com.bondwithme.BondWithMe.db.SQLiteHelperOrm;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.interfaces.NetChangeObserver;
import com.bondwithme.BondWithMe.receiver_service.AlarmControler;
import com.bondwithme.BondWithMe.receiver_service.NetWorkStateReceiver;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.MemberActivity;
import com.bondwithme.BondWithMe.ui.start.StartActivity;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.NotificationUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.PushApi;
import com.bondwithme.BondWithMe.util.SystemUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.InteractivePopupWindow;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.piwik.sdk.Piwik;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wing on 15/3/21.
 */
public class App extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, NetChangeObserver, LocationUtil.GoogleServiceCheckTaskListener {

    private static UserEntity user;
    private static App appContext;
    private SQLiteHelperOrm databaseHelper = null;
    private static MyDialog updateDialog;
    private static boolean foreground;
    private boolean paused;
    private static final int CHECK_DELAY = 500;
    private Runnable check;
    private Handler handler;


    /**
     * 通知记录,方便count
     */
    private static List<String> notificationWallList = new ArrayList<>();
    private static List<String> notificationEventList = new ArrayList<>();
    private static List<String> notificationMemberList = new ArrayList<>();
    private static List<String> notificationMessageList = new ArrayList<>();
    /**
     * 特殊的miss通知
     */
    private static HashMap<String, String> notificationMissList = new HashMap<>();
    private static List<String> notificationBigDayList = new ArrayList<>();
    private static List<String> notificationNewsList = new ArrayList<>();
    private static List<String> notificationGroupList = new ArrayList<>();
    private MyDialog showAddDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        handler = new Handler();
        appContext = this;
        /**图片工具*/
        BitmapTools.init(this);
        /**异常处理*/
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);
        CrashHandler.init(this);
        /**网络工具初始*/
        HttpTools.init(this);

        /**初始化百度地图*/
        SDKInitializer.initialize(this);

        /** 设置从歌地图获取位置，当这次设置获取不到位置说明谷歌地图这次无法使用，应用中调用地图时会默认启动百度地图 */
        LocationUtil.setRequestLocationUpdates(this, this);

        /** 初始化第三方 Universal Image Loader图片处理类 */
        UniversalImageLoaderUtil.initImageLoader(App.this);

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
//        filter.addAction("refresh");
        registerReceiver(mReceiver, filter);
        NetWorkStateReceiver.registerNetStateObserver(this);


    }


    public static App getContextInstance() {
        return appContext;
    }

    /**
     * 登录才能使用这个方法。
     * @param context
     * @param user
     * @param tokenEntity
     */
    public static void userLoginSuccessed(Activity context, UserEntity user, AppTokenEntity tokenEntity) {

        if(user!=null) {
            changeLoginedUser(user, tokenEntity);
            runAlarmTask(context, user);
            goMain(context);
        }

    }

    /**
     * 运行定时任务
     * @param context
     * @param user
     */
    private static void runAlarmTask(Activity context, UserEntity user) {
        try {
            /**少于7天*/
            if (MyDateUtils.getDayDistanceBetweenTimestmaps(MyDateUtils.formatDateString2LocalTimestamp(user.getUser_creation_date()), System.currentTimeMillis()) < 7) {
                AlarmControler.getInstance().createAllTasks(context);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static boolean needUpdate;

    public static void checkVerSion(final Activity context) {
        needUpdate = false;
        Map params = new HashMap();
        params.put("os", "android");
        new HttpTools(context).get(Constant.API_CHECK_VERSION, params, null, new HttpCallback() {
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
                try {
                    JSONObject object = new JSONObject(response);
                    //for test
//                    if(("" + AppInfoUtil.getAppVersionCode(context)).equals(object.get("app_latest_version")) && object.get("app_major_update").equals("1")) {
                    if (!("" + AppInfoUtil.getAppVersionCode(context)).equals(object.get("app_latest_version")) && "1".equals(object.get("app_major_update"))) {
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

                    SystemUtil.updateByGooglePlay(content);

                    //                    // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
                    //                    DownloadManager.Request req = new DownloadManager.Request(Uri.parse("http://bondwith.me/download.php"));
                    //
                    //                    // 通过setAllowedNetworkTypes方法可以设置允许在何种网络下下载，
                    //                    // 也可以使用setAllowedOverRoaming方法，它更加灵活
                    //                    req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                    //
                    //                    // 此方法表示在下载过程中通知栏会一直显示该下载，在下载完成后仍然会显示，
                    //                    // 直到用户点击该通知或者消除该通知。还有其他参数可供选择
                    //                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //
                    //                    // 设置下载文件存放的路径，同样你可以选择以下方法存放在你想要的位置。
                    //                    req.setDestinationInExternalFilesDir(content, Environment.DIRECTORY_DOWNLOADS, content.getString(R.string.title_download_task));
                    //
                    //                    // 设置一些基本显示信息
                    //                    req.setTitle(content.getString(R.string.download_apk_content_title));
                    //                    req.setDescription(content.getString(R.string.download_apk_content_description));
                    //                    req.setMimeType("application/vnd.android.package-archive");
                    //
                    //                    // Ok go!
                    //                    DownloadManager dm = (DownloadManager) content.getSystemService(Context.DOWNLOAD_SERVICE);
                    //                    long downloadId = dm.enqueue(req);

                    appContext.exit(content);
                }
            });
        }
        if (!updateDialog.isShowing())
            updateDialog.show();
    }

    public static void goMain(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
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

    public static boolean isInteractiveTipFinish() {
        if (appContext != null) {
            if (PreferencesUtil.getValue(appContext, InteractivePopupWindow.INTERACTIVE_TIP_TAG_POST, false) &&
                    PreferencesUtil.getValue(appContext, InteractivePopupWindow.INTERACTIVE_TIP_SAVE_EVENT, false)) {
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }


    }

    public static void initToken(String user_login_id, AppTokenEntity tokenEntity) {
        if (tokenEntity != null) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Charset", "UTF-8");
            headers.put("X_BWM_TOKEN", tokenEntity.getUser_token());
            headers.put("X_BWM_USERLOGINID", user_login_id);
            headers.put("X_BWM_DEVID", AppInfoUtil.getDeviceUUID(appContext));
            headers.put("X_BWM_APPID", AppInfoUtil.getAppPackageName(appContext));
            headers.put("X_BWM_USERLOC", "");
            headers.put("X_BWM_APPVER", AppInfoUtil.getAppVersionName(appContext));
            headers.put("X_BWM_APPLANG", Locale.getDefault().getLanguage());
            // 接收请求返回的语言
            headers.put("Accept-Language", Locale.getDefault().getLanguage());

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
            FileUtil.clearCache(context);
            PreferencesUtil.saveValue(context, "user", null);

            //反注册推送
            clearPush(context);
            //默认tab
            PreferencesUtil.saveValue(context, MainActivity.LAST_LEAVE_INDEX, 0);

            //to Login activity
            Intent intent = new Intent(context, StartActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            context.startActivity(mainIntent);

            user = null;

            Piwik.getInstance(getContextInstance()).setAppOptOut(true);//禁止Piwik
            if (FacebookSdk.isInitialized()) {
                LoginManager.getInstance().logOut();//清除Facebook授权缓存
            }
            AlarmControler.getInstance().cancleAllTasks(context);
            context.finish();
        }
        MainActivity.clearAllRedPoint();
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
        clearAllNotificationMsgs();
    }

    public void exit() {
        exit(null);
    }

    public void exit(Activity context) {
        if (context != null && !context.isFinishing()) {
            context.finish();
        }
//        App.getContextInstance().finishAllActivitys();
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

    /**
     * 应用是否在前台运行
     *
     * @return
     */
    public static boolean isForeground() {
        return foreground;
    }

    /**
     * 应用是否在后台运行
     *
     * @return
     */
    public static boolean isBackground() {
        return !foreground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if(activity instanceof MainActivity&&user!=null){
//            if (needShowAddDialog)
                checkHasPendingRequest(activity);
        }
    }

    private List<Activity> activityList = new ArrayList<>();

    public void finishAllActivitys() {
        if (activityList != null) {
            for (Activity activity : activityList) {
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityList != null)
            activityList.add(activity);
    }

    private boolean needShowAddDialog = true;
    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;

//        if(user!=null){
//            if (needShowAddDialog)
//                checkHasPendingRequest(activity);
//        }
        foreground = true;
        if (check != null) {
            handler.removeCallbacks(check);
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;
        if (check != null) {
            handler.removeCallbacks(check);
        }
        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    //重置add弹窗
                    if(user!=null){
                        if (isBackground()) {
                            needShowAddDialog = true;
                        }
                    }
                } else {
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        if(isBackground()&&httpTools!=null) {
//            httpTools.cancelRequestByTag(TAG_CHECK_PENDING);
//        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activityList != null)
            activityList.remove(activity);
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                AppControler.getAppControler().finishAllActivity();
                HttpTools.getHeaders().put("X_BWM_APPLANG", Locale.getDefault().getLanguage());
            }
        }
    };

    public static List<String> getNotificationMsgsByType(NotificationUtil.MessageType messageType) {

        switch (messageType) {
            case BONDALERT_WALL:
                return notificationWallList;
            case BONDALERT_EVENT:
                return notificationEventList;
            case BONDALERT_MEMBER:
                return notificationMemberList;
            case BONDALERT_MESSAGE:
                return notificationMessageList;
//            case BONDALERT_MISS:
//                return notificationMissList;
            case BONDALERT_BIGDAY:
                return notificationBigDayList;
            case BONDALERT_OTHER:
                return notificationNewsList;
            case BONDALERT_GROUP:
                return notificationGroupList;
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 蛋疼的搞特殊
     */
    public static HashMap<String, String> getMissNotificationInfos() {
        return notificationMissList;
    }

    /**
     * 清除缓存的通知消息
     */
    public static void clearAllNotificationMsgs() {
        notificationWallList.clear();
        notificationEventList.clear();
        notificationMemberList.clear();
        notificationMessageList.clear();
        notificationMissList.clear();
        notificationBigDayList.clear();
        notificationNewsList.clear();
        notificationGroupList.clear();
        NotificationUtil.clearNotification(appContext);
    }

    public void clearNotificationMsgsByType(NotificationUtil.MessageType messageType) {
        if (messageType == null) {
            return;
        }
        switch (messageType) {
            case BONDALERT_WALL:
                notificationWallList.clear();
            case BONDALERT_EVENT:
                notificationEventList.clear();
            case BONDALERT_MEMBER:
                notificationMemberList.clear();
            case BONDALERT_MESSAGE:
                notificationMessageList.clear();
            case BONDALERT_MISS:
                notificationMissList.clear();
            case BONDALERT_BIGDAY:
                notificationBigDayList.clear();
            case BONDALERT_OTHER:
                notificationNewsList.clear();
            case BONDALERT_GROUP:
                notificationGroupList.clear();
        }
    }


    @Override
    public void OnConnect(int netType) {
        //重新检查是否可用google服务
        LocationUtil.setRequestLocationUpdates(this, this);
    }

    @Override
    public void OnDisConnect() {

    }

    @Override
    public void googleServiceCheckFinished(boolean googleAvailable) {
        //初始推送api
        PushApi.initPushApi(getContextInstance(), googleAvailable);
    }
    HttpTools httpTools;
    private final static String TAG_CHECK_PENDING = "check_pending";
    private void checkHasPendingRequest(final Activity activity){
        httpTools = new HttpTools(activity);

        httpTools.get(String.format(Constant.API_CHECK_HAS_PENDING_REQUEST, user.getUser_id()), null, TAG_CHECK_PENDING, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                if("\"true\"".equals(string)) {
                    showAddDialog(activity);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void showAddDialog(final Activity activity) {
        if(activity!=null&&!activity.isFinishing()) {
            needShowAddDialog = false;
            if (showAddDialog != null && showAddDialog.isShowing()) {

            } else {
                showAddDialog = new MyDialog(activity, R.string.text_tips_title, R.string.desc_recerve_member_add_request);
                showAddDialog.setCanceledOnTouchOutside(false);

                showAddDialog.setButtonCancel(R.string.text_later, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddDialog.dismiss();
                    }
                });

                showAddDialog.setButtonAccept(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity.getBaseContext(), MemberActivity.class));
                        showAddDialog.dismiss();
                    }
                });

                showAddDialog.show();
            }
        }
    }
}
