package com.madx.bwm.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchiveGroupChatActivity extends BaseActivity implements View.OnClickListener{
    private CardView c1;

    @Override
    public int getLayout() {
        return R.layout.activity_more_archive_groupchat;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_group_setting);
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
//        getViewById(R.id.chat_top).setOnClickListener(this);
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
        Intent intent = new Intent(ArchiveGroupChatActivity.this,ArchiveGroupCommentActivity.class);
        startActivity(intent);
    }
    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
