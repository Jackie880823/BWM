package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.OrgGroupListAdapter;
import com.madxstudio.co8.adapter.OrgMemberListAdapter;
import com.madxstudio.co8.adapter.PendingRequestAdapter;
import com.madxstudio.co8.entity.AdminPendingRequest;
import com.madxstudio.co8.entity.OrgGroupEntity;
import com.madxstudio.co8.entity.OrgMemberEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.ui.add.AddMembersActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.OrganisationConstants;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 16/3/8.
 */
public class OrgDetailActivity extends BaseActivity implements OrgMemberListAdapter.AdminAdapterListener{
    private static final String TAG = "OrgDetailActivity";
    private static final String PENDING_REQUEST_LIST = "Pending request list";
    public static final String ACCEPT_REQUEST_TAG = "accept request";
    private String transmitData;

    /**
     * add by Jackie
     * 管理的类型：   -   {@link Constant#ADMIN_REQUEST}管理员的请求可以管理员工<br>
     * -   {@link Constant#GENERAL_REQUEST} 普通员工的请求，无法管理员工
     */
    private String requestType;
    private Context mContext;
    private View vProgress;
    private List<AdminPendingRequest> pendingRequestList;
    private List<OrgMemberEntity> memberList;//不包括family_tree
    private List<OrgGroupEntity> groupEntityList;
    private static final int GET_DATA = 0x11;
    private final static int ADD_MEMBER = 0x12;
    private final static int CREATE_GROUP = 0x13;
    private static final int GET_PENDING_REQUEST = 0x14;
    private PendingRequestAdapter requestAdapter;
    private OrgMemberListAdapter memberAdapter;
    private OrgGroupListAdapter groupAdapter;
    private MySwipeRefreshLayout refreshLayout;
    private ListView gridView;
    private ImageButton userIb;
    private View emptyView;
    private boolean isRefresh = false;
    private String Tag = OrgDetailActivity.class.getName();
    private EditText etSearch;
    private View serachLinear;
    private TextView tv_org_empty;
    private TextView searchTv;

    @Override
    protected void initBottomBar() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_org_list;
    }

    @Override
    protected void setTitle() {
        if (Constant.ORG_TRANSMIT_PENDING_REQUEST.equals(transmitData)) {
            tvTitle.setText(R.string.pending_requests);
        } else if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            tvTitle.setText(R.string.text_org_my_group);
        } else if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
            tvTitle.setText(R.string.text_org_all_staff);
        } else {
            tvTitle.setText(R.string.text_org_others);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        if (Constant.ORG_TRANSMIT_PENDING_REQUEST.equals(transmitData)) {
            rightButton.setVisibility(View.GONE);
            rightSearchButton.setVisibility(View.GONE);
        } else {
            if (Constant.ADMIN_REQUEST.equals(requestType) || Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
                rightButton.setImageResource(R.drawable.org_search_icon);
                rightSearchButton.setVisibility(View.GONE);
            } else {
                rightSearchButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void titleRightEvent() {
        if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            Intent intent = new Intent(mContext, SelectMemberActivity.class);
            intent.putExtra("isCreateNewGroup", true);
            intent.putExtra(Constant.SELECT_MEMBER_NORMAL_DATA, (Serializable) memberList);
            startActivityForResult(intent, CREATE_GROUP);
        } else if (Constant.ADMIN_REQUEST.equals(requestType) || Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
            if (serachLinear.getVisibility() == View.VISIBLE) {
                serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.org_up_from_bottom));
                serachLinear.setVisibility(View.GONE);
            } else {
                serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_from_top));
                serachLinear.setVisibility(View.VISIBLE);
            }
        } else if (Constant.ORG_TRANSMIT_OTHER.equals(transmitData)) {
            startActivity(new Intent(mContext, AddMembersActivity.class));
        }
    }

    @Override
    protected void titleRightSearchEvent() {
        super.titleRightSearchEvent();
        if (serachLinear.getVisibility() == View.VISIBLE) {
            serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.org_up_from_bottom));
            serachLinear.setVisibility(View.GONE);
        } else {
            serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_from_top));
            serachLinear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREATE_GROUP:
                if (resultCode == RESULT_OK) {
                    if (groupEntityList != null) {
                        getData();
                    }
                }
                break;
            case ADD_MEMBER:
                if (resultCode == RESULT_OK) {
                    vProgress.setVisibility(View.GONE);
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
                } else {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_canceled);
                }
                break;
        }

    }

    public void onceAdd(String ActionUserId) {
        vProgress.setVisibility(View.VISIBLE);
        Intent intent = new Intent(mContext, AddMemberWorkFlow.class);
        intent.putExtra(AddMemberWorkFlow.FLAG_FROM, MainActivity.getUser().getUser_id());
        intent.putExtra(AddMemberWorkFlow.FLAG_TO, ActionUserId);
        startActivityForResult(intent, ADD_MEMBER);
    }

    private void awaitingRemove(final String memberId) {
        vProgress.setVisibility(View.VISIBLE);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_BONDALERT_MEMEBER_REMOVE + MainActivity.getUser().getUser_id();
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(mContext).put(requestInfo, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                vProgress.setVisibility(View.GONE);
                MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
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

    private void showNoFriendDialog(final OrgMemberEntity familyMemberEntity) {

        // 没有添加好友，弹出添加好友界面提示框由用户选择是否添加
        final MyDialog myDialog = new MyDialog(mContext, "", mContext.getString(R.string.alert_ask_add_member));
        myDialog.setButtonAccept(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同意添加好友跳转至添加好友界面
                onceAdd(familyMemberEntity.getUser_id());
                myDialog.dismiss();
            }
        });
        myDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 不同意添加，关闭对话框
                myDialog.dismiss();
            }
        });
        myDialog.show();

