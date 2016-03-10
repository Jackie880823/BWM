package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.FamilyGroupAdapter;
import com.madxstudio.co8.adapter.MyFamilyAdapter;
import com.madxstudio.co8.entity.FamilyGroupEntity;
import com.madxstudio.co8.entity.FamilyMemberEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.add.AddMembersActivity;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by quankun on 16/3/8.
 */
public class OrgListActivity extends BaseActivity {
    private String transmitData;
    private Context mContext;
    private View vProgress;
    private List<FamilyMemberEntity> memberList;//不包括family_tree
    private List<FamilyGroupEntity> groupEntityList;
    private static final int GET_DATA = 0x11;
    private final static int ADD_MEMBER = 0x12;
    private final static int CREATE_GROUP = 0x13;
    private MyFamilyAdapter memberAdapter;
    private FamilyGroupAdapter groupAdapter;
    private MySwipeRefreshLayout refreshLayout;
    private GridView gridView;
    private ImageButton userIb;
    private View emptyView;
    private boolean isRefresh = false;
    private String Tag = OrgListActivity.class.getName();
    private Dialog showSelectDialog;
    private EditText etSearch;
    private View serachLinear;
    private TextView tv_org_empty;

    @Override
    protected void initBottomBar() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_org_list;
    }

    @Override
    protected void setTitle() {
        if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
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
        if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
            rightButton.setVisibility(View.GONE);
        }
        rightSearchButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void titleRightEvent() {
        if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            Intent intent = new Intent(mContext, InviteMemberActivity.class);
            intent.putExtra("isCreateNewGroup", true);
            intent.putExtra("jumpIndex", 0);
            startActivityForResult(intent, CREATE_GROUP);
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
                        groupAdapter.clearBitmap(groupEntityList);
                        groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
                        gridView.setAdapter(groupAdapter);
                        getData();
                    }
                }
                break;
            case ADD_MEMBER:
                if (resultCode == RESULT_OK) {
                    vProgress.setVisibility(View.GONE);
                    MessageUtil.showMessage(mContext, R.string.msg_action_successed);
                } else {
                    MessageUtil.showMessage(mContext, R.string.msg_action_canceled);
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
                MessageUtil.showMessage(mContext, R.string.msg_action_successed);
                if (groupEntityList != null) {
                    groupAdapter.clearBitmap(groupEntityList);
                    groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
                    gridView.setAdapter(groupAdapter);
                    getData();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(mContext, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void showNoFriendDialog(final FamilyMemberEntity familyMemberEntity) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_bond_alert_member, null);
        showSelectDialog = new MyDialog(mContext, null, selectIntention);
        showSelectDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        selectIntention.findViewById(R.id.subject_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onceAdd(familyMemberEntity.getUser_id());
                showSelectDialog.dismiss();
            }
        });
        selectIntention.findViewById(R.id.subject_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awaitingRemove(familyMemberEntity.getUser_id());
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    @Override
    public void initView() {
        mContext = this;
        transmitData = getIntent().getStringExtra(Constant.ORG_TRANSMIT_DATA);
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        refreshLayout = getViewById(R.id.swipe_refresh_layout);
        gridView = getViewById(R.id.family_grid_view);
        userIb = getViewById(R.id.ib_top);
        etSearch = getViewById(R.id.et_search);
        emptyView = getViewById(R.id.family_group_text_empty);
        serachLinear = getViewById(R.id.search_linear);
        tv_org_empty= getViewById(R.id.tv_org_empty);
        if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            groupEntityList = new ArrayList<>();
            groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
            gridView.setAdapter(groupAdapter);
            tv_org_empty.setText(R.string.text_org_no_group);
        } else {
            memberList = new ArrayList<>();
            memberAdapter = new MyFamilyAdapter(mContext, memberList);
            gridView.setAdapter(memberAdapter);
            tv_org_empty.setText("No Contacts");
        }
        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setSelection(0);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
                    Intent intent = new Intent(mContext, MessageChatActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("groupId", groupAdapter.getGroupList().get(arg2).getGroup_id());
                    intent.putExtra("titleName", groupAdapter.getGroupList().get(arg2).getGroup_name());
                    intent.putExtra(Constant.GROUP_DEFAULT, groupAdapter.getGroupList().get(arg2).getGroup_default());
                    startActivityForResult(intent, 1);
                } else {
                    FamilyMemberEntity familyMemberEntity = memberAdapter.getList().get(arg2);
                    if ("0".equals(familyMemberEntity.getFam_accept_flag())) {
                        //不是好友,提示等待接收
                        if (showSelectDialog != null && showSelectDialog.isShowing()) {
                            return;
                        }
                        showNoFriendDialog(familyMemberEntity);
                        return;
                    } else {
                        //put请求消除爱心
                        if ("".equals(familyMemberEntity.getMiss())) {
                            updateMiss(familyMemberEntity.getUser_id());
                            arg0.findViewById(R.id.myfamily_image_right).setVisibility(View.GONE);
                            familyMemberEntity.setMiss(null);
                        }
                        Intent intent = new Intent(mContext, FamilyProfileActivity.class);
                        intent.putExtra(UserEntity.EXTRA_MEMBER_ID, familyMemberEntity.getUser_id());
                        intent.putExtra(UserEntity.EXTRA_GROUP_ID, familyMemberEntity.getGroup_id());
                        intent.putExtra(UserEntity.EXTRA_GROUP_NAME, familyMemberEntity.getUser_given_name());
                        startActivityForResult(intent, 1);
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

    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
            List<FamilyGroupEntity> familyGroupEntityList;
            if (TextUtils.isEmpty(etImport)) {
                familyGroupEntityList = groupEntityList;
                groupAdapter.addData(familyGroupEntityList);
            } else {
                Filter filter = groupAdapter.getFilter();
                filter.filter(etImport);
            }
        } else {
            List<FamilyMemberEntity> familyMemberEntityList;
            if (!TextUtils.isEmpty(etImport)) {
                memberAdapter.setSerach(memberList);
                Filter filter = memberAdapter.getFilter();
                filter.filter(etImport);
            } else {
                familyMemberEntityList = searchMemberList(etImport, memberList);
                memberAdapter.addNewData(familyMemberEntityList);
            }
        }

    }

    private List<FamilyMemberEntity> searchMemberList(String name, List<FamilyMemberEntity> list) {
        if (TextUtils.isEmpty(name)) {
            return list;
        }
        List<FamilyMemberEntity> results = new ArrayList();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (FamilyMemberEntity memberEntity : list) {
            String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getUser_given_name());
            Matcher matcher = pattern.matcher(userName);
            if (matcher.find()) {
                results.add(memberEntity);
            }
        }
        return results;
    }

    private void finishReFresh() {
        if (isRefresh) {
            refreshLayout.setRefreshing(false);
            isRefresh = false;
        }
    }

    public void updateMiss(String member_id) {
        final RequestInfo requestInfo = new RequestInfo();
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
                    MessageUtil.showMessage(mContext, R.string.msg_action_failed);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(mContext, R.string.msg_action_failed);
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

        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(mContext).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null, Tag, new HttpCallback() {
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
                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
                            showEmptyView();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<FamilyMemberEntity> memberList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<FamilyMemberEntity>>() {
                            }.getType());
                            List<FamilyGroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<FamilyGroupEntity>>() {
                            }.getType());
                            if (Constant.ORG_TRANSMIT_GROUP.equals(transmitData)) {
                                if (groupList != null && groupList.size() > 0) {
                                    hideEmptyView();
                                    Message.obtain(handler, GET_DATA, groupList).sendToTarget();
                                } else {
                                    showEmptyView();
                                }
                            } else {
                                if (memberList != null && memberList.size() > 0) {
                                    List<FamilyMemberEntity> staffList = new ArrayList<FamilyMemberEntity>();
                                    List<FamilyMemberEntity> otherList = new ArrayList<FamilyMemberEntity>();
                                    for (FamilyMemberEntity memberEntity : memberList) {
                                        String tree_type = memberEntity.getTree_type();
                                        if (Constant.FAMILY_PARENT.equalsIgnoreCase(tree_type) || Constant.FAMILY_CHILDREN.equalsIgnoreCase(tree_type)
                                                || Constant.FAMILY_SIBLING.equalsIgnoreCase(tree_type)) {
                                            staffList.add(memberEntity);
                                        } else {
                                            otherList.add(memberEntity);
                                        }
                                    }
                                    if (Constant.ORG_TRANSMIT_STAFF.equals(transmitData)) {
                                        if (staffList.size() > 0) {
                                            hideEmptyView();
                                            Message.obtain(handler, GET_DATA, staffList).sendToTarget();
                                        } else {
                                            showEmptyView();
                                        }
                                    } else {
                                        if (otherList.size() > 0) {
                                            hideEmptyView();
                                            Message.obtain(handler, GET_DATA, otherList).sendToTarget();
                                        } else {
                                            showEmptyView();
                                        }
                                    }
                                } else {
                                    showEmptyView();
                                }
                            }
                        } catch (JSONException e) {
                            finishReFresh();
                            showEmptyView();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.showMessage(mContext, R.string.msg_action_failed);
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
        }.start();
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
                        groupEntityList = (List<FamilyGroupEntity>) msg.obj;
                        groupAdapter.addData(groupEntityList);
                    } else {
                        if (memberList != null) {
                            memberList.clear();
                        }
                        memberList = (List<FamilyMemberEntity>) msg.obj;
                        memberAdapter.addNewData(memberList);
                    }
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
}
