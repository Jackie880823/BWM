package com.bondwithme.BondWithMe.adapter;

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
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.ArchiveChatEntity;
import com.bondwithme.BondWithMe.exception.StickerTypeException;
import com.bondwithme.BondWithMe.interfaces.ArchiveChatViewClickListener;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveChatAdapter extends RecyclerView.Adapter<ArchiveChatAdapter.VHItem>{
    private Context mContext;
    private List<ArchiveChatEntity> data = new ArrayList<>();
    private String Tap;

    public ArchiveChatAdapter(Context context,List<ArchiveChatEntity> data,String Tap){
        mContext = context;
        this.data = data;
        this.Tap = Tap;
    }

    public void add(List<ArchiveChatEntity> newData){
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public ArchiveChatAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archivechat_item, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(ArchiveChatAdapter.VHItem holder, int position) {
        final ArchiveChatEntity archive = data.get(position);
//        holder.group_name.setText(archive.getComment_count());
        /*
        if(TextUtils.isEmpty(archive.getFile_id())){
            holder.llArchiveImage.setVisibility(View.GONE);
        }else {
            //如果有照片
            holder.llArchiveImage.setVisibility(View.VISIBLE);
            //照片的总数
            int PhotoCount = Integer.valueOf(archive.getPhoto_count());
            if(PhotoCount > 1){
                String photoCountStr;
                photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
                holder.tvPhotoCount.setText(photoCountStr);
                holder.tvPhotoCount.setVisibility(View.VISIBLE);
            }else {
                holder.tvPhotoCount.setVisibility(View.GONE);
            }
            //加载照片
            BitmapTools.getInstance(mContext).display( holder.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        }
        */
        if("1".equals(Tap)){
            holder.tvUserName.setText(archive.getUser_given_name());
        }else {
            holder.tvUserName.setText(archive.getUser_given_name());
        }
        String Content = archive.getText_description();
        String locationName = archive.getLoc_name();

        if(!TextUtils.isEmpty(Content)){
//            holder.liContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(Content);
            holder.liContent.setVisibility(View.VISIBLE);
        } else {
            holder.liContent.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(locationName)){
            holder.locationName.setText(locationName);
            holder.lllocation.setVisibility(View.VISIBLE);
        }else {
            holder.lllocation.setVisibility(View.GONE);
        }
        holder.tvLoveCount.setText(archive.getLove_count());
        holder.tvCommentCount.setText(archive.getComment_count());
//        String tempdate = MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date());
        holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date()));
//        holder.tvDate.setText(tempdate);
        setCommentPic(holder,archive);
    }

    private void setCommentPic(ArchiveChatAdapter.VHItem holder,ArchiveChatEntity archive){
        if(!TextUtils.isEmpty(archive.getFile_id())){
            //如果有照片
            holder.llArchiveImage.setVisibility(View.VISIBLE);
            holder.imArchiveImages.setVisibility(View.VISIBLE);
            holder.tvPhotoCount.setVisibility(View.VISIBLE);
            holder.imArchivePic.setVisibility(View.GONE);
            holder.imArchiveGif.setVisibility(View.GONE);
            holder.imArchiveGif.setImageDrawable(null);
            //照片的总数
            int PhotoCount = Integer.valueOf(archive.getPhoto_count());
            if(PhotoCount > 1){
                String photoCountStr;
                photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
                holder.tvPhotoCount.setText(photoCountStr);
                holder.tvPhotoCount.setVisibility(View.VISIBLE);
            }else {
                holder.tvPhotoCount.setVisibility(View.GONE);
            }
            //加载照片
            BitmapTools.getInstance(mContext).display(holder.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            //如果有大表情
        }else if(!TextUtils.isEmpty(archive.getSticker_group_path())){
            holder.llArchiveImage.setVisibility(View.VISIBLE);
            holder.imArchiveGif.setVisibility(View.VISIBLE);
            holder.imArchivePic.setVisibility(View.GONE);
            holder.imArchiveImages.setVisibility(View.GONE);
            holder.imArchiveGif.setImageDrawable(null);
            holder.tvPhotoCount.setVisibility(View.GONE);
            try {
                UniversalImageLoaderUtil.decodeStickerPic(holder.imArchiveGif, archive.getSticker_group_path(), archive.getSticker_name(), archive.getSticker_type());
            } catch(StickerTypeException e) {
                e.printStackTrace();
            }

//            if(Constant.Sticker_Gif.equals(archive.getSticker_type())){
//                //如果大表情是GIF
//                holder.llArchiveImage.setVisibility(View.VISIBLE);
//                holder.imArchiveGif.setVisibility(View.VISIBLE);
//                holder.imArchiveGif.setImageDrawable(null);
//                holder.imArchivePic.setVisibility(View.GONE);
//                holder.imArchiveImages.setVisibility(View.GONE);
//                holder.tvPhotoCount.setVisibility(View.GONE);
//                String stickerGroupPath = archive.getSticker_group_path();
//                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
//                    stickerGroupPath = stickerGroupPath.replace("/", "");
//                }
//                try {
//                    String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + archive.getSticker_name() + "_B.gif";
//                    GifDrawable gifDrawable = new GifDrawable(new File(gifFilePath));
//                    if(gifDrawable != null) {
//                        holder.imArchivePic.setImageDrawable(gifDrawable);
//                    } else {
//                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, archive.getSticker_type());
////                        downloadAsyncTask(holder.imArchiveGif, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
//                        UniversalImageLoaderUtil.decodeStickerPic(holder.imArchiveGif, archive.getSticker_group_path(), archive.getSticker_name(), archive.getSticker_type());
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }else if(Constant.Sticker_Png.equals(archive.getSticker_type())){
//                //如果大表情是PNG
//                holder.llArchiveImage.setVisibility(View.VISIBLE);
//                holder.imArchivePic.setVisibility(View.VISIBLE);
//                holder.imArchivePic.setImageDrawable(null);
//                holder.imArchiveGif.setVisibility(View.GONE);
//                holder.imArchiveImages.setVisibility(View.GONE);
//                holder.tvPhotoCount.setVisibility(View.GONE);
//                String stickerGroupPath = archive.getSticker_group_path();
//                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
//                    stickerGroupPath = stickerGroupPath.replace("/", "");
//                }
//
//                try {
//                    String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + archive.getSticker_name() + "_B.png";
////                    InputStream is = mContext.getAssets().open(pngFileName);
//                    InputStream is = new FileInputStream(new File(pngFileName));
//                    if(is != null) {
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//                        holder.imArchivePic.setImageBitmap(bitmap);
//                    } else {
//                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
////                        downloadAsyncTask(holder.imArchivePic, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
//                        UniversalImageLoaderUtil.decodeStickerPic(holder.imArchivePic, archive.getSticker_group_path(), archive.getSticker_name(), archive.getSticker_type());
//                    }
//                } catch(Exception e) {
//                    //本地没有png的时候，从服务器下载
////                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), archive.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
////                    downloadAsyncTask(holder.imArchivePic, stickerUrl, archive.getSticker_type(), R.drawable.network_image_default);
//                    e.printStackTrace();
//                }
//            }

        } else {
            holder.llArchiveImage.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        NetworkImageView imArchiveImages;

        /**
         * 显示大表情 png
         */
        GifImageView imArchivePic;
        /**
         * 显示大表情 Gif
         */
        GifImageView imArchiveGif;
        /**
         * 地址名称
         */
        TextView locationName;
        View lllocation;

        private TextView group_name;

        private View cardView;

        private View groupDefault;

        public VHItem(View itemView) {
            super(itemView);

//            group_name = (TextView) itemView.findViewById(R.id.group_name);


            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvDate = (TextView) itemView.findViewById(R.id.up_time);
            tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_chat_photo_count);
            llArchiveImage = itemView.findViewById(R.id.ll_chats_image);
            tvCommentCount = (TextView) itemView.findViewById(R.id.member_comment);
            lllocation = itemView.findViewById(R.id.lo_location);
            tvLoveCount = (TextView) itemView.findViewById(R.id.memeber_love);
            locationName = (TextView) itemView.findViewById(R.id.tv_archive_location_name);
            liContent = itemView.findViewById(R.id.li_content);
            tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
            groupDefault = itemView.findViewById(R.id.group_default);
            imArchivePic = (GifImageView) itemView.findViewById(R.id.iv_chats_pic);
            imArchiveGif = (GifImageView) itemView.findViewById(R.id.iv_chats_gif);
            tvContent.setMaxLines(9);
            imArchiveImages = (NetworkImageView) itemView.findViewById(R.id.iv_chats_images);
            cardView = itemView.findViewById(R.id.top_archive);
            if("1".equals(Tap)){
                groupDefault.setVisibility(View.GONE);
            }
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
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ArchiveChatEntity entity = data.get(position);
            switch (v.getId()){
                case R.id.iv_chats_images:
                    if(marchiveChatViewClickListener != null){
                        marchiveChatViewClickListener.showOriginalPic(entity);
                    }
                    break;
                case R.id.top_archive:
                    if("0".equals(Tap)){
                        if(marchiveChatViewClickListener != null){
                            marchiveChatViewClickListener.showComments(entity.getContent_group_id(),entity.getGroup_id());
                        }
                    }
            }

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

        };
        //for not work in down 11
        if (SDKUtil.IS_HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        } else {
            task.execute(path);
        }

    }

    public ArchiveChatViewClickListener marchiveChatViewClickListener;
    public void setPicClickListener(ArchiveChatViewClickListener archiveChatViewClickListener){
        marchiveChatViewClickListener = archiveChatViewClickListener;
    }
}
