package com.bondwithme.BondWithMe.ui.wall;

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

import com.android.volley.Utils;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.LocalImagesAdapter;
import com.bondwithme.BondWithMe.entity.ImageData;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.widget.CustomGridView;

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
    private HashMap<String, ArrayList<ImageData>> mImageUris = new HashMap<>();
    /**
     * 当前显示的图片的Ur列表
     */
    private ArrayList<ImageData> mImageUriList = new ArrayList<>();

    private boolean multi;

    /**
     * 已经选择的图Ur列表
     */
    ArrayList<ImageData> mSelectedImageUris;
    /**
     * 目录列表
     */
    private ArrayList<String> buckets;
    private int curLoaderPosition = 0;
    private Cursor imageCursor;

    /**
     * 本地图片显示adapter
     */
    private LocalImagesAdapter localImagesAdapter;

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
            Log.i(TAG, "uiRunnable& update adapter");
            updateBucketsAdapter();
            if(buckets.size() > 0 && lastPosition != curLoaderPosition) {
                Log.i(TAG, "uiRunnable& loadLocalImageOrder");
                // 查找到了图片显示列表第一项的图片
                loadLocalImageOrder(curLoaderPosition);
                lastPosition = curLoaderPosition;
            }
        }
    };

    /**
     * 加栽图片Uri的字线程
     */
    private HandlerThread mLoadThread = new HandlerThread("Loader Image");

    public SelectPhotosFragment(ArrayList<ImageData> selectUris) {
        super();
        mSelectedImageUris = selectUris;
    }

    public static final SelectPhotosFragment newInstance(ArrayList<ImageData> selectUris, String... params) {
        return createInstance(new SelectPhotosFragment(selectUris), params);
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.activity_select_photo;
    }

    @Override
    public void initView() {
        mDrawerList = getViewById(R.id.lv_images_titles);
        mGvShowPhotos = getViewById(R.id.gv_select_photo);
        mDrawerLayout = getViewById(R.id.drawer_layout);

        String[] columns = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy).loadInBackground();
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
                    drawerArrowDrawable.setFlip(flipped);
                } else if(slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
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
                    loadLocalImageOrder(curLoaderPosition);
                }
            }
        });

        mGvShowPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick& position: " + position);

                if(selectImageUirListener != null) {
                    ImageData itemUri = mImageUriList.get(position);
                    if(multi) {
                        selectImageUirListener.preview(itemUri);
                    } else {
                        selectImageUirListener.addUri(itemUri);
                    }
                }
            }
        });

        multi = getParentActivity().getIntent().getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }

    /**
     * 初始化用于加载图片URI的Handler
     */
    private void initHandler() {
        mLoadThread.start();
        Handler mLoadHandler = new Handler(mLoadThread.getLooper()) {
            /**
             * Subclasses must implement this to receive messages.
             *
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                    case HANDLER_LOAD_FLAG:
                        if(imageCursor == null) {
                            return;
                        }

                        String bucketsFirst = getParentActivity().getString(R.string.text_nearest);
                        ArrayList<ImageData> nearest = new ArrayList<>();
                        buckets = new ArrayList<>();
                        buckets.add(bucketsFirst);
                        mImageUris.put(bucketsFirst, nearest);

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
                                ImageData imageData = new ImageData(contentUri, path);
                                addToImageMap(bucket, imageData);
                            }
                        }
                        imageCursor.close();
                        Log.d(TAG, "loadImages(), buckets size: " + buckets.size());
                        break;
                }
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
     * @param bucket
     * @param imageData
     */
    private void addToImageMap(String bucket, ImageData imageData) {
        ArrayList<ImageData> nearest = mImageUris.get(getParentActivity().getString(R.string.text_nearest));
        if(nearest.size() < 30) {
            nearest.add(imageData);
        }
        if(mImageUris.containsKey(bucket)) {
            mImageUris.get(bucket).add(imageData);
        } else {
            ArrayList<ImageData> al = new ArrayList<>();
            al.add(imageData);
            mImageUris.put(bucket, al);
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
     * 加载目录列表指定index项的图片目录中的图片
     *
     */
    private void loadLocalImageOrder(int index) {
        Log.i(TAG, "index = " + index + "; buckets length " + buckets.size());
        if(index < buckets.size() && index >= 0) {
            String bucket = buckets.get(index);
            mImageUriList.clear();
            mImageUriList.addAll(mImageUris.get(bucket));
            Log.i(TAG, "mImageUriList size = " + mImageUriList + "; bucket " + bucket);
            if(localImagesAdapter == null) {
                localImagesAdapter = new LocalImagesAdapter(getActivity(), mImageUriList, getParentActivity().getActionBarColor());
                localImagesAdapter.setCheckBoxVisible(multi);
                localImagesAdapter.setSelectedImages(mSelectedImageUris);
                localImagesAdapter.setListener(selectImageUirListener);
                mGvShowPhotos.setAdapter(localImagesAdapter);
                localImagesAdapter.setColumnWidthHeight(mGvShowPhotos.getColumnWidth());
            } else {
                localImagesAdapter.notifyDataSetChanged();
            }

            mDrawerList.setSelection(index);
        } else {
            Log.i(TAG, "index out of buckets");
        }

    }

    private SelectImageUirChangeListener selectImageUirListener;

    public void setSelectImageUirListener(SelectImageUirChangeListener selectImageUirListener) {
        this.selectImageUirListener = selectImageUirListener;
    }

}
