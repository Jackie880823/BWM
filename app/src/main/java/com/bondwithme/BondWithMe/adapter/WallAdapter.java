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
import com.bondwithme.BondWithMe.ui.BaseFragment;

import java.util.List;

public class WallAdapter extends RecyclerView.Adapter<WallHolder> {

    private Context context;
    private BaseFragment fragment;
    private List<WallEntity> data;
    private HttpTools mHttpTools;


    public WallAdapter(BaseFragment fragment, List<WallEntity> data) {
        context = fragment.getContext();
        this.fragment = fragment;
        this.data = data;
        mHttpTools = new HttpTools(context);
    }

    @Override
    public WallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new WallHolder(fragment, view, mHttpTools, false);
    }

    public void add(List<WallEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(final WallHolder holder, int position) {
        WallEntity wall = data.get(position);
        holder.setViewClickListener(mViewClickListener);
        holder.setWallEntity(wall);
        holder.setSwitchVisibility(View.GONE);
        holder.setContent(wall, context);
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