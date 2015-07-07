package com.bondwithme.BondWithMe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liangzemian on 15/7/7.
 */
public class PrivateListAdapter extends RecyclerView.Adapter<PrivateListAdapter.VHItem>{
    @Override
    public PrivateListAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(PrivateListAdapter.VHItem holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    class VHItem extends RecyclerView.ViewHolder {

        public VHItem(View itemView) {
            super(itemView);
        }
    }
}
