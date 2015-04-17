package com.madx.bwm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.EventCommentEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

public class EventCommentAdapter extends RecyclerView.Adapter<EventCommentAdapter.ViewHolder> {
    private Context mContext;
    private List<EventCommentEntity> data;

    public EventCommentAdapter(Context context, List<EventCommentEntity> data) {
        mContext = context;
        this.data = data;

    }

    public void addData(List<EventCommentEntity> newdData) {
        data.addAll(newdData);
//        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void addOrUpdateItem(EventCommentEntity q) {
        int pos = data.indexOf(q);
        if (pos >= 0) {
            updateItem(q, pos);
        } else {
            addItem(q);
        }
    }


    private void updateItem(EventCommentEntity q, int pos) {
        data.remove(pos);
        notifyItemRemoved(pos);
        addItem(q);
    }

    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(EventCommentEntity q) {
        data.add(q);
        notifyItemInserted(data.size() - 1);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventCommentEntity ece = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, ece.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.tv_comment_owner_name.setText(ece.getUser_given_name());
        holder.tv_comment_content.setText(ece.getComment_content());
        holder.tv_agree_count.setText((TextUtils.isEmpty(ece.getLove_count()) ? "0" : ece.getLove_count()));
        holder.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, ece.getComment_creation_date()));

        if (MainActivity.getUser().getUser_id().equals(ece.getUser_id())) {
            holder.btn_comment_del.setVisibility(View.VISIBLE);
        } else {
            holder.btn_comment_del.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(ece.getLove_id())) {
            holder.iv_agree.setImageResource(R.drawable.agree_normal);
        } else {
            holder.iv_agree.setImageResource(R.drawable.agree_press);
        }
    }



    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularNetworkImage civ_comment_owner_head;
        TextView tv_comment_owner_name;
        TextView tv_comment_content;
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;

        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
            iv_agree.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    EventCommentEntity comment = data.get(getAdapterPosition());
//                    //自己发的或event creator 可以删除
//                    if(MainActivity.getUser().getUser_id().equals(comment.getUser_id())){
//                        removeComment(comment.getComment_id());
//                    }
//                }
//            });

        }




        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            EventCommentEntity commentEntity = data.get(position);
            switch (v.getId()) {
                case R.id.iv_agree:
                    newClick = true;
                    int count = Integer.valueOf(tv_agree_count.getText().toString());
                    if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                        iv_agree.setImageResource(R.drawable.agree_press);
                        commentEntity.setLove_id(MainActivity.getUser().getUser_id());
                        tv_agree_count.setText(count + 1 + "");
                    } else {
                        iv_agree.setImageResource(R.drawable.agree_normal);
                        commentEntity.setLove_id(null);
                        tv_agree_count.setText(count - 1 + "");
                    }
                    //判断是否已经有进行中的判断
                    if (!runningList.contains(position)) {
                        runningList.add(position);
                        check(position);
                    }
                    break;
                case R.id.btn_comment_del:
                    EventCommentEntity comment = data.get(getAdapterPosition());
                    //自己发的或event creator 可以删除
                    if (mCommentActionListener != null) {
                        if (MainActivity.getUser().getUser_id().equals(comment.getUser_id())) {
                            mCommentActionListener.doDelete(comment.getComment_id());
                        }
                    }
                    break;
            }
        }
    }

    boolean newClick;
    List<Integer> runningList = new ArrayList<Integer>();

    private void check(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();//点击时间
                long nowTime = System.currentTimeMillis();
                //缓冲时间为1000
                while (nowTime - startTime < 1000) {
                    if (newClick) {
                        startTime = System.currentTimeMillis();
                        newClick = false;
                    }
                    nowTime = System.currentTimeMillis();
                }
                try {
                    runningList.remove(position);
                } catch (Exception e) {
                }
                final EventCommentEntity commentEntity = data.get(position);
                if (mCommentActionListener != null) {
                    if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                        mCommentActionListener.doLove(commentEntity, false);
                    } else {
                        mCommentActionListener.doLove(commentEntity, true);
                    }
                }

            }
        }).start();
    }


    private CommentActionListener mCommentActionListener;

    public void setCommentActionListener(CommentActionListener commentActionListener) {
        mCommentActionListener = commentActionListener;
    }

    public interface CommentActionListener {
        public void doLove(final EventCommentEntity commentEntity, final boolean love);

        public void doDelete(String commentId);
    }


}