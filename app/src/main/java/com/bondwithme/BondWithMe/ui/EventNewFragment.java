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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MembersGridAdapter;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.SharedPreferencesUtils;
import com.bondwithme.BondWithMe.widget.DatePicker;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.TimePicker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bondwithme.BondWithMe.ui.EventNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.bondwithme.BondWithMe.ui.EventNewFragment#newInstance} factory method to
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

    public List<UserEntity> members_data = new ArrayList();//好友
    public List<GroupEntity> at_groups_data = new ArrayList<>();//群组
    public List<UserEntity> tempuserList = new ArrayList();
    public List<UserEntity> userList = new ArrayList();
    private final static String USER_HEAD = "user_head";
    private final static String USER_NAME = "user_name";

    private ProgressBarCircularIndeterminate progressBar;
    Calendar mCalendar;
    Calendar calendar;

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
        event_desc.addTextChangedListener(mTextWatcher);
        event_desc.setSelection(event_desc.length());
        mTextView =  getViewById(R.id.count);
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
                    if (isEventDate()) {
                        showSaveAlert();
                    } else {
                        getParentActivity().finish();
                    }
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

    /**
     * 返回键监听
     * @return
     */
    public boolean backCheck() {
        if(isEventDate()){
            showSaveAlert();
            return true;
        }else {
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
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
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
//             Log.i("event_title=====================",event_title.getText().toString().trim());
            return true;
        }
        if (!TextUtils.isEmpty(event_desc.getText().toString().trim())) {
//             Log.i("Spmemeber_date=====================",event_desc.getText().toString().trim());
            return true;
        }
        if (!TextUtils.isEmpty(position_name.getText().toString().trim())) {
//             Log.i("Spmemeber_date=====================",position_name.getText().toString().trim());
            return true;
        }
        if (!TextUtils.isEmpty(date_desc.getText().toString().trim())) {
//             Log.i("Spmemeber_date=====================",date_desc.getText().toString().trim());
            return true;
        }
//        if (members != null) {
//            String tempdate = members.trim().replaceAll("\\[([^\\]]*)\\]", "$1");//处理两边的中括号
//            if (!TextUtils.isEmpty(tempdate.trim())) {
//                return true;
//            }
//        }
//        if (groups != null) {
//            String userDate = groups.trim().replaceAll("\\[([^\\]]*)\\]", "$1");
//            if (!TextUtils.isEmpty(userDate.trim())) {
//                return true;
//            }
//        }
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
        title = SharedPreferencesUtils.getParam(getParentActivity(), "title", "").toString();
        content = SharedPreferencesUtils.getParam(getParentActivity(), "content", "").toString();
        location = SharedPreferencesUtils.getParam(getParentActivity(), "location", "").toString();
        date = (Long) SharedPreferencesUtils.getParam(getParentActivity().getApplicationContext(), "date", 0L);
//        members = SharedPreferencesUtils.getParam(getParentActivity(), "members_data", "").toString();
//        groups = SharedPreferencesUtils.getParam(getParentActivity(), "Groups_date", "").toString();
        users_date = SharedPreferencesUtils.getParam(getParentActivity(), "users_date", "").toString();

        setText();
        latitude = TextUtils.isEmpty(mEevent.getLoc_latitude()) ? -1000 : Double.valueOf(mEevent.getLoc_latitude());
        longitude = TextUtils.isEmpty(mEevent.getLoc_longitude()) ? -1000 : Double.valueOf(mEevent.getLoc_longitude());
    }

    /**
     * 移除重复的好友
     * @param userList
     */
    public static void removeDuplicate(List<UserEntity> userList) {
        for (int i = 0; i < userList.size() - 1; i++) {
            for (int j = userList.size() - 1; j > i; j--) {
                if (userList.get(j).getUser_id().equals(userList.get(i).getUser_id()) ||
                        userList.get(j).getUser_id().equals(MainActivity.getUser().getUser_id())) {
//                    Log.i("remove===",j+"");
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
        new HttpTools(getActivity()).get(url, null,Tag, new HttpCallback() {
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
//                Log.i("onError===",e.getMessage());
                Toast.makeText(getActivity(), getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });



        /*
        if(at_groups_data.size()>0){

            for (int i=0; i < at_groups_data.size(); i++){
                final int temp = i;
                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("group_id", at_groups_data.get(i).getGroup_id());
                jsonParams.put("viewer_id", MainActivity.getUser().getUser_id());
                String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);//??
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("condition", jsonParamsString);
                String url = UrlUtil.generateUrl(Constant.API_GROUP_MEMBERS, params);

                new HttpTools(getActivity()).get(url, null, new HttpCallback() {
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
                        Log.i("at_groups_data===", at_groups_data.get(temp).getGroup_id());
                        tempuserList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());
                        userList.addAll(tempuserList);
                        if(temp == (at_groups_data.size()-1)){
                            removeDuplicate(userList);
                            changeData();
//                                users_date ＝
                        }

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
        }
        */

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
            HashMap<String, String> params = new HashMap<String, String>();
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
            requestInfo.params = params;

            new HttpTools(getActivity()).post(requestInfo,Tag, new HttpCallback() {
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

    private void setText() {
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

    private void cleanText() {
        event_title.setText("");
        event_desc.setText("");
        position_name.setText("");
        date_desc.setText("");
    }

    private void reomveSP() {
//        File file = new File("/data/data/"+getParentActivity().getPackageName().toString()+"/shared_prefs","EventNew_date.xml");
//        if (file.exists()){
//            file.delete();
////            Toast.makeText(getParentActivity(), "删除成功", Toast.LENGTH_LONG).show();
//        }
        SharedPreferencesUtils.removeParam(getParentActivity(), "title", "");
        SharedPreferencesUtils.removeParam(getParentActivity(), "content", "");
        SharedPreferencesUtils.removeParam(getParentActivity(), "location", "");
        SharedPreferencesUtils.removeParam(getParentActivity(), "date", 0L);
//        SharedPreferencesUtils.removeParam(getParentActivity(), "members_data", "");
//        SharedPreferencesUtils.removeParam(getParentActivity(), "Groups_date", "");
        SharedPreferencesUtils.removeParam(getParentActivity(), "users_date", "");

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
                if(pickDateTimeDialog==null||!pickDateTimeDialog.isShowing()) {
                    showDateTimePicker();
                }
                break;
        }
    }

    private void showSaveAlert() {
        if (saveAlertDialog == null) {
            saveAlertDialog = new MyDialog(getActivity(), getString(R.string.text_tips_title), getString(R.string.draft_ask_save));
            saveAlertDialog.setButtonAccept(getString(R.string.event_accept), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAlertDialog.dismiss();
//                    Long tiem = mCalendar.getTimeInMillis();
                    if (!TextUtils.isEmpty(event_title.getText().toString().trim())) {
                        SharedPreferencesUtils.setParam(getParentActivity(), "title", event_title.getText().toString());
//                        Log.i("title=============",event_title.getText().toString());
                    }
                    if (!TextUtils.isEmpty(event_desc.getText().toString().trim())) {
                        SharedPreferencesUtils.setParam(getParentActivity(), "content", event_desc.getText().toString());
//                        Log.i("content=============",event_desc.getText().toString());
                    }
                    if (!TextUtils.isEmpty(position_name.getText().toString().trim())) {
                        SharedPreferencesUtils.setParam(getParentActivity(), "location", position_name.getText().toString());
//                        Log.i("location=============",position_name.getText().toString());
                    }
//                    if (!TextUtils.isEmpty(members.trim())) {
//                        SharedPreferencesUtils.setParam(getParentActivity(), "members_data", members);
////                        Log.i("Set_Spmemeber_date===", Spmemeber_date);
//                    }
//                    if (!TextUtils.isEmpty(groups.trim())) {
//                        SharedPreferencesUtils.setParam(getParentActivity(), "Groups_date", groups);
////                        Log.i("Set_Groups_date===", Groups_date);
//                    }
                    if (userList.size() > 0) {
                        Gson gson = new Gson();
                        SharedPreferencesUtils.setParam(getParentActivity(), "users_date", gson.toJson(userList));
//                        Log.i("Set_users_date===", users_date);
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
        pickDateTimeDialog.setButtonAccept(getString(R.string.accept), new View.OnClickListener() {
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
                    SharedPreferencesUtils.setParam(getParentActivity(), "date", mCalendar.getTimeInMillis());
                }
                //将日历的时间转化成字符串
                String dateDesc = MyDateUtils.getEventLocalDateStringFromLocal(getActivity(), mCalendar.getTimeInMillis());
                mEevent.setGroup_event_date(MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis()));
                //将日历的时间显示出来
                date_desc.setText(dateDesc);
            }
        });
        pickDateTimeDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
            }
        });

        pickDateTimeDialog.show();
    }

    private void goLocationSetting() {
        Intent intent = LocationUtil.getPlacePickerIntent(getActivity(), latitude, longitude,position_name.getText().toString());
        if(intent!=null)
            startActivityForResult(intent, GET_LOCATION);
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
                        //        intent.putExtra("has_location", position_name.getText().toString());
//                        if (SystemUtil.checkPlayServices(getActivity())) {
//                            final Place place = PlacePicker.getPlace(data, getActivity());
//                            if(place!=null) {
////                                String locationName = place.getAddress().toString();
//                                String locationName = place.getName().toString();
//                                //TODO 弹窗输入目标名称
//                                if(locationName==null){
//
//                                }
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