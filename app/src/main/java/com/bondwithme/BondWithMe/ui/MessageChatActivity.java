package com.bondwithme.BondWithMe.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.action.MessageAction;
import com.bondwithme.BondWithMe.adapter.MessageChatAdapter;
import com.bondwithme.BondWithMe.entity.MsgEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.StickerViewClickListener;
import com.bondwithme.BondWithMe.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.CustomLengthFilter;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.widget.StickerLinearLayout;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by quankun on 15/4/24.
 */
public class MessageChatActivity extends BaseActivity implements View.OnTouchListener, StickerViewClickListener, View.OnLongClickListener {
    private Context mContext;
//    private ProgressDialog progressDialog;
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
    public SwipeRefreshLayout swipeRefreshLayout;
    private TextView cameraTextView;//相机
    private TextView albumTextView;//相册
    private TextView locationTextView;//地图
    private TextView videoTextView;//视频
    private TextView contactTextView;//名片
    private TextView sendTextView;//发送按钮
    private LinearLayout expandFunctionLinear;//加号
    private LinearLayout stickerLinear;//表情库

    private LinearLayout chat_gn_ll;//隐藏键盘输入时布局
    private ImageView chat_mic_keyboard;//语音和文字输入切换按钮
    private LinearLayout chat_mic_ll;//语音按钮布局
    private TextView chat_mic_text;//语音输入提示
    private TextView chat_mic_time;//语音录制时间显示
    private ImageView mic_iv;//语音录制按钮
    private ImageView mic_left;//语音录制左边按钮
    private ImageView mic_right;//语音录制右边按钮
    private ImageView chat_gn;

    private boolean isShowKBPic = false;

    public LinearLayout empty_message;

    private List<MsgEntity> msgList;

    private String groupId;//API的一个参数
    private int userOrGroupType = -1;//0为私聊，1为群聊
    /**
     * 放置表情图标库的默认文件夹名称
     */

    private final static int REQUEST_GET_GROUP_NAME = 4;

    /*相册和相机使用的参数*/
    private final static int REQUEST_HEAD_PHOTO = 100;
    private final static int REQUEST_HEAD_CAMERA = 101;
    private final static int REQUEST_HEAD_FINAL = 102;
    private final static int INPUT_EDIT_MAX_LENGTH = 1000;
    private Intent intent;

    private int indexPage = 1;
    private String titleName = "";
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
    public final static int SEND_TEXT_MESSAGE = 0X103;
    public final static int SEND_PIC_MESSAGE = 0X104;
    public final static int GET_HISTORY_MESSAGE = 0X105;
    public final static int GET_SEND_OVER_MESSAGE = 0X106;
    public final static int GET_TIMER_MESSAGE = 0X107;
    public int INITIAL_LIMIT = 10;

    public MessageAction messageAction;
    public MessageChatAdapter messageChatAdapter;
    public LinearLayoutManager llm;
    private InputMethodManager imm;
    private Timer mTimer;

