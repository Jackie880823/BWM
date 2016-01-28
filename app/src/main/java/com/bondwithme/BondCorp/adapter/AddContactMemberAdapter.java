package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.ContactDetailEntity;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/12/9.
 */
public class AddContactMemberAdapter extends RecyclerView.Adapter<AddContactMemberAdapter.ContactMemberViewHolder> {

    private final Context mContext;
    private List<ContactDetailEntity> mData;
    private final LayoutInflater mLayoutInflater;

    private final String INVITE = "0";
    private final String PENDING = "1";
    private final String ADD = "2";
    private final String ADDED = "3";

    public AddContactMemberAdapter(Context context, List<ContactDetailEntity> contactDetailEntityList) {
        mContext = context;
        mData = contactDetailEntityList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void changeData(List<ContactDetailEntity> newData) {
        mData = newData;
        notifyDataSetChanged();
    }

    @Override
    public ContactMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactMemberViewHolder(mLayoutInflater.inflate(R.layout.item_contact_members, parent, false));
    }

    @Override
    public void onBindViewHolder(ContactMemberViewHolder holder, int position) {
        if (!TextUtils.isEmpty(mData.get(position).getUser_id()))
        {
            BitmapTools.getInstance(mContext).display(holder.cniv, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position).getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
        }
        else
        {
            holder.cniv.setImageUrl(null, null);
            holder.cniv.setDefaultImageResId(R.drawable.default_head_icon);
            holder.cniv.setErrorImageResId(R.drawable.default_group_head_icon);
        }

//        if (!TextUtils.isEmpty(mData.get(position).getUser_id()))
//        {
//            BitmapTools.getInstance(mContext).display(holder.cniv, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position).getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
//        }
//        else
//        {
//            holder.cniv.setDefaultImageResId();
//        }

        holder.tvName.setText(mData.get(position).getDisplayName());
        holder.tvId.setText(mData.get(position).getUser_login_id());
        switch (mData.get(position).getMemberType()) {
            case INVITE:
                holder.ibInvite.setVisibility(View.VISIBLE);
                holder.ibPending.setVisibility(View.GONE);
                holder.ibAdd.setVisibility(View.GONE);
                holder.tvAdded.setVisibility(View.GONE);
                break;

            case PENDING:
                holder.ibInvite.setVisibility(View.GONE);
                holder.ibPending.setVisibility(View.VISIBLE);
                holder.ibAdd.setVisibility(View.GONE);
                holder.tvAdded.setVisibility(View.GONE);
                break;

            case ADD:
                holder.ibInvite.setVisibility(View.GONE);
                holder.ibPending.setVisibility(View.GONE);
                holder.ibAdd.setVisibility(View.VISIBLE);
                holder.tvAdded.setVisibility(View.GONE);
                break;

            case ADDED:
                holder.ibInvite.setVisibility(View.GONE);
                holder.ibPending.setVisibility(View.GONE);
                holder.ibAdd.setVisibility(View.GONE);
                holder.tvAdded.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ContactMemberViewHolder extends RecyclerView.ViewHolder {
        private CircularNetworkImage cniv;
        private TextView tvName;
        private TextView tvId;
        private TextView tvAdded;
        private ImageButton ibAdd;
        private ImageButton ibPending;
        private ImageButton ibInvite;

        public ContactMemberViewHolder(View itemView) {
            super(itemView);
            cniv = (CircularNetworkImage) itemView.findViewById(R.id.cni);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvId = (TextView) itemView.findViewById(R.id.tv_id);
            tvAdded = (TextView) itemView.findViewById(R.id.tv_added);
            ibAdd = (ImageButton) itemView.findViewById(R.id.ib_add);
            ibPending = (ImageButton) itemView.findViewById(R.id.ib_pending);
            ibInvite = (ImageButton) itemView.findViewById(R.id.ib_invite);

            //bwm成员，成好友
            tvAdded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onIconClickListener != null)
                        onIconClickListener.onIconClick(v, getAdapterPosition(), mData.get(getAdapterPosition()));
                }
            });

            //bwm成员，未添加
            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onIconClickListener != null)
                        onIconClickListener.onIconClick(v, getAdapterPosition(), mData.get(getAdapterPosition()));
                }
            });

            //bwm成员，未答复
            ibPending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onIconClickListener != null)
                        onIconClickListener.onIconClick(v, getAdapterPosition(), mData.get(getAdapterPosition()));
                }
            });

            //非bwm成员
            ibInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onIconClickListener != null)
                        onIconClickListener.onIconClick(v, getAdapterPosition(), mData.get(getAdapterPosition()));
                }
            });


        }
    }

    private OnIconClickListener onIconClickListener;

    public interface OnIconClickListener {
        void onIconClick(View v, int position, ContactDetailEntity contactDetailEntity);
    }

    public void setOnIconClickListener(OnIconClickListener onIconClickListener) {
        this.onIconClickListener = onIconClickListener;
    }


}
