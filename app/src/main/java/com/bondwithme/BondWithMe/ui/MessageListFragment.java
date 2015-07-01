package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MessageGroupAdapter;
import com.bondwithme.BondWithMe.adapter.MessageMemberListAdapter;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MslToast;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/5/5.
 */
public class MessageListFragment extends BaseFragment<MainActivity> implements View.OnClickListener {
    private ViewPager pager;
    private TextView message_member_tv;
    private TextView message_group_tv;
    private Dialog showSelectDialog;
    private Context mContext;
    private List<UserEntity> userEntityList;
    private List<GroupEntity> groupEntityList;
    private MessageGroupAdapter messageGroupAdapter;
    private MessageMemberListAdapter privateAdapter;
    private boolean isPullData = false;
    private MySwipeRefreshLayout userRefreshLayout, groupRefreshLayout;
    private int startIndex = 0;
    private boolean isUserRefresh = false;
    private boolean isGroupRefresh = false;
    private ProgressDialog mProgressDialog;
    static List<String> STICKER_NAME_LIST = new ArrayList<>();
    static List<String> FIRST_STICKER_LIST = new ArrayList<>();
    private String TAG;

    public static MessageListFragment newInstance(String... params) {

        return createInstance(new MessageListFragment());
    }

    private static final int GET_NEW_DATA = 0x11;
    private static final int GET_PULL_DATA = 0x12;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_NEW_DATA:
                    Map<String, List> map = (Map<String, List>) msg.obj;
                    List<UserEntity> userEntityList = map.get("private");
                    List<GroupEntity> groupEntityList = map.get("group");
                    privateAdapter.NewUserEntityData(userEntityList);
                    messageGroupAdapter.NewGroupEntityData(groupEntityList);
                    break;
                case GET_PULL_DATA:
                    Map<String, List> mapPull = (Map<String, List>) msg.obj;
                    List<UserEntity> userEntityListPull = mapPull.get("private");
                    List<GroupEntity> groupEntityListPull = mapPull.get("group");
                    privateAdapter.AddUserEntityData(userEntityListPull);
                    messageGroupAdapter.AddGroupEntityData(groupEntityListPull);
                    break;
            }

        }
    };

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_message_list;
    }

    @Override
    public void initView() {
        mContext = getActivity();
        TAG = mContext.getClass().getSimpleName();
        pager = getViewById(R.id.message_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        message_group_tv = getViewById(R.id.message_group_tv);
        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
        mProgressDialog.show();
        userEntityList = new ArrayList<>();
        groupEntityList = new ArrayList<>();
        privateAdapter = new MessageMemberListAdapter(mContext, userEntityList);
        messageGroupAdapter = new MessageGroupAdapter(mContext, groupEntityList);
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                showSelectDialog();
                return false;
            }
        });
        List<View> pagerViewList = initPagerView();
        pager.setAdapter(new MessageViewPagerAdapter(pagerViewList));
        pager.setOnPageChangeListener(new MyOnPageChanger());
        pager.setCurrentItem(0);
        message_member_tv.setOnClickListener(this);
        message_group_tv.setOnClickListener(this);
        new GetStickerNameList().start();
    }

    class GetStickerNameList extends Thread {
        @Override
        public void run() {
            super.run();
            addStickerList();
            addImageList();
        }
    }

    private void addStickerList() {
        try {
            List<String> pathList = FileUtil.getAllFilePathsFromAssets(mContext, MainActivity.STICKERS_NAME);
            if (null != pathList) {
                for (String string : pathList) {
                    STICKER_NAME_LIST.add(MainActivity.STICKERS_NAME + File.separator + string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageList() {
        if (STICKER_NAME_LIST != null && STICKER_NAME_LIST.size() > 0) {
            for (String string : STICKER_NAME_LIST) {
                List<String> stickerAllNameList = FileUtil.getAllFilePathsFromAssets(mContext, string);
                if (null != stickerAllNameList) {
                    String iconPath = string + File.separator + stickerAllNameList.get(0);
                    FIRST_STICKER_LIST.add(iconPath);
                }
            }
        }
    }

    private List<View> initPagerView() {
        List<View> mLists = new ArrayList<>();
        View userView = LayoutInflater.from(mContext).inflate(R.layout.message_list_view_layout, null);
        final ListView userListView = (ListView) userView.findViewById(R.id.message_listView);
        final ImageButton userIb = (ImageButton) userView.findViewById(R.id.ib_top);
        userRefreshLayout = (MySwipeRefreshLayout) userView.findViewById(R.id.swipe_refresh_layout);
        userListView.setAdapter(privateAdapter);
        userListView.setClickable(true);
        userListView.setFocusable(true);
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
                    MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
                    userRefreshLayout.setRefreshing(false);
                    return;
                }
                startIndex = 0;
                isUserRefresh = true;
                requestData();
            }

        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                arg1.findViewById(R.id.tv_num).setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("groupId", privateAdapter.getmUserEntityList().get(arg2).getGroup_id());
                intent.putExtra("titleName", privateAdapter.getmUserEntityList().get(arg2).getUser_given_name());
                intent.putExtra("type", 0);
                //intent.putExtra("userEntity", privateAdapter.getmUserEntityList().get(arg2));
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
                if ((userListView.getLastVisiblePosition() > (privateAdapter.getCount() - 5)) && !isPullData) {
                    requestData();
                    isPullData = true;
                }
                if (userListView.getFirstVisiblePosition() == 0) {
                    userRefreshLayout.setEnabled(true);
                    userIb.setVisibility(View.GONE);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    userRefreshLayout.setEnabled(false);
                }
            }
        });
        mLists.add(userView);
        View groupView = LayoutInflater.from(mContext).inflate(R.layout.message_list_view_layout, null);
        final ListView groupListView = (ListView) groupView.findViewById(R.id.message_listView);
        final ImageButton groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
        groupListView.setAdapter(messageGroupAdapter);
        groupListView.setClickable(true);
        groupListView.setFocusable(true);
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
                    MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
                    groupRefreshLayout.setRefreshing(false);
                    return;
                }
                startIndex = 0;
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
                intent.putExtra("groupId", messageGroupAdapter.getmGroupList().get(arg2).getGroup_id());
                intent.putExtra("titleName", messageGroupAdapter.getmGroupList().get(arg2).getGroup_name());
                // intent.putExtra("groupEntity", messageGroupAdapter.getmGroupList().get(arg2));
                arg1.findViewById(R.id.tv_num).setVisibility(View.GONE);//服务器会消除。本地直接直接消除。
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
                if ((groupListView.getLastVisiblePosition() > (messageGroupAdapter.getCount() - 5)) && !isPullData) {
                    requestData();
                    isPullData = true;
                }
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupRefreshLayout.setEnabled(true);
                    groupIb.setVisibility(View.GONE);
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

    class MessageViewPagerAdapter extends PagerAdapter {
        private List<View> mLists;

        public MessageViewPagerAdapter(List<View> array) {
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

    private void userFinishReFresh() {
        if (isUserRefresh) {
            userRefreshLayout.setRefreshing(false);
            isUserRefresh = false;
        }
    }

    private void groupFinishReFresh() {
        if (isGroupRefresh) {
            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }

    @Override
    public void requestData() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
            userFinishReFresh();
            groupFinishReFresh();
            return;
        }

        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "20");
        int start = startIndex * 20 - 1 < 0 ? 0 : startIndex * 20 - 1;
        params.put("start", start + "");
        requestInfo.params = params;
        requestInfo.url = String.format(Constant.API_MESSAGE_MAIN, MainActivity.getUser().getUser_id());
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
                    }

                    @Override
                    public void onResult(String response) {
                        userFinishReFresh();
                        groupFinishReFresh();
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<UserEntity> userList = gson.fromJson(jsonObject.getString("member"), new TypeToken<ArrayList<UserEntity>>() {
                            }.getType());
                            List<GroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {
                            }.getType());
                            Map<String, List> map = new HashMap<>();
                            if (userList != null && userList.size() > 0) {
                                map.put("private", userList);
                            }
                            if (groupList != null && groupList.size() > 0) {
                                map.put("group", groupList);
                            }
                            if (isPullData) {
                                startIndex++;
                                isPullData = false;
                                Message.obtain(handler, GET_PULL_DATA, map).sendToTarget();
                            } else {
                                Message.obtain(handler, GET_NEW_DATA, map).sendToTarget();
                            }
                        } catch (JSONException e) {
                            MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                        userFinishReFresh();
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

    /**
     * ViewPager页面选项卡进行切换时候的监听器处理
     *
     * @author jiangqingqing
     */
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
}
