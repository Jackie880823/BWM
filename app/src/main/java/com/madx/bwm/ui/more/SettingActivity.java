package com.madx.bwm.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.ui.BaseActivity;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class SettingActivity extends BaseActivity implements CheckBox.OnCheckListener {


//    private TextView mail_subject;
    ProgressDialog mProgressDialog;
    private CheckBox dob_alert_1;
    private CheckBox dob_alert_3;
    private CheckBox dob_alert_7;
    private CheckBox dob_alert_30;

    private CheckBox auto_acp_all;
    private CheckBox auto_acp_chd;
    private CheckBox auto_acp_oth;
    private CheckBox auto_acp_prt;
    private CheckBox auto_acp_sib;
    private CheckBox auto_acp_sps;

    public int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

        tvTitle.setText(R.string.text_setting);
    }
    @Override
    protected void initTitleBar() {
        super.initTitleBar();
//        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setImageResource(R.drawable.btn_done);
//        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {
        updateConfig();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

//    private boolean autoCheck;
    private int checkCount = 0;

    @Override
    public void initView() {
        mProgressDialog = new ProgressDialog(this,R.string.text_loading);
        mProgressDialog.show();

        dob_alert_1 = getViewById(R.id.dob_alert_1);
        dob_alert_3 = getViewById(R.id.dob_alert_3);
        dob_alert_7 = getViewById(R.id.dob_alert_7);
        dob_alert_30 = getViewById(R.id.dob_alert_30);

        auto_acp_all = getViewById(R.id.auto_acp_all);
        auto_acp_chd = getViewById(R.id.auto_acp_chd);
        auto_acp_oth = getViewById(R.id.auto_acp_oth);
        auto_acp_prt = getViewById(R.id.auto_acp_prt);
        auto_acp_sib = getViewById(R.id.auto_acp_sib);
        auto_acp_sps = getViewById(R.id.auto_acp_sps);

        auto_acp_chd.setOncheckListener(this);
        auto_acp_oth.setOncheckListener(this);
        auto_acp_prt.setOncheckListener(this);
        auto_acp_sib.setOncheckListener(this);
        auto_acp_sps.setOncheckListener(this);


        auto_acp_all.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean check) {
//                if (autoCheck) {
//                    autoCheck = false;
//                    return;
//                }
                if (auto_acp_all.isCheck()) {
                    auto_acp_chd.setChecked(true);
                    auto_acp_oth.setChecked(true);
                    auto_acp_prt.setChecked(true);
                    auto_acp_sib.setChecked(true);
                    auto_acp_sps.setChecked(true);
                    checkCount= 5;
                } else {
                    auto_acp_chd.setChecked(false);
                    auto_acp_oth.setChecked(false);
                    auto_acp_prt.setChecked(false);
                    auto_acp_sib.setChecked(false);
                    auto_acp_sps.setChecked(false);
                    checkCount= 0;
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateConfig(){

        mProgressDialog.show();

        Map<String, String> params = new HashMap<>();

//        Map<String, String> birthday = new HashMap<>();
        params.put("dob_alert_1",(dob_alert_1.isCheck()?"1":"0"));
        params.put("dob_alert_3",(dob_alert_3.isCheck()?"1":"0"));
        params.put("dob_alert_7",(dob_alert_7.isCheck()?"1":"0"));
        params.put("dob_alert_30",(dob_alert_30.isCheck()?"1":"0"));
//        params.put("birthday", UrlUtil.mapToJsonstring(birthday));
//
//        Map<String, String> auto = new HashMap<>();
        params.put("auto_acp_all",(auto_acp_all.isCheck()?"1":"0"));
        params.put("auto_acp_chd",(auto_acp_chd.isCheck()?"1":"0"));
        params.put("auto_acp_oth",(auto_acp_oth.isCheck()?"1":"0"));
        params.put("auto_acp_prt",(auto_acp_prt.isCheck()?"1":"0"));
        params.put("auto_acp_sib",(auto_acp_sib.isCheck()?"1":"0"));
        params.put("auto_acp_sps",(auto_acp_sps.isCheck()?"1":"0"));
//        params.put("auto", UrlUtil.mapToJsonstring(auto));


        RequestInfo requestInfo = new RequestInfo();
//        requestInfo.params = params;
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                MessageUtil.showMessage(SettingActivity.this,R.string.msg_action_successed);
                SettingActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(SettingActivity.this,R.string.msg_action_failed);
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
    public void requestData() {


        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG,MainActivity.getUser().getUser_id()),null,new HttpCallback() {
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
                        bindConfig2Accept(jsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MessageUtil.showMessage(SettingActivity.this,R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(SettingActivity.this,R.string.msg_load_config_failed);
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
    private void bindConfig2Accept(JSONObject acceptConfig) throws JSONException {

        if("1".equals(acceptConfig.get("auto_acp_all"))){
            auto_acp_all.setChecked(true);
            auto_acp_chd.setChecked(true);
            auto_acp_oth.setChecked(true);
            auto_acp_prt.setChecked(true);
            auto_acp_sps.setChecked(true);
            auto_acp_sib.setChecked(true);
            return;
        }else{
            auto_acp_all.setChecked(false);
        }

        if("1".equals(acceptConfig.get("auto_acp_chd"))){
            auto_acp_chd.setChecked(true);
        }else{
            auto_acp_chd.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_sib"))){
            auto_acp_sib.setChecked(true);
        }else{
            auto_acp_sib.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_oth"))){
            auto_acp_oth.setChecked(true);
        }else{
            auto_acp_oth.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_prt"))){
            auto_acp_prt.setChecked(true);
        }else{
            auto_acp_prt.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_sps"))){
            auto_acp_sps.setChecked(true);
        }else{
            auto_acp_sps.setChecked(false);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCheck(boolean check) {
        if(check){
            checkCount++;
        }else{
            checkCount--;
        }
        if(checkCount==5){
//            autoCheck = true;
            auto_acp_all.setChecked(true);
        }else{
//            autoCheck = true;
            auto_acp_all.setChecked(false);
        }
    }
}
