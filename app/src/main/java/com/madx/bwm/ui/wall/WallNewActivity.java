package com.madx.bwm.ui.wall;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;
import com.madx.bwm.util.UIUtil;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class WallNewActivity extends BaseActivity {
    private static final String TAG = WallNewActivity.class.getSimpleName();

    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void setTitle() {

        tvTitle.setText(R.string.title_wall_new);
    }

    /**
     * TitilBar 左边事件
     */
    @Override
    protected void titleLeftEvent() {
        Fragment fragment = getFragmentInstance();
        if(fragment instanceof WallNewFragment) {
            banBack = ((WallNewFragment) fragment).backCheck();
        }
        if(!banBack) {
            super.titleLeftEvent();
        }
    }

    @Override
    protected void titleRightEvent() {
        finish();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return WallNewFragment.newInstance();
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
    protected void onDestroy() {
        UIUtil.hideKeyboard(this, getCurrentFocus());
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown& keyCode: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    private boolean banBack;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "dispatchKeyEvent& keyCode: " + event.getKeyCode() + "; Action: " + event.getAction());
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                Fragment fragment = getFragmentInstance();
                if(fragment instanceof WallNewFragment) {
                    banBack = ((WallNewFragment) fragment).backCheck();
                }
                Log.i(TAG, "dispatchKeyEvent& banBack: " + banBack);
                return banBack ? banBack : super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
