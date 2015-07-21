package com.bondwithme.BondWithMe.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.MessageUtil;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GroupPrivacyActivity extends BaseActivity {

    View mProgressDialog;
    private CheckBox cbTO;
    private CheckBox cbTM;
    private CheckBox cbGC;
    private LinearLayout llGroupPrivacy;


    @Override
    public int getLayout() {
        return R.layout.activity_group_privacy;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_group_privacy);
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

        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);

        llGroupPrivacy = getViewById(R.id.ll_group_privacy);

        cbTO = getViewById(R.id.cb_to_other);
        cbTM = getViewById(R.id.cb_to_me);
        cbGC = getViewById(R.id.cb_group_chat);


    }

    private void updateConfig(){

        mProgressDialog.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<>();

        params.put("recommend_me",(cbTO.isChecked()?"1":"0"));
        params.put("recommend_others",(cbTM.isChecked()?"1":"0"));
        params.put("group_add", (cbGC.isChecked() ? "1" : "0"));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo,this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(String string) {
                MessageUtil.showMessage(GroupPrivacyActivity.this,R.string.msg_action_successed);
                GroupPrivacyActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(GroupPrivacyActivity.this,R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    boolean result;
    @Override
    public void requestData() {
        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id()), null,this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
                if(result) {
                    llGroupPrivacy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject != null) {
//                        JSONObject birthdayConfig = jsonObject.getJSONObject("birthday");
//                        bindConfig2Birthday(jsonObject);

//                        JSONObject acceptConfig = jsonObject.getJSONObject("auto");
//                        bindConfig2Accept(jsonObject);

                        bindConfig2Group(jsonObject);
                        result = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MessageUtil.showMessage(GroupPrivacyActivity.this, R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(GroupPrivacyActivity.this, R.string.msg_load_config_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void    bindConfig2Group(JSONObject birthdayConfig) throws JSONException {
        if("1".equals(birthdayConfig.get("recommend_me"))){
            cbTO.setChecked(true);
        }else{
            cbTO.setChecked(false);
        }
        if("1".equals(birthdayConfig.get("recommend_others"))){
            cbTM.setChecked(true);
        }else{
            cbTM.setChecked(false);
        }
        if("1".equals(birthdayConfig.get("group_add"))){
            cbGC.setChecked(true);
        }else{
            cbGC.setChecked(false);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null)
        {
            mProgressDialog.setVisibility(View.INVISIBLE);
        }
    }
}
