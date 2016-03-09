package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.family.FamilyTreeActivity;

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
        linear_company_profile.setOnClickListener(this);
        linear_org_chart.setOnClickListener(this);
        linear_my_group.setOnClickListener(this);
        linear_all_staff.setOnClickListener(this);
        linear_org_others.setOnClickListener(this);
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
                break;
            case R.id.linear_org_chart:
                Intent intent = new Intent(mContext, FamilyTreeActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_my_group:
                Intent intent1 = new Intent(mContext, OrgListActivity.class);
                intent1.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_GROUP);
                startActivity(intent1);
                break;
            case R.id.linear_all_staff:
                Intent intent2 = new Intent(mContext, OrgListActivity.class);
                intent2.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_STAFF);
                startActivity(intent2);
                break;
            case R.id.linear_org_others:
                Intent intent3 = new Intent(mContext, OrgListActivity.class);
                intent3.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_OTHER);
                startActivity(intent3);
                break;
        }
    }
}
