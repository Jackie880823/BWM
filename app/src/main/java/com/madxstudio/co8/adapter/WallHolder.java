package com.madxstudio.co8.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.MediaData;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.http.PicturesCacheUtil;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.interfaces.WallViewClickListener;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.share.PreviewVideoActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.ui.wall.DiaryCommentActivity;
import com.madxstudio.co8.ui.wall.DiaryInformationFragment;
import com.madxstudio.co8.ui.wall.NewDiaryActivity;
import com.madxstudio.co8.ui.wall.WallViewPicActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.LocationUtil;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.SDKUtil;
import com.madxstudio.co8.util.WallUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.FreedomSelectionTextView;
import com.madxstudio.co8.widget.MyDialog;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jackie on 8/7/15.
 * <p/>
 * 日志内容的{@link RecyclerView.ViewHolder}, 在Wall界面各Comment都使用这个Holder来显示日志内容
 *
 * @author Jackie
 * @version 1.0
 */
public class WallHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = WallHolder.class.getSimpleName();
    private static final int ACTION_POST_PHOTOS_SUCCEED = 100;
    private static final int ACTION_POST_PHOTOS_FAIL = 101;

    private static final String POST_LOVE = TAG + "_POST_LOVE";
    private static final String UPLOAD_PIC = TAG + "_UPLOAD_PIC";
    private static final String SAVE_PHOTO = TAG + "_SAVE_PHOTO";
    private static final String PUT_PHOTO_MAX = TAG + "_PUT_PHOTO_MAX";

    private WallViewClickListener mViewClickListener;
    private int position = -1;
    private WallEntity wallEntity;
    private HttpTools mHttpTools;
    private Context context;
    private BaseFragment fragment;

    /**
     * 监听日志内容设置的变化，根据{@link #needFull}的值来剪切文字
     */
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    /**
     * 头像视图
     */
    private CircularNetworkImage nivHead;
    /**
     * 日志文字内容
     */
    private FreedomSelectionTextView tvContent;

    /**
     * 切换日志文件的开关
     */
    private TextView tvSwitch;

    /**
     * 发表日期显示视图
     */
    private TextView tvDate;

    /**
     * Wall公开性标识图标
     */
    private ImageView ivLock;

    /**
     * 用户昵称视图
     */
    private TextView tvUserName;

    /**
     * 网络图片视图父控件
     */
    public View llWallsImage;

    /**
     * 显示网络图片的视图控件
     */
    private NetworkImageView imWallsImages;
    /**
     * 视频图标
     */
    private ImageView videoImage;

    /**
     * 用于显示图片总数的提示控件
     */
    private TextView tvPhotoCount;

    /**
     * 红星总数视图
     */
    private TextView tvAgreeCount;

    /**
     * 点赞用户名显示列表
     */
    //    private TextView tvLoveList;


    /**
     * 评论总数视图
     */
    private final TextView tvCommentCount;

    private View llComment;

    /**
     * 红心按钮点击添加或者取消赞
     */
    private ImageView ibAgree;

    /**
     * 更多功能视图
     */
    private ImageButton btnOption;

    /**
     * 心情视图
     */
    private ImageView iv_mood;

    /**
     * 显示地址信息的布局
     */
    private LinearLayout llLocation;
    /**
     * 显示地图标，可点击跳转至地图
     */
    private ImageView ivLocation;
    /**
     * 显示地址名称，可点击跳转至地图
     */
    private TextView tvLocation;

    private boolean needFull = false;

    private boolean isDetailed;
    private View ll_love;

    private List<CompressBitmapTask> tasks = new ArrayList<>();
    private List<Uri> localPhotos = new ArrayList<>();
    private CallBack callBack = new CallBack();
    Handler mHandler = new Handler() {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_POST_PHOTOS_SUCCEED:
                    mViewClickListener.addPhotoed(wallEntity, true);
                    break;
                case ACTION_POST_PHOTOS_FAIL:
                    mViewClickListener.addPhotoed(wallEntity, false);
                    break;
            }
        }
    };
    private ArrayList<PhotoEntity> data = new ArrayList<>();
    private int lastPic = 0;

    public final TextView getTvCommentCount() {
        return tvCommentCount;
    }

    public void setWallEntity(WallEntity wallEntity) {
        this.wallEntity = wallEntity;
    }

    public void setViewClickListener(WallViewClickListener mViewClickListener) {
        this.mViewClickListener = mViewClickListener;
    }

    /**
     * @param itemView   日志视图的整个UI
     * @param httpTools  网络上传工具
     * @param isDetailed 是wall的详情：{@value true} 是wall详情；{@value false} 不是wall详情
     * @param fragment   用于引导应用资源
     */
    public WallHolder(BaseFragment fragment, View itemView, HttpTools httpTools, boolean isDetailed) {
        // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
        super(itemView);
        mHttpTools = httpTools;
        this.context = fragment.getContext();
        this.fragment = fragment;
        this.isDetailed = isDetailed;
        nivHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
        tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
        tvContent = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_wall_content);
        tvSwitch = (TextView) itemView.findViewById(R.id.switch_text_show);
        tvDate = (TextView) itemView.findViewById(R.id.push_date);
        ivLock = (ImageView) itemView.findViewById(R.id.lock_post_iv);
        llWallsImage = itemView.findViewById(R.id.ll_walls_image);
        imWallsImages = (NetworkImageView) itemView.findViewById(R.id.iv_walls_images);
        videoImage = (ImageView) itemView.findViewById(R.id.iv_video_top);
        tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_wall_photo_count);
        tvAgreeCount = (TextView) itemView.findViewById(R.id.tv_wall_agree_count);
        //        tvLoveList = (TextView) itemView.findViewById(R.id.tv_love_list);
        tvCommentCount = (TextView) itemView.findViewById(R.id.tv_wall_relay_count);
        ibAgree = (ImageView) itemView.findViewById(R.id.iv_love);
        btnOption = (ImageButton) itemView.findViewById(R.id.btn_option);
        iv_mood = (ImageView) itemView.findViewById(R.id.iv_mood);
        llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
        ivLocation = (ImageView) itemView.findViewById(R.id.iv_location);
        tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
        ll_love=itemView.findViewById(R.id.ll_love);

        if (!isDetailed) { // 是列表要做文字内容切换
            switchContentShow(needFull);
        }

        itemView.findViewById(R.id.top_event).setOnClickListener(this);
        llComment = itemView.findViewById(R.id.ll_comment);
        llComment.setOnClickListener(this);
        tvAgreeCount.setOnClickListener(this);
        //        tvLoveList.setOnClickListener(this);
