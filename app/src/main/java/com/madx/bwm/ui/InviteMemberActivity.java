package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.InviteGroupAdapter;
import com.madx.bwm.adapter.InviteMemberAdapter;
import com.madx.bwm.entity.FamilyGroupEntity;
import com.madx.bwm.entity.FamilyMemberEntity;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.util.PinYin4JUtil;
import com.madx.bwm.widget.MyDialog;

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
 * Created by quankun on 15/5/12.
 */
public class InviteMemberActivity extends BaseActivity {
    private static final String Tag = InviteMemberActivity.class.getSimpleName();
    private EditText etSearch;
    private ViewPager pager;
    private TextView message_member_tv;
    private TextView message_group_tv;
    private Context mContext;
    private boolean isMemberRefresh, isGroupRefresh;
    private List<FamilyMemberEntity> memberEntityList;
    private List<FamilyGroupEntity> groupEntityList;
//    private MySwipeRefreshLayout groupRefreshLayout, memberRefreshLayout;
    private ProgressDialog mProgressDialog;
    private static final int GET_DATA = 0x11;
    private InviteMemberAdapter memberAdapter;
    private InviteGroupAdapter groupAdapter;
    private List<String> selectMemberList;
    private List<String> selectGroupList;
    private int type;
    private int groupType;
    private List<FamilyMemberEntity> selectMemberEntityList;
    private List<FamilyGroupEntity> selectGroupEntityList;
    private boolean isFirstData = true;
    private boolean isCreateNewGroup;
    private int jumpIndex = 0;

    private String MemeberSearch;
    private String GroupSearch;

