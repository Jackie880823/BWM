package com.bondwithme.BondWithMe.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.EventCommentAdapter;
import com.bondwithme.BondWithMe.entity.EventCommentEntity;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FullyLinearLayoutManager;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.SendComment;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
 * {@link com.bondwithme.BondWithMe.ui.EventDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.bondwithme.BondWithMe.ui.EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment<EventDetailActivity> implements View.OnClickListener {
    private static final String Tag = EventDetailFragment.class.getSimpleName();
//    private final static String TAG = EventDetailFragment.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    private List<EventCommentEntity> data = new ArrayList<EventCommentEntity>();
    private TextView push_date;
    private TextView owner_name;
    private CircularNetworkImage owner_head;
    private TextView event_title;
    private NetworkImageView event_picture_4_location;
    private TextView event_desc;
    private TextView event_date;
    private TextView location_desc;
    private RelativeLayout btn_intent_all;
    private LinearLayout iv_intent_agree;
    private LinearLayout iv_intent_maybe;
    private LinearLayout iv_intent_no;
    private CardView btn_location;
    private TextView going_count;
    private TextView maybe_count;
    private TextView not_going_count;

    private boolean isStickerItemClick = false;

    private boolean isRefresh;
    private boolean isComment;
    private boolean isCommentBim;
    private int startIndex = 0;
    private int currentPage = 1;
    private final static int offset = 20;
    private boolean loading;
    MyDialog removeAlertDialog;

    private RecyclerView rvList;
    private EventEntity event;
    int colorIntentSelected;
    private ProgressBarCircularIndeterminate progressBar;

    private SendComment sendCommentView;
    private ScrollView Socontent;
    private LinearLayout expandFunctionLinear;//加号
    private LinearLayout stickerLinear;//表情库
    private EditText etChat;

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

    EventCommentEntity  stickerEntity = new EventCommentEntity();

    public static EventDetailFragment newInstance(String... params) {
//        event = eventEntity;
        return createInstance(new EventDetailFragment(), params);
    }

    public EventDetailFragment() {
        super();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bindData();
            requestComment();
        }
    };

    @Override
    public void onDestroy() {
        event = null;
        super.onDestroy();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_detail;
    }

    @Override
    public void initView() {
        isComment = true;
        isCommentBim = true;
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        mContext = getParentActivity();
//        messageAction = new MessageAction(mContext, handler);
        rvList = getViewById(R.id.rv_event_comment_list);
        final FullyLinearLayoutManager llm = new FullyLinearLayoutManager(getParentActivity());
//        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
//        rvList.setHasFixedSize(true);
        initAdapter();
        EventDetailActivity eventDetailActivity = new EventDetailActivity();
        eventDetailActivity.setTitleLeftClick(new EventDetailActivity.TitleLeftClick() {
            @Override
            public void Click() {
//                if (false) {
                    getParentActivity().finish();
//                } else {
//                    MessageUtil.showMessage(getActivity(), R.string.msg_date_not_commentbim_now);
//                }
            }
        });
        if (NetworkUtil.isNetworkConnected(getActivity())) {

            progressBar = getViewById(R.id.event_detail_progress_bar);

            etChat = getViewById(R.id.et_chat);
            expandFunctionButton = getViewById(R.id.ib_more);
            stickerImageButton = getViewById(R.id.ib_sticker);
            expandFunctionLinear = getViewById(R.id.ll_more);
            stickerLinear = getViewById(R.id.ll_sticker);
            Socontent = getViewById(R.id.content);

            push_date = getViewById(R.id.push_date);
            owner_name = getViewById(R.id.owner_name);
            owner_head = getViewById(R.id.owner_head);
            event_title = getViewById(R.id.event_title);
            event_picture_4_location = getViewById(R.id.event_picture_4_location);

            event_desc = getViewById(R.id.event_desc);
            event_date = getViewById(R.id.event_date);
            location_desc = getViewById(R.id.location_desc);
            btn_intent_all = getViewById(R.id.btn_intent_all);
            iv_intent_agree = getViewById(R.id.iv_intent_agree);
            iv_intent_maybe = getViewById(R.id.iv_intent_maybe);
            iv_intent_no = getViewById(R.id.iv_intent_no);
            going_count = getViewById(R.id.going_count);
            maybe_count = getViewById(R.id.maybe_count);
            not_going_count = getViewById(R.id.not_going_count);
//        comment_container = getViewById(R.id.comment_container);


            btn_intent_all.setOnClickListener(this);
            iv_intent_agree.setOnClickListener(this);
            iv_intent_maybe.setOnClickListener(this);
            iv_intent_no.setOnClickListener(this);
            option_status = getViewById(R.id.option_status);
            option_cancel = getViewById(R.id.option_cancel);
            event_options = getViewById(R.id.event_options);
            option_no_going = getViewById(R.id.image_no_going);
            option_maybe = getViewById(R.id.image_maybe);
            option_going = getViewById(R.id.image_going);
//        option_no_going = getViewById(R.id.option_no_going);
//        option_maybe = getViewById(R.id.option_maybe);
//        option_going = getViewById(R.id.option_going);
            colorIntentSelected = getResources().getColor(R.color.btn_bg_color_green_press);
//        colorIntentSelected = getResources().getColor(R.color.default_text_color_while);

            //bind data
//        bindData();

            option_cancel.setOnClickListener(this);
            option_no_going.setOnClickListener(this);
            option_maybe.setOnClickListener(this);
            option_going.setOnClickListener(this);
            Socontent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideAllViewState();
                    return false;
                }
            });

            sendCommentView = getViewById(R.id.send_comment);
            sendCommentView.initViewPager(getParentActivity(), this);
            sendCommentView.setCommentListenr(new SendComment.CommentListener() {
                @Override
                public void onStickerItemClick(String type, String folderName, String filName) {
                    isStickerItemClick = true;
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
                    if(mUri != null){
                        String path = LocalImageLoader.compressBitmap(getActivity(), FileUtil.getRealPathFromURI(getActivity(), mUri), 480, 800, false);
                        File file = new File(path);
                        if (file.exists()){
                            progressBar.setVisibility(View.VISIBLE);
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
                    if (isComment){
                        sendComment();
                    }
                    isStickerItemClick = false;
                }

                @Override
                public void onRemoveClick() {
                    isStickerItemClick = false;
                    stickerEntity.setSticker_type("");
                    stickerEntity.setSticker_group_path("");
                    stickerEntity.setSticker_name("");
                }
            });

            rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastVisibleItem = ((FullyLinearLayoutManager) llm).findLastVisibleItemPosition();
                    int totalItemCount = llm.getItemCount();
                    //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                    // dy>0 表示向下滑动
                    if ((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                        loading = true;
                        requestComment();
                    }
                }
            });



            getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
                @Override
                public boolean execute(View v) {
                    if(vProgress.getVisibility()==View.VISIBLE ){
                        return false;
                    }

                    if (R.id.tv_title == v.getId()) {
                        if (MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {

                            option_cancel.setVisibility(View.VISIBLE);
                            option_status.setVisibility(View.GONE);

                        } else {
                            option_cancel.setVisibility(View.GONE);
                            option_status.setVisibility(View.VISIBLE);

                        }
                        if("2".equals(event.getGroup_event_status())){
                            event_options.setVisibility(View.GONE);
                        }else {
                            if(event_options.getVisibility() == View.VISIBLE) {
                                event_options.setVisibility(View.GONE);
                                getParentActivity().title_icon.setImageResource(R.drawable.arrow_down);
                            } else {
                                event_options.setVisibility(View.VISIBLE);
                                getParentActivity().title_icon.setImageResource(R.drawable.arrow_up);
                            }
                        }

                    } else if (R.id.ib_top_button_right == v.getId()) {
                        //打开编辑页面
                        intent = new Intent(getParentActivity(), EventEditActivity.class);
                        intent.putExtra("event", event);
                        getActivity().startActivityForResult(intent, 1);
//                        startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE);
                    }else if(v.getId() == getParentActivity().leftButton.getId()){
//                        if (isCommentBim){
                            getParentActivity().finish();
//                        }else {
//                            MessageUtil.showMessage(getActivity(), R.string.msg_date_not_commentbim_now);
//                        }

                    }
                    return false;
                }
            });
        }
