package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.MessageListAdapter;
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.InteractivePopupWindow;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;
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
public class MessageFragment extends BaseFragment<MainActivity> {
    private View search_linear;
    private EditText et_search;
    private MySwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ImageButton ib_top;
    private View emptyView;
    private Context mContext;
    private static final int GET_DELAY_ADD_PHOTO = 0x30;
    private InteractivePopupWindow popupWindowAddPhoto;
    private static final String MESSAGE_DATA = "data";
    private static final String MESSAGE_UNREAD_NUM = "num";
    private MessageListAdapter privateAdapter;
    private View vProgress;
    private List<PrivateMessageEntity> userEntityList;
    private boolean isUserRefresh = false;
    private int startIndex = 1;
    private boolean isPullData = false;
    private static final int GET_NEW_DATA = 0x11;
    private static final int GET_PULL_DATA = 0x12;
    private String TAG;
    private static final int DEF_DATA_NUM = 20;
    private InputMethodManager imm;
    private TextView searchTv;

    public static MessageFragment newInstance(String... params) {

        return createInstance(new MessageFragment());
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_message;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        mContext = getActivity();
        TAG = mContext.getClass().getSimpleName();
        search_linear = getViewById(R.id.search_linear);
        et_search = getViewById(R.id.et_search);
        refreshLayout = getViewById(R.id.swipe_refresh_layout);
        listView = getViewById(R.id.message_listView);
        ib_top = getViewById(R.id.ib_top);
        emptyView = getViewById(R.id.message_main_empty_linear);
        searchTv = getViewById(R.id.message_search);
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        userEntityList = new ArrayList<>();
        privateAdapter = new MessageListAdapter(mContext, userEntityList);
        listView.setAdapter(privateAdapter);
        search_linear.setVisibility(View.GONE);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        ib_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(0);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    MessageUtil.getInstance(mContext).showShortToast(getString(R.string.text_no_network));
                    refreshLayout.setRefreshing(false);
                    return;
                }
                startIndex = 1;
                isUserRefresh = true;
                getData(0);
            }

        });

        privateAdapter.showNoData(new NoFoundDataListener() {
            @Override
            public void showFoundData(String string) {
                emptyView.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.GONE);
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
                PrivateMessageEntity messageEntity = privateAdapter.getmUserEntityList().get(arg2);
                Intent intent = new Intent(mContext, MessageChatActivity.class);
                intent.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_MEMBER);
                intent.putExtra(UserEntity.EXTRA_GROUP_ID, messageEntity.getGroup_id());
                intent.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, messageEntity.getUser_given_name());
                startActivity(intent);
