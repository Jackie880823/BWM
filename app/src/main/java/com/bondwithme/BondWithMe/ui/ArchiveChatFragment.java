package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.ArchiveChatAdapter;
import com.bondwithme.BondWithMe.entity.ArchiveChatEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.ArchiveChatViewClickListener;
import com.bondwithme.BondWithMe.ui.more.Archive.ArchiveGroupCommentActivity;
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
public class ArchiveChatFragment extends BaseFragment<BaseActivity> implements ArchiveChatViewClickListener {
    private static final String TAG = ArchiveChatFragment.class.getSimpleName();
    //如果是0，则是group，否则是private
//    private String tag;
    private RecyclerView rvList;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh = true;
    private int startIndex = 0;
    private int offset = 10;
    private int currentPage = 1;
    private int startIndexSearch = 0;
    private int offsetSearch = 10;
    private int currentPageSearch = 1;
    private boolean loading;
    private boolean isSearch;
    LinearLayoutManager llm;
    private String archive_id;
    private View vProgress;

    private String Tap;//0是群组传进来的，1成员传进来的
    private String group_id;
    private String group_name;

    private TextView searchText;
    private ImageButton searchButton;

    private ArchiveChatAdapter adapter;
    private ArchiveChatAdapter searchAdapter;
    private List<ArchiveChatEntity> data = new ArrayList<>();
    private List<ArchiveChatEntity> searchData = new ArrayList<>();

    private boolean isEtImport = true;

    public static ArchiveChatFragment newInstance(String... params) {
        return createInstance(new ArchiveChatFragment(), params);
    }

    public ArchiveChatFragment() {
        super();
    }
//    public ArchiveChatFragment(String params){
//        tag = params;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_archive_chat;
    }

    @Override
    public void initView() {
        //如果该fragment带参数
        if (getArguments() != null) {
            Tap = getArguments().getString(ARG_PARAM_PREFIX + "0");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
            group_name = getArguments().getString(ARG_PARAM_PREFIX + "2");
//            if (Tap.equals("0")){
//                group_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
//            }else {
//                user_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
//            }

        }
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        searchText = getViewById(R.id.et_search);
        searchButton = getViewById(R.id.bv_search);
        rvList = getViewById(R.id.rv_Archive_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();

        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                if (v.getId() == getParentActivity().leftButton.getId()) {
                    if (!isEtImport && isSearch) {
                        searchText.setText("");
                    } else {
                        getParentActivity().finish();
                    }
                }
                if (v.getId() == getParentActivity().rightButton.getId()) {
                    Intent intent = new Intent(getParentActivity(), GroupSettingActivity.class);
                    intent.putExtra("groupId", group_id);
                    intent.putExtra("groupName", group_name);
                    intent.putExtra("groupType", 1);
                    startActivity(intent);
                }
                return false;
            }
        });

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

        //搜索按钮监听事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) searchText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(
                                getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                if (!isEtImport) {
                    startIndex = 0;
                    isRefresh = true;
                    isSearch = true;
                    vProgress.setVisibility(View.VISIBLE);
                    searchData.clear();
                    searchData(searchText.getText().toString().trim());
                } else {
                    isSearch = false;
                }
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String etImport = searchText.getText().toString().trim();
                if (TextUtils.isEmpty(etImport)) {
                    isEtImport = true;
                    rvList.setAdapter(adapter);
//                    adapter.setDefaultData();
                } else {
                    isEtImport = false;
                }
            }
        });
        searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//        searchText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (KeyEvent.ACTION_DOWN == event.getAction()) {
                        //隐藏软键盘
                        ((InputMethodManager) searchText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(
                                        getActivity().getCurrentFocus().getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
//                    Log.i("onEditorAction====","onEditorAction");
                        if (!isEtImport) {
                            startIndex = 0;
                            isRefresh = true;
                            isSearch = true;
                            vProgress.setVisibility(View.VISIBLE);
                            searchData.clear();
                            searchData(searchText.getText().toString().trim());

//                            return true;
                        } else {
                            isSearch = false;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void initSearchAdapter() {
        searchAdapter = new ArchiveChatAdapter(getParentActivity(), searchData, Tap);
        searchAdapter.setPicClickListener(this);
        rvList.setAdapter(searchAdapter);
    }

    private void initAdapter() {
        adapter = new ArchiveChatAdapter(getParentActivity(), data, Tap);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);
    }

    @Override
    public void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("start", startIndex + "");
        params.put("limit", offset + "");
        params.put("group_id", group_id);
        params.put("view_user", MainActivity.getUser().getUser_id());
//        Log.i("group_id","");
        params.put("search_key", "");
        String url = UrlUtil.generateUrl(Constant.API_MORE_ARCHIVE_POSTING_LIST, params);

        new HttpTools(getActivity()).get(url, null, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                if (data == null) {
                    getParentActivity().finish();
                } else {
                    vProgress.setVisibility(View.GONE);
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
                try {
                    data = gson.fromJson(response, new TypeToken<ArrayList<ArchiveChatEntity>>() {
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
                    if (data.size() > 0) {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    }
                    loading = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    reInitDataStatus();
                } finally {
                    vProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
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

    private void searchData(String searchText) {
//        Log.i("searchText====",searchText+"");
//        Map<String,String> params = new HashMap<>();
//        params.put("start",startIndex + "");
//        params.put("limit",offset + "");
//        params.put("group_id",group_id);
//        params.put("view_user",MainActivity.getUser().getUser_id());
////        params.put("search_key",searchText);
//        params.put("search_key","");
//        String url = UrlUtil.generateUrl(Constant.API_MORE_ARCHIVE_POSTING_LIST, params);
        Map<String, String> params = new HashMap<>();
        params.put("start", startIndexSearch + "");
        params.put("limit", offset + "");
        params.put("group_id", group_id);
        params.put("view_user", MainActivity.getUser().getUser_id());
//        Log.i("group_id","");
//        params.put("search_key","");
        params.put("search_key", searchText);


        Log.i("searchText====", UrlUtil.mapToJsonstring(params));

//        String url = UrlUtil.generateUrl(Constant.API_MORE_ARCHIVE_POSTING_LIST, params);

        new HttpTools(getActivity()).get(Constant.API_MORE_ARCHIVE_POSTING_LIST, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                Log.i("22searchText====", response + "");
//                Log.i("response====",response+"");
                GsonBuilder gsonb = new GsonBuilder();
                //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                //DateDeserializer ds = new DateDeserializer();
                //给GsonBuilder方法单独指定Date类型的反序列化方法
                //gsonb.registerTypeAdapter(Date.class, ds);
                Gson gson = gsonb.create();
                try {
                    searchData = gson.fromJson(response, new TypeToken<ArrayList<ArchiveChatEntity>>() {
                    }.getType());
                    if (!isEtImport) {
                        if (isRefresh) {
                            startIndex = searchData.size();
                            currentPageSearch = 1;
                            finishReFresh();
                            initSearchAdapter();
                        } else {
                            startIndexSearch += searchData.size();
                            searchAdapter.add(searchData);
                        }
                    }
                    if (data.size() > 0) {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    }
                    loading = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    reInitDataStatus();
                } finally {
                    vProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
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

    private void reInitDataStatus() {
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
        Log.i("content_id====2", content_id + " ");
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
        Intent intent = new Intent(getActivity(), ArchiveGroupCommentActivity.class);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivity(intent);
    }

}
