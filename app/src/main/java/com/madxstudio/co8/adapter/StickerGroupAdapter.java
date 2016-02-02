package com.madxstudio.co8.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.StickerGroupEntity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.more.sticker.StickerDetailActivity;
import com.madxstudio.co8.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by heweidong on 15/6/10.
 */
public class StickerGroupAdapter extends HeaderListRecyclerAdapter implements View.OnClickListener {

    private static final String TAG = StickerGroupAdapter.class.getSimpleName();
    public static final String PATH = "sticker_path";
    private Context mContext;
    private List<StickerGroupEntity> dataStickerGroup;
    private String url;
    private StickerGroupEntity stickerGroupEntity = null;
    public static final String POSITION = "position";
    public static final String STICKER_GROUP = "STICKER_GROUP";
    private List<String> mStickers;

    public StickerGroupAdapter(Context mContext, List<StickerGroupEntity> dataStickerGroup, List<String> stickers, Map<String, Uri> mAdsPaths) {
        super(mContext);
        this.mContext = mContext;
        this.dataStickerGroup = dataStickerGroup;
        mStickers = stickers;
        setList(dataStickerGroup);
        this.mAdsPaths = mAdsPaths;
    }

    public void setMAdsPaths(Map<String, Uri> mAdsMap) {
        if (mAdsMap != null && mAdsMap.size() > 0) {
            this.mAdsPaths = mAdsMap;
        }
    }

    public void addSticker(String path) {
        if (mStickers != null && !mStickers.contains(path)) {
            mStickers.add(path);
        }
    }

    public void removeSticker(String path) {
        if (mStickers != null) {
            mStickers.remove(path);
        }
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        VHItem holder = (VHItem) viewHolder;
        stickerGroupEntity = dataStickerGroup.get(position);
//        url = String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), "1_S", stickerGroupEntity.getPath(), stickerGroupEntity.getType());
        url = String.format(Constant.API_STICKER_ORIGINAL_IMAGE, MainActivity.getUser().getUser_id(), stickerGroupEntity.getFirst_sticker_code(),stickerGroupEntity.getVersion());
        //设置new sticker
        if (stickerGroupEntity.getSticker_new().trim().equals("1")) {
            holder.ivNewSticker.setVisibility(View.VISIBLE);
        } else {
            holder.ivNewSticker.setVisibility(View.INVISIBLE);
        }
        //设置sticker缩略图
        setFirstBigSticker(holder.ivSticker);



        //设置sticker name
        holder.tvStickerName.setText(stickerGroupEntity.getName());

        LogUtil.d("", "position============" + position);
        LogUtil.d("", "stickerGroupEntity.getPath()============" + mStickers.contains(stickerGroupEntity.getPath()));
        LogUtil.d("", "stickerGroupEntity.isDownloading()============" + stickerGroupEntity.isDownloading());
        if ((mStickers != null && mStickers.contains(stickerGroupEntity.getPath()))) {
            holder.tvDownload.setVisibility(View.INVISIBLE);
            holder.ivExist.setVisibility(View.VISIBLE);
            holder.getPbDownload().setVisibility(View.INVISIBLE);
        } else {
            if (!stickerGroupEntity.isDownloading()) {
                holder.tvDownload.setVisibility(View.VISIBLE);
                holder.ivExist.setVisibility(View.INVISIBLE);
                holder.getPbDownload().setVisibility(View.INVISIBLE);
            } else {
                holder.tvDownload.setVisibility(View.INVISIBLE);
                holder.ivExist.setVisibility(View.INVISIBLE);
                holder.getPbDownload().setVisibility(View.VISIBLE);
            }
        }

    }

