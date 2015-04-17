package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.madx.bwm.ui.ViewOriginalPicesActivity;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by christepherzhang on 15/1/28.
 */
public class ChatAdapter extends BaseAdapter {

    private List<MsgEntity> myList;
    private Context context;
    private LayoutInflater mInflater;

    public ChatAdapter(Context context, List<MsgEntity> myList){

        this.context = context;
        this.myList = myList;

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MsgEntity msgEntity = myList.get(myList.size() - position - 1);
        Log.d("","posistion---" + position);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        /*
        *用户发出的的信息。服务器也会提供，根据id号判断是自己发出然后在右边显示
        * */
        if (msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id()))//47需要变成变量，用作以后与使用的用户ID比较。
        {
            //文本内容
            if (msgEntity.getText_id() != null)
            {
                convertView = mInflater.inflate(R.layout.list_item_right_text, null);

                viewHolder.rightMessageText = (TextView) convertView.findViewById(R.id.right_message_tv);

                viewHolder.rightMessageText.setText(msgEntity.getText_description());
            }

            //照片
            else if (msgEntity.getFile_id() != null)
            {
                convertView = mInflater.inflate(R.layout.list_item_right_photo, null);

                viewHolder.rightMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.right_message_niv);

//                VolleyUtil.initNetworkImageView(context, viewHolder.rightMessagePhoto, String.format(Constant.API_GET_PIC, Constant.Module_preview_xl, msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                VolleyUtil.initNetworkImageView(context, viewHolder.rightMessagePhoto, String.format(Constant.API_GET_PIC, "post_preview_m" , msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);

                viewHolder.rightMessagePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ViewOriginalPicesActivity.class);

                        ArrayList<PhotoEntity> datas = new ArrayList();

                        PhotoEntity peData = new PhotoEntity();

                        peData.setUser_id(msgEntity.getUser_id());
                        peData.setFile_id(msgEntity.getFile_id());
                        peData.setPhoto_caption(Constant.Module_Original);
                        peData.setPhoto_multipe("false");

                        datas.add(peData);

                        intent.putExtra("is_data", true);
                        intent.putExtra("datas", datas);

                        context.startActivity(intent);
                    }
                });

            }

            //经纬度
            else if (msgEntity.getLoc_id() != null)
            {
                convertView = mInflater.inflate(R.layout.list_item_right_location, null);

                viewHolder.rightMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.right_message_niv);

                VolleyUtil.initNetworkImageView(context, viewHolder.rightMessagePhoto
                        , String.format(Constant.MAP_API_GET_LOCATION_PIC, msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude(), context.getString(R.string.google_map_pic_size), msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude())
                        , R.drawable.network_image_default, R.drawable.network_image_default);

                viewHolder.rightMessagePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });

            }

            //本地图片
            else if (".png".equals(msgEntity.getSticker_type()))
            {
                convertView = mInflater.inflate(R.layout.list_item_right_png, null);

                viewHolder.rightMessageImage = (ImageView) convertView.findViewById(R.id.right_message_iv);

                ProgressBarCircularIndeterminate progressBar = (ProgressBarCircularIndeterminate)convertView.findViewById(R.id.right_progress_bar);

                if (msgEntity.getUri() != null)//直接显示在ListView,相册和相机
                {
                    Bitmap bitmap;
//                    try {
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inSampleSize = 4;
//                        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(msgEntity.getUri()), null , bitmapOptions);

                            bitmap = LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(msgEntity.getUri().getPath()), ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(context, msgEntity.getUri())), 200, 200));

                        viewHolder.rightMessageImage.setImageBitmap(bitmap);
//                        viewHolder.rightMessageImage.setImageBitmap(LocalImageLoader.loadBitmapFromFile(context,msgEntity.getUri().getPath(),200,200));//java.lang.OutOfMemoryError 直接设置的时候

