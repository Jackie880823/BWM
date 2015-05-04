package com.madx.bwm.ui.wall;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class SelectPhotosActivity extends BaseActivity {

    public static final String IMAGES_STR = "images";

    private SelectPhotosFragment fragment;
    private List<Uri> mSelectedImages;

    private SelectPhotosFragment.SelectImageUirChangeListener listener = new SelectPhotosFragment.SelectImageUirChangeListener() {
        @Override
        public void onChange(List<Uri> imagerUris) {
            mSelectedImages = imagerUris;
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
        titleBar.setBackgroundColor(getResources().getColor(R.color.default_text_color_red));
//        leftButton.setImageResource(R.drawable.text_title_seletor);
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
//        boolean statu = fragment.changeDrawer();
//        if(statu) {
//            leftButton.setImageResource(R.drawable.back_normal);
//        } else {
//            leftButton.setImageResource(R.drawable.text_title_seletor);
//        }
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(IMAGES_STR, (ArrayList<? extends Parcelable>) mSelectedImages);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected Fragment getFragment() {
        fragment = SelectPhotosFragment.newInstance("");
        return fragment;
    }

    @Override
    public void initView() {
        fragment.setSelectImageUirChangeListener(listener);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
