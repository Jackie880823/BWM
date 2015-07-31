package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.bondwithme.BondWithMe.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Jackie on 7/28/15.
 * @author Jackie
 * @version 1.0
 */
public class UniversalImageLoaderUtil {

    public static DisplayImageOptions options;

    /**
     * 全局初始化{@link ImageLoader},用于优化本地或网络加载图片
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration configuration;
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        builder.diskCacheFileCount(100);
        builder.writeDebugLogs();
        configuration = builder.build();
        ImageLoader.getInstance().init(configuration);
        initOptions();
    }

    /**
     * 初始化图片加载时的显示格式{@link DisplayImageOptions}
     */
    private static void initOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        // 设置图片加载/解码过程中错误时候显示的图片
        builder.showImageOnFail(R.drawable.network_image_default);
        // 设置图片在加载期间显示的图片
        builder.showImageOnLoading(R.drawable.network_image_default);
        // 设置图片Uri为空或是错误的时候显示的图
        builder.showImageForEmptyUri(R.drawable.network_image_default);
        // 考虑JPEG图像EXIF参数（旋转，翻转）
        builder.considerExifParams(true);
        // 设置加载的图片是否缓存在内存中
        builder.cacheInMemory(true);
        //设置图片的解码类型
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        // 图片加载好后渐入的动画时间
        builder.displayer(new FadeInBitmapDisplayer(100));
        // 加载图片大小为目标控件大小一致
        builder.imageScaleType(ImageScaleType.EXACTLY);
        // 构建完成
        options = builder.build();
    }

    public static DisplayImageOptions.Builder cloneFrom(DisplayImageOptions options){
        DisplayImageOptions.Builder result = new DisplayImageOptions.Builder().cloneFrom(options);
        return result;
    }
}
