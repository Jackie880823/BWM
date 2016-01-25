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
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.RewardEntity;
import com.bondwithme.BondWithMe.util.LogUtil;

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
        LogUtil.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(final VHItem holder, int position) {
        rewardEntity = data.get(position);
        BitmapTools.getInstance(mContext).display(holder.ivReward,rewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
        holder.tvRewardTile.setText(rewardEntity.getTitle());
        holder.tvRewardDate.setText(mContext.getString(R.string.valid_till)+" "+rewardEntity.getVoucher_due());
        holder.tvRewardCostPoint.setText(rewardEntity.getPoint()+" "+mContext.getString(R.string.points));

        //user_point < reward_point，locked；
        if(Integer.parseInt(rewardEntity.getPoint()) > Integer.parseInt(userPoint)){
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.rl.setBackgroundColor(mContext.getResources().getColor(R.color.reward_locked));
        }else {
            holder.ivLock.setVisibility(View.INVISIBLE);
            holder.rl.setBackgroundColor(mContext.getResources().getColor(R.color.default_text_color_while));
        }
        Long finishMillis = getFinishMillis(rewardEntity.getEnd_date_timestamp());
        holder.setTextTime(finishMillis);
    }

    private Long getFinishMillis(String sec) {
        Long currentSec = System.currentTimeMillis()/1000;
        LogUtil.d(TAG,"currentSec======="+currentSec + "second======"+sec);
        return Long.parseLong(sec) - currentSec;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvDay;
        private TextView tvHrs;
        private TextView tvMin;
        private TextView tvSec;
        private TextView tvRewardTile;
        private TextView tvRewardDate;
        private TextView tvRewardCostPoint;
        private NetworkImageView ivReward;
        private RelativeLayout rl;
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
            ivReward = (NetworkImageView) itemView.findViewById(R.id.iv_reward);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl_reward_item);
            ivLock = (ImageView) itemView.findViewById(R.id.iv_locked);
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


        @Override
        public void onClick(View v) {

        }
    }



}