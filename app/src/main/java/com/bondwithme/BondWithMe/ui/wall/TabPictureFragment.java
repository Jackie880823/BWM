package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.VideoView;

import com.bondwithme.BondWithMe.Constant;
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
import com.bondwithme.BondWithMe.widget.MyDialog;
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
public class TabPictureFragment extends BaseFragment<WallNewActivity> implements View.OnClickListener, MediaPlayer.OnPreparedListener {
    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private final static String TAG = TabPictureFragment.class.getSimpleName();

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp_";
    public final static String VIDEO_NAME_TEMP = "video_temp_";

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_IMAGE = 2;
    private final static int REQUEST_HEAD_VIDEO = 3;
    public static final String MP4 = ".mp4";

    /**
     * 头像缓存文件名称
     */
    //    public final static String CACHE_PIC_NAME = "head_cache";
    public static int cache_count = 0;

    private Uri videoUri = Uri.EMPTY;

    private String videoDuration;

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
    private View previewVideoView;

    private PickPicAdapter adapter;

    private MyDialog myDialog;

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

        previewVideoView = LayoutInflater.from(getActivity()).inflate(R.layout.tab_picture_voide_view, null);
        vvDisplay = (VideoView) previewVideoView.findViewById(R.id.preview_video_view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        previewVideoView.setLayoutParams(params);

        // 删除视频
        previewVideoView.findViewById(R.id.delete_video_view).setOnClickListener(this);
        vvDisplay.setOnPreparedListener(this);

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
                        e.printStackTrace();
                    }
                }
            }
        });

        gvPictures.setAdapter(adapter);

        if(!uris.isEmpty()) {
            clearVideo();
            addDataAndNotify(uris);
        } else if(!Uri.EMPTY.equals(videoUri)) {
            clearPhotos();
            vvDisplay.setVideoURI(videoUri);
        }
    }

    @Override
    public void requestData() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        final int residue = SelectPhotosActivity.MAX_SELECT - datas.size();
        if(residue <= 0) {
            MessageUtil.showMessage(getActivity(), String.format(getActivity().getString(R.string.select_too_many), SelectPhotosActivity.MAX_SELECT));
            return;
        }
        switch(v.getId()) {
            // 点击打开相册
            case R.id.ll_to_photo:
                if(!Uri.EMPTY.equals(videoUri)) {
                    // 已经选择了视频需要弹出提示
                    myDialog = new MyDialog(getParentActivity(), "", getParentActivity().getString(R.string.will_remove_selected_video));
                    myDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            openPhotos(residue);
                        }
                    });

                    // 不选择视频
                    myDialog.setButtonCancel(R.string.text_dialog_no, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                } else {
                    openPhotos(residue);
                }
                break;

            // 点击打开相机
            case R.id.ll_to_camera:
                myDialog = new MyDialog(getParentActivity(), "", getActivity().getString(R.string.select_media));
                myDialog.setButtonAccept(getParentActivity().getString(R.string.text_video), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        openCameraAfterCheck(REQUEST_HEAD_VIDEO);
                    }
                });
                myDialog.setButtonCancel(getParentActivity().getString(R.string.text_camera), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        openCameraAfterCheck(REQUEST_HEAD_IMAGE);
                    }
                });
                myDialog.show();
                break;

            case R.id.delete_video_view:
                clearVideo();
                break;
        }
    }

    private void openCameraAfterCheck(int request) {
        cache_count++;
        switch(request) {
            case REQUEST_HEAD_VIDEO:
                if(uris.isEmpty()) {
                    Uri out = getOutVideoUri();
                    openCamera(MediaStore.ACTION_VIDEO_CAPTURE, out, REQUEST_HEAD_VIDEO);
                } else {
                    myDialog = new MyDialog(getParentActivity(), "", getString(R.string.will_remove_photos));
                    myDialog.setButtonAccept(R.string.text_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            Uri out = getOutVideoUri();
                            openCamera(MediaStore.ACTION_VIDEO_CAPTURE, out, REQUEST_HEAD_VIDEO);
                        }
                    });
                    myDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                }
                break;
            case REQUEST_HEAD_IMAGE:
                if(Uri.EMPTY.equals(videoUri)) {
                    Uri out = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count));
                    openCamera(MediaStore.ACTION_IMAGE_CAPTURE, out, REQUEST_HEAD_IMAGE);
                } else {
                    myDialog = new MyDialog(getParentActivity(), "", getString(R.string.will_remove_selected_video));
                    myDialog.setButtonAccept(R.string.text_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            Uri out = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count));
                            openCamera(MediaStore.ACTION_IMAGE_CAPTURE, out, REQUEST_HEAD_IMAGE);
                        }
                    });
                    myDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                }
                break;
            default:
                LogUtil.e(TAG, "the request error");
        }

    }

    /**
     * @return 获取保存视频的{@link Uri}
     */
    private Uri getOutVideoUri() {
        File file = new File(Constant.VIDEO_PATH);
        boolean exists = file.exists() || file.mkdir();
        File video;
        video = exists ? new File(Constant.VIDEO_PATH + VIDEO_NAME_TEMP + MP4) : new File(Environment.getDataDirectory() + VIDEO_NAME_TEMP + MP4);
        return Uri.fromFile(video);
    }

    /**
     * 打开相册，选择图片
     *
     * @param residue 选择图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPhotos(int residue) {

        Intent intent = new Intent(getParentActivity(), SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        /**
         * 使用了Universal Image Loader库来处理图片需要返回的Uri与传统有差异，传此值用于区分
         */
        intent.putExtra(MediaData.USE_UNIVERSAL, true);
        intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, true);
        intent.putExtra(SelectPhotosActivity.SELECTED_PHOTOS, !datas.isEmpty());
        intent.putExtra(SelectPhotosActivity.RESIDUE, residue);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_HEAD_PHOTO);
    }

    /**
     * 调用此函数为打开相机，启动系统中相机拍摄图片或视频
     *
     * @param action  打开相机方式动作
     *                <br>{@link MediaStore#ACTION_IMAGE_CAPTURE} - 拍摄照片
     *                <br>{@link MediaStore#ACTION_VIDEO_CAPTURE} - 拍摄视频
     * @param request 请求代码
     *                <br>{@link #REQUEST_HEAD_IMAGE} - 拍照
     *                <br> {@link #REQUEST_HEAD_VIDEO} - 视频
     */
    private void openCamera(String action, Uri out, int request) {
        Intent intent2 = new Intent(action);
        intent2.putExtra("android.intent.extras.CAMERA_FACING", getCameraId(false));
        //                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
        //                intent2.putExtra("camerasensortype", 2);//using the front camera
        intent2.putExtra("autofocus", true);

        // 设置限制文件大小
        intent2.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MediaData.MAX_SIZE);

        // 下面这句指定调用相机后存储的路径
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, out);
        // 图片质量为高
        intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent2.putExtra("return-data", true);
        startActivityForResult(intent2, request);
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

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
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
                            addVideoFromActivityResult(data);
                        } else {
                            clearVideo();

                            ArrayList<Uri> pickUris;
                            pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.IMAGES_STR);
                            addDataAndNotify(pickUris);
                            uris.addAll(pickUris);
                        }
                    }
                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_IMAGE: {
                    clearVideo();

                    int residue = SelectPhotosActivity.MAX_SELECT - datas.size();
                    if(residue <= 0) {
                        MessageUtil.showMessage(getActivity(), String.format(getActivity().getString(R.string.select_too_many), SelectPhotosActivity.MAX_SELECT));
                        return;
                    }
                    //                    if (data != null) {
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(getActivity(), CACHE_PIC_NAME_TEMP + cache_count));
                    uri = Uri.parse(ImageDownloader.Scheme.FILE.wrap(uri.getPath()));
                    if(new File(uri.getPath()).exists()) {
                        addDataAndNotify(uri);
                        uris.add(uri);
                    }
                    break;
                }

                // 调用用相机录制返回的视频数据
                case REQUEST_HEAD_VIDEO:
                    addVideoFromActivityResult(data);
                    break;

                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 添加Activity请求返回的视频数据
     *
     * @param data 请求返回的{@link Intent}
     */
    private void addVideoFromActivityResult(Intent data) {
        clearPhotos();

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(getContext(), data.getData());
        videoDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        metadataRetriever.release();
        videoUri = data.getData();
        LogUtil.i(TAG, "addVideoFromActivityResult& videoUri: " + videoUri);
        LogUtil.i(TAG, "addVideoFromActivityResult& videoDuration: " + videoDuration);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(vvDisplay);
        vvDisplay.setMediaController(mediaController);
        vvDisplay.setVideoURI(videoUri);
        if(vvDisplay.isPlaying()) {
            vvDisplay.stopPlayback();
        }
    }

    /**
     * 清除选择的图片数据和UI
     */
    private void clearPhotos() {
        // 删除图片显示View
        if(rlMediaDisplay.indexOfChild(gvPictures) >= 0) {
            rlMediaDisplay.removeView(gvPictures);
        }

        // 显示视频View
        if(rlMediaDisplay.indexOfChild(previewVideoView) < 0) {
            rlMediaDisplay.addView(previewVideoView, 0);
        }

        // 清册选择的图片
        uris.clear();
        datas.clear();
    }

    /**
     * 清空选择的视频和显示的UI
     */
    private void clearVideo() {
        videoUri = Uri.EMPTY;

        // 删除视频View
        if(rlMediaDisplay.indexOfChild(previewVideoView) >= 0) {
            rlMediaDisplay.removeView(previewVideoView);
        }

        // 添加图片显示View
        if(rlMediaDisplay.indexOfChild(gvPictures) < 0) {
            rlMediaDisplay.addView(gvPictures, 0);
        }
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        uris.clear();
        this.videoUri = videoUri;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public List<Uri> getEditPic4Content() {
        return uris;
    }

    public void setEditPicContent(List<Uri> uris) {
        videoUri = Uri.EMPTY;
        this.uris = uris;
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        if(vvDisplay.isPlaying()) {
            vvDisplay.pause();
        }
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(vvDisplay.isPlaying()) {
            vvDisplay.stopPlayback();
        }
        FileUtil.clearCache(getActivity());
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
