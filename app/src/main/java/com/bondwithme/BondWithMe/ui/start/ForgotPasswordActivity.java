package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener, EditText.OnEditorActionListener{

    private final static String TAG = ForgotPasswordActivity.class.getSimpleName();
    private final static String CHECK_GET_CODE = TAG + "_CHECK_GET_CODE";

    public final static String RM_PHONE_NUMBERNOT_FOUND = "PhoneNumberNotFound";
    public final static String RM_CREATE_VERIFICATION_FAIL = "Server.CreateVerificationFail";
    public final static String RM_CREATE_OUT_MESSAGE_FAIL = "Server.CreateOutMessageFail";

    private static final int GET_COUNTRY_CODE = 0;

    private static final int HANDLER_ERROR = -1;
    private static final int HANDRLER_SUCCESS_GET_CODE = 1;
    private static final int HANDRLER_RM_PHONE_NUMBERNOT_FOUND = 2;
    private static final int HANDRLER_RM_CREATE_VERIFICATION_FAIL = 3;
    private static final int HANDRLER_RM_CREATE_OUT_MESSAGE_FAIL = 4;
    private static final int HANDRLER_CATCH = 5;

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private TextView tvStartCountryCode;
    private EditText etPhoneNumber;
    private PaperButton brNext;
    private RelativeLayout rlProgress;

    private String strCountryCode;
    private String strPhoneNumber;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case HANDRLER_SUCCESS_GET_CODE:
                    goVerification();
                    break;

                case HANDRLER_RM_PHONE_NUMBERNOT_FOUND:
                    etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    break;

                case HANDRLER_RM_CREATE_VERIFICATION_FAIL:
                    break;

                case HANDRLER_RM_CREATE_OUT_MESSAGE_FAIL:
                    break;

                case HANDRLER_CATCH:
                    break;

                case HANDLER_ERROR:
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    public int getLayout() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_start_forgot_password);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_bg_color_green_normal);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        rlCountryCode = getViewById(R.id.rl_country_code);
        tvCountry = getViewById(R.id.tv_country);
        tvCountryCode = getViewById(R.id.tv_country_code);
        tvStartCountryCode = getViewById(R.id.tv_start_country_code);
        etPhoneNumber = getViewById(R.id.et_phone_number);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);

        etPhoneNumber.setOnEditorActionListener(this);

        rlCountryCode.setOnClickListener(this);
        brNext.setOnClickListener(this);

        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(this));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case GET_COUNTRY_CODE:
                if (resultCode == RESULT_OK)
                {
                    tvCountry.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
                    tvCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                    tvStartCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.rl_country_code:
                startActivityForResult(new Intent(this, CountryCodeActivity.class), GET_COUNTRY_CODE);
                break;

            case R.id.br_next:
                doGetVerification();
                break;

            default:
                break;
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            doGetVerification();
            return true;
        }
        return false;
    }

    private void doGetVerification()
    {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        strCountryCode = tvCountryCode.getText().toString();
        strPhoneNumber = etPhoneNumber.getText().toString().trim();

        if (checkAll())
        {
            doingHttpChangeUI();

            HashMap<String, String> params = new HashMap<>();
            params.put("user_country_code", strCountryCode);
            params.put("user_phone", MyTextUtil.NoZero(strPhoneNumber));

            new HttpTools(this).get(Constant.API_START_FORGOT_PASSWORD_GET_CODE, params, CHECK_GET_CODE, new HttpCallback() {
                @Override
                public void onStart() {

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
                            handler.sendEmptyMessage(HANDRLER_SUCCESS_GET_CODE);
                        }
                        else
                        {
                            if (Constant.FAIL.equals(jsonObject.getString("response_status")))
                            {
                                switch (jsonObject.getString("response_message"))
                                {
                                    case RM_PHONE_NUMBERNOT_FOUND:
                                        handler.sendEmptyMessage(HANDRLER_RM_PHONE_NUMBERNOT_FOUND);
                                        break;

                                    case RM_CREATE_VERIFICATION_FAIL:
                                        handler.sendEmptyMessage(HANDRLER_RM_CREATE_VERIFICATION_FAIL);
                                        break;

                                    case RM_CREATE_OUT_MESSAGE_FAIL:
                                        handler.sendEmptyMessage(HANDRLER_RM_CREATE_OUT_MESSAGE_FAIL);
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        handler.sendEmptyMessage(HANDRLER_CATCH);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    handler.sendEmptyMessage(HANDLER_ERROR);
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
            if (TextUtils.isEmpty(tvCountryCode.getText().toString()))
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                rlCountryCode.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(etPhoneNumber.getText().toString()))
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                etPhoneNumber.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }
        }
    }

    private void doingHttpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
        UIUtil.hideKeyboard(this, etPhoneNumber);
    }

    private void finishHttpChangeUI() {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
    }

    private boolean checkAll() {
        return ( !MyTextUtil.isHasEmpty(strCountryCode, strPhoneNumber) );
    }

    private void goVerification() {
        Intent intent = new Intent(ForgotPasswordActivity.this, VerificationActivity.class);
        intent.putExtra(Constant.TYPE, Constant.TYPE_FORGOT_PASSWORD);
        intent.putExtra("user_country_code", strCountryCode);
        intent.putExtra("user_phone", MyTextUtil.NoZero(strPhoneNumber));
        startActivity(intent);
    }

}
