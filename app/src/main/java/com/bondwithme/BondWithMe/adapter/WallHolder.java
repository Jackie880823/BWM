package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.ViewOriginalPicesActivity;
import com.bondwithme.BondWithMe.ui.share.PreviewVideoActivity;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;

import java.io.InputStream;
import java.util.HashMap;
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

    private static final String POST_LOVE = TAG + "_POST_LOVE";

    private WallViewClickListener mViewClickListener;
    private WallEntity wallEntity;
    private HttpTools mHttpTools;
    private Context context;

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
    private View llWallsImage;

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
    private ImageButton ibAgree;

    /**
     * 删除按钮，点击删除日志
     */
    private ImageButton btn_del;

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
     * @param context    用于引导应用资源
     */
    public WallHolder(final Context context, View itemView, HttpTools httpTools, boolean isDetailed) {
        // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
        super(itemView);
        mHttpTools = httpTools;
        this.context = context;

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
        ibAgree = (ImageButton) itemView.findViewById(R.id.iv_love);
        btn_del = (ImageButton) itemView.findViewById(R.id.btn_del);
        iv_mood = (ImageView) itemView.findViewById(R.id.iv_mood);
        llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
        ivLocation = (ImageView) itemView.findViewById(R.id.iv_location);
        tvLocation = (TextView) itemView.findViewById(R.id.tv_location);

        if (!isDetailed) { // 是列表要做文字内容切换
            switchContentShow(needFull);
        }

        itemView.findViewById(R.id.top_event).setOnClickListener(this);
        llComment = itemView.findViewById(R.id.ll_comment);
        llComment.setOnClickListener(this);
        tvAgreeCount.setOnClickListener(this);
        //        tvLoveList.setOnClickListener(this);
        ibAgree.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        imWallsImages.setOnClickListener(this);
        tvSwitch.setOnClickListener(this);
    }

    /**
     * 设置收起开头的显示/隐藏
     * @see View#setVisibility(int)
     * @param visiblity
     */
    public void setSwitchVisibility(int visiblity) {
        tvSwitch.setVisibility(visiblity);
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_wall_agree_count:
            case R.id.tv_love_list:
                if (Integer.valueOf(wallEntity.getLove_count()) > 0) {
                    if (mViewClickListener != null) {
                        mViewClickListener.showLovedMember(MainActivity.getUser().getUser_id(), wallEntity.getContent_id(), WallUtil.LOVE_MEMBER_WALL_TYPE);
                    }
                }
                break;

            case R.id.ll_love:
            case R.id.iv_love:
                updateLovedView();

                if (TextUtils.isEmpty(wallEntity.getLove_id())) {
                    doLove(wallEntity, false);
                } else {
                    doLove(wallEntity, true);
                }

                //判断是否已经有进行中的判断
                //                if(!runningList.contains(position)) {
                //                    runningList.add(position);
                //                    check(position);
                //                }
                break;
            case R.id.tv_wall_content:
            case R.id.ll_comment:
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
                        mViewClickListener.showComments(wallEntity);
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

            case R.id.btn_del:
                if (mViewClickListener != null) {
                    mViewClickListener.remove(v,wallEntity.getContent_group_id());
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
        context.startActivity(intent);
    }

    private void showOriginPic() {
        Intent intent = new Intent(context, ViewOriginalPicesActivity.class);
        Map<String, String> condition = new HashMap<>();
        condition.put("content_id", wallEntity.getContent_id());
        Map<String, String> params = new HashMap<>();
        params.put("condition", UrlUtil.mapToJsonstring(condition));
        String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
        intent.putExtra("request_url", url);
        context.startActivity(intent);
    }

    /**
     * 更新点赞相关视图
     */
    private void updateLovedView() {
        int count = Integer.valueOf(wallEntity.getLove_count());
        //        String text = tvLoveList.getText().toString();
        //        String name = MainActivity.getUser().getUser_given_name();
        int resId;

        if (TextUtils.isEmpty(wallEntity.getLove_id())) {
            count += 1;
            resId = R.drawable.love_press;
            wallEntity.setLove_id(MainActivity.getUser().getUser_id());

            //            if(count > 1) {
            //                text += (name + " ");
            //            } else {
            //                text = name;
            //            }
        } else {
            count -= 1;
            resId = R.drawable.love_normal;
            wallEntity.setLove_id(null);

            //            if(count > 0) {
            //                StringBuilder temp = new StringBuilder();
            //                String split;
            //                split = name + " ";
            //
            //                for(String str : text.split(split)) {
            //                    temp.append(str);
            //                }
            //                text = temp.toString();
            //            } else {
            //                text = "";
            //            }
        }

        wallEntity.setLove_count(String.valueOf(count));
        ibAgree.setImageResource(resId);
        tvAgreeCount.setText(String.format(tvAgreeCount.getContext().getString(R.string.loves_count), count));
        //        tvLoveList.setText(text);
    }

    private void doLove(final WallEntity wallEntity, final boolean love) {

        HashMap<String, String> params = new HashMap<>();
        params.put("content_id", wallEntity.getContent_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());

        RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE, params);

        mHttpTools.post(requestInfo, POST_LOVE, new HttpCallback() {
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

    public void setContent(WallEntity wallEntity, final Context context) {

        this.wallEntity = wallEntity;

        VolleyUtil.initNetworkImageView(context, nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wallEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

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
        //wing modified end 2015.08.13
        if (publicType == 0) {
            ivLock.setVisibility(View.VISIBLE);
        } else {
            ivLock.setVisibility(View.GONE);
        }
        // 用户名
        tvUserName.setText(this.wallEntity.getUser_given_name());

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
         /*is owner wall*/
        //        if (!TextUtils.isEmpty(wall.getUser_id())&&wall.getUser_id().equals("49")) {
        //            ibDelete.setVisibility(View.VISIBLE);
        //        } else {
        //            ibDelete.setVisibility(View.GONE);
        //        }

        try {
            if (this.wallEntity.getDofeel_code() != null) {
                StringBuilder b = new StringBuilder(this.wallEntity.getDofeel_code());
                int charIndex = this.wallEntity.getDofeel_code().lastIndexOf("_");
                b.replace(charIndex, charIndex + 1, "/");

                InputStream is = context.getAssets().open(b.toString());
                iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
            } else {
                iv_mood.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            iv_mood.setVisibility(View.GONE);
        }

        /*location*/
        //        if (TextUtils.isEmpty(wall.getLoc_name())) {
        //            at.setVisibility(View.GONE);
        //        } else {
        //            at.setVisibility(View.VISIBLE);
        //            tvLocation.setText(wall.getLoc_name());
        //        }
        //        int count = Integer.valueOf(this.wallEntity.getLove_count());
        tvAgreeCount.setText(String.format(tvAgreeCount.getContext().getString(R.string.loves_count), Integer.valueOf(this.wallEntity.getLove_count())));
        //        if(count > 0) {
        //            WallUtil.getLoveList(mHttpTools, tvLoveList, MainActivity.getUser().getUser_id(), wallEntity.getContent_id(), WallUtil.LOVE_MEMBER_WALL_TYPE);
        //        } else {
        //            tvLoveList.setText("");
        //        }

        if (WallEntity.CONTENT_TYPE_ADS.equals(wallEntity.getContent_type())) {
            llComment.setVisibility(View.INVISIBLE);
        } else {
            llComment.setVisibility(View.VISIBLE);
            tvCommentCount.setText(this.wallEntity.getComment_count());
        }


        if (MainActivity.getUser().getUser_id().equals(this.wallEntity.getUser_id())) {
            btn_del.setVisibility(View.VISIBLE);
        } else {
            btn_del.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(this.wallEntity.getLove_id())) {
            ibAgree.setImageResource(R.drawable.love_normal);
        } else {
            ibAgree.setImageResource(R.drawable.love_press);
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
                    // 字符显示超过5行，只显示到第九行
                    int lineCount = tvContent.getLineCount();
                    if (lineCount > 5) {
                        // 第九行只显示十个字符
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(4);
                        CharSequence sourceText = tvContent.getText();
                        SpannableStringBuilder ssb = new SpannableStringBuilder(sourceText);
                        ssb.replace(maxLineEndIndex - 3, ssb.length() - 1, "...");
                        setSpanContent(context, ssb.toString());
                        tvSwitch.setVisibility(View.VISIBLE);
                        tvSwitch.setText(R.string.more);
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
}
