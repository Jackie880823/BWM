package com.bondwithme.BondCorp.ui.more;

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
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.RewardsAdapter;
import com.bondwithme.BondCorp.entity.RewardsEntity;
import com.bondwithme.BondCorp.ui.BaseActivity;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.MessageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heweidong on 15/10/16.
 */
public class RewardsActivity extends BaseActivity {

    private static final int OFFSET_LEFT_2_LOAD_MORE = 2;
    View mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 8;
    private boolean loading;
    private List<RewardsEntity> data = new ArrayList<>();
    private RewardsAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private TextView tvNoData;
    private LinearLayout llNoData;

    public int getLayout() {
        return R.layout.activity_rewards;
    }

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
        llNoData = getViewById(R.id.ll_no_data_display);

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

                LogUtil.d("onScrolled", "lastVisibleItem=======" + lastVisibleItem);
                LogUtil.d("onScrolled", "totalItemCount=======" + totalItemCount);

                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                int count = totalItemCount - OFFSET_LEFT_2_LOAD_MORE;
                if (!loading && count != 0 && lastVisibleItem >= Math.abs(count)  && dy > 0) {
                    LogUtil.d("onScrolled","REQUEST======="+totalItemCount);
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });

    }

    private void initAdapter() {
        adapter = new RewardsAdapter(this,data);
        rvList.setAdapter(adapter);
    }


    @Override
    public void requestData() {

        Map<String,String> params = new HashMap<>();
        params.put("Start","" + startIndex);
        params.put("limit","" + offSet);

        new HttpTools(this).get(String.format(Constant.API_REWARDS, MainActivity.getUser().getUser_id()), params, this, new HttpCallback() {
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
                data = gson.fromJson(string, new TypeToken<ArrayList<RewardsEntity>>() {
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

                LogUtil.d("RewardActivity","item count"+adapter.getItemCount());

                if (!data.isEmpty()) {
                    llNoData.setVisibility(View.GONE);
                } else if (adapter.getItemCount()<=0 && data.isEmpty() && !RewardsActivity.this.isFinishing()) {
                    llNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(getResources().getString(R.string.text_no_rewards));
                }


            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(RewardsActivity.this, R.string.msg_action_failed);
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

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        mProgressDialog.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        finish();
        startActivity(intent);
    }
}
