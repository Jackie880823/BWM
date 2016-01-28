package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondWithMe.entity.MyRewardEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by heweidong on 16/1/24.
 */
public class MyRewardAdapter extends RecyclerView.Adapter<MyRewardAdapter.VHItem> {

    private List<MyRewardEntity> data;
    private Context mContext;
    private MyRewardEntity myRewardEntity = null;

    public MyRewardAdapter(List<MyRewardEntity> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_reward,parent,false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        myRewardEntity = data.get(position);
        BitmapTools.getInstance(mContext).display(holder.ivMyReward,myRewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
        holder.tvReWardTitle.setText(myRewardEntity.getTitle());
        holder.tvReWardExpireDate.setText(mContext.getString(R.string.valid_till) + " "+myRewardEntity.getVoucher_due());
//        if(getTimeMillis(myRewardEntity.getVoucher_due()+" 00:00:00") <= System.currentTimeMillis()){
        if("1".equals(myRewardEntity.getExpired_flag())){
            holder.rlMyReward.setVisibility(View.VISIBLE);
            holder.tvExpired.setVisibility(View.VISIBLE);
            holder.rlRewardInfo.setVisibility(View.INVISIBLE);
        }else {
            holder.rlMyReward.setVisibility(View.INVISIBLE);
            holder.tvExpired.setVisibility(View.INVISIBLE);
            holder.rlRewardInfo.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //将“yyyy-MM-dd HH:mm:ss”格式的时间转换成Long类型的秒数
    public static Long getTimeMillis(String time) {
        long l = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(time);
            l = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    class VHItem extends RecyclerView.ViewHolder{
        private NetworkImageView ivMyReward;
        private TextView tvReWardTitle;
        private TextView tvReWardExpireDate;
        private TextView tvExpired;
        private RelativeLayout rlMyReward;
        private RelativeLayout rlRewardInfo;

        public VHItem(View itemView) {
            super(itemView);
            ivMyReward = (NetworkImageView) itemView.findViewById(R.id.iv_my_reward);
            tvReWardTitle = (TextView) itemView.findViewById(R.id.tv_reward_title);
            tvReWardExpireDate = (TextView) itemView.findViewById(R.id.tv_reward_time);
            tvExpired = (TextView) itemView.findViewById(R.id.tv_expired);
            rlMyReward = (RelativeLayout) itemView.findViewById(R.id.rl_my_reward);
            rlRewardInfo = (RelativeLayout) itemView.findViewById(R.id.rl_reward_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && data != null) {
                        itemClickListener.itemClick(data.get(getAdapterPosition()), getAdapterPosition());

                    }
                }
            });

        }
    }

    public ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void itemClick(MyRewardEntity myRewardEntity, int position);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
