package com.madx.bwm.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.j256.ormlite.dao.Dao;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.LocalStickerInfo;
import com.madx.bwm.entity.StickerGroupEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.more.sticker.StickerDetailActivity;
import com.madx.bwm.ui.more.sticker.StickerStoreActivity;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.ZipUtils;


import java.io.File;
import java.lang.Exception;import java.lang.Integer;import java.lang.Override;import java.lang.String;import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by heweidong on 15/6/10.
 */
public class StickerGroupAdapter extends RecyclerView.Adapter<StickerGroupAdapter.VHItem> {

    private static final String TAG = StickerGroupAdapter.class.getSimpleName();
    private Context mContext;
    private List<StickerGroupEntity> dataStickerGroup;
    private String url;
    private StickerGroupEntity stickerGroupEntity = null;
    String urlString = null;
    public static final String POSITION = "position";
    private int finished;


    public static final String STICKER_GROUP = "STICKER_GROUP";

    public StickerGroupAdapter(Context mContext, List<StickerGroupEntity> dataStickerGroup) {
        this.mContext = mContext;
        this.dataStickerGroup = dataStickerGroup;
    }

    @Override
    public StickerGroupAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item,parent,false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(final StickerGroupAdapter.VHItem holder, final int position) {
        boolean isNew = false;
        stickerGroupEntity = dataStickerGroup.get(position);
        url = String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), stickerGroupEntity.getFirst_sticker(), stickerGroupEntity.getPath(),stickerGroupEntity.getType());
        //设置new sticker
        if(isNew){
            holder.ivNewSticker.setVisibility(View.VISIBLE);
        }else {
            holder.ivNewSticker.setVisibility(View.INVISIBLE);
        }

//        设置sticker缩略图
        VolleyUtil.initNetworkImageView(mContext,
                holder.ivSticker,
                url,
                R.drawable.network_image_default, R.drawable.network_image_default);

        //设置sticker name
        holder.tvStickerName.setText(stickerGroupEntity.getName());

        //设置Download
        List<LocalStickerInfo> data = new ArrayList<>();

        try {       //查询数据,看表情包是否存在  where name = stickerGroupEntity.getName()
            Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
            data = stickerDao.queryForEq("name",stickerGroupEntity.getName());
            if(data.size() > 0){
                holder.tvDownload.setVisibility(View.INVISIBLE);
                holder.ivExist.setVisibility(View.VISIBLE);
            }else {
                holder.tvDownload.setVisibility(View.VISIBLE);
                holder.ivExist.setVisibility(View.INVISIBLE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //下载sticker zip
        holder.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvDownload.setVisibility(View.INVISIBLE);
                downloadZip(holder,position);
            }
        });

    }

    /**下载sticker Group zip*/
    private void downloadZip(final VHItem item,final int position) {
        final StickerGroupEntity stickerGroupEntity = dataStickerGroup.get(position);
        urlString = String.format(Constant.API_STICKER_ZIP, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath());
        final String target = FileUtil.getCacheFilePath(mContext) + String.format("/%s.zip", "" + stickerGroupEntity.getName());
        DownloadRequest download = new HttpTools(mContext).download(urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {
                item.pbDownload.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                item.pbDownload.setVisibility(View.INVISIBLE);
                item.pbDownload.setProgress(0);
                item.ivExist.setVisibility(View.VISIBLE);

                //插入sticker info
                try {
                    Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                    LocalStickerInfo stickerInfo = new LocalStickerInfo();
                    stickerInfo.setName(stickerGroupEntity.getName());
                    stickerInfo.setPath(stickerGroupEntity.getPath());
                    stickerInfo.setSticker_name(stickerGroupEntity.getFirst_sticker());
                    stickerInfo.setVersion(stickerGroupEntity.getVersion());
                    stickerInfo.setType(stickerGroupEntity.getType());
                    stickerInfo.setPosition(position);
                    stickerDao.create(stickerInfo);

                    Log.i(TAG, "=======tickerInfo==========" +stickerInfo.toString() );

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onResult(String response) {
                File zipFile = new File(target);
                try {
                    ZipUtils.unZipFile(zipFile, FileUtil.getCacheFilePath(mContext));
                    zipFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {
                //更新item中的进度条
                finished = (int) (current * 100 / count);
                item.pbDownload.setProgress(finished);


                //发广播更新StickerDetailActivity的progressbar
                Intent intent = new Intent(StickerStoreActivity.ACTION_UPDATE);
                intent.putExtra("finished",finished);
                intent.putExtra(POSITION,position);
                mContext.sendBroadcast(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return dataStickerGroup.size();
    }

    public class VHItem extends RecyclerView.ViewHolder{
         ImageView ivNewSticker;
         NetworkImageView ivSticker;
         TextView tvStickerName;
         TextView tvDownload;
         ImageView ivExist;
         ProgressBar pbDownload;

        public VHItem(View itemView) {
            super(itemView);
            ivNewSticker = (ImageView)itemView.findViewById(R.id.iv_news_ticker);
            ivSticker = (NetworkImageView)itemView.findViewById(R.id.iv_sticker);
            tvStickerName = (TextView)itemView.findViewById(R.id.tv_sticker_name);
            tvDownload = (TextView)itemView.findViewById(R.id.tv_download);
            ivExist = (ImageView) itemView.findViewById(R.id.iv_exist);
            pbDownload = (ProgressBar) itemView.findViewById(R.id.pb_download);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, StickerDetailActivity.class);
                    intent.putExtra(STICKER_GROUP, dataStickerGroup.get(getAdapterPosition()));
                    intent.putExtra(POSITION, getAdapterPosition());
                    mContext.startActivity(intent);

                }
            });
        }


    }
}
