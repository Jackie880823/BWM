package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MembersGridAdapter;
import com.madx.bwm.entity.EventEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.widget.DatePicker;
import com.madx.bwm.widget.MyDialog;
import com.madx.bwm.widget.MyGridViewForScroolView;
import com.madx.bwm.widget.TimePicker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.EventEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventEditFragment extends BaseFragment<EventEditActivity> implements View.OnClickListener {


    private MembersGridAdapter adapter;
    Gson gson = new Gson();
    private MyDialog pickDateTimeDialog;
    private MyDialog saveAlertDialog;

    public static EventEditFragment newInstance(String... params) {
        return createInstance(new EventEditFragment());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_edit;
    }

    private MyGridViewForScroolView gvFriends;
    private TextView event_title;
    private TextView event_desc;
    private ImageButton position_choose;
    private CardView item_date;
    private TextView date_desc;
    private TextView position_name;

    private double latitude = -1000;
    private double longitude = -1000;
    private ProgressBarCircularIndeterminate progressBar;

    Calendar mCalendar;

    @Override
    public void initView() {
        progressBar = getViewById(R.id.progressBar);
        mEevent = getParentActivity().eventEntity;
        gvFriends = getViewById(R.id.gv_all_friends);

        changeData();

        event_title = getViewById(R.id.event_title);
        event_desc = getViewById(R.id.event_desc);
        position_choose = getViewById(R.id.position_choose);
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
        item_date = getViewById(R.id.item_date);
        date_desc = getViewById(R.id.date_desc);
        getViewById(R.id.rl_add_members).setOnClickListener(this);
        position_choose.setOnClickListener(this);
        item_date.setOnClickListener(this);

        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                if (v.getId() == getParentActivity().leftButton.getId()) {
                    showSaveAlert();
                } else if (v.getId() == getParentActivity().rightButton.getId()) {
                    submit();
                }
                return true;
            }
        });
        bindData2View();

    }


    private EventEntity mEevent;

    private void bindData2View() {

        event_title.setText(getParentActivity().eventEntity.getGroup_name());
        event_desc.setText(getParentActivity().eventEntity.getText_description());
        position_name.setText(mEevent.getLoc_name());
        date_desc.setText(MyDateUtils.getLocalDateStringFromUTC(getActivity(), mEevent.getGroup_event_date()));

        latitude = TextUtils.isEmpty(mEevent.getLoc_latitude()) ? -1000 : Double.valueOf(mEevent.getLoc_latitude());
        longitude = TextUtils.isEmpty(mEevent.getLoc_longitude()) ? -1000 : Double.valueOf(mEevent.getLoc_longitude());

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

    private void submit() {

        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        if (validateForm()) {
            progressBar.setVisibility(View.VISIBLE);
            mEevent.setEvent_member(setGetMembersIds(members_data));

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constant.API_EVENT_UPDATE + mEevent.getGroup_id(), new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Intent intent = new Intent();
                    intent.putExtra("event", mEevent);
                    getParentActivity().setResult(Activity.RESULT_OK, intent);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    progressBar.setVisibility(View.GONE);
                    getActivity().finish();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X_BWM_DEVID", "anonymous");
                    headers.put("X_BWM_TOKEN", "ccebaf1e71d606204ad1c31e86696d3e");
                    headers.put("X_BWM_USEREMAIL", "kengwai.chiah@madxstudio.com");
                    return headers;

                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    if (latitude == -1000 || longitude == -1000) {
                        mEevent.setLoc_latitude("");
                        mEevent.setLoc_longitude("");
                    } else {
                        mEevent.setLoc_latitude("" + latitude);
                        mEevent.setLoc_longitude("" + longitude);
                    }
                    return gson.toJson(mEevent).getBytes();
//                    return jsonParamsString.getBytes();
                }
            };


            stringRequest.setShouldCache(false);
            VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "event_new");
        }
    }

    public List<UserEntity> members_data = new ArrayList<UserEntity>();
    private final static String USER_HEAD = "user_head";
    private final static String USER_NAME = "user_name";

    @Override
    public void requestData() {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", mEevent.getGroup_id());
        jsonParams.put("response", "all");
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);

        String url = UrlUtil.generateUrl(Constant.API_EVENT_INVITED, params);

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                members_data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                //刷新
                changeData();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "errorX=====" + error.getMessage());
            }
        });
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity().getApplicationContext(), stringRequest, "");
    }

    private final static int GET_LOCATION = 1;
    private final static int GET_MEMBERS = 2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_members:
                goEditMembers();
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
                    getParentActivity().finish();
                }
            });
            saveAlertDialog.setButtonCancel(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAlertDialog.dismiss();
                }
            });
        }
        if (!saveAlertDialog.isShowing()) {
            saveAlertDialog.show();
        }
    }

    @Override
    public void onDestroyView() {
        closeDialogs();
        super.onDestroyView();
    }

    private void closeDialogs() {
        if (saveAlertDialog != null && saveAlertDialog.isShowing()) {
            saveAlertDialog.dismiss();
        }
    }

    private void showDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.timePicker);

        Timestamp ts = Timestamp.valueOf(mEevent.getGroup_event_date());
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
        datePicker.setCalendar(calendar);
        timePicker.setCalendar(calendar);

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

                if (MyDateUtils.isBeforeDate(mCalendar.getTimeInMillis())) {
                    MessageUtil.showMessage(getActivity(), R.string.msg_date_not_befort_now);
                    return;
                }

                String dateDesc = MyDateUtils.getLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
                mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis()));

                date_desc.setText(dateDesc);
            }
        });
        pickDateTimeDialog.setButtonCancel(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
            }
        });

        pickDateTimeDialog.show();
    }

    private void goEditMembers() {
        getParentActivity().setResult(Activity.RESULT_OK);
        Intent intent = new Intent(getActivity(), InvitedEditActivity.class);
        intent.putExtra("members_data", gson.toJson(members_data));
        intent.putExtra("group_id", mEevent.getGroup_id());
        intent.putExtra("owner_id", mEevent.getGroup_owner_id());
        startActivityForResult(intent, GET_MEMBERS);
    }

    private void goLocationSetting() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
//        intent.putExtra("has_location", position_name.getText().toString());
        intent.putExtra("location_name", position_name.getText().toString());
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivityForResult(intent, GET_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
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
                        } else {
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

    private void changeData() {
        if (members_data == null) {
            members_data = new ArrayList<>();
        }
        //排队创建者
        if (MainActivity.getUser().getUser_id().equals(mEevent.getGroup_owner_id())) {
            for (UserEntity user : members_data) {
                if (MainActivity.getUser().getUser_id().equals(user.getUser_id())) {
                    members_data.remove(user);
                    break;
                }
            }
        }

        adapter = new MembersGridAdapter(getActivity(), members_data);
        gvFriends.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

        mEevent.setText_description(event_desc.getText().toString());
        mEevent.setGroup_name(event_title.getText().toString());


        return true;
    }

}