//                Intent intent = new Intent(mContext, MessageChatActivity.class);
//                intent.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_GROUP);
//                intent.putExtra(Constant.MESSAGE_CHART_GROUP_ID, groupEntity.getGroup_id());
//                intent.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, groupEntity.getGroup_name());
//                intent.putExtra(Constant.GROUP_DEFAULT, groupEntity.getGroup_default());
//                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater factory = LayoutInflater.from(mContext);
                View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
                final Dialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
                TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
                tv_no_member.setText(getString(R.string.text_delete));
                tv_no_member.setTextColor(ContextCompat.getColor(mContext, R.color.delete_text_color));
                TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
                cancelTv.setText(R.string.text_start_cancle);
                tv_no_member.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectDialog.dismiss();
                        PrivateMessageEntity messageEntity = privateAdapter.getmUserEntityList().get(position);
                        privateAdapter.getmUserEntityList().remove(position);
                        privateAdapter.notifyDataSetChanged();
                    }
                });
                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectDialog.dismiss();
                    }
                });
                showSelectDialog.show();
                return true;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listView.getFirstVisiblePosition() == 0) {
                    ib_top.setVisibility(View.GONE);
                } else {
                    ib_top.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getFirstVisiblePosition() == 0) {
                    refreshLayout.setEnabled(true);
                    ib_top.setVisibility(View.GONE);
                } else {
                    ib_top.setVisibility(View.VISIBLE);
                    refreshLayout.setEnabled(false);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                    }
                }
                int adapterCount = privateAdapter.getCount();
                if (adapterCount == DEF_DATA_NUM * startIndex && listView.getFirstVisiblePosition() < (adapterCount - 5)
                        && (listView.getLastVisiblePosition() > (adapterCount - 5)) && !isPullData) {
                    getData(startIndex);
                    isPullData = true;
                }
            }
        });
        //绑定自定义适配器
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                if (search_linear.getVisibility() == View.VISIBLE) {
                    search_linear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.org_up_from_bottom));
                    search_linear.setVisibility(View.GONE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                    }
                } else {
                    search_linear.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_from_top));
                    search_linear.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String etImport = et_search.getText().toString();
                setSearchData(etImport);
            }
        });
    }

    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        List<PrivateMessageEntity> list;
        if (TextUtils.isEmpty(etImport)) {
            list = userEntityList;
            privateAdapter.addNewData(list);
        } else {
            Filter filter = privateAdapter.getFilter();
            filter.filter(etImport);
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getData(0);
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


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_NEW_DATA:
                    Map<String, Object> pullPrivateMapNew = (Map) msg.obj;
                    userEntityList = (List<PrivateMessageEntity>) pullPrivateMapNew.get(MESSAGE_DATA);
                    privateAdapter.addNewData(userEntityList);
                    break;
                case GET_PULL_DATA:
                    Map<String, Object> pullPrivateMap = (Map) msg.obj;
                    List<PrivateMessageEntity> userEntityListPull1 = (List<PrivateMessageEntity>) pullPrivateMap.get(MESSAGE_DATA);
                    userEntityList.addAll(userEntityListPull1);
                    privateAdapter.addNewData(userEntityList);
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

    private void getDataGroup(int beginIndex) {
//        final RequestInfo requestInfo = new RequestInfo();
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("limit", DEF_DATA_NUM + "");
//        int start = beginIndex * DEF_DATA_NUM;
//        params.put("start", start + "");
//        requestInfo.putAllParams(params);
//        requestInfo.url = String.format(Constant.API_GET_CHAT_MESSAGE_LIST, MainActivity.getUser().getUser_id(), "group");
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                new HttpTools(getActivity()).get(requestInfo, TAG, new HttpCallback() {
//                    @Override
//                    public void onStart() {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        isPullDataGroup = false;
//                    }
//
//                    @Override
//                    public void onResult(String response) {
//                        groupFinishReFresh();
//                        Gson gson = new GsonBuilder().create();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            List<GroupMessageEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupMessageEntity>>() {
//                            }.getType());
//                            String totalUnread = jsonObject.optString("groupUnread", "");//私聊的消息总共有多少未读
//                            Map<String, Object> map = new HashMap<>();
//                            map.put(MESSAGE_DATA, groupList);
//                            map.put(MESSAGE_UNREAD_NUM, totalUnread);
//                            if (isPullDataGroup) {
//                                if (groupList != null && groupList.size() == DEF_DATA_NUM) {
//                                    startIndexGroup++;
//                                }
//                                Message.obtain(handler, GET_PULL_DATA_GROUP, map).sendToTarget();
//                            } else {
//                                if (null == groupList || groupList.size() == 0) {
//                                    showGroupEmptyView();
//                                } else {
//                                    hideGroupEmptyView();
//                                }
//                                Message.obtain(handler, GET_NEW_DATA_GROUP, map).sendToTarget();
//                            }
//                        } catch (JSONException e) {
//                            if (isPullDataGroup) {
//                                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
//                            } else {
//                                showGroupEmptyView();
//                            }
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
//                        groupFinishReFresh();
//                    }
//
//                    @Override
//                    public void onCancelled() {
//
//                    }
//
//                    @Override
//                    public void onLoading(long count, long current) {
//
//                    }
//                });
//            }
//        }.start();
    }

    private void userFinishReFresh() {
        if (isUserRefresh) {
            refreshLayout.setRefreshing(false);
            isUserRefresh = false;
        }
    }

    private void showMemberEmptyView() {
        if (refreshLayout.getVisibility() == View.VISIBLE) {
            refreshLayout.setVisibility(View.GONE);
        }
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideMemberEmptyView() {
        if (refreshLayout.getVisibility() == View.GONE) {
            refreshLayout.setVisibility(View.VISIBLE);
        }
        emptyView.setVisibility(View.GONE);
    }
}
