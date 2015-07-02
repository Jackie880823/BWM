package com.madx.bwm.ui.more.sticker;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.StickerGroupAdapter;
import com.madx.bwm.adapter.StickerItemAdapter;
import com.madx.bwm.dao.LocalStickerInfoDao;
import com.madx.bwm.entity.LocalStickerInfo;
import com.madx.bwm.entity.StickerGroupEntity;
import com.madx.bwm.entity.StickerItemEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.BaseActivity;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LogUtil;
import com.madx.bwm.util.ZipUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.madx.bwm.R.color.tab_color_normal;


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
            initDownloadView();
        }

        VolleyUtil.initNetworkImageView(this, insideSticker,
                String.format(Constant.API_STICKERSTORE_FIRST_STICKER, MainActivity.getUser().getUser_id(), stickerGroupEntity.getFirst_sticker(), stickerGroupEntity.getPath(), stickerGroupEntity.getType()),
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
        List<LocalStickerInfo> data = new ArrayList<>();
        try {       //查询数据,看表情包是否存在  where name = stickerGroupEntity.getName()
            Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
            data = stickerDao.queryForEq("name",stickerGroupEntity.getName());
            Log.i(TAG,"==========data.size============="+data.size());
            if(data.size() > 0){
                tvDownload.setText("Downloaded");
                tvDownload.setBackgroundColor(tab_color_normal);
                tvDownload.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
                if(finished>=100) {
                    //
                    pbProgress.setVisibility(View.INVISIBLE);
                    pbProgress.setProgress(0);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvDownload.setText("Downloaded");
                    tvDownload.setBackgroundColor(tab_color_normal);
                    tvDownload.setEnabled(false);

                    //插入sticker info
                    try {
                        LocalStickerInfo stickerInfo = new LocalStickerInfo();
                        stickerInfo.setName(stickerGroupEntity.getName());
                        stickerInfo.setPath(stickerGroupEntity.getPath());
                        stickerInfo.setSticker_name(stickerGroupEntity.getFirst_sticker());
                        stickerInfo.setVersion(stickerGroupEntity.getVersion());
                        stickerInfo.setType(stickerGroupEntity.getType());
                        stickerInfo.setPosition(position);
                        LocalStickerInfoDao.getInstance(StickerDetailActivity.this).addOrUpdate(stickerInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onResult(String response) {
                File zipFile = new File(target);
                try {
                    ZipUtils.unZipFile(zipFile, MainActivity.STICKERS_NAME);
                    zipFile.delete();
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


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initAdapter() {
        adapter = new StickerItemAdapter(this,data,stickerGroupEntity);

//        gvSticker.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                return false;
//            }
//        });

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
                        tvDownload.setVisibility(View.VISIBLE);
                        tvDownload.setText("Downloaded");
                        tvDownload.setBackgroundColor(tab_color_normal);
                        tvDownload.setEnabled(false);
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
                        tvDownload.setVisibility(View.VISIBLE);
                        tvDownload.setText("Downloaded");
                        tvDownload.setBackgroundColor(tab_color_normal);
                        tvDownload.setEnabled(false);
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
