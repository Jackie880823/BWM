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
import com.bondwithme.BondWithMe.util.SortMediaComparator;
import com.bondwithme.BondWithMe.widget.CustomGridView;
import com.bondwithme.BondWithMe.widget.DrawerArrowDrawable;
import com.bondwithme.BondWithMe.widget.NewtonCradleLoading;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jackie Zhu
 * @version 1.0
 */
public class SelectPhotosFragment extends BaseFragment<SelectPhotosActivity> {

    private static final String TAG = SelectPhotosFragment.class.getSimpleName();
    public static final String LOADER_IMAGE = "Loader Image";
    public static final String LOADER_VIDEO = "Loader Video";

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
    private HashMap<String, List<MediaData>> mMediaUris = new HashMap<>();
    /**
     * 当前显示的图片的Ur列表
     */
    private List<MediaData> mImageUriList = new ArrayList<>();

    /**
     * <p>是否可以选择多张图片的标识</p>
     * <p>true: 可以选择多张图片</p>
     * <p>false: 只允许选择一张图片</p>
     */
    private boolean multi;

    /**
     * 已经选择的图Ur列表
     */
    List<MediaData> mSelectedImageUris;
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

            if (localMediaAdapter != null) {
                selectImageUirListener.onLoadedMedia(mMediaUris.get(getContext().getString(R.string.text_all)), localMediaAdapter);
            }
        }
    };

    /**
     * 加栽图片Uri的字线程
     */
    private HandlerThread mLoadImageThread = new HandlerThread(LOADER_IMAGE);

    /**
     * 加栽图片Uri的字线程
     */
    private HandlerThread mLoadVideoThread = new HandlerThread(LOADER_VIDEO);

    private static WeakReference<List<MediaData>> weakReference;

    public SelectPhotosFragment() {
        super();
        mSelectedImageUris = weakReference.get();
        weakReference = null;
    }

    public static SelectPhotosFragment newInstance(List<MediaData> selectUris, String... params) {
        weakReference = new WeakReference<>(selectUris);
        return createInstance(new SelectPhotosFragment(), params);
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.activity_select_photo;
    }

    @Override
    protected void setParentTitle() {

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

        multi = getActivity().getIntent().getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        /*
      请求数据是否可以包含视频
     */
        boolean useVideo = getActivity().getIntent().getBooleanExtra(MediaData.USE_VIDEO_AVAILABLE, false);

        // 获取数据库中的图片资源游标
//        String[] imageColumns = {MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails._ID};
//        String imageOrderBy = MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER ;
//        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy).loadInBackground();

        String[] imageColumns = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED};
        String imageOrderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        String imageSelect = MediaStore.Images.Media.SIZE + ">0";
        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, imageSelect, null, imageOrderBy).loadInBackground();

        if (useVideo) {
            // 获取数据库中的视频资源游标
            String[] videoColumns = {MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns._ID, MediaStore.Video.Media.SIZE, MediaStore.Video.VideoColumns.DURATION, MediaStore.Video.Media.DATE_ADDED};
            String videoOrderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
            String select = String.format("%s <= %d and %s >= %d", MediaStore.Video.VideoColumns.SIZE, MediaData.MAX_SIZE, MediaStore.Video.VideoColumns.DURATION, 1000);
            videoCursor = new CursorLoader(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, select, null, videoOrderBy).loadInBackground();
        }

        // 查找到的数据条目总数,用于初始化显示所有的媒体数据的ArrayList的大小
        int dataCount = 0;
        if (imageCursor != null) {
            dataCount += imageCursor.getCount();
        }
        if (videoCursor != null) {
            dataCount += videoCursor.getCount();
        }
        String bucketsFirst = getActivity().getString(R.string.text_all);
        ArrayList<MediaData> allMedias;
        allMedias = new ArrayList<>(dataCount);
        mMediaUris.put(bucketsFirst, allMedias);

        buckets = new ArrayList<>();
        buckets.add(bucketsFirst);

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


        localMediaAdapter = new LocalMediaAdapter(getActivity(), mImageUriList);
        localMediaAdapter.setCheckBoxVisible(multi);
        localMediaAdapter.setDeleteMediaListListener(new LocalMediaAdapter.DeleteMediaListListener() {
            @Override
            public void deleteList(List<MediaData> list) {
                LogUtil.d(TAG, "deleteList: curLoaderPosition = " + curLoaderPosition);
                loadLocalMediaOrder(curLoaderPosition);
            }
        });
        mGvShowPhotos.setAdapter(localMediaAdapter);
    }

    /**
     * 初始化用于加载媒体URI的Handler
     */
    private void initHandler() {
        mLoadImageThread.start();
        mLoadVideoThread.start();
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
                        } finally {
                            quitLoadImageThread();
                        }
                        break;
                }
            }

        };
        Handler loadVideoHandler = new Handler(mLoadVideoThread.getLooper()) {
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
                            loadVideos();
                        } finally {
                            quitLoadVideoThread();
                        }
                        break;
                }
            }

        };

        loadImageHandler.sendEmptyMessage(HANDLER_LOAD_FLAG);
        loadVideoHandler.sendEmptyMessage(HANDLER_LOAD_FLAG);
    }


    /**
     * 加载图片的URI到内存列表
     */
    private void loadImages() {
        synchronized (LOADER_IMAGE) {
            if (imageCursor == null || imageCursor.isClosed()) {
                return;
            }

            int uriColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
            int bucketColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int pathColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int addedDateColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);

            while (imageCursor.moveToNext()) {
                String path;
                String bucket;
                long id;
                long addedDate;
                Uri contentUri;
                path = imageCursor.getString(pathColumnIndex);
                bucket = imageCursor.getString(bucketColumnIndex);
                id = imageCursor.getLong(uriColumnIndex);
                addedDate = imageCursor.getLong(addedDateColumnIndex);
                contentUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

                if (!TextUtils.isEmpty(path) && !path.contains(FileUtil.getCacheFilePath(getActivity(), true))) {
                    MediaData mediaData = new MediaData(contentUri, path, MediaData.TYPE_IMAGE, 0);
                    mediaData.setId(id);
                    mediaData.setAddedDate(addedDate);
                    addToMediaMap(bucket, mediaData);
                }
            }
            imageCursor.close();
        }

        refreshAdapter();
        LogUtil.d(TAG, "loadImages(), buckets size: " + buckets.size());
    }

    /**
     * 刷新Adapter
     */
    private void refreshAdapter() {
        if ((videoCursor == null || videoCursor.isClosed()) && (imageCursor == null || imageCursor.isClosed())) {

            List<MediaData> allMedias = mMediaUris.get(getContext().getString(R.string.text_all));
            SortMediaComparator comparator = new SortMediaComparator();
            Collections.sort(allMedias, comparator);

            getActivity().runOnUiThread(adapterRefresh);
        }
    }

    /**
     * 加载视频的URI到内存列表
     */
    private void loadVideos() {
        synchronized (LOADER_VIDEO) {
            if (videoCursor != null && !videoCursor.isClosed() && videoCursor.getCount() > 0) {

                String bucket;
                bucket = getContext().getString(R.string.text_video);
                buckets.add(1, bucket);
                mMediaUris.put(bucket, new ArrayList<MediaData>(videoCursor.getCount()));

                int uriColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int durationColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                int addedDateColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED);

                while (videoCursor.moveToNext()) {
                    String path;
                    Uri contentUri;
                    long duration;
                    long id;
                    long addedDate;

                    duration = videoCursor.getLong(durationColumnIndex);
                    path = videoCursor.getString(pathColumnIndex);
                    id = videoCursor.getLong(uriColumnIndex);
                    addedDate = videoCursor.getLong(addedDateColumnIndex);
                    contentUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

                    if (!TextUtils.isEmpty(path)) {
                        MediaData mediaData = new MediaData(contentUri, path, MediaData.TYPE_VIDEO, duration);
                        mediaData.setAddedDate(addedDate);
                        mediaData.setId(id);
                        addToMediaMap(bucket, mediaData);
                    }

                }
                videoCursor.close();
            }
        }

        refreshAdapter();
        LogUtil.d(TAG, "loadVideos(), buckets size: " + buckets.size());
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

    private void quitAllLoadThread() {
        quitLoadImageThread();
        quitLoadVideoThread();
    }

    /**
     * 关闭加载的线程
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void quitLoadVideoThread() {
        // 关闭数据库游标
        synchronized (LOADER_VIDEO) {
            if (videoCursor != null && !videoCursor.isClosed()) {
                videoCursor.close();
            }
        }

        if (mLoadVideoThread.getState() != Thread.State.TERMINATED) {
            // 关闭加载视频线程
            if (Utils.hasJellyBeanMR2()) {
                mLoadVideoThread.quitSafely();
            } else {
                mLoadVideoThread.quit();
            }
        }
    }

    private void quitLoadImageThread() {

        // 关闭数据库游标
        synchronized (LOADER_IMAGE) {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
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
        List<MediaData> allMedias = mMediaUris.get(getContext().getString(R.string.text_all));
        allMedias.add(mediaData);
        if (allMedias.size() == 50) {
            SortMediaComparator comparator = new SortMediaComparator();
            Collections.sort(allMedias, comparator);
        }

        if (mMediaUris.containsKey(bucket)) {
            mMediaUris.get(bucket).add(mediaData);
        } else {
            LogUtil.i(TAG, "addToMediaMap: add map to:" + bucket + ";");
            ArrayList<MediaData> al = new ArrayList<>();
            al.add(mediaData);
            mMediaUris.put(bucket, al);
            buckets.add(bucket);
            getActivity().runOnUiThread(uiRunnable);
        }
    }

    public void insertMedia(String bucket, MediaData mediaData) {
        List<MediaData> allMedias = mMediaUris.get(getContext().getString(R.string.text_all));
        allMedias.add(0, mediaData);

        if (mMediaUris.containsKey(bucket)) {
            mMediaUris.get(bucket).add(0, mediaData);
        } else {
            LogUtil.i(TAG, "addToMediaMap: add map to:" + bucket + ";");
            List<MediaData> al = new ArrayList<>();
            al.add(mediaData);
            mMediaUris.put(bucket, al);
            buckets.add(bucket);

            updateBucketsAdapter();
        }

        loadLocalMediaOrder(curLoaderPosition);
    }

    private ArrayAdapter<String> bucketsAdapter;

    /**
     * 初始化目录图片列表的adapter
     */
    private void updateBucketsAdapter() {
        if (bucketsAdapter == null) {
            ArrayList<String> data = new ArrayList<>();
            data.addAll(buckets);
            bucketsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, data);
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
        curLoaderPosition = index;
        if (index < buckets.size() && index >= 0) {
            String bucket = buckets.get(index);
            if (index == 1 && bucket.equals(getString(R.string.text_video))) {
                MessageUtil.showMessage(getActivity(), getActivity().getString(R.string.show_video_limit));
            }

            mImageUriList = mMediaUris.get(bucket);
            if (mImageUriList.size() <= 0) {
                mMediaUris.remove(bucket);
                buckets.remove(index);
                bucketsAdapter.remove(bucket);
                loadLocalMediaOrder(index - 1);
                return;
            }

            LogUtil.i(TAG, "mImageUriList size = " + mImageUriList.size() + "; bucket " + bucket);

            localMediaAdapter.setData(mImageUriList);
            localMediaAdapter.notifyDataSetChanged();
            localMediaAdapter.setSelectedImages(mSelectedImageUris);

            mGvShowPhotos.setSelection(0);
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
