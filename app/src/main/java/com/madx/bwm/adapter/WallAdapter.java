package com.madx.bwm.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.WallEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.interfaces.WallViewClickListener;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.LocationUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.util.WallUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.VHItem> {
    private static final String TAG = WallAdapter.class.getSimpleName();

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
    public void onBindViewHolder(final WallAdapter.VHItem holder, int position) {
        final WallEntity wall = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wall.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        String atDescription = TextUtils.isEmpty(wall.getText_description()) ? "" : wall.getText_description();
        holder.tvContent.setText(atDescription);
        Log.i(TAG, "onBindViewHolder& description: " + atDescription);

        int tagMemberCount = wall.getTag_member() == null ? 0 : wall.getTag_member().size();
        int tagGroupCount = wall.getTag_member() == null ? 0 : wall.getTag_group().size();
        if(tagMemberCount > 0 || tagGroupCount > 0) {
            // 有TAG用户或分组需要显示字符特效
            WallUtil util = new WallUtil(mContext, mViewClickListener);
            util.setSpanContent(holder.tvContent, wall, atDescription, tagMemberCount, tagGroupCount);
        } else {
            holder.tvContent.setOnClickListener(holder);
        }

        // 显示发表的时间
        holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, wall.getContent_creation_date()));
        // 用户名
        holder.tvUserName.setText(wall.getUser_given_name());

        // file_id 为空表示没有发表图片，有则需要显示图片
        if(TextUtils.isEmpty(wall.getFile_id())) {
            holder.llWallsImage.setVisibility(View.GONE);
        } else {
            holder.llWallsImage.setVisibility(View.VISIBLE);

            // 有图片显示图片总数
            int count = Integer.valueOf(wall.getPhoto_count());
            if(count > 1) {
                String photoCountStr;
                photoCountStr = count + " " + mContext.getString(R.string.text_photos);
                holder.tvPhotoCount.setText(photoCountStr);
                holder.tvPhotoCount.setVisibility(View.VISIBLE);
            } else {
                holder.tvPhotoCount.setVisibility(View.GONE);
            }

            VolleyUtil.initNetworkImageView(mContext, holder.imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, wall.getUser_id(), wall.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        }
         /*is owner wall*/
        //        if (!TextUtils.isEmpty(wall.getUser_id())&&wall.getUser_id().equals("49")) {
        //            holder.ibDelete.setVisibility(View.VISIBLE);
        //        } else {
        //            holder.ibDelete.setVisibility(View.GONE);
        //        }

        try {
            if(wall.getDofeel_code() != null) {
                StringBuilder b = new StringBuilder(wall.getDofeel_code());
                int charIndex = wall.getDofeel_code().lastIndexOf("_");
                b.replace(charIndex, charIndex + 1, "/");

                InputStream is = mContext.getAssets().open(b.toString());
                if(is != null) {
                    holder.iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
                } else {
                    holder.iv_mood.setVisibility(View.GONE);
                }
            } else {
                holder.iv_mood.setVisibility(View.GONE);
            }
        } catch(Exception e) {
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


        if(MainActivity.getUser().getUser_id().equals(wall.getUser_id())) {
            holder.btn_del.setVisibility(View.VISIBLE);
        } else {
            holder.btn_del.setVisibility(View.GONE);
        }

        if(TextUtils.isEmpty(wall.getLove_id())) {
            holder.ibAgree.setImageResource(R.drawable.love_normal);
        } else {
            holder.ibAgree.setImageResource(R.drawable.love_press);
        }

        String locationName = wall.getLoc_name();
        if(TextUtils.isEmpty(locationName) || TextUtils.isEmpty(wall.getLoc_latitude()) || TextUtils.isEmpty(wall.getLoc_longitude())) {
            holder.llLocation.setVisibility(View.GONE);
        } else {
            holder.llLocation.setVisibility(View.VISIBLE);
            holder.tvLocation.setText(locationName);
            holder.llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(wall);
                }
            });
            holder.tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(wall);
                }
            });
            holder.ivLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(wall);
                }
            });
        }

    }

    private void gotoLocationSetting(WallEntity wall) {
        if(TextUtils.isEmpty(wall.getLoc_latitude()) || TextUtils.isEmpty(wall.getLoc_longitude())) {
            return;
        }

        LocationUtil.goNavigation(mContext, Double.valueOf(wall.getLoc_latitude()), Double.valueOf(wall.getLoc_longitude()), wall.getLoc_type());

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private int mLastPosition;

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * 头像视图
         */
        CircularNetworkImage nivHead;
        TextView tvContent;

        /**
         * 发表日期显示视图
         */
        TextView tvDate;

        /**
         * 用户昵称视图
         */
        TextView tvUserName;

        View llWallsImage;

        /**
         * 显示网络图片的视图控件
         */
        NetworkImageView imWallsImages;

        /**
         * 用于显示图片总数的提示控件
         */
        TextView tvPhotoCount;

        /**
         * 红星总数视图
         */
        TextView tvAgreeCount;

        /**
         * 评论总数视图
         */
        TextView tvCommentCount;

        /**
         * 红心按钮
         */
        ImageButton ibAgree;
        ImageButton ibComment;
        ImageButton btn_del;
        ImageView iv_mood;
        // location tag
        LinearLayout llLocation;
        ImageView ivLocation;
        TextView tvLocation;

        public VHItem(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            nivHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_wall_content);
            tvDate = (TextView) itemView.findViewById(R.id.push_date);
            llWallsImage = itemView.findViewById(R.id.ll_walls_image);
            imWallsImages = (NetworkImageView) itemView.findViewById(R.id.iv_walls_images);
            tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_wall_photo_count);
            tvAgreeCount = (TextView) itemView.findViewById(R.id.tv_wall_agree_count);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tv_wall_relay_count);
            ibAgree = (ImageButton) itemView.findViewById(R.id.iv_love);
            ibComment = (ImageButton) itemView.findViewById(R.id.iv_comment);
            btn_del = (ImageButton) itemView.findViewById(R.id.btn_del);
            iv_mood = (ImageView) itemView.findViewById(R.id.iv_mood);
            llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
            ivLocation = (ImageView) itemView.findViewById(R.id.iv_location);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvContent.setMaxLines(9);

            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 字符显示超过9行，只显示到第九行
                    int lineCount = tvContent.getLineCount();
                    if(lineCount > 8) {
                        // 第九行只显示十个字符
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(8);
                        int maxLineStartIndex = tvContent.getLayout().getLineStart(8);
                        String sourceText = tvContent.getText().toString();
                        if(maxLineEndIndex - maxLineStartIndex > 7) {
                            // 截取到第九行文字的第7个字符，其余字符用...替代
                            String newText = sourceText.substring(0, maxLineStartIndex + 7) + "...";
                            tvContent.setText(newText);
                        } else if(lineCount > 9) {
                                // 截取到第九行文字未满7个字符，行数超过9号，说明有换行，将换行替换成"..."
                                String newText = sourceText.substring(0, maxLineEndIndex - 1) + "...";
                                tvContent.setText(newText);
                        }

                    }
                }
            });

            itemView.findViewById(R.id.top_event).setOnClickListener(this);
            ibAgree.setOnClickListener(this);
            btn_del.setOnClickListener(this);
            imWallsImages.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            WallEntity wallEntity = data.get(position);
            switch(v.getId()) {
                case R.id.iv_love:
                    newClick = true;
                    int count = Integer.valueOf(tvAgreeCount.getText().toString());
                    if(TextUtils.isEmpty(wallEntity.getLove_id())) {
                        tvAgreeCount.setText(count + 1 + "");
                        ibAgree.setImageResource(R.drawable.love_press);
                        wallEntity.setLove_id(MainActivity.getUser().getUser_id());
                    } else {
                        ibAgree.setImageResource(R.drawable.love_normal);
                        wallEntity.setLove_id(null);
                        tvAgreeCount.setText(count - 1 + "");
                    }
                    //判断是否已经有进行中的判断
                    if(!runningList.contains(position)) {
                        runningList.add(position);
                        check(position);
                    }
                    break;
                case R.id.tv_wall_content:
                case R.id.top_event:
                    if(mViewClickListener != null) {
                        mViewClickListener.showComments(wallEntity.getContent_group_id(), wallEntity.getGroup_id());
                    }
                    break;
                case R.id.iv_walls_images:
                    if(mViewClickListener != null) {
                        mViewClickListener.showOriginalPic(wallEntity.getContent_id());
                    }
                    break;
                case R.id.btn_del:
                    if(mViewClickListener != null) {
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
                    while(nowTime - startTime < 1000) {
                        if(newClick) {
                            startTime = System.currentTimeMillis();
                            newClick = false;
                        }
                        nowTime = System.currentTimeMillis();
                    }
                    try {
                        runningList.remove(position);
                    } catch(Exception e) {
                    }

                    final WallEntity wallEntity = data.get(position);
                    if(TextUtils.isEmpty(wallEntity.getLove_id())) {
                        doLove(wallEntity, false);
                    } else {
                        doLove(wallEntity, true);
                    }
                }
            }).start();
        }

        private void doLove(final WallEntity wallEntity, final boolean love) {

            HashMap<String, String> params = new HashMap<>();
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

    public WallViewClickListener mViewClickListener;

    public void setPicClickListener(WallViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

}