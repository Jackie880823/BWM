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

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.appsflyer.AppsFlyerLib;
import com.madxstudio.co8.App;
import com.madxstudio.co8.AppControler;
import com.madxstudio.co8.util.NotificationUtil;
import com.madxstudio.co8.util.UIUtil;

/**
 * Created 16/8/29.
 *
 * @author Jackie
 * @version 1.0
 */
public abstract class SuperActivity extends AppCompatActivity implements View.OnClickListener, BaseFragment.OnFragmentInteractionListener {

    /**
     * 是否为外部启动
     */
    public static final String IS_OUTSIDE_INTENT = "is_outside_intent";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doFinish();
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.getAppControler().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppControler.getAppControler().finishActivity(this);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
        NotificationUtil.clearBadge(this);//重置应用图标上的数量
        /**是否是程序外进入(点击通知)*/
        if (getIntent().getBooleanExtra(IS_OUTSIDE_INTENT, false)) {
            /**重置通知数量*/
            App.clearAllNotificationMsgs();
        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        // 这里会影响子类返回键的监听事件，请谨慎处理
//        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent
//                .KEYCODE_BACK) {
//            doFinish();
//            return true;
//        }
//        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent
//                .KEYCODE_MENU) {
//            return true;
//        }
//        return super.dispatchKeyEvent(event); // 按下其他按钮，调用父类进行默认处理
//    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;

    }

    protected void doFinish() {
        if (getIntent().getBooleanExtra(IS_OUTSIDE_INTENT, false)) {
            // 如果是外部或通知跳入本Activity，返回进入应用主界面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            if (!isFinishing()) {
                UIUtil.hideKeyboard(this, getCurrentFocus());
            }
        }
        finish();
    }

    /**
     * 跳转至网络设置
     */
    protected void goNetworkSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_SETTINGS);
//        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
