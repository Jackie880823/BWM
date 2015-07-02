package com.madx.bwm.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.toolbox.NetworkImageView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.StickerGroupEntity;
import com.madx.bwm.entity.StickerItemEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.madx.bwm.R.color.default_editor_panel_bg;

/**
 * Created by heweidong on 15/6/11.
 */
public class StickerItemAdapter extends BaseAdapter{
    private String TAG = StickerItemAdapter.class.getSimpleName();
    private Context mContext;
    private List<StickerItemEntity> data;
    private StickerGroupEntity stickerGroupEntity;


    public StickerItemAdapter(Context mContext, List<StickerItemEntity> data,StickerGroupEntity stickerGroupEntity) {
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
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sticker_item_for_grid,null);
            viewHolder.ivStickerItem = (NetworkImageView) convertView.findViewById(R.id.iv_sticker_item);

//            viewHolder.ivStickerItem.setOnHoverListener(new View.OnHoverListener() {
//                @Override
//                public boolean onHover(View v, MotionEvent event) {
//                    pressTime = System.currentTimeMillis();
//                    if((pressTime - event.getDownTime()) > 2*1000){

//                        popupBigSticker.showAsDropDown(v,0,0);
//                    }
//                    return false;
//                }
//            });
//
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StickerItemEntity stickerItemEntity = data.get(position);
        Log.i(TAG,"stickerItemEntity============="+stickerItemEntity.toString());
        VolleyUtil.initNetworkImageView(mContext,viewHolder.ivStickerItem,
                String.format( Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), stickerItemEntity.getSticker_name()+"_S", stickerGroupEntity.getPath(),stickerGroupEntity.getType()),
                R.drawable.network_image_default, R.drawable.network_image_default);
        viewHolder.ivStickerItem.setOnLongClickListener(new View.OnLongClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onLongClick(View v) {
                popupBigSticker = new PopupWindow();
                popupBigSticker.setWidth(250);
                popupBigSticker.setHeight(250);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.gif_big_sticker);
                popupBigSticker.setBackgroundDrawable(drawable);
                if (stickerGroupEntity!=null && stickerGroupEntity.getType().equals(".png")){
                    NetworkImageView bigSticker = new NetworkImageView(mContext);
                    bigSticker.setScaleType(ImageView.ScaleType.FIT_XY);
                    bigSticker.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    bigSticker.setPadding(5,5,5,5);
                    VolleyUtil.initNetworkImageView(mContext, bigSticker,
                            String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), data.get(position).getSticker_name() + "_B", stickerGroupEntity.getPath(), stickerGroupEntity.getType()));
                    popupBigSticker.setContentView(bigSticker);
                }else if (stickerGroupEntity!=null && stickerGroupEntity.getType().equals(".gif")){
                    GifImageView bigSticker = new GifImageView(mContext);
                    bigSticker.setScaleType(ImageView.ScaleType.FIT_XY);
                    bigSticker.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    bigSticker.setPadding(2,2,2,2);
                    downloadAsyncTask(bigSticker,String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), data.get(position).getSticker_name() + "_B", stickerGroupEntity.getPath(), stickerGroupEntity.getType()));
                    popupBigSticker.setContentView(bigSticker);
                }
                popupBigSticker.setOutsideTouchable(true);
                popupBigSticker.setFocusable(true);

                if ((position + 1) % 3 == 1) {
                    popupBigSticker.showAsDropDown(v, 0, -400);
                } else if ((position + 1) % 3 == 2) {
                    popupBigSticker.showAsDropDown(v, -60, -400);
                } else if ((position + 1) % 3 == 0) {
                    popupBigSticker.showAsDropDown(v, -110, -400);
                }
                return false;
            }
        });
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
    public void downloadAsyncTask( final GifImageView gifImageView, final String path) {
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


    public class ViewHolder{
        NetworkImageView ivStickerItem;

        public ViewHolder() {
            super();
        }
    }
}
