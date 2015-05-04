package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MembersGridAdapter;
import com.madx.bwm.entity.EventEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.util.SharedPreferencesUtils;
import com.madx.bwm.widget.DatePicker;
import com.madx.bwm.widget.MyDialog;
import com.madx.bwm.widget.TimePicker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.EventNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.EventNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventNewFragment extends BaseFragment<EventNewActivity> implements View.OnClickListener {

    private MembersGridAdapter adapter;
    private MyDialog saveAlertDialog;
    private EventEntity mEevent = new EventEntity();
    private GridView gvFriends;
    private TextView event_title;
    private TextView event_desc;
    private CardView item_date;
    private TextView date_desc;
    private EditText position_name;
    private ImageButton position_choose;

    private double latitude = -1000;
    private double longitude = -1000;
    Gson gson = new Gson();

    private String title;
    private String content;
    private String location;
    private Long date;

    public List<UserEntity> members_data = new ArrayList();
    private final static String USER_HEAD = "user_head";
    private final static String USER_NAME = "user_name";

    private ProgressBarCircularIndeterminate progressBar;
    Calendar mCalendar;
    Calendar calendar;

    String members;
    String Spmemeber_date;

    public static EventNewFragment newInstance(String... params) {

        return createInstance(new EventNewFragment());
    }

    public EventNewFragment() {
        super();
        // Required empty public constructor
    }

//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event, container, false);
//    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_new;
    }

    @Override
    public void initView() {
        progressBar = getViewById(R.id.progressBar);

        gvFriends = getViewById(R.id.gv_all_friends);
        changeData();


        event_title = getViewById(R.id.event_title);
        event_desc = getViewById(R.id.event_desc);
        position_choose = getViewById(R.id.position_choose);
        item_date = getViewById(R.id.item_date);

        date_desc = getViewById(R.id.date_desc);
        position_name = getViewById(R.id.position_name);
        position_name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    return true;
                }
                return false;
            }
        });
        //invited members点击事件
        getViewById(R.id.rl_add_members).setOnClickListener(this);
        position_choose.setOnClickListener(this);
        item_date.setOnClickListener(this);


        //点击事件
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                if (v.getId() == getParentActivity().leftButton.getId()) {
                    showSaveAlert();
//                    getParentActivity().finish();
                } else if (v.getId() == getParentActivity().rightButton.getId()) {
                    reomveSP();
                    submit();
//                    changeData();
                }
                return false;
            }
        });
        bindData2View();

    }
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            //相当于Fragment的onResume
//            cleanText();
//        } else {
//            //相当于Fragment的onPause
//        }
//    }

    private void bindData2View() {
//        Log.i("bindData2View====================================="," ");
        title = SharedPreferencesUtils.getParam(getParentActivity(), "title", "").toString();
        content = SharedPreferencesUtils.getParam(getParentActivity(),"content","").toString();
        location = SharedPreferencesUtils.getParam(getParentActivity(),"location","").toString();
        date = (Long)SharedPreferencesUtils.getParam(getParentActivity().getApplicationContext(),"date",0L);
        Spmemeber_date = SharedPreferencesUtils.getParam(getParentActivity(),"members_data","").toString();

        setText();
        latitude = TextUtils.isEmpty(mEevent.getLoc_latitude()) ? -1000 : Double.valueOf(mEevent.getLoc_latitude());
        longitude = TextUtils.isEmpty(mEevent.getLoc_longitude()) ? -1000 : Double.valueOf(mEevent.getLoc_longitude());
    }

    private void submit() {

        if(progressBar.getVisibility()==View.VISIBLE){
            return;
        }
        if (validateForm()) {
            setText();
            progressBar.setVisibility(View.VISIBLE);
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.url = Constant.API_EVENT_CREATE;
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("content_type", "post");
            params.put("first_post", "1");
            params.put("group_event_date", mEevent.getGroup_event_date());
            params.put("group_event_status", "1");
            params.put("group_name", event_title.getText().toString());
            params.put("group_owner_id", MainActivity.getUser().getUser_id());
            params.put("group_type", "1");
            if(latitude==-1000||longitude==-1000) {
                params.put("loc_latitude", "");
                params.put("loc_longitude", "");
            }else{
                params.put("loc_latitude", latitude + "");
                params.put("loc_longitude", longitude + "");
            }
            params.put("loc_name", position_name.getText().toString());
            params.put("text_description", event_desc.getText().toString());
            params.put("user_id", MainActivity.getUser().getUser_id());
            params.put("event_member", gson.toJson(setGetMembersIds(members_data)));
            requestInfo.params = params;

            new HttpTools(getActivity()).post(requestInfo,new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String response) {
                    getParentActivity().setResult(Activity.RESULT_OK);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    getParentActivity().finish();
                }

                @Override
                public void onError(Exception e) {
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });

        }
    }

    private void setText(){
        if(!TextUtils.isEmpty(title)){
            event_title.setText(title);

        }
        if(!TextUtils.isEmpty(content)){
            event_desc.setText(content);

        }
        if(!TextUtils.isEmpty(location)){
            position_name.setText(location);

        }
        if(date!=null && date!=0L){
            date_desc.setText(MyDateUtils.getLocalDateStringFromLocal(getParentActivity(),date));
            mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(date));

        }
        if(!TextUtils.isEmpty(Spmemeber_date)){
//            members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {}.getType());
            members_data = gson.fromJson(Spmemeber_date, new TypeToken<ArrayList<UserEntity>>() {}.getType());
//             = Spmemeber_date;
            changeData();
        }

    }
    private void cleanText(){
        event_title.setText("");
        event_desc.setText("");
        position_name.setText("");
        date_desc.setText("");
    }

    private void reomveSP(){
//        File file = new File("/data/data/"+getParentActivity().getPackageName().toString()+"/shared_prefs","EventNew_date.xml");
//        if (file.exists()){
//            file.delete();
////            Toast.makeText(getParentActivity(), "删除成功", Toast.LENGTH_LONG).show();
//        }
        SharedPreferencesUtils.removeParam(getParentActivity(),"title","");
        SharedPreferencesUtils.removeParam(getParentActivity(),"content","");
        SharedPreferencesUtils.removeParam(getParentActivity(),"location","");
        SharedPreferencesUtils.removeParam(getParentActivity(),"date",0L);
        SharedPreferencesUtils.removeParam(getParentActivity(),"members_data","");

    }

    @Override
    public void requestData() {

    }

    private final static int GET_LOCATION = 1;
    private final static int GET_MEMBERS = 2;
    private MyDialog pickDateTimeDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_members:
                goChooseMembers();
                break;
            case R.id.position_choose:
                goLocationSetting();
                break;
            case R.id.item_date:
                showDateTimePicker();
                break;
        }
    }

    private void showSaveAlert() {
        if (saveAlertDialog == null) {
            saveAlertDialog = new MyDialog(getActivity(), getString(R.string.text_tips_title), getString(R.string.msg_ask_save));
            saveAlertDialog.setButtonAccept(getString(R.string.accept), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAlertDialog.dismiss();
//                    Long tiem = mCalendar.getTimeInMillis();
                    if(!TextUtils.isEmpty(event_title.getText().toString().trim())){
                        SharedPreferencesUtils.setParam(getParentActivity(), "title", event_title.getText().toString());
//                        Log.i("title=============",event_title.getText().toString());
                    }
                    if(!TextUtils.isEmpty(event_desc.getText().toString().trim())){
                        SharedPreferencesUtils.setParam(getParentActivity(), "content", event_desc.getText().toString());
//                        Log.i("content=============",event_desc.getText().toString());
                    }
                    if(!TextUtils.isEmpty(position_name.getText().toString().trim())){
                        SharedPreferencesUtils.setParam(getParentActivity(), "location", position_name.getText().toString());
//                        Log.i("location=============",position_name.getText().toString());
                    }
                    getParentActivity().finish();
                }
            });
            saveAlertDialog.setButtonCancel(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAlertDialog.dismiss();
                    getParentActivity().finish();
                }
            });
        }
        if (!saveAlertDialog.isShowing()) {
            saveAlertDialog.show();
        }
    }


    private void showDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.timePicker);

        calendar = Calendar.getInstance();
