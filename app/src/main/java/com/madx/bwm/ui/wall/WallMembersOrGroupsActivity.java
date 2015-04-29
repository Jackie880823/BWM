package com.madx.bwm.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madx.bwm.ui.BaseActivity;


public class WallMembersOrGroupsActivity extends BaseActivity {

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {

    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {

    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();

        String content_group_id = intent.getStringExtra("content_group_id");
        String user_id = intent.getStringExtra("user_id");
        String group_id = intent.getStringExtra("group_id");
        return WallMembersOrGroupsFragment.newInstance(content_group_id, user_id, group_id);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initTitleBar(){
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
