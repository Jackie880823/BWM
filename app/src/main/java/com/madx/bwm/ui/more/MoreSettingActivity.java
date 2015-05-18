package com.madx.bwm.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.madx.bwm.R;
import com.madx.bwm.ui.BaseActivity;

public class MoreSettingActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;

    @Override
    public int getLayout() {
        return R.layout.activity_more_setting;
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
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        rl1 = getViewById(R.id.rl_1);
        rl2 = getViewById(R.id.rl_2);
        rl3 = getViewById(R.id.rl_3);

        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_1:
                startActivity(new Intent(MoreSettingActivity.this, AutoAcceptActivity.class));
                break;
            case R.id.rl_2:
                startActivity(new Intent(MoreSettingActivity.this, BirthdayAlertActivity.class));
                break;
            case R.id.rl_3:
                startActivity(new Intent(MoreSettingActivity.this, GroupPrivacyActivity.class));
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
