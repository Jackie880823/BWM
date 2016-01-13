package com.bondwithme.BondWithMe.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.Tranck.MyAppsFlyer;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.introduction.IntroductionActivity;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;

/**
 * Created by wing on 15/8/3.
 */
public class TransitionActivity extends Activity {

    private final static int HANDLER_CHECK_LOGIN = 1;

    private final static int HANDLER_SEND_MESSAGE_DELAYED_TIME = 3000;
    private boolean initDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        init(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                long end = System.currentTimeMillis();
                while ((end - start < HANDLER_SEND_MESSAGE_DELAYED_TIME) || !initDone) {
                    end = System.currentTimeMillis();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //start activity
//                        if (isLogin()) {
//                            startActivity(new Intent(TransitionActivity.this, MainActivity.class));
//                        } else {
//                            startActivity(new Intent(TransitionActivity.this, StartActivity.class));
//                        }
                        startActivity(new Intent(TransitionActivity.this, IntroductionActivity.class));
                        finish();
                    }
                });

            }
        }).start();
    }

    /**
     * 所有初始化的代码放在这个位置
     */
    private void init(final Activity content) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FacebookSdk.sdkInitialize(content);
                MyAppsFlyer.appsFlyerInit();
                //TODO
                //...往后添加你们要初始化的代码
                initDone = true;
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_CHECK_LOGIN:
                    if (isLogin()) {
                        startActivity(new Intent(TransitionActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(TransitionActivity.this, StartActivity.class));
                    }
                    finish();
                    break;
            }
        }
    };

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
                App.initToken(userEntity.getUser_login_id(),new Gson().fromJson(tokenString, AppTokenEntity.class));
                return true;
            }
        }
        return false;
    }


}
