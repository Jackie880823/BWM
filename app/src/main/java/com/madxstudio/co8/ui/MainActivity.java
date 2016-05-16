package com.madxstudio.co8.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.Tranck.MyAppsFlyer;
import com.madxstudio.co8.Tranck.MyPiwik;
import com.madxstudio.co8.adapter.MyFragmentPagerAdapter;
import com.madxstudio.co8.dao.LocalStickerInfoDao;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.receiver_service.ReportIntentService;
import com.madxstudio.co8.ui.company.CompanyActivity;
import com.madxstudio.co8.ui.wall.NewDiaryActivity;
import com.madxstudio.co8.ui.wall.WallFragment;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NotificationUtil;
import com.madxstudio.co8.util.PreferencesUtil;
import com.madxstudio.co8.util.ZipUtils;
import com.madxstudio.co8.widget.InteractivePopupWindow;
import com.madxstudio.co8.widget.MyDialog;
import com.material.widget.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static String STICKER_VERSION = "4";

    //    public static Boolean IS_INTERACTIVE_USE;
    public static Map<String, InteractivePopupWindow> interactivePopupWindowMap;
    private static final int GET_DELAY = 0x28;

    public static final String ACTION_REFRESH_RED_POINT_4_FIMILY = "action_refresh_red_point";
    public static final String JUMP_INDEX = "jumpIndex";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, "onCreate: api: " + Constant.API_SERVER + "; site id: " + Constant.TRACKER_SITE_ID);
        if (App.getLoginedUser() == null) {//防止出现迷之不存在用户数据进入到主页
            App.getContextInstance().logout(this);
            return;
        }

        if (getUser().isShow_add_member())//新用户注册先进入添加好友。
        {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);

        //表示这个用户已经登陆过。提供给登录界面判断显示sign up 还是 log in
        if (TextUtils.isEmpty(PreferencesUtil.getValue(this, Constant.HAS_LOGED_IN, ""))) {
            PreferencesUtil.saveValue(this, Constant.HAS_LOGED_IN, Constant.HAS_LOGED_IN);
        }

        App.checkVerSion(this);

        //TODO del the test code
