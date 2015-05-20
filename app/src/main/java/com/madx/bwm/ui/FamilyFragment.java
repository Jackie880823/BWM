package com.madx.bwm.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MyFamilyAdapter;
import com.madx.bwm.entity.FamilyGroupEntity;
import com.madx.bwm.entity.FamilyMemberEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MslToast;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.util.PinYin4JUtil;
import com.madx.bwm.widget.CircularNetworkImage;
import com.madx.bwm.widget.MyDialog;
import com.madx.bwm.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by quankun on 15/5/12.
 */
public class FamilyFragment extends BaseFragment<MainActivity> implements View.OnClickListener {
    private EditText etSearch;
    private ViewPager pager;
    private TextView message_member_tv;
    private TextView message_group_tv;
    private Dialog showSelectDialog;
    private Context mContext;
    private boolean isMemberRefresh, isGroupRefresh;
    private List<FamilyMemberEntity> memberEntityList;
    private List<FamilyMemberEntity> memberList;
    private List<FamilyMemberEntity> moreMemberList;
    private List<FamilyGroupEntity> groupEntityList;
    private MySwipeRefreshLayout groupRefreshLayout, memberRefreshLayout;
    private ProgressDialog mProgressDialog;
    private static final int GET_DATA = 0x11;
    private MyFamilyAdapter memberAdapter;
    private FamilyGroupAdapter groupAdapter;
    public static final String FAMILY_TREE = "family_tree";
    public static final String FAMILY_PARENT = "parent";
    public static final String FAMILY_CHILDREN = "children";
    public static final String FAMILY_SIBLING = "sibling";
    public static final String FAMILY_SPOUSE = "spouse";
    public static final String FAMILY_MORE_MEMBER = "More";
    public static final String FAMILY_HIDE_MEMBER = "Hide";

