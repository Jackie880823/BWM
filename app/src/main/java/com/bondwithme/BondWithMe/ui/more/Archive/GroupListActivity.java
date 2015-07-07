package com.bondwithme.BondWithMe.ui.more.Archive;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.GroupListEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.bondwithme.BondWithMe.adapter.GroupListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzemian on 15/6/30.
 */
public class GroupListActivity extends BaseActivity implements View.OnClickListener{
    ProgressDialog mProgressDialog;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private GroupListAdapter adapter;
    private List<GroupListEntity> data = new ArrayList<>();
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;

    @Override
    public int getLayout() {
        return R.layout.activity_archive_grouplist;
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
        tvTitle.setText(R.string.text_group);

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
        rl1 = getViewById(R.id.rl_1);
        rl2 = getViewById(R.id.rl_2);
        rl3 = getViewById(R.id.rl_3);

        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this, R.string.text_loading);
//        }
//        mProgressDialog.show();
//        rvList = getViewById(R.id.rvList);
//        llm = new LinearLayoutManager(this);
//        rvList.setLayoutManager(llm);
//        rvList.setHasFixedSize(true);
//        initAdapter();
//
//        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                isRefresh = true;
//                startIndex = 0;
//                requestData();
//            }
//
//        });
//
//        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
//                int totalItemCount = llm.getItemCount();
//                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
//                // dy>0 表示向下滑动
//                if ((data.size()==(currentPage*offset))&&!loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
//                    loading = true;
//                    requestData();//再请求数据
//                }
//            }
//        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_1:
                starttest();
                break;
            case R.id.rl_2:
                starttest();
                break;
            case R.id.rl_3:
                starttest();
                break;
            default:
                super.onClick(v);
                break;
        }

    }
    private void starttest(){
        Intent intent = new Intent(this,ArchiveGroupChatActivity.class);
        startActivity(intent);
    }
}
