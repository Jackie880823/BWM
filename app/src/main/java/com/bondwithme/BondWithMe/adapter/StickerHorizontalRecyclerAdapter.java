package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.AnimatedGifDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/6/30.
 */
public class StickerHorizontalRecyclerAdapter extends RecyclerView.Adapter<StickerHorizontalRecyclerAdapter.VHItem> {
    private List<String> list = new ArrayList<>();
    private List<String> stickerNameList = new ArrayList<>();
    private Context mContext;
    private int clickPosition;
    private LinearLayoutManager linearLayoutManager;

    public StickerHorizontalRecyclerAdapter(LinkedHashMap<String, List<String>> map, Context mContext, LinearLayoutManager linearLayoutManager) {
        if (null != map && map.size() > 0) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                List<String> listString = entry.getValue();
                String name = entry.getKey();
                if (listString != null && listString.size() > 0) {
                    list.add(listString.get(0));
                    stickerNameList.add(name);
                }
            }
        }
        this.mContext = mContext;
        this.linearLayoutManager = linearLayoutManager;
    }

    public void setScrollPosition(String positionName) {
        int lastPosition = clickPosition;
        for (int i = 0; i < stickerNameList.size(); i++) {
            if (stickerNameList.get(i).equals(positionName)) {
                clickPosition = i;
                break;
            }
        }
        notifyItemChanged(lastPosition);
        notifyItemChanged(clickPosition);
        linearLayoutManager.scrollToPosition(clickPosition);
    }

    public String getFirstStickerName() {
        if (stickerNameList.size() > 0) {
            return stickerNameList.get(0);
        }
        return "";
    }

    @Override
    public StickerHorizontalRecyclerAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.stickers_item_layout, null);
        return new VHItem(convertView);
    }

    @Override
    public void onBindViewHolder(StickerHorizontalRecyclerAdapter.VHItem holder, int position) {
        String firstStickerPath = list.get(position);
        if (clickPosition == position) {
            holder.stickRelative.setBackgroundColor(Color.GRAY);
        } else {
            holder.stickRelative.setBackgroundColor(Color.WHITE);
        }
        try {
            File file = new File(firstStickerPath);
            InputStream inputStream = new FileInputStream(file);//mContext.getAssets().open(filePath);
            if (firstStickerPath.endsWith("gif")) {
                AnimatedGifDrawable animatedGifDrawable = new AnimatedGifDrawable(mContext.getResources(), 0, inputStream, null);
                Drawable drawable = animatedGifDrawable.getDrawable();
                holder.imageView.setImageDrawable(drawable);
            } else {
                holder.imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VHItem extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout stickRelative;

        public VHItem(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.stick_item_imageView);
            stickRelative = (RelativeLayout) itemView.findViewById(R.id.stick_item_relative);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int lastPosition = clickPosition;
                    int position = getAdapterPosition();
                    clickPosition = position;
                    notifyItemChanged(lastPosition);
                    notifyItemChanged(clickPosition);
                    if (mViewClickListener != null) {
                        mViewClickListener.showOriginalPic(stickerNameList.get(position));
                    }
                }
            });
        }
    }

    public interface StickerItemClickListener {
        public void showOriginalPic(String positionName);
    }

    public StickerItemClickListener mViewClickListener;

    public void setPicClickListener(StickerItemClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
}
