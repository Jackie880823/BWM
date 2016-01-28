package com.bondwithme.BondCorp.http;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.bondwithme.BondCorp.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
     * @param context
     */
    private static void makeCacheDir(Context context,boolean isOut) {
        pictureCachePath = FileUtil.getCacheFilePath(context,isOut);
    }


    /**
     * 获取缓存图片路径(包括图片name,生成的)
     *
     * @param context
     * @return
     */
    public static String getCachePicPath(Context context,boolean isOut) {
        return getCacheFilePath(context,isOut) + String.format(CACHE_PIC_NAME, "" + System.currentTimeMillis());
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
     * 保存bitmap到文件
     *
     * @param filePath 保存的路径
     * @param bmp
     */
    public static void saveToFile(String filePath, Bitmap bmp) {

        try {
            FileOutputStream out = new FileOutputStream(filePath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
        }


    }

    /**
     * 保存到本地图片的路径(文件夹)
     *
     * @return
     */
    private static String getSavePicPath(Context context) {
        if (save_pic_path == null) {
            save_pic_path = getSaveRootPath(context, true) + PIC_DIR_NAME;
        }

        File dir = new File(save_pic_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return save_pic_path;
    }

    /**
     * 获取缓存图片
     *
     * @param context
     * @param name
     * @return
     */
    public static File getCachePicFileByName(Context context, String name,boolean isOut) {
        makeCacheDir(context,isOut);
        if (name != null && !"".equals(name)) {
            File f = new File(pictureCachePath, name);

            return f;
        }
        return null;

    }

    public static String saveImageToGallery(Context context, String path,String prefix) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, prefix);
        values.put(MediaStore.Images.Media.DESCRIPTION, prefix);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, path);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
        return path;
    }

    /**
     * @param context
     * @param oldFile 原文件
     * @param prefix
     * @return
     * @throws IOException
     */
    public static String saveImageToGallery(Context context, File oldFile, String prefix) throws IOException {
        String newPath = getPicPath(context, prefix);
        File newFile = new File(newPath);
//        oldFile.renameTo(newFile);//移动文件
        copyFileUsingChannel(oldFile,newFile);
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, prefix);
//        values.put(MediaStore.Images.Media.DESCRIPTION, prefix);
//        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//        values.put(MediaStore.MediaColumns.DATA, newPath);
//
//        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
////        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(newPath)));
        return saveImageToGallery(context,newPath,prefix);
    }

//    public static String saveImageToGallery(Context context, Bitmap bmp, String prefix) throws IOException {
//        String fileName = getPicPath(context, prefix);
//        File file = new File(fileName);
//        FileOutputStream fos = new FileOutputStream(file);
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//        fos.flush();
//        fos.close();
//        return saveImageToGallery(context,file,prefix);
//
//    }

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
                    source.compress(Bitmap.CompressFormat.PNG, 50, imageOut);
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