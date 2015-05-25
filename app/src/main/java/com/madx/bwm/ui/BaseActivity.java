package com.madx.bwm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.madx.bwm.R;
import com.madx.bwm.interfaces.IViewCommon;
import com.madx.bwm.interfaces.NetChangeObserver;
import com.madx.bwm.receiver_service.NetWorkStateReceiver;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.util.UIUtil;

import org.xmlpull.v1.XmlPullParser;

/**
 * activity基类
 *
 * @author wing
 */
public abstract class BaseActivity extends BaseFragmentActivity implements IViewCommon, NetChangeObserver {

    protected ImageButton leftButton;            //头部栏的左边的按钮
    protected TextView tvTitle;                          //头部栏的标题
    protected ImageView title_icon;                          //头部栏的标题
    protected ImageButton rightButton;             //头部栏的右边按钮
    protected LinearLayout bottom;             //底部栏的布局
//    protected TextView rightTextButton;

    Fragment fragment;
    private View msg_bar;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            BaseActivity.this.finish();
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event); // 按下其他按钮，调用父类进行默认处理
    }

    protected Fragment getFragmentInstance() {
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 打开Activity隐藏软键盘；
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayout());
        //注册网络观察者
        NetWorkStateReceiver.registerNetStateObserver(this);

        fragment = getFragment();
        if (fragment != null) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }
        msg_bar = getViewById(R.id.msg_bar);
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

    private View setWaittingView(){

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        relativeLayout.setClickable(true);
//        relativeLayout.setVisibility(View.GONE);


//        obtainStyledAttributes(R.style.progress_bar,null);

        XmlPullParser parser = getResources().getXml(R.xml.progress_bar_style);
        AttributeSet attributes = Xml.asAttributeSet(parser);
        ProgressBarCircularIndeterminate progress_bar = new ProgressBarCircularIndeterminate(this,attributes);

        relativeLayout.addView(progress_bar);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progress_bar.getLayoutParams();
        layoutParams.width =100;
        layoutParams.height =100;
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progress_bar.setBackgroundColor(getResources().getColor(R.color.bg_progressBar));

        return relativeLayout;
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
        UIUtil.hideKeyboard(this, getCurrentFocus());
        finish();
    }

    /**
     * TitilBar 右边事件
     */
    protected abstract void titleRightEvent();

    protected void titleMiddleEvent() {
    }

    protected LinearLayout titleBar;
//    protected RelativeLayout titleBar;

    protected void initTitleBar() {
        titleBar = getViewById(R.id.title_bar);
        if(currentColor!=-1){
            titleBar.setBackgroundColor(getResources().getColor(currentColor));
        }
        leftButton = getViewById(R.id.ib_top_button_left);
        tvTitle = getViewById(R.id.tv_title);
        title_icon = getViewById(R.id.title_icon);
        rightButton = getViewById(R.id.ib_top_button_right);
//        rightTextButton = getViewById(R.id.ib_top_text_right);
        getViewById(R.id.tv_top_title).setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
//        rightTextButton.setOnClickListener(this);
        setTitle();

    }

    public <V extends View> V getViewById(int id) {
        return (V) findViewById(id);
    }

    protected static int currentColor=-1;
    protected void changeTitleColor(int color) {
        if (titleBar != null) {
            currentColor = color;
            titleBar.setBackgroundColor(getResources().getColor(color));
        }
    }


    protected abstract Fragment getFragment();

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		Logger.i("test", fragmentManager.getBackStackEntryCount() + "");
//		fragmentManager.popBackStack();
        super.onPause();
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
            case R.id.msg_bar:
                goNetworkSetting();
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

    private void msgBarChangeByStatus(int status) {
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
    }

    protected void showWaitting(){
        ((ProgressBarCircularIndeterminate)getViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
    }
}
