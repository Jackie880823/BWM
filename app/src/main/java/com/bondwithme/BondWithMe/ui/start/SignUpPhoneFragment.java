package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.ui.TermsActivity;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.gc.materialdesign.views.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class SignUpPhoneFragment extends Fragment implements View.OnClickListener, EditText.OnEditorActionListener{

    private final static String TAG = SignUpPhoneFragment.class.getSimpleName();
    private final static String CHECK_GET_CODE = TAG + "_CHECK_GET_CODE";

    public final static String RESPONSE_MESSAGE_ID_EXIST = "Server.LoginIdExist";

    private static final int GET_COUNTRY_CODE = 0;

    private static final int ERROR = -1;
    private static final int SUCCESS_GET_CODE = 1;
    private static final int LOG_IN_EXIST = 2;
    private static final int CATCH = 3;

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private TextView tvStartCountryCode;
    private EditText etPhoneNumber;
    private TextView tvPhoneNumberPrompt;
    private EditText etPassword;
    private TextView tvPasswordPrompt;
//    private TextView tvLogIn;
    private Button brSignUp;
    private TextView tvTerms;
    private ImageView ivUsername;
    private RelativeLayout rlProgress;


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
                    tvPhoneNumberPrompt.setVisibility(View.VISIBLE);
                    etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);

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
//                break;

            case R.id.iv_username:
                startActivity(new Intent(getActivity(), SignUpUsernameActivity.class));
                break;

            case R.id.br_sign_up:
                doSignUp();
                break;

            case R.id.tv_terms:
                startActivity(new Intent(getActivity(), TermsActivity.class));
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
                    tvStartCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                }
                break;

            default:
                break;
        }
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

    private void initView(View view)
    {
        rlCountryCode = (RelativeLayout)view.findViewById(R.id.rl_country_code);
        tvCountry = (TextView)view.findViewById(R.id.tv_country);
        tvCountryCode = (TextView)view.findViewById(R.id.tv_country_code);
        tvStartCountryCode = (TextView)view.findViewById(R.id.tv_start_country_code);
        etPhoneNumber = (EditText)view.findViewById(R.id.et_phone_number);
        tvPhoneNumberPrompt = (TextView)view.findViewById(R.id.tv_phone_number_prompt);
        etPassword = (EditText)view.findViewById(R.id.et_password);
        tvPasswordPrompt = (TextView)view.findViewById(R.id.tv_password_prompt);
//        tvLogIn = (TextView)view.findViewById(R.id.tv_btn_log_in);
        brSignUp = (Button)view.findViewById(R.id.br_sign_up);
        tvTerms = (TextView)view.findViewById(R.id.tv_terms);
        ivUsername = (ImageView)view.findViewById(R.id.iv_username);
        rlProgress = (RelativeLayout)view.findViewById(R.id.rl_progress);

        rlCountryCode.setOnClickListener(this);
//        tvLogIn.setOnClickListener(this);
        brSignUp.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        ivUsername.setOnClickListener(this);

        etPassword.setOnEditorActionListener(this);

        tvTerms.setText(Html.fromHtml(getResources().getString(R.string.text_start_terms)));
        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));


        //怎么把下面两个统一在一起。、？？？
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //账号被提示已用，用户修改时，更改UI提示。
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvPhoneNumberPrompt.setVisibility(View.GONE);
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
                //用户输入密码长度不足时，提示。
                if (etPassword.getText().toString().length() < 5 && etPassword.getText().toString().length() > 0)
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

    /**
     * 功能：网络请求
     */
    public void doSignUp()
    {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strCountryCode = tvCountryCode.getText().toString().trim();
        strPhoneNumber = etPhoneNumber.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if( (!MyTextUtil.isHasEmpty(strCountryCode, strPhoneNumber, strPassword)) && (strPassword.length() > 4) )//检查输入，提取出来。
        {
            doingSignUpChangeUI();

            HashMap<String, String> params = new HashMap<>();
            params.put("user_login_id",strCountryCode + MyTextUtil.NoZero(strPhoneNumber));
            params.put("user_country_code",strCountryCode);
            params.put("user_phone",MyTextUtil.NoZero(strPhoneNumber));

            new HttpTools(getActivity()).get(Constant.API_START_CHECK_LOG_ID, params, CHECK_GET_CODE, new HttpCallback() {
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
                tvPasswordPrompt.setTextColor(getResources().getColor(R.color.stroke_color_red_wrong));
            }
            else
            {
                etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvPasswordPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
            }
        }



    }

    private void doingSignUpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        brSignUp.setClickable(false);
        normalUI();
        UIUtil.hideKeyboard(getActivity(), etPassword);
    }

    public void finishLogInChangeUI()
    {
        rlProgress.setVisibility(View.GONE);
        brSignUp.setClickable(true);
    }

    private void normalUI()
    {
        etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
        tvPhoneNumberPrompt.setVisibility(View.GONE);
        etPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
        tvPasswordPrompt.setTextColor(getResources().getColor(R.color.default_text_color_light));
    }

    private void goVerification()
    {
        Intent intent = new Intent(getActivity(), VerificationActivity.class);
        intent.putExtra(Constant.TYPE, Constant.TYPE_PHONE);
        intent.putExtra("user_login_id", strCountryCode + MyTextUtil.NoZero(strPhoneNumber));
        intent.putExtra("user_country_code", strCountryCode);
        intent.putExtra("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        intent.putExtra("user_password", MD5Util.string2MD5(strPassword));
        startActivity(intent);
    }
}