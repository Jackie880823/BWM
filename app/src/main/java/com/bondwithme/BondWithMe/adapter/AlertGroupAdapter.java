package com.bondwithme.BondWithMe.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AlertGroupEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.util.List;

/**
 * 有关 AlertGroup list 的适配器
 * Created by heweidong on 15/5/14.
 */
public class AlertGroupAdapter extends RecyclerView.Adapter<AlertGroupAdapter.VHItem>{

    private Context mContext;
    private List<AlertGroupEntity> data;
    private int tag;


    //AlertGroup的几种情况：“pending”、“add”、“reject”
    private String moduleAction;
    private final static int TAG_PENDING = 0;
    private final static int TAG_ADD = 1;
    private final static int TAG_REJECT = 2;





    public AlertGroupAdapter(Context context, List<AlertGroupEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public AlertGroupAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_group_item,parent,false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(AlertGroupAdapter.VHItem holder, int position) {
        AlertGroupEntity alertGroupEntity = data.get(position);
        moduleAction = alertGroupEntity.getModule_action();

        String pendingText = mContext.getResources().getString(R.string.text_pending);
        String addText = mContext.getResources().getString(R.string.text_add_group);
        String rejectTextHas = mContext.getResources().getString(R.string.text_reject_has);
        String rejectTextDec = mContext.getResources().getString(R.string.text_reject_dec);
        String rejectTextInvite = mContext.getResources().getString(R.string.text_reject_invite);
        String rejectTextGroup = mContext.getResources().getString(R.string.text_reject_group);
        tag = tag(moduleAction);
        //设置头像
        VolleyUtil.initNetworkImageView(mContext,
                holder.ownerHead,
                String.format(Constant.API_GET_PHOTO, Constant.Module_profile, alertGroupEntity.getAction_user_id()),
                R.drawable.network_image_default, R.drawable.network_image_default);

        //设置AlertGroupContent、option
        switch (tag){
            case TAG_PENDING:
            {
                holder.tvAlertGroupContent.setText(Html.fromHtml(
                                "<b>"+alertGroupEntity.getAction_username()+"</b>" +  //人名
                                "<font color='#000000'>"+pendingText+"</font>"+   //requested you to join the group
                                "<b>\""+alertGroupEntity.getModule_name()+"\"</b>"));     //groupName
                holder.ibOption.setVisibility(View.VISIBLE); //移除option Button
            }
            break ;
            case TAG_ADD:
            {
                holder.tvAlertGroupContent.setText(Html.fromHtml(
                                "<b>"+alertGroupEntity.getAction_username()+"</b>" +
                                "<font color='#000000'>"+addText+"</font><br/>"+
                                "<b>\""+alertGroupEntity.getModule_name()+"\"</b>"));
                holder.ibOption.setVisibility(View.GONE); //移除option Button
            }
            break ;
            case TAG_REJECT:
            {
                holder.tvAlertGroupContent.setText(Html.fromHtml(
                        "<b>"+alertGroupEntity.getAction_username()+"</b>" +   //username
                        "<font color='#000000'>"+rejectTextHas+"<b>"+rejectTextDec+"</b>"+rejectTextInvite+"</font>" + //"has declined (粗体) your invite to"
                        "<b>\""+alertGroupEntity.getModule_name()+"\"</b>" +   //groupname
                        "<font color='#000000'>"+rejectTextGroup+"</font>"));  //"group"
                holder.ibOption.setVisibility(View.GONE);  //移除option Button

            }
            break ;
        }

        //设置时间
        holder.tvCreateDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext,alertGroupEntity.getCreation_date()));



    }

    private int tag(String moduleAction) {
        switch (moduleAction) {
            case "pending":
                tag = TAG_PENDING;
                break;
            case "add":
                tag = TAG_ADD;
                break;
            case "reject":
                tag = TAG_REJECT;
                break;
        }
        return tag;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder{

        private CircularNetworkImage ownerHead;
        private TextView tvAlertGroupContent;
        private TextView tvCreateDate;
        private ImageButton ibOption;
        public VHItem(View itemView) {
            super(itemView);
            ownerHead = (CircularNetworkImage)itemView.findViewById(R.id.owner_head);
            tvAlertGroupContent = (TextView) itemView.findViewById(R.id.tv_alert_group_content);
            tvCreateDate = (TextView) itemView.findViewById(R.id.tv_create_date);
            ibOption = (ImageButton) itemView.findViewById(R.id.ib_option);


            ibOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && data != null) {
                        itemClickListener.optionClick(data.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }
    }
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void optionClick(AlertGroupEntity alertGroupEntity, int position);

    }
}
