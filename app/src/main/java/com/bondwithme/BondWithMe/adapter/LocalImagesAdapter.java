package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gc.materialdesign.views.CheckBox;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.AsyncLoadBitmapTask;

import java.util.List;

/**
 * Created by zhuweiping on 5/4/15.
 */
public class LocalImagesAdapter extends BaseAdapter {

    private static final String TAG = LocalImagesAdapter.class.getSimpleName();

    private Context mContext;
    /**
     * 显示图片的Ur列表
     */
    private List<Uri> mDatas;
    /**
     * 已经选中的图片
     */
    private List<Uri> mSelectImages;

    private int columnWidthHeight;

    private int mColor = -1;

    public LocalImagesAdapter(Context context, List<Uri> datas, int color) {
        mContext = context;
        mDatas = datas;
        mColor = color;
    }

    public void setColumnWidthHeight(int columnWidthHeight) {
        this.columnWidthHeight = columnWidthHeight;
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
     * 设置选中图片列表用于判断显示选择框的选中状态
     *
     * @param selectedImages
     */
    public void setSelectedImages(List<Uri> selectedImages) {
        mSelectImages = selectedImages;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.local_images_item_for_gridview, null);
            holder = new HolderView();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.check = (CheckBox) convertView.findViewById(R.id.select_image_right);
            if(mColor != -1) {
                // 需要修改颜色
                holder.check.setBackgroundColor(mColor);
            }
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        holder.iv.setImageResource(R.drawable.network_image_default);
        loadLocalBitmap(holder.iv, position);

        if(mSelectImages != null && mSelectImages.contains(mDatas.get(position))) {
            // 当前图片已被选中
            holder.check.setChecked(true);
        } else {
            holder.check.setChecked(false);
        }
        return convertView;
    }

    /**
     * 通过Uir列中positio项的uri异步获取缩略图，并加载传入的imageVie中
     *
     * @param imageView －  显示图片的ImageVie视图
     * @param position
     */
    private void loadLocalBitmap(ImageView imageView, int position) {
        if(position < mDatas.size()) {
            Uri uri = mDatas.get(position);
            Bitmap bitmap = AsyncLoadBitmapTask.getBitmap4MemoryMap(uri);
            if(bitmap == null) {
                // map中还没有获取这个uri的图片，从手机内丰中加载
                if(cancelPotentialWork(uri, imageView)) {
                    AsyncLoadBitmapTask task = new AsyncLoadBitmapTask(mContext, imageView, columnWidthHeight);
                    imageView.setTag(task);
                    task.execute(uri);
                }
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 比较是否在加载同一个Uri,不同取消并取消上一次的加载
     *
     * @param uri       - 将要加载的uri
     * @param imageView - 显示图片的视图
     * @return - false: 取消加载; true: 取消了加载
     */
    private boolean cancelPotentialWork(Uri uri, ImageView imageView) {
        AsyncLoadBitmapTask task = getLoadBitmapTask(imageView);
        boolean result = true;
        if(task != null) {
            AsyncTask.Status status = task.getStatus();
            switch(status) {
                case PENDING:
                    result = true;
                    break;
                case RUNNING:
                    if(task.getUri() == null || !task.getUri().equals(uri)) {
                        task.cancel(true);
                        Log.i(TAG, "cancelPotentialWork& task cancel; uri: " + uri);
                        result = true;
                    } else {
                        Log.i(TAG, "cancelPotentialWork& task nothing; uri: " + uri);
                        result = false;
                    }
                    break;
                case FINISHED:
                    result = true;
                    break;
            }
        } else {
            Log.i(TAG, "cancelPotentialWork& task not process; uri: " + uri);
            result = true;
        }
        return result;
    }

    private AsyncLoadBitmapTask getLoadBitmapTask(ImageView imageView) {
        if(imageView != null) {
            Object tag = imageView.getTag();
            if(tag instanceof AsyncLoadBitmapTask) {
                return (AsyncLoadBitmapTask) tag;
            }
        }
        return null;
    }

    class HolderView {
        ImageView iv;
        CheckBox check;
    }

}
