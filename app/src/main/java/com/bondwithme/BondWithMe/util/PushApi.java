package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by christepherzhang on 15/7/1.
 */
public class PushApi {

    private static String regid;
    private static boolean isGCM;
    private static Context mContext;

    public static void initPushApi(Context context) {
        mContext = context;
        if (SystemUtil.checkPlayServices(mContext)) {
            /**GCM推送*/
            regid = AppInfoUtil.getGCMRegistrationId(mContext);
            if (TextUtils.isEmpty(regid)) {
                isGCM = true;
                registerInBackground();
            }
        } else {
            JPushInterface.init(mContext);
            regid = AppInfoUtil.getJpushRegistrationId(mContext);
            if (TextUtils.isEmpty(regid)) {
                isGCM = false;
                registerInBackground();
            }
            /**极光推送*/
//            JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志

//        JPushInterface.stopPush(getApplicationContext());
//        JPushInterface.resumePush(getApplicationContext());
        }


    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private static void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                if (isGCM) {
                    return doRegistration2GCM();
                } else {
                    return doRegistration2Jpush();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                if (isGCM) {

                }else{

                }
            }

        }.execute(null, null, null);
    }

    private  static String doRegistration2Jpush() {
        String msg = "";
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        regid = JPushInterface.getRegistrationID(mContext);
//        if(TextUtils.isEmpty(regid)){
//            regid = JPushInterface.getRegistrationID(context);
//        }
        while (TextUtils.isEmpty(regid)){
            regid = JPushInterface.getRegistrationID(mContext);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        msg = "Device registered, registration ID=" + regid;
        Log.d("","hhhhhhhh" + regid);
        sendRegistrationIdToBackend(regid, "jpush");
        AppInfoUtil.storeRegistrationId(mContext, regid, false);
        return msg;
    }

    private static String doRegistration2GCM() {
        String msg = "";
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
            regid = gcm.register(mContext.getString(R.string.gcm_sender_id));

            msg = "Device registered, registration ID=" + regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend(regid, "gcm");


            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the registration ID - no need to register again.
            AppInfoUtil.storeRegistrationId(mContext, regid,true);


        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private static void sendRegistrationIdToBackend(String regid, String service) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_REGIST_PUSH;
        Map<String, Object> params = new HashMap<>();
        params.put("pushToken", regid);
        params.put("deviceUuid", AppInfoUtil.getDeviceUUID(mContext));
        params.put("devicePlatform", "android");
        params.put("lang", Locale.getDefault().getCountry());
        params.put("appType", "native");
        params.put("pushService", service);
        params.put("appID", AppInfoUtil.getAppPackageName(mContext));
        requestInfo.putAllParams(params);
        new HttpTools(mContext).post(requestInfo, mContext, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {

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
}
