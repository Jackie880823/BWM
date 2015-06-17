package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.EventEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MyDateUtils;

import java.util.HashMap;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class EventDetailActivity extends BaseActivity {
    private static final String Tag = EventDetailActivity.class.getSimpleName();

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        title_icon.setVisibility(View.VISIBLE);
        title_icon.setImageResource(R.drawable.arrow_down);
//        changeTitleColor(R.color.tab_color_press2);
//        if (isCurrentUser()) {
//            rightButton.setImageResource(R.drawable.btn_edit);
//            rightButton.setVisibility(View.VISIBLE);
//            if(MyDateUtils.isBeforeDate(MyDateUtils.formatTimestamp2Local(MyDateUtils.dateString2Timestamp(event.getGroup_event_date()).getTime()))){
//                rightButton.setImageResource(R.drawable.icon_edit_press);
//                rightButton.setEnabled(false);
//            }
//        }else{
//            rightButton.setVisibility(View.INVISIBLE);
//        }

    }

    private boolean banBack;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                Fragment fragment = getFragmentInstance();
                if(fragment instanceof EventDetailFragment) {
                    banBack = ((EventDetailFragment) fragment).backCheck();
                }
            }
            return banBack ? banBack : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    protected void titleMiddleEvent() {
        if (commandlistener != null) {
            commandlistener.execute(tvTitle);
        }

    }

    private EventEntity event;

    public EventEntity getEventEntity() {
        return event;
    }

    private boolean isCurrentUser() {

        if(event!=null) {
            if (MainActivity.getUser() == null)
                return false;
            return event.getGroup_owner_id().equals(MainActivity.getUser().getUser_id());
        }
        return false;
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_event_detail);
    }

    boolean changeMode;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN) {
            if(titleLeftClick != null){
                titleLeftClick.Click();
            }
            titleLeftEvent();
            return true;
        }
        return super.onKeyUp(keyCode,event);
    }
    @Override
    protected void titleLeftEvent() {
        if(commandlistener!=null)
            commandlistener.execute(leftButton);
    }

    @Override
    protected void titleRightEvent() {

        if (commandlistener != null)
            commandlistener.execute(rightButton);
//            commandlistener.execute(rightTextButton);

    }

    private String group_id;

    @Override
    protected Fragment getFragment() {
//        event = (EventEntity) getIntent().getSerializableExtra("event");
        group_id = getIntent().getStringExtra("group_id");
        return EventDetailFragment.newInstance(group_id);
    }

    @Override
    protected void onResume() {
//        if(commandlistener!=null&&commandlistener.execute(rightTextButton)){
//            rightTextButton.setVisibility(View.VISIBLE);
//            rightTextButton.setText(R.string.text_update);
//        }
        super.onResume();
    }

    @Override
    public void initView() {
    }

    @Override
    public void requestData() {
        if (TextUtils.isEmpty(group_id))
            return;

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("group_id", group_id);
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_DETAIL, params);
        new HttpTools(this).get(url, params,Tag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                getDataDone = true;
            }

            @Override
            public void onResult(String response) {
                event = new Gson().fromJson(response, EventEntity.class);
                if (isCurrentUser()) {
                    rightButton.setImageResource(R.drawable.btn_edit);
                    rightButton.setVisibility(View.VISIBLE);
                    if (MyDateUtils.isBeforeDate(MyDateUtils.formatTimestamp2Local(MyDateUtils.dateString2Timestamp(event.getGroup_event_date()).getTime()))) {
                        rightButton.setImageResource(R.drawable.icon_edit_press);
                        rightButton.setEnabled(false);
                    }
                    if("2".equals(event.getGroup_event_status())){
                        rightButton.setImageResource(R.drawable.icon_edit_press);
                        title_icon.setVisibility(View.GONE);
                        rightButton.setEnabled(false);
                    }
                } else {
                    rightButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public boolean getDataDone;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == 1 ){
            if(resultCode == 1){
                setResult(1);
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public TitleLeftClick titleLeftClick;
    public void setTitleLeftClick(TitleLeftClick titleLeftClick){
        this.titleLeftClick = titleLeftClick;
    }
    public interface TitleLeftClick{
        void Click();

    }
}
