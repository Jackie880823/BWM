package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class DiaryInformationActivity extends BaseActivity {

    private DiaryInformationFragment diaryInformationFragment;

    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setImageResource(R.drawable.option_dots);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText("");
    }

    @Override
    protected void titleRightEvent() {
        diaryInformationFragment.getHolder().initItemMenu(rightButton);
    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();

        String content_group_id = intent.getStringExtra(Constant.CONTENT_GROUP_ID);
        String user_id = intent.getStringExtra(Constant.USER_ID);
        String group_id = intent.getStringExtra(Constant.GROUP_ID);
        diaryInformationFragment = DiaryInformationFragment.newInstance(content_group_id, user_id, group_id);
        return  diaryInformationFragment;
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
