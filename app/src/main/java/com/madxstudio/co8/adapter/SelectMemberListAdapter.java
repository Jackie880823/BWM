package com.madxstudio.co8.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.OrgMemberEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class SelectMemberListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<OrgMemberEntity> list;
    private List<OrgMemberEntity> serachList;

    private PersonFilter filter;
    private List<String> selectList = new ArrayList<>();

    public SelectMemberListAdapter(Context mContext, List<OrgMemberEntity> memberList) {
        list = memberList;
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mContext = mContext;
    }

    public void addNewData(List<OrgMemberEntity> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void addSelectData(String userId) {
        if (!selectList.contains(userId)) {
            selectList.add(userId);
            notifyDataSetChanged();
        }
    }

    public void removeSelectData(String userId) {
        if (selectList.contains(userId)) {
            selectList.remove(userId);
            notifyDataSetChanged();
        }
    }

    public void removeAllSelectData() {
        selectList.clear();
        notifyDataSetChanged();
    }

    public void addAllSelectData() {
        selectList.clear();
        if (list != null && list.size() > 0) {
            for (OrgMemberEntity memberEntity : list) {
                selectList.add(memberEntity.getUser_id());
            }
        }
        notifyDataSetChanged();
    }

    public List<OrgMemberEntity> getList() {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.select_member_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.org_icon_image);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.tv_org_name);
            viewHolder.orgPosition = (TextView) convertView.findViewById(R.id.tv_org_position);
            viewHolder.selectMemberCheckBox = (CheckBox) convertView.findViewById(R.id.check_member_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrgMemberEntity memberEntity = list.get(position);
        String userId = memberEntity.getUser_id();
        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userId), R.drawable.default_head_icon, R.drawable.default_head_icon);
        viewHolder.memberName.setText(memberEntity.getUser_given_name());
        viewHolder.orgPosition.setText(memberEntity.getPosition());
        if (selectList.contains(userId)) {
            viewHolder.selectMemberCheckBox.setChecked(true);
        } else {
            viewHolder.selectMemberCheckBox.setChecked(false);
        }
        return convertView;
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView orgPosition;
        CheckBox selectMemberCheckBox;
    }

    public void setSerach(List<OrgMemberEntity> list) {
        this.serachList = list;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PersonFilter(serachList);
        }
        return filter;
    }

    //自定义Filer类
    private class PersonFilter extends Filter {

        private List<OrgMemberEntity> original;

        public PersonFilter(List<OrgMemberEntity> list) {
            this.original = list;
        }

        //该方法在子线程中执行
        //自定义的过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.values = original;//原始数据
                results.count = original.size();
            } else {
                List<OrgMemberEntity> mList = new ArrayList<OrgMemberEntity>();
                String filterString = MyTextUtil.ToDBC(constraint.toString().trim().toLowerCase());
                for (OrgMemberEntity memberEntity : original) {
                    String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getUser_given_name());
                    if (-1 != userName.toLowerCase().indexOf(filterString)) {
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
            list = (List<OrgMemberEntity>) results.values;
            if (list == null) {
                list = new ArrayList<>();
            }
            notifyDataSetChanged();
            if (results.count == 0) {
                if (showData != null && !TextUtils.isEmpty(constraint))
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