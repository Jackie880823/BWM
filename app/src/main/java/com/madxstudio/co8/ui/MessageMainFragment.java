package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.MessageGroupFragmentAdapter;
import com.madxstudio.co8.adapter.MessagePrivateListAdapter;
import com.madxstudio.co8.entity.GroupMessageEntity;
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.ui.add.AddMembersActivity;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.widget.InteractivePopupWindow;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/4/17.
 */
public class MessageMainFragment extends BaseFragment<MainActivity> implements View.OnClickListener {
    private ViewPager pager;
    private TextView message_member_tv;
    private TextView message_group_tv;
    private View msg_member_relative;
    private View msg_group_relative;
    private View group_red_point;
    private Dialog showSelectDialog;
    private static final int GET_DELAY_ADD_PHOTO = 0x30;
    private InteractivePopupWindow popupWindowAddPhoto;
    private static final String MESSAGE_DATA = "data";
    private static final String MESSAGE_UNREAD_NUM = "num";

    public static MessageMainFragment newInstance(String... params) {

        return createInstance(new MessageMainFragment());
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_message_list;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        mContext = getActivity();
        TAG = mContext.getClass().getSimpleName();
        pager = getViewById(R.id.message_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        msg_member_relative = getViewById(R.id.msg_member_relative);
        message_group_tv = getViewById(R.id.message_group_tv);
        msg_group_relative = getViewById(R.id.msg_group_relative);
        group_red_point = getViewById(R.id.group_red_point);
        //绑定自定义适配器
        pager.setAdapter(new FamilyPagerAdapter(initPagerView()));
        pager.setOnPageChangeListener(new MyOnPageChanger());
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                showSelectDialog();
//                Intent intent = new Intent();
//                if (pager.getCurrentItem() == 0) {
//                    intent.setClass(getActivity(), AddNewMembersActivity.class);
//                } else {
//                    intent.setClass(getActivity(), CreateGroupActivity.class);
//                }
//                startActivity(intent);
                return false;
            }
        });
        msg_member_relative.setOnClickListener(this);
        msg_group_relative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_member_relative:
                pager.setCurrentItem(0);
                break;
            case R.id.msg_group_relative:
                pager.setCurrentItem(1);
                break;
        }
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
                msg_member_relative.setBackgroundResource(R.drawable.message_member_selected_shap);
                msg_group_relative.setBackgroundResource(R.drawable.message_group_normal_shap);
                message_group_tv.setTextColor(Color.parseColor("#666666"));
                message_member_tv.setTextColor(Color.parseColor("#ffffff"));
            } else {
                msg_member_relative.setBackgroundResource(R.drawable.message_member_normal_shap);
                msg_group_relative.setBackgroundResource(R.drawable.message_group_selected_shap);
                message_group_tv.setTextColor(Color.parseColor("#ffffff"));
                message_member_tv.setTextColor(Color.parseColor("#666666"));
            }
        }

    }

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);

        showSelectDialog = new MyDialog(getParentActivity(), null, selectIntention);
        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddMembersActivity.class));
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
                intent.putExtra("isCreateNewGroup", true);
                intent.putExtra("jumpIndex", 1);
                startActivity(intent);
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

    @Override
    public void requestData() {

    }

    private ListView userListView;
    private ImageButton userIb;
    private MySwipeRefreshLayout userRefreshLayout;
    private MessagePrivateListAdapter privateAdapter;
    private Context mContext;
    private List<PrivateMessageEntity> userEntityList;
    private boolean isUserRefresh = false;
    private int startIndex = 1;
    private boolean isPullData = false;
    private LinearLayout emptyMemberMessageLinear;
    private ImageView emptyMemberMessageIv;
    private TextView emptyMemberMessageTv;
    private View vProgress;
    private ListView groupListView;
    private ImageButton groupIb;
    private MySwipeRefreshLayout groupRefreshLayout;
    private MessageGroupFragmentAdapter messageGroupAdapter;
    private List<GroupMessageEntity> userEntityListGroup;
    private boolean isGroupRefresh = false;
    private int startIndexGroup = 1;
    private boolean isPullDataGroup = false;
    private LinearLayout emptyGroupMessageLinear;
    private ImageView emptyGroupMessageIv;
    private TextView emptyGroupMessageTv;
    private String TAG;
    private static final int DEF_DATA_NUM = 20;

    @Override
    public void onStart() {
        super.onStart();
        getData(0);
        getDataGroup(0);
    }

    private List<View> initPagerView() {
        List<View> mLists = new ArrayList<>();
        View userView = LayoutInflater.from(mContext).inflate(R.layout.message_list_view_layout, null);
        userListView = (ListView) userView.findViewById(R.id.message_listView);
        userIb = (ImageButton) userView.findViewById(R.id.ib_top);
        userRefreshLayout = (MySwipeRefreshLayout) userView.findViewById(R.id.swipe_refresh_layout);
        emptyMemberMessageLinear = (LinearLayout) userView.findViewById(R.id.message_main_empty_linear);
        emptyMemberMessageIv = (ImageView) userView.findViewById(R.id.message_main_image_empty);
        emptyMemberMessageTv = (TextView) userView.findViewById(R.id.message_main_text_empty);
        vProgress = userView.findViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        userEntityList = new ArrayList<>();
        privateAdapter = new MessagePrivateListAdapter(mContext, userEntityList);
        userListView.setAdapter(privateAdapter);
        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListView.setSelection(0);
            }
        });
        userRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    MessageUtil.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
                    userRefreshLayout.setRefreshing(false);
                    return;
                }
                startIndex = 1;
                isUserRefresh = true;
                getData(0);
            }

        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                arg1.findViewById(R.id.tv_num).setVisibility(View.GONE);
                privateAdapter.getmUserEntityList().get(arg2).setUnread("0");
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("groupId", privateAdapter.getmUserEntityList().get(arg2).getGroup_id());
                intent.putExtra("titleName", privateAdapter.getmUserEntityList().get(arg2).getUser_given_name());
                startActivity(intent);
            }
        });

        userListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (userListView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userListView.getFirstVisiblePosition() == 0) {
                    userRefreshLayout.setEnabled(true);
                    userIb.setVisibility(View.GONE);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    userRefreshLayout.setEnabled(false);
                }
                int adapterCount = privateAdapter.getCount();
                if (adapterCount == DEF_DATA_NUM * startIndex && userListView.getFirstVisiblePosition() < (adapterCount - 5)
                        && (userListView.getLastVisiblePosition() > (adapterCount - 5)) && !isPullData) {
                    getData(startIndex);
                    isPullData = true;
                }
            }
        });
