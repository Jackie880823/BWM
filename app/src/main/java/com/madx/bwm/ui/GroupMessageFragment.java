package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MessageGroupFragmentAdapter;
import com.madx.bwm.entity.GroupMessageEntity;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MslToast;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quankun on 15/5/8.
 */
public class GroupMessageFragment extends BaseFragment<MainActivity> {
    private ListView groupListView;
    private ImageButton groupIb;
    private MySwipeRefreshLayout groupRefreshLayout;
    private MessageGroupFragmentAdapter messageGroupAdapter;
    private Context mContext;
    private List<GroupMessageEntity> userEntityList;
    private boolean isGroupRefresh = false;
    private int startIndex = 0;
    private boolean isPullData = false;

    public static GroupMessageFragment newInstance(String... params) {

        return createInstance(new GroupMessageFragment());
    }

    private static final int GET_NEW_DATA = 0x11;
    private static final int GET_PULL_DATA = 0x12;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_NEW_DATA:
                    List<GroupMessageEntity> userEntityList = (List<GroupMessageEntity>) msg.obj;
                    messageGroupAdapter.NewGroupEntityData(userEntityList);
                    break;
                case GET_PULL_DATA:
                    List<GroupMessageEntity> userEntityListPull = (List<GroupMessageEntity>) msg.obj;
                    messageGroupAdapter.AddGroupEntityData(userEntityListPull);
                    break;
            }

        }
    };

    @Override
    public void setLayoutId() {
        layoutId = R.layout.message_list_view_layout;
    }

    @Override
    public void initView() {
        mContext = getActivity();
        groupListView = getViewById(R.id.message_listView);
        groupIb = getViewById(R.id.ib_top);
        groupRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        userEntityList = new ArrayList<>();
        messageGroupAdapter = new MessageGroupFragmentAdapter(mContext, userEntityList);
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
                    MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
                    groupRefreshLayout.setRefreshing(false);
                    return;
                }
                startIndex = 0;
                isGroupRefresh = true;
                getData();
            }

        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupEntity", messageGroupAdapter.getmGroupList().get(arg2));
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
                if (messageGroupAdapter.getCount() > 0 && (groupListView.getLastVisiblePosition() > (messageGroupAdapter.getCount() - 5)) && !isPullData) {
                    getData();
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);
    }

    private void groupFinishReFresh() {
        if (isGroupRefresh) {
            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }

    private void getData() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
            groupFinishReFresh();
            return;
        }

        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "20");
        int start = startIndex * 20 - 1 < 0 ? 0 : startIndex * 20 - 1;
        params.put("start", start + "");
        requestInfo.params = params;
        requestInfo.url = String.format(Constant.API_GET_CHAT_MESSAGE_LIST, MainActivity.getUser().getUser_id(), "group");
        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).get(requestInfo, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResult(String response) {
                        groupFinishReFresh();
                        Gson gson = new GsonBuilder().create();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<GroupMessageEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupMessageEntity>>() {
                            }.getType());

                            if (isPullData) {
                                startIndex++;
                                isPullData = false;
                                Message.obtain(handler, GET_PULL_DATA, groupList).sendToTarget();
                            } else {
                                Message.obtain(handler, GET_NEW_DATA, groupList).sendToTarget();
                            }
                        } catch (JSONException e) {
                            MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
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

    @Override
    public void requestData() {

    }
}
