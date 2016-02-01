package com.madxstudio.co8.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.PhotoEntity;

import java.util.List;

/**
 * Created by christepherzhang on 15/4/9.
 */
public class AlbumGalleryAdapter extends ArrayAdapter {

    private  int resourceId;
    private Context mContext;
    private String mMemberId;

    public List<List<PhotoEntity>> mListData ;//经过处理后: 每个List = 每个月份所有照片

    public AlbumGalleryAdapter(Context context, int listViewResourceId, List<List<PhotoEntity>> listData, String memberId) {
        super(context, listViewResourceId);
        resourceId = listViewResourceId;
        mContext = context;
        mListData = listData;
        mMemberId = memberId;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

        viewHolder = new ViewHolder();

        viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
        viewHolder.image = new NetworkImageView[6];
        viewHolder.image[0] = (NetworkImageView)convertView.findViewById(R.id.ib_1);
        viewHolder.image[1] = (NetworkImageView)convertView.findViewById(R.id.ib_2);
        viewHolder.image[2] = (NetworkImageView)convertView.findViewById(R.id.ib_3);
        viewHolder.image[3] = (NetworkImageView)convertView.findViewById(R.id.ib_4);
        viewHolder.image[4] = (NetworkImageView)convertView.findViewById(R.id.ib_5);
        viewHolder.image[5] = (NetworkImageView)convertView.findViewById(R.id.ib_6);

        List<PhotoEntity> mData = mListData.get(position);

        viewHolder.tvTime.setText(mData.get(0).getCreation_date());

        if(mData.size() > 7)
        {
            for (int i = 0; i < 6; i++) {
                BitmapTools.getInstance(mContext).display(viewHolder.image[i], String.format(Constant.API_GET_PIC, Constant.Module_preview_m, mMemberId, mData.get(i).getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }
        }
        else
        {
            for (int i = 0; i < mData.size(); i++) {
                BitmapTools.getInstance(mContext).display(viewHolder.image[i], String.format(Constant.API_GET_PIC, Constant.Module_preview_m, mMemberId, mData.get(i).getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }
        }


        return convertView;
    }


    class ViewHolder {
        TextView tvTime;
        NetworkImageView image[];
    }
}
