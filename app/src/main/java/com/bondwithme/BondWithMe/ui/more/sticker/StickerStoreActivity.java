package com.bondwithme.BondWithMe.ui.more.sticker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.StickerGroupAdapter;
import com.bondwithme.BondWithMe.adapter.StickerPagerAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.StickerBannerEntity;
import com.bondwithme.BondWithMe.entity.StickerBannerPic;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
import com.bondwithme.BondWithMe.widget.FullyLinearLayoutManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by heweidong on 2015/6/7.
 */
public class StickerStoreActivity extends BaseActivity implements StickerBannerPic.DownloadBannerListener,View.OnTouchListener {

    private static final String TAG = StickerStoreActivity.class.getSimpleName();
    private static final String GET_STICKER_GROUP = TAG + "GET_STICKER_GROUP";
    private static final String GET_STICKER_BANNER = TAG + "GET_STICKER_BANNER";
    public static final String ACTION_UPDATE = "ACTION_UPDATE_FROM_STICKER_STORE";
    public static final String ACTION_FINISHED = "ACTION_FINISHED_FROM_STICKER_STORE";
    public static final String FINISHED = "finished";


    View mProgressDialog;

    private StickerPagerAdapter stickerPagerAdapter;
    private List<View> views;
    private RecyclerView recyclerViewList;
    private LinearLayoutManager llm;
    private List<StickerGroupEntity> dataStickerGroup = new ArrayList<>();
    private List<StickerBannerEntity> dataStickerBanner = new ArrayList<>();
    private  List<StickerGroupEntity> data = new ArrayList<>();
    private final int AUTO_PLAY = 1;
    private final int INIT_ADAPTER = 2;
    private final int DISMISS_DIALOG = 3;
    private ScrollView scrollView;
    int finished;
    int positionFromStickerDetail = -1;
    private ImageSwitcher isStickerBanner;
    private int currentItem;
    private List<Uri> uriList;
    StickerBannerPic stickerBannerPic;



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

