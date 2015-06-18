package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class FamilyProfileActivity extends BaseActivity {
    public List<UserEntity> data = new ArrayList<>();
    UserEntity userEntity = new UserEntity();

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
        tvTitle.setText(getResources().getString(R.string.title_family_profile));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return FamilyProfileFragment.newInstance();
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
        } else {
            super.titleLeftEvent();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                titleLeftEvent();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}
