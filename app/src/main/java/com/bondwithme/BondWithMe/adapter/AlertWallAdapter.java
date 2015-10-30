package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AlertWallEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.wall.DiaryInformationActivity;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.util.List;

public class AlertWallAdapter extends RecyclerView.Adapter<AlertWallAdapter.VHItem> {
    private Context mContext;
    private List<AlertWallEntity> data;


    public AlertWallAdapter(Context context, List<AlertWallEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_wall_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<AlertWallEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        AlertWallEntity alertWallEntity = data.get(position);
        holder.owner_name.setText(alertWallEntity.getAction_username());
        String moduleAction = alertWallEntity.getModule_action();
        SpannableString msp=null;
        if ("postText".equalsIgnoreCase(moduleAction)) {
            moduleAction = mContext.getString(R.string.text_diary_posted_to_diary);
        } else if ("tag".equalsIgnoreCase(moduleAction)) {
            moduleAction = mContext.getString(R.string.text_diary_tagged_you_in_post);
        } else if ("comment".equalsIgnoreCase(moduleAction)) {
            String postOwner = alertWallEntity.getPostowner_username();
            if (TextUtils.isEmpty(postOwner)) {
                moduleAction = mContext.getString(R.string.text_diary_commented_on_your_post);
            } else {
                moduleAction = "also commented on " + postOwner + " post";
                msp = new SpannableString(moduleAction);
                msp.setSpan(new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.text_medium_size)), moduleAction.length() - postOwner.length() - 5, moduleAction.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                msp.setSpan(new ForegroundColorSpan(Color.parseColor("#00b7ff")), moduleAction.length() - postOwner.length() - 5, moduleAction.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), moduleAction.length() - postOwner.length() - 5, moduleAction.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if ("loveComment".equalsIgnoreCase(moduleAction)) {
            moduleAction = mContext.getString(R.string.text_diary_loved_your_comment);
        } else if ("love".equalsIgnoreCase(moduleAction)) {
            moduleAction = mContext.getString(R.string.text_diary_loved_your_post);
        } else if ("postText".equalsIgnoreCase(moduleAction)) {
            moduleAction = mContext.getString(R.string.text_diary_posted_to_diary);
        }
        if(TextUtils.isEmpty(msp)){
            holder.content_action.setText(moduleAction);
        }else {
            holder.content_action.setText(msp);
        }
        holder.push_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, alertWallEntity.getCreation_date()));
        VolleyUtil.initNetworkImageView(mContext, holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, alertWallEntity.getAction_user_id()), R.drawable.network_image_default, R.drawable.network_image_default);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView owner_name;
        private TextView push_date;
        private TextView content_action;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            owner_head = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            owner_name = (TextView) itemView.findViewById(R.id.owner_name);
            push_date = (TextView) itemView.findViewById(R.id.push_date);
            content_action = (TextView) itemView.findViewById(R.id.content_action);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DiaryInformationActivity.class);
                    intent.putExtra("content_group_id", data.get(getAdapterPosition()).getReference_id());
                    intent.putExtra("group_id", data.get(getAdapterPosition()).getModule_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }


}