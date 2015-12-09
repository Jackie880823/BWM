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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.Tranck.MyAppsFlyer;
import com.bondwithme.BondWithMe.Tranck.MyPiwik;
import com.bondwithme.BondWithMe.adapter.MyFragmentPagerAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.receiver_service.ReportIntentService;
import com.bondwithme.BondWithMe.ui.wall.NewDiaryActivity;
import com.bondwithme.BondWithMe.ui.wall.WallFragment;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.NotificationUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.util.ZipUtils;
import com.bondwithme.BondWithMe.widget.InteractivePopupWindow;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.material.widget.SnackBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean iseventdate;


    //以下为暂定全局变量
    private boolean hasUpdate;
    private int jumpIndex;
    public static String LAST_LEAVE_INDEX = "lastLeaveIndex";
    private int leavePagerIndex = 0;
    private static View red_point_1;
    private static View red_point_2;
    private static View red_point_3;
    private static View red_point_4;
    private static View red_point_5;
    public static String STICKERS_NAME = "stickers";
    public static String IS_FIRST_LOGIN = "isFirstLogin";

    public static String STICKER_VERSION = "3";

    public static  Boolean IS_INTERACTIVE_USE;
    public static Map<String,InteractivePopupWindow> interactivePopupWindowMap;
    private static final int GET_DELAY = 0x28;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.getLoginedUser() == null) {
            App.getContextInstance().logout(this);
            return;
        }

        //表示这个用户已经登陆过。提供给登录界面判断显示sign up 还是 log in
        if (TextUtils.isEmpty(PreferencesUtil.getValue(this, Constant.HAS_LOGED_IN,"")))
        {
            PreferencesUtil.saveValue(this, Constant.HAS_LOGED_IN, Constant.HAS_LOGED_IN);
        }

        App.checkVerSion(this);

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
        if (refersh) {
            refersh = false;
            Intent reintent = getIntent();
            finish();
            startActivity(reintent);
        }
        super.onResume();
        if (fragments != null) {
            for (Fragment f : fragments) {
                f.getRetainInstance();
            }
        }
//        if(currentTabEnum!=null){
//            changeTitleColor();
//        }


        //初始小红点


        //提示异常反馈
        if (PreferencesUtil.getValue(this, Constant.APP_CRASH, false)) {
            PreferencesUtil.saveValue(this, Constant.APP_CRASH, false);
            showFeedbackDialog();
        }

    }

    private static MyDialog errorReportDialog;

    private void showFeedbackDialog() {
        if (errorReportDialog == null) {
            errorReportDialog = new MyDialog(this, R.string.error_feedback, R.string.error_feedback_desc);
            errorReportDialog.setCanceledOnTouchOutside(false);
            errorReportDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                }
            });
            errorReportDialog.setButtonAccept(R.string.report, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    Intent intentService = new Intent(App.getContextInstance(), ReportIntentService.class);
                    startService(intentService);
                    MessageUtil.showMessage(MainActivity.this, R.string.say_thanks_for_report);
                }
            });
        }
        if (!errorReportDialog.isShowing())
            errorReportDialog.show();
    }

    @Override
    protected void onStop() {
        if ("lastLeaveIndex".equals(LAST_LEAVE_INDEX)) {
            LAST_LEAVE_INDEX += App.getLoginedUser().getUser_id();
        }
        PreferencesUtil.saveValue(this, LAST_LEAVE_INDEX, currentTabEnum.ordinal());
        LAST_LEAVE_INDEX = "lastLeaveIndex";
        IS_FIRST_LOGIN = "isFirstLogin";
        super.onStop();
    }

    public static UserEntity getUser() {
        if (App.getLoginedUser() == null) {
            App.logout(null);
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
                            fragment.startActivityForResult(new Intent(getApplicationContext(), NewDiaryActivity.class), Constant.INTENT_REQUEST_CREATE_WALL);
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
                case GET_DELAY:
                    if(!interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)){
                        InteractivePopupWindow popupWindow = new InteractivePopupWindow(MainActivity.this,bottom,getResources().getString(R.string.text_tip_add_photo),1) ;
                        popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                            @Override
                            public void popDismiss() {

                            }
                        });
                        interactivePopupWindowMap.put(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO,popupWindow);
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

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if(MainActivity.IS_INTERACTIVE_USE){
//            handler.sendEmptyMessageDelayed(GET_DELAY, 500);
        }
