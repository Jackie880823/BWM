package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.RecommendEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.util.List;

/**
 * Created by christepherzhang on 15/11/3.
 */
public class AddMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{


    private enum ITEM_TYPE
    {
        ITEM_HEAD,
        ITEM_CONTENT
    }
    private Context mContext;
    private List<RecommendEntity> mData;


    public AddMembersAdapter(Context context, List<RecommendEntity> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_HEAD.ordinal())
        {
            return new HeaderViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_members_options_head, null));
        }
        else if (viewType == ITEM_TYPE.ITEM_CONTENT.ordinal())
        {
            return new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_members, null));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder)
        {

        }
        else if (holder instanceof ContentViewHolder)
        {


            ContentViewHolder itemView = (ContentViewHolder)holder;
            if (position == 1)//推荐的第一个要展示提示布局，其余的不用
            {
                itemView.llPrompt.setVisibility(View.VISIBLE);
            }
            else
            {
                itemView.llPrompt.setVisibility(View.GONE);
            }

            VolleyUtil.initNetworkImageView(mContext, itemView.cni, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position - 1).getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            itemView.tvName.setText(mData.get(position - 1).getUser_given_name());
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
        {
            return ITEM_TYPE.ITEM_HEAD.ordinal();
        }
        else
        {
            return ITEM_TYPE.ITEM_CONTENT.ordinal();
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llContacts;
        private LinearLayout llFacebook;
        private LinearLayout llQq;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            llContacts = (LinearLayout) itemView.findViewById(R.id.ll_add_via_contacts);
            llFacebook = (LinearLayout) itemView.findViewById(R.id.ll_facebook);
            llQq = (LinearLayout) itemView.findViewById(R.id.ll_qq);

        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llPrompt;
        private CircularNetworkImage cni;
        private TextView tvName;
        private ImageButton ibAdd;

        public ContentViewHolder(View itemView) {
            super(itemView);
            llPrompt = (LinearLayout) itemView.findViewById(R.id.ll_prompt);
            cni = (CircularNetworkImage) itemView.findViewById(R.id.cni);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ibAdd = (ImageButton) itemView.findViewById(R.id.ib_add);
        }
    }


    @Override
    public void onClick(View v) {

    }


}
