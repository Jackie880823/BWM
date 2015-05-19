package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.action.MessageAction;
import com.madx.bwm.adapter.EventCommentAdapter;
import com.madx.bwm.entity.EventCommentEntity;
import com.madx.bwm.entity.EventEntity;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.util.UIUtil;
import com.madx.bwm.widget.CircularNetworkImage;
import com.madx.bwm.widget.FullyLinearLayoutManager;
import com.madx.bwm.widget.MyDialog;
import com.madx.bwm.widget.SendComment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.EventDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment<EventDetailActivity> implements View.OnClickListener {

    private final static String TAG = EventDetailFragment.class.getSimpleName();

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
    private int startIndex = 0;
    private int currentPage = 1;
    private final static int offset = 20;
    private boolean loading;
    MyDialog removeAlertDialog;

    private RecyclerView rvList;
    private EventEntity event;
    private EditText et_comment;
    //发送按钮
    private LinearLayout btn_submit;
    //    private TextView btn_submit;
    int colorIntentSelected;
    private ProgressBarCircularIndeterminate progressBar;

    private SendComment sendCommentView;
    private ScrollView Socontent;
    private LinearLayout expandFunctionLinear;//加号
    private LinearLayout stickerLinear;//表情库
    private EditText etChat;
    /**
     * 扩展功能按钮
     */
    private ImageButton expandFunctionButton;
    /**
     * 表情按钮
     */
    private ImageButton stickerImageButton;
    private TextView cameraTextView;//相机
    private TextView albumTextView;//相册
    private TextView locationTextView;//地图
    private TextView videoTextView;//视频
    private TextView contactTextView;//名片

    /**
     * 放置表情图标库的默认文件夹名称
     */
    public static final String STICKERS_NAME = "stickers";
    private final static int REQUEST_GET_GROUP_NAME = 4;

    /*相册和相机使用的参数*/
    private final static int REQUEST_HEAD_PHOTO = 100;
    private final static int REQUEST_HEAD_CAMERA = 101;
    private final static int REQUEST_HEAD_FINAL = 102;

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    private int indexPage = 1;

    public final static int GET_LATEST_MESSAGE = 0X100;
    public final static int SEN_MESSAGE_FORM_CAMERA = 0X101;
    public final static int SEN_MESSAGE_FORM_ALBUM = 0X102;
    public final static int SEND_TEXT_MESSAGE = 0X103;
    public final static int SEND_PIC_MESSAGE = 0X104;
    public final static int GET_HISTORY_MESSAGE = 0X105;
    public final static int GET_SEND_OVER_MESSAGE = 0X106;
    public int INITIAL_LIMIT = 10;

    public MessageAction messageAction;

    Intent intent;
    private Context mContext;
    private Uri uri;//原图uri
    private Uri mUri;

    EventCommentEntity  stickerEntity = new EventCommentEntity();

    public static EventDetailFragment newInstance(String... params) {
//        event = eventEntity;
        return createInstance(new EventDetailFragment(), params);
    }

    public EventDetailFragment() {
        super();

    }

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case GET_LATEST_MESSAGE:
////                    progressDialog.dismiss();
//                    List<MsgEntity> msgList = (List<MsgEntity>) msg.obj;
//                    if (null == msgList || msgList.size() == 0) {
////                        empty_message.setVisibility(View.VISIBLE);
////                        swipeRefreshLayout.setVisibility(View.GONE);
//                    } else {
////                        empty_message.setVisibility(View.GONE);
////                        swipeRefreshLayout.setVisibility(View.VISIBLE);
////                        messageChatAdapter.addData(msgList);
//                    }
//                    break;
//                case GET_SEND_OVER_MESSAGE:
//                    List<EventCommentEntity> msgSendList = (List<EventCommentEntity>) msg.obj;
//                    if (null != msgSendList) {
//                        adapter.addSendData(msgSendList);
//                    }
//                    break;
//                case GET_HISTORY_MESSAGE:
//                    List<EventCommentEntity> msgHistoryList = (List<EventCommentEntity>) msg.obj;
////                    swipeRefreshLayout.setRefreshing(false);
//                    if (null != msgHistoryList || msgHistoryList.size() == 0) {
//                        break;
//                    }
//                    indexPage++;
//                    if (null != msgHistoryList) {
//                        adapter.addHistoryData(msgHistoryList);
//                    }
//                    break;
//                case SEN_MESSAGE_FORM_ALBUM:
//                    //上传相册图片
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (pickUries != null) {
//                                for (Uri uri : pickUries) {
//                                    uploadImage(uri);
//                                }
//                            }
//                        }
//                    }).start();
//                    break;
//                case SEN_MESSAGE_FORM_CAMERA:
////                    上传相机拍照图片
//                    EventCommentEntity msgEntity = new EventCommentEntity();
//                    msgEntity.setSticker_type(".png");
//                    msgEntity.setUser_id(MainActivity.getUser().getUser_id());
//                    msgEntity.setUri(uri);
//                    adapter.addMsgEntity(msgEntity);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            uploadImage(uri);
//                        }
//                    }).start();
//
//                    break;
//                case SEND_PIC_MESSAGE:
//                    JSONObject jsonObject = (JSONObject) msg.obj;
//                    if (null == jsonObject) {
//                        break;
//                    }
//                    try {
//                        String postType = jsonObject.optString("postType");
//                        if ("postPhoto".equals(postType) || "postSticker".equals(postType)) {
////                            getMsg(INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                case SEND_TEXT_MESSAGE:
//                    JSONObject textJsonObject = (JSONObject) msg.obj;
//                    try {
//                        if ("postText".equals(textJsonObject.getString("postType"))) {
////                            getMsg(INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//            }
//        }
//    };

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
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getParentActivity(), R.string.text_loading);
        }
        mContext = getParentActivity();
