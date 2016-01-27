package com.bondwithme.BondCorp.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bondwithme.BondCorp.util.LogUtil;


public abstract class HeaderListRecyclerAdapter<E, VH extends RecyclerView.ViewHolder>
        extends ListRecyclerAdapter<E, RecyclerView.ViewHolder> {


    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private final LayoutInflater inflater;


    public HeaderListRecyclerAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }


    protected final View inflate(@LayoutRes int layout, ViewGroup parent) {
        return inflater.inflate(layout, parent, false);
    }


    @Override
    public final int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }


    @Override
    public final int getItemCount() {
        return super.getItemCount() + 1;
    }


    public abstract VH onCreateItemViewHolder(ViewGroup parent);
    public abstract VH onCreateHeaderViewHolder(ViewGroup parent);


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.i("", "onCreateViewHolder===== " + viewType);
        if(VIEW_TYPE_HEADER != viewType) {
            return onCreateItemViewHolder(parent);
        } else {
            return onCreateHeaderViewHolder(parent);
        }

//        return viewType == VIEW_TYPE_HEADER
//                ? onCreateHeaderViewHolder(parent)
//                : onCreateItemViewHolder(parent);
    }


    public abstract void onBindItemViewHolder(VH holder, int position);
    public abstract void onBindHeaderViewHolder(VH holder, int position);


    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            onBindItemViewHolder((VH) holder, position - 1);
        }else{
            onBindHeaderViewHolder((VH) holder, position);
        }
    }


}
