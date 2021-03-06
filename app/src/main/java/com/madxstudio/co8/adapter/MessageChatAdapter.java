package com.madxstudio.co8.adapter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.MsgEntity;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.task.DownloadStickerTask;
import com.madxstudio.co8.ui.FamilyProfileActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.MeActivity;
import com.madxstudio.co8.ui.MessageChatActivity;
import com.madxstudio.co8.ui.ViewOriginalPicesActivity;
import com.madxstudio.co8.ui.share.PreviewVideoActivity;
import com.madxstudio.co8.util.AudioPlayUtils;
import com.madxstudio.co8.util.AudioPlayUtils.StopCallback;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocationUtil;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.SDKUtil;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.HorizontalProgressBarWithNumber;
import com.madxstudio.co8.widget.MyDialog;
import com.material.widget.CircularProgress;
import com.material.widget.Dialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by quankun on 15/4/24.
 */
public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.VHItem> {
    private List<MsgEntity> myList;
    private Context context;
    private RecyclerView recyclerView;
    private MessageChatActivity messageChatActivity;
    private LinearLayoutManager llm;
    private boolean isGroupChat;
    private static final int FROM_ME_TYPE_TEXT = 1;
    private static final int FROM_ME_TYPE_PIC = 2;
    private static final int FROM_ME_TYPE_LOC = 3;
    private static final int FROM_ME_TYPE_GIF = 4;
    private static final int FROM_ME_TYPE_PNG = 5;

    private static final int FROM_OTHER_TYPE_TEXT = 6;
    private static final int FROM_OTHER_TYPE_PIC = 7;
    private static final int FROM_OTHER_TYPE_LOC = 8;
    private static final int FROM_OTHER_TYPE_GIF = 9;
    private static final int FROM_OTHER_TYPE_PNG = 10;
    private static final int FROM_ME_TYPE_AUDIO = 11;
    private static final int FROM_OTHER_TYPE_AUDIO = 12;
    private static final int FROM_ME_TYPE_VIDEO = 13;
    private static final int FROM_OTHER_TYPE_VIDEO = 14;
    private boolean isIconOnClick = true;
    private static final int PLAY_AUDIO_HANDLER = 0X110;
    private String audioName;
    private int playPros;
    private int clickPosition;
    private AudioPlayUtils playerManager;

    public MessageChatAdapter(Context context, List<MsgEntity> myList, RecyclerView recyclerView, MessageChatActivity messageChatActivity, LinearLayoutManager llm, boolean isGroupChat, AudioPlayUtils playerManager) {
        this.context = context;
        this.myList = myList;
        this.recyclerView = recyclerView;
        this.messageChatActivity = messageChatActivity;
        this.llm = llm;
        this.isGroupChat = isGroupChat;
        this.playerManager = playerManager;
    }

    public void addHistoryData(List<MsgEntity> list) {
        List<MsgEntity> msgList = new ArrayList<>();
        for (MsgEntity msgEntity : list) {
            if (!myList.contains(msgEntity)) {
                msgList.add(msgEntity);
            }
        }
        int listSize = list.size();
        myList.addAll(0, msgList);
        notifyDataSetChanged();
        llm.scrollToPosition(listSize);
    }

    public void addData(List<MsgEntity> list) {
        myList.addAll(0, list);
        notifyDataSetChanged();
        llm.scrollToPosition(getItemCount() - 1);
    }

    public void addTimerData(List<MsgEntity> list) {
        int scrollPosition = llm.findLastVisibleItemPosition();
        if (myList != null && myList.size() > 0) {
            myList.clear();
        }
        myList.addAll(list);
        notifyDataSetChanged();
        llm.scrollToPosition(scrollPosition);
    }

    public void addSendData(List<MsgEntity> list) {
        if (null == list) {
            return;
        }
        myList.clear();
        myList.addAll(list);
        notifyDataSetChanged();
        llm.scrollToPosition(getItemCount() - 1);
    }

    public void addMsgEntity(MsgEntity msgEntity) {
        if (messageChatActivity.empty_message.getVisibility() == View.VISIBLE) {
            messageChatActivity.empty_message.setVisibility(View.GONE);
            messageChatActivity.swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        int listSize = myList.size();
        myList.add(msgEntity);
        notifyDataSetChanged();
        llm.scrollToPosition(getItemCount() - 1);
        //notifyItemInserted(myList.size());
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public void setPlayPros(int playPros) {
        this.playPros = playPros;
    }

    @Override
    public int getItemViewType(int position) {
        MsgEntity msgEntity = myList.get(position);
        if (msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id())) {
            if (null != msgEntity.getText_id()) {
                return FROM_ME_TYPE_TEXT;
            } else if (msgEntity.getFile_id() != null) {
                return FROM_ME_TYPE_PIC;
            } else if (msgEntity.getLoc_id() != null) {
                return FROM_ME_TYPE_LOC;
            } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {
                return FROM_ME_TYPE_GIF;
            } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {
                return FROM_ME_TYPE_PNG;
            } else if (msgEntity.getVideo_filename() != null) {
                return FROM_ME_TYPE_VIDEO;
            } else if (msgEntity.getAudio_filename() != null) {
                return FROM_ME_TYPE_AUDIO;
            } else {
                return FROM_ME_TYPE_PIC;
            }
        } else {
            if (null != msgEntity.getText_id()) {
                return FROM_OTHER_TYPE_TEXT;
            } else if (msgEntity.getFile_id() != null) {
                return FROM_OTHER_TYPE_PIC;
            } else if (msgEntity.getLoc_id() != null) {
                return FROM_OTHER_TYPE_LOC;
            } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {
                return FROM_OTHER_TYPE_GIF;
            } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {
                return FROM_OTHER_TYPE_PNG;
            } else if (msgEntity.getVideo_filename() != null) {
                return FROM_OTHER_TYPE_VIDEO;
            } else if (msgEntity.getAudio_filename() != null) {
                return FROM_OTHER_TYPE_AUDIO;
            } else {
                return FROM_OTHER_TYPE_PIC;
            }
        }
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        LayoutInflater mInflater = LayoutInflater.from(context);
        switch (viewType) {
            case FROM_ME_TYPE_TEXT:
                convertView = mInflater.inflate(R.layout.message_item_right_text, null);
                break;
            case FROM_ME_TYPE_PIC:
                convertView = mInflater.inflate(R.layout.message_item_right_pic, null);
                break;
            case FROM_ME_TYPE_LOC:
                convertView = mInflater.inflate(R.layout.message_item_right_pic, null);
                break;
            case FROM_ME_TYPE_GIF:
                convertView = mInflater.inflate(R.layout.message_item_right_gif_pic, null);
                break;
            case FROM_ME_TYPE_PNG:
                convertView = mInflater.inflate(R.layout.message_item_right_png_pic, null);
                break;
            case FROM_OTHER_TYPE_TEXT:
                convertView = mInflater.inflate(R.layout.message_item_left_text, null);
                break;
            case FROM_OTHER_TYPE_PIC:
                convertView = mInflater.inflate(R.layout.message_item_left_pic, null);
                break;
            case FROM_OTHER_TYPE_LOC:
                convertView = mInflater.inflate(R.layout.message_item_left_pic, null);
                break;
            case FROM_OTHER_TYPE_GIF:
                convertView = mInflater.inflate(R.layout.message_item_left_gif_pic, null);
                break;
            case FROM_OTHER_TYPE_PNG:
                convertView = mInflater.inflate(R.layout.message_item_left_png_pic, null);
                break;
            case FROM_OTHER_TYPE_AUDIO:
                convertView = mInflater.inflate(R.layout.message_item_left_audio, null);
                break;
            case FROM_OTHER_TYPE_VIDEO:
                convertView = mInflater.inflate(R.layout.message_item_left_video, null);
                break;
            case FROM_ME_TYPE_AUDIO:
                convertView = mInflater.inflate(R.layout.message_item_right_audio, null);
                break;
            case FROM_ME_TYPE_VIDEO:
                convertView = mInflater.inflate(R.layout.message_item_right_video, null);
                break;
        }
        return new VHItem(convertView);
    }

    @Override
    public void onBindViewHolder(final VHItem holder, int position) {
        MsgEntity msgEntity = myList.get(position);
        boolean isSendMe = msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id());
        holder.dateTime.setText(MyDateUtils.getLocalDateStringFromUTC(context, msgEntity.getContent_creation_date()));
        String iconUrl = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id());
        //网络获取头像图片
        if (MainActivity.getUser().getUser_id().equals(msgEntity.getUser_id())) {
            BitmapTools.getInstance(context).display(holder.iconImage, iconUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        } else {
            VolleyUtil.initNetworkImageView(context, holder.iconImage, iconUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        }
//        VolleyUtil.initNetworkImageView(context, holder.iconImage, iconUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        if (!isSendMe) {
            if (isGroupChat) {
                holder.leftName.setVisibility(View.VISIBLE);
                holder.leftName.setText(msgEntity.getUser_given_name());
                if (PrivateMessageEntity.STATUS_DE_ACTIVE.equalsIgnoreCase(msgEntity.getStatus())) {
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.user_left_minilcon);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.leftName.setCompoundDrawables(drawable, null, null, null);
                    holder.leftName.setCompoundDrawablePadding(10);
                    holder.leftName.setGravity(Gravity.CENTER_VERTICAL);
                } else {
                    holder.leftName.setCompoundDrawables(null, null, null, null);
                }
            } else {
                holder.leftName.setVisibility(View.GONE);
            }
        } else {
            if (holder.msgFail != null) {
                holder.msgFail.setVisibility(View.GONE);
            }
            if (holder.sendProgress != null) {
                holder.sendProgress.setVisibility(View.GONE);
            }
            String sendStatus = msgEntity.getSendStatus();
            if (MsgEntity.SEND_FAIL.equals(sendStatus) && holder.msgFail != null) {
                holder.msgFail.setVisibility(View.VISIBLE);
            } else if (MsgEntity.SEND_IN.equals(sendStatus) && holder.sendProgress != null) {
                holder.sendProgress.setVisibility(View.VISIBLE);
            }
        }
        if (!TextUtils.isEmpty(msgEntity.getText_id())) {//文字
            holder.messageText.setText(msgEntity.getText_description());
//            String atDescription = msgEntity.getText_description();
//            holder.messageText.setMovementMethod(LinkMovementMethod.getInstance());
//            atDescription += " ";
//            SpannableStringBuilder ssb = new SpannableStringBuilder(atDescription);
//            SpannableString ssMind = new SpannableString(atDescription);
//            ssMind.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                    ds.setColor(Color.BLACK);
//                }
//            }, 0, atDescription.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            try {
//                if (!TextUtils.isEmpty(atDescription)) {
//                    String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
//                    for (String key : fbsArr) {
//                        if (atDescription.contains(key)) {
//                            atDescription = atDescription.replace(key, "\\" + key);
//                        }
//                    }
//                }
//                Pattern p = Pattern.compile(atDescription);
//                Matcher m = p.matcher(ssb.toString());
//                if (m.find()) {
//                    int start = m.start();
//                    int end = m.end();
//                    ssb.replace(start, end - 1, ssMind);
//                } else {
//                    ssb.append(ssMind);
//                }
//            } catch (Exception e) {
//                ssb.append(ssMind);
//                e.printStackTrace();
//            }
//            holder.messageText.setText(ssb);
        } else if (msgEntity.getLoc_id() != null) {//地图 item
            String locUrl = LocationUtil.getLocationPicUrl(context, msgEntity.getLoc_latitude(), msgEntity.getLoc_longitude(), msgEntity.getLoc_type());
            VolleyUtil.initNetworkImageView(context, holder.networkImageView, locUrl, R.drawable.network_image_default, R.drawable.network_image_default);
        } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {//gif item
            holder.progressBar.setVisibility(View.GONE);
            holder.gifImageView.setImageDrawable(null);
            String stickerGroupPath = msgEntity.getSticker_group_path();
            if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                stickerGroupPath = stickerGroupPath.replace("/", "");
            }
//            String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator +"B"+ File.separator+ msgEntity.getSticker_name() + ".gif";
            String gifFilePath = FileUtil.getBigStickerPath(context, stickerGroupPath, msgEntity.getSticker_name(), msgEntity.getSticker_type());
            try {
                //GifDrawable gifDrawable = new GifDrawable(context.getAssets(), gifFilePath);
                Log.i("stickerPath", gifFilePath);
                File file = new File(gifFilePath);
                GifDrawable gifDrawable = null;
                if (file != null && file.exists()) {
                    gifDrawable = new GifDrawable(file);
                }
                if (gifDrawable != null) {
                    holder.gifImageView.setImageDrawable(gifDrawable);
                } else {
                    DownloadStickerTask.getInstance().downloadGifSticker(holder.progressBar, stickerGroupPath, msgEntity.getSticker_name(), R.drawable.network_image_default, holder.gifImageView);
                }
            } catch (Exception e) {
                DownloadStickerTask.getInstance().downloadGifSticker(holder.progressBar, stickerGroupPath, msgEntity.getSticker_name(), R.drawable.network_image_default, holder.gifImageView);
                LogUtil.e("", "插入sticker info", e);
            }
        } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {//Png
            holder.pngImageView.setImageResource(R.drawable.network_image_default);
            holder.pngImageView.setImageDrawable(null);
            holder.progressBar.setVisibility(View.GONE);
            if (msgEntity.getUri() != null)//直接显示在ListView,相册和相机
            {
//                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                bitmapOptions.inSampleSize = 4;
//                Bitmap bitmap = LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(msgEntity.getUri().getPath()), ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(context, msgEntity.getUri())), 200, 200));
//                holder.pngImageView.setImageBitmap(bitmap);//直接把图片显示出来
                ImageLoader.getInstance().displayImage(msgEntity.getUri().toString(), holder.pngImageView, UniversalImageLoaderUtil.options);
            } else {
                String stickerGroupPath = msgEntity.getSticker_group_path();
                if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }
//                String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator +"B"+ File.separator+ msgEntity.getSticker_name() + ".png";
                String pngFileName = FileUtil.getBigStickerPath(context, stickerGroupPath, msgEntity.getSticker_name(), msgEntity.getSticker_type());
                try {
                    //拼接大图路径
                    //InputStream is = context.getAssets().open(pngFileName);//得到数据流
                    Log.i("stickerPath", pngFileName);
                    File file = new File(pngFileName);
                    InputStream is = null;
                    if (file != null && file.exists()) {
                        is = new FileInputStream(file);//得到数据流
                    }
                    if (is != null) {//如果有图片直接显示，否则网络下载
                        Bitmap bitmap = BitmapFactory.decodeStream(is);//将流转化成Bitmap对象
                        holder.pngImageView.setImageBitmap(bitmap);//显示图片
                    } else {
                        DownloadStickerTask.getInstance().downloadPngSticker(holder.progressBar, stickerGroupPath, msgEntity.getSticker_name(), R.drawable.network_image_default, holder.pngImageView);
                    }
                } catch (Exception e) {
                    //本地没有png的时候，从服务器下载
                    DownloadStickerTask.getInstance().downloadPngSticker(holder.progressBar, stickerGroupPath, msgEntity.getSticker_name(), R.drawable.network_image_default, holder.pngImageView);
                    LogUtil.e("", "插入sticker info", e);
                }
            }
        } else if (!TextUtils.isEmpty(msgEntity.getFile_id())) {
            holder.progressBar.setVisibility(View.GONE);
            VolleyUtil.initNetworkImageView(context, holder.networkImageView, String.format(Constant.API_GET_PIC, "post_preview_m", msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        } else if (!TextUtils.isEmpty(msgEntity.getAudio_filename())) {
            String name = msgEntity.getAudio_filename();
            holder.id_progressbar.setProgress(0);
            holder.audio_time.setText(MyDateUtils.formatRecordTimeForString(msgEntity.getAudio_duration()));
            if (name != null && name.equals(audioName) && playPros != 0) {
                holder.id_progressbar.setProgress(playPros);
                Map<String, Object> map = new HashMap<>();
                String audioDuration = msgEntity.getAudio_duration();
                audioDuration = formatTime(audioDuration);
                map.put("duration", audioDuration);
                map.put("position", position);
                map.put("name", msgEntity.getAudio_filename());
                mHandler.sendMessage(mHandler.obtainMessage(PLAY_AUDIO_HANDLER, map));
            } else {
                holder.id_progressbar.setProgress(0);
            }
            DownloadStickerTask.getInstance().downloadAudioFile(context, msgEntity.getUser_id(), name);
        } else if (!TextUtils.isEmpty(msgEntity.getVideo_filename())) {
            holder.progressBar.setVisibility(View.GONE);
            holder.video_time.setText(MyDateUtils.formatRecordTimeForString(msgEntity.getVideo_duration()));
            final String video_format = msgEntity.getVideo_format1();
            if (video_format != null) {
                holder.message_video_start.setVisibility(View.GONE);
                holder.btn_video.setImageResource(R.drawable.btn_video);
                if (null != holder.message_video_default) {
                    holder.message_video_default.setVisibility(View.VISIBLE);
                    Bitmap bitmap = base64ToBitmap(video_format);
                    if (bitmap != null) {
                        holder.message_video_default.setImageBitmap(bitmap);
                    } else {
                        holder.message_video_default.setImageResource(R.drawable.network_image_default);
                    }
                } else {
                    holder.message_video_start.setVisibility(View.VISIBLE);
                }
            } else {
                if (null != holder.message_video_default) {
                    holder.message_video_default.setVisibility(View.GONE);
                }
                holder.message_video_start.setVisibility(View.VISIBLE);
                File file = new File(PreviewVideoActivity.VIDEO_PATH + msgEntity.getVideo_filename());
                if (file != null && file.exists()) {
                    holder.btn_video.setImageResource(R.drawable.btn_video);
                } else {
                    holder.btn_video.setImageResource(R.drawable.download_video);
                }
                String videoUrl = String.format(Constant.API_MESSAGE_DOWNLOAD_VIDEO_PIC, msgEntity.getUser_id(), msgEntity.getVideo_thumbnail());
                VolleyUtil.initNetworkImageView(context, holder.message_video_start, videoUrl, R.drawable.network_image_default, R.drawable.network_image_default);
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_AUDIO_HANDLER:
                    Map<String, Object> map = (Map) msg.obj;
                    if (map == null) {
                        break;
                    }
                    int position = (int) map.get("position");
                    String duration = (String) map.get("duration");
                    String name = (String) map.get("name");
                    int playTime = 0;
                    try {
                        playTime = Integer.parseInt(duration);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    VHItem holder = (VHItem) recyclerView.findViewHolderForAdapterPosition(position);
                    if (holder == null) {
                        break;
                    }
                    HorizontalProgressBarWithNumber mProgressBar = (HorizontalProgressBarWithNumber) holder.itemView.findViewById(R.id.id_progressbar);
                    if (mProgressBar == null) {
                        break;
                    }
                    if (playTime != 0) {
                        mProgressBar.setMax(playTime);
                        mProgressBar.setProgress(++playPros);
                        audioName = name;
                    }
                    if (playPros > playTime) {
                        mProgressBar.setProgress(0);
                        mHandler.removeMessages(PLAY_AUDIO_HANDLER);
                        audioName = null;
                        playPros = 0;
                    } else {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(PLAY_AUDIO_HANDLER, map), 1000);
                    }
            }
            return false;
        }
    });

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        //接收
        CircularNetworkImage iconImage;
        TextView messageText;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        TextView dateTime;
        TextView leftName;
        ImageView pngImageView;
        CircularProgress progressBar;
        RelativeLayout audio_play;
        HorizontalProgressBarWithNumber id_progressbar;
        TextView audio_time;
        NetworkImageView message_video_start;
        ImageView btn_video;
        TextView video_time;
        CircularProgress sendProgress;
        ImageView msgFail;
        ImageView message_video_default;
        RelativeLayout pic_linear;

        public VHItem(View itemView) {
            super(itemView);
            iconImage = (CircularNetworkImage) itemView.findViewById(R.id.message_icon_image);
            messageText = (TextView) itemView.findViewById(R.id.message_item_content_tv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            dateTime = (TextView) itemView.findViewById(R.id.date_time_tv);
            leftName = (TextView) itemView.findViewById(R.id.tv_name);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_pic_png_iv);
            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
            audio_play = (RelativeLayout) itemView.findViewById(R.id.audio_play);
            id_progressbar = (HorizontalProgressBarWithNumber) itemView.findViewById(R.id.id_progressbar);
            audio_time = (TextView) itemView.findViewById(R.id.audio_time);
            message_video_start = (NetworkImageView) itemView.findViewById(R.id.message_video_start);
            video_time = (TextView) itemView.findViewById(R.id.video_time);
            btn_video = (ImageView) itemView.findViewById(R.id.btn_video);
            sendProgress = (CircularProgress) itemView.findViewById(R.id.send_progress_bar);
            msgFail = (ImageView) itemView.findViewById(R.id.msg_send_fail_iv);
            message_video_default = (ImageView) itemView.findViewById(R.id.message_video_default);
            pic_linear = (RelativeLayout) itemView.findViewById(R.id.pic_linear_re);

            if (null != iconImage) {
                iconImage.setOnClickListener(this);
            }
            if (null != networkImageView) {
                networkImageView.setOnClickListener(this);
            }
            if (null != audio_play) {
                audio_play.setOnClickListener(this);
            }
            if (pic_linear != null) {
                pic_linear.setOnClickListener(this);
            }
            if (msgFail != null) {
                msgFail.setOnClickListener(this);
            }

            if (null != messageText) {
                messageText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setOnLongClickListener(getAdapterPosition());
                        return true;
                    }
                });
            }
            if (null != networkImageView) {
                networkImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setOnLongClickListener(getAdapterPosition());
                        return true;
                    }
                });
            }
            if (null != gifImageView) {
                gifImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setOnLongClickListener(getAdapterPosition());
                        return true;
                    }
                });
            }
            if (null != pngImageView) {
                pngImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setOnLongClickListener(getAdapterPosition());
                        return true;
                    }
                });
            }
            if (null != pic_linear) {
                pic_linear.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setOnLongClickListener(getAdapterPosition());
                        return true;
                    }
                });
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setOnLongClickListener(getAdapterPosition());
                    return false;
                }
            });
        }

        //点击事件
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (myList == null || position > myList.size() || position < 0) {
                return;
            }
            final MsgEntity msgEntity = myList.get(position);
            boolean isFromMe = msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id());
            Intent intent;
            switch (v.getId()) {
                case R.id.message_icon_image://点击头像跳转个人资料
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    if (!isIconOnClick) {
                        return;
                    }
                    isIconOnClick = false;
                    if (isFromMe) {
                        intent = new Intent(context, MeActivity.class);
                        context.startActivity(intent);
                        isIconOnClick = true;
                    } else {
                        if (!isGroupChat) {
                            intent = new Intent(context, FamilyProfileActivity.class);
                            intent.putExtra(UserEntity.EXTRA_MEMBER_ID, msgEntity.getUser_id());
                            intent.putExtra(UserEntity.EXTRA_GROUP_ID, msgEntity.getGroup_id());
                            intent.putExtra(UserEntity.EXTRA_GROUP_NAME, msgEntity.getUser_given_name());
                            context.startActivity(intent);
                            isIconOnClick = true;
                        } else {
                            String url = String.format(Constant.API_MESSAGE_GROUP_IS_FRIEND, MainActivity.getUser().getUser_id(), msgEntity.getUser_id());
                            new HttpTools(context).get(url, null, "MessageChatActivity", new HttpCallback() {
                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onFinish() {
                                    isIconOnClick = true;
                                }

                                @Override
                                public void onResult(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String memberFlag = jsonObject.optString("member_flag");
                                        if ("1".equals(memberFlag)) {
                                            Intent intent = new Intent(context, FamilyProfileActivity.class);
                                            intent.putExtra(UserEntity.EXTRA_MEMBER_ID, msgEntity.getUser_id());
                                            intent.putExtra(UserEntity.EXTRA_GROUP_ID, msgEntity.getGroup_id());
                                            intent.putExtra(UserEntity.EXTRA_GROUP_NAME, msgEntity.getUser_given_name());
                                            context.startActivity(intent);
                                        } else {
                                            MessageUtil.getInstance().showShortToast(context.getString(R.string.text_show_message_is_friend));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    MessageUtil.getInstance().showShortToast(context.getResources().getString(R.string.text_error));
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
                    break;
                case R.id.message_pic_iv://点击图片跳转大图
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    if (msgEntity.getLoc_id() != null) {//地图大图片
                        //图片路径
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                        //??
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    } else if (msgEntity.getFile_id() != null) {//相册图片
                        intent = new Intent(context, ViewOriginalPicesActivity.class);
                        ArrayList<PhotoEntity> data = new ArrayList();//图片实体集合
                        PhotoEntity peData = new PhotoEntity();
                        peData.setUser_id(msgEntity.getUser_id());
                        peData.setFile_id(msgEntity.getFile_id());
                        peData.setPhoto_caption(Constant.Module_Original);
                        peData.setPhoto_multipe("false");
                        data.add(peData);
                        intent.putExtra("is_data", true);
                        intent.putExtra("datas", data);
                        context.startActivity(intent);
                    }
                    break;
                case R.id.audio_play:
                    resumePlayAudioStatus();
                    clickPosition = position;
                    String path = FileUtil.getAudioRootPath(context) + File.separator + msgEntity.getAudio_filename();
//                    AudioPlayUtils.getInstance(path, llm, MessageChatAdapter.this).playAudio();
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    if (null == playerManager) {
                        playerManager = AudioPlayUtils.getManager();
                    }
                    playerManager.play(path, stopCallback);
                    Map<String, Object> map = new HashMap<>();
                    String audioDuration = msgEntity.getAudio_duration();
                    audioDuration = formatTime(audioDuration);
                    map.put("duration", audioDuration);
                    map.put("position", position);
                    map.put("name", msgEntity.getAudio_filename());
                    audioName = null;
                    playPros = 0;
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(PLAY_AUDIO_HANDLER, map), 500);
                    break;
                case R.id.pic_linear_re:
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    String video_format = msgEntity.getVideo_format1();
                    intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
                    if (video_format != null) {
                        intent.putExtra(PreviewVideoActivity.EXTRA_VIDEO_URI, msgEntity.getVideo_format2());
                    } else {
                        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, msgEntity.getUser_id());
                        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, msgEntity.getVideo_filename());
                    }
                    context.startActivity(intent);
                    break;
                case R.id.msg_send_fail_iv:
//                    AudioPlayUtils.stopAudio();
                    if (playerManager != null) {
                        playerManager.stop();
                    }
                    VHItem holder1 = (VHItem) recyclerView.findViewHolderForAdapterPosition(position);
                    if (holder1 == null) {
                        return;
                    }
                    if (sendFailMsgClick != null && !TextUtils.isEmpty(msgEntity.getPhoto_postsize())) {
                        CircularProgress mProgressBar = (CircularProgress) holder1.itemView.findViewById(R.id.send_progress_bar);
                        v.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        msgEntity.setSendStatus(MsgEntity.SEND_IN);
                        sendFailMsgClick.sendFailMsg(msgEntity);
                    }
                    break;
            }
        }
    }

    private void resumePlayAudioStatus() {
        mHandler.removeMessages(PLAY_AUDIO_HANDLER);
        VHItem holder = (VHItem) recyclerView.findViewHolderForAdapterPosition(clickPosition);
        if (holder != null) {
            HorizontalProgressBarWithNumber mProgressBar = (HorizontalProgressBarWithNumber) holder.itemView.findViewById(R.id.id_progressbar);
            if (mProgressBar != null) {
                mProgressBar.setProgress(0);
            }
        }
    }

    StopCallback stopCallback = new StopCallback() {
        @Override
        public void stopPlayAudio() {
            resumePlayAudioStatus();
        }
    };

    public void setOnLongClickListener(final int position) {
        VHItem holder = (VHItem) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) {
            return;
        }
        if (myList == null || position > myList.size() || position < 0) {
            return;
        }
        if (holder.sendProgress != null && holder.sendProgress.getVisibility() == View.VISIBLE) {
            return;
        }
        final MsgEntity msgEntity = myList.get(position);
        boolean isFromMe = msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id());
        boolean showCopy = false;
        if (null != msgEntity.getText_id()) {
            showCopy = true;
        }
        if (!isFromMe && !showCopy) {
            return;
        }
        View selectIntention = LayoutInflater.from(context).inflate(R.layout.dialog_message_delete, null);
        final MyDialog showSelectDialog = new MyDialog(context, null, selectIntention);
        TextView copyText = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView deleteText = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        View copyLinear = selectIntention.findViewById(R.id.message_copy_view);
        copyText.setText(R.string.text_message_copy);
        deleteText.setText(context.getString(R.string.text_delete));
        if (!showCopy) {
            copyText.setVisibility(View.GONE);
            copyLinear.setVisibility(View.GONE);
        } else {
            copyText.setVisibility(View.VISIBLE);
            copyLinear.setVisibility(View.VISIBLE);
        }
        if (!isFromMe && showCopy) {
            copyLinear.setVisibility(View.GONE);
            deleteText.setVisibility(View.GONE);
        }
        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                String content = msgEntity.getText_description();
                if (SDKUtil.IS_HONEYCOMB) {
                    android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    c.setPrimaryClip(ClipData.newPlainText("", content));
                } else {
                    android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    c.setText(content);
                }
            }
        });

        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                RequestInfo requestInfo = new RequestInfo();
                requestInfo.headers = HttpTools.getHeaders();
                requestInfo.url = String.format(Constant.API_MESSAGE_DELETE, msgEntity.getContent_group_id());
                new HttpTools(context).put(requestInfo, "MessageChatActivity", new HttpCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResult(String string) {
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            if (("Server.DeleteSuccess").equals(jsonObject.optString("response_message"))) {
                                messageChatActivity.getNewMsg();
//                                myList.remove(position);
//                                notifyItemRemoved(position);
                            }
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
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    public SendFailMsgClick sendFailMsgClick;

    public void onClickFailMsg(SendFailMsgClick sendFailMsgClick) {
        this.sendFailMsgClick = sendFailMsgClick;
    }

    public interface SendFailMsgClick {
        void sendFailMsg(MsgEntity msgEntity);
    }

    private String formatTime(String audioDuration) {
        if (audioDuration.contains(":")) {
            int minute = 0;
            int seconds = 0;
            try {
                minute = Integer.parseInt(audioDuration.substring(0, audioDuration.indexOf(":")));
                seconds = Integer.parseInt(audioDuration.substring(audioDuration.indexOf(":") + 1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception e) {
                return "0";
            }
            audioDuration = minute * 60 + seconds + "";
        }
        if (audioDuration.contains("：")) {
            int minute = 0;
            int seconds = 0;
            try {
                minute = Integer.parseInt(audioDuration.substring(0, audioDuration.indexOf("：")));
                seconds = Integer.parseInt(audioDuration.substring(audioDuration.indexOf("：") + 1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception e) {
                return "0";
            }
            audioDuration = minute * 60 + seconds + "";
        }
        return audioDuration;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