//        getData(0);

        mLists.add(userView);
        View groupView = LayoutInflater.from(mContext).inflate(R.layout.message_list_view_layout, null);
        groupListView = (ListView) groupView.findViewById(R.id.message_listView);
        groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
        emptyGroupMessageLinear = (LinearLayout) groupView.findViewById(R.id.message_main_empty_linear);
        emptyGroupMessageIv = (ImageView) groupView.findViewById(R.id.message_main_image_empty);
        emptyGroupMessageTv = (TextView) groupView.findViewById(R.id.message_main_text_empty);
        userEntityListGroup = new ArrayList<>();
        messageGroupAdapter = new MessageGroupFragmentAdapter(mContext, userEntityListGroup);
        groupListView.setAdapter(messageGroupAdapter);
        groupIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupListView.setSelection(0);
            }
        });
        groupRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    MessageUtil.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
                    groupRefreshLayout.setRefreshing(false);
                    return;
                }
                startIndexGroup = 1;
                isGroupRefresh = true;
                getDataGroup(0);
            }

        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupId", messageGroupAdapter.getmGroupList().get(arg2).getGroup_id());
                intent.putExtra("titleName", messageGroupAdapter.getmGroupList().get(arg2).getGroup_name());
                intent.putExtra(Constant.GROUP_DEFAULT, messageGroupAdapter.getmGroupList().get(arg2).getGroup_default());
                arg1.findViewById(R.id.tv_num).setVisibility(View.GONE);//服务器会消除。本地直接直接消除。
                messageGroupAdapter.getmGroupList().get(arg2).setUnread("0");
                startActivity(intent);
            }
        });
        groupListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupRefreshLayout.setEnabled(true);
                    groupIb.setVisibility(View.GONE);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
                    groupRefreshLayout.setEnabled(false);
                }
                int adapterCount = messageGroupAdapter.getCount();
                if (adapterCount == DEF_DATA_NUM * startIndexGroup && groupListView.getFirstVisiblePosition() < (adapterCount - 5)
                        && (groupListView.getLastVisiblePosition() > (adapterCount - 5)) && !isPullDataGroup) {
                    getDataGroup(startIndexGroup);
                    isPullDataGroup = true;
                }
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getDataGroup(0);
//            }
//        }, 1500);
        mLists.add(groupView);
        return mLists;
    }

    private void getData(int beginIndex) {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            MessageUtil.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
            userFinishReFresh();
            return;
        }
        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, Object> params = new HashMap<>();
        params.put("limit", DEF_DATA_NUM + "");
        int start = beginIndex * DEF_DATA_NUM;
        params.put("start", start + "");
        requestInfo.putAllParams(params);
        Log.i(TAG, beginIndex + "");
        requestInfo.url = String.format(Constant.API_GET_CHAT_MESSAGE_LIST, MainActivity.getUser().getUser_id(), "member");
        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).get(requestInfo, TAG, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                        if (vProgress != null) {
                            vProgress.setVisibility(View.GONE);
                        }
                        isPullData = false;
                    }

                    @Override
                    public void onResult(String response) {
                        userFinishReFresh();
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
                            showMemberEmptyView();
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<PrivateMessageEntity> userList = gson.fromJson(jsonObject.getString("member"), new TypeToken<ArrayList<PrivateMessageEntity>>() {
                            }.getType());
                            String totalUnread = jsonObject.optString("memberUnread", "");//私聊的消息总共有多少未读"groupUnread","memberUnread","totalUnread"
                            Map<String, Object> map = new HashMap<>();
                            map.put(MESSAGE_DATA, userList);
                            map.put(MESSAGE_UNREAD_NUM, totalUnread);
                            if (isPullData) {
                                if (userList != null && userList.size() == DEF_DATA_NUM) {
                                    startIndex++;
                                }
                                Message.obtain(handler, GET_PULL_DATA, map).sendToTarget();
                            } else {
                                if (null == userList || userList.size() == 0) {
                                    showMemberEmptyView();
                                } else {
                                    hideMemberEmptyView();
                                }
                                Message.obtain(handler, GET_NEW_DATA, map).sendToTarget();
                            }
                        } catch (JSONException e) {
                            if (isPullData) {
                                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                            } else {
                                showMemberEmptyView();
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                        userFinishReFresh();
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

    private static final int GET_NEW_DATA = 0x11;
    private static final int GET_PULL_DATA = 0x12;
    private static final int GET_NEW_DATA_GROUP = 0x13;
    private static final int GET_PULL_DATA_GROUP = 0x14;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_NEW_DATA:
                    Map<String, Object> pullPrivateMapNew = (Map) msg.obj;
                    List<PrivateMessageEntity> userEntityList1 = (List<PrivateMessageEntity>) pullPrivateMapNew.get(MESSAGE_DATA);
                    String unReadNumNewPrivate = (String) pullPrivateMapNew.get(MESSAGE_UNREAD_NUM);
                    if (TextUtils.isEmpty(unReadNumNewPrivate)||"0".equalsIgnoreCase(unReadNumNewPrivate)) {
//                        ,,
                    }
                    privateAdapter.NewUserEntityData(userEntityList1);
                    break;
                case GET_PULL_DATA:
                    Map<String, Object> pullPrivateMap = (Map) msg.obj;
                    List<PrivateMessageEntity> userEntityListPull1 = (List<PrivateMessageEntity>) pullPrivateMap.get(MESSAGE_DATA);
                    String unReadNumPullPrivate = (String) pullPrivateMap.get(MESSAGE_UNREAD_NUM);
                    if (TextUtils.isEmpty(unReadNumPullPrivate)||"0".equalsIgnoreCase(unReadNumPullPrivate)) {
//                        ,,
                    }
//                    List<PrivateMessageEntity> userEntityListPull1 = (List<PrivateMessageEntity>) msg.obj;
                    privateAdapter.AddUserEntityData(userEntityListPull1);
                    break;
                case GET_NEW_DATA_GROUP:
                    Map<String, Object> newGroupMap = (Map) msg.obj;
                    List<GroupMessageEntity> userEntityList = (List<GroupMessageEntity>) newGroupMap.get(MESSAGE_DATA);
                    String unReadNum = (String) newGroupMap.get(MESSAGE_UNREAD_NUM);
                    if (TextUtils.isEmpty(unReadNum)||"0".equalsIgnoreCase(unReadNum)) {
                        group_red_point.setVisibility(View.GONE);
                    } else {
                        group_red_point.setVisibility(View.VISIBLE);
                    }
                    messageGroupAdapter.NewGroupEntityData(userEntityList);
                    break;
                case GET_PULL_DATA_GROUP:
                    Map<String, Object> pullGroupMap = (Map) msg.obj;
                    List<GroupMessageEntity> userEntityListPull = (List<GroupMessageEntity>) pullGroupMap.get(MESSAGE_DATA);
                    String unReadNumPull = (String) pullGroupMap.get(MESSAGE_UNREAD_NUM);
                    if (TextUtils.isEmpty(unReadNumPull)||"0".equalsIgnoreCase(unReadNumPull)) {
                        group_red_point.setVisibility(View.GONE);
                    } else {
                        group_red_point.setVisibility(View.VISIBLE);
                    }
                    messageGroupAdapter.AddGroupEntityData(userEntityListPull);
                    break;
                case GET_DELAY_ADD_PHOTO:
                    if (MainActivity.interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)) {
                        popupWindowAddPhoto = MainActivity.interactivePopupWindowMap.get(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO);
                        popupWindowAddPhoto.showPopupWindowUp();
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            newPopAddPhoto();
        }
    }

    private void newPopAddPhoto() {
        if (MainActivity.interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)) {
            popupWindowAddPhoto = MainActivity.interactivePopupWindowMap.get(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO);
            popupWindowAddPhoto.showPopupWindowUp();
        } else {
            handler.sendEmptyMessageDelayed(GET_DELAY_ADD_PHOTO, 500);
        }

    }

    private void groupFinishReFresh() {
        if (isGroupRefresh) {
            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }

    private void showGroupEmptyView() {
        if (groupRefreshLayout.getVisibility() == View.VISIBLE) {
            groupRefreshLayout.setVisibility(View.GONE);
        }
        emptyGroupMessageLinear.setVisibility(View.VISIBLE);
        emptyGroupMessageIv.setImageResource(R.drawable.message_member_empty);
        emptyGroupMessageTv.setText(mContext.getString(R.string.text_msg_start_bonding));
    }

    private void hideGroupEmptyView() {
        if (groupRefreshLayout.getVisibility() == View.GONE) {
            groupRefreshLayout.setVisibility(View.VISIBLE);
        }
        emptyGroupMessageLinear.setVisibility(View.GONE);
    }

    private void getDataGroup(int beginIndex) {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            MessageUtil.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
            groupFinishReFresh();
            return;
        }

        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, Object> params = new HashMap<>();
        params.put("limit", DEF_DATA_NUM + "");
        int start = beginIndex * DEF_DATA_NUM;
        params.put("start", start + "");
        requestInfo.putAllParams(params);
        requestInfo.url = String.format(Constant.API_GET_CHAT_MESSAGE_LIST, MainActivity.getUser().getUser_id(), "group");
        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).get(requestInfo, TAG, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                        isPullDataGroup = false;
                    }

                    @Override
                    public void onResult(String response) {
                        groupFinishReFresh();
                        Gson gson = new GsonBuilder().create();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<GroupMessageEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupMessageEntity>>() {
                            }.getType());
                            String totalUnread = jsonObject.optString("groupUnread", "");//私聊的消息总共有多少未读
                            Map<String, Object> map = new HashMap<>();
                            map.put(MESSAGE_DATA, groupList);
                            map.put(MESSAGE_UNREAD_NUM, totalUnread);
                            if (isPullDataGroup) {
                                if (groupList != null && groupList.size() == DEF_DATA_NUM) {
                                    startIndexGroup++;
                                }
                                Message.obtain(handler, GET_PULL_DATA_GROUP, map).sendToTarget();
                            } else {
                                if (null == groupList || groupList.size() == 0) {
                                    showGroupEmptyView();
                                } else {
                                    hideGroupEmptyView();
                                }
                                Message.obtain(handler, GET_NEW_DATA_GROUP, map).sendToTarget();
                            }
                        } catch (JSONException e) {
                            if (isPullDataGroup) {
                                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                            } else {
                                showGroupEmptyView();
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                        groupFinishReFresh();
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

    private void userFinishReFresh() {
        if (isUserRefresh) {
            userRefreshLayout.setRefreshing(false);
            isUserRefresh = false;
        }
    }

    private void showMemberEmptyView() {
        if (userRefreshLayout.getVisibility() == View.VISIBLE) {
            userRefreshLayout.setVisibility(View.GONE);
        }
        emptyMemberMessageLinear.setVisibility(View.VISIBLE);
        emptyMemberMessageIv.setImageResource(R.drawable.message_member_empty);
        emptyMemberMessageTv.setText(mContext.getString(R.string.text_msg_start_bonding));
    }

    private void hideMemberEmptyView() {
        if (userRefreshLayout.getVisibility() == View.GONE) {
            userRefreshLayout.setVisibility(View.VISIBLE);
        }
        emptyMemberMessageLinear.setVisibility(View.GONE);
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
}
