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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.SelectMemberListAdapter;
import com.madxstudio.co8.entity.OrgMemberEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;
import com.material.widget.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by quankun on 15/5/12.
 */
public class SelectMemberActivity extends BaseActivity {
    private Context mContext;
    private View vProgress;
    private List<OrgMemberEntity> memberList;//不包括family_tree
    private static final int GET_DATA = 0x11;
    private SelectMemberListAdapter memberAdapter;
    private MySwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ImageButton userIb;
    private View emptyView;
    private boolean isRefresh = false;
    private String Tag = SelectMemberActivity.class.getName();
    private EditText etSearch;
    private View serachLinear;
    private TextView tv_org_empty;
    private TextView searchTv;
    private CheckBox selectAllMember;
    private List<OrgMemberEntity> selectMemberEntityList;

    @Override
    protected void initBottomBar() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_select_member;
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_select_members));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (memberList == null || memberList.size() == 0) {
            getData();
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.ok_press);
        rightSearchButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent();
//        if (isCreateNewGroup) {
        if (selectMemberEntityList.size() > 0) {
            intent.putExtra("members_data", new Gson().toJson(selectMemberEntityList));
            intent.setClass(mContext, CreateGroupDialogActivity.class);
            startActivity(intent);
            finish();
        } else {
            LayoutInflater factory = LayoutInflater.from(mContext);
            View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
            final Dialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
            TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
            tv_no_member.setText(getString(R.string.text_create_group_members_least_two));
            TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectDialog.dismiss();
                }
            });
            showSelectDialog.show();
        }
//        } else {
//            setResult(RESULT_OK, intent);
//            finish();
//        }
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
//        switch (requestCode) {
//            case CREATE_GROUP:
//                if (resultCode == RESULT_OK) {
//                    if (groupEntityList != null) {
//                        getData();
//                    }
//                }
//                break;
//            case ADD_MEMBER:
//                if (resultCode == RESULT_OK) {
//                    vProgress.setVisibility(View.GONE);
//                    MessageUtil.showMessage(mContext, R.string.msg_action_successed);
//                } else {
//                    MessageUtil.showMessage(mContext, R.string.msg_action_canceled);
//                }
//                break;
//        }

    }

    @Override
    public void initView() {
        mContext = this;
        vProgress = getViewById(R.id.rl_progress);
        refreshLayout = getViewById(R.id.swipe_refresh_layout);
        listView = getViewById(R.id.org_list_view);
        userIb = getViewById(R.id.ib_top);
        etSearch = getViewById(R.id.et_search);
        emptyView = getViewById(R.id.family_group_text_empty);
        serachLinear = getViewById(R.id.search_linear);
        tv_org_empty = getViewById(R.id.tv_org_empty);
        searchTv = getViewById(R.id.message_search);
        selectAllMember = getViewById(R.id.check_member_item);
        serachLinear.setVisibility(View.GONE);
        memberList = (List<OrgMemberEntity>) getIntent().getSerializableExtra(Constant.SELECT_MEMBER_NORMAL_DATA);

        memberAdapter = new SelectMemberListAdapter(mContext, memberList);
        listView.setAdapter(memberAdapter);
        selectMemberEntityList = new ArrayList<>();
        String memberData = getIntent().getStringExtra(Constant.SELECT_MEMBER_DATA);
        if (memberData != null) {
            selectMemberEntityList = new Gson().fromJson(memberData, new TypeToken<ArrayList<OrgMemberEntity>>() {
            }.getType());
            if (null != selectMemberEntityList && selectMemberEntityList.size() > 0) {
                memberAdapter.addNewData(selectMemberEntityList);
            }
        }

        tv_org_empty.setText(getString(R.string.text_org_no_contact));

        if (memberList == null) {
            memberList = new ArrayList<>();
            vProgress.setVisibility(View.VISIBLE);
        } else {
            vProgress.setVisibility(View.GONE);
        }
        getViewById(R.id.select_all_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAllMember.isChecked() == true) {
                    selectAllMember.setChecked(false);
                    selectMemberEntityList.clear();
                    memberAdapter.removeAllSelectData();
                } else {
                    selectAllMember.setChecked(true);
                    selectMemberEntityList.clear();
                    memberAdapter.addAllSelectData();
                    selectMemberEntityList.addAll(memberList);
                }
            }
        });

        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(0);
            }
        });
        memberAdapter.showNoData(new NoFoundDataListener() {
            @Override
            public void showFoundData(String string) {
                refreshLayout.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                searchTv.setVisibility(View.VISIBLE);
                searchTv.setText(String.format(mContext.getString(R.string.text_search_no_data), string));
            }

            @Override
            public void showRefreshLayout() {
                refreshLayout.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                searchTv.setVisibility(View.GONE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                OrgMemberEntity familyMemberEntity = memberAdapter.getList().get(arg2);
                String userId = familyMemberEntity.getUser_id();
                if ("0".equals(familyMemberEntity.getFam_accept_flag())) {
//                    showNoFriendDialog();
                } else {
                    CheckBox selectItem = (CheckBox) arg1.findViewById(R.id.check_member_item);
                    if (selectItem.isChecked()) {
                        selectItem.setChecked(false);
                        memberAdapter.removeSelectData(userId);
                        selectMemberEntityList.remove(familyMemberEntity);
                    } else {
                        selectItem.setChecked(true);
                        memberAdapter.addSelectData(userId);
                        selectMemberEntityList.add(familyMemberEntity);
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
        listView.setOnTouchListener(new View.OnTouchListener() {
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                    refreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    refreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getFirstVisiblePosition() == 0) {
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
        List<OrgMemberEntity> familyMemberEntityList;
        if (!TextUtils.isEmpty(etImport)) {
            memberAdapter.setSerach(memberList);
            Filter filter = memberAdapter.getFilter();
            filter.filter(etImport);
        } else {
            familyMemberEntityList = searchMemberList(etImport, memberList);
            memberAdapter.addNewData(familyMemberEntityList);
        }
    }

    private List<OrgMemberEntity> searchMemberList(String name, List<OrgMemberEntity> list) {
        if (TextUtils.isEmpty(name)) {
            return list;
        }
        List<OrgMemberEntity> results = new ArrayList();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (OrgMemberEntity memberEntity : list) {
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
                new HttpTools(mContext).get(String.format(Constant.API_GET_ALL_STAFF, MainActivity.getUser().getUser_id()), null, Tag, new HttpCallback() {
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
                        List<OrgMemberEntity> memberEntityList = gson.fromJson(response, new TypeToken<ArrayList<OrgMemberEntity>>() {
                        }.getType());
                        if (memberEntityList != null && memberEntityList.size() > 0) {
                            List<OrgMemberEntity> list = new ArrayList<>();
                            for (OrgMemberEntity memberEntity : memberEntityList) {
                                if (!"0".equals(memberEntity.getFam_accept_flag())) {
                                    list.add(memberEntity);
                                }
                            }
                            if (list.size() > 0) {
                                hideEmptyView();
                                Message.obtain(handler, GET_DATA, list).sendToTarget();
                            } else {
                                showEmptyView();
                            }
                        } else {
                            showEmptyView();
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
                    if (memberList != null) {
                        memberList.clear();
                    }
                    memberList = (List<OrgMemberEntity>) msg.obj;
                    memberAdapter.addNewData(memberList);
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