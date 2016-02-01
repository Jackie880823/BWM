package com.madxstudio.co8.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.util.WallUtil;


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
        String action = getIntent().getAction();
        if(Constant.ACTION_SHOW_NOTIFY_GROUP.equals(action)) {
            tvTitle.setText(R.string.text_group);
        } else if(Constant.ACTION_SHOW_NOTIFY_USER.equals(action)) {
            tvTitle.setText(R.string.text_member);
        } else if(Constant.ACTION_SHOW_LOVED_USER.equals(action)){
            tvTitle.setText(R.string.title_member_love);
        }
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();
        Fragment fragment;
        if(!Constant.ACTION_SHOW_LOVED_USER.equals(intent.getAction())) {
            String content_group_id = intent.getStringExtra(Constant.CONTENT_GROUP_ID);
            String user_id = intent.getStringExtra(Constant.USER_ID);
            String group_id = intent.getStringExtra(Constant.GROUP_ID);
            fragment = WallMembersOrGroupsFragment.newInstance(content_group_id, user_id, group_id);
        } else {
            String viewer = intent.getStringExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID);
            String refer = intent.getStringExtra(WallUtil.GET_LOVE_LIST_REFER_ID);
            String type = intent.getStringExtra(WallUtil.GET_LOVE_LIST_TYPE);
            fragment = WallMembersOrGroupsFragment.newInstance(viewer, refer, type);
        }
        return fragment;
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
