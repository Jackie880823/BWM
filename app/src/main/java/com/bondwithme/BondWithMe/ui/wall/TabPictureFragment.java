package com.bondwithme.BondWithMe.ui.wall;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.PickPicAdapter;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.CustomGridView;

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
 * {@link TabPictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabPictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabPictureFragment extends BaseFragment<WallNewActivity> implements View.OnClickListener {

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp_";

    public final static String RESIDUE = "residue";
    /**
     * 限制最多可选图片张数
     */
    public final static int MAX_SELECT = 10;
    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private final static String TAG = TabPictureFragment.class.getSimpleName();
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;

    /**
     * 头像缓存文件名称
     */
    //    public final static String CACHE_PIC_NAME = "head_cache";
    private final static int REQUEST_HEAD_FINAL = 3;
    public int cache_count = 0;

    Uri mCropImagedUri;
    List<Uri> uris = new ArrayList<>();
    private String imagePath;
    private CustomGridView gvPictures;
    private PickPicAdapter adapter;
    private LinkedList<Map<String, Bitmap>> datas = new LinkedList<>();
    private int columnWidthHeight;

    public TabPictureFragment() {
        super();
    }

    public static TabPictureFragment newInstance(String... params) {

        return createInstance(new TabPictureFragment());
    }

    public static int getCameraId(boolean front) {
        int num = Camera.getNumberOfCameras();
        for(int i = 0; i < num; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && front) {
                return i;
            }
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK && !front) {
                return i;
            }
        }
        return -1;
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if(ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_tab_picture;
    }

    @Override
    public void initView() {

        getViewById(R.id.ll_to_photo).setOnClickListener(this);
        getViewById(R.id.ll_to_camera).setOnClickListener(this);

        gvPictures = getViewById(R.id.gv_pictures);

        adapter = new PickPicAdapter(getActivity(), datas, R.layout.picture_item_for_gridview, new String[]{"pic_resId",}, new int[]{R.id.iv_pic});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {

                if((view instanceof ImageView) && (data instanceof Bitmap)) {
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
                if(uris != null) {
                    try {
                        uris.remove(index);
                    } catch(Exception e) {
                    }
                }
            }
        });

        gvPictures.setAdapter(adapter);
        if(uris != null && uris.size() > 0) {
            addDataAndNotify(getMiniThumbnailUri(uris));
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View v) {
        int residue = MAX_SELECT - datas.size();
        if(residue <= 0) {
            MessageUtil.showMessage(getActivity(), String.format(getActivity().getString(R.string.select_too_many), MAX_SELECT));
            return;
        }
        switch(v.getId()) {
            case R.id.ll_to_photo:
                Intent intent = new Intent(getParentActivity(), SelectPhotosActivity.class);
                //                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.putExtra(RESIDUE, residue);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
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
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count)));
                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent2.putExtra("return-data", true);
                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
                break;
        }
    }

    private void addDataAndNotify(Bitmap uri) {
        Map<String, Bitmap> map = new HashMap<>();
        map.put("pic_resId", uri);
        datas.add(map);
        adapter.notifyDataSetChanged();
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

    /**
     * pick到的图片byte[]
     */
    //	byte[] personHeadImage;
    private void addDataAndNotify(List<Bitmap> uris) {
        if(uris != null && uris.size() > 0) {
            int count = uris.size();
            for(int i = 0; i < count; i++) {
                Map<String, Bitmap> map = new HashMap<>();
                map.put("pic_resId", uris.get(i));
                datas.add(map);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        // 没有退出编辑不用保存蓝草稿
        getParentActivity().getSharedPreferences(WallNewFragment.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(WallNewFragment.PREFERENCE_KEY_IS_SAVE, false).commit();

        if(getActivity().RESULT_OK == resultCode) {

            switch(requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    if(data != null) {
                        ArrayList pickUris;
                        pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.IMAGES_STR);
                        addDataAndNotify(getMiniThumbnailUri(pickUris));
                        uris.addAll(pickUris);
                    }
                    break;
                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
                    int residue = MAX_SELECT - datas.size();
                    if(residue <= 0) {
                        MessageUtil.showMessage(getActivity(), String.format(getActivity().getString(R.string.select_too_many), MAX_SELECT));
                        return;
                    }
                    //                    if (data != null) {
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count));
                    if(new File(uri.getPath()).exists()) {
                        if(columnWidthHeight == 0) {
                            columnWidthHeight = gvPictures.getColumnWidth();
                        }
                        addDataAndNotify(LocalImageLoader.getMiniThumbnailBitmap(getActivity(), uri, columnWidthHeight));
                        uris.add(uri);
                    }
                    //                    }

                    break;
                // 取得裁剪后的图片
                case REQUEST_HEAD_FINAL:
                    if(data != null) {
                        Bitmap photo;
                        try {
                            imagePath = mCropImagedUri.getPath();
                            if(!TextUtils.isEmpty(imagePath)) {
                                //								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if(photo != null) {
                                    setPicToView(photo);
                                }
                            }
                        } catch(FileNotFoundException e) {
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
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private void setPicToView(Bitmap photo) {
        if(photo != null) {
            //            try{
            //                image_head.setImageBitmap(photo);
            //                updateUserHead(true);
            //            }catch(Exception e){
            //                Toast.makeText(this, "加载图片异常", Toast.LENGTH_LONG).show();
            //            }
        }
    }

    public int getColumnWidthHeight() {
        return columnWidthHeight;
    }

    public void setColumnWidthHeight(int columnWidthHeight) {
        this.columnWidthHeight = columnWidthHeight;
    }

    private List<Bitmap> getMiniThumbnailUri(List<Uri> uris) {

        List<Bitmap> miniThumbnailUris = new ArrayList<>();
        if(uris != null) {
            // 已经有图片和将要加载的图片的总和不能大于最大的限定数
            if(datas.size() >= MAX_SELECT) {
                // 已经达到最大选择图片数无需再加载
                return null;
            }
            int count = uris.size() + datas.size() > MAX_SELECT ? MAX_SELECT - datas.size() : uris.size();
            for(int i = 0; i < count; i++) {
                if(columnWidthHeight == 0) {
                    columnWidthHeight = gvPictures.getColumnWidth();
                }
                miniThumbnailUris.add(LocalImageLoader.getMiniThumbnailBitmap(getActivity(), uris.get(i), columnWidthHeight));
            }
        }
        return miniThumbnailUris;
    }


    public List<Uri> getEditPic4Content() {
        return uris;
    }

    public void setEditPicContent(List<Uri> uris) {
        this.uris = uris;
    }


    @Override
    public void onDestroy() {
        FileUtil.clearCache(getActivity());
        super.onDestroy();
    }
}
