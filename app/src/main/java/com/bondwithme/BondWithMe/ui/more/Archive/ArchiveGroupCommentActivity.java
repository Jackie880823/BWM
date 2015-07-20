package com.bondwithme.BondWithMe.ui.more.Archive;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.ArchiveCommentFragment;
import com.bondwithme.BondWithMe.ui.BaseActivity;


/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchiveGroupCommentActivity extends BaseActivity {
    private String content_group_id;
    private String group_id;

//    @Override
//    public int getLayout() {
//        return R.layout.activity_more_archive_groupcomment;
////        return R.layout.activity_more_archive_groupchat;
//    }

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
        tvTitle.setText(R.string.text_archive_comment);

    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        content_group_id = getIntent().getStringExtra("content_group_id");
        group_id  = getIntent().getStringExtra("group_id");
        return new ArchiveCommentFragment().newInstance(content_group_id,group_id);
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
