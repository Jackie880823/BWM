package com.madxstudio.co8.util;

import android.content.Context;

import com.madxstudio.co8.dao.LocalStickerInfoDao;
import com.madxstudio.co8.entity.LocalStickerInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 解压缩zip file, 慎用 ！！！
 * Created by heweidong on 15/6/24.
 */
public class ZipUtils {


    public static String PATH_DEFAULT_STICKER_1 = "Female";
    public static String PATH_DEFAULT_STICKER_2 = "Male";
    private static String PATH_DEFAULT_STICKER_3 = "MaleAndFemale";
//    private static String PATH_DEFAULT_STICKER_4 = "GranpaTurtle";
//    private static String PATH_DEFAULT_STICKER_5 = "GranmaGoose";
//    private static String PATH_DEFAULT_STICKER_6 = "Bunny";

    private static String NAME_DEFAULT_STICKER_1 = "Office Lady";
    private static String NAME_DEFAULT_STICKER_2 = "Office Man";
    private static String NAME_DEFAULT_STICKER_3 = "Man & Lady";
//    private static String NAME_DEFAULT_STICKER_4 = "Grandpa Turtle";
//    private static String NAME_DEFAULT_STICKER_5 = "Grandma Goose";
//    private static String NAME_DEFAULT_STICKER_6 = "Bunny";

    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static int unZipFile(File zipFile, String folderPath) throws IOException {
        //wing modified begin
        if (!zipFile.exists()) {
//            zipFile.mkdirs();
            return -1;
        }
        folderPath = folderPath + File.separator;
        //wing modified end
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            return ret;
        }
        return ret;
    }

    public static void unZip(Context context, String assetName, String outputDirectory) throws IOException {
        //创建解压目标目录
        File file = new File(outputDirectory);
        //如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        //使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        //解压时字节计数
        int count = 0;
        //如果进入点为空说明已经遍历完所有压缩包中文件和目录
        String zipFileName = zipEntry.getName();
        while (zipEntry != null) {
            //如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                file.mkdir();
            } else {
                //如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                //创建该文件
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((count = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
            }
            //定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        LocalStickerInfo stickerInfo = new LocalStickerInfo();
        if (zipFileName.contains(File.separator)) {
            zipFileName = zipFileName.substring(0, zipFileName.indexOf(File.separator));
        }

        if (PATH_DEFAULT_STICKER_1.equals(zipFileName)) {
            stickerInfo.setName(NAME_DEFAULT_STICKER_1);
        } else if (PATH_DEFAULT_STICKER_2.equals(zipFileName)) {
            stickerInfo.setName(NAME_DEFAULT_STICKER_2);
        } else if (PATH_DEFAULT_STICKER_3.equals(zipFileName)) {
            stickerInfo.setName(NAME_DEFAULT_STICKER_3);
        }
//        else if (PATH_DEFAULT_STICKER_4.equals(zipFileName)) {
//            stickerInfo.setName(NAME_DEFAULT_STICKER_4);
//        } else if (PATH_DEFAULT_STICKER_5.equals(zipFileName)) {
//            stickerInfo.setName(NAME_DEFAULT_STICKER_5);
//        } else if (PATH_DEFAULT_STICKER_6.equals(zipFileName)) {
//            stickerInfo.setName(NAME_DEFAULT_STICKER_6);
//        }

        stickerInfo.setPath(zipFileName);
        stickerInfo.setSticker_name("1");
        stickerInfo.setVersion("1");

        stickerInfo.setType(".png");
        stickerInfo.setOrder(System.currentTimeMillis());
        stickerInfo.setDefaultSticker(LocalStickerInfo.DEFAULT_STICKER);
//        if (zipFileName.contains("Barry")) {
//            stickerInfo.setType(".png");
//            stickerInfo.setOrder(System.currentTimeMillis());
//        } else if (zipFileName.contains("Bunny")) {
//            stickerInfo.setOrder(System.currentTimeMillis());
//            stickerInfo.setType(".png");
//        } else if (zipFileName.contains("Merdeka")) {
//            stickerInfo.setOrder(System.currentTimeMillis());
//            stickerInfo.setType(".png");
//        }
        LocalStickerInfoDao.getInstance(context).addOrUpdate(stickerInfo);

    }
}
