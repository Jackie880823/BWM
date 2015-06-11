package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.AppTokenEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.AppInfoUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MyTextUtil;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.util.PreferencesUtil;
import com.madx.bwm.util.SystemUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends Activity {

    private Button btnLogin;//登陆按钮
    private LinearLayout tvSignUp;//进入注册
    private TextView btnForgetPassword;//进入修改密码
    private TextView tvCountryCode;//国家区号
    private RelativeLayout ll;//方便点击修改国家区号
    private EditText etAccount;
    private EditText etPassword;
    private LinearLayout ivRemove;
    private TextView do_faile_login_tv;
    private LinearLayout do_faile_login_linear;
    private List<UserEntity> userList;
    private RelativeLayout rlProgress;

    private static final int GET_COUNTRY_CODE = 1;

    String regid;
    private boolean isGCM;

    private void initPushApi() {

        if (SystemUtil.checkPlayServices(this)) {
            /**GCM推送*/
            regid = AppInfoUtil.getGCMRegistrationId(this);
            if (TextUtils.isEmpty(regid)) {
                isGCM = true;
                registerInBackground();
            }
        } else {
            JPushInterface.init(LoginActivity.this);
            regid = AppInfoUtil.getJpushRegistrationId(this);
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
    private void registerInBackground() {
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

    private String doRegistration2Jpush() {
        String msg = "";
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        regid = JPushInterface.getRegistrationID(this);
//        if(TextUtils.isEmpty(regid)){
//            regid = JPushInterface.getRegistrationID(this);
//        }
        while (TextUtils.isEmpty(regid)){
            regid = JPushInterface.getRegistrationID(this);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        msg = "Device registered, registration ID=" + regid;
        sendRegistrationIdToBackend(regid, "jpush");
        AppInfoUtil.storeRegistrationId(LoginActivity.this, regid,false);
        return msg;
    }

    private String doRegistration2GCM() {
        String msg = "";
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
            regid = gcm.register(getString(R.string.gcm_sender_id));
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
            AppInfoUtil.storeRegistrationId(LoginActivity.this, regid,true);


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
    private void sendRegistrationIdToBackend(String regid, String service) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_REGIST_PUSH;
        Map<String, String> params = new HashMap<>();
        params.put("pushToken", regid);
        params.put("deviceUuid", AppInfoUtil.getDeviceUUID(this));
        params.put("devicePlatform", "android");
        params.put("lang", Locale.getDefault().getCountry());
        params.put("appType", "native");
        params.put("pushService", service);
        params.put("appID", AppInfoUtil.getAppPackageName(this));
        requestInfo.params = params;
        new HttpTools(LoginActivity.this).post(requestInfo, new HttpCallback() {
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

    @Override
    protected void onResume() {
        super.onResume();
//        finishByNoPlayService();
    }

    private void finishByNoPlayService() {
        if (!SystemUtil.checkPlayServices(this)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    finish();
                }
            }.execute();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserEntity userEntity = App.getLoginedUser();
        if (userEntity != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            String tokenString = PreferencesUtil.getValue(this, Constant.HTTP_TOKEN, null);
            if (!TextUtils.isEmpty(tokenString)) {
                App.initToken(userEntity.getUser_login_id(), new Gson().fromJson(tokenString, AppTokenEntity.class));//init http header
            }

            /**wing begin test gcm*/
            initPushApi();
            /**wing end test gcm*/
            finish();
            return;
        }

        btnLogin = (Button) findViewById(R.id.login);
        tvSignUp = (LinearLayout) findViewById(R.id.ll_sign_up);
        btnForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        tvCountryCode = (TextView) findViewById(R.id.tv_code);
        ll = (RelativeLayout) findViewById(R.id.ll_code);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        ivRemove = (LinearLayout) findViewById(R.id.iv_move);
        do_faile_login_tv = (TextView) findViewById(R.id.do_faile_login_tv);
        do_faile_login_linear = (LinearLayout) findViewById(R.id.do_faile_login_linear);
        rlProgress = (RelativeLayout)findViewById(R.id.rl_progress);

        //取消国家区号
        ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCountryCode.setText("");
            }
        });

        //登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    return;
                }
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(!TextUtils.isEmpty(account)&&!TextUtils.isEmpty(password)){

                    rlProgress.setVisibility(View.VISIBLE);
                    btnLogin.setClickable(false);

                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    if ((account.length() != 0)) {

                        if (TextUtils.isEmpty(tvCountryCode.getText())) {
//                            jsonParams.put("user_phone", account);//是否可以不需要
                            jsonParams.put("username", tvCountryCode.getText() + account);
                            jsonParams.put("login_type", "username");
                        } else {
                            account = MyTextUtil.NoZero(account);
//                            jsonParams.put("username", tvCountryCode.getText().toString() + account);//是否可以不需要
                            jsonParams.put("user_phone", account);
                            jsonParams.put("login_type", "phone");
                        }
                    }

                    jsonParams.put("user_country_code", tvCountryCode.getText().toString());
                    jsonParams.put("user_password", MD5(password));
                    jsonParams.put("user_uuid", Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                    jsonParams.put("user_app_version", AppInfoUtil.getAppVersionName(LoginActivity.this));
                    jsonParams.put("user_app_os", "android");
                    String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("condition", jsonParamsString);
//                    String url = UrlUtil.generateUrl(Constant.API_LOGIN, params);

                    new HttpTools(LoginActivity.this).get(Constant.API_LOGIN, params, new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {
                            rlProgress.setVisibility(View.GONE);
                            btnLogin.setClickable(true);
                        }

                        @Override
                        public void onResult(String response) {
                            GsonBuilder gsonb = new GsonBuilder();
                            Gson gson = gsonb.create();

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                //bad response data...
                                userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {

                                }.getType());
                                AppTokenEntity tokenEntity = gson.fromJson(jsonObject.getString("token"), AppTokenEntity.class);
                                if (userList != null && userList.get(0) != null) {
                                    UserEntity userEntity = userList.get(0);//登录的用户数据
                                    App.changeLoginedUser(userEntity, tokenEntity);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    /**wing begin test gcm*/
                                    initPushApi();
                                    /**wing end test gcm*/
                                    finish();
                                }

                            } catch (Exception e) {
                                MessageUtil.showMessage(LoginActivity.this, e.getMessage());
//                                MessageUtil.showMessage(LoginActivity.this, R.string.msg_action_failed);
                                btnLogin.setClickable(true);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            MessageUtil.showMessage(LoginActivity.this, R.string.msg_action_failed);
                            btnLogin.setClickable(true);

                            if (TextUtils.isEmpty(tvCountryCode.getText())) {
                                //用户名
                                do_faile_login_linear.setVisibility(View.VISIBLE);
                                String failePrompt = tvCountryCode.getText().toString() + " "
                                        + etAccount.getText().toString();
                                do_faile_login_tv.setText(failePrompt);
                            } else {
                                //手机号
                                do_faile_login_linear.setVisibility(View.VISIBLE);
                                String failePrompt = tvCountryCode.getText().toString() + " "
                                        + MyTextUtil.NoZero(etAccount.getText().toString().trim());
                                do_faile_login_tv.setText(failePrompt);
                            }

                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });
                } else if ((account.length() == 0) && (password.length() == 0)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.text_enter_details), Toast.LENGTH_SHORT).show();
                } else if (account.length() == 0) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.text_input_phone_number), Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.text_input_password), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //跳转到注册界面
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpPhoneActivity.class);
                startActivity(intent);
            }
        });

        //跳转到找回密码解码
        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        //跳转到国家区号选择list
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CountryCodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    //string -> MD5
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_COUNTRY_CODE :
                if (resultCode == RESULT_OK) {
                    tvCountryCode.setText(data.getStringExtra("code"));
                }
        }
    }

    //自动获取国家区号方法
    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    //判断字符串的是否为字母，字母true。
    public static boolean test(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }



}
