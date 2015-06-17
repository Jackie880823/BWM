package com.madx.bwm.ui;

import android.os.AsyncTask;

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

    protected void onVisible(){
        if(lazyLoadTask !=null){
            lazyLoadTask.cancel(true);
            isFinished = true;
        }
        lazyLoadTask = new LazyLoadTask();
        lazyLoadTask.execute();

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