//        ibAgree.setOnClickListener(this);
        btnOption.setOnClickListener(this);
        imWallsImages.setOnClickListener(this);
        tvSwitch.setOnClickListener(this);
        ll_love.setOnClickListener(this);
    }

    /**
     * 设置收起开头的显示/隐藏
     *
     * @param visibility
     * @see View#setVisibility(int)
     */
    public void setSwitchVisibility(int visibility) {
        tvSwitch.setVisibility(visibility);
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_wall_agree_count:
            case R.id.tv_love_list:
//                if (WallEntity.CONTENT_TYPE_ADS.equals(wallEntity.getContent_type())) {
//                    LogUtil.i(TAG, "is ADS can't show member");
//                } else {
//
//                    if (Integer.valueOf(wallEntity.getLove_count()) > 0) {
//                        if (mViewClickListener != null) {
//                            mViewClickListener.showLovedMember(accountUserId, wallEntity.getContent_id(), WallUtil.LOVE_MEMBER_WALL_TYPE);
//                        }
//                    }
//                }
                break;

            case R.id.ll_love:
                updateLovedView();

                if (TextUtils.isEmpty(wallEntity.getLove_id())) {
                    clickLove(wallEntity, false);
                } else {
                    clickLove(wallEntity, true);
                }

                //判断是否已经有进行中的判断
                //                if(!runningList.contains(position)) {
                //                    runningList.add(position);
                //                    check(position);
                //                }
                break;
            case R.id.iv_love:
//                updateLovedView();
//
//                if (TextUtils.isEmpty(wallEntity.getLove_id())) {
//                    doLove(wallEntity, false);
//                } else {
//                    doLove(wallEntity, true);
//                }
//
//                //判断是否已经有进行中的判断
//                //                if(!runningList.contains(position)) {
//                //                    runningList.add(position);
//                //                    check(position);
//                //                }
//                break;
            case R.id.ll_comment: {
                Intent intent;
                intent = new Intent(context, DiaryCommentActivity.class);
                intent.putExtra(Constant.CONTENT_GROUP_ID, wallEntity.getContent_group_id());
                intent.putExtra(Constant.CONTENT_ID, wallEntity.getContent_id());
                intent.putExtra(Constant.GROUP_ID, wallEntity.getGroup_id());
                intent.putExtra(Constant.AGREE_COUNT, wallEntity.getLove_count());
                intent.putExtra(Constant.POSITION, position);
                fragment.startActivityForResult(intent, Constant.INTENT_UPDATE_DIARY);
            }
            break;

            case R.id.tv_wall_content:
            case R.id.top_event:
                if (WallEntity.CONTENT_TYPE_ADS.equals(wallEntity.getContent_type())) {
                    if (TextUtils.isEmpty(wallEntity.getVideo_filename())) {// 没有视频需要打开网页
                        String trackUrl = wallEntity.getTrack_url() + MainActivity.getUser().getUser_id();
                        if (!TextUtils.isEmpty(trackUrl)) {
                            Uri uri = Uri.parse(trackUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(intent);
                        }
                    }
                } else {
                    if (mViewClickListener != null) {
                        mViewClickListener.showDiaryInformation(wallEntity);
                    }
                }
                break;
            case R.id.iv_walls_images:
                if (needOpenWeb(wallEntity)) {
                    String trackUrl = wallEntity.getTrack_url() + MainActivity.getUser().getUser_id();
                    if (!TextUtils.isEmpty(trackUrl)) {
                        Uri uri = Uri.parse(trackUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                } else {
                    if (TextUtils.isEmpty(wallEntity.getVideo_filename())) {
                        showOriginPic();
                    } else {
                        showPreviewVideo();
                    }
                }
                break;

            case R.id.btn_option:
                if (wallEntity != null) {
                    initItemMenu(v);
                }
                break;

            case R.id.switch_text_show:
                switchContentShow(!needFull);
                break;
        }
    }


    public boolean needOpenWeb(WallEntity wallEntity) {
        if (WallEntity.CONTENT_TYPE_ADS.equals(wallEntity.getContent_type())) {
            if (!TextUtils.isEmpty(wallEntity.getVideo_filename())) { // 视频不为空
                new HttpTools(context).get(wallEntity.getTrack_url() + MainActivity.getUser().getUser_id(), null, null, null);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void showPreviewVideo() {
        // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
        Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
        // 传的值对应视频的content_creator_id
        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, wallEntity.getContent_creator_id());
        // 传的值对应video_filename
        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, wallEntity.getVideo_filename());
        fragment.startActivity(intent);
    }

    private void showOriginPic() {
        Intent intent = new Intent(context, WallViewPicActivity.class);
        Map<String, String> condition = new HashMap<>();
        condition.put(Constant.CONTENT_ID, wallEntity.getContent_id());
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CONDITION, UrlUtil.mapToJsonstring(condition));
        String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
        intent.putExtra(Constant.REQUEST_URL, url);
        intent.putExtra(Constant.USER_ID, wallEntity.getUser_id());
        intent.putExtra(Constant.POSITION, position);
        fragment.startActivityForResult(intent, Constant.INTENT_REQUEST_UPDATE_PHOTOS);
    }

    /**
     * 更新点赞相关视图
     */
    private void updateLovedView() {
        int count = Integer.valueOf(wallEntity.getLove_count());
        int resId;

        if (TextUtils.isEmpty(wallEntity.getLove_id())) {
            count += 1;
            resId = R.drawable.goodjob_clicked;
            wallEntity.setLove_id(MainActivity.getUser().getUser_id());
        } else {
            count -= 1;
            resId = R.drawable.goodjob_nonclicked;
            wallEntity.setLove_id(null);
        }

        wallEntity.setLove_count(String.valueOf(count));
        ibAgree.setImageResource(resId);
        tvAgreeCount.setText(String.format(tvAgreeCount.getContext().getString(R.string.loves_count), count));
    }

    /**
     * 点赞
     *
     * @param wallEntity
     * @param love
     */
    private void clickLove(WallEntity wallEntity, boolean love) {
        HashMap<String, String> params = new HashMap<>();
        params.put("content_id", wallEntity.getContent_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());
        RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE, params);
        callBack.setLinkType(CallBack.LINK_TYPE_POST_LOVE);
        mHttpTools.post(requestInfo, POST_LOVE, callBack);

    }

    /**
     * Diary内容显示
     *
     * @param wallEntity
     * @param position
     * @param context
     */
    public void setContent(WallEntity wallEntity, int position, final Context context) {

        this.wallEntity = wallEntity;
        this.position = position;

        VolleyUtil.initNetworkImageView(context, nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wallEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);

        String atDescription = wallEntity.getText_description();
        if (TextUtils.isEmpty(atDescription)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(atDescription);
        }
        LogUtil.i(TAG, "onBindViewHolder& description: " + atDescription);

        setSpanContent(context, atDescription);

        // 显示发表的时间
        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(context, wallEntity.getContent_creation_date()));
        //wing modified begin 2015.08.13
        int publicType = 0;
        if (!TextUtils.isEmpty(wallEntity.getContent_group_public())) {
            publicType = Integer.valueOf(wallEntity.getContent_group_public());
        }
        //wing modified end 2015.08.13// 0- Only Me, 1- All Members , 2-Organisation
        if (publicType == 0) {
            ivLock.setImageResource(R.drawable.lock_post);
            ivLock.setVisibility(View.VISIBLE);
        } else if (publicType == 2) {
            ivLock.setImageResource(R.drawable.workspace_management_icon);
            ivLock.setVisibility(View.VISIBLE);
        } else {
            ivLock.setImageResource(R.drawable.privacy_open);
            ivLock.setVisibility(View.GONE);
        }
        // 用户名
        tvUserName.setText(this.wallEntity.getUser_given_name());
        if (PrivateMessageEntity.STATUS_DE_ACTIVE.equalsIgnoreCase(this.wallEntity.getStatus())) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.user_left_minilcon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvUserName.setCompoundDrawables(drawable, null, null, null);
            tvUserName.setCompoundDrawablePadding(10);
            tvUserName.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            tvUserName.setCompoundDrawables(null, null, null, null);
        }

        // file_id 为空表示没有发表图片，有则需要显示图片
        if (TextUtils.isEmpty(this.wallEntity.getFile_id()) && TextUtils.isEmpty(wallEntity.getVideo_thumbnail())) {
            llWallsImage.setVisibility(View.GONE);
        } else {
            llWallsImage.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(this.wallEntity.getVideo_thumbnail())) { // 有视频图片说这条Wall上传的是视频并有图片，显示视频图片
                String url = String.format(Constant.API_GET_VIDEO_THUMBNAIL, wallEntity.getContent_creator_id(), wallEntity.getVideo_thumbnail());
                LogUtil.i(TAG, "setContent& video_thumbnail: " + url);
                videoImage.setVisibility(View.VISIBLE);
                VolleyUtil.initNetworkImageView(context, imWallsImages, url, R.drawable.network_image_default, R.drawable.network_image_default);

                String duration = MyDateUtils.formatDuration(wallEntity.getVideo_duration());
                if (TextUtils.isEmpty(duration)) {
                    tvPhotoCount.setVisibility(View.GONE);
                } else {
                    tvPhotoCount.setVisibility(View.VISIBLE);
                    tvPhotoCount.setText(duration);
                }
            } else {
                // 有图片显示图片总数
                int count = Integer.valueOf(wallEntity.getPhoto_count());
                if (count > 1) {
                    String photoCountStr;
                    photoCountStr = count + " " + context.getString(R.string.text_photos);
                    tvPhotoCount.setText(photoCountStr);
                    tvPhotoCount.setVisibility(View.VISIBLE);
                } else {
                    tvPhotoCount.setVisibility(View.GONE);
                }
                videoImage.setVisibility(View.GONE);
                VolleyUtil.initNetworkImageView(context, imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, wallEntity.getUser_id(), wallEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }
        }

        if (isDetailed) {
            // 在详情界面不需要在Holder显更多功能按钮视图
            btnOption.setVisibility(View.GONE);
            if (TextUtils.isEmpty(wallEntity.getVideo_filename())) {
                llWallsImage.setVisibility(View.GONE);
            }
        } else if ((!MainActivity.getUser().getUser_id().equals(this.wallEntity.getUser_id()) && (Integer.valueOf(wallEntity.getPhoto_count()) <= 0 && TextUtils.isEmpty(wallEntity.getVideo_filename())))) {
            // 不是当前用户：没有图片也没有视频都不需要显更多功能按钮
            btnOption.setVisibility(View.GONE);
        } else {
            btnOption.setVisibility(View.VISIBLE);
        }

        String feelCode = wallEntity.getDofeel_code();
        LogUtil.i(TAG, "setContent& feelCode: " + feelCode);
        try {
            if (!TextUtils.isEmpty(feelCode) && !feelCode.toLowerCase().equals("null")) {
                iv_mood.setVisibility(View.VISIBLE);
                StringBuilder b = new StringBuilder(feelCode);
                int charIndex = this.wallEntity.getDofeel_code().lastIndexOf("_");
                if (charIndex > 0 && b.length() > charIndex) {
                    b.replace(charIndex, charIndex + 1, "/");
                    String s = String.valueOf(b.charAt(charIndex + 1));
                    // 为了兼容以前版本把心情名称的首字母都转为大写与当前心情包的文件相匹配
                    b.replace(charIndex + 1, charIndex + 2, s.toUpperCase());
                }

                LogUtil.d(TAG, "setContent& mood: " + b.toString());

                if (b.toString().endsWith("png")) {
                    InputStream is = context.getAssets().open(b.toString());
                    iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
                } else {
                    iv_mood.setVisibility(View.GONE);
                }
            } else {
                iv_mood.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvAgreeCount.setText(String.format(tvAgreeCount.getContext().getString(R.string.loves_count), Integer.valueOf(this.wallEntity.getLove_count())));

        if (WallEntity.CONTENT_TYPE_ADS.equals(wallEntity.getContent_type())) {
            llComment.setVisibility(View.INVISIBLE);
        } else {
            llComment.setVisibility(View.VISIBLE);
            tvCommentCount.setText(this.wallEntity.getComment_count());
        }

        if (TextUtils.isEmpty(this.wallEntity.getLove_id())) {
            ibAgree.setImageResource(R.drawable.goodjob_nonclicked);
        } else {
            ibAgree.setImageResource(R.drawable.goodjob_clicked);
        }

        String locationName = this.wallEntity.getLoc_name();
        if (TextUtils.isEmpty(locationName) || TextUtils.isEmpty(this.wallEntity.getLoc_latitude()) || TextUtils.isEmpty(this.wallEntity.getLoc_longitude())) {
            llLocation.setVisibility(View.GONE);
        } else {
            llLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(locationName);
            llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(context, WallHolder.this.wallEntity);
                }
            });
            tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(context, WallHolder.this.wallEntity);
                }
            });
            ivLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(context, WallHolder.this.wallEntity);
                }
            });
        }
    }

    /**
     * 设置各字段的点击效果
     *
     * @param context
     * @param atDescription
     */
    private void setSpanContent(Context context, String atDescription) {
        int tagMemberCount = this.wallEntity.getTag_member() == null ? 0 : wallEntity.getTag_member().size();
        int tagGroupCount = this.wallEntity.getTag_member() == null ? 0 : wallEntity.getTag_group().size();
        if (tagMemberCount >= 0 || tagGroupCount >= 0) {
            // 有TAG用户或分组需要显示字符特效
            WallUtil util = new WallUtil(context, mViewClickListener);
            util.setSpanContent(tvContent, this.wallEntity, atDescription, tagMemberCount, tagGroupCount);
        } else {
            tvContent.setOnClickListener(this);
        }
    }

    /**
     * 切换日志内容的显示
     *
     * @param needFull
     */
    private void switchContentShow(boolean needFull) {
        this.needFull = needFull;
        if (globalLayoutListener == null) {
            // 监听
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    LogUtil.i(TAG, "onGlobalLayout&");
                    // 字符显示超过5行，只显示到第5行
                    int lineCount = tvContent.getLineCount();
                    if (lineCount > 5) {
                        // 第5行只显示十个字符
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(3);
                        String sourceText = tvContent.getText().toString();
                        String string = sourceText.substring(maxLineEndIndex);
                        if(string.length()>10){
                            sourceText = sourceText.replace(sourceText.substring(maxLineEndIndex+10),"...");
                        }
                        setSpanContent(context, sourceText);
                        tvSwitch.setVisibility(View.VISIBLE);
                        tvSwitch.setText(R.string.text_more);
                    }
                }
            };
        }


        if (!needFull) {
            // 不显示全部内容只显示5九行
            tvContent.setMaxLines(5);
            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        } else {
            tvContent.setMaxLines(Integer.MAX_VALUE);
            tvContent.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            tvSwitch.setText(R.string.text_collapse);
        }


        if (wallEntity != null) {
            setSpanContent(context, wallEntity.getText_description());
        }
    }

    /**
     * 跳至地图
     *
     * @param context 资源
     * @param wall    {@link WallEntity}
     */
    private void gotoLocationSetting(Context context, WallEntity wall) {
        if (TextUtils.isEmpty(wall.getLoc_latitude()) || TextUtils.isEmpty(wall.getLoc_longitude())) {
            return;
        }

        LocationUtil.goNavigation(context, Double.valueOf(wall.getLoc_latitude()), Double.valueOf(wall.getLoc_longitude()), wall.getLoc_type());
    }


    MyDialog myDialog;

    /**
     * 选择功能：保存图片；添加图片；编辑日记；删除日志
     *
     * @param v 需要弹出选择视图的{@link View};
     * @see PopupMenu
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initItemMenu(View v) {
        if (wallEntity == null) {
            // wallEntity 为null说明数据还没有加载成功
            return;
        }

        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.wall_item_menu);


        if (!MainActivity.getUser().getUser_id().equals(this.wallEntity.getUser_id())) { // 不是当前用户的日志不能显示：添加图片；编辑日记；删除日志等功能
            popupMenu.getMenu().findItem(R.id.menu_item_add_photo).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_edit_this_post).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_delete_this_post).setVisible(false);
        }

        String photoCount = wallEntity.getPhoto_count();

        if (TextUtils.isEmpty(photoCount) || Integer.valueOf(photoCount) <= 0) { // 没有图片不需要显示:保存图片功能
            if (TextUtils.isEmpty(wallEntity.getVideo_filename())) {
                popupMenu.getMenu().findItem(R.id.menu_save_all_medias).setVisible(false);
            } else {
                popupMenu.getMenu().findItem(R.id.menu_save_all_medias).setTitle(context.getString(R.string.save_all_video));
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mViewClickListener.showPopClick(WallHolder.this);
                LogUtil.d(TAG, "onMenuItemClick& id: " + menuItem.getItemId());
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_add_photo:
                        if (!TextUtils.isEmpty(wallEntity.getVideo_filename())) {
                            LogUtil.i(TAG, "onMenuItemClick& need Alert");
                            // 已经选择了视频需要弹出提示
                            myDialog = new MyDialog(context, "", context.getString(R.string.add_photo_before_title));
                            myDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.dismiss();
                                    editDiaryAction(null);
                                }
                            });

                            // 不选择视频
                            myDialog.setButtonCancel(R.string.text_dialog_no, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.dismiss();
                                }
                            });
                            myDialog.show();
                        } else {
                            LogUtil.d(TAG, "onMenuItemClick& openPhoto");
                            openPhotos();
                        }
                        break;
                    case R.id.menu_save_all_medias:
                        LogUtil.d(TAG, "onMenuItemClick&  save all media");
                        String photoCount = wallEntity.getPhoto_count();
                        if (!TextUtils.isEmpty(photoCount) && Integer.valueOf(photoCount) > 0) {
                            savePhotos();
                        } else if (!TextUtils.isEmpty(wallEntity.getVideo_filename())) {
                            saveVideo();
                        }
                        break;
                    case R.id.menu_edit_this_post:
                        editDiaryAction(null);
                        break;
                    case R.id.menu_delete_this_post:
                        mViewClickListener.remove(wallEntity);
                        break;
                }
                return true;
            }
        });

        //使用反射，强制显示菜单图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
//
        popupMenu.show();

//        PopupWindow popupMenu = new PopupWindow(getActivity());
//        popupMenu.setAnimationStyle(R.style.PopupAnimation);
//
//        popupMenu = new PopupWindow(getActivity().getLayoutInflater().inflate(R.layout.wall_item_menu,null),
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        popupMenu.setOutsideTouchable(true);
//        popupMenu.showAsDropDown(v,0,0);
    }

    private void editDiaryAction(Intent intent) {
        if (intent == null) {
            intent = new Intent(context, NewDiaryActivity.class);
        }

        intent.putExtra(Constant.WALL_ENTITY, wallEntity);
        intent.putExtra(Constant.CONTENT_GROUP_ID, wallEntity.getContent_group_id());
        intent.putExtra(Constant.GROUP_ID, wallEntity.getGroup_id());
        intent.putExtra(Constant.POSITION, position);
        fragment.startActivityForResult(intent, Constant.INTENT_REQUEST_UPDATE_WALL);
    }

    /**
     * 保存选择的日志上伟平的所有图片
     */
    private void savePhotos() {

        int photoCount = Integer.valueOf(wallEntity.getPhoto_count());
        LogUtil.d(TAG, "GET_WALL_SUCCEED photoCount = " + photoCount);
        if (photoCount > 0) {
            /**wing add*/
            mViewClickListener.onSave();
            /**wing add*/
            Map<String, String> condition = new HashMap<>();
            condition.put("content_id", wallEntity.getContent_id());
            Map<String, String> params = new HashMap<>();
            params.put("condition", UrlUtil.mapToJsonstring(condition));
            String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
            callBack.setLinkType(CallBack.LINK_TYPE_SAVE_PHOTOS);
            new HttpTools(context).get(url, null, SAVE_PHOTO, callBack);
        } else {
            MessageUtil.getInstance().showShortToast( R.string.no_photo_2_save);
            LogUtil.e(TAG, "save Photo Fail");
        }
//        String.format(Constant.API_GET_PIC, Constant.Module_Original, userId, photoEntity.getFile_id());
    }


    private void saveVideo() {
        mViewClickListener.onSave();
        String createdContentId = wallEntity.getContent_creator_id();
        String fileName = wallEntity.getVideo_filename();
        String url;
        url = String.format(Constant.API_GET_VIDEO, createdContentId, fileName);

        /*******为适应news、rewards中的视频下载，添加如下代码 **********/
        if (createdContentId.equals("for_news_or_rewards")) {
            url = fileName;
            fileName = url.substring(url.lastIndexOf('/') + 1);
            LogUtil.d(TAG, "fileName" + fileName);
        }

        String targetParent = PreviewVideoActivity.VIDEO_PATH;
        File cacheFile = new File(targetParent);
        boolean canWrite = cacheFile.exists() || cacheFile.mkdir();
        String saveVideoPath;
        saveVideoPath = canWrite ? targetParent + fileName : FileUtil.getCacheFilePath(context.getApplicationContext(), false) + String.format("/%s", fileName);

        callBack.setLinkType(CallBack.LINK_TYPE_SAVE_VIDEO);
        new HttpTools(context).download(context, url, saveVideoPath, true, callBack);
    }

    /**
     * 打开相册，选择图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPhotos() {
        LogUtil.i(TAG, "openPhoto&");
        Intent intent = new Intent(context, SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        /**
         * 使用了Universal Image Loader库来处理图片需要返回的Uri与传统有差异，传此值用于区分
         */
        intent.putExtra(MediaData.EXTRA_USE_UNIVERSAL, true);
        intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, false);
