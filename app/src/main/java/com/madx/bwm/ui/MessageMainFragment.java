package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MessageGroupAdapter;
import com.madx.bwm.adapter.MessageGridViewAdapter;
import com.madx.bwm.adapter.MessageViewPagerAdapter;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quankun on 15/4/17.
 */
public class MessageMainFragment extends BaseFragment<MainActivity> {

    private ViewPager mPager;
    private Context mContext;
    int count = 0;//多少页Fragment

    ListView listView;
    ArrayList<UserEntity> pagerList = new ArrayList<>();

    ImageButton mTop;
    ScrollView mSv;

    ProgressBarCircularIndeterminate progressBar;
    private Dialog showSelectDialog;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    MessageGroupAdapter messageGroupAdapter;

    LinearLayout mNumLayout;

    private int index = 0;
    private MessageViewPagerAdapter adapter;

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_message;
    }


    public List<GridView> init(final List<UserEntity> userEntityList) {

        List<GridView> mLists = new ArrayList<GridView>();
        GridView gv;
        for (int i = 0; i < count; i++) {
            gv = new GridView(mContext);
            gv.setAdapter(new MessageGridViewAdapter(mContext, userEntityList, i));
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(true);

            gv.setNumColumns(4);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    arg1.findViewById(R.id.tv_num).setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("type", 0);
                    intent.putExtra("userEntity", userEntityList.get(index * 8 + arg2));
                    startActivity(intent);
                }
            });
            mLists.add(gv);
        }
        return mLists;
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
            if (tv != null) {
                tv.setBackgroundResource(R.drawable.bg_num_gray_message);
            }

            TextView currentBt = (TextView) mNumLayout.getChildAt(arg0);
            if (currentBt == null) {
                return;
            }
            currentBt.setBackgroundResource(R.drawable.bg_num_gray_press_message);
            tv = currentBt;
            index = arg0;
        }

    }

    @Override
    public void initView() {
        mContext = getActivity();
        mPager = getViewById(R.id.viewpager);
        listView = getViewById(R.id.message_listView);

        mTop = getViewById(R.id.ib_top);
        mSv = getViewById(R.id.sv);
        progressBar = getViewById(R.id.watting_progressBar);

        mNumLayout = getViewById(R.id.ll_paper_num);

        mPager.setOnPageChangeListener(new MyOnPageChanger());


        mTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSv.post(new Runnable() {

                    @Override
                    public void run() {
                        mSv.post(new Runnable() {
                            public void run() {
                                // 滚动至顶部
                                mSv.fullScroll(ScrollView.FOCUS_UP);
                            }
                        });
                    }
                });
            }
        });


        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });


        //重写MainActivity右button事件
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                showSelectDialog();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupEntity", messageGroupAdapter.getmGroupList().get(position));
                view.findViewById(R.id.tv_num).setVisibility(View.GONE);//服务器会消除。本地直接直接消除。
                startActivity(intent);
            }
        });

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    finishReFresh();
                    return;
                }
                isRefresh = true;
                requestData();

            }

        });
    }

    private static final int GET_FRIST_GROUP = 0x11;
    private static final int GET_FRIST_PRIVATE = 0x12;
    private static final int REFRESH_GET_FRIST_GROUP = 0x13;
    private static final int REFRESH_GET_FRIST_PRIVATE = 0x14;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_FRIST_GROUP:
                    List<GroupEntity> groupEntityList = (List<GroupEntity>) msg.obj;
                    messageGroupAdapter = new MessageGroupAdapter(getActivity(), groupEntityList);
                    listView.setAdapter(messageGroupAdapter);
                    break;
                case GET_FRIST_PRIVATE:
                    List<UserEntity> userEntityList = (List<UserEntity>) msg.obj;
                    if (null == userEntityList || userEntityList.size() == 0) {
                        break;
                    }
                    count = (userEntityList.size() % 8) == 0 ? (userEntityList.size() / 8) : (userEntityList.size() / 8 + 1);
                    List<GridView> mLists = init(userEntityList);
                    adapter = new MessageViewPagerAdapter(mContext, mLists);
                    mPager.setAdapter(adapter);
                    setPage();
                    break;
                case REFRESH_GET_FRIST_GROUP:
                    List<GroupEntity> refreshGroupEntityList = (List<GroupEntity>) msg.obj;
                    messageGroupAdapter = new MessageGroupAdapter(getActivity(), refreshGroupEntityList);
                    listView.setAdapter(messageGroupAdapter);
                    messageGroupAdapter.notifyDataSetChanged();
                    break;
                case REFRESH_GET_FRIST_PRIVATE:
                    mNumLayout.removeAllViews();
                    index=0;
                    List<UserEntity> refreshUserEntityList = (List<UserEntity>) msg.obj;
                    if (null == refreshUserEntityList || refreshUserEntityList.size() == 0) {
                        break;
                    }
                    count = (refreshUserEntityList.size() % 8) == 0 ? (refreshUserEntityList.size() / 8) : (refreshUserEntityList.size() / 8 + 1);
                    List<GridView> mList = init(refreshUserEntityList);
                    adapter = new MessageViewPagerAdapter(mContext, mList);
                    mPager.setAdapter(adapter);
                    setPage();
                    break;
            }

        }
    };

    private void setPage() {
        for (int i = 0; i < count; i++) {
            TextView tv = new TextView(getActivity());
            tv.setBackgroundResource(R.drawable.bg_num_gray_message);
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(20, 20);
            mLayoutParams.leftMargin = 10;
            mLayoutParams.topMargin = 10;
            mNumLayout.addView(tv, mLayoutParams);
        }

        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChanger());
    }

    @Override
    public void requestData() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }

        RequestInfo requestInfo = new RequestInfo();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", "20");
        params.put("start", "0");
        requestInfo.params = params;
        requestInfo.url = String.format(Constant.API_MESSAGE_MAIN, MainActivity.getUser().getUser_id());

        new HttpTools(getActivity()).get(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                Gson gson = new GsonBuilder().create();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    ArrayList<UserEntity> userEntityList = gson.fromJson(jsonObject.getString("member"), new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    ArrayList<GroupEntity> groupEntityList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {
                    }.getType());
                    if (isRefresh) {
                        Message.obtain(handler, REFRESH_GET_FRIST_GROUP, groupEntityList).sendToTarget();
                        Message.obtain(handler, REFRESH_GET_FRIST_PRIVATE, userEntityList).sendToTarget();
                    } else {
                        Message.obtain(handler, GET_FRIST_GROUP, groupEntityList).sendToTarget();
                        Message.obtain(handler, GET_FRIST_PRIVATE, userEntityList).sendToTarget();
                    }
                    finishReFresh();
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    if (isRefresh) {
                        finishReFresh();
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                if (isRefresh) {
                    finishReFresh();
                }
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    TextView tv;

    public static MessageMainFragment newInstance(String... params) {

        return createInstance(new MessageMainFragment());
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

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }


}
