package com.bondwithme.BondWithMe.ui.more.sticker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.StickerGroupAdapter;
import com.bondwithme.BondWithMe.adapter.StickerPagerAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.StickerBannerEntity;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
import com.bondwithme.BondWithMe.widget.FullyLinearLayoutManager;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heweidong on 2015/6/7.
 */
public class StickerStoreActivity extends BaseActivity {

    private static final String TAG= StickerStoreActivity.class.getSimpleName();
    private static final String GET_STICKER_GROUP = TAG + "GET_STICKER_GROUP";
    private static final String GET_STICKER_BANNER = TAG + "GET_STICKER_BANNER";
    public static final String ACTION_UPDATE = "ACTION_UPDATE_FROM_STICKER_STORE";
    public static final String FINISHED = "finished";


    ProgressDialog mProgressDialog;

    private ViewPager vp;
    private StickerPagerAdapter stickerPagerAdapter;
    private List<View> views;
    private RecyclerView recyclerViewList;
    private LinearLayoutManager llm;
    private  List<StickerGroupEntity> dataStickerGroup = new ArrayList<>();
    private  List<StickerBannerEntity> dataStickerBanner = new ArrayList<>();
    private  List<StickerGroupEntity> data = new ArrayList<>();
    private final int AUTO_PLAY = 1;
    private ScrollView scrollView;
    int finished;
    int positionFromStickerDetail;