//        messageAction = new MessageAction(mContext, handler);
        rvList = getViewById(R.id.rv_event_comment_list);
        final FullyLinearLayoutManager llm = new FullyLinearLayoutManager(getParentActivity());
//        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
//        rvList.setHasFixedSize(true);
        initAdapter();
        if (NetworkUtil.isNetworkConnected(getActivity())) {

            progressBar = getViewById(R.id.progressBar);


//            et_comment = getViewById(R.id.et_comment);
//            btn_submit = getViewById(R.id.btn_submit);

//            btn_submit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (TextUtils.isEmpty(et_comment.getText())) {
//                        MessageUtil.showMessage(getActivity(), R.string.alert_comment_null);
//                    } else {
//                        sendComment();
//                    }
//                }
//            });
            etChat = getViewById(R.id.et_chat);
            expandFunctionButton = getViewById(R.id.ib_more);
            stickerImageButton = getViewById(R.id.ib_sticker);
            expandFunctionLinear = getViewById(R.id.ll_more);
            stickerLinear = getViewById(R.id.ll_sticker);
            Socontent = getViewById(R.id.content);

            cameraTextView = getViewById(R.id.camera_tv);
            albumTextView = getViewById(R.id.album_tv);
            locationTextView = getViewById(R.id.location_tv);
            videoTextView = getViewById(R.id.video_tv);
            contactTextView = getViewById(R.id.contact_tv);

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
                }

                @Override
                public void onReceiveBitmapUri(Uri uri) {
                    mUri = uri;
                    hideAllViewState();
                    if(mUri != null) {
                        new CompressBitmapTask().execute(mUri);
                        return;
                    }
                }

                @Override
                public void onSendCommentClick(EditText et) {
                    sendComment();
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
                    if (R.id.tv_title == v.getId()) {
                        if (MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                            option_cancel.setVisibility(View.VISIBLE);
                            option_status.setVisibility(View.GONE);
                        } else {
                            option_cancel.setVisibility(View.GONE);
                            option_status.setVisibility(View.VISIBLE);

                        }
                        if (event_options.getVisibility() == View.VISIBLE) {
                            event_options.setVisibility(View.GONE);
                            getParentActivity().title_icon.setImageResource(R.drawable.arrow_down);
                        } else {
                            event_options.setVisibility(View.VISIBLE);
                            getParentActivity().title_icon.setImageResource(R.drawable.arrow_up);
                        }

                    } else if (R.id.ib_top_button_right == v.getId()) {
                        intent = new Intent(getParentActivity(), EventEditActivity.class);
                        intent.putExtra("event", event);
                        getActivity().startActivityForResult(intent, 1);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (event != null) {
            bindData();
            requestComment();
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (true) {
                        if (getParentActivity() != null && getParentActivity().getDataDone) {
                            break;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    bindData();
                    requestComment();
                }
            }.execute();
        }

    }

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
                ;
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

            if (!TextUtils.isEmpty(event.getLoc_latitude()) && !TextUtils.isEmpty(event.getLoc_longitude())) {
                event_picture_4_location.setVisibility(View.VISIBLE);
                VolleyUtil.initNetworkImageView(getActivity(), event_picture_4_location
                        , String.format(Constant.MAP_API_GET_LOCATION_PIC, event.getLoc_latitude() + "," + event.getLoc_longitude(), getActivity().getString(R.string.google_map_pic_size), event.getLoc_latitude() + "," + event.getLoc_longitude())
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
    //    TextView option_cancel;
    ImageView option_no_going;
    ImageView option_maybe;
    ImageView option_going;
//    LinearLayout option_no_going;
//    LinearLayout option_maybe;
//    LinearLayout option_going;


    public EventCommentAdapter adapter;

    //发送评论
    private void sendComment() {

        if (NetworkUtil.isNetworkConnected(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("content_group_id", event.getContent_group_id());
            params.put("comment_owner_id", MainActivity.getUser().getUser_id());
            params.put("content_type", "comment");
            params.put("comment_content", etChat.getText().toString().trim());
            if(isStickerItemClick){
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



            new HttpTools(getActivity()).post(Constant.API_EVENT_POST_COMMENT, params, new HttpCallback() {
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

    //发送图片
    class CompressBitmapTask extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... params) {
            if(params == null) {
                return null;
            }
            return LocalImageLoader.compressBitmap(getActivity(), FileUtil.getRealPathFromURI(getActivity(), params[0]), 480, 800, false);
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


            new HttpTools(getActivity()).upload(Constant.API_EVENT_COMMENT_PIC_POST, params, new HttpCallback() {
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

            new HttpTools(getActivity()).get(url, null, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
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
        startActivity(intent);
    }

    private void goNavigation() {
        if (TextUtils.isEmpty(event.getLoc_latitude()) || TextUtils.isEmpty(event.getLoc_longitude())) {
            return;
        }
        try {
            //14为缩放比例
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(event.getLoc_latitude()), Double.valueOf(event.getLoc_longitude()), Double.valueOf(event.getLoc_latitude()), Double.valueOf(event.getLoc_longitude()));
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            getActivity().startActivity(intent);
        } catch (Exception e) {
            MessageUtil.showMessage(getActivity(), R.string.msg_no_map_app);
        }
    }

    private void cancelEvent() {

        RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_EVENT_CANCEL, event.getGroup_id()), null);

        new HttpTools(getActivity()).put(requestInfo, new HttpCallback() {
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
        new HttpTools(getActivity()).put(requestInfo, new HttpCallback() {
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
        go("1"), maybe("3"), not_go("2");

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
                goNavigation();
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


        new HttpTools(getActivity()).get(String.format(Constant.API_EVENT_RESPONSE_INFOS, event.getGroup_id()), null, new HttpCallback() {

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

        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_COMMENT_DELETE, commentId), null);
                new HttpTools(getActivity()).delete(requestInfo, new HttpCallback() {
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

    private void doLoveComment(final EventCommentEntity commentEntity, final boolean love) {


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("comment_id", commentEntity.getComment_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());
        RequestInfo requestInfo = new RequestInfo(Constant.API_EVENT_COMMENT_LOVE, params);
        new HttpTools(getActivity()).post(requestInfo, new HttpCallback() {
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

        /**
         * Map<String, Object> params = new HashMap<>();
         params.put("content_creator_id", MainActivity.getUser().getUser_id());
         params.put("group_id", groupId);
         params.put("content_type", "post");
         params.put("content_group_public", "0");
         params.put("photo_caption", "");
         params.put("multiple", "0");
         params.put("file", file);
         params.put("photo_fullsize", "1");
         messageAction.doRequest(MessageAction.REQUEST_UPLOAD, params, Constant.API_MESSAGE_POST_TEXT, SEND_PIC_MESSAGE);

         */
        Map<String, Object> params = new HashMap<>();
        params.put("content_group_id", event.getContent_group_id());
        params.put("comment_owner_id", MainActivity.getUser().getUser_id());
        params.put("content_type", "comment");
//        params.put("content_group_public", "0");
//        params.put("photo_caption", "");
//        params.put("multiple", "0");
        params.put("file", file);
        params.put("photo_fullsize", "1");
        messageAction.doRequest(MessageAction.REQUEST_UPLOAD, params, Constant.API_COMMENT_POST_TEXT, SEND_PIC_MESSAGE);
    }

     /**
      * 打开相册
      */
    private void openAlbum() {
        intent = new Intent(Intent.ACTION_PICK, null);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_HEAD_PHOTO);

    }

    /**
     * 打开相机
     */
    private void openCamera() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 2);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                .fromFile(PicturesCacheUtil.getCachePicFileByName(mContext,
                        CACHE_PIC_NAME_TEMP)));
        // 图片质量为高
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, REQUEST_HEAD_CAMERA);
    }

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