    private void setFirstBigSticker(ImageView view) {
//        String picPath = FileUtil.getBigStickerPath(mContext, stickerGroupEntity.getPath(), "1", stickerGroupEntity.getType());
//        File file = new File(picPath);
//        if (file.exists()){
//            view.setImageBitmap(BitmapFactory.decodeFile(picPath));
//        }else{
            BitmapTools.getInstance(mContext).display(
                    (NetworkImageView) view,
                    url,
                    R.drawable.network_image_default, R.drawable.network_image_default);
//        }

    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HeaderHolder holder = (HeaderHolder) viewHolder;
//        if (adSizeConfirm && !holder.paperInited) {
        holder.paperInited = true;
        ArrayList<ImageView> views = new ArrayList<>();
        if (mAdsPaths != null) {
            for (Map.Entry<String, Uri> entry : mAdsPaths.entrySet()) {
                ImageView view = new ImageView(mContext);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setLayoutParams(new ImageSwitcher.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
                view.setImageURI(entry.getValue());
                view.setTag(entry.getKey());
                view.setOnClickListener(this);
                views.add(view);
            }

            holder.isStickerBanner.setAdapter(new ImageAdapter(views));
            holder.sticker_progress.setVisibility(View.GONE);
        }
//        }else{
//            holder.sticker_progress.setVisibility(View.VISIBLE);
//        }

//        holder.isStickerBanner.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        final String path = (String) v.getTag();
//        if (dataStickerBanner != null && dataStickerGroup != null && dataStickerBanner.size() > 0 && dataStickerGroup.size() > 0) {
//            LogUtil.d(TAG, "====setOnClickListener=====" + path + "    " + !path.isEmpty());
        final int position = getPosition(path, dataStickerGroup);
        if (position != -1) {
            final StickerGroupEntity stickerGroupEntity = dataStickerGroup.get(position);
            if (!path.isEmpty()) {
                Intent intent = new Intent(mContext, StickerDetailActivity.class);
                intent.putExtra(StickerGroupAdapter.STICKER_GROUP, stickerGroupEntity);
                intent.putExtra(StickerGroupAdapter.POSITION, position);
                mContext.startActivity(intent);
            }
        }
    }

    //获取广告图对应的表情包的position
    private int getPosition(String stickerGroupPath, List<StickerGroupEntity> dataStickerGroup) {
        String path = null;
        for (int i = 0; i < dataStickerGroup.size(); i++) {
            path = dataStickerGroup.get(i).getPath();
            if (path.equals(stickerGroupPath)) {
                return i;
            }
        }
        return -1;
    }

    private class ImageAdapter extends PagerAdapter {

        private ArrayList<ImageView> viewlist;

        public ImageAdapter(ArrayList<ImageView> viewlist) {
            this.viewlist = viewlist;
        }

        @Override
        public int getCount() {
            //设置成最大，使用户看不到边界
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            //Warning：不要在这里调用removeView
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求模取出View列表中要显示的项
            if (!viewlist.isEmpty()) {
                position %= viewlist.size();
                if (position < 0) {
                    position = viewlist.size() + position;
                }
                ImageView view = viewlist.get(position);
                //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
                ViewParent vp = view.getParent();
                if (vp != null) {
                    ViewGroup parent = (ViewGroup) vp;
                    parent.removeView(view);
                }
                container.addView(view);
                //add  listeners  here  if  necessary
                return view;
            }

            return null;
        }
    }


    private Map<String, Uri> mAdsPaths = new HashMap<>();

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, parent, false);
        return new VHItem(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_store_top_ads, parent, false);
        return new HeaderHolder(view);
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        private ViewPager isStickerBanner;
        private View sticker_progress;
        private ImageHandler mHandler;
        private boolean paperInited;

        public HeaderHolder(View itemView) {
            super(itemView);
            mHandler = new ImageHandler(mContext);
            isStickerBanner = (ViewPager) itemView.findViewById(R.id.rl_ads);
            isStickerBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                //配合Adapter的currentItem字段进行设置。
                @Override
                public void onPageSelected(int arg0) {
                    mHandler.sendMessage(Message.obtain(mHandler, ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                //覆写该方法实现轮播效果的暂停和恢复
                @Override
                public void onPageScrollStateChanged(int arg0) {
                    switch (arg0) {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            mHandler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                            break;
                        case ViewPager.SCROLL_STATE_IDLE:
                            mHandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                            break;
                        default:
                            break;
                    }
                }
            });
            isStickerBanner.setCurrentItem(Integer.MAX_VALUE / 2);//默认在中间，使用户看不到边界
            sticker_progress = itemView.findViewById(R.id.sticker_progress);
            sticker_progress.setVisibility(View.VISIBLE);
            //开始轮播效果
            mHandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);

        }

