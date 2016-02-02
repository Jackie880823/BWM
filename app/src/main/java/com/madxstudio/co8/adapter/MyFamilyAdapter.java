package com.madxstudio.co8.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.madxstudio.co8.ui.FamilyFragment;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.PinYin4JUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/12.
 */
public class MyFamilyAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<FamilyMemberEntity> list;
    private List<FamilyMemberEntity> serachList;

    private PersonFilter filter;

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
        if (FamilyFragment.FAMILY_TREE.equals(userId)) {
            viewHolder.textName.setText(R.string.text_org_chart);
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain, null, R.drawable.org_chart, R.drawable.org_chart);
        } else if (FamilyFragment.FAMILY_MORE_MEMBER.equals(userId)) {
            viewHolder.textName.setText(familyMemberEntity.getUser_given_name());
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain, null, R.drawable.everyone_icon, R.drawable.everyone_icon);

        } else if (FamilyFragment.FAMILY_HIDE_MEMBER.equals(userId)) {
            viewHolder.textName.setText(familyMemberEntity.getUser_given_name());
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain, null, R.drawable.my_organisation, R.drawable.my_organisation);
        } else {
            viewHolder.textName.setText(familyMemberEntity.getUser_given_name());
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile,
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
                }
            }
        }
        return convertView;
    }

    public void setSerach(List<FamilyMemberEntity> list){
        this.serachList = list;
//        FamilyMemberEntity member = new FamilyMemberEntity();
//        member.setUser_given_name("family_tree");
//        member.setUser_id("family_tree");
//        FamilyMemberEntity familyMember = new FamilyMemberEntity();
//        familyMember.setUser_given_name("MyFamily");
//        familyMember.setUser_id("MyFamily");
//        serachList.remove(member);
//        serachList.remove(familyMember);

    }
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PersonFilter(serachList);
        }
        return filter;
    }
    //自定义Filer类
    private class PersonFilter extends Filter{

        private List<FamilyMemberEntity> original;

        public PersonFilter(List<FamilyMemberEntity> list) {
            this.original = list;
        }
        //该方法在子线程中执行
        //自定义的过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0){
                results.values = original;//原始数据
                results.count = original.size();
            }else {
                List<FamilyMemberEntity> mList = new ArrayList<FamilyMemberEntity>();
                String filterString = MyTextUtil.ToDBC(constraint.toString().trim().toLowerCase());
                for(FamilyMemberEntity memberEntity : original){
                    String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getUser_given_name());
                    if(-1 != userName.toLowerCase().indexOf(filterString)){
                        mList.add(memberEntity);
                    }
//                    if(-1 !=userName.toLowerCase().indexOf("family_tree") ||
//                            -1 != userName.toLowerCase().indexOf("MyFamily")){
//                        mList.remove(memberEntity);
//                    }
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
        ImageView imageLeft;
        ImageView imageRight;
        ImageView imageTop;
        TextView textName;
    }
}
