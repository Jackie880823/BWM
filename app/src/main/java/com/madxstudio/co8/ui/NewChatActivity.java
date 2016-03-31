package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.OrgGroupListAdapter;
import com.madxstudio.co8.adapter.OrgMemberListAdapter;
import com.madxstudio.co8.entity.FamilyGroupEntity;
import com.madxstudio.co8.entity.FamilyMemberEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by quankun on 15/5/12.
 */
public class NewChatActivity extends BaseActivity implements View.OnClickListener {
    private static final String Tag = NewChatActivity.class.getSimpleName();
    private EditText etSearch;
    private ViewPager pager;
    private TextView message_member_tv;
    private TextView message_group_tv;
    private Context mContext;
    private View serachLinear;
    private boolean isMemberRefresh, isGroupRefresh;
    private List<FamilyMemberEntity> memberEntityList;//只有成员，不包括亲人
    private List<FamilyGroupEntity> groupEntityList;
    private MySwipeRefreshLayout groupRefreshLayout, memberRefreshLayout;
    private static final int GET_DATA = 0x11;
    private OrgMemberListAdapter memberAdapter;
    private OrgGroupListAdapter groupAdapter;

    private TextView emptyGroupIv;
    private TextView emptyMemberIv;
    private View vProgress;
    private ListView groupListView;
    private String MemeberSearch;
    private String GroupSearch;
    private List<FamilyMemberEntity> allMemberList;

