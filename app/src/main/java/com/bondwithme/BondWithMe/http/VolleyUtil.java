package com.bondwithme.BondWithMe.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;

/**
 * @ClassName:  VolleyUtil
 * @Description 图标加载工具类
 * @author 钟荣观
 * @date 2014-11-14 下午3:29:42
 *
 */
public class VolleyUtil {

	private static RequestQueue mQueue;
	private static ImageLoader mImageLoader;
	private static ImageCache mImageCache;

	static{
		mImageCache = new ImageCache() {
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				ImageCacheUtil.put(url, bitmap);
			}

			@Override
			public Bitmap getBitmap(String url) {
				return ImageCacheUtil.get(url);
			}
		};
	}

	private static RequestQueue getRequestQueue(Context context){
//		if(mQueue==null) {
////			mQueue = Volley.newRequestQueue(context,null,false);
//			mQueue = Volley.newRequestQueue(context);
//        }

//		return mQueue;
		return new HttpTools(context).getHttpRequestQueue();
	}

	private static ImageLoader getImageLoader(Context context){
		if(mImageLoader==null){
            /**for no cache image loader*/
//			mImageLoader = new ImageLoader(getRequestQueue(context), null);
			mImageLoader = new ImageLoader(getRequestQueue(context), mImageCache);
		}
		return mImageLoader;
	}

	/**
	 * @Description 对NetworkImageView控件进入初始化
	 * @date 2014-11-14 下午3:30:16
	 * @param context
	 * @param networkImageView
	 * @param url
	 * @param defalutImage 默认图片
	 * @param failedImage 加载失败图片
	 */
	public static void initNetworkImageView(Context context,NetworkImageView networkImageView,String url,int defalutImage,int failedImage){
	  networkImageView.setDefaultImageResId(defalutImage);
	  networkImageView.setErrorImageResId(failedImage);
      networkImageView.setImageUrl(url,getImageLoader(context));
	}


    /**
     * @Description 对NetworkImageView控件进入初始化
     * @date 2014-11-14 下午3:30:16
     * @param context
     * @param networkImageView
     * @param url
     */
    public static void initNetworkImageView(Context context,NetworkImageView networkImageView,String url){
        networkImageView.setImageUrl(url,getImageLoader(context));
    }


	/**
	 * @Description 从网络加载图片,内部使用缓存
	 * @date 2014-11-14 下午2:53:51
	 * @param context
	 * @param url
	 * @param imageListener
	 */
	public static void loadImage(Context context,String url,ImageLoader.ImageListener imageListener){
		loadImage(context, url, 0, 0, imageListener);
	}
	/**
	 * @Description 从网络加载图片,内部使用缓存
	 * @date 2014-11-14 下午2:54:35
	 * @param context
	 * @param url
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param imageListener
	 */
	public static void loadImage(Context context,String url,int maxWidth,int maxHeight,ImageLoader.ImageListener imageListener){
		getImageLoader(context).get(context,url, imageListener,maxWidth,maxHeight);
	}

	/**
	 * @Description 从网络加载图片,无缓存
	 * @date 2014-11-14 下午3:29:04
	 * @param context
	 * @param url
	 * @param listenner
	 * @param errorListenner
	 */
	public static void request4Image(Context context,String requestTag,String url,Response.Listener<Bitmap> listenner,Response.ErrorListener errorListenner){
        request4Image(context,requestTag,url, 0, 0, Config.RGB_565, listenner,errorListenner);
	}

	/**
	 * @Description 从网络加载图片,无缓存
	 * @date 2014-11-14 下午3:29:20
	 * @param context
	 * @param url
	 * @param maxWidth
	 * @param maxHeight
	 * @param rgb
	 * @param listenner
	 * @param errorListenner
	 */
	public static void request4Image(Context context,String requestTag,String url,int maxWidth,int maxHeight,Config rgb,Response.Listener<Bitmap> listenner,Response.ErrorListener errorListenner){
		ImageRequest imageRequest = new ImageRequest(context,url,listenner, maxWidth, maxHeight, rgb, errorListenner);
        addRequest2Queue(context, imageRequest, requestTag);
	}

    public static <T extends Class> void request4Entity(Context context,int method,String requestTag,String url,T t
            ,Response.Listener<T> listener,Response.ErrorListener errorListener){
        addRequest2Queue(context,new GsonRequest(method,url,t,listener,errorListener),requestTag);
    }



    public static void request4ArrayEntity(Context context,int method,Object requestTag,String url,Class <? extends Class>  t
            ,Response.Listener<Class <? extends Class>> listener,Response.ErrorListener errorListener){

    }

	public static void addRequest2Queue(Context context,Request request,String requestTag){
        request.setTag(requestTag);
		getRequestQueue(context).add(request);
    }



    public static void cancelPendingRequests(Context context,Object tag) {
        if (getRequestQueue(context) != null) {
            getRequestQueue(context).cancelAll(tag);
        }
    }


}
