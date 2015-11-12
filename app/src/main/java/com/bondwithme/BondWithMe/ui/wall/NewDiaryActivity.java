package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;

/**
 * Created 10/20/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class NewDiaryActivity extends BaseActivity {
    private static final String TAG = NewDiaryActivity.class.getSimpleName();
    private String content_group_id;

    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void setTitle() {
        int resId;
        if (TextUtils.isEmpty(content_group_id)) {
            resId = R.string.title_diary_new;
        } else {
            resId = R.string.title_edit_post;
        }
        tvTitle.setText(resId);
    }

    /**
     * TitilBar 左边事件
     */
    @Override
    protected void titleLeftEvent() {
        Fragment fragment = getFragmentInstance();
        if (fragment instanceof EditDiaryFragment) {
            banBack = ((EditDiaryFragment) fragment).backCheck();
        }
        if (!banBack) {
            super.titleLeftEvent();
        }
    }

    @Override
    protected void titleRightEvent() {
        Fragment fragment = getFragmentInstance();
        if (fragment instanceof EditDiaryFragment) {
            ((EditDiaryFragment) fragment).submitWall();
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setImageResource(R.drawable.icon_post);
    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();
        content_group_id = intent.getStringExtra(Constant.CONTENT_GROUP_ID);
        String group_id = intent.getStringExtra(Constant.GROUP_ID);
        return EditDiaryFragment.newInstance(content_group_id, group_id);
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

    private boolean banBack;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.i(TAG, "dispatchKeyEvent& keyCode: " + event.getKeyCode() + "; Action: " + event.getAction());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Fragment fragment = getFragmentInstance();
                if (fragment instanceof EditDiaryFragment) {
                    banBack = ((EditDiaryFragment) fragment).backCheck();
                }
                LogUtil.i(TAG, "dispatchKeyEvent& banBack: " + banBack);
                return banBack ? banBack : super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
