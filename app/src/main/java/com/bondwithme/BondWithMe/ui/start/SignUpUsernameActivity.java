package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.gc.materialdesign.views.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignUpUsernameActivity extends BaseActivity implements View.OnClickListener, EditText.OnEditorActionListener{

    private static final String TAG = SignUpUsernameActivity.class.getSimpleName();
    private final static String CHECK_GET_CODE = TAG + "_CHECK_GET_CODE";

    public final static String RESPONSE_MESSAGE_ID_EXIST = "Server.LoginIdExist";

    private static final int GET_COUNTRY_CODE = 0;

    private static final int ERROR = -1;
    private static final int SUCCESS_GET_CODE = 1;
    private static final int LOG_IN_EXIST = 2;
    private static final int CATCH = 3;

    private EditText etUsername;
    private TextView tvUsernamePrompt;
    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private TextView tvStartCountryCode;
    private EditText etPhoneNumber;
//    private TextView tvPhoneNumberPrompt;
    private EditText etPassword;
    private TextView tvPasswordPrompt;
    private Button brNext;
    private RelativeLayout rlProgress;

    private String strUsername;
    private String strCountryCode;
    private String strPhoneNumber;
    private String strPassword;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SUCCESS_GET_CODE:
                    //账号可用，获得验证码，跳转界面。
                    goVerification();
                    break;

                case LOG_IN_EXIST:
                    //账号不可用，显示提示。
                    etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvUsernamePrompt.setText(R.string.text_start_username_exist);
                    tvUsernamePrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
                    break;

                case CATCH:
                    //验证码发送失败
                    break;

                case ERROR:
                    //请求错误
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_sign_up_username2;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.text_start_sign_up));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.btn_gradient_color_green_normal);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        etUsername = getViewById(R.id.et_username);
        tvUsernamePrompt = getViewById(R.id.tv_username_prompt);
        rlCountryCode = getViewById(R.id.rl_country_code);
        tvCountry = getViewById(R.id.tv_country);
        tvCountryCode = getViewById(R.id.tv_country_code);
        tvStartCountryCode = getViewById(R.id.tv_start_country_code);
        etPhoneNumber = getViewById(R.id.et_phone_number);
//        tvPhoneNumberPrompt = getViewById(R.id.tv_phone_number_prompt);
        etPassword = getViewById(R.id.et_password);
        tvPasswordPrompt = getViewById(R.id.tv_password_prompt);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);

        brNext.setOnClickListener(this);
        rlCountryCode.setOnClickListener(this);

        etPassword.setOnEditorActionListener(this);

        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(this));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(this));

        //怎么把以下合并起来。过于臃肿。
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etUsername.getText().toString().length() < 5) {
                    etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvUsernamePrompt.setText(R.string.text_start_least5_prompt);
                    tvUsernamePrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
                } else {
                    etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    tvUsernamePrompt.setText(R.string.text_start_least5_prompt);
                    tvUsernamePrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
                }
            }
        });

        tvCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(tvCountryCode.getText().toString()))
                {
                    rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                }
                else
                {
                    rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                }
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
                if(TextUtils.isEmpty(etPhoneNumber.getText().toString()))
                {
                    etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
//                    tvPhoneNumberPrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
                }
                else
                {
                    etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
//                    tvPhoneNumberPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
                }
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
                if (etPassword.getText().toString().length() < 5)
                {
                    etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvPasswordPrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
                }
                else
                {
                    etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    tvPasswordPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.rl_country_code:
                Intent intent = new Intent(this, CountryCodeActivity.class);
                startActivityForResult(intent, GET_COUNTRY_CODE);
                break;

            case R.id.br_next:
                doSignUp();
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case GET_COUNTRY_CODE:
                if (resultCode == RESULT_OK)
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

    /**
     * 功能：网络请求
     */
    private void doSignUp()
    {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strUsername = etUsername.getText().toString();
        strCountryCode = tvCountryCode.getText().toString().trim();
        strPhoneNumber = etPhoneNumber.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if( (checkAll()))//检查输入，提取出来。
        {
            doingSignUpChangeUI();

            HashMap<String, String> params = new HashMap<>();
            params.put("user_login_id", strUsername);
            params.put("user_country_code",strCountryCode);
            params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));

            new HttpTools(this).get(Constant.API_START_CHECK_LOG_ID, params, CHECK_GET_CODE, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    finishLogInChangeUI();
                }

                @Override
                public void onResult(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                            //成功获得验证码
                            handler.sendEmptyMessage(SUCCESS_GET_CODE);
                        } else if (Constant.FAIL.equals(jsonObject.getString("response_status"))) {
                            if (RESPONSE_MESSAGE_ID_EXIST.equals(jsonObject.getString("response_message"))) {
                                //账号被注册
                                handler.sendEmptyMessage(LOG_IN_EXIST);
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
        else
        {
            //log id
            if (TextUtils.isEmpty(etUsername.getText().toString()))
            {
                etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                tvUsernamePrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
            }
            else
            {
                etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvUsernamePrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
            }

            //code
            if (TextUtils.isEmpty(tvCountryCode.getText().toString()))
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            //phone
            if(TextUtils.isEmpty(etPhoneNumber.getText().toString()))
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
//                tvPhoneNumberPrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
            }
            else
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
//                tvPhoneNumberPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
            }

            //password
            if (TextUtils.isEmpty(etPassword.getText().toString()))
            {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                tvPasswordPrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
            }
            else
            {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvPasswordPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
            }
        }
    }

    private boolean checkAll() {
        return ( (etUsername.getText().toString().length() > 4) && (!TextUtils.isEmpty(tvCountryCode.getText().toString())) && (!TextUtils.isEmpty(etPhoneNumber.getText().toString())) && (etPassword.getText().toString().length() > 4) );
    }

    private void doingSignUpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
        normalUI();//亲，真的需要吗？
        UIUtil.hideKeyboard(this, etPassword);
    }

    private void finishLogInChangeUI() {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            doSignUp();
            return true;
        }
        return false;
    }


    private void normalUI()
    {
        etUsername.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
        tvUsernamePrompt.setText(R.string.text_start_least5_prompt);
        tvUsernamePrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));

        rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);

        etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);

        etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
        tvPasswordPrompt.setText(R.string.text_start_least5_prompt);
        tvPasswordPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
    }

    private void goVerification()
    {
        Intent intent = new Intent(SignUpUsernameActivity.this, VerificationActivity.class);
        intent.putExtra(Constant.TYPE, Constant.TYPE_USERNAME);
        intent.putExtra("user_login_id", strUsername);
        intent.putExtra("user_country_code", strCountryCode);
        intent.putExtra("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        intent.putExtra("user_password", MD5Util.string2MD5(strPassword));
        startActivity(intent);
    }
}