package com.bondwithme.BondWithMe.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MyFragmentPagerAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.wall.WallFragment;
import com.bondwithme.BondWithMe.ui.wall.WallNewActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.NotificationUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
import com.material.widget.SnackBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页Activity,包含了头部和底部，无需定义中间内容(ViewPaper)
 */
public class MainActivity extends BaseActivity implements NotificationUtil.NotificationOtherHandle {

    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * 标题栏左边控件点击事件消息
     */
    private final static int LEFT_CLICK_EVENT = 10;

    /**
     * 标题栏右边控件点击事件消息
     */
    private final static int RIGHT_CLICK_EVENT = 11;
    /**
     * 标题栏右边控件点击事件消息
     */
    private final static int SHOW_RED_POINT = 12;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private MyFragmentPagerAdapter tabPagerAdapter;

    private RelativeLayout ivTab0;
    private RelativeLayout ivTab1;
    private RelativeLayout ivTab2;
    private RelativeLayout ivTab3;
    private RelativeLayout ivTab4;
    private ImageView tabIv0;
    private ImageView tabIv1;
    private ImageView tabIv2;
    private ImageView tabIv3;
    private ImageView tabIv4;
    private TextView tabTv0;
    private TextView tabTv1;
    private TextView tabTv2;
    private TextView tabTv3;
    private TextView tabTv4;
    List<Fragment> fragments;

    EventFragment eventFragment;
    EventStartupFragment eventStartupFragment;
    private boolean iseventdate;


