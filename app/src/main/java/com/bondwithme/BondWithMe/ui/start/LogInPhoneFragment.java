package com.bondwithme.BondWithMe.ui.start;

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
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.FaceBookUserEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.LogInStateListener;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.bondwithme.BondWithMe.util.LoginManager;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.bondwithme.BondWithMe.util.MessageUtil;
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

public class LogInPhoneFragment extends Fragment implements View.OnClickListener , TextView.OnEditorActionListener, LogInStateListener {

    private final static String TAG = LogInPhoneFragment.class.getSimpleName();
    private final static String GET_USER = TAG + "_GET_USER";


    private static final int GET_COUNTRY_CODE = 0;

    private static final int ERROR = -1;
    private static final int GO_DETAILS = 1;
    private static final int GO_MAIN = 2;
    private static final int CATCH =3;

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private TextView tvStartCountryCode;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private TextView tvLogIn;
    private Button brLogIn;
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

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
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
        switch (v.getId())
        {
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

            switch (requestCode)
            {
                case GET_COUNTRY_CODE:
                    if (resultCode == getActivity().RESULT_OK)
                    {
                        tvCountry.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
                        tvCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
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

    private void initView(View view)
    {
        rlCountryCode = (RelativeLayout)view.findViewById(R.id.rl_country_code);
        tvCountry = (TextView)view.findViewById(R.id.tv_country);
        tvCountryCode = (TextView)view.findViewById(R.id.tv_country_code);
        tvStartCountryCode = (TextView)view.findViewById(R.id.tv_start_country_code);
        etPhoneNumber = (EditText)view.findViewById(R.id.et_phone_number);
        etPassword = (EditText)view.findViewById(R.id.et_password);
//        tvLogIn = (TextView)view.findViewById(R.id.tv_btn_log_in);
        brLogIn = (Button)view.findViewById(R.id.br_log_in);
        tvForgetPassword = (TextView)view.findViewById(R.id.tv_forget_password);
        ivUsername = (ImageView)view.findViewById(R.id.iv_username);
        ivFacebook = (ImageView)view.findViewById(R.id.iv_facebook);
        rlProgress = (RelativeLayout)view.findViewById(R.id.rl_progress);

//        tvLogIn.setOnClickListener(this);
        brLogIn.setOnClickListener(this);

        ivUsername.setOnClickListener(this);
        rlCountryCode.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        etPassword.setOnEditorActionListener(this);

        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));

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

    public void doLogIn()
    {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strCountryCode = tvCountryCode.getText().toString().trim();
        strPhoneNumber = etPhoneNumber.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if(!MyTextUtil.isHasEmpty(strCountryCode, strPhoneNumber, strPassword))
        {
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

                        if (userEntities.size() == 0 && TextUtils.isEmpty(userEntities.get(0).getUser_login_id()))
                        {
                            //这样可以当做是bad date
                            unknowWrong();
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
            if (TextUtils.isEmpty(tvCountryCode.getText().toString()))
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(etPhoneNumber.getText().toString()))
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
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
//        tvLogIn.setClickable(false);
        brLogIn.setClickable(false);
        UIUtil.hideKeyboard(getActivity(), etPassword);
    }

    public void finishLogInChangeUI() {
        rlProgress.setVisibility(View.GONE);
//        tvLogIn.setClickable(true);
        brLogIn.setClickable(true);
    }

    public void goMainActivity()
    {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        App.changeLoginedUser(userEntity, tokenEntity);
        startActivity(intent);
        //TODO
        //要改。为什么在这边初始化？
        PushApi.initPushApi(getActivity());
        getActivity().finish();
    }

    public void goDetails()
    {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
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

    private void unknowWrong()
    {
        etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
        etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
    }

    @Override
    public void OnLoginSuccess(FaceBookUserEntity faceBookUserEntity, String logType) {
        MessageUtil.showMessage(getActivity(),"OnLoginSuccess");
        Log.d("","faceBookUserEntity" + faceBookUserEntity.toString());
    }

    @Override
    public void OnLoginError(String error) {
        MessageUtil.showMessage(getActivity(),"OnLoginError");
    }
}
