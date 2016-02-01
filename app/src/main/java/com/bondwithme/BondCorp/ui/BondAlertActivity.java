package com.madxstudio.co8.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.madxstudio.co8.R;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class BondAlertActivity extends BaseActivity {



    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

        tvTitle.setText(R.string.title_bond_alert);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
    }




    @Override
    protected void titleLeftEvent() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return new BondAlertFragment();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
        return super.dispatchKeyEvent(event);
    }
}
