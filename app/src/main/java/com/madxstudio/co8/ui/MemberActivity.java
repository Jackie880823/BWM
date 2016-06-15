package com.madxstudio.co8.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.MemberAdapter;
import com.madxstudio.co8.entity.MemberEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.widget.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemberActivity extends BaseActivity {
    View mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;
    private MyDialog showSelectDialog;
    private MyDialog showAddDialog;

    private int startIndex = 0;
    private final static int offset = 20;
    private int currentPage = 1;
    private boolean loading;
    private List<MemberEntity> data = new ArrayList<>();
    private MemberAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private String TAG;
    private TextView tvNoDate;
    private LinearLayout llNoData;

    public int getLayout() {
        return R.layout.activity_news;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_member_alert);
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
//        mProgressDialog.setVisibility(View.VISIBLE);
        tvNoDate = getViewById(R.id.tv_no_data_display);
        llNoData = getViewById(R.id.ll_no_data_display);

        rvList = getViewById(R.id.rvList);
        llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
        rvList.setHasFixedSize(true);
        TAG = this.getClass().getSimpleName();
        initAdapter();//为空 为什么要做这步？？？

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                isRefresh = true;
//                startIndex = 0;
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
//        int count = Math.abs(totalItemCount - 5);
//        if (data.size() >= offset && !loading && lastVisibleItem >= count && dy > 0) {
//                    currentPage++;
//                    loading = true;
//                    requestData();//再请求数据
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showAddDialog != null) {
            showAddDialog.dismiss();
        }
        if (showSelectDialog != null) {
            showSelectDialog.dismiss();
        }
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        mProgressDialog.setVisibility(View.GONE);
    }

    @Override
    public void requestData() {

//        Map<String, String> params = new HashMap<>();
//        params.put("start", "" + startIndex);
//        params.put("limit", "" + offset);

        new HttpTools(this).get(String.format(Constant.API_BONDALERT_MEMEBER, MainActivity.getUser().getUser_id()), null, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                mProgressDialog.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                finishReFresh();
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                data = gson.fromJson(string, new TypeToken<ArrayList<MemberEntity>>() {
                }.getType());

                //no data!
                if (!data.isEmpty()) {
                    llNoData.setVisibility(View.GONE);
                } else if (adapter.getItemCount() <= 0 && data.isEmpty() && !MemberActivity.this.isFinishing()) {
                    llNoData.setVisibility(View.VISIBLE);
                    tvNoDate.setText(getResources().getString(R.string.text_no_date_members));
                }

                initAdapter();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
//                if (isRefresh) {
//                    finishReFresh();
//                }
//                loading = false;
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
                showAddDialog(member);
            }
        });
        rvList.setAdapter(adapter);
    }

    private final static int ADD_MEMBER = 10;
    private final static int TO_PROFILE = 11;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_MEMBER:
                if (resultCode == RESULT_OK) {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
//                    startIndex = 0;
//                    isRefresh = true;
                    requestData();//这样直接请求???
                } else {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_canceled);
                }
                mProgressDialog.setVisibility(View.GONE);
                break;
            case TO_PROFILE:
                if (resultCode == RESULT_OK) {
                    requestData();
                }
                break;
        }
    }

    private void doAdd(final MemberEntity member) {
        mProgressDialog.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AddMemberWorkFlow.class);
        intent.putExtra(AddMemberWorkFlow.FLAG_FROM, MainActivity.getUser().getUser_id());
        intent.putExtra(AddMemberWorkFlow.FLAG_TO, member.getAction_user_id());
        startActivityForResult(intent, ADD_MEMBER);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void showAddDialog(final MemberEntity member) {
        LayoutInflater factory = LayoutInflater.from(this);
        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
        showAddDialog = new MyDialog(this, null, selectIntention);
        TextView ll_contact_profile = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
        TextView tvApprove = (TextView) selectIntention.findViewById(R.id.tv_to_message);
        TextView tvReject = (TextView) selectIntention.findViewById(R.id.tv_leave_or_delete);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        ll_contact_profile.setText(R.string.text_view_profile);
        tvApprove.setText(R.string.text_dialog_accept);
        tvReject.setText(R.string.text_item_reject);
        tvApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAdd(member);
                showAddDialog.dismiss();
            }
        });
        tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemove(member.getAction_user_id());
                showAddDialog.dismiss();
            }
        });

        ll_contact_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog.dismiss();
                Intent intent = new Intent(MemberActivity.this, FamilyViewProfileActivity.class);
                intent.putExtra(UserEntity.EXTRA_MEMBER_ID, member.getAction_user_id());
                intent.putExtra(Constant.LOOK_USER_PROFILE, true);
                startActivityForResult(intent, TO_PROFILE);
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog.dismiss();
            }
        });
        showAddDialog.show();

