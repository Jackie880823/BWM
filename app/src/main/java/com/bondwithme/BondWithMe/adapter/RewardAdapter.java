package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.entity.RewardEntity;
import com.madxstudio.co8.R;
import com.madxstudio.co8.util.LogUtil;

import java.util.List;


public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.VHItem> {

    private final String TAG = "RewardAdapter";
    private final int REFRESH_MER_SEC = 1;
    private Context mContext;
    private List<RewardEntity> data;
    private RewardEntity rewardEntity;
    private String userPoint;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_MER_SEC:
                    downCount++;
                    notifyDataSetChanged();
                    handler.sendEmptyMessageDelayed(REFRESH_MER_SEC,1000);
                    break;
            }
        }
    };
    public RewardAdapter(Context context, List<RewardEntity> data,String userPoint) {
        mContext = context;
        this.data = data;
        this.userPoint = userPoint;
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(REFRESH_MER_SEC,1000);
            }
        }).start();

    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(final VHItem holder, int position) {
        rewardEntity = data.get(position);
        long leftTime  = Long.parseLong(rewardEntity.getTimestamp_left())-downCount;
//        long leftTime  =  10 - downCount;
        if(leftTime < 0){
            data.remove(position);
            notifyItemRemoved(position);
        }else {
            BitmapTools.getInstance(mContext).display(holder.ivReward, rewardEntity.getImage(), R.drawable.network_image_default, R.drawable.network_image_default);
            holder.tvRewardTile.setText(rewardEntity.getTitle());
            holder.tvRewardDate.setText(mContext.getString(R.string.valid_till) + " " + rewardEntity.getVoucher_due());
            holder.tvRewardCostPoint.setText(rewardEntity.getPoint() + " " + mContext.getString(R.string.points));

            //user_point < reward_point，locked；
            if (Integer.parseInt(rewardEntity.getPoint()) > Integer.parseInt(userPoint)) {
                holder.ivLock.setVisibility(View.VISIBLE);
                holder.rl.setVisibility(View.VISIBLE);
            } else {
                holder.ivLock.setVisibility(View.INVISIBLE);
                holder.rl.setVisibility(View.INVISIBLE);
            }
            LogUtil.d(TAG,"onBindViewHolder==Total_voucher="+rewardEntity.getTotal_voucher());
            //voucher_total = 0;
            if ("0".equals(rewardEntity.getTotal_voucher())){
                holder.rl.setVisibility(View.VISIBLE);
                holder.ivLock.setVisibility(View.INVISIBLE);
                holder.rlRewardInfo.setVisibility(View.INVISIBLE);
                holder.rlCountDownTime.setVisibility(View.INVISIBLE);
                holder.tvFullyRed.setVisibility(View.VISIBLE);
            }else {
                holder.rlRewardInfo.setVisibility(View.VISIBLE);
                holder.rlCountDownTime.setVisibility(View.VISIBLE);
                holder.tvFullyRed.setVisibility(View.INVISIBLE);
            }

            holder.setTextTime(leftTime);
            LogUtil.d(TAG,"count==="+downCount);

            LogUtil.d(TAG,"rl_item_visibility======"+holder.rl.getVisibility());
        }


    }
    int downCount = 0;


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {
        private TextView tvDay;
        private TextView tvHrs;
        private TextView tvMin;
        private TextView tvSec;
        private TextView tvRewardTile;
        private TextView tvRewardDate;
        private TextView tvRewardCostPoint;
        private TextView tvFullyRed;
        private NetworkImageView ivReward;
        private RelativeLayout rl;
        private RelativeLayout rlRewardInfo;
        private RelativeLayout rlCountDownTime;
        private ImageView ivLock;

        public VHItem(View itemView) {
            super(itemView);

            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvHrs = (TextView) itemView.findViewById(R.id.tv_hour);
            tvMin = (TextView) itemView.findViewById(R.id.tv_min);
            tvSec = (TextView) itemView.findViewById(R.id.tv_sec);
            tvRewardTile = (TextView) itemView.findViewById(R.id.tv_reward_title);
            tvRewardDate = (TextView) itemView.findViewById(R.id.tv_reward_time);
            tvRewardCostPoint = (TextView) itemView.findViewById(R.id.tv_reward_point);
            tvFullyRed = (TextView) itemView.findViewById(R.id.tv_fully_redeemed);
            ivReward = (NetworkImageView) itemView.findViewById(R.id.iv_reward);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl_reward_item);
            rlRewardInfo = (RelativeLayout) itemView.findViewById(R.id.rl_reward_info);
            rlCountDownTime = (RelativeLayout) itemView.findViewById(R.id.rl_count_down_time);
            ivLock = (ImageView) itemView.findViewById(R.id.iv_locked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && data != null) {
                        itemClickListener.itemClick(data.get(getAdapterPosition()), getAdapterPosition());

                    }
                }
            });
        }

        public void setTextTime(Long finishTime) {
            Long sec = finishTime % 60;
            Long min = (finishTime)/60 % 60 ;
            Long hour = (finishTime)/60/60 % 24;
            Long day = (finishTime )/60/60/24;

            tvSec.setText(String.valueOf(sec));
            tvMin.setText(String.valueOf(min));
            tvHrs.setText(String.valueOf(hour));
            tvDay.setText(String.valueOf(day));
        }


    }


    public ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void itemClick(RewardEntity rewardEntity, int position);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}