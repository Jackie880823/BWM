package com.bondwithme.BondWithMe.http;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.http.cache.ImageLreCache;


public class VolleySingleton {


    // 取运行内存阈值的1/8作为图片缓存
    private static final int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) App.getContextInstance()
            .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 6;
    private static ImageLreCache mImageLreCache = new ImageLreCache(MEM_CACHE_SIZE,"images",MEM_CACHE_SIZE*3);

    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(App.getContextInstance());
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

//        mImageLoader = new ImageLoader(mRequestQueue,mImageLreCache);

    }


    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }


    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }


}
