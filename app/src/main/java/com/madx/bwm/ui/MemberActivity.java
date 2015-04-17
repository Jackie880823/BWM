package com.madx.bwm.ui;

import android.content.Intent;
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
import com.madx.bwm.adapter.MemberAdapter;
import com.madx.bwm.entity.MemberEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzemian on 15/4/12.
 */
public class MemberActivity extends BaseActivity {
    ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;
    private MyDialog showSelectDialog;


    private int startIndex = 0;
    private int offSet = 20;
    private boolean loading;
    private List<MemberEntity> data = new ArrayList<>();
    private MemberAdapter adapter;
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

        tvTitle.setText(R.string.text_member);

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
        params.put("limit", "" + offSet);


        new HttpTools(this).get(String.format(Constant.API_BONDALERT_MEMEBER, MainActivity.getUser().getUser_id()), params, new HttpCallback() {
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
                data = gson.fromJson(string, new TypeToken<ArrayList<MemberEntity>>() {
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
                MessageUtil.showMessage(MemberActivity.this, R.string.msg_action_failed);
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
        adapter = new MemberAdapter(this, data);
        adapter.setItemClickListener(new MemberAdapter.ItemClickListener() {
            @Override
            public void aWaittingClick(MemberEntity member, int position) {
                showSelectDialog(member);
            }

            @Override
            public void addClick(MemberEntity member, int position) {
                doAdd(member);
            }
        });
        rvList.setAdapter(adapter);
    }
//
//    private void add(final MemberEntity member){
//        Intent  intent = new Intent(MemberActivity.this, AddMemberWorkFlow.class);
//        intent.putExtra("to",);
//        intent.putExtra("from",MainActivity.getUser().getUser_id());
//        startActivityForResult(intent,);
//
//
//    }

    private final static int ADD_MEMBER = 10;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_MEMBER:
                if (resultCode == RESULT_OK) {
                    MessageUtil.showMessage(this,R.string.msg_action_successed);
                    requestData();
                } else {
                    MessageUtil.showMessage(this,R.string.msg_action_canceled);
                }
                break;
        }
    }

    private void doAdd(final MemberEntity member) {

        Intent intent = new Intent(this, AddMemberWorkFlow.class);
        intent.putExtra("from", MainActivity.getUser().getUser_id());
        intent.putExtra("to", member.getAction_user_id());
        startActivityForResult(intent, ADD_MEMBER);

//        Map<String, String> map = new HashMap<String, String>();
////        "user_id":"1",//user_id,
////                "user_relationship_name":"Sister",// relationship name
////                "member_id":"2"//member_id
//
//
//        Log.i("","user_id========="+ MainActivity.getUser().getUser_id());
//        map.put("user_id", MainActivity.getUser().getUser_id());
//        map.put("user_relationship_name", member.getRelationship());
//        map.put("member_id", member.getAction_user_id());
//
//        new HttpTools(this).post(Constant.API_BONDALERT_MEMEBER_RESEND,map,new HttpCallback() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//
//            @Override
//            public void onResult(String string) {
//                Log.d("", "response -> " + string);
//            }
//
//            @Override
//            public void onError(Exception error) {
//                Log.e("", error.getMessage(), error);
//            }
//
//            @Override
//            public void onCancelled() {
//
//            }
//
//            @Override
//            public void onLoading(long count, long current) {
//
//            }
//        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void showSelectDialog(final MemberEntity member) {

        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.member_add, null);
        showSelectDialog = new MyDialog(this, "Action", selectIntention);
        showSelectDialog.setCanceledOnTouchOutside(false);
        showSelectDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        selectIntention.findViewById(R.id.subject_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                wawitingResend(member);
                doAdd(member);
                showSelectDialog.dismiss();
            }
        });
        selectIntention.findViewById(R.id.subject_2).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("", "position=====" + member.getAction_user_id());
                        wawitingRemove(member.getAction_user_id());
                        showSelectDialog.dismiss();
                    }
                }
        );
        showSelectDialog.show();
    }


    private void wawitingResend(final MemberEntity member) {
        Log.i("", "user_id=========" + MainActivity.getUser().getUser_id());

        Map<String, String> map = new HashMap<String, String>();
//        "user_id":"1",//user_id,
//                "user_relationship_name":"Sister",// relationship name
//                "member_id":"2"//member_id

        map.put("user_id", MainActivity.getUser().getUser_id());
        map.put("user_relationship_name", member.getRelationship());
        map.put("member_id", member.getAction_user_id());

        new HttpTools(this).post(Constant.API_BONDALERT_MEMEBER_RESEND, map, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                Log.d("", "response -> " + string);
            }

            @Override
            public void onError(Exception error) {
                Log.e("", error.getMessage(), error);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


    }

    private void wawitingRemove(final String memberId) {

//        Log.i("", "2position==============" + memberId);
        RequestInfo requestInfo = new RequestInfo();
//        Log.i("", "3position==============" + MainActivity.getUser().getUser_id());
        requestInfo.url = Constant.API_BONDALERT_MEMEBER_REMOVE + MainActivity.getUser().getUser_id();
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(this).put(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                Log.i("response", "string------------------------------" + string);
                requestData();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(MemberActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


    }


}
