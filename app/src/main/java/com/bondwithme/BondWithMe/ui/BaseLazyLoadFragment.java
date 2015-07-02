package com.bondwithme.BondWithMe.ui;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.bondwithme.BondWithMe.util.SDKUtil;

/**
 * 懒加载fragment 基类
 * @author wing
 */
public abstract class BaseLazyLoadFragment extends BaseFragment {

    protected boolean useLazyLoad;
    protected boolean isVisible;
    protected boolean initDone,isFinished;
    LazyLoadTask lazyLoadTask;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(useLazyLoad&&!isVisible) {
            if (getUserVisibleHint()) {
                isVisible = true;
                onVisible();
            } else {
                isVisible = false;
                onInvisible();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onVisible() {
        if(lazyLoadTask != null) {
            lazyLoadTask.cancel(true);
            isFinished = true;
        }
        lazyLoadTask = new LazyLoadTask();

        //for not work in down 11
        if(SDKUtil.IS_HONEYCOMB) {
            lazyLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        } else {
            lazyLoadTask.execute();
        }

    }

    private class LazyLoadTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            while (!initDone&&!isFinished) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            lazyLoad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initDone = true;
    }

    protected abstract void lazyLoad();

    protected void onInvisible(){}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lazyLoadTask !=null){
            lazyLoadTask.cancel(true);
            isFinished = true;
        }
    }
}
