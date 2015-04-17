package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.madx.bwm.R;
import com.madx.bwm.entity.EventEntity;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class EventEditActivity extends BaseActivity {



    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
//        rightButton.setVisibility(View.GONE);
//        rightTextButton.setVisibility(View.VISIBLE);
//        rightTextButton.setText(R.string.text_save);
//        titleBar.invalidate();
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_event_edit);
    }

    @Override
    protected void titleLeftEvent() {
        if(commandlistener!=null)
            commandlistener.execute(leftButton);
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN) {
            titleLeftEvent();
            return true;
        }
        return super.onKeyUp(keyCode,event);
    }

    @Override
    protected void titleRightEvent() {
        if(commandlistener!=null)
            commandlistener.execute(rightButton);

    }

    public EventEntity eventEntity;
    @Override
    protected Fragment getFragment() {
        eventEntity = (EventEntity)getIntent().getSerializableExtra("event");
        return EventEditFragment.newInstance();
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
