package com.madx.bwm.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchiveGroupCommentActivity extends BaseActivity implements View.OnClickListener{

    @Override
    public int getLayout() {
        return R.layout.activity_more_archive_groupcomment;
    }

    @Override
    protected void initBottomBar() {

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
