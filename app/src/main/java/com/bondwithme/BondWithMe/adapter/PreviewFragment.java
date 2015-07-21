package com.bondwithme.BondWithMe.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosActivity;

/**
 * Created by Jackie Zhu on 7/14/15.
 */
public class PreviewFragment extends BaseFragment<SelectPhotosActivity> {

    private static final String TAG = PreviewFragment.class.getSimpleName();

    private ImageView imageView;
    private Bitmap bitmap;

    public static final PreviewFragment newInstance(String... params) {
        return createInstance(new PreviewFragment(), params);
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.preview_album;
    }

    @Override
    public void initView() {
        Log.i(TAG, "initView");
        imageView = getViewById(R.id.preview_image_iv);
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void requestData() {

    }

    public void setBitmap(Bitmap bitmap) {
        Log.i(TAG, "setBitmap");
        this.bitmap = bitmap;
        if(imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
