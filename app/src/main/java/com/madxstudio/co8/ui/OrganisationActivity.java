package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.GsonBuilder;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.company.CompanyActivity;
import com.madxstudio.co8.ui.family.FamilyTreeActivity;
import com.madxstudio.co8.ui.start.CreateNewOrgActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by quankun on 16/3/8.
 */
public class OrganisationActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout linear_company_profile;
    private RelativeLayout linear_org_chart;
    private RelativeLayout linear_my_group;
    private RelativeLayout linear_all_staff;
    private RelativeLayout linear_org_others;
    private Context mContext;
    private View rl_org_join;
    private TextView tv_join;
    private Button br_join_now;
    private Button br_create_new;
    private View ll_org_list;
    private UserEntity userEntity;
    private String Tag = OrganisationActivity.class.getName();
    private View vProgress;
    private static final int CANCEL_JOIN_ORG_SUCCESS = 0X10;
    private static final int RESEND_JOIN_ORG_SUCCESS = 0X11;

    @Override
    public int getLayout() {
        return R.layout.activity_organisation;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_rewards);
    }

    @Override
    protected void titleRightEvent() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        mContext = this;
        linear_company_profile = getViewById(R.id.linear_company_profile);
        linear_org_chart = getViewById(R.id.linear_org_chart);
        linear_my_group = getViewById(R.id.linear_my_group);
        linear_all_staff = getViewById(R.id.linear_all_staff);
        linear_org_others = getViewById(R.id.linear_org_others);
        rl_org_join = getViewById(R.id.rl_org_join);
        tv_join = getViewById(R.id.tv_join);
        br_join_now = getViewById(R.id.br_join_now);
        br_create_new = getViewById(R.id.br_create_new);
        ll_org_list = getViewById(R.id.ll_org_list);
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.GONE);

        linear_company_profile.setOnClickListener(this);
        linear_org_chart.setOnClickListener(this);
        linear_my_group.setOnClickListener(this);
        linear_all_staff.setOnClickListener(this);
        linear_org_others.setOnClickListener(this);
        br_join_now.setOnClickListener(this);
        br_create_new.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userEntity = App.getLoginedUser();
        if ("0".equals(userEntity.getDemo()) && "0".equals(userEntity.getPending_org())) {
            rl_org_join.setVisibility(View.GONE);
            ll_org_list.setVisibility(View.VISIBLE);
        } else if ("1".equals(userEntity.getDemo()) && "1".equals(userEntity.getPending_org())) {
            rl_org_join.setVisibility(View.VISIBLE);
            ll_org_list.setVisibility(View.GONE);
            br_join_now.setText(getString(R.string.text_org_cancel_request));
            br_create_new.setText(getString(R.string.text_org_resend_request));
            String string = String.format(getString(R.string.text_org_have_join), userEntity.getOrganisation());
            tv_join.setText(Html.fromHtml(string));
        } else {
            rl_org_join.setVisibility(View.VISIBLE);
            ll_org_list.setVisibility(View.GONE);
            br_join_now.setText(getString(R.string.text_org_join_now));
            br_create_new.setText(getString(R.string.text_org_create_new));
            tv_join.setText(getString(R.string.text_org_no_join));
        }
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
        switch (v.getId()) {
            case R.id.linear_company_profile:
                Intent companyIntent = new Intent(mContext, CompanyActivity.class);
                startActivity(companyIntent);
                break;
            case R.id.linear_org_chart:
                Intent intent = new Intent(mContext, FamilyTreeActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_my_group:
                Intent intent1 = new Intent(mContext, OrgDetailActivity.class);
                intent1.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_GROUP);
                startActivity(intent1);
                break;
            case R.id.linear_all_staff:
                Intent intent2 = new Intent(mContext, OrgDetailActivity.class);
                intent2.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_STAFF);
                startActivity(intent2);
                break;
            case R.id.linear_org_others:
                Intent intent3 = new Intent(mContext, OrgDetailActivity.class);
                intent3.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_OTHER);
                startActivity(intent3);
                break;
            case R.id.br_join_now:
                if ("1".equals(userEntity.getDemo()) && "1".equals(userEntity.getPending_org())) {
                    cancelJoinOrg();
                } else {
                    Intent intent4 = new Intent(mContext, OrgSearchActivity.class);
                    intent4.putExtra(Constant.PARAM_USER_ID, userEntity.getUser_id());
                    intent4.putExtra(Constant.IS_FROM_ORG, true);
                    startActivityForResult(intent4, Constant.SEARCH_ORG_DATA);
                }
                break;
            case R.id.br_create_new:
                if ("1".equals(userEntity.getDemo()) && "1".equals(userEntity.getPending_org())) {
                    resendJoinOrg();
                } else {
                    Intent intent4 = new Intent(mContext, CreateNewOrgActivity.class);
                    intent4.putExtra(Constant.PARAM_USER_ID, userEntity.getUser_id());
                    startActivityForResult(intent4, Constant.CREATE_NEW_ORG);
                }
                break;
        }
    }

    private void cancelJoinOrg() {
        RequestInfo info = new RequestInfo();
        info.url = String.format(Constant.API_ORG_CANCEL_JOIN, userEntity.getUser_id());
        new HttpTools(this).put(info, Tag, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    UserEntity userEntity = new GsonBuilder().create().fromJson(jsonObject.optString(Constant.LOGIN_USER), UserEntity.class);
                    if (userEntity != null) {
                        App.changeLoginedUser(userEntity);
                    }
                    handler.sendEmptyMessage(CANCEL_JOIN_ORG_SUCCESS);
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

    private void resendJoinOrg() {
        RequestInfo info = new RequestInfo();
        info.url = String.format(Constant.API_ORG_RESEND_JOIN, userEntity.getUser_id());
        new HttpTools(this).put(info, Tag, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    UserEntity userEntity = new GsonBuilder().create().fromJson(jsonObject.optString(Constant.LOGIN_USER), UserEntity.class);
                    if (userEntity != null) {
                        App.changeLoginedUser(userEntity);
                    }
                    handler.sendEmptyMessage(RESEND_JOIN_ORG_SUCCESS);
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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RESEND_JOIN_ORG_SUCCESS:
                    break;
                case CANCEL_JOIN_ORG_SUCCESS:
                    rl_org_join.setVisibility(View.VISIBLE);
                    ll_org_list.setVisibility(View.GONE);
                    br_join_now.setText(getString(R.string.text_org_join_now));
                    br_create_new.setText(getString(R.string.text_org_create_new));
                    tv_join.setText(getString(R.string.text_org_no_join));
                    userEntity = App.getLoginedUser();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constant.CREATE_NEW_ORG:
//                    userEntity.setDemo("0");
//                    userEntity.setPending_org("0");
//                    OrgSearchEntity searchEntity = (OrgSearchEntity) data.getSerializableExtra(Constant.CREATE_COUNTRY_NAME);
//                    if (searchEntity != null) {
//
//                    }
                    break;
                case Constant.SEARCH_ORG_DATA:
//                    if (data != null) {
//                        OrgSearchEntity searchEntity1 = (OrgSearchEntity) data.getSerializableExtra(Constant.CREATE_COUNTRY_NAME);
//                        if (searchEntity1 != null) {
//                            userEntity.setOrganisation(searchEntity1.getName());
//                        }
//                        userEntity.setDemo("1");
//                        userEntity.setPending_org("1");
//                    }

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
