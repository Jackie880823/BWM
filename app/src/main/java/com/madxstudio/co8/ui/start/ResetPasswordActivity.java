package com.madxstudio.co8.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.util.MD5Util;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyTextUtil;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener, EditText.OnEditorActionListener {

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_UPDATA:
                    goSuccessful();
                    break;

                case FAIL_UPDATA:
                    MessageUtil.getInstance().showShortToast(getString(R.string.text_start_no_changes_found));
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
        switch (v.getId()) {
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
        tvTitle.setText(getResources().getString(R.string.text_reset_password));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_bg_color_login_normal);
        tvTitle.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.login_text_bg_color));
        leftButton.setImageResource(R.drawable.co8_back_button);
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
                if (s.length() > 4) {
                    etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.default_text_color_light));
                } else {
                    etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.stroke_color_red_wrong));
                }
            }
        });

        etFirstPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tvPasswordPrompt.setText(R.string.text_start_least5_prompt);
                        tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.default_text_color_light));
                        break;
                }
                return false;
            }
        });

        etSecondPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tvPasswordPrompt.setText(R.string.text_start_least5_prompt);
                        tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.default_text_color_light));
                        break;
                }
                return false;
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
                if (s.length() > 4 && (s.toString().equals(etFirstPassword.getText().toString()))) {
                    etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    tvPasswordPrompt.setText(R.string.text_start_least5_prompt);
                    tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.default_text_color_light));
                } else {
                    etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvPasswordPrompt.setText(R.string.text_pwd_type_wrong);
                    tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.stroke_color_red_wrong));
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    private void upDataPassword() {
        String strPassword = etFirstPassword.getText().toString();
        String confirmPassword = etSecondPassword.getText().toString();
        if (!MyTextUtil.isHasEmpty(strPassword) && strPassword.length() > 4 && strPassword.equals(confirmPassword)) {
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
                    Log.d("", "rrrrrr----" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                            handler.sendEmptyMessage(SUCCESS_UPDATA);
                        } else {
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
        } else {
            if (TextUtils.isEmpty(strPassword) || strPassword.length() < 5) {
                etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.stroke_color_red_wrong));
            } else {
                etFirstPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.default_text_color_light));
            }
            if (TextUtils.isEmpty(confirmPassword) || !confirmPassword.equals(strPassword)) {
                etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                tvPasswordPrompt.setText(R.string.text_pwd_type_wrong);
                tvPasswordPrompt.setTextColor(ContextCompat.getColor(ResetPasswordActivity.this, R.color.stroke_color_red_wrong));
            } else {
                etSecondPassword.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        }
    }

    private void doHttpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
    }

    private void finishHttpChangeUI() {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            upDataPassword();
            return true;
        }
        return false;
    }
}
