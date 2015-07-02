package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.entity.StickerItemEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.MainActivity;

import java.util.List;

/**
 * Created by heweidong on 15/6/11.
 */
public class StickerItemAdapter extends BaseAdapter{
    private String TAG = StickerItemAdapter.class.getSimpleName();
    private Context mContext;
    private List<StickerItemEntity> data;
    private StickerGroupEntity stickerGroupEntity;


    public StickerItemAdapter(Context mContext, List<StickerItemEntity> data,StickerGroupEntity stickerGroupEntity) {
        this.mContext = mContext;
        this.data = data;
        this.stickerGroupEntity = stickerGroupEntity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    ViewHolder viewHolder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sticker_item_for_grid,null);
            viewHolder.ivStickerItem = (NetworkImageView) convertView.findViewById(R.id.iv_sticker_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        StickerItemEntity stickerItemEntity = data.get(position);
        Log.i(TAG,"stickerItemEntity============="+stickerItemEntity.toString());
        VolleyUtil.initNetworkImageView(mContext,viewHolder.ivStickerItem,
                String.format( Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), stickerItemEntity.getSticker_name()+"_S", stickerGroupEntity.getPath(),stickerGroupEntity.getType()),
                R.drawable.network_image_default, R.drawable.network_image_default);
        return convertView;
    }


    class ViewHolder{
        private NetworkImageView ivStickerItem;

    }
}