    private int isNewGroup;
    private ModifyStickerReceiver stickerReceiver;
    private boolean isGroupChat;
    private StickerLinearLayout chat_main_ll;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_LATEST_MESSAGE:
//                    progressDialog.dismiss();
                    List<MsgEntity> msgList = (List<MsgEntity>) msg.obj;
                    if (null == msgList || msgList.size() == 0) {
                        empty_message.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                    } else {
                        empty_message.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        messageChatAdapter.addData(msgList);
                    }
                    break;
                case GET_SEND_OVER_MESSAGE:
                    List<MsgEntity> msgSendList = (List<MsgEntity>) msg.obj;
                    if (null != msgSendList && msgSendList.size() > 0) {
                        if (empty_message.getVisibility() == View.VISIBLE) {
                            empty_message.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                        messageChatAdapter.addSendData(msgSendList);
                    }
                    break;
                case GET_HISTORY_MESSAGE:
                    List<MsgEntity> msgHistoryList = (List<MsgEntity>) msg.obj;
                    swipeRefreshLayout.setRefreshing(false);
                    if (null == msgHistoryList || msgHistoryList.size() == 0) {
                        break;
                    }
                    indexPage++;
                    if (null != msgHistoryList) {
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
                    MsgEntity msgEntity = new MsgEntity();
                    msgEntity.setSticker_type(".png");
                    msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                    msgEntity.setUri(uri);
                    messageChatAdapter.addMsgEntity(msgEntity);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImage(uri);
                        }
                    }).start();
                    break;
                case SEND_PIC_MESSAGE:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (null == jsonObject) {
                        break;
                    }
                    try {
                        String postType = jsonObject.optString("postType");
                        if ("postPhoto".equals(postType) || "postSticker".equals(postType)) {
                            getMsg(indexPage * INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case SEND_TEXT_MESSAGE:
                    JSONObject textJsonObject = (JSONObject) msg.obj;
                    try {
                        if ("postText".equals(textJsonObject.getString("postType"))) {
                            getMsg(indexPage * INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_TIMER_MESSAGE:
                    List<MsgEntity> msgTimerList = (List<MsgEntity>) msg.obj;
                    if (null != msgTimerList && msgTimerList.size() > 0) {
                        if (empty_message.getVisibility() == View.VISIBLE) {
                            empty_message.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                        messageChatAdapter.addTimerData(msgTimerList);
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


//    @Override
//    public void finish() {
//        if (isNewGroup == 1) {
//            setResult(RESULT_OK);
//        }else {
//
//        }
//        super.finish();
//    }

    @Override
    protected void setTitle() {
        tvTitle.setText(titleName);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        if (userOrGroupType == 1) {
            rightButton.setVisibility(View.VISIBLE);
        } else {
            rightButton.setVisibility(View.INVISIBLE);
        }
        rightButton.setImageResource(R.drawable.btn_group_setting);
    }

    @Override
    protected void titleRightEvent() {
        Intent intent;
        if (userOrGroupType == 0) {
            intent = new Intent(mContext, FamilyProfileActivity.class);
            intent.putExtra("member_id", groupId);//传进来的,他人个人信息
            startActivity(intent);
        } else if (userOrGroupType == 1) {
            intent = new Intent(mContext, GroupSettingActivity.class);
            intent.putExtra("groupId", groupId);
            if (!TextUtils.isEmpty(tvTitle.getText())) {
                intent.putExtra("groupName", tvTitle.getText());
            } else {
                intent.putExtra("groupName", titleName);
            }
            startActivityForResult(intent, REQUEST_GET_GROUP_NAME);
        }
    }

//    @Override
//    protected void titleLeftEvent() {
//        if (imm.isActive()) {
//            imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 50);
//        } else {
//            finish();
//        }
//    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        userOrGroupType = getIntent().getIntExtra("type", -1);
        if (userOrGroupType == 0) {
            isGroupChat = false;
        } else {
            isGroupChat = true;
        }
        //如果是从新建group打开的
        isNewGroup = getIntent().getIntExtra("isNewGroup", 0);
//        Log.i("isNewGroup====",isNewGroup+"");
        if (isNewGroup == 1) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        mContext = this;
        messageAction = new MessageAction(mContext, handler);
//        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
//        progressDialog.show();
        msgList = new ArrayList<>();
//        if (userOrGroupType == 0) {
//            titleName
//            tvTitle.setText(userEntity.getUser_given_name());
//        } else if (userOrGroupType == 1) {
//            tvTitle.setText(groupEntity.getGroup_name());
//        } else {
//            tvTitle.setText("error");
//        }
        groupId = getIntent().getStringExtra("groupId");
        titleName = getIntent().getStringExtra("titleName");
//        if (userOrGroupType == 0) {
//            userEntity = (PrivateMessageEntity) getIntent().getExtras().getSerializable("userEntity");
//            if (userEntity != null) {
//                groupId = userEntity.getGroup_id();
//            }
//        } else if (userOrGroupType == 1) {
//            groupEntity = (GroupMessageEntity) getIntent().getExtras().getSerializable("groupEntity");
//            if (groupEntity != null) {
//                groupId = groupEntity.getGroup_id();
//            }
//        } else {
//            finish();
//        }
        setView();
        setAllListener();
        imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        llm = new LinearLayoutManager(MessageChatActivity.this);
        recyclerView.setLayoutManager(llm);

        messageChatAdapter = new MessageChatAdapter(mContext, msgList, recyclerView, MessageChatActivity.this, llm, isGroupChat);
        recyclerView.setAdapter(messageChatAdapter);
        getMsg(INITIAL_LIMIT, 0, GET_LATEST_MESSAGE);//接收对话消息
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getMsg(indexPage * INITIAL_LIMIT, 0, GET_TIMER_MESSAGE);//接收对话消息
            }
        }, 10000, 10000);

        IntentFilter intentFilter = new IntentFilter(StickerStoreActivity.ACTION_FINISHED);
        stickerReceiver = new ModifyStickerReceiver();
        registerReceiver(stickerReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        unregisterReceiver(stickerReceiver);
    }

    private void setView() {
        expandFunctionButton = getViewById(R.id.cb_1);
        stickerImageButton = getViewById(R.id.cb_2);
        recyclerView = getViewById(R.id.message_recyclerView);
        etChat = getViewById(R.id.et_chat);
        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        expandFunctionLinear = getViewById(R.id.ll_1);
        stickerLinear = getViewById(R.id.ll_2);
        cameraTextView = getViewById(R.id.camera_tv);
        albumTextView = getViewById(R.id.album_tv);
        locationTextView = getViewById(R.id.location_tv);
        videoTextView = getViewById(R.id.video_tv);
        contactTextView = getViewById(R.id.contact_tv);
        sendTextView = getViewById(R.id.btn_send);
        empty_message = getViewById(R.id.no_message_data_linear);
        chat_main_ll = getViewById(R.id.chat_main_ll);

        chat_main_ll.setOnResizeListener(new StickerLinearLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                if (h < oldh) {
                    llm.scrollToPosition(messageChatAdapter.getItemCount() - 1);
                }
            }
        });

        chat_gn_ll = getViewById(R.id.chat_gn_ll);
        chat_mic_keyboard = getViewById(R.id.chat_mic_keyboard);
        chat_mic_ll = getViewById(R.id.chat_mic_ll);
        chat_mic_text = getViewById(R.id.chat_mic_text);
        chat_mic_time = getViewById(R.id.chat_mic_time);
        mic_iv = getViewById(R.id.mic_iv);
        mic_left = getViewById(R.id.mic_left);
        mic_right = getViewById(R.id.mic_right);
        chat_gn = getViewById(R.id.chat_gn);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int startIndex = indexPage * INITIAL_LIMIT;
                getMsg(INITIAL_LIMIT, startIndex, GET_HISTORY_MESSAGE);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViewPager();
            }
        }, 50);

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    class ModifyStickerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initViewPager();
        }
    }

    private void initViewPager() {
        if (isFinishing()) {
            return;
        }
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        StickerMainFragment mainFragment = new StickerMainFragment();//selectStickerName, MessageChatActivity.this, groupId);
        mainFragment.setPicClickListener(this);
        transaction.replace(R.id.sticker_message_fragment, mainFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.cb_1://扩展功能按钮
                if (expandFunctionLinear.getVisibility() == View.VISIBLE) {
                    hideAllViewState();
                } else {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showExpandFunctionView();
                            }
                        }, 50);
                    } else {
                        showExpandFunctionView();
                    }
                }

                break;
            case R.id.cb_2://表情功能按钮
                if (stickerLinear.getVisibility() == View.VISIBLE) {
                    hideAllViewState();
                } else {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showStickerView();
                            }
                        }, 50);
                    } else {
                        showStickerView();
                    }
                }
                break;
            case R.id.et_chat://文本框
