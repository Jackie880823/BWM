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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.StickerGroupAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.StickerBannerEntity;
import com.bondwithme.BondWithMe.entity.StickerGroupEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
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
public class StickerStoreActivity extends BaseActivity implements View.OnTouchListener {

    private static final String TAG = StickerStoreActivity.class.getSimpleName();
    private static final String GET_STICKER_GROUP = TAG + "GET_STICKER_GROUP";
    private static final String GET_STICKER_BANNER = TAG + "GET_STICKER_BANNER";
    public static final String ACTION_UPDATE = "ACTION_UPDATE_FROM_STICKER_STORE";
    public static final String ACTION_FINISHED = "ACTION_FINISHED_FROM_STICKER_STORE";
    public static final String PROGRESS = "progress";


    View stickerProgressDialog;
    //    View adProgressDialog;
    private RecyclerView recyclerViewList;
    private LinearLayoutManager llm;
    private List<StickerGroupEntity> dataStickerGroup = new ArrayList<>();
    private List<StickerBannerEntity> dataStickerBanner = new ArrayList<>();
    private List<StickerGroupEntity> data = new ArrayList<>();
//    private final int AUTO_PLAY = 1;

    //    int positionFromStickerDetail = -1;
    //    private ImageSwitcher isStickerBanner;
    private int currentItem;
    private Map<String, Uri> uriMap = new HashMap<>();
    private static final int SHOW_ADS = 10;
    private static final int DOWNLOAD_DONE = 11;
    StickerGroupAdapter adapter;


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
        /**wing modified for 性能 begin*/
        stickerProgressDialog = getViewById(R.id.sticker_progress);
        stickerProgressDialog.setVisibility(View.VISIBLE);
//        adProgressDialog = getViewById(R.id.ad_progress);
//        adProgressDialog.setVisibility(View.VISIBLE);

        recyclerViewList = getViewById(R.id.recyclerview_sticker);

        final View sticker_new_update = getViewById(R.id.sticker_new_update);
        TextView sticker_store_update = getViewById(R.id.sticker_store_update);
        TextView sticker_store_close = getViewById(R.id.sticker_store_close);
//        llm = new FullyLinearLayoutManager(this);
        llm = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(llm);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setAdapter(adapter);
//        initAdapter();      //加载表情包列表；
//        requestData();
        /**wing modified for 性能 end*/

        String filePath = FileUtil.getSaveRootPath(this, false).getAbsolutePath() + File.separator + "Sticker";
        File file = new File(filePath);
        if (file.exists()) {
            sticker_new_update.setVisibility(View.VISIBLE);
        } else {
            sticker_new_update.setVisibility(View.GONE);
        }
        sticker_store_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<LocalStickerInfo> list = LocalStickerInfoDao.getInstance(StickerStoreActivity.this).queryAllLocalStickerInfo();
                if (list == null || list.size() == 0) {
                    return;
                }
                String filePath = FileUtil.getSaveRootPath(StickerStoreActivity.this, false).getAbsolutePath() + File.separator + "Sticker";
                File file = new File(filePath);
                deleteFile(file);
                StickerGroupEntity groupEntity;
                for (LocalStickerInfo info : list) {
                    groupEntity = new StickerGroupEntity();
                    groupEntity.setType(info.getType());
                    groupEntity.setName(info.getName());
                    groupEntity.setVersion(info.getVersion());
                    groupEntity.setFirst_sticker_code(info.getSticker_name());
                    groupEntity.setPath(info.getPath());
                    updateSticker(groupEntity);
                }
                sticker_new_update.setVisibility(View.GONE);
            }
        });
        sticker_store_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //注册广播接收器，更新progressbar、Download按钮状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(StickerDetailActivity.ACTION_UPDATE);
        filter.addAction(MyStickerActivity.ACTION_UPDATE);
        filter.addAction(StickerStoreActivity.ACTION_UPDATE);
        filter.addAction(StickerStoreActivity.ACTION_FINISHED);
        registerReceiver(mReceiver, filter);


        //恶心
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (finishedAdCount < allAdCount && !hasError) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(DOWNLOAD_DONE);

