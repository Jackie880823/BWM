package com.madx.bwm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.BigDayEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by liangzemian on 15/4/12.
 */
public class BigDayAdapter extends RecyclerView.Adapter<BigDayAdapter.VHItem> {

    private Context mContext;
    private List<BigDayEntity> data;


    public BigDayAdapter(Context context, List<BigDayEntity> data ) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.big_day_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }


    public void add(List<BigDayEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        BigDayEntity bigDayEntity = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, bigDayEntity.getAction_user_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.user_name.setText(bigDayEntity.getAction_username());
        holder.day_time.setText(bigDayEntity.getMessage());

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView user_name;
        private TextView day_time;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            owner_head= (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            user_name= (TextView) itemView.findViewById(R.id.user_name);
            day_time = (TextView)itemView.findViewById(R.id.day_time);

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

}
