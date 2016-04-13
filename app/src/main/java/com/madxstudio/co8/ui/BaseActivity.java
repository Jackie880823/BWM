package com.madxstudio.co8.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.madxstudio.co8.App;
import com.madxstudio.co8.AppControler;
import com.madxstudio.co8.R;
import com.madxstudio.co8.interfaces.IViewCommon;
import com.madxstudio.co8.interfaces.NetChangeObserver;
import com.madxstudio.co8.receiver_service.NetWorkStateReceiver;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.util.NotificationUtil;
import com.madxstudio.co8.util.UIUtil;

/**
 * activity基类
 *
 * @author wing
 */
public abstract class BaseActivity extends BaseFragmentActivity implements IViewCommon, NetChangeObserver {

    private static final String TAG = BaseActivity.class.getSimpleName();
    /**
     * 是否为外部启动
     */
    public static final String IS_OUTSIDE_INTENT = "is_outside_intent";

    protected ImageButton leftButton;            //头部栏的左边的按钮
    protected TextView tvTitle;                          //头部栏的标题
    protected ImageView title_icon;                          //头部栏的标题
    public ImageButton rightButton;             //头部栏的右边按钮
    public ImageButton rightSearchButton;
    protected LinearLayout bottom;             //底部栏的布局
    protected TextView yearButton;
    //    protected TextView rightTextButton;

