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
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.base.BaseAdapter;
import com.madxstudio.co8.entity.WorkspaceDetail;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.ui.workspace.ToDoListActivity;
import com.madxstudio.co8.util.LogUtil;

/**
 * Created 16/8/4.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BaseAdapter<WorkspaceDetail>{
    private static final String TAG = "WorkspaceDetailAdapter";

    public static final int VIEW_TYPE_HEAD = 100;
    public static final int VIEW_TYPE_NORMAL = 101;
    public static final int VIEW_TYPE_FOOTER = 102;

    private Context context;

    private WorkspaceDetail detail;

    public WorkspaceDetailAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case VIEW_TYPE_HEAD:
                itemView = inflater.inflate(R.layout.head_workspace_detail, parent, false);
                holder = new HeadHolder(itemView);
                break;
            default:
                itemView = inflater.inflate(R.layout.item_post_discussion, parent, false);
                holder = new DiscussionViewHolder(itemView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case VIEW_TYPE_HEAD:
                bindHeadHolder((HeadHolder) holder, position);
                break;
        }
    }

    private void bindHeadHolder(HeadHolder holder, int position) {
        LogUtil.d(TAG, "bindHeadHolder() called with: " + "holder = [" + holder + "], position = " +
                "[" + position + "]");
        WorkspaceEntity entity = detail.getEntity();
        holder.setEntity(entity);
    }

    @Override
    public int getItemViewType(int position) {
        LogUtil.d(TAG, "getItemViewType() called with: " + "position = [" + position + "]");
        if (position == 0) {
            return VIEW_TYPE_HEAD;
        } else if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return detail.getCommentList().size() + 1;
    }

    @Override
    public void setData(WorkspaceDetail data) {
        LogUtil.d(TAG, "setData: ");
        detail = data;
        notifyDataSetChanged();
    }

    @Override
    public void addData(WorkspaceDetail data) {
        LogUtil.d(TAG, "addData: ");
        int itemCount = getItemCount();
        detail.getCommentList().addAll(data.getCommentList());
        notifyItemRangeInserted(itemCount, data.getCommentList().size());
    }

    /**
     * 评论内容视图
     */
    static class DiscussionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DiscussionViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemView.getContext().startActivity(new Intent(itemView.getContext(),
                    ToDoListActivity.class));
        }
    }

    /**
     * 头部的内容，主要有Workspace除评论以外的所有内容
     */
    static class HeadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "HeadHolder";
        private WorkspaceEntity entity;

        private TextView txtWorkspaceDate;
        private ImageView imgTitle;
        private TextView txtWorkspaceTitle;
        private TextView txtWorkspaceDesc;

        /**
         * 适配器封装的{@link View}是否是以{@link android.support.v7.widget.GridLayoutManager}形式加载
         */
        private boolean isHorizontal = false;

        protected View layoutHorizontal;
        protected View layoutVertical;

        protected ImageView imgCollapseIcon;

        // 横向时的四个图标
        protected ImageView imgAttachment;
        protected ImageView imgMembers;
        protected ImageView imgToDoList;
        protected ImageView imgPrivilege;

        protected ImageView imgAttachmentIcon;
        protected ImageView imgMembersIcon;
        protected ImageView imgToDoListIcon;
        protected ImageView imgPrivilegeIcon;

        // 竖向时四个功能行
        protected View layoutAttachment;
        protected View layoutMembers;
        protected View layoutToDoList;
        protected View layoutPrivilege;

        // 竖向时四个功能了项数量
        protected TextView txtNumberAttachment;
        protected TextView txtNumberMembers;
        protected TextView txtNumberToDoList;

        public HeadHolder(View itemView) {
            super(itemView);
            initView();

            setHorizontal(false);
        }

        private void initView() {
            imgTitle = getViewById(R.id.img_title);
            txtWorkspaceTitle = getViewById(R.id.txt_workspace_title);
            txtWorkspaceDate = getViewById(R.id.txt_workspace_date);
            txtWorkspaceDesc = getViewById(R.id.txt_workspace_description);

            layoutHorizontal = getViewById(R.id.layout_horizontal_icons);
            layoutVertical = getViewById(R.id.layout_vertical_icons);

            imgCollapseIcon = getViewById(R.id.img_collapse_icon);

            imgAttachment = getViewById(R.id.img_attachments);
            imgMembers = getViewById(R.id.img_members);
            imgToDoList = getViewById(R.id.img_todo_list);
            imgPrivilege = getViewById(R.id.img_privilege);

            imgAttachmentIcon = getViewById(R.id.img_attachments_icon);
            imgMembersIcon = getViewById(R.id.img_members_icon);
            imgToDoListIcon = getViewById(R.id.img_todo_list_icon);
            imgPrivilegeIcon = getViewById(R.id.img_privilege_icon);

            layoutAttachment = getViewById(R.id.layout_attachments);
            layoutMembers = getViewById(R.id.layout_members);
            layoutToDoList = getViewById(R.id.layout_todo_list);
            layoutPrivilege = getViewById(R.id.layout_privilege);

            txtNumberAttachment = getViewById(R.id.txt_attachments_number);
            txtNumberMembers = getViewById(R.id.txt_members_number);
            txtNumberToDoList = getViewById(R.id.txt_todo_list_number);

            imgTitle.setOnClickListener(this);

            imgCollapseIcon.setOnClickListener(this);

            imgAttachmentIcon.setOnClickListener(this);
            imgMembersIcon.setOnClickListener(this);
            imgToDoListIcon.setOnClickListener(this);
            imgPrivilegeIcon.setOnClickListener(this);

            layoutAttachment.setOnClickListener(this);
            layoutMembers.setOnClickListener(this);
            layoutToDoList.setOnClickListener(this);
            layoutPrivilege.setOnClickListener(this);
        }

        public void setEntity(WorkspaceEntity entity) {
            LogUtil.d(TAG, "setEntity() called with: " + "entity = [" + entity + "]");
            this.entity = entity;


            String date = String.format(itemView.getContext().getString(R.string.txt_by_name_date), entity
                    .getUser_given_name(), entity
                    .getContent_creation_date());
            txtWorkspaceDate.setText(date);
            txtWorkspaceTitle.setText(entity.getContent_title());
            txtWorkspaceDesc.setText(entity.getText_description());


            String attachmentCount = entity.getAttachment_count();
            String memberCount = entity.getContent_member_count();
            String toDoCount = entity.getTo_do_count();
            String groupPublic = entity.getContent_group_public();

            txtNumberMembers.setText(attachmentCount);
            txtNumberMembers.setText(memberCount);
            txtNumberToDoList.setText(toDoCount);

            imgAttachment.setEnabled(hasNumber(attachmentCount));
            imgAttachmentIcon.setEnabled(hasNumber(attachmentCount));
            imgToDoList.setEnabled(hasNumber(toDoCount));
            imgToDoListIcon.setEnabled(hasNumber(toDoCount));
            // 0- Me Only, 1- Everyone , 2-All Staff
            if ("1".equals(groupPublic)) {
                imgPrivilege.setImageResource(R.drawable.ic_privilege_public);
                imgPrivilegeIcon.setImageResource(R.drawable.ic_privilege_public);
            } else if ("2".equals(groupPublic)) {
                imgPrivilege.setImageResource(R.drawable.ic_privilege_all);
                imgPrivilegeIcon.setImageResource(R.drawable.ic_privilege_all);
            } else {
                imgPrivilege.setImageResource(R.drawable.ic_privilege_only_me);
                imgPrivilegeIcon.setImageResource(R.drawable.ic_privilege_only_me);
            }


        }

        private <V extends View> V getViewById(@IdRes int resId) {
            return (V) itemView.findViewById(resId);
        }

        public boolean hasNumber(String count) {
            return !TextUtils.isEmpty(count) && Integer.valueOf(count) > 0;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_collapse_icon:
                    setHorizontal(!isHorizontal);
                    break;
            }
        }

        public boolean isHorizontal() {
            return isHorizontal;
        }

        public void setHorizontal(boolean horizontal) {
            isHorizontal = horizontal;
            if (isHorizontal) {
                layoutVertical.setVisibility(View.GONE);
                layoutHorizontal.setVisibility(View.VISIBLE);
                imgCollapseIcon.setImageResource(R.drawable.ic_collapse);
            } else {
                layoutHorizontal.setVisibility(View.GONE);
                layoutVertical.setVisibility(View.VISIBLE);
                imgCollapseIcon.setImageResource(R.drawable.ic_relapse);
            }
        }
    }
}