//        LayoutInflater factory = LayoutInflater.from(mContext);
//        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
//        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
//        TextView profileView = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
//        TextView messageView = (TextView) selectIntention.findViewById(R.id.tv_to_message);
//        TextView leaveView = (TextView) selectIntention.findViewById(R.id.tv_leave_or_delete);
//        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
//        profileView.setText(R.string.text_view_profile);
//        messageView.setText(R.string.text_org_resend_request);
//        leaveView.setText(R.string.text_org_cancel_request);
//        profileView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSelectDialog.dismiss();
//                Intent intent = new Intent(mContext, FamilyViewProfileActivity.class);
//                intent.putExtra(UserEntity.EXTRA_MEMBER_ID, familyMemberEntity.getUser_id());
//                startActivity(intent);
//            }
//        });
//
//        messageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSelectDialog.dismiss();
//                onceAdd(familyMemberEntity.getUser_id());
//            }
//        });
//        leaveView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSelectDialog.dismiss();
//                awaitingRemove(familyMemberEntity.getUser_id());
//            }
//        });
//        cancelTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSelectDialog.dismiss();
//            }
//        });
//        showSelectDialog.show();
    }

    private void showGroupDialog(final OrgGroupEntity groupEntity) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView profileView = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
        TextView messageView = (TextView) selectIntention.findViewById(R.id.tv_to_message);
        TextView leaveView = (TextView) selectIntention.findViewById(R.id.tv_leave_or_delete);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        profileView.setText(R.string.text_org_group_profile);
        messageView.setText(R.string.text_org_message);
        leaveView.setText(R.string.text_org_leave_group);
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                Intent intent = new Intent(mContext, GroupSettingActivity.class);
                intent.putExtra(UserEntity.EXTRA_GROUP_ID, groupEntity.getGroup_id());
                intent.putExtra("groupName", groupEntity.getGroup_name());
                intent.putExtra(Constant.GROUP_DEFAULT, groupEntity.getGroup_default());
                startActivity(intent);
            }
        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageChatActivity.class);
                intent.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_GROUP);
                intent.putExtra(Constant.MESSAGE_CHART_GROUP_ID, groupEntity.getGroup_id());
                intent.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, groupEntity.getGroup_name());
                intent.putExtra(Constant.GROUP_DEFAULT, groupEntity.getGroup_default());
                startActivity(intent);
                showSelectDialog.dismiss();
            }
        });

        leaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                final MyDialog leaveGroupAlertDialog = new MyDialog(mContext, null, getResources().getString(R.string.text_leave_group_sure));
                leaveGroupAlertDialog.setButtonAccept(getResources().getString(R.string.text_dialog_yes), new View.OnClickListener() {
                    /**
                     * end
                     */
                    @Override
                    public void onClick(View v) {
                        leaveGroupAlertDialog.dismiss();
                        RequestInfo requestInfo = new RequestInfo();
                        HashMap<String, String> jsonParams = new HashMap<String, String>();
                        jsonParams.put("group_id", groupEntity.getGroup_id());
                        jsonParams.put("group_owner_id", "");
                        jsonParams.put("group_user_default", "0");
                        jsonParams.put("query_on", "exitGroup");
                        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
                        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                        requestInfo.url = String.format(Constant.API_LEAVE_GROUP, groupEntity.getGroup_id());
                        requestInfo.jsonParam = jsonParamsString;

                        new HttpTools(mContext).put(requestInfo, Tag, new HttpCallback() {
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
                                    if (("200").equals(jsonObject.getString("response_status_code"))) {
                                        MessageUtil.getInstance().showShortToast(getString(R.string.text_success_leave_group));//成功
                                        groupAdapter.removeData(groupEntity);
                                    } else {
                                        MessageUtil.getInstance().showShortToast(getString(R.string.text_fail_leave_group));//失败
                                    }
                                } catch (JSONException e) {
                                    MessageUtil.getInstance().showShortToast(getString(R.string.text_error));
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                MessageUtil.getInstance().showShortToast(getString(R.string.text_error));
                            }

                            @Override
                            public void onCancelled() {

                            }

                            @Override
                            public void onLoading(long count, long current) {

                            }
                        });
                    }
                });
                leaveGroupAlertDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGroupAlertDialog.dismiss();

                    }
                });
                if (!leaveGroupAlertDialog.isShowing()) {
                    leaveGroupAlertDialog.show();
                }
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    private void showStaffDialog(final OrgMemberEntity memberEntity) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView profileView = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
        TextView messageView = (TextView) selectIntention.findViewById(R.id.tv_to_message);
        TextView leaveView = (TextView) selectIntention.findViewById(R.id.tv_leave_or_delete);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        View lineView = selectIntention.findViewById(R.id.leave_line);
        if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
            profileView.setText(R.string.text_org_view_profile);
            leaveView.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        } else {
            profileView.setText(R.string.text_view_profile);
            if (Constant.ADMIN_REQUEST.equals(requestType)) {
                leaveView.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
            } else {
                leaveView.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
            }
            leaveView.setText(R.string.text_org_delete_contact);
        }
        messageView.setText(R.string.text_org_message);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                Intent intent = new Intent(mContext, FamilyProfileActivity.class);
                intent.putExtra(UserEntity.EXTRA_MEMBER_ID, memberEntity.getUser_id());
                intent.putExtra(UserEntity.EXTRA_GROUP_ID, memberEntity.getGroup_id());
                intent.putExtra(UserEntity.EXTRA_GROUP_NAME, memberEntity.getUser_given_name());
                startActivity(intent);
            }
        });

        leaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                removeOwnSupplier(memberEntity.getUser_id());
            }
        });
        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                Intent intent = new Intent(mContext, MessageChatActivity.class);
                intent.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_MEMBER);
                intent.putExtra(UserEntity.EXTRA_GROUP_ID, memberEntity.getGroup_id());
                intent.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, memberEntity.getUser_given_name());
                startActivity(intent);
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    private void removeOwnSupplier(String member_id){
        RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", member_id);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_REMOVE_OWN_SUPPLIER, MainActivity.getUser().getUser_id());
        new HttpTools(mContext).put(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void showSearchNoDataView(String string) {
        refreshLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        searchTv.setVisibility(View.VISIBLE);
        searchTv.setText(String.format(mContext.getString(R.string.text_search_no_data), string));
    }

    private void hideSearchNoDataView() {
        refreshLayout.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        searchTv.setVisibility(View.GONE);
    }

    @Override
    public void initView() {
        mContext = this;
        transmitData = getIntent().getStringExtra(Constant.ORG_TRANSMIT_DATA);
        requestType = getIntent().getStringExtra(Constant.REQUEST_TYPE);
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        refreshLayout = getViewById(R.id.swipe_refresh_layout);
        gridView = getViewById(R.id.org_list_view);
        userIb = getViewById(R.id.ib_top);
        etSearch = getViewById(R.id.et_search);
        emptyView = getViewById(R.id.family_group_text_empty);
        serachLinear = getViewById(R.id.search_linear);
        tv_org_empty = getViewById(R.id.tv_org_empty);
        searchTv = getViewById(R.id.message_search);
        if (Constant.ORG_TRANSMIT_PENDING_REQUEST.equals(transmitData)) {
            pendingRequestList = new ArrayList<>();
            requestAdapter = new PendingRequestAdapter(mContext, pendingRequestList);
            gridView.setAdapter(requestAdapter);
            tv_org_empty.setText(R.string.text_org_no_request);
        } else if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            groupEntityList = new ArrayList<>();
            groupAdapter = new OrgGroupListAdapter(mContext, groupEntityList);
            gridView.setAdapter(groupAdapter);
            tv_org_empty.setText(R.string.text_org_no_group);
        } else {
            memberList = new ArrayList<>();
            memberAdapter = new OrgMemberListAdapter(mContext, memberList, transmitData, requestType);
            memberAdapter.setListener(this);
            gridView.setAdapter(memberAdapter);
            tv_org_empty.setText(getString(R.string.text_org_no_contact));
        }
        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setSelection(0);
            }
        });

        if (requestAdapter != null) {
            requestAdapter.showNoData(new NoFoundDataListener() {
                @Override
                public void showFoundData(String string) {
                    showSearchNoDataView(string);
                }

                @Override
                public void showRefreshLayout() {
                    hideSearchNoDataView();
                }
            });
        }

        if (memberAdapter != null) {
            memberAdapter.showNoData(new NoFoundDataListener() {
                @Override
                public void showFoundData(String string) {
                    showSearchNoDataView(string);
                }

                @Override
                public void showRefreshLayout() {
                    hideSearchNoDataView();
                }
            });
        }
        if (groupAdapter != null) {
            groupAdapter.showNoData(new NoFoundDataListener() {
                @Override
                public void showFoundData(String string) {
                    showSearchNoDataView(string);
                }

                @Override
                public void showRefreshLayout() {
                    hideSearchNoDataView();
                }
            });
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {

                if (Constant.ORG_TRANSMIT_PENDING_REQUEST.equals(transmitData)) {
                    showRequestDialog(requestAdapter.getList().get(arg2));
                } else if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
                    showGroupDialog(groupAdapter.getList().get(arg2));
                } else {
                    OrgMemberEntity familyMemberEntity = memberAdapter.getList().get(arg2);
                    if (Constant.REQUEST_ADD_ADMIN.equals(requestType)) {
                        // 将点击的Item中的FamilyMemberEntity返回给启动的Activity;
                        Intent intent = new Intent();
                        intent.putExtra(OrganisationConstants.NEED_ADD_ADMIN_USER, familyMemberEntity);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData) && arg2 == 0) {
                        return;
                    } else if ("0".equals(familyMemberEntity.getAdded_flag())) {
                        showNoFriendDialog(familyMemberEntity);
                    } else {
                        //put请求消除爱心
//                            if ("".equals(familyMemberEntity.getMiss())) {
//                                updateMiss(familyMemberEntity.getUser_id());
//                                arg0.findViewById(R.id.myfamily_image_right).setVisibility(View.GONE);
//                                familyMemberEntity.setMiss(null);
//                            }
//                            Intent intent = new Intent(mContext, FamilyProfileActivity.class);
//                            intent.putExtra(UserEntity.EXTRA_MEMBER_ID, familyMemberEntity.getUser_id());
//                            intent.putExtra(UserEntity.EXTRA_GROUP_ID, familyMemberEntity.getGroup_id());
//                            intent.putExtra(UserEntity.EXTRA_GROUP_NAME, familyMemberEntity.getUser_given_name());
//                            startActivityForResult(intent, 1);
                        showStaffDialog(familyMemberEntity);

                    }

                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();
            }
        });
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
                return false;
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (gridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                    refreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    refreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (gridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                    refreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    refreshLayout.setEnabled(false);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String etImport = etSearch.getText().toString();
                setSearchData(etImport);
            }
        });
    }

    /**
     * 弹出需要处理请求的对话框
     *
     * @param pendingRequest - 被处理的请求对象封装实例
     */
    private void showRequestDialog(final AdminPendingRequest pendingRequest) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView tvApprove = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
        TextView tvReject = (TextView) selectIntention.findViewById(R.id.tv_to_message);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);

        selectIntention.findViewById(R.id.tv_leave_or_delete).setVisibility(View.GONE);
        selectIntention.findViewById(R.id.leave_line).setVisibility(View.GONE);

        tvApprove.setText(R.string.text_approve);
        tvReject.setText(R.string.text_item_reject);
        tvReject.setTextColor(Color.RED);

        tvApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = null;
                if (OrganisationConstants.MODULE_ACTION_JOIN.equals(pendingRequest.getModule_action())) {
                    url = String.format(OrganisationConstants.API_PUT_ACCEPT_JOIN_ORG_REQ, MainActivity.getUser().getOrg_id());
                } else if (OrganisationConstants.MODULE_ACTION_LEAVE.equals(pendingRequest.getModule_action())) {
                    url = String.format(OrganisationConstants.API_PUT_ACCEPT_LEAVE_ORG_REQ, MainActivity.getUser().getOrg_id());
                }
                if (url != null) {
                    dealRequest(url, pendingRequest);
                }
                showSelectDialog.dismiss();
            }
        });

        tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = null;
                if (OrganisationConstants.MODULE_ACTION_JOIN.equals(pendingRequest.getModule_action())) {
                    url = String.format(OrganisationConstants.API_PUT_REJECT_JOIN_ORG_REQ, MainActivity.getUser().getOrg_id());
                } else if (OrganisationConstants.MODULE_ACTION_LEAVE.equals(pendingRequest.getModule_action())) {
                    url = String.format(OrganisationConstants.API_PUT_REMOVE_ORG_MEMBER, MainActivity.getUser().getOrg_id());
                }
                if (url != null) {
                    dealRequest(url, pendingRequest);
                }
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
    }

    /**
     * 处理成员的请求
     *
     * @param url            请求路径：可以是拒绝，接受
     * @param pendingRequest 请求成员封装实例
     */
    private void dealRequest(String url, final AdminPendingRequest pendingRequest) {
        LogUtil.d(TAG, "dealRequest() called with: " + "url = [" + url + "]");
        Map<String, String> params = new HashMap<>();
        params.put(OrganisationConstants.USER_ID, MainActivity.getUser().getUser_id());
        params.put(OrganisationConstants.MEMBER_ID, pendingRequest.getAction_user_id());
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = url;
        new HttpTools(mContext).put(requestInfo, ACCEPT_REQUEST_TAG, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "onStart: accept request");
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "onFinish: accept request");
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: accept request " + "string = [" + string + "]");
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("200".equals(jsonObject.optString("response_status_code", ""))) {
                        Toast.makeText(mContext, getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                        requestAdapter.getList().remove(pendingRequest);
                        requestAdapter.notifyDataSetChanged();
                        if (requestAdapter.getList().isEmpty()) {
                            showEmptyView();
                        }
                    }
                } catch (JSONException e) {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "onError: accept request", e);
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "onCancelled: accept request");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading() called with: accept request " + "count = [" + count + "], current = [" + current + "]");
            }
        });
    }

    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        if (Constant.ORG_TRANSMIT_PENDING_REQUEST.equals(transmitData)) {
            Filter filter = requestAdapter.getFilter();
            filter.filter(etImport);
        } else if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            Filter filter = groupAdapter.getFilter();
            filter.filter(etImport);
        } else {
            memberAdapter.setSerach(memberList);
            Filter filter = memberAdapter.getFilter();
            filter.filter(etImport);
        }
    }

    private void finishReFresh() {
        if (isRefresh) {
            refreshLayout.setRefreshing(false);
            isRefresh = false;
        }
    }

    public void updateMiss(String member_id) {
        RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", member_id);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_UPDATE_GOOD_JOB, MainActivity.getUser().getUser_id());
        new HttpTools(mContext).put(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("200".equals(jsonObject.optString("response_status_code", ""))) {
                        Toast.makeText(mContext, getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                    e.printStackTrace();
                }
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

    @Override
    public void requestData() {
    }

    private void getData() {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }

        if (Constant.ORG_TRANSMIT_PENDING_REQUEST.equals(transmitData)) {
            getPendingList(String.format(OrganisationConstants.API_GET_ADMIN_PENDING_REQUEST_LIST, MainActivity.getUser().getOrg_id()));
        } else if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            getGroupList();
        } else if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
            getMemberList(String.format(Constant.API_GET_ALL_STAFF, MainActivity.getUser().getUser_id()));
        } else {
            UserEntity current = MainActivity.getUser();
            if (Constant.ADMIN_REQUEST.equals(requestType)) { // 管理员管理其它成员列表
                getMemberList(String.format(OrganisationConstants.API_GET_ADMIN_ALL_OTHER, current.getUser_id(), current.getOrg_id()));
            } else {
                getMemberList(String.format(Constant.API_GET_ALL_OTHER, current.getUser_id()));
            }
        }
    }

    /**
     * 获取请求获取管理员待批准列表
     *
     * @param url 获取的网络路径
     */
    private void getPendingList(String url) {
        LogUtil.d(TAG, "getPendingList() called with: " + "url = [" + url + "]");

        new HttpTools(App.getContextInstance()).get(url, null, PENDING_REQUEST_LIST, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "onStart: pending request list");
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "onFinish: pending request list");
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: pending request list " + "string = [" + string + "]");

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                finishReFresh();

                if ((TextUtils.isEmpty(string) || "[]".equals(string))) {
                    showEmptyView();
                    return;
                }

                List<AdminPendingRequest> list = new ArrayList<>();
                List<AdminPendingRequest> adminPendingRequests = gson.fromJson(string, new TypeToken<ArrayList<AdminPendingRequest>>() {
                }.getType());

                if (adminPendingRequests != null && adminPendingRequests.size() > 0) {
                    list.addAll(adminPendingRequests);
                }
                if (list.size() > 0) {
                    hideEmptyView();
                    Message.obtain(handler, GET_PENDING_REQUEST, list).sendToTarget();
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "onError: pending request list", e);
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "onCancelled: pending request list");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading: pending request list");
            }
        });
    }

    private void getMemberList(String url) {
        new HttpTools(mContext).get(url, null, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                finishReFresh();
                List<OrgMemberEntity> list = new ArrayList<>();
                if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData) // 显示的是成员
                        && !Constant.REQUEST_ADD_ADMIN.equals(requestType) // 不是添加管理员的请求需要显示自己
                        ) {
                    OrgMemberEntity meMemberEntity = new OrgMemberEntity();
                    UserEntity userEntity = MainActivity.getUser();
                    meMemberEntity.setGroup_id(userEntity.getGroup_id());
                    meMemberEntity.setUser_id(userEntity.getUser_id());
                    meMemberEntity.setPosition(userEntity.getPosition());
                    meMemberEntity.setDepartment(userEntity.getDepartment());
                    meMemberEntity.setAdmin_flag(userEntity.getAdmin());
                    meMemberEntity.setUser_given_name(mContext.getString(R.string.text_me));
                    list.add(meMemberEntity);
                }
                if ((TextUtils.isEmpty(response) || "[]".equals(response)) && list.size() == 0) {
                    showEmptyView();
                    return;
                }
                List<OrgMemberEntity> memberEntityList = gson.fromJson(response, new TypeToken<ArrayList<OrgMemberEntity>>() {
                }.getType());

                if (memberEntityList != null && memberEntityList.size() > 0) { //
                    list.addAll(memberEntityList);
                    if (Constant.REQUEST_ADD_ADMIN.equals(requestType)) { // 是添加管理员，所以已经是管理员的成员不能显示，遍历移除显示列表
                        for (OrgMemberEntity orgMemberEntity : memberEntityList) {
                            if ("1".equals(orgMemberEntity.getAdmin_flag())) {
                                list.remove(orgMemberEntity);
                            }
                        }
                        if (list.isEmpty()) {
                            showEmptyView();
                            return;
                        }
                    }
                }
                if (list.size() > 0) {
                    hideEmptyView();
                    Message.obtain(handler, GET_DATA, list).sendToTarget();
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                finishReFresh();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void getGroupList() {
        new HttpTools(mContext).get(String.format(Constant.API_GET_GROUP_LIST, MainActivity.getUser().getUser_id()), null, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                finishReFresh();
                if (TextUtils.isEmpty(response) || "[]".equals(response)) {
                    showEmptyView();
                    return;
                }
                List<OrgGroupEntity> groupList = gson.fromJson(response, new TypeToken<ArrayList<OrgGroupEntity>>() {
                }.getType());
                if (groupList != null && groupList.size() > 0) {
                    hideEmptyView();
                    Message.obtain(handler, GET_DATA, groupList).sendToTarget();
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                finishReFresh();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
                        if (groupEntityList != null) {
                            groupEntityList.clear();
                        }
                        groupEntityList = (List<OrgGroupEntity>) msg.obj;
                        groupAdapter.addNewData(groupEntityList);
                    } else {
                        if (memberList != null) {
                            memberList.clear();
                        }
                        memberList = (List<OrgMemberEntity>) msg.obj;
                        memberAdapter.addNewData(memberList);
                    }
                    break;

                case GET_PENDING_REQUEST:
                    if (pendingRequestList != null) {
                        pendingRequestList.clear();
                    }
                    pendingRequestList = (List<AdminPendingRequest>) msg.obj;
                    requestAdapter.addNewData(pendingRequestList);
                    break;
            }
            return false;
        }
    });

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * 删除联系人
     *
     * @param memberEntity 被删除对象的实例
     */
    @Override
    public void removeMember(final OrgMemberEntity memberEntity) {
        UserEntity currentUser = MainActivity.getUser();
        String url;
        Map<String, String> params = new HashMap<>();
        if (Constant.ORG_TRANSMIT_OTHER.equals(transmitData) && "1".equals(memberEntity.getAdded_flag())) {
            // 管理员删除自己的供应商和客户
            url = String.format(OrganisationConstants.API_PUT_REMOVE_OTHER, currentUser.getUser_id());
            params.put(OrganisationConstants.MEMBER_ID, memberEntity.getUser_id());
        } else if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData)){
            url = String.format(OrganisationConstants.API_PUT_REMOVE_ORG_MEMBER, currentUser.getOrg_id());
            params.put(OrganisationConstants.USER_ID, currentUser.getUser_id());
            params.put(OrganisationConstants.MEMBER_ID, memberEntity.getUser_id());
        } else if (Constant.ORG_TRANSMIT_OTHER.equals(transmitData)) {
            url = String.format(OrganisationConstants.API_PUT_REMOVE_ORG_OTHER, currentUser.getOrg_id());
            params.put(OrganisationConstants.USER_ID, currentUser.getUser_id());
            params.put(OrganisationConstants.MEMBER_ID, memberEntity.getUser_id());
        } else {
            return;
        }

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = url;
        new HttpTools(mContext).put(requestInfo, "remove member", new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "onStart: remove member");
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "onFinish: remove member");
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: remove member " + "string = [" + string + "]");
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("200".equals(jsonObject.optString("response_status_code", ""))) {
                        Toast.makeText(mContext, getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                        memberAdapter.getList().remove(memberEntity);
                        memberAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "onError: remove member", e);
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "onCancelled: remove member");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading() called with: remove member " + "count = [" + count + "], current = [" + current + "]");
            }
        });
    }
}
