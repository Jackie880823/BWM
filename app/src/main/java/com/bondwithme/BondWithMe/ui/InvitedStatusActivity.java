package com.bondwithme.BondWithMe.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.EventEntity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class InvitedStatusActivity extends BaseActivity {


    EventEntity eventEntity;
    int tabIndex;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
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
            tabIndex = (int) getIntent().getSerializableExtra("tabIndex");
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