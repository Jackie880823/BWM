package com.madxstudio.co8.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.NewsAdapter;
import com.madxstudio.co8.entity.NewsEntity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class NewsActivity extends BaseActivity {

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



    public int getLayout() {
        return R.layout.activity_news;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_news_alert);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent(NewsActivity.this, WriteNewsActivity.class);
        startActivity(intent);
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
        llNoData = getViewById(R.id.ll_no_data_display);

        rvList = getViewById(R.id.rvList);
        llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
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
                if (!loading && count!=0 && lastVisibleItem >= count && dy > 0) {
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
        mProgressDialog.setVisibility(View.INVISIBLE);
    }

    @Override
    public void requestData() {

        Map<String, String> params = new HashMap<>();
        params.put("start", "" + startIndex);
        params.put("limit",""+offSet);


        new HttpTools(this).get(String.format(Constant.API_NEWS, MainActivity.getUser().getUser_id()), params, this, new HttpCallback() {
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
                } else if (adapter.getItemCount()<=0 && data.isEmpty() && !NewsActivity.this.isFinishing()) {
                    llNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(getResources().getString(R.string.text_no_date_news));
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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
        adapter = new NewsAdapter(this, data);
        rvList.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * add by wing
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        finish();
        startActivity(intent);
    }
}
