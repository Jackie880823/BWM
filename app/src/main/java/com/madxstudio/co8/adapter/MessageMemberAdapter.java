package com.madxstudio.co8.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/1/31.
 */
public class MessageMemberAdapter extends ArrayAdapter{
    private Context mContext;
    private int resourceId;
    private List<UserEntity> memberList;

    public MessageMemberAdapter(Context context, int gridViewResource, List<UserEntity> memberList) {
        super(context, gridViewResource, memberList);
        this.mContext = context;
        this.resourceId = gridViewResource;
        this.memberList = memberList;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();

            viewHolder.imageMain = (CircularNetworkImage)convertView.findViewById(R.id.message_member_image_main);
            viewHolder.memberName = (TextView)convertView.findViewById(R.id.message_member_name);
            viewHolder.tvNum = (TextView)convertView.findViewById(R.id.tv_num);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserEntity userEntity = memberList.get(position);

        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.memberName.setText(userEntity.getUser_given_name());
        if ((Integer.parseInt(userEntity.getUnread().toString()) > 0) && (Integer.parseInt(userEntity.getUnread().toString())) < 100)
        {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText(userEntity.getUnread().toString());
        }
        else if(Integer.parseInt(userEntity.getUnread().toString()) > 99)
        {
            viewHolder.tvNum.setVisibility(View.VISIBLE);
            viewHolder.tvNum.setText("99");
        }

        viewHolder.imageMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memberClickListener != null) {
                    memberClickListener.memberClick(memberList.get(position));
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    class ViewHolder
    {
        CircularNetworkImage imageMain;


        TextView memberName;

        TextView tvNum;
    }

    private MemberClickListener memberClickListener;

    public void setMemberClickListener(MemberClickListener memberClickListener){
        this.memberClickListener = memberClickListener;
    }

    public interface MemberClickListener{
        void memberClick(UserEntity userEntity);
    }
}

