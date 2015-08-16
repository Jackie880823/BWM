package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.PreviewFragment;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;

import java.util.ArrayList;


/**
 * 选择图片的Activity
 * @author Jackie
 * @see BaseActivity
 */
public class SelectPhotosActivity extends BaseActivity {

    private static final String TAG = SelectPhotosActivity.class.getSimpleName();

    public static final String IMAGES_STR = "images";
    public static final String RESULT_MEDIA_TYPE = "result_media_type";

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
    private int residue;
    /**
     * 当前是否为浏览状态标识位
     */
    private boolean isPreview = false;
    private MediaData currentData;

    private SelectImageUirChangeListener listener = new SelectImageUirChangeListener() {

        PreviewFragment previewFragment;

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
            if(MediaData.TYPE_VIDEO.equals(mediaData.getType())) {
                LogUtil.i(TAG, "addUri& uri: " + mediaData.getPath());
                Intent intent = new Intent();
                intent.putExtra(RESULT_MEDIA_TYPE, MediaData.TYPE_VIDEO);
                intent.setType(MediaData.TYPE_VIDEO);
                intent.setData(Uri.parse(mediaData.getPath()));
                setResult(RESULT_OK, intent);
                finish();
                result = true;
            } else {
                if(multi) {
                    if(mSelectedImages.size() < residue) {
                        // 没有超过限制的图片数量可以继续添加并返回添加结果的返回值
                        result = mSelectedImages.contains(mediaData) || mSelectedImages.add(mediaData);
                    } else {
                        // 提示用户添加的图片超过限制的数量
                        MessageUtil.showMessage(SelectPhotosActivity.this, String.format(SelectPhotosActivity.this.getString(R.string.select_too_many), TabPictureFragment.MAX_SELECT));
                    }
                } else {
                    // 不是同时添加多张图片，添加完成关闭当前Activity
                    Intent intent = new Intent();
                    intent.setType(MediaData.TYPE_IMAGE);
                    if(useUniversal) {
                        intent.setData(Uri.parse(mediaData.getPath()));
                    } else {
                        intent.setData(mediaData.getContentUri());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                    result = true;
                }
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
            // 返回删除结果成功与否的值
            return !mSelectedImages.contains(mediaData) || mSelectedImages.remove(mediaData);
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
            if(previewFragment == null) {
                previewFragment = PreviewFragment.newInstance("");
            }
            changeFragment(previewFragment, true);
            previewFragment.displayImage(currentData);
            isPreview = true;
            leftButton.setImageResource(R.drawable.back_normal);
        }

    };

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
        leftButton.setImageResource(R.drawable.text_title_seletor);
    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        if(useVideo) {
            tvTitle.setText(R.string.select_photos_or_video);
        } else {
            tvTitle.setText(R.string.title_select_photos);
        }
    }

    @Override
    protected void titleLeftEvent() {
        if(isPreview) {
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
        if(isPreview) {
            changeFragment(fragment, false);
            if(!mSelectedImages.contains(currentData)) {
                listener.addUri(currentData);
            }
            isPreview = false;
        } else {
            if(mSelectedImages != null && mSelectedImages.size() > 0) {
                Intent intent = new Intent();
                ArrayList<Uri> uriList = new ArrayList<>();
                for(MediaData mediaData : mSelectedImages) {
                    if(useUniversal) {
                        uriList.add(Uri.parse(mediaData.getPath()));
                    } else {
                        uriList.add(mediaData.getContentUri());
                    }
                }
                intent.putParcelableArrayListExtra(IMAGES_STR, uriList);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
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
        useUniversal = intent.getBooleanExtra(MediaData.USE_UNIVERSAL, false);
        useVideo = intent.getBooleanExtra(MediaData.USE_VIDEO_AVAILABLE, false);

        // 总共需要添加的图片数量
        residue = intent.getIntExtra(TabPictureFragment.RESIDUE, 10);
        fragment.setSelectImageUirListener(listener);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i(TAG, "onKeyDown& " + keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isPreview) {
                changeFragment(fragment, false);
                isPreview = false;
                LogUtil.i(TAG, "onKeyDown& back is false");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            LogUtil.i(TAG, "dispatchKeyEvent& back");
            if(isPreview) {
                LogUtil.i(TAG, "dispatchKeyEvent& back is false");
                changeFragment(fragment, false);
                isPreview = false;
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void changeFragment(Fragment f, boolean init) {
        super.changeFragment(f, init);
        if(f instanceof SelectPhotosFragment) {
            Resources resources = getResources();
            DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(resources);
            drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawer_arrow_color));
            leftButton.setImageDrawable(drawerArrowDrawable);
        }
    }

    @Override
    public boolean dispatchKeyShortcutEvent(@NonNull KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }
}
