package com.bondwithme.BondWithMe.ui.introduction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.IntroductionPagerAdapter;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.IntroductionEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.start.StartActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

/**
 * Created 11/5/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class IntroductionActivity extends FragmentActivity implements View.OnClickListener{
    private static final String TAG = IntroductionActivity.class.getSimpleName();

    /**
     * 需要显示绍介的应用版，如果某个版本需要显示介绍可以修改当前常量与对应版本的versionCode一致，用于在
     * {@link #SHARED_PREFERENCES_VERSION}在首选项中的{@link #VERSION_CODE}的值比较，若当前常量大，
     * 无论应用是否为首次安装启动都将打开介绍页
     */
    private static final int SHOW_INTRODUCTION_VERSION = 59;
    private static final String SHARED_PREFERENCES_VERSION = "SHARED_PREFERENCES_VERSION";
    private static final String VERSION_CODE = "version_code";
    private static final String FIRST_START = "first_start";
    private SharedPreferences sharedPreferences;

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p/>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
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
        boolean firstStart = sharedPreferences.getBoolean(FIRST_START, true);

        // 上一次启动的保存的版本号比需要显示介绍页的版本小或者是应用首次安装启动
        if (versionCode < SHOW_INTRODUCTION_VERSION || firstStart) { //
            initView();
        } else {
            // 不需要显示介绍页，调用用跳转函数
            goMainOrSign();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_introduction);

        IntroductionPagerManager manager = new IntroductionPagerManager();
        manager.addCommonFragment(IntroductionPagerItemFragment.class, getIntroductions(), getTitles());
        IntroductionPagerAdapter adapter = new IntroductionPagerAdapter(getSupportFragmentManager(), manager);
        ScrollerViewPager vpIntroductions = (ScrollerViewPager) findViewById(R.id.introduction_view_paper);
        vpIntroductions.setAdapter(adapter);
        vpIntroductions.fixScrollSpeed();

        // just set viewPager
        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
        springIndicator.setViewPager(vpIntroductions);

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    private List<String> getTitles() {
        return Lists.newArrayList("1", "2", "3", "4", "5");
    }

    private List<IntroductionEntity> getIntroductions() {
        ArrayList<IntroductionEntity> list = new ArrayList<>();
        String[] introductionStrings = getResources().getStringArray(R.array.introduction_descriptions);
        for (int i = 0; i < introductionStrings.length; i++) {
            IntroductionEntity entity = new IntroductionEntity();
            entity.setDescription(introductionStrings[i]);
            entity.setImagesResId(R.drawable.splash_page_01 + i);
            list.add(entity);
        }
        return list;
    }

    /**
     * 如果不需要显示介绍页就调用这个方法跳转至应用主界面或者登陆页并关闭当前Activity
     */
    private void goMainOrSign() {
        if (isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, StartActivity.class));
        }
        finish();
    }


    /**
     * 判断是否已经登录过了
     *
     * @return
     */
    public boolean isLogin() {
        UserEntity userEntity = App.getLoginedUser();
        if (userEntity != null) {
            String tokenString = PreferencesUtil.getValue(this, Constant.HTTP_TOKEN, null);
            if (!TextUtils.isEmpty(tokenString)) {
                App.userLoginSuccessed(this, userEntity, new Gson().fromJson(tokenString, AppTokenEntity.class));
                return true;
            }
        }
        return false;
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when you are no longer visible to the user.  You will next
     * receive either {@link #onRestart}, {@link #onDestroy}, or nothing,
     * depending on later user activity.
     * <p/>
     * <p>Note that this method may never be called, in low memory situations
     * where the system does not have enough memory to keep your activity's
     * process running after its {@link #onPause} method is called.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestart
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onDestroy
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     * <p/>
     * <p><em>Note: do not count on this method being called as a place for
     * saving data! For example, if an activity is editing data in a content
     * provider, those edits should be committed in either {@link #onPause} or
     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
     * free resources like threads that are associated with an activity, so
     * that a destroyed activity does not leave such things around while the
     * rest of its application is still running.  There are situations where
     * the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to
     * do things that are intended to remain around after the process goes
     * away.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void goStartActivity(String type){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 保存第一次启动标识为false
        editor.putBoolean(FIRST_START, false);

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
}
