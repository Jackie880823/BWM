package com.madx.bwm.ui;

import android.content.Intent;
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

import com.gc.materialdesign.widgets.SnackBar;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MyFragmentPagerAdapter;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.ui.wall.WallFragment;
import com.madx.bwm.ui.wall.WallNewActivity;
import com.madx.bwm.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页Activity,包含了头部和底部，无需定义中间内容(ViewPaper)
 */
public class MainActivity extends BaseActivity {

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
    private static boolean hasUpdate;
    private static MainActivity mainActivityInstance;
    private String leaveGroup;
    public static final String LAST_LEAVE_INDEX = "lastLeaveIndex";
    private int leavePagerIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (App.getLoginedUser() == null) {
            finish();
            return;
        }

        super.onCreate(savedInstanceState);
        mainActivityInstance = this;
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
        super.onStop();
        PreferencesUtil.saveValue(this, LAST_LEAVE_INDEX, currentTabEnum.ordinal());
//        editor.putInt(LAST_LEAVE_INDEX, mViewPager.getCurrentItem());
    }

    public static UserEntity getUser() {
        if (App.getLoginedUser() == null) {
            App.exit(mainActivityInstance);
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
        tvTitle.setText(R.string.title_tab_wall);
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
                            mViewPager.setCurrentItem(0);
                            //startActivity(new Intent(MainActivity.this, MainActivity.class));
                            break;
                        case more:
                            // TODO
                            break;
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


        leavePagerIndex = PreferencesUtil.getValue(this, LAST_LEAVE_INDEX, 0);
        leaveGroup = getIntent().getStringExtra("leaveGroup");
        if ("leaveGroup".equals(leaveGroup)) {
            leavePagerIndex = 3;
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
        switch (tabEnum) {
            case wall:
                setDrawable();
                //ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_press1));
                changeTitleColor(R.color.tab_color_press1);
                changeTitle(R.string.title_tab_wall);
//                ivTab0.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab2.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
//                ivTab4.setBackgroundColor(getResources().getColor(R.color.tab_color_normal));
                leftButton.setVisibility(View.INVISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                tabIv1.setImageResource(R.drawable.tab_wall_select);
                ivTab1.setBackgroundColor(getResources().getColor(R.color.tab_color_press1));
                tabTv1.setTextColor(Color.BLACK);
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
                leftButton.setVisibility(View.VISIBLE);
                leftButton.setImageResource(R.drawable.btn_family);
                rightButton.setVisibility(View.VISIBLE);
                tabIv3.setImageResource(R.drawable.tab_message_select);
                ivTab3.setBackgroundColor(getResources().getColor(R.color.tab_color_press3));
                tabTv3.setTextColor(Color.BLACK);
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
        family, chat,wall, event, more;
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
                        App.exit(MainActivity.this);
                    }
                });
                snackBar.show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        if (snackBar != null) {
            snackBar.dismiss();
        }
        super.onDestroy();
    }
}
