package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener, EditText.OnEditorActionListener{

    private final static int FAIL_UPDATA = 0;
    private final static int SUCCESS_UPDATA = 1;

    private EditText etFirstPassword;
    private EditText etSecondPassword;
    private TextView tvPasswordPrompt;
    private PaperButton brNext;
    private RelativeLayout rlProgress;

    private UserEntity userEntity;
    private String strCountryCode;
    private String strPhoneNumber;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SUCCESS_UPDATA:
                    goSuccessful();
                    break;

                case FAIL_UPDATA:
                    break;
            }
        }
    };

    private void goSuccessful() {
        userEntity.setUser_password(MD5Util.string2MD5(etSecondPassword.getText().toString()));
        Intent intent = new Intent(this, ResetPasswordSuccessfulActivity.class);
        userEntity.setUser_country_code(strCountryCode);
        userEntity.setUser_phone(strPhoneNumber);
        intent.putExtra(Constant.LOGIN_USER, userEntity);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.br_next:
                upDataPassword();
                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_reset_password2;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_gradient_color_green_normal);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        userEntity = (UserEntity) getIntent().getExtras().getSerializable(Constant.LOGIN_USER);
        strCountryCode = getIntent().getStringExtra("user_country_code");
        strPhoneNumber = getIntent().getStringExtra("user_phone");

        etFirstPassword = getViewById(R.id.et_first_pw);
        etSecondPassword = getViewById(R.id.et_second_pw);
        tvPasswordPrompt = getViewById(R.id.tv_password_prompt);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);

        brNext.setOnClickListener(this);
        etSecondPassword.setOnEditorActionListener(this);

        etFirstPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etFirstPassword.getText().toString().length() > 0 && etFirstPassword.getText().toString().length() < 5)
                {
                    etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                }
                else
                {
                    etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    if (etSecondPassword.getText().toString().equals(etFirstPassword.getText().toString()))
                    {
                        etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    }
                    else
                    {
                        etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    }
                }
            }
        });

        etSecondPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etSecondPassword.getText().toString().equals(etFirstPassword.getText().toString()))
                {
                    if (etSecondPassword.getText().toString().length() > 4)
                    {
                        etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    }
                    else
                    {
                        etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    }
                }
                else
                {
                    etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    private void upDataPassword()
    {
        if( (etFirstPassword.getText().toString().length() > 4) && (etSecondPassword.getText().toString().equals(etFirstPassword.getText().toString())) )
        {
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("user_password", MD5Util.string2MD5(etSecondPassword.getText().toString()));
            final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

            RequestInfo requestInfo = new RequestInfo();
            requestInfo.url = Constant.API_UPDATE_PASSWORD + userEntity.getUser_id();
            requestInfo.jsonParam = jsonParamsString;


            new HttpTools(ResetPasswordActivity.this).put(requestInfo, this, new HttpCallback() {
                @Override
                public void onStart() {
                    doHttpChangeUI();
                }

                @Override
                public void onFinish() {
                    finishHttpChangeUI();
                }

                @Override
                public void onResult(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (Constant.SUCCESS.equals(jsonObject.getString("response_status")))
                        {
                            handler.sendEmptyMessage(SUCCESS_UPDATA);
                        }
                        else
                        {
                            handler.sendEmptyMessage(FAIL_UPDATA);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
        else
        {
            if (TextUtils.isEmpty(etFirstPassword.getText().toString()))
            {
                etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }

            if (TextUtils.isEmpty(etSecondPassword.getText().toString()))
            {
                etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
        }
    }

    private void doHttpChangeUI()
    {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
    }

    private void finishHttpChangeUI()
    {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            upDataPassword();
            return true;
        }
        return false;
    }
}
