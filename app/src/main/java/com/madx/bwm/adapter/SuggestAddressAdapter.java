package com.madx.bwm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.madx.bwm.R;

import java.util.List;

public class SuggestAddressAdapter extends RecyclerView.Adapter<SuggestAddressAdapter.ViewHolder> {
    private Context mContext;
    private List<SuggestionResult.SuggestionInfo> data;

    public SuggestAddressAdapter(Context context, List<SuggestionResult.SuggestionInfo> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.map_search_address_item, viewGroup, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new ViewHolder(view);
    }

    private String defaultTitle;

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        SuggestionResult.SuggestionInfo suggestionInfo = data.get(i);
        viewHolder.tv_address_desc.setText(suggestionInfo.key);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address_desc;

        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            tv_address_desc = (TextView) itemView.findViewById(R.id.tv_address_desc);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemCheckListener!=null){
                        mItemCheckListener.onItemCheckedChange(data.get(getAdapterPosition()));
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
        void onItemCheckedChange(SuggestionResult.SuggestionInfo suggestionInfo);
    }
}