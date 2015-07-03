package com.bondwithme.BondWithMe.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

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

public class AutoAcceptActivity extends BaseActivity implements CheckBox.OnCheckListener{

    ProgressDialog mProgressDialog;

    private int checkCount = 0;

    private CheckBox auto_acp_all;
    private CheckBox auto_acp_chd;
    private CheckBox auto_acp_oth;
    private CheckBox auto_acp_prt;
    private CheckBox auto_acp_sib;
    private CheckBox auto_acp_sps;

    @Override
    public int getLayout() {
        return R.layout.activity_auto_accept;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_auto_accept);
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

    @Override
    public void initView() {

        mProgressDialog = new ProgressDialog(this,getString(R.string.text_loading));
        mProgressDialog.show();

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
            public void onCheck(CheckBox checkBox,boolean check) {
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

    private void bindConfig2Accept(JSONObject acceptConfig) throws JSONException {

        if("1".equals(acceptConfig.get("auto_acp_all"))){
            auto_acp_all.setChecked(true);
            auto_acp_chd.setChecked(true);
            auto_acp_oth.setChecked(true);
            auto_acp_prt.setChecked(true);
            auto_acp_sps.setChecked(true);
            auto_acp_sib.setChecked(true);
            checkCount=5;
            return;
        }else{
            auto_acp_all.setChecked(false);
        }

        if("1".equals(acceptConfig.get("auto_acp_chd"))){
            auto_acp_chd.setChecked(true);
            checkCount++;
        }else{
            auto_acp_chd.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_sib"))){
            auto_acp_sib.setChecked(true);
            checkCount++;
        }else{
            auto_acp_sib.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_oth"))){
            auto_acp_oth.setChecked(true);
            checkCount++;
        }else{
            auto_acp_oth.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_prt"))){
            auto_acp_prt.setChecked(true);
            checkCount++;
        }else{
            auto_acp_prt.setChecked(false);
        }
        if("1".equals(acceptConfig.get("auto_acp_sps"))){
            auto_acp_sps.setChecked(true);
            checkCount++;
        }else{
            auto_acp_sps.setChecked(false);
        }
    }

    private void updateConfig(){

        mProgressDialog.show();

        Map<String, String> params = new HashMap<>();

        params.put("auto_acp_all",(auto_acp_all.isCheck()?"1":"0"));
        params.put("auto_acp_chd",(auto_acp_chd.isCheck()?"1":"0"));
        params.put("auto_acp_oth",(auto_acp_oth.isCheck()?"1":"0"));
        params.put("auto_acp_prt",(auto_acp_prt.isCheck()?"1":"0"));
        params.put("auto_acp_sib",(auto_acp_sib.isCheck()?"1":"0"));
        params.put("auto_acp_sps",(auto_acp_sps.isCheck()?"1":"0"));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo,this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                MessageUtil.showMessage(AutoAcceptActivity.this,R.string.msg_action_successed);
                AutoAcceptActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(AutoAcceptActivity.this,R.string.msg_action_failed);
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
        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id()),null,this,new HttpCallback() {
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
//                        bindConfig2Birthday(jsonObject);

//                        JSONObject acceptConfig = jsonObject.getJSONObject("auto");
                        bindConfig2Accept(jsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MessageUtil.showMessage(AutoAcceptActivity.this, R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(AutoAcceptActivity.this,R.string.msg_load_config_failed);
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
    public void onCheck(CheckBox checkBox,boolean check) {
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

        Log.d("", "ccccccccc---->" + checkCount);
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
