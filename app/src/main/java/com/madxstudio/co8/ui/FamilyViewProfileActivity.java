package com.madxstudio.co8.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madxstudio.co8.R;

public class FamilyViewProfileActivity extends BaseActivity {


    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_family_profile));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return new FamilyViewProfileFragment();
    }

    @Override
    public void initView() {

    }


    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
