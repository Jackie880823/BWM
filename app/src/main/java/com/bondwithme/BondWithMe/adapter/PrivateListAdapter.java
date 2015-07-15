package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PrivateListEntity;

import java.util.List;

/**
 * Created by liangzemian on 15/7/7.
 */
public class PrivateListAdapter extends RecyclerView.Adapter<PrivateListAdapter.VHItem>{

    private Context mContext;
    private List<PrivateListEntity> data;

    public PrivateListAdapter(Context context,List<PrivateListEntity> data){
        mContext = context;
        this.data = data;

    }
    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_list_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<PrivateListEntity> newData){
        data.addAll(newData);
        notifyItemInserted(data.size());
    }
    @Override
    public void onBindViewHolder(PrivateListAdapter.VHItem holder, int position) {
        PrivateListEntity privateListEntity = data.get(position);
        holder.private_name.setText(privateListEntity.getUser_given_name());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class VHItem extends RecyclerView.ViewHolder {

        private TextView private_name;

        public VHItem(View itemView) {
            super(itemView);
            private_name = (TextView) itemView.findViewById(R.id.private_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        itemClickListener.topItemClick(data.get(getAdapterPosition()).getUser_id());
                    }
                }
            });
        }
    }

    public ItemClickListener itemClickListener ;
    //点击事件回调接口
    public interface ItemClickListener{
        void topItemClick(String user_id);
    }
    public void setItemClickListener(ItemClickListener itCL){
        itemClickListener  = itCL;
    }
}
