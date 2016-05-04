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
import com.madxstudio.co8.entity.OrgMemberEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.OrgDetailActivity;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.MyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/6.
 */
public class OrgMemberListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<OrgMemberEntity> list;
    private List<OrgMemberEntity> serachList;

    private PersonFilter filter;
    private String transmitData;

    /**
     * add by Jackie
     * 管理的类型：   -   {@link Constant#ADMIN_REQUEST}管理员的请求可以管理员工<br>
     * -   {@link Constant#GENERAL_REQUEST} 普通员工的请求，无法管理员工
     */
    private String requestType;

    public OrgMemberListAdapter(Context mContext, List<OrgMemberEntity> memberList, String transmitData, String requestType) {
        list = memberList;
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mContext = mContext;
        this.transmitData = transmitData;
        this.requestType = requestType;
    }

    public void addNewData(List<OrgMemberEntity> newList) {
        list.clear();
        if (null != newList && newList.size() > 0) {
            list.addAll(newList);
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
            if (Constant.ADMIN_REQUEST.equals(requestType)) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.org_admin_staff_list_item, null);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.org_list_item, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.org_icon_image);
            viewHolder.memberName = (TextView) convertView.findViewById(R.id.tv_org_name);
            viewHolder.orgPosition = (TextView) convertView.findViewById(R.id.textv_org_position);
            viewHolder.meLine = convertView.findViewById(R.id.view_line);
            viewHolder.orgRequest = (ImageView) convertView.findViewById(R.id.org_add_request);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final OrgMemberEntity memberEntity = list.get(position);
        String userId = memberEntity.getUser_id();
        BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userId), R.drawable.default_head_icon, R.drawable.default_head_icon);
        if (!TextUtils.isEmpty(userId) && userId.equals(MainActivity.getUser().getUser_id()) && list.size() > 1) {
            viewHolder.meLine.setVisibility(View.VISIBLE);
        } else {
            viewHolder.meLine.setVisibility(View.GONE);
        }
        if (Constant.ORG_TRANSMIT_OTHER.equals(transmitData) && "0".equals(memberEntity.getAdded_flag())) {
            viewHolder.orgRequest.setVisibility(View.VISIBLE);
        } else if ("1".equals(memberEntity.getAdmin_flag()) && mContext instanceof OrgDetailActivity) {
            viewHolder.orgRequest.setVisibility(View.VISIBLE);
            viewHolder.orgRequest.setImageResource(R.drawable.org_admin_icon);
        } else {
            viewHolder.orgRequest.setVisibility(View.GONE);
        }
        viewHolder.memberName.setText(memberEntity.getUser_given_name());
        if(Constant.ORG_TRANSMIT_OTHER.equals(transmitData)) {
            viewHolder.orgPosition.setText(memberEntity.getOrganisation());
        }else{
            viewHolder.orgPosition.setText(memberEntity.getPosition());
        }
        if (Constant.ADMIN_REQUEST.equals(requestType)) {
            View view = convertView.findViewById(R.id.iv_right);
            if (MainActivity.getUser().getUser_id().equals(memberEntity.getUser_id())) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.iv_right).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteMember(memberEntity);
                    }
                });
            }
        }

        return convertView;
    }

    private MyDialog alertDialog;

    private void showDeleteMember(final OrgMemberEntity entity) {
        alertDialog = new MyDialog(mContext, R.string.text_tips_title, R.string.text_ask_delete);
        alertDialog.setButtonAccept(mContext.getString(R.string.text_dialog_accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (listener != null) {
                    listener.removeMember(entity);
                }
            }
        });
        alertDialog.setButtonCancel(R.string.text_dialog_show_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sureRemvoeMember(String url, OrgMemberEntity entity) {

    }

    static class ViewHolder {
        CircularNetworkImage imageMain;
        TextView memberName;
        TextView orgPosition;
        View meLine;
        ImageView orgRequest;
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

    AdminAdapterListener listener;

    public void setListener(AdminAdapterListener listener) {
        this.listener = listener;
    }

    /**
     * 删除联系人
     */
    public interface AdminAdapterListener {
        /**
         * 删除联系人
         *
         * @param memberEntity 被删除对象的实例
         */
        void removeMember(OrgMemberEntity memberEntity);
    }
}