package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.UserEntity;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/4/17.
 */
public class MessageGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserEntity> mUserEntityList;
    public static final int PAGE_SIZE = 8; // 每一屏幕显示28 Button

    public MessageGridViewAdapter(Context mContext, List<UserEntity> userEntityList, int page) {
        this.mContext = mContext;
        mUserEntityList = new ArrayList<UserEntity>();
        int i = page * PAGE_SIZE;
        int end = i + PAGE_SIZE;
        while ((i < userEntityList.size()) && (i < end)) {
            mUserEntityList.add(userEntityList.get(i));
            i++;
        }
    }

    @Override
    public int getCount() {

        return mUserEntityList.size();
    }

    @Override
    public Object getItem(int position) {

        return mUserEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_gridview_item, null);

            viewHolder = new ViewHolder();

            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.message_member_image_main);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.message_member_name);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserEntity userEntity = mUserEntityList.get(position);

        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.memberName.setText(userEntity.getUser_given_name());
        if ((Integer.parseInt(userEntity.getUnread().toString()) > 0) && (Integer.parseInt(userEntity.getUnread().toString())) < 100) {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText(userEntity.getUnread().toString());
        } else if (Integer.parseInt(userEntity.getUnread().toString()) > 99) {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText("99+");
        }

        return convertView;
    }


    class ViewHolder {
        CircularNetworkImage imageMain;


        TextView memberName;

        TextView tvNum;
    }
}