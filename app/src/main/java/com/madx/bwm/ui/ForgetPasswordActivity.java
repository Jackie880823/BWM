package com.madx.bwm.ui;

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
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgetPasswordActivity extends BaseActivity {

//  ========================
    EditText etUsername;
//  -----------or-----------
    RelativeLayout rlCountryCode;//跳转到国家区号选择
    TextView tvCountry;//国家名
    TextView tvCountryCode;//国家区号
    EditText etPhone;//输入的手机号
//  ========================

    LinearLayout llWarning;
    TextView tvPhone;

    EditText etVerifyCode;//输入的验证码
    Button btnRequest;//获取验证码
    String userId ;//申请验证码成功以后返回的用户ID，到时候会在点击Next的时候使用到
    String phoneNumber;//申请验证码成功以后返回的用户手机号，到时候会在点击Next的时候使用到

    Button btnNext;//跳转到下个界面设置新密码

    TextView tvContact;//点击后显示contact界面
    RelativeLayout llContact;//隐藏的弹窗
    String phoneString = "86700781"; //联系电话
    Button btnCall;//打电话
    Button btnCancle;//点击后隐藏contact界面
    RelativeLayout llBottom;//

    UserEntity userEntity = new UserEntity();

    private TimeCount time;

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            btnRequest.setText("Again");
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
        return R.layout.activity_forget_password;
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
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_forget_password));
        /**
         * end
         */
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
//        setListenerToRootView();

        etUsername = getViewById(R.id.et_username);

        rlCountryCode = getViewById(R.id.rl_code);//跳转选国家区号
        tvCountry = getViewById(R.id.tv_country);
        tvCountryCode = getViewById(R.id.tv_code);//国家区号
        etPhone = getViewById(R.id.et_phone_number);

        llWarning = getViewById(R.id.ll_warning);
        tvPhone = getViewById(R.id.tv_phone_number);

        etVerifyCode = getViewById(R.id.et_verification);//输入验证码

//        etVerifyCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

//        etVerifyCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true)
//                {
//                    btnNext.setVisibility(View.GONE);
//                }
//                else
//                {
//                    btnNext.setVisibility(View.VISIBLE);
//                }
//            }
//        });


        btnRequest = getViewById(R.id.btn_send);//请求验证码

        btnNext = getViewById(R.id.btn_next);

        tvContact = getViewById(R.id.tv_contact);
        llContact = getViewById(R.id.ll_contact);
        btnCall = getViewById(R.id.btn_call);
        btnCancle = getViewById(R.id.btn_cancel);
        llBottom = getViewById(R.id.ll_bottom);

        tvCountryCode.setText(GetCountryZipCode());//设置自动获取到的国家区号

        time = new TimeCount(60000, 1000);//构造CountDownTimer对象

        //跳转到国家区号选择list
        rlCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, CountryCodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        //申请验证码,username or code+phone
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> params = new HashMap<String, String>();

                if (!TextUtils.isEmpty(etUsername.getText()))
                {
                    if(!TextUtils.isEmpty(etPhone.getText())){
                        /**
                         * begin QK
                         */
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_please_select_username_phone), Toast.LENGTH_SHORT).show();
                        /**
                         * end
                         */
                        return;
                    }
                    params.put("user_login_id", etUsername.getText().toString());//用户名作为账号
                    requestVerification(params);

                    userEntity.setUser_login_id(etUsername.getText().toString());
                    userEntity.setUser_login_type("username");
                }
                else if(!TextUtils.isEmpty(etPhone.getText()))
                {
                    if (TextUtils.isEmpty(tvCountryCode.getText()))
                    {
                        /**
                         * begin QK
                         */
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_please_choose_country_code), Toast.LENGTH_SHORT).show();
                        /**
                         * end
                         */
                        return;
                    }
                    params.put("user_login_id", tvCountryCode.getText().toString() + etPhone.getText().toString());//手机号作为账号
                    requestVerification(params);

                    llWarning.setVisibility(View.VISIBLE);
                    tvPhone.setText(tvCountryCode.getText().toString() + etPhone.getText().toString());

                    userEntity.setUser_country_code(tvCountryCode.getText().toString());
                    userEntity.setUser_phone(etPhone.getText().toString());
                    userEntity.setUser_login_id(tvCountryCode.getText().toString() + etPhone.getText().toString());
                    userEntity.setUser_login_type("phone");
                }
                else
                {
                    //username && phone 都为空
                    /**
                     * begin QK
                     */
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_input_username_phone), Toast.LENGTH_SHORT).show();
                    /**
                     * end
                     */
                }
            }
        });


        //检查验证码, 需要用到请求验证码时服务器发送来的手机号。成功就跳转到下个界面填写新的账号密码
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((etVerifyCode.getText().toString().length() != 0)) {
                    String url;
                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("user_phone_number", phoneNumber);//验证码提交后返回的数据中有个手机号 用在这边。
                    jsonParams.put("verify_code", etVerifyCode.getText().toString());
                    String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("condition", jsonParamsString);

                    url = UrlUtil.generateUrl(Constant.API_VERIFY_CODE, params);


                    new HttpTools(ForgetPasswordActivity.this).get(url, null, new HttpCallback() {
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

                                if ((jsonObject.getString("response_status")).equals("Fail")) {
                                    Toast.makeText(ForgetPasswordActivity.this, "Verification fail. Try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, "Verification successful.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);

                                    intent.putExtra("userEntity", userEntity);

                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });


//
//                    StringRequest stringRequest3 = new StringRequest(url, new Response.Listener<String>() {
//
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//
//                                if ((jsonObject.getString("response_status")).equals("Fail")) {
//                                    Toast.makeText(ForgetPasswordActivity.this, "Verification fail. Try again.", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(ForgetPasswordActivity.this, "Verification successful.", Toast.LENGTH_SHORT).show();
//
//                                    Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
//
//                                    intent.putExtra("userEntity", userEntity);
//
//                                    startActivity(intent);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            /**
//                             * begin QK
//                             */
//                            Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                            /**
//                             * end
//                             */
//                        }
//                    });
//                    stringRequest3.setShouldCache(false);
//                    VolleyUtil.addRequest2Queue(ForgetPasswordActivity.this, stringRequest3, "");
                }
                else if (etVerifyCode.getText().toString().length() == 0)
                {
                    /**
                     * begin QK
                     */
                    Toast.makeText(ForgetPasswordActivity.this,getResources().getString(R.string.text_fill_in_verification_code), Toast.LENGTH_SHORT).show();
                    /**
                     * end
                     */
                }
            }
        });

        //弹出打电话界面
        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llContact.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
            }
        });

        //隐藏打电话界面
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llContact.setVisibility(View.GONE);
                llBottom.setVisibility(View.VISIBLE);
            }
        });

