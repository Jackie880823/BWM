package com.madxstudio.co8.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.ArchiveChatEntity;
import com.madxstudio.co8.entity.ArchiveCommentEntity;
import com.madxstudio.co8.interfaces.ArchiveChatViewClickListener;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.SDKUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context mContext;
    private List<ArchiveCommentEntity> data;
    private List<ArchiveChatEntity> detailData;
    private int detailItemCount;

    public ArchiveCommentAdapter(Context context, List<ArchiveCommentEntity> data, List<ArchiveChatEntity> detailData) {
        mContext = context;
        this.detailData = detailData;
        this.data = data;
        if (detailData != null && detailData.size() > 0) {
            detailItemCount = 1;
        }
    }

    public void addData(List<ArchiveCommentEntity> newData) {
        data.addAll(newData);
        notifyItemInserted(getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(mContext).inflate(R.layout.fragment_archive_comment_head, parent, false);
                viewHolder = new VHHeader(view);
//                return new VHHeader(view);
                break;
            case TYPE_ITEM:
                view = LayoutInflater.from(mContext).inflate(R.layout.archive_comment_item, parent, false);
                viewHolder = new VHItem(view);
//                return new VHItem(view);
                break;
        }
        return viewHolder;
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            VHHeader item = (VHHeader) holder;
            ArchiveChatEntity archive = detailData.get(0);
            /*
            if(TextUtils.isEmpty(archive.getFile_id())){
                item.llArchiveImage.setVisibility(View.GONE);
            }else {
                //如果有照片
                item.llArchiveImage.setVisibility(View.VISIBLE);
                //照片的总数
                int PhotoCount = Integer.valueOf(archive.getPhoto_count());
                if(PhotoCount > 1){
                    String photoCountStr;
                    photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
                    item.tvPhotoCount.setText(photoCountStr);
                    item.tvPhotoCount.setVisibility(View.VISIBLE);
                }else {
                    item.tvPhotoCount.setVisibility(View.GONE);
                }
                //加载照片
                BitmapTools.getInstance(mContext).display( item.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }
            item.tvUserName.setText(archive.getGroup_name());

            String Content = archive.getText_description();
            if(TextUtils.isEmpty(Content)){
//            holder.liContent.setVisibility(View.VISIBLE);
                item.liContent.setVisibility(View.GONE);
            }else {
                item.tvContent.setText(Content);
                item.liContent.setVisibility(View.VISIBLE);
            }
            item.tvLoveCount.setText(archive.getLove_count());
            item.tvCommentCount.setText(archive.getComment_count());
//        String tempdate = MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date());
            item.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date()));//时间有问题
            */

            item.tvUserName.setText(archive.getGroup_name());
            String Content = archive.getText_description();
            String locationName = archive.getLoc_name();

            if (!TextUtils.isEmpty(Content)) {
//            holder.liContent.setVisibility(View.VISIBLE);
                item.tvContent.setText(Content);
                item.liContent.setVisibility(View.VISIBLE);
            } else {
                item.liContent.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(locationName)) {
                item.locationName.setText(locationName);
                item.lllocation.setVisibility(View.VISIBLE);
            } else {
                item.lllocation.setVisibility(View.GONE);
            }
            item.tvLoveCount.setText(archive.getLove_count());
            item.tvCommentCount.setText(archive.getComment_count());
//        String tempdate = MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date());
            item.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date()));
