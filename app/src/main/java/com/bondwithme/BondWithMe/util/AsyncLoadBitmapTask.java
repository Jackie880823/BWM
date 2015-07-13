package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bondwithme.BondWithMe.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuweiping on 5/5/15.
 * 异步加载本图片，
 */
public class AsyncLoadBitmapTask extends AsyncTask<Uri, Integer, Bitmap> {
    private static final String TAG = AsyncLoadBitmapTask.class.getSimpleName();
    private Context mContext;
    private int mColumnWidthHeight;
    private WeakReference imageViewReference;
    private Uri uri;
    // 存放图片的Map, 防止重复从手机中获取同一张图片提高加载速度
    private static Map<Uri, Bitmap> bitmaps = new HashMap<>();

    public AsyncLoadBitmapTask(Context context, ImageView imageView, int columnWidthHeight) {
        mContext = context;
        imageViewReference = new WeakReference(imageView);
        mColumnWidthHeight = columnWidthHeight;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Bitmap doInBackground(Uri... params) {
        uri = params[0];
        if(uri == null) {
            // ur为空则返回空
            return null;
        }
        // 获取memory中对应的图片
        Bitmap bitmap = getBitmap4MemoryMap(uri);
        if(bitmap == null) {
            // memory中没有这张图片从手机内存
            Log.i(TAG, "doInBackground& not cache bitmap");
            bitmap = LocalImageLoader.getMiniThumbnailBitmap(mContext, uri, mColumnWidthHeight);
            if(bitmaps.size() > 60) {
                bitmaps.clear();
            }
            bitmaps.put(uri, bitmap);
        } else {
            Log.i(TAG, "doInBackground& cached bitmap");
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(imageViewReference != null) {
            ImageView imageView = (ImageView) imageViewReference.get();
            if(imageView != null) {
                if(bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.default_head_icon);
                }
            }
        }
    }

    public Uri getUri() {
        return uri;
    }

    /**
     * 从map集合中获取对应uri的图片
     *
     * @param uri
     * @return 返回获取到的图片，有可能为null
     */
    public static Bitmap getBitmap4MemoryMap(Uri uri) {
        return bitmaps.get(uri);
    }

    /**
     * 清楚存放在Memory中的图片
     */
    public static void clearBitmaps() {
        bitmaps.clear();
    }
}
