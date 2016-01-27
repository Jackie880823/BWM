package com.bondwithme.BondCorp.ui.wall;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.ui.BaseActivity;

/**
 * Created 10/29/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class FeelingActivity extends BaseActivity {


    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.GONE);
    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {

    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        int checkItemIndex = getIntent().getIntExtra(Constant.EXTRA_CHECK_ITEM_INDEX, -1);
        return FeelingFragment.createInstance(String.valueOf(checkItemIndex));
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
