package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.MessageChatActivity;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WallCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WallCommentAdapter.class.getSimpleName();

    private Context mContext;
    private List<WallCommentEntity> data;

    public WallCommentAdapter(Context context, List<WallCommentEntity> data) {
        mContext = context;
        this.data = data;
    }

    public void addData(List<WallCommentEntity> newData) {
        data.addAll(newData);
        //        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(mContext).inflate(R.layout.wall_comment_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        VHItem item = (VHItem) viewHolder;
        WallCommentEntity comment = data.get(i);
        VolleyUtil.initNetworkImageView(mContext, item.civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, comment.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
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
    }

    private void setCommentPic(GifImageView iv, NetworkImageView niv, WallCommentEntity comment) {
        Log.i(TAG, "setCommentPic& file_id: " + comment.getFile_id() + "; StickerName is " + comment.getSticker_name());
        if(!TextUtils.isEmpty(comment.getFile_id())) {
            niv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);
            VolleyUtil.initNetworkImageView(mContext, niv, String.format(Constant.API_GET_COMMENT_PIC, Constant.Module_preview_m, comment.getUser_id(), comment.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        } else if(!TextUtils.isEmpty(comment.getSticker_group_path())) {
            iv.setVisibility(View.VISIBLE);
            niv.setVisibility(View.GONE);
            if(Constant.Sticker_Gif.equals(comment.getSticker_type())) {
                String stickerGroupPath = comment.getSticker_group_path();
                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }
                try {
                    String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.gif";
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
                    String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.png";
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
                            if(bitmap != null && gifImageView != null) {
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
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircularNetworkImage civ_comment_owner_head;
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
            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
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
            int position = getAdapterPosition();
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
        void doLove(final WallCommentEntity commentEntity, final boolean love);

        void doDelete(String commentId);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public WallViewClickListener mViewClickListener;

    public void setPicClickListener(WallViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

}