package com.madxstudio.co8.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
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
import com.madxstudio.co8.entity.AdminPendingRequest;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.OrganisationConstants;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 16/4/6.
 *
 * @author Jackie
 * @version 1.0
 */
public class PendingRequestAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<AdminPendingRequest> list;

    private PersonFilter filter;

    public PendingRequestAdapter(Context mContext, List<AdminPendingRequest> list) {
        this.list = list;
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mContext = mContext;
    }

    public void addNewData(List<AdminPendingRequest> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void removeData(AdminPendingRequest request) {
        list.remove(request);
        notifyDataSetChanged();
    }

    public List<AdminPendingRequest> getList() {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.org_request_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.org_icon_image);
            viewHolder.tvRequestName = (TextView) convertView.findViewById(R.id.tv_request_name);
            viewHolder.tvRequestAction = (TextView) convertView.findViewById(R.id.tv_request_action);
            viewHolder.tvRequestDate = (TextView) convertView.findViewById(R.id.tv_request_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AdminPendingRequest pendingRequest = list.get(position);
        String userId = pendingRequest.getAction_user_id();
        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userId), R.drawable.default_head_icon, R.drawable.default_head_icon);
        viewHolder.tvRequestName.setText(pendingRequest.getAction_username());
        viewHolder.tvRequestAction.setText(pendingRequest.getModule_action());
        viewHolder.tvRequestAction.setText(setAction(pendingRequest));

        String date = MyDateUtils.getLocalDateStringFromUTC(mContext, pendingRequest.getCreation_date());
        viewHolder.tvRequestDate.setText(date);
        return convertView;
    }

    private SpannableStringBuilder setAction(AdminPendingRequest pendingRequest) {
        SpannableStringBuilder result = new SpannableStringBuilder();
        if (OrganisationConstants.MODULE_ACTION_LEAVE.equals(pendingRequest.getModule_action())) {
            result.append(mContext.getString(R.string.wants_to_leave));
        } else if (OrganisationConstants.MODULE_ACTION_JOIN.equals(pendingRequest.getModule_action())) {
            result.append(mContext.getString(R.string.want_to_join));
        }

        SpannableString spannableString = new SpannableString(pendingRequest.getMessage_variable());
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(styleSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        result.append(spannableString);
        return result;
    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        TextView tvRequestName;
        TextView tvRequestAction;
        TextView tvRequestDate;
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
        private List<AdminPendingRequest> pendingRequests;

        public PersonFilter(List<AdminPendingRequest> requestList) {
            this.pendingRequests = requestList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.values = pendingRequests;//原始数据
                results.count = pendingRequests.size();
            } else {
                List<AdminPendingRequest> mList = new ArrayList<>();
                String filterString = MyTextUtil.ToDBC(constraint.toString().trim().toLowerCase());
                for (AdminPendingRequest pendingRequest : pendingRequests) {
                    String userName = PinYin4JUtil.getPinyinWithMark(pendingRequest.getAction_username());
                    if (-1 != userName.toLowerCase().indexOf(filterString)) {
                        mList.add(pendingRequest);
                    }
                }
                results.values = mList;
                results.count = mList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (List<AdminPendingRequest>) results.values;
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
