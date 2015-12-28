package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.wall.NewDiaryActivity;

public class MeActivity extends BaseActivity implements MeFragment.MeFragmentListener{
    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_me));
    }

    @Override
    protected void titleRightEvent() {
        startActivityForResult(new Intent(this, NewDiaryActivity.class), Constant.INTENT_REQUEST_CREATE_WALL);
    }

    @Override
    protected Fragment getFragment() {
        MeFragment fragment = MeFragment.newInstance();
        fragment.setListener(this);
        return fragment;
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

    @Override
    protected void titleLeftEvent() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            rightButton.setVisibility(View.GONE);
        } else {
            super.titleLeftEvent();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                titleLeftEvent();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /**
     * 点击了日记
     */
    @Override
    public void clickedDiary() {
        rightButton.setVisibility(View.VISIBLE);
    }

    /**
     * 点击了除日记以外的控件
     *
     * @param resID 控件ID
     */
    @Override
    public void clickedOther(int resID) {

    }
}
