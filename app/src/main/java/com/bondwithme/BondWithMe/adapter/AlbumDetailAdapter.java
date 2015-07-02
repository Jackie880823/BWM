package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AlbumPhotoEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;

import java.util.List;

/**
 * Created by quankun on 15/5/21.
 */
public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.VHItem> {
    private Context context;
    private List<AlbumPhotoEntity> albumList;
    private String memberId;
    private int selectPosition = 0;
    private LinearLayoutManager linearLayoutManager;

    public AlbumDetailAdapter(Context context, String memberId, List<AlbumPhotoEntity> albumList, LinearLayoutManager linearLayoutManager) {
        this.context = context;
        this.albumList = albumList;
        this.memberId = memberId;
        this.linearLayoutManager = linearLayoutManager;
    }

    public void addData(List<AlbumPhotoEntity> list) {
        int positionSize = albumList.size() - 1 < 0 ? 0 : albumList.size() - 1;
        if (list == null || list.size() == 0) {
            return;
        }
        albumList.addAll(albumList.size(), list);
        notifyDataSetChanged();
        linearLayoutManager.scrollToPosition(positionSize);
    }

    public List<AlbumPhotoEntity> getAlbumList() {
        return albumList;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int position) {
        int pastPosition = selectPosition;
        this.selectPosition = position;
        notifyItemChanged(pastPosition);
        notifyItemChanged(selectPosition);
        linearLayoutManager.scrollToPosition(selectPosition);
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        convertView = LayoutInflater.from(context).inflate(R.layout.activity_aibum_detail_item, null);
        return new VHItem(convertView);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        AlbumPhotoEntity photoEntity = albumList.get(position);
        VolleyUtil.initNetworkImageView(context, holder.image_view, String.format(Constant.API_GET_PIC, Constant.Module_preview_m, memberId, photoEntity.getFile_id()),
                R.drawable.network_image_default, R.drawable.network_image_default);
        if (selectPosition == position) {
            holder.album_item_linear.setBackgroundColor(Color.YELLOW);
        } else {
            holder.album_item_linear.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class VHItem extends RecyclerView.ViewHolder {
        NetworkImageView image_view;
        LinearLayout album_item_linear;

        public VHItem(View itemView) {
            super(itemView);
            image_view = (NetworkImageView) itemView.findViewById(R.id.album_item_image_view);
            album_item_linear = (LinearLayout) itemView.findViewById(R.id.album_item_linear);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int pastPosition = selectPosition;
                    AlbumPhotoEntity albumPhotoEntity = albumList.get(position);
                    selectPosition = position;
                    notifyItemChanged(pastPosition);
                    notifyItemChanged(selectPosition);
                    if (mViewClickListener != null) {
                        mViewClickListener.showOriginalPic(albumPhotoEntity.getFile_id());
                        linearLayoutManager.scrollToPosition(position);
                    }
                }
            });
        }
    }

    public interface SelectViewClickListener {
        public void showOriginalPic(String fileId);
    }

    public SelectViewClickListener mViewClickListener;

    public void setPicClickListener(SelectViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
}