//                expandFunctionLinear.setVisibility(View.GONE);
//                stickerLinear.setVisibility(View.GONE);
//                expandFunctionButton.setImageResource(R.drawable.chat_plus_normal);
//                stickerImageButton.setImageResource(R.drawable.chat_expression_normal);
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
            case R.id.chat_gn_ll://隐藏键盘输入时布局
                if (chat_mic_ll.getVisibility() == View.VISIBLE) {
                    goneView(chat_mic_ll, chat_gn, R.drawable.chat_gn_upward);
                } else {
                    visibleView(chat_mic_ll, chat_gn, R.drawable.chat_gn_normal);
                }
                break;
            case R.id.chat_mic_keyboard://语音和文字输入切换按钮
                goneView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_normal);
                goneView(stickerLinear, stickerImageButton, R.drawable.chat_expression_normal);
                if (isShowKBPic) {
                    isShowKBPic = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            goneView(chat_gn_ll, chat_gn, R.drawable.chat_gn_normal);
                            goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_microphone_normol);
                            visibleView(etChat, null, 0);
                            etChat.setFocusable(true);
                            etChat.setFocusableInTouchMode(true);
                            etChat.requestFocus();
                        }
                    }, 50);
                } else {
                    UIUtil.hideKeyboard(mContext, etChat);
                    isShowKBPic = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            visibleView(chat_gn_ll, chat_gn, R.drawable.chat_gn_normal);
                            visibleView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_keyboard_normol);
                            goneView(etChat, null, 0);
                        }
                    }, 50);
                }

                break;
            case R.id.chat_mic_ll://语音按钮布局
                break;
            case R.id.chat_mic_text://语音输入提示
                break;
            case R.id.chat_mic_time://语音录制时间显示
                break;
            case R.id.mic_iv://语音录制按钮
                break;
            case R.id.mic_left://语音录制左边按钮
                break;
            case R.id.mic_right://语音录制右边按钮
                break;
        }
    }

    private void goneView(View view, ImageView iv, int resourceId) {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
        if (iv != null && resourceId != 0) {
            iv.setImageResource(resourceId);
        }
    }

    private void visibleView(View view, ImageView iv, int resourceId) {
        if (view != null && view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        }
        if (iv != null && resourceId != 0) {
            iv.setImageResource(resourceId);
        }
    }

    private void inVisibleView(View view, ImageView iv, int resourceId) {
        if (view != null && view.getVisibility() == View.GONE) {
            view.setVisibility(View.INVISIBLE);
        }
        if (iv != null && resourceId != 0) {
            iv.setImageResource(resourceId);
        }
    }

    private void showExpandFunctionView() {
        goneView(stickerLinear, stickerImageButton, R.drawable.chat_expression_normal);
        isShowKBPic = false;
        goneView(chat_gn_ll, chat_gn, R.drawable.chat_gn_normal);
        goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_microphone_normol);
        visibleView(etChat, null, 0);
        visibleView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_press);
        recyclerView.scrollToPosition(messageChatAdapter.getItemCount() - 1);
    }

    private void showStickerView() {
        goneView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_normal);
        isShowKBPic = false;
        goneView(chat_gn_ll, chat_gn, R.drawable.chat_gn_normal);
        goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_microphone_normol);
        visibleView(etChat, null, 0);
        visibleView(stickerLinear, stickerImageButton, R.drawable.chat_expression_press);
        llm.scrollToPosition(messageChatAdapter.getItemCount() - 1);
    }

    private void hideAllViewState() {
        UIUtil.hideKeyboard(mContext, etChat);
        isShowKBPic = false;
        goneView(chat_gn_ll, chat_gn, R.drawable.chat_gn_normal);
        goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_microphone_normol);
        visibleView(etChat, null, 0);
        goneView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_normal);
        goneView(stickerLinear, stickerImageButton, R.drawable.chat_expression_normal);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.message_recyclerView:
                        hideAllViewState();
                        break;
                    case R.id.no_message_data_linear:
                        hideAllViewState();
                        break;
                    case R.id.et_chat:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (expandFunctionLinear.getVisibility() == View.VISIBLE) {
                                    expandFunctionLinear.setVisibility(View.GONE);
                                    expandFunctionButton.setImageResource(R.drawable.chat_plus_normal);
                                }
                                if (stickerLinear.getVisibility() == View.VISIBLE) {
                                    stickerLinear.setVisibility(View.GONE);
                                    stickerImageButton.setImageResource(R.drawable.chat_expression_normal);
                                }
                            }
                        }, 50);
                        break;
                }
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.et_chat:
                        if (!imm.isActive()) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                    etChat.setFocusable(true);
                                    etChat.setFocusableInTouchMode(true);
                                    etChat.requestFocus();
                                }
                            }, 50);
                        }
                        break;
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
            messageChatAdapter.addMsgEntity(msgEntity);
            etChat.setText("");

            Map<String, String> params = new HashMap<String, String>();
            params.put("content_creator_id", MainActivity.getUser().getUser_id());
            params.put("group_id", groupId);
            params.put("content_type", "post");
            params.put("text_description", content);
            params.put("group_ind_type", "");
            params.put("content_group_public", "0");
            messageAction.doRequest(MessageAction.REQUEST_POST, params, Constant.API_MESSAGE_POST_TEXT, SEND_TEXT_MESSAGE);
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        intent = new Intent(getApplicationContext(), SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_HEAD_PHOTO);
    }

    List<Uri> pickUries = new ArrayList();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("M_requestCode====",requestCode+"");
