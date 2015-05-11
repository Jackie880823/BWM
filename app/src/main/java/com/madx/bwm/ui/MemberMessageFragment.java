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
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MessagePrivateListAdapter;
import com.madx.bwm.entity.PrivateMessageEntity;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MslToast;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    private ProgressDialog mProgressDialog;
    private MessagePrivateListAdapter privateAdapter;
    private Context mContext;
    private List<PrivateMessageEntity> userEntityList;
    private boolean isUserRefresh = false;
    private int startIndex = 0;
    private boolean isPullData = false;


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
        userListView = getViewById(R.id.message_listView);
        userIb = getViewById(R.id.ib_top);
        userRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
        mProgressDialog.show();
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
                startIndex = 0;
                isUserRefresh = true;
                getData();
            }

        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                arg1.findViewById(R.id.tv_num).setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("userEntity", privateAdapter.getmUserEntityList().get(arg2));
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
                if (privateAdapter.getCount() > 0 && (userListView.getLastVisiblePosition() > (privateAdapter.getCount() - 5)) && !isPullData) {
                    getData();
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                addStickerList();
                addImageList();
            }
        });
        thread.start();
        getData();
    }

    static List<String> STICKER_NAME_LIST = new ArrayList<>();
    static List<String> FIRST_STICKER_LIST = new ArrayList<>();

    private void addStickerList() {
        try {
            List<String> pathList = FileUtil.getAllFilePathsFromAssets(getActivity(), MessageChatActivity.STICKERS_NAME);
            if (null != pathList) {
                for (String string : pathList) {
                    STICKER_NAME_LIST.add(MessageChatActivity.STICKERS_NAME + File.separator + string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageList() {
        if (STICKER_NAME_LIST != null && STICKER_NAME_LIST.size() > 0) {
            for (String string : STICKER_NAME_LIST) {
                List<String> stickerAllNameList = FileUtil.getAllFilePathsFromAssets(getActivity(), string);
                if (null != stickerAllNameList) {
                    String iconPath = string + File.separator + stickerAllNameList.get(0);
                    FIRST_STICKER_LIST.add(iconPath);
                }
            }
        }
    }

    private void userFinishReFresh() {
        if (isUserRefresh) {
            userRefreshLayout.setRefreshing(false);
            isUserRefresh = false;
        }
    }

    private void getData() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            MslToast.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
            userFinishReFresh();
            return;
        }
        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "20");
        int start = startIndex * 20 - 1 < 0 ? 0 : startIndex * 20 - 1;
        params.put("start", start + "");
        requestInfo.params = params;
        requestInfo.url = String.format(Constant.API_GET_CHAT_MESSAGE_LIST, MainActivity.getUser().getUser_id(), "member");
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
                        userFinishReFresh();
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
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
                                Message.obtain(handler, GET_NEW_DATA, userList).sendToTarget();
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
