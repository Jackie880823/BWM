package com.bondwithme.BondWithMe.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;

/**
 * Created by wing on 15/8/3.
 */
public class TransitionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        if(isLogin()){
            startActivity(new Intent(this,MainActivity.class));
        }else{
            App.piwikGuest();
            startActivity(new Intent(this,StartActivity.class));
        }
        finish();

    }

    public boolean isLogin()
    {
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
}
