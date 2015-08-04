package com.bondwithme.BondWithMe.ui;

import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResetPasswordActivity extends BaseActivity{


    EditText etFstPw;
    EditText etSecPw;

    ImageView ivFst;
    ImageView ivSec;

    Button btnNext;

    boolean blnPasswordNum = false;

    private UserEntity userEntity = new UserEntity();
    private List<UserEntity> userList;

//    ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_login);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_reset_password));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        
//        progressDialog = new ProgressDialog(this,getResources().getString(R.string.text_dialog_loading));

        userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");

        btnNext = getViewById(R.id.btn_next);
        etFstPw = getViewById(R.id.et_first_pw);
        etSecPw = getViewById(R.id.et_second_pw);
        ivFst = getViewById(R.id.iv_fst);
        ivSec = getViewById(R.id.iv_sec);

        etFstPw.requestFocus();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((etFstPw.getText().toString().equals(etSecPw.getText().toString())) && blnPasswordNum)//两次输入密码相同 && 密码长度符合要求
                {

                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("user_password", MD5(etSecPw.getText().toString()));
                    final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);


                    RequestInfo requestInfo = new RequestInfo();
                    requestInfo.url = Constant.API_UPDATE_PASSWORD + userEntity.getUser_id();
                    requestInfo.jsonParam = jsonParamsString;


                    new HttpTools(ResetPasswordActivity.this).put(requestInfo, this,new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResult(String response) {

                            try {
                                String responseStatus;
                                JSONObject jsonObject = new JSONObject(response);
                                responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
                                if (responseStatus.equals("Fail")) {
                                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_password_specified_invalid), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_password_changed_successfully), Toast.LENGTH_SHORT).show();

                                    login();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });



//                    //这个Url拼接需要换换吗？？
//                    StringRequest stringRequestUpdatePassword = new StringRequest(Request.Method.PUT, Constant.API_UPDATE_PASSWORD + userEntity.getUser_id(), new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            try {
//                                String responseStatus;
//                                JSONObject jsonObject = new JSONObject(response);
//                                responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
//                                if (responseStatus.equals("Fail")) {
//                                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_password_specified_invalid), Toast.LENGTH_SHORT).show();
//
//                                } else {
//                                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_password_changed_successfully), Toast.LENGTH_SHORT).show();
//
//                                    login();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            //TODO
//                            error.printStackTrace();
//                            Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        }
//                    }) {
//
//                        @Override
//                        public byte[] getBody() throws AuthFailureError {
//                            return jsonParamsString.getBytes();
//                        }
//
//                    };
//
//                    stringRequestUpdatePassword.setShouldCache(false);
//                    VolleyUtil.addRequest2Queue(ResetPasswordActivity.this, stringRequestUpdatePassword, "");

                } else {
                    ivFst.setImageResource(R.drawable.wrong);
                    ivSec.setImageResource(R.drawable.wrong);

                    etFstPw.setText("");
                    etSecPw.setText("");

                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });


        etFstPw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if ((etFstPw.getText().toString().length() > 5) && (etFstPw.getText().toString().length() < 17)) {
                        ivFst.setImageResource(R.drawable.correct);
                        blnPasswordNum = true;
                    } else {
                        ivFst.setImageResource(R.drawable.wrong);
                        blnPasswordNum = false;
                    }
                }
            }
        });

        etSecPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO
                if ((etFstPw.getText().toString().equals(etSecPw.getText().toString())) && blnPasswordNum) {
                    ivSec.setImageResource(R.drawable.correct);
                } else {
                    ivSec.setImageResource(R.drawable.wrong);
                }
            }
        });

        etSecPw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if ((etFstPw.getText().toString().equals(etSecPw.getText().toString())) && blnPasswordNum) {
                    ivSec.setImageResource(R.drawable.correct);
                } else {
                    ivSec.setImageResource(R.drawable.wrong);
                }


            }
        });

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //string -> MD5
    public static String MD5(String str)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for(int i = 0; i < charArray.length; i++)
        {
            byteArray[i] = (byte)charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for( int i = 0; i < md5Bytes.length; i++)
        {
            int val = ((int)md5Bytes[i])&0xff;
            if(val < 16)
            {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    //判断字符串的是否为字母，字母true。
    public static boolean test(String s)
    {
        char   c   =   s.charAt(0);
        int   i   =(int)c;
        if((i>=65&&i<=90)||(i>=97&&i<=122))
        {
            return   true;
        }
        else
        {
            return   false;
        }
    }

    //登录
    public void login()
    {
        btnNext.setClickable(false);
//        progressDialog.show();
        HashMap<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("username", userEntity.getUser_login_id());
        jsonParams.put("user_phone", userEntity.getUser_phone());
        jsonParams.put("login_type", userEntity.getUser_login_type());

        jsonParams.put("user_country_code", userEntity.getUser_country_code());
        jsonParams.put("user_password", MD5(etSecPw.getText().toString()));
        jsonParams.put("user_uuid", Settings.Secure.getString(ResetPasswordActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        jsonParams.put("user_app_version", "1.8.0");
        jsonParams.put("user_app_os", "android");
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_LOGIN, params);


        new HttpTools(ResetPasswordActivity.this).get(url, null,this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());

                    AppTokenEntity tokenEntity = gson.fromJson(jsonObject.getString("token"), AppTokenEntity.class);

                    if (userList != null && userList.get(0) != null) {
                        UserEntity userEntity = userList.get(0);//登录的用户数据
                        App.userLoginSuccessed(ResetPasswordActivity.this,userEntity, tokenEntity);
//                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    btnNext.setClickable(true);
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                btnNext.setClickable(true);
//                progressDialog.dismiss();
                Log.i("", "error=====" + e.getMessage());
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });




//        StringRequest stringRequestLogin = new StringRequest(url, new Response.Listener<String>() {
//
//            GsonBuilder gsonb = new GsonBuilder();
//
//            Gson gson = gsonb.create();
//
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {
//                    }.getType());
//
//                    AppTokenEntity tokenEntity = gson.fromJson(jsonObject.getString("token"), AppTokenEntity.class);
//
//                    if (userList != null && userList.get(0) != null) {
//                        UserEntity userEntity = userList.get(0);//登录的用户数据
//                        App.changeLoginedUser(userEntity, tokenEntity);
//                        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
////                                    intent.putExtra("user", userEntity);
//                        PreferencesUtil.saveValue(ResetPasswordActivity.this, "user", gson.toJson(userEntity));
//                        PreferencesUtil.saveValue(ResetPasswordActivity.this, "token", gson.toJson(tokenEntity));
//
//                        startActivity(intent);
//                        finish();
//                        progressDialog.dismiss();
//                    }
//
//                } catch (JSONException e) {
//                    btnNext.setClickable(true);
//                    progressDialog.dismiss();
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                btnNext.setClickable(true);
//                progressDialog.dismiss();
//                Log.i("", "error=====" + error.getMessage());
//                Toast.makeText(ResetPasswordActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        VolleyUtil.addRequest2Queue(ResetPasswordActivity.this, stringRequestLogin, "");
    }
}
