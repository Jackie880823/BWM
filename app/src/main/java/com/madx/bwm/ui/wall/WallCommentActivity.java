package com.madx.bwm.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class WallCommentActivity extends BaseActivity {


    private String content_group_id;
    private String user_id;
    private String group_id;

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
        tvTitle.setText(R.string.title_wall_comment);
    }

    @Override
    protected void titleRightEvent() {
    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();

        content_group_id = intent.getStringExtra("content_group_id");
        user_id = intent.getStringExtra("user_id");
        group_id = intent.getStringExtra("group_id");

        return  WallCommentFragment.newInstance(content_group_id, user_id, group_id);
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
