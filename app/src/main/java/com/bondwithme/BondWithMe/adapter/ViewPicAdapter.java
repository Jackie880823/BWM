package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;

import java.util.List;

public class ViewPicAdapter extends RecyclerView.Adapter<ViewPicAdapter.VHItem> {
    private Context mContext;
    private List<PhotoEntity> data;
    private String memberId;

    public ViewPicAdapter(Context context, List<PhotoEntity> data, String memberId) {
        mContext = context;
        this.data = data;
        this.memberId = memberId;
    }

    @Override
    public ViewPicAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pic_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<PhotoEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }

    @Override
    public void onBindViewHolder(ViewPicAdapter.VHItem holder, int position) {
        PhotoEntity photo = data.get(position);
        String userId = photo.getUser_id();
        if (TextUtils.isEmpty(userId)) {
            userId = memberId;
        }
        VolleyUtil.initNetworkImageView(mContext, holder.iv_pic, String.format(Constant.API_GET_PIC, Constant.Module_preview_m, userId, photo.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private int mLastPosition;

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        NetworkImageView iv_pic;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            iv_pic = (NetworkImageView) itemView.findViewById(R.id.iv_pic);
            itemView.setOnClickListener(this);
//            ibComment.setOnClickListener(this);
//            imWallsImages.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mItemClickListenner != null) {
                PhotoEntity photoEntity = data.get(position);
                NetworkImageView ss = (NetworkImageView) v.findViewById(R.id.iv_pic);
                ss.buildDrawingCache();
                mItemClickListenner.onItemClick(ss.getDrawable(), photoEntity, position);
            }

        }

    }

    public ItemClickListenner mItemClickListenner;

    public void setItemClickListenner(ItemClickListenner itemClickListenner) {
        mItemClickListenner = itemClickListenner;
    }

    public interface ItemClickListenner {
        void onItemClick(Drawable smallPic, PhotoEntity photoEntity, int position);
    }

}