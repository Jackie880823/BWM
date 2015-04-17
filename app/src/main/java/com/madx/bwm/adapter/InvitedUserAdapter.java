package com.madx.bwm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

public class InvitedUserAdapter extends RecyclerView.Adapter<InvitedUserAdapter.ViewHolder> {
    private Context mContext;
    private List<UserEntity> data;

    public InvitedUserAdapter(Context context, List<UserEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invited_user_status_item, viewGroup, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new ViewHolder(view);
    }


    private String loginedUserId = MainActivity.getUser().getUser_id();

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        UserEntity user = data.get(i);
        VolleyUtil.initNetworkImageView(mContext, viewHolder.user_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, user.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.user_name.setText(user.getUser_given_name());
        if (!loginedUserId.equals(user.getUser_id())) {
            if ("1".equals(user.getFriend())) {
                viewHolder.chat.setVisibility(View.VISIBLE);
                viewHolder.add.setVisibility(View.GONE);
            } else {
                viewHolder.chat.setVisibility(View.GONE);
                viewHolder.add.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.add.setVisibility(View.GONE);
            viewHolder.chat.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularNetworkImage user_head;
        TextView user_name;
        ImageButton add;
        ImageButton chat;

        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            user_head = (CircularNetworkImage) itemView.findViewById(R.id.user_head);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            add = (ImageButton) itemView.findViewById(R.id.add);
            chat = (ImageButton) itemView.findViewById(R.id.chat);
            add.setOnClickListener(this);
            chat.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            UserEntity user = data.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.add:
                    if (mOperaListener != null)
                        mOperaListener.addMember(user);
                    break;
                case R.id.chat:
                    if (mOperaListener != null)
                        mOperaListener.goChat(user);
                    break;
            }
        }
    }

    private OperaListener mOperaListener;

    public void setOperaListener(OperaListener operaListener) {
        mOperaListener = operaListener;
    }


    public interface OperaListener {
        void addMember(UserEntity user);

        void goChat(UserEntity user);
    }


}