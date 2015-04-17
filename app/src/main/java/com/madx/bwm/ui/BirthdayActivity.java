package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.entity.BirthdayEntity;

import java.util.List;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class BirthdayActivity extends BaseActivity {



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
        tvTitle.setText(R.string.title_birthday_alert);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        if(getIntent().getSerializableExtra("birthday_events")!=null) {
            return BirthdayFragment.newInstance(((List<BirthdayEntity>)getIntent().getSerializableExtra("birthday_events")));
        }
        return null;
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