    @Override
    public int getLayout() {
        return R.layout.activity_sticker_store;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        tvTitle.setText(getResources().getString(R.string.text_sticker_store));
        rightButton.setImageResource(R.drawable.stickies_setting);

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {
//        rightButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                Intent intent = new Intent(StickerStoreActivity.this, MyStickerActivity.class);
                startActivity(intent);
//            }
//        });
    }




    @Override
    protected Fragment getFragment() {
        return  null;
    }


    @Override
    public void initView() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, getString(R.string.text_loading));
        }
        mProgressDialog.show();

        scrollView = getViewById(R.id.sc_sticker_store);
        vp = getViewById(R.id.viewpager_sticker);



        recyclerViewList = getViewById(R.id.recyclerview_sticker);
        llm = new FullyLinearLayoutManager(this);
        recyclerViewList.setLayoutManager(llm);
        recyclerViewList.setHasFixedSize(true);
        initAdapter();
        requestData();
        initPagerAdapter();

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(StickerDetailActivity.ACTION_UPDATE);
        filter.addAction(MyStickerActivity.ACTION_UPDATE);
        filter.addAction(StickerStoreActivity.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);

    }
    /**sticker banner Adapter*/
    private void initPagerAdapter() {
        int stickerBanner = dataStickerBanner.size();
        int i;
        int position = 0;
        views = new ArrayList<View>();
        for(i=0; i<stickerBanner; i++){
            StickerBannerEntity stickerBannerEntity = dataStickerBanner.get(i);
            String stickerGroupPath = stickerBannerEntity.getSticker_group_path();
            position = getPosition(stickerGroupPath,dataStickerGroup);
            StickerGroupEntity stickerGroupEntity = null;
            if (dataStickerGroup.size()>0){
                stickerGroupEntity = dataStickerGroup.get(position);
            }

            NetworkImageView view =  new NetworkImageView(this);
            addView(i, view,stickerGroupEntity,position);
            views.add(view);
        }
        mProgressDialog.dismiss();
        stickerPagerAdapter = new StickerPagerAdapter(views);
        vp.setAdapter(stickerPagerAdapter);

    }

    //获取广告图对应的表情包的position
    private int getPosition(String stickerGroupPath, List<StickerGroupEntity> dataStickerGroup) {
        String path = null;
        for (int i=0; i<dataStickerGroup.size();i++){
            path = dataStickerGroup.get(i).getPath();
            if (path.equals(stickerGroupPath)){
                return i;
            }
        }
        return 0;
    }

    /**sticker group Adapter*/
    private void initAdapter() {
        StickerGroupAdapter adapter = new StickerGroupAdapter(this,dataStickerGroup);
        adapter.setItemClickListener(new StickerGroupAdapter.ItemClickListener() {
            @Override
            public void itemClick(StickerGroupEntity stickerGroupEntity, int position) {
                Intent intent = new Intent(StickerStoreActivity.this, StickerDetailActivity.class);
                intent.putExtra(StickerGroupAdapter.STICKER_GROUP, dataStickerGroup.get(position));
                intent.putExtra(StickerGroupAdapter.POSITION, position);
                intent.putExtra(FINISHED, finished);
                intent.putExtra("positionFromStickerDetail",positionFromStickerDetail);
                startActivity(intent);



            }
        });
        adapter.setDownloadClickListener(new StickerGroupAdapter.DownloadClickListener() {
            @Override
            public void downloadClick(StickerGroupEntity stickerGroupEntity, int position) {
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(position);
                ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
                TextView tvDownload = (TextView)holder.itemView.findViewById(R.id.tv_download);
                tvDownload.setVisibility(View.INVISIBLE);
                pbDownload.setVisibility(View.VISIBLE);
                downloadZip(holder,position);
            }
        });




        recyclerViewList.setAdapter(adapter);

    }

    /**下载sticker Group zip*/
    private void downloadZip(final StickerGroupAdapter.VHItem holder,final int position) {
        final ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
        final ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
        final StickerGroupEntity stickerGroupEntity = dataStickerGroup.get(position);
        String urlString = String.format(Constant.API_STICKER_ZIP, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath());
        final String target = FileUtil.getCacheFilePath(this) + String.format("/%s.zip", "" + stickerGroupEntity.getName());
        DownloadRequest download = new HttpTools(this).download(urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                pbDownload.setVisibility(View.INVISIBLE);
                pbDownload.setProgress(0);
                ivExist.setVisibility(View.VISIBLE);

                //插入sticker info
                try {
                    LocalStickerInfo stickerInfo = new LocalStickerInfo();
                    stickerInfo.setName(stickerGroupEntity.getName());
                    stickerInfo.setPath(stickerGroupEntity.getPath());
                    stickerInfo.setSticker_name(stickerGroupEntity.getFirst_sticker());
                    stickerInfo.setVersion(stickerGroupEntity.getVersion());
                    stickerInfo.setType(stickerGroupEntity.getType());
                    stickerInfo.setOrder(System.currentTimeMillis());
                    LocalStickerInfoDao.getInstance(StickerStoreActivity.this).addOrUpdate(stickerInfo);
                    Log.i(TAG, "=======tickerInfo==========" +stickerInfo.toString() );

                } catch (Exception e) {
                    LogUtil.e(TAG,"插入sticker info",e);
                }


            }

            @Override
            public void onResult(String response) {
                File zipFile = new File(target);
                //解压
                try {
                    ZipUtils.unZipFile(zipFile, MainActivity.STICKERS_NAME);
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
                pbDownload.setProgress(finished);


                //发广播更新progressbar
                Intent intent = new Intent(StickerStoreActivity.ACTION_UPDATE);
                intent.putExtra(FINISHED,finished);
                intent.putExtra(StickerGroupAdapter.POSITION,position);
                sendBroadcast(intent);

            }
        });
    }


    //加载View pager中的表情引导图，并设监听事件
    private void addView(int i, NetworkImageView view, final StickerGroupEntity stickerGroupEntity, final int position) {
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        VolleyUtil.initNetworkImageView(this, view, String.format(Constant.API_STICKER_BANNER_PIC, dataStickerBanner.get(i).getBanner_photo()),
                R.drawable.network_image_default, R.drawable.network_image_default);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StickerStoreActivity.this,StickerDetailActivity.class);
                intent.putExtra(StickerGroupAdapter.STICKER_GROUP, stickerGroupEntity);
                intent.putExtra(StickerGroupAdapter.POSITION,position);
                StickerStoreActivity.this.startActivity(intent);
            }
        });
    }


    //自动播放View pager

    @Override
    protected void onResume() {
        super.onResume();
        data = dataStickerGroup;
//        handler.sendEmptyMessageDelayed(AUTO_PLAY, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        data = dataStickerGroup;
    }

    //    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case AUTO_PLAY:
