package com.madxstudio.co8.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.widget.WallEditView;

/**
 * Created 10/23/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class HeadHolder extends RecyclerView.ViewHolder {

    /**
     * Diary详情
     */
    public WallEditView wevContent;

    /**
     * 显示已经选择了的心情视图
     */
    public ImageView iv_feeling;

    /**
     * 地址描述
     */
    public TextView tvLocationDesc;

    public ImageView ivHead;
    public TextView tvUserName;

    public HeadHolder(View itemView) {
        super(itemView);
        //头部分
        UserEntity owner = MainActivity.getUser();
        ivHead = (ImageView) itemView.findViewById(R.id.owner_head);
        tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
        tvUserName.setText(owner.getUser_given_name());

        // 显示的列表
        iv_feeling = (ImageView) itemView.findViewById(R.id.iv_feeling);
        tvLocationDesc = (TextView) itemView.findViewById(R.id.location_desc);
        wevContent = (WallEditView) itemView.findViewById(R.id.diary_edit_content);
    }
}
