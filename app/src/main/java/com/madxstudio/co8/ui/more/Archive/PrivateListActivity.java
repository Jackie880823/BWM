package com.madxstudio.co8.ui.more.Archive;

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
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.PrivateListAdapter;
import com.madxstudio.co8.entity.PrivateListEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.MessageUtil;
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
public class PrivateListActivity extends BaseActivity {
    private static final String Tag = PrivateListActivity.class.getSimpleName();
    ProgressDialog mProgressDialog;
    private boolean isRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int startIndex = 0;
    private int offset = 20;
    private int currentPage = 1;
    private boolean loading;

    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private PrivateListAdapter adapter;
    private List<PrivateListEntity> data = new ArrayList<>();
    private View vProgress;

//    private PrivateListEntity data;
//    private RelativeLayout rl1;
//    private RelativeLayout rl2;

    @Override
    public int getLayout() {
        return R.layout.activity_archive_privatelist;
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
        tvTitle.setText(R.string.text_member_list);
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
                int count = Math.abs(totalItemCount - 5);
                if (data.size() >= offset && !loading && lastVisibleItem >= count && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });
//        rl1 = getViewById(R.id.rl_1);
//        rl2 = getViewById(R.id.rl_2);
//
//        rl1.setOnClickListener(this);
//        rl2.setOnClickListener(this);

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

//    private void starttest(){
//        Intent intent = new Intent(PrivateListActivity.this,ArchivePrivateChatActivity.class);
//        startActivity(intent);
//    }

    @Override
    public void requestData() {
        Map<String,String> params = new HashMap<>();
        params.put("start","" + startIndex);
        params.put("limit",""+ offset);
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("type","member");

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
                data = gson.fromJson(string, new TypeToken<ArrayList<PrivateListEntity>>() {
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


    private void  initAdapter(){
        adapter = new PrivateListAdapter(this,data);
        adapter.setItemClickListener(new PrivateListAdapter.ItemClickListener() {
            @Override
            public void topItemClick(String group_id,String user_name) {
                Intent intent = new Intent(PrivateListActivity.this,ArchivePrivateChatActivity.class);
                intent.putExtra("group_id",group_id);
                intent.putExtra("user_name",user_name);
                startActivity(intent);
            }
        });
        rvList.setAdapter(adapter);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
