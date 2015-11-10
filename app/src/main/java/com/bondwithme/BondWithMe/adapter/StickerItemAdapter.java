package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.entity.StickerItemEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by heweidong on 15/6/11.
 */
public class StickerItemAdapter extends BaseAdapter {
    private String TAG = StickerItemAdapter.class.getSimpleName();
    private Context mContext;
    private List<StickerItemEntity> data;
    private StickerGroupEntity stickerGroupEntity;
    private int addSize = 60;


    public StickerItemAdapter(Context mContext, List<StickerItemEntity> data, StickerGroupEntity stickerGroupEntity) {
        this.mContext = mContext;
        this.data = data;
        this.stickerGroupEntity = stickerGroupEntity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;
    PopupWindow popupBigSticker = null;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sticker_item_for_grid, null);
            viewHolder.ivStickerItem = (NetworkImageView) convertView.findViewById(R.id.iv_sticker_item);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen._90dp));
//            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT );
            convertView.setLayoutParams(layoutParams);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        setStickerItem(viewHolder.ivStickerItem,position,false);

//        StickerItemEntity stickerItemEntity = data.get(position);
//        String picPath = FileUtil.getSmallStickerPath(mContext, stickerGroupEntity.getPath(), stickerItemEntity.getSticker_name(), stickerGroupEntity.getType());
//        File file = new File(picPath);
//        if(file.exists()){
//            Bitmap bmp = BitmapFactory.decodeFile(picPath);
////        Bitmap bmp = LocalImageLoader.loadBitmapFromFile(mContext, picPath, holder.ivMySticker.getWidth(), holder.ivMySticker.getHeight());
//            viewHolder.ivStickerItem.setImageBitmap(bmp);
//            LogUtil.d(TAG,"file.exists()");
//        }else{
//            LogUtil.d(TAG,"initNetworkImageView()");
//            VolleyUtil.initNetworkImageView(mContext, viewHolder.ivStickerItem,
//                    String.format(Constant.API_STICKER_ORIGINAL_IMAGE, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath() + "_S_" + stickerItemEntity.getSticker_name() + stickerGroupEntity.getType(),stickerGroupEntity.getVersion()),
//                    R.drawable.network_image_default, R.drawable.network_image_default);
//        }


        viewHolder.ivStickerItem.setOnLongClickListener(new LongClickListener(position));
        viewHolder.ivStickerItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (popupBigSticker != null && popupBigSticker.isShowing()) {
                            popupBigSticker.dismiss();
                        }

                }
                return false;
            }
        });
        return convertView;
    }

    /**
     * 此方法用来异步加载图片
     *
     * @param path
     */
    public void downloadAsyncTask(final GifImageView gifImageView, final String path) {
        new AsyncTask<String, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(String... params) {
                return getImageByte(path);
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
                        GifDrawable gifDrawable = new GifDrawable(resultByte);
                        if (gifDrawable != null && gifImageView != null) {
                            gifImageView.setImageDrawable(gifDrawable);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.execute(new String[]{});

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


    class ViewHolder {
        private NetworkImageView ivStickerItem;

    }

    class LongClickListener implements View.OnLongClickListener {
        int position;

        public LongClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onLongClick(View view) {
            popupBigSticker = new PopupWindow();
            popupBigSticker.setWidth(view.getWidth() + addSize);
            popupBigSticker.setHeight(view.getWidth() + addSize);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.gif_big_sticker);
            popupBigSticker.setBackgroundDrawable(drawable);
            if (stickerGroupEntity != null && stickerGroupEntity.getType().equals(".png")) {
                NetworkImageView bigSticker = new NetworkImageView(mContext);
                bigSticker.setScaleType(ImageView.ScaleType.FIT_XY);
                bigSticker.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    bigSticker.setPadding(5,5,5,5);
//                VolleyUtil.initNetworkImageView(mContext, bigSticker,
//                        String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), data.get(position).getSticker_name() + "_B", stickerGroupEntity.getPath(), stickerGroupEntity.getType()));

                setStickerItem(bigSticker,position,false);
                popupBigSticker.setContentView(bigSticker);
            } else if (stickerGroupEntity != null && stickerGroupEntity.getType().equals(".gif")) {
                GifImageView bigSticker = new GifImageView(mContext);
                bigSticker.setScaleType(ImageView.ScaleType.FIT_XY);
                bigSticker.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                bigSticker.setPadding(2, 2, 2, 2);
//                downloadAsyncTask(bigSticker, String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), data.get(position).getSticker_name() + "_B", stickerGroupEntity.getPath(), stickerGroupEntity.getType()));
                setStickerItem(bigSticker,position,true);
                popupBigSticker.setContentView(bigSticker);
            }
            popupBigSticker.setOutsideTouchable(true);
            popupBigSticker.setFocusable(true);

//                if ((position + 1) % 3 == 1) {
//                    popupBigSticker.showAsDropDown(v, 0, -450);
//                } else if ((position + 1) % 3 == 2) {
//                    popupBigSticker.showAsDropDown(v, -60, -450);
//                } else if ((position + 1) % 3 == 0) {
//                    popupBigSticker.showAsDropDown(v, -110, -450);
//                }
            LogUtil.d(TAG, "==v.getWidth()==" + view.getWidth() + "==v.getHeight()==" + view.getHeight());

            popupBigSticker.showAsDropDown(view, -addSize / 2, -(int) 3 * view.getHeight());
            return true;
        }
    }
    private void setStickerItem(ImageView item,int position,boolean isGif) {
        StickerItemEntity stickerItemEntity = data.get(position);
        String picPath = FileUtil.getBigStickerPath(mContext, stickerGroupEntity.getPath(), stickerItemEntity.getSticker_name(), stickerGroupEntity.getType());
        File file = new File(picPath);
        if(file.exists()){
            if (isGif){
                try{
                    item.setImageDrawable(new GifDrawable(file));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                Bitmap bmp = BitmapFactory.decodeFile(picPath);
                item.setImageBitmap(bmp);
            }
            LogUtil.d(TAG, "file.exists()");
        }else{
            if (isGif){
                downloadAsyncTask((GifImageView) item, String.format(Constant.API_STICKER_ORIGINAL_IMAGE, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath() + "_B_" + data.get(position).getSticker_name() + stickerGroupEntity.getType(), stickerGroupEntity.getVersion()));
            }else {
                VolleyUtil.initNetworkImageView(mContext, (NetworkImageView)item,
                        String.format(Constant.API_STICKER_ORIGINAL_IMAGE, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath() + "_B_" + data.get(position).getSticker_name() + stickerGroupEntity.getType(), stickerGroupEntity.getVersion()),
                        R.drawable.network_image_default, R.drawable.network_image_default);
            }
        }
    }
}
