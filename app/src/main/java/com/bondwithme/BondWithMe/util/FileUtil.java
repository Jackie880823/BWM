package com.bondwithme.BondWithMe.util;

/**
 * Created by wing on 15/3/22.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static File path;
    private final static String CACHE_DIR_NAME = "/cache";
    public final static String BANNER_DIR_NAME = "/banner";
    private static File appPath;

    /**
     * 获取保存路径
     * @param context
     * @param isOutPath 是否保存在app外(沙盒)
     * @return
     */
    public static File getSavePath(Context context,boolean isOutPath) {

        if(isOutPath) {
            if (path == null) {
                if (hasSDCard()) { // SD card
                    path = new File(getSDCardPath() + "/" + AppInfoUtil.getApplicationName(context));
                    path.mkdir();
                } else {
                    path = Environment.getDataDirectory();
                }
            }
            LogUtil.d("","2getSavePath=========="+appPath);
            return path;
        }else{
            if(appPath==null){
                appPath  = context.getFilesDir();
            }
            LogUtil.d("","1getSavePath=========="+appPath);
            return appPath;
        }

    }
    /**
     * 获取全局缓存目录路径
     * @param context
     * @return
     */
    public static String getCacheFilePath(Context context) {
        File f = getSavePath(context,true);

        f = new File(f.getAbsolutePath()+CACHE_DIR_NAME);
        if(!f.exists()){
            f.mkdirs();
        }

        return f.getAbsolutePath();
    }

    /**
     * 保存bitmap到文件
     * @param filename 保存的路径
     * @param bmp
     */
    public static void saveToFile(String filename,Bitmap bmp) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            bmp.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch(Exception e) {}
    }

    /**
     * 判断是否有sd卡
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取sd卡路径
     * @return
     */
    public static String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }

    /**
     * 获取uri的真实url
     * @param context
     * @param contentURI
     * @return
     */
    public static String getRealPathFromURI(Context context,Uri contentURI) {
        String result;
        String[] proj = { MediaStore.Images.ImageColumns.DATA };
        Cursor cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return String.format(result, "utf-8");
    }

    /***
     * clear the app cache create when photo handling,not app all cache
     * @param context
     */
    public static void clearCache(Context context){
        File fileRoot = new File(getCacheFilePath(context));
        if(fileRoot!=null) {
            File[] cacheFiles = fileRoot.listFiles();
            for (File file : cacheFiles) {
                file.delete();
            }
        }
    }

    /**
     * get all files path of assets by parent path
     * @param context
     * @param path parent path
     * @return
     */
    public static List<String> getAllFilePathsFromAssets(Context context,final String path){
        List<String> filePaths = new ArrayList<>();
        String[] fileNames = null;
        try {
            fileNames = context.getAssets().list(path);
            if(fileNames!=null) {
                for (String fileName : fileNames) {
                    filePaths.add(fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePaths;
    }

    public static String getBannerFilePath(Context context) {
        File f = getSavePath(context,true);

        f = new File(f.getAbsolutePath()+CACHE_DIR_NAME+BANNER_DIR_NAME);
        if(!f.exists()){
            f.mkdirs();
        }

        return f.getAbsolutePath();
    }

}