//        holder.tvDate.setText(tempdate);
            setCommentPic(item, archive);
        } else {
            VHItem item = (VHItem) holder;
            ArchiveCommentEntity commentEntity = data.get(position - detailItemCount);
            item.tvUserName.setText(commentEntity.getUser_given_name());
            item.tvContent.setText(commentEntity.getComment_content());
            item.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, commentEntity.getComment_creation_date()));
        }
    }

    private void setCommentPic(RecyclerView.ViewHolder item, ArchiveChatEntity archive) {
        VHHeader holder = (VHHeader) item;
        if (!TextUtils.isEmpty(archive.getFile_id())) {
//            Log.i("照片====", archive.getFile_id());
            //如果有照片
            holder.llArchiveImage.setVisibility(View.VISIBLE);
            holder.imArchiveImages.setVisibility(View.VISIBLE);
            holder.tvPhotoCount.setVisibility(View.VISIBLE);
            holder.imArchivePic.setVisibility(View.GONE);
            //照片的总数
            int PhotoCount = Integer.valueOf(archive.getPhoto_count());
            if (PhotoCount > 1) {
                String photoCountStr;
                photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
                holder.tvPhotoCount.setText(photoCountStr);
                holder.tvPhotoCount.setVisibility(View.VISIBLE);
            } else {
                holder.tvPhotoCount.setVisibility(View.GONE);
            }
            //加载照片
            BitmapTools.getInstance(mContext).display(holder.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        } else if (!TextUtils.isEmpty(archive.getSticker_group_path())) {
            //如果有大表情
            holder.llArchiveImage.setVisibility(View.VISIBLE);
            holder.imArchivePic.setVisibility(View.VISIBLE);
            holder.imArchivePic.setImageDrawable(null);
            holder.imArchiveImages.setVisibility(View.GONE);
            holder.tvPhotoCount.setVisibility(View.GONE);
            if (Constant.Sticker_Gif.equals(archive.getSticker_type())) {
//                Log.i("GIF====",archive.getSticker_type());
                //如果大表情是GIF
                String stickerGroupPath = archive.getSticker_group_path();
                if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }
                try {
//                    String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + archive.getSticker_name() + "_B.gif";
                    String gifFilePath = FileUtil.getBigStickerPath(mContext, stickerGroupPath, archive.getSticker_name(), archive.getSticker_type());
                    GifDrawable gifDrawable = new GifDrawable(new File(gifFilePath));
                    if (gifDrawable != null) {
                        holder.imArchivePic.setImageDrawable(gifDrawable);
                        //                    if ("true".equals(comment.getIsNate())) {
                        //                        holder.progressBar.setVisibility(View.VISIBLE);
                        //                    } else {
                        //                        holder.progressBar.setVisibility(View.GONE);
                        //                    }
                    } else {
                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, archive.getSticker_type());
                        downloadAsyncTask(holder.imArchivePic, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
                    }
                } catch (IOException e) {
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, archive.getSticker_type());
                    downloadAsyncTask(holder.imArchivePic, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
                    e.printStackTrace();
                }


            } else if (Constant.Sticker_Png.equals(archive.getSticker_type())) {
//                Log.i("PNG====",archive.getSticker_type());
                //如果大表情是PNG
                String stickerGroupPath = archive.getSticker_group_path();
                if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }

                try {
//                    String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + archive.getSticker_name() + "_B.png";
                    String pngFileName = FileUtil.getBigStickerPath(mContext, stickerGroupPath, archive.getSticker_name(), archive.getSticker_type());
//                    InputStream is = mContext.getAssets().open(pngFileName);
                    InputStream is = new FileInputStream(new File(pngFileName));
                    if (is != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        holder.imArchivePic.setImageBitmap(bitmap);
                    } else {
                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                        downloadAsyncTask(holder.imArchivePic, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
                    }
                } catch (IOException e) {
                    //本地没有png的时候，从服务器下载
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                    downloadAsyncTask(holder.imArchivePic, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
                    e.printStackTrace();
                }
            }

        } else {
            holder.llArchiveImage.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return data.size() + detailItemCount;
    }

    class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * 用户昵称视图
         */
        TextView tvUserName;
        /**
         * 用于显示图片总数的提示控件
         */
        TextView tvPhotoCount;
        /**
         * 发表日期显示视图
         */
        TextView tvDate;
        /**
         * 评论总数视图
         */
        TextView tvCommentCount;
        /**
         * 喜欢的总数视图
         */
        TextView tvLoveCount;
        /**
         * 内容视图
         */
        TextView tvContent;

        View liContent;

        View llArchiveImage;

        /**
         * 显示网络图片的视图控件
         */
        /**
         * 显示大表情
         */
        GifImageView imArchivePic;
        /**
         * 地址名称
         */
        TextView locationName;
        View lllocation;

        NetworkImageView imArchiveImages;

        public VHHeader(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvDate = (TextView) itemView.findViewById(R.id.up_time);
            tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_chat_photo_count);
            llArchiveImage = itemView.findViewById(R.id.ll_chats_image);
            tvCommentCount = (TextView) itemView.findViewById(R.id.member_comment);
            lllocation = itemView.findViewById(R.id.lo_location);
            tvLoveCount = (TextView) itemView.findViewById(R.id.memeber_love);
            liContent = itemView.findViewById(R.id.li_content);
            locationName = (TextView) itemView.findViewById(R.id.tv_archive_location_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
            imArchivePic = (GifImageView) itemView.findViewById(R.id.iv_chats_pic);
            tvContent.setMaxLines(9);
            imArchiveImages = (NetworkImageView) itemView.findViewById(R.id.iv_chats_images);
            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 字符显示超过9行，只显示到第九行
                    int lineCount = tvContent.getLineCount();
                    if (lineCount > 8) {
                        // 第九行只显示十个字符
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(8);
                        int maxLineStartIndex = tvContent.getLayout().getLineStart(8);
                        String sourceText = tvContent.getText().toString();
                        if (maxLineEndIndex - maxLineStartIndex > 7) {
                            // 截取到第九行文字的第7个字符，其余字符用...替代
                            String newText = sourceText.substring(0, maxLineStartIndex + 7) + "...";
                            tvContent.setText(newText);
                        } else if (lineCount > 9) {
                            // 截取到第九行文字未满7个字符，行数超过9号，说明有换行，将换行替换成"..."
                            String newText = sourceText.substring(0, maxLineEndIndex - 1) + "...";
                            tvContent.setText(newText);
                        }

                    }
                }
            });

            imArchiveImages.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ArchiveChatEntity archive = detailData.get(0);
            switch (v.getId()) {
                case R.id.iv_chats_images:
                    if (marchiveChatViewClickListener != null) {
                        marchiveChatViewClickListener.showOriginalPic(archive);
                    }
            }
        }
    }


    class VHItem extends RecyclerView.ViewHolder {
        /**
         * 用户昵称视图
         */
        TextView tvUserName;
        /**
         * 内容视图
         */
        TextView tvContent;
        /**
         * 发表日期显示视图
         */
        TextView tvDate;


        public VHItem(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
            tvDate = (TextView) itemView.findViewById(R.id.up_time);
            tvContent.setMaxLines(9);
            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 字符显示超过9行，只显示到第九行
                    int lineCount = tvContent.getLineCount();
                    if (lineCount > 8) {
                        // 第九行只显示十个字符
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(8);
                        int maxLineStartIndex = tvContent.getLayout().getLineStart(8);
                        String sourceText = tvContent.getText().toString();
                        if (maxLineEndIndex - maxLineStartIndex > 7) {
                            // 截取到第九行文字的第7个字符，其余字符用...替代
                            String newText = sourceText.substring(0, maxLineStartIndex + 7) + "...";
                            tvContent.setText(newText);
                        } else if (lineCount > 9) {
                            // 截取到第九行文字未满7个字符，行数超过9号，说明有换行，将换行替换成"..."
                            String newText = sourceText.substring(0, maxLineEndIndex - 1) + "...";
                            tvContent.setText(newText);
                        }

                    }
                }
            });

        }
    }

    /**
     * 此方法用来异步加载图片
     *
     * @param path
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void downloadAsyncTask(final GifImageView gifImageView, final String path, final String type, final int defaultResource) {
        AsyncTask task = new AsyncTask<Object, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(Object... params) {
                return NetworkUtil.getImageByte(params[0].toString());
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(byte[] resultByte) {
                super.onPostExecute(resultByte);
                try {
                    if (null != resultByte) {
                        if (Constant.Sticker_Png.equals(type)) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(resultByte, 0, resultByte.length);
                            if (bitmap != null && gifImageView != null) {
                                gifImageView.setImageBitmap(bitmap);
                            } else {
                                gifImageView.setImageResource(defaultResource);
                            }
                        } else if (Constant.Sticker_Gif.equals(type)) {
                            GifDrawable gifDrawable = new GifDrawable(resultByte);
                            if (gifDrawable != null && gifImageView != null) {
                                gifImageView.setImageDrawable(gifDrawable);
                            } else {
                                gifImageView.setImageResource(defaultResource);
                            }
                        }
                    } else {
                        gifImageView.setImageResource(defaultResource);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };
        //for not work in down 11
        if (SDKUtil.IS_HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        } else {
            task.execute(path);
        }

    }

    public ArchiveChatViewClickListener marchiveChatViewClickListener;

    public void setPicClickListener(ArchiveChatViewClickListener archiveChatViewClickListener) {
        marchiveChatViewClickListener = archiveChatViewClickListener;
    }
}