    @Override
    public int getLayout() {
        return R.layout.activity_new_chat;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    Map<String, List> map = (Map<String, List>) msg.obj;
                    if (memberEntityList != null) {
                        memberEntityList.clear();
                    }
                    if (groupEntityList != null) {
                        groupEntityList.clear();
                    }
                    memberEntityList = map.get("private");
                    memberAdapter.addNewData(memberEntityList);

                    groupEntityList = map.get("group");
                    groupAdapter.addNewData(groupEntityList);
                    break;
            }
            return false;
        }
    });

    @Override
    public void initView() {
        mContext = this;
        pager = getViewById(R.id.family_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        message_group_tv = getViewById(R.id.message_group_tv);
        etSearch = getViewById(R.id.et_search);
        serachLinear = getViewById(R.id.search_linear);

        memberEntityList = new ArrayList<>();
        groupEntityList = new ArrayList<>();
        allMemberList = new ArrayList<>();
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        serachLinear.setVisibility(View.GONE);
        memberAdapter = new OrgMemberListAdapter(mContext, memberEntityList, Constant.ORG_TRANSMIT_STAFF);
        groupAdapter = new OrgGroupListAdapter(mContext, groupEntityList);
        //绑定自定义适配器
        pager.setAdapter(new FamilyPagerAdapter(initPagerView()));
        pager.setOnPageChangeListener(new MyOnPageChanger());
        message_member_tv.setOnClickListener(this);
        message_group_tv.setOnClickListener(this);

        memberAdapter.showNoData(new NoFoundDataListener() {
            @Override
            public void showFoundData(String string) {
                memberRefreshLayout.setVisibility(View.GONE);
                emptyMemberIv.setVisibility(View.VISIBLE);
                emptyMemberIv.setText(String.format(mContext.getString(R.string.text_search_no_data), string));
            }

            @Override
            public void showRefreshLayout() {
                memberRefreshLayout.setVisibility(View.VISIBLE);
                emptyMemberIv.setVisibility(View.GONE);
            }
        });
        groupAdapter.showNoData(new NoFoundDataListener() {
            @Override
            public void showFoundData(String string) {
                groupRefreshLayout.setVisibility(View.GONE);
                emptyGroupIv.setVisibility(View.VISIBLE);
                emptyGroupIv.setText(String.format(mContext.getString(R.string.text_search_no_data), string));
            }

            @Override
            public void showRefreshLayout() {
                groupRefreshLayout.setVisibility(View.VISIBLE);
                emptyGroupIv.setVisibility(View.GONE);
            }
        });
        //搜索框监听器
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
                if (pager.getCurrentItem() == 0) {
                    MemeberSearch = etImport;
                } else {
                    GroupSearch = etImport;
                }
                setSearchData(etImport);
            }
        });
    }

    //搜索
    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        if (pager.getCurrentItem() == 0) {
            memberAdapter.setSerach(memberEntityList);
            Filter filter = memberAdapter.getFilter();
            filter.filter(etImport);
        } else {
            Filter filter = groupAdapter.getFilter();
            filter.filter(etImport);
        }
    }

    private void showMemberEmptyView() {
        if (memberRefreshLayout.getVisibility() == View.VISIBLE) {
            memberRefreshLayout.setVisibility(View.GONE);
        }
        emptyMemberIv.setVisibility(View.VISIBLE);
        emptyMemberIv.setText("");
    }

    private void showGroupEmptyView() {
        if (groupRefreshLayout.getVisibility() == View.VISIBLE) {
            groupRefreshLayout.setVisibility(View.GONE);
        }
        emptyGroupIv.setVisibility(View.VISIBLE);
        emptyGroupIv.setText("");
    }

    private void hideMemberEmptyView() {
        if (memberRefreshLayout.getVisibility() == View.GONE) {
            memberRefreshLayout.setVisibility(View.VISIBLE);
        }
        emptyMemberIv.setVisibility(View.GONE);
    }

    private void hideGroupEmptyView() {
        if (groupRefreshLayout.getVisibility() == View.GONE) {
            groupRefreshLayout.setVisibility(View.VISIBLE);
        }
        emptyGroupIv.setVisibility(View.GONE);
    }

    private List<View> initPagerView() {
        List<View> mLists = new ArrayList<>();
        View userView = LayoutInflater.from(mContext).inflate(R.layout.new_chat_layout, null);
        final ListView userGridView = (ListView) userView.findViewById(R.id.org_list_view);
        final ImageButton userIb = (ImageButton) userView.findViewById(R.id.ib_top);
        memberRefreshLayout = (MySwipeRefreshLayout) userView.findViewById(R.id.swipe_refresh_layout);
        emptyMemberIv = (TextView) userView.findViewById(R.id.message_search);
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
                if ("0".equals(familyMemberEntity.getFam_accept_flag())) {

                } else {
                    Intent intent = new Intent(mContext, MessageChatActivity.class);
                    intent.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_MEMBER);
                    intent.putExtra(UserEntity.EXTRA_GROUP_ID, familyMemberEntity.getGroup_id());
                    intent.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, familyMemberEntity.getUser_given_name());
                    startActivity(intent);
                }
            }
        });
        memberRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isMemberRefresh = true;
                getData();
            }
        });
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
                    memberRefreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    memberRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userGridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                    memberRefreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    memberRefreshLayout.setEnabled(false);
                }
            }
        });

        mLists.add(userView);
        View groupView = LayoutInflater.from(mContext).inflate(R.layout.new_chat_layout, null);
        groupListView = (ListView) groupView.findViewById(R.id.org_list_view);
        final ImageButton groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
        emptyGroupIv = (TextView) groupView.findViewById(R.id.message_search);
        groupListView.setAdapter(groupAdapter);
        groupIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupListView.setSelection(0);
            }
        });
        groupRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isGroupRefresh = true;
                getData();
            }

        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                FamilyGroupEntity groupEntity = groupAdapter.getList().get(arg2);
                Intent intent = new Intent(mContext, MessageChatActivity.class);
                intent.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_GROUP);
                intent.putExtra(Constant.MESSAGE_CHART_GROUP_ID, groupEntity.getGroup_id());
                intent.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, groupEntity.getGroup_name());
                intent.putExtra(Constant.GROUP_DEFAULT, groupEntity.getGroup_default());
                startActivity(intent);
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
                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
                    groupRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
                    groupRefreshLayout.setEnabled(false);
                }
            }
        });
        mLists.add(groupView);
        return mLists;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
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
                rightButton.setImageResource(R.drawable.org_search_icon);
                rightSearchButton.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(MemeberSearch)) {
                    etSearch.setText(MemeberSearch);
                } else {
                    etSearch.setText("");
                }
                etSearch.setSelection(etSearch.length());
            } else {
                message_member_tv.setBackgroundResource(R.drawable.message_member_normal_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_selected_shap);
                message_group_tv.setTextColor(Color.parseColor("#ffffff"));
                message_member_tv.setTextColor(Color.parseColor("#666666"));
                rightButton.setImageResource(R.drawable.add_group_icon);
                rightSearchButton.setVisibility(View.VISIBLE);
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
                            showMemberEmptyView();
                            showGroupEmptyView();
                        }
                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
                            showMemberEmptyView();
                            showGroupEmptyView();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<FamilyMemberEntity> memberList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<FamilyMemberEntity>>() {
                            }.getType());
                            List<FamilyGroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<FamilyGroupEntity>>() {
                            }.getType());
                            Map<String, List> map = new HashMap<>();
                            if (memberList != null && memberList.size() > 0) {
                                hideMemberEmptyView();
                                List<FamilyMemberEntity> list = new ArrayList<>();
                                allMemberList.clear();
                                for (FamilyMemberEntity memberEntity : memberList) {
                                    String tree_type = memberEntity.getTree_type();
                                    if (Constant.FAMILY_PARENT.equalsIgnoreCase(tree_type) || Constant.FAMILY_CHILDREN.equalsIgnoreCase(tree_type)
                                            || Constant.FAMILY_SIBLING.equalsIgnoreCase(tree_type)) {
                                        list.add(memberEntity);
                                    }
                                    if (!"0".equals(memberEntity.getFam_accept_flag())) {
                                        allMemberList.add(memberEntity);
                                    }
                                }
                                map.put("private", list);
                            } else {
                                showMemberEmptyView();
                            }
                            if (groupList != null && groupList.size() > 0) {
                                hideGroupEmptyView();
                                map.put("group", groupList);
                            } else {
                                showGroupEmptyView();
                            }
                            if (map.size() > 0) {
                                Message.obtain(handler, GET_DATA, map).sendToTarget();
                            }
                        } catch (JSONException e) {
                            finishReFresh();
                            showGroupEmptyView();
                            showMemberEmptyView();
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

    @Override
    public void requestData() {

    }

    private void finishReFresh() {
        if (isMemberRefresh) {
            memberRefreshLayout.setRefreshing(false);
            isMemberRefresh = false;
        }
        if (isGroupRefresh) {
            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText("New Chat");
    }

    @Override
    protected void titleRightEvent() {
        if (pager.getCurrentItem() == 0) {
            if (serachLinear.getVisibility() == View.VISIBLE) {
                serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.org_up_from_bottom));
                serachLinear.setVisibility(View.GONE);
            } else {
                serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_from_top));
                serachLinear.setVisibility(View.VISIBLE);
            }
        } else {
            Intent intent = new Intent(mContext, SelectMemberActivity.class);
            intent.putExtra(Constant.SELECT_MEMBER_NORMAL_DATA, (Serializable) allMemberList);
            startActivity(intent);
        }
    }

    @Override
    protected void titleRightSearchEvent() {
        super.titleRightSearchEvent();
        if (pager.getCurrentItem() == 1) {
            if (serachLinear.getVisibility() == View.VISIBLE) {
                serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.org_up_from_bottom));
                serachLinear.setVisibility(View.GONE);
            } else {
                serachLinear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_from_top));
                serachLinear.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        if (pager.getCurrentItem() == 0) {
            rightButton.setImageResource(R.drawable.org_search_icon);
            rightSearchButton.setVisibility(View.GONE);
        } else {
            rightButton.setImageResource(R.drawable.add_group_icon);
            rightSearchButton.setVisibility(View.VISIBLE);
        }
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
                pager.setCurrentItem(0);
                break;
            case R.id.message_group_tv:
                pager.setCurrentItem(1);
                break;
        }
    }
}