package com.madxstudio.co8.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.madxstudio.co8.R;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class EventNewActivity extends BaseActivity {



    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
//        changeTitleColor(R.color.tab_color_press2);
//        rightTextButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_event_new);
    }

    private boolean banBack;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                Fragment fragment = getFragmentInstance();
                if(fragment instanceof EventNewFragment) {
                    banBack = ((EventNewFragment) fragment).backCheck();
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
    protected void titleLeftEvent() {
        if(commandlistener!=null)
            commandlistener.execute(leftButton);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    protected Fragment getFragment() {
        return EventNewFragment.newInstance();
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
