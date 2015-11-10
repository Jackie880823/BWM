package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.FaceBookUserEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.facebook.login.LoginManager;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ThirdPartyVerifyPhoneActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = ThirdPartyVerifyPhoneActivity.class.getSimpleName();
    private final static String GET_CODE = TAG + "_GET_CODE";

    public final static String RESPONSE_MESSAGE_ID_EXIST = "Server.LoginIdExist";

    private static final int GET_COUNTRY_CODE = 0;

    private static final int ERROR = -1;
    private static final int SUCCESS_GET_CODE = 1;
    private static final int LOG_IN_EXIST = 2;
    private static final int CATCH = 3;

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
//    private TextView tvCountryCode;
    private TextView tvStartCountryCode;
    private EditText etPhoneNumber;

    private PaperButton brNext;
    private RelativeLayout rlProgress;

    private FaceBookUserEntity faceBookUserEntity;

    private String strCountryCode;
    private String strPhoneNumber;

    private boolean blnChooseCountryCode;//通过选择获得的国家区号。如果用户手动修改。把国家名称改回原始状态。这是用来判断的

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SUCCESS_GET_CODE:
                    //账号可用。
                    goVerification();
                    break;

                case LOG_IN_EXIST:
                    //账号不可用。
//                    LoginManager.getInstance().logOut();//清除Facebook授权缓存
                    finish();
                    break;

                case CATCH:
                    //验证码发送失
//                    LoginManager.getInstance().logOut();//清除Facebook授权缓存
                    finish();
                    break;

                case ERROR:
                    //请求错误
//                    LoginManager.getInstance().logOut();//清除Facebook授权缓存
                    finish();
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    public int getLayout() {
        return R.layout.activity_third_party_verify_phone;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        //TODO
        //需要什么标题呢
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.btn_gradient_color_green_normal);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        faceBookUserEntity = (FaceBookUserEntity) getIntent().getSerializableExtra(Constant.TYPE_FACEBOOK);

        rlCountryCode = getViewById(R.id.rl_country_code);
        tvCountry = getViewById(R.id.tv_country);
//        tvCountryCode = getViewById(R.id.tv_country_code);
        etPhoneNumber = getViewById(R.id.et_phone_number);
        tvStartCountryCode = getViewById(R.id.tv_start_country_code);

        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);

        rlCountryCode.setOnClickListener(this);
        brNext.setOnClickListener(this);

//        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(this));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(this));

        tvStartCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (blnChooseCountryCode) {

                } else {
                    tvCountry.setText(getText(R.string.title_country_code));
                }
                blnChooseCountryCode = false;
            }
        });

        tvStartCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(tvStartCountryCode.getText().toString())) {
                    rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                } else {
                    rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
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


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.rl_country_code:
                Intent intent = new Intent(this, CountryCodeActivity.class);
                startActivityForResult(intent, GET_COUNTRY_CODE);
                break;

            case R.id.br_next:
                doVerityCode();
                break;
        }
    }

    private void doVerityCode() {

        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strCountryCode = tvStartCountryCode.getText().toString().trim();
        strPhoneNumber = etPhoneNumber.getText().toString().trim();

        if (!MyTextUtil.isHasEmpty(strCountryCode, strPhoneNumber))
        {

            Map<String, String> params = new HashMap<>();
            params.put("user_login_id", faceBookUserEntity.getUserId());
            params.put("user_country_code", strCountryCode);
            params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));
            params.put("login_type", Constant.TYPE_FACEBOOK);

            new HttpTools(this).get(Constant.API_START_THIRD_PARTY_CHECK_ID, params, GET_CODE, new HttpCallback() {
                @Override
                public void onStart() {
                    doingHttp();
                }

                @Override
                public void onFinish() {
                    finishHttp();
                }

                @Override
                public void onResult(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (Constant.SUCCESS.equals(jsonObject.getString("response_status"))) {
                            //成功获得验证码
                            handler.sendEmptyMessage(SUCCESS_GET_CODE);
                        } else if (Constant.FAIL.equals(jsonObject.getString("response_status"))) {
                            if (RESPONSE_MESSAGE_ID_EXIST.equals(jsonObject.getString("response_message"))) {
                                //账号被注册
                                handler.sendEmptyMessage(LOG_IN_EXIST);
                            }
                        }

                    } catch (JSONException e) {
                        //验证码发送失败
                        handler.sendEmptyMessage(CATCH);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    handler.sendEmptyMessage(ERROR);
                    e.printStackTrace();
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
            if (TextUtils.isEmpty(strCountryCode))
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(strPhoneNumber))
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case GET_COUNTRY_CODE:
                if (resultCode == RESULT_OK)
                {
                    blnChooseCountryCode = true;
                    tvCountry.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
//                    tvCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                    tvStartCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                }
                break;

            default:
                break;
        }
    }


    private void goVerification() {
        Intent intent = new Intent(ThirdPartyVerifyPhoneActivity.this, VerificationActivity.class);
        intent.putExtra(Constant.TYPE, Constant.TYPE_FACEBOOK);
        intent.putExtra("faceBookUserEntity", faceBookUserEntity);
        intent.putExtra("user_country_code", strCountryCode);
        intent.putExtra("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        startActivity(intent);
    }

    private void doingHttp()
    {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
    }

    private void finishHttp()
    {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();//清除Facebook授权缓存
    }
}