//            firstOpPop = true;
//        }


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
        MyAppsFlyer.doLoginTrack();
        interactivePopupWindowMap = new HashMap<String,InteractivePopupWindow>();
        STICKERS_NAME = new LocalStickerInfoDao(this).getSavePath();
        IS_FIRST_LOGIN = IS_FIRST_LOGIN + STICKER_VERSION + App.getLoginedUser().getUser_id();
        boolean isFirstLogin = PreferencesUtil.getValue(this, IS_FIRST_LOGIN, true);
        IS_INTERACTIVE_USE = PreferencesUtil.getValue(this, InteractivePopupWindow.INTERACTIVE_TIP_START,true);
        LogUtil.d(TAG,"isFirstLogin========="+isFirstLogin+"======IS_FIRST_LOGIN======"+IS_FIRST_LOGIN);
        if (isFirstLogin) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        LogUtil.d(TAG,"isFirstLogin");
                        LocalStickerInfoDao dao = LocalStickerInfoDao.getInstance(MainActivity.this);
                        dao.deleteSticker(MainActivity.this,"Barry");
                        dao.deleteSticker(MainActivity.this,"PapaPanda");
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

        //检查显示小红点
        checkAndShowRedPoit();

        //TODO test mush delete
//        Intent intent = new Intent(this, CrashActivity.class);
//        startActivity(intent);
//        try {
//            MediaUtil.encodeFile2Mp4(this, FileUtil.getSaveRootPath(this, true).toString() + "/VID_20150827_111334.3gp", FileUtil.getSaveRootPath(this, true).toString() + "/" + System.currentTimeMillis() + ".mp4");
////            MediaUtil.encodeFile2Mp4(this, FileUtil.getSaveRootPath(this, true).toString() + "/VID_20150827_181646.mp4", FileUtil.getSaveRootPath(this, true).toString() + "/" + System.currentTimeMillis() + ".mp4");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 清除所有tab小红点
     */
    public static void clearAllRedPoint(){
        if(red_point_1!=null) {
            red_point_1.setVisibility(View.INVISIBLE);
        }
        if(red_point_2!=null) {
            red_point_2.setVisibility(View.INVISIBLE);
        }
        if(red_point_3!=null) {
            red_point_3.setVisibility(View.INVISIBLE);
        }
        if(red_point_4!=null) {
            red_point_4.setVisibility(View.INVISIBLE);
        }
        if(red_point_5!=null) {
            red_point_5.setVisibility(View.INVISIBLE);
        }
    }

    private void checkAndShowRedPoit() {
        if (App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_MESSAGE).size() != 0) {
            doSomething(TabEnum.chat);
        }
        if (App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_EVENT).size() != 0) {
            doSomething(TabEnum.event);
        }
        if (App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_WALL).size() != 0) {
            doSomething(TabEnum.wall);
        }
        if (App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_BIGDAY).size() != 0
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_MISS).size() != 0
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_NEWS).size() != 0
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_MEMBER).size() != 0
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_RECOMMENDED).size() != 0
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_GROUP).size() != 0
                ) {
            doSomething(TabEnum.more);
        }
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
        MyPiwik.piwikUser();
        MyAppsFlyer.appsFlyer5MainPager(tabEnum.toString());
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
        tvTitle.requestFocus();//让title获取焦点以便文字可以滚动
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
        mViewPager.setCurrentItem(tab.ordinal(),false);

    }

    public enum TabEnum {
        family, chat, wall, event, more;
    }

    SnackBar snackBar;


    private long startTime;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - startTime < 1000) {
                    if ("lastLeaveIndex".equals(LAST_LEAVE_INDEX)) {
                        LAST_LEAVE_INDEX += App.getLoginedUser().getUser_id();
                    }
                    PreferencesUtil.saveValue(this, LAST_LEAVE_INDEX, currentTabEnum.ordinal());
                    LAST_LEAVE_INDEX = "lastLeaveIndex";
                    IS_FIRST_LOGIN = "isFirstLogin";
                    App.getContextInstance().exit(MainActivity.this);
                } else {
                    MessageUtil.showMessage(this, R.string.click_again_exit, 1000);
                    startTime = System.currentTimeMillis();
                }
//                snackBar = new SnackBar(MainActivity.this,
//                        getString(R.string.msg_ask_exit_app),
//                        getString(R.string.text_yes), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        App.getContextInstance().exit(MainActivity.this);
//                    }
//                });
//                snackBar.show();
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
        unregisterReceiver(mReceiver);
//        MainActivity.this.getApplication().onTerminate();
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

    static boolean refersh;
    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                if (App.isBackground()) {
                    refersh = true;
                }
            }
        }
    };




}
