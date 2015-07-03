package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.PushApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class LogInUsernameActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = LogInUsernameActivity.class.getSimpleName();
    private final static String GET_USER = TAG + "_GET_USER";

    private final static int GO_DETAILS = 1;
    private final static int GO_MAIN = 2;

    private EditText etUsername;
    private EditText etPassword;
    private com.gc.materialdesign.views.Button btnLogIn;
    private RelativeLayout rlProgress;

    private String strUsername;
    private String strPassword;

    private List<UserEntity> userEntities;
    private UserEntity userEntity;
    private AppTokenEntity tokenEntity;


    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case GO_MAIN:
                    goMainActivity();
                    break;

                case GO_DETAILS:
                    break;

                default:
                    break;
            }
        }
    };



    @Override
    public int getLayout() {
        return R.layout.activity_log_in_username;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.text_start_log_in));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.btn_gradient_color_green_normal);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }


    @Override
    public void initView() {
        etUsername = getViewById(R.id.et_username);
        etPassword = getViewById(R.id.et_password);
        btnLogIn = getViewById(R.id.tv_btn_log_in);
        rlProgress = getViewById(R.id.rl_progress);

        btnLogIn.setOnClickListener(this);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.tv_btn_log_in:
                doLogIn();
                break;
        }
    }

    public void doLogIn() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strUsername = etUsername.getText().toString();
        strPassword = etPassword.getText().toString();

        if (!MyTextUtil.checkEmptyInputText(strUsername, strPassword))
        {
            doingLogInChangeUI();

            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("user_country_code", "");
            jsonParams.put("user_phone", "");
            jsonParams.put("username", strUsername);
            jsonParams.put("user_password", MD5Util.string2MD5(strPassword));
            jsonParams.put("login_type", Constant.LOGIN_TYPE_USERNAME);
            jsonParams.put("user_uuid", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonParams.put("user_app_version", AppInfoUtil.getAppVersionName(this));
            jsonParams.put("user_app_os", Constant.USER_APP_OS);

            final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("condition", jsonParamsString);

            new HttpTools(this).get(Constant.API_LOGIN, params, GET_USER, new HttpCallback() {
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

                        userEntities = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), new TypeToken<List<UserEntity>>(){}.getType());
                        tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);
                        String verificationFlag = jsonObject.getString("verification_flag");

                        if (userEntities.size() == 0 && TextUtils.isEmpty(userEntities.get(0).getUser_login_id()))
                        {
                            return;
                        }

                        if ("true".equals(verificationFlag))
                        {
                            userEntity = userEntities.get(0);
                            App.changeLoginedUser(userEntity, tokenEntity);
                            PreferencesUtil.saveValue(LogInUsernameActivity.this, Constant.HAS_LOGED_IN, Constant.HAS_LOGED_IN);
                            handler.sendEmptyMessage(GO_MAIN);
                        }
                        else
                        {
                            //TODO
                            handler.sendEmptyMessage(GO_DETAILS);
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

    }


    public void doingLogInChangeUI()
    {
        rlProgress.setVisibility(View.VISIBLE);
        btnLogIn.setClickable(false);
    }

    public void finishLogInChangeUI()
    {
        rlProgress.setVisibility(View.GONE);
        btnLogIn.setClickable(true);
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        PushApi.initPushApi(this);
        finish();
    }
}
