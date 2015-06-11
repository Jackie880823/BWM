package com.madx.bwm.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.GroupMessageEntity;
import com.madx.bwm.entity.PrivateMessageEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/1/30.
 */
public class MessageGroupFragmentAdapter extends BaseAdapter {
    private Context mContext;

    List<GroupMessageEntity> mGroupList;

    public MessageGroupFragmentAdapter(Context context, List<GroupMessageEntity> mGroupList) {
        mContext = context;
        this.mGroupList = mGroupList;
    }

    public List<GroupMessageEntity> getmGroupList() {
        return mGroupList;
    }

    public void setmGroupList(List<GroupMessageEntity> mGroupList) {
        if (null != mGroupList) {
            mGroupList.clear();
        }
        this.mGroupList = mGroupList;
        notifyDataSetChanged();
    }

    public void AddGroupEntityData(List<GroupMessageEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            for (GroupMessageEntity userEntity : userEntityList) {
                if (!mGroupList.contains(userEntity)) {
                    mGroupList.add(userEntity);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void NewGroupEntityData(List<GroupMessageEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            mGroupList.clear();
            mGroupList.addAll(userEntityList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_message_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.message_group_icon_image);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.group_message_name);
            viewHolder.lastMessageContent = (TextView) convertView.findViewById(R.id.last_message_content);
            viewHolder.lastMessageTime = (TextView) convertView.findViewById(R.id.last_message_date);
            viewHolder.lastMessageName = (TextView) convertView.findViewById(R.id.last_message_send_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        GroupMessageEntity groupEntity = mGroupList.get(position);
        VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO, groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.groupName.setText(groupEntity.getGroup_name());
        viewHolder.lastMessageTime.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, groupEntity.getGroup_active_date()));
        String type = groupEntity.getType();
        if (PrivateMessageEntity.POST_TEXT.equals(type)) {
            viewHolder.lastMessageContent.setText(groupEntity.getMessage());
        } else if (PrivateMessageEntity.POST_STICKER.equals(type)) {
            viewHolder.lastMessageContent.setText(mContext.getString(R.string.text_message_chat_post_text));
        } else if (PrivateMessageEntity.POST_PHOTO.equals(type)) {
            viewHolder.lastMessageContent.setText(mContext.getString(R.string.text_message_chat_post_photo));
        } else if (PrivateMessageEntity.POST_LOCATION.equals(type)) {
            viewHolder.lastMessageContent.setText(mContext.getString(R.string.text_message_chat_post_location));
        }else{
            viewHolder.lastMessageContent.setText("");
        }
        viewHolder.lastMessageName.setText(groupEntity.getMember_name());
        int noReadNews = 0;
        try {
            noReadNews = Integer.parseInt(groupEntity.getUnread().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (noReadNews > 0) {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            if (noReadNews < 100) {
                viewHolder.tvNum.setText(groupEntity.getUnread().toString());
            } else {
                viewHolder.tvNum.setText("99+");
            }
        } else {
            viewHolder.tvNum.setVisibility(View.GONE);
        }


        return convertView;
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        TextView groupName;
        TextView lastMessageTime;
        TextView lastMessageContent;
        TextView lastMessageName;

        TextView tvNum;
    }
}