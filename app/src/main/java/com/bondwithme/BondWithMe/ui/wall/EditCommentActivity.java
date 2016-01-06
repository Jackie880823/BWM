package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;

/**
 * Created 15/12/31.
 *
 * @author Jackie
 * @version 1.0
 */
public class EditCommentActivity extends BaseActivity {

    public static final String WALL_COMMENT_ENTITY = "Wall_Comment_Entity";
    public static final String DATA_INDEX = "data_index";

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {

    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_edit);
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        Fragment tempFragment = getFragmentInstance();
        if (tempFragment instanceof EditCommentFragment) {
            ((EditCommentFragment) tempFragment).putComment();
        }

    }

    private WallCommentEntity wallCommentEntity;
    @Override
    protected Fragment getFragment() {
        EditCommentFragment tempFragment = EditCommentFragment.newInstance();
        tempFragment.setListener(new EditCommentFragment.EditCommentFragmentListener() {
            @Override
            public WallCommentEntity getEntity() {
                wallCommentEntity = (WallCommentEntity) getIntent().getSerializableExtra(WALL_COMMENT_ENTITY);
                return wallCommentEntity;
            }

            @Override
            public void putEntity(WallCommentEntity wallCommentEntity) {
                Intent intent = getIntent();
                intent.putExtra(WALL_COMMENT_ENTITY, wallCommentEntity);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        return tempFragment;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setImageResource(R.drawable.btn_done);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
