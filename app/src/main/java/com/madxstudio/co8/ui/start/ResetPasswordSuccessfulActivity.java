package com.madxstudio.co8.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.util.AppInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ResetPasswordSuccessfulActivity extends Activity {

    private static final int ERROR = -1;
    private static final int GO_DETAILS = 1;
    private static final int GO_MAIN = 2;
    private static final int CATCH = 3;

    private Button btnOk;

    private List<UserEntity> userEntities;
    private UserEntity userEntity;
    private UserEntity intentUserEntity;
    private AppTokenEntity tokenEntity;

    private RelativeLayout rlProgress;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_DETAILS:
                    goDetails();
                    break;

                case GO_MAIN:
                    goMainActivity();
                    break;

                case CATCH:
                    break;

                case ERROR:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_successful);

        intentUserEntity = (UserEntity) getIntent().getExtras().getSerializable(Constant.LOGIN_USER);

        rlProgress = (RelativeLayout) findViewById(R.id.rl_progress);

        btnOk = (Button) findViewById(R.id.tv_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        if (Constant.TYPE_PHONE.equals(intentUserEntity.getUser_login_type())) {
            loginPhone();
        } else if (Constant.TYPE_USERNAME.equals(intentUserEntity.getUser_login_type())) {
            loginUsername();
        }
    }

    private void loginPhone() {
        doingLogInChangeUI();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_country_code", intentUserEntity.getUser_country_code());
        jsonParams.put("user_phone", intentUserEntity.getUser_phone());
        jsonParams.put("username", "");
        jsonParams.put("user_password", intentUserEntity.getUser_password());
        jsonParams.put("login_type", Constant.TYPE_PHONE);
        jsonParams.put("user_uuid", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        jsonParams.put("user_app_version", AppInfoUtil.getAppVersionName(this));
        jsonParams.put("user_app_os", Constant.USER_APP_OS);

        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);

        new HttpTools(this).get(Constant.API_LOGIN, params, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishLogInChangeUI();
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userEntities = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), new TypeToken<List<UserEntity>>() {
                    }.getType());
                    tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);

                    if (userEntities.size() == 0 && TextUtils.isEmpty(userEntities.get(0).getUser_login_id())) {
                        //这样可以当做是bad date
                        return;
                    }

                    userEntity = userEntities.get(0);

                    if ("created".equals(userEntity.getUser_status())) {
                        handler.sendEmptyMessage(GO_DETAILS);
                    } else {
                        handler.sendEmptyMessage(GO_MAIN);
                    }

                } catch (JSONException e) {
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(ERROR);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void doingLogInChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        btnOk.setClickable(false);
    }

    private void finishLogInChangeUI() {
        rlProgress.setVisibility(View.GONE);
        btnOk.setClickable(true);
    }


    private void loginUsername() {
        doingLogInChangeUI();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_country_code", "");
        jsonParams.put("user_phone", "");
        jsonParams.put("username", intentUserEntity.getUser_login_id());
        jsonParams.put("user_password", intentUserEntity.getUser_password());
        jsonParams.put("login_type", Constant.TYPE_USERNAME);
        jsonParams.put("user_uuid", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        jsonParams.put("user_app_version", AppInfoUtil.getAppVersionName(this));
        jsonParams.put("user_app_os", Constant.USER_APP_OS);

        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);

        new HttpTools(this).get(Constant.API_LOGIN, params, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishLogInChangeUI();
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userEntities = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), new TypeToken<List<UserEntity>>() {
                    }.getType());
                    tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);

                    if (userEntities.size() == 0 && TextUtils.isEmpty(userEntities.get(0).getUser_login_id())) {
                        //这样可以当做是bad date
                        return;
                    }

                    userEntity = userEntities.get(0);

                    if ("created".equals(userEntity.getUser_status())) {
                        handler.sendEmptyMessage(GO_DETAILS);
                    } else {
                        handler.sendEmptyMessage(GO_MAIN);
                    }

                } catch (JSONException e) {
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(ERROR);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    public void goMainActivity() {
        App.userLoginSuccessed(this, userEntity, tokenEntity);
    }

    public void goDetails() {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Constant.LOGIN_USER, userEntity);
        intent.putExtra(Constant.HTTP_TOKEN, tokenEntity);
        startActivity(intent);
    }

}
