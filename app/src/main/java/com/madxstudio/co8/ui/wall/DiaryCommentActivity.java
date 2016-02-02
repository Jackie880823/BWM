package com.madxstudio.co8.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseActivity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class DiaryCommentActivity extends BaseActivity {


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
        tvTitle.setText(R.string.title_diary_comment);
    }

    @Override
    protected void titleRightEvent() {
    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();

        String content_group_id;
        String content_id;
        String group_id;
        String agree_count;

        content_group_id = intent.getStringExtra(Constant.CONTENT_GROUP_ID);
        content_id = intent.getStringExtra(Constant.CONTENT_ID);
        agree_count = intent.getStringExtra(Constant.AGREE_COUNT);
        group_id = intent.getStringExtra(Constant.GROUP_ID);

        return DiaryCommentFragment.newInstance(content_group_id, content_id, group_id, agree_count);
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
