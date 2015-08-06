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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.EventCommentEntity;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.exception.StickerTypeException;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;
import com.material.widget.CircularProgress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class EventCommentAdapterTest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EventCommentEntity> data;
    private EventEntity detailData;
    private int detailItemCount;
    RecyclerView recyclerView;

    private static final int HEADER_VIEW_TYPE = -1000;
    private static final int FOOTER_VIEW_TYPE = -2000;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_SECOND = 2;
    private static final int TYPE_FOOTER = 3;

    private static final int TEXT = 1;
    private static final int GIF = 2;
    private static final int PIC = 3;
    private static final int PNG = 4;
    private static final int GEF = 5;

    public EventCommentAdapterTest(Context context,EventEntity detailData, List<EventCommentEntity> data, RecyclerView recyclerView) {
        mContext = context;
        this.detailData = detailData;
        this.data = data;
        this.recyclerView = recyclerView;
//        if(detailData != null){
//            detailItemCount = 1;
//        }
        detailItemCount = 1;

    }
    //添加数据到实体
    public void addData(List<EventCommentEntity> newdData) {
        data.addAll(newdData);
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
//        Log.i("添加item======","");
        int listSize = data.size();
        data.add(0, msgEntity);

//        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void removeCommentData(){
        data.clear();
        notifyDataSetChanged();
    }
    private boolean isHeader(int position) {
        return position == -1000;
    }
    /**
     * 是否有评论
     * @return
     */
    private boolean isComment() {
        return data.size() > 0;
    }

    /**
     * 是否是最后一评论
     * @param viewType
     * @return
     */
    private boolean isFooter(int viewType){
        return viewType >= FOOTER_VIEW_TYPE && viewType < (FOOTER_VIEW_TYPE + 1);
    }
    @Override
    public int getItemViewType(int position) {
////        Log.i("getItemViewType: ", "position: " + position);
//        int type = GEF;
//        if(!TextUtils.isEmpty(myEevent.getComment_content().trim()) && TextUtils.isEmpty(myEevent.getSticker_group_path().trim())){
//            type = TEXT;
//        }else if(Constant.Sticker_Gif.equals(myEevent.getSticker_type())){
//            type = GIF;
//        }else if(Constant.Sticker_Png.equals(myEevent.getSticker_type())) {
//            type = PNG;
//        }else if(myEevent.getFile_id()!= null ){
//            type = PIC;
//        }
//        return type;



//        if (isHeader(position)) {
//            return HEADER_VIEW_TYPE + position;
//        }else if(isComment(position)){
//            return HEADER_VIEW_TYPE + position + 1;
//        }else if(position < (2 + detailData.size())){
//            return position - 2;
//        }else {
//            return FOOTER_VIEW_TYPE + position - 2 - detailData.size();
//        }


        if(position == 0){
            return TYPE_HEADER;
        } else if(position == 1){
            return TYPE_SECOND;
        }else if (position < (data.size() + 1) && position >= 2 ){
            return TYPE_ITEM;
        }else if(position == data.size() + 1 ){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载Item的布局.布局中用到的真正的CardView.
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item, parent, false);
        View view = null ;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case TYPE_HEADER:
                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_detail_head, parent, false);
//                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item_test, parent, false);
                viewHolder = new VHHeader(view);
                break;
            case TYPE_SECOND:
