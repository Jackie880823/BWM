package com.madx.bwm.ui;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.action.MessageAction;
import com.madx.bwm.adapter.ChatAdapter;
import com.madx.bwm.adapter.MessageChatAdapter;
import com.madx.bwm.adapter.MessageHorizontalListViewAdapter;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.MsgEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MyTextUtil;
import com.madx.bwm.util.SDKUtil;
import com.madx.bwm.util.UIUtil;
import com.madx.bwm.widget.HorizontalListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/4/24.
 */
public class MessageChatActivity extends BaseActivity implements View.OnTouchListener{
    private Context mContext;
    private ProgressDialog progressDialog;
    /**
     * 聊天布局
     */
    private RecyclerView recyclerView;
    /**
     * 扩展功能按钮
     */
    private ImageButton expandFunctionButton;
    /**
     * 表情按钮
     */
    private ImageButton stickerImageButton;
    /**
     * 文本输入框
     */
    private EditText etChat;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;
    private ViewPager mPager;
    private TextView cameraTextView;//相机
    private TextView albumTextView;//相册
    private TextView locationTextView;//地图
    private TextView videoTextView;//视频
    private TextView contactTextView;//名片
    private TextView sendTextView;//发送按钮
    private LinearLayout expandFunctionLinear;//加号
    private LinearLayout stickerLinear;//表情库

    List<String> stickerNameList = new ArrayList<>();
    private HorizontalListView horizontalListView;
    private List<String> sticker_List_Id = new ArrayList<>();
    private MessageStickerFragment fragment = null;

    private List<MsgEntity> msgList;

    private String groupId;//API的一个参数
    private int userOrGroupType = -1;//0为私聊，1为群聊
    private UserEntity userEntity;//是私聊信息
    private GroupEntity groupEntity;//群聊信息
    /**
     * 放置表情图标库的默认文件夹名称
     */
    public static final String STICKERS_NAME = "stickers";
    private final static int REQUEST_GET_GROUP_NAME = 4;

    /*相册和相机使用的参数*/
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

    private Intent intent;

    private int indexPage = 0;

    private Uri mCropImagedUri;//裁剪后的Uri
    private String imagePath;
    private Uri uri;//原图uri
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    public final static int GET_LATEST_MESSAGE = 0X100;
    public final static int SEN_MESSAGE_FORM_CAMERA = 0X101;
    public final static int SEN_MESSAGE_FORM_ALBUM = 0X102;
    public final static int SEND_TEXT_MESSAGE= 0X103;
    public final static int SEND_PIC_MESSAGE=0X104;
    public final static int GET_HISTORY_MESSAGE=0X105;
    public int INITIAL_LIMIT=10;

