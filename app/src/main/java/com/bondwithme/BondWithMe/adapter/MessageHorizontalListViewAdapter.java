package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.AnimatedGifDrawable;

import java.io.InputStream;
import java.util.List;

/**
 * Created by quankun on 15/4/28.
 */
public class MessageHorizontalListViewAdapter extends BaseAdapter {
    private List<String> list;
    private Context mContext;
    private int choosePosition;

    public int getChoosePosition() {
        return choosePosition;
    }

    public void setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
    }

    public MessageHorizontalListViewAdapter(List<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void addData(List<String> stickerList) {
        for (String string : stickerList) {
            if (!list.contains(string)) {
                list.add(string);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.stickers_item_layout, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.stick_item_imageView);
            viewHolder.stickRelative = (RelativeLayout) convertView.findViewById(R.id.stick_item_relative);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setBackgroundDrawable(null);
        viewHolder.imageView.setImageDrawable(null);
        if (choosePosition == position) {
            viewHolder.stickRelative.setBackgroundColor(Color.GRAY);
        } else {
            viewHolder.stickRelative.setBackgroundColor(Color.WHITE);
        }
        try {
            String filePath = list.get(position);
            InputStream inputStream = mContext.getAssets().open(filePath);
            if (filePath.endsWith("gif")) {
                AnimatedGifDrawable animatedGifDrawable = new AnimatedGifDrawable(mContext.getResources(), 0, inputStream, null);
                Drawable drawable = animatedGifDrawable.getDrawable();
                viewHolder.imageView.setImageDrawable(drawable);
            } else {
                viewHolder.imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        RelativeLayout stickRelative;
    }
}