package com.bondwithme.BondCorp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.MissEntity;
import com.bondwithme.BondCorp.util.MyDateUtils;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;

import java.util.List;

public class MissAdapter extends RecyclerView.Adapter<MissAdapter.VHItem> {
    private Context mContext;
    private List<MissEntity> data;
    Activity activity;


    public MissAdapter(Context context, List<MissEntity> data ,Activity activity) {
        mContext = context;
        this.data = data;
        activity = (Activity)activity;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.miss_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<MissEntity> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        MissEntity missEntity = data.get(position);
        holder.owner_name.setText(missEntity.getMessage_variable());
        holder.push_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, missEntity.getCreation_date()));
        BitmapTools.getInstance(mContext).display(holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, missEntity.getAction_user_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView owner_name;
        private TextView push_date;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            owner_head= (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            owner_name= (TextView) itemView.findViewById(R.id.owner_name);
            push_date= (TextView) itemView.findViewById(R.id.push_date);

            /**
             * Begin
             */
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null ) {
                        itemClickListener.topItemClick(data.get(getAdapterPosition()).getAction_user_id());
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
        void topItemClick(String member_id);


    }

/**
 * end
 */

}