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

import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.OrganisationDetail;
import com.madxstudio.co8.entity.Profile;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.OrganisationConstants;
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
     * 头部项的个数，即为在管理员列表前项目的个数
     */
    private static final int HEAD_ITEMS = 2;

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
    private static final int ADMIN_DEFAULT_VIEW = 2;

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
    private List<UserEntity> admins;

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
    private boolean isAdmin;

    /**
     * 公司背景图片的路径，可以是网络路径也可以是本地路径
     */
    private String profileImageUrl;

    public ProfileAdapter(Context context, String companyID, boolean isAdmin) {
        this.context = context;
        this.companyID = companyID;
        this.isAdmin = isAdmin;
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
            case ADMIN_DEFAULT_VIEW:// 默认的Admin显示
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
            return ADMIN_DEFAULT_VIEW;// Admin名称显示项
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtil.d(TAG, "onBindViewHolder() called with:  position = [" + position + "]");
        switch (holder.getItemViewType()) {
            case HEAD_VIEW:
                if (holder instanceof HeadHolderView) {
                    ((HeadHolderView) holder).changeText();

                    if (profile != null) {
                        if (TextUtils.isEmpty(profileImageUrl)) {
                            profileImageUrl = String.format(OrganisationConstants.API_GET_ORGANISATION_COVER, companyID);
                        }

                        // 公司背影图片显示
                        displayProfileImage(holder);
                        // 公司描述资料显示
                        displayProfileText((HeadHolderView) holder);
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
                setAdminView(holder, position);
                break;

            case ADMIN_DEFAULT_VIEW:
                setAdminView(holder, position);
                break;

            case LAST_VIEW:
                holder.itemView.setVisibility(View.INVISIBLE);
                break;
            default:
                LogUtil.e(TAG, "onBindViewHolder: View type is error, not find type that is " + holder.getItemViewType());
                break;
        }
    }

    /**
     * 公司描述资料显示
     *
     * @param holder 相应控件的持有实例
     */
    private void displayProfileText(HeadHolderView holder) {
        holder.tvDescription.setText(profile.getDescription());
        holder.tvCompanyName.setText(profile.getName());
        holder.tvAddress.setText(profile.getAddress());
        holder.tvEmail.setText(profile.getEmail());
        holder.tvPhone.setText(profile.getPhone());

        setEditText(holder.etDescription, profile.getDescription());
        setEditText(holder.etCompanyName, profile.getName());
        setEditText(holder.etAddress, profile.getAddress());
        setEditText(holder.etEmail, profile.getEmail());
        setEditText(holder.etPhone, profile.getPhone());
    }

    private void setEditText(EditText et, String desc) {
        if (TextUtils.isEmpty(et.getText())) {
            et.setText(desc);
        }
    }

    /**
     * 显示公司背影图片
     *
     * @param holder 相应资料详情显示控件的持有实例
     * @param strUrl 背影图片的URL
     */
    public void displayProfileImage(RecyclerView.ViewHolder holder, String strUrl) {
        this.profileImageUrl = strUrl;
        displayProfileImage(holder);
    }

    /**
     * 显示公司背影图片
     *
     * @param holder 相应资料详情显示控件的持有实例
     */
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

    /**
     * 管理员相送的子项视图设置，包括Admin设置标题
     *
     * @param holder   子视图的{@link RecyclerView.ViewHolder}
     * @param position 对应{@code holder}在列表的位置下标
     */
    public void setAdminView(RecyclerView.ViewHolder holder, int position) {
        // 获取当管理员在管理员列表的索引
        final int index = position - HEAD_ITEMS;
        final int type = holder.getItemViewType();

        if (type == ADMIN_DEFAULT_VIEW && admins != null && !admins.isEmpty()) {
            UserEntity userEntity = admins.get(index);
            ((AdminHolder) holder).tvAdminName.setText(userEntity.getUser_given_name());
            LogUtil.d(TAG, "setAdminView: name => " + userEntity.getUser_given_name() + "; user id => " + userEntity.getUser_id());
        }

        if ("1".equals(MainActivity.getUser().getAdmin()) && isAdmin) { // 管理员才能有权力对管理列表进行操作

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
                            if (ADMIN_DEFAULT_VIEW == type) {
                                if (listener != null) {
                                    listener.viewAdminProfile(admins.get(index));
                                }
                            }
                            break;

                        case R.id.tv_message_admin:
                            // TODO: 16/3/23 给Admin发送信息
                            if (ADMIN_DEFAULT_VIEW == type) {
                                if (listener != null) {
                                    listener.sendMessageToAdmin(admins.get(index));
                                }
                            }
                            break;

                        case R.id.iv_right:
                            if (type == ADMIN_DEFAULT_VIEW) {
                                alertDialogRemove();
                            } else if (type == ADMIN_TITLE_VIEW) {
                                if (listener != null) {
                                    listener.requestAddAdmin();
                                }
                            }
                            break;

                        case R.id.btn_p:
                            // TODO: 16/3/23 dialog中点击了确认按钮
                            if (listener != null) {
                                listener.removeAdmin(admins.get(index));
                            }
                            break;

                        case R.id.btn_n:
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

                /**
                 * 弹出删除管理员的确认提示选择框
                 */
                private void alertDialogRemove() {
                    listDialog = new MyDialog(context, null, String.format(context.getString(R.string.alert_remove_admin), admins.get(index).getUser_given_name()));
                    listDialog.setButtonAccept(R.string.text_dialog_yes, this);
                    listDialog.setButtonCancel(R.string.text_dialog_cancel, this);
                    listDialog.show();
                }
            };

            holder.itemView.findViewById(R.id.tv_admin).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.tv_admin).setOnClickListener(clickListener);

            holder.itemView.findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.iv_right).setOnClickListener(clickListener);

        } else {
            // 非管理员不能添加或删除管理员
            holder.itemView.findViewById(R.id.iv_right).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        int count;
        if (admins == null || admins.isEmpty()) {
            count = HEAD_ITEMS + 1;
        } else {
            count = HEAD_ITEMS + admins.size() + 1;// 这里的总数要包含
        }
        return count;
    }

    /**
     * 已经删除了管理员
     *
     * @param index 删除了的管理员在管理员列表的下标位置
     */
    public void removedAdmin(int index) {
        admins.remove(index);
        int position = index + HEAD_ITEMS;
        notifyItemRemoved(position);
        LogUtil.d(TAG, "removedAdmin: count = " + getItemCount());
        for (int i = position; i < getItemCount(); i++) {
            notifyItemChanged(i);
        }
    }

    /**
     * Adapter自定义的监听接口，可根据需要自由修改、增、删接口函数
     *
     * @param listener {@link ProfileAdapterListener}监听本{@code adapter}
     */
    public void setListener(ProfileAdapterListener listener) {
        this.listener = listener;
    }

    /**
     * 列表头的ViewHolder,这里包含了公司背影图片和资料的相关显示控件
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
                            int maxLineEndIndex = tvDescription.getLayout().getLineEnd(3);
                            String sourceText = tvDescription.getText().toString();
                            String string = sourceText.substring(maxLineEndIndex);
                            if(string.length()>10){
                                sourceText = sourceText.replace(sourceText.substring(maxLineEndIndex+10),"...");
                            }
                            tvDescription.setText(sourceText);
                            tvSwitch.setVisibility(View.VISIBLE);
                            tvSwitch.setText(R.string.text_more);
                        }
                    }
                };
            }

            if (!needFull) {
                // 不显示全部内容只显示5九行
                tvDescription.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
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

        /**
         * 状态切换显示资料控件转换和转换确认相关操作<br/>
         * <br/>
         * <br/>
         * 非编辑状态 - 用{@link TextView}来显示资料<br/>
         * 编辑状态 - 用{@link EditText}来显示资料<br/>
         */
        public void changeText() {
            if (write) {// 编辑状态所有内容在编辑框中显示
                tvChangeText.setVisibility(View.VISIBLE);
                etCompanyName.setVisibility(View.GONE);
                etDescription.setVisibility(View.VISIBLE);
                etAddress.setVisibility(View.VISIBLE);
                etPhone.setVisibility(View.VISIBLE);
                etEmail.setVisibility(View.VISIBLE);

                tvCompanyName.setVisibility(View.VISIBLE);
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
         *
         * @return - {@code true}: 有变化<br/>
         * - {@code false}: 没变化<br/>
         */
        private boolean checkEditText() {
            return profile == null // 传入的公司简介为空，所以编辑内容有效
                    || !etDescription.getText().toString().equals(profile.getDescription()) // 编辑后的简介与公司原简介不一样，所以编辑内容有效
                    || !etCompanyName.getText().toString().equals(profile.getName()) // 编辑后的公司名称与公司原名称不同，所以编辑内容有效
                    || !etAddress.getText().toString().equals(profile.getAddress()) // 编辑后的地址与公司原地址不同，所以编辑内容有效
                    || !etPhone.getText().toString().equals(profile.getPhone()) // 编辑后的电话与公司原电话不同，所以编辑内容有效
                    || !etEmail.getText().toString().equals(profile.getEmail()); // 编辑后的Email与公司原Email不同，所以编辑的内容有效
        }
    }

    /**
     * 管理员的名称和相关操作控件的{@code ViewHolder}
     */
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

        /**
         * 确认编辑，对编辑内容提交时调用
         *
         * @param profile 公司资料
         */
        void confirmWrite(Profile profile);

        /**
         * 删除管理员
         *
         * @param userEntity 管理员,封装内容并不全，只饮食{@code user_id}和{@code user_given_name}
         */
        void removeAdmin(UserEntity userEntity);

        /**
         * 请求添加管理员
         */
        void requestAddAdmin();

        /**
         * 查看管理员详情
         *
         * @param userEntity 管理员,封装内容并不全，只包含{@code group_id}、{@code user_id}和{@code user_given_name}
         */
        void viewAdminProfile(UserEntity userEntity);

        /**
         * 给管理员发送信息
         *
         * @param userEntity 管理员,封装内容并不全，只包含{@code group_id}、{@code user_id}和{@code user_given_name}
         */
        void sendMessageToAdmin(UserEntity userEntity);
    }
}
