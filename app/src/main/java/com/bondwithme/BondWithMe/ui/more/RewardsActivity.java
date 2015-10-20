package com.bondwithme.BondWithMe.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.RewardsAdapter;
import com.bondwithme.BondWithMe.entity.RewardsEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heweidong on 15/10/16.
 */
public class RewardsActivity extends BaseActivity {

    View mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 20;
    private boolean loading;
    private List<RewardsEntity> data = new ArrayList<>();
    private RewardsAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private TextView tvNoData;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_rewards);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setVisibility(View.INVISIBLE);
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
        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);
        tvNoData = getViewById(R.id.tv_no_data_display);

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