//        Log.i("M_resultCode====",resultCode+"");
        String groupNmae;
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    pickUries.clear();
                    if (data != null) {
                        ArrayList uris = data.getParcelableArrayListExtra(SelectPhotosActivity.IMAGES_STR);
                        pickUries.addAll(uris);
                        for (Uri uri : pickUries) {
                            MsgEntity msgEntity = new MsgEntity();
                            msgEntity.setSticker_type(".png");
                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                            msgEntity.setUri(uri);
                            messageChatAdapter.addMsgEntity(msgEntity);
                        }
                        handler.sendEmptyMessage(SEN_MESSAGE_FORM_ALBUM);
                    }

                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
                    uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mContext, CACHE_PIC_NAME_TEMP));
                    handler.sendEmptyMessage(SEN_MESSAGE_FORM_CAMERA);
                    break;

                // 取得裁剪后的图片
                case REQUEST_HEAD_FINAL:
                    break;
                case REQUEST_GET_GROUP_NAME:
                    setResult(RESULT_OK);
                    Log.i("Me_onActivityResult===2", "onActivityResult");
                    groupNmae = data.getStringExtra("groupName");
                    if (!TextUtils.isEmpty(groupNmae)) {
                        tvTitle.setText(groupNmae);
                    }
                    break;
                default:
                    break;

            }
        }
        if (RESULT_CANCELED == resultCode && data != null) {
            switch (requestCode) {
                case REQUEST_GET_GROUP_NAME:
                    groupNmae = data.getStringExtra("groupName");
                    if (!TextUtils.isEmpty(groupNmae)) {
                        tvTitle.setText(groupNmae);
                    }
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
        cameraTextView.setOnClickListener(this);
        albumTextView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);
        videoTextView.setOnClickListener(this);
        contactTextView.setOnClickListener(this);
        sendTextView.setOnClickListener(this);
        chat_gn_ll.setOnClickListener(this);
        chat_mic_keyboard.setOnClickListener(this);
        mic_iv.setOnClickListener(this);
        mic_left.setOnClickListener(this);
        mic_right.setOnClickListener(this);

        recyclerView.setOnTouchListener(this);
        etChat.setOnTouchListener(this);
        empty_message.setOnTouchListener(this);

        etChat.setFilters(new InputFilter[]{new CustomLengthFilter(INPUT_EDIT_MAX_LENGTH)});

        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    visibleView(sendTextView, null, 0);
                    goneView(chat_mic_keyboard, null, 0);
                } else {
                    visibleView(chat_mic_keyboard, null, 0);
                    goneView(sendTextView, null, 0);
                }
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
        messageAction.doRequest(MessageAction.REQUEST_UPLOAD, params, Constant.API_MESSAGE_POST_TEXT, SEND_PIC_MESSAGE);
    }

    /**
     * 从网络获取聊天消息
     */
    public void getMsg(int limitIndex, int startIndex, int msgIndex) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", limitIndex + "");
        params.put("offset", "0");
        params.put("group_id", groupId);
        params.put("page", "1");
        params.put("start", startIndex + "");
        params.put("view_user", MainActivity.getUser().getUser_id());
        String url = UrlUtil.generateUrl(Constant.API_MESSAGE_POST_TEXT, params);
        messageAction.doRequest(MessageAction.REQUEST_GET, null, url, msgIndex);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void showComments(String type, String fileName, String Sticker_name) {
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setUser_id(MainActivity.getUser().getUser_id());
        msgEntity.setSticker_type(type);
        msgEntity.setSticker_group_path(fileName);
        msgEntity.setSticker_name(Sticker_name);
        msgEntity.setIsNate("true");
        messageChatAdapter.addMsgEntity(msgEntity);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("group_id", groupId);
        params.put("content_type", "post");
        params.put("sticker_group_path", fileName);
        params.put("sticker_name", Sticker_name);
        params.put("sticker_type", type);
        messageAction.doRequest(MessageAction.REQUEST_POST, params, Constant.API_MESSAGE_POST_TEXT, MessageChatActivity.SEND_PIC_MESSAGE);
    }

    /**
     * add by wing
     *
     * @param intent
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }
}
