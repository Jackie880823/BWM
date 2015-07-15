package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.more.sticker.MyStickerActivity;
import com.bondwithme.BondWithMe.util.AnimatedGifDrawable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * 本地表情包列表适配器
 * Created by heweidong on 15/6/11.
 */
public class MyStickerAdapter extends RecyclerView.Adapter<MyStickerAdapter.VHItem>{
    private final String TAG = "MyStickerAdapter";
    private Context mContext;
    private List<LocalStickerInfo> data;

    public MyStickerAdapter(Context mContext, List<LocalStickerInfo> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public MyStickerAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mysticker_item, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(final MyStickerAdapter.VHItem holder, final int position) {
        final LocalStickerInfo stickerInfo = data.get(position);

        String picPath = MainActivity.STICKERS_NAME+"/"+stickerInfo.getPath()+"/"+stickerInfo.getSticker_name()+stickerInfo.getType();
        Bitmap bmp = BitmapFactory.decodeFile(picPath);
        holder.ivMySticker.setImageBitmap(bmp);

        holder.tvName.setText(stickerInfo.getName());
        holder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleted = true;
//                File f = new File(MainActivity.STICKERS_NAME +"/"+stickerInfo.getPath());
//                deleted = deleteDirectory(f);
                if (deleted){
                    try {
                        Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                        DeleteBuilder deleteBuilder= stickerDao.deleteBuilder();
                        deleteBuilder.where().eq("loginUserId",MainActivity.getUser().getUser_id()).and().eq("path",stickerInfo.getPath());
                        deleteBuilder.delete();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //发广播更新StickerStoreActivity的DOWNLOAD or √
                    Intent intent = new Intent(MyStickerActivity.ACTION_UPDATE);
                    intent.putExtra("path",stickerInfo.getPath());
                    mContext.sendBroadcast(intent);

                    int position = data.indexOf(stickerInfo);
                    data.remove(position);
                    notifyItemRemoved(position);

                }
            }
        });
    }

    private boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder{
        private ImageView ivMySticker;
        private TextView tvName;
        private TextView tvRemove;

        public VHItem(View itemView) {
            super(itemView);
            ivMySticker = (ImageView) itemView.findViewById(R.id.iv_my_sticker);
            tvName = (TextView) itemView.findViewById(R.id.tv_sticker_name);
            tvRemove = (TextView) itemView.findViewById(R.id.tv_remove);


        }
    }

}
