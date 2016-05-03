package com.madxstudio.co8.ui.introduction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.appsflyer.AppsFlyerLib;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.IntroductionPagerAdapter;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.IntroductionEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.start.StartActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.PreferencesUtil;
import com.madxstudio.co8.widget.SpringIndicator;
import com.madxstudio.co8.widget.viewpager.ScrollerViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示应用的界面，只在第一次安装本应用没有且没有记录账号显示本界面，已有账号或不是第一次启动些应用则从此{@code Activity}跳转到{@link MainActivity}.<br/>
 *
 * <p/>
 * Created 11/5/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class IntroductionActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = IntroductionActivity.class.getSimpleName();

    /**
     * 需要显示绍介的应用版，如果某个版本需要显示介绍可以修改当前常量与对应版本的versionCode一致，用于在
     * {@link #SHARED_PREFERENCES_VERSION}在首选项中的{@link #VERSION_CODE}的值比较，若当前常量大，
     * 无论应用是否为首次安装启动都将打开介绍页
     *
     * @deprecated 这个值目前还没有被使用
     */
    private static final int SHOW_INTRODUCTION_VERSION = 59;
    private static final String SHARED_PREFERENCES_VERSION = "SHARED_PREFERENCES_VERSION";
    private static final String VERSION_CODE = "version_code";
    private static final String FIRST_START = "first_start";
    private SharedPreferences sharedPreferences;

    /**
     * Activity已经启动，在这里检测本应用是否是安装第一次开启（或更新前没有帐户记录）则初始化UI显示介绍页
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_VERSION, Context.MODE_PRIVATE);
        int versionCode = sharedPreferences.getInt(VERSION_CODE, 0);
        LogUtil.d(TAG, "onCreate& preferences saved version code: " + versionCode);
        // 获取是否为第一次启动的标识：如果没有保存值默认得到一个true的值，说明应用还没有启动过
        boolean firstStart = sharedPreferences.getBoolean(FIRST_START, true);

        // 上一次启动的保存的版本号比需要显示介绍页的版本小或者是应用首次安装启动
//        if (versionCode < SHOW_INTRODUCTION_VERSION || firstStart) { //
//        if (firstStart && !isLogin()) { //
//            initView();
//        } else {
            // 不需要显示介绍页，调用用跳转函数
            goMainOrSign();
//        }
    }

    private void initView() {
        setContentView(R.layout.activity_introduction);

        // 介绍页的布标管理器，管理布标显示的标题和页面内容
        IntroductionPagerManager manager = new IntroductionPagerManager();
        manager.addCommonFragment(IntroductionPagerItemFragment.class, getIntroductions(), getTitles());
        IntroductionPagerAdapter adapter = new IntroductionPagerAdapter(getSupportFragmentManager(), manager);

        // ViewPager
        ScrollerViewPager vpIntroductions = (ScrollerViewPager) findViewById(R.id.introduction_view_paper);
        vpIntroductions.setAdapter(adapter);
        vpIntroductions.fixScrollSpeed();

        // just set viewPager
        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
        springIndicator.setViewPager(vpIntroductions);

        // 设置底部两个按钮的监听
        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    private List<String> getTitles() {
        return Lists.newArrayList("1", "2", "3", "4", "5");
    }

    /**
     * 获取介绍内容列表
     * @return 返回介绍内容封装类的实例列表
     */
    private List<IntroductionEntity> getIntroductions() {
        List<IntroductionEntity> list = new ArrayList<>();

        // 获取介绍的文字描述
        String[] introductionStrings = getResources().getStringArray(R.array.introduction_descriptions);

        for (int i = 0; i < introductionStrings.length; i++) {
            IntroductionEntity entity = new IntroductionEntity();
            // 介绍描述封装
            entity.setDescription(introductionStrings[i]);
            // 介绍展示图片封装
            entity.setImagesResId(R.drawable.splash_page_01 + i);
            // 将封装好的实例添加到列表
            list.add(entity);
        }

        return list;
    }

    /**
     * 如果不需要显示介绍页就调用这个方法跳转至应用主界面或者登陆页并关闭当前Activity
     */
    private void goMainOrSign() {
        if (isLogin()) {
            startActivity(new Intent(this, MainActivity.class));//这里是已经登录过的 唯一之路。
        } else {
            // 没有登陆跳转到登陆页
            startActivity(new Intent(this, StartActivity.class));
        }
        finish();
    }


    /**
     * 判断是否已经登录过了
     *
     * @return  {@code true}:   已经登陆<br/>
     *          {@code false}:  没有登陆
     */
    public boolean isLogin() {
        UserEntity userEntity = App.getLoginedUser();
        if (userEntity != null) {
            String tokenString = PreferencesUtil.getValue(this, Constant.HTTP_TOKEN, null);
            if (!TextUtils.isEmpty(tokenString)) {
                App.initOthers(userEntity.getUser_login_id(), new Gson().fromJson(tokenString, AppTokenEntity.class));
                return true;
            }
        }
        return false;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                goStartActivity(StartActivity.SHOW_LOG_IN);
                break;
            case R.id.btn_sign_up:
                goStartActivity(StartActivity.SHOW_SIGN_UP);
                break;
        }
    }

    /**
     * 点击“Sing Up” 或 “Log In”按钮从这里跳转到跳转到注册或登陆页，{@code type}为传入的跳转类型{@link StartActivity#SHOW_SIGN_UP}或{@link StartActivity#SHOW_LOG_IN}
     * @param type  {@linkplain StartActivity#SHOW_SIGN_UP}:    点击了{@link R.id#btn_sign_up}按钮传此值<br/>
     *              - {@linkplain StartActivity#SHOW_LOG_IN}:     点击了{@link R.id#btn_login}按钮传此值<br/>
     */
    private void goStartActivity(String type) {
        // 保存第一次启动标识为false
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_START, false);

        // 获取版本号并保存
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PROVIDERS);
            LogUtil.d(TAG, "onCreate& package version Code: " + packageInfo.versionCode);
            // 保存当前应用版本的版本号
            editor.putInt(VERSION_CODE, packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 提交修改
            editor.apply();
        }

        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra(StartActivity.TYPE, type);
        startActivity(intent);

        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppsFlyerLib.onActivityResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppsFlyerLib.onActivityPause(this);
    }
}
