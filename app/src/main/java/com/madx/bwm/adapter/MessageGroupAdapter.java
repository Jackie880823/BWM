package com.madx.bwm.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/1/30.
 */
public class MessageGroupAdapter extends ArrayAdapter {

    private int resourceId;
    private Context mContext;

    List<GroupEntity> mGroupList;

    public MessageGroupAdapter(Context context, int listViewResourceId, List<GroupEntity> groupList) {
        super(context, listViewResourceId);
        resourceId = listViewResourceId;
        mContext = context;
        mGroupList = groupList;
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;



//            /*又试viewHolder出现问题。*/
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
//
//            viewHolder = new ViewHolder();
//
//            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.message_listview_image);
//
//
//            viewHolder.image = new CircularNetworkImage[8];
//
//            viewHolder.image[0] = (CircularNetworkImage) convertView.findViewById(R.id.image1);
//            viewHolder.image[1] = (CircularNetworkImage) convertView.findViewById(R.id.image2);
//            viewHolder.image[2] = (CircularNetworkImage) convertView.findViewById(R.id.image3);
//            viewHolder.image[3] = (CircularNetworkImage) convertView.findViewById(R.id.image4);
//            viewHolder.image[4] = (CircularNetworkImage) convertView.findViewById(R.id.image5);
//            viewHolder.image[5] = (CircularNetworkImage) convertView.findViewById(R.id.image6);
//            viewHolder.image[6] = (CircularNetworkImage) convertView.findViewById(R.id.image7);
//            viewHolder.image[7] = (CircularNetworkImage) convertView.findViewById(R.id.image8);
//
//            viewHolder.groupName = (TextView) convertView.findViewById(R.id.groupName);
//            viewHolder.msg = (TextView) convertView.findViewById(R.id.groupMessage);
//            viewHolder.time = (TextView) convertView.findViewById(R.id.groupTime);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        GroupEntity groupEntity = mGroupList.get(position);
//
//        List<UserEntity> mMemberList = mGroupList.get(position).getMember();
//
//        VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO, groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//
//        int count = mMemberList.size() < 8 ? mMemberList.size() : 8;
//
//        for (int i = 0; i < count; i++) {
//            VolleyUtil.initNetworkImageView(mContext, viewHolder.image[i], String.format(Constant.API_GET_PHOTO, Constant.Module_profile_s, mMemberList.get(i).getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//        }
//
//        viewHolder.groupName.setText(groupEntity.getGroup_name());
//        viewHolder.time.setText(groupEntity.getGroup_active_date());
//        viewHolder.msg.setText("还没有数据啊啊啊！！！");



        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
        }
        else
        {
            return convertView;
        }





//        convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

        viewHolder = new ViewHolder();

        viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.message_listview_image);
        viewHolder.tvNum = (TextView)convertView.findViewById(R.id.tv_num);

        viewHolder.image = new CircularNetworkImage[8];

        viewHolder.image[0] = (CircularNetworkImage) convertView.findViewById(R.id.image1);
        viewHolder.image[1] = (CircularNetworkImage) convertView.findViewById(R.id.image2);
        viewHolder.image[2] = (CircularNetworkImage) convertView.findViewById(R.id.image3);
        viewHolder.image[3] = (CircularNetworkImage) convertView.findViewById(R.id.image4);
        viewHolder.image[4] = (CircularNetworkImage) convertView.findViewById(R.id.image5);
        viewHolder.image[5] = (CircularNetworkImage) convertView.findViewById(R.id.image6);
        viewHolder.image[6] = (CircularNetworkImage) convertView.findViewById(R.id.image7);
        viewHolder.image[7] = (CircularNetworkImage) convertView.findViewById(R.id.image8);

        viewHolder.groupName = (TextView) convertView.findViewById(R.id.groupName);
        viewHolder.msg = (TextView) convertView.findViewById(R.id.groupMessage);
        viewHolder.time = (TextView) convertView.findViewById(R.id.groupTime);

        GroupEntity groupEntity = mGroupList.get(position);

        List<UserEntity> mMemberList = mGroupList.get(position).getMember();

        VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO, groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        int count = mMemberList.size() < 8 ? mMemberList.size() : 8;

        for (int i = 0; i < count; i++) {
            VolleyUtil.initNetworkImageView(mContext, viewHolder.image[i], String.format(Constant.API_GET_PHOTO, Constant.Module_profile_s, mMemberList.get(i).getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        }

        viewHolder.groupName.setText(groupEntity.getGroup_name());
        viewHolder.time.setText(MyDateUtils.getLocalDateStringFromUTC(mContext,groupEntity.getGroup_active_date()));
        viewHolder.msg.setText(groupEntity.getUser_given_name());

        if ((Integer.parseInt(groupEntity.getUnread().toString()) > 0) && (Integer.parseInt(groupEntity.getUnread().toString())) < 100)
        {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText(groupEntity.getUnread().toString());
        }
        else if(Integer.parseInt(groupEntity.getUnread().toString()) > 99)
        {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText("99");
        }


        return convertView;
    }

    class ViewHolder {
        CircularNetworkImage imageMain;
        CircularNetworkImage image[];
        TextView groupName;
        TextView time;
        TextView msg;

        TextView tvNum;
    }
}