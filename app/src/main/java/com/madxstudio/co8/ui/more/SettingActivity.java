package com.madxstudio.co8.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class SettingActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener {


//    private TextView mail_subject;
//    ProgressDialog mProgressDialog;
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
//        mProgressDialog = new ProgressDialog(this,getString(R.string.text_loading));
//        mProgressDialog.show();

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

        auto_acp_chd.setOnCheckedChangeListener(this);
        auto_acp_oth.setOnCheckedChangeListener(this);
        auto_acp_prt.setOnCheckedChangeListener(this);
        auto_acp_sib.setOnCheckedChangeListener(this);
        auto_acp_sps.setOnCheckedChangeListener(this);


        auto_acp_all.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (auto_acp_all.isChecked()) {
                    auto_acp_chd.setChecked(true);
                    auto_acp_oth.setChecked(true);
                    auto_acp_prt.setChecked(true);
                    auto_acp_sib.setChecked(true);
                    auto_acp_sps.setChecked(true);
                    checkCount = 5;
                } else {
                    auto_acp_chd.setChecked(false);
                    auto_acp_oth.setChecked(false);
                    auto_acp_prt.setChecked(false);
                    auto_acp_sib.setChecked(false);
                    auto_acp_sps.setChecked(false);
                    checkCount = 0;
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateConfig(){

//        mProgressDialog.show();

        Map<String, String> params = new HashMap<>();

//        Map<String, String> birthday = new HashMap<>();
        params.put("dob_alert_1",(dob_alert_1.isChecked()?"1":"0"));
        params.put("dob_alert_3",(dob_alert_3.isChecked()?"1":"0"));
        params.put("dob_alert_7",(dob_alert_7.isChecked()?"1":"0"));
        params.put("dob_alert_30",(dob_alert_30.isChecked()?"1":"0"));
//        params.put("birthday", UrlUtil.mapToJsonstring(birthday));
//
//        Map<String, String> auto = new HashMap<>();
        params.put("auto_acp_all",(auto_acp_all.isChecked()?"1":"0"));
        params.put("auto_acp_chd",(auto_acp_chd.isChecked()?"1":"0"));
        params.put("auto_acp_oth",(auto_acp_oth.isChecked()?"1":"0"));
        params.put("auto_acp_prt",(auto_acp_prt.isChecked()?"1":"0"));
        params.put("auto_acp_sib",(auto_acp_sib.isChecked()?"1":"0"));
        params.put("auto_acp_sps",(auto_acp_sps.isChecked()?"1":"0"));
//        params.put("auto", UrlUtil.mapToJsonstring(auto));


        RequestInfo requestInfo = new RequestInfo();
//        requestInfo.params = params;
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo,this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
//                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
                SettingActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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


        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG,MainActivity.getUser().getUser_id()),null,this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
//                mProgressDialog.dismiss();
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
                MessageUtil.getInstance().showShortToast(R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_load_config_failed);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isCheckeded) {
        if(isCheckeded){
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
