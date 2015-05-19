package com.madx.bwm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.EventCommentEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.MessageChatActivity;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class EventCommentAdapter extends RecyclerView.Adapter<EventCommentAdapter.ViewHolder> {
    private Context mContext;
    private List<EventCommentEntity> data;
    RecyclerView recyclerView;
    private static final int TEXT = 1;
    private static final int GIF = 2;
    private static final int PIC = 3;
    private static final int PNG = 4;

    public EventCommentAdapter(Context context, List<EventCommentEntity> data,RecyclerView recyclerView) {
        mContext = context;
        this.data = data;
        this.recyclerView = recyclerView;

    }
    //添加数据到实体
    public void addData(List<EventCommentEntity> newdData) {
        data.addAll(0, newdData);
//        notifyItemInserted(0);
        notifyDataSetChanged();
    }
    //发送评论的时候，添加到数据实体list集合
    public void addHistoryData(List<EventCommentEntity> list) {
        List<EventCommentEntity> msgList = new ArrayList<>();
        for (EventCommentEntity msgEntity : list) {
            if (!data.contains(msgEntity)) {
                msgList.add(msgEntity);
            }
        }
        int listSize = list.size();
        data.addAll(0, msgList);
        notifyDataSetChanged();
//        recyclerView.scrollToPosition(listSize);
    }
    public void addSendData(List<EventCommentEntity> list) {
        if (null == list) {
            return;
        }
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
//        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void addOrUpdateItem(EventCommentEntity q) {
        int pos = data.indexOf(q);
        if (pos >= 0) {
            updateItem(q, pos);
        } else {
            addItem(q);
        }
    }
    public void addMsgEntity(EventCommentEntity msgEntity) {
        //如果有聊天信息隐藏默认的布局
//        messageChatActivity.empty_message.setVisibility(View.GONE);
        //显示聊天布局下拉刷新控件
//        messageChatActivity.swipeRefreshLayout.setVisibility(View.VISIBLE);
        Log.i("添加item======","");
        int listSize = data.size();
        data.add(0, msgEntity);

//        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        EventCommentEntity myEevent = data.get(position);
        Log.i("getItemViewType: ", "position: " + position);
        int type = 0;
        if(!TextUtils.isEmpty(myEevent.getComment_content().trim()) && TextUtils.isEmpty(myEevent.getSticker_group_path().trim())){
            Log.i("getItemViewType 文字====",myEevent.getComment_content().toString());
            type = TEXT;
        }else if(Constant.Sticker_Gif.equals(myEevent.getSticker_type())){
            type = GIF;
        }
        else if(Constant.Sticker_Png.equals(myEevent.getSticker_type())) {
            Log.i("getItemViewType 本地图片===",myEevent.getSticker_type());
            type = PNG;
        }else if(myEevent.getFile_id()!= null ){
            Log.i("getItemViewType 网络图片===",myEevent.getFile_id());
            type = PIC;
        }
//        else{
//            Log.i("getItemViewType file_id",myEevent.getFile_id()+"");
//        }
        return type;

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
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item, parent, false);
        View view = null ;
        switch (viewType){
            case TEXT:
                 view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item, parent, false);
//                Log.i("TEXT==========","");
                break;
            case PIC:
                Log.i("网络图片===","");
                 view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_pic, parent, false);
//                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_png, parent, false);
//                Log.i("PIC==========","");
                break;
            case GIF:
                 view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_gif, parent, false);
//                Log.i("GIF==========","");
                break;
            case PNG:
                 view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_png, parent, false);
//                Log.i("PNG==========","");
                break;
            default:
                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_pic, parent, false);
                break;

        }
        // ViewHolder参数一定要是Item的Root节点.
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventCommentEntity ece = data.get(position);
        VolleyUtil.initNetworkImageView(mContext, holder.civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, ece.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.tv_comment_owner_name.setText(ece.getUser_given_name());
//        holder.tv_comment_content.setText(ece.getComment_content());
        holder.tv_agree_count.setText((TextUtils.isEmpty(ece.getLove_count()) ? "0" : ece.getLove_count()));
        holder.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, ece.getComment_creation_date()));


