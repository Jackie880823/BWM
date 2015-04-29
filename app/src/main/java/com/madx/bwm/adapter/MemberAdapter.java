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

    //可以做为字符数组的下标
    private final static int TAG_ADD = 0;
    private final static int TAG_AWAITING = 1;
    private final static int TAG_AUTO_ACCEPT = 2;
    private final static int TAG_ADDED = 3;
    private final static int TAG_UPDATED = 4;

    //add，awaiting，auto-accept，added(同意)，updated(修改关系)。
    private String[] action ={"request to add you as ", "awaiting member confirmation", "You have auto-accept as member", "accept you as member", "set you as "};
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
//        holder.owner_content.setText(action[tag]);

//        if(tag==0){
//            holder.added.setVisibility(View.VISIBLE);
//            holder.add.setVisibility(View.GONE);
//            holder.awaiting.setVisibility(View.GONE);
//        }else if(tag==1){
//            holder.add.setVisibility(View.VISIBLE);
//            holder.added.setVisibility(View.GONE);
//            holder.awaiting.setVisibility(View.GONE);
//        }else if(tag==2){
//            holder.awaiting.setVisibility(View.VISIBLE);
//            holder.added.setVisibility(View.GONE);
//            holder.add.setVisibility(View.GONE);
//        }

        switch (tag)
        {
            case TAG_ADD:
            {
                holder.add.setVisibility(View.VISIBLE);

                holder.awaiting.setVisibility(View.GONE);
                holder.added.setVisibility(View.GONE);
                holder.updated.setVisibility(View.GONE);

                holder.owner_content.setText(action[TAG_ADD] + memberEntity.getRelationship());
            }
            break;

            case TAG_AWAITING:
            {
                holder.awaiting.setVisibility(View.VISIBLE);

                holder.add.setVisibility(View.GONE);
                holder.added.setVisibility(View.GONE);
                holder.updated.setVisibility(View.GONE);

                holder.owner_content.setText(action[TAG_AWAITING]);
            }
            break;

            case TAG_AUTO_ACCEPT:
            {
                holder.added.setVisibility(View.VISIBLE);

                holder.add.setVisibility(View.GONE);
                holder.awaiting.setVisibility(View.GONE);
                holder.updated.setVisibility(View.GONE);

                holder.owner_content.setText(action[TAG_AUTO_ACCEPT]);
            }
            break;

            case TAG_ADDED:
            {
                holder.added.setVisibility(View.VISIBLE);

                holder.add.setVisibility(View.GONE);
                holder.awaiting.setVisibility(View.GONE);
                holder.updated.setVisibility(View.GONE);

                holder.owner_content.setText(action[TAG_ADDED]);
            }
            break;

            case TAG_UPDATED:
            {
                holder.updated.setVisibility(View.VISIBLE);

                holder.add.setVisibility(View.GONE);
                holder.awaiting.setVisibility(View.GONE);
                holder.added.setVisibility(View.GONE);

                holder.owner_content.setText(action[TAG_UPDATED] + memberEntity.getRelationship());
            }
            break;

            default:
                break;
        }

    }

    public int tag(String moduleAction){
        switch (moduleAction) {
            case "add":
                tag = TAG_ADD;
                break;
            case "awaiting":
                tag = TAG_AWAITING;
                break;
            case "autoAcp":
                tag = TAG_AUTO_ACCEPT;
                break;
            case "accept":
                tag = TAG_ADDED;
                break;
            case "updateRel":
                tag = TAG_UPDATED;
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

        private TextView add;
        private TextView awaiting;
        private TextView added;
        private TextView updated;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            owner_head= (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            owner_name= (TextView) itemView.findViewById(R.id.owner_name);
            owner_content = (TextView)itemView.findViewById(R.id.owner_content);

            add = (TextView)itemView.findViewById(R.id.add);
            awaiting = (TextView)itemView.findViewById(R.id.awaiting);
            added = (TextView)itemView.findViewById(R.id.added);
            updated = (TextView)itemView.findViewById(R.id.updated);

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
