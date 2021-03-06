package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.madxstudio.co8.adapter.InviteGroupAdapter;
import com.madxstudio.co8.adapter.InviteMemberAdapter;
import com.madxstudio.co8.entity.FamilyGroupEntity;
import com.madxstudio.co8.entity.FamilyMemberEntity;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
//    private ProgressDialog mProgressDialog;
    private static final int GET_DATA = 0x11;
    private InviteMemberAdapter memberAdapter;
    private InviteGroupAdapter groupAdapter;
    private List<String> selectMemberList;
    private List<String> selectGroupList;
    private int type;
    private int groupType;
    private int selectNewData;
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

    private View message_member_view, message_group_view;
    private View serachLinear;
    /**
     * wing added
     */
    ListView userGridView;
    TextView userNoDataView;
    ListView groupGridView;
    TextView groupNoDataView;
    /**
     * end
     */

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

                                } else {
                                    if (isFirstData && selectNewData == 0) {
                                        selectMemberEntityList.add(memberEntity);
                                    }
                                }
                            }
                            searchmemberList.addAll(memberList);
                            memberAdapter.addNewData(memberList);
                        } else {
                            if (selectNewData == 0) {
                                for (FamilyMemberEntity memberEntity : memberEntityList) {
                                    if (memberEntity.getUser_id().equals("665")) {
                                        Log.i("cccccc", memberEntity.getUser_given_name());
                                    }
                                    if (selectMemberList.contains(memberEntity.getUser_id()) && isFirstData) {
                                        selectMemberEntityList.add(memberEntity);
                                    }
                                }
                                Log.i("cccccddd", selectMemberEntityList.size() + "");

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


                    //wing added for no data
                    if (memberNull) {
                        userGridView.setVisibility(View.GONE);
                        userNoDataView.setVisibility(View.VISIBLE);
                    } else {
                        userGridView.setVisibility(View.VISIBLE);
                        userNoDataView.setVisibility(View.GONE);
                    }

                    if (groupNull) {
                        groupGridView.setVisibility(View.GONE);
                        groupNoDataView.setVisibility(View.VISIBLE);
                    } else {
                        groupGridView.setVisibility(View.VISIBLE);
                        groupNoDataView.setVisibility(View.GONE);
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
        selectNewData = intent.getIntExtra("selectNewData", 0);//如果是1，回传的数据只包含新选
        if (groupType == -1) {
            groupType = type;
        }
        List<FamilyMemberEntity> memberSelectList = null;
        if (memberData != null) {
            memberSelectList = new Gson().fromJson(memberData, new TypeToken<ArrayList<FamilyMemberEntity>>() {
            }.getType());
            Log.i("memberSelectList====1", memberSelectList.size() + "");
        }
        selectMemberList = new ArrayList<>();
        selectGroupList = new ArrayList<>();
        selectMemberEntityList = new ArrayList<>();
        selectGroupEntityList = new ArrayList<>();
        if (null != memberSelectList && memberSelectList.size() > 0) {
            for (FamilyMemberEntity userEntity : memberSelectList) {
                //为什么把传过来的member添加进来？
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
        message_member_view = getViewById(R.id.message_member_view);
        message_group_view = getViewById(R.id.message_group_view);
        serachLinear = getViewById(R.id.search_linear);
        etSearch = getViewById(R.id.et_search);
        memberEntityList = new ArrayList<>();
        groupEntityList = new ArrayList<>();
        serachLinear.setVisibility(View.GONE);

//        mProgressDialog = new ProgressDialog(mContext, getString(R.string.text_loading));
//        mProgressDialog.show();

        memberAdapter = new InviteMemberAdapter(mContext, memberEntityList, selectMemberList);
        groupAdapter = new InviteGroupAdapter(mContext, groupEntityList, selectGroupList);

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
                    Filter filter = memberAdapter.getFilter();
                    filter.filter(MemeberSearch);
//                    familyMemberEntityList = searchMemberList(MemeberSearch, memberList);
                } else {

//                    familyMemberEntityList = searchMemberList(MemeberSearch, memberEntityList);
                    memberAdapter.setSerchList(memberEntityList);
                    Filter filter = memberAdapter.getFilter();
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
                Filter filter = groupAdapter.getFilter();
                filter.filter(etImport);
            }

        }
    }

    private void shoeGroupNoFriendDialog(final View arg1, final String groupId, final FamilyGroupEntity groupEntity) {
        final LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_group_nofriend, null);
        final MyDialog shoeGroupNoFriendDialog = new MyDialog(mContext, null, selectIntention);
        TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
        tv_no_member.setText(getString(R.string.test_group_friend));
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);//确定
        TextView cancelCal = (TextView) selectIntention.findViewById(R.id.tv_cal);//取消
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoeGroupNoFriendDialog.dismiss();
                CheckBox selectItem = (CheckBox) arg1.findViewById(R.id.creategroup_image_right);
                selectItem.setChecked(true);
                groupAdapter.addSelectData(groupId);
                selectGroupEntityList.add(groupEntity);
            }
        });
        cancelCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoeGroupNoFriendDialog.dismiss();
                CheckBox selectItem = (CheckBox) arg1.findViewById(R.id.creategroup_image_right);
                if (selectItem.isChecked()) {
                    selectItem.setChecked(false);
                    groupAdapter.removeSelectData(groupId);
                    selectGroupEntityList.remove(groupEntity);
                }
                return;
            }
        });
        shoeGroupNoFriendDialog.show();


    }

    private void showNoFriendDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
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
        /**wing modified for no data*/
        userGridView = (ListView) userView.findViewById(R.id.family_grid_view);
        userNoDataView = (TextView) userView.findViewById(R.id.data_null);
        userNoDataView.setText(R.string.no_member);
        /**end*/
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
        /**wing modified for no data*/
        groupGridView = (ListView) groupView.findViewById(R.id.family_grid_view);
        groupNoDataView = (TextView) groupView.findViewById(R.id.data_null);
        groupNoDataView.setText(R.string.no_group);
        /** end*/
        final ImageButton groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
