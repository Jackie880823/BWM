package com.madxstudio.co8.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.madxstudio.co8.App;
import com.madxstudio.co8.R;
import com.madxstudio.co8.http.PicturesCacheUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.MessageUtil;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by wing on 15/3/23.
 */
public class ViewPicFragment extends BaseFragment {
    private static final String TAG = ViewPicFragment.class.getSimpleName();
    private ImageView iv_pic;
    private final static int IMAGE_LOADED_SUCCESSED = 10;
    private final static int HIDE_WAITTING = 11;
    private final static int SHOW_WAITTING = 12;
    private View vProgress;

    public ViewPicFragment() {
        super();
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_view_pic;
    }

    @Override
    protected void setParentTitle() {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_LOADED_SUCCESSED:
                    if (ViewPicFragment.this.isAdded()) {
                        iv_pic.setImageDrawable(new BitmapDrawable(getResources(), (Bitmap) msg
                                .obj));
                    }
                    mHandler.sendEmptyMessage(HIDE_WAITTING);
                    break;
                case SHOW_WAITTING:
                    if (vProgress == null) {
                        vProgress = getViewById(R.id.rl_progress);
                    }
                    vProgress.setVisibility(View.VISIBLE);
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
        super.onDestroy();
    }

    private Bitmap bitmapCache;

    @Override
    public void initView() {
        mBitmapTools = BitmapTools.getInstance(getActivity());
    }

    @Override
    public void requestData() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPic();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    BitmapTools mBitmapTools;

    DownloadRequest downloadRequest;

    protected void loadPic() {
        String pic_url = getArguments().getString("pic_url");
        iv_pic = (ImageView) getViewById(R.id.iv_pic);
        RelativeLayout btn_save_2_local = (RelativeLayout) getViewById(R.id.btn_save_2_local);
        btn_save_2_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapCache != null && !bitmapCache.isRecycled()) {
                    try {
                        PicturesCacheUtil.saveImageToGallery(getActivity(), new File
                                (cacheFilePath), "wall");
                    } catch (Exception e) {
                        MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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
            iv_pic.setImageResource(R.drawable.network_image_default);

            downloadRequest = new HttpTools(getContext()).download(App.getContextInstance(),
                    pic_url, PicturesCacheUtil.getCachePicPath(getActivity(), false), true, new
                            HttpCallback() {
                @Override
                public void onStart() {
                    if (iv_pic != null) {
                        iv_pic.setImageResource(R.drawable.network_image_default);
                    }
                    mHandler.sendEmptyMessage(SHOW_WAITTING);
                }

                @Override
                public void onFinish() {
                    mHandler.sendEmptyMessage(HIDE_WAITTING);
                }

                @Override
                public void onResult(String string) {

                    if (iv_pic != null) {
                        cacheFilePath = string;
                        bitmapCache = LocalImageLoader.loadBitmapFromFile(getActivity(), string,
                                iv_pic.getWidth(), iv_pic.getHeight());
                        iv_pic.setImageBitmap(bitmapCache);
                        PhotoViewAttacher attacher = new PhotoViewAttacher(iv_pic);
                        attacher.update();
                    }

                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onCancelled() {
                    if (iv_pic != null) {
                        iv_pic.setImageResource(R.drawable.network_image_default);
                    }
                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
    }

    private String cacheFilePath;

}
