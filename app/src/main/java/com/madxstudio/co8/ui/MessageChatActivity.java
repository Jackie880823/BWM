package com.madxstudio.co8.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.action.MessageAction;
import com.madxstudio.co8.adapter.MessageChatAdapter;
import com.madxstudio.co8.entity.MediaData;
import com.madxstudio.co8.entity.MsgEntity;
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.http.PicturesCacheUtil;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.interfaces.StickerViewClickListener;
import com.madxstudio.co8.ui.more.sticker.StickerStoreActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.AudioMediaRecorder;
import com.madxstudio.co8.util.AudioPlayUtils;
import com.madxstudio.co8.util.CustomLengthFilter;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.UIUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.RoundProgressBarWidthNumber;
import com.madxstudio.co8.widget.StickerLinearLayout;
import com.material.widget.Dialog;

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
public class MessageChatActivity extends BaseActivity implements View.OnTouchListener, StickerViewClickListener, View.OnLongClickListener, SensorEventListener {
    private Context mContext;
//    private ProgressDialog progressDialog;
    /**
     * 聊天布局
     */
    private RecyclerView recyclerView;
    /**
     * 扩展功能按钮
     */
    private ImageView expandFunctionButton;
    /**
     * 表情按钮
     */
    private ImageView stickerImageButton;
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
    private ImageView sendTextView;//发送按钮
    private LinearLayout expandFunctionLinear;//加号
    private LinearLayout stickerLinear;//表情库

    private LinearLayout chat_gn_ll;//隐藏键盘输入时布局
    private ImageView chat_mic_keyboard;//语音和文字输入切换按钮
    private RelativeLayout chat_mic_ll;//语音按钮布局
    private TextView chat_mic_text;//语音输入提示
    private TextView chat_mic_time;//语音录制时间显示
    private ImageView mic_iv;//语音录制按钮
    private ImageView mic_left;//语音录制左边按钮
    private ImageView mic_right;//语音录制右边按钮
    private ImageView chat_gn;
    private ImageView bend_line;
    private RoundProgressBarWidthNumber id_progressbar;

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
    private final static int CAMERA_ACTIVITY = 103;
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

    public final static String MESSAGE_TYPE = "type";
    public final static String MESSAGE_GROUP_ID = "groupId";
    public final static String MESSAGE_TITLE_NAME = "titleName";

    public final static int GET_LATEST_MESSAGE = 0X100;
    public final static int SEN_MESSAGE_FORM_CAMERA = 0X101;
    public final static int SEN_MESSAGE_FORM_ALBUM = 0X102;
    public final static int SEND_TEXT_MESSAGE = 0X103;
    public final static int SEND_PIC_MESSAGE = 0X104;
    public final static int GET_HISTORY_MESSAGE = 0X105;
    public final static int GET_SEND_OVER_MESSAGE = 0X106;
    public final static int GET_TIMER_MESSAGE = 0X107;
    private final static int GET_RECORD_TIME = 0X108;
    private final static int SEND_AUDIO_MESSAGE = 0X109;
    private final static int PLAY_AUDIO_HANDLER = 0X110;
    public final static int NOTIFY_DATA = 0X111;
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

    private int mlCount = 1;

    private AudioMediaRecorder mRecorder;
    private Timer timer;
    private File audioFile;
    private boolean isAudition = false;

    Map<String, MsgEntity> sendMap = new HashMap<>();