        private class ImageHandler extends Handler {
            /**
             * 请求更新显示的View。
             */
            protected static final int MSG_UPDATE_IMAGE = 1;
            /**
             * 请求暂停轮播。
             */
            protected static final int MSG_KEEP_SILENT = 2;
            /**
             * 请求恢复轮播。
             */
            protected static final int MSG_BREAK_SILENT = 3;
            /**
             * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
             * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
             * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
             */
            protected static final int MSG_PAGE_CHANGED = 4;

            //轮播间隔时间
            protected static final long MSG_DELAY = 3000;

            //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
            private Context context;
            private int currentItem = 0;

            protected ImageHandler(Context context) {
                this.context = context;
            }

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
                if (hasMessages(MSG_UPDATE_IMAGE)) {
                    removeMessages(MSG_UPDATE_IMAGE);
                }
                switch (msg.what) {
                    case MSG_UPDATE_IMAGE:
                        currentItem++;
                        isStickerBanner.setCurrentItem(currentItem);
                        //准备下次播放
                        sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                        break;
                    case MSG_KEEP_SILENT:
                        //只要不发送消息就暂停了
                        break;
                    case MSG_BREAK_SILENT:
                        sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                        break;
                    case MSG_PAGE_CHANGED:
                        //记录当前的页号，避免播放的时候页面显示不正确。
                        currentItem = msg.arg1;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public class VHItem extends RecyclerView.ViewHolder {
        ImageView ivNewSticker;
        NetworkImageView ivSticker;
        TextView tvStickerName;
        TextView tvDownload;
        ImageView ivExist;
        ProgressBar pbDownload;

        public VHItem(View itemView) {
            super(itemView);
            ivNewSticker = (ImageView) itemView.findViewById(R.id.iv_news_ticker);
            ivSticker = (NetworkImageView) itemView.findViewById(R.id.iv_sticker);
            tvStickerName = (TextView) itemView.findViewById(R.id.tv_sticker_name);
            tvDownload = (TextView) itemView.findViewById(R.id.tv_download);
            ivExist = (ImageView) itemView.findViewById(R.id.iv_exist);
            pbDownload = (ProgressBar) itemView.findViewById(R.id.pb_download);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && dataStickerGroup != null) {
                        itemClickListener.itemClick(stickerGroupEntity, getAdapterPosition() - 1);

                    }
                }
            });
            tvDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadClickListener != null && dataStickerGroup != null) {
                        downloadClickListener.downloadClick(stickerGroupEntity, getAdapterPosition() - 1);
                    }
                }
            });

        }

        public NetworkImageView getIvSticker() {
            return ivSticker;
        }

        public void setIvSticker(NetworkImageView ivSticker) {
            this.ivSticker = ivSticker;
        }

        public TextView getTvStickerName() {
            return tvStickerName;
        }

        public void setTvStickerName(TextView tvStickerName) {
            this.tvStickerName = tvStickerName;
        }

        public TextView getTvDownload() {
            return tvDownload;
        }

        public void setTvDownload(TextView tvDownload) {
            this.tvDownload = tvDownload;
        }

        public ImageView getIvExist() {
            return ivExist;
        }

        public void setIvExist(ImageView ivExist) {
            this.ivExist = ivExist;
        }

        public ProgressBar getPbDownload() {
            return pbDownload;
        }

        public void setPbDownload(ProgressBar pbDownload) {
            this.pbDownload = pbDownload;
        }
    }

    public ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void itemClick(StickerGroupEntity stickerGroupEntity, int position);

    }

    public DownloadClickListener downloadClickListener;

    public interface DownloadClickListener {
        void downloadClick(StickerGroupEntity stickerGroupEntity, int position);

    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setDownloadClickListener(DownloadClickListener downloadClickListener) {
        this.downloadClickListener = downloadClickListener;
    }


}
