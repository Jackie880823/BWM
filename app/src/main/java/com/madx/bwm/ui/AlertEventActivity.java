package com.madx.bwm.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.AlertEventAdapter;
import com.madx.bwm.entity.AlertEventEntity;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.SDKUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class AlertEventActivity extends BaseActivity {

    ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offset = 20;
    private int currentPage = 1;
    private boolean loading;
    private List<AlertEventEntity> data = new ArrayList<>();
    private AlertEventAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;


    public int getLayout() {
        return R.layout.activity_news;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_tab_event);
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

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.string.text_loading);
        }
        mProgressDialog.show();

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
        mProgressDialog.dismiss();
    }

    @Override
    public void requestData() {

        Map<String, String> params = new HashMap<>();
        params.put("start", "" + startIndex);
        params.put("limit",""+ offset);


        new HttpTools(this).get(String.format(Constant.API_BONDALERT_EVENT, MainActivity.getUser().getUser_id()), params, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<AlertEventEntity>>() {
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
                loading = false;
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(AlertEventActivity.this, R.string.msg_action_failed);
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

    private void initAdapter() {
        adapter = new AlertEventAdapter(this, data);
        rvList.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * add by wing
     * @param intent
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (SDKUtil.IS_HONEYCOMB){
            recreate();
        }else{
            finish();
            startActivity(intent);
        }
    }
}