//        LayoutInflater factory = LayoutInflater.from(this);
//        final View selectIntention = factory.inflate(R.layout.dialog_bond_alert_member, null);
//        showAddDialog = new MyDialog(this, getResources().getString(R.string.text_tips_title), selectIntention);
//        showAddDialog.setCanceledOnTouchOutside(false);
//
//        showAddDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddDialog.dismiss();
//            }
//        });
//
//        TextView item1 = (TextView) selectIntention.findViewById(R.id.subject_1);
//        item1.setText(R.string.text_dialog_accept);
//        item1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doAdd(member);
//                showAddDialog.dismiss();
//            }
//        });
//
//        TextView item2 = (TextView) selectIntention.findViewById(R.id.subject_2);
//        item2.setText(R.string.text_item_reject);
//        item2.setOnClickListener(new View.OnClickListener() {
//            //        selectIntention.findViewById(R.id.subject_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addRemove(member.getAction_user_id());
//                showAddDialog.dismiss();
//            }
//        });
//
//        showAddDialog.show();
    }

    private void showSelectDialog(final MemberEntity member) {
        LayoutInflater factory = LayoutInflater.from(this);
        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
        showSelectDialog = new MyDialog(this, null, selectIntention);
        TextView tvApprove = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
        TextView tvReject = (TextView) selectIntention.findViewById(R.id.tv_to_message);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);

        selectIntention.findViewById(R.id.tv_leave_or_delete).setVisibility(View.GONE);
        selectIntention.findViewById(R.id.leave_line).setVisibility(View.GONE);

        tvApprove.setText(R.string.text_item_resend);
        tvReject.setText(R.string.text_item_remove);
        tvApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAdd(member);
                showSelectDialog.dismiss();
            }
        });
        tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awaitingRemove(member.getAction_user_id());
                showSelectDialog.dismiss();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View selectIntention = factory.inflate(R.layout.dialog_bond_alert_member, null);
//        showSelectDialog = new MyDialog(this, getResources().getString(R.string.text_tips_title), selectIntention);
//        showSelectDialog.setCanceledOnTouchOutside(false);
//
//        showSelectDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSelectDialog.dismiss();
//            }
//        });
//
//        selectIntention.findViewById(R.id.subject_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doAdd(member);
//                showSelectDialog.dismiss();
//            }
//        });
//
//        selectIntention.findViewById(R.id.subject_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                awaitingRemove(member.getAction_user_id());
//                showSelectDialog.dismiss();
//            }
//        });
//
//        showSelectDialog.show();
    }

    private void addRemove(final String memberId) {

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_REJECT_PENDING_MEMBER, MainActivity.getUser().getUser_id());
        Map<String, String> params = new HashMap<>();
        params.put("requestor_id", memberId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                //bad date???
//                startIndex = 0;
//                isRefresh = true;
                requestData();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void awaitingRemove(final String memberId) {

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_BONDALERT_MEMEBER_REMOVE + MainActivity.getUser().getUser_id();
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                //bad date???
//                startIndex = 0;
//                isRefresh = true;
                requestData();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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