    //以下为暂定全局变量
    private boolean hasUpdate;
    private int jumpIndex;
    public static String LAST_LEAVE_INDEX = "lastLeaveIndex";
    private int leavePagerIndex = 0;
    private View red_point_1;
    private View red_point_2;
    private View red_point_3;
    private View red_point_4;
    private View red_point_5;
    public static String STICKERS_NAME = "stickers";
    public static String IS_FIRST_LOGIN = "firstLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (App.getLoginedUser() == null) {
            finish();
            return;
        }

        //表示这个用户已经登陆过。提供给登录界面判断显示sign up 还是 log in
        //-----------------------------------------------------------------------------
        PreferencesUtil.saveValue(this, Constant.HAS_LOGED_IN, Constant.HAS_LOGED_IN);
        //-----------------------------------------------------------------------------
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();


        //for default
        setDrawable();
        changeTitleColor(R.color.tab_color_press5);
        changeTitle(R.string.title_tab_my_family);
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        tabIv0.setImageResource(R.drawable.tab_family_select);
        ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_press5));
        tabTv0.setTextColor(Color.BLACK);
        //for last tab
        mViewPager.setCurrentItem(leavePagerIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fragments != null) {
            for (Fragment f : fragments) {
                f.setRetainInstance(true);
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (fragments != null) {
            for (Fragment f : fragments) {
                f.getRetainInstance();
            }
        }
//        if(currentTabEnum!=null){
//            changeTitleColor();
//        }

    }

    @Override
    protected void onStop() {
        PreferencesUtil.saveValue(this, LAST_LEAVE_INDEX, currentTabEnum.ordinal());
        LAST_LEAVE_INDEX = "lastLeaveIndex";
        IS_FIRST_LOGIN = "firstLogin";
        super.onStop();
    }

    public static UserEntity getUser() {
        if (App.getLoginedUser() == null) {
//            App.exit(null);
//            getApplication().onTerminate();
        }
        return App.getLoginedUser();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initBottomBar() {
        bottom = (LinearLayout) findViewById(R.id.bottom);
        ivTab0 = (RelativeLayout) findViewById(R.id.iv_tab_icon0);
        ivTab1 = (RelativeLayout) findViewById(R.id.iv_tab_icon1);
        ivTab2 = (RelativeLayout) findViewById(R.id.iv_tab_icon2);
        ivTab3 = (RelativeLayout) findViewById(R.id.iv_tab_icon3);
        ivTab4 = (RelativeLayout) findViewById(R.id.iv_tab_icon4);
        tabIv0 = (ImageView) findViewById(R.id.iv_tab_icon0_iv);
        tabIv1 = (ImageView) findViewById(R.id.iv_tab_icon1_iv);
        tabIv2 = (ImageView) findViewById(R.id.iv_tab_icon2_iv);
        tabIv3 = (ImageView) findViewById(R.id.iv_tab_icon3_iv);
        tabIv4 = (ImageView) findViewById(R.id.iv_tab_icon4_iv);
        tabTv0 = (TextView) findViewById(R.id.iv_tab_icon0_tv);
        tabTv1 = (TextView) findViewById(R.id.iv_tab_icon1_tv);
        tabTv2 = (TextView) findViewById(R.id.iv_tab_icon2_tv);
        tabTv3 = (TextView) findViewById(R.id.iv_tab_icon3_tv);
        tabTv4 = (TextView) findViewById(R.id.iv_tab_icon4_tv);
        ivTab0.setOnClickListener(this);
        ivTab1.setOnClickListener(this);
        ivTab2.setOnClickListener(this);
        ivTab3.setOnClickListener(this);
        ivTab4.setOnClickListener(this);

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_tab_diary);
    }

    @Override
    protected void titleLeftEvent() {
        handler.sendEmptyMessage(LEFT_CLICK_EVENT);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Fragment fragment = tabPagerAdapter.getActiveFragment(mViewPager, currentTabEnum.ordinal());
//            Fragment fragment = tabPagerAdapter.getItem(currentTabEnum.ordinal());
            switch (msg.what) {
                case RIGHT_CLICK_EVENT:
                    switch (currentTabEnum) {
                        case wall:
                            fragment.startActivityForResult(new Intent(getApplicationContext(), WallNewActivity.class), Constant.ACTION_CREATE_WALL);
                            break;
                        case event:
                            fragment.startActivityForResult(new Intent(getApplicationContext(), EventNewActivity.class), Constant.ACTION_EVENT_CREATE);
                            break;
                        case chat:
//                          fragment.startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
                            if (commandlistener != null) {
                                commandlistener.execute(rightButton);
                            }
                            break;
                        case family:
//                          fragment.startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
                            if (familyCommandListener != null) {
                                familyCommandListener.execute(rightButton);
                            }
                            break;
                        case more:
                            // TODO
                            break;
                    }
                    break;
                case LEFT_CLICK_EVENT:
                    switch (currentTabEnum) {
                        case wall:
                            // TODO
                            break;
                        case event:
                            // TODO
                            break;
                        case chat:
                            //mViewPager.setCurrentItem(0);
                            //startActivity(new Intent(MainActivity.this, MainActivity.class));
                            break;
                        case more:
                            // TODO
                            break;
                    }
                    break;
                case SHOW_RED_POINT:
                    if (msg.obj != null) {
                        setRedPoint((TabEnum) msg.obj, false);
                    }
                    break;
            }

            return false;
        }
    });

    @Override
    protected void titleRightEvent() {
        // 发送右边控件被点击的消息致handler
        handler.sendEmptyMessage(RIGHT_CLICK_EVENT);
    }

    private TitleEventListenner mTitleEventListenner;

    @Override
    public void doSomething(TabEnum tab) {
        if (handler != null && tab != currentTabEnum) {
            Message msg = handler.obtainMessage();
            msg.what = SHOW_RED_POINT;
            msg.obj = tab;
            handler.sendMessage(msg);
        }
    }


    public interface TitleEventListenner {
        public void toggleLeftEvent();

        public void toggleRightEvent();
    }


    public boolean isCurrentTab(TabEnum tab) {
        return currentTabEnum == tab;
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void initView() {
        STICKERS_NAME = new LocalStickerInfoDao(this).getSavePath();
        IS_FIRST_LOGIN += App.getLoginedUser().getUser_id();
        boolean isFirstLogin = PreferencesUtil.getValue(this, IS_FIRST_LOGIN, true);
        if (isFirstLogin) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        List<String> pathList = FileUtil.getAllFilePathsFromAssets(MainActivity.this, "stickers");
                        if (null != pathList) {
                            for (String string : pathList) {
                                String filePath = "stickers" + File.separator + string;
                                ZipUtils.unZip(MainActivity.this, filePath, STICKERS_NAME);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            PreferencesUtil.saveValue(this, IS_FIRST_LOGIN, false);
        }

        mViewPager = getViewById(R.id.pager);
        fragments = new ArrayList<>();
//        WallFragment wallFragment = WallFragment.newInstance();
        fragments.add(FamilyFragment.newInstance());
        fragments.add(MessageMainFragment.newInstance());
        fragments.add(WallFragment.newInstance());
        fragments.add(EventFragment.newInstance());
//        eventFragment = EventFragment.newInstance();
//        eventStartupFragment = EventStartupFragment.newInstance();
//        fragments.add(eventFragment);
//        if(isEventFragmentDate()){
//            Log.i("isEventFragmentDate==================","true");
//
//        }
//        else {
//            Log.i("isEventFragmentDate==================","false");
////            fragments.remove(eventFragment);
////            fragments.add(1,eventStartupFragment);
//        }
//        fragments.add(MessageFragment.newInstance());
        fragments.add(MoreFragment.newInstance());


        tabPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this, mViewPager, fragments);
        tabPagerAdapter.setOnMyPageChangeListenner(new MyFragmentPagerAdapter.OnPageChangeListenner() {
            @Override
            public void onPageChange(int position) {
                changeTab(TabEnum.values()[position]);
            }
        });
        mViewPager.setAdapter(tabPagerAdapter);

        mViewPager.setOffscreenPageLimit(0);

        LAST_LEAVE_INDEX += getUser().getUser_id();
        leavePagerIndex = PreferencesUtil.getValue(this, LAST_LEAVE_INDEX, 0);
        jumpIndex = getIntent().getIntExtra("jumpIndex", -1);
        if (jumpIndex != -1) {
            leavePagerIndex = jumpIndex;
        }

        red_point_1 = getViewById(R.id.red_point_1);
        red_point_2 = getViewById(R.id.red_point_2);
        red_point_3 = getViewById(R.id.red_point_3);
        red_point_4 = getViewById(R.id.red_point_4);
        red_point_5 = getViewById(R.id.red_point_5);

        NotificationUtil.setNotificationOtherHandle(this);

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
//        filter.addAction("refresh");
        registerReceiver(mReceiver, filter);
    }

    private boolean isEventFragmentDate() {

//        eventFragment.setItemClickListener(new EventFragment.ItemClickListener() {
//            @Override
//            public void topItemClick(int startIndex) {
//                if(startIndex>0){
//                    Log.i("startIndex======================3", startIndex+"");
//                    iseventdate = true;
//                }else
//                {
//                    Log.i("startIndex======================4", startIndex+"");
//                    iseventdate = false;
//                }
//
//            }
//        });
        return iseventdate;
    }

    @Override
    public void requestData() {

    }


    private TabEnum currentTabEnum = TabEnum.family;

    private void changeTitle(int title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    private void setDrawable() {
        tabIv0.setImageResource(R.drawable.tab_family);
        tabIv1.setImageResource(R.drawable.tab_wall);
        tabIv2.setImageResource(R.drawable.tab_event);
        tabIv3.setImageResource(R.drawable.tab_message);
        tabIv4.setImageResource(R.drawable.tab_more);
        tabTv0.setTextColor(Color.GRAY);
        tabTv1.setTextColor(Color.GRAY);
        tabTv2.setTextColor(Color.GRAY);
        tabTv3.setTextColor(Color.GRAY);
        tabTv4.setTextColor(Color.GRAY);
        ivTab0.setBackgroundColor(getResources().getColor(R.color.transparent_color));
        ivTab1.setBackgroundColor(getResources().getColor(R.color.transparent_color));
        ivTab2.setBackgroundColor(getResources().getColor(R.color.transparent_color));
        ivTab3.setBackgroundColor(getResources().getColor(R.color.transparent_color));
        ivTab4.setBackgroundColor(getResources().getColor(R.color.transparent_color));

    }

    protected void changeTab(TabEnum tabEnum) {
        switch (tabEnum) {
            case wall:
                setDrawable();
                //ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_press1));
                changeTitleColor(R.color.tab_color_press1);
                changeTitle(R.string.title_tab_diary);
//                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
                leftButton.setVisibility(View.INVISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                tabIv1.setImageResource(R.drawable.tab_wall_select);
                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_press1));
                tabTv1.setTextColor(Color.BLACK);
                setRedPoint(TabEnum.wall, true);
                break;
            case event:
                setDrawable();
                //ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_press2));
                changeTitleColor(R.color.tab_color_press2);
                changeTitle(R.string.title_tab_event);
//                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
                leftButton.setVisibility(View.INVISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                tabIv2.setImageResource(R.drawable.tab_event_select);
                ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_press2));
                tabTv2.setTextColor(Color.BLACK);
                setRedPoint(TabEnum.event, true);
                break;
            case chat:
                setDrawable();
                //ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_press3));
                changeTitleColor(R.color.tab_color_press3);
                changeTitle(R.string.title_tab_chat);
//                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
                leftButton.setVisibility(View.INVISIBLE);
                leftButton.setImageResource(R.drawable.btn_family);
                rightButton.setVisibility(View.VISIBLE);
                tabIv3.setImageResource(R.drawable.tab_message_select);
                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_press3));
                tabTv3.setTextColor(Color.BLACK);
                setRedPoint(TabEnum.chat, true);
                break;
            case more:
                setDrawable();
                //ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
                changeTitleColor(R.color.tab_color_press4);
                changeTitle(R.string.title_tab_more);
