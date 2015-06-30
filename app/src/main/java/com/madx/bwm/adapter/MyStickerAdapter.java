package com.madx.bwm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.madx.bwm.App;
import com.madx.bwm.R;
import com.madx.bwm.entity.LocalStickerInfo;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.more.sticker.MyStickerActivity;
import com.madx.bwm.util.AnimatedGifDrawable;
import com.madx.bwm.util.FileUtil;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.Integer;import java.lang.Override;import java.lang.String;import java.sql.SQLException;
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

        //设置sticker icon                                        /FamilyWishes
        String path = MainActivity.STICKERS_NAME + File.separator + stickerInfo.getPath();
        File file = new File(path);
        File[] files = file.listFiles();
        if (null != files && files.length > 0) {
            for (File file1 : files) {
                String filePath = file1.getAbsolutePath();
                if (filePath.substring(filePath.lastIndexOf(File.separator) + 1).contains("B")) {
                    try {
                        File f = new File(filePath);
                        InputStream inputStream = new FileInputStream(f);//mContext.getAssets().open(filePath);
                        if (filePath.endsWith("gif")) {
                            AnimatedGifDrawable animatedGifDrawable = new AnimatedGifDrawable(mContext.getResources(), 0, inputStream, null);
                            Drawable drawable = animatedGifDrawable.getDrawable();
                            holder.ivMySticker.setImageDrawable(drawable);
                        } else {
                            holder.ivMySticker.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //FIRST_STICKER_LIST.add(filePath);
                    break;
                }
            }

        }

//        String picPath = MainActivity.STICKERS_NAME+"/"+stickerInfo.getPath()+"/"+stickerInfo.getSticker_name()+stickerInfo.getType();
//        Bitmap bmp = BitmapFactory.decodeFile(picPath);
//        holder.ivMySticker.setImageBitmap(bmp);

        holder.tvName.setText(stickerInfo.getName());
        holder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleted = false;
                File f = new File(MainActivity.STICKERS_NAME +"/"+stickerInfo.getPath());
                deleted = deleteDirectory(f);
                if (deleted){
                    try {
                        Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                        stickerDao.delete(stickerInfo);
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
