package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MyTextUtil;
import com.madx.bwm.util.NetworkUtil;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUpPhoneActivity extends BaseActivity {

    RelativeLayout rlCountryCode;//跳转到国家区号选择
    TextView tvCountry;//国家名
    TextView tvCountryCode;//国家区号

    EditText etPhone;//输入的手机号

    LinearLayout llWarning;//提示用户输入的手机号码
    TextView tvWarningPhone;//把区号+手机号显示出来提示给用户的

    EditText etVerifyCode;//输入的验证码
    Button btnRequest;//获取验证码

    Button btnNext;//下一步

    TextView tvUsernameRegistration;//跳转到username注册

    UserEntity userEntity = new UserEntity();

    private TimeCount time;

    String phone;

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            btnRequest.setText(getString(R.string.text_try_again));
            btnRequest.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            btnRequest.setClickable(false);
            btnRequest.setText(millisUntilFinished / 1000 + " S");
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_verify_phone;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_login);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_sign_up));
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

        rlCountryCode = getViewById(R.id.rl_code);//跳转选国家区号
        tvCountry = getViewById(R.id.tv_country);
        tvCountryCode = getViewById(R.id.tv_code);//国家区号

        etPhone = getViewById(R.id.et_phone_number);//输入手机号

        llWarning = getViewById(R.id.ll_warning);//显示
        tvWarningPhone = getViewById(R.id.tv_phone_number);//红颜色手机号

        etVerifyCode = getViewById(R.id.et_verification);//输入验证码
        btnRequest = getViewById(R.id.btn_send);//请求验证码

        btnNext = getViewById(R.id.btn_next);//下一步

        tvUsernameRegistration = getViewById(R.id.tv_username_registration);//跳转用户名注册

        tvCountryCode.setText(GetCountryZipCode());//自动获取国家区号，需要SIM卡

        time = new TimeCount(60000, 1000);//构造CountDownTimer对象

        tvUsernameRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到使用用户名注册的界面
                Intent intent = new Intent(SignUpPhoneActivity.this, SignUpUsernameActivity.class);
                startActivity(intent);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkConnected(SignUpPhoneActivity.this)) {
                    Toast.makeText(SignUpPhoneActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((etPhone.getText().toString().length() != 0) && (tvCountryCode.getText().toString().length() != 0))//输入了号码才去请求验证码
                {
                    phone = MyTextUtil.NoZero(etPhone.getText().toString());
                    /*
                    * 先检查loginId(区号+手机号)是否能用，不合法:提示，合法:post得到验证码
                    * */
                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("login_id", tvCountryCode.getText().toString() + phone);
                    String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("condition", jsonParamsString);
//                    String url = UrlUtil.generateUrl(Constant.API_LOGINID_AVAILABILITY, params);//检查手机注册合法性检查URL

                    //手机注册合法性检查请求
                    new HttpTools(SignUpPhoneActivity.this).get(Constant.API_LOGINID_AVAILABILITY, params, new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResult(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                tvWarningPhone.setText(tvCountryCode.getText().toString() + phone);
                                llWarning.setVisibility(View.VISIBLE);

                                if ("Fail".equals(jsonObject.getString("response_status"))) {
                                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_account_already_exist), Toast.LENGTH_SHORT).show();
                                } else if ("Success".equals(jsonObject.getString("response_status"))){
                                    time.start();//开始计时,手机合法才倒计时。
                                    //手机号码合法获取验证码
                                    getVerification();
                                }
                                else {
                                    Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });
                }
                else if (tvCountryCode.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_choose_country_code), Toast.LENGTH_SHORT).show();
                }

                else if (etPhone.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_input_phone_number), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkConnected(SignUpPhoneActivity.this)) {
                    Toast.makeText(SignUpPhoneActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    return;
                }


                if ((etPhone.getText().toString().length() != 0) && (etVerifyCode.getText().toString().length() != 0)) {

                    phone = MyTextUtil.NoZero(etPhone.getText().toString());

                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("user_phone_number", tvCountryCode.getText().toString() + phone);
                    jsonParams.put("verify_code", etVerifyCode.getText().toString());
                    String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("condition", jsonParamsString);
                    String url = UrlUtil.generateUrl(Constant.API_VERIFY_CODE, params);

                    new HttpTools(SignUpPhoneActivity.this).get(Constant.API_VERIFY_CODE, params, new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResult(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if ("Fail".equals(jsonObject.getString("response_status")))
                                {
                                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_fail_verify_phone_number), Toast.LENGTH_SHORT).show();
                                } else if ("Success".equals(jsonObject.getString("response_status")))
                                {
                                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_verification_successful), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpPhoneActivity.this, SignUpPhonePasswordActivity.class);

                                    userEntity.setUser_country_code(tvCountryCode.getText().toString());//国家区号
                                    userEntity.setUser_phone(phone);//手机号
                                    userEntity.setUser_login_id(tvCountryCode.getText().toString() + phone);//账号
                                    userEntity.setUser_login_type("phone");//注册类型phone

                                    intent.putExtra("userEntity",userEntity);

                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });
                }
                else if (tvCountryCode.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_choose_country_code), Toast.LENGTH_SHORT).show();
                }
                else if (etPhone.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_input_phone_number), Toast.LENGTH_SHORT).show();
                }
                else if (etVerifyCode.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_fill_in_verification_code), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });

        rlCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPhoneActivity.this, CountryCodeActivity.class);
                startActivityForResult(intent, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    tvCountryCode.setText(data.getStringExtra("code"));
                }
        }
    }

    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }



    //获取验证码
    public void getVerification()
    {
        phone = MyTextUtil.NoZero(etPhone.getText().toString());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_phone_number", tvCountryCode.getText().toString() + phone);
        params.put("verification_action", "register");

        new HttpTools(SignUpPhoneActivity.this).post(Constant.API_VERIFICATION, params,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if ("Fail".equals(jsonObject.getString("response_status"))) {
                        Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_verification_fail), Toast.LENGTH_SHORT).show();
                    } else if ("Success".equals(jsonObject.getString("response_status"))){
                        Toast.makeText(SignUpPhoneActivity.this, getString(R.string.text_verification_successful), Toast.LENGTH_SHORT).show();
                        etVerifyCode.requestFocus();
                    }
                    else {
                        Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SignUpPhoneActivity.this,getString(R.string.text_error), Toast.LENGTH_LONG).show();
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
}
