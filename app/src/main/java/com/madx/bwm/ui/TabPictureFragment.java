package com.madx.bwm.ui;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.madx.bwm.R;
import com.madx.bwm.adapter.PickPicAdapter;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.SDKUtil;
import com.madx.bwm.widget.CustomGridView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.TabPictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.TabPictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabPictureFragment extends BaseFragment<MainActivity> implements View.OnClickListener {


    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

    /**
     * 头像缓存文件名称
     */
//    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp_";
    public int cache_count = 0;

    Uri mCropImagedUri;
    private String imagePath;

    public static TabPictureFragment newInstance(String... params) {

        return createInstance(new TabPictureFragment());
    }

    public TabPictureFragment() {
        super();
    }


    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_tab_picture;
    }

    private CustomGridView gvPictures;
    private PickPicAdapter adapter;
    private LinkedList<Map<String, Bitmap>> datas = new LinkedList<>();

    @Override
    public void initView() {

        getViewById(R.id.ll_to_photo).setOnClickListener(this);
        getViewById(R.id.ll_to_camera).setOnClickListener(this);

        gvPictures = getViewById(R.id.gv_pictures);
        adapter = new PickPicAdapter(getActivity(), datas, R.layout.picture_item_for_gridview
                , new String[]{"pic_resId",}
                , new int[]{R.id.iv_pic}
        );

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {

                if ((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ImageView imageView = (ImageView) view;
                    Bitmap bmp = (Bitmap) data;
                    imageView.setImageBitmap(bmp);
                    return true;
                }
                return false;
            }
        });

        adapter.setSelectImageListener(new PickPicAdapter.SelectImageListener() {
            @Override
            public void onImageDelete(int index) {
                if(uries!=null){
                    try {
                        uries.remove(index);
                    }catch (Exception e){}
                }
            }
        });

        gvPictures.setAdapter(adapter);

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_to_photo:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
                break;
            case R.id.ll_to_camera:

                cache_count++;
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra("android.intent.extras.CAMERA_FACING", getCameraId(false));
//                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                intent2.putExtra("camerasensortype", 2);//using the front camera
                intent2.putExtra("autofocus", true);

                // 下面这句指定调用相机拍照后的照片存储的路径
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(),
                                CACHE_PIC_NAME_TEMP + cache_count)));
                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent2.putExtra("return-data", true);
                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
                break;
        }
    }

    public static int getCameraId(boolean front) {
        int num = Camera.getNumberOfCameras();
        for (int i = 0; i < num; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && front) {
                return i;
            }
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK && !front) {
                return i;
            }
        }
        return -1;
    }


    private void addDataAndNofify(Bitmap uri) {
        Map<String, Bitmap> map = new HashMap<>();
        map.put("pic_resId", uri);
        datas.add(map);
        adapter.notifyDataSetChanged();
    }

    private void addDataAndNofify(List<Bitmap> uries) {
        if (uries != null) {
            for (int i = 0; i < uries.size(); i++) {
                Map<String, Bitmap> map = new HashMap<>();
                map.put("pic_resId", uries.get(i));
                datas.add(map);

            }
            adapter.notifyDataSetChanged();
        }
    }

    List<Uri> uries = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getActivity().RESULT_OK == resultCode) {

            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    if (data != null) {
                        List<Uri> pickUries = new ArrayList();
                        if (SDKUtil.IS_JB) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                int size = clipData.getItemCount();
                                for (int i = 0; i < size; i++) {
                                    Uri uri = clipData.getItemAt(i).getUri();
                                    pickUries.add(uri);
                                }
                            } else {
                                pickUries.add(data.getData());
                            }
                        } else {
                            pickUries.add(data.getData());
                        }
                        addDataAndNofify(getMiniThumbanilUri(pickUries));
                        uries.addAll(pickUries);
                    }
                    break;
                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
//                    if (data != null) {
                        Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count));
                        if (new File(uri.getPath()).exists()) {
                            addDataAndNofify(getMiniThumbanilBitmap(uri));
                            uries.add(uri);
                        }
