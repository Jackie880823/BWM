package com.bondwithme.BondWithMe.ui.more.sticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.StickerGroupAdapter;
import com.bondwithme.BondWithMe.adapter.StickerItemAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.entity.StickerItemEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StickerDetailActivity extends BaseActivity {
    private String TAG = StickerDetailActivity.class.getSimpleName();
    Intent intent = null;
    private StickerGroupEntity stickerGroupEntity = null;
    private List<StickerItemEntity> data = new ArrayList<>();
    private GridView gvSticker;
    private StickerItemAdapter adapter;
    private ProgressBar pbProgress;
    private TextView tvDownload;
    private int position;
    public static final String ACTION_UPDATE = "ACTION_UPDATE_FROM_STICKER_DETAIL";
    int finished;
    private final int UPDATE_PROGRESSBAR =1;


    @Override
    public int getLayout() {
        return R.layout.activity_sticker_detail;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        tvTitle.setText(stickerGroupEntity.getName());
                rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        intent = getIntent();
        stickerGroupEntity = (StickerGroupEntity) intent.getSerializableExtra(StickerGroupAdapter.STICKER_GROUP);
        position = intent.getIntExtra(StickerGroupAdapter.POSITION,0);
        finished = intent.getIntExtra("finished",0);
        int loadingPosition = intent.getIntExtra("positionFromStickerDetail",0);

        NetworkImageView insideSticker = getViewById(R.id.iv_inside_sticker);
        TextView insideStickerName = getViewById(R.id.tv_inside_sticker_name);
        TextView desc = getViewById(R.id.tv_description);
        desc.setText("sticker_type: "+stickerGroupEntity.getType());
        TextView price = getViewById(R.id.price);
        tvDownload = getViewById(R.id.tv_inside_download);
        gvSticker = getViewById(R.id.gv_sticker);
        pbProgress = getViewById(R.id.pb_download);

        if(finished>0 && finished<100 && position == loadingPosition) {
            tvDownload.setVisibility(View.INVISIBLE);
            pbProgress.setVisibility(View.VISIBLE);
            pbProgress.setProgress(finished);
        } else{
            tvDownload.setVisibility(View.VISIBLE);
            pbProgress.setVisibility(View.INVISIBLE);
            pbProgress.setProgress(0);
            initDownloadView();
        }

        VolleyUtil.initNetworkImageView(this, insideSticker,
                String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), "1_B", stickerGroupEntity.getPath(), stickerGroupEntity.getType()),
                R.drawable.network_image_default, R.drawable.network_image_default);
        insideStickerName.setText(stickerGroupEntity.getName());
        if("0".equals(stickerGroupEntity.getPrice())){
            price.setText(getResources().getString(R.string.free));
        }else{
            price.setText(stickerGroupEntity.getPrice());
        }

        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadZip(stickerGroupEntity, position);
                tvDownload.setVisibility(View.INVISIBLE);
                pbProgress.setVisibility(View.VISIBLE);
            }
        });


        initAdapter();
        requestData();

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(StickerStoreActivity.ACTION_UPDATE);
        filter.addAction(StickerDetailActivity.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);


    }



    private void initDownloadView() {
        try {       //查询数据,看表情包是否存在  where name = stickerGroupEntity.getName()
            LocalStickerInfoDao dao = LocalStickerInfoDao.getInstance(this);
            if(dao.hasDownloadSticker(stickerGroupEntity.getPath())){
                setTvDownloaded();
            }
        } catch (Exception e) {
            LogUtil.e(TAG,"",e);
        }

    }

    private void setTvDownloaded() {
        tvDownload.setText(getResources().getString(R.string.Downloaded));
        tvDownload.setTextColor(getResources().getColor(R.color.default_text_color_dark));
        tvDownload.setBackgroundColor(getResources().getColor(R.color.default_unenable_item_bg));
        tvDownload.setEnabled(false);
    }

    //下载表情包
    private void downloadZip(final StickerGroupEntity stickerGroupEntity, final int position) {
        String urlString = String.format(Constant.API_STICKER_ZIP, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath());
        final String target = FileUtil.getCacheFilePath(this) + String.format("/%s.zip", "" + stickerGroupEntity.getName());
        new HttpTools(this).download(urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                File zipFile = new File(target);
                try {
                    ZipUtils.unZipFile(zipFile, MainActivity.STICKERS_NAME);
                    zipFile.delete();

                    if(finished>=100) {
                        //
                        pbProgress.setVisibility(View.INVISIBLE);
                        pbProgress.setProgress(0);
                        tvDownload.setVisibility(View.VISIBLE);
                        setTvDownloaded();

                        //插入sticker info
                        try {
                            Dao<LocalStickerInfo, Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                            LocalStickerInfo stickerInfo = new LocalStickerInfo();
                            stickerInfo.setName(stickerGroupEntity.getName());
                            stickerInfo.setPath(stickerGroupEntity.getPath());
                            stickerInfo.setSticker_name(stickerGroupEntity.getFirst_sticker());
                            stickerInfo.setVersion(stickerGroupEntity.getVersion());
                            stickerInfo.setType(stickerGroupEntity.getType());
                            stickerInfo.setOrder(System.currentTimeMillis());
                            LocalStickerInfoDao.getInstance(StickerDetailActivity.this).addOrUpdate(stickerInfo);
                            Log.i(TAG, "=======tickerInfo==========" + stickerInfo.toString());

                            Intent intent = new Intent(StickerStoreActivity.ACTION_FINISHED);
                            sendBroadcast(intent);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
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
                //更新item中的进度条
                finished = (int) (current * 100 / count);
                pbProgress.setProgress(finished);

                //发广播更新StickerStoreActivity的progressbar
                Intent intent = new Intent(StickerDetailActivity.ACTION_UPDATE);
                intent.putExtra("finished",finished);
                intent.putExtra(StickerGroupAdapter.POSITION, position);
                sendBroadcast(intent);
            }
        });
    }

    private void initAdapter() {
        adapter = new StickerItemAdapter(this,data,stickerGroupEntity);
        gvSticker.setAdapter(adapter);

    }

    @Override
    public void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("path", stickerGroupEntity.getPath());


        new HttpTools(this).get(Constant.API_STICKER_ITEM,params,TAG,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                data = gson.fromJson(response, new TypeToken<ArrayList<StickerItemEntity>>() {
                }.getType());
                initAdapter();
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

            }
        });

    }


    /**更新UI的广播接收器*/
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(StickerStoreActivity.ACTION_UPDATE.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished",0);
                int positionLoading = intent.getIntExtra(StickerGroupAdapter.POSITION,0);
                if (positionLoading == position) {
                    tvDownload.setVisibility(View.INVISIBLE);
                    pbProgress.setVisibility(View.VISIBLE);
                    pbProgress.setProgress(finished);
                    if (finished == 100){
                        pbProgress.setVisibility(View.INVISIBLE);
                        pbProgress.setProgress(0);
                        tvDownload.setVisibility(View.VISIBLE);
                        setTvDownloaded();
                    }

                }

            }else if (StickerDetailActivity.ACTION_UPDATE.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished",0);
                int positionLoading = intent.getIntExtra(StickerGroupAdapter.POSITION,0);
                if (positionLoading == position) {
                    tvDownload.setVisibility(View.INVISIBLE);
                    pbProgress.setVisibility(View.VISIBLE);
                    pbProgress.setProgress(finished);
                    if (finished == 100){
                        pbProgress.setVisibility(View.INVISIBLE);
                        pbProgress.setProgress(0);
                        tvDownload.setVisibility(View.VISIBLE);
                        setTvDownloaded();
                    }

                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