//                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_head_item_test, parent, false);
                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_head_item_test, parent, false);
                viewHolder = new Second(view);
                break;
            case TYPE_ITEM:
                view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item_test, parent, false);
                viewHolder  = new VHItem(view);
                break;
            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_footer_item_test, parent, false);
                viewHolder = new Footer(view);
                break;

        }
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position == 0 ){
            VHHeader header = (VHHeader) holder;
            EventEntity detail = detailData;
            if(detailData == null){
                return;
            }
            setDetail(header, detail);
//            Log.i("getGroup_owner_id()", detail.getGroup_owner_id()+"");
            VolleyUtil.initNetworkImageView(mContext, ((VHHeader) holder).ownerHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, detail.getGroup_owner_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            header.pushDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext, detail.getGroup_creation_date()));
            header.eventTitle.setText(detail.getGroup_name());
            header.tvUserName.setText(detail.getUser_given_name());
            header.tvContent.setText(detail.getText_description());
            header.tvDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext, detail.getGroup_event_date()));
            header.TvLocation.setText(detail.getLoc_name());
            header.tvMaybe.setText(detail.getTotal_maybe());
            header.tvNotGoing.setText(detail.getTotal_no());
            if(MainActivity.getUser().getUser_id().equals(detail.getGroup_owner_id())) {
                try {
                    header.tvGoing.setText((Integer.valueOf(detail.getTotal_yes()) - 1) + "");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }else {
                header.tvGoing.setText(detail.getTotal_yes());
            }

        }
        if(position == 1){
            Second second = (Second) holder;
            EventCommentEntity entity =  data.get(position - detailItemCount);
            VolleyUtil.initNetworkImageView(mContext, ((Second) holder).civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            second.tv_comment_owner_name.setText(entity.getUser_given_name());
            second.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, entity.getComment_creation_date()));
            second.tv_agree_count.setText((TextUtils.isEmpty(entity.getLove_count()) ? "0" : entity.getLove_count()));
            if(!TextUtils.isEmpty(entity.getComment_content().trim())){//如果文字不为空
//                Log.i("文字=====", position + "");
                second.tv_comment_content.setVisibility(View.VISIBLE);
                second.tv_comment_content.setText(entity.getComment_content());
                second.chatsImage.setVisibility(View.GONE);
            }else {
                second.tv_comment_content.setVisibility(View.GONE);
                setCommentPic(second.gifImageView,second.networkImageView,second.chatsImage, entity);
            }
//            if (MainActivity.getUser().getUser_id().equals(entity.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
//                second.btn_comment_del.setVisibility(View.VISIBLE);
//
//            } else {
//                second.btn_comment_del.setVisibility(View.GONE);
//            }
//            if (TextUtils.isEmpty(entity.getLove_id())) {//如果有人点赞
//                second.iv_agree.setImageResource(R.drawable.agree_normal);
//            } else {
//                second.iv_agree.setImageResource(R.drawable.agree_press);
//            }
            setComment(second.btn_comment_del,second.iv_agree,entity);
        }else if(position >1 && position < (data.size()+1)){
            VHItem vhItem = (VHItem) holder;
            EventCommentEntity entity =  data.get(position - detailItemCount);
            VolleyUtil.initNetworkImageView(mContext, ((VHItem) holder).civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            vhItem.tv_comment_owner_name.setText(entity.getUser_given_name());
            vhItem.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, entity.getComment_creation_date()));
            vhItem.tv_agree_count.setText((TextUtils.isEmpty(entity.getLove_count()) ? "0" : entity.getLove_count()));
            if(!TextUtils.isEmpty(entity.getComment_content().trim())){//如果文字不为空
//                Log.i("文字=====", position + "");
                vhItem.tv_comment_content.setVisibility(View.VISIBLE);
                vhItem.tv_comment_content.setText(entity.getComment_content());
                vhItem.chatsImage.setVisibility(View.GONE);

            }else {
                vhItem.tv_comment_content.setVisibility(View.GONE);
                setCommentPic(vhItem.gifImageView,vhItem.networkImageView,vhItem.chatsImage, entity);
            }

//            if (MainActivity.getUser().getUser_id().equals(entity.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
//                vhItem.btn_comment_del.setVisibility(View.VISIBLE);
//
//            } else {
//                vhItem.btn_comment_del.setVisibility(View.GONE);
//            }
//            if (TextUtils.isEmpty(entity.getLove_id())) {//如果有人点赞
//                vhItem.iv_agree.setImageResource(R.drawable.agree_normal);
//            } else {
//                vhItem.iv_agree.setImageResource(R.drawable.agree_press);
//            }
            setComment(vhItem.btn_comment_del,vhItem.iv_agree,entity);
        }else if(position == data.size() + 1 ){
            Footer footer = (Footer) holder;
            EventCommentEntity entity =  data.get(position - detailItemCount);
            VolleyUtil.initNetworkImageView(mContext, ((Footer) holder).civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            footer.tv_comment_owner_name.setText(entity.getUser_given_name());
            footer.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, entity.getComment_creation_date()));
            footer.tv_agree_count.setText((TextUtils.isEmpty(entity.getLove_count()) ? "0" : entity.getLove_count()));
            if(!TextUtils.isEmpty(entity.getComment_content().trim())){//如果文字不为空
//                Log.i("文字=====", position + "");
                footer.tv_comment_content.setVisibility(View.VISIBLE);
                footer.tv_comment_content.setText(entity.getComment_content());
                footer.chatsImage.setVisibility(View.GONE);

            }else {
                footer.tv_comment_content.setVisibility(View.GONE);
                setCommentPic(footer.gifImageView,footer.networkImageView,footer.chatsImage, entity);
            }
