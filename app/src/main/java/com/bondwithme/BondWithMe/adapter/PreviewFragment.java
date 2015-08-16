package com.bondwithme.BondWithMe.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosActivity;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosFragment;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * <br>预览图片或视频的{@link Fragment }，在{@link SelectPhotosFragment }中点击子项缩略图跳转至当前{@link Fragment }
 * <br>Created by Jackie on 7/14/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class PreviewFragment extends BaseFragment<SelectPhotosActivity> {

    private static final String TAG = PreviewFragment.class.getSimpleName();

    /**
     * 当点击的是图片的缩略图在此{@link ImageView}控件中显示此缩略图的全部内容
     */
    private ImageView imageView;

    /**
     * 当点击的是视频的缩略图在此{@link VideoView}控件中显示此视频的内容并在{@link VideoView}中播放
     */
    private VideoView videoView;

    /**
     * @see MediaController
     */
    private MediaController mediaController;

    /**
     * 所点击的缩略图的{@link MediaData}封装对象
     */
    private MediaData mediaData;

    public static PreviewFragment newInstance(String... params) {
        return createInstance(new PreviewFragment(), params);
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.preview_album;
    }

    @Override
    public void initView() {
        Log.i(TAG, "initView");
        imageView = getViewById(R.id.preview_image_view);
        videoView = getViewById(R.id.preview_video_view);
        displayView();
    }

    /**
     * <br>根据点击的缩略图传入的{@link MediaData}数据类型的{@link MediaData#getType()}在当前UI中显示不同的视图
     * <br>{@link MediaData#getType()}若得到的为{@link MediaData#TYPE_VIDEO}则通过{@link VideoView}显示可播放的视频
     * <br>预览，若得到的为{@link MediaData#TYPE_IMAGE}显示图片的全部内容
     */
    private void displayView() {
        if(!TextUtils.isEmpty(mediaData.getPath()) && imageView != null) {
            if(MediaData.TYPE_VIDEO.equals(mediaData.getType())) {

                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);

                // 视频预览
                if(mediaController == null) {
                    mediaController = new MediaController(getActivity());
                }
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(mediaData.getContentUri());
                if(videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
                videoView.start();

            } else {

                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);

                // 显示图片预览
                ImageLoader.getInstance().displayImage(mediaData.getPath(), imageView, UniversalImageLoaderUtil.options);
            }
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
        if(videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    @Override
    public void requestData() {

    }

    /**
     * 传入缩略图的封装数据{@link MediaData}在当前UI中显示数据中的内容
     * @param mediaData 缩略图的数据内容
     */
    public void displayImage(MediaData mediaData) {
        this.mediaData = mediaData;
        displayView();
    }
}
