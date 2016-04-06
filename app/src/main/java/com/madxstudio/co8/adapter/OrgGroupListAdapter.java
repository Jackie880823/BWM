package com.madxstudio.co8.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.OrgGroupEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class OrgGroupListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<OrgGroupEntity> list;

    private PersonFilter filter;

    public OrgGroupListAdapter(Context mContext, List<OrgGroupEntity> memberList) {
        list = memberList;
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mContext = mContext;
    }

    public void addNewData(List<OrgGroupEntity> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void removeData(OrgGroupEntity groupEntity) {
        list.remove(groupEntity);
        notifyDataSetChanged();
    }

    public List<OrgGroupEntity> getList() {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.org_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.org_icon_image);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.tv_org_name);
            viewHolder.orgPosition = (TextView) convertView.findViewById(R.id.tv_org_position);
            viewHolder.orgAdmin = (TextView) convertView.findViewById(R.id.tv_org_admin);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrgGroupEntity memberEntity = list.get(position);
        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO, memberEntity.getGroup_id()), R.drawable.b2be_normal, R.drawable.b2be_normal);
        viewHolder.memberName.setText(memberEntity.getGroup_name());
        viewHolder.orgPosition.setText(String.format(mContext.getString(R.string.text_members_num),memberEntity.getTotal_member()));
        viewHolder.orgAdmin.setText("");
        return convertView;
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView orgPosition;
        TextView orgAdmin;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PersonFilter(list);
        }
        return filter;
    }

    //自定义Filer类
    private class PersonFilter extends Filter {
        private List<OrgGroupEntity> original;

        public PersonFilter(List<OrgGroupEntity> groupList) {
            this.original = groupList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.values = original;//原始数据
                results.count = original.size();
            } else {
                List<OrgGroupEntity> mList = new ArrayList<>();
                String filterString = MyTextUtil.ToDBC(constraint.toString().trim().toLowerCase());
                for (OrgGroupEntity groupEntity : original) {
                    String userName = PinYin4JUtil.getPinyinWithMark(groupEntity.getGroup_name());
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
            list = (List<OrgGroupEntity>) results.values;
            if (list == null) {
                list = new ArrayList<>();
            }
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