//            Log.i("ece.getSticker_group_path()",ece.getSticker_group_path()+"");
            if(!TextUtils.isEmpty(ece.getComment_content().trim())){//如果文字不为空
                Log.i("文字=====", position + "");
                holder.tv_comment_content.setVisibility(View.VISIBLE);
                holder.tv_comment_content.setText(ece.getComment_content());

            }
            if(!TextUtils.isEmpty(ece.getSticker_group_path().trim())){
                switch (ece.getSticker_type().trim()){
                    case ".gif" :
                        Log.i("gifImageView=====",ece.getSticker_group_path());
                        holder.progressBar.setVisibility(View.GONE);
                        holder.gifImageView.setVisibility(View.VISIBLE);
//                        holder.tv_comment_content.setVisibility(View.GONE);
                        String stickerGroupPathGig = ece.getSticker_group_path();//获取图片地址
                        if(null != stickerGroupPathGig && stickerGroupPathGig.indexOf("/") != -1){
                            stickerGroupPathGig = stickerGroupPathGig.replace("/", "");
                        }
                        try {
                            String gifFilePath = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPathGig + File.separator + ece.getSticker_name() + "_B.gif";
                            GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(), gifFilePath);
                            if (gifDrawable != null) {
//                            Log.i("gifFilePath=====", gifFilePath);
//                            Log.i("显示GIF======",gifFilePath);
                                holder.gifImageView.setImageDrawable(gifDrawable);
                                holder.gifImageView.setVisibility(View.VISIBLE);
//                    if ("true".equals(msgEntity.getIsNate())) {
//                        holder.progressBar.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.progressBar.setVisibility(View.GONE);
//                    }
                            } else {
                                String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
                                        ece.getSticker_name(), stickerGroupPathGig, ece.getSticker_type());
                                downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
                            }
                        } catch (IOException e) {
                            String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
                                    ece.getSticker_name(), stickerGroupPathGig, ece.getSticker_type());
                            downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
                            e.printStackTrace();
                        }
                        break;


                    case ".png" :
//                        Log.i("png=====", position+"");
                        Log.i("pngImageView=====",ece.getSticker_group_path());
                        holder.progressBar.setVisibility(View.GONE);
                        holder.pngImageView.setVisibility(View.VISIBLE);
//                        holder.tv_comment_content.setVisibility(View.GONE);
                        holder.pngImageView.setImageResource(R.drawable.network_image_default);//设置默认到显示的图片
                        if(ece.getUri() != null){
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                            bitmapOptions.inSampleSize = 4;
                            Bitmap bitmap = LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(ece.getUri().getPath()),
                                    ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(mContext, ece.getUri())), 200, 200));
                            holder.pngImageView.setImageBitmap(bitmap);
                        }else {
                            String stickerGroupPathPng = ece.getSticker_group_path();
                            if (null != stickerGroupPathPng && stickerGroupPathPng.indexOf("/") != -1) {
                                stickerGroupPathPng = stickerGroupPathPng.replace("/", "");
                            }

                            try {
                                String pngFileName = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPathPng + File.separator + ece.getSticker_name() + "_B.png";
                                InputStream is = mContext.getAssets().open(pngFileName);
                                if (is != null) {
                                    holder.pngImageView.setVisibility(View.VISIBLE);
                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    holder.pngImageView.setImageBitmap(bitmap);
                                } else {
                                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), ece.getSticker_name(), stickerGroupPathPng, Constant.Sticker_Png);
                                    downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
                                }
                            } catch (IOException e) {
                                //本地没有png的时候，从服务器下载
                                String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), ece.getSticker_name(), stickerGroupPathPng, Constant.Sticker_Png);
                                downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
                                e.printStackTrace();
                            }
                        }
                        break;
                }

