package com.madx.bwm.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madx.bwm.R;

import java.util.List;

public class FeelingAdapter extends RecyclerView.Adapter<FeelingAdapter.VHItem> {
    private Context mContext;
    private List<String> data;
    private String[] feelingDesces;


    public FeelingAdapter(Context context, List<String> data) {
        mContext = context;
        this.data = data;
        feelingDesces = context.getResources().getStringArray(R.array.feeling_desc);
    }

    @Override
    public FeelingAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeling_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(FeelingAdapter.VHItem holder, int position) {

        String iconPath = data.get(position);
        try {
            holder.iv_feel_icon.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(iconPath)));
            if (feelingDesces != null)
                holder.tv_feel_desc.setText(feelingDesces[position]);
            if (checkIndex == position) {
                holder.checked_icon.setVisibility(View.VISIBLE);
            } else {
                holder.checked_icon.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private int checkIndex = -1;
    public void setCheckIndex(int checkIndex){
        this.checkIndex = checkIndex;
    }

    class VHItem extends RecyclerView.ViewHolder {

        ImageView iv_feel_icon;
        ImageView checked_icon;
        TextView tv_feel_desc;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            iv_feel_icon = (ImageView) itemView.findViewById(R.id.iv_feel_icon);
            checked_icon = (ImageView) itemView.findViewById(R.id.checked_icon);
            tv_feel_desc = (TextView) itemView.findViewById(R.id.tv_feel_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int oldCheck = checkIndex;
                    checkIndex = getAdapterPosition();
                    checked_icon.setVisibility(View.VISIBLE);
                    notifyItemChanged(oldCheck);
                    if(mItemCheckListener!=null){
                        mItemCheckListener.onItemCheckedChange(getAdapterPosition());
                    }
                }
            });
        }

    }

    private ItemCheckListener mItemCheckListener;

    public void setItemCheckListener(ItemCheckListener itemCheckListener){
        mItemCheckListener = itemCheckListener;
    }

    public interface ItemCheckListener{
        void onItemCheckedChange(int position);
    }

}