package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;

import java.util.List;

public class WallAdapter extends RecyclerView.Adapter<WallHolder> {

    private Context mContext;
    private List<WallEntity> data;
    private HttpTools mHttpTools;


    public WallAdapter(Context context, List<WallEntity> data) {
        mContext = context;
        this.data = data;
        mHttpTools = new HttpTools(mContext);
    }

    @Override
    public WallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new WallHolder(mContext, view, mHttpTools, false);
    }

    public void add(List<WallEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(final WallHolder holder, int position) {
        WallEntity wall = data.get(position);
        if (WallEntity.CONTENT_TYPE_ads.equals(wall.getContent_type())) {
            holder.setViewClickListener(null);
        } else {
            holder.setViewClickListener(mViewClickListener);
        }
        holder.setWallEntity(wall);
        holder.setSwitchVisibility(View.GONE);
        holder.setContent(wall, mContext);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public WallViewClickListener mViewClickListener;

    public void setPicClickListener(WallViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;

    }

}