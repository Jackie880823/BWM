package com.madxstudio.co8.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.PrivateMessageEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class MessageListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<PrivateMessageEntity> mUserEntityList;
    private PersonFilter filter;

    public MessageListAdapter(Context mContext, List<PrivateMessageEntity> userEntityList) {
        this.mContext = mContext;
        mUserEntityList = userEntityList;
    }

    public void addNewData(List<PrivateMessageEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            mUserEntityList.clear();
            mUserEntityList.addAll(userEntityList);
            notifyDataSetChanged();
        }
    }

    public void addData(List<PrivateMessageEntity> userEntityList) {
        if (null != userEntityList && userEntityList.size() > 0) {
            mUserEntityList.addAll(userEntityList);
            notifyDataSetChanged();
        }
    }


    public List<PrivateMessageEntity> getmUserEntityList() {
        return mUserEntityList;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_main_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.message_icon_image);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.message_name);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
            viewHolder.lastMessage = (TextView) convertView.findViewById(R.id.last_message_content);
            viewHolder.lastData = (TextView) convertView.findViewById(R.id.last_message_date);
            viewHolder.lastMessageName = (TextView) convertView.findViewById(R.id.last_message_send_name);
            viewHolder.groupSign = (ImageView) convertView.findViewById(R.id.message_group_sign);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PrivateMessageEntity userEntity = mUserEntityList.get(position);
        if ("group".equals(userEntity.getMessage_type())) {
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()),
                    R.drawable.b2be_normal, R.drawable.b2be_normal);
            viewHolder.lastMessageName.setText(userEntity.getMember_name());
            viewHolder.groupSign.setVisibility(View.VISIBLE);
            viewHolder.memberName.setText(userEntity.getGroup_name());
        } else {
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()),
                    R.drawable.default_head_icon, R.drawable.default_head_icon);
            viewHolder.lastMessageName.setText("");
            viewHolder.groupSign.setVisibility(View.GONE);
            viewHolder.memberName.setText(userEntity.getUser_given_name());
        }

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
        String type = userEntity.getType();
        if (PrivateMessageEntity.POST_TEXT.equals(type)) {
            viewHolder.lastMessage.setText(userEntity.getMessage());
        } else if (PrivateMessageEntity.POST_STICKER.equals(type)) {
            viewHolder.lastMessage.setText(mContext.getString(R.string.text_message_chat_post_text));
        } else if (PrivateMessageEntity.POST_PHOTO.equals(type)) {
            viewHolder.lastMessage.setText(mContext.getString(R.string.text_message_chat_post_photo));
        } else if (PrivateMessageEntity.POST_LOCATION.equals(type)) {
            viewHolder.lastMessage.setText(mContext.getString(R.string.text_message_chat_post_location));
        } else if (PrivateMessageEntity.POST_AUDIO.equals(type)) {
            viewHolder.lastMessage.setText(R.string.text_message_chat_post_audio);
        } else if (PrivateMessageEntity.POST_VIDEO.equals(type)) {
            viewHolder.lastMessage.setText(R.string.text_message_chat_post_video);
        } else {
            viewHolder.lastMessage.setText(userEntity.getMessage());
        }
        viewHolder.lastData.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, userEntity.getGroup_active_date()));
        return convertView;
    }


    class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView lastMessage;
        TextView lastData;
        TextView lastMessageName;
        ImageView groupSign;
        TextView tvNum;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PersonFilter(mUserEntityList);
        }
        return filter;
    }

    //自定义Filer类
    private class PersonFilter extends Filter {
        private List<PrivateMessageEntity> original;

        public PersonFilter(List<PrivateMessageEntity> groupList) {
            this.original = groupList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.values = original;//原始数据
                results.count = original.size();
            } else {
                List<PrivateMessageEntity> mList = new ArrayList<>();
                String filterString = MyTextUtil.ToDBC(constraint.toString().trim().toLowerCase());
                for (PrivateMessageEntity groupEntity : original) {
                    String userName = PinYin4JUtil.getPinyinWithMark(groupEntity.getUser_given_name());
                    if (-1 != userName.toLowerCase().indexOf(filterString)) {
                        mList.add(groupEntity);
                    }
                }
                results.values = mList;
                results.count = mList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mUserEntityList = (List<PrivateMessageEntity>) results.values;
            notifyDataSetChanged();
            if (results.count == 0) {
                if (showData != null)
                    showData.showFoundData(constraint.toString());
            } else {
                if (showData != null)
                    showData.showRefreshLayout();
            }
        }
    }

    private NoFoundDataListener showData;

    public void showNoData(NoFoundDataListener showData) {
        this.showData = showData;
    }
}