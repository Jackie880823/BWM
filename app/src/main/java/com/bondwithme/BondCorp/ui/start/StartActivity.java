package com.bondwithme.BondCorp.ui.start;


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

import com.appsflyer.AppsFlyerLib;
import com.bondwithme.BondCorp.App;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.Tranck.MyAppsFlyer;
import com.bondwithme.BondCorp.Tranck.MyPiwik;
import com.bondwithme.BondCorp.util.PreferencesUtil;

public class StartActivity extends FragmentActivity implements View.OnClickListener {

    public static final String SHOW_SIGN_UP = "sign up";
    public static final String SHOW_LOG_IN = "log in";
    public static final String TYPE = "type";
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
        App.checkVerSion(this);//
        setContentView(R.layout.activity_start);

        initView();
        checkForShow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    public void initView() {
        llSignUp = (LinearLayout) findViewById(R.id.ll_sign_up);
        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);
        ivSignUp = (ImageView) findViewById(R.id.iv_sign_up);
        llLogIn = (LinearLayout) findViewById(R.id.ll_log_in);
        tvLogIn = (TextView) findViewById(R.id.tv_log_in);
        ivLogIn = (ImageView) findViewById(R.id.iv_log_in);

        llSignUp.setOnClickListener(this);
        llLogIn.setOnClickListener(this);
    }

    public void showSignUp() {
        //        可以优化为hide？
        if (blnSignUp) {
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

    public void showLogIn() {
        //        可以优化为hide？
        if (blnLogIn) {
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

    public void setLook(String string) {
        switch (string) {
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


    public void checkForShow() {
        if (SHOW_SIGN_UP.equals(getIntent().getStringExtra(TYPE))) {
            showSignUp();
            MyAppsFlyer.appsFlyerGuest();
            MyPiwik.piwikGuest();
        } else if (SHOW_LOG_IN.equals(getIntent().getStringExtra(TYPE))) {
            showLogIn();
        } else if (TextUtils.isEmpty(PreferencesUtil.getValue(this, Constant.HAS_LOGED_IN, null))) {
            showSignUp();
            MyAppsFlyer.appsFlyerGuest();
            MyPiwik.piwikGuest();
        } else {
            showLogIn();
        }
        return;
    }

    @Override
    public void onStart() {
        super.onStart();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }
}