    List<FamilyMemberEntity> memberList;
    List<FamilyGroupEntity> groupList;
    List<FamilyMemberEntity> searchmemberList = new LinkedList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA:
                    Map<String, List> map = (Map<String, List>) msg.obj;
                    if (memberEntityList != null) {
                        memberEntityList.clear();
                    }
                    memberEntityList = map.get("private");
                    if (memberEntityList != null) {
                        if (type == 1) {
                            memberList = new ArrayList<>();
                            for (FamilyMemberEntity memberEntity : memberEntityList) {
                                if (!selectMemberList.contains(memberEntity.getUser_id())) {
                                    memberList.add(memberEntity);

                                }
                                else {
                                    if (isFirstData) {
                                        selectMemberEntityList.add(memberEntity);
                                    }
                                }
                            }
                            searchmemberList.addAll(memberList);
                            memberAdapter.addNewData(memberList);
                        } else {
                            for (FamilyMemberEntity memberEntity : memberEntityList) {
                                if (selectMemberList.contains(memberEntity.getUser_id()) && isFirstData) {
                                    selectMemberEntityList.add(memberEntity);
                                }
                            }
                            memberAdapter.addNewData(memberEntityList);
                        }
                    }
                    if (groupEntityList != null) {
                        groupEntityList.clear();
                    }
                    groupEntityList = map.get("group");
                    if (groupEntityList != null) {
                        if (groupType == 1) {
                            groupList = new ArrayList<>();
                            for (FamilyGroupEntity groupEntity : groupEntityList) {
                                if (!selectGroupList.contains(groupEntity.getGroup_id())) {
                                    groupList.add(groupEntity);
                                } else {
                                    if (isFirstData) {
                                        selectGroupEntityList.add(groupEntity);
                                    }
                                }
                            }
                            groupAdapter.addData(groupList);
                        } else {
                            for (FamilyGroupEntity groupEntity : groupEntityList) {
                                if (selectGroupList.contains(groupEntity.getGroup_id()) && isFirstData) {
                                    selectGroupEntityList.add(groupEntity);
                                }
                            }
                            groupAdapter.addData(groupEntityList);
                        }
                    }
                    break;
            }

        }
    };

    @Override
    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        String memberData = intent.getStringExtra("members_data");//需要传过来的已选中的gson格式的UserEntity或FamilyMemberEntity
        String groupData = intent.getStringExtra("groups_data");//需要传过来的已选中的gson格式的GroupEntity或FamilyGroupEntity
        isCreateNewGroup = intent.getBooleanExtra("isCreateNewGroup", false);
        jumpIndex = intent.getIntExtra("jumpIndex", 0);
        type = intent.getIntExtra("type", 0);//传过来1表示要隐藏；0表示不隐藏
        groupType = intent.getIntExtra("groupType", -1);
        if (groupType == -1) {
            groupType = type;
        }
        List<FamilyMemberEntity> memberSelectList = null;
        if (memberData != null) {
            memberSelectList = new Gson().fromJson(memberData, new TypeToken<ArrayList<FamilyMemberEntity>>() {
            }.getType());
        }
        selectMemberList = new ArrayList<>();
        selectGroupList = new ArrayList<>();
        selectMemberEntityList = new ArrayList<>();
        selectGroupEntityList = new ArrayList<>();
        if (null != memberSelectList && memberSelectList.size() > 0) {
            for (FamilyMemberEntity userEntity : memberSelectList) {
                selectMemberList.add(userEntity.getUser_id());
            }
        }
        List<FamilyGroupEntity> groupSelectList = null;
        if (groupData != null) {
            groupSelectList = new Gson().fromJson(groupData, new TypeToken<ArrayList<FamilyGroupEntity>>() {
            }.getType());
        }
        if (null != groupSelectList && groupSelectList.size() > 0) {
            for (FamilyGroupEntity userEntity : groupSelectList) {
                selectGroupList.add(userEntity.getGroup_id());
            }
        }
        pager = getViewById(R.id.family_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        message_group_tv = getViewById(R.id.message_group_tv);
        etSearch = getViewById(R.id.et_search);
        memberEntityList = new ArrayList<>();
        groupEntityList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(mContext, getString(R.string.text_loading));
        mProgressDialog.show();

        memberAdapter = new InviteMemberAdapter(mContext, memberEntityList, selectMemberList);
        groupAdapter = new InviteGroupAdapter(mContext,groupEntityList, selectGroupList);

        //绑定自定义适配器
        pager.setAdapter(new FamilyPagerAdapter(initPagerView()));
        pager.setOnPageChangeListener(new MyOnPageChanger());
        message_member_tv.setOnClickListener(this);
        message_group_tv.setOnClickListener(this);

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
//                if(TextUtils.isEmpty(etImport)){
//                    memberAdapter.addNewData(memberList);
//                }else {
//                    setSearchData(etImport);
//                }

                if (pager.getCurrentItem() == 0) {
                    MemeberSearch = etImport;
                } else {
                    GroupSearch = etImport;
                }
                if (!isTabChanged) {
                    setSearchData(etImport);
                } else {
                    isTabChanged = false;
                }

            }
        });
    }

    boolean isTabChanged;

    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        if (pager.getCurrentItem() == 0) {
            List<FamilyMemberEntity> familyMemberEntityList;
            if (TextUtils.isEmpty(MemeberSearch)) {
                if (type == 1) {
                    familyMemberEntityList = memberList;
                } else {
                    familyMemberEntityList = memberEntityList;
                }
                //刷新适配器数据
                memberAdapter.addNewData(familyMemberEntityList);
            } else {
                if (type == 1) {
                    List<FamilyMemberEntity> memberList = new ArrayList<>();
//                    familyMemberEntityList = searchMemberList(MemeberSearch, searchmemberList);
                    memberAdapter.setSerchList(searchmemberList);
                    Filter filter =  memberAdapter.getFilter();
                    filter.filter(MemeberSearch);
//                    familyMemberEntityList = searchMemberList(MemeberSearch, memberList);
                } else {

//                    familyMemberEntityList = searchMemberList(MemeberSearch, memberEntityList);
                    memberAdapter.setSerchList(memberEntityList);
                    Filter filter =  memberAdapter.getFilter();
                    filter.filter(MemeberSearch);
                }
            }
        } else {
            List<FamilyGroupEntity> familyGroupEntityList;
            if (TextUtils.isEmpty(etImport)) {
                familyGroupEntityList = groupEntityList;
                //刷新适配器数据
                groupAdapter.addData(familyGroupEntityList);
            } else {
//                familyGroupEntityList = searchGroupList(etImport, groupEntityList);
                Filter filter =  groupAdapter.getFilter();
                filter.filter(etImport);
            }

        }
    }

    private List<FamilyMemberEntity> searchMemberList(String name, List<FamilyMemberEntity> list) {
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

    private List<FamilyGroupEntity> searchGroupList(String name, List<FamilyGroupEntity> list) {
        List<FamilyGroupEntity> results = new ArrayList();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (FamilyGroupEntity memberEntity : list) {
            String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getGroup_name()).toLowerCase();
            Matcher matcher = pattern.matcher(userName);
            if (matcher.find()) {
                results.add(memberEntity);
            }
        }
        return results;
    }
    private void shoeGroupNoFriendDialog(){
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_group_nofriend, null);
        final Dialog shoeGroupNoFriendDialog = new MyDialog(mContext, null, selectIntention);
        TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
        tv_no_member.setText(getString(R.string.test_group_friend));
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
        TextView cancelCal = (TextView) selectIntention.findViewById(R.id.tv_cal);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoeGroupNoFriendDialog.dismiss();
            }
        });
        cancelCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoeGroupNoFriendDialog.dismiss();
                return;
            }
        });
        shoeGroupNoFriendDialog.show();


    }
    private void showNoFriendDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
        final Dialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
        tv_no_member.setText(getString(R.string.text_pending_approval));
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    private List<View> initPagerView() {
        List<View> mLists = new ArrayList<>();
        View userView = LayoutInflater.from(mContext).inflate(R.layout.select_list_view_layout, null);
        final GridView userGridView = (GridView) userView.findViewById(R.id.family_grid_view);
        final ImageButton userIb = (ImageButton) userView.findViewById(R.id.ib_top);
//        memberRefreshLayout = (MySwipeRefreshLayout) userView.findViewById(R.id.swipe_refresh_layout);
        userGridView.setAdapter(memberAdapter);
        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userGridView.setSelection(0);
            }
        });

        userGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                FamilyMemberEntity familyMemberEntity = memberAdapter.getList().get(arg2);
                String userId = familyMemberEntity.getUser_id();