//        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
        groupGridView.setAdapter(groupAdapter);
        groupIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupGridView.setSelection(0);
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
        groupGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                FamilyGroupEntity groupEntity = groupAdapter.getGroupList().get(arg2);
                String groupId = groupEntity.getGroup_id();
                //如果group里面有不是好友的成员
                if ("0".equals(groupEntity.getFriend_flag())) {
                    shoeGroupNoFriendDialog(arg1, groupId, groupEntity);
                } else {
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
        groupGridView.setOnTouchListener(new View.OnTouchListener() {
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
        groupGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (groupGridView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
//                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
//                    groupRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (groupGridView.getFirstVisiblePosition() == 0) {
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
//                message_member_tv.setBackgroundResource(R.drawable.message_member_selected_shap);
//                message_group_tv.setBackgroundResource(R.drawable.message_group_normal_shap);
                message_member_view.setVisibility(View.VISIBLE);
                message_group_view.setVisibility(View.INVISIBLE);
                message_group_tv.setTextColor(ContextCompat.getColor(mContext, R.color.message_comment));
                message_member_tv.setTextColor(ContextCompat.getColor(mContext, R.color.tab_color_press1));
                if (!TextUtils.isEmpty(MemeberSearch)) {
                    etSearch.setText(MemeberSearch);
                } else {
                    etSearch.setText("");
                }
                etSearch.setSelection(etSearch.length());
            } else {
                message_group_tv.setTextColor(ContextCompat.getColor(mContext, R.color.tab_color_press1));
                message_member_tv.setTextColor(ContextCompat.getColor(mContext, R.color.message_comment));
                message_member_view.setVisibility(View.INVISIBLE);
                message_group_view.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(GroupSearch)) {
                    etSearch.setText(GroupSearch);
                } else {
                    etSearch.setText("");
                }
                etSearch.setSelection(etSearch.length());
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

    private boolean memberNull;
    private boolean groupNull;

    @Override
    public void requestData() {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }

        new HttpTools(mContext).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null, Tag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                Gson gson = new GsonBuilder().create();
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<FamilyMemberEntity> memberList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<FamilyMemberEntity>>() {
                    }.getType());
                    List<FamilyGroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<FamilyGroupEntity>>() {
                    }.getType());
                    Map<String, List> map = new HashMap<>();
                    if (memberList != null && memberList.size() > 0) {
                        List<FamilyMemberEntity> list = new ArrayList<>();
                        for (FamilyMemberEntity memberEntity : memberList) {
                            if (!"0".equals(memberEntity.getAdded_flag())) {
                                list.add(memberEntity);
                            }
                        }
                        if (list.size() > 0) {
                            map.put("private", list);
                            memberNull = false;
                        }
//                        map.put("private", memberList);
//                        memberNull = false;
                    } else {
                        memberNull = true;
                    }
                    if (groupList != null && groupList.size() > 0) {
                        map.put("group", groupList);
                        groupNull = false;
                    } else {
                        groupNull = true;
                    }
                    Message.obtain(handler, GET_DATA, map).sendToTarget();
                } catch (JSONException e) {
                    MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                    e.printStackTrace();
                } finally {
                    finishReFresh();
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
        rightSearchButton.setVisibility(View.VISIBLE);
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
        for (FamilyMemberEntity familyMemberEntity : selectMemberEntityList) {
            Log.i("familyMemberEntity===", familyMemberEntity.getUser_id());
        }
        intent.putExtra("members_data", gson.toJson(selectMemberEntityList));
//        Log.i("members_data====", gson.toJson(selectMemberEntityList));
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
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
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
                break;
            case R.id.message_group_tv:
                isTabChanged = true;
                pager.setCurrentItem(1);
                break;
        }
    }
}