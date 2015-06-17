package com.madx.bwm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.FamilyGroupEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.PinYin4JUtil;
import com.madx.bwm.util.ToDc;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzemian on 15/6/15.
 */
public class FamilyGroupAdapter extends BaseAdapter implements Filterable {
    List<FamilyGroupEntity> groupList;
    private Context mContext;
    private PersonFilter filter;

    public FamilyGroupAdapter(Context context,List<FamilyGroupEntity> groupList) {
        this.groupList = groupList;
        this.mContext = context;
    }

    public void addData(List<FamilyGroupEntity> list) {
        if (null == list || list.size() == 0) {
            return;
        }
        groupList.clear();
        groupList.addAll(list);
        notifyDataSetChanged();
    }

    public List<FamilyGroupEntity> getGroupList() {
        return groupList;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FamilyGroupEntity familyGroupEntity = groupList.get(position);
        viewHolder.textName.setText(familyGroupEntity.getGroup_name());
        VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO,
                familyGroupEntity.getGroup_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PersonFilter(groupList);
        }
        return filter;
    }
    private class PersonFilter extends Filter{
        private List<FamilyGroupEntity> original;

        public PersonFilter(List<FamilyGroupEntity> groupList) {
            this.original = groupList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null && constraint.length() == 0){
                results.values = original;//原始数据
                results.count = original.size();
            }else {
                List<FamilyGroupEntity> mList = new ArrayList<>();
                String filterString = ToDc.ToDBC(constraint.toString().trim().toLowerCase());
                for(FamilyGroupEntity groupEntity : original){
                    String userName = PinYin4JUtil.getPinyinWithMark(groupEntity.getGroup_name());
                    if(-1 != userName.toLowerCase().indexOf(filterString)){
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
            groupList = (List<FamilyGroupEntity>)results.values;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        CircularNetworkImage imageMain;
        TextView textName;
    }
}
