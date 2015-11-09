package com.bondwithme.BondWithMe.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.RecommendEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.RecommendActivity;
import com.bondwithme.BondWithMe.ui.RelationshipActivity;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.VHItem> {
    private Context mContext;
    private List<RecommendEntity> data;
    List<String> data_Zh = new ArrayList<String>();
    List<String> data_Us = new ArrayList<String>();
    private boolean isZh;
    public static String From_RecommendActivity = "From_RecommendActivity";
    //begin
    public static final int CHOOSE_RELATION_CODE = 1;
    private int positionId;
    private boolean isEditingMode;

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
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(RecommendAdapter.VHItem holder, int position) {
        RecommendEntity recommendEntity = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, recommendEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.user_name.setText(recommendEntity.getUser_given_name());

        getDataEn();
        if (Locale.getDefault().toString().equals("zh_CN")) {
            isZh = true;
            getDataZh();
        }

        String relationship = recommendEntity.getUser_recom_rel();
        if (TextUtils.isEmpty(relationship)) {
//            continue;
        }
        if (isZh) {
            int index = data_Us.indexOf(relationship);
            if (index != -1) {
                relationship = getDataZh().get(index);
                holder.tvRelationshipWithMember.setText(recommendEntity.getUser_recommend() + mContext.getResources().getString(R.string.title_birthday_title_prefix2) + relationship);
            }
        } else {
            holder.tvRelationshipWithMember.setText(recommendEntity.getUser_recommend() + mContext.getResources().getString(R.string.title_birthday_title_prefix2) +" "+ relationship);
        }

        if (isEditingMode){
            holder.recommend_relationship.setVisibility(View.INVISIBLE);
            holder.cbRecommend.setVisibility(View.VISIBLE);
            holder.cbRecommend.setChecked(recommendEntity.isSelected());
        }else {
            holder.cbRecommend.setVisibility(View.INVISIBLE);
            holder.recommend_relationship.setVisibility(View.VISIBLE);
        }

        holder.itemView.setTag(data.get(position));

    }

    private List<String> getDataZh() {
        Configuration configuration = new Configuration();
        //设置应用为简体中文
        configuration.locale = Locale.SIMPLIFIED_CHINESE;
        mContext.getResources().updateConfiguration(configuration, null);
        String[] ralationArrayZh = mContext.getResources().getStringArray(R.array.relationship_item);
        data_Zh = Arrays.asList(ralationArrayZh);
        return data_Zh;
    }

    private List<String> getDataEn() {
        Configuration configuration = new Configuration();
        //设置应用为英文
        configuration.locale = Locale.US;
        mContext.getResources().updateConfiguration(configuration, null);
        String[] ralationArrayUs = mContext.getResources().getStringArray(R.array.relationship_item);
        data_Us = Arrays.asList(ralationArrayUs);
        return data_Us;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setEditingMode(boolean isEditingMode){
        this.isEditingMode = isEditingMode;
    }




    public class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView user_name;
        private TextView tvRelationshipWithMember;
        //begin
        private TextView recommend_relationship;
        private CheckBox cbRecommend;


        //end
        public VHItem(final View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            owner_head = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            tvRelationshipWithMember = (TextView) itemView.findViewById(R.id.tv_relationship_with_member);
            //begin
            recommend_relationship = (TextView) itemView.findViewById(R.id.recommend_relationship);
            cbRecommend = (CheckBox) itemView.findViewById(R.id.cb_recommend);
            recommend_relationship.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPositionId(getAdapterPosition());
                    Intent intent = new Intent(mContext, RelationshipActivity.class);
                    intent.putExtra("member_id", data.get(getAdapterPosition()).getUser_id());
                    intent.putExtra(RecommendActivity.TAG,From_RecommendActivity);
                    ((Activity) mContext).startActivityForResult(intent, CHOOSE_RELATION_CODE);
                }
            });


            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClcikListener != null && data != null){
                        mItemClcikListener.itemClick(data.get(getAdapterPosition()),getAdapterPosition());
                    }
                }
            });

            //end
        }


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClcikListener = itemClickListener;
    }

    public ItemClickListener mItemClcikListener;

    public interface ItemClickListener {
        void itemClick(RecommendEntity recommendEntity, int position);
    }


}