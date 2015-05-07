package com.madx.bwm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.madx.bwm.R;

import java.lang.ref.WeakReference;

/**
 * Created by zhuweiping on 5/5/15.
 * 异步加载本图片，
 */
public class AsyncLoadBitmapTask extends AsyncTask<Uri, Integer, Bitmap> {
    private Context mContext;
    private int mColumnWidthHeight;
    private WeakReference imageViewReference;
    private Uri uri;

    public AsyncLoadBitmapTask(Context context, ImageView imageView, int columnWidthHeight){
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
        if (uri == null) {
            // ur为空则返回空
            return null;
        }

        return LocalImageLoader.getMiniThumbnailBitmap(mContext, uri, mColumnWidthHeight);
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
}
