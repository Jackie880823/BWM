package com.madxstudio.co8.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.NewsAdapter;
import com.madxstudio.co8.entity.NewsEntity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 16/1/22.
 */
public class NewsFragment extends BaseFragment<MainActivity> {
    View mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 8;
    private boolean loading;
    private List<NewsEntity> data = new ArrayList<>();
    private NewsAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private TextView tvNoData;
    private LinearLayout llNoData;

    public static NewsFragment newInstance(String... params) {
        return createInstance(new NewsFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_news_fragment;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);
        tvNoData = getViewById(R.id.tv_no_data_display);
        llNoData = getViewById(R.id.ll_no_data_display);

        rvList = getViewById(R.id.rvList);
        llm = new LinearLayoutManager(getActivity());
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
                int count = Math.abs(totalItemCount - 3);
                if (!loading && count != 0 && lastVisibleItem >= count && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        mProgressDialog.setVisibility(View.INVISIBLE);
    }

    @Override
    public void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("start", "" + startIndex);
        params.put("limit", "" + offSet);


        new HttpTools(getActivity()).get(String.format(Constant.API_NEWS, MainActivity.getUser().getUser_id()), params, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<NewsEntity>>() {
                }.getType());
                if (data != null) {
                    if (isRefresh) {
                        startIndex = data.size();
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


                LogUtil.d("NewsActivity", "item count" + adapter.getItemCount());
                //no data!!!
                if (!data.isEmpty()) {
                    llNoData.setVisibility(View.GONE);
                } else if (adapter.getItemCount() <= 0 && data.isEmpty() && !getActivity().isFinishing()) {
                    llNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(getResources().getString(R.string.text_no_date_news));
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
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
        adapter = new NewsAdapter(getActivity(), data);
        rvList.setAdapter(adapter);
    }
}
