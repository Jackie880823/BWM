package com.bondwithme.BondWithMe.ui.start;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;

public class ForgotPasswordActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_start_forgot_password);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_bg_color_green_normal);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
