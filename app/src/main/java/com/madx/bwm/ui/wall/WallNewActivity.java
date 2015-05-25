package com.madx.bwm.ui.wall;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class WallNewActivity extends BaseActivity {


    @Override
    protected void initBottomBar() {
        // 隐藏键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void setTitle() {

        tvTitle.setText(R.string.title_wall_new);
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
}
