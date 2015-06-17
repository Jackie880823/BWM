package com.madx.bwm.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MissAdapter;
import com.madx.bwm.entity.MissEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class MissListActivity extends BaseActivity {

    ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 20;
    private boolean loading;
    private List<MissEntity> data = new ArrayList<>();
    private MissAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;


    public int getLayout() {
        return R.layout.activity_miss;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_miss_alert);
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
                if (!loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
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
        params.put("limit",""+offSet);

        new HttpTools(this).get(String.format(Constant.API_BONDALERT_LIST, MainActivity.getUser().getUser_id()), params, this,new HttpCallback() {
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
                data = gson.fromJson(string, new TypeToken<ArrayList<MissEntity>>() {
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
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(MissListActivity.this, R.string.msg_action_failed);
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
        adapter = new MissAdapter(this, data,this);
        /**
         * Begin
         */
        adapter.setItemClickListener(new MissAdapter.ItemClickListener() {
            @Override
            public void topItemClick(String member_id) {
                updateMiss(member_id);
                Intent intent = new Intent(MissListActivity.this, FamilyProfileActivity.class);
                intent.putExtra("member_id", member_id);
                startActivity(intent);
            }
        });
        /**
         * end
         */
        rvList.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void updateMiss(String member_id)
    {
        RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", member_id);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_UPDATE_MISS, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                //服务器不管成功与否都返回200  没法判断准确情况
                Log.d("", "update----" + string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("200".equals(jsonObject.getString("response_status_code")))
                    {
                        Toast.makeText(MissListActivity.this, getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    MessageUtil.showMessage(MissListActivity.this, R.string.msg_action_failed);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(MissListActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
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
