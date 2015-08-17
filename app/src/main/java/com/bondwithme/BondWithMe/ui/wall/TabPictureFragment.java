package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.VideoView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.PickPicAdapter;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.CustomGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
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
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private final static String TAG = TabPictureFragment.class.getSimpleName();

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp_";

    public final static String RESIDUE = "residue";
    /**
     * 限制最多可选图片张数
     */
    public final static int MAX_SELECT = 10;

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;

    /**
     * 头像缓存文件名称
     */
    //    public final static String CACHE_PIC_NAME = "head_cache";
    public static int cache_count = 0;

    private Uri voideUri = null;

    /**
     * 存放图片Uri列表
     */
    List<Uri> uris = new ArrayList<>();

    private RelativeLayout rlMediaDisplay;

    /**
     * 选择图片缩略图显示在这个控件上
     */
    private CustomGridView gvPictures;

    /**
     * 得到的视频显示在这个控件上
     */
    private VideoView vvDisplay;

    private PickPicAdapter adapter;

    private LinkedList<Map<String, Uri>> datas = new LinkedList<>();

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

    //    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {
    //
    //        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
    //        if(ca != null && ca.moveToFirst()) {
    //            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
    //            ca.close();
    //            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    //        }
    //
    //        ca.close();
    //        return null;
    //
    //    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_tab_picture;
    }

    @Override
    public void initView() {

        getViewById(R.id.ll_to_photo).setOnClickListener(this);
        getViewById(R.id.ll_to_camera).setOnClickListener(this);

        rlMediaDisplay = getViewById(R.id.media_display_rl);

        gvPictures = getViewById(R.id.gv_pictures);
        vvDisplay = new VideoView(getParentActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gvPictures.getLayoutParams());
        vvDisplay.setLayoutParams(gvPictures.getLayoutParams());

        adapter = new PickPicAdapter(getActivity(), datas, R.layout.picture_item_for_gridview, new String[]{"pic_resId",}, new int[]{R.id.iv_pic});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {

                if((view instanceof ImageView) && (data instanceof Uri)) {
                    ImageView imageView = (ImageView) view;
                    ImageLoader.getInstance().displayImage(data.toString(), imageView, UniversalImageLoaderUtil.options);
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
            addDataAndNotify(uris);
        }
    }

    @Override
    public void requestData() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        int residue = MAX_SELECT - datas.size();
        if(residue <= 0) {
            MessageUtil.showMessage(getActivity(), String.format(getActivity().getString(R.string.select_too_many), MAX_SELECT));
            return;
        }
        switch(v.getId()) {
            // 点击打开相册
            case R.id.ll_to_photo:
                Intent intent = new Intent(getParentActivity(), SelectPhotosActivity.class);
                //                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                /**
                 * 使用了Universal Image Loader库来处理图片需要返回的Uri与传统有差异，传此值用于区分
                 */
                intent.putExtra(MediaData.USE_UNIVERSAL, true);
                intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, true);
                intent.putExtra(RESIDUE, residue);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
                break;

            // 点击打开相机
            case R.id.ll_to_camera:
                openCamera(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
        }
    }

    /**
     * 调用此函数为打开相机，启动系统中相机拍摄图片或视频
     *
     * @param action 打开相机方式动作
     *               <br>{@link MediaStore#ACTION_IMAGE_CAPTURE} - 拍摄照片
     *               <br>{@link MediaStore#ACTION_VIDEO_CAPTURE} - 拍摄视频
     */
    private void openCamera(String action) {
        cache_count++;
        Intent intent2 = new Intent(action);
        intent2.putExtra("android.intent.extras.CAMERA_FACING", getCameraId(false));
        //                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
        //                intent2.putExtra("camerasensortype", 2);//using the front camera
        intent2.putExtra("autofocus", true);

        // 下面这句指定调用相机后存储的路径
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count)));
        // 图片质量为高
        intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent2.putExtra("return-data", true);
        startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
    }

    private void addDataAndNotify(Uri uri) {
        Map<String, Uri> map = new HashMap<>();
        map.put("pic_resId", uri);
        datas.add(map);
        adapter.notifyDataSetChanged();
    }

    /**
     * pick到的图片byte[]
     */
    //	byte[] personHeadImage;
    private void addDataAndNotify(List<Uri> uris) {
        if(uris != null && uris.size() > 0) {
            for(Uri uri : uris) {
                Map<String, Uri> map = new HashMap<>();
                map.put("pic_resId", uri);
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

        if(Activity.RESULT_OK == resultCode) {

            switch(requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    if(data != null) {
                        LogUtil.i(TAG, "onClick& play video uri: " + data.getData());
                        String type = data.getStringExtra(SelectPhotosActivity.RESULT_MEDIA_TYPE);
                        if(MediaData.TYPE_VIDEO.equals(type)) {
                            rlMediaDisplay.removeAllViews();
                            rlMediaDisplay.addView(vvDisplay);

                            voideUri = data.getData();

                            MediaController mediaController = new MediaController(getActivity());
                            mediaController.setAnchorView(vvDisplay);
                            vvDisplay.setMediaController(mediaController);
                            vvDisplay.setVideoURI(voideUri);
                            if(vvDisplay.isPlaying()) {
                                vvDisplay.stopPlayback();
                            }
                            vvDisplay.start();
                        } else {
                            rlMediaDisplay.removeAllViews();
                            rlMediaDisplay.addView(gvPictures);

                            ArrayList<Uri> pickUris;
                            pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.IMAGES_STR);
                            addDataAndNotify(pickUris);
                            uris.addAll(pickUris);
                        }
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
                    uri = Uri.parse(ImageDownloader.Scheme.FILE.wrap(uri.getPath()));
                    if(new File(uri.getPath()).exists()) {
                        addDataAndNotify(uri);
                        uris.add(uri);
                    }
                    //                    }

                    break;

                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Uri getVoideUri() {
        return voideUri;
    }

    public List<Uri> getEditPic4Content() {
        return voideUri == null? null : uris;
    }

    public void setEditPicContent(List<Uri> uris) {
        this.uris = uris;
    }


    @Override
    public void onDestroy() {
        if(vvDisplay.isPlaying()) {
            vvDisplay.stopPlayback();
        }
        FileUtil.clearCache(getActivity());
        super.onDestroy();
    }
}