//        PreferencesUtil.saveValue(this, Constant.APP_CRASH, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void init4DefaultPage() {
        setDrawable();
        changeTitleColor(R.color.tab_color_press5);
        changeTitle(R.string.title_tab_my_news);
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        tabIv2.setImageResource(R.drawable.tab_news_select);
        ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_press5));
        tabTv2.setTextColor(Color.WHITE);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(currentTabEnum!=null){
//            changeTitleColor();
//        }
        int newJumpIndex = getIntent().getIntExtra(JUMP_INDEX, -1);
        /**新的跳转，以区分旧的，避免第一次启动重复set tab*/
        if (newJumpIndex != -1 && newJumpIndex != jumpIndex && currentTabEnum.ordinal() != newJumpIndex) {
            mViewPager.setCurrentItem(newJumpIndex, false);
        }

        if (fragments != null) {
            for (Fragment f : fragments) {
                f.getRetainInstance();
            }
        }

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
            errorReportDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    //TODO the follow line is for test , remember del it
//                    throw new NullPointerException();
                }
            });
            errorReportDialog.setButtonAccept(R.string.report, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    Intent intentService = new Intent(App.getContextInstance(), ReportIntentService.class);
                    startService(intentService);
                    MessageUtil.getInstance().showShortToast(R.string.say_thanks_for_report);
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
                            fragment.startActivityForResult(new Intent(getApplicationContext(), NewChatActivity.class), Constant.ACTION_MESSAGE_CREATE);
//                          fragment.startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
//                            if (commandlistener != null) {
//                                commandlistener.execute(rightButton);
//                            }
                            break;
                        case family:
                            fragment.startActivityForResult(new Intent(getApplicationContext(), WriteNewsActivity.class), Constant.ACTION_NEWS_CREATE);
//                            if (familyCommandListener != null) {
//                                familyCommandListener.execute(rightButton);
//                            }
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
                        disableRedPoint((TabEnum) msg.obj, false);
                    }
                    break;
                case GET_DELAY:
                    if (!interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)) {
                        InteractivePopupWindow popupWindow = new InteractivePopupWindow(MainActivity.this, bottom, getResources().getString(R.string.text_tip_add_photo), 1);
                        popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                            @Override
                            public void popDismiss() {

                            }
                        });
                        interactivePopupWindowMap.put(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO, popupWindow);
                    }
                    break;
            }

            return false;
        }
    });

    private void bondDatas(JSONObject jsonObject) throws JSONException {
        checkDataAndBond2View(TabEnum.wall, jsonObject.getString("wall"));
        checkDataAndBond2View(TabEnum.event, jsonObject.getString("event"));
        checkDataAndBond2View(TabEnum.family, jsonObject.getString("news"));
        checkDataAndBond2View(TabEnum.chat, jsonObject.getString("message"));
        checkDataAndBond2View(TabEnum.more, jsonObject.getString("miss"));
        checkDataAndBond2View(TabEnum.more, jsonObject.getString("bigDay"));
        checkDataAndBond2View(TabEnum.more, jsonObject.getString("group"));
        checkDataAndBond2View(TabEnum.more, jsonObject.getString("member"));
    }

    private void checkDataAndBond2View(TabEnum tab, String countString) {
        int count = Integer.valueOf(countString);

        if (count > 0 && tab != currentTabEnum) {
            disableRedPoint(tab, false);
        }
    }

    public void checkPoint() {
        new HttpTools(this).get(String.format(Constant.API_BONDALERT_MODULES_COUNT, MainActivity.getUser().getUser_id()), null, this, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    bondDatas(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

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
    protected void titleRightSearchEvent() {
        super.titleRightSearchEvent();
        if (TabEnum.chat == currentTabEnum) {
            if (commandlistener != null) {
                commandlistener.execute(rightSearchButton);
            }
        }
    }

    @Override
    public void initView() {
        MyAppsFlyer.doLoginTrack();
        interactivePopupWindowMap = new HashMap<String, InteractivePopupWindow>();
        STICKERS_NAME = new LocalStickerInfoDao(this).getSavePath();
        IS_FIRST_LOGIN = IS_FIRST_LOGIN + App.getLoginedUser().getUser_id();
        String isFirstLogin = PreferencesUtil.getValue(this, IS_FIRST_LOGIN, "0");
        LogUtil.d(TAG, "isFirstLogin=========" + isFirstLogin + "======IS_FIRST_LOGIN======" + IS_FIRST_LOGIN);
        if (!STICKER_VERSION.equals(isFirstLogin)) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        LogUtil.d(TAG, "isFirstLogin");
                        LocalStickerInfoDao dao = LocalStickerInfoDao.getInstance(MainActivity.this);
                        dao.deleteSticker(MainActivity.this, "Barry");
                        dao.deleteSticker(MainActivity.this, "PapaPanda");
                        List<String> pathList = FileUtil.getAllFilePathsFromAssets(MainActivity.this, "stickers");
                        if (null != pathList) {
                            List<String> list = dao.queryAllDefaultSticker();
                            if (list != null && list.size() > 0) {
                                boolean isDefaultData = false;
                                for (String listData : list) {
                                    isDefaultData = false;
                                    for (String string : pathList) {
                                        if (string.endsWith(".zip")) {
                                            string = string.substring(0, string.indexOf(".zip"));
                                        }
                                        if (listData.equals(string)) {
                                            isDefaultData = true;
                                            break;
                                        }
                                    }
                                    if (!isDefaultData) {
                                        dao.updateDefaultSticker(listData);
                                    }
                                }
                            }
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
            PreferencesUtil.saveValue(this, IS_FIRST_LOGIN, STICKER_VERSION);
        }

        mViewPager = getViewById(R.id.pager);
        fragments = new ArrayList<>();
//        WallFragment wallFragment = WallFragment.newInstance();
        fragments.add(WallFragment.newInstance());
        fragments.add(EventFragment.newInstance());
        fragments.add(NewsFragment.newInstance());
        fragments.add(MessageFragment.newInstance());
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
        jumpIndex = getIntent().getIntExtra(JUMP_INDEX, -1);
        if (jumpIndex != -1) {
            leavePagerIndex = jumpIndex;
        }

        red_point_1 = getViewById(R.id.red_point_1);
        red_point_2 = getViewById(R.id.red_point_2);
        red_point_3 = getViewById(R.id.red_point_3);
        red_point_4 = getViewById(R.id.red_point_4);
        red_point_5 = getViewById(R.id.red_point_5);

        NotificationUtil.setNotificationOtherHandle(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.ACTION_REFRESH_RED_POINT_4_FIMILY);
        registerReceiver(mReceiver, filter);

        //检查显示小红点
        checkAndShowRedPoit();
        boolean isCreateOrg = PreferencesUtil.getValue(this, Constant.IS_FIRST_CREATE_ORG + getUser().getUser_id(), false);
        if (isCreateOrg) {
            final LayoutInflater factory = LayoutInflater.from(this);
            View selectIntention = factory.inflate(R.layout.dialog_group_nofriend, null);
            final MyDialog dialog = new MyDialog(this, null, selectIntention);
            TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
            tv_no_member.setText(R.string.text_org_profile_page);
            TextView acceptTv = (TextView) selectIntention.findViewById(R.id.tv_cal);//确定
            TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);//取消
            selectIntention.findViewById(R.id.line_v).setVisibility(View.VISIBLE);
            acceptTv.setText(R.string.text_update_now);
            cancelTv.setText(R.string.text_later);
            cancelTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            dialog.setCanceledOnTouchOutside(false);
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    PreferencesUtil.saveValue(MainActivity.this, Constant.IS_FIRST_CREATE_ORG + getUser().getUser_id(), false);
                }
            });
            acceptTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    PreferencesUtil.saveValue(MainActivity.this, Constant.IS_FIRST_CREATE_ORG + getUser().getUser_id(), false);
                    startActivity(new Intent(MainActivity.this, CompanyActivity.class));
                }
            });
            dialog.show();
        }
        if (!isCreateOrg && ("1".equals(getUser().getDemo()) && "0".equals(getUser().getPending_org()))) {
            final LayoutInflater factory = LayoutInflater.from(this);
            View selectIntention = factory.inflate(R.layout.dialog_group_nofriend, null);
            final MyDialog dialog = new MyDialog(this, null, selectIntention);
            TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
            tv_no_member.setText(R.string.text_join_org_now);
            TextView acceptTv = (TextView) selectIntention.findViewById(R.id.tv_cal);//确定
            TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);//取消
            selectIntention.findViewById(R.id.line_v).setVisibility(View.VISIBLE);
            acceptTv.setText(R.string.text_org_join_now);
            cancelTv.setText(R.string.text_later);
            cancelTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            dialog.setCanceledOnTouchOutside(false);
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            acceptTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this, OrganisationActivity.class));
                }
            });
            dialog.show();
        }
    }


    /**
     * 清除所有tab小红点
     */
    public static void clearAllRedPoint() {
        if (red_point_1 != null) {
            red_point_1.setVisibility(View.INVISIBLE);
        }
        if (red_point_2 != null) {
            red_point_2.setVisibility(View.INVISIBLE);
        }
        if (red_point_3 != null) {
            red_point_3.setVisibility(View.INVISIBLE);
        }
        if (red_point_4 != null) {
            red_point_4.setVisibility(View.INVISIBLE);
        }
        if (red_point_5 != null) {
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
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_OTHER).size() != 0
                || App.getNotificationMsgsByType(NotificationUtil.MessageType.BONDALERT_MEMBER).size() != 0
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
    protected void initTitleBar() {
        super.initTitleBar();
        if (leavePagerIndex != 0) {
            //for last tab
            mViewPager.setCurrentItem(leavePagerIndex);
        } else {
//            init4DefaultPage();
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
            tabIv0.setImageResource(R.drawable.tab_workspace_select);
            ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_press1));
            tabTv0.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void requestData() {
        checkPoint();
    }


    private TabEnum currentTabEnum = TabEnum.wall;

    private void changeTitle(int title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    private void setDrawable() {
        tabIv0.setImageResource(R.drawable.tab_workspace);
        tabIv1.setImageResource(R.drawable.tab_event);
        tabIv2.setImageResource(R.drawable.tab_news);
        tabIv3.setImageResource(R.drawable.tab_message);
        tabIv4.setImageResource(R.drawable.tab_more);
        rightSearchButton.setVisibility(View.GONE);
        rightButton.setImageResource(R.drawable.btn_add);
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
                tabIv0.setImageResource(R.drawable.tab_workspace_select);
                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_press1));
                tabTv0.setTextColor(Color.WHITE);
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
                tabIv1.setImageResource(R.drawable.tab_event_select);
                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_press2));
                tabTv1.setTextColor(Color.WHITE);
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
                rightButton.setVisibility(View.VISIBLE);
                rightSearchButton.setVisibility(View.VISIBLE);
                rightButton.setImageResource(R.drawable.profile_add_message);
                tabIv3.setImageResource(R.drawable.tab_message_select);
                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_press3));
                tabTv3.setTextColor(Color.WHITE);
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
                tabTv4.setTextColor(Color.WHITE);
                break;
            case family:
                init4DefaultPage();
                break;
        }
        tvTitle.setSelected(true);
        tvTitle.requestFocus();//让title获取焦点以便文字可以滚动
        currentTabEnum = tabEnum;
        /**当前tab不显示红点*/
        disableRedPoint(currentTabEnum, true);
    }

    @Override
    public void onClick(View v) {
        TabEnum tab = null;

        switch (v.getId()) {
            case R.id.iv_tab_icon0:
                tab = TabEnum.values()[0];
                break;
            case R.id.iv_tab_icon1:
                tab = TabEnum.values()[1];
                break;
            case R.id.iv_tab_icon2:
                tab = TabEnum.values()[2];
                break;
            case R.id.iv_tab_icon3:
                tab = TabEnum.values()[3];
                break;
            case R.id.iv_tab_icon4:
                tab = TabEnum.values()[4];
                break;
            default:
                super.onClick(v);
                break;
        }
        if (tab == null || isCurrentTab(tab)) {
            return;
        }
        mViewPager.setCurrentItem(tab.ordinal(), false);

    }

    public enum TabEnum {
        wall, event, family, chat, more;
    }

    SnackBar snackBar;


    private long startTime;
    /**
     * 返回确定退出间隔时间
     */
    private static final int EXIT_BUTTON_RELAY_TIME = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (startTime == 0L || System.currentTimeMillis() - startTime > EXIT_BUTTON_RELAY_TIME) {
                    startTime = System.currentTimeMillis();
                    MessageUtil.getInstance().showToast(R.string.click_again_exit, EXIT_BUTTON_RELAY_TIME);
                } else {
                    if ("lastLeaveIndex".equals(LAST_LEAVE_INDEX)) {
                        LAST_LEAVE_INDEX += App.getLoginedUser().getUser_id();
                    }
                    PreferencesUtil.saveValue(this, LAST_LEAVE_INDEX, currentTabEnum.ordinal());
                    LAST_LEAVE_INDEX = "lastLeaveIndex";
                    IS_FIRST_LOGIN = "isFirstLogin";
                    App.getContextInstance().exit(MainActivity.this);
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

    public void disableRedPoint(TabEnum tab, boolean cancel) {
        int action = cancel ? View.GONE : View.VISIBLE;
        switch (tab) {
            case family:
                red_point_3.setVisibility(action);
                break;
            case chat:
                red_point_4.setVisibility(action);
                break;
            case wall:
                red_point_1.setVisibility(action);
                break;
            case event:
                red_point_2.setVisibility(action);
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
        if (errorReportDialog != null) {
            errorReportDialog.dismiss();
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

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_REFRESH_RED_POINT_4_FIMILY)) {
                disableRedPoint(TabEnum.family, true);
            }
        }
    };


}