//                if (selectMemberList.contains(userId)) {
//                    return;
//                }
                if ("0".equals(familyMemberEntity.getFam_accept_flag())) {
                    //不是好友,提示等待接收
                    showNoFriendDialog();
                    return;
                } else {
                    CheckBox selectItem = (CheckBox) arg1.findViewById(R.id.creategroup_image_right);
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
//        memberRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                isMemberRefresh = true;
//                isFirstData = false;
//                requestData();
//            }
//        });
        userGridView.setOnTouchListener(new View.OnTouchListener() {
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
        userGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (userGridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
//                    memberRefreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
//                    memberRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userGridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
//                    memberRefreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
//                    memberRefreshLayout.setEnabled(false);
                }
            }
        });

        mLists.add(userView);
        View groupView = LayoutInflater.from(mContext).inflate(R.layout.select_list_view_layout, null);
        final GridView groupListView = (GridView) groupView.findViewById(R.id.family_grid_view);
        final ImageButton groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
//        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
        groupListView.setAdapter(groupAdapter);
        groupIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupListView.setSelection(0);
            }
        });
//        groupRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                isGroupRefresh = true;
//                isFirstData = false;
//                requestData();
//            }
//
//        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                FamilyGroupEntity groupEntity = groupAdapter.getGroupList().get(arg2);
                String groupId = groupEntity.getGroup_id();
