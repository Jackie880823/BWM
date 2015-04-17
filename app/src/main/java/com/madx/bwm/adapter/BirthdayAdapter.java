package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.BirthdayEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.FamilyProfileActivity;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.ViewHolder> {
    private Context mContext;
    private List<BirthdayEntity> data;

    public BirthdayAdapter(Context context, List<BirthdayEntity> data) {
        mContext = context;
        this.data = data;
        defaultTitle = context.getString(R.string.title_birthday_title);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.birthday_item, viewGroup, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new ViewHolder(view);
    }

    private String defaultTitle;

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        BirthdayEntity be = data.get(i);
        VolleyUtil.initNetworkImageView(mContext, viewHolder.userHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, be.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.userHead.setImageResource(R.drawable.ic_launcher);
        viewHolder.tvBirthdayTitle.setText(String.format(defaultTitle, be.getUser_given_name(), mContext.getString(R.string.title_birthday_title_prefix2)));
        viewHolder.tvDate.setText(be.getUser_dob());
        if ("0".equals(be.getDay_count())) {
            viewHolder.tvLeftDayCount.setText(mContext.getString(R.string.text_today));
            viewHolder.tv_date_desc.setVisibility(View.GONE);
        } else {
            viewHolder.tvLeftDayCount.setText(be.getDay_count() + "");
            viewHolder.tv_date_desc.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircularNetworkImage userHead;
        TextView tvBirthdayTitle;
        TextView tvDate;
        TextView tvLeftDayCount;
        TextView tv_date_desc;

        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            userHead = (CircularNetworkImage) itemView.findViewById(R.id.civ_user_head);
            tvBirthdayTitle = (TextView) itemView.findViewById(R.id.tv_brithday_title);
            tvLeftDayCount = (TextView) itemView.findViewById(R.id.tv_birthday_left_day_count);
            tvDate = (TextView) itemView.findViewById(R.id.tv_birthday_date);
            tv_date_desc = (TextView) itemView.findViewById(R.id.tv_date_desc);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FamilyProfileActivity.class);
                    intent.putExtra("member_id", data.get(getAdapterPosition()).getUser_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}