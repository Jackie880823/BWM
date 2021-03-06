package com.madxstudio.co8.http;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.madxstudio.co8.App;

/**
 * @ClassName:  VolleyUtil
 * @Description 图标加载工具类
 * @author 钟荣观
 * @date 2014-11-14 下午3:29:42
 *
 */
public class VolleyUtil {

	// 取运行内存阈值的1/8作为图片缓存
	private static final int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) App.getContextInstance()
			.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 6;

	private static RequestQueue mQueue;

	private static RequestQueue getRequestQueue(Context context){
		if(mQueue==null) {
//			mQueue = Volley.newRequestQueue(context,null,false);
			mQueue = Volley.newRequestQueue(App.getContextInstance());
        }

		return mQueue;
//		return new HttpTools(context).getHttpRequestQueue();
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
      networkImageView.setImageUrl(url,VolleySingleton.getInstance().getImageLoader());
	}


    /**
     * @Description 对NetworkImageView控件进入初始化
     * @date 2014-11-14 下午3:30:16
     * @param context
     * @param networkImageView
     * @param url
     */
    public static void initNetworkImageView(Context context,NetworkImageView networkImageView,String url){
        networkImageView.setImageUrl(url,VolleySingleton.getInstance().getImageLoader());
    }

	public static ImageLoader.ImageContainer loadImage(Context context,String requestUrl,
													   ImageLoader.ImageListener imageListener) {
		return loadImage(context,requestUrl, imageListener, 0, 0);
	}

	public static ImageLoader.ImageContainer loadImage(Context context,String url,ImageLoader.ImageListener listener, int maxWidth, int maxHeight){
		return VolleySingleton.getInstance().getImageLoader().get(App.getContextInstance(), url, listener, maxWidth, maxHeight);
	}

	/**
	 * 外部调用次方法即可完成将url处图片现在view上，并自动实现内存和硬盘双缓存。
	 * @param url 远程url地址
	 * @param view 待现实图片的view
	 * @param defaultImageBitmap 默认显示的图片
	 * @param errorImageBitmap 网络出错时显示的图片
	 */
	public static ImageLoader.ImageContainer loadImage(Context context,final String url, final ImageView view,
													   final Bitmap defaultImageBitmap, final Bitmap errorImageBitmap){
		return loadImage(context,url, getImageLinseter(view, defaultImageBitmap,
				errorImageBitmap));
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
        request4Image(context, requestTag, url, 0, 0, Config.RGB_565, listenner, errorListenner);
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

	public static ImageLoader.ImageListener getImageLinseter(final ImageView view,
															 final Bitmap defaultImageBitmap, final Bitmap errorImageBitmap){


		return new ImageLoader.ImageListener() {
			@Override
			public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
				if(imageContainer.getBitmap() != null ){
					view.setImageBitmap(imageContainer.getBitmap());
				}else if(defaultImageBitmap != null ){
					view.setImageBitmap(defaultImageBitmap);
				}
			}


			@Override
			public void onErrorResponse(VolleyError volleyError) {
				if(errorImageBitmap != null){
					view.setImageBitmap(errorImageBitmap);
				}
			}
		};
	}


}
