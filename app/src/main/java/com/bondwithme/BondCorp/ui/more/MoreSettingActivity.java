package com.madxstudio.co8.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madxstudio.co8.App;
import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.util.AppInfoUtil;
import com.madxstudio.co8.widget.MyDialog;

public class MoreSettingActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;

    private TextView tvVersion;
    private MyDialog myDialog;

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
//        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
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
        tvVersion = getViewById(R.id.tv_version);

        tvVersion.setText(AppInfoUtil.getAppVersionName(MoreSettingActivity.this));

        getViewById(R.id.btn_sign_out).setOnClickListener(this);

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
            case R.id.btn_sign_out:
                if (myDialog == null) {
                    myDialog = new MyDialog(this, R.string.text_tips_title, R.string.msg_ask_exit_app);
                    myDialog.setCanceledOnTouchOutside(false);
                    myDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (myDialog != null) {
                                myDialog.dismiss();
                            }
                        }
                    });
                    myDialog.setButtonAccept(R.string.text_dialog_accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            App.logout(MoreSettingActivity.this);
                        }
                    });
                }
                if (!myDialog.isShowing())
                    myDialog.show();

                break;

            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDialog != null) {
            myDialog.dismiss();
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
