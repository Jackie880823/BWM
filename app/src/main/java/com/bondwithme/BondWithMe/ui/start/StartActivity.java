package com.bondwithme.BondWithMe.ui.start;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.PushApi;
import com.google.gson.Gson;

public class StartActivity extends FragmentActivity implements View.OnClickListener {

    private static final String SHOW_SIGN_UP = "sign up";
    private static final String SHOW_LOG_IN = "log in";
    private SignUpPhoneFragment signUpPhoneFragment = new SignUpPhoneFragment();
    private LogInPhoneFragment logInPhoneFragment = new LogInPhoneFragment();

    private LinearLayout llSignUp;
    private TextView tvSignUp;
    private ImageView ivSignUp;

    private LinearLayout llLogIn;
    private TextView tvLogIn;
    private ImageView ivLogIn;

    private boolean blnSignUp = false;
    private boolean blnLogIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkForGoMainActivity();
        initView();
        checkForShow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_sign_up:
                    showSignUp();
                break;

            case R.id.ll_log_in:
                    showLogIn();
                break;

            default:
                break;
        }
    }

    public void initView()
    {
        llSignUp = (LinearLayout)findViewById(R.id.ll_sign_up);
        tvSignUp = (TextView)findViewById(R.id.tv_sign_up);
        ivSignUp = (ImageView)findViewById(R.id.iv_sign_up);
        llLogIn = (LinearLayout)findViewById(R.id.ll_log_in);
        tvLogIn = (TextView)findViewById(R.id.tv_log_in);
        ivLogIn = (ImageView)findViewById(R.id.iv_log_in);

        llSignUp.setOnClickListener(this);
        llLogIn.setOnClickListener(this);
    }

    public void showSignUp()
    {
        //        可以优化为hide？
        if (blnSignUp)
        {
            return;
        }
        setLook(SHOW_SIGN_UP);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_start, signUpPhoneFragment);
        transaction.commit();
        blnSignUp = true;
        blnLogIn = false;
    }

    public void showLogIn()
    {
        //        可以优化为hide？
        if (blnLogIn)
        {
            return;
        }
        setLook(SHOW_LOG_IN);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_start, logInPhoneFragment);
        transaction.commit();
        blnLogIn = true;
        blnSignUp = false;
    }

    public void setLook(String string)
    {
        switch (string)
        {
            case SHOW_SIGN_UP:
                tvSignUp.setTextColor(getResources().getColor(R.color.default_text_color_dark));
                tvSignUp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                ivSignUp.setVisibility(View.VISIBLE);
                tvLogIn.setTextColor(getResources().getColor(R.color.default_text_color_while));
                tvLogIn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                ivLogIn.setVisibility(View.INVISIBLE);
                break;

            case SHOW_LOG_IN:
                tvLogIn.setTextColor(getResources().getColor(R.color.default_text_color_dark));
                tvLogIn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                ivLogIn.setVisibility(View.VISIBLE);
                tvSignUp.setTextColor(getResources().getColor(R.color.default_text_color_while));
                tvSignUp.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                ivSignUp.setVisibility(View.INVISIBLE);
                break;

            default:
                break;

        }
    }

    public void checkForGoMainActivity()
    {
        UserEntity userEntity = App.getLoginedUser();
        if (userEntity != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            String tokenString = PreferencesUtil.getValue(this, Constant.HTTP_TOKEN, null);
            if (!TextUtils.isEmpty(tokenString)) {
                App.initToken(userEntity.getUser_login_id(), new Gson().fromJson(tokenString, AppTokenEntity.class));//为什么这里又要初始化Token??
            }
            PushApi.initPushApi(this);
            finish();
            return;
        }
    }

    public void checkForShow()
    {
        if (TextUtils.isEmpty(PreferencesUtil.getValue(this, Constant.HAS_LOGED_IN, null)))
        {
            showSignUp();
        }
        else
        {
            showLogIn();
        }
        return;
    }
}