//                    int totalItem = views.size();
//                    int currentItem = vp.getCurrentItem();
//                    int toItem = currentItem + 1 == totalItem ? 0 :currentItem + 1;
////                    Log.i("TAG", "totalItem:" + totalItem + "  currentItem:" + currentItem);
//                    vp.setCurrentItem(toItem, true);
//                    this.sendEmptyMessageDelayed(AUTO_PLAY,4000);
//            }
//        }
//    };

    @Override
    protected void onStop() {
        super.onStop();
//        handler.removeMessages(AUTO_PLAY);
    }

    @Override
    public void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", MainActivity.getUser().getUser_id());


        //获取sticker group list
        new HttpTools(this).get(Constant.API_STICKER_GROUP,params, GET_STICKER_GROUP, new HttpCallback() {
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

                dataStickerGroup = gson.fromJson(response, new TypeToken<ArrayList<StickerGroupEntity>>() {
                }.getType());

                initAdapter();

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(StickerStoreActivity.this, R.string.msg_action_failed);

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
        //获取 sticker banner 广告图
        new HttpTools(this).get(Constant.API_STICKER_BANNER, null, GET_STICKER_BANNER, new HttpCallback() {
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

                dataStickerBanner = gson.fromJson(response, new TypeToken<ArrayList<StickerBannerEntity>>() {
                }.getType());

                initPagerAdapter();

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(StickerStoreActivity.this, R.string.msg_action_failed);


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
            if(StickerDetailActivity.ACTION_UPDATE.equals(intent.getAction())){
                finished = intent.getIntExtra(FINISHED,0);
                positionFromStickerDetail = intent.getIntExtra(StickerGroupAdapter.POSITION,0);
                Log.i(TAG, "=======positionFromStickerDetail========" + positionFromStickerDetail);
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(positionFromStickerDetail);
                if ( holder!= null){
                    ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
                    ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
                    TextView tvDownload = (TextView)holder.itemView.findViewById(R.id.tv_download);
                    tvDownload.setVisibility(View.INVISIBLE);
                    pbDownload.setVisibility(View.VISIBLE);
                    pbDownload.setProgress(finished);
                    if (finished == 100){
                        pbDownload.setVisibility(View.INVISIBLE);
                        ivExist.setVisibility(View.VISIBLE);
                    }

                }
            }else if (MyStickerActivity.ACTION_UPDATE.equals(intent.getAction())){
                String path = intent.getStringExtra("path");
                int position = getPosition(path, data);
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(position);
                ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
                TextView tvDownload = (TextView)holder.itemView.findViewById(R.id.tv_download);
                ivExist.setVisibility(View.INVISIBLE);
                tvDownload.setVisibility(View.VISIBLE);
            }else if (StickerStoreActivity.ACTION_UPDATE.equals(intent.getAction())){
                int position = intent.getIntExtra(StickerGroupAdapter.POSITION,0);
                finished = intent.getIntExtra(FINISHED,0);
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(position);
                if ( holder!= null){
                    ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
                    ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
                    TextView tvDownload = (TextView)holder.itemView.findViewById(R.id.tv_download);
                    tvDownload.setVisibility(View.INVISIBLE);
                    pbDownload.setVisibility(View.VISIBLE);
                    pbDownload.setProgress(finished);
                    if (finished == 100){
                        pbDownload.setVisibility(View.INVISIBLE);
                        ivExist.setVisibility(View.VISIBLE);
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
