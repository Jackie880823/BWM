package com.madx.bwm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.MemberEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.VHItem> {

    private Context mContext;
    private List<MemberEntity> data;
    private int tag;
    private String[] action ={"has added you","wanted to add you","waiting for member confirmation"};
    private String moduleAction;
    private String memberId;

    public MemberAdapter(Context context, List<MemberEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }


    public void add(List<MemberEntity> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        MemberEntity memberEntity = data.get(position);
        moduleAction = memberEntity.getModule_action();
        tag = tag(moduleAction);
        memberId = memberEntity.getAction_user_id();
        VolleyUtil.initNetworkImageView(mContext, holder.owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, memberEntity.getAction_user_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.owner_name.setText(memberEntity.getAction_username());
        holder.owner_content.setText(action[tag]);

        if(tag==0){
            holder.added.setVisibility(View.VISIBLE);
            holder.add.setVisibility(View.GONE);
            holder.awaiting.setVisibility(View.GONE);
        }else if(tag==1){
            holder.add.setVisibility(View.VISIBLE);
            holder.added.setVisibility(View.GONE);
            holder.awaiting.setVisibility(View.GONE);
        }else if(tag==2){
            holder.awaiting.setVisibility(View.VISIBLE);
            holder.added.setVisibility(View.GONE);
            holder.add.setVisibility(View.GONE);
        }

    }

    public int tag(String moduleAction){
//        "has added you","waiting fo member confirmation","wanted to add you"
        switch (moduleAction) {
            case "added":
                tag = 0;
                break;
            case "autoAcp":
                tag = 0;
                break;
            case "accept":
                tag = 0;
                break;
            case "updateRel":
                tag = 0;
                break;
            case "add":
                tag = 1;
                break;
            case "awaiting":
                tag = 2;
                break;
        }
        return tag;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private CircularNetworkImage owner_head;
        private TextView owner_name;
        private TextView owner_content;

        private TextView added;
        private TextView add;
        private TextView awaiting;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            owner_head= (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            owner_name= (TextView) itemView.findViewById(R.id.owner_name);
            owner_content = (TextView)itemView.findViewById(R.id.owner_content);
            added = (TextView)itemView.findViewById(R.id.added);
            add = (TextView)itemView.findViewById(R.id.add);
            awaiting = (TextView)itemView.findViewById(R.id.awaiting);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && data != null) {
                        itemClickListener.addClick(data.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });

            awaiting.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                    if (itemClickListener != null && data != null) {
                        itemClickListener.aWaittingClick(data.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }
    }
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void aWaittingClick(MemberEntity memberId, int position);
        void addClick(MemberEntity memberId, int position);

    }

}