//        MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis())
        //如果有时间缓存
//        Long ltime =  (Long)SharedPreferencesUtils.getParam(getParentActivity().getApplicationContext(),"date",0L);
        if(date != null && date != 0L){
            Timestamp ts = Timestamp.valueOf(MyDateUtils.getUTCDateString4DefaultFromLocal(date));
            calendar.setTimeInMillis(ts.getTime());
            datePicker.setCalendar(calendar);
            timePicker.setCalendar(calendar);
            mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(calendar.getTimeInMillis()));
//            Log.i("ltime=======================",ltime.toString());
        }
        //日历dialog
        pickDateTimeDialog = new MyDialog(getParentActivity(), getString(R.string.title_pick_date_time), dateTimePicker);
        pickDateTimeDialog.setButtonAccept(getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
                if(datePicker != null && timePicker != null){

                }
                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, datePicker.getYear());
                mCalendar.set(Calendar.MONTH, datePicker.getMonth());
                mCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDay());
                mCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHourOfDay());
                mCalendar.set(Calendar.MINUTE, timePicker.getMinute());

                if(MyDateUtils.isBeforeDate(mCalendar.getTimeInMillis())){
                    MessageUtil.showMessage(getActivity(),R.string.msg_date_not_befort_now);
                    return;
                }
                //把时间储存到缓存
                if(mCalendar!=null){
                    SharedPreferencesUtils.setParam(getParentActivity(), "date", mCalendar.getTimeInMillis());
                }
                //将日历的时间转化成字符串
                String dateDesc = MyDateUtils.getLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
                mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis()));
                //将日历的时间显示出来
                date_desc.setText(dateDesc);
            }
        });
        pickDateTimeDialog.setButtonCancel("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
            }
        });

        pickDateTimeDialog.show();
    }

    private void goLocationSetting() {
        //TODO 判断是用百度还是google
//        Intent intent = new Intent(getActivity(), Map4GoogleActivity.class);
        Intent intent = new Intent(getActivity(), Map4BaiduActivity.class);
        //        intent.putExtra("has_location", position_name.getText().toString());
        if (!TextUtils.isEmpty(position_name.getText())) {
            intent.putExtra("location_name", position_name.getText().toString());
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }
        startActivityForResult(intent, GET_LOCATION);
    }

    private void goChooseMembers() {
        Intent intent = new Intent(getActivity(), SelectPeopleActivity.class);
        intent.putExtra("members_data", gson.toJson(members_data));
        intent.putExtra("type", 0);
        startActivityForResult(intent, GET_MEMBERS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case GET_LOCATION:
                    if (data != null) {
                        //        intent.putExtra("has_location", position_name.getText().toString());
                        String locationName = data.getStringExtra("location_name");
                        if (!TextUtils.isEmpty(locationName)) {
                            position_name.setText(locationName);
                            mEevent.setLoc_name(locationName);
                            latitude = data.getDoubleExtra("latitude", 0);
                            longitude = data.getDoubleExtra("longitude", 0);
                        }else{
                            position_name.setText("");
                            latitude = -1000;
                            longitude = -1000;
                        }
                    }
                    break;
                case GET_MEMBERS:
                    String members = data.getStringExtra("members_data");
                    members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    changeData();
                    break;
            }
        }
    }

    //判断输入的内容是否为空
    private boolean validateForm() {
        if (TextUtils.isEmpty(event_title.getText().toString().trim())) {
            MessageUtil.showMessage(getParentActivity(), R.string.alert_text_title_null);
            return false;
        }
        if (TextUtils.isEmpty(event_desc.getText().toString().trim())) {
            MessageUtil.showMessage(getParentActivity(), R.string.alert_text_desc_null);
            return false;
        }
        if (TextUtils.isEmpty(position_name.getText().toString().trim())) {
            MessageUtil.showMessage(getParentActivity(), R.string.alert_text_location_null);
            return false;
        }
        if (TextUtils.isEmpty(date_desc.getText())) {
            MessageUtil.showMessage(getParentActivity(), R.string.alert_text_date_null);
            return false;
        }

        if (mCalendar == null) {
            if (MyDateUtils.isBeforeDate(MyDateUtils.dateString2Timestamp(MyDateUtils.getLocalDateString4DefaultFromUTC(mEevent.getGroup_event_date())).getTime())) {
                MessageUtil.showMessage(getActivity(), R.string.msg_date_not_befort_now);
                return false;
            }

        } else {
            if (MyDateUtils.isBeforeDate(mCalendar.getTimeInMillis())) {
                MessageUtil.showMessage(getActivity(), R.string.msg_date_not_befort_now);
                return false;
            }
        }

        return true;
    }

    //刷新数据
    private void changeData() {
        if (members_data == null) {
            members_data = new ArrayList<>();
        }
        adapter = new MembersGridAdapter(getActivity(), members_data);
        gvFriends.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private List<String> setGetMembersIds(List<UserEntity> users) {
        List<String> ids = new ArrayList<>();
        if (users != null) {
            int count = users.size();
            for (int i = 0; i < count; i++) {
                ids.add(users.get(i).getUser_id());
            }
        }
        return ids;
    }
}
