package com.bondwithme.BondCorp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.EventEntity;
import com.bondwithme.BondCorp.http.UrlUtil;
import com.bondwithme.BondCorp.util.MyDateUtils;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class EventDetailActivity extends BaseActivity {
    private static final String Tag = EventDetailActivity.class.getSimpleName();
    private String group_id;
    private String Content_group_id;
    public EventEntity mEvent;


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

    protected void titleMiddleEvent() {
        if (commandlistener != null) {
            commandlistener.execute(tvTitle);
        }

    }

    public EventEntity event;

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
        return super.onKeyUp(keyCode, event);
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

    @Override
    protected Fragment getFragment() {
//        event = (EventEntity) getIntent().getSerializableExtra("event");
        group_id = getIntent().getStringExtra("group_id");
        Content_group_id = getIntent().getStringExtra("Content_group_id");
        return EventDetailFragment.newInstance(group_id, Content_group_id);
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
                try {
                    event = new Gson().fromJson(response, EventEntity.class);
                    if (isCurrentUser()) {
                        rightButton.setImageResource(R.drawable.btn_edit);
                        rightButton.setVisibility(View.VISIBLE);
                        if (MyDateUtils.isBeforeDate(MyDateUtils.formatTimestamp2Local(MyDateUtils.dateString2Timestamp(event.getGroup_event_date())))) {
                            rightButton.setImageResource(R.drawable.edit_caption);
                            rightButton.setEnabled(false);
                        }
                        if("2".equals(event.getGroup_event_status())){
                            rightButton.setImageResource(R.drawable.edit_caption);
                            title_icon.setVisibility(View.GONE);
                            rightButton.setEnabled(false);
                        }
                    } else {
                        rightButton.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    finish();
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
//        Fragment fragment = getFragment();
//        fragment.onActivityResult(requestCode,resultCode,data);
       if(resultCode == Activity.RESULT_OK){
           switch (requestCode){
               case 3:
                   if(data != null){
                       mEvent = (EventEntity) data.getSerializableExtra("event");
                   }
                   setResult(Activity.RESULT_OK);
                   EventDetailFragment.newInstance();
                   break;
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
