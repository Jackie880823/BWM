package com.bondwithme.BondWithMe.ui.share;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Utils;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.LocalMediaAdapter;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.CustomGridView;
import com.bondwithme.BondWithMe.widget.DrawerArrowDrawable;
import com.bondwithme.BondWithMe.widget.NewtonCradleLoading;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Jackie Zhu
 * @version 1.0
 */
public class SelectPhotosFragment extends BaseFragment<SelectPhotosActivity> {

    private static final String TAG = SelectPhotosFragment.class.getSimpleName();

    /**
     * 侧滑出来的目录列表的ListView
     */
    private ListView mDrawerList;

    private RelativeLayout rlLeftList;
    private NewtonCradleLoading loading;
    /**
     * 显示图片的目录控件
     */
    private CustomGridView mGvShowPhotos;
    private DrawerLayout mDrawerLayout;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private HashMap<String, ArrayList<MediaData>> mMediaUris = new HashMap<>();
    /**
     * 当前显示的图片的Ur列表
     */
    private ArrayList<MediaData> mImageUriList = new ArrayList<>();

    /**
     * <p>是否可以选择多张图片的标识</p>
     * <p>true: 可以选择多张图片</p>
     * <p>false: 只允许选择一张图片</p>
     */
    private boolean multi;

    /**
     * 已经选择的图Ur列表
     */
    ArrayList<MediaData> mSelectedImageUris;
    /**
     * 目录列表
     */
    private ArrayList<String> buckets;
    private int curLoaderPosition = 0;

    /**
     * 手机数据库中指向图库的游标
     */
    private Cursor imageCursor;

    private Cursor videoCursor;

    /**
     * 本地图片显示adapter
     */
    private LocalMediaAdapter localMediaAdapter;

    /**
     * 传递加载图片Uri的消息
     */
    private static final int HANDLER_LOAD_FLAG = 100;

    /**
     * post到UI线程中的更新图片的Runnable
     */
    private Runnable uiRunnable = new Runnable() {
        private int lastPosition = -1;

        @Override
        public synchronized void run() {
            LogUtil.i(TAG, "uiRunnable& update adapter");
            updateBucketsAdapter();
            if (buckets.size() > 0 && lastPosition != curLoaderPosition) {
                LogUtil.i(TAG, "uiRunnable& loadLocalMediaOrder");
                // 查找到了图片显示列表第一项的图片
                loadLocalMediaOrder(curLoaderPosition);
                lastPosition = curLoaderPosition;
            }
        }
    };

    private Runnable adapterRefresh = new Runnable() {
        @Override
        public void run() {
            if (bucketsAdapter != null) {
                bucketsAdapter.clear();
                bucketsAdapter.addAll(buckets);
            }
            loading.stop();
            loading.setVisibility(View.GONE);
        }
    };

    /**
     * 加栽图片Uri的字线程
     */
    private HandlerThread mLoadImageThread = new HandlerThread("Loader Image");

    public SelectPhotosFragment(ArrayList<MediaData> selectUris) {
        super();
        mSelectedImageUris = selectUris;
    }

