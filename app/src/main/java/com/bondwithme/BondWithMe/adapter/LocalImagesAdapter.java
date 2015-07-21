package com.bondwithme.BondWithMe.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.interfaces.SelectImageUirChangeListener;
import com.bondwithme.BondWithMe.util.AsyncLoadBitmapTask;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.SDKUtil;

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

    private SelectImageUirChangeListener mListener;

    private int columnWidthHeight;

    private int mColor = -1;

    /**
     * 控制是否显示选择按钮，当选择一张图片时不需要显示选择框
     */
    private boolean checkBoxVisible = true;

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
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.local_images_item_for_gridview, null);
            holder = new HolderView();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.check = (CheckBox) convertView.findViewById(R.id.select_image_right);
            /**wing modified begin 2015.07.15 (如果真要改变checkbox 的颜色使用checkbox_color和checkbox_checked_color属性)*/
//            if(mColor != -1) {
//                // 需要修改颜色
//                holder.check.setBackgroundColor(mColor);
//            }
            /**wing modified end*/
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }

        holder.iv.setImageResource(R.drawable.network_image_default);
        loadLocalBitmap(holder.iv, position);
        if(!checkBoxVisible) {
            holder.check.setVisibility(View.GONE);
        } else {

            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.preview(mDatas.get(position));
                    }
                }
            });

            // 需要显示选择框，并显设置点击监听事件
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    boolean isChecked = checkBox.isChecked();
                    checkBox.setChecked(isChecked);

                    Uri uri = mDatas.get(position);
                    if(mListener != null) {
                        LogUtil.i(TAG, "onCheck& check2");
                        if(isChecked) {
                            LogUtil.i(TAG, "onCheck& check5");
                            boolean result = mListener.addUri(uri);
                            if(!result) {
                                // 添加失败，当前图片不能显示选中
                                checkBox.setChecked(false);
                            }
                        } else {
                            LogUtil.i(TAG, "onCheck& check4");
                            boolean result = mListener.removeUri(uri);
                            Log.i(TAG, "onCheck& check2: result ＝ " + result);
                            if(!result) {
                                Log.i(TAG, "onCheck& check6:");
                                // 删除失败，当前图片不能显示未选中
                                checkBox.setChecked(true);
                            }
                        }
                    }
                }
            });

            // 判断当前数据是否被选中，一在设置setOnCheckedChangeListener之后执行否则数据无效添加或删除上一次使用当前View的URI
            if(mSelectImages != null && mSelectImages.contains(mDatas.get(position))) {
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
     * @param position
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadLocalBitmap(ImageView imageView, int position) {
        if(position < mDatas.size()) {
            Uri uri = mDatas.get(position);
            Bitmap bitmap = AsyncLoadBitmapTask.getBitmap4MemoryMap(uri);
            if(bitmap == null) {
                // map中还没有获取这个uri的图片，从手机内丰中加载
                if(cancelPotentialWork(uri, imageView)) {
                    AsyncLoadBitmapTask task = new AsyncLoadBitmapTask(mContext, imageView, columnWidthHeight);
                    imageView.setTag(task);
                    //for not work in down 11
                    if (SDKUtil.IS_HONEYCOMB) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
                    } else {
                        task.execute(uri);
                    }
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
