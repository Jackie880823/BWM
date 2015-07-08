package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.MyDialog;

import java.util.List;

public class InvitedUserEditAdapter extends RecyclerView.Adapter<InvitedUserEditAdapter.ViewHolder> {
    private Context mContext;
    private List<UserEntity> data;

    public InvitedUserEditAdapter(Context context, List<UserEntity> data) {
        mContext = context;
        this.data = data;
    }

    public List<UserEntity> getData() {
        return data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invited_user_edit_item, viewGroup, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        UserEntity user = data.get(i);
        VolleyUtil.initNetworkImageView(mContext, viewHolder.user_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, user.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.user_name.setText(user.getUser_given_name());
        String status = user.getGroup_member_response();
        if (!TextUtils.isEmpty(status)) {
            if ("2".equals(status)) {
                UIUtil.setViewBackground(mContext, viewHolder.intent_status, R.drawable.status_not_going_press);
//                viewHolder.intent_status.setImageResource(R.drawable.status_not_going_normal);
            } else if ("1".equals(status)) {
                UIUtil.setViewBackground(mContext, viewHolder.intent_status, R.drawable.status_going_press);
//                viewHolder.intent_status.setImageResource(R.drawable.status_going_normal);
            } else if ("3".equals(status)) {
                UIUtil.setViewBackground(mContext, viewHolder.intent_status, R.drawable.status_maybe_press);
//                viewHolder.intent_status.setImageResource(R.drawable.status_maybe_normal);
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularNetworkImage user_head;
        TextView user_name;
        ImageButton remove;
        ImageButton intent_status;
        private MyDialog alertDialog;

        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            user_head = (CircularNetworkImage) itemView.findViewById(R.id.user_head);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            remove = (ImageButton) itemView.findViewById(R.id.remove);
            intent_status = (ImageButton) itemView.findViewById(R.id.intent_status);
            remove.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.remove:
                    showDeleteAlert();
//                    data.remove(getAdapterPosition());
//                    notifyDataSetChanged();
                    break;
            }
        }


        private void showDeleteAlert() {
            if (alertDialog == null) {
                alertDialog = new MyDialog(mContext, mContext.getString(R.string.text_tips_title), mContext.getString(R.string.text_ask_delete));
                alertDialog.setButtonAccept(mContext.getString(R.string.accept), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMemberDeleteListenere != null) {
                            mMemberDeleteListenere.remove(data.get(getAdapterPosition()).getUser_id());
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButtonCancel("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    private MemberDeleteListenere mMemberDeleteListenere;

    public void setMemberDeleteListenere(MemberDeleteListenere memberDeleteListenere) {
        mMemberDeleteListenere = memberDeleteListenere;
    }

    public interface MemberDeleteListenere {
        boolean remove(String userId);
    }
}