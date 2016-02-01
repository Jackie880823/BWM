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
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class MessagePrivateListAdapter extends BaseAdapter {

    private Context mContext;
    private List<PrivateMessageEntity> mUserEntityList;

    public MessagePrivateListAdapter(Context mContext, List<PrivateMessageEntity> userEntityList) {
        this.mContext = mContext;
        mUserEntityList = userEntityList;
    }

    public void AddUserEntityData(List<PrivateMessageEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            for (PrivateMessageEntity userEntity : userEntityList) {
                if (!mUserEntityList.contains(userEntity)) {
                    mUserEntityList.add(userEntity);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void NewUserEntityData(List<PrivateMessageEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            mUserEntityList.clear();
            mUserEntityList.addAll(userEntityList);
            notifyDataSetChanged();
        }
    }

    public List<PrivateMessageEntity> getmUserEntityList() {
        return mUserEntityList;
    }

    public void setmUserEntityList(List<PrivateMessageEntity> mUserEntityList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_member_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.message_private_icon_image);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.private_last_message_name);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
            viewHolder.lastMessage= (TextView) convertView.findViewById(R.id.last_message_content);
            viewHolder.lastData=(TextView) convertView.findViewById(R.id.last_message_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PrivateMessageEntity userEntity = mUserEntityList.get(position);

        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
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
        String type=userEntity.getType();
        if(PrivateMessageEntity.POST_TEXT.equals(type)){
            viewHolder.lastMessage.setText(userEntity.getMessage());
        }else if(PrivateMessageEntity.POST_STICKER.equals(type)){
            viewHolder.lastMessage.setText(mContext.getString(R.string.text_message_chat_post_text));
        }else if(PrivateMessageEntity.POST_PHOTO.equals(type)){
            viewHolder.lastMessage.setText(mContext.getString(R.string.text_message_chat_post_photo));
        }else if(PrivateMessageEntity.POST_LOCATION.equals(type)){
            viewHolder.lastMessage.setText(mContext.getString(R.string.text_message_chat_post_location));
        }else if(PrivateMessageEntity.POST_AUDIO.equals(type)){
            viewHolder.lastMessage.setText(R.string.text_message_chat_post_audio);
        }else if(PrivateMessageEntity.POST_VIDEO.equals(type)){
            viewHolder.lastMessage.setText(R.string.text_message_chat_post_video);
        }else{
            viewHolder.lastMessage.setText("");
        }
        viewHolder.lastData.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, userEntity.getGroup_active_date()));
        return convertView;
    }


    class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView lastMessage;
        TextView lastData;
        TextView tvNum;
    }
}