    private AudioPlayUtils playerManager;
    private Sensor sensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private SensorManager sensorManager;
    private HeadsetReceiver receiver;
    private String groupDefault;
    private String userStauts;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
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
                        if (sendMap != null && sendMap.size() > 0) {
                            for (String mapKey : sendMap.keySet()) {
                                msgSendList.add(sendMap.get(mapKey));
                            }
//                            Collections.reverse(msgSendList);
//                            Collections.sort(msgSendList, new MessageAction.SortExpertsTeamDate());
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
                        if (sendMap != null && sendMap.size() > 0) {
                            for (String mapKey : sendMap.keySet()) {
                                msgHistoryList.add(sendMap.get(mapKey));
                            }
//                            Collections.reverse(msgHistoryList);
//                            Collections.sort(msgHistoryList, new MessageAction.SortExpertsTeamDate());
                        }
                        messageChatAdapter.addHistoryData(msgHistoryList);
                    }
                    break;
                case SEN_MESSAGE_FORM_ALBUM:
                    //上传相册图片
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    if (pickUries != null) {
                        for (Uri uri : pickUries) {
                            uploadImage(uri);
                        }
                    }
//                        }
//                    }).start();
                    break;
                case SEN_MESSAGE_FORM_CAMERA:
                    //上传相机拍照图片
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    uploadImage(uri);
//                        }
//                    }).start();
                    break;
                case SEND_PIC_MESSAGE:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (null == jsonObject) {
                        break;
                    }
                    try {
                        String postType = jsonObject.optString("postType");
                        if ("postPhoto".equals(postType) || "postSticker".equals(postType)) {
//                            getMsg(indexPage * INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case SEND_TEXT_MESSAGE:
                    JSONObject textJsonObject = (JSONObject) msg.obj;
                    try {
                        if ("postText".equals(textJsonObject.getString("postType"))) {
//                            getMsg(indexPage * INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
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
                        if (sendMap != null && sendMap.size() > 0) {
                            for (String mapKey : sendMap.keySet()) {
                                msgTimerList.add(sendMap.get(mapKey));
                            }
//                            Collections.reverse(msgTimerList);
//                            Collections.sort(msgTimerList, new MessageAction.SortExpertsTeamDate());
                        }
                        messageChatAdapter.addTimerData(msgTimerList);
                    }
                    break;
                case GET_RECORD_TIME:
                    mlCount++;
                    if (mlCount < 115) {
                        chat_mic_time.setText(MyDateUtils.formatRecordTime(mlCount));
                    } else {
                        int eciprocalrCount = 120 - mlCount;
                        chat_mic_time.setText(String.format(getString(R.string.text_can_record_time), eciprocalrCount));
                        if (mlCount == 120) {
                            if (timer != null) {
                                timer.cancel();
                            }
                            mRecorder.stopRecord();
                            chat_mic_time.setText(MyDateUtils.formatRecordTime(mlCount));
                        }
                    }
                    break;
                case SEND_AUDIO_MESSAGE:
                    JSONObject audioJsonObject = (JSONObject) msg.obj;
                    if (audioJsonObject == null) {
                        break;
                    }
                    try {
                        String postType = audioJsonObject.optString("postType");
                        if ("postAudio".equalsIgnoreCase(postType)) {
                            if (audioFile != null && audioFile.exists()) {
                                audioFile.delete();
                            }
                        }
                        if ("postVideo".equalsIgnoreCase(postType) || "postAudio".equalsIgnoreCase(postType)) {
                            getMsg(indexPage * INITIAL_LIMIT, 0, GET_SEND_OVER_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PLAY_AUDIO_HANDLER:
                    int progress = id_progressbar.getProgress();
                    id_progressbar.setProgress(++progress);
                    chat_mic_text.setText(MyDateUtils.formatRecordTime(mlCount - progress));
                    if (progress > mlCount) {
                        id_progressbar.setProgress(0);
                        id_progressbar.setVisibility(View.GONE);
                        chat_mic_text.setText(MyDateUtils.formatRecordTime(mlCount));
                        handler.removeMessages(PLAY_AUDIO_HANDLER);
                    } else {
                        handler.sendEmptyMessageDelayed(PLAY_AUDIO_HANDLER, 1000);
                    }
                    break;
                case NOTIFY_DATA:
                    int scrollPosition = llm.findLastVisibleItemPosition();
                    messageChatAdapter.notifyDataSetChanged();
                    llm.scrollToPosition(scrollPosition);
                    break;
            }

            return false;
        }
    });

    @Override
    public int getLayout() {
        return R.layout.activity_message_chat;
    }

    @Override
    protected void initBottomBar() {

    }

    public void getNewMsg() {
        getMsg(indexPage * INITIAL_LIMIT, 0, GET_TIMER_MESSAGE);
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
        if (userOrGroupType == 0 && PrivateMessageEntity.STATUS_DE_ACTIVE.equalsIgnoreCase(userStauts)) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.user_left_minilcon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTitle.setCompoundDrawables(drawable, null, null, null);
            tvTitle.setCompoundDrawablePadding(10);
        }
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
        if (playerManager != null) {
            playerManager.stop();
        }
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
            intent.putExtra(Constant.GROUP_DEFAULT, groupDefault);
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

    public static final int CHAT_TYPE_PERSONAL = 0;
    public static final int CHAT_TYPE_GROUP = -1;

    @Override
    public void initView() {
        userOrGroupType = getIntent().getIntExtra(MESSAGE_TYPE, -1);
        if (userOrGroupType == CHAT_TYPE_PERSONAL) {
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
        msgList = new ArrayList<>();
        groupId = getIntent().getStringExtra(MESSAGE_GROUP_ID);
        titleName = getIntent().getStringExtra(MESSAGE_TITLE_NAME);
        groupDefault = getIntent().getStringExtra(Constant.GROUP_DEFAULT);
        userStauts = getIntent().getStringExtra(Constant.MESSAGE_CHART_STATUS);
        mRecorder = new AudioMediaRecorder();
        setView();
        setAllListener();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        llm = new LinearLayoutManager(MessageChatActivity.this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(null);
        playerManager = AudioPlayUtils.getManager();
        messageChatAdapter = new MessageChatAdapter(mContext, msgList, recyclerView, MessageChatActivity.this, llm, isGroupChat, playerManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messageChatAdapter);
        getMsg(INITIAL_LIMIT, 0, GET_LATEST_MESSAGE);//接收对话消息
        if (userOrGroupType == 0 && PrivateMessageEntity.STATUS_DE_ACTIVE.equalsIgnoreCase(userStauts)) {
            getViewById(R.id.ll_chat).setVisibility(View.INVISIBLE);
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getMsg(indexPage * INITIAL_LIMIT, 0, GET_TIMER_MESSAGE);//接收对话消息
            }
        }, 10000, 10000);

        messageChatAdapter.onClickFailMsg(new MessageChatAdapter.SendFailMsgClick() {
            @Override
            public void sendFailMsg(MsgEntity msgEntity) {
                if (sendMap != null && sendMap.size() > 0) {
                    String key = msgEntity.getPhoto_postsize();
                    String type = msgEntity.getPhoto_thumbsize();
                    Map<String, Object> params = new HashMap<>();
                    String content = msgEntity.getText_description();
                    String audioName = msgEntity.getAudio_filename();
                    String videoName = msgEntity.getVideo_filename();
                    String sticker_name = msgEntity.getSticker_name();
                    params.put("content_creator_id", MainActivity.getUser().getUser_id());
                    Uri uriPic = msgEntity.getUri();
                    params.put("group_id", groupId);
                    params.put("content_type", "post");
                    if (!TextUtils.isEmpty(content)) {
                        params.put("text_description", content);
                        params.put("group_ind_type", "");
                        params.put("content_group_public", "0");
                    } else if (!TextUtils.isEmpty(videoName)) {
                        File file = new File(msgEntity.getFailUri().getPath());
                        if (file == null || !file.exists()) {
                            for (String mapKey : sendMap.keySet()) {
                                if (key != null && key.equals(mapKey)) {
                                    sendMap.remove(mapKey);
                                    break;
                                }
                            }
                            handler.sendEmptyMessage(NOTIFY_DATA);
                            return;
                        }
                        params.put("file", file);
                        params.put("video", "1");
                        params.put("video_duration", msgEntity.getVideo_duration());
                    } else if (!TextUtils.isEmpty(audioName)) {
                        File file = new File(msgEntity.getFailUri().getPath());
                        if (file == null || !file.exists()) {
                            for (String mapKey : sendMap.keySet()) {
                                if (key != null && key.equals(mapKey)) {
                                    sendMap.remove(mapKey);
                                    break;
                                }
                            }
                            handler.sendEmptyMessage(NOTIFY_DATA);
                            return;
                        }
                        params.put("file", file);
                        params.put("audio", "1");
                        params.put("audio_duration", msgEntity.getAudio_duration());
                    } else if (uriPic != null) {
                        String path = LocalImageLoader.compressBitmap(mContext, FileUtil.getRealPathFromURI(mContext, uriPic), 480, 800, false);
                        File filePic = new File(path);
                        if (filePic == null || !filePic.exists()) {
                            for (String mapKey : sendMap.keySet()) {
                                if (key != null && key.equals(mapKey)) {
                                    sendMap.remove(mapKey);
                                    break;
                                }
                            }
                            handler.sendEmptyMessage(NOTIFY_DATA);
                            return;
                        }
                        params.put("content_group_public", "0");
                        params.put("photo_caption", "");
                        params.put("multiple", "0");
                        params.put("file", filePic);
                        params.put("photo_fullsize", "1");
                    } else if (!TextUtils.isEmpty(sticker_name)) {
                        params.put("sticker_group_path", msgEntity.getSticker_group_path());
                        params.put("sticker_name", sticker_name);
                        params.put("sticker_type", msgEntity.getSticker_type());
                    }
                    handler.sendEmptyMessage(NOTIFY_DATA);
                    String requestType = MessageAction.REQUEST_UPLOAD;
                    if (MessageAction.POST_STICKER.equals(type) || MessageAction.POST_TEXT.equals(type)) {
                        requestType = MessageAction.REQUEST_POST;
                    }
                    messageAction.doRequest(requestType, params, Constant.API_MESSAGE_POST_TEXT, SEND_TEXT_MESSAGE, sendMap, key, type);
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter(StickerStoreActivity.ACTION_FINISHED);
        stickerReceiver = new ModifyStickerReceiver();
        registerReceiver(stickerReceiver, intentFilter);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        receiver = new HeadsetReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        if (playerManager != null) {
            playerManager.stop();
        }
        unregisterReceiver(stickerReceiver);
        unregisterReceiver(receiver);
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
        bend_line = getViewById(R.id.bend_line);

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
        id_progressbar = getViewById(R.id.id_progressbar);

        chat_mic_text.setTextColor(getResources().getColor(getActionBarColor()));
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
        switch (v.getId()) {
            case R.id.mic_iv:
                if (!isAudition) {
                    if (mlCount != 1) {
                        mlCount = 1;
                    }
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    mic_left.setVisibility(View.VISIBLE);
                    mic_right.setVisibility(View.VISIBLE);
                    bend_line.setVisibility(View.VISIBLE);
                    chat_mic_time.setVisibility(View.VISIBLE);
                    mic_iv.setImageResource(R.drawable.chat_voice_press);
                    audioFile = FileUtil.saveAudioFile(mContext);
                    mRecorder.startRecord(audioFile);
                    chat_mic_text.setText(R.string.text_audio_release);
                    TimerTask task = new TimerTask() {
                        public void run() {
                            handler.sendEmptyMessage(GET_RECORD_TIME);
                        }
                    };
                    timer = new Timer(true);
                    timer.schedule(task, 1000, 1000); //延时1000ms后执行，1000ms执行一次
                }
                return true;
        }
        return false;
    }

    class HeadsetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //插入和拔出耳机会触发此广播
                case Intent.ACTION_HEADSET_PLUG:
                    int state = intent.getIntExtra("state", 0);
                    if (state == 1) {
                        playerManager.changeToHeadsetMode();
                    } else if (state == 0) {
                        playerManager.resume();
                    }
                    break;
                //拔出耳机会触发此广播,拔出不会触发,且此广播比上一个早,故可在此暂停播放,收到上一个广播时在恢复播放
                case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                    playerManager.pause();
                    playerManager.changeToSpeakerMode();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //耳机模式下直接返回
        if (playerManager.getCurrentMode() == AudioPlayUtils.MODE_HEADSET) {
            return;
        }

        float value = sensorEvent.values[0];
        if (playerManager.isPlaying()) {
            if (value == sensor.getMaximumRange()) {
                playerManager.changeToSpeakerMode();
                setScreenOn();
            } else {
                playerManager.changeToEarpieceMode();
                setScreenOff();
            }
        } else {
            if (value == sensor.getMaximumRange()) {
                playerManager.changeToSpeakerMode();
                setScreenOn();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void setScreenOff() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, MessageChatActivity.class.getSimpleName());
        }
        wakeLock.acquire();
    }

    private void setScreenOn() {
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
            wakeLock = null;
        }
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
        String filePath = FileUtil.getSaveRootPath(mContext, false).getAbsolutePath() + File.separator + "Sticker";
        File file = new File(filePath);
        if (file.exists()) {
            StickerMainFragment mainFragment = new StickerMainFragment();//selectStickerName, MessageChatActivity.this, groupId);
            mainFragment.setPicClickListener(this);
            transaction.replace(R.id.sticker_message_fragment, mainFragment);
        } else {
            StickerMainNewFragment mainFragment = new StickerMainNewFragment();//selectStickerName, MessageChatActivity.this, groupId);
            mainFragment.setPicClickListener(this);
            transaction.replace(R.id.sticker_message_fragment, mainFragment);
        }
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
                LayoutInflater factory = LayoutInflater.from(mContext);
                View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);
                final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
                TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
                TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
                TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
                tvAddNewMember.setText(R.string.text_recording_video);
                tvCreateNewGroup.setText(R.string.text_take_photos);
                tvAddNewMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (playerManager != null) {
                            playerManager.stop();
                        }
                        // Modify start by Jackie, Use custom recorder
                        Intent mIntent = new Intent(MediaData.ACTION_RECORDER_VIDEO);
//                        Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        // Modify end by Jackie
                        mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.9);//画质0.5
                        //mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60000);//60s
//                        mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 45 * 1024 * 1024l);
                        startActivityForResult(mIntent, CAMERA_ACTIVITY);//CAMERA_ACTIVITY = 1
                        showSelectDialog.dismiss();
                    }
                });

                tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCamera();
                        showSelectDialog.dismiss();
                    }
                });
                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectDialog.dismiss();
                    }
                });
                showSelectDialog.show();
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
                    goneView(chat_mic_ll, chat_gn, R.drawable.chat_choose_up);
                } else {
                    visibleView(chat_mic_ll, chat_gn, R.drawable.chat_choose_down);
                }
                break;
            case R.id.chat_mic_keyboard://语音和文字输入切换按钮
                goneView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_normal);
                goneView(stickerLinear, stickerImageButton, R.drawable.chat_expression_normal);
                llm.scrollToPosition(messageChatAdapter.getItemCount() - 1);
                if (isShowKBPic) {
                    isShowKBPic = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            goneView(chat_gn_ll, chat_gn, R.drawable.chat_choose_down);
                            goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_choose_mic);
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
                            visibleView(chat_gn_ll, chat_gn, R.drawable.chat_choose_down);
                            visibleView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_choose_kb);
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
                if (isAudition) {
                    if (playerManager != null && playerManager.isPlaying()) {
                        playerManager.stop();
                        goneView(id_progressbar, null, 0);
                        id_progressbar.setProgress(0);
                        chat_mic_text.setText(MyDateUtils.formatRecordTime(mlCount));
                        handler.removeMessages(PLAY_AUDIO_HANDLER);
                    } else {
                        if (audioFile != null && audioFile.exists()) {
                            handler.removeMessages(PLAY_AUDIO_HANDLER);
                            String path = audioFile.getAbsolutePath();
//                            AudioPlayUtils.getInstance(path, null, null).playAudio();
                            if (playerManager == null) {
                                playerManager = AudioPlayUtils.getManager();
                            }
                            playerManager.play(path, null);
                            id_progressbar.setVisibility(View.VISIBLE);
                            id_progressbar.setMax(mlCount);
                            handler.sendEmptyMessage(PLAY_AUDIO_HANDLER);
                        }
                    }
                    break;
                }
                break;
            case R.id.mic_left://语音录制左边按钮
                if (isAudition) {
                    isAudition = false;
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    goneView(id_progressbar, null, 0);
                    handler.removeMessages(PLAY_AUDIO_HANDLER);
                    if (audioFile != null && audioFile.exists()) {
                        audioFile.delete();
                    }
                    mlCount = 1;
                    mic_left.setImageResource(R.drawable.chat_play);
                    mic_right.setImageResource(R.drawable.delete_voice);
                    mic_iv.setImageResource(R.drawable.chat_voice);
                    hideAudioView();
                    break;
                }
                break;
            case R.id.mic_right://语音录制右边按钮
                if (isAudition) {
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    isAudition = false;
                    goneView(id_progressbar, null, 0);
                    mic_left.setImageResource(R.drawable.chat_play);
                    mic_right.setImageResource(R.drawable.delete_voice);
                    mic_iv.setImageResource(R.drawable.chat_voice);
                    if (audioFile != null && audioFile.exists()) {
                        uploadAudioOrVideo(audioFile, true, null, mlCount);
                    }
                    hideAudioView();
                    mlCount = 1;
                }
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
        goneView(chat_gn_ll, chat_gn, R.drawable.chat_choose_down);
        goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_choose_mic);
        visibleView(etChat, null, 0);
        visibleView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_press);
        recyclerView.scrollToPosition(messageChatAdapter.getItemCount() - 1);
    }

    private void showStickerView() {
        goneView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_normal);
        isShowKBPic = false;
        goneView(chat_gn_ll, chat_gn, R.drawable.chat_choose_down);
        goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_choose_mic);
        visibleView(etChat, null, 0);
        visibleView(stickerLinear, stickerImageButton, R.drawable.chat_expression_press);
        llm.scrollToPosition(messageChatAdapter.getItemCount() - 1);
    }

    private void hideAllViewState() {
        UIUtil.hideKeyboard(mContext, etChat);
        isShowKBPic = false;
        goneView(chat_gn_ll, chat_gn, R.drawable.chat_choose_down);
        goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_choose_mic);
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
                                goneView(chat_mic_ll, chat_mic_keyboard, R.drawable.chat_choose_mic);
                                goneView(expandFunctionLinear, expandFunctionButton, R.drawable.chat_plus_normal);
                                goneView(stickerLinear, stickerImageButton, R.drawable.chat_expression_normal);
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

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    public boolean isInView(View view1, MotionEvent event) {
        int clickX = ((int) event.getRawX());
        int clickY = ((int) event.getRawY());
        //如下的view表示Activity中的子View或者控件
        int[] location = new int[2];
        view1.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = view1.getWidth() / 2;
        int height = view1.getHeight() / 2;
        if (clickX >= (x + width) && clickX <= (x + width * 3) &&
                clickY >= (y + height) && clickY <= (y + height * 3)) {
            return true;  //这个条件成立，则判断这个view被点击了
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
            msgEntity.setSendStatus(MsgEntity.SEND_IN);
            etChat.setText("");
            Map<String, String> params = new HashMap<String, String>();
            params.put("content_creator_id", MainActivity.getUser().getUser_id());
            params.put("group_id", groupId);
            params.put("content_type", "post");
            params.put("text_description", content);
            params.put("group_ind_type", "");
            params.put("content_group_public", "0");
            msgEntity.setContent_creation_date(MyDateUtils.getUTCDateString4DefaultFromLocal(System.currentTimeMillis()));
            String key = MessageAction.POST_TEXT + System.currentTimeMillis();
            msgEntity.setPhoto_postsize(key);
            msgEntity.setPhoto_thumbsize(MessageAction.POST_TEXT);
            messageChatAdapter.addMsgEntity(msgEntity);
            sendMap.put(key, msgEntity);
            messageAction.doRequest(MessageAction.REQUEST_POST, params, Constant.API_MESSAGE_POST_TEXT, SEND_TEXT_MESSAGE, sendMap, key, MessageAction.POST_TEXT);
        }
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        if (playerManager != null) {
            playerManager.stop();
        }
        intent = new Intent(getApplicationContext(), SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, true);
        startActivityForResult(intent, REQUEST_HEAD_PHOTO);
    }

    List<Uri> pickUries = new ArrayList();

    private void uploadVideo(final Uri videoUri, final long durationTime) {
        LogUtil.i("Jackie", "uploadVideo: duration = " + durationTime);
        final File file = new File(videoUri.getPath());
        if (file != null && file.exists()) {
            float fileLength = file.length();
            String formatLength = null;
            boolean isTooBig = false;
            if (fileLength <= 1024) {
                fileLength = (float) (Math.round(fileLength * 100)) / 100;
                formatLength = fileLength + "B";
            } else {
                fileLength = fileLength / 1024;
                if (fileLength <= 1024) {
                    fileLength = (float) (Math.round(fileLength * 100)) / 100;
                    formatLength = fileLength + "KB";
                } else {
                    fileLength = fileLength / 1024;
                    if (fileLength < 50) {
                        fileLength = (float) (Math.round(fileLength * 100)) / 100;
                        formatLength = fileLength + "MB";
                    } else {
                        isTooBig = true;
                    }
                }
            }

            LayoutInflater factory = LayoutInflater.from(mContext);
            View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
            final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
            TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
            if (!isTooBig) {
                formatLength = String.format(getString(R.string.text_chat_send_video), formatLength);
            } else {
                formatLength = getString(R.string.text_video_too_big);
            }
            tv_no_member.setText(formatLength);

            TextView tv_ok = (TextView) selectIntention.findViewById(R.id.tv_ok);
            TextView tv_cancel = (TextView) selectIntention.findViewById(R.id.tv_cancel);
            View line_view = selectIntention.findViewById(R.id.line_view);
            if (!isTooBig) {
                tv_cancel.setVisibility(View.VISIBLE);
                line_view.setVisibility(View.VISIBLE);
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.fromFile(file);
                        uploadAudioOrVideo(file, false, uri, durationTime);
                        showSelectDialog.dismiss();
                    }
                });
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectDialog.dismiss();
                    }
                });
            } else {
                tv_cancel.setVisibility(View.GONE);
                line_view.setVisibility(View.GONE);
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectDialog.dismiss();
                    }
                });
            }
            showSelectDialog.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String groupNmae;
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    pickUries.clear();
                    if (data != null) {
                        String type = data.getStringExtra(MediaData.EXTRA_MEDIA_TYPE);
                        if (MediaData.TYPE_VIDEO.equals(type)) {
                            long durationTime = data.getLongExtra(MediaData.EXTRA_VIDEO_DURATION, 0);
                            uploadVideo(data.getData(), durationTime);
                        } else {
                            ArrayList uris = data.getParcelableArrayListExtra(SelectPhotosActivity.EXTRA_IMAGES_STR);
                            pickUries.addAll(uris);
                            handler.sendEmptyMessage(SEN_MESSAGE_FORM_ALBUM);
                        }
                    }
                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
