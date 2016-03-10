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
import com.bondwithme.BondWithMe.entity.PhotoEntity;
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
    private boolean isCommentRefresh = true;
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
    public static final String PIC_URL = "pic_url";

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
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        mHttpTools = new HttpTools(getActivity());
        if(getArguments() != null){
            content_group_id  = getArguments().getString(ARG_PARAM_PREFIX + "0");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
        }

        LayoutInflater inflater = LayoutInflater.from(getParentActivity());

        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        rvList = getViewById(R.id.rv_archive_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
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
                int count = Math.abs(totalItemCount - 5);
                if (CommentData.size() >= offset && !loading && lastVisibleItem >= count && dy > 0) {
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
                isCommentRefresh = true;
                startIndex = 0;
                CommentData.clear();
                requestData();
            }

        });
    }

    private void reInitDataStatus() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        isCommentRefresh = false;
        startIndex = 0;
        loading = false;
    }

    private void initAdapter(){
        adapter = new ArchiveCommentAdapter(getParentActivity(),CommentData,detailDate);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);

    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        isCommentRefresh  = false;
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

                try{
                    Gson gson = gsonb.create();
                    detailDate = gson.fromJson(response, new TypeToken<ArrayList<ArchiveChatEntity>>() {}.getType());
                    if(isRefresh) {
                        isRefresh = false;
                        currentPage = 1;//还原为第一页
                        initAdapter();
                    } else {
//                    startIndex += detailDate.size();
                        if(adapter == null) {
                            initAdapter();
                            adapter.notifyDataSetChanged();
                        } else {
//                        abookends.addData(detailDate);
                        }
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    loading = false;
                }catch (Exception e){
                    e.printStackTrace();
                    reInitDataStatus();
                }finally {
                    vProgress.setVisibility(View.GONE);
                }

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
                        isCommentRefresh = false;
                        currentPage = 1;//还原为第一页
//                        initAdapter();
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
    public void showOriginalPic(ArchiveChatEntity entity) {
        Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);
        ArrayList<PhotoEntity> dataList = new ArrayList<>();
        PhotoEntity peData = new PhotoEntity();
        peData.setUser_id(entity.getUser_id());
        peData.setFile_id(entity.getFile_id());
        peData.setPhoto_caption(Constant.Module_Original);
        peData.setPhoto_multipe("false");
        dataList.add(peData);

        intent.putExtra("is_data", true);
        intent.putExtra("datas", dataList);
        startActivity(intent);

    }

    @Override
    public void showComments(String content_group_id, String group_id) {

    }
}