package com.bondwithme.BondWithMe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.BirthdayEntity;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.util.MyDateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                //                item.icon_release_date.setVisibility(View.VISIBLE);
                //                item.tvReleaseDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, ee.getGroup_event_date()));
                item.tvReleaseDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext, ee.getGroup_event_date()));
                item.tvReleaseDate.setVisibility(View.VISIBLE);
                if(!"0".equals(ee.getGroup_new_post())) {
                    this.position = position;
                    //                    item.item_event.setBackgroundResource(0);
                    if(clickitemList.size() > 0 && clickitemList.contains(position)) {
                        item.event_start.setVisibility(View.INVISIBLE);
                    } else {
                        item.event_start.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else if(holder instanceof VHHeader) {
            int todayCount = 0;
            int soonCount = 0;
            VHHeader item = (VHHeader) holder;
            for (int i = 0; i < birthdayEvents.size(); i++){
                if("0".equals(birthdayEvents.get(i).getDay_count())){
                    todayCount++;
                }else {
                    soonCount++;
                }
            }
            BirthdayEntity be = birthdayEvents.get(0);
            if("0".equals(be.getDay_count())) {
                item.left_daty_count.setText(mContext.getString(R.string.text_today));
                item.tv_date_desc.setVisibility(View.GONE);
            } else {
                item.left_daty_count.setText(be.getDay_count() + "");
                item.tv_date_desc.setVisibility(View.VISIBLE);
            }
            setBirthdayTopText(todayTitle,item.tv_top_today_title,be,todayCount);
            setBirthdayTopText(defaultTitle,item.tv_top_soon_title,be,soonCount);
            // add start by Jackie
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(be.getUser_dob());
                String text = DateUtils.formatDateTime(mContext, date.getTime(), DateUtils.FORMAT_NO_YEAR);
                item.date.setText(text);
            } catch(ParseException e) {
                e.printStackTrace();
                item.date.setText(be.getUser_dob());
            }
            // add end by Jackie

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

    private void setBirthdayTopText(String text, TextView textView,BirthdayEntity be,int count){
        if(count == 1) {
            textView.setText(String.format(text, be.getUser_given_name(), mContext.getString(R.string.title_birthday_title_prefix2)));
        } else {
            String stBirthday =  String.format(mContext.getString(R.string.title_birthday_title_prefix1,count));
            textView.setText(String.format(text, stBirthday,""));
        }
        if(count > 0){
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
        //        ImageView icon_release_date;
        RelativeLayout item_event;
        //        FrameLayout event_start;
        RelativeLayout event_start;


        public VHItem(final View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_event_title);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);

            tvReleaseDate = (TextView) itemView.findViewById(R.id.tv_release_date);
            item_unenable = (TextView) itemView.findViewById(R.id.item_unenable);
            //            icon_release_date = (ImageView) itemView.findViewById(R.id.icon_release_date);
            item_event = (RelativeLayout) itemView.findViewById(R.id.item_event);
            event_start = (RelativeLayout) itemView.findViewById(R.id.event_start);
            //
            //            if(){
            //
            //            }

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
                        //                            Log.i("item_click=======================","");
                        //                        }
                    }
                }
            });

        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        private TextView tv_top_today_title;
        private TextView tv_top_soon_title;
        private TextView date;
        private TextView left_daty_count;
        private TextView tv_date_desc;
        private RelativeLayout event_start;


        public VHHeader(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            tv_top_today_title = (TextView) itemView.findViewById(R.id.tv_birthday_today_title);
            tv_top_soon_title = (TextView) itemView.findViewById(R.id.tv_birthday_soon_title);
            date = (TextView) itemView.findViewById(R.id.date);
            left_daty_count = (TextView) itemView.findViewById(R.id.left_daty_count);
            tv_date_desc = (TextView) itemView.findViewById(R.id.tv_date_desc);
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
