package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madx.bwm.R;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class WallNewActivity extends BaseActivity {


    @Override
    protected void initBottomBar() {

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
//        rightTextButton.setVisibility(View.VISIBLE);
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
