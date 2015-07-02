package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MessagePrivateListAdapter;
import com.bondwithme.BondWithMe.entity.PrivateMessageEntity;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MslToast;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quankun on 15/5/8.
 */
public class MemberMessageFragment extends BaseFragment<MainActivity> {
    private ListView userListView;
    private ImageButton userIb;
    private MySwipeRefreshLayout userRefreshLayout;
    //    private ProgressDialog mProgressDialog;
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
    private String TAG;

    public static MemberMessageFragment newInstance(String... params) {

        return createInstance(new MemberMessageFragment());
    }

    private static final int GET_NEW_DATA = 0x11;
    private static final int GET_PULL_DATA = 0x12;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_NEW_DATA:
                    List<PrivateMessageEntity> userEntityList = (List<PrivateMessageEntity>) msg.obj;
                    privateAdapter.NewUserEntityData(userEntityList);
                    break;
                case GET_PULL_DATA:
                    List<PrivateMessageEntity> userEntityListPull = (List<PrivateMessageEntity>) msg.obj;
                    privateAdapter.AddUserEntityData(userEntityListPull);
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
        TAG = mContext.getClass().getSimpleName();
        userListView = getViewById(R.id.message_listView);
        userIb = getViewById(R.id.ib_top);
        userRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        emptyMemberMessageLinear = getViewById(R.id.message_main_empty_linear);
        emptyMemberMessageIv = getViewById(R.id.message_main_image_empty);
        emptyMemberMessageTv = getViewById(R.id.message_main_text_empty);
//        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
//        mProgressDialog.show();
        vProgress = getViewById(R.id.rl_progress);
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
                    MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
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
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("groupId", privateAdapter.getmUserEntityList().get(arg2).getGroup_id());
                intent.putExtra("titleName", privateAdapter.getmUserEntityList().get(arg2).getUser_given_name());
                // intent.putExtra("userEntity", privateAdapter.getmUserEntityList().get(arg2));
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
                if (privateAdapter.getCount() > 0 && (userListView.getFirstVisiblePosition() != 0)
                        && (userListView.getLastVisiblePosition() > (privateAdapter.getCount() - 5)) && !isPullData) {
                    getData(startIndex);
                    isPullData = true;
                }
            }
        });

        getData(0);
    }

    private void userFinishReFresh() {
        if (isUserRefresh) {
            userRefreshLayout.setRefreshing(false);
            isUserRefresh = false;
        }
    }

    private void showMemberEmptyView() {
        emptyMemberMessageLinear.setVisibility(View.VISIBLE);
        emptyMemberMessageIv.setImageResource(R.drawable.message_member_empty);
        emptyMemberMessageTv.setText("");
    }

    private void hideMemberEmptyView() {
        emptyMemberMessageLinear.setVisibility(View.GONE);
    }

    private void getData(int beginIndex) {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
            userFinishReFresh();
            return;
        }
        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "20");
        int start = beginIndex * 20;
        params.put("start", start + "");
        requestInfo.params = params;
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
                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
                            showMemberEmptyView();
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<PrivateMessageEntity> userList = gson.fromJson(jsonObject.getString("member"), new TypeToken<ArrayList<PrivateMessageEntity>>() {
                            }.getType());
                            String totalUnread = jsonObject.optString("totalUnread", "0");//私聊的消息总共有多少未读
                            if (isPullData) {
                                startIndex++;
                                isPullData = false;
                                Message.obtain(handler, GET_PULL_DATA, userList).sendToTarget();
                            } else {
                                if (null == userList || userList.size() == 0) {
                                    showMemberEmptyView();
                                } else {
                                    hideMemberEmptyView();
                                }
                                Message.obtain(handler, GET_NEW_DATA, userList).sendToTarget();
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

    @Override
    public void requestData() {

    }
}
