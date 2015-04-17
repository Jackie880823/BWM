package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.madx.bwm.R;

public class InvitedEditActivity extends BaseActivity {



    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
//        rightButton.setVisibility(View.GONE);
//        rightTextButton.setVisibility(View.VISIBLE);
//        rightTextButton.setText(R.string.text_more);
    }

    @Override
    protected void titleLeftEvent() {
        if(commandlistener!=null) {
            commandlistener.execute(leftButton);
        }
        super.titleLeftEvent();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            if(event.getAction()==KeyEvent.ACTION_DOWN) {
                if (commandlistener != null) {
                    commandlistener.execute(leftButton);
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void changeTitleColor(int color) {
        super.changeTitleColor(color);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_invited_status);
    }

    @Override
    protected void titleRightEvent() {

        if(commandlistener!=null) {
            commandlistener.execute(rightButton);
        }
    }

    @Override
    protected Fragment getFragment() {
        return  InvitedEditFragment.newInstance(getIntent().getStringExtra("members_data"), getIntent().getStringExtra("group_id"));
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
