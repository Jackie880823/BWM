package com.madxstudio.co8.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.NewsAdapter;
import com.madxstudio.co8.entity.NewsEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.interfaces.NewsViewClickListener;
import com.madxstudio.co8.ui.wall.WallMembersOrGroupsActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.WallUtil;
import com.madxstudio.co8.widget.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 16/1/22.
 */
public class NewsFragment extends BaseFragment<MainActivity> implements NewsViewClickListener {
    private static final String TAG = "NewsFragment";
    View mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 8;
    private boolean loading;
    private List<NewsEntity> data = new ArrayList<>();
    private NewsAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    //    private TextView tvNoData;
//    private LinearLayout llNoData;
    private View newsEmpty;

    public static NewsFragment newInstance(String... params) {
        return createInstance(new NewsFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_news_fragment;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);
//        tvNoData = getViewById(R.id.tv_no_data_display);
//        llNoData = getViewById(R.id.ll_no_data_display);
        newsEmpty = getViewById(R.id.newsEmpty);
        newsEmpty.setVisibility(View.GONE);
        rvList = getViewById(R.id.rvList);
        llm = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                getData();
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
                int count = Math.abs(totalItemCount - 3);
                if (!loading && count != 0 && lastVisibleItem >= count && dy > 0) {
                    loading = true;
                    getData();//再请求数据
                }
            }
        });
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.ACTION_EVENT_UPDATE_BIRTHDAY:
                    swipeRefreshLayout.setRefreshing(true);
                    isRefresh = true;
                    startIndex = 0;
                    getData();
                    break;
            }

        }
    };

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        mProgressDialog.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTION_NEWS_CREATE:
                    handler.sendEmptyMessage(Constant.ACTION_EVENT_UPDATE_BIRTHDAY);
                    break;
                case Constant.INTENT_UPDATE_DIARY:
                    if (data == null) {
                        return;
                    }
                    List<NewsEntity> newsEntities = adapter.getData();

                    int position = data.getIntExtra(Constant.POSITION, -1);
                    LogUtil.d(TAG, "onActivityResult& position: " + position);

                    if (position >= 0 && position < newsEntities.size()) {
                        NewsEntity newsEntity;
                        newsEntity = (NewsEntity) data.getSerializableExtra(Constant.WALL_ENTITY);
                        if (newsEntity != null) {
                            newsEntities.set(position, newsEntity);
                            adapter.notifyItemChanged(position);
                        }

                        String commentCount = data.getStringExtra(Constant.COMMENT_COUNT);
                        if (!TextUtils.isEmpty(commentCount)) {
                            newsEntity = newsEntities.get(position);
                            if (!newsEntity.getComment_count().equals(commentCount)) {
                                newsEntity.setComment_count(commentCount);
                                adapter.notifyItemChanged(position);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put("start", "" + startIndex);
        params.put("limit", "" + offSet);
        params.put("user_id", MainActivity.getUser().getUser_id());


        String url = UrlUtil.generateUrl(Constant.API_GET_NEWS_LIST, params);
        new HttpTools(getActivity()).get(url, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<NewsEntity>>() {
                }.getType());
                if (data != null) {
                    if (isRefresh) {
                        startIndex = data.size();
                        finishReFresh();
                        initAdapter();
                    } else {
                        startIndex += data.size();
                        adapter.add(data);
                    }
                } else {
                    finishReFresh();
                }
                loading = false;


                LogUtil.d("NewsActivity", "item count" + adapter.getItemCount());
                //no data!!!
                if (data != null && !data.isEmpty()) {
                    newsEmpty.setVisibility(View.GONE);
                } else if (adapter.getItemCount() <= 0 && data.isEmpty() && !getActivity().isFinishing()) {
                    newsEmpty.setVisibility(View.VISIBLE);
//                    tvNoData.setText(getResources().getString(R.string.text_no_date_news));
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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

    private void initAdapter() {
        adapter = new NewsAdapter(this, getActivity(), data);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);
    }

    @Override
    public void remove(NewsEntity newsEntity) {
        showDeleteDialog(newsEntity);
    }

    @Override
    public void showLovedMember(String viewer_id, String refer_id, String type) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_LOVED_USER);
        intent.putExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID, viewer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_REFER_ID, refer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_TYPE, type);
        startActivity(intent);
    }

    MyDialog removeAlertDialog;

    private void showDeleteDialog(final NewsEntity newsEntity) {
        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_diary_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("delete=====标题", newsEntity.getContent_title());
                LogUtil.d("delete=====作者", newsEntity.getUser_given_name());
                LogUtil.d("delete=====useId", newsEntity.getUser_id());
                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, newsEntity.getContent_group_id()), null);
                new HttpTools(getActivity()).put(requestInfo, TAG, new HttpCallback() {
                    @Override
                    public void onStart() {
                        mProgressDialog.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        mProgressDialog.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String string) {
//                        MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
                        List<NewsEntity> data = adapter.getData();
                        int index = data.indexOf(newsEntity);
                        data.remove(newsEntity);
                        adapter.notifyItemRemoved(index);
                    }

                    @Override
                    public void onError(Exception e) {
                        mProgressDialog.setVisibility(View.GONE);
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
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.text_dialog_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if (!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }
    }
}
