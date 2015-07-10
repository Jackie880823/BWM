package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.ArchiveCommentEntity;

import java.util.List;

/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveCommentAdapter extends RecyclerView.Adapter<ArchiveCommentAdapter.VHItem> {

    private Context mContext;
    private List<ArchiveCommentEntity> data;

    @Override
    public ArchiveCommentAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.archive_comment_item,parent,false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {

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
