package com.madx.bwm.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;

import java.security.MessageDigest;

public class SignUpPhonePasswordActivity extends BaseActivity {

    EditText etFstPw;
    EditText etSecPw;

    boolean blnPasswordNum = false;//第一次输入的密码长度是否符合

    ImageView ivFst;
    ImageView ivSec;

    Button btnNext;//跳转到下个界面

    UserEntity userEntity = new UserEntity();

    @Override
    public int getLayout() {
        return R.layout.activity_sign_up_phone_password;
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
        tvTitle.setText(getString(R.string.title_password));
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

        userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");

        etFstPw = getViewById(R.id.et_first_pw);
        etSecPw = getViewById(R.id.et_second_pw);

        ivFst = getViewById(R.id.iv_fst);
        ivSec = getViewById(R.id.iv_sec);

        btnNext = getViewById(R.id.btn_next);

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

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((etFstPw.getText().toString().length() > 5) && (etFstPw.getText().toString().length() < 17) && blnPasswordNum && (etFstPw.getText().toString().equals(etSecPw.getText().toString())))
                {
                    userEntity.setUser_password(MD5(etSecPw.getText().toString()));//密码

                    Intent intent = new Intent(SignUpPhonePasswordActivity.this, InformationPhoneActivity.class);

                    intent.putExtra("userEntity", userEntity);

                    startActivity(intent);

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
}