//        //打电话
//        btnCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.CALL");
//                intent.setData(Uri.parse("tel:" + phoneString));
//                startActivity(intent);
//            }
//        });
    }

    //跳转到国家区号选择界面结束后调用
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

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void requestVerification(HashMap<String, String> params)
    {
        String url = UrlUtil.generateUrl(Constant.API_VERIFY_CODE_FOR_FORGETPASSWORD, params);




        new HttpTools(ForgetPasswordActivity.this).get(url, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
                    if (responseStatus.equals("Fail")) {
                        /**
                         * begin QK
                         */
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_createVerification_fail), Toast.LENGTH_SHORT).show();
                        /**
                         * end
                         */
                    } else {
                        time.start();//开始计时
                        userId = jsonObject.getString("user_id");
                        userEntity.setUser_id(userId);
                        phoneNumber = jsonObject.getString("phone_number");
                        /**
                         * begin QK
                         */
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_createVerification_success), Toast.LENGTH_SHORT).show();
                        /**
                         * end
                         */
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });





//
//        StringRequest stringRequestVerification = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    String responseStatus;
//                    JSONObject jsonObject = new JSONObject(response);
//                    responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
//                    if (responseStatus.equals("Fail")) {
//                        /**
//                         * begin QK
//                         */
//                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_createVerification_fail), Toast.LENGTH_SHORT).show();
//                        /**
//                         * end
//                         */
//                    } else {
//                        time.start();//开始计时
//                        userId = jsonObject.getString("user_id");
//                        userEntity.setUser_id(userId);
//                        phoneNumber = jsonObject.getString("phone_number");
//                        /**
//                         * begin QK
//                         */
//                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_createVerification_success), Toast.LENGTH_SHORT).show();
//                        /**
//                         * end
//                         */
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //TODO
//                error.printStackTrace();
//                /**
//                 * begin QK
//                 */
//                Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                /**
//                 * end
//                 */
//            }
//        });
//        VolleyUtil.addRequest2Queue(ForgetPasswordActivity.this, stringRequestVerification, "");
    }

    //自动获取国家区号
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

    //判断字符串的是否为字母，字母true。
    public static boolean test(String s)
    {
        char   c   =   s.charAt(0);
        int   i   =(int)c;
        if((i>=65&&i<=90)||(i>=97&&i<=122))
        {
            return   true;
        }
        else
        {
            return   false;
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//
//        // Checks whether a hardware keyboard is available
//        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
//            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();

//
//        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
//            btnNext.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
//        }
//    }

//    boolean isOpened = false;
//
//    public void setListenerToRootView(){
//        final View activityRootView = getWindow().getDecorView().findViewById(R.id.ll_content);
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
//                if (heightDiff > 100 ) { // 99% of the time the height diff will be due to a keyboard.
//                    Toast.makeText(getApplicationContext(), "Gotcha!!! softKeyboardup", Toast.LENGTH_SHORT).show();
//                    if(isOpened == false){
//                        btnNext.setVisibility(View.VISIBLE);
//                        //Do two things, make the view top visible and the editText smaller
//                    }
//                    isOpened = true;
//                }else if(isOpened == true){
//                    Toast.makeText(getApplicationContext(), "softkeyborad Down!!!", Toast.LENGTH_SHORT).show();
//                    btnNext.setVisibility(View.INVISIBLE);
//                    isOpened = false;
//                }
//            }
//        });
//    }

}
