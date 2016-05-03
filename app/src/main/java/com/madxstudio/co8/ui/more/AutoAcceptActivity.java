package com.madxstudio.co8.ui.more;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AutoAcceptActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener {

    View mProgressDialog;
    LinearLayout llAutoAccept;

    private int checkCount = 0;

    private CheckBox auto_acp_all;
    private CheckBox auto_acp_supervisor;
    private CheckBox auto_acp_colleague;
    private CheckBox auto_acp_supplier;
    private CheckBox auto_acp_subordinate;
    private CheckBox auto_acp_customer;

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
        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);

        llAutoAccept = getViewById(R.id.ll_autoaccept);

        auto_acp_all = getViewById(R.id.auto_acp_all);
        auto_acp_supervisor = getViewById(R.id.auto_acp_chd);
        auto_acp_colleague = getViewById(R.id.auto_acp_oth);
        auto_acp_supplier = getViewById(R.id.auto_acp_prt);
        auto_acp_subordinate = getViewById(R.id.auto_acp_sib);
        auto_acp_customer = getViewById(R.id.auto_acp_sps);

        auto_acp_supervisor.setOnCheckedChangeListener(this);
        auto_acp_colleague.setOnCheckedChangeListener(this);
        auto_acp_supplier.setOnCheckedChangeListener(this);
        auto_acp_subordinate.setOnCheckedChangeListener(this);
        auto_acp_customer.setOnCheckedChangeListener(this);


        auto_acp_all.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (auto_acp_all.isChecked()) {
                    auto_acp_supervisor.setChecked(true);
                    auto_acp_colleague.setChecked(true);
                    auto_acp_supplier.setChecked(true);
                    auto_acp_subordinate.setChecked(true);
                    auto_acp_customer.setChecked(true);
                    checkCount = 5;
                } else {
                    auto_acp_supervisor.setChecked(false);
                    auto_acp_colleague.setChecked(false);
                    auto_acp_supplier.setChecked(false);
                    auto_acp_subordinate.setChecked(false);
                    auto_acp_customer.setChecked(false);
                    checkCount = 0;
                }
            }
        });

    }

    private void bindConfig2Accept(JSONObject acceptConfig) throws JSONException {
        if ("1".equals(acceptConfig.optString("auto_acp_all"))) {
            auto_acp_all.setChecked(true);
            auto_acp_supervisor.setChecked(true);
            auto_acp_colleague.setChecked(true);
            auto_acp_supplier.setChecked(true);
            auto_acp_customer.setChecked(true);
            auto_acp_subordinate.setChecked(true);
            checkCount = 5;
            return;
        } else {
            auto_acp_all.setChecked(false);
        }

        if ("1".equals(acceptConfig.optString("auto_acp_supervisor"))) {
            auto_acp_supervisor.setChecked(true);
            checkCount++;
        } else {
            auto_acp_supervisor.setChecked(false);
        }
        if ("1".equals(acceptConfig.optString("auto_acp_subordinate"))) {
            auto_acp_subordinate.setChecked(true);
            checkCount++;
        } else {
            auto_acp_subordinate.setChecked(false);
        }
        if ("1".equals(acceptConfig.optString("auto_acp_colleague"))) {
            auto_acp_colleague.setChecked(true);
            checkCount++;
        } else {
            auto_acp_colleague.setChecked(false);
        }
        if ("1".equals(acceptConfig.optString("auto_acp_supplier"))) {
            auto_acp_supplier.setChecked(true);
            checkCount++;
        } else {
            auto_acp_supplier.setChecked(false);
        }
        if ("1".equals(acceptConfig.optString("auto_acp_customer"))) {
            auto_acp_customer.setChecked(true);
            checkCount++;
        } else {
            auto_acp_customer.setChecked(false);
        }
    }

    private void updateConfig() {

        mProgressDialog.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<>();

        params.put("auto_acp_all", (auto_acp_all.isChecked() ? "1" : "0"));
        params.put("auto_acp_supervisor", (auto_acp_supervisor.isChecked() ? "1" : "0"));
        params.put("auto_acp_colleague", (auto_acp_colleague.isChecked() ? "1" : "0"));
        params.put("auto_acp_supplier", (auto_acp_supplier.isChecked() ? "1" : "0"));
        params.put("auto_acp_subordinate", (auto_acp_subordinate.isChecked() ? "1" : "0"));
        params.put("auto_acp_customer", (auto_acp_customer.isChecked() ? "1" : "0"));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(String string) {
//                MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
                AutoAcceptActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
//                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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
        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id()), null, this, new HttpCallback() {
            @Override
            public void onStart() {
                result = false;
            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
                if (result) {
                    llAutoAccept.setVisibility(View.VISIBLE);
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
                        bindConfig2Accept(jsonObject);
                        result = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                MessageUtil.getInstance().showShortToast(R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
//                MessageUtil.getInstance().showShortToast(R.string.msg_load_config_failed);
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
        if (mProgressDialog != null) {
            mProgressDialog.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isCheckeded) {
        if (isCheckeded) {
            checkCount++;
        } else {
            checkCount--;
        }
        if (checkCount == 5) {
//            autoCheck = true;
            auto_acp_all.setChecked(true);
        } else {
//            autoCheck = true;
            auto_acp_all.setChecked(false);
        }

        Log.d("", "ccccccccc---->" + checkCount);
    }
}
