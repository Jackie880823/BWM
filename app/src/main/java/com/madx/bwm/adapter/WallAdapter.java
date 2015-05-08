package com.madx.bwm.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.madx.bwm.interfaces.ViewClickListener;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.MyDateUtils;
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
    public void onBindViewHolder(WallAdapter.VHItem holder, int position) {
        final WallEntity wall = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wall.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        String atDescription = TextUtils.isEmpty(wall.getText_description()) ? "" : wall.getText_description();
        holder.tvContent.setText(atDescription);
        // 设置文字可点击，实现特殊文字点击跳转必需添加些设置
        holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        if (wall.getTag_member().size() > 0) {
            String strMember = String.format(mContext.getString(R.string.text_wall_content_at_member_desc), wall.getTag_member().size());
            // 文字特殊效果设置
            SpannableString ssMember = new SpannableString(strMember);

            // 给文字添加点击响应，跳转至显示被@的用户
            ssMember.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if(mViewClickListener != null) {
                        Log.i(TAG, "onClick& mViewClickListener not null showMembers");
                        mViewClickListener.showMembers(wall.getContent_group_id(), wall.getGroup_id());
                    } else {
                        Log.i(TAG, "onClick& mViewClickListener do nothing");
                    }
                }
            }, 0, strMember.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 设置文字的前景色为蓝色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
            ssMember.setSpan(colorSpan, 0, ssMember.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvContent.append(ssMember);
        }
        if (wall.getTag_group().size() > 0){
            String strGroup = String.format(mContext.getString(R.string.text_wall_content_at_group_desc), wall.getTag_group().size());
            // 文字特殊效果设置
            SpannableString ssGroup = new SpannableString(strGroup);

            // 给文字添加点击响应，跳转至显示被@的群组
            ssGroup.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if(mViewClickListener != null) {
                        Log.i(TAG, "onClick& mViewClickListener not null showGroups");
                        mViewClickListener.showGroups(wall.getContent_group_id(), wall.getGroup_id());
                    } else {
                        Log.i(TAG, "onClick& mViewClickListener do nothing");
                    }
                }
            }, 0, strGroup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 设置文字的前景色为蓝色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
            ssGroup.setSpan(colorSpan, 0, ssGroup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if(wall.getTag_member().size() > 0) {
                // 同时@了用户和群组，用户和群组之间用&分开
                holder.tvContent.append(" & ");
            }
            holder.tvContent.append(ssGroup);
        }

        holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, wall.getContent_creation_date()));
        //            holder.tvTime.setText(wall.getTime());
        holder.tvUserName.setText(wall.getUser_given_name());
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

            ibAgree.setOnClickListener(this);
            //            ibComment.setOnClickListener(this);
            btn_del.setOnClickListener(this);
            imWallsImages.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mViewClickListener != null) {
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
                case R.id.iv_comment:
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

    public ViewClickListener mViewClickListener;

    public void setPicClickListener(ViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

}