package com.bondwithme.BondWithMe.ui.share;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.DrawerArrowDrawable;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.ArrayList;


/**
 * 选择图片的Activity
 *
 * @author Jackie
 * @see BaseActivity
 */
public class SelectPhotosActivity extends BaseActivity {

    private static final String TAG = SelectPhotosActivity.class.getSimpleName();

    public static final String EXTRA_IMAGES_STR = "images";
    public static final String EXTRA_SELECTED_PHOTOS = "selected_photos";

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp_";

    public final static String EXTRA_RESIDUE = "residue";
    public static final String MP4 = ".mp4";
    private String videoPaht;
    private String imagePaht;


    //    public static final String EXTRA_SELECTED_PHOTOS = "selected_photos";
    /**
     * 限制最多可选图片张数
     */
    public final static int MAX_SELECT = 50;
    public static final int REQUEST_HEAD_VIDEO = 100;
    public static final int REQUEST_HEAD_IMAGE = 101;

    private int residue = MAX_SELECT;
    private SelectPhotosFragment fragment;
    private ArrayList<MediaData> mSelectedImages = new ArrayList<>();

    /**
     * 选择多张图片标识{@value true}可以多张选择图片，{@value false}只允许选择一张图
     */
    private boolean multi;
    /**
     * 请求图片Uir用Universal Image Loader库处理标识
     */
    private boolean useUniversal;

    /**
     * 请求数据
     */
    private boolean useVideo;
    /**
     * 请求多张图片数量
     */
    //    private int residue;
    /**
     * 当前是否为浏览状态标识位
     */
    private boolean isPreview = false;
    /**
     * 当前选择的媒体数据
     */
    private MediaData currentData;

    /**
     * 选择视频询问框，提示选择视频后将删除已经选择好的图片
     */
    private MyDialog selectVideoDialog;

    private ImageButton addButton;