//                if(adapter!=null) {
//                    adapter.setAds(uriMap);
//                }
            }
        }).start();

    }

    private void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
        }
    }

    public void updateSticker(final StickerGroupEntity stickerGroupEntity) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String urlString = String.format(Constant.API_DOWNLOAD_STICKER_ZIP, MainActivity.getUser().getUser_id(), "1", stickerGroupEntity.getPath());
                final String target = FileUtil.getCacheFilePath(StickerStoreActivity.this) + String.format("/%s.zip", "" + stickerGroupEntity.getName());
                new HttpTools(StickerStoreActivity.this).download(App.getContextInstance(), urlString, target, true, new HttpCallback() {
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
        }.start();
    }

    private int allAdCount = 4;
    private int finishedAdCount;

    /**
     * ImageSwitcher for sticker banner
     */
//    private void initStickerBanner() {
//        isStickerBanner.setFactory(new ViewSwitcher.ViewFactory() {
//            @Override
//            public View makeView() {
//                ImageView i = new ImageView(StickerStoreActivity.this);
//                i.setScaleType(ImageView.ScaleType.FIT_XY);
//                i.setLayoutParams(new ImageSwitcher.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.MATCH_PARENT));
//                return i;
//            }
//        });
//        isStickerBanner.setOnTouchListener(this);
//
//
//    }


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

    List<String> stickers;

    /**
     * sticker group Adapter
     */
    private void initAdapter() {
        /**wing modified for 性能 begin*/
        //查询数据,看表情包是否存在  where name = stickerGroupEntity.getName()
        LocalStickerInfoDao dao = LocalStickerInfoDao.getInstance(this);
        stickers = dao.queryAllSticker();

        /**wing modified for 性能 end*/
        adapter = new StickerGroupAdapter(this, dataStickerGroup, stickers, uriMap);
        adapter.setItemClickListener(new StickerGroupAdapter.ItemClickListener() {
            @Override
            public void itemClick(StickerGroupEntity stickerGroupEntity, int position) {
                Intent intent = new Intent(StickerStoreActivity.this, StickerDetailActivity.class);
                intent.putExtra(StickerGroupAdapter.STICKER_GROUP, dataStickerGroup.get(position));
                intent.putExtra(StickerGroupAdapter.POSITION, position);
//                intent.putExtra(PROGRESS, progress);
//                intent.putExtra("positionFromStickerDetail", positionFromStickerDetail);
                startActivity(intent);
            }
        });
        adapter.setDownloadClickListener(new StickerGroupAdapter.DownloadClickListener() {
            @Override
            public void downloadClick(StickerGroupEntity stickerGroupEntity, int position) {


                //加1(header)
                StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(position + 1);
                ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
                TextView tvDownload = (TextView) holder.itemView.findViewById(R.id.tv_download);
                tvDownload.setVisibility(View.INVISIBLE);
                pbDownload.setVisibility(View.VISIBLE);
                dataStickerGroup.get(position).setDownloading(true);
//                adapter.notifyItemChanged(position+1);
//                downloadZip(holder, position);
                downloadZip(position);
            }
        });


        recyclerViewList.setAdapter(adapter);

    }

    /**
     * 下载sticker Group zip
     */
    private void downloadZip(final int position) {
//    private void downloadZip(final StickerGroupAdapter.VHItem holder, final int position) {
//        final ProgressBar pbDownload = (ProgressBar) holder.itemView.findViewById(R.id.pb_download);
//        final ImageView ivExist = (ImageView) holder.itemView.findViewById(R.id.iv_exist);
        final StickerGroupEntity stickerGroupEntity = dataStickerGroup.get(position);
//        String urlString = String.format(Constant.API_STICKER_ZIP, MainActivity.getUser().getUser_id(), stickerGroupEntity.getPath());
        String urlString = String.format(Constant.API_DOWNLOAD_STICKER_ZIP, MainActivity.getUser().getUser_id(), "1", stickerGroupEntity.getPath());
        final String target = FileUtil.getCacheFilePath(this) + String.format("/%s.zip", "" + stickerGroupEntity.getName());
        DownloadRequest download = new HttpTools(this).download(App.getContextInstance(), urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                if (LocalStickerInfoDao.getInstance(StickerStoreActivity.this).hasDownloadSticker(stickerGroupEntity.getPath())) {
                    Intent intent = new Intent(StickerStoreActivity.ACTION_FINISHED);
                    intent.putExtra(StickerGroupAdapter.POSITION, position);
                    sendBroadcast(intent);
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

                //插入sticker info
                try {
//                    Dao<LocalStickerInfo, Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                    LocalStickerInfo stickerInfo = new LocalStickerInfo();
                    stickerInfo.setName(stickerGroupEntity.getName());
                    stickerInfo.setPath(stickerGroupEntity.getPath());
                    stickerInfo.setSticker_name(stickerGroupEntity.getFirst_sticker_code());
                    stickerInfo.setVersion(stickerGroupEntity.getVersion());
                    stickerInfo.setType(stickerGroupEntity.getType());
                    stickerInfo.setOrder(System.currentTimeMillis());
                    LocalStickerInfoDao.getInstance(StickerStoreActivity.this).addOrUpdate(stickerInfo);
                    Log.i(TAG, "=======tickerInfo==========" + stickerInfo.toString());

                } catch (Exception e) {
                    LogUtil.e(TAG, "插入sticker info", e);
                }

//                pbDownload.setVisibility(View.INVISIBLE);
//                pbDownload.setProgress(0);
//                ivExist.setVisibility(View.VISIBLE);


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
                //发广播更新StickerDetailActivity的progressbar
                Intent intent = new Intent(StickerStoreActivity.ACTION_UPDATE);
                intent.putExtra(PROGRESS, (int) (current * 100 / count));
                intent.putExtra(StickerGroupAdapter.POSITION, position);
                sendBroadcast(intent);

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        data = dataStickerGroup;
//        handler.sendEmptyMessageDelayed(AUTO_PLAY, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        data = dataStickerGroup;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case AUTO_PLAY:
//                    int totalItem = 0;
//                    if (dataStickerBanner != null && uriMap != null && uriMap.size() > 0) {
//                        totalItem = dataStickerBanner.size();
//                        currentItem = currentItem + 1 == totalItem ? 0 : currentItem + 1;
//                        setImageSwitcher(currentItem);
//                    }
//                    handler.sendEmptyMessageDelayed(AUTO_PLAY, 2000);
//                    break;
                case SHOW_ADS:
                    String response = (String) msg.obj;
//                    isStickerBanner = getViewById(R.id.is_sticker_banner);
//                    initStickerBanner();

                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();

                    dataStickerBanner = gson.fromJson(response, new TypeToken<ArrayList<StickerBannerEntity>>() {
                    }.getType());

                    downloadPic();
                    break;
                case DOWNLOAD_DONE:
                    initAdapter();
                    stickerProgressDialog.setVisibility(View.GONE);
                    break;
            }
        }
    };

