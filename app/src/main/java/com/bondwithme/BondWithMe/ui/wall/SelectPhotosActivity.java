package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class SelectPhotosActivity extends BaseActivity {

    public static final String IMAGES_STR = "images";

    private SelectPhotosFragment fragment;
    private List<Uri> mSelectedImages = new ArrayList();

    private SelectPhotosFragment.SelectImageUirChangeListener listener = new SelectPhotosFragment.SelectImageUirChangeListener() {
        @Override
        public void onChange(Uri imageUri, boolean isAdd) {
            boolean multi = getIntent().getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            if(multi) {
                if(isAdd) {
                    mSelectedImages.add(imageUri);
                } else if(mSelectedImages.contains(imageUri)) {
                    mSelectedImages.remove(imageUri);
                }
            } else {
                Intent intent = new Intent();
                intent.setData(imageUri);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        @Override
        public void onDrawerOpened(Drawable drawable) {
            leftButton.setImageDrawable(drawable);
//            leftButton.setImageResource(R.drawable.back_normal);
        }

        @Override
        public void onDrawerClose(Drawable drawable) {
            leftButton.setImageDrawable(drawable);
//            leftButton.setImageResource(R.drawable.text_title_seletor);
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
        fragment.changeDrawer();
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        if(mSelectedImages != null && mSelectedImages.size() > 0) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(IMAGES_STR, (ArrayList<? extends Parcelable>) mSelectedImages);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected Fragment getFragment() {
        fragment = SelectPhotosFragment.newInstance("");
        return fragment;
    }

    @Override
    public void initView() {
        fragment.setSelectImageUirListener(listener);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