//                        viewHolder.rightMessageImage.setImageBitmap(LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(msgEntity.getUri().getPath()),bitmap));//java.lang.OutOfMemoryError 直接设置的时候
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
                }
                else//sticker
                {

                    try {
                        InputStream is = context.getAssets().open(msgEntity.getSticker_group_path() + "/" + msgEntity.getSticker_name() + "_B.png");
                        if (is != null) {

                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            viewHolder.rightMessageImage.setImageBitmap(bitmap);
//                        Drawable da = Drawable.createFromStream(is, null);
//                        viewHolder.rightMessageImage.setImageDrawable(da);
                            if ("true".equals(msgEntity.getIsNate()))
                            {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //本地动态图
            else if (".gif".equals(msgEntity.getSticker_type()))
            {
                convertView = mInflater.inflate(R.layout.list_item_right_gif, null);

                viewHolder.rightMessageGif = (GifImageView) convertView.findViewById(R.id.right_message_gif);

                ProgressBarCircularIndeterminate progressBar = (ProgressBarCircularIndeterminate)convertView.findViewById(R.id.right_progress_bar);

                try {
                    GifDrawable gifDrawable = new GifDrawable(context.getAssets(), msgEntity.getSticker_group_path() + "/" + msgEntity.getSticker_name() + "_B.gif");
                    if (gifDrawable != null)
                    {
                        viewHolder.rightMessageGif.setImageDrawable(gifDrawable);
                        if ("true".equals(msgEntity.getIsNate()))
                        {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //服务器没有以上类型数据的时候处理方式。？？？
            else
            {   convertView = mInflater.inflate(R.layout.list_item_right_png, null);

                viewHolder.rightMessageImage = (ImageView) convertView.findViewById(R.id.right_message_iv);

                ProgressBarCircularIndeterminate progressBar = (ProgressBarCircularIndeterminate)convertView.findViewById(R.id.right_progress_bar);
            }

            //共用的
            viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
            viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);
            viewHolder.rightData.setText(MyDateUtils.getLocalDateStringFromUTC(context,msgEntity.getContent_creation_date()));
            VolleyUtil.initNetworkImageView(context, viewHolder.rightImage, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            Log.d("","就问你我们一不一样"+String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id()));

            //跳转资料
            viewHolder.rightImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(context, MeActivity.class);
                    context.startActivity(intent);
                }
            });
        }





        /*
        *好友消息
        * */
        else
        {
            //文本内容
            if (msgEntity.getText_id() != null)
            {
                convertView = mInflater.inflate(R.layout.list_item_left_text, null);

                viewHolder.leftMessageText = (TextView) convertView.findViewById(R.id.left_message_tv);

                viewHolder.leftMessageText.setText(msgEntity.getText_description());
            }

            //照片
            else if (msgEntity.getFile_id() != null)
            {
                convertView = mInflater.inflate(R.layout.list_item_left_photo, null);

                viewHolder.leftMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.left_message_niv);

//                VolleyUtil.loadImage(context,String.format(Constant.API_GET_PIC, Constant.Module_preview_xl, msgEntity.getUser_id(), msgEntity.getFile_id()),new ImageLoader.ImageListener(){
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                        response.getBitmap()
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                });

//                VolleyUtil.initNetworkImageView(context, viewHolder.leftMessagePhoto, String.format(Constant.API_GET_PIC, Constant.Module_preview_xl, msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                VolleyUtil.initNetworkImageView(context, viewHolder.leftMessagePhoto, String.format(Constant.API_GET_PIC, "post_preview_m", msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);

                viewHolder.leftMessagePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ViewOriginalPicesActivity.class);

                        ArrayList<PhotoEntity> datas = new ArrayList();

                        PhotoEntity peData = new PhotoEntity();

                        peData.setUser_id(msgEntity.getUser_id());
                        peData.setFile_id(msgEntity.getFile_id());
                        peData.setPhoto_caption(Constant.Module_Original);
                        peData.setPhoto_multipe("false");

                        datas.add(peData);

                        intent.putExtra("is_data", true);
                        intent.putExtra("datas", datas);

                        context.startActivity(intent);
                    }
                });

            }

            //经纬度
            else if (msgEntity.getLoc_id() != null)
            {
                convertView = mInflater.inflate(R.layout.list_item_left_location, null);

                viewHolder.leftMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.left_message_niv);

                VolleyUtil.initNetworkImageView(context, viewHolder.leftMessagePhoto
                        , String.format(Constant.MAP_API_GET_LOCATION_PIC, msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude(), context.getString(R.string.google_map_pic_size), msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude())
                        , R.drawable.network_image_default, R.drawable.network_image_default);

                viewHolder.leftMessagePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                });
            }

            //本地图片
            else if (".png".equals(msgEntity.getSticker_type()))
            {
                convertView = mInflater.inflate(R.layout.list_item_left_png, null);

                viewHolder.leftMessageImage = (ImageView) convertView.findViewById(R.id.left_message_iv);

                ProgressBarCircularIndeterminate progressBar = (ProgressBarCircularIndeterminate)convertView.findViewById(R.id.left_progress_bar);

                try {
                    InputStream is = context.getAssets().open(msgEntity.getSticker_group_path() + "/" + msgEntity.getSticker_name() + "_B.png");
                    if (is != null)
                    {
                        Bitmap bitmap= BitmapFactory.decodeStream(is);
                        viewHolder.leftMessageImage.setImageBitmap(bitmap);
//                        Drawable da = Drawable.createFromStream(is, null);
//                        viewHolder.leftMessageImage.setImageDrawable(da);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (IOException e) {
                    //本地没有png的时候，从服务器下载
                    convertView = mInflater.inflate(R.layout.list_item_left_photo, null);
                    viewHolder.leftMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.left_message_niv);
                     VolleyUtil.initNetworkImageView(context, viewHolder.leftMessagePhoto, String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), msgEntity.getSticker_name(), msgEntity.getSticker_group_path(), Constant.Sticker_Png), R.drawable.network_image_default, R.drawable.network_image_default);
                    e.printStackTrace();
                }
            }

            //本地动态图
            else if (".gif".equals(msgEntity.getSticker_type()))
            {
                convertView = mInflater.inflate(R.layout.list_item_left_gif, null);

                viewHolder.leftMessageGif = (GifImageView) convertView.findViewById(R.id.left_message_gif);

                ProgressBarCircularIndeterminate progressBar = (ProgressBarCircularIndeterminate)convertView.findViewById(R.id.left_progress_bar);

                try {
                    GifDrawable gifDrawable = new GifDrawable(context.getAssets(), msgEntity.getSticker_group_path() + "/" +msgEntity.getSticker_name() + "_B.gif");
                    if(gifDrawable != null)
                    {
                        viewHolder.leftMessageGif.setImageDrawable(gifDrawable);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (IOException e) {

                    //本地没有gif的时候，从服务器下载
                    e.printStackTrace();
                }
            }

            else
            {
                convertView = mInflater.inflate(R.layout.list_item_left_png, null);

                viewHolder.leftMessageImage = (ImageView) convertView.findViewById(R.id.left_message_iv);

                ProgressBarCircularIndeterminate progressBar = (ProgressBarCircularIndeterminate)convertView.findViewById(R.id.left_progress_bar);
            }

            //共用的
            viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
            viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);
            viewHolder.leftName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.leftName.setText(msgEntity.getUser_given_name());
            viewHolder.leftData.setText(MyDateUtils.getLocalDateStringFromUTC(context,msgEntity.getContent_creation_date()));
            VolleyUtil.initNetworkImageView(context, viewHolder.leftImage, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

            //跳转资料
            viewHolder.leftImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(context, FamilyProfileActivity.class);
                    intent.putExtra("member_id", msgEntity.getUser_id());
                    context.startActivity(intent);
                }
            });

        }

        return convertView;
    }



    class ViewHolder {

        //接收
        CircularNetworkImage leftImage;
        TextView leftMessageText;
        NetworkImageView leftMessagePhoto;
        ImageView leftMessageImage;
        GifImageView leftMessageGif;
        TextView leftData;
        TextView leftName;

        //发送
        CircularNetworkImage rightImage;
        TextView rightMessageText;
        NetworkImageView rightMessagePhoto;
        ImageView rightMessageImage;
        GifImageView rightMessageGif;
        TextView rightData;
    }


























    //这里问题很大。用了hold以后数据加载乱套了。看看是不是hold写得有问题
        /*if (convertView == null)
        {
            viewHolder = new ViewHolder();

            *//*
            *用户发出的的信息。服务器也会提供，根据id号判断是自己发出然后在右边显示
            **//*
            if (msgEntity.getUser_id().equals(MainActivity.getUser().getUser_id()))//47需要变成变量，用作以后与使用的用户ID比较。
            {

                //文本内容
                if (msgEntity.getText_id() != null)
                {
                    convertView = mInflater.inflate(R.layout.list_item_right_text, null);

//                    viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
//                    viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);
                    viewHolder.rightMessageText = (TextView) convertView.findViewById(R.id.right_message_tv);

                    viewHolder.rightMessageText.setText(msgEntity.getText_description());
                }

                //照片
                else if (msgEntity.getFile_id() != null)
                {
                    convertView = mInflater.inflate(R.layout.list_item_right_photo, null);

//                    viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
//                    viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);
                    viewHolder.rightMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.right_message_niv);

                    VolleyUtil.initNetworkImageView(context, viewHolder.rightMessagePhoto, String.format(Constant.API_GET_PIC, Constant.Module_preview_xl, msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                }

                //经纬度
                else if (msgEntity.getLoc_id() != null)
                {
                    convertView = mInflater.inflate(R.layout.list_item_right_location, null);

//                    viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
//                    viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);
                    viewHolder.rightMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.right_message_niv);

                    VolleyUtil.initNetworkImageView(context, viewHolder.rightMessagePhoto
                            , String.format(Constant.MAP_API_GET_LOCATION_PIC, msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude(), context.getString(R.string.google_map_pic_size), msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude())
                            , R.drawable.network_image_default, R.drawable.network_image_default);

                    viewHolder.rightMessagePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            context.startActivity(intent);
                        }
                    });

                }

                //本地图片
                else if (msgEntity.getSticker_type().equals(".png"))
                {
                    convertView = mInflater.inflate(R.layout.list_item_right_png, null);

//                    viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
//                    viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);
                    viewHolder.rightMessageImage = (ImageView) convertView.findViewById(R.id.right_message_iv);

                    InputStream is = null;
                    try {
                        is = context.getAssets().open(msgEntity.getSticker_group_path() + msgEntity.getSticker_name() + "_B.png");
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        viewHolder.rightMessageImage.setImageBitmap(bitmap);
//                Drawable da = Drawable.createFromStream(is, null);
//                viewHolder.rightMessageImage.setImageDrawable(da);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                //本地动态图
                else if (msgEntity.getSticker_type().equals(".gif"))
                {
                    convertView = mInflater.inflate(R.layout.list_item_right_gif, null);

//                    viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
//                    viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);
                    viewHolder.rightMessageGif = (GifImageView) convertView.findViewById(R.id.right_message_gif);

                    try {
                        viewHolder.rightMessageGif.setImageDrawable(new GifDrawable(context.getAssets(), msgEntity.getSticker_group_path() + msgEntity.getSticker_name() + "_B.gif"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.rightImage = (CircularNetworkImage) convertView.findViewById(R.id.right_iv);
                viewHolder.rightData = (TextView) convertView.findViewById(R.id.right_data);

                viewHolder.rightData.setText(msgEntity.getContent_creation_date());
                VolleyUtil.initNetworkImageView(context, viewHolder.rightImage, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

            }

            *//*
            *好友消息
            **//*
            else
            {
                //文本内容
                if (msgEntity.getText_id() != null)
                {
                    convertView = mInflater.inflate(R.layout.list_item_left_text, null);

                    viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
                    viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);
                    viewHolder.leftMessageText = (TextView) convertView.findViewById(R.id.left_message_tv);

                    viewHolder.leftMessageText.setText(msgEntity.getText_description());
                }

                //照片
                else if (msgEntity.getFile_id() != null)
                {
                    convertView = mInflater.inflate(R.layout.list_item_left_photo, null);

                    viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
                    viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);
                    viewHolder.leftMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.left_message_niv);

                    VolleyUtil.initNetworkImageView(context, viewHolder.leftMessagePhoto, String.format(Constant.API_GET_PIC, Constant.Module_preview_xl, msgEntity.getUser_id(), msgEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                }

                //经纬度
                else if (msgEntity.getLoc_id() != null)
                {
                    convertView = mInflater.inflate(R.layout.list_item_left_location, null);

                    viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
                    viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);
                    viewHolder.leftMessagePhoto = (NetworkImageView) convertView.findViewById(R.id.left_message_niv);

                    VolleyUtil.initNetworkImageView(context, viewHolder.leftMessagePhoto
                            , String.format(Constant.MAP_API_GET_LOCATION_PIC, msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude(), context.getString(R.string.google_map_pic_size), msgEntity.getLoc_latitude() + "," + msgEntity.getLoc_longitude())
                            , R.drawable.network_image_default, R.drawable.network_image_default);


                    viewHolder.leftMessagePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()), Double.valueOf(msgEntity.getLoc_latitude()), Double.valueOf(msgEntity.getLoc_longitude()));
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            context.startActivity(intent);
                        }
                    });
                }

                //本地图片
                else if (msgEntity.getSticker_type().equals(".png"))
                {
                    convertView = mInflater.inflate(R.layout.list_item_left_png, null);

                    viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
                    viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);
                    viewHolder.leftMessageImage = (ImageView) convertView.findViewById(R.id.left_message_iv);

                    InputStream is = null;
                    try {
                        is = context.getAssets().open(msgEntity.getSticker_group_path() + msgEntity.getSticker_name() + "_B.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    viewHolder.leftMessageImage.setImageBitmap(bitmap);

//                    Drawable da = Drawable.createFromStream(is, null);
//                    viewHolder.leftMessageImage.setImageDrawable(da);

                }

                //本地动态图
                else if (msgEntity.getSticker_type().equals(".gif"))
                {
                    convertView = mInflater.inflate(R.layout.list_item_left_gif, null);

                    viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
                    viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);
                    viewHolder.leftMessageGif = (GifImageView) convertView.findViewById(R.id.left_message_gif);

                    try {
                        viewHolder.leftMessageGif.setImageDrawable(new GifDrawable(context.getAssets(), msgEntity.getSticker_group_path() + msgEntity.getSticker_name() + "_B.gif"));
                        Log.d("", "gif------" + msgEntity.getSticker_group_path() + msgEntity.getSticker_name() + "_B.gif");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.leftImage = (CircularNetworkImage) convertView.findViewById(R.id.left_iv);
                viewHolder.leftData = (TextView) convertView.findViewById(R.id.left_data);

                viewHolder.leftData.setText(msgEntity.getContent_creation_date());
                VolleyUtil.initNetworkImageView(context, viewHolder.leftImage, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, msgEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }*/
}