//                if(Constant.Sticker_Gif.equals(ece.getSticker_type().trim())){
//
//                }else {
//                if(Constant.Sticker_Png.equals(ece.getSticker_type().trim())){//PNG
//
//
//                    }
//                }
            }
        if (ece.getFile_id() !=null ){//如果有图片id
//                holder.progressBar.setVisibility(View.GONE);
            Log.i("getFile_id===",ece.getFile_id());
            holder.progressBar.setVisibility(View.GONE);
            holder.networkImageView.setVisibility(View.VISIBLE);
            Log.i("显示网络大图", ece.getFile_id());
            VolleyUtil.initNetworkImageView(mContext, holder.networkImageView, String.format(Constant.API_GET_PIC, "post_preview_m", ece.getUser_id(), ece.getFile_id()),
                        R.drawable.network_image_default, R.drawable.network_image_default);
            }
        if (MainActivity.getUser().getUser_id().equals(ece.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
            holder.btn_comment_del.setVisibility(View.VISIBLE);

        } else {
            holder.btn_comment_del.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(ece.getLove_id())) {//如果有人点赞
            holder.iv_agree.setImageResource(R.drawable.agree_normal);
        } else {
            holder.iv_agree.setImageResource(R.drawable.agree_press);
        }








//        if (MainActivity.getUser().getUser_id().equals(ece.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
//            holder.btn_comment_del.setVisibility(View.VISIBLE);
////            Log.i("ece.getSticker_group_path()",ece.getSticker_group_path()+"");
//            if(!TextUtils.isEmpty(ece.getComment_content().trim())){//如果文字不为空
//                Log.i("文字=====",position+"");
//                holder.tv_comment_content.setVisibility(View.INVISIBLE);
//                holder.tv_comment_content.setText(ece.getComment_content());
//                holder.progressBar.setVisibility(View.GONE);
////                holder.message_pic_png_iv.setVisibility(View.GONE);
////                holder.gifImageView.setVisibility(View.GONE);
//
//            }
//        else {
//            holder.btn_comment_del.setVisibility(View.GONE);
//        }
//
//        if (TextUtils.isEmpty(ece.getLove_id())) {//如果有人点赞
//            holder.iv_agree.setImageResource(R.drawable.agree_normal);
//        } else {
//            holder.iv_agree.setImageResource(R.drawable.agree_press);
//        }
//            if (Constant.Sticker_Gif.equals(ece.getSticker_type())){//如果图片类型是gif
////                    Log.i("onBVH=====GIF", "");
//                Log.i("GIF=====", ece.getSticker_group_path());
//                holder.progressBar.setVisibility(View.GONE);
//                holder.message_pic_png_iv.setVisibility(View.INVISIBLE);
////                    holder.tv_comment_content.setVisibility(View.GONE);
//                String stickerGroupPath = ece.getSticker_group_path();//获取图片地址
//                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1){
//                    stickerGroupPath = stickerGroupPath.replace("/", "");
//                }
//                try {
//                    String gifFilePath = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + ece.getSticker_name() + "_B.gif";
//                    GifDrawable gifDrawable = new GifDrawable(mContext.getAssets(), gifFilePath);
//                    if (gifDrawable != null) {
////                            Log.i("显示GIF======",gifFilePath);
//                        holder.gifImageView.setImageDrawable(gifDrawable);
////                    if ("true".equals(msgEntity.getIsNate())) {
////                        holder.progressBar.setVisibility(View.VISIBLE);
////                    } else {
////                        holder.progressBar.setVisibility(View.GONE);
////                    }
//                    } else {
//                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
//                                ece.getSticker_name(), stickerGroupPath, ece.getSticker_type());
//                        downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
//                    }
//                } catch (IOException e) {
//                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
//                            ece.getSticker_name(), stickerGroupPath, ece.getSticker_type());
//                    downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
//                    e.printStackTrace();
//                }
//
//            }
//            else if(Constant.Sticker_Png.equals(ece.getSticker_type())){//如果图片类型是png
//                Log.i("onBVH=====PNG", "");
////                    holder.tv_comment_content.setVisibility(View.GONE);
//                holder.gifImageView.setVisibility(View.INVISIBLE);
//                holder.pngImageView.setImageResource(R.drawable.network_image_default);
//                holder.progressBar.setVisibility(View.GONE);
////                    holder.tv_comment_content.setVisibility(View.GONE);
//                if (ece.getUri() != null)//直接显示在ListView,相册和相机
//                {
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                    bitmapOptions.inSampleSize = 4;
//                    Bitmap bitmap = LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(ece.getUri().getPath()),
//                            ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(mContext, ece.getUri())), 200, 200));
//                    holder.pngImageView.setImageBitmap(bitmap);
//
//                } else {
//                    String stickerGroupPath = ece.getSticker_group_path();
//                    if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
//                        stickerGroupPath = stickerGroupPath.replace("/", "");
//                    }
//
//                    try {
//                        String pngFileName = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + ece.getSticker_name() + "_B.png";
//                        InputStream is = mContext.getAssets().open(pngFileName);
//                        if (is != null) {
//                            Bitmap bitmap = BitmapFactory.decodeStream(is);
//                            holder.pngImageView.setImageBitmap(bitmap);
//                        } else {
//                            String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), ece.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
//                            downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
//                        }
//                    } catch (IOException e) {
//                        //本地没有png的时候，从服务器下载
//                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), ece.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
//                        downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
//                        e.printStackTrace();
//                    }
//                }
//
//            }else if(ece.getFile_id()!=null){
//                //???
//                holder.progressBar.setVisibility(View.GONE);
//                VolleyUtil.initNetworkImageView(mContext, holder.networkImageView, String.format(Constant.API_GET_PIC, "post_preview_m", ece.getUser_id(), ece.getFile_id()),
//                        R.drawable.network_image_default, R.drawable.network_image_default);
//
//            }
//
//        }


    }

    /**
     * 获取图片的byte数组
     *
     * @param urlPath
     * @return
     */
    private static byte[] getImageByte(String urlPath) {
        InputStream in = null;
        byte[] result = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection httpURLconnection = (HttpURLConnection) url
                    .openConnection();
            httpURLconnection.setDoInput(true);
            httpURLconnection.connect();
            if (httpURLconnection.getResponseCode() == 200) {
                in = httpURLconnection.getInputStream();
                result = readInputStream(in);
                in.close();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 将输入流转为byte数组
     *
     * @param in
     * @return
     * @throws Exception
     */
    private static byte[] readInputStream(InputStream in) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.close();
        in.close();
        return baos.toByteArray();

    }

    /**
     * 此方法用来异步加载图片
     *
     * @param path
     */
    public void downloadAsyncTask(final ProgressBarCircularIndeterminate progressBar, final GifImageView gifImageView, final String path, final int defaultResource) {
        new AsyncTask<String, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(String... params) {
                return getImageByte(path);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(byte[] resultByte) {
                super.onPostExecute(resultByte);
                progressBar.setVisibility(View.GONE);
                try {
                    if (null != resultByte) {
                        GifDrawable gifDrawable = new GifDrawable(resultByte);
                        if (gifDrawable != null && gifImageView != null) {
                            gifImageView.setImageDrawable(gifDrawable);
                        } else {
                            gifImageView.setImageResource(defaultResource);
                        }
                    } else {
                        gifImageView.setImageResource(defaultResource);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.execute(new String[]{});

    }


    public void downloadPngAsyncTask(final ProgressBarCircularIndeterminate progressBar, final ImageView imageView, final String path, final int defaultResource) {
        new AsyncTask<String, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(String... params) {
                return getImageByte(path);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(byte[] resultByte) {
                super.onPostExecute(resultByte);
                progressBar.setVisibility(View.GONE);
                try {
                    if (null != resultByte) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(resultByte, 0, resultByte.length);
                        if (bitmap != null && imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(defaultResource);
                        }
                    } else {
                        imageView.setImageResource(defaultResource);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.execute(new String[]{});

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
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        ImageView pngImageView;
        ProgressBarCircularIndeterminate progressBar;


        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_png_iv);

            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
            progressBar = (ProgressBarCircularIndeterminate) itemView.findViewById(R.id.message_progress_bar);
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
//                case R.id.message_pic_iv:
//                    if (commentEntity.getLoc_id() != null) {
//                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
//                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                        context.startActivity(intent);
//                    } else if (msgEntity.getFile_id() != null) {
//                        intent = new Intent(context, ViewOriginalPicesActivity.class);
//                        ArrayList<PhotoEntity> data = new ArrayList();
//                        PhotoEntity peData = new PhotoEntity();
//                        peData.setUser_id(msgEntity.getUser_id());
//                        peData.setFile_id(msgEntity.getFile_id());
//                        peData.setPhoto_caption(Constant.Module_Original);
//                        peData.setPhoto_multipe("false");
//                        data.add(peData);
//                        intent.putExtra("is_data", true);
//                        intent.putExtra("datas", data);
//                        context.startActivity(intent);
//                    }
//                    break;

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