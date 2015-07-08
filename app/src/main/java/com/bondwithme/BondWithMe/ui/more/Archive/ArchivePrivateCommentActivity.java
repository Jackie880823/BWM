package com.bondwithme.BondWithMe.ui.more.Archive;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;


/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchivePrivateCommentActivity extends BaseActivity implements View.OnClickListener{

    @Override
    public int getLayout() {
        return R.layout.activity_more_archive_groupcomment;
    }

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
        return null;
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
