package com.bondwithme.BondWithMe.ui.more;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.NewsHolder;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LocalImageLoader;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewUrlPicActivity extends BaseActivity{

    private View vProgress;
    private ImageView imageView;
    private String picUrl;

    @Override
    public int getLayout() {
        return R.layout.activity_view_url_pic;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        picUrl = getIntent().getStringExtra(NewsHolder.PIC_URL);
        if (TextUtils.isEmpty(picUrl))
        {
            finish();
        }

        vProgress = getViewById(R.id.rl_progress);
        imageView = getViewById(R.id.iv_pic);

    }

    @Override
    public void requestData() {
        new HttpTools(this).download(this, picUrl, PicturesCacheUtil.getCachePicPath(this,false), true, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                //这个string？？？
                if (imageView != null) {
                    Bitmap bitmapCache = LocalImageLoader.loadBitmapFromFile(ViewUrlPicActivity.this, string, imageView.getWidth(), imageView.getHeight());
                    imageView.setImageBitmap(bitmapCache);
                    PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
                    mAttacher.update();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }


    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(Color.BLACK);
        leftButton.setImageResource(R.drawable.back_press);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
