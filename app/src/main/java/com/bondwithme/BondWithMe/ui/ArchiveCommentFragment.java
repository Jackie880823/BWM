package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.ArchiveCommentAdapter;
import com.bondwithme.BondWithMe.adapter.ArchiveDetailAdapter;
import com.bondwithme.BondWithMe.entity.ArchiveChatEntity;
import com.bondwithme.BondWithMe.entity.ArchiveCommentEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.ArchiveChatViewClickListener;
import com.bondwithme.BondWithMe.widget.ArchiveCommentHead;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzemian on 15/7/1.
 */
public class ArchiveCommentFragment extends BaseFragment<Activity>  implements ArchiveChatViewClickListener {
    private final String TAG = ArchiveCommentFragment.class.getSimpleName();
    private RecyclerView rvList;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh = true;
    private int startIndex = 0;
    private int offset = 10;
    private int currentPage = 1;
    private boolean loading;
    LinearLayoutManager llm;
    private String archive_id;
    private View vProgress;

    private String content_group_id;
    private String group_id;
    private List<ArchiveCommentEntity> CommentData = new ArrayList<>();
    private List<ArchiveChatEntity> detailDate = new ArrayList<>();
    private ArchiveCommentAdapter adapter;
    private ArchiveDetailAdapter detailAdapter;

    private HttpTools mHttpTools;

    private View heah;
    private ArchiveCommentHead<ArchiveCommentAdapter> abookends;

    public static ArchiveCommentFragment newInstance(String... params){
        return createInstance(new ArchiveCommentFragment(),params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_archive_comment;
    }

    @Override
    public void initView() {
        mHttpTools = new HttpTools(getActivity());
        if(getArguments() != null){
            content_group_id  = getArguments().getString(ARG_PARAM_PREFIX + "0");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
        }

        LayoutInflater inflater = LayoutInflater.from(getParentActivity());
//        heah = inflater.inflate(R.layout.fragment_archive_comment_head, rvList, false);

        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        rvList = getViewById(R.id.rv_archive_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();

//        LayoutInflater inflater = LayoutInflater.from(getParentActivity());
//        heah = inflater.inflate(R.layout.fragment_archive_comment_head, rvList, false);
//        abookends = new ArchiveCommentHead<>(adapter);
//        abookends.addHeader(heah);
        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if((CommentData.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });
        swipeRefreshLayout = getViewById(R.id.swipe_archive_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });
    }

    private void reInitDataStatus() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        startIndex = 0;
        loading = false;
    }

    private void initAdapter(){
        adapter = new ArchiveCommentAdapter(getParentActivity(),CommentData,detailDate);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);

//        abookends = new ArchiveCommentHead<>(adapter,getParentActivity(),detailDate);
//        abookends.addHeader(heah);
////        abookends.notifyDataSetChanged();
//        rvList.setAdapter(abookends);
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }

    @Override
    public void requestData() {
//        getComments();
        Map<String,String> params = new HashMap<>();
        params.put("start","0");
        params.put("limit","10");
        params.put("content_group_id",content_group_id);
        params.put("view_user",MainActivity.getUser().getUser_id());

        String url = UrlUtil.generateUrl(Constant.API_MORE_ARCHIVE_POSTING_DETAIL, params);
        mHttpTools.get(url, null, TAG, new HttpCallback(){
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                if(detailDate == null){
                    getParentActivity().finish();
                }else {
                    vProgress.setVisibility(View.GONE);
                    getComments();
                }
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                //DateDeserializer ds = new DateDeserializer();
                //给GsonBuilder方法单独指定Date类型的反序列化方法
                //gsonb.registerTypeAdapter(Date.class, ds);
                Gson gson = gsonb.create();
                detailDate = gson.fromJson(response, new TypeToken<ArrayList<ArchiveChatEntity>>() {}.getType());

                try{

                }catch (Exception e){
                    e.printStackTrace();
                    reInitDataStatus();
                }finally {
                    vProgress.setVisibility(View.GONE);
                }

                if(isRefresh) {
                    isRefresh = false;
                    currentPage = 1;//还原为第一页
                    initAdapter();
                } else {
                    startIndex += detailDate.size();
                    if(adapter == null) {
                        initAdapter();
                        adapter.notifyDataSetChanged();
                    } else {
//                        abookends.addData(detailDate);
                    }
                }
                loading = false;
            }

            @Override
            public void onError(Exception e) {
                if(isRefresh) {
                    finishReFresh();
                }
                loading = false;
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void getComments() {
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("content_group_id", content_group_id);
        jsonParams.put("group_id", group_id);
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        Map<String,String> params = new HashMap<>();
        params.put("condition", jsonParamsString);
        params.put("start", startIndex + "");
        params.put("limit", offset + "");

        String url = UrlUtil.generateUrl(Constant.API_MORE_ARCHIVE_COMMENT_LIST, params);

        mHttpTools.get(url, null, TAG, new HttpCallback(){
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
                CommentData = gson.fromJson(response, new TypeToken<ArrayList<ArchiveCommentEntity>>() {}.getType());
                vProgress.setVisibility(View.GONE);
                try{
                    if(isRefresh) {
                        isRefresh = false;
                        currentPage = 1;//还原为第一页
                        initAdapter();
                    } else {
                        startIndex += CommentData.size();
                        if(adapter == null) {
                            initAdapter();
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter.addData(CommentData);
                        }
                    }
                    loading = false;
                }catch (Exception e){
                    e.printStackTrace();
                    reInitDataStatus();
                }

            }

            @Override
            public void onError(Exception e) {
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

    }
}