package com.madx.bwm.ui.wall;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.madx.bwm.R;
import com.madx.bwm.adapter.LocalImagesAdapter;
import com.madx.bwm.ui.BaseFragment;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.CustomGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Jackie Zhu
 */
public class SelectPhotosFragment extends BaseFragment<SelectPhotosActivity> implements LoaderManager.LoaderCallbacks<Void> {

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
    private HashMap<String, ArrayList<Uri>> mImageUris = new HashMap<>();
    /**
     * 当前显示的图片的Ur列表
     */
    ArrayList<Uri> mImageUriList = new ArrayList();

    ArrayList<Uri> mSelecedImageUris = new ArrayList();
    /**
     * 目录列表
     */
    private String[] buckets;
    private int curLoaderPosition = 0;
    private Cursor imageCursor;
    private int loaderCounter = 1;

    private int residue;
    /**
     * 本地图片显示adapter
     */
    private LocalImagesAdapter localImagesAdapter;

    public static final SelectPhotosFragment newInstance(String... params) {
        return createInstance(new SelectPhotosFragment(), params);
    }

    /**
     * Takes a Set of Strings, sorts it, and returns a String array.
     *
     * @param keySet
     * @return
     */
    private static String[] setToOrderedStringArray(Set<String> keySet) {
        int i = 0;
        ArrayList<String> keys = new ArrayList<>(keySet);
        Collections.sort(keys);
        String[] out = new String[keys.size()];
        for(String key : keys) {
            out[i++] = key;
        }
        return out;
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.activity_select_photo;
    }

    @Override
    public void initView() {
        residue = getParentActivity().getIntent().getIntExtra(TabPictureFragment.RESIDUE, TabPictureFragment.MAX_SELECT);
        mDrawerList = getViewById(R.id.lv_images_titles);
        mGvShowPhotos = getViewById(R.id.gv_select_photo);
        mDrawerLayout = getViewById(R.id.drawer_layout);

        String[] columns = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        imageCursor = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy).loadInBackground();
        getLoaderManager().initLoader(loaderCounter++, null, this);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                selectImageUirListener.onDrawerOpened();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                selectImageUirListener.onDrawerClose();
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

                if(curLoaderPosition != position){
                    curLoaderPosition = position;
                    // 加载选中目录下的图片
                    loadLocalImageOrder(curLoaderPosition);
                }
            }
        });

        mGvShowPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox check = (CheckBox) view.findViewById(R.id.select_image_right);
                Log.i(TAG, "onItemClick " + check.isChecked());

                Uri itemUri = mImageUriList.get(position);
                if(check.isChecked()) {
                    check.setChecked(false);
                    mSelecedImageUris.remove(itemUri);
                    //
                    if(selectImageUirListener != null) {
                        selectImageUirListener.onChange(itemUri, false);
                    }
                } else {
                    if(mSelecedImageUris.size() < residue) {
                        check.setChecked(true);
                        mSelecedImageUris.add(itemUri);
                        if(selectImageUirListener != null) {
                            selectImageUirListener.onChange(itemUri, true);
                        }
                    } else {
                        MessageUtil.showMessage(getActivity(), String.format(getActivity().getString(R.string.select_too_many), TabPictureFragment.MAX_SELECT));
                    }
                }

            }
        });
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
     * Adds the url or path to the bucket if it exists, and if not, creates it and adds the url/path.
     *
     * @param bucket
     * @param uri
     */
    private void addToImageMap(String bucket, Uri uri) {
        if(mImageUris.containsKey(bucket)) {
            mImageUris.get(bucket).add(uri);
        } else {
            ArrayList<Uri> al = new ArrayList<>();
            al.add(uri);
            mImageUris.put(bucket, al);
        }
    }

    /**
     * This will load all Images on the phone.
     */
    private void loadPhoneImages() {
        for(int i = 0; i < imageCursor.getCount(); i++) {
            imageCursor.moveToPosition(i);
            int index = imageCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
            int bucketColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            String bucket = imageCursor.getString(bucketColumnIndex);
            addToImageMap(bucket, Uri.parse("content://media/external/images/media/" + imageCursor.getInt(index)));

        }
        imageCursor.close();
        buckets = setToOrderedStringArray(mImageUris.keySet());
        Log.d(TAG, "loadImages(), buckets.length: " + buckets.length);
    }

    /**
     * 初始化目录图片列表的adapter
     */
    private void initBucketsAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getParentActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, buckets);
        mDrawerList.setAdapter(adapter);
    }

    /**
     * 加载目录列表指定inde项的图片目录中的图片
     *
     * @param index 图片目录bucket的inde项的
     */
    private void loadLocalImageOrder(int index) {
        Log.i(TAG, "index = " + index + "; buckets length " + buckets.length);
        if(index < buckets.length && index >= 0) {
            String bucket = buckets[index];
            mImageUriList.clear();
            mImageUriList.addAll(mImageUris.get(bucket));
            Log.i(TAG, "mImageUriList size = " + mImageUriList + "; bucket " + bucket);
            if(localImagesAdapter == null) {
                localImagesAdapter = new LocalImagesAdapter(getActivity(), mImageUriList);
                localImagesAdapter.setSelectedImages(mSelecedImageUris);
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

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader<Void> loader = new AsyncTaskLoader<Void>(getParentActivity()) {

            /**
             * Called on a worker thread to perform the actual load and to return
             * the result of the load operation.
             * <p/>
             * Implementations should not deliver the result directly, but should return them
             * from this method, which will eventually end up calling {@link #deliverResult} on
             * the UI thread.  If implementations need to process the results on the UI thread
             * they may override {@link #deliverResult} and do so there.
             * <p/>
             */
            @Override
            public Void loadInBackground() {
                loadPhoneImages();
                return null;
            }
        };
        loader.forceLoad();
        return loader;
    }

    /**
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link android.content.CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        initBucketsAdapter();
        if(buckets.length > 0) {
            // 查找到了图片显示列表第一项的图片
            loadLocalImageOrder(curLoaderPosition);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }

    private SelectImageUirChangeListener selectImageUirListener;

    public void setSelectImageUirListener(SelectImageUirChangeListener selectImageUirListener) {
        this.selectImageUirListener = selectImageUirListener;
    }

    public interface SelectImageUirChangeListener {
        public void onChange(Uri imageUri, boolean isAdd);

        public void onDrawerOpened();

        public void onDrawerClose();
    }

}
