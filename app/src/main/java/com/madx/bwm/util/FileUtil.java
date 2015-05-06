package com.madx.bwm.util;

/**
 * Created by wing on 15/3/22.
 */
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static File path;
    private final static String CACHE_DIR_NAME = "/cache";

    protected static File getSavePath(Context context) {
        if(path==null) {
            if (hasSDCard()) { // SD card
                path = new File(getSDCardPath() + "/" + AppInfoUtil.getApplicationName(context));
                path.mkdir();
            } else {
                path = Environment.getDataDirectory();
            }
        }
        return path;
    }

    public static String getCacheFilePath(Context context) {
        File f = getSavePath(context);

        f = new File(f.getAbsolutePath()+CACHE_DIR_NAME);
        if(!f.exists()){
            f.mkdirs();
        }

        return f.getAbsolutePath();
    }

    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) { return null; }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveToFile(String filename,Bitmap bmp) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            bmp.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch(Exception e) {}
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
    public static String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }

    public static String getRealPathFromURI(Context context,Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
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
        File fileRoot = getSavePath(context);
        File[] cacheFiles = fileRoot.listFiles();
        for(File file : cacheFiles){
            file.delete();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaths;
    }

}