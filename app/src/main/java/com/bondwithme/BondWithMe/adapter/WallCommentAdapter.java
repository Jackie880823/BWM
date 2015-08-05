package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.ViewOriginalPicesActivity;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class WallCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WallCommentAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_HEAD = 0;
    private static final int VIEW_TYPE_ICON = 1;

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

    public void setData(List<WallCommentEntity> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder& viewType is " + viewType);
        if(VIEW_TYPE_HEAD != viewType) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(mContext).inflate(R.layout.wall_comment_item, parent, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new VHItem(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_item, parent, false);
            return new VHHeadItem(view);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        if(i >= 1) {
            VHItem item = (VHItem) viewHolder;
            if(i == 1) {
                item.commentHead.setVisibility(View.VISIBLE);
                item.commentContent.setVisibility(View.GONE);
                updateListener.updateListHeadView(viewHolder.itemView);
                return;
            } else {
                item.commentContent.setVisibility(View.VISIBLE);
                item.commentHead.setVisibility(View.GONE);
            }

            WallCommentEntity comment = data.get(i - 2);
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
        } else {
            updateListener.updateWallView(viewHolder.itemView);
            return;
        }
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
                String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.gif";
                try {
                    //GifDrawable gifDrawable = new GifDrawable(context.getAssets(), gifFilePath);
                    Log.i("stickerPath", gifFilePath);
                    GifDrawable gifDrawable = new GifDrawable(new File(gifFilePath));
                    if(gifDrawable != null) {
                        iv.setImageDrawable(gifDrawable);
                    } else {
                        UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath, comment.getSticker_name(), R.drawable.network_image_default, iv, comment.getSticker_type());
                    }
                } catch(Exception e) {
                    UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath, comment.getSticker_name(), R.drawable.network_image_default, iv, comment.getSticker_type());
                    //                    LogUtil.e("", "插入sticker info", e);
                }
            } else if(Constant.Sticker_Png.equals(comment.getSticker_type())) {
                String stickerGroupPath = comment.getSticker_group_path();
                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }
                String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.png";
                try {
                    //拼接大图路径
                    //InputStream is = context.getAssets().open(pngFileName);//得到数据流
                    Log.i("stickerPath", pngFileName);
                    InputStream is = new FileInputStream(new File(pngFileName));//得到数据流
                    if(is != null) {//如果有图片直接显示，否则网络下载
                        Bitmap bitmap = BitmapFactory.decodeStream(is);//将流转化成Bitmap对象
                        iv.setImageBitmap(bitmap);//显示图片
                    } else {
                        UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath, comment.getSticker_name(), R.drawable.network_image_default, iv, comment.getSticker_type());
                    }
                } catch(Exception e) {
                    //本地没有png的时候，从服务器下载
                    UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath, comment.getSticker_name(), R.drawable.network_image_default, iv, comment.getSticker_type());
                    //                    LogUtil.e("", "插入sticker info", e);
                }
            }
        } else {
            niv.setVisibility(View.GONE);
            iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 2;
    }

    class VHHeadItem extends RecyclerView.ViewHolder {

        public VHHeadItem(View itemView) {
            super(itemView);
        }
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircularNetworkImage civ_comment_owner_head;
        TextView tv_comment_owner_name;
        FreedomSelectionTextView tv_comment_content;
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;
        GifImageView iv_comment_pic;
        NetworkImageView niv_comment_pic;
        View commentHead;
        View commentContent;

        public VHItem(View itemView) {
            super(itemView);
            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
            iv_comment_pic = (GifImageView) itemView.findViewById(R.id.iv_comment_pic);
            niv_comment_pic = (NetworkImageView) itemView.findViewById(R.id.niv_comment_pic);
            commentHead = itemView.findViewById(R.id.comment_head_ll);
            commentContent = itemView.findViewById(R.id.comment_content);
            itemView.findViewById(R.id.rl_agree).setOnClickListener(this);
            iv_agree.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);
            niv_comment_pic.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            WallCommentEntity commentEntity = data.get(position - 2);
            switch(v.getId()) {
                case R.id.rl_agree:
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
                case R.id.btn_comment_del: {
                    //自己发的或event creator 可以删除
                    if(mCommentActionListener != null) {
                        if(MainActivity.getUser().getUser_id().equals(commentEntity.getUser_id())) {
                            mCommentActionListener.doDelete(commentEntity.getComment_id());
                        }
                    }
                    break;
                }
                case R.id.niv_comment_pic: {
                    Intent intent = new Intent(mContext, ViewOriginalPicesActivity.class);

                    ArrayList<PhotoEntity> dataList = new ArrayList();

                    PhotoEntity peData = new PhotoEntity();
                    peData.setUser_id(commentEntity.getUser_id());
                    peData.setFile_id(commentEntity.getFile_id());
                    peData.setPhoto_caption(Constant.Module_Original);
                    peData.setPhoto_multipe("false");
                    dataList.add(peData);

                    intent.putExtra("is_data", true);
                    intent.putExtra("datas", dataList);
                    mContext.startActivity(intent);
                    break;
                }
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
                final WallCommentEntity commentEntity = data.get(position - 2);
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

    private ListViewItemViewUpdateListener updateListener;

    public void setUpdateListener(ListViewItemViewUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public interface ListViewItemViewUpdateListener {
        void updateWallView(View headView);

        void updateListHeadView(View listHeadView);
    }

}