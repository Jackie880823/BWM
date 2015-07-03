package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.PushApi;
import com.gc.materialdesign.views.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LogInPhoneFragment extends Fragment implements View.OnClickListener{

    private final static String TAG = LogInPhoneFragment.class.getSimpleName();
    private final static String GET_USER = TAG + "_GET_USER";


    private static final int GET_COUNTRY_CODE = 0;
    private static final int GO_DETAILS = 1;
    private static final int GO_MAIN = 2;

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private TextView tvLogIn;
    private Button brLogIn;
    private TextView tvForgetPassword;
    private ImageView ivUsername;
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
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        initView(view);

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
                break;

            case R.id.iv_username:
                goLogInUsernameActivity();
                break;

//            case R.id.ll_log_in_with_username:
//                goLogInUsernameActivity();
//                break;

            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode)
            {
                case GET_COUNTRY_CODE:
                    if (resultCode == getActivity().RESULT_OK)
                    {
                        tvCountry.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
                        tvCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                    }
                    break;

                default:
                    break;
            }
    }

    private void initView(View view)
    {
        rlCountryCode = (RelativeLayout)view.findViewById(R.id.rl_country_code);
        tvCountry = (TextView)view.findViewById(R.id.tv_country);
        tvCountryCode = (TextView)view.findViewById(R.id.tv_country_code);
        etPhoneNumber = (EditText)view.findViewById(R.id.et_phone_number);
        etPassword = (EditText)view.findViewById(R.id.et_password);
//        tvLogIn = (TextView)view.findViewById(R.id.tv_btn_log_in);
        brLogIn = (Button)view.findViewById(R.id.br_log_in);
        tvForgetPassword = (TextView)view.findViewById(R.id.tv_forget_password);
        ivUsername = (ImageView)view.findViewById(R.id.iv_username);
        rlProgress = (RelativeLayout)view.findViewById(R.id.rl_progress);

//        tvLogIn.setOnClickListener(this);
        brLogIn.setOnClickListener(this);

        ivUsername.setOnClickListener(this);
        rlCountryCode.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);

        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));
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

        if(!MyTextUtil.checkEmptyInputText(strCountryCode, strPhoneNumber, strPassword))
        {
            rlProgress.setVisibility(View.VISIBLE);
//            tvLogIn.setClickable(false);
            brLogIn.setClickable(false);

            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("user_country_code", strCountryCode);
            jsonParams.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
            jsonParams.put("username", "");
            jsonParams.put("user_password", MD5Util.string2MD5(strPassword));
            jsonParams.put("login_type", Constant.LOGIN_TYPE_PHONE);
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
                    rlProgress.setVisibility(View.GONE);
//                    tvLogIn.setClickable(true);
                    brLogIn.setClickable(true);
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
                            //bad date????
                            return;
                        }

                        if ("true".equals(verificationFlag))
                        {
                            userEntity = userEntities.get(0);
                            App.changeLoginedUser(userEntity, tokenEntity);
                            PreferencesUtil.saveValue(getActivity(), Constant.HAS_LOGED_IN, Constant.HAS_LOGED_IN);
                            handler.sendEmptyMessage(GO_MAIN);
                        }
                        else
                        {
                            //这种情况当做是登陆过吗？
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

    public void goMainActivity()
    {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        PushApi.initPushApi(getActivity());
        getActivity().finish();
    }

    public void goDetails()
    {

    }

    public void goLogInUsernameActivity()
    {
        Intent intent = new Intent(getActivity(), LogInUsernameActivity.class);
        startActivity(intent);
    }
}
