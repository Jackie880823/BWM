package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madx.bwm.R;
import com.madx.bwm.entity.AlertEventEntity;
import com.madx.bwm.ui.EventDetailActivity;
import com.madx.bwm.util.MyDateUtils;

import java.util.List;

public class AlertEventAdapter extends RecyclerView.Adapter<AlertEventAdapter.VHItem> {
    private Context mContext;
    private List<AlertEventEntity> data;


    public AlertEventAdapter(Context context, List<AlertEventEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<AlertEventEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }


    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        AlertEventEntity alertEventEntity = data.get(position);
        holder.news_title.setText(alertEventEntity.getAction_username());
        holder.news_content.setText(selectEvent(alertEventEntity.getModule_action(), alertEventEntity.getModule_name(), alertEventEntity.getPostowner_username()));
        holder.news_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, alertEventEntity.getCreation_date()));
    }

    private CharSequence selectEvent(String moduleAction, String moduleName, String postOwner) {
        String whatEvent = "";
        switch (moduleAction.toLowerCase().trim()) {
            case "invite":
                whatEvent = String.format(mContext.getString(R.string.text_invited_event), moduleName);
                break;
            case "resp":
                whatEvent = String.format(mContext.getString(R.string.text_responded_event), moduleName);
                break;
            case "comment":
                if (TextUtils.isEmpty(postOwner)) {
                    whatEvent = String.format(mContext.getString(R.string.text_commented_event), moduleName);
                } else {
                    whatEvent = String.format(mContext.getString(R.string.text_also_commented_event), moduleName);
                }
                break;
            case "update":
                whatEvent = String.format(mContext.getString(R.string.text_updated_event), moduleName);
                break;
            case "cancel":
                whatEvent = String.format(mContext.getString(R.string.text_cancelled_event), moduleName);
                break;
            case "lovecomment":
                whatEvent = String.format(mContext.getString(R.string.text_love_comment_event), moduleName);
                break;
            case "love":
                whatEvent = String.format(mContext.getString(R.string.text_love_event), moduleName);
                break;
            case "reminder":
                whatEvent = String.format(mContext.getString(R.string.text_upcoming_event), moduleName);
                break;
            default:
                whatEvent = moduleAction + moduleName;
                break;
        }
        int firstPosition = 0;
        int lastPosition = 0;
        if (null != moduleName) {
            if ("reminder".equalsIgnoreCase(moduleName.trim())) {
                firstPosition = whatEvent.length() - moduleName.length();
                lastPosition = whatEvent.length();
            } else {
                firstPosition = whatEvent.length() - moduleName.length() - 7;
                lastPosition = whatEvent.length() - 7;
            }
        }
        SpannableString msp = new SpannableString(whatEvent);
        msp.setSpan(new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.text_medium_size)), firstPosition, lastPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(Color.parseColor("#00b7ff")), firstPosition, lastPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), firstPosition, lastPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
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

            news_title = (TextView) itemView.findViewById(R.id.news_title);
            news_content = (TextView) itemView.findViewById(R.id.news_content);
            news_date = (TextView) itemView.findViewById(R.id.news_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventDetailActivity.class);
                    intent.putExtra("group_id", data.get(getAdapterPosition()).getModule_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }


}