package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.bondwithme.BondWithMe.util.PushApi;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.gc.materialdesign.views.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class LogInUsernameActivity extends BaseActivity implements View.OnClickListener, EditText.OnEditorActionListener{

    private final static String TAG = LogInUsernameActivity.class.getSimpleName();
    private final static String GET_USER = TAG + "_GET_USER";

    private static final int ERROR = -1;
    private static final int GO_DETAILS = 1;
    private static final int GO_MAIN = 2;
    private static final int CATCH =3;

    private EditText etUsername;
    private EditText etPassword;
//    private TextView tvLogin;
    private Button btnLogIn;
    private RelativeLayout rlProgress;

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
                    App.changeLoginedUser(userEntity, tokenEntity);
                    goMainActivity();
                    break;

                case GO_DETAILS:
                    goDetails();
                    break;

                case CATCH:
                    unkonwWrong();
                    break;

                case ERROR:
                    unkonwWrong();
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
//        tvLogin = getViewById(R.id.tv_btn_log_in);
        btnLogIn = getViewById(R.id.br_log_in);
        rlProgress = getViewById(R.id.rl_progress);

//        tvLogin.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);

        etPassword.setOnEditorActionListener(this);

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        });
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
            case R.id.br_log_in:
                doLogIn();
                break;

//            case R.id.tv_btn_log_in:
//                doLogIn();
//                break;

            default:
                break;

        }
    }

    public void doLogIn() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        String strUsername = etUsername.getText().toString();
        String strPassword = etPassword.getText().toString();

        if (!MyTextUtil.isHasEmpty(strUsername, strPassword))
        {
            doingLogInChangeUI();

            HashMap<String, String> jsonParams = new HashMap<>();
            jsonParams.put("user_country_code", "");
            jsonParams.put("user_phone", "");
            jsonParams.put("username", strUsername);
            jsonParams.put("user_password", MD5Util.string2MD5(strPassword));
            jsonParams.put("login_type", Constant.TYPE_USERNAME);
            jsonParams.put("user_uuid", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonParams.put("user_app_version", AppInfoUtil.getAppVersionName(this));
            jsonParams.put("user_app_os", Constant.USER_APP_OS);

            final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<>();
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

                        if (userEntities.size() == 0 && TextUtils.isEmpty(userEntities.get(0).getUser_login_id()))
                        {
                            //wrong data??
                            unkonwWrong();
                            return;
                        }

                        userEntity = userEntities.get(0);

                        if ("created".equals(userEntity.getUser_status()))
                        {
                            handler.sendEmptyMessage(GO_DETAILS);
                        }
                        else
                        {
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
        else
        {
            if (TextUtils.isEmpty(etUsername.getText().toString()))
            {
                etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(etPassword.getText().toString()))
            {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        }

    }


    public void doingLogInChangeUI()
    {
        rlProgress.setVisibility(View.VISIBLE);
//        tvLogin.setClickable(false);
        btnLogIn.setClickable(false);
        UIUtil.hideKeyboard(this, etPassword);
    }

    public void finishLogInChangeUI()
    {
        rlProgress.setVisibility(View.GONE);
//        tvLogin.setClickable(true);
        btnLogIn.setClickable(true);
    }

    private void goMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //TODO
        //why？？改
        PushApi.initPushApi(this);
        finish();
    }

    public void goDetails()
    {
        Intent intent = new Intent(LogInUsernameActivity.this, DetailsActivity.class);
        intent.putExtra(Constant.LOGIN_USER, userEntity);
        intent.putExtra(Constant.HTTP_TOKEN, tokenEntity);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            doLogIn();
            return true;
        }
        return false;
    }

    private void unkonwWrong()
    {
        etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_red);
        etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
    }
}
