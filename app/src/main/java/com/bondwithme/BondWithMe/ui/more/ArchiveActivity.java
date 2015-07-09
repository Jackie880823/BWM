package com.bondwithme.BondWithMe.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.more.Archive.GroupListActivity;
import com.bondwithme.BondWithMe.ui.more.Archive.PrivateListActivity;


/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchiveActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout rl1;
    private RelativeLayout rl2;

    @Override
    public int getLayout() {
        return R.layout.activity_more_archive;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.GONE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_archive);

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
        rl1 = getViewById(R.id.rl_1);
        rl2 = getViewById(R.id.rl_2);

        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_1:
                startActivity(new Intent(ArchiveActivity.this,GroupListActivity.class));
                break;
            case R.id.rl_2:
                startActivity(new Intent(ArchiveActivity.this,PrivateListActivity.class));
                break;
            default:
                super.onClick(v);
                break;
        }

    }
    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
