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
import com.madx.bwm.entity.WallEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.VHItem> {
    private Context mContext;
    private List<WallEntity> data;


    public WallAdapter(Context context, List<WallEntity> data) {
        mContext = context;
        this.data = data;
    }

    @Override
    public WallAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }

    public void add(List<WallEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(data.size());
    }



    @Override
    public void onBindViewHolder(WallAdapter.VHItem holder, int position) {
        WallEntity ece = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, ece.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

//        if (TextUtils.isEmpty(ece.getText_description())) {
//            holder.tvContent.setVisibility(View.GONE);
//        } else {
            String atMemberDesc = ece.getTag_member().size()==0?"":String.format(mContext.getString(R.string.text_wall_content_at_member_desc),(ece.getTag_member().size()));
            String atGroupDesc = ece.getTag_group().size()==0?"":String.format(mContext.getString(R.string.text_wall_content_at_group_desc),(ece.getTag_group().size()));
            String wallContent = String.format(mContext.getString(R.string.text_wall_content_template),TextUtils.isEmpty(ece.getText_description())?"":ece.getText_description(),atGroupDesc,atMemberDesc);
            holder.tvContent.setText(wallContent);
//        }

        holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, ece.getContent_creation_date()));
//            holder.tvTime.setText(ece.getTime());
        holder.tvUserName.setText(ece.getUser_given_name());
        if (TextUtils.isEmpty(ece.getFile_id())) {
            holder.imWallsImages.setVisibility(View.GONE);
        } else {
            holder.imWallsImages.setVisibility(View.VISIBLE);
            VolleyUtil.initNetworkImageView(mContext, holder.imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, ece.getUser_id(), ece.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        }

         /*is owner wall*/
//        if (!TextUtils.isEmpty(ece.getUser_id())&&ece.getUser_id().equals("49")) {
//            holder.ibDelete.setVisibility(View.VISIBLE);
//        } else {
//            holder.ibDelete.setVisibility(View.GONE);
//        }

        try {
            if(ece.getDofeel_code()!=null) {
                StringBuilder b = new StringBuilder(ece.getDofeel_code());
                int charIndex = ece.getDofeel_code().lastIndexOf("_");
                b.replace(charIndex, charIndex + 1, "/");

                InputStream is = mContext.getAssets().open(b.toString());
                if (is != null) {
                    holder.iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
                } else {
                    holder.iv_mood.setVisibility(View.GONE);
                }
            }else{
                holder.iv_mood.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.iv_mood.setVisibility(View.GONE);
        }

        /*location*/
//        if (TextUtils.isEmpty(ece.getLoc_name())) {
//            holder.at.setVisibility(View.GONE);
//        } else {
//            holder.at.setVisibility(View.VISIBLE);
//            holder.tvLocation.setText(ece.getLoc_name());
//        }

        holder.tvAgreeCount.setText(ece.getLove_count());
        holder.tvCommentCount.setText(ece.getComment_count());


        if (MainActivity.getUser().getUser_id().equals(ece.getUser_id())) {
            holder.btn_del.setVisibility(View.VISIBLE);
        } else {
            holder.btn_del.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(ece.getLove_id())) {
            holder.ibAgree.setImageResource(R.drawable.love_normal);
        } else {
            holder.ibAgree.setImageResource(R.drawable.love_press);
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private int mLastPosition;

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        public VHItem(View itemView) {
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewClickListener != null) {
                        WallEntity wallEntity = data.get(getAdapterPosition());
                        mViewClickListener.showComments(wallEntity.getContent_group_id(), wallEntity.getGroup_id());
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            WallEntity wallEntity = data.get(position);
            switch (v.getId()) {
                case R.id.iv_love:
                    newClick = true;
                    int count = Integer.valueOf(tvAgreeCount.getText().toString());
                    if (TextUtils.isEmpty(wallEntity.getLove_id())) {
                        tvAgreeCount.setText(count + 1 + "");
                        ibAgree.setImageResource(R.drawable.love_press);
                        wallEntity.setLove_id(MainActivity.getUser().getUser_id());
                    } else {
                        ibAgree.setImageResource(R.drawable.love_normal);
                        wallEntity.setLove_id(null);
                        tvAgreeCount.setText(count - 1 + "");
                    }
                    //判断是否已经有进行中的判断
                    if (!runningList.contains(position)) {
                        runningList.add(position);
                        check(position);
                    }
                    break;
                case R.id.iv_comment:
                    if (mViewClickListener != null) {
                        mViewClickListener.showComments(wallEntity.getContent_group_id(), wallEntity.getGroup_id());
                    }
                    break;
                case R.id.iv_walls_images:
                    if (mViewClickListener != null) {
                        mViewClickListener.showOriginalPic(wallEntity.getContent_id());
                    }
                    break;
                case R.id.btn_del:
                    if (mViewClickListener != null) {
                        mViewClickListener.remove(wallEntity.getContent_group_id());
                    }
                    break;
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

                    final WallEntity wallEntity = data.get(position);
                    if (TextUtils.isEmpty(wallEntity.getLove_id())) {
                        doLove(wallEntity, false);
                    } else {
                        doLove(wallEntity, true);
                    }
                }
            }).start();
        }

        private void doLove(final WallEntity wallEntity, final boolean love) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("content_id", wallEntity.getContent_id());
            params.put("love", love ? "1" : "0");// 0-取消，1-赞
            params.put("user_id", "" + MainActivity.getUser().getUser_id());

            RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE,params);

            new HttpTools(mContext).post(requestInfo,new HttpCallback() {
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

    public ViewClickListener mViewClickListener;

    public void setPicClickListener(ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

    public interface ViewClickListener {
        public void showOriginalPic(final String content_id);

        public void showComments(final String content_group_id, final String group_id);

        public void remove(final String content_group_id);
    }


}