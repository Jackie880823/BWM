package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.AlbumEntity;
import com.madx.bwm.entity.AlbumPhotoEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.ViewOriginalPicesActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/5/19.
 */
public class AlbumAdapter extends BaseAdapter {
    private List<AlbumEntity> albumEntityList;
    private Context mContext;
    private String memberId;

    public AlbumAdapter(Context mContext, List<AlbumEntity> albumList, String memberId) {
        this.mContext = mContext;
        this.albumEntityList = albumList;
        this.memberId = memberId;
    }

    public void addNewData(List<AlbumEntity> list) {
        albumEntityList.clear();
        if (null != list && list.size() > 0) {
            albumEntityList.addAll(list);
        }
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_album_gallery, null);
            viewHolder.monthTv = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.image1 = (NetworkImageView) convertView.findViewById(R.id.ib_1);
            viewHolder.image2 = (NetworkImageView) convertView.findViewById(R.id.ib_2);
            viewHolder.image3 = (NetworkImageView) convertView.findViewById(R.id.ib_3);
            viewHolder.image4 = (NetworkImageView) convertView.findViewById(R.id.ib_4);
            viewHolder.image5 = (NetworkImageView) convertView.findViewById(R.id.ib_5);
            viewHolder.image6 = (NetworkImageView) convertView.findViewById(R.id.ib_6);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AlbumEntity albumEntity = albumEntityList.get(position);
        List<AlbumPhotoEntity> photoList = albumEntity.getPhotoList();
        String month = albumEntity.getMonth();
        String year = albumEntity.getYear();
        String total = albumEntity.getTotal();
        if (photoList != null && photoList.size() > 0) {
            viewHolder.monthTv.setText(getMonthContent(month));
            if (photoList.size() > 5) {
                setNetworkImage(viewHolder.image6, photoList.get(5).getFile_id());
                viewHolder.image6.setOnClickListener(new ImageViewClick(year, month, photoList.get(5).getFile_id(), total));
            }
            if (photoList.size() > 4) {
                setNetworkImage(viewHolder.image5, photoList.get(4).getFile_id());
                viewHolder.image5.setOnClickListener(new ImageViewClick(year, month, photoList.get(4).getFile_id(), total));
            }
            if (photoList.size() > 3) {
                setNetworkImage(viewHolder.image4, photoList.get(3).getFile_id());
                viewHolder.image4.setOnClickListener(new ImageViewClick(year, month, photoList.get(3).getFile_id(), total));
            }
            if (photoList.size() > 2) {
                setNetworkImage(viewHolder.image3, photoList.get(2).getFile_id());
                viewHolder.image3.setOnClickListener(new ImageViewClick(year, month, photoList.get(2).getFile_id(), total));
            }
            if (photoList.size() > 1) {
                setNetworkImage(viewHolder.image2, photoList.get(1).getFile_id());
                viewHolder.image2.setOnClickListener(new ImageViewClick(year, month, photoList.get(1).getFile_id(), total));
            }
            setNetworkImage(viewHolder.image1, photoList.get(0).getFile_id());
        }
        viewHolder.image1.setOnClickListener(new ImageViewClick(year, month, photoList.get(0).getFile_id(), total));
        return convertView;
    }

    class ImageViewClick implements View.OnClickListener {
        private String fileId;
        private String year;
        private String month;
        private String total;
        private String contentId;

        public ImageViewClick(String year, String month, String fileId, String total) {
            this.fileId = fileId;
            this.year = year;
            this.month = month;
            this.total = total;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_1:
                    jumpToMoreAlbum(year, month, fileId, total);
                    break;
                case R.id.ib_2:
                    jumpToMoreAlbum(year, month, fileId, total);
                    break;
                case R.id.ib_3:
                    jumpToMoreAlbum(year, month, fileId, total);
                    break;
                case R.id.ib_4:
                    jumpToMoreAlbum(year, month, fileId, total);
                    break;
                case R.id.ib_5:
                    jumpToMoreAlbum(year, month, fileId, total);
                    break;
                case R.id.ib_6:
                    jumpToMoreAlbum(year, month, fileId, total);
                    break;
            }
        }
    }

    private void jumpToMoreAlbum(String year, String month, String fileId, String total) {
//        Intent intent = new Intent(mContext, AlbumDetailActivity.class);
//        intent.putExtra("year", year);
//        intent.putExtra("month", month);
//        intent.putExtra("memberId", memberId);
//        intent.putExtra("fileId", fileId);
//        mContext.startActivity(intent);
        HashMap<String, String> params = new HashMap<>();
        params.put("viewer_id", MainActivity.getUser().getUser_id());
        params.put("member_id", memberId);
        params.put("year", year);
        params.put("month", month);
        params.put("start", "0");
        params.put("limit", total);
        String url = UrlUtil.generateUrl(Constant.API_GET_MONTH_ALBUM_LIST, params);
        Intent intent = new Intent(mContext, ViewOriginalPicesActivity.class);
        intent.putExtra("request_url", url);
        intent.putExtra("memberId", memberId);
        mContext.startActivity(intent);
    }

    private void setNetworkImage(NetworkImageView imageView, String fileId) {
        VolleyUtil.initNetworkImageView(mContext, imageView, String.format(Constant.API_GET_PIC,
                        Constant.Module_preview_m, memberId, fileId),
                R.drawable.network_image_default, R.drawable.network_image_default);
    }

    static class ViewHolder {
        TextView monthTv;
        NetworkImageView image1;
        NetworkImageView image2;
        NetworkImageView image3;
        NetworkImageView image4;
        NetworkImageView image5;
        NetworkImageView image6;
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
