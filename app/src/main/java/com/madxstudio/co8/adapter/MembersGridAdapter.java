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
 * Created by wing on 15/2/11.
 */
public class MembersGridAdapter extends BaseAdapter {

    private List<UserEntity> mUserList;
    private Context mContext;

    public MembersGridAdapter(Context context,List<UserEntity> userList){
        this.mUserList = userList;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

        ViewHolder viewHolder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_item_for_grid, null);
            viewHolder.iv_friend_head = (CircularNetworkImage) convertView.findViewById(R.id.iv_friend_head);
            viewHolder.tv_friend_name = (TextView) convertView.findViewById(R.id.tv_friend_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserEntity user = mUserList.get(position);
        viewHolder.tv_friend_name.setText(user.getUser_given_name());
        BitmapTools.getInstance(mContext).display(viewHolder.iv_friend_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, user.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);

        return convertView;
    }

    class ViewHolder{
        CircularNetworkImage iv_friend_head;
        TextView tv_friend_name;
    }



}
