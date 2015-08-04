package com.bondwithme.BondWithMe.http;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;

import java.io.File;
import java.io.OutputStream;

public class PicturesCacheUtil extends FileUtil {

    /**
     * 缓存图片名
     */
    private final static String CACHE_PIC_NAME = "/cache_%s.png";
    private final static String SAVE_PIC_NAME = "/%s_%s.png";
    /**
     * 图片保存目录名
     */
    private final static String PIC_DIR_NAME = "/pic";
    /**
     * 图片保存目录路径,和PIC_DIR_NAME相关
     */
    private static String save_pic_path;

    private static String pictureCachePath;

    private PicturesCacheUtil() {
    }


    /**
     * //TODO 去掉这个方法
     *
     * @param context
     */
    private static void makeCacheDir(Context context) {
        pictureCachePath = FileUtil.getCacheFilePath(context);
    }


    /**
     * 获取缓存图片路径(包括图片name,生成的)
     *
     * @param context
     * @return
     */
    public static String getCachePicPath(Context context) {
        return getCacheFilePath(context) + String.format(CACHE_PIC_NAME, "" + System.currentTimeMillis());
    }

    public static void saveToCachePic(Context context, Bitmap bmp) {
        saveToFile(getCachePicPath(context), bmp);
    }

    /**
     * 获取应用保存的图片路径(包括图片name,生成的)
     *
     * @param context
     * @return
     */
    public static String getPicPath(Context context, String prefix) {
        return getSavePicPath(context) + String.format(SAVE_PIC_NAME, prefix, "" + System.currentTimeMillis());
    }

    /**
     * 保存到本地图片的路径(文件夹)
     *
     * @return
     */
    private static String getSavePicPath(Context context) {
        if (save_pic_path == null) {
            save_pic_path = getSavePath(context,true) + PIC_DIR_NAME;
        }

        File dir = new File(save_pic_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return save_pic_path;
    }

    /**
     * 获取缓存图片
     * @param context
     * @param name
     * @return
     */
    public static File getCachePicFileByName(Context context, String name) {
        makeCacheDir(context);
        if (name != null && !"".equals(name)) {
            File f = new File(pictureCachePath, name);

            return f;
        }
        return null;

    }

    public static void saveImageToGallery(Context context, Bitmap bmp, String prefix)  {

//        String fileName = getPicPath(context, prefix);
//        saveToFile(fileName, bmp);

//        Log.i("", "fileName==========" + fileName);


//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()); // DATE HERE
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
////        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
//
//        ContentResolver mContentResolver = context.getContentResolver();
//        mContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
////
//        String path =    MediaStore.Images.Media.insertImage(mContentResolver,fileName,"BondWithMe_Wall",null);
//                    file.getAbsolutePath(), file.getName(), "BondWithMe_Wall");
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),bmp,"LLL","");
        String path = insertImage(context.getContentResolver(),bmp,prefix,"BondWithMe_Wall");
//            //照片下载完提示下载位置
            MessageUtil.showMessage(context, context.getString(R.string.saved_to_path) + path);
//        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));

    }

    public static final String insertImage(ContentResolver cr, Bitmap source,
                                           String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()); // DATE HERE

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
//                Bitmap microThumb = StoreThumbnail(cr, miniThumb, id, 50F, 50F,
//                        MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }


}