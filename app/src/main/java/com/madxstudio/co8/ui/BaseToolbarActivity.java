package com.madxstudio.co8.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.appsflyer.AppsFlyerLib;
import com.madxstudio.co8.AppControler;
import com.madxstudio.co8.R;
import com.madxstudio.co8.interfaces.NetChangeObserver;
import com.madxstudio.co8.receiver_service.NetWorkStateReceiver;

/**
 * Created 16/8/1.
 *
 * @author Jackie
 * @version 1.0
 */
public abstract class BaseToolbarActivity extends AppCompatActivity implements NetChangeObserver,
        OnClickListener {
    private static final String TAG = "BaseToolbarActivity";

    protected AppBarLayout mAppBar;
    protected Toolbar mToolbar;
    protected View msgBar;

    /**
     * 下拉展开标题时的显示的图片控件
     */
    protected ImageView imgTitle;

    /**
     * {@link AppBarLayout}是否显示的标识位
     */
    private boolean mBarIsShow = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppControler.getAppControler().addActivity(this);
        //注册网络观察者
        NetWorkStateReceiver.registerNetStateObserver(this);

        setContentView(getLayoutId());
        initView();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected <V extends View> V findView(@IdRes int resId) {
        return (V) findViewById(resId);
    }

    @CallSuper
    protected void initView() {
        mAppBar = findView(R.id.app_bar);
        mToolbar = findView(R.id.toolbar);
        imgTitle = findView(R.id.img_title);
        msgBar = findView(R.id.msg_bar);

        msgBar.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(canBack());
        }
    }

    protected abstract boolean canBack();

    @Override
    protected void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
        mToolbar.collapseActionView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetWorkStateReceiver.unRegisterNetStateObserver(this);
        AppControler.getAppControler().finishActivity(this);
    }

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

    }

    /**
     * 网络状态断开时调用
     */
    @Override
    public void OnDisConnect() {

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

    /**
     * 跳转至网络设置
     */
    private void goNetworkSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_SETTINGS);
//        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
