package com.madx.bwm.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.WallCommentEntity;
import com.madx.bwm.entity.WallEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WallCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<WallCommentEntity> data;
    private WallEntity wall;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public WallCommentAdapter(Context context, List<WallCommentEntity> data, WallEntity wall) {
        mContext = context;
        this.data = data;
        this.wall = wall;
    }

    public void addData(List<WallCommentEntity> newdData) {
        data.addAll(newdData);
//        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //或者无头部时
        if (viewType == TYPE_ITEM) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(mContext).inflate(R.layout.wall_comment_item, parent, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new VHItem(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.wall_item, parent, false);
            return new VHHeader(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof VHItem) {
            VHItem item = (VHItem) viewHolder;
            WallCommentEntity comment = data.get(i - 1);
//            VolleyUtil.initNetworkImageView(mContext, item.civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, comment.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            item.tv_comment_owner_name.setText(comment.getUser_given_name());
            item.tv_comment_content.setText(comment.getComment_content());
            item.tv_agree_count.setText((TextUtils.isEmpty(comment.getLove_count()) ? "0" : comment.getLove_count()));
            item.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, comment.getComment_creation_date()));

            if (MainActivity.getUser().getUser_id().equals(comment.getUser_id())) {
                item.btn_comment_del.setVisibility(View.VISIBLE);
            } else {
                item.btn_comment_del.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(comment.getLove_id())) {
                item.iv_agree.setImageResource(R.drawable.agree_normal);
            } else {
                item.iv_agree.setImageResource(R.drawable.agree_press);
            }
        } else if (viewHolder instanceof VHHeader) {

            VHHeader holder = (VHHeader) viewHolder;
            VolleyUtil.initNetworkImageView(mContext, holder.nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wall.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

//        if (TextUtils.isEmpty(ece.getText_description())) {
//            holder.tvContent.setVisibility(View.GONE);
//        } else {
            String atMemberDesc = wall.getTag_member().size()==0?"":String.format(mContext.getString(R.string.text_wall_content_at_member_desc),(wall.getTag_member().size()));
            String atGroupDesc = wall.getTag_group().size()==0?"":String.format(mContext.getString(R.string.text_wall_content_at_group_desc),(wall.getTag_group().size()));
            String wallContent = String.format(mContext.getString(R.string.text_wall_content_template),TextUtils.isEmpty(wall.getText_description())?"":wall.getText_description(),atGroupDesc,atMemberDesc);
            holder.tvContent.setText(wallContent);
//        }

            holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, wall.getContent_creation_date()));
