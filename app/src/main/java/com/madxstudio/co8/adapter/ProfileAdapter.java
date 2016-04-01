package com.madxstudio.co8.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.Admin;
import com.madxstudio.co8.entity.OrganisationDetail;
import com.madxstudio.co8.entity.Profile;
import com.madxstudio.co8.ui.company.OrganisationConstants;
import com.madxstudio.co8.util.SDKUtil;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created 16/3/21.
 *
 * @author Jackie
 * @version 1.0
 */
public class ProfileAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ProfileAdapter";
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
     * 公司ID
     */
    private String companyID;

    /**
     * 公司资料
     */
    private Profile profile;
    /**
     * 管理员列表
     */
    private List<Admin> admins;

    /**
     * 判断当前内容是否可以编辑的标识
     * - true:     可以编辑
     * - false:    不可编辑
     */
    private boolean write;

    /**
     * Adapter各事件的监听
     */
    private ProfileAdapterListener listener;

    /**
     * 公司背景图片的路径，可以是网络路径也可以是本地路径
     */
    private String profileImageUrl;

    private HttpTools mHttpTools;

    public ProfileAdapter(Context context, String companyID) {
        this.context = context;
        this.companyID = companyID;
        mHttpTools = new HttpTools(context);
    }

    public void setData(OrganisationDetail organisationDetail) {

        profile = organisationDetail.getProfile();
        admins = organisationDetail.getAdmin();
        notifyDataSetChanged();
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

            case LAST_VIEW:// 最一个Item是不显示内容，这里只是为了给显示的Button预留空间
            case DEFAULT_VIEW:// 默认的Admin显示
            case ADMIN_TITLE_VIEW:// Admin的标题显示
            default:
                view = LayoutInflater.from(context).inflate(R.layout.company_admin_holder, null);
                return new AdminHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {// 第一项显示的是公司简介相关内容
            return HEAD_VIEW;
        } else if (position == 1) {// 第二项显示的是Admin标题，Admin成员从下面开始
            return ADMIN_TITLE_VIEW;
        } else if (position == getItemCount() - 1) {
            return LAST_VIEW;// 最后一项预留空位给离开点击按钮
        } else {
            return DEFAULT_VIEW;// Admin名称显示项
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case HEAD_VIEW:
                if (holder instanceof HeadHolderView) {
                    ((HeadHolderView) holder).changeText();

                    if (profile != null) {
                        if (TextUtils.isEmpty(profileImageUrl)) {
                            profileImageUrl = String.format(OrganisationConstants.API_GET_ORGANISATION_COVER, companyID);
                        }
                        displayProfileImage(holder);

                        ((HeadHolderView) holder).tvDescription.setText(profile.getDescription());
                        ((HeadHolderView) holder).tvCompanyName.setText(profile.getName());
                        ((HeadHolderView) holder).tvAddress.setText(profile.getAddress());
                        ((HeadHolderView) holder).tvEmail.setText(profile.getEmail());
                        ((HeadHolderView) holder).tvPhone.setText(profile.getPhone());

                        setEditText(((HeadHolderView) holder).etDescription, profile.getDescription());
                        setEditText(((HeadHolderView) holder).etCompanyName, profile.getName());
                        setEditText(((HeadHolderView) holder).etAddress, profile.getAddress());
                        setEditText(((HeadHolderView) holder).etEmail, profile.getEmail());
                        setEditText(((HeadHolderView) holder).etPhone, profile.getPhone());
                    }
                }
                break;
            case ADMIN_TITLE_VIEW:
                if (holder instanceof AdminHolder) {
                    ((AdminHolder) holder).tvAdminName.setText(context.getString(R.string.text_org_admin));
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
                setAdminView((AdminHolder) holder, position);
                break;
        }
    }

    private void setEditText(EditText et, String desc) {
        if (TextUtils.isEmpty(et.getText())) {
            et.setText(desc);
        }
    }

    /**
     * 显示公司背影图片
     *
     * @param holder
     * @param strUrl
     */
    public void displayProfileImage(RecyclerView.ViewHolder holder, String strUrl) {
        this.profileImageUrl = strUrl;
        displayProfileImage(holder);
    }

    private void displayProfileImage(RecyclerView.ViewHolder holder) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cloneFrom(UniversalImageLoaderUtil.options);
        // 设置图片加载/解码过程中错误时候显示的图片
        builder.showImageOnFail(R.drawable.ic_profile_title_bg);
        // 设置图片在加载期间显示的图片
        builder.showImageOnLoading(R.drawable.ic_profile_title_bg);
        // 设置图片Uri为空或是错误的时候显示的图
        builder.showImageForEmptyUri(R.drawable.ic_profile_title_bg);

        DisplayImageOptions options = builder.build();

        if (holder instanceof HeadHolderView) {
            ImageLoader.getInstance().displayImage(profileImageUrl, ((HeadHolderView) holder).ivProfileImage, options);
        }
    }

    public void setAdminView(AdminHolder holder, int position) {

        if (admins != null && !admins.isEmpty()) {
            holder.tvAdminName.setText(admins.get(position - 2).getUser_given_name());
        }


        View.OnClickListener clickListener = new View.OnClickListener() {
            private MyDialog listDialog;

            @Override
            public void onClick(View v) {

                if (listDialog != null && listDialog.isShowing()) {
                    listDialog.dismiss();
                    listDialog = null;
                }

                switch (v.getId()) {
                    case R.id.tv_view_admin_of_profile:
                        // TODO: 16/3/23 查看Admin简介
                        break;

                    case R.id.tv_message_admin:
                        // TODO: 16/3/23 给Admin发送信息
                        break;

                    case R.id.iv_right:
                        // TODO: 16/3/23 删除Admin
                        listDialog = new MyDialog(context, null, "Remove Gary Liow as Admin?");
                        listDialog.setButtonAccept(R.string.text_dialog_yes, this);
                        listDialog.setButtonCancel(R.string.text_dialog_cancel, this);
                        listDialog.show();
                        break;

                    case com.material.widget.R.id.button_accept:
                        // TODO: 16/3/23 dialog中点击了确认按钮
                        break;

                    case com.material.widget.R.id.button_cancel:
                        // TODO: 16/3/23 dialog中点击了取消按钮
                        break;

                    case R.id.tv_cancel:
                        // TODO: 16/3/23 取消操作
                        break;

                    default:
                        View askOptions = LayoutInflater.from(context).inflate(R.layout.layout_ask_admin_operation, null);
                        listDialog = new MyDialog(context, null, askOptions);
                        listDialog.show();
                        // 这里的 this是指当道匿名内部类，这里不在去多创建点击监听，可以使用同一个
                        askOptions.findViewById(R.id.tv_view_admin_of_profile).setOnClickListener(this);
                        askOptions.findViewById(R.id.tv_message_admin).setOnClickListener(this);
                        askOptions.findViewById(R.id.tv_cancel).setOnClickListener(this);
                        break;
                }
            }
        };
        holder.itemView.findViewById(R.id.tv_admin).setOnClickListener(clickListener);
        holder.itemView.findViewById(R.id.iv_right).setOnClickListener(clickListener);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        int count;
        if (admins == null || admins.size() == 0) {
            count = 2;
        } else {
            count = 2 + admins.size() + 1;// 这里的总数要包含
        }
        return count;
    }

    /**
     * Adapter自定义的监听接口，可根据需要自由修改、增、删接口函数
     *
     * @param listener
     */
    public void setListener(ProfileAdapterListener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    private class HeadHolderView extends RecyclerView.ViewHolder {
        /**
         * 公司图片
         */
        protected ImageView ivProfileImage;
        /**
         * 提示图片可点击，当可编辑显示这个控件
         */
        protected TextView tvChangeText;
        /**
         * 公司名称，当可编辑显示这个控件
         */
        protected EditText etCompanyName;
        /**
         * 公司简介，当可编辑显示这个控件
         */
        protected EditText etDescription;
        /**
         * 公司地址，当可编辑显示这个控件
         */
        protected EditText etAddress;
        /**
         * 公司电话，当可编辑显示这个控件
         */
        protected EditText etPhone;
        /**
         * 公司Email，当可编辑显示这个控件
         */
        protected EditText etEmail;
        /**
         * 公司名称，当不可编辑显示这个控件
         */
        protected TextView tvCompanyName;
        /**
         * 公司简介，当不可编辑显示这个控件
         */
        protected TextView tvDescription;
        /**
         * 公司地址，当不可编辑显示这个控件
         */
        protected TextView tvAddress;
        /**
         * 公司电话，当不可编辑显示这个控件
         */
        protected TextView tvPhone;
        /**
         * 公司Email，当不可编辑显示这个控件
         */
        protected TextView tvEmail;

        protected LinearLayout llTvDescription;
        protected TextView tvSwitch;

        private boolean needFull;

        public HeadHolderView(final View itemView) {
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

            llTvDescription = (LinearLayout) itemView.findViewById(R.id.ll_tv_description);
            tvSwitch = (TextView) itemView.findViewById(R.id.switch_text_show);

            tvSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needFull = !needFull;
                    switchDescriptionView();
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (write && listener != null) {
                        listener.onClickProfileImage(v);
                    }
                }
            });
        }

        private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

        /**
         * 切换公司简介内容显示是否可
         */
        protected void switchDescriptionView() {
            if (onGlobalLayoutListener == null) {
                onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 字符显示超过5行，只显示到第5行
                        int lineCount = tvDescription.getLineCount();
                        if (lineCount > 5) {
                            // TODO: 16/3/23 做一些显示处理
                            // 第5行只显示十个字符
                            int maxLineEndIndex = tvDescription.getLayout().getLineEnd(4);
                            CharSequence sourceText = tvDescription.getText();
                            String string = sourceText.toString().substring(maxLineEndIndex - 3);
                            if (string.startsWith("...")) {
                                return;
                            }

                            SpannableStringBuilder ssb = new SpannableStringBuilder(sourceText);
                            ssb.replace(maxLineEndIndex - 3, ssb.length(), "...");
                            tvDescription.setText(ssb);
                            tvSwitch.setVisibility(View.VISIBLE);
                            tvSwitch.setText(R.string.more);
                        }
                    }
                };
            }

            if (!needFull) {
                // 不显示全部内容只显示5九行
                tvDescription.setMaxLines(5);
                tvDescription.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
            } else {
                tvDescription.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
                tvDescription.setMaxLines(Integer.MAX_VALUE);
                tvSwitch.setText(R.string.text_collapse);
            }

            if (profile != null) {
                tvDescription.setText(profile.getDescription());
            }
        }

        public void changeText() {
            if (write) {// 编辑状态所有内容在编辑框中显示
                tvChangeText.setVisibility(View.VISIBLE);
                etCompanyName.setVisibility(View.VISIBLE);
                etDescription.setVisibility(View.VISIBLE);
                etAddress.setVisibility(View.VISIBLE);
                etPhone.setVisibility(View.VISIBLE);
                etEmail.setVisibility(View.VISIBLE);

                tvCompanyName.setVisibility(View.GONE);
                llTvDescription.setVisibility(View.GONE);
                tvAddress.setVisibility(View.GONE);
                tvPhone.setVisibility(View.GONE);
                tvEmail.setVisibility(View.GONE);

                ivProfileImage.requestFocus();
            } else {// 不可编辑状态所有内容显示在文本框中
                if (etDescription.getVisibility() == View.VISIBLE) {// 说明是从编辑转入不可编辑需要上传编辑的内容

                    if (checkEditText()) {
                        profile = new Profile();

                        profile.setName(String.valueOf(etCompanyName.getText()));
                        profile.setDescription(String.valueOf(etDescription.getText()));
                        profile.setAddress(String.valueOf(etAddress.getText()));
                        profile.setPhone(String.valueOf(etPhone.getText()));
                        profile.setEmail(String.valueOf(etEmail.getText()));
                    }
                    if (listener != null) {
                        listener.confirmWrite(profile);
                    }
                }

                tvChangeText.setVisibility(View.GONE);
                etCompanyName.setVisibility(View.GONE);
                etDescription.setVisibility(View.GONE);
                etAddress.setVisibility(View.GONE);
                etPhone.setVisibility(View.GONE);
                etEmail.setVisibility(View.GONE);

                tvCompanyName.setVisibility(View.VISIBLE);
                llTvDescription.setVisibility(View.VISIBLE);
                tvAddress.setVisibility(View.VISIBLE);
                tvPhone.setVisibility(View.VISIBLE);
                tvEmail.setVisibility(View.VISIBLE);
            }
        }

        /**
         * 检测文本框与Profile对比是否有变化，有变化则表明有修改
         * @return - {@code true}: 有变化<br/>
         * - {@code false}: 没变化<br/>
         */
        private boolean checkEditText() {
            if (profile == null) {
                return true;
            }

            if (!etDescription.getText().toString().equals(profile.getDescription())) {
                return true;
            }

            if (!etCompanyName.getText().toString().equals(profile.getName())) {
                return true;
            }

            if (!etAddress.getText().toString().equals(profile.getAddress())) {
                return true;
            }

            if (!etPhone.getText().toString().equals(profile.getPhone())) {
                return true;
            }

            if (!etEmail.getText().toString().equals(profile.getEmail())) {
                return true;
            }

            return false;
        }
    }

    private static class AdminHolder extends RecyclerView.ViewHolder {
        /**
         * Admin的名称用这个控件显示
         */
        protected TextView tvAdminName;
        /**
         * 右箭头图标显示控件，在Title时是显示添加Admin成员提示图标
         */
        protected ImageView ivRight;

        public AdminHolder(View itemView) {
            super(itemView);
            tvAdminName = (TextView) itemView.findViewById(R.id.tv_admin);
            ivRight = (ImageView) itemView.findViewById(R.id.iv_right);
        }
    }

    /**
     * Company Profile 中适配器的自定义监听，监听适配器中相应的动作或需要的回调
     * Created 16/3/23.
     *
     * @author Jackie
     * @version 1.0
     */
    public interface ProfileAdapterListener {
        /**
         * 公司简介图片被点
         *
         * @param view 被点击的控件
         */
        void onClickProfileImage(View view);

        void confirmWrite(Profile profile);
    }
}
