package com.bondwithme.BondWithMe.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BirthdayAlertActivity extends BaseActivity {

    ProgressDialog mProgressDialog;
    private CheckBox dob_alert_1;
    private CheckBox dob_alert_3;
    private CheckBox dob_alert_7;
    private CheckBox dob_alert_30;

    @Override
    public int getLayout() {
        return R.layout.activity_birthday_alert;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_birthday_alert_reminder);
    }

    @Override
    protected void titleRightEvent() {
        updateConfig();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
//        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setImageResource(R.drawable.btn_done);
//        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        mProgressDialog = new ProgressDialog(this,R.string.text_loading);
        mProgressDialog.show();

        dob_alert_1 = getViewById(R.id.dob_alert_1);
        dob_alert_3 = getViewById(R.id.dob_alert_3);
        dob_alert_7 = getViewById(R.id.dob_alert_7);
        dob_alert_30 = getViewById(R.id.dob_alert_30);

    }

    @Override
    public void requestData() {

        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG,MainActivity.getUser().getUser_id()),null,this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if(jsonObject!=null){
//                        JSONObject birthdayConfig = jsonObject.getJSONObject("birthday");
                        bindConfig2Birthday(jsonObject);

//                        JSONObject acceptConfig = jsonObject.getJSONObject("auto");
//                        bindConfig2Accept(jsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MessageUtil.showMessage(BirthdayAlertActivity.this,R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(BirthdayAlertActivity.this,R.string.msg_load_config_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void    bindConfig2Birthday(JSONObject birthdayConfig) throws JSONException {
        if("1".equals(birthdayConfig.get("dob_alert_1"))){
            dob_alert_1.setChecked(true);
        }else{
            dob_alert_1.setChecked(false);
        }
        if("1".equals(birthdayConfig.get("dob_alert_3"))){
            dob_alert_3.setChecked(true);
        }else{
            dob_alert_3.setChecked(false);
        }
        if("1".equals(birthdayConfig.get("dob_alert_7"))){
            dob_alert_7.setChecked(true);
        }else{
            dob_alert_7.setChecked(false);
        }
        if("1".equals(birthdayConfig.get("dob_alert_30"))){
            dob_alert_30.setChecked(true);
        }else{
            dob_alert_30.setChecked(false);
        }
    }

    private void updateConfig(){

        mProgressDialog.show();

        Map<String, String> params = new HashMap<>();

        params.put("dob_alert_1",(dob_alert_1.isCheck()?"1":"0"));
        params.put("dob_alert_3",(dob_alert_3.isCheck()?"1":"0"));
        params.put("dob_alert_7",(dob_alert_7.isCheck()?"1":"0"));
        params.put("dob_alert_30",(dob_alert_30.isCheck()?"1":"0"));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                MessageUtil.showMessage(BirthdayAlertActivity.this, R.string.msg_action_successed);
                BirthdayAlertActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(BirthdayAlertActivity.this,R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
    }
}
