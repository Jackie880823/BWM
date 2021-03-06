package com.madxstudio.co8.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.StringRequest;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.Tranck.MyAppsFlyer;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.FaceBookUserEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.util.AppInfoUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.UIUtil;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerificationActivity extends BaseActivity implements EditText.OnEditorActionListener, View.OnClickListener {

    private final static String TAG = VerificationActivity.class.getSimpleName();
    private final static String CHECK_GET_CODE = TAG + "_CHECK_GET_CODE";
    private final static String VERIFY_CODE = TAG + "_VERIFY_CODE";

    private final static String LOGIN_ID_EXISET = "LoginIdExist";
    private final static String REGISTER_FAIL = "RegisterFail";
    private final static String FAIL_VERIFY = "FailVerify";

    private final static int MILLIS_IN_FUTURE = 60000;
    private final static int COUNT_DOWN_INTERVAL = 1000;

    private final static int ERROR = -1;
    private final static int CATCH = -2;

    private final static int HANDLE_SUCCESS_RESEND_CODE = 0xa1;
    private final static int HANDLE_UNDEFINE_WRONG_RESEND_CODE = 0xa2;

    private final static int HANDLE_SUCCESS_CREATE_USER = 0xb1;
    private final static int HANDLE_LOGIN_ID_EXISET = 0xb2;
    private final static int HANDLE_REGISTER_FAIL = 0xb3;
    private final static int HANDLE_FAIL_VERIFY = 0xb4;
    private final static int HANDLE_SUCCESS_CREATE_FACEBOOK_USER = 0xb5;

    private final static int HANDLE_SUCCESS_FORGOT_VERIFY_CODE = 0xc1;
    private final static int HANDLE_FAIL_FORGOT_VERIFY_CODE = 0xc2;


    private TextView tvPhoneNumber;
    private EditText etCode;
    private TextView tvTime;
    private PaperButton brNext;
    private TimeCount timeCount;
    private RelativeLayout rlProgress;

    private String type;
    private String strLogId;
    private String strCountryCode;
    private String strPhoneNumber;
    private String strPassword;
    private FaceBookUserEntity faceBookUserEntity;

    private List<UserEntity> userEntities;
    private UserEntity userEntity;
    private AppTokenEntity tokenEntity;

    private ArrayList<UserEntity> forgotAccountList;

    //验证码出错怎么处理三种情况
    //http进入catch error怎么处理
    //TODO
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //以下再次获取验证码回调
                case HANDLE_SUCCESS_RESEND_CODE:
                    timeCount.start();
                    break;

                case HANDLE_UNDEFINE_WRONG_RESEND_CODE:
                    goBackAgain();
                    break;


                //以下验证且创建用户出现的各种情况回调
                case HANDLE_SUCCESS_CREATE_USER:
                    goDetails();
                    break;

                case HANDLE_LOGIN_ID_EXISET:
                    //log_in_id 被使用，返回重新填
                    goBackAgain();
                    break;

                case HANDLE_REGISTER_FAIL:
                    goBackAgain();
                    break;

                case HANDLE_FAIL_VERIFY:
//                    goBackAgain();
                    //验证码错误
                    MessageUtil.getInstance().showShortToast(getString(R.string.text_expired_code));
                    etCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    break;

                case HANDLE_SUCCESS_CREATE_FACEBOOK_USER:
                    //facebook 用户创建成功
                    goMainActivityByFacebook();
                    break;


                //以下忘记密码验证回调
                case HANDLE_SUCCESS_FORGOT_VERIFY_CODE:
                    goSelectAccount();
                    break;

                case HANDLE_FAIL_FORGOT_VERIFY_CODE:
                    etCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    break;


                //http
                case CATCH:
                    goBackAgain();
                    break;

                case ERROR:
                    goBackAgain();
                    break;

                default:
                    break;
            }
        }
    };


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            //参数依次为总时长,和计时的时间间隔
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //计时完毕时触发
            tvTime.setText(getResources().getString(R.string.text_start_verification_resend_code));
            tvTime.setTextColor(getResources().getColor(R.color.default_text_color_blue));
            tvTime.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
            tvTime.setText(String.format(getResources().getString(R.string.text_start_verification_time_prompt), millisUntilFinished / 1000));
            tvTime.setTextColor(getResources().getColor(R.color.default_text_color_light));
            tvTime.setClickable(false);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_verification;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_start_Verification));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.btn_bg_color_login_normal);
        tvTitle.setTextColor(getResources().getColor(R.color.login_text_bg_color));
        leftButton.setImageResource(R.drawable.co8_back_button);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        getData();

        tvPhoneNumber = getViewById(R.id.tv_phone_number);
        etCode = getViewById(R.id.et_country_code);
        tvTime = getViewById(R.id.tv_time);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);

        tvPhoneNumber.setText("+" + strCountryCode + MyTextUtil.NoZero(strPhoneNumber));//显示清零

        tvTime.setOnClickListener(this);
        brNext.setOnClickListener(this);

        etCode.setOnEditorActionListener(this);

        timeCount = new TimeCount(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL);
        timeCount.start();

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        });
    }

    private void getData() {
        Intent intent = getIntent();
        type = intent.getStringExtra(Constant.TYPE);

        switch (type) {
            //怎么优化这种界面传递参数。好麻烦。
            case Constant.TYPE_PHONE:
                strLogId = intent.getStringExtra("user_login_id");
                strCountryCode = intent.getStringExtra("user_country_code");
                strPhoneNumber = intent.getStringExtra("user_phone");
                strPassword = intent.getStringExtra("user_password");
                break;

            case Constant.TYPE_USERNAME:
                strLogId = intent.getStringExtra("user_login_id");
                strCountryCode = intent.getStringExtra("user_country_code");
                strPhoneNumber = intent.getStringExtra("user_phone");
                strPassword = intent.getStringExtra("user_password");
                break;

            case Constant.TYPE_FORGOT_PASSWORD:
                strCountryCode = intent.getStringExtra("user_country_code");
                strPhoneNumber = intent.getStringExtra("user_phone");
                break;

            case Constant.TYPE_FACEBOOK:
                faceBookUserEntity = (FaceBookUserEntity) intent.getSerializableExtra("faceBookUserEntity");
                strCountryCode = intent.getStringExtra("user_country_code");
                strPhoneNumber = intent.getStringExtra("user_phone");
                break;

            default:
                finish();
                break;
        }
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
        switch (v.getId()) {
            case R.id.br_next:
                doNext();
                break;

            case R.id.tv_time:
                doResendCode();
                break;

            default:
                break;
        }
    }


    private void doResendCode() {
        doingHttpChangeUI();
        switch (type) {
            case Constant.TYPE_PHONE:
                doPhoneResendCode();
                break;

            case Constant.TYPE_USERNAME:
                doUsernameResendCode();
                break;

            case Constant.TYPE_FORGOT_PASSWORD:
                doForgotPasswordResendCode();
                break;

            case Constant.TYPE_FACEBOOK:
                doFacebookResendCode();
                break;

            default:
                break;
        }
    }

    private void doNext() {
        if (TextUtils.isEmpty(etCode.getText().toString())) {
            etCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            return;
        }
        doingHttpChangeUI();
        switch (type) {
            case Constant.TYPE_PHONE:
                doPhoneVerifyUser();
                break;

            case Constant.TYPE_USERNAME:
                doUsernameVerifyUser();
                break;

            case Constant.TYPE_FORGOT_PASSWORD:
                doForgotPassword();
                break;

            case Constant.TYPE_FACEBOOK:
                doFacebookVerifyUser();
                break;

            default:
                break;
        }
    }


    private void doPhoneResendCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_login_id", strCountryCode + MyTextUtil.NoZero(strPhoneNumber));
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));

        new HttpTools(this).get(Constant.API_START_CHECK_LOG_ID, params, CHECK_GET_CODE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //成功获得验证码
                        handler.sendEmptyMessage(HANDLE_SUCCESS_RESEND_CODE);
                    } else if (Constant.FAIL.equals(jsonObject.getString("response_status"))) {
                        if (SignUpPhoneFragment.RESPONSE_MESSAGE_ID_EXIST.equals(jsonObject.getString("response_message"))) {
                            //账号被注册
                            handler.sendEmptyMessage(HANDLE_UNDEFINE_WRONG_RESEND_CODE);
                        }
                    }

                } catch (JSONException e) {
                    //验证码发送失败
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                //ERROR
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

    private void doUsernameResendCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_login_id", strLogId);
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));

        new HttpTools(this).get(Constant.API_START_CHECK_LOG_ID, params, CHECK_GET_CODE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //成功获得验证码
                        handler.sendEmptyMessage(HANDLE_SUCCESS_RESEND_CODE);
                    } else if (Constant.FAIL.equals(jsonObject.getString("response_status"))) {
                        if (SignUpUsernameActivity.RESPONSE_MESSAGE_ID_EXIST.equals(jsonObject.getString("response_message"))) {
                            //账号被注册
                            handler.sendEmptyMessage(HANDLE_UNDEFINE_WRONG_RESEND_CODE);
                        }
                    }

                } catch (JSONException e) {
                    //验证码发送失败
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                //ERROR
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

    private void doForgotPasswordResendCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));

        new HttpTools(this).get(Constant.API_START_FORGOT_PASSWORD_GET_CODE, params, CHECK_GET_CODE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //成功获得验证码
                        handler.sendEmptyMessage(HANDLE_SUCCESS_RESEND_CODE);
                    } else {
                        if (Constant.FAIL.equals(jsonObject.getString("response_status"))) {

                            handler.sendEmptyMessage(HANDLE_UNDEFINE_WRONG_RESEND_CODE);

                            switch (jsonObject.getString("response_message")) {
                                case ForgotPasswordActivity.RM_PHONE_NUMBERNOT_FOUND:
                                    break;

                                case ForgotPasswordActivity.RM_CREATE_VERIFICATION_FAIL:
                                    break;

                                case ForgotPasswordActivity.RM_CREATE_OUT_MESSAGE_FAIL:
                                    break;

                                default:
                                    break;
                            }
                        }
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

    private void doFacebookResendCode() {

        Map<String, String> params = new HashMap<>();
        params.put("user_login_id", faceBookUserEntity.getUserId());
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        params.put("login_type", Constant.TYPE_FACEBOOK);

        new HttpTools(this).get(Constant.API_START_THIRD_PARTY_GET_CODE, params, CHECK_GET_CODE, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //成功获得验证码
                        handler.sendEmptyMessage(HANDLE_SUCCESS_RESEND_CODE);
                    } else if (Constant.FAIL.equals(jsonObject.getString("response_status"))) {
                        if (ThirdPartyVerifyPhoneActivity.RESPONSE_MESSAGE_ID_EXIST.equals(jsonObject.getString("response_message"))) {
                            //账号被注册
                            handler.sendEmptyMessage(HANDLE_UNDEFINE_WRONG_RESEND_CODE);
                        }
                    }

                } catch (JSONException e) {
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(ERROR);
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


    private void doPhoneVerifyUser() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        params.put("verify_code", etCode.getText().toString());
        params.put("user_login_id", strLogId);
        params.put("user_login_type", Constant.TYPE_PHONE);
        params.put("user_password", strPassword);
        params.put("user_uuid", Settings.Secure.getString(VerificationActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        params.put("user_app_version", AppInfoUtil.getAppVersionName(this));
        params.put("user_app_os", Constant.USER_APP_OS);

        new HttpTools(this).post(Constant.API_START_PHONE_CREATE_USER, params, VERIFY_CODE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //注册成功，返回部分用户数据，有可能会有错误数据？
                        userEntity = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), UserEntity.class);
                        tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);
                        handler.sendEmptyMessage(HANDLE_SUCCESS_CREATE_USER);
                    } else {
                        switch (jsonObject.getString("response_message")) {
                            case LOGIN_ID_EXISET:
                                handler.sendEmptyMessage(HANDLE_LOGIN_ID_EXISET);
                                break;

                            case REGISTER_FAIL:
                                handler.sendEmptyMessage(HANDLE_REGISTER_FAIL);
                                break;

                            case FAIL_VERIFY:
                                handler.sendEmptyMessage(HANDLE_FAIL_VERIFY);
                                break;

                            default:
                                break;
                        }
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

    private void doUsernameVerifyUser() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        params.put("verify_code", etCode.getText().toString());
        params.put("user_login_id", strLogId);
        params.put("user_login_type", Constant.TYPE_USERNAME);
        params.put("user_password", strPassword);
        params.put("user_uuid", Settings.Secure.getString(VerificationActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        params.put("user_app_version", AppInfoUtil.getAppVersionName(this));
        params.put("user_app_os", Constant.USER_APP_OS);

        new HttpTools(this).post(Constant.API_START_USERNAME_CREATE_USER, params, VERIFY_CODE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                Log.d("", "response---------" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //注册成功，返回部分用户数据，有可能会有错误数据？
                        userEntity = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), UserEntity.class);
                        tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);
                        handler.sendEmptyMessage(HANDLE_SUCCESS_CREATE_USER);
                    } else {
                        switch (jsonObject.getString("response_message")) {
                            case LOGIN_ID_EXISET:
                                handler.sendEmptyMessage(HANDLE_LOGIN_ID_EXISET);
                                break;

                            case REGISTER_FAIL:
                                handler.sendEmptyMessage(HANDLE_REGISTER_FAIL);
                                break;

                            case FAIL_VERIFY:
                                handler.sendEmptyMessage(HANDLE_FAIL_VERIFY);
                                break;

                            default:
                                break;
                        }
                    }
                } catch (JSONException e) {
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
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

    private void doForgotPassword() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_country_code", strCountryCode);
        params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        params.put("verify_code", etCode.getText().toString());

        new HttpTools(this).get(Constant.API_START_FORGOT_PASSWORD_VERIFY_CODE, params, VERIFY_CODE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishHttpChangeUI();
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                        //有可能服务是success还传了错误数据？
                        forgotAccountList = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), new TypeToken<ArrayList<UserEntity>>() {
                        }.getType());
                        handler.sendEmptyMessage(HANDLE_SUCCESS_FORGOT_VERIFY_CODE);
                    } else {
                        handler.sendEmptyMessage(HANDLE_FAIL_FORGOT_VERIFY_CODE);
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

    private void doFacebookVerifyUser() {

//        Map<String, String> params = new HashMap<>();
//        params.put("user_country_code", strCountryCode);
//        params.put("user_phone",MyTextUtil.NoZero(strPhoneNumber));
//        params.put("verify_code", etCode.getText().toString());
//        params.put("user_login_id",faceBookUserEntity.getUserId());
//        params.put("user_login_type", Constant.TYPE_FACEBOOK);
//        params.put("user_uuid", Settings.Secure.getString(VerificationActivity.this.getContentResolver(),
//                Settings.Secure.ANDROID_ID));
//        params.put("user_app_version", AppInfoUtil.getAppVersionName(this));
//        params.put("user_app_os", Constant.USER_APP_OS);
//        params.put("access_token", faceBookUserEntity.getToken());
//        params.put("user_surname", faceBookUserEntity.getLastname());
//        params.put("user_given_name", faceBookUserEntity.getFirstname());
//        if ("male".equals(faceBookUserEntity.getGender()))
//        {
//            params.put("user_gender", "M");
//        }
//        else
//        {
//            params.put("user_gender", "F");
//        }
//
//        new HttpTools(this).post(Constant.API_START_THIRD_PARTY_CREATE_USER, params, VERIFY_CODE, new HttpCallback() {
//
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onFinish() {
//                finishHttpChangeUI();
//            }
//
//            @Override
//            public void onResult(String response) {
//                GsonBuilder gsonb = new GsonBuilder();
//                Gson gson = gsonb.create();
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (Constant.SUCCESS.equals(jsonObject.get("response_status"))) {
//                        userEntity = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), UserEntity.class);
//                        tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);
//                        handler.sendEmptyMessage(HANDLE_SUCCESS_CREATE_FACEBOOK_USER);
//                    } else if (Constant.FAIL.equals(jsonObject.get("response_status"))) {
//                        handler.sendEmptyMessage(HANDLE_FAIL_VERIFY);
//                    }
//
//                } catch (JSONException e) {
//                    handler.sendEmptyMessage(CATCH);
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                handler.sendEmptyMessage(ERROR);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onCancelled() {
//
//            }
//
//            @Override
//            public void onLoading(long count, long current) {
//
//            }
//        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.API_START_THIRD_PARTY_CREATE_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (Constant.SUCCESS.equals(jsonObject.get("response_status"))) {
                        userEntity = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), UserEntity.class);
                        tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);
                        handler.sendEmptyMessage(HANDLE_SUCCESS_CREATE_FACEBOOK_USER);
                    } else if (Constant.FAIL.equals(jsonObject.get("response_status"))) {
                        handler.sendEmptyMessage(HANDLE_FAIL_VERIFY);
                    }

                } catch (JSONException e) {
                    handler.sendEmptyMessage(CATCH);
                    e.printStackTrace();
                } finally {
                    finishHttpChangeUI();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.sendEmptyMessage(ERROR);
                finishHttpChangeUI();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_country_code", strCountryCode);
                params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
                params.put("verify_code", etCode.getText().toString());
                params.put("user_login_id", faceBookUserEntity.getUserId());
                params.put("user_login_type", Constant.TYPE_FACEBOOK);
                params.put("user_uuid", Settings.Secure.getString(VerificationActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID));
                params.put("user_app_version", AppInfoUtil.getAppVersionName(VerificationActivity.this));
                params.put("user_app_os", Constant.USER_APP_OS);
                params.put("access_token", faceBookUserEntity.getToken());
                params.put("user_surname", faceBookUserEntity.getLastname());
                params.put("user_given_name", faceBookUserEntity.getFirstname());
                if ("male".equals(faceBookUserEntity.getGender())) {
                    params.put("user_gender", "M");
                } else {
                    params.put("user_gender", "F");
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyUtil.addRequest2Queue(VerificationActivity.this, stringRequest, VERIFY_CODE);
    }



    private void goBackAgain()
    {
        MessageUtil.getInstance().showShortToast(getString(R.string.text_start_fail_verify_code));
        finish();
    }

    private void doingHttpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        etCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
        UIUtil.hideKeyboard(this, etCode);
    }

    private void finishHttpChangeUI() {
        rlProgress.setVisibility(View.GONE);
    }

    private void goDetails() {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Constant.LOGIN_USER, userEntity);
        intent.putExtra(Constant.HTTP_TOKEN, tokenEntity);
        switch (userEntity.getUser_login_type()) {
            case Constant.TYPE_USERNAME:
                MyAppsFlyer.appsFlyerRegistrationUsername();
                break;

            case Constant.TYPE_PHONE:
                MyAppsFlyer.appsFlyerRegistrationPhone();
                break;
        }
        startActivity(intent);
    }

    private void goSelectAccount() {
        Intent intent = new Intent(this, SelectAccountActivity.class);
        intent.putExtra(Constant.LOGIN_USER, forgotAccountList);
        intent.putExtra("user_country_code", strCountryCode);
        intent.putExtra("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        startActivity(intent);
    }

    private void goMainActivityByFacebook() {
        userEntity.setShow_tip(true);//从登陆流程进入的必须显示tip，此值作为判断依据。因为facebook登录并不进入detail界面所以要在此设置
        userEntity.setShow_add_member(true);
        App.userLoginSuccessed(this, userEntity, tokenEntity);
        MyAppsFlyer.appsFlyerRegistrationFacebook();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doNext();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.TYPE_FACEBOOK.equals(type)) {
            LoginManager.getInstance().logOut();//清除Facebook授权缓存
        }
    }

}
