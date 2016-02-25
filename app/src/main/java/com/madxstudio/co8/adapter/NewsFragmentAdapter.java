package com.madxstudio.co8.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.DiaryPhotoEntity;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.entity.PushedPhotoEntity;
import com.madxstudio.co8.interfaces.ImagesNewsRecyclerListener;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.WallEditView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzemian on 16/1/29.
 */
public class NewsFragmentAdapter extends RecyclerView.Adapter<ViewHolder>{
    private static final String TAG = "NewsFragmentAdapter";
    private static final int VIEW_TYPE_HEAD = 0;
    private static final int VIEW_TYPE_PHOTO = 10;
    private static final int VIEW_TYPE_VIDEO = 100;

    private List<PhotoEntity> data = new ArrayList<>();
    private List<PushedPhotoEntity> mEntities;
    private Context mContext;
    private String userId = MainActivity.getUser().getUser_id();

    private ImagesNewsRecyclerListener listener;
    public WriteNewHeadHolder headHolder = null;
    public VideoHolder videoHolder = null;
    private boolean isPhoto = true;
    private String request_url;


    public NewsFragmentAdapter(Context context, List<PushedPhotoEntity> entities){
        mContext = context;
        mEntities = entities;

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

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return VIEW_TYPE_HEAD;
            default:
                return isPhoto ? VIEW_TYPE_PHOTO : VIEW_TYPE_VIDEO;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
//        RecyclerView.ViewHolder viewHolder = null;
            switch (viewType){
                case VIEW_TYPE_HEAD:
                    if (headHolder == null) {
                        view = LayoutInflater.from(mContext).inflate(R.layout.edit_news_head,null);
                        headHolder = new WriteNewHeadHolder(view);
                    }
                    listener.loadHeadView(headHolder);
                    return headHolder;
                case VIEW_TYPE_VIDEO:
                    if(videoHolder == null){
                        view = LayoutInflater.from(mContext).inflate(R.layout.video_item,null);
                        videoHolder = new VideoHolder(view);
                    }
                    listener.loadVideoView(videoHolder);
                    return videoHolder;
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.photo_item,null);
                    return new ImageHolder(view);
            }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == 0){
            //头部
            WriteNewHeadHolder headHolder = (WriteNewHeadHolder) holder;

        }else if(isPhoto){
            //如果是照片
            ImageHolder imageHolder = (ImageHolder) holder;
            final PushedPhotoEntity entity = mEntities.get(position - 1);
            imageHolder.setCaption(entity.getPhoto_caption());
            Uri uri = Uri.EMPTY;
            if (entity instanceof DiaryPhotoEntity) {
                uri = ((DiaryPhotoEntity) entity).getUri();
            } else if (entity instanceof PhotoEntity) {
                String url = String.format(Constant.API_GET_PIC, Constant.Module_preview, userId, ((PhotoEntity) entity).getFile_id());
                uri = Uri.parse(url);
            }
            imageHolder.setImage(uri);
            imageHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = mEntities.indexOf(entity);
                    LogUtil.d(TAG, "onClick& position = " + index + "; entities size " + mEntities.size());
                    listener.deletePhoto(entity);
                        /* 因为此Adapter包含一个头View,所以在删除数据的View时位置是对应位置+1 */
                    notifyItemRemoved(index + 1);
                }
            });
            ((ImageHolder) holder).setTag(entity);
        }
        if (getItemCount() > 0) {
            listener.loadFinish();
        }

    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        LogUtil.d(TAG, "onViewDetachedFromWindow&");
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof ImageHolder) {
            PushedPhotoEntity entity = ((ImageHolder) holder).getTag();
            entity.setPhoto_caption(((ImageHolder) holder).wevContent.getRelText());
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        LogUtil.d(TAG, "onViewRecycled&");
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
//        isPhoto ? entities.size() + 1 : 2
        return isPhoto ? mEntities.size() + 1 : 2;
    }

    public void setListener(ImagesNewsRecyclerListener listener) {
        this.listener = listener;
    }

//    class HeadHolder extends ViewHolder{
//        public WallEditView wevContent;
//        public TextView tvLocationDesc;
//
//
//        public HeadHolder(View itemView) {
//            super(itemView);
//            tvLocationDesc = (TextView) itemView.findViewById(R.id.location_desc);
//            wevContent = (WallEditView) itemView.findViewById(R.id.diary_edit_content);
//        }
//    }


    public class ImageHolder extends ViewHolder{
        private ImageView ivDelete;
        public ImageView ivDisplay;
        public WallEditView wevContent;
        private PushedPhotoEntity tag;

        public ImageHolder(View itemView) {
            super(itemView);
            ivDelete = (ImageView) itemView.findViewById(R.id.pic_delete);
            ivDisplay = (ImageView) itemView.findViewById(R.id.iv_pic_normal);
            wevContent = (WallEditView) itemView.findViewById(R.id.diary_edit_content);
        }

        public PushedPhotoEntity getTag() {
            return tag;
        }

        public void setTag(PushedPhotoEntity tag) {
            this.tag = tag;
        }

        public void setCaption(String caption) {
            wevContent.setText(caption);
        }
        public void setImage(Uri uri) {
            ivDisplay.setVisibility(View.VISIBLE);

            ImageLoader.getInstance().displayImage(uri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
        }
    }


    private void loadLinkPhoto() {
        LogUtil.i(TAG, "loadLinkPhoto");

        new HttpTools(mContext).get(request_url, null, this, new HttpCallback() {
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
                    int index = mEntities.size();
                    mEntities.addAll(data);
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
}
