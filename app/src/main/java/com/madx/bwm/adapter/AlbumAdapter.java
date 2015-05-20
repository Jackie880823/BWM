package com.madx.bwm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.AlbumEntity;
import com.madx.bwm.entity.AlbumPhotoEntity;
import com.madx.bwm.http.VolleyUtil;

import java.util.List;

/**
 * Created by quankun on 15/5/19.
 */
public class AlbumAdapter extends BaseAdapter {
    private List<AlbumEntity> albumEntityList;
    private Context mContext;
    private String memberId;
    private AlbumPhotoAdapter albumPhotoAdapter;
    public AlbumAdapter(Context mContext, List<AlbumEntity> albumEntityList,String memberId) {
        this.mContext = mContext;
        this.albumEntityList = albumEntityList;
        this.memberId=memberId;
    }

    public void addNewData(List<AlbumEntity> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        albumEntityList.clear();
        albumEntityList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return albumEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder groupViewHolder = null;
        if (convertView == null) {
            groupViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_album_groupview, null);
            groupViewHolder.monthTv = (TextView) convertView.findViewById(R.id.album_month_tv);
            groupViewHolder.album_gridView = (GridView) convertView.findViewById(R.id.album_gridView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (ViewHolder) convertView.getTag();
        }
        AlbumEntity albumEntity = albumEntityList.get(position);
        groupViewHolder.monthTv.setText(getMonthContent(albumEntity.getMonth()));
        albumPhotoAdapter=new AlbumPhotoAdapter(albumEntity.getPhotoList());
        groupViewHolder.album_gridView.setAdapter(albumPhotoAdapter);
        return convertView;
    }
    static class ViewHolder {
        TextView monthTv;
        GridView album_gridView;
    }

    class AlbumPhotoAdapter extends BaseAdapter{
        private List<AlbumPhotoEntity> photoList;
        public AlbumPhotoAdapter(List<AlbumPhotoEntity> photoList){
            this.photoList=photoList;
        }

        @Override
        public int getCount() {
            return photoList.size();
        }

        @Override
        public Object getItem(int position) {
            return photoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NetworkImageView imageView=null;
            if(convertView==null){
                imageView=new NetworkImageView(mContext);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10,10,10,10);
                imageView.setLayoutParams(layoutParams);
                convertView=imageView;
                convertView.setTag(imageView);
            }else{
                imageView=(NetworkImageView)convertView.getTag();
            }
            VolleyUtil.initNetworkImageView(mContext, imageView, String.format(Constant.API_GET_PIC,
                    Constant.Module_preview_m, memberId, photoList.get(position).getFile_id()),
                    R.drawable.default_head_icon, R.drawable.default_head_icon);
            return convertView;
        }

    }

    private String getMonthContent(String monthData) {
        if ("1".endsWith(monthData) || "01".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_january);
        } else if ("2".endsWith(monthData) || "02".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_february);
        } else if ("3".endsWith(monthData) || "03".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_march);
        } else if ("4".endsWith(monthData) || "04".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_april);
        } else if ("5".endsWith(monthData) || "05".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_may);
        } else if ("6".endsWith(monthData) || "06".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_june);
        } else if ("7".endsWith(monthData) || "07".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_july);
        } else if ("8".endsWith(monthData) || "08".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_august);
        } else if ("9".endsWith(monthData) || "09".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_september);
        } else if ("10".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_october);
        } else if ("11".endsWith(monthData)) {
            return mContext.getString(R.string.text_month_november);
        } else {
            return mContext.getString(R.string.text_month_december);
        }
    }

}
