package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.EventAdapter;
import com.madx.bwm.entity.BirthdayEntity;
import com.madx.bwm.entity.EventEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events
 * Use the {@link com.madx.bwm.ui.EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends BaseFragment<MainActivity> {

    private ProgressDialog mProgressDialog;

    public static EventFragment newInstance(String... params) {

        return createInstance(new EventFragment());
    }

    public EventFragment() {
        super();
        // Required empty public constructor
    }

//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event, container, false);
//    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event;
    }


    private RecyclerView rvList;
    private EventAdapter adapter;
    public List<EventEntity> data = new ArrayList<EventEntity>();
    public List<BirthdayEntity> birthdayEvents = new ArrayList<BirthdayEntity>();

    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;
    private int startIndex = 0;
    private final static int offset = 20;
    private int currentPage = 1;
    private boolean loading;

    LinearLayoutManager llm;

    @Override
    public void initView() {


        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
        mProgressDialog.show();
        rvList = getViewById(R.id.rv_event_list);

        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
//        rvList.setHasFixedSize(true);
        adapter = new EventAdapter(getParentActivity(), data, birthdayEvents);

        rvList.setAdapter(adapter);


        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = (llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if ((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    loadMoreEvent();//再请求数据
                }
                swipeRefreshLayout.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0);

            }
        });

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTION_EVENT_CREATE:
                case Constant.ACTION_EVENT_UPDATE:
                case Constant.ACTION_EVENT_UPDATE_BIRTHDAY:
                    swipeRefreshLayout.setRefreshing(true);
                    isRefresh = true;
                    startIndex = 0;
                    requestData();
                    break;
            }
        }
    }

    @Override
    public void requestData() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("show_birthday", "1");
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        params.put("start", "0");
        params.put("limit", offset + "");

        String url = UrlUtil.generateUrl(Constant.API_EVENT_MAIN, params);

        new HttpTools(getActivity()).get(url, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String response) {
                try {
                    GsonBuilder gsonb = new GsonBuilder();
                    //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                    //DateDeserializer ds = new DateDeserializer();
                    //给GsonBuilder方法单独指定Date类型的反序列化方法
                    //gsonb.registerTypeAdapter(Date.class, ds);
                    Gson gson = gsonb.create();
                    JSONObject jsonArray = new JSONObject(response);
                    data = gson.fromJson(jsonArray.getString("event_list"), new TypeToken<ArrayList<EventEntity>>() {
                    }.getType());
                    birthdayEvents = gson.fromJson(jsonArray.getString("birthday_alert"), new TypeToken<ArrayList<BirthdayEntity>>() {
                    }.getType());
                    currentPage = 1;
                    startIndex = data.size();
                    finishReFresh();
                    adapter = new EventAdapter(getParentActivity(), data, birthdayEvents);
                    adapter.setItemClickListener(new EventAdapter.ItemClickListener() {
                        @Override
                        public void topItemClick(List<BirthdayEntity> birthdayEntitys) {
                            Intent intent = new Intent(getActivity(), BirthdayActivity.class);
                            intent.putExtra("birthday_events", (Serializable) birthdayEntitys);
                            startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE_BIRTHDAY);
                        }

                        @Override
                        public void contentItemClick(EventEntity eventEntity) {
                            //item的点击事件跳转到EventDetailActivity
                            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
//                            intent.putExtra("event", eventEntity);
                            intent.putExtra("group_id", eventEntity.getGroup_id());
                            startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE);
                        }
                    });
                    rvList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    loading = false;

                } catch (JSONException e) {
                    if (isRefresh) {
                        finishReFresh();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
                    finishReFresh();
                }
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
        if (itemClickListener != null ) {
//            Log.i("itemClickListener=======================2", "");
            itemClickListener.topItemClick(data,birthdayEvents);
        }

    }

    private void loadMoreEvent() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("show_birthday", "0");
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        params.put("start", "" + startIndex);
        params.put("limit", offset + "");

        String url = UrlUtil.generateUrl(Constant.API_EVENT_MAIN, params);
        new HttpTools(getActivity()).get(url, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    GsonBuilder gsonb = new GsonBuilder();
                    //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                    //DateDeserializer ds = new DateDeserializer();
                    //给GsonBuilder方法单独指定Date类型的反序列化方法
                    //gsonb.registerTypeAdapter(Date.class, ds);
                    Gson gson = gsonb.create();
                    JSONObject jsonArray = new JSONObject(response);
                    data = gson.fromJson(jsonArray.getString("event_list"), new TypeToken<ArrayList<EventEntity>>() {
                    }.getType());
//                    birthdayEvents = gson.fromJson(jsonArray.getString("birthday_alert"), new TypeToken<ArrayList<BirthdayEntity>>() {
//                    }.getType());

                    startIndex += data.size();
                    adapter.add(data);
                    loading = false;
//                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);

                } catch (JSONException e) {
                    if (isRefresh) {
                        finishReFresh();
                    }
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
                    finishReFresh();
                }
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void topItemClick(List<EventEntity> data, List<BirthdayEntity> birthdayEvents);


    }

}
