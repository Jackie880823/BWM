package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.NewsEntity;
import com.madx.bwm.ui.NewsInfoActivity;
import com.madx.bwm.util.MyDateUtils;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.VHItem> {
    private Context mContext;
    private List<NewsEntity> data;


    public NewsAdapter(Context context, List<NewsEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<NewsEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        NewsEntity newsEntity = data.get(position);
        holder.news_title.setText(newsEntity.getModule_name());
        holder.news_content.setText(newsEntity.getMessage_variable());
        holder.news_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getCreation_date()));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private TextView news_title;
        private TextView news_content;
        private TextView news_date;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            news_title= (TextView) itemView.findViewById(R.id.news_title);
            news_content= (TextView) itemView.findViewById(R.id.news_content);
            news_date= (TextView) itemView.findViewById(R.id.news_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,NewsInfoActivity.class);
                    String newUrl=String.format(Constant.API_BONDALERT_NEWS_ITEM,data.get(getAdapterPosition()).getModule_id());
                    intent.putExtra("newUrl",newUrl);
                    mContext.startActivity(intent);
                }
            });
        }
    }



}