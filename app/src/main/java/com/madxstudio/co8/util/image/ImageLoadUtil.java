/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha bless         Never BUG           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.util.image;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.madxstudio.co8.Constant;

/**
 * {@link ImageView} 加载图片
 * Created 16/6/23.
 *
 * @author Jackie
 * @version 1.0
 */
public class ImageLoadUtil {

    public static final int SIZE_ORIGINAL = com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

    /**
     * 初始化对应配置的{@link DrawableTypeRequest}, 统一封装加载图片的请求
     *
     * @param context
     * @param configuration
     * @return
     */
    @Nullable
    private static DrawableTypeRequest initTypeRequest(Context context, Configuration
            configuration) {
        RequestManager manager = Glide.with(context);
        DrawableTypeRequest typeRequest = null;
        if (configuration.uri != null && !configuration.uri.equals(Uri.EMPTY)) {
            if (Constant.SCHEME_HTTP.equalsIgnoreCase(configuration.uri.getScheme()) || Constant
                    .SCHEME_HTTPS.equalsIgnoreCase(configuration.uri.getScheme())) {
                configuration.cacheStrategy = Configuration.CACHE_STRATEGY_ALL;
            } else {
                configuration.cacheStrategy = Configuration.CACHE_STRATEGY_RESULT;
            }
            typeRequest = manager.load(configuration.uri);
        } else if (!TextUtils.isEmpty(configuration.url)) {
            if (configuration.url.contains(Constant.SCHEME_HTTP)) {
                configuration.cacheStrategy = Configuration.CACHE_STRATEGY_ALL;
            } else {
                configuration.cacheStrategy = Configuration.CACHE_STRATEGY_RESULT;
            }
            typeRequest = manager.load(configuration.url);
        } else if (configuration.file != null) {
            configuration.cacheStrategy = Configuration.CACHE_STRATEGY_RESULT;
            typeRequest = manager.load(configuration.file);
        } else if (configuration.model != null) {
            typeRequest = manager.load(configuration.model);
        } else if (configuration.drawableId != null) {
            configuration.cacheStrategy = Configuration.CACHE_STRATEGY_RESULT;
            typeRequest = manager.load(configuration.drawableId);
        }

        if (typeRequest == null) {
            return null;
        }

        return typeRequest;
    }

    /**
     * @param context
     * @param configuration
     * @return
     */
    private static GenericRequestBuilder initRequestBuilder(@NonNull Context context, @NonNull
    Configuration configuration) {
        // 这里牵涉到Glide的build的流程，不要轻易改变其顺序，否则有些build设置无法起做用
        DrawableTypeRequest typeRequest = initTypeRequest(context, configuration);
        if (typeRequest == null) {
            return null;
        }

        GenericRequestBuilder genericRequestBuilder;
        if (configuration.isGif) {
            // gif图片不能裁剪
            genericRequestBuilder = typeRequest.asGif();
        } else {
            DrawableRequestBuilder builder;

            // 加载图片的裁剪方式
            if (configuration.scaleType == Configuration.CENTER_CROP) {
                builder = typeRequest.centerCrop();
            } else if (configuration.scaleType == Configuration.FIT_CENTER) {
                builder = typeRequest.fitCenter();
            } else {
                builder = typeRequest;
            }


            genericRequestBuilder = builder;
        }

        // 是否需要动画
        if (configuration.doNotAnimate) {
            genericRequestBuilder = genericRequestBuilder.dontAnimate();
        }

        if (configuration.thumbnailAvailable) {
            // 模糊渐进
            genericRequestBuilder = genericRequestBuilder.thumbnail(0.5f);
        }

        switch (configuration.cacheStrategy) {
            case Configuration.CACHE_STRATEGY_RESULT:
                genericRequestBuilder = genericRequestBuilder.diskCacheStrategy(DiskCacheStrategy
                        .RESULT);
                break;
            case Configuration.CACHE_STRATEGY_SOURCE:
                genericRequestBuilder = genericRequestBuilder.diskCacheStrategy(DiskCacheStrategy
                        .SOURCE);
                break;
            default:
                genericRequestBuilder = genericRequestBuilder.diskCacheStrategy(DiskCacheStrategy
                        .ALL);
                break;
        }


        genericRequestBuilder = genericRequestBuilder.placeholder(configuration.placeholderId)
                .error(configuration.errorDrawableId);
        //
        if (configuration.thumbnailUri != null && !configuration.thumbnailUri.equals(Uri.EMPTY)) {
            configuration.uri = configuration.thumbnailUri;
            configuration.url = null;
            configuration.file = null;
            configuration.model = null;
            configuration.thumbnailUri = null;
            GenericRequestBuilder thumbnailRequest = initRequestBuilder(context, configuration);
            genericRequestBuilder = genericRequestBuilder.thumbnail(thumbnailRequest);
        }
        return genericRequestBuilder;
    }

    /**
     * 根据{@link Configuration}配置加载图片到指定的{@link ImageView imageView}
     *
     * @param context
     * @param imageView
     * @param configuration 需要显示的配置封装实例
     */
    public static void display(@NonNull Context context, ImageView imageView, @NonNull
    Configuration configuration) {

        GenericRequestBuilder genericRequestBuilder = initRequestBuilder(context, configuration);

        if (genericRequestBuilder != null) {
            genericRequestBuilder.into(imageView);
        }
    }

    /**
     * 加载指定{@link String 路径}的图片到{@link ImageView imageView}控件中 <br/>
     * 这个函数后面应该会丢弃，若想使用相应加载功能请使用{@link #display(Context, ImageView, Configuration)}
     * <br/>
     * 设置{@link Configuration#url}的值与{@link String url}对应即可
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Deprecated
    public static void display(Context context, String url, ImageView imageView) {
        Configuration configuration = new Configuration();
        configuration.url = url;
        display(context, imageView, configuration);
    }

    /**
     * 加载指定{@link Uri 路径}的图片到{@link ImageView imageView}控件中 <br/>
     * 这个函数以后应该会丢弃，若想使用相应加载功能请使用{@link #display(Context, ImageView, Configuration)}
     * <br/>
     * 设置{@link Configuration#url}的值与{@link Uri uri}对应即可
     *
     * @param context
     * @param uri
     * @param imageView
     */
    @Deprecated
    public static void display(@NonNull Context context, Uri uri, ImageView imageView) {
        Configuration configuration = new Configuration();
        configuration.uri = uri;
        display(context, imageView, configuration);
    }

    /**
     * 加载指定{@link DrawableRes}的图片到{@link ImageView imageView}控件中 <br/>
     * 这个函数以后应该会丢弃，若想使用相应加载功能请使用{@link #display(Context, ImageView, Configuration)}
     * <br/>
     * 设置{@link Configuration#url}的值与{@link DrawableRes drawableID}对应即可
     *
     * @param context
     * @param drawableID
     * @param imageView
     */
    @Deprecated
    public static void display(Context context, @DrawableRes int drawableID, ImageView imageView) {
        Configuration configuration = new Configuration();
        configuration.drawableId = drawableID;

        display(context, imageView, configuration);
    }
}
