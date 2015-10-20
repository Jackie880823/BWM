package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.NewsEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.share.PreviewVideoActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;

/**
 * 用于显示news item，便于动态显示文本内容：展开（More）、收起（collapse），兼有播放视频的功能
 * Created by heweidong on 15/10/19.
 */
public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final String TAG = "NewsHolder";
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private NewsEntity newsEntity;
    private Context mContext;

    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvMoreOrCollapse;
    private NetworkImageView ivPic;
    private ImageButton ibtnVideo;

    private String imageUrl;
    private String videoUrl;

    private boolean isDisplayMore;
    private int defaultLineCount = 5;

    public NewsHolder(View itemView,Context mContext) {
        super(itemView);
        this.mContext = mContext;

        tvTitle = (TextView) itemView.findViewById(R.id.news_title);
        tvDate = (TextView) itemView.findViewById(R.id.news_date);

        ivPic = (NetworkImageView) itemView.findViewById(R.id.iv_pic);
        ibtnVideo = (ImageButton) itemView.findViewById(R.id.ibtn_video);

        tvContent = (TextView) itemView.findViewById(R.id.news_content);
        tvMoreOrCollapse = (TextView)itemView.findViewById(R.id.tv_more_or_collapse);

        changeTextDisplay(isDisplayMore);

        ibtnVideo.setOnClickListener(this);
        tvMoreOrCollapse.setOnClickListener(this);
    }

    public void setNewsEntity(NewsEntity newsEntity){
        this.newsEntity = newsEntity;
    }


    //news content包括title，date，image，video_thumbnail，content_text
    public void setContent(NewsEntity newsEntity,Context mContext){
        tvTitle.setText(newsEntity.getTitle());
        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getRelease_date()));
        tvContent.setText(newsEntity.getContent_text());
        imageUrl = newsEntity.getImage();
        videoUrl = newsEntity.getVideo();
        if (!TextUtils.isEmpty(imageUrl)){
            //display pic
            VolleyUtil.initNetworkImageView(mContext,ivPic,newsEntity.getImage());
            ibtnVideo.setVisibility(View.INVISIBLE);
        }else if(!TextUtils.isEmpty(videoUrl)){
            VolleyUtil.initNetworkImageView(mContext,ivPic,newsEntity.getVideo_thumbnail());
            ibtnVideo.setVisibility(View.VISIBLE);
        }else if(TextUtils.isEmpty(imageUrl) && TextUtils.isEmpty(videoUrl)){
            ivPic.setVisibility(View.GONE);
            ibtnVideo.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_more_or_collapse:
                changeTextDisplay(!isDisplayMore);
                break;

            case R.id.ibtn_video:
                playVideo();
                break;

            case R.id.iv_pic:
                enlargePic();
                break;
        }

    }

    private void enlargePic() {

    }

    private void playVideo() {
        LogUtil.d(TAG,"playVideo=========="+newsEntity.getVideo());
        // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
        Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
        // 传的值对应视频的content_creator_id,news or rewards的视频除外
        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, "for_news_or_rewards");
        // 传的值对应video_filename，news or rewards的视频传full_url.
        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, newsEntity.getVideo());
        mContext.startActivity(intent);

    }

    private void changeTextDisplay(boolean isDisplayMore) {
        this.isDisplayMore = isDisplayMore;

        if (globalLayoutListener == null){
            globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LogUtil.i(TAG, "onGlobalLayout");

                    int lineCount = tvContent.getLineCount();
                    if (lineCount > defaultLineCount){
                        tvMoreOrCollapse.setVisibility(View.VISIBLE);
                        tvMoreOrCollapse.setText(R.string.more);
                    }
                }
            };
        }
        if (!isDisplayMore){
            tvContent.setMaxLines(defaultLineCount);
            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        }else{
            tvContent.setMaxLines(Integer.MAX_VALUE);
            tvContent.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            tvMoreOrCollapse.setText(R.string.text_collapse);
        }

    }

    /**
     * 设置收起开头的显示/隐藏
     * @see View#setVisibility(int)
     * @param visibility
     */
    public void setSwitchVisibility(int visibility) {
        tvMoreOrCollapse.setVisibility(visibility);
    }
}
