package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.NewsEntity;
import com.bondwithme.BondCorp.ui.BaseFragment;
import com.bondwithme.BondCorp.util.LogUtil;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
    private Context mContext;
    private List<NewsEntity> data;
    private int contentDisplayStatus = 0;//默认未展开
    private final int defaultMaxLineCount = 5;
    private String TAG = "NewsAdapter";
    private BaseFragment fragment;

    public NewsAdapter(Context context, List<NewsEntity> data) {
        mContext = context;
        this.data = data;
    }

    public NewsAdapter(BaseFragment fragment,Context context, List<NewsEntity> data) {
        mContext = context;
        this.data = data;
        this.fragment = fragment;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new NewsHolder(fragment,view,mContext);
    }

    public void add(List<NewsEntity> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final NewsHolder holder, int position) {
        final NewsEntity news = data.get(position);
        LogUtil.d(TAG, "onBindViewHolder" + "isVisibleOfTvMore=======" + news.isVisibleOfTvMore());
        holder.setNewsEntity(news);
        if(!news.isVisibleOfTvMore()){
            holder.setSwitchVisibility(View.GONE);
        }
        holder.setContent(news,mContext);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

}