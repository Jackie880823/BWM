package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.RewardsEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.more.ViewLargePicActivity;
import com.bondwithme.BondWithMe.ui.share.PreviewVideoActivity;
import com.bondwithme.BondWithMe.util.LogUtil;

import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.VHItem> {

    private final String TAG = "RewardsAdapter";
    private Context mContext;
    private List<RewardsEntity> data;
    private String imageUrl;
    private String videoUrl;

    public RewardsAdapter(Context context, List<RewardsEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<RewardsEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(final VHItem holder, int position) {
        final RewardsEntity rewardsEntity = data.get(position);

        imageUrl = rewardsEntity.getImage();
        videoUrl = rewardsEntity.getVideo();
        if (!TextUtils.isEmpty(imageUrl)){
            //display pic/Video_thumbnail
            VolleyUtil.initNetworkImageView(mContext, holder.ivPic, rewardsEntity.getImage());
            holder.ibtnVideo.setVisibility(View.INVISIBLE);
            holder.ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewLargePicActivity.class);
                    intent.putExtra(NewsHolder.PIC_URL,rewardsEntity.getImage());
                    mContext.startActivity(intent);
                }
            });
        }else if(!TextUtils.isEmpty(videoUrl)){
            VolleyUtil.initNetworkImageView(mContext,holder.ivPic,rewardsEntity.getVideo_thumbnail());
            holder.ibtnVideo.setVisibility(View.VISIBLE);
        }

        holder.tvDescription.setText(rewardsEntity.getDescription());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvDescription;
        private NetworkImageView ivPic;
        private ImageButton ibtnVideo;


        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            tvDescription = (TextView)itemView.findViewById(R.id.tv_description);
            ivPic = (NetworkImageView) itemView.findViewById(R.id.iv_pic);
            ibtnVideo = (ImageButton) itemView.findViewById(R.id.ibtn_video);

            ibtnVideo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, "playVideo==========" + data.get(getAdapterPosition()).getVideo());
            // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
            Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
            // 传的值对应视频的content_creator_id,news or rewards的视频除外
            intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, "for_news_or_rewards");
            // 传的值对应video_filename，news or rewards的视频传full_url.
            intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, data.get(getAdapterPosition()).getVideo());
            mContext.startActivity(intent);


        }
    }



}