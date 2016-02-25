package com.bondwithme.BondWithMe.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES10;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalImageLoader {

    private static final String TAG = LocalImageLoader.class.getSimpleName();
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    /**
     * max upload file size
     */
    private static final int maxSize = 150;

    private LocalImageLoader() {
    }

    public static Bitmap loadBitmapFromFile(Context context, byte[] data) {
        Options options = new Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        int width = UIUtil.getScreenWidth(context);
        int height = UIUtil.getScreenHeight(context);

        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        // 计算缩放比例
        int scaleX = imageWidth / width;
        int scaleY = imageHeight / height;
        int scale = 1;
        if (scaleX > scaleY & scaleY >= 1) {
            scale = scaleX;

        } else if (scaleY > scaleX & scaleX >= 1) {
            scale = scaleY;

        }

        // 颜色设置
        options.inPreferredConfig = Config.RGB_565;

        // 真的解析图片
        options.inJustDecodeBounds = false;
        // 设置采样率
        options.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bitmap;
    }

    public static Bitmap loadBitmapFromFile(Context context, Uri uri) {
        // 图片解析的配置
        Options options = new Options();
        // 不去真的解析图片，只是获取图片的头部信息宽，高
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            return null;
        }
        int width = UIUtil.getScreenWidth(context);
        int height = UIUtil.getScreenHeight(context);

        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        // 计算缩放比例
        int scaleX = imageWidth / width;
        int scaleY = imageHeight / height;
        int scale = 1;
        if (scaleX > scaleY & scaleY >= 1) {
            scale = scaleX;

        } else if (scaleY > scaleX & scaleX >= 1) {
            scale = scaleY;
        }

        // 颜色设置
        options.inPreferredConfig = Config.RGB_565;

        // 真的解析图片
        options.inJustDecodeBounds = false;
        // 设置采样率
        options.inSampleSize = scale;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            String path = null;
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
                int idx = c.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = c.getString(idx);
                c.close();
            }

            if (TextUtils.isEmpty(path)) {
                path = uri.getPath();
            }
            int d = readPictureDegree(path);
            if (d != 0) {
                bitmap = rotaingImageView(d, bitmap);
            }
        } catch (FileNotFoundException e) {
            return null;
        }
        return bitmap;
    }

    public static Bitmap loadBitmapFromFile(Context context, String pathName) {
        return loadBitmapFromFile(context, pathName, UIUtil.getScreenWidth(context), UIUtil.getScreenHeight(context));
    }

    public static Bitmap loadBitmapFromFile(Context context, String pathName, int width, int height) {
        Log.i(TAG, "loadBitmapFromFile& pathName: " + pathName);
        try {
            File file = new File(pathName);
            if (!file.exists()) {
                return null;
            }
            // decode image size
            Options bitmapTempOption = new Options();
            bitmapTempOption.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, bitmapTempOption);

            // Find the correct scale value. It should be the power of 2.
            //            final int REQUIRED_SIZE = 400;
            int width_tmp = bitmapTempOption.outWidth;
            int height_tmp = bitmapTempOption.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < width || height_tmp / 2 < height)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            Options bitmapOption = new Options();
            bitmapOption.inSampleSize = scale;

            try {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file), null, bitmapOption);
                //调整角度
                int degree = LocalImageLoader.readPictureDegree(pathName);
                if (degree != 0)
                    b = LocalImageLoader.rotaingImageView(degree, b);
                return b;
                //                return BitmapFactory.decodeStream(new FileInputStream(file), null, bitmapOption);
            } catch (OutOfMemoryError e) {
                //                DebugHelper.printError(e);
            } catch (Error e) {
                //                DebugHelper.printError(e);
            }
        } catch (Exception exception) {
            //            DebugHelper.printException(exception);
        }
        return null;
        //
        //		// 设置宽高
        //		Bitmap b = Bitmap.createScaledBitmap(bitmap2, width, height, true);
        //        //调整角度
        //        int degree = LocalImageLoader.readPictureDegree(pathName);
        //        if (degree != 0)
        //            b = LocalImageLoader.rotaingImageView(degree,b);
        //
        //		if (b!=null&&!b.equals(bitmap2)&&!bitmap2.isRecycled()) {
        //			bitmap2.recycle();
        //		}
        //
        //		return b;
    }

    /**
     * @Description 根据路径 获取Drawable
     * @date 2014-11-19 下午3:26:36
     * @param context
     * @param pathName
     *            路径
     * @param width
     *            需要的宽度
     * @param height
     *            需要的宽度
     * @return
     * @throws FileNotFoundException
     */
    //	public static Drawable loadDrawableFromUri(Context context, Uri uri,
    //			int width, int height) throws FileNotFoundException {
    //		Drawable drawable;
    //		// try {
    //		// InputStream inputStream =
    //		// context.getContentResolver().openInputStream(uri);
    //		// drawable = Drawable.createFromStream(inputStream, uri.toString() );
    //		// } catch (FileNotFoundException e) {
    //		// drawable =
    //		// context.getResources().getDrawable(R.drawable.default_image);
    //		// }
    //
    //		// 图片解析的配置
    //		BitmapFactory.Options options = new Options();
    //		// 不去真的解析图片，只是获取图片的头部信息宽，高
    //		options.inJustDecodeBounds = true;
    //
    //		InputStream inputStream = context.getContentResolver().openInputStream(
    //				uri);
    //
    //		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
    //
    //		int imageHeight = options.outHeight;
    //		int imageWidth = options.outWidth;
    //		// 计算缩放比例
    //		int scaleX = imageWidth / UIUtil.getScreenWidth(context);
    //		int scaleY = imageHeight / UIUtil.getScreenHeight(context);
    //		int scale = 1;
    //		if (scaleX > scaleY & scaleY >= 1) {
    //			scale = scaleX;
    //
    //		} else if (scaleY > scaleX & scaleX >= 1) {
    //			scale = scaleY;
    //
    //		}
    //
    //		// 颜色设置
    //		options.inPreferredConfig = Bitmap.Config.RGB_565;
    //
    //		// 真的解析图片
    //		options.inJustDecodeBounds = false;
    //		// 设置采样率
    //		options.inSampleSize = scale;
    //		bitmap = BitmapFactory.decodeStream(inputStream, null, options);
    //		// bitmap =
    //		// BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri),null,options);
    //		// 设置宽高
    //		bitmap = Bitmap.createScaledBitmap(bitmap,
    //				UIUtil.getScreenWidth(context),
    //				UIUtil.getScreenHeight(context), false);
    //		return new BitmapDrawable(context.getResources(), bitmap);
    //
    //		// return drawable;
    //	}

    /**
     * @param context
     * @param pathName 路径
     * @param width    需要的宽度
     * @param height   需要的宽度
     * @return
     * @Description 根据路径 获取Drawable
     * @date 2014-11-19 下午3:26:36
     */
    public static Drawable loadDrawableFromFile(Context context, String pathName, int width, int height) {
        return new BitmapDrawable(context.getResources(), loadBitmapFromFile(context, pathName, width, height));
    }

    /**
     * @param context
     * @param pathName 路径
     * @return
     * @Description 根据路径 获取Drawable，大小为屏幕大小
     * @date 2014-11-19 下午3:25:49
     */
    public static Drawable loadDrawableFromFile(Context context, String pathName) {
        return loadDrawableFromFile(context, pathName, UIUtil.getScreenWidth(context), UIUtil.getScreenHeight(context));
    }

    public static Bitmap loadBitmapFromResourceId(Context context, int resourceId, int reqWidth, int reqHeight) {
        // 图片解析的配置
        Options options = new Options();
        // 不去真的解析图片，只是获取图片的头部信息宽，高
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        int finalWidth;
        int finalHeight;
        if (reqWidth == 0) {
            finalWidth = UIUtil.getScreenWidth(context);
        } else {
            finalWidth = reqWidth;
        }
        if (reqHeight == 0) {
            finalHeight = UIUtil.getScreenHeight(context);
        } else {
            finalHeight = reqHeight;
        }
        // 计算缩放比例
        int scaleX = imageWidth / finalWidth;
        int scaleY = imageHeight / finalHeight;
        int scale = 1;
        if (scaleX > scaleY & scaleY >= 1) {
            scale = scaleX;

        } else if (scaleY > scaleX & scaleX >= 1) {
            scale = scaleY;

        }

        // 颜色设置
        options.inPreferredConfig = Config.RGB_565;

        // 真的解析图片
        options.inJustDecodeBounds = false;
        // 设置采样率
        options.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        // 设置宽高
        bitmap = Bitmap.createScaledBitmap(bitmap, UIUtil.getScreenWidth(context), UIUtil.getScreenHeight(context), false);
        return bitmap;
    }

    public static Drawable loadDrawableFromResourceId(Context context, int resourceId) {
        return new BitmapDrawable(context.getResources(), loadBitmapFromResourceId(context, resourceId, 0, 0));
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int offset, int length, int reqWidth, int reqHeight) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }


    private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * caculate the bitmap sampleSize
     *
     * @return
     */
    public final static int caculateInSampleSize(ContentResolver contentResolver,Uri bitmapUri) {
//    public final static int caculateInSampleSize(Resolver resolver,Options options, int rqsW, int rqsH) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (rqsW == 0 || rqsH == 0)
//            return 1;
//        if (height > rqsH || width > rqsW) {
//            final int heightRatio = Math.round((float) height / (float) rqsH);
//            final int widthRatio = Math.round((float) width / (float) rqsW);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        return inSampleSize;
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = contentResolver.openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeSilently(is);
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    private static int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }

    private static int getMaxTextureSize() {
        // The OpenGL texture size is the maximum size that can be drawn in an ImageView
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    /**
     * 压缩指定路径的图片，并得到图片对象
     *
     * @param path bitmap source path
     * @return Bitmap {@link Bitmap}
     */
    public final static Bitmap compressBitmap(Context context,String path, int rqsW, int rqsH) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(context.getContentResolver(),Uri.parse(path));
//        options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);

    }

    /**
     * 压缩指定Uri图片，并将其保存在缓存目录中，通过isDelSrc判定是否删除源文件，并获取到缓存后的图片路径
     *
     * @param context
     * @param uri
     * @param rqsW
     * @param rqsH
     * @param isDelSrc
     * @return
     */
    public final static String compressBitmap(Context context, Uri uri, int rqsW, int rqsH, boolean isDelSrc) {
        Log.i(TAG, "compressBitmap& uri: " + uri);
        Bitmap bitmap;
        ImageSize imageSize = new ImageSize(rqsW, rqsH);
        bitmap = ImageLoader.getInstance().loadImageSync(uri.toString(), imageSize);
        if (bitmap == null) {
            LogUtil.i(TAG, "compressBitmap& bitmap is null");
            return compressBitmap(context, FileUtil.getRealPathFromURI(context, uri), rqsW, rqsH, isDelSrc);
        } else {

            return getPath(context, FileUtil.getRealPathFromURI(context, uri), isDelSrc, bitmap);
        }
    }

    /**
     * 压缩指定路径图片，并将其保存在缓存目录中，通过isDelSrc判定是否删除源文件，并获取到缓存后的图片路径
     *
     * @param context
     * @param srcPath
     * @param rqsW
     * @param rqsH
     * @param isDelSrc
     * @return
     */
    public final static String compressBitmap(Context context, String srcPath, int rqsW, int rqsH, boolean isDelSrc) {

        Bitmap bitmap;
        bitmap = compressBitmap(context,srcPath, rqsW, rqsH);
        return getPath(context, srcPath, isDelSrc, bitmap);
    }

    /**
     * 获得缩小后的图片位置
     *
     * @param context
     * @param srcPath
     * @param isDelSrc
     * @param bitmap
     * @return
     */
    private static String getPath(Context context, String srcPath, boolean isDelSrc, Bitmap bitmap) {
        File srcFile = new File(srcPath);
        String desPath = PicturesCacheUtil.getCachePicPath(context,true);
        int degree = readPictureDegree(srcPath);
        try {
            if (degree != 0)
                bitmap = rotaingImageView(degree, bitmap);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // scale
            int options = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
            int interval = 10;
            while (os.toByteArray().length / 1024 > maxSize) {
                // Clean up os
                os.reset();
                // interval 10
                if (options <= 30 && interval == 10) {
                    interval = 5;
                }
                options -= interval;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
                if (options == 10) {
                    break;
                }
            }

            // Generate compressed image file
            FileOutputStream fos = new FileOutputStream(desPath);
            fos.write(os.toByteArray());
            fos.flush();
            fos.close();

            fos.close();
            if (isDelSrc)
                srcFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desPath;
    }

    /**
     * @param context
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap decodeSampledBitmapFromAssets(Context context, String path, int width, int height) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(path);
            byte data[] = new byte[is.available()];
            is.read(data);
            return decodeSampledBitmapFromByteArray(data, 0, data.length, width, height);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static byte[] readInputStream(InputStream in) throws Exception {
        // TODO Auto-generated method stub
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = in.read(b)) != -1) {
            baos.write(b, 0, len);
        }
        in.close();
        return baos.toByteArray();
    }

    /**
     * 从Assets目录中构建图片
     *
     * @param fileName
     * @return
     */
    public static Bitmap getSourceImageBitmapFromAssetsFile(Context mContext, String fileName) {
        Bitmap image = null;
        Resources resources = mContext.getResources();
        if (resources != null) {
            AssetManager am = resources.getAssets();
            try {
                InputStream is = am.open(fileName);
                image = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
            }
        }
        return image;
    }

    /**
     * @param bitmap
     * @param pixels
     * @return
     * @Description 获取圆形图片
     * @date 2014-11-27 下午5:59:18
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.i(TAG, "getRealPathFromURI& orientation ＝ " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "readPictureDegree& path: " + path + "; degree = " + degree);
        return degree;
    }

    public static byte[] bitmap2bytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
        return null;
    }


    /*
     * 旋转图片
     *
     * @param angle
     *
     * @param bitmap
     *
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * 通过图片UR获取本地图片的略缩图
     *
     * @param uri 图片URI
     * @return 返回所需要的图片
     */
    public static Bitmap getMiniThumbnailBitmap(Context context, Uri uri, int columnWidthHeight) {

        String path = null;

        Log.i(TAG, "getMiniThumbnailBitmap& path uri: " + uri);

        Cursor c = context.getContentResolver().query(uri, null, null, null, null);

        String miniThumbnailUri = null;
        if (c != null) {
            c.moveToFirst();
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), c.getLong(c.getColumnIndex(MediaStore.Images.Thumbnails._ID)), MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();//**EDIT**
                miniThumbnailUri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

                cursor.close();
            } else if (cursor != null) {
                cursor.close();
            }

            int idx = c.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = c.getString(idx);

            c.close();
        } else {
            path = uri.getPath();
        }

        // 略缩图
        Bitmap thumbnail = null;
        if (!TextUtils.isEmpty(miniThumbnailUri)) {
            //系统存有此图的略缩图
            Log.i(TAG, "getMiniThumbnailBitmap& miniThumbnailUri: " + miniThumbnailUri + " for uri: " + uri);

            thumbnail = LocalImageLoader.loadBitmapFromFile(context, Uri.parse(miniThumbnailUri).getPath());
        }

        if (thumbnail == null) {
            // 没有获取的缩略，从原图加载缩略图
            thumbnail = LocalImageLoader.loadBitmapFromFile(context, path, columnWidthHeight, columnWidthHeight);
        }
        return thumbnail;

        //        if(thumbnail == null) {
        //            Log.i(TAG, "getMiniThumbnailBitmap& thumbnail is null. path: " + path);
        //            return null;
        //        } else {
        //            Log.i(TAG, "getMiniThumbnailBitmap& thumbnail not null. path: " + path);
        //            // 转回角度
        //            int rotation = readPictureDegree(path);
        //            return LocalImageLoader.rotaingImageView(rotation, thumbnail);
        //        }


    }

    /**
     * 获取视频缩略图
     *
     * @param context
     * @param uri
     * @return base64 图片字节码
     */
    public static String getVideoThumbnail(Context context, Uri uri) {
        /**wing modified for pic too large begin*/

        if (uri == null)
            return null;

        String[] VIDEOTHUMBNAIL_TABLE = new String[]{
                MediaStore.Video.Media.DATA

        };

        Cursor c = MediaStore.Images.Thumbnails.queryMiniThumbnails(context.getContentResolver(), uri, MediaStore.Images.Thumbnails.MINI_KIND, VIDEOTHUMBNAIL_TABLE);
        String strThumbnail = null;
        try {
            String thumbnailPath = null;
            if ((c != null) && c.moveToFirst()) {
                thumbnailPath = c.getString(0);
            }

            if (TextUtils.isEmpty(thumbnailPath)) {
                Bitmap bitmap;
                ImageSize imageSize = new ImageSize(640, 480);
                bitmap = ImageLoader.getInstance().loadImageSync(uri.toString(), imageSize);
                if (bitmap == null) {
                    bitmap = compressBitmap(context,FileUtil.getRealPathFromURI(context, uri), 640, 480);
                }
                int degree = readPictureDegree(uri.toString());
                try {
                    if (degree != 0)
                        bitmap = rotaingImageView(degree, bitmap);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    // scale
                    int options = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
                    int interval = 10;
                    while (os.toByteArray().length / 1024 > maxSize) {
                        // Clean up os
                        os.reset();
                        // interval 10
                        if (options <= 30 && interval == 10) {
                            interval = 5;
                        }
                        options -= interval;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
                        if (options == 10) {
                            break;
                        }
                    }

                    strThumbnail = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
//        return thumbnailPath;
        /**wing modified for pic too large end*/

//        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(videoPath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        String strThumbnail = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return strThumbnail;
    }
}
