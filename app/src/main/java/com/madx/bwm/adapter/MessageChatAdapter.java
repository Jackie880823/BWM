package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.MsgEntity;
import com.madx.bwm.entity.PhotoEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.FamilyProfileActivity;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.MeActivity;
import com.madx.bwm.ui.MessageChatActivity;
import com.madx.bwm.ui.ViewOriginalPicesActivity;
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
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by quankun on 15/4/24.
 */
public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.VHItem> {
    private List<MsgEntity> myList;
    private Context context;
    private RecyclerView recyclerView;
    private static final int FROM_ME_TYPE_TEXT = 1;
    private static final int FROM_ME_TYPE_PIC = 2;
    private static final int FROM_ME_TYPE_LOC = 3;
    private static final int FROM_ME_TYPE_GIF = 4;
    private static final int FROM_ME_TYPE_PNG = 5;

    private static final int FROM_OTHER_TYPE_TEXT = 6;
    private static final int FROM_OTHER_TYPE_PIC = 7;
    private static final int FROM_OTHER_TYPE_LOC = 8;
    private static final int FROM_OTHER_TYPE_GIF = 9;
    private static final int FROM_OTHER_TYPE_PNG = 10;

    public MessageChatAdapter(Context context, List<MsgEntity> myList, RecyclerView recyclerView) {
        this.context = context;
        this.myList = myList;
        this.recyclerView = recyclerView;
    }

    public void addHistoryData(List<MsgEntity> list) {
        int listSize = list.size();
        myList.addAll(0, list);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(listSize);
    }

    public void addData(List<MsgEntity> list) {
        myList.addAll(0, list);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void addSendData(List<MsgEntity> list) {
        if(null==list){
            return;
        }
        for(MsgEntity msgEntity: list){
            if(!myList.contains(msgEntity)){
                myList.add(msgEntity);
            }
        }
//        myList.clear();
//        myList.addAll(list);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void addMsgEntity(MsgEntity msgEntity) {
        int listSize = myList.size();
        myList.add(msgEntity);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
        //notifyItemInserted(myList.size());
    }

    @Override
    public int getItemViewType(int position) {
        MsgEntity msgEntity = myList.get(position);
        if (msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id())) {
            if (null != msgEntity.getText_id()) {
                return FROM_ME_TYPE_TEXT;
            } else if (msgEntity.getFile_id() != null) {
                return FROM_ME_TYPE_PIC;
            } else if (msgEntity.getLoc_id() != null) {
                return FROM_ME_TYPE_LOC;
            } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {
                return FROM_ME_TYPE_GIF;
            } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {
                return FROM_ME_TYPE_PNG;
            } else {
                return FROM_ME_TYPE_PIC;
            }
        } else {
            if (null != msgEntity.getText_id()) {
                return FROM_OTHER_TYPE_TEXT;
            } else if (msgEntity.getFile_id() != null) {
                return FROM_OTHER_TYPE_PIC;
            } else if (msgEntity.getLoc_id() != null) {
                return FROM_OTHER_TYPE_LOC;
            } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {
                return FROM_OTHER_TYPE_GIF;
            } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {
                return FROM_OTHER_TYPE_PNG;
            } else {
                return FROM_OTHER_TYPE_PIC;
            }
        }
    }

    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        LayoutInflater mInflater = LayoutInflater.from(context);
        switch (viewType) {
            case FROM_ME_TYPE_TEXT:
                convertView = mInflater.inflate(R.layout.message_item_right_text, null);
                break;
            case FROM_ME_TYPE_PIC:
                convertView = mInflater.inflate(R.layout.message_item_right_pic, null);
                break;
            case FROM_ME_TYPE_LOC:
                convertView = mInflater.inflate(R.layout.message_item_right_pic, null);
                break;
            case FROM_ME_TYPE_GIF:
                convertView = mInflater.inflate(R.layout.message_item_right_gif_pic, null);
                break;
            case FROM_ME_TYPE_PNG:
                convertView = mInflater.inflate(R.layout.message_item_right_png_pic, null);
                break;
            case FROM_OTHER_TYPE_TEXT:
                convertView = mInflater.inflate(R.layout.message_item_left_text, null);
                break;
            case FROM_OTHER_TYPE_PIC:
                convertView = mInflater.inflate(R.layout.message_item_left_pic, null);
                break;
            case FROM_OTHER_TYPE_LOC:
                convertView = mInflater.inflate(R.layout.message_item_left_pic, null);
                break;
            case FROM_OTHER_TYPE_GIF:
                convertView = mInflater.inflate(R.layout.message_item_left_gif_pic, null);
                break;
            case FROM_OTHER_TYPE_PNG:
                convertView = mInflater.inflate(R.layout.message_item_left_png_pic, null);
                break;
        }
        return new VHItem(convertView);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        MsgEntity msgEntity = myList.get(position);
        boolean isSendMe = msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id());
        holder.dateTime.setText(MyDateUtils.getLocalDateStringFromUTC(context, msgEntity.getContent_creation_date()));
        String iconUrl = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id());
        VolleyUtil.initNetworkImageView(context, holder.iconImage, iconUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        if (!isSendMe) {
            holder.leftName.setText(msgEntity.getUser_given_name());
        }
        if (null != msgEntity.getText_id()) {
            holder.messageText.setText(msgEntity.getText_description());
        } else if (msgEntity.getLoc_id() != null) {
            String locUrl = String.format(Constant.MAP_API_GET_LOCATION_PIC, msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude(),
                    context.getString(R.string.google_map_pic_size), msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude());
            VolleyUtil.initNetworkImageView(context, holder.networkImageView
                    , locUrl, R.drawable.network_image_default, R.drawable.network_image_default);
        } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {
            holder.progressBar.setVisibility(View.GONE);
            String stickerGroupPath = msgEntity.getSticker_group_path();
            if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                stickerGroupPath = stickerGroupPath.replace("/", "");
            }
            try {
                String gifFilePath = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + msgEntity.getSticker_name() + "_B.gif";
                GifDrawable gifDrawable = new GifDrawable(context.getAssets(), gifFilePath);
                if (gifDrawable != null) {
                    holder.gifImageView.setImageDrawable(gifDrawable);
//                    if ("true".equals(msgEntity.getIsNate())) {
//                        holder.progressBar.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.progressBar.setVisibility(View.GONE);
//                    }
                } else {
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
                            msgEntity.getSticker_name(), stickerGroupPath, msgEntity.getSticker_type());
                    downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
                }
            } catch (IOException e) {
                String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
                        msgEntity.getSticker_name(), stickerGroupPath, msgEntity.getSticker_type());
                downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
                e.printStackTrace();
            }
        } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {
            holder.pngImageView.setImageResource(R.drawable.network_image_default);
            holder.progressBar.setVisibility(View.GONE);
            if (msgEntity.getUri() != null)//直接显示在ListView,相册和相机
            {
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 4;
                Bitmap bitmap = LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(msgEntity.getUri().getPath()),
                        ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(context, msgEntity.getUri())), 200, 200));
                holder.pngImageView.setImageBitmap(bitmap);

            } else {
                String stickerGroupPath = msgEntity.getSticker_group_path();
                if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }

                try {
                    String pngFileName = MessageChatActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + msgEntity.getSticker_name() + "_B.png";
                    InputStream is = context.getAssets().open(pngFileName);
                    if (is != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        holder.pngImageView.setImageBitmap(bitmap);
                    } else {
                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                        downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
                    }
                } catch (IOException e) {
                    //本地没有png的时候，从服务器下载
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                    downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
                    e.printStackTrace();
                }
            }
        } else if (msgEntity.getFile_id() != null) {
            holder.progressBar.setVisibility(View.GONE);
            VolleyUtil.initNetworkImageView(context, holder.networkImageView, String.format(Constant.API_GET_PIC, "post_preview_m", msgEntity.getUser_id(), msgEntity.getFile_id()),
                    R.drawable.network_image_default, R.drawable.network_image_default);
        }
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
        return myList.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        //接收
        CircularNetworkImage iconImage;
        TextView messageText;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        TextView dateTime;
        TextView leftName;
        ImageView pngImageView;
        ProgressBarCircularIndeterminate progressBar;

        public VHItem(View itemView) {
            super(itemView);
            iconImage = (CircularNetworkImage) itemView.findViewById(R.id.message_icon_image);
            messageText = (TextView) itemView.findViewById(R.id.message_item_content_tv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            dateTime = (TextView) itemView.findViewById(R.id.date_time_tv);
            leftName = (TextView) itemView.findViewById(R.id.tv_name);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_pic_png_iv);
            progressBar = (ProgressBarCircularIndeterminate) itemView.findViewById(R.id.message_progress_bar);

            iconImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MsgEntity msgEntity = myList.get(getAdapterPosition());
            boolean isFromMe = msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id());
            Intent intent;
            switch (v.getId()) {
                case R.id.message_icon_image:
                    if (isFromMe) {
                        intent = new Intent(context, MeActivity.class);
                        context.startActivity(intent);
                    } else {
                        intent = new Intent(context, FamilyProfileActivity.class);
                        intent.putExtra("member_id", msgEntity.getUser_id());
                        context.startActivity(intent);
                    }
                    break;
                case R.id.message_pic_iv:
                    if (msgEntity.getLoc_id() != null) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    } else if (msgEntity.getFile_id() != null) {
                        intent = new Intent(context, ViewOriginalPicesActivity.class);
                        ArrayList<PhotoEntity> data = new ArrayList();
                        PhotoEntity peData = new PhotoEntity();
                        peData.setUser_id(msgEntity.getUser_id());
                        peData.setFile_id(msgEntity.getFile_id());
                        peData.setPhoto_caption(Constant.Module_Original);
                        peData.setPhoto_multipe("false");
                        data.add(peData);
                        intent.putExtra("is_data", true);
                        intent.putExtra("datas", data);
                        context.startActivity(intent);
                    }
                    break;

            }
        }
    }
}