//        intent.putParcelableArrayListExtra(SelectPhotosActivity.EXTRA_SELECTED_PHOTOS, (ArrayList<? extends Parcelable>) imageUris);
        int residue = SelectPhotosActivity.MAX_SELECT;
        String photoCountStr = wallEntity.getPhoto_count();
        if (!TextUtils.isEmpty(photoCountStr)) {
            residue = SelectPhotosActivity.MAX_SELECT - Integer.valueOf(photoCountStr);
        }
        intent.putExtra(SelectPhotosActivity.EXTRA_RESIDUE, residue);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        fragment.startActivityForResult(intent, Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO);
    }

    public void setLocalPhotos(@NonNull ArrayList<Uri> localPhotos) {
        this.localPhotos = localPhotos;
        if (!localPhotos.isEmpty()) {
            Intent intent = new Intent(context, NewDiaryActivity.class);
            intent.putParcelableArrayListExtra(SelectPhotosActivity.EXTRA_IMAGES_STR, localPhotos);
            editDiaryAction(intent);
//            int max;
//            String maxPhoto = wallEntity.getPhoto_max();
//            if (TextUtils.isEmpty(maxPhoto)) {
//                max = localPhotos.size() - 1;
//            } else {
//                max = Integer.valueOf(maxPhoto) + localPhotos.size() - 1;
//            }
//            Map<String, String> params = new HashMap<>();
//            params.put(Constant.PARAM_PHOTO_MAX, String.valueOf(max));
//            RequestInfo requestInfo = new RequestInfo();
//            requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
//            requestInfo.url = String.format(Constant.API_PUT_PHOTO_MAX, wallEntity.getContent_id());
//            callBack.setLinkType(CallBack.LINK_TYPE_PUT_PHOTO_MAX);
//            mHttpTools.put(requestInfo, PUT_PHOTO_MAX, callBack);
        }
    }

    private void submitLocalPhotos(String contentId) {
        if (localPhotos.isEmpty()) {
            mHandler.sendEmptyMessage(ACTION_POST_PHOTOS_SUCCEED);
        } else {
            int count = localPhotos.size();
            boolean multiple = (count <= 0);
            tasks = new ArrayList<>();
            for (int index = 0; index < count; index++) {
                Uri photoEntity = localPhotos.get(index);
                if (index == count - 1) {
                    CompressBitmapTask task = new CompressBitmapTask(contentId, index, multiple, true);
                    tasks.add(task);
                    //for not work in down 11
                    if (SDKUtil.IS_HONEYCOMB) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoEntity);
                    } else {
                        task.execute(photoEntity);
                    }
                } else {
                    CompressBitmapTask task = new CompressBitmapTask(contentId, index, multiple, false);
                    tasks.add(task);
                    //for not work in down 11
                    if (SDKUtil.IS_HONEYCOMB) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoEntity);
                    } else {
                        task.execute(photoEntity);
                    }
                }
            }
        }
    }

    class CompressBitmapTask extends AsyncTask<Uri, Void, String> {

        String contentId;
        int index;
        boolean multiple;
        boolean lastPic;

        public CompressBitmapTask(String contentId, int index, boolean multiple, final boolean lastPic) {
            this.contentId = contentId;
            this.index = index;
            this.multiple = multiple;
            this.lastPic = lastPic;
        }

        @Override
        protected String doInBackground(Uri... params) {
            if (params == null) {
                return null;
            }
            return LocalImageLoader.compressBitmap(App.getContextInstance(), params[0], 480, 800, false);
        }

        @Override
        protected void onPostExecute(String path) {
            try {
                submitPic(path, contentId, index, multiple);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(ACTION_POST_PHOTOS_FAIL);
            }
            tasks.remove(this);
        }
    }

    /**
     * 上传图片
     *
     * @param path      - 图片路径
     * @param contentId
     * @param index
     * @param multiple
     */
    private void submitPic(String path, String contentId, int index, boolean multiple) throws FileNotFoundException {
        File f = new File(path);
        if (!f.exists()) {
            throw new FileNotFoundException("path: " + path + "; not found");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("content_id", contentId);
        int photoMax;
        String photoMaxStr = wallEntity.getPhoto_max();
        if (TextUtils.isEmpty(photoMaxStr)) {
            photoMax = 1;
        } else {
            photoMax = Integer.valueOf(photoMaxStr) + 1;
        }
        params.put("photo_index", String.valueOf(index + photoMax));
        params.put("photo_caption", "");
        params.put("file", f);
        params.put("multiple", multiple ? "1" : "0");
        LogUtil.d(TAG, "submitPic: params: " + params);
        callBack.setLinkType(CallBack.LINK_TYPE_SUBMIT_PICTURE);
        mHttpTools.upload(Constant.API_WALL_PIC_POST, params, UPLOAD_PIC, callBack);

    }

    class CallBack implements HttpCallback {
        public static final int LINK_TYPE_SUBMIT_PICTURE = 2;
        public static final int LINK_TYPE_POST_LOVE = 3;
        public static final int LINK_TYPE_SAVE_PHOTOS = 4;
        public static final int LINK_TYPE_SAVE_VIDEO = 5;
        public static final int LINK_TYPE_PUT_PHOTO_MAX = 6;

        /**
         * 当前回调标识，用于识别当前同调用类别
         */
        private int linkType = LINK_TYPE_SUBMIT_PICTURE;

        public void setLinkType(int linkType) {
            this.linkType = linkType;
        }

        @Override
        public void onStart() {
            switch (linkType) {
                case LINK_TYPE_POST_LOVE:
                    if (fragment instanceof DiaryInformationFragment) {
                        ((DiaryInformationFragment) fragment).setProgressVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onResult(String response) {
            LogUtil.i(TAG, "onResult# type: " + linkType + " response: " + response);
            switch (linkType) {
                case LINK_TYPE_SUBMIT_PICTURE:
                    lastPic++;
                    if (lastPic == localPhotos.size()) {
                        mHandler.sendEmptyMessage(ACTION_POST_PHOTOS_SUCCEED);
                    }
                    break;
                case LINK_TYPE_SAVE_PHOTOS:

                    LogUtil.i(TAG, "onResult& response: " + response);
                    try {
                        GsonBuilder gsonb = new GsonBuilder();
                        //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                        //DateDeserializer ds = new DateDeserializer();
                        //给GsonBuilder方法单独指定Date类型的反序列化方法
                        //gsonb.registerTypeAdapter(Date.class, ds);
                        Gson gson = gsonb.create();
                        if (response.startsWith("{\"data\":")) {
                            JSONObject jsonObject = new JSONObject(response);
                            String dataString = jsonObject.optString("data");
                            data = gson.fromJson(dataString, new TypeToken<ArrayList<PhotoEntity>>() {
                            }.getType());
                        } else {
                            data = gson.fromJson(response, new TypeToken<ArrayList<PhotoEntity>>() {
                            }.getType());
                        }
                        downloadPhoto(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case LINK_TYPE_PUT_PHOTO_MAX:
                    submitLocalPhotos(wallEntity.getContent_id());
                    break;
                case LINK_TYPE_POST_LOVE:
                    LogUtil.d(TAG, "onResult& LINK_TYPE_POST_LOVE");
                    if (fragment instanceof DiaryInformationFragment) {
                        ((DiaryInformationFragment) fragment).setResultOK(false);
                        ((DiaryInformationFragment) fragment).setProgressVisibility(View.GONE);
                    }
                    break;
                case LINK_TYPE_SAVE_VIDEO:
                    if (!TextUtils.isEmpty(response) && response.contains("/data/data/")) {
                        try {

                            File saveFile = FileUtil.saveVideoFile(context, true);
                            Files.copy(new File(response), saveFile);
                            mViewClickListener.saved(wallEntity, true);

                            // 通知媒体库更新文件
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(saveFile));
                            context.sendBroadcast(intent);

                            MessageUtil.getInstance().showShortToast( context.getString(R.string.saved_to_path) + saveFile.getPath());
                        } catch (IOException e) {
                            mViewClickListener.saved(wallEntity, false);
                            e.printStackTrace();
                        }
                    } else {
                        mViewClickListener.saved(wallEntity, true);

                        // 通知媒体库更新文件
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(new File(response)));
                        context.sendBroadcast(intent);

                        MessageUtil.getInstance().showShortToast( context.getString(R.string.saved_to_path) + response);
                    }
                    break;

                default:
                    break;

            }
        }

        @Override
        public void onError(Exception e) {
            switch (linkType) {
                case LINK_TYPE_PUT_PHOTO_MAX:
                case LINK_TYPE_SUBMIT_PICTURE:
                    mHandler.sendEmptyMessage(ACTION_POST_PHOTOS_FAIL);
                    break;
                case LINK_TYPE_POST_LOVE:
                    if (fragment instanceof DiaryInformationFragment) {
                        ((DiaryInformationFragment) fragment).setProgressVisibility(View.GONE);
                    }
                    break;
                case LINK_TYPE_SAVE_VIDEO:
                    mViewClickListener.saved(wallEntity, false);
                    break;
                default:
                    break;
            }
            e.printStackTrace();
        }

        @Override
        public void onCancelled() {

        }

        @Override
        public void onLoading(long count, long current) {

        }
    }

    int downloadCount = 0;

    private void downloadPhoto(ArrayList<PhotoEntity> photoEntities) {
        for (PhotoEntity photoEntity : photoEntities) {
            /**wing modified*/
//            String picUrl = String.format(Constant.API_GET_PIC, Constant.Module_Original, MainActivity.getUser().getUser_id(), photoEntity.getFile_id());
            String picUrl = String.format(Constant.API_GET_PIC, Constant.Module_Original, photoEntity.getUser_id(), photoEntity.getFile_id());
            /**wing modified*/
            mHttpTools.download(App.getContextInstance(), picUrl, PicturesCacheUtil.getCachePicPath(context, false), true, new HttpCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    downloadCount++;
                    LogUtil.d(TAG, "debug onFinish& downloadCount = " + downloadCount + "; size = " + data.size());
                    if (downloadCount == data.size()) {
                        downloadCount = 0;
                        mViewClickListener.saved(wallEntity, true);
                    }
                }

                @Override
                public void onResult(String string) {
                    LogUtil.d(TAG, "debug onResult& response: " + string);
                    /**wing modified*/
                    String path;
                    try {
                        path = PicturesCacheUtil.saveImageToGallery(context, new File(string), "wall");
                        LogUtil.d(TAG, "download photo to path: " + path);
                        MessageUtil.getInstance().showShortToast( R.string.photo_saved);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /**wing modified*/
                }

                @Override
                public void onError(Exception e) {
                    mViewClickListener.saved(wallEntity, false);
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
}