    private SelectImageUirChangeListener listener = new SelectImageUirChangeListener() {

        com.bondwithme.BondWithMe.ui.share.PreviewFragment previewFragment;

        /**
         * 添加图片{@code mediaData}到选择列表
         *
         * @param mediaData -   需要添加的图片uri数据
         * @return -   true:   添加成功；
         * -   false:  添加失败；
         */
        @Override
        public boolean addUri(MediaData mediaData) {
            // 添加结果成功与否的返回值，默认不成功
            boolean result = false;
            if (MediaData.TYPE_VIDEO.equals(mediaData.getType())) {
                LogUtil.i(TAG, "addUri& uri: " + mediaData.getPath());
                alertAddVideo(mediaData);
            } else {
                LogUtil.i(TAG, "addUri& uri path: " + mediaData.getPath());
                if (multi) {
                    //                    if(mSelectedImages.size() < residue) {
                    if (mSelectedImages.size() <= residue) {
                        // 没有超过限制的图片数量可以继续添加并返回添加结果的返回值
                        result = mSelectedImages.contains(mediaData) || mSelectedImages.add(mediaData);
                    } else {
                        // 提示用户添加的图片超过限制的数量
                        MessageUtil.showMessage(SelectPhotosActivity.this, String.format(SelectPhotosActivity.this.getString(R.string.select_too_many), residue));
                    }
                } else {
                    // 不是同时添加多张图片，添加完成关闭当前Activity
                    Intent intent = new Intent();
                    intent.setType(MediaData.TYPE_IMAGE);
                    if (useUniversal) {
                        intent.setData(Uri.parse(mediaData.getPath()));
                    } else {
                        intent.setData(mediaData.getContentUri());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                    result = true;
                }
            }
            if (result && addButton.getVisibility() == View.VISIBLE) { // 选择了图片右上角的显示确认图视
                switchRightTitleView(true);
            }
            return result;
        }

        /**
         * 从列表中删除图片{@code mediaData}
         *
         * @param mediaData -   需要删除的图片uri数据
         * @return -   true:   删除成功；
         * -   false:  删除失败；
         */
        @Override
        public boolean removeUri(MediaData mediaData) {
            boolean result;
            // 返回删除结果成功与否的值
            result = !mSelectedImages.contains(mediaData) || mSelectedImages.remove(mediaData);
            if (mSelectedImages.isEmpty()) { // 没有选中图片恢复打开相机的图视
                switchRightTitleView(false);
            }
            return result;
        }

        /**
         * 打开了左侧的目录列表并设置标题栏左侧图标为{@code drawable}
         *
         * @param drawable
         */
        @Override
        public void onDrawerOpened(Drawable drawable) {
            leftButton.setImageDrawable(drawable);
        }

        /**
         * 关闭了左侧的目录列表并设置标题栏左侧图标为{@code drawable}
         *
         * @param drawable
         */
        @Override
        public void onDrawerClose(Drawable drawable) {
            leftButton.setImageDrawable(drawable);
        }

        @Override
        public void preview(MediaData mediaData) {
            currentData = mediaData;
            LogUtil.i(TAG, "preview& mediaData: " + mediaData.toString());
            //            Bitmap bitmap = LocalImageLoader.loadBitmapFromFile(getApplicationContext(), mediaData);
            if (previewFragment == null) {
                previewFragment = com.bondwithme.BondWithMe.ui.share.PreviewFragment.newInstance("");
            }
            changeFragment(previewFragment, true);
            previewFragment.displayImage(currentData);
            isPreview = true;
            leftButton.setImageResource(R.drawable.back_normal);
        }

    };

    @Override
    public int getLayout() {
        return R.layout.activity_select_photos_base;
    }

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {
    }

    private void switchRightTitleView(boolean isEntry) {
        if (isEntry) {
            addButton.setVisibility(View.GONE);
            rightButton.setImageResource(R.drawable.btn_done);
        } else {
            addButton.setVisibility(View.VISIBLE);
            rightButton.setImageResource(R.drawable.add_video);
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        addButton = getViewById(R.id.ib_top_button_add);
        addButton.setImageResource(R.drawable.add_photo);
        rightButton.setImageResource(R.drawable.add_video);
        leftButton.setImageResource(R.drawable.text_title_seletor);

        addButton.setOnClickListener(new View.OnClickListener() {
            int cache_count = 0;

            @Override
            public void onClick(View v) {
                File file = PicturesCacheUtil.getCachePicFileByName(SelectPhotosActivity.this, CACHE_PIC_NAME_TEMP + cache_count++);
                if (file != null) {
                    imagePaht = file.getAbsolutePath();
                }
                Uri out = Uri.fromFile(file);
                openCamera(MediaStore.ACTION_IMAGE_CAPTURE, out, REQUEST_HEAD_IMAGE);
            }
        });
    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        if (useVideo) {
            tvTitle.setText(R.string.select_photos_or_video);
        } else {
            tvTitle.setText(R.string.title_select_photos);
        }
    }

    @Override
    protected void titleLeftEvent() {
        if (isPreview) {
            changeFragment(fragment, false);
            isPreview = false;
        } else {
            fragment.changeDrawer();
        }
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        if (isPreview) {
            changeFragment(fragment, false);
            if (!mSelectedImages.contains(currentData)) {
                listener.addUri(currentData);
            }
            isPreview = false;
        } else {
            if (!mSelectedImages.isEmpty()) {
                Intent intent = new Intent();
                ArrayList<Uri> uriList = new ArrayList<>();
                for (MediaData mediaData : mSelectedImages) {
                    if (useUniversal) {
                        uriList.add(Uri.parse(mediaData.getPath()));
                    } else {
                        uriList.add(mediaData.getContentUri());
                    }
                }
                intent.putParcelableArrayListExtra(EXTRA_IMAGES_STR, uriList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Uri out = getOutVideoUri();
                openCamera(MediaData.ACTION_RECORDER_VIDEO, out, REQUEST_HEAD_VIDEO);
            }
        }
    }

    /**
     * @return 获取保存视频的{@link Uri}
     */
    private Uri getOutVideoUri() {
        File file = new File(Constant.VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
//        boolean exists = file.exists() || file.mkdir();
//        File video;
//        video = exists ? new File(Constant.VIDEO_PATH + System.currentTimeMillis() + MP4) : new File(Environment.getDataDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + MP4);
        videoPaht = file + "/" + System.currentTimeMillis() + MP4;
        return Uri.fromFile(new File(videoPaht));
    }

    /**
     * 调用此函数为打开相机，启动系统中相机拍摄图片或视频
     *
     * @param action  打开相机方式动作
     *                <br>{@link MediaStore#ACTION_IMAGE_CAPTURE} - 拍摄照片
     *                <br>{@link MediaData#ACTION_RECORDER_VIDEO} - 拍摄视频
     * @param request 请求代码
     *                <br>{@link #REQUEST_HEAD_IMAGE} - 拍照
     *                <br> {@link #REQUEST_HEAD_VIDEO} - 视频
     */
    private void openCamera(String action, Uri out, int request) {
        Intent intent = new Intent(action);
        intent.putExtra("android.intent.extras.CAMERA_FACING", getCameraId(false));
        intent.putExtra("autofocus", true);

        // 设置限制文件大小
//        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MediaData.MAX_SIZE);

        // 下面这句指定调用相机后存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        // 图片质量为高
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, request);
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

    @Override
    protected Fragment getFragment() {
        fragment = SelectPhotosFragment.newInstance(mSelectedImages, "");
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void initView() {
        Intent intent = getIntent();
        // 是否为同时添加多张图片
        multi = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        useUniversal = intent.getBooleanExtra(MediaData.EXTRA_USE_UNIVERSAL, false);
        useVideo = intent.getBooleanExtra(MediaData.USE_VIDEO_AVAILABLE, false);
        residue = intent.getIntExtra(EXTRA_RESIDUE, MAX_SELECT);
        // 总共需要添加的图片数量
        //        residue = intent.getIntExtra(EXTRA_RESIDUE, 10);
        fragment.setSelectImageUirListener(listener);
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(EXTRA_SELECTED_PHOTOS);
        mSelectedImages.clear();
        if (uris != null) {
            for (Uri uri : uris) {
                MediaData mediaData = new MediaData(uri, uri.toString(), MediaData.TYPE_IMAGE, 0);
                mSelectedImages.add(mediaData);
            }
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            LogUtil.i(TAG, "dispatchKeyEvent& back");
            if (isPreview) {
                LogUtil.i(TAG, "dispatchKeyEvent& back is false");
                changeFragment(fragment, false);
                isPreview = false;
                return false;
            } else {
                Intent intent = new Intent();
                ArrayList<Uri> uriList = new ArrayList<>();
                for (MediaData mediaData : mSelectedImages) {
                    if (useUniversal) {
                        uriList.add(Uri.parse(mediaData.getPath()));
                    } else {
                        uriList.add(mediaData.getContentUri());
                    }
                }
                intent.putParcelableArrayListExtra(EXTRA_IMAGES_STR, uriList);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void changeFragment(Fragment f, boolean init) {
        super.changeFragment(f, init);
        if (f instanceof SelectPhotosFragment) {
            Resources resources = getResources();
            DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(resources);
            drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawer_arrow_color));
            leftButton.setImageDrawable(drawerArrowDrawable);
            if (mSelectedImages.isEmpty()) {
                switchRightTitleView(false);
            }
        } else {
            switchRightTitleView(true);
        }
    }

    @Override
    public boolean dispatchKeyShortcutEvent(@NonNull KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }

    /**
     * 传入为{@link MediaData#TYPE_VIDEO}弹出提示，选择了{@link MediaData#TYPE_VIDEO}将会删除已经选择好的{@link MediaData#TYPE_IMAGE}
     *
     * @param mediaData {@link MediaData#TYPE_VIDEO}类型的媒体数据
     */
    public void alertAddVideo(final MediaData mediaData) {
        //        if(!mSelectedImages.isEmpty() || residue < MAX_SELECT) {
        if (!mSelectedImages.isEmpty()) {

            if (selectVideoDialog == null) {
                selectVideoDialog = new MyDialog(this, "", getString(R.string.will_remove_photos));
                // 确认要选择视频
                selectVideoDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectVideoDialog.dismiss();
                        mSelectedImages.clear();
                        resultVideo(mediaData);
                    }
                });

                // 不选择视频
                selectVideoDialog.setButtonCancel(R.string.text_dialog_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectVideoDialog.dismiss();
                    }
                });
            }

            selectVideoDialog.show();
        } else {
            resultVideo(mediaData);
        }
    }

    /**
     * 确定返回{@link MediaData#TYPE_VIDEO}的媒体数据
     *
     * @param mediaData 类型为{@link MediaData#TYPE_VIDEO}的{@link MediaData}
     */
    private void resultVideo(MediaData mediaData) {
        Intent intent = new Intent();
        intent.putExtra(MediaData.EXTRA_MEDIA_TYPE, MediaData.TYPE_VIDEO);
        intent.putExtra(MediaData.EXTRA_VIDEO_DURATION, mediaData.getDuration());
        intent.setData(Uri.parse(mediaData.getPath()));
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_HEAD_VIDEO:
                    Uri uri = data.getData();
                    MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                    metadataRetriever.setDataSource(this, uri);
                    String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    ;
                    metadataRetriever.release();
                    MediaData video = new MediaData(data.getData(), videoPaht, MediaData.TYPE_VIDEO, Long.valueOf(duration));
                    listener.addUri(video);
                    break;
                case REQUEST_HEAD_IMAGE:
                    Uri imageUri = Uri.parse(ImageDownloader.Scheme.FILE.wrap(imagePaht));
                    MediaData image = new MediaData(imageUri, imagePaht, MediaData.TYPE_IMAGE, 0);
                    listener.addUri(image);
                    break;
            }
        }
    }
}
