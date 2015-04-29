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
import com.madx.bwm.widget.DatePicker;
import com.madx.bwm.widget.MyDialog;
import com.madx.bwm.widget.TimePicker;

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

    public List<UserEntity> members_data = new ArrayList();
    private final static String USER_HEAD = "user_head";
    private final static String USER_NAME = "user_name";
    private ProgressBarCircularIndeterminate progressBar;
    Calendar mCalendar;

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
        getViewById(R.id.rl_add_members).setOnClickListener(this);
        position_choose.setOnClickListener(this);
        item_date.setOnClickListener(this);


        //实现特定事件
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                submit();
                return false;
            }
        });


    }

    private void submit() {

        if(progressBar.getVisibility()==View.VISIBLE){
            return;
        }


        if (validateForm()) {
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

    private void showDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.timePicker);

        pickDateTimeDialog = new MyDialog(getParentActivity(), getString(R.string.title_pick_date_time), dateTimePicker);
        pickDateTimeDialog.setButtonAccept(getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
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

                String dateDesc = MyDateUtils.getLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
                mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis()));

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
