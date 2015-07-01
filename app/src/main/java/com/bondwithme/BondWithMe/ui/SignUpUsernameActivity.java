package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;

public class SignUpUsernameActivity extends BaseActivity {

    EditText etUserName;//用户名
    ImageView ivUserName;//对 叉

    EditText etFstPw;//第一次密码
    ImageView ivFst;

    EditText etSecPw;//第二次密码
    ImageView ivSec;

    boolean blnPasswordNum = false;//密码长度
    boolean blnLogin = false;

    Button mNext;//跳转到手机号验证

    UserEntity userEntity = new UserEntity();

    RelativeLayout rlProgress;

    @Override
    public int getLayout() {
        return R.layout.activity_sign_up_username;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_login);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_sign_up));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
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

        etUserName = getViewById(R.id.et_username);
        ivUserName = getViewById(R.id.iv_username);

        etFstPw = getViewById(R.id.et_first_pw);
        ivFst = getViewById(R.id.iv_fst);

        etSecPw = getViewById(R.id.et_second_pw);
        ivSec = getViewById(R.id.iv_sec);

        mNext = getViewById(R.id.btn_next);

        rlProgress = getViewById(R.id.rl_progress);

        etUserName.requestFocus();//输入用户名的editview先获得焦点，失去焦点后判断是否能够使用

        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if ((etUserName.getText().toString().length() > 4)) {
                        HashMap<String, String> jsonParams = new HashMap<String, String>();
                        jsonParams.put("login_id", etUserName.getText().toString());
                        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("condition", jsonParamsString);

                        new HttpTools(SignUpUsernameActivity.this).get(Constant.API_LOGINID_AVAILABILITY, params, this,new HttpCallback() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onResult(String response) {

                                String responseStatus;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    responseStatus = jsonObject.getString("response_status");
                                    //手机注册合法性检查状态信息为Fail时
                                    if (responseStatus.equals("Fail")) {
                                        //fail：手机号注册过的时候，展示Text，Toast。
                                        ivUserName.setImageResource(R.drawable.wrong);
                                        blnLogin = false;
                                        Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_account_already_exist), Toast.LENGTH_SHORT).show();
                                    } else {
                                        ivUserName.setImageResource(R.drawable.correct);
                                        blnLogin = true;

                                    }

                                } catch (JSONException e) {
                                    ivUserName.setImageResource(R.drawable.wrong);
                                    blnLogin = false;
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                ivUserName.setImageResource(R.drawable.wrong);
                                blnLogin = false;
                            }

                            @Override
                            public void onCancelled() {

                            }

                            @Override
                            public void onLoading(long count, long current) {

                            }
                        });
                    } else {
                        ivUserName.setImageResource(R.drawable.wrong);
                        blnLogin = false;
                        Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_invalid_account_name), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        etFstPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if ((etFstPw.getText().toString().length() > 5) && (etFstPw.getText().toString().length() < 17))
                {
                    ivFst.setImageResource(R.drawable.correct);
                    if ((etSecPw.getText().toString().length() > 5) && (etSecPw.getText().toString().length() < 17) && (etFstPw.getText().toString().equals(etSecPw.getText().toString())))
                    {
                        ivFst.setImageResource(R.drawable.correct);
                        ivSec.setImageResource(R.drawable.correct);
                    }
                    else
                    {
                        ivSec.setImageResource(R.drawable.wrong);
                    }
                }
                else
                {
                    ivFst.setImageResource(R.drawable.wrong);
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

                if ((etSecPw.getText().toString().length() > 5) && (etSecPw.getText().toString().length() < 17))
                {
                    if ( (etFstPw.getText().toString().equals(etSecPw.getText().toString())) )
                    {
                        ivSec.setImageResource(R.drawable.correct);
                    }
                    else
                    {
                        ivSec.setImageResource(R.drawable.wrong);
                    }
                }
                else
                {
                    ivSec.setImageResource(R.drawable.wrong);
                }
            }
        });

        //用户名注册这边还没有去做判断是否已经被注册，好像这边不能做到判断。只有提交资料后才范围有是否被注册过。
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNext();
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

    public void doNext()
    {
        if ((etUserName.getText().toString().length() > 4)) {

            rlProgress.setVisibility(View.VISIBLE);
            mNext.setClickable(false);

            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("login_id", etUserName.getText().toString());
            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("condition", jsonParamsString);

            new HttpTools(SignUpUsernameActivity.this).get(Constant.API_LOGINID_AVAILABILITY, params, this,new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    rlProgress.setVisibility(View.GONE);
                    mNext.setClickable(true);
                }

                @Override
                public void onResult(String response) {

                    String responseStatus;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        responseStatus = jsonObject.getString("response_status");
                        //手机注册合法性检查状态信息为Fail时
                        if (responseStatus.equals("Fail")) {
                            //fail：手机号注册过的时候，展示Text，Toast。
                            ivUserName.setImageResource(R.drawable.wrong);
                            blnLogin = false;
                            Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_account_already_exist), Toast.LENGTH_SHORT).show();
                        } else if (responseStatus.equals("Success")){
                            ivUserName.setImageResource(R.drawable.correct);
                            blnLogin = true;
                            goNextActivity();
                        }

                    } catch (JSONException e) {
                        ivUserName.setImageResource(R.drawable.wrong);
                        blnLogin = false;
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    ivUserName.setImageResource(R.drawable.wrong);
                    blnLogin = false;
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        } else {
            ivUserName.setImageResource(R.drawable.wrong);
            blnLogin = false;
            Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_invalid_account_name), Toast.LENGTH_SHORT).show();
        }
    }

    public void goNextActivity()
    {
        if (blnLogin && ( etFstPw.getText().toString().equals(etSecPw.getText().toString()) ) && (etFstPw.getText().toString().length() > 5) && (etFstPw.getText().toString().length() < 17)  && (etFstPw.getText().toString().length() != 0) && (etSecPw.getText().toString().length() != 0) && blnLogin && ((etUserName.getText().toString().length() >4)))
        {
            Intent intent = new Intent(SignUpUsernameActivity.this, SignUpUsernameVerifyPhoneActivity.class);

            userEntity.setUser_login_id(etUserName.getText().toString());//账号
            userEntity.setUser_password(MD5(etSecPw.getText().toString()));//密码
            userEntity.setUser_login_type("username");//注册类型username

            intent.putExtra("userEntity",userEntity);

            startActivity(intent);
        }

        else if (etUserName.getText().toString().length() == 0)
        {
            Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_input_username),Toast.LENGTH_LONG).show();
        }

        else if (etFstPw.getText().toString().length() == 0 || etSecPw.getText().toString().length() == 0)
        {
            Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_input_password),Toast.LENGTH_LONG).show();
        }

        else if (!etSecPw.getText().toString().equals(etFstPw.getText().toString()))
        {
            Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_input_re_enter_password),Toast.LENGTH_LONG).show();
        }

        else
        {
            Toast.makeText(SignUpUsernameActivity.this, getString(R.string.text_full_all_fields),Toast.LENGTH_LONG).show();
        }
    }
}
