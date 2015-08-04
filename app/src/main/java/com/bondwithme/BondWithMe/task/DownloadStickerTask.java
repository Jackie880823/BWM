package com.bondwithme.BondWithMe.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.material.widget.CircularProgress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by quankun on 15/8/4.
 */
public class DownloadStickerTask {

    public static DownloadStickerTask getInstance() {
        return new DownloadStickerTask();
    }

    public void downloadGifSticker(final CircularProgress progressBar, String stickerGroupPath, String stickerName,
                                   final int defaultResource, final GifImageView gifImageView) {
        final String urlPath = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
                stickerName, stickerGroupPath, Constant.Sticker_Gif);
        final String downloadPath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath +
                File.separator + stickerName + "_B.gif";
        AsyncTask task = new AsyncTask<Object, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Object... params) {
                return getImageByte(urlPath, downloadPath);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (null != progressBar) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onPostExecute(byte[] resultByte) {
                super.onPostExecute(resultByte);
                if (null != progressBar) {
                    progressBar.setVisibility(View.GONE);
                }
                try {
                    if (null != resultByte) {
                        GifDrawable gifDrawable = new GifDrawable(resultByte);
                        if (gifDrawable != null && gifImageView != null) {
                            gifImageView.setImageDrawable(gifDrawable);
                        } else {
                            if (defaultResource > 0) {
                                gifImageView.setImageResource(defaultResource);
                            }
                        }
                    } else {
                        if (defaultResource > 0) {
                            gifImageView.setImageResource(defaultResource);
                        }
                    }
                } catch (Exception e) {
                    if (defaultResource > 0) {
                        gifImageView.setImageResource(defaultResource);
                    }
                    e.printStackTrace();
                }
            }

        };
        if (SDKUtil.IS_HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    public void downloadPngSticker(final CircularProgress progressBar, String stickerGroupPath, String stickerName,
                                   final int defaultResource, final ImageView imageView) {
        final String urlPath = String.format(Constant.API_STICKER, MainActivity.getUser().getUser_id(),
                stickerName, stickerGroupPath, Constant.Sticker_Png);
        final String downloadPath = MainActivity.STICKERS_NAME + File.separator + stickerGroupPath + File.separator
                + stickerName + "_B.png";
        AsyncTask task = new AsyncTask<Object, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Object... params) {
                return getImageByte(urlPath, downloadPath);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (null != progressBar) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onPostExecute(byte[] resultByte) {
                super.onPostExecute(resultByte);
                if (null != progressBar) {
                    progressBar.setVisibility(View.GONE);
                }
                try {
                    if (null != resultByte) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(resultByte, 0, resultByte.length);
                        if (bitmap != null && imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            if (defaultResource > 0) {
                                imageView.setImageResource(defaultResource);
                            }
                        }
                    } else {
                        if (defaultResource > 0) {
                            imageView.setImageResource(defaultResource);
                        }
                    }
                } catch (Exception e) {
                    if (defaultResource > 0) {
                        imageView.setImageResource(defaultResource);
                    }
                    e.printStackTrace();
                }
            }

        };
        if (SDKUtil.IS_HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    /**
     * 将输入流转为byte数组
     *
     * @param in
     * @return
     * @throws Exception
     */
    private static byte[] readInputStream(InputStream in) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.close();
        in.close();
        return baos.toByteArray();
    }

    /**
     * 获取图片的byte数组
     *
     * @param urlPath
     * @return
     */
    private byte[] getImageByte(String urlPath, String filePath) {
        InputStream in = null;
        byte[] result = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection httpURLconnection = (HttpURLConnection) url.openConnection();
            httpURLconnection.setDoInput(true);
            httpURLconnection.connect();
            if (httpURLconnection.getResponseCode() == 200) {
                in = httpURLconnection.getInputStream();
                result = readInputStream(in);
                in.close();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (null != result && result.length > 0) {
            writeFile(filePath, result);
        }
        return result;
    }

    public void writeFile(String fileName, byte[] bytes) {
        try {
            if (fileName.indexOf(File.separator) >= 0) {
                String path = fileName.substring(0, fileName.lastIndexOf(File.separator));
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            FileOutputStream fos = new FileOutputStream(new File(fileName), true);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
