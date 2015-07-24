package com.bondwithme.BondWithMe.entity;

import android.content.Context;
import android.net.Uri;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heweidong on 15/7/21.
 */
public class StickerBannerPic {
    private Context mContext;
    private String TAG = "StickerBannerPic";
    private List<StickerBannerEntity> data;
    public  List<Uri> uriList = new ArrayList<>();
    private  int count;

    @Override
    public String toString() {
        return "StickerBannerPic{" +
                "mContext=" + mContext +
                ", data=" + data +
                ", mListener=" + mListener +
                '}';
    }

    public StickerBannerPic(List data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }



    public void getUri(){
        String str = FileUtil.getCacheFilePath(mContext) + FileUtil.BANNER_DIR_NAME + data.get(0).getBanner_photo();
        File banner = new File(str);
        if (data != null){
            count = data.size();
        }
        LogUtil.d(TAG,"====banner===="+banner.getAbsolutePath()+"====banner.exists()====="+banner.exists());
        if (banner.exists()){
            for (int i=0;i<count;i++){
                File f = new File(FileUtil.getBannerFilePath(mContext) + String.format("/%s", "" + ((StickerBannerEntity)data.get(i)).getBanner_photo()));
                Uri uri = Uri.fromFile(f);
                uriList.add(uri);
                if(mListener!=null && i == count-1){
                    mListener.downloadFinish();
                }
            }
        }else {
            downloadPic();
        }
    }

    private void downloadPic(){
        if (data != null){
            count = data.size();
        }
        for (int i = 0; i < count;i++){
            String url = String.format(Constant.API_STICKER_BANNER_PIC, ((StickerBannerEntity) data.get(i)).getBanner_photo());
            final String target = FileUtil.getBannerFilePath(mContext) + String.format("/%s", "" + ((StickerBannerEntity)data.get(i)).getBanner_photo());
            LogUtil.d(TAG,"====url===="+url);
            LogUtil.d(TAG,"====target===="+target);
            final int finalI = i;
            DownloadRequest request = new HttpTools(mContext).download(url, target, true, new HttpCallback() {
                @Override
                public void onStart() {
                    LogUtil.d(TAG,"===onStart===");
                }

                @Override
                public void onFinish() {
                    LogUtil.d(TAG,"===onFinish===");

                }

                @Override
                public void onResult(String response) {
//                    String strFile = FileUtil.getBannerFilePath(mContext) + String.format("/%s", "" + ((StickerBannerEntity)data.get(finalI)).getBanner_photo());
                    File f = new File(target);
                    LogUtil.d(TAG, "===onResult==="+target);
                    Uri uri = Uri.fromFile(f);
                    uriList.add(uri);
                    LogUtil.d(TAG, "==========uriList.size()=========" + uriList.size()+"====finalI===="+finalI+"===count==="+count);
                    if(mListener!=null && finalI == count-1){
                        mListener.downloadFinish();
                    }


                }

                @Override
                public void onError(Exception e) {
                    LogUtil.e(TAG,"===onError===",e);

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {
                    LogUtil.d(TAG,"=====count=========="+count+"======current========="+current);
                }
            });
        }
    }

    public DownloadBannerListener mListener;

    public void setDownloadListener(DownloadBannerListener Listener){
        mListener = Listener;
    }

    public interface DownloadBannerListener{
        public void downloadFinish();
    }
}
