package com.madxstudio.co8.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.EventAdapter;
import com.madxstudio.co8.entity.BirthdayEntity;
import com.madxstudio.co8.entity.EventEntity;
import com.madxstudio.co8.entity.UpdateEvent;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.PreferencesUtil;
import com.madxstudio.co8.widget.InteractivePopupWindow;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madxstudio.co8.ui.EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events
 * Use the {@link com.madxstudio.co8.ui.EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends BaseFragment<MainActivity> {

    private static final String Tag = EventFragment.class.getSimpleName();
    public InteractivePopupWindow popupWindow, popupWindowAddPhoto;
    private static final int GET_DELAY_RIGHT = 0x28;
    private static final int GET_DELAY_ADD_PHOTO = 0x30;
//    private ProgressDialog mProgressDialog;

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

    @Override
    protected void setParentTitle() {
        setTitle(getString(R.string.title_tab_event));
    }


    private RecyclerView rvList;
    private EventAdapter adapter;
    public List<EventEntity> data = new ArrayList<EventEntity>();
    public List<BirthdayEntity> birthdayEvents = new ArrayList<BirthdayEntity>();
    private RelativeLayout eventStart;

    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;
    private int startIndex = 0;
    private final static int offset = 20;
    private int currentPage = 1;
    private boolean loading;

    LinearLayoutManager llm;
    private View vProgress;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DELAY_RIGHT:
                    popupWindow = new InteractivePopupWindow(getParentActivity(), getParentActivity().rightButton, getParentActivity().getResources().getString(R.string.text_tip_create_event), 0);
                    popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                        @Override
                        public void popDismiss() {
                            PreferencesUtil.saveValue(getActivity(), InteractivePopupWindow.INTERACTIVE_TIP_CREATE_EVENT, true);
                        }
                    });
                    popupWindow.showPopupWindow(true);
                    break;
                case GET_DELAY_ADD_PHOTO:
                    if (MainActivity.interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)) {
                        popupWindowAddPhoto = MainActivity.interactivePopupWindowMap.get(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO);
                        popupWindowAddPhoto.showPopupWindowUp();
                    }
                    break;
                case Constant.ACTION_EVENT_UPDATE_BIRTHDAY:
                    swipeRefreshLayout.setRefreshing(true);
                    isRefresh = true;
                    startIndex = 0;
                    eventStart.setVisibility(View.GONE);
                    requestData();
                    break;

            }
            return false;
        }
    }) ;

    @Override
    public void initView() {


//        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
//        mProgressDialog.show();
        rvList = getViewById(R.id.rv_event_list);
        eventStart = getViewById(R.id.eventStart);
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
        rvList.setHasFixedSize(true);
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
                int count = Math.abs(totalItemCount - 5);
                if ((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= count && dy > 0) {
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (MainActivity.IS_INTERACTIVE_USE && !PreferencesUtil.getValue(getActivity(), InteractivePopupWindow.INTERACTIVE_TIP_CREATE_EVENT, false)) {
                handler.sendEmptyMessage(GET_DELAY_RIGHT);
            }
            EventBus.getDefault().registerSticky(this);

        } else {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(UpdateEvent event) {
        if (event.getCount() == Constant.ACTION_EVENT_UPDATE_BIRTHDAY) {
            EventBus.getDefault().removeStickyEvent(event);
            handler.sendEmptyMessage(Constant.ACTION_EVENT_UPDATE_BIRTHDAY);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTION_EVENT_CREATE:
                case Constant.ACTION_EVENT_UPDATE:
                case Constant.ACTION_EVENT_UPDATE_BIRTHDAY:
                    handler.sendEmptyMessage(Constant.ACTION_EVENT_UPDATE_BIRTHDAY);
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

        new HttpTools(getActivity()).get(url, null, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                if (vProgress != null)
                    vProgress.setVisibility(View.GONE);
                loading = false;
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
                    if (startIndex == 0 && (data == null || data.size() == 0)) {
                        eventStart.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                    } else {
                        eventStart.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    }
                    currentPage = 1;
                    startIndex = data.size();
//                    Log.i("startIndex=======================1", startIndex+"");

                    finishReFresh();
                    adapter = new EventAdapter(getParentActivity(), data, birthdayEvents);
                    adapter.setItemClickListener(new EventAdapter.ItemClickListener() {
                        @Override
                        public void topItemClick(List<BirthdayEntity> birthdayEntitys) {
//                            Intent intent = new Intent(getActivity(), BirthdayActivity.class);
//                            intent.putExtra("birthday_events", (Serializable) birthdayEntitys);
//                            startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE_BIRTHDAY);
                            //跳转生日界面
                            Intent intent = new Intent(getActivity(), com.madxstudio.co8.ui.more.BondAlert.BigDayActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void contentItemClick(EventEntity eventEntity) {
//                            if("1".equals(eventEntity.getGroup_event_status())){
                            //item的点击事件跳转到EventDetailActivity
//                            requestData();
//                            adapter.notifyDataSetChanged();

                            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
//                            intent.putExtra("event", eventEntity);
                            intent.putExtra("group_id", eventEntity.getGroup_id());
                            intent.putExtra("Content_group_id", eventEntity.getContent_group_id());
                            startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE);
//                            requestData();
//                            }

                        }
                    });
                    rvList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    if (isRefresh) {
                        finishReFresh();
                    } else {
                        eventStart.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
                    finishReFresh();
                } else {
                    eventStart.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                }
                MessageUtil.getInstance().showShortToast( R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


    }

    private void loadMoreEvent() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("show_birthday", "1");
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        params.put("start", "" + startIndex);
        params.put("limit", offset + "");

        String url = UrlUtil.generateUrl(Constant.API_EVENT_MAIN, params);
        new HttpTools(getActivity()).get(url, null, Tag, new HttpCallback() {
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
//                    MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);

                } catch (JSONException e) {
                    if (isRefresh) {
                        finishReFresh();
                    }
                    MessageUtil.getInstance().showShortToast( R.string.msg_action_failed);
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
                    finishReFresh();
                }
                MessageUtil.getInstance().showShortToast( R.string.msg_action_failed);
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

}
