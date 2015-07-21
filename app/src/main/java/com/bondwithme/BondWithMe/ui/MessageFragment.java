package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MessageFragmentPagerAdapter;
import com.bondwithme.BondWithMe.adapter.MessageGroupAdapter;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.CircularProgress;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by christepherzhang on 15/1/26.
 */
public class MessageFragment extends BaseFragment<MainActivity> {

    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    Fragment fragment;

    int count = 0;//多少页Fragment

    ArrayList<UserEntity> userEntityList = new ArrayList<>();
    List<GroupEntity> groupEntityList = new ArrayList<>();
    ListView listView;
    ArrayList<UserEntity> pagerList = new ArrayList<>();

    ImageButton mTop;
    ScrollView mSv;

    CircularProgress progressBar;


    GroupEntity groupEntity = new GroupEntity();

    private Dialog showSelectDialog;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;
    private String TAG;

    MessageGroupAdapter messageGroupAdapter;

    LinearLayout mNumLayout;

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_message;
    }

    @Override
    public void initView() {
        mPager = getViewById(R.id.viewpager);
        listView = getViewById(R.id.message_listView);
        TAG = this.getClass().getSimpleName();
        mTop = getViewById(R.id.ib_top);
        mSv = getViewById(R.id.sv);
        progressBar = getViewById(R.id.watting_progressBar);

        mNumLayout = getViewById(R.id.ll_paper_num);

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

                if (fragmentsList != null) {
                    for (int i = 0; i < fragmentsList.size(); i++) {
                        android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(fragmentsList.get(i));
                        fragmentTransaction.commit();
                    }
                    mNumLayout.removeAllViews();
                    fragmentsList.clear();
                    MessageFragmentPagerAdapter messageFragmentPagerAdapter = new MessageFragmentPagerAdapter(getChildFragmentManager(), fragmentsList);
                    mPager.setAdapter(messageFragmentPagerAdapter);
                }

                groupEntityList.clear();
                messageGroupAdapter = new MessageGroupAdapter(getActivity(), groupEntityList);
                listView.setAdapter(messageGroupAdapter);
                messageGroupAdapter.notifyDataSetChanged();
                requestData();

            }

        });
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

        new HttpTools(getActivity()).get(requestInfo, TAG, new HttpCallback() {
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

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    userEntityList = gson.fromJson(jsonObject.getString("member"), new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    groupEntityList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {
                    }.getType());

                    finishReFresh();

                    messageGroupAdapter = new MessageGroupAdapter(getActivity(), groupEntityList);
                    listView.setAdapter(messageGroupAdapter);
//                    messageGroupAdapter.notifyDataSetChanged();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                            intent.putExtra("type", 1);
                            intent.putExtra("groupId", groupEntityList.get(position).getGroup_id());
                            intent.putExtra("titleName", groupEntityList.get(position).getGroup_name());
                            //intent.putExtra("groupEntity", groupEntityList.get(position));
                            view.findViewById(R.id.tv_num).setVisibility(View.GONE);//服务器会消除。本地直接直接消除。
                            startActivity(intent);
                        }
                    });

                    fragmentsList = new ArrayList<Fragment>();

                    //fragment页数
                    count = (userEntityList.size() % 8) == 0 ? (userEntityList.size() / 8) : (userEntityList.size() / 8 + 1);

                    for (int i = 0; i < count; i++) {
                        pagerList = (ArrayList<UserEntity>) getPageData(i * 8);//传给每个Fragment的数据List

//                                fragment = new MessageTopFragment(pagerList);

                        Bundle bundle = new Bundle();

//                                bundle.putParcelableArrayList("data",pagerList);

                        bundle.putSerializable("data", pagerList);

                        fragment = new MessageTopFragment();

                        fragment.setArguments(bundle);

                        fragmentsList.add(fragment);
                    }

                    MessageFragmentPagerAdapter messageFragmentPagerAdapter = new MessageFragmentPagerAdapter(getChildFragmentManager(), fragmentsList);
                    mPager.setAdapter(messageFragmentPagerAdapter);

                    for (int i = 0; i < fragmentsList.size(); i++) {
                        TextView tv = new TextView(getActivity());
                        tv.setBackgroundResource(R.drawable.bg_num_gray_message);
                        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(20, 20);
                        mLayoutParams.leftMargin = 10;
                        mLayoutParams.topMargin = 10;
                        mNumLayout.addView(tv, mLayoutParams);
                    }

                    mPager.setCurrentItem(0);
                    mPager.setOnPageChangeListener(new MyOnPageChangeListener());
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


        /**旧请求*/
