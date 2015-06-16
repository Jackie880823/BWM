package com.madx.bwm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.FamilyMemberEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.PinYin4JUtil;
import com.madx.bwm.util.ToDc;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/19.
 */
public class InviteMemberAdapter extends BaseAdapter implements Filterable {
    private List<FamilyMemberEntity> list;
    private List<FamilyMemberEntity> serchList;
    private Context mContext;
    private List<String> stringList;
    private PersonFilter filter;

    public InviteMemberAdapter(Context mContext, List<FamilyMemberEntity> list, List<String> stringList) {
        this.mContext = mContext;
        this.list = list;
        this.stringList = stringList;
    }

    public List<FamilyMemberEntity> getList() {
        return list;
    }

    public void setSerchList(List<FamilyMemberEntity> list){
        this.serchList = list;
    }
    public void addNewData(List<FamilyMemberEntity> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void addSelectData(String userId) {
        if (!stringList.contains(userId)) {
            stringList.add(userId);
            notifyDataSetChanged();
        }
    }

    public void removeSelectData(String userId) {
        if (stringList.contains(userId)) {
            stringList.remove(userId);
            notifyDataSetChanged();
        }
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_for_creategroup, null);
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.creategroup_image_main);
            viewHolder.imageRight = (CheckBox) convertView.findViewById(R.id.creategroup_image_right);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.creategroup_name);
            viewHolder.imageTop = (ImageView) convertView.findViewById(R.id.creategroup_image_top);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FamilyMemberEntity memberEntity = list.get(position);
        VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile,
                memberEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
        String userId = memberEntity.getUser_id();
        viewHolder.textName.setText(memberEntity.getUser_given_name());
        if ("0".equals(memberEntity.getFam_accept_flag())) {
            viewHolder.imageTop.setVisibility(View.VISIBLE);
            viewHolder.imageRight.setVisibility(View.GONE);
        } else {
            viewHolder.imageTop.setVisibility(View.GONE);
            viewHolder.imageRight.setVisibility(View.VISIBLE);
        }
        if (null != stringList && stringList.contains(userId)) {
            viewHolder.imageRight.setChecked(true);
        } else {
            viewHolder.imageRight.setChecked(false);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PersonFilter(serchList);
        }
        return filter;
    }
    private class PersonFilter extends Filter{

        private List<FamilyMemberEntity> original;
        public PersonFilter(List<FamilyMemberEntity> list) {
            this.original = list;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0){
                results.values = original;//原始数据
                results.count = original.size();
            }else {
                List<FamilyMemberEntity> mList = new ArrayList<FamilyMemberEntity>();
                String filterString = ToDc.ToDBC(constraint.toString().trim().toLowerCase());
                for(FamilyMemberEntity memberEntity : original){
                    String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getUser_given_name());
                    if(-1 != userName.toLowerCase().indexOf(filterString)){
                        mList.add(memberEntity);
                    }
                }
                results.values = mList;
                results.count = mList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (List<FamilyMemberEntity>)results.values;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        CheckBox imageRight;
        ImageView imageTop;
        TextView textName;
    }
}
