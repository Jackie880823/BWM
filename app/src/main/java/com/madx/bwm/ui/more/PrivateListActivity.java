package com.madx.bwm.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

/**
 * Created by liangzemian on 15/6/30.
 */
public class PrivateListActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout rl1;
    private RelativeLayout rl2;

    @Override
    public int getLayout() {
        return R.layout.activity_archive_privatelist;
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
        tvTitle.setText(R.string.text_member_list);
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
                starttest();
                break;
            case R.id.rl_2:
                starttest();
                break;
            default:
                super.onClick(v);
                break;
        }

    }
    private void starttest(){
        Intent intent = new Intent(PrivateListActivity.this,ArchivePrivateChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
