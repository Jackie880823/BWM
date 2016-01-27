package com.bondwithme.BondCorp.ui.more.Archive;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.ui.ArchiveChatFragment;
import com.bondwithme.BondCorp.ui.BaseActivity;


/**
 * Created by liangzemian on 15/6/30.
 */
public class ArchiveGroupChatActivity extends BaseActivity {
//    private CardView c1;
    private String group_id;
    private String group_name;
    private String Tag;

//    @Override
//    public int getLayout() {
//        return R.layout.activity_more_archive_groupchat;
//    }

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
        group_name = getIntent().getStringExtra("group_name");
        tvTitle.setText(group_name);

    }

    @Override
    protected void titleRightEvent() {
        if(commandlistener!=null)
            commandlistener.execute(rightButton);
//        Intent intent = new Intent(ArchiveGroupChatActivity.this, GroupSettingActivity.class);
//        intent.putExtra("groupId", group_id);
//        intent.putExtra("groupName", group_name);
//        intent.putExtra("groupType",1);
//        startActivity(intent);
    }
    @Override
    protected void titleLeftEvent() {
        if(commandlistener!=null)
            commandlistener.execute(leftButton);
    }

    @Override
    protected Fragment getFragment() {
        group_id = getIntent().getStringExtra("group_id");
        //0是群组备份，1是聊天备份
        return ArchiveChatFragment.newInstance("0",group_id,group_name);
    }

    @Override
    public void initView() {
//        getViewById(R.id.chat_top).setOnClickListener(this);
//        c1 = getViewById(R.id.top_event);
//        c1.setOnClickListener(this);

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
////            case R.id.top_event:
////                starttest();
////                break;
////            default:
////                super.onClick(v);
////                break;
//        }
//        super.onClick(v);
//    }

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