//        initViewPager();

    }

//    private void initViewPager() {
//
////        if (isFinishing()) {
////            return;
////        }
//        // 开启一个Fragment事务
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        StickerMainFragment mainFragment = new StickerMainFragment();//selectStickerName, MessageChatActivity.this, groupId);
//        mainFragment.setPicClickListener(this);
//        transaction.replace(R.id.sticker_event_fragment, mainFragment);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.addToBackStack(null);
//        transaction.commitAllowingStateLoss();
//    }
    //适配器
    private void initAdapter() {
        adapter = new EventCommentAdapter(getParentActivity(), data, rvList);
        adapter.setCommentActionListener(new EventCommentAdapter.CommentActionListener() {
            @Override
            public void doLove(EventCommentEntity commentEntity, boolean love) {
                doLoveComment(commentEntity, love);
            }

            @Override
            public void doDelete(String commentId) {
                removeComment(commentId);
            }
        });
        rvList.setAdapter(adapter);
        RecyclerView.ItemAnimator animator = rvList.getItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(1000);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(event != null){
            bindData();
            requestComment();
        }else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            Thread.sleep(100);
                            if (getParentActivity() != null && getParentActivity().getDataDone) {
                                Message.obtain(handler).sendToTarget();
                                break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            });
            thread.start();
        }
    }

    /**
     * 刷新数据
     */
    public void bindData() {

        if (getParentActivity().getEventEntity() != null) {
            event = getParentActivity().getEventEntity();
            push_date.setText(MyDateUtils.getEventLocalDateStringFromUTC(getActivity(), event.getGroup_creation_date()));
            owner_name.setText(event.getUser_given_name());
            VolleyUtil.initNetworkImageView(getActivity(), owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, event.getGroup_owner_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            event_title.setText(event.getGroup_name());//暂用Group_name

            event_desc.setText(event.getText_description());

            event_date.setText(MyDateUtils.getEventLocalDateStringFromUTC(getActivity(), event.getGroup_event_date()));
            location_desc.setText(event.getLoc_name());

            if (MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                try {
                    going_count.setText((Integer.valueOf(event.getTotal_yes()) - 1) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                going_count.setText(event.getTotal_yes());
            }
            maybe_count.setText(event.getTotal_maybe());
            not_going_count.setText(event.getTotal_no());

            ResponseStatus[] statuses = ResponseStatus.values();

            for (ResponseStatus status : statuses) {
                if (status.getServerCode().equals(event.getGroup_member_response())) {
                    currentStatus = status;
                    break;
                }


            }
            changeIntentUI(currentStatus);
            vProgress.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(event.getLoc_latitude()) && !TextUtils.isEmpty(event.getLoc_longitude())) {
                event_picture_4_location.setVisibility(View.VISIBLE);
                VolleyUtil.initNetworkImageView(getActivity(), event_picture_4_location
                        ,LocationUtil.getLocationPicUrl(mContext, event.getLoc_latitude(), event.getLoc_longitude(),event.getLoc_type())
                        , R.drawable.network_image_default, R.drawable.network_image_default);

                event_picture_4_location.setOnClickListener(this);
//                btn_location.setOnClickListener(this);
            } else {
                event_picture_4_location.setVisibility(View.GONE);
            }
        }
    }

    private void doChangeResponse(ResponseStatus status) {
        changeIntentUI(status);
        updateIntentStatus(status);
    }

    private void changeIntentUI(ResponseStatus status) {
        if (status == null) {
            return;
        }
        switch (status) {
            case not_re:
                if(!MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())){
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
                if(!MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())){
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

    //发送大表情
    private void sendSticker(){
        if (NetworkUtil.isNetworkConnected(getActivity())){
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("content_group_id", event.getContent_group_id());
            params.put("comment_owner_id", MainActivity.getUser().getUser_id());
            params.put("content_type", "comment");
            params.put("comment_content", "");
//          Log.i("isStickerItemClick=====","true");
            params.put("sticker_group_path", stickerEntity.getSticker_group_path());
            params.put("sticker_name", stickerEntity.getSticker_name());
            params.put("sticker_type", stickerEntity.getSticker_type());

            new HttpTools(getActivity()).post(Constant.API_EVENT_POST_COMMENT, params,Tag, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onResult(String response) {
                    startIndex = 0;
                    isRefresh = true;
                    stickerEntity.setSticker_type("");
                    stickerEntity.setSticker_group_path("");
                    stickerEntity.setSticker_name("");

                    requestComment();
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    UIUtil.hideKeyboard(getActivity(), etChat);
                    progressBar.setVisibility(View.GONE);
//                    vProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
//                    UIUtil.hideKeyboard(getActivity(), et_comment);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    progressBar.setVisibility(View.GONE);
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

    //发送评论
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
        }else {
            if (NetworkUtil.isNetworkConnected(getActivity())) {
                progressBar.setVisibility(View.VISIBLE);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("content_group_id", event.getContent_group_id());
                params.put("comment_owner_id", MainActivity.getUser().getUser_id());
                params.put("content_type", "comment");
                params.put("comment_content", etChat.getText().toString().trim());
//            if(isStickerItemClick){
                if(false){
//                Log.i("isStickerItemClick=====","true");
                    params.put("sticker_group_path", stickerEntity.getSticker_group_path());
                    params.put("sticker_name", stickerEntity.getSticker_name());
                    params.put("sticker_type", stickerEntity.getSticker_type());

                }else{
//                Log.i("isStickerItemClick=====","false");
                    params.put("sticker_group_path", "");
                    params.put("sticker_name", "");
                    params.put("sticker_type", "");
                }

                new HttpTools(getActivity()).post(Constant.API_EVENT_POST_COMMENT, params,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResult(String response) {
                        startIndex = 0;
                        isRefresh = true;
                        isComment = true;
                        stickerEntity.setSticker_type("");
                        stickerEntity.setSticker_group_path("");
                        stickerEntity.setSticker_name("");

                        etChat.setText("");
                        requestComment();
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                        UIUtil.hideKeyboard(getActivity(), etChat);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
//                    UIUtil.hideKeyboard(getActivity(), et_comment);
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                        progressBar.setVisibility(View.GONE);
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


    class uploadBimapTask extends AsyncTask<Map, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Map... params) {

            new HttpTools(App.getContextInstance()).upload(Constant.API_COMMENT_POST_TEXT, params[0],Tag, new HttpCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }

                @Override
                public void onResult(String response) {
                    startIndex = 0;
                    isRefresh = true;
                    isCommentBim = true;
                    mUri = null;
                    requestComment();
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

    class CompressBitmapTask extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... params) {
            if(params == null) {
                return null;
            }
            return LocalImageLoader.compressBitmap(App.getContextInstance(), FileUtil.getRealPathFromURI(App.getContextInstance(), params[0]), 480, 800, false);
        }

        @Override
        protected void onPostExecute(String path) {
            submitPic(path);
        }

        private void submitPic(String path) {
            File f = new File(path);
            if(!f.exists()) {
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("content_group_id", event.getContent_group_id());
            params.put("comment_owner_id", MainActivity.getUser().getUser_id());
            params.put("content_type", "comment");
            params.put("file", f);
            params.put("photo_fullsize", "1");


            new HttpTools(App.getContextInstance()).upload(Constant.API_EVENT_COMMENT_PIC_POST, params,Tag, new HttpCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }

                @Override
                public void onResult(String string) {
                    startIndex = 0;
                    isRefresh = true;
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

    @Override
    public void requestData() {

    }


    public void requestComment() {
        if (NetworkUtil.isNetworkConnected(getActivity())) {
            if (event == null || MainActivity.getUser() == null)
                return;

            HashMap<String, String> jsonParams = new HashMap<String, String>();

            jsonParams.put("user_id", MainActivity.getUser().getUser_id());
            jsonParams.put("group_id", event.getGroup_id());
            jsonParams.put("content_group_id", event.getContent_group_id());
            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);//转化换成json

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("condition", jsonParamsString);
            params.put("start", startIndex + "");
            params.put("limit", offset + "");

            String url = UrlUtil.generateUrl(Constant.API_EVENT_COMMENT, params);

            new HttpTools(getActivity()).get(url, null,Tag, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
//                    vProgress.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String response) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    data = gson.fromJson(response, new TypeToken<ArrayList<EventCommentEntity>>() {
                    }.getType());
                    if (isRefresh) {
                        isRefresh = false;
                        currentPage = 1;
                        initAdapter();
                        adapter.notifyDataSetChanged();
                    } else {
                        startIndex += data.size();
                        adapter.addData(data);
                    }
                    if (adapter != null && adapter.getItemCount() > 0) {
                        getViewById(R.id.comment_split_line).setVisibility(View.VISIBLE);
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
        } else {
            MessageUtil.showMessage(getActivity(), R.string.msg_no_internet);
        }
    }

    private void goInvitedStutus() {
        intent = new Intent(getActivity(), InvitedStatusActivity.class);
        intent.putExtra("event", event);
        //打开好友选择页面
        startActivity(intent);
    }



    private void cancelEvent() {

        RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_EVENT_CANCEL, event.getGroup_id()), null);

        new HttpTools(getActivity()).put(requestInfo,Tag, new HttpCallback() {
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
        if (TextUtils.isEmpty(serverCode))
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
        new HttpTools(getActivity()).put(requestInfo,Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    going_count.setText(result.getString("total_yes"));
                    maybe_count.setText(result.getString("total_maybe"));
                    not_going_count.setText(result.getString("total_no"));
                    getParentActivity().setResult(Activity.RESULT_OK);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                } catch (JSONException e) {
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

    private void hideAllViewState() {
        UIUtil.hideKeyboard(getParentActivity(), etChat);
        expandFunctionLinear.setVisibility(View.GONE);
        stickerLinear.setVisibility(View.GONE);
        expandFunctionButton.setImageResource(R.drawable.chat_plus_normal);
        stickerImageButton.setImageResource(R.drawable.chat_expression_normal);
    }

    enum ResponseStatus {
        not_re("1"),go("1"), maybe("3"), not_go("2");

        private String mServerCode;

        private ResponseStatus(String serverCode) {
            mServerCode = serverCode;
        }

        ResponseStatus getStatusByCode(String serverCode) {

            if (go.getServerCode().equals(serverCode)) {
                return go;
            } else if (maybe.getServerCode().equals(serverCode)) {
                return maybe;
            } else if (not_go.getServerCode().equals(serverCode)) {
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

        switch (v.getId()) {
            case R.id.btn_intent_all:
                goInvitedStutus();
                break;
            case R.id.btn_location:
            case R.id.event_picture_4_location:
                if (TextUtils.isEmpty(event.getLoc_latitude()) || TextUtils.isEmpty(event.getLoc_longitude())) {
                    return;
                }
                LocationUtil.goNavigation(getActivity(), Double.valueOf(event.getLoc_latitude()), Double.valueOf(event.getLoc_longitude()),event.getLoc_type());
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
                if (ResponseStatus.go != currentStatus) {
                    doChangeResponse(ResponseStatus.go);
                }
                break;
            case R.id.image_maybe:
//            case R.id.option_maybe:
                event_options.setVisibility(View.GONE);
                if (ResponseStatus.maybe != currentStatus) {
                    doChangeResponse(ResponseStatus.maybe);
                }
                break;
            case R.id.image_no_going:
//            case R.id.option_no_going:

                event_options.setVisibility(View.GONE);
                if (ResponseStatus.not_go != currentStatus) {
                    doChangeResponse(ResponseStatus.not_go);
                }
                break;
        }
    }

    private void getEventResponseInfos() {
        if (event == null)
            return;


        new HttpTools(getActivity()).get(String.format(Constant.API_EVENT_RESPONSE_INFOS, event.getGroup_id()), null,Tag, new HttpCallback() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject result;
                    result = new JSONObject(response);
                    going_count.setText(result.getString("total_yes"));
                    maybe_count.setText(result.getString("total_maybe"));
                    not_going_count.setText(result.getString("total_no"));
                } catch (JSONException e) {
                    e.printStackTrace();
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
     * 删除评论
     * @param commentId
     */
    private void removeComment(final String commentId) {

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_comment_del));

        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.event_accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_COMMENT_DELETE, commentId), null);
                new HttpTools(getActivity()).delete(requestInfo,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                        if(vProgress!=null)
                            vProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String string) {
                        getParentActivity().setResult(Activity.RESULT_OK);

                        startIndex = 0;
                        isRefresh = true;
                        requestComment();
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
        if (!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }
    }

    private void doLoveComment(final EventCommentEntity commentEntity, final boolean love) {


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("comment_id", commentEntity.getComment_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());
        RequestInfo requestInfo = new RequestInfo(Constant.API_EVENT_COMMENT_LOVE, params);
        new HttpTools(getActivity()).post(requestInfo,Tag, new HttpCallback() {
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

//    List<Uri> pickUries = new ArrayList();
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        sendComment.onActivityResult(requestCode, resultCode, data);
//        if (getParentActivity().RESULT_OK == resultCode) {
//            switch (requestCode) {
//                // 如果是直接从相册获取
//                case REQUEST_HEAD_PHOTO:
//                    pickUries.clear();
//                    if (data != null) {
//                        if (SDKUtil.IS_JB) {
//                            ClipData clipData = data.getClipData();
//                            if (clipData != null) {
//                                int size = clipData.getItemCount();
//                                for (int i = 0; i < size; i++) {
//                                    Uri uri = clipData.getItemAt(i).getUri();
//                                    pickUries.add(uri);
//                                }
//                            } else {
//                                pickUries.add(data.getData());
//                            }
//                        } else {
//                            pickUries.add(data.getData());
//                        }
//                        for (Uri uri : pickUries) {
//                            EventCommentEntity msgEntity = new EventCommentEntity();
//                            msgEntity.setSticker_type(".png");
//                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
//                            msgEntity.setUri(uri);
//                            adapter.addMsgEntity(msgEntity);
//                            Log.i("相册uri=====", uri.toString());
//                        }
//                        //handler.sendEmptyMessage(SEN_MESSAGE_FORM_ALBUM);
//                    }
//
//                    break;
//
//                // 如果是调用相机拍照时
//                case REQUEST_HEAD_CAMERA:
//                    uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mContext, CACHE_PIC_NAME_TEMP));
//                    handler.sendEmptyMessage(SEN_MESSAGE_FORM_CAMERA);
//                    break;
//
//                // 取得裁剪后的图片
//                case REQUEST_HEAD_FINAL:
//                    break;
//                case REQUEST_GET_GROUP_NAME:
////                    tvTitle.setText(data.getStringExtra("group_name"));
//                    break;
//
//                default:
//                    break;
//
//            }
//        }
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case Constant.ACTION_EVENT_UPDATE:
//                    if (data != null && data.getSerializableExtra("event") != null) {
//                        event = (EventEntity) data.getSerializableExtra("event");
//                        if (event != null) {
//                            bindData();
//                        }
//                    }
//                    getParentActivity().setResult(Activity.RESULT_OK);
//                    getEventResponseInfos();
//                    break;
//            }
//        }
//    }

    /**
     * 上传照片
     * @param uri
     */
    private void uploadImage(Uri uri) {
        String path = LocalImageLoader.compressBitmap(mContext, FileUtil.getRealPathFromURI(mContext, uri), 480, 800, false);
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("content_group_id", event.getContent_group_id());
        params.put("comment_owner_id", MainActivity.getUser().getUser_id());
        params.put("content_type", "comment");
        params.put("file", file);
        params.put("photo_fullsize", "1");

        new HttpTools(getActivity()).upload(Constant.API_EVENT_COMMENT_PIC_POST, params,Tag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                startIndex = 0;
                isRefresh = true;
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


    /**
     * fragment 再次显示的时候刷新数据
     * @param isVisibleToUser
     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            //相当于Fragment的onResume
//            if (event != null) {
//                bindData();
//                requestComment();
//            } else {
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        while (true) {
//                            if (getParentActivity() != null && getParentActivity().getDataDone) {
//                                break;
//                            }
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        bindData();
//                        requestComment();
//                    }
//                }.execute();
//            }
//        } else {
//            //相当于Fragment的onPause
//        }
//    }


//     /**
//      * 打开相册
//      */
//    private void openAlbum() {
//        intent = new Intent(Intent.ACTION_PICK, null);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(intent, REQUEST_HEAD_PHOTO);
//
//    }
//
//    /**
//     * 打开相机
//     */
//    private void openCamera() {
//        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra("camerasensortype", 2);
//        // 下面这句指定调用相机拍照后的照片存储的路径
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                .fromFile(PicturesCacheUtil.getCachePicFileByName(mContext,
//                        CACHE_PIC_NAME_TEMP)));
//        // 图片质量为高
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        intent.putExtra("return-data", false);
//        startActivityForResult(intent, REQUEST_HEAD_CAMERA);
//    }

//    List<Uri> pickUries = new ArrayList();
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (getParentActivity().RESULT_OK == resultCode) {
//            switch (requestCode) {
//                // 如果是直接从相册获取
//                case REQUEST_HEAD_PHOTO:
//                    pickUries.clear();
//                    if (data != null) {
//                        if (SDKUtil.IS_JB) {
//                            ClipData clipData = data.getClipData();
//                            if (clipData != null) {
//                                int size = clipData.getItemCount();
//                                for (int i = 0; i < size; i++) {
//                                    Uri uri = clipData.getItemAt(i).getUri();
//                                    pickUries.add(uri);
//                                }
//                            } else {
//                                pickUries.add(data.getData());
//                            }
//                        } else {
//                            pickUries.add(data.getData());
//                        }
//                        for (Uri uri : pickUries) {
//                            MsgEntity msgEntity = new MsgEntity();
//                            msgEntity.setSticker_type(".png");
//                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
//                            msgEntity.setUri(uri);
////                            messageChatAdapter.addMsgEntity(msgEntity);
//                        }
//                        handler.sendEmptyMessage(SEN_MESSAGE_FORM_ALBUM);
//                    }
//
//                    break;
//
//                // 如果是调用相机拍照时
//                case REQUEST_HEAD_CAMERA:
//                    uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mContext, CACHE_PIC_NAME_TEMP));
//                    handler.sendEmptyMessage(SEN_MESSAGE_FORM_CAMERA);
//                    break;
//
//                // 取得裁剪后的图片
//                case REQUEST_HEAD_FINAL:
//                    break;
//                case REQUEST_GET_GROUP_NAME:
//                    tvTitle.setText(data.getStringExtra("group_name"));
//                    break;
//
//                default:
//                    break;
//
//            }
//        }
//    }


}
