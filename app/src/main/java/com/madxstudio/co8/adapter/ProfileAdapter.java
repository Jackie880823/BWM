package com.madxstudio.co8.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.util.SDKUtil;
import com.madxstudio.co8.util.UIUtil;

/**
 * Created 16/3/21.
 *
 * @author Jackie
 * @version 1.0
 */
public class ProfileAdapter extends RecyclerView.Adapter {
    /**
     * 公司简介显示的Item类型
     */
    private static final int HEAD_VIEW = 0;
    /**
     * 管理员标题的类型的Item类型
     */
    private static final int ADMIN_TITLE_VIEW = 1;
    /**
     * 默认的类型
     */
    private static final int DEFAULT_VIEW = 2;

    /**
     * 最后一个Item的类型
     */
    private static final int LAST_VIEW = 3;

    private Context context;

    /**
     * 判断当前内容是否可以编辑的标识
     * - true:     可以编辑
     * - false:    不可编辑
     */
    private boolean write;

    public ProfileAdapter(Context context) {
        this.context = context;
    }

    /**
     * 更改公司的内容的可编辑状态
     *
     * @param write - true:     可以编辑
     *              - false:    不可编辑
     */
    public void changeEdit(boolean write) {
        this.write = write;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEAD_VIEW:// 头部公司的图片，简介，地址，电话，Email
                view = LayoutInflater.from(context).inflate(R.layout.company_profile_head, null);
                return new HeadHolderView(view);

            case LAST_VIEW:
            case DEFAULT_VIEW:
            case ADMIN_TITLE_VIEW:
            default:
                view = LayoutInflater.from(context).inflate(R.layout.company_admin_holder, null);
                return new AdminHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD_VIEW;
        } else if (position == 1) {
            return ADMIN_TITLE_VIEW;
        } else if (position == getItemCount() - 1) {
            return LAST_VIEW;
        } else {
            return DEFAULT_VIEW;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case HEAD_VIEW:
                if (holder instanceof HeadHolderView) {
                    ((HeadHolderView) holder).changeText(write);
                }
                break;
            case ADMIN_TITLE_VIEW:
                if (holder instanceof AdminHolder) {
                    ((AdminHolder) holder).tvAdminName.setText("Admin");
                    if (SDKUtil.IS_M) {
                        ((AdminHolder) holder).tvAdminName.setTextColor(context.getColor(R.color.default_text_color_light));
                    } else {
                        ((AdminHolder) holder).tvAdminName.setTextColor(context.getResources().getColor(R.color.default_text_color_light));
                    }
                    ((AdminHolder) holder).ivRight.setImageResource(R.drawable.add_member_icon);
                    holder.itemView.setBackgroundResource(R.color.default_wide_split_line);
                }
                break;
            case LAST_VIEW:
                holder.itemView.setVisibility(View.INVISIBLE);
                break;
            case DEFAULT_VIEW:
            default:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                break;
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 6;
    }

    private static class HeadHolderView extends RecyclerView.ViewHolder {
        protected ImageView ivProfileImage;
        protected TextView tvChangeText;
        protected EditText etCompanyName;
        protected EditText etDescription;
        protected EditText etAddress;
        protected EditText etPhone;
        protected EditText etEmail;
        protected TextView tvCompanyName;
        protected TextView tvDescription;
        protected TextView tvAddress;
        protected TextView tvPhone;
        protected TextView tvEmail;

        public HeadHolderView(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_images);
            tvChangeText = (TextView) itemView.findViewById(R.id.tv_change_text);
            etCompanyName = (EditText) itemView.findViewById(R.id.et_company_name);
            etDescription = (EditText) itemView.findViewById(R.id.et_company_description);
            etAddress = (EditText) itemView.findViewById(R.id.et_address);
            etPhone = (EditText) itemView.findViewById(R.id.et_phone);
            etEmail = (EditText) itemView.findViewById(R.id.et_email);
            tvCompanyName = (TextView) itemView.findViewById(R.id.tv_company_name);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_company_description);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
        }

        public void changeText(boolean write) {
            if (write) {
                tvChangeText.setVisibility(View.VISIBLE);
                etCompanyName.setVisibility(View.VISIBLE);
                etDescription.setVisibility(View.VISIBLE);
                etAddress.setVisibility(View.VISIBLE);
                etPhone.setVisibility(View.VISIBLE);
                etEmail.setVisibility(View.VISIBLE);

                tvCompanyName.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                tvAddress.setVisibility(View.GONE);
                tvPhone.setVisibility(View.GONE);
                tvEmail.setVisibility(View.GONE);

                etCompanyName.requestFocus();
                UIUtil.showKeyboard(itemView.getContext(), etCompanyName);
                etCompanyName.callOnClick();
            } else {
                tvChangeText.setVisibility(View.GONE);
                etCompanyName.setVisibility(View.GONE);
                etDescription.setVisibility(View.GONE);
                etAddress.setVisibility(View.GONE);
                etPhone.setVisibility(View.GONE);
                etEmail.setVisibility(View.GONE);

                tvCompanyName.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);
                tvAddress.setVisibility(View.VISIBLE);
                tvPhone.setVisibility(View.VISIBLE);
                tvEmail.setVisibility(View.VISIBLE);
            }
        }
    }

    private static class AdminHolder extends RecyclerView.ViewHolder {
        protected TextView tvAdminName;
        protected ImageView ivRight;

        public AdminHolder(View itemView) {
            super(itemView);
            tvAdminName = (TextView) itemView.findViewById(R.id.tv_admin);
            ivRight = (ImageView) itemView.findViewById(R.id.iv_right);
        }
    }
}
