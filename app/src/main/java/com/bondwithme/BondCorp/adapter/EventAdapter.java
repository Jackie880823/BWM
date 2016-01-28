package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.BirthdayEntity;
import com.bondwithme.BondCorp.entity.EventEntity;
import com.bondwithme.BondCorp.util.MyDateUtils;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context mContext;
    private List<EventEntity> data;
    private List<BirthdayEntity> birthdayEvents;
    private ArrayList<Integer> clickitemList = new ArrayList<>();
    //是否有生日item
    private boolean hasTop;
    //生日item的条目
    private int otherItemCount;
    //生日item显示的文字
    private String defaultTitle;
    private String todayTitle;

    private int position;

    public EventAdapter(Context context, List<EventEntity> data, List<BirthdayEntity> birthdayEvents) {
        mContext = context;
        this.data = data;
        this.birthdayEvents = birthdayEvents;
        if(birthdayEvents != null && birthdayEvents.size() > 0) {
            hasTop = true;
            otherItemCount = 1;
            defaultTitle = context.getString(R.string.title_birthday_title);
            todayTitle = context.getString(R.string.title_birthday_today_title);
        } else {
            hasTop = false;
            otherItemCount = 0;
        }

    }

    public void add(List<EventEntity> newEventDatas) {
        data.addAll(newEventDatas);
        notifyItemInserted(getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //或者无头部时
        if(viewType == TYPE_ITEM || !hasTop) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);//一般event的cardview item
            // ViewHolder参数一定要是Item的Root节点.
            return new VHItem(view);
        } else if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_top, parent, false);//生日cardview item
            return new VHHeader(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHItem || !hasTop) {
            VHItem item = (VHItem) holder;
            //一般event item的位置要减去生日item的条目
            EventEntity ee = data.get(position - otherItemCount);
            item.tvTitle.setText(ee.getGroup_name());
            if("2".equals(ee.getGroup_event_status())) {
                item.item_unenable.setVisibility(View.VISIBLE);
                item.tvUserName.setText(R.string.text_canceled);
                item.tvUserName.setTextColor(mContext.getResources().getColor(R.color.bold_text_color_red));
                item.tvUserName.setTypeface(null, Typeface.BOLD);
                //                item.icon_release_date.setVisibility(View.GONE);
                item.tvReleaseDate.setVisibility(View.GONE);
                if(!"0".equals(ee.getGroup_new_post())) {
                    this.position = position;
                    //                    //去除背景和颜色
                    //                    item.item_event.setBackgroundResource(0);
                    if(clickitemList.size() > 0 && clickitemList.contains(position)) {
                        item.event_start.setVisibility(View.INVISIBLE);
                    } else {
                        item.event_start.setVisibility(View.VISIBLE);
                    }
                }
            } else {

                item.item_unenable.setVisibility(View.GONE);
                item.tvUserName.setTextColor(mContext.getResources().getColor(R.color.default_text_color_light));
                item.tvUserName.setText(ee.getUser_given_name());
                item.tvUserName.setTypeface(null, Typeface.NORMAL);
                item.tvReleaseDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext, ee.getGroup_event_date()));
                item.tvReleaseDate.setVisibility(View.VISIBLE);
                if(!"0".equals(ee.getGroup_new_post())) {
                    this.position = position;
                    if(clickitemList.size() > 0 && clickitemList.contains(position)) {
                        item.event_start.setVisibility(View.INVISIBLE);
                    } else {
                        item.event_start.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else if(holder instanceof VHHeader) {
            List<BirthdayEntity> todayEntity = new ArrayList<>(),soonEntity = new ArrayList<>();
            VHHeader item = (VHHeader) holder;
            for (BirthdayEntity birthdayEntity :birthdayEvents){
                if("0".equals(birthdayEntity.getDay_count())){
                    todayEntity.add(birthdayEntity);
                }else {
                    soonEntity.add(birthdayEntity);
                }
            }
            setBirthdayTopText(todayTitle,item.tv_top_today_title,todayEntity);
            setBirthdayTopText(defaultTitle,item.tv_top_soon_title,soonEntity);

        }
    }

    @Override
    public int getItemCount() {
        return data.size() + otherItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private void setBirthdayTopText(String text, TextView textView,List<BirthdayEntity> be){
        if(be.size() == 1) {
            textView.setText(String.format(text, be.get(0).getUser_given_name(), mContext.getString(R.string.title_birthday_title_prefix2)));
        } else {
            String stBirthday =  String.format(mContext.getString(R.string.title_birthday_title_prefix1,be.size()));
            textView.setText(String.format(text, stBirthday,""));
        }
        if(be.size() > 0){
            textView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvUserName;
        TextView tvReleaseDate;
        TextView item_unenable;
        RelativeLayout item_event;
        RelativeLayout event_start;


        public VHItem(final View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_event_title);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);

            tvReleaseDate = (TextView) itemView.findViewById(R.id.tv_release_date);
            item_unenable = (TextView) itemView.findViewById(R.id.item_unenable);
            item_event = (RelativeLayout) itemView.findViewById(R.id.item_event);
            event_start = (RelativeLayout) itemView.findViewById(R.id.event_start);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null) {
                        //                        /**取消的event不可点击*/
                        //                        if(!"2".equals(data.get(getAdapterPosition()).getGroup_event_status())) {
                        itemClickListener.contentItemClick(data.get(getAdapterPosition() - otherItemCount));//回调方法
                        //清除点击item的背景和颜色
                        item_event.setBackgroundResource(0);
                        event_start.setVisibility(View.INVISIBLE);
                        clickitemList.add(position);
                    }
                }
            });

        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        private TextView tv_top_today_title;
        private TextView tv_top_soon_title;
        private RelativeLayout event_start;


        public VHHeader(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            tv_top_today_title = (TextView) itemView.findViewById(R.id.tv_birthday_today_title);
            tv_top_soon_title = (TextView) itemView.findViewById(R.id.tv_birthday_soon_title);
            event_start = (RelativeLayout) itemView.findViewById(R.id.event_start);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null && birthdayEvents != null) {
                        itemClickListener.topItemClick(birthdayEvents);
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
        void topItemClick(List<BirthdayEntity> birthdayEntitys);

        void contentItemClick(EventEntity eventEntity);
    }


}
