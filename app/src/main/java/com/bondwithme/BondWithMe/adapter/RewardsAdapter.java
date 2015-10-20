package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.NewsEntity;
import com.bondwithme.BondWithMe.util.MyDateUtils;

import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.VHItem> {
    private Context mContext;
    private List<NewsEntity> data;
    private int contentDisplayStatus = 0;//默认未展开
    private final int defaultMaxLineCount = 5;


    public RewardsAdapter(Context context, List<NewsEntity> data) {
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
    public void onBindViewHolder(final VHItem holder, int position) {
        NewsEntity newsEntity = data.get(position);
        holder.news_title.setText(newsEntity.getTitle());
        holder.news_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, newsEntity.getRelease_date()));
        holder.news_content.setText(newsEntity.getContent_text());
        holder.news_content.setMaxLines(defaultMaxLineCount);

        //判断textview的总高度是否大于默认行数的高度，从而控制是否显示“more”
        int fullHeight = holder.news_content.getMeasuredHeight();
        int defaultHeight = (holder.news_content.getLineHeight()) * defaultMaxLineCount;
        if (fullHeight <= defaultHeight){
            holder.tvMoreOrCollapse.setVisibility(View.GONE);

        }else {

            holder.tvMoreOrCollapse.setVisibility(View.VISIBLE);
            holder.tvMoreOrCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contentDisplayStatus == 0){
                        holder.news_content.setMaxLines(Integer.MAX_VALUE);
                        holder.tvMoreOrCollapse.setText("collapse");
                        contentDisplayStatus = 1;
                    }else if (contentDisplayStatus == 1){
                        holder.news_content.setMaxLines(defaultMaxLineCount);
                        holder.tvMoreOrCollapse.setText("more");
                        contentDisplayStatus = 0;
                    }

                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class VHItem extends RecyclerView.ViewHolder {

        private TextView news_title;
        private TextView news_date;
        private TextView news_content;
        private TextView tvMoreOrCollapse;


        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);

            news_title= (TextView) itemView.findViewById(R.id.news_title);
            news_date= (TextView) itemView.findViewById(R.id.news_date);
            news_content= (TextView) itemView.findViewById(R.id.news_content);
            tvMoreOrCollapse = (TextView)itemView.findViewById(R.id.tv_more_or_collapse);



//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext,NewsInfoActivity.class);
//                    String newUrl=String.format(Constant.API_BONDALERT_NEWS_ITEM,data.get(getAdapterPosition()).getModule_id());
//                    intent.putExtra("newUrl",newUrl);
//                    mContext.startActivity(intent);
//                }
//            });
        }
    }



}