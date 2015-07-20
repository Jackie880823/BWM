package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.GroupListEntity;

import java.util.List;


public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.VHItem> {
    private Context mContext;
    private List<GroupListEntity> data;


    public GroupListAdapter(Context context, List<GroupListEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<GroupListEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        GroupListEntity groupListEntity = data.get(position);
        holder.group_name.setText(groupListEntity.getGroup_name());
//        holder.news_title.setText(alertEventEntity.getModule_name());
//        holder.news_content.setText(selectEvent(alertEventEntity.getModule_action(), alertEventEntity.getAction_username(), alertEventEntity.getPostowner_username()));
//        holder.news_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, alertEventEntity.getCreation_date()));
    }

//    private CharSequence selectEvent(String moduleAction, String moduleName, String postOwner) {
//        String whatEvent = "";
//        switch (moduleAction.toLowerCase().trim()) {
//            case "invite":
//                whatEvent = String.format(mContext.getString(R.string.text_invited_event), moduleName);
//                break;
//            case "resp":
//                whatEvent = String.format(mContext.getString(R.string.text_responded_event), moduleName);
//                break;
//            case "comment":
//                if (TextUtils.isEmpty(postOwner)) {
//                    whatEvent = String.format(mContext.getString(R.string.text_commented_event), moduleName);
//                } else {
//                    whatEvent = String.format(mContext.getString(R.string.text_also_commented_event), moduleName);
//                }
//                break;
//            case "update":
//                whatEvent = String.format(mContext.getString(R.string.text_updated_event), moduleName);
//                break;
//            case "cancel":
//                whatEvent = String.format(mContext.getString(R.string.text_cancelled_event), moduleName);
//                break;
//            case "lovecomment":
//                whatEvent = String.format(mContext.getString(R.string.text_love_comment_event), moduleName);
//                break;
//            case "love":
//                whatEvent = String.format(mContext.getString(R.string.text_love_event), moduleName);
//                break;
//            case "reminder":
//                whatEvent = String.format(mContext.getString(R.string.text_upcoming_event), moduleName);
//                break;
//            default:
//                whatEvent = moduleAction + moduleName;
//                break;
//        }
//
//        return whatEvent;
//    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private TextView group_name;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            group_name = (TextView) itemView.findViewById(R.id.group_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        itemClickListener.topItemClick(data.get(getAdapterPosition()).getGroup_id(),data.get(getAdapterPosition()).getGroup_name());
                    }
//                    Intent intent = new Intent(mContext, EventDetailActivity.class);
//                    intent.putExtra("group_id", data.get(getAdapterPosition()).getModule_id());
//                    mContext.startActivity(intent);
                }
            });
        }
    }

    public ItemClickListener itemClickListener ;
    //点击事件回调接口
    public interface ItemClickListener{
        void topItemClick(String group_id,String group_name);
    }
    public void setItemClickListener(ItemClickListener itCL){
        itemClickListener  = itCL;
    }

}