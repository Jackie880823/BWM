/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.appsflyer.AppsFlyerLib;
import com.madxstudio.co8.App;
import com.madxstudio.co8.AppControler;
import com.madxstudio.co8.R;
import com.madxstudio.co8.interfaces.IViewCommon;
import com.madxstudio.co8.interfaces.NetChangeObserver;
import com.madxstudio.co8.receiver_service.NetWorkStateReceiver;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.NotificationUtil;

/**
 * Created 16/8/1.
 *
 * @author Jackie
 * @version 1.0
 */
public abstract class BaseToolbarActivity extends SuperActivity implements IViewCommon,
        NetChangeObserver {
    private static final String TAG = "BaseToolbarActivity";

    /**
     * 是否为外部启动
     */
    public static final String IS_OUTSIDE_INTENT = "is_outside_intent";

    protected AppBarLayout mAppBar;
    protected Toolbar mToolbar;
    protected View msgBar;

    /**
     * {@link AppBarLayout}是否显示的标识位
     */
    private boolean mBarIsShow = true;
    protected ActionBar actionBar;

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();

        AppControler.getAppControler().addActivity(this);
        // 注册网络观察者
        NetWorkStateReceiver.registerNetStateObserver(this);

        requestData();
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
        mToolbar.collapseActionView();
        AppsFlyerLib.onActivityResume(this);
        NotificationUtil.clearBadge(this);//重置应用图标上的数量
        /**是否是程序外进入(点击通知)*/
        if (getIntent().getBooleanExtra(IS_OUTSIDE_INTENT, false)) {
            /**重置通知数量*/
            App.clearAllNotificationMsgs();
        }
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        NetWorkStateReceiver.unRegisterNetStateObserver(this);
    }

    public <V extends View> V getViewById(@IdRes int resId) {
        return (V) findViewById(resId);
    }

    @CallSuper
    public void initView() {
        mAppBar = getViewById(R.id.app_bar);
        mToolbar = getViewById(R.id.toolbar);
        msgBar = getViewById(R.id.msg_bar);

        msgBar.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(canBack());
        }

        // 检测网络的连通性
        if (NetworkUtil.isNetworkConnected(this)) {
            msgBar.setVisibility(View.GONE);
        } else {
            msgBar.setVisibility(View.VISIBLE);
        }
    }

    protected abstract boolean canBack();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * 显示或者隐藏{@link AppBarLayout}的切换,如果是显示的则隐藏；如果是隐藏则显示
     *
     * @return 反回{@link AppBarLayout}的显示状态
     */
    @CallSuper
    protected boolean hideOrShowAppBar() {
        mAppBar.animate()
                .translationY(mBarIsShow ? -mAppBar.getHeight() : 0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mBarIsShow = !mBarIsShow;
        return mBarIsShow;
    }

    /**
     * 网络状态连接时调用
     *
     * @param netType
     */
    @Override
    public void OnConnect(int netType) {
        LogUtil.d(TAG, "OnConnect: ");
        msgBar.setVisibility(View.GONE);
    }

    /**
     * 网络状态断开时调用
     */
    @Override
    public void OnDisConnect() {
        LogUtil.d(TAG, "OnDisConnect: ");
        msgBar.setVisibility(View.VISIBLE);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    @CallSuper
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_bar:
                goNetworkSetting();
                break;
        }
    }
}
