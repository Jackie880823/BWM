package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MembersGridAdapter;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.widget.DatePicker;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.MyGridViewForScroolView;
import com.bondwithme.BondWithMe.widget.TimePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.material.widget.CircularProgress;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bondwithme.BondWithMe.ui.EventEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventEditFragment extends BaseFragment<EventEditActivity> implements View.OnClickListener {

    private static final String Tag = EventEditFragment.class.getSimpleName();
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
    private EditText event_desc;
    private TextView mTextView;
    private ImageButton position_choose;
    private CardView item_date;
    private TextView date_desc;
    private TextView position_name;

    private boolean isFinish;

    private double latitude = -1000;
    private double longitude = -1000;
    private CircularProgress progressBar;

    private static final int MAX_COUNT = 300;

    Calendar mCalendar;

    @Override
    public void initView() {
        progressBar = getViewById(R.id.progressBar);
        mEevent = getParentActivity().eventEntity;
        gvFriends = getViewById(R.id.gv_all_friends);
        isFinish = true;

        changeData();

        event_title = getViewById(R.id.event_title);
        event_desc = getViewById(R.id.event_desc);
        event_desc.addTextChangedListener(mTextWatcher);
        event_desc.setSelection(event_desc.length());
        mTextView =  getViewById(R.id.count);
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
                //??
                if (v.getId() == getParentActivity().leftButton.getId()) {
                    // showSaveAlert();
                    getParentActivity().finish();
                } else if (v.getId() == getParentActivity().rightButton.getId()) {
                    //右边打勾按钮触发的事件的事件
                    if (isFinish){
                        isFinish = true;
                        submit();
                    }
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
        date_desc.setText(MyDateUtils.getEventLocalDateStringFromUTC(getActivity(), mEevent.getGroup_event_date()));

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
            mEevent.setLoc_name(position_name.getText().toString());

            if (latitude == -1000 || longitude == -1000) {
                mEevent.setLoc_latitude("");
                mEevent.setLoc_longitude("");
            } else {
                mEevent.setLoc_latitude("" + latitude);
                mEevent.setLoc_longitude("" + longitude);
            }


            RequestInfo requestInfo = new RequestInfo();
            requestInfo.jsonParam = gson.toJson(mEevent);
            requestInfo.url = Constant.API_EVENT_UPDATE + mEevent.getGroup_id();

            new HttpTools(getActivity()).put(requestInfo,Tag, new HttpCallback() {

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String response) {
                    isFinish = false;
                    Intent intent = new Intent();
                    intent.putExtra("event", mEevent);
                    getParentActivity().setResult(Activity.RESULT_OK, intent);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    getActivity().setResult(1);
                    getActivity().finish();
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

    /**
     * 监听输入字符
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        private int editStart;

        private int editEnd;

        public void afterTextChanged(Editable s) {
            editStart = event_desc.getSelectionStart();
            editEnd = event_desc.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            event_desc.removeTextChangedListener(mTextWatcher);

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            // 恢复监听器
            event_desc.addTextChangedListener(mTextWatcher);

            setLeftCount();
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    };

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            len++;

//            int tmp = (int) c.charAt(i);
//            if (tmp > 0 && tmp < 127) {
//                len += 0.5;
//            } else {
//                len++;
//            }
        }
        return Math.round(len);
    }

    /**
     * 刷新剩余输入字数
     */
    private void setLeftCount() {
        mTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(event_desc.getText().toString());
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

        new HttpTools(getActivity()).get(url, null,Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                members_data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                //刷新
                changeData();
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
                if(pickDateTimeDialog==null||!pickDateTimeDialog.isShowing()) {
                    showDateTimePicker();
                }
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

                String dateDesc = MyDateUtils.getEventLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
//                Log.i("TimeDialog===",dateDesc);
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
    //??
    private void goLocationSetting() {
        final Intent intent = LocationUtil.getPlacePickerIntent(getActivity(), latitude, longitude,position_name.getText().toString());
        if(intent!=null) {

            if(!LocationUtil.isOPen(getActivity())) {
                final MyDialog myDialog = new MyDialog(getActivity(), getString(R.string.open_gps_title), getString(R.string.use_gps_hint));
                myDialog.setButtonAccept(R.string.text_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationUtil.openGPS(getActivity());
                        myDialog.dismiss();
                        startActivityForResult(intent, GET_LOCATION);
                    }
                });
                myDialog.setButtonCancel(R.string.text_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        startActivityForResult(intent, GET_LOCATION);
                    }
                });
                myDialog.show();
            } else {
                startActivityForResult(intent, GET_LOCATION);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GET_LOCATION:
                    if (data != null) {
                        //        intent.putExtra("has_location", position_name.getText().toString());
//                        if (SystemUtil.checkPlayServices(getActivity())) {
//                            final Place place = PlacePicker.getPlace(data, getActivity());
//                            if(place!=null) {
//                                String locationName = place.getAddress().toString();
//                                position_name.setText(locationName);
//                                latitude = place.getLatLng().latitude;
//                                longitude = place.getLatLng().longitude;
//                            }
//
//                        }else {
                            String locationName = data.getStringExtra(Constant.EXTRA_LOCATION_NAME);
                            if (!TextUtils.isEmpty(locationName)) {
                                position_name.setText(locationName);
                                mEevent.setLoc_name(locationName);
                                latitude = data.getDoubleExtra(Constant.EXTRA_LATITUDE, 0);
                                longitude = data.getDoubleExtra(Constant.EXTRA_LONGITUDE, 0);
                            } else {
                                position_name.setText(null);
                                latitude = -1000;
                                longitude = -1000;
                            }
//                        }
                         //坐标数据类型
                        mEevent.setLoc_type(data.getStringExtra("loc_type"));
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
