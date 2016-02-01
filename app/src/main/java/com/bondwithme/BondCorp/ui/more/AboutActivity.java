package com.madxstudio.co8.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.AboutUsActivity;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.ContactUsActivity;
import com.madxstudio.co8.ui.TermsActivity;

/**
 * Created by heweidong on 15/8/26.
 */
public class AboutActivity extends BaseActivity {

    public int getLayout() {
        return R.layout.activity_about;
    }
    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_about_us);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        getViewById(R.id.rl_1).setOnClickListener(this);
        getViewById(R.id.rl_2).setOnClickListener(this);
        getViewById(R.id.rl_3).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_1:
                goAboutUs();
                break;
            case R.id.rl_2:
                contactUs();
                break;
            case R.id.rl_3:
                showTerms();
                break;
        }
    }

    private void goAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void showTerms() {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    private void contactUs() {
        Intent intent = new Intent(this, ContactUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
