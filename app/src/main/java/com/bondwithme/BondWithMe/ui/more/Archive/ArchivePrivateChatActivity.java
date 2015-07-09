package com.bondwithme.BondWithMe.ui.more.Archive;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;


/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchivePrivateChatActivity extends BaseActivity implements View.OnClickListener{
    private CardView c1;

    @Override
    public int getLayout() {
        return R.layout.activity_more_archive_privatechat;
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
        c1 = getViewById(R.id.top_event);
        c1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top_event:
                starttest();
                break;
            default:
                super.onClick(v);
                break;
        }
        super.onClick(v);
    }
    private void  starttest(){
        Intent intent = new Intent(ArchivePrivateChatActivity.this,ArchivePrivateCommentActivity.class);
        startActivity(intent);
    }
    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
