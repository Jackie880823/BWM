package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.wall.WallViewPicActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 10/21/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class DiaryInformationAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int VIEW_TYPE_HEAD = 0;
    public static final String TAG = DiaryInformationAdapter.class.getSimpleName();

    private DiaryInformationListener listener;
    private String userId;
    private BaseFragment fragment;
    private Context context;
    private String request_url;
    private ArrayList<PhotoEntity> data = new ArrayList<>();

    public WallHolder headHolder = null;

    public DiaryInformationAdapter(BaseFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    public void clearData(){
        if (!data.isEmpty()) {
            data.clear();
            notifyItemRangeRemoved(1, getItemCount());
        }
    }

    public void setRequest_url(String request_url, String userId) {
        LogUtil.d(TAG, "setRequest_url: " + request_url);
        this.request_url = request_url;
        this.userId = userId;
        loadLinkPhoto();
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LogUtil.d(TAG, "onCreateViewHolder& viewType is " + viewType);
        switch (viewType) {
            case VIEW_TYPE_HEAD:
                if (headHolder == null) {
                    view = LayoutInflater.from(context).inflate(R.layout.wall_item, null);
                    headHolder = new WallHolder(fragment, view, new HttpTools(context), true);
                }
                listener.loadHeadView(headHolder);
                return headHolder;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.diary_photo_item, null);
                return new ImageHolder(view);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LogUtil.d(TAG, "onBindViewHolder& position = " + position);
        if (position > 0) {
            ImageHolder imageHolder = (ImageHolder) holder;
            PhotoEntity entity = data.get(position - 1);
            imageHolder.setCaption(entity.getPhoto_caption());
            Uri uri;
            String url = String.format(Constant.API_GET_PIC, Constant.Module_preview_m, userId, entity.getFile_id());
            uri = Uri.parse(url);
            LogUtil.d(TAG, "onBindViewHolder& DiaryPhotoEntity uri: " + uri.toString());
            imageHolder.setImage(uri);
            imageHolder.ivDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WallViewPicActivity.class);
                    intent.putExtra(Constant.REQUEST_URL, request_url);
                    intent.putExtra(Constant.USER_ID, userId);
                    intent.putExtra(Constant.PHOTO_POSITION, position - 1);
                    fragment.startActivityForResult(intent, Constant.INTENT_REQUEST_UPDATE_PHOTOS);
                }
            });
        } else if (position == 0) {
            if (holder instanceof WallHolder) {
                listener.loadHeadView((WallHolder) holder);
            }

        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p/>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        LogUtil.d(TAG, "getItemViewType position = " + position + ";");
        switch (position) {
            case 0: // Adpate的第一项必须返回是头部UI的类型
                return VIEW_TYPE_HEAD;
            default:// 是图片list显示图片的UI类型，否则显示视频类型
                return position;
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    private void loadLinkPhoto() {
        LogUtil.i(TAG, "loadLinkPhoto");

        new HttpTools(context).get(request_url, null, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "onResult& response: " + response);
                try {
                    GsonBuilder gsonb = new GsonBuilder();
                    //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                    //DateDeserializer ds = new DateDeserializer();
                    //给GsonBuilder方法单独指定Date类型的反序列化方法
                    //gsonb.registerTypeAdapter(Date.class, ds);
                    clearData();
                    Gson gson = gsonb.create();
                    if (response.startsWith("{\"data\":")) {
                        JSONObject jsonObject = new JSONObject(response);
                        String dataString = jsonObject.optString("data");
                        data = gson.fromJson(dataString, new TypeToken<ArrayList<PhotoEntity>>() {
                        }.getType());
                    } else {
                        data = gson.fromJson(response, new TypeToken<ArrayList<PhotoEntity>>() {
                        }.getType());
                    }
                    notifyItemRangeInserted(1, data.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    public void setListener(DiaryInformationListener listener) {
        this.listener = listener;
    }

    public class ImageHolder extends ViewHolder {

        public ImageView ivDisplay;
        public FreedomSelectionTextView tvCaption;

        public ImageHolder(View itemView) {
            super(itemView);
            ivDisplay = (ImageView) itemView.findViewById(R.id.iv_pic_normal);
            tvCaption = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_photo_caption);
        }

        public void setCaption(String caption) {
            this.tvCaption.setText(caption);
        }

        public void setImage(Uri uri) {
            ivDisplay.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(uri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
        }
    }

    public interface DiaryInformationListener {
        void loadHeadView(WallHolder wallHolder);
    }
}
