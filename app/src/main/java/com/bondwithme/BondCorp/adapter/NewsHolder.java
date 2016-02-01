package com.bondwithme.BondCorp.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.NewsEntity;
import com.bondwithme.BondCorp.ui.BaseFragment;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.ui.WriteNewsActivity;
import com.bondwithme.BondCorp.ui.wall.DiaryCommentActivity;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.MyDateUtils;

import java.lang.reflect.Field;

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
     * 内容
     */
    private TextView tvContent;

    private TextView tvMoreOrCollapse;
    /**
     * 网络图片
     */
    private NetworkImageView ivPic;
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
    public View llnewsImage;
    private static final String accountUserId = MainActivity.getUser().getUser_id();
    private BaseFragment fragment;

    private String imageUrl;
    private String videoUrl;

    private boolean isDisplayMore;
    private int defaultLineCount = 5;

    public NewsHolder(BaseFragment fragment,View itemView, Context mContext) {
        super(itemView);
        this.mContext = mContext;
        this.fragment = fragment;

        tvCategoryName = (TextView) itemView.findViewById(R.id.news_category_name);
        tvTitle = (TextView) itemView.findViewById(R.id.news_title);
        tvDate = (TextView) itemView.findViewById(R.id.news_date);

        llnewsImage = itemView.findViewById(R.id.ll_news_image);
        ivPic = (NetworkImageView) itemView.findViewById(R.id.iv_pic);
        ibtnVideo = (ImageView) itemView.findViewById(R.id.iv_video_top);

        tvContent = (TextView) itemView.findViewById(R.id.news_content);
        tvMoreOrCollapse = (TextView) itemView.findViewById(R.id.tv_more_or_collapse);
        new_comment_linear = itemView.findViewById(R.id.new_comment_linear);
        new_good_job_linear = itemView.findViewById(R.id.new_comment_linear);
        btnOption = (ImageButton) itemView.findViewById(R.id.btn_option);

        changeTextDisplay(isDisplayMore);

        ivPic.setOnClickListener(this);
        ibtnVideo.setOnClickListener(this);
        tvMoreOrCollapse.setOnClickListener(this);
        new_comment_linear.setOnClickListener(this);
        new_good_job_linear.setOnClickListener(this);
        btnOption.setOnClickListener(this);
    }

    public void setNewsEntity(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;
    }


    //news content包括title，date，image，video_thumbnail，content_text
    public void setContent(NewsEntity newsEntity, Context mContext) {
        tvTitle.setText(newsEntity.getContent_title());
        LogUtil.d(TAG, "date======" + newsEntity.getContent_creation_date());
//        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getRelease_date()));
        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getContent_creation_date()));
        tvContent.setText(newsEntity.getText_description());

        if ((!accountUserId.equals(this.newsEntity.getUser_id()) && (Integer.valueOf(newsEntity.getPhoto_count()) <= 0 && TextUtils.isEmpty(newsEntity.getVideo_filename())))) {
            // 不是当前用户：没有图片也没有视频都不需要显更多功能按钮
            btnOption.setVisibility(View.GONE);
        } else {
            btnOption.setVisibility(View.VISIBLE);
        }
//        imageUrl = newsEntity.getImage();
//        videoUrl = newsEntity.getVideo();
//        if (!TextUtils.isEmpty(imageUrl)) {
//            //display pic
//            ivPic.setVisibility(View.VISIBLE);
//            BitmapTools.getInstance(mContext).display(ivPic, newsEntity.getImage());
//            ibtnVideo.setVisibility(View.INVISIBLE);
//        } else if (!TextUtils.isEmpty(videoUrl)) {
//            ivPic.setVisibility(View.VISIBLE);
//            BitmapTools.getInstance(mContext).display(ivPic, newsEntity.getVideo_thumbnail());
//            ibtnVideo.setVisibility(View.VISIBLE);
//        } else if (TextUtils.isEmpty(imageUrl) && TextUtils.isEmpty(videoUrl)) {
//            ivPic.setVisibility(View.GONE);
//            ibtnVideo.setVisibility(View.GONE);
//        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more_or_collapse:
                changeTextDisplay(!isDisplayMore);
                break;

            case R.id.ibtn_video:
                playVideo();
                break;

            case R.id.iv_pic:
                if (!TextUtils.isEmpty(imageUrl)) {
                    enlargePic();
                }
                break;
            case R.id.new_comment_linear://评论
                Intent intent;
                intent = new Intent(mContext, DiaryCommentActivity.class);
//                intent.putExtra(Constant.CONTENT_GROUP_ID, wallEntity.getContent_group_id());
//                intent.putExtra(Constant.CONTENT_ID, wallEntity.getContent_id());
//                intent.putExtra(Constant.GROUP_ID, wallEntity.getGroup_id());
//                intent.putExtra(Constant.AGREE_COUNT, wallEntity.getLove_count());
//                intent.putExtra(Constant.POSITION, position);
                ((Activity) mContext).startActivityForResult(intent, Constant.INTENT_UPDATE_NEWS);
                break;
            case R.id.new_good_job_linear://赞

                break;
            case R.id.btn_option:
                if (newsEntity != null) {
                    initItemMenu(v);
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
        Intent intent;
        intent = new Intent(mContext, WriteNewsActivity.class);
        intent.putExtra(Constant.WALL_ENTITY, newsEntity);
        intent.putExtra(Constant.CONTENT_GROUP_ID, newsEntity.getContent_group_id());
        intent.putExtra(Constant.GROUP_ID, newsEntity.getGroup_id());
        intent.putExtra(Constant.POSITION, newsEntity);
        fragment.startActivityForResult(intent, Constant.ACTION_NEWS_CREATE);
    }

    private void enlargePic() {
//        Intent intent = new Intent(mContext, ViewLargePicActivity.class);
//        Intent intent = new Intent(mContext, ViewUrlPicActivity.class);
//        intent.putExtra(PIC_URL, newsEntity.getImage());
//        mContext.startActivity(intent);


    }

    private void playVideo() {
//        LogUtil.d(TAG, "playVideo==========" + newsEntity.getVideo());
//        // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
//        Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
//        // 传的值对应视频的content_creator_id,news or rewards的视频除外
//        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, "for_news_or_rewards");
//        // 传的值对应video_filename，news or rewards的视频传full_url.
//        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, newsEntity.getVideo());
//        mContext.startActivity(intent);

    }

    private void changeTextDisplay(boolean isDisplayMore) {
        this.isDisplayMore = isDisplayMore;
        LogUtil.i(TAG, "isDisplayMore==========" + isDisplayMore);
        if (globalLayoutListener == null) {
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LogUtil.i(TAG, "onGlobalLayout");

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
