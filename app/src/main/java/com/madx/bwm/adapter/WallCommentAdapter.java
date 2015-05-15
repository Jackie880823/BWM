package com.madx.bwm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
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
import com.madx.bwm.interfaces.ViewClickListener;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.Map4BaiduActivity;
import com.madx.bwm.ui.MessageChatActivity;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WallCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WallCommentAdapter.class.getSimpleName();

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
        if(viewType == TYPE_ITEM) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(mContext).inflate(R.layout.wall_comment_item, parent, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new VHItem(view);
        } else if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.wall_item, parent, false);
            return new VHHeader(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        if(viewHolder instanceof VHItem) {
            VHItem item = (VHItem) viewHolder;
            WallCommentEntity comment = data.get(i - 1);
            //            VolleyUtil.initNetworkImageView(mContext, item.civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, comment.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            item.tv_comment_owner_name.setText(comment.getUser_given_name());
            item.tv_comment_content.setText(comment.getComment_content());
            item.tv_agree_count.setText((TextUtils.isEmpty(comment.getLove_count()) ? "0" : comment.getLove_count()));
            item.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, comment.getComment_creation_date()));

            if(MainActivity.getUser().getUser_id().equals(comment.getUser_id())) {
                item.btn_comment_del.setVisibility(View.VISIBLE);
            } else {
                item.btn_comment_del.setVisibility(View.GONE);
            }

            if(TextUtils.isEmpty(comment.getLove_id())) {
                item.iv_agree.setImageResource(R.drawable.agree_normal);
            } else {
                item.iv_agree.setImageResource(R.drawable.agree_press);
            }
            setCommentPic(item.iv_comment_pic, item.niv_comment_pic, comment);
        } else if(viewHolder instanceof VHHeader) {

            VHHeader holder = (VHHeader) viewHolder;
            VolleyUtil.initNetworkImageView(mContext, holder.nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wall.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

            String atDescription = TextUtils.isEmpty(wall.getText_description()) ? "" : wall.getText_description();
            holder.tvContent.setText(atDescription);
            // 设置文字可点击，实现特殊文字点击跳转必需添加些设置
            holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            if(wall.getTag_member().size() > 0) {
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

                //设置文字的前景色为蓝色
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
                ssMember.setSpan(colorSpan, 0, strMember.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.tvContent.append(ssMember);
            }
            if(wall.getTag_group().size() > 0) {
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

                //设置文字的前景色为蓝色
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

                VolleyUtil.initNetworkImageView(mContext, holder.imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, wall.getUser_id(), wall.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);

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

            if(TextUtils.isEmpty(wall.getLoc_name())) {
                holder.llLocation.setVisibility(View.GONE);
            } else {
                holder.llLocation.setVisibility(View.VISIBLE);
                holder.tvLocation.setText(wall.getLoc_name());
            }

        }
    }

    private void setCommentPic(GifImageView iv, NetworkImageView niv, WallCommentEntity comment) {
        Log.i(TAG, "setCommentPic& file_id: " + comment.getFile_id() + "; StickerName is " + comment.getSticker_name());
        if(!TextUtils.isEmpty(comment.getFile_id())) {
            niv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);
            VolleyUtil.initNetworkImageView(mContext, niv, String.format(Constant.API_GET_COMMENT_PIC, Constant.Module_preview_m, comment.getUser_id(), comment.getFile_id()),
                    R.drawable.network_image_default, R.drawable.network_image_default);
        } else if(!TextUtils.isEmpty(comment.getSticker_group_path())) {
            iv.setVisibility(View.VISIBLE);
            niv.setVisibility(View.GONE);
            if(Constant.Sticker_Gif.equals(comment.getSticker_type())) {
                String stickerGroupPath = comment.getSticker_group_path();
                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }
                try {
                    String gifFilePath = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.gif";
                    GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(), gifFilePath);
                    if(gifDrawable != null) {
                        iv.setImageDrawable(gifDrawable);
                        //                    if ("true".equals(comment.getIsNate())) {
                        //                        holder.progressBar.setVisibility(View.VISIBLE);
                        //                    } else {
                        //                        holder.progressBar.setVisibility(View.GONE);
                        //                    }
                    } else {
                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, comment.getSticker_type());
                        downloadAsyncTask(iv, stickerUrl, comment.getSticker_type(), R.drawable.network_image_default);
                    }
                } catch(IOException e) {
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, comment.getSticker_type());
                    downloadAsyncTask(iv, stickerUrl, comment.getSticker_type(), R.drawable.network_image_default);
                    e.printStackTrace();
                }
            } else if(Constant.Sticker_Png.equals(comment.getSticker_type())) {
                String stickerGroupPath = comment.getSticker_group_path();
                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }

                try {
                    String pngFileName = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.png";
                    InputStream is = mContext.getAssets().open(pngFileName);
                    if(is != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        iv.setImageBitmap(bitmap);
                    } else {
                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                        downloadAsyncTask(iv, stickerUrl, comment.getSticker_type(), R.drawable.network_image_default);
                    }
                } catch(IOException e) {
                    //本地没有png的时候，从服务器下载
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                    downloadAsyncTask(iv, stickerUrl, comment.getSticker_type(), R.drawable.network_image_default);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 此方法用来异步加载图片
     *
     * @param path
     */
    private void downloadAsyncTask(final GifImageView gifImageView, final String path, final String type, final int defaultResource) {
        new AsyncTask<String, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(String... params) {
                return NetworkUtil.getImageByte(path);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(byte[] resultByte) {
                super.onPostExecute(resultByte);
                try {
                    if(null != resultByte) {
                        if(Constant.Sticker_Png.equals(type)) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(resultByte, 0, resultByte.length);
                            if (bitmap != null && gifImageView != null) {
                                gifImageView.setImageBitmap(bitmap);
                            } else {
                                gifImageView.setImageResource(defaultResource);
                            }
                        } else if(Constant.Sticker_Gif.equals(type)) {
                            GifDrawable gifDrawable = new GifDrawable(resultByte);
                            if(gifDrawable != null && gifImageView != null) {
                                gifImageView.setImageDrawable(gifDrawable);
                            } else {
                                gifImageView.setImageResource(defaultResource);
                            }
                        }
                    } else {
                        gifImageView.setImageResource(defaultResource);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }

        }.execute(new String[]{});

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
        GifImageView iv_comment_pic;
        NetworkImageView niv_comment_pic;

        public VHItem(View itemView) {
            super(itemView);
            //            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
            iv_comment_pic = (GifImageView) itemView.findViewById(R.id.iv_comment_pic);
            niv_comment_pic = (NetworkImageView) itemView.findViewById(R.id.niv_comment_pic);
            iv_agree.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition() - 1;
            WallCommentEntity commentEntity = data.get(position);
            switch(v.getId()) {
                case R.id.iv_agree:
                    newClick = true;
                    int count = Integer.valueOf(tv_agree_count.getText().toString());
                    if(TextUtils.isEmpty(commentEntity.getLove_id())) {
                        iv_agree.setImageResource(R.drawable.agree_press);
                        commentEntity.setLove_id(MainActivity.getUser().getUser_id());
                        tv_agree_count.setText(count + 1 + "");
                    } else {
                        iv_agree.setImageResource(R.drawable.agree_normal);
                        commentEntity.setLove_id(null);
                        tv_agree_count.setText(count - 1 + "");
                    }
                    //判断是否已经有进行中的判断
                    if(!runningList.contains(position)) {
                        runningList.add(position);
                        check(position);
                    }
                    break;
                case R.id.btn_comment_del:
                    WallCommentEntity comment = data.get(position);
                    //自己发的或event creator 可以删除
                    if(mCommentActionListener != null) {
                        if(MainActivity.getUser().getUser_id().equals(comment.getUser_id())) {
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
                final WallCommentEntity commentEntity = data.get(position);
                if(mCommentActionListener != null) {
                    if(TextUtils.isEmpty(commentEntity.getLove_id())) {
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
        /**
         * 时间
         */
        TextView tvDate;
        /**
         * 用户名
         */
        TextView tvUserName;
        View llWallsImage;
        /**
         * 分享的网络图片显示控件
         */
        NetworkImageView imWallsImages;
        /**
         * 图片数量统计显示
         */
        TextView tvPhotoCount;
        /**
         *
         */
        TextView tvAgreeCount;
        /**
         * 评论总数显示
         */
        TextView tvCommentCount;
        ImageButton ibAgree;
        ImageButton ibComment;
        ImageButton btn_del;
        ImageView iv_mood;
        // location tag
        LinearLayout llLocation;
        ImageView ivLocation;
        TextView tvLocation;

        public VHHeader(View itemView) {
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

            ivLocation.setOnClickListener(this);
            tvLocation.setOnClickListener(this);

            ibAgree.setOnClickListener(this);
            //            ibComment.setOnClickListener(this);
            btn_del.setOnClickListener(this);
            imWallsImages.setOnClickListener(this);
        }

        boolean loving = false;

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.iv_love:
                    newClick = true;
                    int count = Integer.valueOf(tvAgreeCount.getText().toString());
                    if(TextUtils.isEmpty(wall.getLove_id())) {
                        tvAgreeCount.setText(count + 1 + "");
                        ibAgree.setImageResource(R.drawable.love_press);
                        wall.setLove_id(MainActivity.getUser().getUser_id());
                    } else {
                        ibAgree.setImageResource(R.drawable.love_normal);
                        wall.setLove_id(null);
                        tvAgreeCount.setText(count - 1 + "");
                    }
                    Log.i("WallcommentAdapter", "love count = " + count);
                    //判断是否已经有进行中的判断
                    if(!loving) {
                        Log.i("WallcommentAdapter", "prepare love");
                        loving = true;
                        check();
                    } else {
                        Log.i("WallcommentAdapter", "not love");
                    }
                    break;
                case R.id.iv_walls_images:
                    if(mViewClickListener != null) {
                        mViewClickListener.showOriginalPic(wall.getContent_id());
                    }
                    break;
                case R.id.btn_del:
                    if(mViewClickListener != null) {
                        mViewClickListener.remove(wall.getContent_group_id());
                    }
                    break;
                case R.id.iv_location:
                case R.id.tv_location:
                    gotoLocationSetting(wall);
                    break;
            }
        }

        private void gotoLocationSetting(WallEntity wall) {
            if(!TextUtils.isEmpty(wall.getLoc_name())) {
                Intent intent = new Intent(mContext, Map4BaiduActivity.class);
                //        Intent intent = new Intent(getActivity(), Map4GoogleActivity.class);
                //        intent.putExtra("has_location", position_name.getText().toString());
                intent.putExtra("location_name", wall.getLoc_name());
                intent.putExtra("latitude", Double.valueOf(wall.getLoc_latitude()));
                intent.putExtra("longitude", Double.valueOf(wall.getLoc_longitude()));
                mContext.startActivity(intent);
            }
        }

        boolean newClick;
        List<Integer> runningList = new ArrayList<Integer>();

        private void check() {

            // 判断传进来的Context对象是否为Activity
            if(mContext instanceof Activity) {
                Log.i("WallCommentAdapter", "onResult setResult");
                // 数据有修改设置result 为 Activity.RESULT_OK
                ((Activity) mContext).setResult(Activity.RESULT_OK);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //缓冲时间为100
                        Thread.sleep(100);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        loving = false;
                    } catch(Exception e) {
                    }

                    if(TextUtils.isEmpty(wall.getLove_id())) {
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
                    Log.i("WallCommentAdapter", "onStart");
                }

                @Override
                public void onFinish() {
                    Log.i("WallCommentAdapter", "onFinish");
                }

                @Override
                public void onResult(String response) {
                    Log.i("WallCommentAdapter", "onResult");
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
        if(isPositionHeader(position))
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

}