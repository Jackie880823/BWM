package com.madx.bwm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.http.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;

public class ProfileResetPasswordActivity extends Activity {

    EditText etOld;
    EditText etFstPw;
    EditText etSecPw;

    ImageView ivOld;
    ImageView ivFst;
    ImageView ivSec;

    TextView tvInvalid;//旧密码输入不对的时候显示提示


    boolean blnPasswordNum = false;//密码长度判断
    boolean blnOldRight = false;//旧密码判断
    boolean blnEquals = false;//重设密码两次是否相等

    TextView tvBack;
    TextView tvDone;//请求修改密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_reset_password);

        etOld = (EditText)findViewById(R.id.et_old_password);
        etFstPw = (EditText) findViewById(R.id.et_first_pw);
        etSecPw = (EditText) findViewById(R.id.et_second_pw);

        ivFst = (ImageView) findViewById(R.id.iv_fst);
        ivSec = (ImageView) findViewById(R.id.iv_sec);
        ivOld = (ImageView)findViewById(R.id.iv_old);

        tvInvalid = (TextView)findViewById(R.id.tv_invalid);

        tvBack = (TextView)findViewById(R.id.tv_back);
        tvDone = (TextView)findViewById(R.id.tv_done);

        etOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (MainActivity.getUser().getUser_password().equals(MD5(etOld.getText().toString()))) {
                        ivOld.setImageResource(R.drawable.correct);
                        blnOldRight = true;
                    } else {
                        ivOld.setImageResource(R.drawable.wrong);
                        blnOldRight = false;
                        tvInvalid.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        etFstPw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if ((etFstPw.getText().toString().length() > 7) && (etFstPw.getText().toString().length() < 17)) {
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
                    blnEquals = true;
                } else {
                    ivSec.setImageResource(R.drawable.wrong);
                    blnEquals = false;
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blnOldRight && blnPasswordNum && blnEquals)//两次输入密码相同 && 密码长度符合要求
                {

                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("user_password", MD5(etSecPw.getText().toString()));
                    final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);


                    RequestInfo requestInfo = new RequestInfo();
                    requestInfo.url = Constant.API_UPDATE_PASSWORD + MainActivity.getUser().getUser_id();
                    requestInfo.jsonParam = jsonParamsString;


                    new HttpTools(ProfileResetPasswordActivity.this).put(requestInfo,  this, new HttpCallback() {
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
                                    Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_password_specified_invalid), Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_password_changed_successfully), Toast.LENGTH_SHORT).show();
                                    App.logout(ProfileResetPasswordActivity.this);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });





//                    //这个Url拼接需要换换吗？？
//                    StringRequest stringRequestUpdatePassword = new StringRequest(Request.Method.PUT, Constant.API_UPDATE_PASSWORD + MainActivity.getUser().getUser_id(), new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            try {
//                                String responseStatus;
//                                JSONObject jsonObject = new JSONObject(response);
//                                responseStatus = jsonObject.getString("response_status");//申请验证码状态信息，失败也分两种情况
//                                if (responseStatus.equals("Fail")) {
//                                    Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_password_specified_invalid), Toast.LENGTH_SHORT).show();
//
//                                } else {
//                                    Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_password_changed_successfully), Toast.LENGTH_SHORT).show();
//                                    App.logout(ProfileResetPasswordActivity.this);
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
//                            Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
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
//                    VolleyUtil.addRequest2Queue(ProfileResetPasswordActivity.this, stringRequestUpdatePassword, "");

                }
                else if (!blnOldRight)
                {
                    Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_old_password_wrong), Toast.LENGTH_SHORT).show();
                }
                else {
                    ivFst.setImageResource(R.drawable.wrong);
                    ivSec.setImageResource(R.drawable.wrong);

                    etFstPw.setText("");
                    etSecPw.setText("");

                    Toast.makeText(ProfileResetPasswordActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

}