//                if (selectGroupList.contains(groupId)) {
//                    return;
//                }
                //如果group里面有不是好友的成员
                if(false){
                    shoeGroupNoFriendDialog();
//                    return;
                }else {
                    CheckBox selectItem = (CheckBox) arg1.findViewById(R.id.creategroup_image_right);
                    if (selectItem.isChecked()) {
                        selectItem.setChecked(false);
                        groupAdapter.removeSelectData(groupId);
                        selectGroupEntityList.remove(groupEntity);
                    } else {
                        selectItem.setChecked(true);
                        groupAdapter.addSelectData(groupId);
                        selectGroupEntityList.add(groupEntity);
                    }
                }

            }
        });
        groupListView.setOnTouchListener(new View.OnTouchListener() {
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
        groupListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
//                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
//                    groupRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
//                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
//                    groupRefreshLayout.setEnabled(false);
                }
            }
        });
        mLists.add(groupView);
        return mLists;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class MyOnPageChanger implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                message_member_tv.setBackgroundResource(R.drawable.message_member_selected_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_normal_shap);
                message_group_tv.setTextColor(Color.parseColor("#666666"));
                message_member_tv.setTextColor(Color.parseColor("#ffffff"));
            } else {
                message_member_tv.setBackgroundResource(R.drawable.message_member_normal_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_selected_shap);
                message_group_tv.setTextColor(Color.parseColor("#ffffff"));
                message_member_tv.setTextColor(Color.parseColor("#666666"));
            }
        }

    }

    class FamilyPagerAdapter extends PagerAdapter {

        private List<View> mLists;

        public FamilyPagerAdapter(List<View> array) {
            this.mLists = array;
        }

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mLists.get(arg1));
            return mLists.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }
    }

    @Override
    public void requestData() {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(mContext).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResult(String response) {
                        Gson gson = new GsonBuilder().create();
                        finishReFresh();
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<FamilyMemberEntity> memberList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<FamilyMemberEntity>>() {
                            }.getType());
                            List<FamilyGroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<FamilyGroupEntity>>() {
                            }.getType());
                            Map<String, List> map = new HashMap<>();
                            if (memberList != null && memberList.size() > 0) {
                                map.put("private", memberList);
                            }
                            if (groupList != null && groupList.size() > 0) {
                                map.put("group", groupList);
                            }
                            Message.obtain(handler, GET_DATA, map).sendToTarget();
                        } catch (JSONException e) {
                            MessageUtil.showMessage(mContext, R.string.msg_action_failed);
                            finishReFresh();
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

    private void finishReFresh() {
        if (isMemberRefresh) {
//            memberRefreshLayout.setRefreshing(false);
            isMemberRefresh = false;
        }
        if (isGroupRefresh) {
//            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }


    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
        rightButton.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_invite_members;
    }

    @Override
    protected void initBottomBar() {
        // 隐藏键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_select_members));
    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent();
        Gson gson = new Gson();
        intent.putExtra("members_data", gson.toJson(selectMemberEntityList));
        intent.putExtra("groups_data", gson.toJson(selectGroupEntityList));
        if (isCreateNewGroup) {
            if ((selectMemberEntityList != null && selectMemberEntityList.size() > 0) ||
                    (selectGroupEntityList != null && selectGroupEntityList.size() > 0)) {
                intent.setClass(mContext, CreateGroupDialogActivity.class);
                intent.putExtra("jumpIndex", jumpIndex);
                startActivity(intent);
                finish();
            } else {
                showSelectDialog();
            }
        } else {
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void showSelectDialog() {
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

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.message_member_tv:
                isTabChanged = true;
                pager.setCurrentItem(0);
                if (!TextUtils.isEmpty(MemeberSearch)) {
                    etSearch.setText(MemeberSearch);
                } else {
                    etSearch.setText("");
                }
                etSearch.setSelection(etSearch.length());
                break;
            case R.id.message_group_tv:
                isTabChanged = true;
                pager.setCurrentItem(1);
                if (!TextUtils.isEmpty(GroupSearch)) {
                    etSearch.setText(GroupSearch);
                } else {
                    etSearch.setText("");
                }
                etSearch.setSelection(etSearch.length());
                break;
        }
    }
}