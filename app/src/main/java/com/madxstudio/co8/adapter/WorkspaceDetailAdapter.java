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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.workspace.ToDoListActivity;

/**
 * Created 16/8/4.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "WorkspaceDetailAdapter";

    public static final int VIEW_TYPE_HEAD = 100;
    public static final int VIEW_TYPE_NORMAL = 101;
    public static final int VIEW_TYPE_FOOTER = 102;

    private Context context;

    private int count;

    public WorkspaceDetailAdapter(Context context) {
        this.context = context;
        count = 4;
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
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEAD;
        } else if (position == count - 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public void setItemCount(int count) {
        this.count = count;
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

        // 竖向时四个功能行
        protected View layoutAttachment;
        protected View layoutMembers;
        protected View layoutToDoList;
        protected View layoutPrivilege;

        // 竖向时四个功能名称
        protected TextView txtAttachment;
        protected TextView txtMembers;
        protected TextView txtToDoList;
        protected TextView txtPrivilege;

        // 竖向时四个功能了项数量
        protected TextView txtNumberAttachment;
        protected TextView txtNumberMembers;
        protected TextView txtNumberToDoList;
        protected TextView txtNumberPrivilege;

        public HeadHolder(View itemView) {
            super(itemView);
            initView();

            setHorizontal(false);
        }

        private void initView() {
            layoutHorizontal = getViewById(R.id.layout_horizontal_icons);
            layoutVertical = getViewById(R.id.layout_vertical_icons);

            imgCollapseIcon = getViewById(R.id.img_collapse_icon);

            imgAttachment = getViewById(R.id.img_attachments);
            imgMembers = getViewById(R.id.img_members);
            imgToDoList = getViewById(R.id.img_todo_list);
            imgPrivilege = getViewById(R.id.img_privilege);

            layoutAttachment = getViewById(R.id.layout_attachments);
            layoutMembers = getViewById(R.id.layout_members);
            layoutToDoList = getViewById(R.id.layout_todo_list);
            layoutPrivilege = getViewById(R.id.layout_privilege);

            txtAttachment = getViewById(R.id.txt_attachments_desc);
            txtMembers = getViewById(R.id.txt_members_desc);
            txtToDoList = getViewById(R.id.txt_todo_list_desc);
            txtPrivilege = getViewById(R.id.txt_privilege_desc);

            txtNumberAttachment = getViewById(R.id.txt_attachments_number);
            txtNumberMembers = getViewById(R.id.txt_members_number);
            txtNumberToDoList = getViewById(R.id.txt_todo_list_number);
            txtNumberPrivilege = getViewById(R.id.txt_privilege_number);

            imgCollapseIcon.setOnClickListener(this);

            imgAttachment.setOnClickListener(this);
            imgMembers.setOnClickListener(this);
            imgToDoList.setOnClickListener(this);
            imgPrivilege.setOnClickListener(this);

            layoutAttachment.setOnClickListener(this);
            layoutMembers.setOnClickListener(this);
            layoutToDoList.setOnClickListener(this);
            layoutPrivilege.setOnClickListener(this);
        }

        private <V extends View> V getViewById(@IdRes int resId) {
            return (V) itemView.findViewById(resId);
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