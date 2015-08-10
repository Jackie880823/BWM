package com.bondwithme.BondWithMe.ui.more.Archive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.GroupListAdapter;
import com.bondwithme.BondWithMe.entity.GroupListEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzemian on 15/6/30.
 */
public class GroupListActivity extends BaseActivity{
    private static final String Tag = GroupListActivity.class.getSimpleName();
    ProgressDialog mProgressDialog;
    private boolean isRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int startIndex = 0;
    private int offset = 20;
    private int currentPage = 1;
    private boolean loading;

    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private GroupListAdapter adapter;
    private List<GroupListEntity> data = new ArrayList<>();
    private View vProgress;

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
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this, getString(R.string.text_loading));
//        }
//        mProgressDialog.show();
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        rvList = getViewById(R.id.rvList);
        llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });

        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if ((data.size()==(currentPage*offset))&&!loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
//        mProgressDialog.dismiss();
        vProgress.setVisibility(View.GONE);
    }

    @Override
    public void requestData() {
        Map<String,String> params = new HashMap<>();
        params.put("start","" + startIndex);
        params.put("limit",""+ offset);
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("type","group");

        String url = UrlUtil.generateUrl(Constant.API_MORE_ARCHIVE_LIST, params);

        new HttpTools(this).get(url, null, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
//                mProgressDialog.dismiss();
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<GroupListEntity>>() {
                }.getType());
                if (data != null) {
                    if (isRefresh) {
                        startIndex = data.size();
                        currentPage = 1;//还原为第一页
                        finishReFresh();
                        initAdapter();
                    } else {
                        startIndex += data.size();
                        adapter.add(data);
                    }
                } else {
                    finishReFresh();
                }
                if(adapter.getItemCount() < 1){
                    getViewById(R.id.not_archive).setVisibility(View.VISIBLE);
                }
                loading = false;
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(GroupListActivity.this, R.string.msg_action_failed);
                if (isRefresh) {
                    finishReFresh();
                }
                loading = false;
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void  initAdapter(){
        adapter = new  GroupListAdapter(this,data);
        adapter.setItemClickListener(new GroupListAdapter.ItemClickListener() {
            @Override
            public void topItemClick(String group_id,String group_name) {
                Intent intent = new Intent(GroupListActivity.this,ArchiveGroupChatActivity.class);
                intent.putExtra("group_id",group_id);
                intent.putExtra("group_name",group_name);
                startActivity(intent);
            }
        });
        rvList.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private void starttest(){
        Intent intent = new Intent(this,ArchiveGroupChatActivity.class);
        startActivity(intent);
    }
}
