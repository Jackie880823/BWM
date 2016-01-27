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
import com.bondwithme.BondCorp.entity.UserEntity;
import com.bondwithme.BondCorp.util.UIUtil;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;
import com.bondwithme.BondCorp.widget.MyDialog;

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
        BitmapTools.getInstance(mContext).display(viewHolder.user_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, user.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
        viewHolder.user_name.setText(user.getUser_given_name());
        String status = user.getGroup_member_response();
//        LogUtil.i("status====",status+"_"+i);
        if (!TextUtils.isEmpty(status)) {
            viewHolder.intent_status.setVisibility(View.VISIBLE);
            switch (status){
                case "2":
                    UIUtil.setViewBackground(mContext, viewHolder.intent_status, R.drawable.status_not_going_press);
                    break;
                case "1":
                    UIUtil.setViewBackground(mContext, viewHolder.intent_status, R.drawable.status_going_press);
                    break;
                case "3":
                    UIUtil.setViewBackground(mContext, viewHolder.intent_status, R.drawable.status_maybe_press);
                    break;
                case "0":
                    viewHolder.intent_status.setBackgroundResource(0);
                    break;
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
                alertDialog.setButtonAccept(mContext.getString(R.string.text_dialog_accept), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMemberDeleteListenere != null) {
                            mMemberDeleteListenere.remove(data.get(getAdapterPosition()).getUser_id());
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButtonCancel(R.string.text_dialog_show_no, new View.OnClickListener() {
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