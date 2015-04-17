package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.WallAdapter;
import com.madx.bwm.entity.WallEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.MyDialog;
import com.madx.bwm.widget.MySwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.WallFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.WallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallFragment extends BaseFragment<MainActivity> implements WallAdapter.ViewClickListener {


    private ProgressDialog mProgressDialog;

    public static WallFragment newInstance(String... params) {
        return createInstance(new WallFragment(), params);
    }

    public WallFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall;
    }

    private RecyclerView rvList;
    private WallAdapter adapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh = true;
    public List<WallEntity> data = new ArrayList<WallEntity>();
    private int startIndex = 0;
    private int offset = 10;
    private int currentPage = 1;
    private boolean loading;
    LinearLayoutManager llm;
    private String member_id;//根据member查看的wall

    @Override
    public void initView() {
        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
        mProgressDialog.show();

        if (getArguments() != null) {
            member_id = getArguments().getString(ARG_PARAM_PREFIX + 0);
        }

        rvList = getViewById(R.id.rv_wall_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();

        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if ((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });


        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });
//        swipeRefreshLayout.setProgressBackgroundColor(getResources().getColor(R.color.default_text_color_light));
//        swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_red_dark);

//        isRefresh = true;
//        swipeRefreshLayout.setRefreshing(true);


    }

    private void initAdapter() {
        adapter = new WallAdapter(getParentActivity(), data);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);
    }


    @Override
    public void requestData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("limit", offset + "");
        params.put("start", startIndex + "");
        if (!TextUtils.isEmpty(member_id)) {
            params.put("member_id", member_id + "");
        }

        String url = UrlUtil.generateUrl(Constant.API_WALL_MAIN, params);
        new HttpTools(getActivity()).get(url, params, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                //DateDeserializer ds = new DateDeserializer();
                //给GsonBuilder方法单独指定Date类型的反序列化方法
                //gsonb.registerTypeAdapter(Date.class, ds);
                Gson gson = gsonb.create();
                try {
                    Log.d("", "2memberId============" + response);
                    data = gson.fromJson(response, new TypeToken<ArrayList<WallEntity>>() {
                    }.getType());
                    if (isRefresh) {
                        startIndex = data.size();
                        currentPage = 1;
                        finishReFresh();
                        initAdapter();
                    } else {
                        startIndex += data.size();
                        adapter.add(data);
                    }
                    loading = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    reInitDataStatus();
                }finally {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
                    finishReFresh();
                }
                loading = false;
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void reInitDataStatus(){
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        startIndex = 0;
        loading = false;
    }


    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }

    @Override
    public void showOriginalPic(String content_id) {
        Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);
        Map<String, String> condition = new HashMap<>();
        condition.put("content_id", content_id);
        Map<String, String> params = new HashMap<>();
        params.put("condition", UrlUtil.mapToJsonstring(condition));
        String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
        intent.putExtra("request_url", url);
        startActivity(intent);
    }

    @Override
    public void showComments(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallCommentActivity.class);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivityForResult(intent, Constant.ACTION_COMMENT_WALL);
    }

    MyDialog removeAlertDialog;


    @Override
    public void remove(final String content_group_id) {

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_wall_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, content_group_id), null);
                new HttpTools(getActivity()).put(requestInfo, new HttpCallback() {
                    @Override
                    public void onStart() {
                        mProgressDialog.setTitle(R.string.text_waiting);
                        mProgressDialog.show();
                    }

                    @Override
                    public void onFinish() {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResult(String string) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
//                        refresh();
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
                removeAlertDialog.dismiss();
            }
        });
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if (!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTION_CREATE_WALL:
                    //wait a mement for the pic handle on server
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                case Constant.ACTION_COMMENT_WALL:
                    refresh();
                    break;
            }
        }
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        startIndex = 0;
        requestData();
    }
}
