package com.bondwithme.BondWithMe.ui.more.Archive;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.ui.ArchiveChatFragment;
import com.bondwithme.BondWithMe.ui.BaseActivity;


/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchivePrivateChatActivity extends BaseActivity implements View.OnClickListener{
//    private CardView c1;
    private String group_id;
    private String user_name;

//    @Override
//    public int getLayout() {
//        return R.layout.activity_more_archive_privatechat;
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
        user_name = getIntent().getStringExtra("user_name");
        tvTitle.setText(user_name);

    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        group_id = getIntent().getStringExtra("group_id");
        //0是群组备份，1是聊天备份
        return ArchiveChatFragment.newInstance("1",group_id);
    }

    @Override
    public void initView() {
//        c1 = getViewById(R.id.top_event);
//        c1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.top_event:
//                starttest();
//                break;
//            default:
//                super.onClick(v);
//                break;
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