    protected View msg_bar;
    protected TextView tvMsg;
    protected Bundle mSavedInstanceState;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 这里会影响子类返回键的监听事件，请谨慎处理
        //        Log.i(TAG, "dispatchKeyEvent& keyCode: " + event.getKeyCode() + "; Action: " + event.getAction());
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            doFinish();
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event); // 按下其他按钮，调用父类进行默认处理
    }

    protected Fragment getFragmentInstance() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.getAppControler().addActivity(this);
        mSavedInstanceState = savedInstanceState;
        // 打开Activity隐藏软键盘；
        //        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayout());
        //注册网络观察者
        NetWorkStateReceiver.registerNetStateObserver(this);

        if (savedInstanceState == null) {
            Fragment fragment = getFragment();
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
            }
        }

        msg_bar = getViewById(R.id.msg_bar);
        tvMsg = getViewById(R.id.msg);
        if (NetworkUtil.isNetworkConnected(this)) {
            msgBarChangeByStatus(View.GONE);
        } else {
            msgBarChangeByStatus(View.VISIBLE);
        }
        msg_bar.setOnClickListener(this);
        initBottomBar();//要先初始
        initView();
        requestData();
        initTitleBar();


        //        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
        //                .findViewById(android.R.id.content)).getChildAt(0);
        //        viewGroup.addView(setWaittingView());

    }

    public int getLayout() {
        return R.layout.activity_base;
    }

    /**
     * 初始底部栏，没有可以不操作
     */
    protected abstract void initBottomBar();

    /**
     * 设置title
     */
    protected abstract void setTitle();

    /**
     * TitilBar 左边事件
     */
    protected void titleLeftEvent() {
        doFinish();
    }

    private void doFinish() {
        if (getIntent().getBooleanExtra(IS_OUTSIDE_INTENT, false)) {
            Intent intent = new Intent(this, MainActivity.class);
//            ComponentName cn = intent.getComponent();
//            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(mainIntent);
            startActivity(intent);
        } else {
            if (!isFinishing()) {
                UIUtil.hideKeyboard(this, getCurrentFocus());
            }
        }
        finish();
    }

    @Override
    protected void onStop() {
//        /**重置通知数量*/
//        if(getIntent().getBooleanExtra(IS_OUTSIDE_INTENT,false)) {
//            App.clearAllNotificationMsgs();
////            App.getContextInstance().clearNotificationMsgsByType((NotificationUtil.MessageType) getIntent().getSerializableExtra(NotificationUtil.MSG_TYPE));
//        }
        super.onStop();
    }

    /**
     * TitilBar 右边事件
     */
    protected abstract void titleRightEvent();

    protected void titleRightSearchEvent() {

    }

    protected void titleMiddleEvent() {
    }

    protected LinearLayout titleBar;
    //    protected RelativeLayout titleBar;

    protected void initTitleBar() {
        titleBar = getViewById(R.id.title_bar);
        if (currentColor != -1) {
            titleBar.setBackgroundColor(getResources().getColor(currentColor));
        }
        leftButton = getViewById(R.id.ib_top_button_left);
        tvTitle = getViewById(R.id.tv_title);
        title_icon = getViewById(R.id.title_icon);
        rightButton = getViewById(R.id.ib_top_button_right);
        rightSearchButton = getViewById(R.id.ib_top_button_right2);
        yearButton = getViewById(R.id.ib_top_button_right_year);
        //        rightTextButton = getViewById(R.id.ib_top_text_right);
        getViewById(R.id.tv_top_title).setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        if (rightSearchButton != null) {
            rightSearchButton.setOnClickListener(this);
        }
        yearButton.setOnClickListener(this);
        //        rightTextButton.setOnClickListener(this);
        setTitle();

    }

    public <V extends View> V getViewById(int id) {
        return (V) findViewById(id);
    }

    protected static int currentColor = -1;

    protected void changeTitleColor(int color) {
        if (titleBar != null) {
            currentColor = color;
            titleBar.setBackgroundColor(getResources().getColor(color));
        }
    }

    /**
     * 返回当前Action Bar的颜色值
     *
     * @return - 颜色值
     */
    public int getActionBarColor() {
        if (currentColor == -1) {
            currentColor = R.color.tab_color_press1;
        }
        return currentColor;
    }

    protected abstract Fragment getFragment();

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //		FragmentManager fragmentManager = getSupportFragmentManager();
        //		Logger.i("test", fragmentManager.getBackStackEntryCount() + "");
        //		fragmentManager.popBackStack();
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 进行弹出窗口//
            case R.id.ib_top_button_left:
                titleLeftEvent();
                break;
            case R.id.tv_top_title:
                titleMiddleEvent();
                break;
            case R.id.ib_top_button_right:
                //            case R.id.ib_top_text_right:
                titleRightEvent();
                break;
            case R.id.ib_top_button_right2:
                titleRightSearchEvent();
                break;
            case R.id.msg_bar:
                goNetworkSetting();
                break;
            case R.id.ib_top_button_right_year:
                titleRightEvent();
                break;
            default:
                //                super.onClick(v);
                break;
        }
    }

    private void goNetworkSetting() {
        Intent intent = new Intent();

        //        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        intent.setAction(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void OnConnect(int netType) {
        msgBarChangeByStatus(View.GONE);
    }

    protected void msgBarChangeByStatus(int status) {
        if (msg_bar != null) {
            msg_bar.setVisibility(status);
        }
    }

    @Override
    public void OnDisConnect() {
        msgBarChangeByStatus(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetWorkStateReceiver.unRegisterNetStateObserver(this);
        AppControler.getAppControler().finishActivity(this);
    }

    @Override
    protected void onResume() {
//        if (isNeedRefersh&&!(this instanceof MainActivity)) {
//            Intent intent = getIntent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            finish();
//            startActivity(intent);
//
////            isNeedRefersh = false;
////            Intent reintent = getIntent();
////            finish();
////            startActivity(reintent);
//            return;
//        }
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
        NotificationUtil.clearBadge(this);//重置应用图标上的数量
        /**是否是程序外进入(点击通知)*/
        if (getIntent().getBooleanExtra(IS_OUTSIDE_INTENT, false)) {
            /**重置通知数量*/
            App.clearAllNotificationMsgs();
//            App.getContextInstance().clearNotificationMsgsByType((NotificationUtil.MessageType) getIntent().getSerializableExtra(NotificationUtil.MSG_TYPE));
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}
