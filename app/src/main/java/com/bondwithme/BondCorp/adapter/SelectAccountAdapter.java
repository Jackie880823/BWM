package com.madxstudio.co8.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.start.ResetPasswordActivity;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/7/8.
 */
public class SelectAccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<UserEntity> mData;
    private String strCountryCode;
    private String strPhoneNumber;

    public SelectAccountAdapter(Context mContext, List<UserEntity> mData, String strCountryCode, String strPhoneNumber) {
        this.mContext = mContext;
        this.mData = mData;
        this.strCountryCode = strCountryCode;
        this.strPhoneNumber = strPhoneNumber;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberViewHolder(mLayoutInflater.inflate(R.layout.item_forgot_select_account, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MemberViewHolder item = (MemberViewHolder)holder;

        item.tvAccount.setText(mData.get(position).getUser_login_id());
        BitmapTools.getInstance(mContext).display(item.cnivMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position).getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder
    {
        private CircularNetworkImage cnivMain;
        private TextView tvAccount;

        public MemberViewHolder(View itemView) {
            super(itemView);

            cnivMain = (CircularNetworkImage)itemView.findViewById(R.id.cniv_main);
            tvAccount = (TextView)itemView.findViewById(R.id.tv_account);

            //怎么用回调
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ResetPasswordActivity.class);
                    intent.putExtra(Constant.LOGIN_USER, mData.get(getAdapterPosition()));
                    intent.putExtra("user_country_code", strCountryCode);
                    intent.putExtra("user_phone", MyTextUtil.NoZero(strPhoneNumber));
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
