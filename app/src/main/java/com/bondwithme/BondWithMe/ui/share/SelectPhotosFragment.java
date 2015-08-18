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

import com.android.volley.Utils;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.LocalMediaAdapter;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.widget.CustomGridView;
import com.bondwithme.BondWithMe.widget.DrawerArrowDrawable;

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
        public void run() {
            LogUtil.i(TAG, "uiRunnable& update adapter");
            updateBucketsAdapter();
            if(buckets.size() > 0 && lastPosition != curLoaderPosition) {
                LogUtil.i(TAG, "uiRunnable& loadLocalMediaOrder");
                // 查找到了图片显示列表第一项的图片
                loadLocalMediaOrder(curLoaderPosition);
                lastPosition = curLoaderPosition;
            }
        }
    };

    /**
     * 加栽图片Uri的字线程
     */
    private HandlerThread mLoadThread = new HandlerThread("Loader Image");

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

        multi = getParentActivity().getIntent().getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        boolean useVideo = getParentActivity().getIntent().getBooleanExtra(MediaData.USE_VIDEO_AVAILABLE, false);

        // 获取数据库中的图片资源游标
        String[] imageColumns = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        String imageOrderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy).loadInBackground();

        if(useVideo) {
            // 获取数据库中的视频资源游标
            String[] videoColumns = {MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, MediaStore.Video.Media.SIZE};
            String videoOrderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
            videoCursor = new CursorLoader(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, videoOrderBy).loadInBackground();
        }

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
                if(slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(true);
                } else if(slideOffset <= .005) {
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
                mDrawerLayout.closeDrawer(mDrawerList);

                if(curLoaderPosition != position) {
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

                if(selectImageUirListener != null) {
                    MediaData itemUri = mImageUriList.get(position);
                    if(multi) {
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
        mLoadThread.start();
        Handler mLoadHandler = new Handler(mLoadThread.getLooper()) {
            /**
             * Subclasses must implement this to receive messages.
             *
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                    case HANDLER_LOAD_FLAG:
                        String bucketsFirst = getParentActivity().getString(R.string.text_nearest);
                        ArrayList<MediaData> recent;
                        recent = new ArrayList<>();
                        buckets = new ArrayList<>();
                        buckets.add(bucketsFirst);
                        mMediaUris.put(bucketsFirst, recent);

                        loadImages();
                        loadVideos();
                        break;
                }
            }

            /**
             * 加载图片的URI到内存列表
             */
            private void loadImages() {
                if(imageCursor == null) {
                    return;
                }

                int cursorCount = imageCursor.getCount();
                for(int i = 0; i < cursorCount; i++) {
                    String path;
                    String bucket;
                    Uri contentUri;
                    synchronized(SelectPhotosFragment.this) {
                        if(imageCursor.isClosed()) {
                            return;
                        }

                        imageCursor.moveToPosition(i);
                        int uriColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        int bucketColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                        int pathColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        path = imageCursor.getString(pathColumnIndex);
                        bucket = imageCursor.getString(bucketColumnIndex);
                        contentUri = Uri.parse("content://media/external/images/media/" + imageCursor.getInt(uriColumnIndex));
                    }

                    if(!TextUtils.isEmpty(path)) {
                        MediaData mediaData = new MediaData(contentUri, path, MediaData.TYPE_IMAGE);
                        addToMediaMap(bucket, mediaData);
                    }
                }
                imageCursor.close();
                LogUtil.d(TAG, "loadImages(), buckets size: " + buckets.size());
            }

            /**
             * 加载视频的URI到内存列表
             */
            private void loadVideos() {
                if(videoCursor == null) {
                    return;
                }

                String bucket;
                bucket = getActivity().getString(R.string.text_video);
                buckets.add(1, bucket);
                mMediaUris.put(bucket, new ArrayList<MediaData>());
                int cursorCount = videoCursor.getCount();

                // 限制定显示的视频大小的最大数为50M
                long maxSize = 50 * 1024 * 1024;
                for(int i = 0; i < cursorCount; i++) {
                    String path;
                    Uri contentUri;
                    synchronized(SelectPhotosFragment.this) {
                        if(videoCursor.isClosed()) {
                            return;
                        }

                        videoCursor.moveToPosition(i);
                        long size = videoCursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                        if(size > maxSize) {
                            // 视频大小超过限定不添加该视频到列表，执行下一循环
                            continue;
                        }

                        int uriColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                        int pathColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
                        path = videoCursor.getString(pathColumnIndex);
                        contentUri = Uri.parse("content://media/external/video/media/" + videoCursor.getInt(uriColumnIndex));
                    }

                    if(!TextUtils.isEmpty(path)) {
                        MediaData mediaData = new MediaData(contentUri, path, MediaData.TYPE_VIDEO);
                        addToMediaMap(bucket, mediaData);
                    }
                }
                imageCursor.close();
                LogUtil.d(TAG, "loadImages(), buckets size: " + buckets.size());
            }
        };

        mLoadHandler.sendEmptyMessage(HANDLER_LOAD_FLAG);
    }

    public boolean changeDrawer() {
        if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return false;
        } else {
            mDrawerLayout.openDrawer(mDrawerList);
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {
        super.onDestroy();

        // 退出Activity关闭数据库游标
        synchronized(SelectPhotosFragment.this) {
            if(imageCursor != null) {
                if(!imageCursor.isClosed()) {
                    imageCursor.close();
                }
            }
        }

        // 关闭加载图片线程
        if(Utils.hasJellyBeanMR2()) {
            mLoadThread.quitSafely();
        } else {
            mLoadThread.quit();
        }
    }

    /**
     * Adds the url or path to the bucket if it exists, and if not, creates it and adds the url/path.
     *
     * @param bucket  path
     * @param mediaData data about of url
     */
    private void addToMediaMap(String bucket, MediaData mediaData) {
        ArrayList<MediaData> nearest = mMediaUris.get(getParentActivity().getString(R.string.text_nearest));
        if(nearest.size() < 30) {
            nearest.add(mediaData);
        }
        if(mMediaUris.containsKey(bucket)) {
            mMediaUris.get(bucket).add(mediaData);
        } else {
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
        if(bucketsAdapter == null) {
            ArrayList<String> data = new ArrayList<>();
            data.addAll(buckets);
            bucketsAdapter = new ArrayAdapter<>(getParentActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, data);
            mDrawerList.setAdapter(bucketsAdapter);
        } else {
            if(buckets.size() > bucketsAdapter.getCount()) {
                bucketsAdapter.add(buckets.get(buckets.size() - 1));
            }
        }
    }

    /**
     * 加载目录列表指定index项的媒体目录中的媒体
     */
    private void loadLocalMediaOrder(int index) {
        LogUtil.i(TAG, "index = " + index + "; buckets length " + buckets.size());
        if(index < buckets.size() && index >= 0) {
            String bucket = buckets.get(index);
            mImageUriList.clear();
            mImageUriList.addAll(mMediaUris.get(bucket));
            LogUtil.i(TAG, "mImageUriList size = " + mImageUriList + "; bucket " + bucket);
            if(localMediaAdapter == null) {
                localMediaAdapter = new LocalMediaAdapter(getActivity(), mImageUriList);
                localMediaAdapter.setCheckBoxVisible(multi);
                localMediaAdapter.setSelectedImages(mSelectedImageUris);
                localMediaAdapter.setListener(selectImageUirListener);
                mGvShowPhotos.setAdapter(localMediaAdapter);
            } else {
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
