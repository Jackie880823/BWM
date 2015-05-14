package com.madx.bwm.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.FamilyMemberEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.FamilyFragment;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by quankun on 15/5/12.
 */
public class MyFamilyAdapter extends BaseAdapter {
    private Context mContext;
    private List<FamilyMemberEntity> list;

    public MyFamilyAdapter(Context mContext, List<FamilyMemberEntity> list) {
        this.list = list;
        this.mContext = mContext;
    }

    public void addNewData(List<FamilyMemberEntity> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void addMoreData(List<FamilyMemberEntity> moreList) {
        if (null != moreList && moreList.size() > 0) {
            list.remove(list.size() - 1);
            for (FamilyMemberEntity familyMemberEntity : moreList) {
                if (!list.contains(familyMemberEntity)) {
                    list.add(familyMemberEntity);
                }
            }
            notifyDataSetChanged();
        }
    }

    public List<FamilyMemberEntity> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_for_myfamily, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.myfamily_image_main);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.myfamily_name);
            viewHolder.imageTop = (ImageView) convertView.findViewById(R.id.myfamily_image_top);
            viewHolder.imageRight = (ImageView) convertView.findViewById(R.id.myfamily_image_right);
            viewHolder.imageLeft = (ImageView) convertView.findViewById(R.id.myfamily_image_left);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FamilyMemberEntity familyMemberEntity = list.get(position);
        String userId = familyMemberEntity.getUser_id();
        viewHolder.imageTop.setVisibility(View.GONE);
        viewHolder.imageRight.setVisibility(View.GONE);
        viewHolder.imageLeft.setVisibility(View.GONE);
        // viewHolder.imageMain.setImageDrawable(null);
        viewHolder.imageMain.setImageBitmap(null);
        if (FamilyFragment.FAMILY_TREE.equals(userId)) {
            viewHolder.textName.setText(mContext.getString(R.string.text_family_tree));
            VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, null, R.drawable.family_tree, R.drawable.family_tree);
        } else if (FamilyFragment.FAMILY_MORE_MEMBER.equals(userId)) {
            viewHolder.textName.setText(familyMemberEntity.getUser_given_name());
            VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, null, R.drawable.family_tree, R.drawable.family_tree);

        } else if (FamilyFragment.FAMILY_HIDE_MEMBER.equals(userId)) {
            viewHolder.textName.setText(familyMemberEntity.getUser_given_name());
            VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, null, R.drawable.family_tree, R.drawable.family_tree);
        } else {
            viewHolder.textName.setText(familyMemberEntity.getUser_given_name());
            VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile,
                    familyMemberEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            //右上角指示是否为好友
            if ("0".equals(familyMemberEntity.getFam_accept_flag())) {
                viewHolder.imageTop.setVisibility(View.VISIBLE);
            }
            //右下角miss,滚动后会重新构建出来
            if ("".equals(familyMemberEntity.getMiss())) {
                viewHolder.imageRight.setVisibility(View.VISIBLE);
            }
            //左下角心情图标
            String dofeel_code = familyMemberEntity.getDofeel_code();
            if (!TextUtils.isEmpty(dofeel_code)) {
                viewHolder.imageLeft.setVisibility(View.VISIBLE);
                viewHolder.imageLeft.setImageDrawable(null);
                try {
                    String filePath = "";
                    if (dofeel_code.indexOf("_") != -1) {
                        filePath = dofeel_code.replaceAll("_", File.separator);
                    }
                    InputStream is = mContext.getAssets().open(filePath);
                    Drawable da = Drawable.createFromStream(is, null);
                    viewHolder.imageLeft.setImageDrawable(da);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return convertView;
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        ImageView imageLeft;
        ImageView imageRight;
        ImageView imageTop;
        TextView textName;
    }
}
