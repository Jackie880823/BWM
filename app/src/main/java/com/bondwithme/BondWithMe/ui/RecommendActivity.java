package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.RecommendAdapter;
import com.bondwithme.BondWithMe.entity.RecommendEntity;
import com.bondwithme.BondWithMe.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class RecommendActivity extends BaseActivity {

    ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 20;
    private boolean loading;
    private List<RecommendEntity> data = new ArrayList<>();
    private RecommendAdapter adapter;
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
        tvTitle.setText(R.string.title_recommended_alert);
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
            mProgressDialog = new ProgressDialog(this, getString(R.string.text_loading));
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

//        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
//                int totalItemCount = llm.getItemCount();
//                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
//                // dy>0 表示向下滑动
//                if (!loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
//                    loading = true;
//                    requestData();//再请求数据
//                }
//            }
//        });
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

    void sdf()
    {
        findViewById(R.id.iv_move);

    }

    @Override
    public void requestData() {


//        Map<String, String> params = new HashMap<>();
//        params.put("start", "" + startIndex);
//        params.put("limit", "" + offSet);


        new HttpTools(this).get(String.format(Constant.API_BONDALERT_RECOMMEND, MainActivity.getUser().getUser_id()), null, this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
                if (isRefresh) {
                    finishReFresh();
                }
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<RecommendEntity>>() {
                }.getType());
//                if (data != null) {
//                    if (isRefresh) {
//                        startIndex = data.size();
//                        finishReFresh();
//                        initAdapter();
//                    } else {
//                        startIndex += data.size();
//                        adapter.add(data);
//                    }
//                } else {
//                    finishReFresh();
//                }
//                loading =

                finishReFresh();
                initAdapter();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(RecommendActivity.this, R.string.msg_action_failed);
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

    //begin
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecommendAdapter.CHOOSE_RELATION_CODE:
                if (resultCode == RESULT_OK) {
                    String relationShip = data.getStringExtra("relationship");
                    if (!TextUtils.isEmpty(relationShip)) {
                        addUser(relationShip);
                    }
                }
        }
    }

    private void addUser(final String relationShip) {


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("user_relationship_name", relationShip);
        params.put("member_id", data.get(adapter.getPositionId()).getUser_id());

        new HttpTools(this).post(Constant.API_ADD_MEMBER,params,this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        Toast.makeText(RecommendActivity.this, "Success to add member.", Toast.LENGTH_SHORT).show();
                        adapter.setAdapterNotifyDataSetChanged(adapter.getPositionId());
                        // finish();
                    } else {
                        Toast.makeText(RecommendActivity.this, "Fail to add member.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(RecommendActivity.this,R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    //end
    private void initAdapter() {
        adapter = new RecommendAdapter(this, data);
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
