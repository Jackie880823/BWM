package com.bondwithme.BondWithMe.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MsgEntity;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.FamilyProfileActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.MeActivity;
import com.bondwithme.BondWithMe.ui.MessageChatActivity;
import com.bondwithme.BondWithMe.ui.ViewOriginalPicesActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MslToast;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;
import com.material.widget.CircularProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    private MessageChatActivity messageChatActivity;
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

    public MessageChatAdapter(Context context, List<MsgEntity> myList, RecyclerView recyclerView, MessageChatActivity messageChatActivity) {
        this.context = context;
        this.myList = myList;
        this.recyclerView = recyclerView;
        this.messageChatActivity = messageChatActivity;
    }

    public void addHistoryData(List<MsgEntity> list) {
        List<MsgEntity> msgList = new ArrayList<>();
        for (MsgEntity msgEntity : list) {
            if (!myList.contains(msgEntity)) {
                msgList.add(msgEntity);
            }
        }
        int listSize = list.size();
        myList.addAll(0, msgList);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(listSize);
    }

    public void addData(List<MsgEntity> list) {
        myList.addAll(0, list);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void addSendData(List<MsgEntity> list) {
        if (null == list) {
            return;
        }
        myList.clear();
        myList.addAll(list);
        notifyDataSetChanged();
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void addMsgEntity(MsgEntity msgEntity) {
        messageChatActivity.empty_message.setVisibility(View.GONE);
        messageChatActivity.swipeRefreshLayout.setVisibility(View.VISIBLE);
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
        //网络获取头像图片
        VolleyUtil.initNetworkImageView(context, holder.iconImage, iconUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        if (!isSendMe) {
            holder.leftName.setText(msgEntity.getUser_given_name());
        }
        if (null != msgEntity.getText_id()) {//文字
            holder.messageText.setText(msgEntity.getText_description());
        } else if (msgEntity.getLoc_id() != null) {//地图 item
            String locUrl = LocationUtil.getLocationPicUrl(context, msgEntity.getLoc_latitude(), msgEntity.getLoc_longitude(), msgEntity.getLoc_type());
            VolleyUtil.initNetworkImageView(context, holder.networkImageView, locUrl, R.drawable.network_image_default, R.drawable.network_image_default);
        } else if (Constant.Sticker_Gif.equals(msgEntity.getSticker_type())) {//gif item
            holder.progressBar.setVisibility(View.GONE);
            holder.gifImageView.setImageDrawable(null);
            String stickerGroupPath = msgEntity.getSticker_group_path();
            if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                stickerGroupPath = stickerGroupPath.replace("/", "");
            }
            try {
                String gifFilePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + msgEntity.getSticker_name() + "_B.gif";
                //GifDrawable gifDrawable = new GifDrawable(context.getAssets(), gifFilePath);
                Log.i("stickerPath", gifFilePath);
                GifDrawable gifDrawable = new GifDrawable(new File(gifFilePath));
                if (gifDrawable != null) {
                    holder.gifImageView.setImageDrawable(gifDrawable);
                    //                    if ("true".equals(msgEntity.getIsNate())) {
                    //                        holder.progressBar.setVisibility(View.VISIBLE);
                    //                    } else {
                    //                        holder.progressBar.setVisibility(View.GONE);
                    //                    }
                } else {
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), stickerGroupPath, msgEntity.getSticker_type());
                    downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
                }
            } catch (Exception e) {
                String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), stickerGroupPath, msgEntity.getSticker_type());
                downloadAsyncTask(holder.progressBar, holder.gifImageView, stickerUrl, R.drawable.network_image_default);
                LogUtil.e("", "插入sticker info", e);
            }
        } else if (Constant.Sticker_Png.equals(msgEntity.getSticker_type())) {//Png
            holder.pngImageView.setImageResource(R.drawable.network_image_default);
            holder.pngImageView.setImageDrawable(null);
            holder.progressBar.setVisibility(View.GONE);
            if (msgEntity.getUri() != null)//直接显示在ListView,相册和相机
            {
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 4;
                Bitmap bitmap = LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(msgEntity.getUri().getPath()), ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(context, msgEntity.getUri())), 200, 200));
                holder.pngImageView.setImageBitmap(bitmap);//直接把图片显示出来

            } else {
                String stickerGroupPath = msgEntity.getSticker_group_path();
                if (null != stickerGroupPath && stickerGroupPath.indexOf("/") != -1) {
                    stickerGroupPath = stickerGroupPath.replace("/", "");
                }
                try {
                    //拼接大图路径
                    String pngFileName = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + msgEntity.getSticker_name() + "_B.png";
                    //InputStream is = context.getAssets().open(pngFileName);//得到数据流
                    Log.i("stickerPath", pngFileName);
                    InputStream is = new FileInputStream(new File(pngFileName));//得到数据流
                    if (is != null) {//如果有图片直接显示，否则网络下载
                        Bitmap bitmap = BitmapFactory.decodeStream(is);//将流转化成Bitmap对象
                        holder.pngImageView.setImageBitmap(bitmap);//显示图片
                    } else {
                        String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                        downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
                    }
                } catch (Exception e) {
                    //本地没有png的时候，从服务器下载
                    String stickerUrl = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), stickerGroupPath, Constant.Sticker_Png);
                    Log.i("stickerUrl", stickerUrl);
                    downloadPngAsyncTask(holder.progressBar, holder.pngImageView, stickerUrl, R.drawable.network_image_default);
                    LogUtil.e("", "插入sticker info", e);
                }
            }
        } else if (msgEntity.getFile_id() != null) {
            holder.progressBar.setVisibility(View.GONE);
            VolleyUtil.initNetworkImageView(context, holder.networkImageView, String.format(Constant.API_GET_PIC, "post_preview_m", msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
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
            HttpURLConnection httpURLconnection = (HttpURLConnection) url.openConnection();
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
        AsyncTask task = new AsyncTask<Object, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Object... params) {
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
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }

    }

    public void downloadPngAsyncTask(final CircularProgress progressBar, final ImageView imageView, final String path, final int defaultResource) {
        AsyncTask task = new AsyncTask<Object, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(Object... params) {
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
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        //接收
        CircularNetworkImage iconImage;
        FreedomSelectionTextView messageText;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        TextView dateTime;
        TextView leftName;
        ImageView pngImageView;
        CircularProgress progressBar;

        public VHItem(View itemView) {
            super(itemView);
            iconImage = (CircularNetworkImage) itemView.findViewById(R.id.message_icon_image);
            messageText = (FreedomSelectionTextView) itemView.findViewById(R.id.message_item_content_tv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            dateTime = (TextView) itemView.findViewById(R.id.date_time_tv);
            leftName = (TextView) itemView.findViewById(R.id.tv_name);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_pic_png_iv);
            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
            if (null != iconImage) {
                iconImage.setOnClickListener(this);
            }
            if (null != networkImageView) {
                networkImageView.setOnClickListener(this);
            }
        }

        //点击事件
        @Override
        public void onClick(View v) {
            final MsgEntity msgEntity = myList.get(getAdapterPosition());
            boolean isFromMe = msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id());
            Intent intent;
            switch (v.getId()) {
                case R.id.message_icon_image://点击头像跳转个人资料
                    if (isFromMe) {
                        intent = new Intent(context, MeActivity.class);
                        context.startActivity(intent);
                    } else {
                        String url = String.format(Constant.API_MESSAGE_GROUP_IS_FRIEND, MainActivity.getUser().getUser_id(), msgEntity.getUser_id());
                        new HttpTools(context).get(url, null, "MessageChatActivity", new HttpCallback() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onFinish() {
                            }

                            @Override
                            public void onResult(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String memberFlag = jsonObject.optString("member_flag");
                                    if ("1".equals(memberFlag)) {
                                        Intent intent = new Intent(context, FamilyProfileActivity.class);
                                        intent.putExtra("member_id", msgEntity.getUser_id());
                                        context.startActivity(intent);
                                    } else {
                                        MslToast.getInstance(context).showShortToast(context.getString(R.string.text_show_message_is_friend));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                MslToast.getInstance(context).showShortToast(context.getResources().getString(R.string.text_error));
                            }

                            @Override
                            public void onCancelled() {

                            }

                            @Override
                            public void onLoading(long count, long current) {

                            }
                        });

                    }
                    break;
                case R.id.message_pic_iv://点击图片跳转大图
                    if (msgEntity.getLoc_id() != null) {//地图大图片
                        //图片路径
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                        //??
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    } else if (msgEntity.getFile_id() != null) {//相册图片
                        intent = new Intent(context, ViewOriginalPicesActivity.class);
                        ArrayList<PhotoEntity> data = new ArrayList();//图片实体集合
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
