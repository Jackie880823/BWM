package com.madx.bwm.http;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.madx.bwm.R;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.MessageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    ;


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

    public static void saveImageToGallery(Context context, Bitmap bmp, String prefix) throws IOException {

        String fileName = getPicPath(context, prefix);
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();

//        Log.i("","fileName=========="+file.getName());


        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),bmp,"LLL","");
            //照片下载完提示下载位置
            MessageUtil.showMessage(context, context.getString(R.string.saved_to_path) + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(fileName)));

    }


}