    public MessageAction messageAction;
    public MessageChatAdapter messageChatAdapter;
    public LinearLayoutManager llm;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_LATEST_MESSAGE:
                    progressDialog.dismiss();
                    List<MsgEntity> msgList=(List<MsgEntity>)msg.obj;
                    if(null!=msgList) {
                        messageChatAdapter.addData(msgList);
                    }
                    break;
                case GET_HISTORY_MESSAGE:
                    indexPage++;
                    List<MsgEntity> msgHistoryList=(List<MsgEntity>)msg.obj;
                    if(null!=msgHistoryList) {
                        messageChatAdapter.addHistoryData(msgHistoryList);
                    }
                    break;
                case SEN_MESSAGE_FORM_ALBUM:
                    //上传相册图片
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (pickUries != null) {
                                for (Uri uri : pickUries) {
                                    uploadImage(uri);
                                }
                            }
                        }
                    }).start();
                    break;
                case SEN_MESSAGE_FORM_CAMERA:
                    //上传相机拍照图片
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImage(uri);
                        }
                    }).start();
                    break;
                case SEND_PIC_MESSAGE:
                    String response=(String)msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("postPhoto".equals(jsonObject.getString("postType"))) {
                            getMsg(INITIAL_LIMIT, 0, GET_HISTORY_MESSAGE);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                case SEND_TEXT_MESSAGE:
                    JSONObject textJsonObject=(JSONObject)msg.obj;
                    try {
                        if ("postText".equals(textJsonObject.getString("postType"))) {
                            getMsg(INITIAL_LIMIT, 0, GET_HISTORY_MESSAGE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_message_chat;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        if (userOrGroupType == 0) {
            tvTitle.setText(userEntity.getUser_given_name());
        } else if (userOrGroupType == 1) {
            tvTitle.setText(groupEntity.getGroup_name());
        } else {
            tvTitle.setText("error");
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
        if (userOrGroupType == 1) {
            rightButton.setVisibility(View.VISIBLE);
        } else {
            rightButton.setVisibility(View.GONE);
        }
        rightButton.setImageResource(R.drawable.btn_group_setting);
    }

    @Override
    protected void titleRightEvent() {
        Intent intent;
        if (userOrGroupType == 0) {
            intent = new Intent(mContext, FamilyProfileActivity.class);
            intent.putExtra("member_id", userEntity.getUser_id());//传进来的,他人个人信息
            startActivity(intent);
        } else if (userOrGroupType == 1) {
            intent = new Intent(mContext, GroupSettingActivity.class);
            intent.putExtra("groupEntity", groupEntity);
            startActivityForResult(intent, REQUEST_GET_GROUP_NAME);
        }
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        userOrGroupType = getIntent().getIntExtra("type", -1);
        mContext = this;
        messageAction=new MessageAction(mContext,handler);
        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        progressDialog.show();
        msgList = new ArrayList<>();
        if (userOrGroupType == 0) {
            userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");
            groupId = userEntity.getGroup_id();
        } else if (userOrGroupType == 1) {
            groupEntity = (GroupEntity) getIntent().getExtras().getSerializable("groupEntity");
            groupId = groupEntity.getGroup_id();
        } else {
            finish();
        }
        setView();
        setAllListener();
        llm = new LinearLayoutManager(MessageChatActivity.this);
        recyclerView.setLayoutManager(llm);
        messageChatAdapter=new MessageChatAdapter(mContext,msgList,recyclerView);
        recyclerView.setAdapter(messageChatAdapter);
        getMsg(INITIAL_LIMIT, 0, GET_LATEST_MESSAGE);//接收对话消息
    }

    private void setView() {
        expandFunctionButton = getViewById(R.id.cb_1);
        stickerImageButton = getViewById(R.id.cb_2);
        recyclerView = getViewById(R.id.message_recyclerView);
        mPager = getViewById(R.id.viewpager);
        etChat = getViewById(R.id.et_chat);
        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        expandFunctionLinear = getViewById(R.id.ll_1);
        stickerLinear = getViewById(R.id.ll_2);
        cameraTextView = getViewById(R.id.camera_tv);
        albumTextView = getViewById(R.id.album_tv);
        locationTextView = getViewById(R.id.location_tv);
        videoTextView = getViewById(R.id.video_tv);
        contactTextView = getViewById(R.id.contact_tv);
        horizontalListView = getViewById(R.id.sticker_listView);
        sendTextView = getViewById(R.id.btn_send);
        initViewPager();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getMsg(INITIAL_LIMIT, indexPage*INITIAL_LIMIT-1, GET_HISTORY_MESSAGE);
            }

        });
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }

    private void addStickerList() {
        try {
            List<String> pathList = FileUtil.getAllFilePathsFromAssets(mContext, STICKERS_NAME);
            if (null != pathList) {
                for (String string : pathList) {
                    stickerNameList.add(STICKERS_NAME + File.separator + string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageList() {
        if (stickerNameList != null && stickerNameList.size() > 0) {
            for (String string : stickerNameList) {
                List<String> stickerAllNameList = FileUtil.getAllFilePathsFromAssets(mContext, string);
                if (null != stickerAllNameList) {
                    String iconPath = string + File.separator + stickerAllNameList.get(0);
                    sticker_List_Id.add(iconPath);
                }
            }
        }
    }

    private void initViewPager() {
        addStickerList();
        addImageList();
        final MessageHorizontalListViewAdapter horizontalListViewAdapter = new MessageHorizontalListViewAdapter(sticker_List_Id, mContext);
        horizontalListView.setAdapter(horizontalListViewAdapter);
        setTabSelection(0);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTabSelection(position);
                horizontalListViewAdapter.setChoosePosition(position);
                horizontalListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setTabSelection(int index) {
        String selectStickerName = stickerNameList.get(index);
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        if (fragment != null) {
            transaction.remove(fragment);
        }
        fragment = new MessageStickerFragment(selectStickerName, MessageChatActivity.this, groupId);
        transaction.add(R.id.message_frame, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.cb_1://扩展功能按钮
                if(expandFunctionLinear.getVisibility()== View.VISIBLE){
                    hideAllViewState();
                }else {
                    hideAllViewState();
                    expandFunctionLinear.setVisibility(View.VISIBLE);
                    expandFunctionButton.setImageResource(R.drawable.chat_plus_press);
                }
                recyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                break;
            case R.id.cb_2://表情功能按钮
                if(stickerLinear.getVisibility()== View.VISIBLE){
                    hideAllViewState();
                }else {
                    hideAllViewState();
                    stickerLinear.setVisibility(View.VISIBLE);
                    stickerImageButton.setImageResource(R.drawable.chat_expression_press);
                }
                recyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                break;
            case R.id.et_chat://文本框
                expandFunctionLinear.setVisibility(View.GONE);
                stickerLinear.setVisibility(View.GONE);
                expandFunctionButton.setImageResource(R.drawable.chat_plus_normal);
                stickerImageButton.setImageResource(R.drawable.chat_expression_normal);
                recyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                break;
            case R.id.camera_tv://打开相机
                openCamera();
                break;
            case R.id.album_tv://打开本地相册
                openAlbum();
                break;
            case R.id.location_tv://打开地图
                break;
            case R.id.video_tv://视频功能
                break;
            case R.id.contact_tv://打开名片
                break;
            case R.id.btn_send://信息发送按钮
                sendTextMessage();
                break;
        }
    }

    private void hideAllViewState() {
        UIUtil.hideKeyboard(mContext, etChat);
        expandFunctionLinear.setVisibility(View.GONE);
        stickerLinear.setVisibility(View.GONE);
        expandFunctionButton.setImageResource(R.drawable.chat_plus_normal);
        stickerImageButton.setImageResource(R.drawable.chat_expression_normal);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.cb_1:
                        UIUtil.hideKeyboard(mContext, etChat);
                        break;
                    case R.id.cb_2:
                        UIUtil.hideKeyboard(mContext, etChat);
                        break;
                    case R.id.message_recyclerView:
                        hideAllViewState();
                }
        }
        return false;
    }


    private void sendTextMessage() {
        final String content = etChat.getText().toString();
        if (!MyTextUtil.isInvalidText(content)) {
            //直接显示在list上
            MsgEntity msgEntity = new MsgEntity();
            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
            msgEntity.setText_id("1");
            msgEntity.setText_description(content);
            msgList.add(0, msgEntity);
            etChat.setText("");

            Map<String, String> params = new HashMap<String, String>();
            params.put("content_creator_id", MainActivity.getUser().getUser_id());
            params.put("group_id", groupId);
            params.put("content_type", "post");
            params.put("text_description", content);
            params.put("group_ind_type", "");
            params.put("content_group_public", "0");
            messageAction.sendChatMessage(params,Constant.API_MESSAGE_POST_TEXT,SEND_TEXT_MESSAGE);
        }
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

    List<Uri> pickUries = new ArrayList();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ChatActivity.RESULT_OK == resultCode) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    pickUries.clear();
                    if (data != null) {

                        if (SDKUtil.IS_JB) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                int size = clipData.getItemCount();
                                for (int i = 0; i < size; i++) {
                                    Uri uri = clipData.getItemAt(i).getUri();
                                    pickUries.add(uri);
                                }
                            } else {
                                pickUries.add(data.getData());
                            }
                        } else {
                            pickUries.add(data.getData());
                        }

//                        //直接显示在list上， 会在相册停留太久
                        for (int i = 0; i < pickUries.size(); i++) {
                            MsgEntity msgEntity = new MsgEntity();
                            msgEntity.setSticker_type(".png");
                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                            msgEntity.setUri(pickUries.get(i));
                            msgList.add(0, msgEntity);

                        }

                        handler.sendEmptyMessage(SEN_MESSAGE_FORM_ALBUM);

                    }

                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:

                    uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mContext, CACHE_PIC_NAME_TEMP));

                    handler.sendEmptyMessage(SEN_MESSAGE_FORM_CAMERA);
                    //直接显示在list上， 会在相册停留太久
                    MsgEntity msgEntity = new MsgEntity();
                    msgEntity.setSticker_type(".png");
                    msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                    msgEntity.setUri(uri);
                    msgList.add(0, msgEntity);
                    break;

                // 取得裁剪后的图片
                case REQUEST_HEAD_FINAL:
                    break;
                case REQUEST_GET_GROUP_NAME:
                    tvTitle.setText(data.getStringExtra("group_name"));
                    break;

                default:
                    break;

            }
        }
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

    private void setAllListener() {
        expandFunctionButton.setOnClickListener(this);
        stickerImageButton.setOnClickListener(this);
        etChat.setOnClickListener(this);
        cameraTextView.setOnClickListener(this);
        albumTextView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);
        videoTextView.setOnClickListener(this);
        contactTextView.setOnClickListener(this);
        sendTextView.setOnClickListener(this);

        recyclerView.setOnTouchListener(this);
        stickerImageButton.setOnTouchListener(this);
        expandFunctionButton.setOnTouchListener(this);
        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    sendTextView.setClickable(false);
                } else {
                    sendTextView.setClickable(true);
                }
            }
        });
    }

    private void uploadImage(Uri uri) {
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, uri), 480, 800, false);
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("group_id", groupId);
        params.put("content_type", "post");
        params.put("content_group_public", "0");
        params.put("photo_caption", "");
        params.put("multiple", "0");
        params.put("file", file);
        params.put("photo_fullsize", "1");
        messageAction.sendChatMessage(params, Constant.API_MESSAGE_POST_TEXT, SEND_PIC_MESSAGE);
    }

    /**
     * 从网络获取聊天消息
     */
    public void getMsg(int limitIndex,int startIndex,int msgIndex) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", limitIndex+"");
        params.put("offset", "0");
        params.put("group_id", groupId);
        params.put("page", "1");
        params.put("start", startIndex+"");
        params.put("view_user", MainActivity.getUser().getUser_id());
        String url = UrlUtil.generateUrl(Constant.API_MESSAGE_POST_TEXT, params);
        messageAction.getChatMessage(null,url,msgIndex);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
