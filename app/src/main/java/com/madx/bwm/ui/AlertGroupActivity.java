package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.AlertGroupAdapter;
import com.madx.bwm.entity.AlertGroupEntity;

import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment(){})
 * Created by heweidong on 2015/5/13.
 */
public class AlertGroupActivity extends BaseActivity{
    ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private MyDialog showOptionDialog;

    private RecyclerView rvList;
    private LinearLayoutManager llm;

    private List<AlertGroupEntity> data = new ArrayList<>();

    public int getLayout() {
        return R.layout.activity_news;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_group_alert);
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
                requestData();
            }

        });
    }

    private void initAdapter() {
        AlertGroupAdapter adapter = new AlertGroupAdapter(this,data);

        adapter.setItemClickListener(new AlertGroupAdapter.ItemClickListener() {
            @Override
            public void optionClick(AlertGroupEntity alertGroupEntity, int position) {
                showOptionDialog(alertGroupEntity);
            }
        });

        rvList.setAdapter(adapter);
    }

    private void showOptionDialog(final AlertGroupEntity alertGroupEntity) {
        String dialogTitle = this.getResources().getString(R.string.text_dialog_title);//Dialog title
        LayoutInflater factory = LayoutInflater.from(this);
        final View optionIntention = factory.inflate(R.layout.dialog_bond_alert_group,null);
        showOptionDialog = new MyDialog(this, dialogTitle, optionIntention);
        showOptionDialog.setCanceledOnTouchOutside(false);
        showOptionDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog.dismiss();
            }
        });

        // accept join the group;
        optionIntention.findViewById(R.id.subject_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmJoinGroup(alertGroupEntity);
                showOptionDialog.dismiss();

            }
        });
        // reject the invite to the group;
        optionIntention.findViewById(R.id.subject_decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectJoinGroup(alertGroupEntity);
                showOptionDialog.dismiss();

            }
        });
        showOptionDialog.show();
    }



    private void confirmJoinGroup(AlertGroupEntity alertGroupEntity) {
        mProgressDialog.show();
        Map<String, String> params = new HashMap<>();
        RequestInfo requestInfo = new RequestInfo();
        params.put("receiver_user_id",alertGroupEntity.getReceiver_user_id());
        params.put("action_user_id",alertGroupEntity.getAction_user_id());
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_BONDALERT_GROUP_CONFIRM,alertGroupEntity.getModule_id());

        //上传“同意加入Group”的参数
        new HttpTools(this).put(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();

            }

            @Override
            public void onResult(String response) {
                MessageUtil.showMessage(AlertGroupActivity.this,R.string.msg_action_successed);
                requestData();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(AlertGroupActivity.this,R.string.msg_action_failed);
                e.printStackTrace();

            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void rejectJoinGroup(AlertGroupEntity alertGroupEntity) {
        mProgressDialog.show();
        Map<String, String> params = new HashMap<>();
        RequestInfo requestInfo = new RequestInfo();
        params.put("receiver_user_id",alertGroupEntity.getReceiver_user_id());
        params.put("action_user_id",alertGroupEntity.getAction_user_id());
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_BONDALERT_GROUP_REJECT,alertGroupEntity.getModule_id());
        Log.i("TAG_reject_GroupID","alertGroupEntity.getModule_id()========="+alertGroupEntity.getModule_id());
        Log.i("TAG_requestInfo.url","requestInfo.url========="+requestInfo.url);
        //上传“拒绝加入Group”的参数
        new HttpTools(this).put(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();

            }

            @Override
            public void onResult(String response) {

                MessageUtil.showMessage(AlertGroupActivity.this,R.string.msg_action_successed);
                requestData();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(AlertGroupActivity.this,R.string.msg_action_failed);

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    @Override
    public void requestData() {
        new HttpTools(this).get(String.format(Constant.API_BONDALERT_GROUP, MainActivity.getUser().getUser_id()),null,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishReFresh();
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                data = gson.fromJson(string, new TypeToken<ArrayList<AlertGroupEntity>>() {
                }.getType());

                initAdapter();

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(AlertGroupActivity.this, R.string.msg_action_failed);
                if (isRefresh) {
                    finishReFresh();
                }
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
        mProgressDialog.dismiss();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
