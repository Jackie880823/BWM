package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.entity.EventEntity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class InvitedStatusActivity extends BaseActivity {


    EventEntity eventEntity;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void changeTitleColor(int color) {
        super.changeTitleColor(color);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_invited_status);
    }

    @Override
    protected void titleRightEvent() {
    }

    @Override
    protected Fragment getFragment() {
        if(getIntent().getSerializableExtra("event")!=null) {
            eventEntity = (EventEntity) getIntent().getSerializableExtra("event");
        }else{
            finish();
        }
        return  InvitedStatusFragment.newInstance();
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
