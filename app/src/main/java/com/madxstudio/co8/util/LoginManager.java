package com.madxstudio.co8.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madxstudio.co8.interfaces.LogInStateListener;
import com.madxstudio.co8.interfaces.LogOutStateListener;

/**
 * Created by christepherzhang on 15/7/16.
 */
public class LoginManager {
    private static FaceBookLoginUtil fbUtil=null;
    /**
     * 登陆工具类，
     *
     */
    public static void initialize(Context context){
        fbUtil=FaceBookLoginUtil.getInstance();
    }
    /**
     * 1 if you use facebook account login,you must call this method and set params.
     * @param activity
     * @param loginButton
     * @param arrarPermission
     * @param
     * @param loginstateListener
     */
    public  static void setFaceBookLoginParams(Activity activity,Fragment fragment,View loginButton,String arrarPermission,LogInStateListener loginstateListener){
        fbUtil.SetFaceBookLoginActivity(activity);
        fbUtil.SetFaceBookLoginFragment(fragment);
        fbUtil.SetFaceBookLoginButton(loginButton);
        fbUtil.SetFaceBookReadPermission(arrarPermission);
        fbUtil.SetOnFaceBookLoginStateListener(loginstateListener);
        fbUtil.open();
    }
    /**
     * if you use facebook logout,you must call this method and set params
     * @param
     * @param logoutButton
     * @param logOutStateListener
     */
    public static void setFaceBookLogOutParams(View logoutButton,LogOutStateListener logOutStateListener){
        //fbUtil.SetFaceBookLoginActivity(activity);
        fbUtil.SetFaceBookLogOutButton(logoutButton);
        fbUtil.SetOnFaceBookLogOutListener(logOutStateListener);
        fbUtil.open();
    };
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(fbUtil!=null){
            fbUtil.onActivityResult(requestCode, resultCode, data);
        }
    }
    public static void OnDestory(){
        if(fbUtil!=null){
            fbUtil.OnDestory();
        }
    }
}
