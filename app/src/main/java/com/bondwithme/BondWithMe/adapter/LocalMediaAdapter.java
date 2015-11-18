package com.bondwithme.BondWithMe.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackie on 5/4/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class LocalMediaAdapter extends BaseAdapter {

    private static final String TAG = LocalMediaAdapter.class.getSimpleName();

    private Context mContext;

    /**
     * 显示图片的Ur列表
     */
    private List<MediaData> mDatas;
    /**
     * 已经选中的图片
     */
    private ArrayList<MediaData> mSelectMedias;

    private SelectImageUirChangeListener mListener;


    /**
     * 控制是否显示选择按钮，当选择一张图片时不需要显示选择框
     */
    private boolean checkBoxVisible = true;

    public LocalMediaAdapter(Context context, List<MediaData> datas) {
        mContext = context;
        mDatas = datas;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mDatas.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    @Override
    public void notifyDataSetChanged() {
        LogUtil.i(TAG, "notifyDataSetChanged& ");
        clearLoad();
        super.notifyDataSetChanged();
    }

    public void setData(ArrayList<MediaData> datas) {
        mDatas = datas;
    }

    /**
     * 设置选中图片列表用于判断显示选择框的选中状态
     *
     * @param selectedMedias
     */
    public void setSelectedImages(ArrayList<MediaData> selectedMedias) {
        mSelectMedias = selectedMedias;
    }

    public void setListener(SelectImageUirChangeListener listener) {
        this.mListener = listener;
    }

    public void setCheckBoxVisible(boolean visible) {
        checkBoxVisible = visible;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HolderView holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.local_images_item_for_gridview, null);
            holder = new HolderView();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.videoIcon = (ImageView) convertView.findViewById(R.id.video_icon_iv);
            holder.check = (CheckBox) convertView.findViewById(R.id.select_image_right);
            holder.llDuration = (LinearLayout) convertView.findViewById(R.id.duration_ll);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.duration_tv);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }

        mHandler.removeCallbacksAndMessages(holder.iv);
        Message msg = new Message();
        msg.what = MSG_LOCAL_BITMAP;
        msg.obj = holder.iv;
        msg.arg1 = position;
        mHandler.sendMessage(msg);

        if (!checkBoxVisible) {
            holder.check.setVisibility(View.GONE);
        } else {
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.preview(mDatas.get(position));
                    }
                }
            });

            MediaData mediaData = mDatas.get(position);
            if (MediaData.TYPE_VIDEO.equals(mediaData.getType())) {
                holder.llDuration.setVisibility(View.VISIBLE);
                holder.videoIcon.setVisibility(View.VISIBLE);
                long duration = mediaData.getDuration();
                holder.tvDuration.setText(MyDateUtils.formatDuration(duration));
            } else {
                holder.llDuration.setVisibility(View.GONE);
                holder.videoIcon.setVisibility(View.GONE);
            }

            // 需要显示选择框，并显设置点击监听事件
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    boolean isChecked = checkBox.isChecked();
                    checkBox.setChecked(isChecked);

                    MediaData uri = mDatas.get(position);
                    if (mListener != null) {
                        LogUtil.i(TAG, "onCheck& check2");
                        if (isChecked) {
                            LogUtil.i(TAG, "onCheck& check5");
                            boolean result = mListener.addUri(uri);
                            if (!result) {
                                // 添加失败，当前图片不能显示选中
                                checkBox.setChecked(false);
                            }
                        } else {
                            LogUtil.i(TAG, "onCheck& check4");
                            boolean result = mListener.removeUri(uri);
                            LogUtil.i(TAG, "onCheck& check2: result ＝ " + result);
                            if (!result) {
                                LogUtil.i(TAG, "onCheck& check6:");
                                // 删除失败，当前图片不能显示未选中
                                checkBox.setChecked(true);
                            }
                        }
                    }
                }
            });

            // 判断当前数据是否被选中，一在设置setOnCheckedChangeListener之后执行否则数据无效添加或删除上一次使用当前View的URI
            if (mSelectMedias != null && mSelectMedias.contains(mDatas.get(position))) {
                LogUtil.i(TAG, "onCheck& check7");
                // 当前图片已被选中
                holder.check.setChecked(true);
            } else {
                LogUtil.i(TAG, "onCheck& check8");
                holder.check.setChecked(false);
            }
        }
        return convertView;
    }

    /**
     * 通过Uir列中{@code position}项的uri异步获取缩略图，并加载传入的imageVie中
     *
     * @param imageView －  显示图片的ImageVie视图
     * @param position  Uir列中{@code position}项的uri
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadLocalBitmap(ImageView imageView, int position) {
        if (position < mDatas.size()) {
            MediaData mediaData = mDatas.get(position);

            LogUtil.i(TAG, "loadLocalBitmap& uri: " + mediaData.getContentUri());

            Uri thumbnailUri;
            thumbnailUri = mediaData.getThumbnailUri();

            if (thumbnailUri != null && !thumbnailUri.equals(Uri.EMPTY)) {
                LogUtil.i(TAG, "loadLocalBitmap& load thumbnail: " + thumbnailUri);
                ImageLoader.getInstance().displayImage(thumbnailUri.toString(), imageView, UniversalImageLoaderUtil.options, imageLoadingListener);
            } else {
                String uri = mediaData.getPath();
                LogUtil.i(TAG, "loadLocalBitmap& load picture: " + uri);
                ImageLoader.getInstance().displayImage(uri, imageView, UniversalImageLoaderUtil.options, imageLoadingListener);
            }
        }
    }

    /**
     * 图片加载处理监听
     */
    private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            LogUtil.w(TAG, "onLoadingFailed& imageUri: " + imageUri);
            // 图片加载失败从列表中删除
            boolean success = mDatas.remove(new MediaData(Uri.parse(imageUri), imageUri, MediaData.TYPE_IMAGE, 0));
            if (success) {
                mHandler.removeMessages(MSG_NOTIFY);
                mHandler.sendEmptyMessageDelayed(MSG_NOTIFY, 500);
            } else {

                success = mDatas.remove(new MediaData(Uri.parse(imageUri), imageUri, MediaData.TYPE_VIDEO, 0));
                if (success) {
                    mHandler.removeMessages(MSG_NOTIFY);
                    mHandler.sendEmptyMessageDelayed(MSG_NOTIFY, 500);
                } else {

                    // 删除不成功有可能是略缩图无法使用加载原图
                    Cursor cursor = null;
                    try {
                        String select = MediaStore.Images.Thumbnails.DATA + " = " + ImageDownloader.Scheme.FILE.crop(imageUri);
                        cursor = new CursorLoader(mContext, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, select, null, null).loadInBackground();
                        if (cursor != null && cursor.getCount() > 0) {
                            int columnImageId = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
                            long imageId = cursor.getLong(columnImageId);
                            String contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + imageId;
                            ImageLoader.getInstance().displayImage(contentUri, (ImageView) view, UniversalImageLoaderUtil.options);
                        }
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                }
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };

    /**
     * 加载图片的处理事件
     */
    private static final int MSG_LOCAL_BITMAP = 0;

    /**
     * 刷新处理事件
     */
    private static final int MSG_NOTIFY = 1;

    /**
     * 适配器消息处理{@link Handler}
     */
    private Handler mHandler = new Handler() {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOCAL_BITMAP:
                    loadLocalBitmap((ImageView) msg.obj, msg.arg1);
                    break;
                case MSG_NOTIFY:
                    notifyDataSetChanged();
                    break;
            }
        }
    };

    /**
     * 清掉正加载的图片的等待队列
     */
    public void clearLoad() {
        mHandler.removeMessages(MSG_LOCAL_BITMAP);
    }

    class HolderView {
        ImageView iv;
        ImageView videoIcon;
        CheckBox check;
        LinearLayout llDuration;
        TextView tvDuration;
    }

}