    public static SelectPhotosFragment newInstance(ArrayList<MediaData> selectUris, String... params) {
        return createInstance(new SelectPhotosFragment(selectUris), params);
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.activity_select_photo;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void initView() {
        mDrawerList = getViewById(R.id.lv_images_titles);
        mGvShowPhotos = getViewById(R.id.gv_select_photo);
        mDrawerLayout = getViewById(R.id.drawer_layout);
        rlLeftList = getViewById(R.id.rl_left_list);
        loading = getViewById(R.id.ncl_loading);
        loading.start();

        multi = getParentActivity().getIntent().getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        boolean useVideo = getParentActivity().getIntent().getBooleanExtra(MediaData.USE_VIDEO_AVAILABLE, false);

        // 获取数据库中的图片资源游标
//        String[] imageColumns = {MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails._ID};
//        String imageOrderBy = MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER ;
//        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy).loadInBackground();

        String[] imageColumns = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        String imageOrderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        String imageSelect = MediaStore.Images.Media.SIZE + ">0";
        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, imageSelect, null, imageOrderBy).loadInBackground();

        if (useVideo) {
            // 获取数据库中的视频资源游标
            String[] videoColumns = {MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns._ID, MediaStore.Video.Media.SIZE, MediaStore.Video.VideoColumns.DURATION};
            String videoOrderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
            String select = String.format("%s <= %d and %s >= %d", MediaStore.Video.VideoColumns.SIZE, MediaData.MAX_SIZE, MediaStore.Video.VideoColumns.DURATION, 1000);
            videoCursor = new CursorLoader(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, select, null, videoOrderBy).loadInBackground();
        }


        String bucketsFirst = getParentActivity().getString(R.string.text_all);
        ArrayList<MediaData> recent;
        recent = new ArrayList<>();
        buckets = new ArrayList<>();
        buckets.add(bucketsFirst);
        mMediaUris.put(bucketsFirst, recent);

        initHandler();

        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawer_arrow_color));
        ImageButton imageButton = getParentActivity().getViewById(R.id.ib_top_button_left);
        imageButton.setImageDrawable(drawerArrowDrawable);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(true);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(false);
                }

                drawerArrowDrawable.setParameter(offset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerArrowDrawable = new DrawerArrowDrawable(resources, true);
                drawerArrowDrawable.setParameter(offset);
                drawerArrowDrawable.setFlip(flipped);
                drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawer_arrow_color));
                selectImageUirListener.onDrawerOpened(drawerArrowDrawable);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerArrowDrawable = new DrawerArrowDrawable(resources, false);
                drawerArrowDrawable.setParameter(offset);
                drawerArrowDrawable.setFlip(flipped);
                drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawer_arrow_color));
                selectImageUirListener.onDrawerClose(drawerArrowDrawable);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 关闭目录选择列表
                mDrawerLayout.closeDrawer(rlLeftList);

                if (curLoaderPosition != position) {
                    curLoaderPosition = position;
                    // 加载选中目录下的图片
                    loadLocalMediaOrder(curLoaderPosition);
                }
            }
        });

        mGvShowPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i(TAG, "onItemClick& position: " + position);

                if (selectImageUirListener != null) {
                    MediaData itemUri = mImageUriList.get(position);
                    if (multi) {
                        selectImageUirListener.preview(itemUri);
                    } else {
                        selectImageUirListener.addUri(itemUri);
                    }
                }
            }
        });
    }

    /**
     * 初始化用于加载媒体URI的Handler
     */
    private void initHandler() {
        mLoadImageThread.start();
        Handler loadImageHandler = new Handler(mLoadImageThread.getLooper()) {
            /**
             * Subclasses must implement this to receive messages.
             *
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_LOAD_FLAG:

                        // 线程没执行完退出程序会出现资源引用无法使用的异常，捕获后关闭线程，释放资源
                        try {
                            loadImages();
                            loadVideos();
                        } finally {
                            quitAllLoadThread();
                        }
                        break;
                }
            }

            /**
             * 加载图片的URI到内存列表
             */
            private void loadImages() {
                synchronized (SelectPhotosFragment.this) {
                    if (imageCursor == null || imageCursor.isClosed()) {
                        return;
                    }

                    int cursorCount = imageCursor.getCount();
                    LogUtil.i(TAG, "loadImages& cursorCount: " + cursorCount);
                    for (int i = 0; i < cursorCount; i++) {
                        String path;
                        String bucket;
                        long id;
                        Uri contentUri;

                        imageCursor.moveToPosition(i);
                        int uriColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        int bucketColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                        int pathColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        path = imageCursor.getString(pathColumnIndex);
                        bucket = imageCursor.getString(bucketColumnIndex);
                        id = imageCursor.getLong(uriColumnIndex);
                        contentUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

                        if (!TextUtils.isEmpty(path) && !path.contains(FileUtil.getCacheFilePath(getActivity()))) {
                            MediaData mediaData = new MediaData(contentUri, path, MediaData.TYPE_IMAGE, 0);
                            mediaData.setThumbnailUri(getImageThumbnailUri(id));
                            addToMediaMap(bucket, mediaData);
                            //wing
//                            int id = imageCursor.getInt(uriColumnIndex);
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inDither = false;
//                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                            imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy).loadInBackground();
//                            info.b = MediaStore.Video.Thumbnails.getContentUri()getThumbnail(getActivity().getContentResolver(), id,  MediaStore.Images.Thumbnails.MICRO_KIND, options);
                        }
                    }
                    imageCursor.close();
                }

                LogUtil.d(TAG, "loadImages(), buckets size: " + buckets.size());
            }

            /**
             * 加载视频的URI到内存列表
             */
            private void loadVideos() {
                synchronized (SelectPhotosFragment.this) {
                    if (videoCursor == null || videoCursor.isClosed()) {
                        return;
                    }

                    String bucket;
                    bucket = getParentActivity().getString(R.string.text_video);
                    buckets.add(1, bucket);

                    if (videoCursor.isClosed()) {
                        return;
                    }

                    mMediaUris.put(bucket, new ArrayList<MediaData>());
                    int cursorCount = videoCursor.getCount();

                    // 限制定显示的视频大小的最大数为50M
                    for (int i = 0; i < cursorCount; i++) {
                        String path;
                        Uri contentUri;
                        long duration;
                        long id;

                        videoCursor.moveToPosition(i);

                        int uriColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                        int pathColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                        int durationColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                        duration = videoCursor.getLong(durationColumnIndex);
                        path = videoCursor.getString(pathColumnIndex);
                        id = videoCursor.getLong(uriColumnIndex);
                        contentUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

                        if (!TextUtils.isEmpty(path)) {
                            MediaData mediaData = new MediaData(contentUri, path, MediaData.TYPE_VIDEO, duration);
                            mediaData.setThumbnailUri(getVideoThumbnailUri(id));
                            addToMediaMap(bucket, mediaData);
                        }

                    }
                    imageCursor.close();
                }

                getParentActivity().runOnUiThread(adapterRefresh);
                LogUtil.d(TAG, "loadVideos(), buckets size: " + buckets.size());
            }

        };

        loadImageHandler.sendEmptyMessage(HANDLER_LOAD_FLAG);
    }

    /**
     * 获取小图的{@link Uri}
     *
     * @param origId 原图{@value android.provider.MediaStore.Images.Media#_ID}
     * @return 返回小图的Uri
     */
    private Uri getImageThumbnailUri(long origId) {
        Uri result = null;

        Log.i(TAG, "getImageThumbnailUri& origId: " + origId);
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(getParentActivity().getContentResolver(), origId, MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            File file = new File(path);
            if (file.exists() && !file.isDirectory() && file.canRead()) {
                result = Uri.parse(ImageDownloader.Scheme.FILE.wrap(path));
            }
        }

        try {
            if (cursor != null) {
                // 关闭游标
                cursor.close();
            }
        } finally {
            Log.i(TAG, "getImageThumbnailUri& result: " + result);
            return result;
        }
    }

    /**
     * 获取小图的{@link Uri}
     *
     * @param origId 视频{@value android.provider.MediaStore.Video.Media#_ID}
     * @return 返回小图的Uri
     */
    private Uri getVideoThumbnailUri(long origId) {
        Uri result = null;

        Log.i(TAG, "getVideoThumbnailUri& origId: " + origId);
        String[] columns = {MediaStore.Images.Thumbnails.DATA};
        String select = MediaStore.Video.Thumbnails.VIDEO_ID + " = " + origId;
        Cursor cursor = new CursorLoader(getParentActivity(), MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, columns, select, null, null).loadInBackground();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            File file = new File(path);
            if (file.exists() && !file.isDirectory() && file.canRead()) {
                result = Uri.parse(ImageDownloader.Scheme.FILE.wrap(path));
            }
        }

        try {
            if (cursor != null) {
                // 关闭游标
                cursor.close();
            }
        } finally {
            Log.i(TAG, "getVideoThumbnailUri& result: " + result);
            return result;
        }
    }

    public boolean changeDrawer() {
        if (mDrawerLayout.isDrawerOpen(rlLeftList)) {
            mDrawerLayout.closeDrawer(rlLeftList);
            return false;
        } else {
            mDrawerLayout.openDrawer(rlLeftList);
            return true;
        }
    }

    @Override
    public void requestData() {

    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        quitAllLoadThread();
    }

    /**
     * 关闭加载的线程
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void quitAllLoadThread() {
        // 关闭数据库游标
        synchronized (SelectPhotosFragment.this) {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }

            if (videoCursor != null && !videoCursor.isClosed()) {
                videoCursor.close();
            }
        }

        if (mLoadImageThread.getState() != Thread.State.TERMINATED) {
            // 关闭加载图片线程
            if (Utils.hasJellyBeanMR2()) {
                mLoadImageThread.quitSafely();
            } else {
                mLoadImageThread.quit();
            }
        }
    }

    /**
     * Adds the url or path to the bucket if it exists, and if not, creates it and adds the url/path.
     *
     * @param bucket    path
     * @param mediaData data about of url
     */
    private synchronized void addToMediaMap(String bucket, MediaData mediaData) {
        ArrayList<MediaData> nearest = mMediaUris.get(getParentActivity().getString(R.string.text_all));
        if (MediaData.TYPE_IMAGE.equals(mediaData.getType())) {
            nearest.add(mediaData);
        }
        if (mMediaUris.containsKey(bucket)) {
            mMediaUris.get(bucket).add(mediaData);
        } else {
            LogUtil.i(TAG, "addToMediaMap: add map to:" + bucket + ";");
            ArrayList<MediaData> al = new ArrayList<>();
            al.add(mediaData);
            mMediaUris.put(bucket, al);
            buckets.add(bucket);
            getParentActivity().runOnUiThread(uiRunnable);
        }
    }

    private ArrayAdapter<String> bucketsAdapter;

    /**
     * 初始化目录图片列表的adapter
     */
    private void updateBucketsAdapter() {
        if (bucketsAdapter == null) {
            ArrayList<String> data = new ArrayList<>();
            data.addAll(buckets);
            bucketsAdapter = new ArrayAdapter<>(getParentActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, data);
            mDrawerList.setAdapter(bucketsAdapter);
        } else {
            int count = bucketsAdapter.getCount();
            if (buckets.size() > count) {
                String name = buckets.get(count);
                LogUtil.i(TAG, "buckets add to adapter: " + name);
                bucketsAdapter.add(name);
            }
        }
    }

    /**
     * 加载目录列表指定index项的媒体目录中的媒体
     */
    private void loadLocalMediaOrder(int index) {
        LogUtil.i(TAG, "index = " + index + "; buckets length " + buckets.size());
        if (index < buckets.size() && index >= 0) {
            String bucket = buckets.get(index);

            if (index == 1 && bucket.equals(getString(R.string.text_video))) {
                MessageUtil.showMessage(getActivity(), getActivity().getString(R.string.show_vidoe_limit));
            }

            mImageUriList = mMediaUris.get(bucket);
            LogUtil.i(TAG, "mImageUriList size = " + mImageUriList.size() + "; bucket " + bucket);
            if (localMediaAdapter == null) {
                localMediaAdapter = new LocalMediaAdapter(getActivity(), mImageUriList);
                localMediaAdapter.setCheckBoxVisible(multi);
                localMediaAdapter.setSelectedImages(mSelectedImageUris);
                localMediaAdapter.setListener(selectImageUirListener);
                mGvShowPhotos.setAdapter(localMediaAdapter);
            } else {
                localMediaAdapter.setData(mImageUriList);
                localMediaAdapter.notifyDataSetChanged();
            }

            mDrawerList.setSelection(index);
        } else {
            LogUtil.i(TAG, "index out of buckets");
        }

    }

    private SelectImageUirChangeListener selectImageUirListener;

    public void setSelectImageUirListener(SelectImageUirChangeListener selectImageUirListener) {
        this.selectImageUirListener = selectImageUirListener;
    }

}
