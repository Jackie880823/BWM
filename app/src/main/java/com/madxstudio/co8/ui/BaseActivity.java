package com.madxstudio.co8.ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.interfaces.IViewCommon;
import com.madxstudio.co8.interfaces.NetChangeObserver;
import com.madxstudio.co8.receiver_service.NetWorkStateReceiver;
import com.madxstudio.co8.util.NetworkUtil;

/**
 * activity基类
 *
 * @author wing
 */
public abstract class BaseActivity extends BaseFragmentActivity implements IViewCommon, NetChangeObserver {

    private static final String TAG = BaseActivity.class.getSimpleName();

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

    protected Fragment getFragmentInstance() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        NetWorkStateReceiver.unRegisterNetStateObserver(this);
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
    @CallSuper
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

}