//                    }

                    break;
                // 取得裁剪后的图片
                case REQUEST_HEAD_FINAL:
                    if (data != null) {
                        Bitmap photo;
                        try {
                            imagePath = mCropImagedUri.getPath();
                            if (!TextUtils.isEmpty(imagePath)) {
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     * @throws IOException
     */
//    public void startPhotoZoom(Uri uri, boolean fromPhoto) throws IOException {
//
//        if (uri == null || uri.getPath() == null) {
//            return;
//        }
//
//        //TODO换为view 的宽度
//        int width = 100;
//        int height = 100;
////        int width = image_head.getWidth();
////        int height = image_head.getHeight();
//
//        /**
//         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
//         */
//        int degree = LocalImageLoader.readPictureDegree(uri.getPath());
//
//        if (degree != 0) {
//            /**
//             * 把图片旋转为正的方向
//             */
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            Bitmap bitmap = BitmapFactory.decodeStream(
//                    new FileInputStream(uri.getPath()), null, options);
//            options.inSampleSize = 4;
//            options.outWidth = width;
//            options.outHeight = height;
//            options.inJustDecodeBounds = false;
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            bitmap = BitmapFactory.decodeStream(
//                    new FileInputStream(uri.getPath()), null, options);
//            bitmap = LocalImageLoader.rotaingImageView(degree, bitmap);
//            byte[] newBytes = LocalImageLoader.bitmap2bytes(bitmap);
//            File file = new File(uri.getPath());
//            file.delete();
//            FileOutputStream fos = new FileOutputStream(uri.getPath());
//            fos.write(newBytes);
//            fos.flush();
//            fos.close();
//            bitmap.recycle();
//            bitmap = null;
//        }
//
//
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(getActivity(), "未找到裁剪程序！", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
//            intent.putExtra("crop", "true");
//            // aspectX aspectY 是宽高的比例
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            // outputX outputY 是裁剪图片宽高
//            intent.putExtra("outputX", width);
//            intent.putExtra("outputY", height);
//
//            // //防止毛边
//            // intent.putExtra("scale", true);//黑边
//            // intent.putExtra("scaleUpIfNeeded", true);//黑边
//
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//            intent.putExtra("noFaceDetection", true);
//
//            //		if(fromPhoto){
//            File f = MessageUtil.PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME);
//            mCropImagedUri = Uri.fromFile(f);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
//            //		}else{
//            //			mCropImagedUri = uri;
//            //		}
//            startActivityForResult(intent, REQUEST_HEAD_FINAL);
//        }
//    }

    /** pick到的图片byte[] */
//	byte[] personHeadImage;

    /**
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private void setPicToView(Bitmap photo) {
        if (photo != null) {
//            try{
//                image_head.setImageBitmap(photo);
//                updateUserHead(true);
//            }catch(Exception e){
//                Toast.makeText(this, "加载图片异常", Toast.LENGTH_LONG).show();
//            }
        }
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }

    private Bitmap getMiniThumbanilBitmap(Uri uri) {
//        Bitmap bitmap;
//        try {
//            return getThumbnail(getActivity().getContentResolver(),uri.getPath());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;

        Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);

        String miniThumbanilUri = null;
        if (c != null) {
            c.moveToFirst();
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                    getActivity().getContentResolver(), c.getLong(c.getColumnIndex(MediaStore.Images.Thumbnails._ID)),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();//**EDIT**
                miniThumbanilUri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

                cursor.close();
            }

            c.close();
        }

        Log.i("","base Degree=========="+LocalImageLoader.readPictureDegree(uri.getPath()));

        if (TextUtils.isEmpty(miniThumbanilUri)) {
            if (columnWidthHeight == 0) {
                columnWidthHeight = gvPictures.getColumnWidth();
            }
//            if(SDKUtil.IS_L){
//                return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(getActivity(), uri)),columnWidthHeight,columnWidthHeight);
//            }else {
                return LocalImageLoader.rotaingImageView(LocalImageLoader.readPictureDegree(uri.getPath()), ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FileUtil.getRealPathFromURI(getActivity(), uri)), columnWidthHeight, columnWidthHeight));
//            }
        } else {
            return LocalImageLoader.loadBitmapFromFile(getActivity(), Uri.parse(miniThumbanilUri).getPath());

        }
    }

    private int columnWidthHeight;

    private List<Bitmap> getMiniThumbanilUri(List<Uri> uries) {

        List<Bitmap> miniThumbanilUries = new ArrayList<>();
        if (uries != null) {
            for (int i = 0; i < uries.size(); i++) {
                miniThumbanilUries.add(getMiniThumbanilBitmap(uries.get(i)));
            }
        }
        return miniThumbanilUries;
    }


    public List<Uri> getEditPic4Content() {
        return uries;
    }


    @Override
    public void onDestroy() {
        FileUtil.clearCache(getActivity());
        super.onDestroy();
    }
}
