package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.RecommendEntity;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;

import java.util.List;

public class AlertMemberAdapter extends RecyclerView.Adapter<AlertMemberAdapter.VHItem> {
    private Context mContext;
    private List<RecommendEntity> data;


    public AlertMemberAdapter(Context context, List<RecommendEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public AlertMemberAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_member_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<RecommendEntity> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(AlertMemberAdapter.VHItem holder, int position) {
//        RecommendEntity recommendEntity = data.get(position);
//        BitmapTools.getInstance(mContext).display( holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, RecommendEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//        holder.user_name.setText(recommendEntity.getUser_given_name());
//        holder.create_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, recommendEntity.getUser_active_date()));

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView user_name;
        private TextView create_date;
        private TextView status_add;
        private TextView action_add;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            owner_head= (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            user_name= (TextView) itemView.findViewById(R.id.user_name);
            create_date= (TextView) itemView.findViewById(R.id.create_date);
            status_add= (TextView) itemView.findViewById(R.id.status_add);
            action_add= (TextView) itemView.findViewById(R.id.action_add);
        }
    }



}