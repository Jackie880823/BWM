package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.exception.StickerTypeException;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Jackie on 7/28/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class UniversalImageLoaderUtil {
    private static final String TAG = UniversalImageLoaderUtil.class.getSimpleName();

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
        // 设置加载的图片缓存在内存中
        builder.cacheInMemory(true);
        // 设置加载的图片缓存在SD卡中
        builder.cacheOnDisk(true);
        // 设置图片的解码类型
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        // 图片加载好后渐入的动画时间
        builder.displayer(new FadeInBitmapDisplayer(100));
        // 加载图片大小为目标控件大小一致
        builder.imageScaleType(ImageScaleType.EXACTLY);
        // 构建完成
        options = builder.build();
    }

    public static DisplayImageOptions.Builder cloneFrom(DisplayImageOptions options) {
        DisplayImageOptions.Builder result = new DisplayImageOptions.Builder().cloneFrom(options);
        return result;
    }

    /**
     * 解码表情的方法，传入显示表情的控件和表情相关的参数即可正确加载表情图片
     *
     * @param imageView        显示表情的控件
     * @param stickerGroupPath 表情组目录
     * @param stickerName      表情名称
     * @param type             表情类型格式:
     *                         <br> - .gif gif格式
     *                         <br> - .png png格式
     */
    public static void decodeStickerPic(GifImageView imageView, String stickerGroupPath, String stickerName, String type) throws StickerTypeException {

        if(!TextUtils.isEmpty(stickerGroupPath)) {
            if(stickerGroupPath.contains("/")) {
                // 替换老版本表情中多余的"/"
                stickerGroupPath = stickerGroupPath.replace("/", "");
            }
        } else {
            return;
        }

        // 先从文件中读取对应表情，如果读取不到再从网络获取图片
        // 表情目录
        String filePath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator + stickerName;
        // 表情文件
        File stickerFile;

        if(Constant.Sticker_Gif.equals(type)) {
            // gif格式的表情图片
            filePath += "_B.gif";
            stickerFile = new File(filePath);
            if(stickerFile.exists()) {
                // 图片存在直接读取显示
                try {
                    GifDrawable gifDrawable = new GifDrawable(stickerFile);
                    imageView.setImageDrawable(gifDrawable);
                    return;
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(Constant.Sticker_Png.equals(type)) {
            // png格式的表情图片
            filePath += "_B.png";
            stickerFile = new File(filePath);
            if(stickerFile.exists()) {
                // 图片存在直接读取显示
                InputStream is;
                // 得到数据流
                try {
                    is = new FileInputStream(stickerFile);
                    // 将流转化成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bitmap);//显示图片
                    return;
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 即不是gif格式也不是png表情
            LogUtil.e(TAG, "decodeStickerPic& the type is error");
            throw new StickerTypeException("sticker type error, not found the type: " + type);
        }

        // 没有从本地得到与表情信息对应的表情图片，解释得到网络路径从网络中获取对应的表情图片
        final String urlPath = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(), stickerName, stickerGroupPath, type);
        Log.i(TAG, "decodeStickerPic& urlPath: " + urlPath);
        ImageLoader.getInstance().displayImage(urlPath, imageView, UniversalImageLoaderUtil.options);
    }
}
