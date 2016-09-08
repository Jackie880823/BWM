/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.base.BaseAdapter;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.ui.workspace.WorkSpaceFragment.OnListFragmentInteractionListener;
import com.madxstudio.co8.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WorkspaceEntity} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class WorkSpaceRecyclerViewAdapter extends RecyclerView
        .Adapter<WorkSpaceRecyclerViewAdapter.ViewHolder> implements
        BaseAdapter<List<WorkspaceEntity>> {
    private static final String TAG = "WorkSpaceRecyclerViewAdapter";

    private final OnListFragmentInteractionListener mListener;
    private List<WorkspaceEntity> mValues = new ArrayList<>();
    private Context context;

    public WorkSpaceRecyclerViewAdapter(Context context, OnListFragmentInteractionListener
            listener) {
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workspace_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        WorkspaceEntity entity = mValues.get(position);
        LogUtil.d(TAG, "onBindViewHolder() called with: " + "holder = [" + entity.toString() + "]");

        holder.mItem = mValues.get(position);
        String date = String.format("By: %1s, %2$s", entity.getUser_given_name(), entity
                .getContent_creation_date());

        holder.txtDate.setText(date);
        holder.txtTitle.setText(entity.getContent_title());
        holder.txtDescription.setText(entity.getText_description());

        String url = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity
                .getUser_id());
        BitmapTools.getInstance(context).display(holder.imgTitle, url, R.drawable
                .default_head_icon, R.drawable.default_head_icon);
        String attachmentCount = entity.getAttachment_count();
        String toDoCount = entity.getTo_do_count();
        String groupPublic = entity.getContent_group_public();
        holder.imgAttachment.setEnabled(hasNumber(attachmentCount));
        holder.imgTodoList.setEnabled(hasNumber(toDoCount));

        // 0- Me Only, 1- Everyone , 2-All Staff， Everyone和All Staff时锁开
        if ("1".equals(groupPublic) || "2".equals(groupPublic)) {
            holder.imgPrivilege.setEnabled(true);
        } else {
            holder.imgPrivilege.setEnabled(false);
        }
    }

    private boolean hasNumber(String count) {
        return !TextUtils.isEmpty(count) && Integer.valueOf(count) > 0;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void setData(List<WorkspaceEntity> data) {
        mValues = data;
        notifyDataSetChanged();
    }

    @Override
    public void addData(List<WorkspaceEntity> data) {
        int size = getItemCount();
        mValues.addAll(data);
        notifyItemRangeInserted(size, data.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnListFragmentInteractionListener mListener;
        public View mView;
        public ImageView imgTitle;
        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtDate;
        public WorkspaceEntity mItem;

        public TextView tvNum;
        public ImageView imgPrivilege;
        public ImageView imgTodoList;
        public ImageView imgAttachment;


        public ViewHolder(View view, OnListFragmentInteractionListener listener) {
            super(view);
            mView = view;
            mListener = listener;
            imgTitle = (ImageView) view.findViewById(R.id.img_title);
            txtTitle = (TextView) view.findViewById(R.id.txt_workspace_title);
            txtDescription = (TextView) view.findViewById(R.id.txt_workspace_description);
            txtDate = (TextView) view.findViewById(R.id.txt_workspace_date);

            tvNum = (TextView) view.findViewById(R.id.tv_num);
            imgPrivilege = (ImageView) view.findViewById(R.id.img_privilege);
            imgTodoList = (ImageView) view.findViewById(R.id.img_todo_list);
            imgAttachment = (ImageView) view.findViewById(R.id.img_attachment);

            tvNum.setVisibility(View.INVISIBLE);

            mView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + txtDescription.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if (null != mListener) {
                mListener.onListFragmentInteraction(mItem);
            }
        }
    }
}