    public static FamilyFragment newInstance(String... params) {
        return createInstance(new FamilyFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_my_family;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA:
                    Map<String, List> map = (Map<String, List>) msg.obj;
                    memberEntityList.clear();
                    memberList.clear();
                    moreMemberList.clear();
                    memberEntityList = map.get("private");
                    if (memberEntityList != null && memberEntityList.size() > 0) {
                        FamilyMemberEntity member = new FamilyMemberEntity();
                        member.setUser_given_name(FAMILY_TREE);
                        member.setUser_id(FAMILY_TREE);
                        memberList.add(member);
                        for (FamilyMemberEntity memberEntity : memberEntityList) {
                            String tree_type = memberEntity.getTree_type().trim();
                            if (FAMILY_PARENT.equals(tree_type) || FAMILY_CHILDREN.equals(tree_type)
                                    || FAMILY_SIBLING.equals(tree_type) || FAMILY_SPOUSE.equals(tree_type)) {
                                memberList.add(memberEntity);
                            } else {
                                moreMemberList.add(memberEntity);
                            }
                        }
                        FamilyMemberEntity familyMemberEntity = new FamilyMemberEntity();
                        familyMemberEntity.setUser_given_name(FAMILY_MORE_MEMBER);
                        familyMemberEntity.setUser_id(FAMILY_MORE_MEMBER);
                        memberList.add(familyMemberEntity);
                        FamilyMemberEntity familyMember = new FamilyMemberEntity();
                        familyMember.setUser_given_name(FAMILY_HIDE_MEMBER);
                        familyMember.setUser_id(FAMILY_HIDE_MEMBER);
                        moreMemberList.add(familyMember);
                    }
                    memberAdapter.addNewData(memberList);

                    groupEntityList = map.get("group");
                    groupAdapter.addData(groupEntityList);
                    break;
            }

        }
    };

    @Override
    public void initView() {
        mContext = getActivity();
        pager = getViewById(R.id.family_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        message_group_tv = getViewById(R.id.message_group_tv);
        etSearch = getViewById(R.id.et_search);
        memberEntityList = new ArrayList<>();
        groupEntityList = new ArrayList<>();
        memberList = new ArrayList<>();
        moreMemberList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
        mProgressDialog.show();

        memberAdapter = new MyFamilyAdapter(mContext, memberEntityList);
        groupAdapter = new FamilyGroupAdapter(groupEntityList);
        //绑定自定义适配器
        pager.setAdapter(new FamilyPagerAdapter(initPagerView()));
        pager.setOnPageChangeListener(new MyOnPageChanger());
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                showSelectDialog();
                return false;
            }
        });
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
                setSearchData(etImport);
                //etSearch.setText("");
            }
        });
    }

    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        if (pager.getCurrentItem() == 0) {
            List<FamilyMemberEntity> familyMemberEntityList;
            if (TextUtils.isEmpty(etImport)) {
                familyMemberEntityList = memberList;
            } else {
                familyMemberEntityList = searchMemberList(etImport, memberEntityList);
            }
            memberAdapter.addNewData(familyMemberEntityList);
        } else {
            List<FamilyGroupEntity> familyGroupEntityList;
            if (TextUtils.isEmpty(etImport)) {
                familyGroupEntityList = groupEntityList;
            } else {
                familyGroupEntityList = searchGroupList(etImport, groupEntityList);
            }
            groupAdapter.addData(familyGroupEntityList);
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

    private List<View> initPagerView() {
        List<View> mLists = new ArrayList<>();
        View userView = LayoutInflater.from(mContext).inflate(R.layout.family_list_view_layout, null);
        final GridView userGridView = (GridView) userView.findViewById(R.id.family_grid_view);
        final ImageButton userIb = (ImageButton) userView.findViewById(R.id.ib_top);
        memberRefreshLayout = (MySwipeRefreshLayout) userView.findViewById(R.id.swipe_refresh_layout);
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
                if (FamilyFragment.FAMILY_TREE.equals(userId)) {
                    getUrl();
                } else if (FamilyFragment.FAMILY_MORE_MEMBER.equals(userId)) {
                    memberAdapter.addMoreData(moreMemberList);
                } else if (FamilyFragment.FAMILY_HIDE_MEMBER.equals(userId)) {
                    memberAdapter.addNewData(memberList);
                } else {
                    if ("0".equals(familyMemberEntity.getFam_accept_flag())) {
                        //不是好友,提示等待接收
                        MslToast.getInstance(mContext).showLongToast(getString(R.string.text_awaiting_for_approval));
                        return;
                    } else {
                        //member, 跳转到个人资料页面需要
                        //put请求消除爱心
                        if ("".equals(familyMemberEntity.getMiss())) {
                            updateMiss(familyMemberEntity.getUser_id());
                            arg0.findViewById(R.id.myfamily_image_right).setVisibility(View.GONE);
                        }
                        Intent intent = new Intent(getActivity(), FamilyProfileActivity.class);
                        intent.putExtra("member_id", familyMemberEntity.getUser_id());
                        startActivity(intent);
                    }
                }
            }
        });
        memberRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isMemberRefresh = true;
                requestData();
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
        View groupView = LayoutInflater.from(mContext).inflate(R.layout.family_list_view_layout, null);
        final GridView groupListView = (GridView) groupView.findViewById(R.id.family_grid_view);
        final ImageButton groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
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
                requestData();
            }

        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupId", groupAdapter.getGroupList().get(arg2).getGroup_id());
                intent.putExtra("titleName", groupAdapter.getGroupList().get(arg2).getGroup_name());
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

    public void updateMiss(String member_id) {
        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", member_id);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_UPDATE_MISS, MainActivity.getUser().getUser_id());
        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).put(requestInfo, new HttpCallback() {
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
                            if ("200".equals(jsonObject.getString("response_status_code"))) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
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

    private void getUrl() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).get(String.format(Constant.API_FAMILY_TREE, MainActivity.getUser().getUser_id()), null, new HttpCallback() {
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
                            if ("Success".equals(jsonObject.getString("response_status"))) {
                                String urlString = jsonObject.getString("filePath");
                                if (!TextUtils.isEmpty(urlString)) {
                                    getPdf(urlString);
                                }
                            } else {
                                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                                mProgressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                            mProgressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                        mProgressDialog.dismiss();
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

    public void getPdf(String urlString) {

        final String target = FileUtil.getCacheFilePath(getActivity()) + String.format("/cache_%s.pdf", "" + System.currentTimeMillis());

        new HttpTools(getActivity()).download(urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                File file = new File(target);
                if (file.exists()) {
                    mProgressDialog.dismiss();
                    Uri path1 = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path1, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                mProgressDialog.dismiss();
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
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
                message_group_tv.setTextColor(Color.parseColor("#878787"));
                message_member_tv.setTextColor(Color.parseColor("#ffffff"));
            } else {
                message_member_tv.setBackgroundResource(R.drawable.message_member_normal_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_selected_shap);
                message_group_tv.setTextColor(Color.parseColor("#ffffff"));
                message_member_tv.setTextColor(Color.parseColor("#878787"));
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

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);

        showSelectDialog = new MyDialog(getParentActivity(), null, selectIntention);

        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);

        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNewMembersActivity.class));
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    @Override
    public void requestData() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResult(String response) {
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
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
                            MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                            finishReFresh();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
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
            memberRefreshLayout.setRefreshing(false);
            isMemberRefresh = false;
        }
        if (isGroupRefresh) {
            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }

    class FamilyGroupAdapter extends BaseAdapter {
        List<FamilyGroupEntity> groupList;

        public FamilyGroupAdapter(List<FamilyGroupEntity> groupList) {
            this.groupList = groupList;
        }

        public void addData(List<FamilyGroupEntity> list) {
            if (null == list || list.size() == 0) {
                return;
            }
            groupList.clear();
            groupList.addAll(list);
            notifyDataSetChanged();
        }

        public List<FamilyGroupEntity> getGroupList() {
            return groupList;
        }

        @Override
        public int getCount() {
            return groupList.size();
        }

        @Override
        public Object getItem(int position) {
            return groupList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_for_myfamily, null);
                viewHolder = new ViewHolder();
                viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.myfamily_image_main);
                viewHolder.textName = (TextView) convertView.findViewById(R.id.myfamily_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FamilyGroupEntity familyGroupEntity = groupList.get(position);
            viewHolder.textName.setText(familyGroupEntity.getGroup_name());
            VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO,
                    familyGroupEntity.getGroup_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            return convertView;
        }

        class ViewHolder {
            CircularNetworkImage imageMain;
            TextView textName;
        }
    }

    @Override
    public void onClick(View v) {
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