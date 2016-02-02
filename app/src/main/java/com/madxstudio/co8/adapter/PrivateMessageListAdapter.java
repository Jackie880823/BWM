package com.madxstudio.co8.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class PrivateMessageListAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserEntity> mUserEntityList;

    public PrivateMessageListAdapter(Context mContext, List<UserEntity> userEntityList) {
        this.mContext = mContext;
        mUserEntityList = userEntityList;
    }

    public void AddUserEntityData(List<UserEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            for (UserEntity userEntity : userEntityList) {
                if (!mUserEntityList.contains(userEntity)) {
                    mUserEntityList.add(userEntity);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void NewUserEntityData(List<UserEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            mUserEntityList.clear();
            mUserEntityList.addAll(userEntityList);
            notifyDataSetChanged();
        }
    }

    public List<UserEntity> getmUserEntityList() {
        return mUserEntityList;
    }

    public void setmUserEntityList(List<UserEntity> mUserEntityList) {
        this.mUserEntityList = mUserEntityList;
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
        int messageNum = 0;
        try {
            messageNum = Integer.parseInt(userEntity.getUnread().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (messageNum > 0 && messageNum < 100) {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText(userEntity.getUnread().toString());
        } else if (messageNum > 99) {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText("99+");
        } else {
            viewHolder.tvNum.setVisibility(View.GONE);
        }

        return convertView;
    }


    class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView tvNum;
    }
}