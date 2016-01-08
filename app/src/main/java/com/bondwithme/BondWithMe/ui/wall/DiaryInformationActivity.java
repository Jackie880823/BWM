package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.interfaces.DiaryInformationFragmentListener;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class DiaryInformationActivity extends BaseActivity implements DiaryInformationFragmentListener{

    private DiaryInformationFragment diaryInformationFragment;

    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setImageResource(R.drawable.option_dots);
    }


    @Override
    protected void titleRightEvent() {
        diaryInformationFragment.getHolder().initItemMenu(rightButton);
    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();

        String content_group_id = intent.getStringExtra(Constant.CONTENT_GROUP_ID);
        String user_id = intent.getStringExtra(Constant.USER_ID);
        String group_id = intent.getStringExtra(Constant.GROUP_ID);
        diaryInformationFragment = DiaryInformationFragment.newInstance(content_group_id, user_id, group_id);
        diaryInformationFragment.setListener(this);
        return  diaryInformationFragment;
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

    /**
     * 日志加载完成
     *
     * @param wallEntity
     */
    @Override
    public void onLoadedEntity(@Nullable WallEntity wallEntity) {
        if (wallEntity == null ) {
            rightButton.setVisibility(View.GONE);
        } else if (!wallEntity.getUser_id().equals(MainActivity.getUser().getUser_id())){
            if (!TextUtils.isEmpty(wallEntity.getVideo_filename()) || Integer.valueOf(wallEntity.getPhoto_count()) > 0) {
                rightButton.setVisibility(View.VISIBLE);
            } else {
                rightButton.setVisibility(View.GONE);
            }
        }
    }
}