//    protected void setImageSwitcher(int currentItem) {
//        rightInAnim();
//        isStickerBanner.setImageURI(uriMap.get(dataStickerBanner.get(currentItem).getSticker_group_path()));
//        setSwitcherClick();
//
//    }

    private void rightInAnim() {
        Animation animIn = AnimationUtils.loadAnimation(this,
                R.anim.slide_in_right);
        animIn.setFillAfter(true);
//        isStickerBanner.setInAnimation(animIn);

        Animation animOut = AnimationUtils.loadAnimation(this,
                R.anim.slide_out_left);
        animOut.setFillAfter(true);
//        isStickerBanner.setOutAnimation(animOut);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        handler.removeMessages(AUTO_PLAY);
    }

    private boolean hasError;

    @Override
    public void requestData() {
        Map<String, String> param = new HashMap<>();
        param.put("format", "1");

//        //获取 sticker banner 广告图
        new HttpTools(this).get(Constant.API_STICKER_BANNER, param, TAG, new HttpCallback() {
            @Override
            public void onStart() {
//                adProgressDialog.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                Message msg = handler.obtainMessage();
                msg.what = SHOW_ADS;
                msg.obj = response;
                handler.sendMessage(msg);

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(StickerStoreActivity.this, R.string.msg_action_failed);
//                adProgressDialog.setVisibility(View.GONE);
                hasError = true;

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

        //获取sticker group list
//        new HttpTools(this).get(Constant.API_STICKER_GROUP, params, TAG, new HttpCallback() {
        Map<String, String> stickerListMap = new HashMap<>();
        stickerListMap.put("user_id", MainActivity.getUser().getUser_id());
        stickerListMap.put("format", "1");
        new HttpTools(this).get(Constant.API_STICKER_GROUP_LIST, stickerListMap, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finishedAdCount++;
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                dataStickerGroup = gson.fromJson(response, new TypeToken<ArrayList<StickerGroupEntity>>() {
                }.getType());

//                initAdapter();

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(StickerStoreActivity.this, R.string.msg_action_failed);
                hasError = true;
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
     * download pictures for banner
     */
    private void downloadPic() {
        int count = 0;
        if (data != null) {
            count = dataStickerBanner.size();
        }
        for (int i = 0; i < count; i++) {
            String url = String.format(Constant.API_STICKER_BANNER_PIC, dataStickerBanner.get(i).getBanner_photo());
            final String target = FileUtil.getBannerFilePath(this) + "/" + String.format("%s", "" + dataStickerBanner.get(i).getBanner_photo());
            LogUtil.d(TAG, "====url====" + url);
            LogUtil.d(TAG, "====target====" + target);
            final int finalI = i;
            new HttpTools(this).download(App.getContextInstance(), url, target, false, new HttpCallback() {
                @Override
                public void onStart() {
                    LogUtil.d(TAG, "===onStart===" + "target===" + target);
                }

                @Override
                public void onFinish() {
                    finishedAdCount++;
//                    adProgressDialog.setVisibility(View.GONE);
                    if (!StickerStoreActivity.this.isFinishing()) {
                        File f = new File(target);
                        LogUtil.d(TAG, "===onFinish===" + f.exists());

                        if (f.exists()) {
                            Uri uri = Uri.parse(target);
                            uriMap.put(dataStickerBanner.get(finalI).getSticker_group_path(), uri);
                        }


//                        //下载好一张banner就显示
//                        if (uriMap.size() == 1) {
//                            currentItem = finalI;
////                            downloadFinish(currentItem);
////                            stickerProgressDialog.setVisibility(View.INVISIBLE);
//                        }
                    }

                }

                @Override
                public void onResult(String response) {


                }

                @Override
                public void onError(Exception e) {
                    LogUtil.e(TAG, "===onError===", e);

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {
                    LogUtil.d(TAG, "====onLoading===" + "=====count==========" + count + "======current=========" + current);
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
//        FileUtil.deleteBanner(this);
        uriMap = null;
        new HttpTools(this).cancelRequestByTag(TAG);
        super.onDestroy();

    }


    public void downloadFinish(int currentItem) {
        if (uriMap.size() > 0) {
            LogUtil.d(TAG, "===display_banner[0]===" + uriMap.size());
//            isStickerBanner.setImageURI(uriMap.get(dataStickerBanner.get(currentItem).getSticker_group_path()));
//            setSwitcherClick();
        }

    }

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float downX = 0;
    float upX = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            downX = event.getX();
            LogUtil.d(TAG, "===downX===" + downX);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            upX = event.getX();
            LogUtil.d(TAG, "===upX===" + upX);
            if (downX - upX > 80) {      //左滑
                int totalItem = 0;
                if (dataStickerBanner.size() > 0 && uriMap.size() > 0) {
                    totalItem = dataStickerBanner.size();
                    currentItem = currentItem + 1 == totalItem ? 0 : currentItem + 1;
                    rightInAnim();
                    if (uriMap.size() > 0) {
//                        isStickerBanner.setImageURI(uriMap.get(dataStickerBanner.get(currentItem).getSticker_group_path()));
                    }
                    LogUtil.d(TAG, "=========currentItem========" + currentItem);
                }
                return true;
            } else if (upX - downX > 80) {       //右滑
                int totalItem = 0;
                if (dataStickerBanner.size() > 0 && uriMap.size() > 0) {
                    totalItem = dataStickerBanner.size();
                    currentItem = currentItem - 1 < 0 ? totalItem - 1 : currentItem - 1;
                    Animation animIn = AnimationUtils.loadAnimation(this,
                            R.anim.slide_in_left);
                    animIn.setFillAfter(true);
//                    isStickerBanner.setInAnimation(animIn);

                    Animation animOut = AnimationUtils.loadAnimation(this,
                            R.anim.slide_out_right);
                    animOut.setFillAfter(true);
//                    isStickerBanner.setOutAnimation(animOut);
                    if (uriMap.size() > 0) {
//                        isStickerBanner.setImageURI(uriMap.get(dataStickerBanner.get(currentItem).getSticker_group_path()));
                    }
                    LogUtil.d(TAG, "=========currentItem========" + currentItem);
                }
                return true;
            }
        }

        if (Math.abs(downX - upX) < 5) {
            LogUtil.d(TAG, "====return_false===");
//            setSwitcherClick();
            return false;
        }

        LogUtil.d(TAG, "====return_false===");
        return false;

    }

//    private void setSwitcherClick() {
//        final String path = dataStickerBanner.get(currentItem).getSticker_group_path();
//        if (dataStickerBanner != null && dataStickerGroup != null && dataStickerBanner.size() > 0 && dataStickerGroup.size() > 0) {
//            LogUtil.d(TAG, "====setOnClickListener=====" + path + "    " + !path.isEmpty());
//            final int position = getPosition(path, dataStickerGroup);
//            final StickerGroupEntity stickerGroupEntity = dataStickerGroup.get(position);
////            isStickerBanner.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    if (!path.isEmpty()) {
////                        Intent intent = new Intent(StickerStoreActivity.this, StickerDetailActivity.class);
////                        intent.putExtra(StickerGroupAdapter.STICKER_GROUP, stickerGroupEntity);
////                        intent.putExtra(StickerGroupAdapter.POSITION, position);
////                        StickerStoreActivity.this.startActivity(intent);
////                    }
////                }
////            });
//
//
//        }
//    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                int positionFromStickerDetail = -1;
                int dataPosition;
                //避免重新进入activity,dataStickerGroup被销毁时刷新UI
                if (dataStickerGroup.size() == 0) {
                    return;
                }
                switch (intent.getAction()) {
                    case StickerDetailActivity.ACTION_UPDATE:
                    case StickerStoreActivity.ACTION_UPDATE:
                        dataPosition = intent.getIntExtra(StickerGroupAdapter.POSITION, 0);
                        dataStickerGroup.get(dataPosition).setDownloading(true);
                        //加1(header)
                        positionFromStickerDetail = dataPosition + 1;//add header
                        final RecyclerView.ViewHolder viewHolder = recyclerViewList.findViewHolderForAdapterPosition(positionFromStickerDetail);
                        if (viewHolder != null) {
                            StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) viewHolder;
                            int progress = intent.getIntExtra(PROGRESS, 0);
                            setViewProgress(holder, progress, dataStickerGroup.get(dataPosition), dataPosition);
                        }
                        break;
                    case MyStickerActivity.ACTION_UPDATE:
                        String path = intent.getStringExtra(StickerGroupAdapter.PATH);
                        dataPosition = getPosition(path, dataStickerGroup);
//                        positionFromStickerDetail = dataPosition+1;//add header
//                        final StickerGroupAdapter.VHItem holder = (StickerGroupAdapter.VHItem) recyclerViewList.findViewHolderForAdapterPosition(positionFromStickerDetail);
//                        if(holder!=null) {
//                            holder.getIvExist().setVisibility(View.INVISIBLE);
//                            holder.getTvDownload().setVisibility(View.VISIBLE);
//                        }
//                        dataStickerGroup.get(dataPosition).setPath(path);
//                        stickers.remove(path);
                        adapter.removeSticker(path);
                        adapter.notifyItemChanged(dataPosition + 1);
                        break;
                    case StickerStoreActivity.ACTION_FINISHED:
                        dataPosition = intent.getIntExtra(StickerGroupAdapter.POSITION, -1);
                        if (dataPosition != -1) {
                            adapter.addSticker(dataStickerGroup.get(dataPosition).getPath());
                            dataStickerGroup.get(dataPosition).setDownloading(false);
                            adapter.notifyItemChanged(dataPosition + 1);
//                            adapter.notifyDataSetChanged();
                        }
                        break;

                }
            }

        }
    };

    private void setViewProgress(StickerGroupAdapter.VHItem holder, int progress, StickerGroupEntity stickerGroupEntity, int position) {
        holder.getTvDownload().setVisibility(View.INVISIBLE);
        holder.getPbDownload().setVisibility(View.VISIBLE);
        holder.getPbDownload().setProgress(progress);
        if (progress >= 100) {
            holder.getPbDownload().setVisibility(View.INVISIBLE);
//            holder.getPbDownload().setProgress(0);
            holder.getIvExist().setVisibility(View.VISIBLE);
            dataStickerGroup.get(position).setDownloading(false);
            adapter.addSticker(stickerGroupEntity.getPath());
//            adapter.notifyItemChanged(position);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