        Intent intent = new Intent(StickerStoreActivity.this, MyStickerActivity.class);
        startActivity(intent);
    }


    @Override
    protected Fragment getFragment() {
        return null;
    }


    @Override
    public void initView() {
        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);

        scrollView = getViewById(R.id.sc_sticker_store);
        isStickerBanner = getViewById(R.id.is_sticker_banner);
        initStickerBanner();



        recyclerViewList = getViewById(R.id.recyclerview_sticker);
        llm = new FullyLinearLayoutManager(this);
        recyclerViewList.setLayoutManager(llm);
        recyclerViewList.setHasFixedSize(true);
        initAdapter();      //加载表情包列表；
        requestData();



        //注册广播接收器，更新progressbar、Download按钮状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(StickerDetailActivity.ACTION_UPDATE);
        filter.addAction(MyStickerActivity.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);

    }

    /**ImageSwitcher for sticker banner */
    private void initStickerBanner() {
        isStickerBanner.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView i = new ImageView(StickerStoreActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_XY);
                i.setLayoutParams(new ImageSwitcher.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
                return i;
            }
        });
        isStickerBanner.setOnTouchListener(this);


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
        return 0;
    }

    /**
     * sticker group Adapter
     */
    private void initAdapter() {
        StickerGroupAdapter adapter = new StickerGroupAdapter(this, dataStickerGroup);
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
                TextView tvDownload = (TextView) holder.itemView.findViewById(R.id.tv_download);
                tvDownload.setVisibility(View.INVISIBLE);
                pbDownload.setVisibility(View.VISIBLE);
                downloadZip(holder, position);
            }
        });




        recyclerViewList.setAdapter(adapter);

    }

    /**
     * 下载sticker Group zip
     */
    private void downloadZip(final StickerGroupAdapter.VHItem holder, final int position) {
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
                    LocalStickerInfoDao.getInstance(StickerStoreActivity.this).addOrUpdate(stickerInfo);
                    Log.i(TAG, "=======tickerInfo==========" + stickerInfo.toString());

                    Intent intent = new Intent(StickerStoreActivity.ACTION_FINISHED);
                    sendBroadcast(intent);

                } catch (Exception e) {
                    LogUtil.e(TAG, "插入sticker info", e);
                }

                pbDownload.setVisibility(View.INVISIBLE);
                pbDownload.setProgress(0);
                ivExist.setVisibility(View.VISIBLE);



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


                //发广播更新StickerDetailActivity的progressbar
                Intent intent = new Intent(StickerStoreActivity.ACTION_UPDATE);
                intent.putExtra(FINISHED, finished);
                intent.putExtra(StickerGroupAdapter.POSITION, position);
                sendBroadcast(intent);

            }
        });
    }




    @Override
    protected void onResume() {
        super.onResume();
        data = dataStickerGroup;
        handler.sendEmptyMessageDelayed(AUTO_PLAY, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        data = dataStickerGroup;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case AUTO_PLAY:
                    int totalItem = 0;
                    if (dataStickerBanner != null && uriList != null && uriList.size()>0){
                        totalItem = dataStickerBanner.size();
                        currentItem = currentItem + 1 == totalItem ? 0 : currentItem + 1;
                        setImageSwitcher(currentItem);
                    }
                    handler.sendEmptyMessageDelayed(AUTO_PLAY, 5000);
            }
        }
    };

    protected void setImageSwitcher(int currentItem){
        rightInAnim();
        isStickerBanner.setImageURI(uriList.get(currentItem));
        setSwitcherClick();

    }

    private void rightInAnim() {
        Animation animIn = AnimationUtils.loadAnimation(this,
                R.anim.slide_in_right);
        animIn.setFillAfter(true);
        isStickerBanner.setInAnimation(animIn);

        Animation animOut = AnimationUtils.loadAnimation(this,
                R.anim.slide_out_left);
        animOut.setFillAfter(true);
        isStickerBanner.setOutAnimation(animOut);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(AUTO_PLAY);
    }

    @Override
    public void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", MainActivity.getUser().getUser_id());


        //获取sticker group list
        new HttpTools(this).get(Constant.API_STICKER_GROUP, params, GET_STICKER_GROUP, new HttpCallback() {
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

                stickerBannerPic = new StickerBannerPic(dataStickerBanner, StickerStoreActivity.this);
                stickerBannerPic.setDownloadListener(StickerStoreActivity.this);
                stickerBannerPic.getUri();
                LogUtil.d(TAG, "=======stickerBannerPic====" + stickerBannerPic.toString());


//                initPagerAdapter();

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

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (StickerDetailActivity.ACTION_UPDATE.equals(intent.getAction())) {
                finished = intent.getIntExtra(FINISHED, 0);
                positionFromStickerDetail = intent.getIntExtra(StickerGroupAdapter.POSITION, 0);
                Log.i(TAG, "=======positionFromStickerDetail========" + positionFromStickerDetail);
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(positionFromStickerDetail);
                if (holder != null) {
                    ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
                    ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
                    TextView tvDownload = (TextView) holder.itemView.findViewById(R.id.tv_download);
                    tvDownload.setVisibility(View.INVISIBLE);
                    pbDownload.setVisibility(View.VISIBLE);
                    pbDownload.setProgress(finished);
                    if (finished == 100) {
                        pbDownload.setVisibility(View.INVISIBLE);
                        ivExist.setVisibility(View.VISIBLE);
                    }

                }
            } else if (MyStickerActivity.ACTION_UPDATE.equals(intent.getAction())) {
                int position = getPosition(intent.getStringExtra("sticker_path"),dataStickerGroup);
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(position);
                ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
                TextView tvDownload = (TextView) holder.itemView.findViewById(R.id.tv_download);
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
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    @Override
    public void downloadFinish() {
        LogUtil.d(TAG, "=====downloadFinish=====");
        uriList = stickerBannerPic.uriList;
        LogUtil.d(TAG, "==========uriList.size()========="+uriList.size());
        if (uriList.size() > 0){
            isStickerBanner.setImageURI(uriList.get(currentItem));
            setSwitcherClick();
        }
        mProgressDialog.setVisibility(View.INVISIBLE);
    }

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float downX = 0;
    float upX = 0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            downX = event.getX();
            LogUtil.d(TAG,"===downX==="+downX);
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            upX = event.getX();
            LogUtil.d(TAG, "===upX===" + upX);
            if(downX - upX > 80) {      //左滑
                int totalItem = 0;
                if (dataStickerBanner.size() > 0 && uriList.size()>0){
                    totalItem = dataStickerBanner.size();
                    currentItem = currentItem + 1 == totalItem ? 0 : currentItem + 1;
                    rightInAnim();
                    if (uriList.size() > 0){
                        isStickerBanner.setImageURI(uriList.get(currentItem));
                    }
                    LogUtil.d(TAG,"=========currentItem========"+currentItem);
                }
                return true;
            } else if(upX - downX > 80) {       //右滑
                int totalItem = 0;
                if (dataStickerBanner.size() > 0 && uriList.size()>0){
                    totalItem = dataStickerBanner.size();
                    currentItem = currentItem - 1 < 0 ? totalItem - 1 : currentItem - 1;
                    Animation animIn = AnimationUtils.loadAnimation(this,
                            R.anim.slide_in_left);
                    animIn.setFillAfter(true);
                    isStickerBanner.setInAnimation(animIn);

                    Animation animOut = AnimationUtils.loadAnimation(this,
                            R.anim.slide_out_right);
                    animOut.setFillAfter(true);
                    isStickerBanner.setOutAnimation(animOut);
                    if (uriList.size() > 0){
                        isStickerBanner.setImageURI(uriList.get(currentItem));
                    }
                    LogUtil.d(TAG,"=========currentItem========"+currentItem);
                }
                return true;
            }
        }

        if (Math.abs(downX - upX) < 5){
            LogUtil.d(TAG,"====return_false===");
            setSwitcherClick();
            return false;
        }

        LogUtil.d(TAG,"====return_false===");
        return false;

    }

    private void setSwitcherClick() {

        if (dataStickerBanner != null && dataStickerGroup != null){
            if(dataStickerBanner.size() > 0 && dataStickerGroup.size() > 0){
                final int position = getPosition(dataStickerBanner.get(currentItem).getSticker_group_path(),dataStickerGroup);
                final StickerGroupEntity stickerGroupEntity = dataStickerGroup.get(position);
                isStickerBanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.d(TAG,"=========currentItem========"+currentItem);
                        LogUtil.d(TAG, "========onClick======");
                        Intent intent = new Intent(StickerStoreActivity.this, StickerDetailActivity.class);
                        intent.putExtra(StickerGroupAdapter.STICKER_GROUP, stickerGroupEntity);
                        intent.putExtra(StickerGroupAdapter.POSITION, position);
                        StickerStoreActivity.this.startActivity(intent);
                    }
                });
            }

        }
    }
}
