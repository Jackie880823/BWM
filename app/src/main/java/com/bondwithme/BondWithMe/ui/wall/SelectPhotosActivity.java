package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.PreviewFragment;
import com.bondwithme.BondWithMe.entity.ImageData;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.ui.BaseActivity;
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

    private SelectPhotosFragment fragment;
    private ArrayList<ImageData> mSelectedImages = new ArrayList();

    /**
     * 选择多张图片标识{@value true}可以多张选择图片，{@value false}只允许选择一张图
     */
    private boolean multi;
    /**
     * 请求图片Uir用Universal Image Loader库处理标识
     */
    private boolean useUniversal;
    /**
     * 请求多张图片数量
     */
    private int residue;
    /**
     * 当前是否为浏览状态标识位
     */
    private boolean isPreview = false;
    private ImageData currentUri;

    private SelectImageUirChangeListener listener = new SelectImageUirChangeListener() {

        PreviewFragment previewFragment;

        /**
         * 添加图片{@code imageData}到选择列表
         *
         * @param imageData -   需要添加的图片uri数据
         * @return -   true:   添加成功；
         * -   false:  添加失败；
         */
        @Override
        public boolean addUri(ImageData imageData) {
            // 添加结果成功与否的返回值，默认不成功
            boolean result = false;
            if(multi) {
                if(mSelectedImages.size() < residue) {
                    // 没有超过限制的图片数量可以继续添加并返回添加结果的返回值
                    if(mSelectedImages.contains(imageData)) {
                        result = true;
                    } else {
                        result = mSelectedImages.add(imageData);
                    }
                } else {
                    // 提示用户添加的图片超过限制的数量
                    MessageUtil.showMessage(SelectPhotosActivity.this, String.format(SelectPhotosActivity.this.getString(R.string.select_too_many), TabPictureFragment.MAX_SELECT));
                }
            } else {
                // 不是同时添加多张图片，添加完成关闭当前Activity
                Intent intent = new Intent();
                if(useUniversal) {
                    intent.setData(imageData.getPathUri());
                } else {
                    intent.setData(imageData.getContentUri());
                }
                setResult(RESULT_OK, intent);
                finish();
                result = true;
            }
            return result;
        }

        /**
         * 从列表中删除图片{@code imageData}
         *
         * @param imageData -   需要删除的图片uri数据
         * @return -   true:   删除成功；
         * -   false:  删除失败；
         */
        @Override
        public boolean removeUri(ImageData imageData) {
            // 返回删除结果成功与否的值
            if(mSelectedImages.contains(imageData)) {
                return mSelectedImages.remove(imageData);
            } else {
                return true;
            }
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
        public void preview(ImageData imageData) {
            currentUri = imageData;
            Log.i(TAG, "preview& imageData: " + imageData.toString());
            //            Bitmap bitmap = LocalImageLoader.loadBitmapFromFile(getApplicationContext(), imageData);
            if(previewFragment == null) {
                previewFragment = PreviewFragment.newInstance("");
            }
            changeFragment(previewFragment, true);
            previewFragment.displayImage(currentUri.getPathUri().toString());
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
        tvTitle.setText(R.string.title_select_photos);
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
            if(!mSelectedImages.contains(currentUri)) {
                listener.addUri(currentUri);
            }
            isPreview = false;
        } else {
            if(mSelectedImages != null && mSelectedImages.size() > 0) {
                Intent intent = new Intent();
                ArrayList<Uri> uriList = new ArrayList<>();
                for(ImageData imageData : mSelectedImages) {
                    if(useUniversal) {
                        uriList.add(imageData.getPathUri());
                    } else {
                        uriList.add(imageData.getContentUri());
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

    @Override
    public void initView() {
        Intent intent = getIntent();
        // 是否为同时添加多张图片
        multi = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        useUniversal = intent.getBooleanExtra(ImageData.USE_UNIVERSAL, false);
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
        Log.i(TAG, "onKeyDown& " + keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isPreview) {
                changeFragment(fragment, false);
                isPreview = false;
                Log.i(TAG, "onKeyDown& back is false");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "dispatchKeyEvent& back");
            if(isPreview) {
                Log.i(TAG, "dispatchKeyEvent& back is false");
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
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }
}
