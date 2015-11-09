package com.bondwithme.BondWithMe.util;

/**
 * Created by wing on 15/3/22.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.ui.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static File path;
    private final static String CACHE_DIR_NAME = File.separator + "cache";
    public final static String BANNER_DIR_NAME = File.separator + "banner";
    private static File appPath;

    /**
     * 获取保存文件位置根路径
     *
     * @param context
     * @param isOutPath 网络相关的请不要使用true!!!!!,其它可考虑是否保存在app外(沙盒)
     * @return File()
     */
    public static File getSaveRootPath(Context context, boolean isOutPath) {

        if (isOutPath) {
            if (path == null) {
                if (hasSDCard()) { // SD card
                    path = new File(getSDCardPath() + File.separator + "BondWithMe");
                    path.mkdir();
                } else {
                    path = Environment.getDataDirectory();
                }
            }
            return path;
        } else {
            if (appPath == null) {
                appPath = context.getFilesDir();
            }
            return appPath;
        }
    }

    public static String getSaveCrashPath(Context context) {
        String date = MyDateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        return getCrashRootPath(context) + File.separator + date + ".log";
    }

    /**
     * 获取Crash文件保存位置根路径
     *
     * @param context
     * @return
     */
    private static File getCrashRootPath(Context context) {
        File file = new File(getSaveRootPath(context, true) + File.separator + "crash");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File[] getCrashFiles(Context context) {
        return getCrashRootPath(context).listFiles();
    }

    public static void clearCrashFiles(Context context) {
        File[] cacheFiles = getCrashFiles(context);
        if (cacheFiles != null) {
            for (File file : cacheFiles) {
                file.delete();
            }
        }
    }

    /**
     * 获取全局缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getCacheFilePath(Context context) {
        File f = getSaveRootPath(context, true);

        f = new File(f.getAbsolutePath() + CACHE_DIR_NAME);
        if (!f.exists()) {
            f.mkdirs();
        }

        return f.getAbsolutePath();
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
     * 判断是否有sd卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取sd卡路径
     *
     * @return
     */
    private static String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }

    /**
     * 获取uri的真实url
     *
     * @param context
     * @param contentURI
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        String[] proj = {MediaStore.Images.ImageColumns.DATA};
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
     *
     * @param context
     */
    public static void clearCache(Context context) {
        /**TODO 暂时取消*/
//        File fileRoot = new File(getCacheFilePath(context));
//        if (fileRoot != null) {
//            File[] cacheFiles = fileRoot.listFiles();
//            for (File file : cacheFiles) {
//                file.delete();
//            }
//        }
    }

    /**
     * get all files path of assets by parent path
     *
     * @param context
     * @param path    parent path
     * @return
     */
    public static List<String> getAllFilePathsFromAssets(Context context, final String path) {
        List<String> filePaths = new ArrayList<>();
        String[] fileNames = null;
        try {
            fileNames = context.getAssets().list(path);
            if (fileNames != null) {
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
        File f = getSaveRootPath(context, false);

        f = new File(f.getAbsolutePath() + CACHE_DIR_NAME + BANNER_DIR_NAME);
        if (!f.exists()) {
            f.mkdirs();
        }

        return f.getAbsolutePath();
    }

    public static void deleteBanner(Context context) {
        File fileRoot = new File(getBannerFilePath(context));
        LogUtil.d("FileUtil", "===fileRoot===" + fileRoot.getAbsolutePath());
        if (fileRoot != null) {
            File[] cacheFiles = fileRoot.listFiles();
            for (File file : cacheFiles) {
                file.delete();
            }
        }
        boolean deleted = fileRoot.delete();
        LogUtil.d("FileUtil", "===fileRoot===" + fileRoot.getAbsolutePath() + "===" + fileRoot.exists() + "====" + deleted);
    }

    private static final String RECORD = "Audio";
    private static final String VIDEO = "Video";
    private static final String PDF = "pdf";

    public static File saveAudioFile(Context mContext) {
        return new File(getAudioRootPath(mContext) + File.separator + System.currentTimeMillis() + ".aac");
    }

    public static String getAudioRootPath(Context mContext) {
        File bootFile = getSaveRootPath(mContext, false);
        String filePath = bootFile.getAbsolutePath();
        filePath = filePath + File.separator + App.getLoginedUser().getUser_id() + File.separator + RECORD;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static File saveVideoFile(Context mContext) {
        return new File(getVideoRootPath(mContext) + File.separator + System.currentTimeMillis() + ".mp4");
    }

    public static String getPDFSavePath(Context mContext) {
        return getPDFRootPath(mContext) + String.format("/cache_%s.pdf", "" + System.currentTimeMillis());
    }

    public static String getPDFRootPath(Context mContext) {
        File bootFile = getSaveRootPath(mContext, true);
        String filePath = bootFile.getAbsolutePath();
        filePath = filePath + File.separator + App.getLoginedUser().getUser_id() + File.separator + PDF;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static String getVideoRootPath(Context mContext) {
        File bootFile = getSaveRootPath(mContext, false);
        String filePath = bootFile.getAbsolutePath();
        filePath = filePath + File.separator + App.getLoginedUser().getUser_id() + File.separator + VIDEO;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static String getBigStickerPath(Context mContext, String stickerPath, String stickerName, String stickerType) {
        String filePath = FileUtil.getSaveRootPath(mContext, false).getAbsolutePath() + File.separator + "Sticker";
        File file = new File(filePath);
        if (file.exists()) {
            return filePath + File.separator + stickerPath + File.separator + stickerName + "_B" + stickerType;
        }
        return MainActivity.STICKERS_NAME + File.separator + stickerPath + File.separator + "B" + File.separator + stickerName + stickerType;
    }

    /**
     *
     * @param mContext
     * @param stickerPath 表情包名
     * @param stickerName
     * @param stickerType
     * @return
     */
    public static String getSmallStickerPath(Context mContext, String stickerPath, String stickerName, String stickerType) {
        String filePath = FileUtil.getSaveRootPath(mContext, false).getAbsolutePath() + File.separator + "Sticker";
        File file = new File(filePath);
        if (file.exists()) {
            return filePath + File.separator + stickerPath + File.separator + stickerName + "_S" + stickerType;
        }
        return MainActivity.STICKERS_NAME + File.separator + stickerPath + File.separator + "S" + File.separator + stickerName + stickerType;
    }

    public static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

}