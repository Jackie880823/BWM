package com.bondwithme.BondWithMe.adapter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Jackie Zhu on 7/14/15.
 */
public class PreviewFragment extends BaseFragment<SelectPhotosActivity> {

    private static final String TAG = PreviewFragment.class.getSimpleName();

    private ImageView imageView;
    @Deprecated
    private String iagmeUri;

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
        if(!TextUtils.isEmpty(iagmeUri)) {
            ImageLoader.getInstance().displayImage(iagmeUri, imageView, UniversalImageLoaderUtil.options);
        }
    }

    @Override
    public void requestData() {

    }

    @Deprecated
    public void setBitmap(Bitmap bitmap) {
        Log.i(TAG, "setBitmap");
        if(imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void displayImage(String uri) {
        this.iagmeUri = uri;
        if(imageView != null) {
            ImageLoader.getInstance().displayImage(uri, imageView, UniversalImageLoaderUtil.options);
        }
    }
}
