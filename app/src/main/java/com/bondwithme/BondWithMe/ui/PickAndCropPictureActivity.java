package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

public class PickAndCropPictureActivity extends Activity {

    /**
     * 图片来源指定参数
     */
    public final static String FLAG_PIC_FROM = "pic_from";
    /**
     * 想要获取的宽
     */
    public final static String FLAG_PIC_FINAL_WIDTH = "pic_final_width";
    /**
     * 想要获取的高
     */
    public final static String FLAG_PIC_FINAL_HEIGHT = "pic_final_height";
    /**
     * 最终结果返回参数
     */
    public final static String FINAL_PIC_URI = "final_pic_uri";
    /**
     * 是否需要裁剪
     */
    public final static String FLAG_CROP = "flag_crop";
    public final static int REQUEST_FROM_PHOTO = 1;
    public final static int REQUEST_FROM_CAMERA = 2;
    private final static int REQUEST_PIC_FINAL = 3;
    private int picFrom;
    private int picFinalWidth;
    private boolean needCrop;
    private int picFinalHeight;
    private Uri mCropImagedUri;
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    private String headCache = CACHE_PIC_NAME;
    /**
     * 临时文件用户裁剪
     */
    public String CACHE_PIC_NAME_TEMP;

    /**
     * 图片宽比例
     */
    public final static String FLAG_PIC_ASPECT_WIDTH = "pic_aspect_width";
    /**
     * 图片高比例
     */
    public final static String FLAG_PIC_ASPECT_HEIGHT = "pic_aspect_height";
    private int picAspectWidth;
    private int picAspectHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picFrom = getIntent().getIntExtra(FLAG_PIC_FROM, REQUEST_FROM_PHOTO);
        picFinalWidth = getIntent().getIntExtra(FLAG_PIC_FINAL_WIDTH, 100);
        picFinalHeight = getIntent().getIntExtra(FLAG_PIC_FINAL_HEIGHT, 100);
        needCrop = getIntent().getBooleanExtra(FLAG_CROP, true);
        picAspectWidth = getIntent().getIntExtra(FLAG_PIC_ASPECT_WIDTH, 1);
        picAspectHeight = getIntent().getIntExtra(FLAG_PIC_ASPECT_HEIGHT, 1);
        headCache = headCache + getIntent().getStringExtra(CACHE_PIC_NAME);
        LogUtil.i("photoWidth*photoHeight_2", picFinalWidth + "*" + picFinalHeight);
        doAction();
    }

    private void doAction() {
        Intent intent;
        switch (picFrom) {
            case REQUEST_FROM_PHOTO:
                //打开选择照片
                intent = new Intent(this, SelectPhotosActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_FROM_PHOTO);
                break;
            case REQUEST_FROM_CAMERA:
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent2.putExtra("camerasensortype", 2);


                CACHE_PIC_NAME_TEMP = System.currentTimeMillis() + "_head_cache_temp.png";
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(this, CACHE_PIC_NAME_TEMP, true)));
                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent2.putExtra("return-data", false);
                startActivityForResult(intent2, REQUEST_FROM_CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_FROM_PHOTO:
                    LogUtil.i("Pick_onActivityResult", "REQUEST_FROM_PHOTO");
                    if (data != null) {
                        Uri uri;
                        uri = data.getData();
                        if (needCrop) {
                            try {
                                startPhotoZoom(uri, true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mCropImagedUri = uri;
                            done();
                        }

                    }
                    break;

                // 如果是调用相机拍照时
                case REQUEST_FROM_CAMERA:
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(this, CACHE_PIC_NAME_TEMP, true));
                    if (needCrop) {
                        try {
                            startPhotoZoom(uri, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mCropImagedUri = uri;
                        done();
                    }
                    break;
                // 取得裁剪后的图片
                case REQUEST_PIC_FINAL:
                    done();
                    break;

                default:
                    finish();
                    break;

            }
        } else {
            finish();
        }

    }

    private void done() {
        Intent intent = new Intent();
        intent.putExtra(FINAL_PIC_URI, mCropImagedUri);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     * @throws java.io.IOException
     */
    public void startPhotoZoom(Uri uri, boolean fromPhoto) throws IOException {

//        String path = FileUtil.getRealPathFromURI(this, uri);
//        if (uri == null || path == null) {
//            return;
//        }
//
//        /**
//         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
//         */
//        int degree = LocalImageLoader.readPictureDegree(path);
//
//        if (fromPhoto && degree != 0) {
//            InputStream is = null;
//            try {
//                int sampleSize = LocalImageLoader.caculateInSampleSize(getContentResolver(), uri);
//                is = getContentResolver().openInputStream(uri);
//                BitmapFactory.Options option = new BitmapFactory.Options();
//                option.inSampleSize = sampleSize;
//                RotateBitmap rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), degree);
////                PicturesCacheUtil.saveToFile(path,rotateBitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (OutOfMemoryError e) {
//                e.printStackTrace();
//            } finally {
//                FileUtil.closeSilently(is);
//            }
//
////            Bitmap bitmap = LocalImageLoader.loadBitmapFromFile(this, uri);
////            //            /**
////            //             * 把图片旋转为正的方向
////            //             */
////            bitmap = LocalImageLoader.rotaingImageView(degree, bitmap);
////            PicturesCacheUtil.saveToFile(path, bitmap);
//        }
//
//        Intent intent = new Intent("com.android.camera.action.CROP");
//
//        intent.setDataAndType(uri, "image/*");
//        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(this, getResources().getString(R.string.text_no_found_reduce), Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
//            intent.putExtra("crop", "true");
//            // aspectX aspectY 是宽高的比例
//            intent.putExtra("aspectX", picAspectWidth);
//            intent.putExtra("aspectY", picAspectHeight);
//            // outputX outputY 是裁剪图片宽高
//            intent.putExtra("outputX", picFinalWidth);
//            intent.putExtra("outputY", picFinalHeight);
//            // //防止毛边
//            // intent.putExtra("scale", true);//黑边
//            // intent.putExtra("scaleUpIfNeeded", true);//黑边
//            intent.putExtra("return-data", false);
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//            intent.putExtra("noFaceDetection", true);
//            File f = PicturesCacheUtil.getCachePicFileByName(this, CACHE_PIC_NAME + System.currentTimeMillis() + ".png", true);
//            mCropImagedUri = Uri.fromFile(f);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
//            startActivityForResult(intent, REQUEST_PIC_FINAL);
//        }

        /**使用第三方*/
            if (uri == null || uri.getPath() == null) {
                return;
            }
//            String path = LocalImageLoader.compressBitmap(this, uri, 400, 480, false);
//            Uri source = Uri.fromFile(new File(path));
            File f = PicturesCacheUtil.getCachePicFileByName(this, headCache,true);
            mCropImagedUri = Uri.fromFile(f);
//            Crop.of(uri, mCropImagedUri).asSquare().start(this,REQUEST_PIC_FINAL);
            Crop.of(uri, mCropImagedUri).withAspect(picAspectWidth, picAspectHeight).start(this, REQUEST_PIC_FINAL);
    }
}