//            if (MainActivity.getUser().getUser_id().equals(entity.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
//                footer.btn_comment_del.setVisibility(View.VISIBLE);
//
//            } else {
//                footer.btn_comment_del.setVisibility(View.GONE);
//            }
//            if (TextUtils.isEmpty(entity.getLove_id())) {//如果有人点赞
//                footer.iv_agree.setImageResource(R.drawable.agree_normal);
//            } else {
//                footer.iv_agree.setImageResource(R.drawable.agree_press);
//            }
            setComment(footer.btn_comment_del, footer.iv_agree, entity);
        }

    }
    private void setDetail(RecyclerView.ViewHolder holder,EventEntity entity){

    }
    private void setComment(ImageButton btn_comment_del,ImageButton iv_agree,EventCommentEntity entity){

        if (MainActivity.getUser().getUser_id().equals(entity.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
            btn_comment_del.setVisibility(View.VISIBLE);

        } else {
            btn_comment_del.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(entity.getLove_id())) {//如果有人点赞
            iv_agree.setImageResource(R.drawable.love_normal);
        } else {
            iv_agree.setImageResource(R.drawable.love_press);
        }

//        setCommentPic(holder,entity);
    }
    private  void setCommentPic(GifImageView gifImageView, NetworkImageView networkImageView,View chatsImage,EventCommentEntity comment){
//        Log.i(TAG, "setCommentPic& file_id: " + comment.getFile_id() + "; StickerName is " + comment.getSticker_name());
        if(!TextUtils.isEmpty(comment.getFile_id())) {
            chatsImage.setVisibility(View.VISIBLE);
            networkImageView.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.GONE);
            VolleyUtil.initNetworkImageView(mContext, networkImageView, String.format(Constant.API_GET_COMMENT_PIC, Constant.Module_preview_m, comment.getUser_id(), comment.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        } else if(!TextUtils.isEmpty(comment.getSticker_group_path())) {
            chatsImage.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.VISIBLE);
            networkImageView.setVisibility(View.GONE);
            gifImageView.setImageDrawable(null);
            try {
                UniversalImageLoaderUtil.decodeStickerPic(gifImageView, comment.getSticker_group_path(), comment.getSticker_name(), comment.getSticker_type());
            } catch(StickerTypeException e) {
                e.printStackTrace();
            }
        } else {
            chatsImage.setVisibility(View.GONE);
        }
//            if(Constant.Sticker_Gif.equals(comment.getSticker_type())) {
//                String stickerGroupPath = comment.getSticker_group_path();
//                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
//                    stickerGroupPath = stickerGroupPath.replace("/", "");
//                }
//                try {
//                    String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.gif";
//                    GifDrawable gifDrawable = new GifDrawable(new File(gifFilePath));
//                    if(gifDrawable != null) {
//                        gifImageView.setImageDrawable(gifDrawable);
//                    } else {
//                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, comment.getSticker_type());
//                        UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath,comment.getSticker_name(),R.drawable.network_image_default,gifImageView,comment.getSticker_type());
//
//                    }
//                } catch(IOException e) {
//                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, comment.getSticker_type());
//                    UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath,comment.getSticker_name(),R.drawable.network_image_default,gifImageView,comment.getSticker_type());
//
//                    e.printStackTrace();
//                }
//            } else if(Constant.Sticker_Png.equals(comment.getSticker_type())) {
//                String stickerGroupPath = comment.getSticker_group_path();
//                if(null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
//                    stickerGroupPath = stickerGroupPath.replace("/", "");
//                }
//
//                try {
//                    String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + comment.getSticker_name() + "_B.png";
////                    InputStream is = mContext.getAssets().open(pngFileName);
//                    InputStream is = new FileInputStream(new File(pngFileName));
//                    if(is != null) {
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//                        gifImageView.setImageBitmap(bitmap);
//                    } else {
//                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
//                        UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath, comment.getSticker_name(), R.drawable.network_image_default, networkImageView, comment.getSticker_type());
//
//                    }
//                } catch(IOException e) {
//                    //本地没有png的时候，从服务器下载
//                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), comment.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
//                    UniversalImageLoaderUtil.downloadStickPic(stickerGroupPath, comment.getSticker_name(), R.drawable.network_image_default, networkImageView, comment.getSticker_type());
//                    e.printStackTrace();
//                }
//            }else {
//                chatsImage.setVisibility(View.GONE);
//            }


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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void downloadAsyncTask(final CircularProgress progressBar, final GifImageView gifImageView, final String path, final int defaultResource) {
        AsyncTask task = new AsyncTask<String, Void, byte[]>() {

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

        };
        //for not work in down 11
        if (SDKUtil.IS_HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new String[0]);
        } else {
            task.execute(new String[0]);
        }

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void downloadPngAsyncTask(final CircularProgress progressBar, final ImageView imageView, final String path, final int defaultResource) {
        AsyncTask task = new AsyncTask<String, Void, byte[]>() {

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

        };
        //for not work in down 11
        if (SDKUtil.IS_HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{});
        } else {
            task.execute(new String[]{});
        }

    }

    @Override
    public int getItemCount() {
        return data.size() + detailItemCount;
    }


    class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener{
        /**
         * 提交时间
         */
        private TextView pushDate;
        /**
         * 用户昵称
         */
        private TextView tvUserName;
        /**
         * 用户头像
         */
        private CircularNetworkImage ownerHead;
        /**
         * event头目
         */
        private TextView eventTitle;
        /**
         *  event地图
         */
        private NetworkImageView eventMapCation;
        /**
         *  event内容
         */
        private TextView tvContent;
        /**
         * 日历
         */
        private TextView tvDate;
        /**
         * 地址
         */
        private TextView TvLocation;
        /**
         * 被邀请好友视图
         */
        private RelativeLayout intentAll;
        /**
         *  参加
         */
        private LinearLayout intentAgree;
        /**
         * 不确定
         */
        private LinearLayout intentMaybe;
        /**
         * 拒绝
         */
        private LinearLayout intentNo;

        private TextView tvGoing;
        private TextView tvMaybe;
        private TextView tvNotGoing;
        private View intenAll;
        public VHHeader(View itemView) {
            super(itemView);
            pushDate = (TextView) itemView.findViewById(R.id.push_date);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            ownerHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventMapCation = (NetworkImageView) itemView.findViewById(R.id.event_picture_4_location);
            tvContent = (TextView) itemView.findViewById(R.id.event_desc);
            tvDate = (TextView) itemView.findViewById(R.id.event_date);
            TvLocation = (TextView) itemView.findViewById(R.id.location_desc);
            intentAll = (RelativeLayout) itemView.findViewById(R.id.btn_intent_all);
            intentAgree = (LinearLayout) itemView.findViewById(R.id.iv_intent_agree);
            intentMaybe = (LinearLayout) itemView.findViewById(R.id.iv_intent_maybe);
            intentNo = (LinearLayout) itemView.findViewById(R.id.iv_intent_no);
            tvGoing = (TextView) itemView.findViewById(R.id.going_count);
            tvMaybe = (TextView) itemView.findViewById(R.id.maybe_count);
            tvNotGoing = (TextView) itemView.findViewById(R.id.not_going_count);
            intenAll = itemView.findViewById(R.id.btn_intent_all);
            intenAll.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_intent_all:
                    break;
            }
        }
    }

    class Second extends RecyclerView.ViewHolder implements View.OnClickListener{
        /**
         * 用户头像视图
         */
        CircularNetworkImage civ_comment_owner_head;
        /**
         * 用户昵称视图
         */
        TextView tv_comment_owner_name;
        /**
         *
         */
        FreedomSelectionTextView tv_comment_content;
        /**
         *
         */
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        ImageView pngImageView;
//        CircularProgress progressBar;
        View agreeTouch;
        private View chatsImage;
        public Second(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_png_iv);

            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            agreeTouch = itemView.findViewById(R.id.agree_touch);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
//            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
            chatsImage = itemView.findViewById(R.id.ll_chats_image);
            agreeTouch.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener{

        /**
         * 用户头像视图
         */
        CircularNetworkImage civ_comment_owner_head;
        /**
         * 用户昵称视图
         */
        TextView tv_comment_owner_name;
        /**
         *
         */
        FreedomSelectionTextView tv_comment_content;
        /**
         *
         */
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        ImageView pngImageView;
//        CircularProgress progressBar;
        View agreeTouch;
        private View chatsImage;
        public VHItem(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_png_iv);

            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            agreeTouch = itemView.findViewById(R.id.agree_touch);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
//            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
            chatsImage = itemView.findViewById(R.id.ll_chats_image);
            agreeTouch.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    class Footer extends RecyclerView.ViewHolder implements View.OnClickListener{
        /**
         * 用户头像视图
         */
        CircularNetworkImage civ_comment_owner_head;
        /**
         * 用户昵称视图
         */
        TextView tv_comment_owner_name;
        /**
         *
         */
        FreedomSelectionTextView tv_comment_content;
        /**
         *
         */
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        ImageView pngImageView;
        CircularProgress progressBar;
        View agreeTouch;
        private View chatsImage;
        public Footer(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_png_iv);

            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            agreeTouch = itemView.findViewById(R.id.agree_touch);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
            chatsImage = itemView.findViewById(R.id.ll_chats_image);
            agreeTouch.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


//    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        CircularNetworkImage civ_comment_owner_head;
//        TextView tv_comment_owner_name;
//        FreedomSelectionTextView tv_comment_content;
//        TextView tv_agree_count;
//        ImageButton iv_agree;
//        ImageButton btn_comment_del;
//        TextView comment_date;
//        NetworkImageView networkImageView;
//        GifImageView gifImageView;
//        ImageView pngImageView;
//        CircularProgress progressBar;
//        View agreeTouch;
//
//
//        public ViewHolder(View itemView) {
//            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
//            super(itemView);
//            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
//            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
//            pngImageView = (ImageView) itemView.findViewById(R.id.message_png_iv);
//
//            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
//            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
//            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
//            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
//            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
//            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
//            agreeTouch = itemView.findViewById(R.id.agree_touch);
//            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
//            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
//            agreeTouch.setOnClickListener(this);
//            btn_comment_del.setOnClickListener(this);
//
////            itemView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    EventCommentEntity comment = data.get(getAdapterPosition());
////                    //自己发的或event creator 可以删除
////                    if(MainActivity.getUser().getUser_id().equals(comment.getUser_id())){
////                        removeComment(comment.getComment_id());
////                    }
////                }
////            });
//
//        }
//
//
//
//
//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            EventCommentEntity commentEntity = data.get(position);
//            switch (v.getId()) {
//                case R.id.agree_touch:
//                    newClick = true;
//                    int count = Integer.valueOf(tv_agree_count.getText().toString());
//                    if (TextUtils.isEmpty(commentEntity.getLove_id())) {
//                        iv_agree.setImageResource(R.drawable.agree_press);
//                        commentEntity.setLove_id(MainActivity.getUser().getUser_id());
//                        tv_agree_count.setText(count + 1 + "");
//                    } else {
//                        iv_agree.setImageResource(R.drawable.agree_normal);
//                        commentEntity.setLove_id(null);
//                        tv_agree_count.setText(count - 1 + "");
//                    }
//                    //判断是否已经有进行中的判断
//                    if (!runningList.contains(position)) {
//                        runningList.add(position);
//                        check(position);
//                    }
//                    break;
//                case R.id.btn_comment_del:
//                    EventCommentEntity comment = data.get(getAdapterPosition());
//                    //自己发的或event creator 可以删除
//                    if (mCommentActionListener != null) {
//                        if (MainActivity.getUser().getUser_id().equals(comment.getUser_id())) {
//                            mCommentActionListener.doDelete(comment.getComment_id());
//                        }
//                    }
//                    break;
////                case R.id.message_pic_iv:
//////                    EventCommentEntity comment = data.get(getAdapterPosition());
////                    //自己发的或event creator 可以删除
////                    if (mCommentActionListener != null) {
////                            mCommentActionListener.showOriginalPic(commentEntity.getContent_id());
////                    }
////                    break;
//            }
//        }
//    }

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

        public void showOriginalPic(String User_id, String File_id);

        public void setIntentAll(EventEntity entity);
    }


}