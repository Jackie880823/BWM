package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
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
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpUsernameVerifyPhoneActivity extends BaseActivity {

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
    LinearLayout llUsernameRegistration;

    UserEntity userEntity = new UserEntity();

    private TimeCount time;

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

        userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");

        rlCountryCode = getViewById(R.id.rl_code);//跳转选国家区号
        tvCountry = getViewById(R.id.tv_country);//国家
        tvCountryCode = getViewById(R.id.tv_code);//国家区号

        etPhone = getViewById(R.id.et_phone_number);//输入手机号

        llWarning = getViewById(R.id.ll_warning);//显示
        tvWarningPhone = getViewById(R.id.tv_phone_number);//红颜色手机号

        etVerifyCode = getViewById(R.id.et_verification);//输入验证码
        btnRequest = getViewById(R.id.btn_send);//请求验证码

        btnNext = getViewById(R.id.btn_next);//下一步

        tvUsernameRegistration = getViewById(R.id.tv_username_registration);//跳转用户名注册
        llUsernameRegistration =getViewById(R.id.ll_username_registration);
        llUsernameRegistration.setVisibility(View.INVISIBLE);//隐藏
        tvUsernameRegistration.setVisibility(View.INVISIBLE);//隐藏

        tvCountryCode.setText(GetCountryZipCode());//自动获取国家区号，需要SIM卡

        time = new TimeCount(60000, 1000);//构造CountDownTimer对象

        rlCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpUsernameVerifyPhoneActivity.this, CountryCodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });





        //用户名注册-请求验证码
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((etPhone.getText().toString().length() != 0) && (tvCountryCode.getText().toString().length() != 0))
                {

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user_phone_number", tvCountryCode.getText().toString() + etPhone.getText().toString());
                    params.put("verification_action", "register");




                    new HttpTools(SignUpUsernameVerifyPhoneActivity.this).post(Constant.API_VERIFICATION, params, this,new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResult(String response) {
                            tvWarningPhone.setText(tvCountryCode.getText().toString() + etPhone.getText().toString());
                            llWarning.setVisibility(View.VISIBLE);

                            try {
                                String responseStatus;
                                JSONObject jsonObject = new JSONObject(response);
                                responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
                                if (responseStatus.equals("Fail")) {
                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_createVerification_fail), Toast.LENGTH_SHORT).show();
                                } else {
                                    time = new TimeCount(60000, 1000);//构造CountDownTimer对象
                                    time.start();//开始计时
                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_createVerification_success), Toast.LENGTH_SHORT).show();
                                    etVerifyCode.requestFocus();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });






//                    StringRequest stringRequestVerification = new StringRequest(Request.Method.POST, Constant.API_VERIFICATION, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            tvWarningPhone.setText(tvCountryCode.getText().toString() + etPhone.getText().toString());
//                            llWarning.setVisibility(View.VISIBLE);
//
//                            try {
//                                String responseStatus;
//                                JSONObject jsonObject = new JSONObject(response);
//                                responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
//                                if (responseStatus.equals("Fail")) {
//                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_createVerification_fail), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    time = new TimeCount(60000, 1000);//构造CountDownTimer对象
//                                    time.start();//开始计时
//                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_createVerification_success), Toast.LENGTH_SHORT).show();
//                                    etVerifyCode.requestFocus();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            //TODO
//                            error.printStackTrace();
//                            Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            HashMap<String, String> params = new HashMap<String, String>();
//
//                            params.put("user_phone_number", tvCountryCode.getText().toString() + etPhone.getText().toString());
//                            params.put("verification_action", "register");
//                            return params;
//                        }
//                    };
//                    stringRequestVerification.setShouldCache(false);
//                    VolleyUtil.addRequest2Queue(SignUpUsernameVerifyPhoneActivity.this, stringRequestVerification, "");



                }

                else if (tvCountryCode.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_choose_country_code), Toast.LENGTH_SHORT).show();
                }

                else if (etPhone.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_input_phone_number), Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }

            }
        });


        //用户名注册-验证验证码
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((etPhone.getText().toString().length() != 0) && (tvCountryCode.getText().toString().length() != 0) && (!TextUtils.isEmpty(etVerifyCode.getText())))
                {
                    String url;
                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("user_phone_number", tvCountryCode.getText().toString() + etPhone.getText().toString());
                    jsonParams.put("verify_code", etVerifyCode.getText().toString());
                    String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("condition", jsonParamsString);
                    url = UrlUtil.generateUrl(Constant.API_VERIFY_CODE, params);



                    new HttpTools(SignUpUsernameVerifyPhoneActivity.this).get(Constant.API_VERIFY_CODE, params, this,new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResult(String response) {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getString("response_status").equals("Fail"))
                                {
                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_verification_fail), Toast.LENGTH_SHORT).show();
                                }
                                else if (jsonObject.getString("response_status").equals("Success"))
                                {
                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_verification_successful), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpUsernameVerifyPhoneActivity.this, InformationUsernameActivity.class);

                                    userEntity.setUser_phone(etPhone.getText().toString());
                                    userEntity.setUser_country_code(tvCountryCode.getText().toString());

                                    intent.putExtra("userEntity", userEntity);
                                    startActivity(intent);
                                }
                            }
                            catch (JSONException e)
                            {
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


//
//                    StringRequest stringRequestVerifyCode = new StringRequest(url, new Response.Listener<String>() {
//
//                        @Override
//                        public void onResponse(String response) {
//                            try
//                            {
//                                JSONObject jsonObject = new JSONObject(response);
//
//                                if (jsonObject.getString("response_status").equals("Fail"))
//                                {
//                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_verification_fail), Toast.LENGTH_SHORT).show();
//                                }
//                                else if (jsonObject.getString("response_status").equals("Success"))
//                                {
//                                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_verification_successful), Toast.LENGTH_SHORT).show();
//
//                                    Intent intent = new Intent(SignUpUsernameVerifyPhoneActivity.this, InformationUsernameActivity.class);
//
//                                    userEntity.setUser_phone(etPhone.getText().toString());
//                                    userEntity.setUser_country_code(tvCountryCode.getText().toString());
//
//                                    intent.putExtra("userEntity", userEntity);
//                                    startActivity(intent);
//                                }
//                            }
//                            catch (JSONException e)
//                            {
//                                e.printStackTrace();
//                            }
//                        }
//                    },new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//                    });
//                    stringRequestVerifyCode.setShouldCache(false);
//                    VolleyUtil.addRequest2Queue(SignUpUsernameVerifyPhoneActivity.this, stringRequestVerifyCode, "");



                }

                else if (tvCountryCode.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_choose_country_code), Toast.LENGTH_SHORT).show();
                }

                else if (etPhone.getText().toString().length() == 0)
                {
                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_input_phone_number), Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(SignUpUsernameVerifyPhoneActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
}
