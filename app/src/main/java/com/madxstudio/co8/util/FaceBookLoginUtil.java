package com.madxstudio.co8.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.FaceBookUserEntity;
import com.madxstudio.co8.interfaces.LogInStateListener;
import com.madxstudio.co8.interfaces.LogOutStateListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class FaceBookLoginUtil{
    private  static FaceBookLoginUtil mFaceBookLoginUtil=null;
    private static Activity loginActivity=null;
    private static Fragment loginFragment=null;
    private View loginClickView=null;
    private View logOutView=null;
    private  List<String> permissions;
    private LogInStateListener mBookLoginStateChanged;
    private LogOutStateListener mBookLogOutStateListener;
    private CallbackManager callbackManager;
    private FaceBookCallBackListener callback=new FaceBookCallBackListener();
    private  OnFaceBookLoginClickListener onFaceBookLoginClickListener=new OnFaceBookLoginClickListener();
    private boolean isLogOut=false;
    public static FaceBookLoginUtil getInstance() {
        if(mFaceBookLoginUtil==null){
            mFaceBookLoginUtil=new FaceBookLoginUtil();
        }
        return mFaceBookLoginUtil;
    }
    public  void SetFaceBookLoginActivity(Activity activity){
        if(activity==null){
            throw new  NullPointerException("login activity is null");
        }else{
            loginActivity=activity;
        }
    }

    public  void SetFaceBookLoginButton(View view){
        loginClickView=view;
    }

    public void SetFaceBookLogOutButton(View view){
        logOutView=view;
    }

    public  void SetFaceBookReadPermission(String  array){
        if(array==null){
            permissions=Arrays.asList("public_profile");
        }else{
            permissions=Arrays.asList(array);
        }
    }

    public  void SetOnFaceBookLoginStateListener(LogInStateListener LoginStateChanged){
        if(LoginStateChanged==null){
            throw new NullPointerException("LoginStateListener is null");
        }else{
            mBookLoginStateChanged=LoginStateChanged;
        }
    }

    public void SetOnFaceBookLogOutListener(LogOutStateListener logoutListener){
        mBookLogOutStateListener=logoutListener;
    }

    public void open() {
        FacebookSdk.sdkInitialize(loginActivity);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, callback);
        if(loginClickView!=null){
            loginClickView.setOnClickListener(onFaceBookLoginClickListener);
        }
        if(logOutView!=null){
            logOutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logOut();
                    isLogOut=true;
                    mBookLogOutStateListener.OnLogOutListener(isLogOut,"facebook");
                }
            });
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void OnDestory(){
    }

    public void SetFaceBookLoginFragment(Fragment fragment) {
        loginFragment = fragment;
    }

    private class FaceBookCallBackListener implements FacebookCallback<LoginResult>{
        @Override
        public void onSuccess(LoginResult result) {
            fetchUserInfo(result.getAccessToken());
        }
        @Override
        public void onCancel() {
            LoginManager.getInstance().logOut();//清除Facebook授权缓存
            mBookLoginStateChanged.OnLoginError(loginActivity.getString(R.string.text_start_cancle));
        }
        @Override
        public void onError(FacebookException error) {
            LoginManager.getInstance().logOut();//清除Facebook授权缓存
            mBookLoginStateChanged.OnLoginError(error.getMessage());
        }

    }

    private  class OnFaceBookLoginClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //这里要填入fragment，为什么呢？
            LoginManager.getInstance().logInWithReadPermissions(loginFragment, permissions);
        }
    }
    private void fetchUserInfo(final AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        try{
                            if(response.getError()!=null){
                                mBookLoginStateChanged.OnLoginError(response.getError().toString());
                            }else if(response.getConnection().getResponseCode()==200){
                                FaceBookUserEntity faceBookUserEntity =new FaceBookUserEntity();
//                                faceBookUserEntity.setEmail(object.getString("email"));
                                faceBookUserEntity.setGender(object.getString("gender"));
                                faceBookUserEntity.setToken(accessToken.getToken().toString());//每次登录这个token都变化。有何作用？
//                                faceBookUserEntity.setLink(object.getString("link"));
                                faceBookUserEntity.setFirstname(object.getString("first_name"));
                                faceBookUserEntity.setLastname(object.getString("last_name"));
//                                faceBookUserEntity.setLocale(object.getString("locale"));
//                                faceBookUserEntity.setTimezone(object.getString("timezone"));
                                faceBookUserEntity.setUserId(object.getString("id"));
                                faceBookUserEntity.setUserName(object.getString("name"));
                                mBookLoginStateChanged.OnLoginSuccess(faceBookUserEntity, "facebook");
                            }
                        }catch(Exception e){
                            mBookLoginStateChanged.OnLoginError(e.getMessage());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
