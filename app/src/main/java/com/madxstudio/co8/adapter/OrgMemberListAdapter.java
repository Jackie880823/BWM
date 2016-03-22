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
import com.madxstudio.co8.entity.FamilyMemberEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class OrgMemberListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<FamilyMemberEntity> list;
    private List<FamilyMemberEntity> serachList;

    private PersonFilter filter;
    private String transmitData;

    public OrgMemberListAdapter(Context mContext, List<FamilyMemberEntity> memberList, String transmitData) {
        list = memberList;
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mContext = mContext;
        this.transmitData = transmitData;
    }

    public void addNewData(List<FamilyMemberEntity> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.org_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.org_icon_image);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.tv_org_name);
            viewHolder.orgPosition = (TextView) convertView.findViewById(R.id.tv_org_position);
            viewHolder.orgAdmin = (TextView) convertView.findViewById(R.id.tv_org_admin);
            viewHolder.meLine = convertView.findViewById(R.id.view_line);
            viewHolder.orgRequest = (ImageView) convertView.findViewById(R.id.org_add_request);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FamilyMemberEntity memberEntity = list.get(position);
        String userId = memberEntity.getUser_id();
        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userId), R.drawable.default_head_icon, R.drawable.default_head_icon);
        if (!TextUtils.isEmpty(userId) && userId.equals(MainActivity.getUser().getUser_id()) && list.size() > 1) {
            viewHolder.meLine.setVisibility(View.VISIBLE);
        } else {
            viewHolder.meLine.setVisibility(View.GONE);
        }
        if (Constant.ORG_TRANSMIT_OTHER.equals(transmitData) && "0".equals(memberEntity.getFam_accept_flag())) {
            viewHolder.orgRequest.setVisibility(View.VISIBLE);
        } else {
            viewHolder.orgRequest.setVisibility(View.GONE);
        }
        viewHolder.memberName.setText(memberEntity.getUser_given_name());
        viewHolder.orgPosition.setText(memberEntity.getPosition());
        viewHolder.orgAdmin.setText("");
        return convertView;
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView orgPosition;
        TextView orgAdmin;
        View meLine;
        ImageView orgRequest;
    }

    public void setSerach(List<FamilyMemberEntity> list) {
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

        private List<FamilyMemberEntity> original;

        public PersonFilter(List<FamilyMemberEntity> list) {
            this.original = list;
        }

        //该方法在子线程中执行
        //自定义的过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = original;//原始数据
                results.count = original.size();
            } else {
                List<FamilyMemberEntity> mList = new ArrayList<FamilyMemberEntity>();
                String filterString = MyTextUtil.ToDBC(constraint.toString().trim().toLowerCase());
                for (FamilyMemberEntity memberEntity : original) {
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
            list = (List<FamilyMemberEntity>) results.values;
            notifyDataSetChanged();
            if (results.count == 0) {
                showData.showFoundData(constraint.toString());
            } else {
                showData.showRefreshLayout();
            }
        }
    }

    private NoFoundDataListener showData;

    public void showNoData(NoFoundDataListener showData) {
        this.showData = showData;
    }
}