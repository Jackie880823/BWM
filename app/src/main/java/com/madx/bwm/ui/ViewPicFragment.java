package com.madx.bwm.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.madx.bwm.R;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MessageUtil;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by wing on 15/3/23.
 */
public class ViewPicFragment extends BaseLazyLoadFragment {

    private String pic_url;
    private PhotoView iv_pic;
    private RelativeLayout btn_save_2_local;
    private final static int IMAGE_LOADED_SUCCESSED = 10;
    private final static int HIDE_WAITTING = 11;
    private final static int SHOW_WAITTING = 12;
    private View vProgress;

    public ViewPicFragment() {
        super();
        useLazyLoad = true;
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_view_pic;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_LOADED_SUCCESSED:
                    if (ViewPicFragment.this.isAdded()) {
                        iv_pic.setImageDrawable(new BitmapDrawable(getResources(), (Bitmap) msg.obj));
                    }
                    mHandler.sendEmptyMessage(HIDE_WAITTING);
                    break;
                case SHOW_WAITTING:
                    if (vProgress == null) {
                        vProgress = getViewById(R.id.rl_progress);
                    }
                    vProgress.setVisibility(View.VISIBLE);
                    Log.i("", "SHOW_WAITTING================");
                    break;
                case HIDE_WAITTING:
                    vProgress.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        if (iv_pic != null) {
            iv_pic.destroyDrawingCache();
            iv_pic.getDrawable().setCallback(null);
            iv_pic = null;
        }
        if (downloadRequest != null) {
            downloadRequest.cancel();
        }
        if (vProgress != null) {
            vProgress.setVisibility(View.GONE);
        }
        clearCache();
        super.onDestroy();
    }

    private Bitmap bitmapCache;

    @Override
    public void initView() {

        Log.i("", "initView===================");
//        iv_pic = (PhotoView) getViewById(R.id.iv_pic);
//        btn_save_2_local = (RelativeLayout) getViewById(R.id.btn_save_2_local);
//        btn_save_2_local.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bitmapCache != null && !bitmapCache.isRecycled()) {
//                    try {
//                        PicturesCacheUtil.saveImageToGallery(getActivity(), bitmapCache, "wall");
//                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
//                    } catch (Exception e) {
//                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
//                    }
//                }
//            }
//        });
//        if (!TextUtils.isEmpty(pic_url)) {
//
//            new HttpTools(getActivity()).download(pic_url, PicturesCacheUtil.getCachePicPath(getActivity()), true, new HttpCallback() {
//                @Override
//                public void onStart() {
//                    iv_pic.setImageResource(R.drawable.network_image_default);
//                }
//
//                @Override
//                public void onFinish() {
//                    mHandler.sendEmptyMessage(HIDE_WAITTING);
//                }
//
//                @Override
//                public void onResult(String string) {
//
//                    if (iv_pic != null) {
//                        bitmapCache = LocalImageLoader.loadBitmapFromFile(getActivity(), string, iv_pic.getWidth(), iv_pic.getHeight());
//                        iv_pic.setImageBitmap(bitmapCache);
//
////                    iv_pic.setImageBitmap(getImageFromFile(string, iv_pic.getWidth(), iv_pic.getHeight()));
//                    }
//
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onCancelled() {
//
//                }
//
//                @Override
//                public void onLoading(long count, long current) {
//
//                }
//            });
//        }
    }


    @Override
    public boolean getUserVisibleHint() {

        return super.getUserVisibleHint();
    }

    @Override
    public void requestData() {
//        if (TextUtils.isEmpty(pic_url)) {
//            getActivity().finish();
//            return;


//        }

    }


    private void clearCache() {
        if (bitmapCache != null) {
            bitmapCache.recycle();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clearCache();
    }


    DownloadRequest downloadRequest;

    @Override
    protected void lazyLoad() {

        pic_url = getArguments().getString("pic_url");
        iv_pic = (PhotoView) getViewById(R.id.iv_pic);
        btn_save_2_local = (RelativeLayout) getViewById(R.id.btn_save_2_local);
        btn_save_2_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapCache != null && !bitmapCache.isRecycled()) {
                    try {
                        PicturesCacheUtil.saveImageToGallery(getActivity(), bitmapCache, "wall");
                    } catch (Exception e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    }
                }
            }
        });
        if (!TextUtils.isEmpty(pic_url)) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (downloadRequest != null) {
                downloadRequest.cancel();
            }

            downloadRequest = new HttpTools(getActivity()).download(pic_url, PicturesCacheUtil.getCachePicPath(getActivity()), true, new HttpCallback() {
                @Override
                public void onStart() {
                    mHandler.sendEmptyMessage(SHOW_WAITTING);
                    iv_pic.setImageResource(R.drawable.network_image_default);
                }

                @Override
                public void onFinish() {
                    mHandler.sendEmptyMessage(HIDE_WAITTING);
                }

                @Override
                public void onResult(String string) {

                    if (iv_pic != null) {
                        bitmapCache = LocalImageLoader.loadBitmapFromFile(getActivity(), string, iv_pic.getWidth(), iv_pic.getHeight());
                        iv_pic.setImageBitmap(bitmapCache);

//                    iv_pic.setImageBitmap(getImageFromFile(string, iv_pic.getWidth(), iv_pic.getHeight()));
                    }

                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onCancelled() {
                    iv_pic.setImageResource(R.drawable.network_image_default);
                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }


    }

}
