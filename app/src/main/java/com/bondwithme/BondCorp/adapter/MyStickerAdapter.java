package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bondwithme.BondCorp.App;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.LocalStickerInfo;
import com.bondwithme.BondCorp.ui.more.sticker.MyStickerActivity;
import com.bondwithme.BondCorp.util.FileUtil;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * 本地表情包列表适配器
 * Created by heweidong on 15/6/11.
 */
public class MyStickerAdapter extends RecyclerView.Adapter<MyStickerAdapter.VHItem> {
    private final String TAG = "MyStickerAdapter";
    private Context mContext;
    private List<LocalStickerInfo> data;

    private String path;
    private String DEFAULT_STICKER_1 = "PapaPanda2";
    private String DEFAULT_STICKER_2 = "Barry2";
    private String DEFAULT_STICKER_3 = "MamaHippo";
    private String DEFAULT_STICKER_4 = "GranpaTurtle";
    private String DEFAULT_STICKER_5 = "GranmaGoose";
    private String DEFAULT_STICKER_6 = "Bunny";


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

//        String picPath = MainActivity.STICKERS_NAME + "/" + stickerInfo.getPath() + "/S/1" + stickerInfo.getType();
        String picPath = FileUtil.getSmallStickerPath(mContext, stickerInfo.getPath(), "1", stickerInfo.getType());
        Bitmap bmp = BitmapFactory.decodeFile(picPath);
//        Bitmap bmp = LocalImageLoader.loadBitmapFromFile(mContext, picPath, holder.ivMySticker.getWidth(), holder.ivMySticker.getHeight());
        holder.ivMySticker.setImageBitmap(bmp);
        holder.tvName.setText(stickerInfo.getName());

//        path = stickerInfo.getPath();
//        if (path.equals(DEFAULT_STICKER_1)|path.equals(DEFAULT_STICKER_2)|path.equals(DEFAULT_STICKER_3)|path.equals(DEFAULT_STICKER_4)|path.equals(DEFAULT_STICKER_5)|path.equals(DEFAULT_STICKER_6)){
        if (LocalStickerInfo.DEFAULT_STICKER.equals(stickerInfo.getDefaultSticker())) {
            holder.tvRemove.setVisibility(View.GONE);
        } else {
            holder.tvRemove.setVisibility(View.VISIBLE);
        }
        holder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean deleted = true;
//                File f = new File(MainActivity.STICKERS_NAME +"/"+stickerInfo.getPath());
//                deleted = deleteDirectory(f);
//                if (deleted){
                try {
                    Dao<LocalStickerInfo, Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                    stickerDao.delete(stickerInfo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //发广播更新StickerStoreActivity的DOWNLOAD or √
                Intent intent = new Intent(MyStickerActivity.ACTION_UPDATE);
                intent.putExtra(StickerGroupAdapter.PATH, stickerInfo.getPath());
//                    intent.putExtra("position",stickerInfo.getPosition());
                mContext.sendBroadcast(intent);

                int position = data.indexOf(stickerInfo);
                if (position != -1) {
                    data.remove(position);
                    notifyItemRemoved(position);
                }
//                }
            }
        });
    }

    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
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
