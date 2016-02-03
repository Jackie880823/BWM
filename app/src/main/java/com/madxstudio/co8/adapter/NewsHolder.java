package com.madxstudio.co8.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.NewsEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.WriteNewsActivity;
import com.madxstudio.co8.ui.share.PreviewVideoActivity;
import com.madxstudio.co8.ui.wall.DiaryCommentActivity;
import com.madxstudio.co8.ui.wall.DiaryInformationFragment;
import com.madxstudio.co8.ui.wall.WallViewPicActivity;
import com.madxstudio.co8.util.LocationUtil;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MyDateUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于显示news item，便于动态显示文本内容：展开（More）、收起（collapse），兼有播放视频的功能
 * Created by heweidong on 15/10/19.
 */
public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final String TAG = "NewsHolder";
    public static final String PIC_URL = "pic_url";
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private NewsEntity newsEntity;
    private Context mContext;
    private int position = -1;

    /**
     * 新闻类型
     */
    private TextView tvCategoryName;
    /**
     * 标题
     */
    private TextView tvTitle;
    /**
     * 时间
     */
    private TextView tvDate;
    /**
     * 用户
     */
    private TextView tvUser;
    /**
     * 内容
     */
    private TextView tvContent;

    private TextView tvMoreOrCollapse;
    /**
     * 网络图片
     */
    private NetworkImageView ivPic;
    /**
     * 图片总数
     */
    private TextView tvPhotoCount;
    /**
     * 视频图标
     */
    private ImageView ibtnVideo;
    private View new_comment_linear;
    private View new_good_job_linear;
    private ImageButton btnOption;
    /**
     * 网络图片父亲视图
     */
    public View llNewsImage;
    /**
     * 评论数
     */
    private TextView newsCommentMember;
    /**
     * 点赞数
     */
    private TextView newsGoodMember;
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
    /**
     * 点赞
     */
    private ImageButton ibAgree;
    private static final String accountUserId = MainActivity.getUser().getUser_id();
    private BaseFragment fragment;
    private CallBack callBack = new CallBack();
    private HttpTools mHttpTools;

    private String imageUrl;
    private String videoUrl;

    private boolean isDisplayMore;
    private int defaultLineCount = 5;

    public NewsHolder(BaseFragment fragment,View itemView, Context mContext) {
        super(itemView);
        this.mContext = mContext;
        this.fragment = fragment;

        mHttpTools = new HttpTools(mContext);
        tvCategoryName = (TextView) itemView.findViewById(R.id.news_category_name);
        tvTitle = (TextView) itemView.findViewById(R.id.news_title);
        tvDate = (TextView) itemView.findViewById(R.id.news_date);
        tvUser = (TextView) itemView.findViewById(R.id.news_user);

        llNewsImage = itemView.findViewById(R.id.ll_news_image);
        ivPic = (NetworkImageView) itemView.findViewById(R.id.iv_pic);
        ibtnVideo = (ImageView) itemView.findViewById(R.id.iv_video_top);
        tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_wall_photo_count);

        ibAgree = (ImageButton) itemView.findViewById(R.id.iv_good);
        tvContent = (TextView) itemView.findViewById(R.id.news_content);
        tvMoreOrCollapse = (TextView) itemView.findViewById(R.id.tv_more_or_collapse);
        new_comment_linear = itemView.findViewById(R.id.new_comment_linear);
        new_good_job_linear = itemView.findViewById(R.id.new_comment_linear);
        btnOption = (ImageButton) itemView.findViewById(R.id.btn_option);

        newsGoodMember = (TextView) itemView.findViewById(R.id.new_good_job);
        newsCommentMember = (TextView) itemView.findViewById(R.id.new_comment);

        llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
        ivLocation = (ImageView) itemView.findViewById(R.id.iv_location);
        tvLocation = (TextView) itemView.findViewById(R.id.tv_location);

        changeTextDisplay(isDisplayMore);

        ivPic.setOnClickListener(this);
        ibtnVideo.setOnClickListener(this);
        tvMoreOrCollapse.setOnClickListener(this);
        new_comment_linear.setOnClickListener(this);
        new_good_job_linear.setOnClickListener(this);
        btnOption.setOnClickListener(this);
        ibAgree.setOnClickListener(this);
    }

    public void setNewsEntity(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;
    }


    //news content包括title，date，image，video_thumbnail，content_text
    public void setContent(NewsEntity newsEntity, final Context mContext, int position) {
        this.position = position;
        tvCategoryName.setText(newsEntity.getCategory_name());
        tvTitle.setText(newsEntity.getContent_title());
//        LogUtil.d(TAG, "date======" + newsEntity.getContent_creation_date());
//        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getRelease_date()));
        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getContent_creation_date()));
        tvUser.setText("Posted by: "+newsEntity.getUser_given_name());
        tvContent.setText(newsEntity.getText_description());
        if(Integer.valueOf(newsEntity.getComment_count()).intValue() > 0){
            newsCommentMember.setText(newsEntity.getComment_count());
        }
        if(Integer.valueOf(newsEntity.getLove_count()).intValue() > 0){
            newsGoodMember.setText(newsEntity.getLove_count());
        }

        if ((!accountUserId.equals(this.newsEntity.getUser_id()))) {
            // 不是当前用户：不显更多功能按钮
            btnOption.setVisibility(View.GONE);
        } else {
            btnOption.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(this.newsEntity.getLove_id())) {
            ibAgree.setImageResource(R.drawable.goodjob_nonclicked);
        } else {
            ibAgree.setImageResource(R.drawable.goodjob_clicked);
        }

        if(TextUtils.isEmpty(this.newsEntity.getFile_id()) && TextUtils.isEmpty(newsEntity.getVideo_thumbnail())){
            llNewsImage.setVisibility(View.GONE);
        }else {
            llNewsImage.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(this.newsEntity.getVideo_thumbnail())) { // 有视频图片说这条Wall上传的是视频并有图片，显示视频图片
                String url = String.format(Constant.API_GET_VIDEO_THUMBNAIL, newsEntity.getContent_creator_id(), newsEntity.getVideo_thumbnail());
//                LogUtil.i(TAG, "setContent& video_thumbnail: " + url);
                ibtnVideo.setVisibility(View.VISIBLE);
                VolleyUtil.initNetworkImageView(mContext, ivPic, url, R.drawable.network_image_default, R.drawable.network_image_default);

                String duration = MyDateUtils.formatDuration(newsEntity.getVideo_duration());
                if (TextUtils.isEmpty(duration)) {
                    tvPhotoCount.setVisibility(View.GONE);
                } else {
                    tvPhotoCount.setVisibility(View.VISIBLE);
                    tvPhotoCount.setText(duration);
                }
            }else {
                int count = Integer.valueOf(newsEntity.getPhoto_count());
                if (count > 1) {
                    String photoCountStr;
                    photoCountStr = count + " " + mContext.getString(R.string.text_photos);
                    tvPhotoCount.setText(photoCountStr);
                    tvPhotoCount.setVisibility(View.VISIBLE);
                } else {
                    tvPhotoCount.setVisibility(View.GONE);
                }
                ibtnVideo.setVisibility(View.GONE);
                VolleyUtil.initNetworkImageView(mContext, ivPic, String.format(Constant.API_GET_PIC, Constant.Module_preview, newsEntity.getUser_id(), newsEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }

        }

        String locationName = this.newsEntity.getLoc_name();
        if (TextUtils.isEmpty(locationName) || TextUtils.isEmpty(this.newsEntity.getLoc_latitude()) || TextUtils.isEmpty(this.newsEntity.getLoc_longitude())) {
            llLocation.setVisibility(View.GONE);
        } else {
            llLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(locationName);
            llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(mContext, NewsHolder.this.newsEntity);
                }
            });
            tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(mContext, NewsHolder.this.newsEntity);
                }
            });
            ivLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(mContext, NewsHolder.this.newsEntity);
                }
            });
        }


    }

    /**
     * 跳至地图
     *
     * @param context 资源
     * @param entity    {@link WallEntity}
     */
    private void gotoLocationSetting(Context context, NewsEntity entity) {
        if (TextUtils.isEmpty(entity.getLoc_latitude()) || TextUtils.isEmpty(entity.getLoc_longitude())) {
            return;
        }

        LocationUtil.goNavigation(context, Double.valueOf(entity.getLoc_latitude()), Double.valueOf(entity.getLoc_longitude()), entity.getLoc_type());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more_or_collapse:
                changeTextDisplay(!isDisplayMore);
                break;

            case R.id.iv_video_top:
                playVideo();
                break;

            case R.id.iv_pic:
                if (needOpenWeb(newsEntity)) {
                    String trackUrl = newsEntity.getTrack_url() + accountUserId;
                    if (!TextUtils.isEmpty(trackUrl)) {
                        Uri uri = Uri.parse(trackUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                } else {
                    if (TextUtils.isEmpty(newsEntity.getVideo_filename())) {
                        enlargePic();
                    } else {
                        playVideo();
                    }
                }
//                if (!TextUtils.isEmpty(imageUrl)) {
//                    enlargePic();
//                }
                break;
            case R.id.new_comment_linear://评论
                Intent intent;
                intent = new Intent(mContext, DiaryCommentActivity.class);
                intent.putExtra(Constant.CONTENT_GROUP_ID, newsEntity.getContent_group_id());
                intent.putExtra(Constant.CONTENT_ID, newsEntity.getContent_id());
                intent.putExtra(Constant.GROUP_ID, newsEntity.getGroup_id());
                intent.putExtra(Constant.AGREE_COUNT, newsEntity.getLove_count());
                intent.putExtra(Constant.POSITION, position);
                fragment.startActivityForResult(intent, Constant.INTENT_UPDATE_DIARY);
                break;
            case R.id.new_good_job_linear://赞
                break;
            case R.id.btn_option:
                if (newsEntity != null) {
                    initItemMenu(v);
                }
                break;
            case R.id.iv_good:
                updateLovedView();
                if (TextUtils.isEmpty(newsEntity.getLove_id())) {
                    doLove(newsEntity, false);
                } else {
                    doLove(newsEntity, true);
                }
                break;
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initItemMenu(View v) {
        if(newsEntity == null)return;

        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.inflate(R.menu.news_item_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit_this_post:
                        editDiaryAction();
                        break;
                    case R.id.menu_delete_this_post:
//                        mViewClickListener.remove(newsEntity);
                        break;
                }
                return true;
            }
        });
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
    }


    private void editDiaryAction() {
        LogUtil.d(TAG, "onBindViewHolder" + "Content_group_id=======2" + newsEntity.getContent_group_id());

        Intent intent;
        intent = new Intent(mContext, WriteNewsActivity.class);
        intent.putExtra(Constant.WALL_ENTITY, newsEntity);
        intent.putExtra(Constant.CONTENT_GROUP_ID, newsEntity.getContent_group_id());
        intent.putExtra(Constant.GROUP_ID, newsEntity.getGroup_id());
        intent.putExtra(Constant.POSITION, newsEntity);
        fragment.startActivityForResult(intent, Constant.ACTION_NEWS_CREATE);
    }

    public boolean needOpenWeb(NewsEntity newsEntity) {
        if (NewsEntity.CONTENT_TYPE_ADS.equals(newsEntity.getContent_type())) {
            if (!TextUtils.isEmpty(newsEntity.getVideo_filename())) { // 视频不为空
                new HttpTools(mContext).get(newsEntity.getTrack_url() + accountUserId, null, null, null);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void enlargePic() {
        Intent intent = new Intent(mContext, WallViewPicActivity.class);
        Map<String, String> condition = new HashMap<>();
        condition.put(Constant.CONTENT_ID, newsEntity.getContent_id());
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CONDITION, UrlUtil.mapToJsonstring(condition));
        String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
        intent.putExtra(Constant.REQUEST_URL, url);
        intent.putExtra(Constant.USER_ID, newsEntity.getUser_id());
        intent.putExtra(Constant.POSITION, position);
        fragment.startActivityForResult(intent, Constant.INTENT_REQUEST_UPDATE_PHOTOS);


    }

    private void playVideo() {
//       // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
        Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
        // 传的值对应视频的content_creator_id
        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, newsEntity.getContent_creator_id());
        // 传的值对应video_filename
        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, newsEntity.getVideo_filename());
        fragment.startActivity(intent);

    }

    private void doLove(NewsEntity newsEntity, boolean love) {
        HashMap<String, String> params = new HashMap<>();
        params.put("content_id", newsEntity.getContent_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + accountUserId);
        RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE, params);
        callBack.setLinkType(CallBack.LINK_TYPE_POST_LOVE);
        mHttpTools.post(requestInfo, TAG, callBack);
    }

    private void updateLovedView() {
        int count = Integer.valueOf(newsEntity.getLove_count());
        int resId;

        if (TextUtils.isEmpty(newsEntity.getLove_id())) {
            count += 1;
            resId = R.drawable.goodjob_clicked;
            newsEntity.setLove_id(accountUserId);
        } else {
            count -= 1;
            resId = R.drawable.goodjob_nonclicked;
            newsEntity.setLove_id(null);
        }

        newsEntity.setLove_count(String.valueOf(count));
        ibAgree.setImageResource(resId);
        newsGoodMember.setText(String.format(newsGoodMember.getContext().getString(R.string.loves_count), count));
    }

    private void changeTextDisplay(boolean isDisplayMore) {
        this.isDisplayMore = isDisplayMore;
//        LogUtil.i(TAG, "isDisplayMore==========" + isDisplayMore);
        if (globalLayoutListener == null) {
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    LogUtil.i(TAG, "onGlobalLayout");

                    int lineCount = tvContent.getLineCount();
                    if (lineCount > defaultLineCount) {
                        newsEntity.setVisibleOfTvMore(true);
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(4);
                        CharSequence sourceText = tvContent.getText();
                        SpannableStringBuilder ssb = new SpannableStringBuilder(sourceText);
                        ssb.replace(maxLineEndIndex - 3, ssb.length(), "...");
                        tvContent.setText(ssb);
                        tvMoreOrCollapse.setVisibility(View.VISIBLE);
                        tvMoreOrCollapse.setText(R.string.text_more);
                    }
                }
            };
        }
        if (!isDisplayMore) {
            tvContent.setMaxLines(defaultLineCount);
            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        } else {
            tvContent.setMaxLines(Integer.MAX_VALUE);
            tvContent.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            tvMoreOrCollapse.setText(R.string.text_collapse);
        }

        if (newsEntity != null) {
            tvContent.setText(newsEntity.getText_description());
        }

    }


    class CallBack implements HttpCallback {
        public static final int LINK_TYPE_SUBMIT_PICTURE = 2;
        public static final int LINK_TYPE_POST_LOVE = 3;
        public static final int LINK_TYPE_SAVE_PHOTOS = 4;
        public static final int LINK_TYPE_SAVE_VIDEO = 5;
        public static final int LINK_TYPE_PUT_PHOTO_MAX = 6;

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
        public void onResult(String string) {
            switch (linkType) {
                case LINK_TYPE_POST_LOVE:
                    LogUtil.d(TAG, "onResult& LINK_TYPE_POST_LOVE");
                    if (fragment instanceof DiaryInformationFragment) {
                        ((DiaryInformationFragment) fragment).setResultOK(false);
                        ((DiaryInformationFragment) fragment).setProgressVisibility(View.GONE);
                    }
                    break;
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
}

    /**
     * 设置收起开头的显示/隐藏
     *
     * @param visibility
     * @see View#setVisibility(int)
     */
    public void setSwitchVisibility(int visibility) {
        tvMoreOrCollapse.setVisibility(visibility);
    }
}