//                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
                leftButton.setVisibility(View.INVISIBLE);
                rightButton.setVisibility(View.INVISIBLE);
                tabIv4.setImageResource(R.drawable.tab_more_select);
                ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
                tabTv4.setTextColor(Color.BLACK);
                setRedPoint(TabEnum.more, true);
                break;
            case family:
                setDrawable();
                //ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
                changeTitleColor(R.color.tab_color_press5);
                changeTitle(R.string.title_tab_my_family);
//                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
                leftButton.setVisibility(View.INVISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                tabIv0.setImageResource(R.drawable.tab_family_select);
                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_press5));
                tabTv0.setTextColor(Color.BLACK);
                break;
        }
        tvTitle.setSelected(true);
        tvTitle.requestFocus();
        currentTabEnum = tabEnum;
    }

    @Override
    public void onClick(View v) {
        TabEnum tab = null;

        switch (v.getId()) {
            case R.id.iv_tab_icon0:
                tab = TabEnum.family;
                break;
            case R.id.iv_tab_icon1:
                tab = TabEnum.wall;
                break;
            case R.id.iv_tab_icon2:
                tab = TabEnum.event;
                break;
            case R.id.iv_tab_icon3:
                tab = TabEnum.chat;
                break;
            case R.id.iv_tab_icon4:
                tab = TabEnum.more;
                break;
            default:
                super.onClick(v);
                break;
        }
        if (tab == null || isCurrentTab(tab)) {
            return;
        }
        mViewPager.setCurrentItem(tab.ordinal());

    }

    public enum TabEnum {
        family, chat, wall, event, more;
    }

    SnackBar snackBar;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                snackBar = new SnackBar(MainActivity.this,
                        getString(R.string.msg_ask_exit_app),
                        getString(R.string.text_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        FileUtil.clearCache(MainActivity.this);
//                        MainActivity.this.finish();
                        MainActivity.this.getApplication().onTerminate();
//                        App.exit(MainActivity.this);
                    }
                });
                snackBar.show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void setRedPoint(TabEnum tab, boolean cancel) {
        int action = cancel ? View.GONE : View.VISIBLE;
        switch (tab) {
            case family:
                break;
            case chat:
                red_point_2.setVisibility(action);
                break;
            case wall:
                red_point_3.setVisibility(action);
                break;
            case event:
                red_point_4.setVisibility(action);
                break;
            case more:
                red_point_5.setVisibility(action);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (snackBar != null) {
            snackBar.dismiss();
        }
        super.onDestroy();
    }

    private void updateViewPager() {
//        data.clear();
//        data.add("X");
//        data.add("Y");
//        data.add("Z");
//        myViewPager.getAdapter().notifyDataSetChanged();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        LogUtil.i("", "onConfigurationChanged====================");
//        //restart
//            Intent intent = getIntent();
//            finish();
//            startActivity(intent);
//    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                Intent reintent = getIntent();
                finish();
                startActivity(reintent);
            }
        }
    };
}
