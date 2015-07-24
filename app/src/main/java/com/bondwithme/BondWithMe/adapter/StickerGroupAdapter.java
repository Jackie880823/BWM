package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
import com.j256.ormlite.dao.Dao;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;


import java.io.File;
import java.sql.SQLException;
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
        url = String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), "1_S", stickerGroupEntity.getPath(), stickerGroupEntity.getType());
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
//            Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
//            data = stickerDao.queryForEq("name",stickerGroupEntity.getName());
            LocalStickerInfoDao dao = LocalStickerInfoDao.getInstance(mContext);
            boolean hasSticker = dao.hasDownloadSticker(stickerGroupEntity.getPath());
            if(hasSticker){

                holder.tvDownload.setVisibility(View.INVISIBLE);
                holder.ivExist.setVisibility(View.VISIBLE);
            }else {
                holder.tvDownload.setVisibility(View.VISIBLE);
                holder.ivExist.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    if (itemClickListener != null && dataStickerGroup != null){
                        itemClickListener.itemClick(stickerGroupEntity,getAdapterPosition());

                    }
                }
            });
            tvDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadClickListener != null && dataStickerGroup != null){
                        downloadClickListener.downloadClick(stickerGroupEntity, getAdapterPosition());
                    }
                }
            });

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
