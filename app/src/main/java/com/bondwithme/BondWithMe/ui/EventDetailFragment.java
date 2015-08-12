package com.bondwithme.BondWithMe.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.EventCommentAdapter;
import com.bondwithme.BondWithMe.entity.EventCommentEntity;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;
import com.bondwithme.BondWithMe.widget.SendComment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.CircularProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment<EventDetailActivity> implements View.OnClickListener {
    private static final String Tag = EventDetailFragment.class.getSimpleName();
    //    private final static String TAG = EventDetailFragment.class.getSimpleName();


    private List<EventCommentEntity> data = new ArrayList<EventCommentEntity>();
    private String group_id;
    private boolean isCommentRefresh = true;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private boolean isCommentTest;

    private boolean isRefresh;
    private boolean isComment;
    private boolean isCommentBim;
    private int startIndex = 0;
    private int currentPage = 1;
    private final static int offset = 10;
    private boolean loading;
    MyDialog removeAlertDialog;

    private RecyclerView rvList;
    private EventEntity event;
    int colorIntentSelected;
    private CircularProgress progressBar;
    private View defaultComment;

    private SendComment sendCommentView;
    private ScrollView Socontent;
    private LinearLayout expandFunctionLinear;//加号
    private LinearLayout stickerLinear;//表情库
    private EditText etChat;
    private View connentLayout;

    private View vProgress;
    /**
     * 扩展功能按钮
     */
    private ImageButton expandFunctionButton;
    /**
     * 表情按钮
     */
    private ImageButton stickerImageButton;

    Intent intent;
    private Context mContext;
    private Uri mUri;

    EventCommentEntity stickerEntity = new EventCommentEntity();

    public static EventDetailFragment newInstance(String... params) {
        //        event = eventEntity;
        return createInstance(new EventDetailFragment(), params);
    }

    public EventDetailFragment() {
        super();

    }


    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(sendCommentView != null) {
            sendCommentView.commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        event = null;
        super.onDestroy();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_detail_test;
    }

    @Override
    public void initView() {
        isComment = true;
        isCommentBim = true;
        group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        mContext = getParentActivity();
        //        messageAction = new MessageAction(mContext, handler);
        rvList = getViewById(R.id.rv_event_comment_list);
        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        //        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        //        rvList.setHasFixedSize(true);
        initAdapter();
        EventDetailActivity eventDetailActivity = new EventDetailActivity();
        eventDetailActivity.setTitleLeftClick(new EventDetailActivity.TitleLeftClick() {
            @Override
            public void Click() {
                if(false) {
                    getParentActivity().finish();
                } else {
                    //                    MessageUtil.showMessage(getActivity(), R.string.msg_date_not_commentbim_now);
                }
            }
        });
        if(NetworkUtil.isNetworkConnected(getActivity())) {


            etChat = getViewById(R.id.et_chat);
            expandFunctionButton = getViewById(R.id.ib_more);
            stickerImageButton = getViewById(R.id.ib_sticker);
            expandFunctionLinear = getViewById(R.id.ll_more);
            stickerLinear = getViewById(R.id.ll_sticker);
            connentLayout = getViewById(R.id.rv_event_comment_list);
            option_status = getViewById(R.id.option_status);
            option_cancel = getViewById(R.id.option_cancel);
            event_options = getViewById(R.id.event_options);
            option_no_going = getViewById(R.id.image_no_going);
            option_maybe = getViewById(R.id.image_maybe);
            option_going = getViewById(R.id.image_going);
            colorIntentSelected = getResources().getColor(R.color.btn_bg_color_green_press);
            getViewById(R.id.connent_Rl).setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideAllViewState();
                    return false;
                }
            });
            option_cancel.setOnClickListener(this);
            option_no_going.setOnClickListener(this);
            option_maybe.setOnClickListener(this);
            option_going.setOnClickListener(this);
            connentLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideAllViewState();
                    return false;
                }
            });


            swipeRefreshLayout = getViewById(R.id.swipe_archive_layout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    isRefresh = true;
                    isCommentRefresh = true;
                    startIndex = 0;
                    data.clear();
                    requestData();
                }

            });
            sendCommentView = getViewById(R.id.send_comment);
            sendCommentView.initViewPager(getParentActivity(), this);
            sendCommentView.setCommentListener(new SendComment.CommentListener() {
                @Override
                public void onStickerItemClick(String type, String folderName, String filName) {
                    stickerEntity.setSticker_type(type);
                    stickerEntity.setSticker_group_path(folderName);
                    stickerEntity.setSticker_name(filName);
                    sendSticker();
                }

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveBitmapUri(Uri uri) {
                    isCommentBim = true;
                    hideAllViewState();
                    mUri = uri;
                    if(mUri != null) {
                        String path = LocalImageLoader.compressBitmap(getActivity(), mUri, 480, 800, false);
                        File file = new File(path);
                        if(file.exists()) {
//                            progressBar.setVisibility(View.VISIBLE);
                            //                            vProgress.setVisibility(View.VISIBLE);
                            Map<String, Object> param = new HashMap<>();
                            param.put("content_group_id", event.getContent_group_id());
                            param.put("comment_owner_id", MainActivity.getUser().getUser_id());
                            param.put("content_type", "comment");
                            param.put("file", file);
                            param.put("photo_fullsize", "1");
                            uploadBimapTask task = new uploadBimapTask();
                            //for not work in down 11
                            if(SDKUtil.IS_HONEYCOMB) {
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
                            } else {
                                task.execute(param);
                            }
                        }
                    }

                }

                @Override
                public void onSendCommentClick(EditText et) {
                    if(isComment) {
                        sendComment();
                    }
                }

                @Override
                public void onRemoveClick() {
                    stickerEntity.setSticker_type("");
                    stickerEntity.setSticker_group_path("");
                    stickerEntity.setSticker_name("");
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
                    if((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
//                        LogUtil.i("eventdetail====", "onScrolled& getComments");
//                        currentPage++;
                        loading = true;
                        requestComment();
                    }
                }
            });


            getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
                @Override
                public boolean execute(View v) {
                    if(vProgress.getVisibility() == View.VISIBLE) {
                        return false;
                    }

                    if(R.id.tv_title == v.getId()) {
                        if(MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {

                            option_cancel.setVisibility(View.VISIBLE);
                            option_status.setVisibility(View.GONE);

                        } else {
                            option_cancel.setVisibility(View.GONE);
                            option_status.setVisibility(View.VISIBLE);

                        }

                        if(event_options.getVisibility() == View.VISIBLE) {
                            event_options.setVisibility(View.GONE);
                            getParentActivity().title_icon.setImageResource(R.drawable.arrow_down);
                        } else {
                            event_options.setVisibility(View.VISIBLE);
                            getParentActivity().title_icon.setImageResource(R.drawable.arrow_up);
                        }

                    } else if(R.id.ib_top_button_right == v.getId()) {
                        //打开编辑页面
                        intent = new Intent(getParentActivity(), EventEditActivity.class);
                        intent.putExtra("event", event);
                        getActivity().startActivityForResult(intent, 1);
                        //                        startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE);
                    } else if(v.getId() == getParentActivity().leftButton.getId()) {
                        if(isCommentBim) {
                            getParentActivity().finish();
                        } else {
                            //                            MessageUtil.showMessage(getActivity(), R.string.msg_date_not_commentbim_now);
                        }

                    }
                    return false;
                }
            });
        }
        //        initViewPager();

    }

    //适配器
    private void initAdapter() {
        adapter = new EventCommentAdapter(getParentActivity(),event, data, rvList);
        adapter.setCommentActionListener(new EventCommentAdapter.CommentActionListener() {
            @Override
            public void doLove(EventCommentEntity commentEntity, boolean love) {
                doLoveComment(commentEntity, love);
            }

            @Override
            public void doDelete(String commentId) {
                removeComment(commentId);
            }

            @Override
            public void showOriginalPic(String User_id,String File_id) {
                Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);
                ArrayList<PhotoEntity> datas = new ArrayList();
                PhotoEntity peData = new PhotoEntity();
                peData.setUser_id(User_id);
                peData.setFile_id(File_id);
                peData.setPhoto_caption(Constant.Module_Original);
                peData.setPhoto_multipe("false");
                datas.add(peData);
                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);
                startActivity(intent);
            }
            @Override
            public void setIntentAll(EventEntity entity,int memeber) {
                goInvitedStutus(memeber);
            }


        });
        adapter.setUpdateListener(new EventCommentAdapter.ListViewItemViewUpdateListener() {
            @Override
            public void updateHeadView(View headView) {
                initHeadView(headView);
            }

            @Override
            public void updateListSecondView(View listSecondView) {
                initListSecondView(listSecondView);
            }
        });
        rvList.setAdapter(adapter);
        RecyclerView.ItemAnimator animator = rvList.getItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(1000);
    }

    private void initListSecondView(View listSecondView){
        progressBar = (CircularProgress) listSecondView.findViewById(R.id.event_detail_progress_bar);

    }

    private void initHeadView(View headView){
        defaultComment = headView.findViewById(R.id.default_comment);
//        defaultComment.setVisibility(View.GONE);
    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if(event != null) {
////            bindData();
//            requestComment();
//        } else {
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while(true) {
//                        try {
//                            Thread.sleep(100);
//                            if(getParentActivity() != null && getParentActivity().getDataDone) {
//                                Message.obtain(handler).sendToTarget();
//                                break;
//                            }
//                        } catch(Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            });
//            thread.start();
//        }
//    }


    private void doChangeResponse(ResponseStatus status) {
        changeIntentUI(status);
        updateIntentStatus(status);
    }

    private void changeIntentUI(ResponseStatus status) {
        if(status == null) {
            return;
        }
        switch(status) {
            case not_re:
                if(!MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                    if(event_options.getVisibility() == View.GONE) {
                        event_options.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case go:
                currentStatus = status;
                option_going.setImageResource(R.drawable.status_going_press);
                option_maybe.setImageResource(R.drawable.status_maybe_normal);
                option_no_going.setImageResource(R.drawable.status_not_going_normal);
                break;
            case maybe:
                currentStatus = status;
                option_going.setImageResource(R.drawable.status_going_normal);
                option_maybe.setImageResource(R.drawable.status_maybe_press);
                option_no_going.setImageResource(R.drawable.status_not_going_normal);
                if(!MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                    if(event_options.getVisibility() == View.GONE) {
                        event_options.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case not_go:
                currentStatus = status;
                option_going.setImageResource(R.drawable.status_going_normal);
                option_maybe.setImageResource(R.drawable.status_maybe_normal);
                option_no_going.setImageResource(R.drawable.status_not_going_press);
                break;
        }
    }

    RelativeLayout event_options;
    RelativeLayout option_status;
    LinearLayout option_cancel;
    ImageView option_no_going;
    ImageView option_maybe;
    ImageView option_going;
    public EventCommentAdapter adapter;

    /**
     * 发送大表情
     */
    private void sendSticker() {
        if(NetworkUtil.isNetworkConnected(getActivity())) {
//            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("content_group_id", event.getContent_group_id());
            params.put("comment_owner_id", MainActivity.getUser().getUser_id());
            params.put("content_type", "comment");
            params.put("comment_content", "");
            //          Log.i("isStickerItemClick=====","true");
            params.put("sticker_group_path", stickerEntity.getSticker_group_path());
            params.put("sticker_name", stickerEntity.getSticker_name());
            params.put("sticker_type", stickerEntity.getSticker_type());

            new HttpTools(getActivity()).post(Constant.API_EVENT_POST_COMMENT, params, Tag, new HttpCallback() {
                @Override
                public void onStart() {
                    if(progressBar != null){
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFinish() {
//                        data.clear();
//                        adapter.removeCommentData();
//                        requestComment();
                    if(progressBar != null){
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onResult(String response) {
                    startIndex = 0;
//                    isRefresh = true;
                    currentPage = 1;
                    stickerEntity.setSticker_type("");
                    stickerEntity.setSticker_group_path("");
                    stickerEntity.setSticker_name("");
                    data.clear();
                    adapter.removeCommentData();
                    requestComment();
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    UIUtil.hideKeyboard(getActivity(), etChat);
//                    progressBar.setVisibility(View.GONE);
                    //                    vProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    //                    UIUtil.hideKeyboard(getActivity(), et_comment);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
//                    progressBar.setVisibility(View.GONE);
                    //                    vProgress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
    }

    /**
     * 发送评论
     */
    private void sendComment() {
        //        String commentText = et.getText().toString();
        //        if(TextUtils.isEmpty(etChat.getText().toString().trim()) && isStickerItemClick==false) {
        //            // 如果没有输入字符且没有添加表情，不发送评论
        //            MessageUtil.showMessage(getActivity(), R.string.msg_no_content);
        //            return;
        //        }
        isComment = false;
        if(TextUtils.isEmpty(etChat.getText().toString().trim())) {
            // 如果没有输入字不发送评论
            MessageUtil.showMessage(getActivity(), R.string.msg_no_content);
            isComment = true;
            return;
        } else {
            if(NetworkUtil.isNetworkConnected(getActivity())) {
//                progressBar.setVisibility(View.VISIBLE);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("content_group_id", event.getContent_group_id());
                params.put("comment_owner_id", MainActivity.getUser().getUser_id());
                params.put("content_type", "comment");
                params.put("comment_content", etChat.getText().toString().trim());
                //            if(isStickerItemClick){
                if(false) {
                    //                Log.i("isStickerItemClick=====","true");
                    params.put("sticker_group_path", stickerEntity.getSticker_group_path());
                    params.put("sticker_name", stickerEntity.getSticker_name());
                    params.put("sticker_type", stickerEntity.getSticker_type());

                } else {
                    //                Log.i("isStickerItemClick=====","false");
                    params.put("sticker_group_path", "");
                    params.put("sticker_name", "");
                    params.put("sticker_type", "");
                }

                new HttpTools(getActivity()).post(Constant.API_EVENT_POST_COMMENT, params, Tag, new HttpCallback() {
                    @Override
                    public void onStart() {
                        if(progressBar != null){
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFinish() {
//                        data.clear();
//                        adapter.removeCommentData();
//                        requestComment();
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onResult(String response) {
                        isCommentTest = true;
                        startIndex = 0;
//                        isRefresh = true;
                        currentPage = 1;
                        stickerEntity.setSticker_type("");
                        stickerEntity.setSticker_group_path("");
                        stickerEntity.setSticker_name("");
                        etChat.setText("");
                        data.clear();
                        adapter.removeCommentData();
                        requestComment();
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                        UIUtil.hideKeyboard(getActivity(), etChat);
//                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        //                    UIUtil.hideKeyboard(getActivity(), et_comment);
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
//                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
            } else {
                MessageUtil.showMessage(getActivity(), R.string.msg_no_internet);
            }
        }


    }

    /**
     * 异步上传照片
     */
    class uploadBimapTask extends AsyncTask<Map, Void, Boolean>{

        @Override
        protected void onPreExecute() {
//            LogUtil.i("AsyncTask_开始", "");
            if(progressBar != null){
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(Map... params) {

            new HttpTools(App.getContextInstance()).upload(Constant.API_COMMENT_POST_TEXT, params[0], Tag, new HttpCallback() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    if(progressBar != null){
                        progressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onResult(String response) {
                    startIndex = 0;
//                    isRefresh = true;
                    isCommentBim = true;
                    currentPage = 1;
                    mUri = null;
                    data.clear();
                    adapter.removeCommentData();
                    requestComment();
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    UIUtil.hideKeyboard(getActivity(), etChat);
                    getParentActivity().setResult(Activity.RESULT_OK);

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
            return null;
        }

    }

    @Override
    public void requestData() {
        //请求detail数据
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("group_id", group_id);
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_DETAIL, params);
        new HttpTools(getParentActivity()).get(url, params,Tag, new HttpCallback() {
            @Override
            public void onStart() {
                if(vProgress.getVisibility()==View.GONE){
                    vProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
//                //如果只是Activity回调就不用在刷新评论
//                if(data.size() < 1 ){
//                    requestComment();
//                }

            }

            @Override
            public void onResult(String response) {
                event = new Gson().fromJson(response, EventEntity.class);
                vProgress.setVisibility(View.GONE);
                try{
                    isRefresh = false;
                    currentPage = 1;//还原为第一页
                    initAdapter();
//                    if(isRefresh) {
//                        isRefresh = false;
//                        currentPage = 1;//还原为第一页
//                        initAdapter();
//                    } else {
////                    startIndex += detailDate.size();
//                        if(adapter == null) {
//                            initAdapter();
//                            adapter.notifyDataSetChanged();
//                        } else {
////                        abookends.addData(detailDate);
//                        }
//                    }
                    ResponseStatus[] statuses = ResponseStatus.values();
                    for(ResponseStatus status : statuses) {
                        if(status.getServerCode().equals(event.getGroup_member_response())) {
                            currentStatus = status;
                            break;
                        }

                    }
                    changeIntentUI(currentStatus);
                    swipeRefreshLayout.setRefreshing(false);
                    loading = false;
                    if(data.size() < 1 ){
                        requestComment();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    reInitDataStatus();
                }

                if (MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                    getParentActivity().rightButton.setImageResource(R.drawable.btn_edit);

                    getParentActivity().rightButton.setVisibility(View.VISIBLE);
                    if (MyDateUtils.isBeforeDate(MyDateUtils.formatTimestamp2Local(MyDateUtils.dateString2Timestamp(event.getGroup_event_date()).getTime()))) {
                        getParentActivity().rightButton.setImageResource(R.drawable.icon_edit_press);
                        getParentActivity().rightButton.setEnabled(false);
                    }
                    if("2".equals(event.getGroup_event_status())){
                        getParentActivity().rightButton.setImageResource(R.drawable.icon_edit_press);
                        getParentActivity().title_icon.setVisibility(View.GONE);
                        getParentActivity().rightButton.setEnabled(false);
                    }
                } else {
                    getParentActivity().rightButton.setVisibility(View.INVISIBLE);
                }

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

    }

    /**
     * 请求评论数据
     */
    public void requestComment() {
        if(NetworkUtil.isNetworkConnected(getActivity())) {
            if(event == null || MainActivity.getUser() == null)
                return;

            HashMap<String, String> jsonParams = new HashMap<String, String>();
//            defaultComment.setVisibility(View.GONE);
            jsonParams.put("user_id", MainActivity.getUser().getUser_id());
            jsonParams.put("group_id", event.getGroup_id());
            jsonParams.put("content_group_id", event.getContent_group_id());
            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);//转化换成json

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("condition", jsonParamsString);
            params.put("start", startIndex + "");
            params.put("limit", offset + "");

            String url = UrlUtil.generateUrl(Constant.API_EVENT_COMMENT, params);

            new HttpTools(getActivity()).get(url, null, Tag, new HttpCallback() {
                @Override
                public void onStart() {
//                    if(adapter != null && adapter.getItemCount() == 1){
//                        defaultComment.setVisibility(View.GONE);
//                    }
                }

                @Override
                public void onFinish() {
//                    progressBar.setVisibility(View.GONE);
                    //                    vProgress.setVisibility(View.GONE);

                }

                @Override
                public void onResult(String response) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    data = gson.fromJson(response, new TypeToken<ArrayList<EventCommentEntity>>() {}.getType());
                    if(isRefresh){
                        isRefresh = false;
                        swipeRefreshLayout.setRefreshing(false);
                        currentPage = 1;//还原为第一页
                        initAdapter();
                    }else {
                        startIndex += data.size();
                        if(adapter != null){
                            adapter.addData(data);
                        }else {
                            initAdapter();
                        }
                    }
//                    if(adapter != null && adapter.getItemCount() > 1){
//                        defaultComment.setVisibility(View.GONE);
//                    }
                    loading = false;
//                    //如果有评论，则隐藏进度条
//                    if(adapter != null && adapter.getItemCount() > 1) {
//                        getViewById(R.id.comment_split_line).setVisibility(View.VISIBLE);
//                    }
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
        } else {
            MessageUtil.showMessage(getActivity(), R.string.msg_no_internet);
        }
    }

    private void reInitDataStatus() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        isCommentRefresh = false;
        startIndex = 0;
        loading = false;
    }
    /**
     * 打开选择好友界面
     */
    private void goInvitedStutus(int memeber) {
        intent = new Intent(getActivity(), InvitedStatusActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("tabIndex",memeber);
        startActivity(intent);
    }

    /**
     * 取消event
     */
    private void cancelEvent() {

        RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_EVENT_CANCEL, event.getGroup_id()), null);

        new HttpTools(getActivity()).put(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
            }

            @Override
            public void onError(Exception e) {
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

    /**
     * update the user intent
     *
     * @param status 1- yes, 2- no, 3- maybe
     */
    private void updateIntentStatus(ResponseStatus status) {
        String serverCode = status.getServerCode();
        if(TextUtils.isEmpty(serverCode))
            return;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", event.getGroup_id());
        jsonParams.put("group_owner_id", event.getGroup_owner_id());
        jsonParams.put("group_member_response", serverCode);
        event.setGroup_event_status(serverCode);
        jsonParams.put("member_id", MainActivity.getUser().getUser_id());
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_EVENT_INTENT, event.getGroup_id());
        requestInfo.jsonParam = jsonParamsString;
        new HttpTools(getActivity()).put(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                requestData();
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject result = new JSONObject(response);
//                    going_count.setText(result.getString("total_yes"));
//                    maybe_count.setText(result.getString("total_maybe"));
//                    not_going_count.setText(result.getString("total_no"));
                    getParentActivity().setResult(Activity.RESULT_OK);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
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

    private ResponseStatus currentStatus;

    /**
     * 缩进输入框
     */
    private void hideAllViewState() {
        UIUtil.hideKeyboard(getParentActivity(), etChat);
        expandFunctionLinear.setVisibility(View.GONE);
        stickerLinear.setVisibility(View.GONE);
        expandFunctionButton.setImageResource(R.drawable.chat_plus_normal);
        stickerImageButton.setImageResource(R.drawable.chat_expression_normal);
    }

    enum ResponseStatus {
        not_re("0"), go("1"), maybe("3"), not_go("2");

        private String mServerCode;

        private ResponseStatus(String serverCode) {
            mServerCode = serverCode;
        }

        ResponseStatus getStatusByCode(String serverCode) {

            if(go.getServerCode().equals(serverCode)) {
                return go;
            } else if(maybe.getServerCode().equals(serverCode)) {
                return maybe;
            } else if(not_go.getServerCode().equals(serverCode)) {
                return not_go;
            }
            return null;
        }

        String getServerCode() {
            return mServerCode;
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
//            case R.id.btn_intent_all:
//                goInvitedStutus();
//                break;
            case R.id.btn_location:
            case R.id.event_picture_4_location:
                if(TextUtils.isEmpty(event.getLoc_latitude()) || TextUtils.isEmpty(event.getLoc_longitude())) {
                    return;
                }
                LocationUtil.goNavigation(getActivity(), Double.valueOf(event.getLoc_latitude()), Double.valueOf(event.getLoc_longitude()), event.getLoc_type());
                break;
            case R.id.option_cancel:
                event_options.setVisibility(View.GONE);
                cancelEvent();
                getParentActivity().setResult(Activity.RESULT_OK);
                getParentActivity().finish();
                break;
            case R.id.image_going:
                //            case R.id.option_going:
                event_options.setVisibility(View.GONE);
                if(ResponseStatus.go != currentStatus) {
                    doChangeResponse(ResponseStatus.go);
                }
                break;
            case R.id.image_maybe:
                //            case R.id.option_maybe:
                event_options.setVisibility(View.GONE);
                if(ResponseStatus.maybe != currentStatus) {
                    doChangeResponse(ResponseStatus.maybe);
                }
                break;
            case R.id.image_no_going:
                //            case R.id.option_no_going:

                event_options.setVisibility(View.GONE);
                if(ResponseStatus.not_go != currentStatus) {
                    doChangeResponse(ResponseStatus.not_go);
                }
                break;
        }
    }


    /**
     * 删除评论
     *
     * @param commentId
     */
    private void removeComment(final String commentId) {

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_comment_del));

        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.event_accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_COMMENT_DELETE, commentId), null);
                new HttpTools(getActivity()).delete(requestInfo, Tag, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                        if(vProgress != null)
                            vProgress.setVisibility(View.GONE);

//                        data.clear();
//                        adapter.removeCommentData();
//                        requestComment();
                    }

                    @Override
                    public void onResult(String string) {
                        getParentActivity().setResult(Activity.RESULT_OK);
                        startIndex = 0;
//                        isRefresh = true;
                        data.clear();
                        adapter.removeCommentData();
                        requestComment();
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                        UIUtil.hideKeyboard(getActivity(), etChat);
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
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.event_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if(!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }
    }

    /**
     * 点赞
     * @param commentEntity
     * @param love
     */
    private void doLoveComment(final EventCommentEntity commentEntity, final boolean love) {


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("comment_id", commentEntity.getComment_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());
        RequestInfo requestInfo = new RequestInfo(Constant.API_EVENT_COMMENT_LOVE, params);
        new HttpTools(getActivity()).post(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {

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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        Log.i(TAG, "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);
        sendCommentView.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 上传照片
     *
     * @param uri
     */
    private void uploadImage(Uri uri) {
        String path = LocalImageLoader.compressBitmap(mContext, FileUtil.getRealPathFromURI(mContext, uri), 480, 800, false);
        File file = new File(path);
        if(!file.exists()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("content_group_id", event.getContent_group_id());
        params.put("comment_owner_id", MainActivity.getUser().getUser_id());
        params.put("content_type", "comment");
        params.put("file", file);
        params.put("photo_fullsize", "1");

        new HttpTools(getActivity()).upload(Constant.API_EVENT_COMMENT_PIC_POST, params, Tag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                startIndex = 0;
//                isRefresh = true;
                isCommentBim = true;
                mUri = null;
                requestComment();
                getParentActivity().setResult(Activity.RESULT_OK);
                //                    Log.i("onResult====",string);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

}