//                    uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mContext, CACHE_PIC_NAME_TEMP, true));
                    uri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
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
                case CAMERA_ACTIVITY:
                    // Modify start by Jackie
//                    String[] videoColumns = {MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.VideoColumns.DATA,
//                            MediaStore.Video.VideoColumns._ID, MediaStore.Video.Media.SIZE, MediaStore.Video.VideoColumns.DURATION};
//                    Uri uri = data.getData();
//                    Cursor cursor = this.getContentResolver().query(uri, videoColumns, null, null, null);
//                    String filePath = null;
//                    long duration = 0;
//                    if (cursor != null && cursor.moveToNext()) {
//                        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
//                        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
//                        cursor.close();
//                    }
//                    if (!TextUtils.isEmpty(filePath)) {
//                        uploadVideo(Uri.parse(filePath), duration);
//                    }

                    // Modify end and add start by Jackie, video data from custom recorder
                    long durationTime = data.getLongExtra(MediaData.EXTRA_VIDEO_DURATION, 0);
                    uploadVideo(data.getData(), durationTime);
                    // add end by Jackie
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
        if (playerManager != null) {
            playerManager.stop();
        }
//        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra("camerasensortype", 2);
//        // 下面这句指定调用相机拍照后的照片存储的路径
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                .fromFile(PicturesCacheUtil.getCachePicFileByName(mContext,
//                        CACHE_PIC_NAME_TEMP, true)));
//        // 图片质量为高
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        intent.putExtra("return-data", false);
//        startActivityForResult(intent, REQUEST_HEAD_CAMERA);
        Intent intent = new Intent(mContext, PickAndCropPictureActivity.class);
        intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
        intent.putExtra(PickAndCropPictureActivity.FLAG_CROP, false);
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


        mic_iv.setOnLongClickListener(this);

//        etChat.setFilters(new InputFilter[]{new CustomLengthFilter(INPUT_EDIT_MAX_LENGTH)});

        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (playerManager != null) {
                        playerManager.stop();
                    }
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

        mic_iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (!isAudition) {
                            int[] arrayLeft = new int[2];
                            mic_left.getLocationOnScreen(arrayLeft);
                            int[] arrayRight = new int[2];
                            mic_right.getLocationOnScreen(arrayRight);
                            int totalMove = (arrayRight[0] - arrayLeft[0]) / 4;
                            int[] arrayMic = new int[2];
                            mic_iv.getLocationOnScreen(arrayMic);
                            float touchDownX = arrayMic[0] + mic_iv.getWidth() / 2;
                            int micWidth = mic_iv.getWidth() / 2;
                            float moveX = event.getRawX();
                            if (moveX < (touchDownX - micWidth)) {
                                //左边按钮变大
                                float moveRange = (touchDownX - micWidth - moveX) / totalMove;
                                if (moveRange < 1) {
                                    mic_left.setScaleX(1 + moveRange);
                                    mic_left.setScaleY(1 + moveRange);
                                }
                                boolean isInLeft = isInView(mic_left, event);
                                if (isInLeft) {
                                    chat_mic_text.setText(R.string.text_audio_loosen_audition);
                                    mic_left.setImageResource(R.drawable.chat_play_press);
                                } else {
                                    chat_mic_text.setText(R.string.text_audio_release);
                                    mic_left.setImageResource(R.drawable.chat_play);
                                }
                            }

                            if (moveX > (touchDownX + micWidth)) {
                                //右边边按钮变大
                                float moveRange = (moveX - touchDownX - micWidth) / totalMove;
                                if (moveRange < 1) {
                                    mic_right.setScaleX(1 + moveRange);
                                    mic_right.setScaleY(1 + moveRange);
                                }
                                boolean isInRight = isInView(mic_right, event);
                                if (isInRight) {
                                    chat_mic_text.setText(R.string.text_audio_cancle_send);
                                    mic_right.setImageResource(R.drawable.delete_voice_press);
                                } else {
                                    chat_mic_text.setText(R.string.text_audio_release);
                                    mic_right.setImageResource(R.drawable.delete_voice);
                                }
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (!isAudition) {
                            mRecorder.stopRecord();
                            boolean isInLeft = isInView(mic_left, event);
                            boolean isInRight = isInView(mic_right, event);
                            mic_iv.setImageResource(R.drawable.chat_voice);
                            chat_mic_time.setText(MyDateUtils.formatRecordTime(mlCount));
                            if (timer != null) {
                                timer.cancel();
                            }
                            handler.removeMessages(GET_RECORD_TIME);
                            if (audioFile != null && audioFile.exists() && mlCount < 3 && !isInRight) {
                                audioFile.delete();
                                mic_left.setScaleX(1);
                                mic_left.setScaleY(1);
                                mic_right.setScaleX(1);
                                mic_right.setScaleY(1);
                                mic_left.setImageResource(R.drawable.chat_play);
                                MessageUtil.getInstance().showShortToast(getString(R.string.text_record_audio_too_short));
                                mlCount = 1;
                                hideAudioView();
                            } else if (isInLeft) {
                                isAudition = true;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mic_left.setScaleX(1);
                                        mic_left.setScaleY(1);
                                        bend_line.setVisibility(View.INVISIBLE);
                                        mic_left.setImageResource(R.drawable.chat_cancel_audio);
                                        mic_right.setImageResource(R.drawable.chat_send_audio);
                                        mic_iv.setImageResource(R.drawable.chat_play_voice);
                                        chat_mic_time.setText("");
                                        chat_mic_time.setVisibility(View.INVISIBLE);
                                        chat_mic_text.setText(MyDateUtils.formatRecordTime(mlCount));
                                    }
                                }, 50);
                                return true;
                            } else if (isInRight) {
                                mic_right.setScaleX(1);
                                mic_right.setScaleY(1);
                                if (audioFile != null && audioFile.exists()) {
                                    audioFile.delete();
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mic_right.setImageResource(R.drawable.delete_voice);
                                    }
                                }, 50);
                                mlCount = 1;
                                hideAudioView();
                            } else {
                                mic_left.setScaleX(1);
                                mic_left.setScaleY(1);
                                mic_right.setScaleX(1);
                                mic_right.setScaleY(1);
                                if (audioFile != null && audioFile.exists()) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            uploadAudioOrVideo(audioFile, true, null, mlCount);
                                            mlCount = 1;
                                        }
                                    }, 200);
                                } else {
                                    mlCount = 1;
                                }
                                hideAudioView();
                            }
                            return false;
                        }
                }
                return false;
            }
        });
    }

    private void hideAudioView() {
        chat_mic_text.setText(R.string.text_hold_and_speak);
        bend_line.setVisibility(View.INVISIBLE);
        mic_left.setVisibility(View.INVISIBLE);
        mic_right.setVisibility(View.INVISIBLE);
        chat_mic_time.setText("");
        chat_mic_time.setVisibility(View.INVISIBLE);
    }

    private void uploadAudioOrVideo(File file, boolean isAudio, Uri uri, long durationTime) {
        if (file == null || !file.exists()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("group_id", groupId);
        params.put("content_type", "post");
        params.put("file", file);
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setUser_id(MainActivity.getUser().getUser_id());
        msgEntity.setSendStatus(MsgEntity.SEND_IN);
        msgEntity.setContent_creation_date(MyDateUtils.getUTCDateString4DefaultFromLocal(System.currentTimeMillis()));
        String audioFile = file.getAbsolutePath();
        audioFile = audioFile.substring(audioFile.lastIndexOf(File.separator) + 1);
        String postType = MessageAction.POST_AUDIO;
        if (!isAudio) {
            String duration = durationTime / 1000L + "";
            params.put("video", "1");
            params.put("video_duration", duration);
            msgEntity.setVideo_filename(audioFile);
            String videoThumbnail = getVideoThumbnail(uri);
            msgEntity.setVideo_format1(videoThumbnail);
            msgEntity.setVideo_format2(uri.getPath());
            msgEntity.setFailUri(uri);
            msgEntity.setVideo_duration(duration);
            params.put("video_thumbnail", String.format("data:image/jpeg;base64,%s", videoThumbnail));
            postType = MessageAction.POST_VIDEO;
        } else {
            params.put("audio", "1");
            params.put("audio_duration", durationTime + "");
            msgEntity.setAudio_filename(audioFile);
            msgEntity.setAudio_duration(durationTime + "");
            msgEntity.setFailUri(Uri.fromFile(file));
        }
        String key = postType + System.currentTimeMillis();
        msgEntity.setPhoto_postsize(key);
        msgEntity.setPhoto_thumbsize(postType);
        messageChatAdapter.addMsgEntity(msgEntity);
        sendMap.put(key, msgEntity);
        messageAction.doRequest(MessageAction.REQUEST_UPLOAD, params, Constant.API_MESSAGE_POST_TEXT, SEND_AUDIO_MESSAGE, sendMap, key, postType);
    }

    private String getVideoThumbnail(Uri uri) {
        /**wing modified for pic too large begin*/
        if (uri == null)
            return null;
        return LocalImageLoader.getVideoThumbnail(this, uri);
    }

    private void uploadImage(final Uri uri) {
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setSticker_type(".png");
        msgEntity.setUser_id(MainActivity.getUser().getUser_id());
        msgEntity.setUri(uri);
        msgEntity.setSendStatus(MsgEntity.SEND_IN);
        msgEntity.setContent_creation_date(MyDateUtils.getUTCDateString4DefaultFromLocal(System.currentTimeMillis()));
        final String key = MessageAction.POST_PHOTO + System.currentTimeMillis();
        msgEntity.setPhoto_postsize(key);
        msgEntity.setPhoto_thumbsize(MessageAction.POST_PHOTO);
        messageChatAdapter.addMsgEntity(msgEntity);
        sendMap.put(key, msgEntity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = LocalImageLoader.compressBitmap(mContext, FileUtil.getRealPathFromURI(mContext, uri), 480, 800, false);
                File file = new File(path);
                if (!file.exists()) {
                    sendMap.remove(key);
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
                messageAction.doRequest(MessageAction.REQUEST_UPLOAD, params, Constant.API_MESSAGE_POST_TEXT, SEND_PIC_MESSAGE, sendMap, key, MessageAction.POST_PHOTO);
            }
        }).start();
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
        messageAction.doRequest(MessageAction.REQUEST_GET, null, url, msgIndex, null, null, null);
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
        msgEntity.setContent_creation_date(MyDateUtils.getUTCDateString4DefaultFromLocal(System.currentTimeMillis()));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("group_id", groupId);
        params.put("content_type", "post");
        params.put("sticker_group_path", fileName);
        params.put("sticker_name", Sticker_name);
        params.put("sticker_type", type);
        String key = MessageAction.POST_STICKER + System.currentTimeMillis();
        msgEntity.setSendStatus(MsgEntity.SEND_IN);
        msgEntity.setPhoto_postsize(key);
        msgEntity.setPhoto_thumbsize(MessageAction.POST_STICKER);
        messageChatAdapter.addMsgEntity(msgEntity);
        sendMap.put(key, msgEntity);
        messageAction.doRequest(MessageAction.REQUEST_POST, params, Constant.API_MESSAGE_POST_TEXT, MessageChatActivity.SEND_PIC_MESSAGE, sendMap, key, MessageAction.POST_STICKER);
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
