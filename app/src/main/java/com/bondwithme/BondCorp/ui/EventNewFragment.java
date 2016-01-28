package com.bondwithme.BondCorp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.App;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.MembersGridAdapter;
import com.bondwithme.BondCorp.entity.EventEntity;
import com.bondwithme.BondCorp.entity.GroupEntity;
import com.bondwithme.BondCorp.entity.UserEntity;
import com.bondwithme.BondCorp.http.UrlUtil;
import com.bondwithme.BondCorp.util.LocationUtil;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.MessageUtil;
import com.bondwithme.BondCorp.util.MyDateUtils;
import com.bondwithme.BondCorp.util.PreferencesUtil;
import com.bondwithme.BondCorp.util.UIUtil;
import com.bondwithme.BondCorp.widget.DatePicker;
import com.bondwithme.BondCorp.widget.InteractivePopupWindow;
import com.bondwithme.BondCorp.widget.MyDialog;
import com.bondwithme.BondCorp.widget.TimePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bondwithme.BondCorp.ui.EventNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.bondwithme.BondCorp.ui.EventNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventNewFragment extends BaseFragment<EventNewActivity> implements View.OnClickListener {
    private static final String Tag = EventNewFragment.class.getSimpleName();
    private MembersGridAdapter adapter;
    private MyDialog saveAlertDialog;
    private EventEntity mEevent = new EventEntity();
    private GridView gvFriends;
    private TextView event_title;
    private EditText event_desc;
    private TextView mTextView;
    private CardView item_date;
    private CardView item_end_date;
    private CardView item_reminder;
    private TextView date_desc;
    private TextView date_end_desc;
    private TextView reminder_desc;
    private EditText position_name;
    private ImageButton position_choose;

    private double latitude = -1000;
    private double longitude = -1000;
    Gson gson = new Gson();

    private String title;
    private String content;
    private String location;
    private Long date;

    public List<UserEntity> members_data = new ArrayList();//好友
    public List<GroupEntity> at_groups_data = new ArrayList<>();//群组
    public List<UserEntity> tempuserList = new ArrayList();
    public List<UserEntity> userList = new ArrayList();
    public List<EventEntity> prepareData = new ArrayList<EventEntity>();
    private final static String USER_HEAD = "user_head";
    private final static String USER_NAME = "user_name";
    private static final int GET_DELAY = 0x28;
    private InteractivePopupWindow popupWindow;

    private View progressBar;
    Calendar mCalendar;
    Calendar calendar;

    private String locationName;
    private long startMeetingTime = 0;

    String members;
    String groups;
    private String Spmemeber_date;
    private String users_date;

    private static final int MAX_COUNT = 300;

    public static EventNewFragment newInstance(String... params) {

        return createInstance(new EventNewFragment());
    }

    public EventNewFragment() {
        super();
        // Required empty public constructor
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DELAY:
                    String popText = getParentActivity().getResources().getString(R.string.text_tip_save_event);
                    if (TextUtils.isEmpty(popText)) return;

                    popupWindow = new InteractivePopupWindow(getParentActivity(), getParentActivity().rightButton, popText, 0);
                    popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                        @Override
                        public void popDismiss() {
                            LogUtil.i("==============event_save", "onDismiss");
                            //存储本地
                            PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_SAVE_EVENT, true);
                        }
                    });
                    popupWindow.showPopupWindow(true);
                    break;
            }
        }
    };

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_new;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        progressBar = getViewById(R.id.rl_progress);

        gvFriends = getViewById(R.id.gv_all_friends);
        changeData();

        event_title = getViewById(R.id.event_title);
        event_desc = getViewById(R.id.event_desc);
        event_desc.addTextChangedListener(mTextWatcher);
        event_desc.setSelection(event_desc.length());
        mTextView = getViewById(R.id.count);
        position_choose = getViewById(R.id.position_choose);
        item_date = getViewById(R.id.item_date);
        item_end_date = getViewById(R.id.item_end_date);
        reminder_desc = getViewById(R.id.reminder_desc);
        item_reminder = getViewById(R.id.item_reminder);
        date_desc = getViewById(R.id.date_desc);
        date_end_desc = getViewById(R.id.date_end_desc);
        position_name = getViewById(R.id.position_name);
        position_name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    return true;
                }
                return false;
            }
        });
        position_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tplocation = position_name.getText().toString().trim();
                if (!tplocation.equals(locationName) && locationName != null) {
                    latitude = -1000;
                    longitude = -1000;
                }
            }
        });
        //invited members点击事件
        getViewById(R.id.rl_add_members).setOnClickListener(this);
        position_choose.setOnClickListener(this);
        item_date.setOnClickListener(this);

        item_end_date.setOnClickListener(this);
        item_reminder.setOnClickListener(this);
        //点击事件
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                if (v.getId() == getParentActivity().leftButton.getId()) {
                    if (isEventDate()) {
                        showSaveAlert();
                    } else {
                        UIUtil.hideKeyboard(getActivity(), event_title);
                        getParentActivity().finish();
                    }
                } else if (v.getId() == getParentActivity().rightButton.getId()) {
                    reomveSP();
                    submit();
                }
                return false;
            }
        });
        bindData2View();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.IS_INTERACTIVE_USE &&
                !PreferencesUtil.getValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_SAVE_EVENT, false)) {
            getParentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
            handler.sendEmptyMessageDelayed(GET_DELAY, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (App.isInteractiveTipFinish()) {
            LogUtil.i("event_new====", "true");
            PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_START, false);
        } else {
            LogUtil.i("event_new====", "false");
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (MainActivity.IS_INTERACTIVE_USE && isVisibleToUser) {
            handler.sendEmptyMessage(GET_DELAY);
        }
    }

    /**
     * 返回键监听
     *
     * @return
     */
    public boolean backCheck() {
        if (isEventDate()) {
            showSaveAlert();
            return true;
        } else {
            getParentActivity().finish();
            return false;
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

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

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

    private boolean isEventDate() {
        if (!TextUtils.isEmpty(event_title.getText().toString().trim())) {
            return true;
        }
        if (!TextUtils.isEmpty(event_desc.getText().toString().trim())) {
            return true;
        }
        if (!TextUtils.isEmpty(position_name.getText().toString().trim())) {
            return true;
        }
        if (!TextUtils.isEmpty(date_desc.getText().toString().trim())) {
            return true;
        }
        if (users_date != null) {
            String userDate = users_date.trim().replaceAll("\\[([^\\]]*)\\]", "$1");
            if (!TextUtils.isEmpty(userDate.trim())) {
                return true;
            }
        }
        return false;
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
        String stlatitude;
        String stlongitude;

        title = PreferencesUtil.getValue(getParentActivity(), "title", "").toString();
        content = PreferencesUtil.getValue(getParentActivity(), "content", "").toString();
        location = PreferencesUtil.getValue(getParentActivity(), "location", "").toString();
        date = PreferencesUtil.getValue(getParentActivity().getApplicationContext(), "date", 0L);
        stlatitude = PreferencesUtil.getValue(getParentActivity(), "latitude", "-1000");
        stlongitude = PreferencesUtil.getValue(getParentActivity(), "longitude", "-1000");
        latitude = Double.valueOf(stlatitude).doubleValue();
        longitude = Double.valueOf(stlongitude).doubleValue();

        members = PreferencesUtil.getValue(getParentActivity(), "members_data", "").toString();
        groups = PreferencesUtil.getValue(getParentActivity(), "Groups_date", "").toString();
        users_date = PreferencesUtil.getValue(getParentActivity(), "users_date", "").toString();

        setText();
        latitude = TextUtils.isEmpty(mEevent.getLoc_latitude()) ? -1000 : Double.valueOf(mEevent.getLoc_latitude());
        longitude = TextUtils.isEmpty(mEevent.getLoc_longitude()) ? -1000 : Double.valueOf(mEevent.getLoc_longitude());
    }

    /**
     * 移除重复的好友
     *
     * @param userList
     */
    public static void removeDuplicate(List<UserEntity> userList) {
        for (int i = 0; i < userList.size() - 1; i++) {
            for (int j = userList.size() - 1; j > i; j--) {
                if (userList.get(j).getUser_id().equals(userList.get(i).getUser_id()) || userList.get(j).getUser_id().equals(MainActivity.getUser().getUser_id())) {
                    userList.remove(j);
                }

            }
        }
    }


    private void getMembersList(final String strGroupsid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("group_list", strGroupsid);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_GROUP_MEMBERS, params);
        new HttpTools(getActivity()).get(url, null, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                //                Log.i("at_groups_data===", at_groups_data.get(temp).getGroup_id());
                tempuserList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                //                Log.i("onResult===",response);
                //                Log.i("tempuserList_size===", tempuserList.size()+"");
                userList.addAll(tempuserList);
                removeDuplicate(userList);

                changeData();

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void submit() {

        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (validateForm()) {
            setText();
            progressBar.setVisibility(View.VISIBLE);
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.url = Constant.API_EVENT_CREATE;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("content_type", "post");
            params.put("first_post", "1");
            params.put("group_event_date", mEevent.getGroup_event_date());
            params.put("group_event_status", "1");
            params.put("group_name", event_title.getText().toString());
            params.put("group_owner_id", MainActivity.getUser().getUser_id());
            params.put("group_type", "1");
            if (latitude == -1000 || longitude == -1000) {
                params.put("loc_latitude", "");
                params.put("loc_longitude", "");
            } else {
                params.put("loc_latitude", latitude + "");
                params.put("loc_longitude", longitude + "");
            }
            params.put("loc_name", position_name.getText().toString());
            //坐标数据类型
            params.put("loc_type", mEevent.getLoc_type());
            params.put("text_description", event_desc.getText().toString());
            params.put("user_id", MainActivity.getUser().getUser_id());
            params.put("event_member", gson.toJson(setGetMembersIds(userList)));
            requestInfo.putAllParams(params);

            new HttpTools(getActivity()).post(requestInfo, Tag, new HttpCallback() {
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
                    //                    Log.i("new_button_rt====================", "");
//                    getParentActivity().finish();

                    try {
                        JSONObject jsonArray = null;
                        jsonArray = new JSONObject(response);
                        String group_id = jsonArray.getString("group_id");
                        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                        intent.putExtra("group_id", group_id);
                        startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE);
                        getParentActivity().finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

    private void setText() {
        LogUtil.i("setText_title", title);
        if (!TextUtils.isEmpty(title)) {
            event_title.setText(title);

        }
        if (!TextUtils.isEmpty(content)) {
            event_desc.setText(content);

        }
        if (!TextUtils.isEmpty(location)) {
            position_name.setText(location);

        }
        if (date != null && date != 0L) {
            date_desc.setText(MyDateUtils.getEventLocalDateStringFromLocal(getParentActivity(), date));
            mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(date));

        }
        if (!TextUtils.isEmpty(members)) {
            members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
            }.getType());
        }
        if (!TextUtils.isEmpty(groups)) {
            at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<UserEntity>>() {
            }.getType());

        }
        if (!TextUtils.isEmpty(users_date)) {
            userList = gson.fromJson(users_date, new TypeToken<ArrayList<UserEntity>>() {
            }.getType());
            changeData();
        }

    }

    /**
     * 删除草稿
     */
    private void reomveSP() {
        PreferencesUtil.saveValue(getParentActivity(), "title", "");
        PreferencesUtil.saveValue(getParentActivity(), "content", "");
        PreferencesUtil.saveValue(getParentActivity(), "location", "");
        PreferencesUtil.saveValue(getParentActivity(), "date", 0L);
        PreferencesUtil.saveValue(getParentActivity(), "members_data", "");
        PreferencesUtil.saveValue(getParentActivity(), "Groups_date", "");
        PreferencesUtil.saveValue(getParentActivity(), "users_date", "");
        PreferencesUtil.saveValue(getParentActivity(), "latitude", "-1000");
        PreferencesUtil.saveValue(getParentActivity(), "longitude", "-1000");

    }

    @Override
    public void requestData() {

    }

    private final static int GET_LOCATION = 1;
    private final static int GET_MEMBERS = 2;
    private final static int OPEN_GPS = 3;
    private MyDialog pickDateTimeDialog;
    private MyDialog pickEndDateTimeDialog;
    private MyDialog item_reminderDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_members:
                goChooseMembers();
                break;
            case R.id.position_choose:
                if (!LocationUtil.isOPen(getActivity())) {
                    LogUtil.i(Tag, "onClick& need open GPS");
                    final MyDialog myDialog = new MyDialog(getActivity(), R.string.open_gps_title, R.string.use_gps_hint);
                    myDialog.setButtonAccept(R.string.text_dialog_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, OPEN_GPS);
                            myDialog.dismiss();
                        }
                    });
                    myDialog.setButtonCancel(R.string.text_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                } else {
                    LogUtil.i(Tag, "onClick& GPS opened");
                    goLocationSetting();
                }
                break;
            case R.id.item_date:
                if (pickDateTimeDialog == null || !pickDateTimeDialog.isShowing()) {
                    showDateTimePicker();
                }
                break;
            case R.id.item_end_date:
                if (TextUtils.isEmpty(date_desc.getText())) {
                    MessageUtil.getInstance(getActivity()).showShortToast(getString(R.string.text_choose_start_time));
                } else {
                    if (pickEndDateTimeDialog == null || !pickEndDateTimeDialog.isShowing()) {
                        showEndDateTimePicker();
                    }
                }
                break;
            case R.id.item_reminder:
                if (item_reminderDialog == null || !item_reminderDialog.isShowing()) {
                    showReminderDialog();
                }
                break;
        }
    }

    private void showReminderDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View reminderView = factory.inflate(R.layout.meeting_reminder_list, null);
        ListView listView = (ListView) reminderView.findViewById(R.id.reminder_list_view);
        String[] reminderArrayUs = getActivity().getResources().getStringArray(R.array.reminder_item);
        final List<String> list = Arrays.asList(reminderArrayUs);
        ArrayAdapter reminderAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(reminderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item_reminderDialog.dismiss();
                reminder_desc.setText(list.get(i));
            }
        });
        item_reminderDialog = new MyDialog(getParentActivity(), "", reminderView);
        if (!item_reminderDialog.isShowing()) {
            item_reminderDialog.show();
        }
    }

    private void showSaveAlert() {
        if (saveAlertDialog == null) {
            saveAlertDialog = new MyDialog(getActivity(), getString(R.string.text_tips_title), getString(R.string.draft_ask_save));
            saveAlertDialog.setButtonAccept(getString(R.string.event_accept), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAlertDialog.dismiss();

                    PreferencesUtil.saveValue(getParentActivity(), "latitude", Double.toString(latitude));
                    PreferencesUtil.saveValue(getParentActivity(), "longitude", Double.toString(longitude));
                    if (!TextUtils.isEmpty(event_title.getText().toString().trim())) {
                        PreferencesUtil.saveValue(getParentActivity(), "title", event_title.getText().toString());
                    }
                    if (!TextUtils.isEmpty(event_desc.getText().toString().trim())) {
                        PreferencesUtil.saveValue(getParentActivity(), "content", event_desc.getText().toString());

                    }
                    if (!TextUtils.isEmpty(position_name.getText().toString().trim())) {
                        PreferencesUtil.saveValue(getParentActivity(), "location", position_name.getText().toString());

                    }
                    if (userList.size() > 0) {
                        Gson gson = new Gson();
                        PreferencesUtil.saveValue(getParentActivity(), "users_date", gson.toJson(userList));
                    }
                    getParentActivity().finish();
                }
            });
            saveAlertDialog.setButtonCancel(getString(R.string.event_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reomveSP();
                    saveAlertDialog.dismiss();
                    getParentActivity().finish();
                }
            });
        }
        if (!saveAlertDialog.isShowing()) {
            saveAlertDialog.show();
        }
    }

    private void showEndDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.timePicker);

        calendar = Calendar.getInstance();
        //        MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis())
        //        Long ltime =  (Long)SharedPreferencesUtils.getParam(getParentActivity().getApplicationContext(),"date",0L);
        //如果有时间缓存
        if (mEevent.getGroup_event_date() != null) {
            Timestamp ts = Timestamp.valueOf(mEevent.getGroup_event_date());
            calendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
            datePicker.setCalendar(calendar);
            timePicker.setCalendar(calendar);
        } else if (date != null && date != 0L) {

            Timestamp ts = Timestamp.valueOf(MyDateUtils.getUTCDateString4DefaultFromLocal(date));
            calendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
            datePicker.setCalendar(calendar);
            timePicker.setCalendar(calendar);
            mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(calendar.getTimeInMillis()));
        }
        //日历dialog
        pickEndDateTimeDialog = new MyDialog(getParentActivity(), getString(R.string.title_pick_date_time), dateTimePicker);
        pickEndDateTimeDialog.setButtonAccept(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (datePicker != null && timePicker != null) {

                }
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
                //把时间储存到缓存
                if (mCalendar != null) {
                    PreferencesUtil.saveValue(getParentActivity(), "date", mCalendar.getTimeInMillis());
                }
                //将日历的时间转化成字符串
                long endMeetingTime = mCalendar.getTimeInMillis();
                String dateDesc = MyDateUtils.getEventLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
                mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis()));
                //将日历的时间显示出来
                if (endMeetingTime - startMeetingTime <= 0) {
                    MessageUtil.getInstance(getActivity()).showShortToast(getString(R.string.text_meeting_end_time));
                } else {
                    pickEndDateTimeDialog.dismiss();
                    date_end_desc.setText(dateDesc);
                }
            }
        });
        pickEndDateTimeDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEndDateTimeDialog.dismiss();
            }
        });

        pickEndDateTimeDialog.show();
    }

    //日历
    private void showDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.timePicker);

        calendar = Calendar.getInstance();
        //        MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis())
        //        Long ltime =  (Long)SharedPreferencesUtils.getParam(getParentActivity().getApplicationContext(),"date",0L);
        //如果有时间缓存
        if (mEevent.getGroup_event_date() != null) {
            Timestamp ts = Timestamp.valueOf(mEevent.getGroup_event_date());
            calendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
            datePicker.setCalendar(calendar);
            timePicker.setCalendar(calendar);
        } else if (date != null && date != 0L) {

            Timestamp ts = Timestamp.valueOf(MyDateUtils.getUTCDateString4DefaultFromLocal(date));
            calendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
            datePicker.setCalendar(calendar);
            timePicker.setCalendar(calendar);
            mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(calendar.getTimeInMillis()));
        }
        //日历dialog
        pickDateTimeDialog = new MyDialog(getParentActivity(), getString(R.string.title_pick_date_time), dateTimePicker);
        pickDateTimeDialog.setButtonAccept(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
                if (datePicker != null && timePicker != null) {

                }
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
                //把时间储存到缓存
                if (mCalendar != null) {
                    PreferencesUtil.saveValue(getParentActivity(), "date", mCalendar.getTimeInMillis());
                }
                //将日历的时间转化成字符串
                String dateDesc = MyDateUtils.getEventLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
                mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis()));
                //将日历的时间显示出来
                startMeetingTime = mCalendar.getTimeInMillis();
                date_desc.setText(dateDesc);
            }
        });
        pickDateTimeDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
            }
        });

        pickDateTimeDialog.show();
    }

    private void goLocationSetting() {
        Intent intent = LocationUtil.getPlacePickerIntent(getActivity(), latitude, longitude, position_name.getText().toString());
        if (intent != null) {
            startActivityForResult(intent, GET_LOCATION);
        }
    }

    private void goChooseMembers() {
        //        Intent intent = new Intent(getActivity(), SelectPeopleActivity.class);
        Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
        intent.putExtra("members_data", gson.toJson(userList));
        intent.putExtra("groups_data", "");
        intent.putExtra("type", 0);
        tempuserList.clear();
        //        userList.clear();
        startActivityForResult(intent, GET_MEMBERS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case GET_LOCATION:
                    if (data != null) {
                        locationName = data.getStringExtra(Constant.EXTRA_LOCATION_NAME);
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
                        //坐标数据类型
                        mEevent.setLoc_type(data.getStringExtra("loc_type"));
                        //                        }
                    }
                    break;
                case GET_MEMBERS:
                    //获取SelectPeopleActivity回调的参数
                    members = data.getStringExtra("members_data");//获取好友选择页面传来到好友数据
                    members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    //                    Log.i("members===",members);

                    groups = data.getStringExtra("groups_data");//获取好友选择页面的群组数据
                    at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {
                    }.getType());
                    userList.clear();
                    userList.addAll(members_data);
                    List groupIdList = new ArrayList();
                    for (int i = 0; i < at_groups_data.size(); i++) {
                        groupIdList.add(at_groups_data.get(i).getGroup_id());
                    }
                    if (groupIdList.size() != 0) {
                        //                        Log.i("groupsid====", gson.toJson(groupIdList));
                        getMembersList(gson.toJson(groupIdList));
                    } else {
                        //                    Log.i("groups===", groups);
                        changeData();
                    }
                    break;
                case OPEN_GPS:
                    goLocationSetting();
                    break;
            }
        }

        if (requestCode == OPEN_GPS && LocationUtil.isOPen(getActivity())) {
            goLocationSetting();
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
        if (TextUtils.isEmpty(date_end_desc.getText())) {
            MessageUtil.showMessage(getParentActivity(), R.string.alert_text_date_null);
            return false;
        }

        if (mCalendar == null) {
            if (MyDateUtils.isBeforeDate(MyDateUtils.dateString2Timestamp(MyDateUtils.getLocalDateString4DefaultFromUTC(mEevent.getGroup_event_date())).getTime())) {
                MessageUtil.showMessage(getActivity(), R.string.msg_date_not_befort_now);
                return false;
            }

        } else {
            if (MyDateUtils.isBeforeDate(startMeetingTime)) {
                MessageUtil.showMessage(getActivity(), R.string.msg_date_not_befort_now);
                return false;
            }
        }

        return true;
    }


    //刷新数据
    private void changeData() {
        if (userList == null) {
            userList = new ArrayList<>();
        }
        for (UserEntity u : userList) {
            if (u.getUser_id().equals(MainActivity.getUser().getUser_id())) {
                userList.remove(u);
            }
        }
        adapter = new MembersGridAdapter(getActivity(), userList);
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