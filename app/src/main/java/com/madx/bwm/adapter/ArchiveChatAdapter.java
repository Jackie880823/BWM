package com.madx.bwm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bondwithme.BondWithMe.R;


/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveChatAdapter extends RecyclerView.Adapter<ArchiveChatAdapter.VHItem>{
    @Override
    public ArchiveChatAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archivechat_item, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(ArchiveChatAdapter.VHItem holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public VHItem(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