//        HashMap<String, String> params = new HashMap<String, String>();
//
//        params.put("limit", "20");
//        params.put("start", "0");
//
//        String url = UrlUtil.generateUrl(Constant.API_MESSAGE_MAIN, params);
//
//        StringRequest stringRequest = new StringRequest(String.format(url, MainActivity.getUser().getUser_id()), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                GsonBuilder gsonb = new GsonBuilder();
//
//                Gson gson = gsonb.create();
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    userEntityList = gson.fromJson(jsonObject.getString("member"), new TypeToken<ArrayList<UserEntity>>() {
//                    }.getType());
//                    groupEntityList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {
//                    }.getType());
//
//                    finishReFresh();
//
//                    messageGroupAdapter = new MessageGroupAdapter(getActivity(), R.layout.message_listview_item, groupEntityList);
//                    listView.setAdapter(messageGroupAdapter);
////                    messageGroupAdapter.notifyDataSetChanged();
//
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Intent intent = new Intent(getActivity(), ChatActivity.class);
//                            intent.putExtra("type", 1);
//                            intent.putExtra("groupEntity", groupEntityList.get(position));
//                            view.findViewById(R.id.tv_num).setVisibility(View.GONE);//服务器会消除。本地直接直接消除。
//                            startActivity(intent);
//                        }
//                    });
//
//                    fragmentsList = new ArrayList<Fragment>();
//
//                    //fragment页数
//                    count = (userEntityList.size() % 8) == 0 ? (userEntityList.size() / 8) : (userEntityList.size() / 8 + 1);
//
//                    for (int i = 0; i < count; i++) {
//                        pagerList = (ArrayList<UserEntity>) getPageData(i * 8);//传给每个Fragment的数据List
//
////                                fragment = new MessageTopFragment(pagerList);
//
//                        Bundle bundle = new Bundle();
//
////                                bundle.putParcelableArrayList("data",pagerList);
//
//                        bundle.putSerializable("data", pagerList);
//
//                        fragment = new MessageTopFragment();
//
//                        fragment.setArguments(bundle);
//
//                        fragmentsList.add(fragment);
//                    }
//
//                    MessageFragmentPagerAdapter messageFragmentPagerAdapter = new MessageFragmentPagerAdapter(getChildFragmentManager(), fragmentsList);
//                    mPager.setAdapter(messageFragmentPagerAdapter);
//
//                    for (int i = 0; i < fragmentsList.size(); i++) {
//                        TextView tv = new TextView(getActivity());
//                        tv.setBackgroundResource(R.drawable.bg_num_gray_message);
//                        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(20, 20);
//                        mLayoutParams.leftMargin = 10;
//                        mLayoutParams.topMargin = 10;
//                        mNumLayout.addView(tv,mLayoutParams);
//                    }
//
//                    mPager.setCurrentItem(0);
//                    mPager.setOnPageChangeListener(new MyOnPageChangeListener());
//                    progressBar.setVisibility(View.GONE);
//                } catch (JSONException e) {
//                    if (isRefresh) {
//                        finishReFresh();
//                    }
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (isRefresh) {
//                    finishReFresh();
//                }
//            }
//        });
//        VolleyUtil.addRequest2Queue(getActivity().getApplicationContext(), stringRequest, "");
    }

    public List<UserEntity> getPageData(int startIndex) {
        int size = userEntityList.size();
        List<UserEntity> pageData = new ArrayList();
        int maxCount = (size - startIndex) > 8 ? 8 : (size - startIndex);
        for (int i = 0; i < maxCount; i++) {
            pageData.add(userEntityList.get(i + startIndex));
        }
        return pageData;
    }

    TextView tv;

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

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

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }


    }

    public static MessageFragment newInstance(String... params) {

        return createInstance(new MessageFragment());
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

