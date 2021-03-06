package com.madxstudio.co8.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.FaceBookUserEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.interfaces.LogInStateListener;
import com.madxstudio.co8.ui.CountryCodeActivity;
import com.madxstudio.co8.util.AppInfoUtil;
import com.madxstudio.co8.util.CountryCodeUtil;
import com.madxstudio.co8.util.LoginManager;
import com.madxstudio.co8.util.MD5Util;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogInPhoneFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener, LogInStateListener {

    private final static String TAG = LogInPhoneFragment.class.getSimpleName();
    private final static String GET_USER = TAG + "_GET_USER";
    private final static String CHECK_ID = TAG + "_CHECK_ID";


    private static final int GET_COUNTRY_CODE = 0;

    private static final int ERROR = -1;
    private static final int GO_DETAILS = 1;
    private static final int GO_MAIN = 2;
    private static final int CATCH = 3;
    private static final int THIRD_PARTY_SIGN_UP = 4;


    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    //    private TextView tvCountryCode;
    private EditText tvStartCountryCode;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private TextView tvLogIn;
    private PaperButton brLogIn;
    private TextView tvForgetPassword;
    private ImageView ivUsername;
    private ImageView ivFacebook;
    private RelativeLayout rlProgress;

    private String strCountryCode;
    private String strPhoneNumber;
    private String strPassword;

    private List<UserEntity> userEntities;
    private UserEntity userEntity;
    private AppTokenEntity tokenEntity;

    private boolean blnChooseCountryCode;//通过选择获得的国家区号。如果用户手动修改。把国家名称改回原始状态。这是用来判断的

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
                    //需要怎么处理？暂时？
                    unknowWrong();
                    break;

                case THIRD_PARTY_SIGN_UP:
                    goThirdPartyCheckId();
                    break;

                case ERROR:
                    //需要怎么处理？暂时？
                    unknowWrong();
                    break;


                default:
                    break;
            }

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        initView(view);

        LoginManager.initialize(getActivity());
        LoginManager.setFaceBookLoginParams(getActivity(), this, ivFacebook, null, this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_country_code:
                Intent intent = new Intent(getActivity(), CountryCodeActivity.class);
                startActivityForResult(intent, GET_COUNTRY_CODE);
                break;

//            case R.id.tv_btn_log_in:
//                doLogIn();
//                break;

            case R.id.br_log_in:
                doLogIn();
                break;

            case R.id.tv_forget_password:
                startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
                break;

            case R.id.iv_username:
                startActivity(new Intent(getActivity(), LogInUsernameActivity.class));
                break;

            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LoginManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GET_COUNTRY_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    blnChooseCountryCode = true;
                    tvCountry.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
//                        tvCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                    tvStartCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.OnDestory();
    }

    private void initView(View view) {
        rlCountryCode = (RelativeLayout) view.findViewById(R.id.rl_country_code);
        tvCountry = (TextView) view.findViewById(R.id.tv_country);
//        tvCountryCode = (TextView)view.findViewById(R.id.tv_country_code);
        tvStartCountryCode = (EditText) view.findViewById(R.id.tv_start_country_code);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
        etPassword = (EditText) view.findViewById(R.id.et_password);
//        tvLogIn = (TextView)view.findViewById(R.id.tv_btn_log_in);
        brLogIn = (PaperButton) view.findViewById(R.id.br_log_in);
        tvForgetPassword = (TextView) view.findViewById(R.id.tv_forget_password);
        ivUsername = (ImageView) view.findViewById(R.id.iv_username);
        ivFacebook = (ImageView) view.findViewById(R.id.iv_facebook);
        rlProgress = (RelativeLayout) view.findViewById(R.id.rl_progress);

//        tvLogIn.setOnClickListener(this);
        brLogIn.setOnClickListener(this);

        ivUsername.setOnClickListener(this);
        rlCountryCode.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        etPassword.setOnEditorActionListener(this);

//        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));

        tvStartCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (blnChooseCountryCode) {

                } else {
                    tvCountry.setText(getText(R.string.title_country_code));
                }
                blnChooseCountryCode = false;
            }
        });

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
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
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        });

        tvStartCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(tvStartCountryCode.getText().toString())) {
                    rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                } else {
                    rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                }
            }
        });
    }

    public void doLogIn() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strCountryCode = tvStartCountryCode.getText().toString().trim();
        strPhoneNumber = etPhoneNumber.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if (!MyTextUtil.isHasEmpty(strCountryCode, strPhoneNumber, strPassword)) {
            doingLogInChangeUI();

            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("user_country_code", strCountryCode);
            jsonParams.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
            jsonParams.put("username", "");
            jsonParams.put("user_password", MD5Util.string2MD5(strPassword));
            jsonParams.put("login_type", Constant.TYPE_PHONE);
            jsonParams.put("user_uuid", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonParams.put("user_app_version", AppInfoUtil.getAppVersionName(getActivity()));
            jsonParams.put("user_app_os", Constant.USER_APP_OS);

            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("condition", jsonParamsString);

            new HttpTools(getActivity()).get(Constant.API_LOGIN, params, GET_USER, new HttpCallback() {
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
                            unknowWrong();
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
        } else {
            if (TextUtils.isEmpty(tvStartCountryCode.getText().toString())) {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            } else {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            } else {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(etPassword.getText().toString())) {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            } else {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        }
    }

    public void doingLogInChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
//        tvLogIn.setClickable(false);
        brLogIn.setClickable(false);
        UIUtil.hideKeyboard(getActivity(), etPassword);
    }

    public void finishLogInChangeUI() {
        rlProgress.setVisibility(View.GONE);
//        tvLogIn.setClickable(true);
        brLogIn.setClickable(true);
    }

    public void goMainActivity() {
        App.userLoginSuccessed(getActivity(), userEntity, tokenEntity);
    }

    public void goDetails() {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(Constant.LOGIN_USER, userEntity);
        intent.putExtra(Constant.HTTP_TOKEN, tokenEntity);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doLogIn();
            return true;
        }
        return false;
    }

    private void unknowWrong() {
        MessageUtil.getInstance().showShortToast(getString(R.string.text_wrong_pwd_name));
        etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
        etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
    }

    private FaceBookUserEntity faceBookUserEntity;

    @Override
    public void OnLoginSuccess(FaceBookUserEntity faceBookUserEntity, String logType) {

        com.facebook.login.LoginManager.getInstance().logOut();//清除Facebook授权缓存
        if (!MyTextUtil.isHasEmpty(faceBookUserEntity.getUserId(), faceBookUserEntity.getFirstname(), faceBookUserEntity.getLastname(), faceBookUserEntity.getGender())) {
            Log.d("", faceBookUserEntity.toString());
            this.faceBookUserEntity = faceBookUserEntity;
            checkFacebookId();
        } else {
            //没必要吧？？？
        }
    }

    @Override
    public void OnLoginError(String error) {
        MessageUtil.getInstance().showShortToast(error);
        com.facebook.login.LoginManager.getInstance().logOut();//清除Facebook授权缓存
    }

    private void checkFacebookId() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("user_login_id", faceBookUserEntity.getUserId());
        params.put("login_type", Constant.TYPE_FACEBOOK);
        params.put("access_token", faceBookUserEntity.getToken());
        params.put("user_uuid", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        params.put("user_app_version", AppInfoUtil.getAppVersionName(getActivity()));
        params.put("user_app_os", Constant.USER_APP_OS);

        new HttpTools(getActivity()).get(Constant.API_START_THIRD_PARTY_CHECK_ID, params, CHECK_ID, new HttpCallback() {
            @Override
            public void onStart() {
                doingLogInChangeUI();
            }

            @Override
            public void onFinish() {
                finishLogInChangeUI();
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                Log.d("", "---facebook--checkId" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("1".equals(jsonObject.getString("bwm_user"))) {
                        userEntities = gson.fromJson(jsonObject.getString(Constant.LOGIN_USER), new TypeToken<List<UserEntity>>() {
                        }.getType());
                        tokenEntity = gson.fromJson(jsonObject.getString(Constant.HTTP_TOKEN), AppTokenEntity.class);
                        if (userEntities.size() == 0 && TextUtils.isEmpty(userEntities.get(0).getUser_login_id())) {
                            //这样可以当做是bad date
                            return;
                        }
                        userEntity = userEntities.get(0);
                        handler.sendEmptyMessage(GO_MAIN);
                    } else if ("0".equals(jsonObject.getString("bwm_user"))) {
                        handler.sendEmptyMessage(THIRD_PARTY_SIGN_UP);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void goThirdPartyCheckId() {
        Intent intent = new Intent(getActivity(), ThirdPartyVerifyPhoneActivity.class);
        intent.putExtra(Constant.TYPE_FACEBOOK, faceBookUserEntity);
        startActivity(intent);
    }


}
