package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
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
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.DiaryPhotoEntity;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.PushedPhotoEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.ImagesRecyclerListener;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.wall.HeadHolder;
import com.bondwithme.BondWithMe.ui.wall.VideoHolder;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.WallEditView;
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
public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int VIEW_TYPE_HEAD = 0;
    private static final int VIEW_TYPE_PHOTO = 10;
    private static final int VIEW_TYPE_VIDEO = 100;
    public static final String TAG = ImagesRecyclerViewAdapter.class.getSimpleName();

    private ImagesRecyclerListener listener;
    private boolean isPhoto = true;
    private String userId = MainActivity.getUser().getUser_id();
    private Context context;
    private ArrayList<PushedPhotoEntity> entities = new ArrayList<>();
    private boolean isEdit;
    private String request_url;
    private ArrayList<PhotoEntity> data = new ArrayList<>();

    public HeadHolder headHolder = null;
    public VideoHolder videoHolder = null;

    public ImagesRecyclerViewAdapter(Context context, ArrayList<PushedPhotoEntity> entities) {
        this.context = context;
        this.entities = entities;
    }

    public void setRequest_url(String request_url) {
        LogUtil.d(TAG, "setRequest_url: " + request_url);
        this.request_url = request_url;
        loadLinkPhoto();
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setIsPhoto(boolean isPhoto) {
        this.isPhoto = isPhoto;
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
                    view = LayoutInflater.from(context).inflate(R.layout.edit_diary_head, null);
                    headHolder = new HeadHolder(view);
                }
                listener.loadHeadView(headHolder);
                return headHolder;
            case VIEW_TYPE_VIDEO:
                if (videoHolder == null) {
                    view = LayoutInflater.from(context).inflate(R.layout.tab_picture_voide_view, null);
                    videoHolder = new VideoHolder(view);
                }
                listener.loadVideoView(videoHolder);
                return videoHolder;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogUtil.d(TAG, "onBindViewHolder& position = " + position);
        if (position > 0) {
            if (isPhoto) {
                ImageHolder imageHolder = (ImageHolder) holder;
                PushedPhotoEntity entity = entities.get(position - 1);
                imageHolder.setCaption(entity.getPhoto_caption());
                if (entity instanceof DiaryPhotoEntity) {
                    Uri uri = ((DiaryPhotoEntity) entity).getUri();
                    LogUtil.d(TAG, "onBindViewHolder& DiaryPhotoEntity uri: " + uri.toString());
                    imageHolder.setImage(uri);
                } else if (entity instanceof PhotoEntity) {
                    VolleyUtil.initNetworkImageView(context, imageHolder.ivDisplay, String.format(Constant.API_GET_PIC, Constant.Module_preview_m, userId, ((PhotoEntity)entity).getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                }
                imageHolder.setPosition(position);
            }

        } else if (position == 0) {
            ((HeadHolder)holder).wevContent.requestFocus();
        }
        if (position == getItemCount() - 1) {
            listener.loadFinish();
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
        LogUtil.d(TAG, "getItemViewType position = " + position + "; is photo " + isPhoto);
        switch (position) {
            case 0: // Adpate的第一项必须返回是头部UI的类型
                return VIEW_TYPE_HEAD;
            default:// 是图片list显示图片的UI类型，否则显示视频类型
                return isPhoto ? VIEW_TYPE_PHOTO : VIEW_TYPE_VIDEO;
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return isPhoto ? entities.size() + 1 : 2;
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
                    int index = entities.size();
                    entities.addAll(data);
                    notifyItemRangeInserted(index, data.size());
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

    public void setListener(ImagesRecyclerListener listener) {
        this.listener = listener;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView ivDelete;
        public NetworkImageView ivDisplay;
        public WallEditView wevContent;
        private int position;

        public ImageHolder(View itemView) {
            super(itemView);
            ivDelete = (ImageView) itemView.findViewById(R.id.pic_delete);
            ivDisplay = (NetworkImageView) itemView.findViewById(R.id.iv_pic);
            wevContent = (WallEditView) itemView.findViewById(R.id.diary_edit_content);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position > 0) {
                        PushedPhotoEntity entity = entities.remove(position - 1);
                        notifyItemRemoved(position);
                        listener.deletePhoto(entity);
                    }
                }
            });
        }

        public void setCaption(String caption) {
            wevContent.setText(caption);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setImage(Uri uri) {
            ImageLoader.getInstance().displayImage(uri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
        }
    }
}
