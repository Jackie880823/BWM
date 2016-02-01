package com.madxstudio.co8.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.madxstudio.co8.R;

/**
 * Created by quankun on 16/1/25.
 */
public class WriteNewsActivity extends BaseActivity {
    private boolean banBack;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_write_new);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
//        changeTitleColor(R.color.tab_color_press2);
//        rightTextButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                Fragment fragment = getFragmentInstance();
                if(fragment instanceof WriteNewsFragment) {
                    banBack = ((WriteNewsFragment) fragment).backCheck();
                }
            }
            return banBack ? banBack : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void titleRightEvent() {
        if(commandlistener!=null)
            commandlistener.execute(rightButton);
//            commandlistener.execute(rightTextButton);

    }

    @Override
    protected Fragment getFragment() {
        return WriteNewsFragment.newInstance();
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
