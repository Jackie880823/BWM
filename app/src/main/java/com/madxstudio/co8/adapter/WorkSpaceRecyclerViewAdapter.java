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
import android.support.annotation.IdRes;
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
import com.madxstudio.co8.base.BaseHolderViewInterface;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.ui.workspace.WorkSpaceFragment.OnListFragmentInteractionListener;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.image.Configuration;
import com.madxstudio.co8.util.image.ImageLoadUtil;

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
        holder.bindEntity(entity);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            BaseHolderViewInterface<WorkspaceEntity> {
        private WorkspaceEntity entity;

        OnListFragmentInteractionListener mListener;
        public View mView;
        public ImageView imgTitle;
        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtDate;

        public TextView tvNum;
        public ImageView imgPrivilege;
        public ImageView imgTodoList;
        public ImageView imgAttachment;


        public ViewHolder(View view, OnListFragmentInteractionListener listener) {
            super(view);
            mView = view;
            mListener = listener;
            imgTitle = getViewById(R.id.img_title);
            txtTitle = getViewById(R.id.txt_workspace_title);
            txtDescription = getViewById(R.id.txt_workspace_description);
            txtDate = getViewById(R.id.txt_workspace_date);

            tvNum = getViewById(R.id.tv_num);
            imgPrivilege = getViewById(R.id.img_privilege);
            imgTodoList = getViewById(R.id.img_todo_list);
            imgAttachment = getViewById(R.id.img_attachment);

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
                mListener.onListFragmentInteraction(entity);
            }
        }

        @Override
        public <V extends View> V getViewById(@IdRes int resId) {
            return (V) itemView.findViewById(resId);
        }

        @Override
        public void bindEntity(WorkspaceEntity entity) {
            this.entity = entity;


            String date = String.format(itemView.getResources().getString(R.string
                    .txt_by_name_date), entity
                    .getUser_given_name(), entity
                    .getContent_creation_date());

            txtDate.setText(date);
            txtTitle.setText(entity.getContent_title());
            txtDescription.setText(entity.getText_description());

            String url = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity
                    .getUser_id());
            Configuration configuration = new Configuration();
            configuration.url = url;
            configuration.errorDrawableId = R.drawable.default_head_icon;
            configuration.placeholderId = R.drawable.default_head_icon;
            ImageLoadUtil.display(itemView.getContext(), imgTitle, configuration);
            BitmapTools.getInstance(itemView.getContext()).display(imgTitle, url, R.drawable
                    .default_head_icon, R.drawable.default_head_icon);

            String attachmentCount = entity.getAttachment_count();
            String toDoCount = entity.getTo_do_count();
            String groupPublic = entity.getContent_group_public();
            imgAttachment.setEnabled(hasNumber(attachmentCount));
            imgTodoList.setEnabled(hasNumber(toDoCount));

            // 0- Me Only, 1- Everyone , 2-All Staff
            if ("1".equals(groupPublic)) {
                imgPrivilege.setImageResource(R.drawable.ic_privilege_public);
            } else if ("2".equals(groupPublic)) {
                imgPrivilege.setImageResource(R.drawable.ic_privilege_all);
            } else {
                imgPrivilege.setImageResource(R.drawable.ic_privilege_only_me);
            }
        }

        private boolean hasNumber(String count) {
            return !TextUtils.isEmpty(count) && Integer.valueOf(count) > 0;
        }
    }
}