//            holder.tvTime.setText(wall.getTime());
            holder.tvUserName.setText(wall.getUser_given_name());
            if (TextUtils.isEmpty(wall.getFile_id())) {
                holder.imWallsImages.setVisibility(View.GONE);
            } else {
                holder.imWallsImages.setVisibility(View.VISIBLE);
                VolleyUtil.initNetworkImageView(mContext, holder.imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, wall.getUser_id(), wall.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }

         /*is owner wall*/
//        if (!TextUtils.isEmpty(wall.getUser_id())&&wall.getUser_id().equals("49")) {
//            holder.ibDelete.setVisibility(View.VISIBLE);
//        } else {
//            holder.ibDelete.setVisibility(View.GONE);
//        }

            try {
                if (wall.getDofeel_code() != null) {
                    StringBuilder b = new StringBuilder(wall.getDofeel_code());
                    int charIndex = wall.getDofeel_code().lastIndexOf("_");
                    b.replace(charIndex, charIndex + 1, "/");

                    InputStream is = mContext.getAssets().open(b.toString());
                    if (is != null) {
                        holder.iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
                    } else {
                        holder.iv_mood.setVisibility(View.GONE);
                    }
                } else {
                    holder.iv_mood.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.iv_mood.setVisibility(View.GONE);
            }

        /*location*/
//        if (TextUtils.isEmpty(wall.getLoc_name())) {
//            holder.at.setVisibility(View.GONE);
//        } else {
//            holder.at.setVisibility(View.VISIBLE);
//            holder.tvLocation.setText(wall.getLoc_name());
//        }

            holder.tvAgreeCount.setText(wall.getLove_count());
            holder.tvCommentCount.setText(wall.getComment_count());


            if (MainActivity.getUser().getUser_id().equals(wall.getUser_id())) {
                holder.btn_del.setVisibility(View.VISIBLE);
            } else {
                holder.btn_del.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(wall.getLove_id())) {
                holder.ibAgree.setImageResource(R.drawable.love_normal);
            } else {
                holder.ibAgree.setImageResource(R.drawable.love_press);
            }
        }
    }

    public int getItemCount() {
        return data.size() + 1;
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        //        CircularNetworkImage civ_comment_owner_head;
        TextView tv_comment_owner_name;
        TextView tv_comment_content;
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;

        public VHItem(View itemView) {
            super(itemView);
//            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
            iv_agree.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition() - 1;
            WallCommentEntity commentEntity = data.get(position);
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
                    WallCommentEntity comment = data.get(position);
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
                final WallCommentEntity commentEntity = data.get(position);
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
        public void doLove(final WallCommentEntity commentEntity, final boolean love);

        public void doDelete(String commentId);
    }

    class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularNetworkImage nivHead;
        TextView tvContent;
        TextView tvDate;
        TextView tvUserName;
        NetworkImageView imWallsImages;
        TextView tvAgreeCount;
        TextView tvCommentCount;
        ImageButton ibAgree;
        ImageButton ibComment;
        ImageButton btn_del;
        ImageView iv_mood;

        public VHHeader(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            nivHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_wall_content);
            tvDate = (TextView) itemView.findViewById(R.id.push_date);
            imWallsImages = (NetworkImageView) itemView.findViewById(R.id.iv_walls_images);
            tvAgreeCount = (TextView) itemView.findViewById(R.id.tv_wall_agree_count);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tv_wall_relay_count);
            ibAgree = (ImageButton) itemView.findViewById(R.id.iv_love);
            ibComment = (ImageButton) itemView.findViewById(R.id.iv_comment);
            btn_del = (ImageButton) itemView.findViewById(R.id.btn_del);
            iv_mood = (ImageView) itemView.findViewById(R.id.iv_mood);

            ibAgree.setOnClickListener(this);
//            ibComment.setOnClickListener(this);
            btn_del.setOnClickListener(this);
            imWallsImages.setOnClickListener(this);
        }

        boolean loving;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_love:
                    newClick = true;
                    int count = Integer.valueOf(tvAgreeCount.getText().toString());
                    if (TextUtils.isEmpty(wall.getLove_id())) {
                        tvAgreeCount.setText(count + 1 + "");
                        ibAgree.setImageResource(R.drawable.love_press);
                        wall.setLove_id(MainActivity.getUser().getUser_id());
                    } else {
                        ibAgree.setImageResource(R.drawable.love_normal);
                        wall.setLove_id(null);
                        tvAgreeCount.setText(count - 1 + "");
                    }
                    //判断是否已经有进行中的判断
                    if (!loving) {
                        loving = true;
                        check();
                    }
                    break;
                case R.id.iv_walls_images:
                    if (mViewClickListener != null) {
                        mViewClickListener.showOriginalPic(wall.getContent_id());
                    }
                    break;
                case R.id.btn_del:
                    if (mViewClickListener != null) {
                        mViewClickListener.remove(wall.getContent_group_id());
                    }
                    break;
            }
        }

        boolean newClick;
        List<Integer> runningList = new ArrayList<Integer>();

        private void check() {
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
                        loving = false;
                    } catch (Exception e) {
                    }

                    if (TextUtils.isEmpty(wall.getLove_id())) {
                        doLove(wall, false);
                    } else {
                        doLove(wall, true);
                    }
                }
            }).start();
        }

        private void doLove(final WallEntity wallEntity, final boolean love) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("content_id", wallEntity.getContent_id());
            params.put("love", love ? "1" : "0");// 0-取消，1-赞
            params.put("user_id", "" + MainActivity.getUser().getUser_id());

            RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE, params);

            new HttpTools(mContext).post(requestInfo, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onResult(String response) {

                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });

        }
    }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;

            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        public ViewClickListener mViewClickListener;

        public void setPicClickListener(ViewClickListener viewClickListener) {
            mViewClickListener = viewClickListener;
        }

        public interface ViewClickListener {
            public void showOriginalPic(final String content_id);

            public void remove(final String content_group_id);
        }

    }