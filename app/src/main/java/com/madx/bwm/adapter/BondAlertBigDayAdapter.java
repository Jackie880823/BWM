package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.BigDayEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.FamilyProfileActivity;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/5/19.
 */
public class BondAlertBigDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public enum ITEM_TYPE
    {
        ITEM_TYPE_TODAY,
        ITEM_TYPE_UPCOMING
    }

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BigDayEntity> mData;

    private boolean isFstToday = false;
    private int fstTodayPosition;

    private boolean isFstUpcoming = false;
    private int fstUpcomingPosition;

    public BondAlertBigDayAdapter(Context mContext, List<BigDayEntity> data) {
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_TODAY.ordinal())
        {
            return new TodayViewHolder(mLayoutInflater.inflate(R.layout.bond_alert_big_day_today_item, parent, false));
        }
        else if (viewType == ITEM_TYPE.ITEM_TYPE_UPCOMING.ordinal())
        {
            return new UpcomingViewHolder(mLayoutInflater.inflate(R.layout.bond_alert_big_day_upcoming_item, parent, false));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof TodayViewHolder)
        {
            TodayViewHolder item = (TodayViewHolder)holder;

            if (isFstToday)
            {
                if (fstTodayPosition == position)
                {
                    item.llTop.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                isFstToday = true;
                fstTodayPosition = position;
                item.llTop.setVisibility(View.VISIBLE);
            }

            VolleyUtil.initNetworkImageView(mContext, item.cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position).getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            item.tvName.setText(mData.get(position).getUser_given_name() + mContext.getResources().getString(R.string.text_today_birthday_on));
            item.tvTime.setText(mData.get(position).getDay() + ", " + mData.get(position).getBirthday_date());
        }
        else if (holder instanceof UpcomingViewHolder)
        {
            UpcomingViewHolder item = (UpcomingViewHolder)holder;
            if (isFstUpcoming)
            {
                if (fstUpcomingPosition == position)
                {
                    item.llTop.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                isFstUpcoming = true;
                fstUpcomingPosition = position;
                item.llTop.setVisibility(View.VISIBLE);
            }


            VolleyUtil.initNetworkImageView(mContext, item.cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position).getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            item.tvName.setText(mData.get(position).getUser_given_name() + mContext.getResources().getString(R.string.text_upcoming_birthdays_on));
            item.tvTime.setText(mData.get(position).getDay() + ", " + mData.get(position).getBirthday_date());
            item.tvLeft.setText(mData.get(position).getDay_left() + mContext.getResources().getString(R.string.text_d) + mContext.getResources().getString(R.string.text_left));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ("0").equals(mData.get(position).getDay_left()) ? ITEM_TYPE.ITEM_TYPE_TODAY.ordinal() : ITEM_TYPE.ITEM_TYPE_UPCOMING.ordinal();
    }



    public  class TodayViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llTop;
        private CircularNetworkImage cniMain;
        private TextView tvName;
        private TextView tvTime;

        TodayViewHolder(View view){
            super(view);
            llTop = (LinearLayout) view.findViewById(R.id.ll_top);
            cniMain = (CircularNetworkImage) view.findViewById(R.id.owner_head);
            tvName = (TextView) view.findViewById(R.id.user_name);
            tvTime = (TextView) view.findViewById(R.id.day_time);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FamilyProfileActivity.class);
                    intent.putExtra("member_id", mData.get(getAdapterPosition()).getUser_id());
                    mContext.startActivity(intent);
                }
            });
        }

    }

    public  class UpcomingViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llTop;
        private CircularNetworkImage cniMain;
        private TextView tvName;
        private TextView tvTime;
        private TextView tvLeft;

        UpcomingViewHolder(View view)
        {
            super(view);
            llTop = (LinearLayout) view.findViewById(R.id.ll_top);
            cniMain = (CircularNetworkImage) view.findViewById(R.id.owner_head);
            tvName = (TextView) view.findViewById(R.id.user_name);
            tvTime = (TextView) view.findViewById(R.id.day_time);
            tvLeft = (TextView) view.findViewById(R.id.day_left);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FamilyProfileActivity.class);
                    intent.putExtra("member_id", mData.get(getAdapterPosition()).getUser_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }


}
