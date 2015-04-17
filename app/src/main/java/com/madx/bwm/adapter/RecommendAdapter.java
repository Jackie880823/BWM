package com.madx.bwm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.RecommendEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.RelationshipActivity;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.VHItem> {
    private Context mContext;
    private List<RecommendEntity> data;
    //begin
    public static final int CHOOSE_RELATION_CODE = 1;
    private int positionId;

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public void setAdapterNotifyDataSetChanged(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public RecommendAdapter(Context context, List<RecommendEntity> data) {
        mContext = context;
        this.data = data;
    }

    //end
    @Override
    public RecommendAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<RecommendEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(RecommendAdapter.VHItem holder, int position) {
        RecommendEntity recommendEntity = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, recommendEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.user_name.setText(recommendEntity.getUser_given_name());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView user_name;
        //begin
        private TextView recommend_relationship;

        //end
        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            owner_head = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            //begin
            recommend_relationship = (TextView) itemView.findViewById(R.id.recommend_relationship);
            recommend_relationship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPositionId(getAdapterPosition());
                    Intent intent = new Intent(mContext, RelationshipActivity.class);
                    intent.putExtra("member_id", data.get(getAdapterPosition()).getUser_id());
                    ((Activity) mContext).startActivityForResult(intent, CHOOSE_RELATION_CODE);
                }
            });

            //end
        }
    }


}