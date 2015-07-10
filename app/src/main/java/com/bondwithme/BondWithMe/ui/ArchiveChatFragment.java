package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.ArchiveChatAdapter;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;
import com.bondwithme.BondWithMe.entity.ArchiveChatEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzemian on 15/7/1.
 */
public class ArchiveChatFragment extends BaseFragment<Activity> implements View.OnClickListener {
    private static final String TAG = ArchiveChatFragment.class.getSimpleName();
    //如果是0，则是group，否则是private
//    private String tag;
    private RecyclerView rvList;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh = true;
    private int startIndex = 0;
    private int offset = 10;
    private int currentPage = 1;
    private boolean loading;
    LinearLayoutManager llm;
    private String archive_id;
    private View vProgress;

    private ArchiveChatAdapter adapter;
    private List<ArchiveChatEntity> data = new ArrayList<>();

    public static ArchiveChatFragment newInstance(String... params) {
            return createInstance(new ArchiveChatFragment(),params[0]);
    }

    public ArchiveChatFragment(){
        super();
    }
//    public ArchiveChatFragment(String params){
//        tag = params;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void setLayoutId() {
        this.layoutId =  R.layout.fragment_archive_chat;
    }

    @Override
    public void initView() {
        //如果该fragment带参数
        if(getArguments() != null){
            archive_id =  getArguments().getString(ARG_PARAM_PREFIX + 0);
        }
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        rvList = getViewById(R.id.rv_wall_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();
        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });
    }

    private void initAdapter(){

    }
    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View v) {

    }
}
