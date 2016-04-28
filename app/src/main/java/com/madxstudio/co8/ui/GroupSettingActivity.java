package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.FamilyGroupEntity;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quankun on 16/4/25.
 */
public class GroupSettingActivity extends BaseActivity implements View.OnClickListener {
    private String Tag = GroupSettingActivity.class.getSimpleName();
    private CardView llSetting;
    private CircularNetworkImage cniMain;
    private TextView tvName;
    private TextView tvNumMembers;
    private ListView lvMembers;
    private Button btnLeaveGroup;
    private List<UserEntity> userList;
    private String groupId;//当前群Id
    private String groupName;//当前群名字
    private String groupDefault;
    private final static int GET_MEMBERS = 2;
    private final static int SET_GROUP_PIC_NAME = 3;
    private final static int GET_RELATIONSHIP = 4;
    private List<String> addMemberList;
    private Context mContext;
    private static final int GET_DATA = 0X11;
    private static final int GET_MEMBERS_DATA = 0X12;

    //    private int type;//0  其他界面打开，1是Archive打开
    private boolean isGroupDefault;
    private boolean isGroupAdmin;
    private View vProgress;
    private GroupInfoAdapter groupSettingAdapter;
    private String memberTypeId;
    private String response_type;

    @Override
    public int getLayout() {
        return R.layout.activity_group_setting;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void titleLeftEvent() {
        Intent intent = new Intent();
        intent.putExtra("groupName", groupName);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_group_info));
    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent(mContext, SelectMemberActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra(Constant.SELECT_MEMBER_DATA, new Gson().toJson(userList));
        intent.putExtra("groups_data", "");
        intent.putExtra("selectNewData", 1);
        startActivityForResult(intent, GET_MEMBERS);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        mContext = this;
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
//        type = getIntent().getIntExtra("groupType", 0);
        groupDefault = getIntent().getStringExtra(Constant.GROUP_DEFAULT);
        if ("1".equals(groupDefault)) {
            isGroupDefault = true;
        }
        llSetting = getViewById(R.id.ll_setting);
        cniMain = getViewById(R.id.cni_main);
        tvName = getViewById(R.id.tv_group_name);
        tvNumMembers = getViewById(R.id.tv_num_members);
        lvMembers = getViewById(R.id.lv_members);
        btnLeaveGroup = getViewById(R.id.btn_leave_group);
        vProgress = getViewById(R.id.rl_progress);
        tvName.setText(groupName);
        addMemberList = new ArrayList();
        getIsGroupDefault();
        if (isGroupDefault) {
            btnLeaveGroup.setVisibility(View.GONE);
        }
        BitmapTools.getInstance(mContext).display(cniMain, String.format(Constant.API_GET_GROUP_PHOTO, groupId),
                R.drawable.b2be_normal, R.drawable.b2be_normal);
        groupSettingAdapter = new GroupInfoAdapter(mContext, R.layout.item_group_setting_members, new ArrayList());
        lvMembers.setAdapter(groupSettingAdapter);
        getMembersList();
        llSetting.setOnClickListener(this);
        cniMain.setOnClickListener(this);
        btnLeaveGroup.setOnClickListener(this);
        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserEntity userEntity = groupSettingAdapter.getmUserList().get(position);
                if (isGroupAdmin) {
                    if (!MainActivity.getUser().getUser_id().equals(userEntity.getUser_id())) {
                        if ("0".equals(userEntity.getAdded_flag())) {
                            showAdminNoFriendDialog(userEntity);
                        } else if ("1".equals(userEntity.getAdded_flag())) {
                            showAdminIsFriendDialog(userEntity);
                        }
                    }
                } else {
                    if (!MainActivity.getUser().getUser_id().equals(userEntity.getUser_id())) {
                        if ("0".equals(userEntity.getAdded_flag())) {
                            showNoAdminNoFriendDialog(userEntity);
                        } else if ("1".equals(userEntity.getAdded_flag())) {
                            showNoAdminIsFriendDialog(userEntity);
                        }
                    }
                }
            }
        });
    }

    private void showNoAdminNoFriendDialog(final UserEntity userEntity) {
        View selectIntention = LayoutInflater.from(this).inflate(R.layout.dialog_group_info_options_non_admin0, null);
        final MyDialog showNonAdminDialog0 = new MyDialog(this, null, selectIntention);
        TextView tvAdd = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNonAdminDialog0.dismiss();
                getMemberType(userEntity.getUser_id());
            }
        });
        if (!showNonAdminDialog0.isShowing())
            showNonAdminDialog0.show();
    }

    private void showNoAdminIsFriendDialog(final UserEntity userEntity) {
        View selectIntention = LayoutInflater.from(this).inflate(R.layout.dialog_group_info_options_non_admin1, null);
        final MyDialog showNonAdminDialog1 = new MyDialog(this, null, selectIntention);
        TextView tvFamilyProfile = (TextView) selectIntention.findViewById(R.id.tv_family_profile);
        TextView tvMessage = (TextView) selectIntention.findViewById(R.id.tv_message);
        tvFamilyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext, FamilyProfileActivity.class);
                intent1.putExtra(UserEntity.EXTRA_MEMBER_ID, userEntity.getUser_id());
                intent1.putExtra(UserEntity.EXTRA_GROUP_ID, userEntity.getGroup_id());
                intent1.putExtra(UserEntity.EXTRA_GROUP_NAME, userEntity.getUser_given_name());
                startActivity(intent1);
                showNonAdminDialog1.dismiss();
            }
        });

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MessageChatActivity.class);
                intent2.putExtra(Constant.MESSAGE_CHART_TYPE, Constant.MESSAGE_CHART_TYPE_MEMBER);
                intent2.putExtra(UserEntity.EXTRA_GROUP_ID, userEntity.getGroup_id());
                intent2.putExtra(Constant.MESSAGE_CHART_TITLE_NAME, userEntity.getUser_given_name());
                intent2.putExtra(Constant.MESSAGE_CHART_STATUS, userEntity.getStatus());
                startActivity(intent2);
                showNonAdminDialog1.dismiss();
            }
        });
        if (!showNonAdminDialog1.isShowing())
            showNonAdminDialog1.show();
    }

    private void removeMember(final UserEntity userEntity) {
        RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userEntity.getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        requestInfo.url = String.format(Constant.API_GROUP_REMOVE_MEMBERS, groupId);
        requestInfo.jsonParam = jsonParamsString;
        new HttpTools(mContext).put(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (("200").equals(jsonObject.getString("response_status_code"))) {
                        userList.remove(userEntity);
                        groupSettingAdapter.removeData(userEntity);
                        MessageUtil.getInstance(mContext).showShortToast(R.string.text_success_remove);
                        getMembersList();
                    } else {
                        MessageUtil.getInstance(mContext).showShortToast(R.string.text_fail_remove);
                    }
                } catch (JSONException e) {
                    MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void showAdminIsFriendDialog(final UserEntity userEntity) {
        View selectIntention = LayoutInflater.from(this).inflate(R.layout.dialog_group_info_options_admin1, null);
        final MyDialog showAdminDialog1 = new MyDialog(this, null, selectIntention);
        TextView tvRemoveUser = (TextView) selectIntention.findViewById(R.id.tv_remove_user);
        TextView tvFamilyProfile = (TextView) selectIntention.findViewById(R.id.tv_family_profile);
        TextView tvMessage = (TextView) selectIntention.findViewById(R.id.tv_message);
        tvRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminDialog1.dismiss();
                removeMember(userEntity);
            }
        });


        tvFamilyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext, FamilyProfileActivity.class);
                intent1.putExtra(UserEntity.EXTRA_MEMBER_ID, userEntity.getUser_id());
                intent1.putExtra(UserEntity.EXTRA_GROUP_ID, userEntity.getGroup_id());
                intent1.putExtra(UserEntity.EXTRA_GROUP_NAME, userEntity.getUser_given_name());
                startActivity(intent1);
                showAdminDialog1.dismiss();
            }
        });

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MessageChatActivity.class);
                intent2.putExtra("type", 0);
                intent2.putExtra("groupId", userEntity.getGroup_id());
                intent2.putExtra("titleName", userEntity.getUser_given_name());
                startActivity(intent2);
                showAdminDialog1.dismiss();
            }
        });
        if (!showAdminDialog1.isShowing())
            showAdminDialog1.show();
    }

    private void showAdminNoFriendDialog(final UserEntity userEntity) {
        View selectIntention = LayoutInflater.from(this).inflate(R.layout.dialog_group_info_options_admin0, null);
        final MyDialog showAdminDialog0 = new MyDialog(this, null, selectIntention);
        TextView tvRemoveUser = (TextView) selectIntention.findViewById(R.id.tv_remove_user);
        TextView tvAdd = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        tvRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminDialog0.dismiss();
                removeMember(userEntity);
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMemberType(userEntity.getUser_id());
                showAdminDialog0.dismiss();
            }
        });
        if (!showAdminDialog0.isShowing())
            showAdminDialog0.show();
    }

    public void getMemberType(String userId) {
        memberTypeId = userId;
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_GET_MEMBER_TYPE, MainActivity.getUser().getUser_id(), userId);
        new HttpTools(this).get(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        response_type = jsonObject.getString("response_type");
                        String response_relationship = jsonObject.getString("response_relationship");
                        if ("Resend".equals(response_type)) {
                            MessageUtil.getInstance(mContext).showShortToast(R.string.text_success_resend);
                        } else if ("Request".equals(response_type)) {
                            MessageUtil.getInstance(mContext).showShortToast(R.string.text_success_request);
                        } else if ("Accept".equals(response_type)) {
                            MessageUtil.getInstance(mContext).showShortToast(R.string.text_success_accept);
                        }
                        if (TextUtils.isEmpty(response_relationship)) {//关系为空时, 跳转到选择界面
                            startActivityForResult(new Intent(mContext, RelationshipActivity.class), GET_RELATIONSHIP);
                        }
                    } else {
                        MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
                    }

                } catch (JSONException e) {
                    MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
                    e.printStackTrace();
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_setting:
                if (isGroupDefault || isGroupAdmin) {
                    Intent intent = new Intent(mContext, GroupNameSettingActivity.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra(Constant.GROUP_DEFAULT, isGroupDefault);
                    startActivityForResult(intent, SET_GROUP_PIC_NAME);
                }
                break;
            case R.id.cni_main:
                Intent intent1 = new Intent(mContext, ViewOriginalPicesActivity.class);
                ArrayList<PhotoEntity> datas = new ArrayList();
                PhotoEntity peData = new PhotoEntity();
                peData.setUser_id(groupId);
                peData.setFile_id("group_profile");
                peData.setPhoto_caption("photo_profile");
                peData.setPhoto_multipe("false");
                datas.add(peData);
                intent1.putExtra("is_data", true);
                intent1.putExtra("datas", datas);
                startActivity(intent1);
                break;
            case R.id.btn_leave_group:
                leaveGroup();
                break;
        }
    }

    private void leaveGroup() {
        final MyDialog leaveGroupAlertDialog = new MyDialog(this, null, getResources().getString(R.string.text_leave_group_sure));
        leaveGroupAlertDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
            /**
             * end
             */
            @Override
            public void onClick(View v) {
                leaveGroupAlertDialog.dismiss();
                RequestInfo requestInfo = new RequestInfo();
                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("group_id", groupId);
                jsonParams.put("group_owner_id", "");
                jsonParams.put("group_user_default", "0");
                jsonParams.put("query_on", "exitGroup");
                jsonParams.put("user_id", MainActivity.getUser().getUser_id());
                final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                requestInfo.url = String.format(Constant.API_LEAVE_GROUP, groupId);
                requestInfo.jsonParam = jsonParamsString;
                new HttpTools(mContext).put(requestInfo, Tag, new HttpCallback() {
                    @Override
                    public void onStart() {
                        vProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        vProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (("200").equals(jsonObject.getString("response_status_code"))) {
                                MessageUtil.getInstance(mContext).showShortToast(R.string.text_success_leave_group);//成功
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.putExtra("jumpIndex", 3);
                                startActivity(intent);
                                finish();
                            } else {
                                MessageUtil.getInstance(mContext).showShortToast(R.string.text_fail_leave_group);//失败
                            }
                        } catch (JSONException e) {
                            MessageUtil.getInstance(mContext).showShortToast(R.string.text_fail_leave_group);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.getInstance(mContext).showShortToast(R.string.text_fail_leave_group);
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
            }
        });
        leaveGroupAlertDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroupAlertDialog.dismiss();

            }
        });
        if (!leaveGroupAlertDialog.isShowing()) {
            leaveGroupAlertDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case SET_GROUP_PIC_NAME:
                    if (data != null) {
                        groupName = data.getStringExtra("groupName");
                        groupId = data.getStringExtra("groupid");
                        tvName.setText(groupName);
                        BitmapTools.getInstance(mContext).display(cniMain, String.format(Constant.API_GET_GROUP_PHOTO, Constant.Module_profile, groupId),
                                R.drawable.b2be_normal, R.drawable.b2be_normal);
                    }
                    break;
                case GET_MEMBERS:
                    if (data == null) {
                        return;
                    }
                    if (addMemberList != null && addMemberList.size() > 0) {
                        addMemberList.clear();
                    }
                    String members = data.getStringExtra("members_data");
                    String groupData = data.getStringExtra("groups_data");
                    List<UserEntity> userEntityList = new GsonBuilder().create().fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    if (null != userEntityList && userEntityList.size() > 0) {
                        for (UserEntity user : userEntityList) {
//                            String userId = user.getUser_id();
//                            if (!memberList.contains(userId)) {
                            addMemberList.add(user.getUser_id());
//                            }
                        }
                    }

                    List<FamilyGroupEntity> groupEntityList = null;
                    if (null != groupData) {
                        groupEntityList = new GsonBuilder().create().fromJson(groupData, new TypeToken<ArrayList<FamilyGroupEntity>>() {
                        }.getType());
                    }
                    if (groupEntityList != null && groupEntityList.size() > 0) {
                        List<String> groupIdList = new ArrayList<>();
                        for (FamilyGroupEntity familyGroupEntity : groupEntityList) {
                            groupIdList.add(familyGroupEntity.getGroup_id());
                        }
                        getSelectMembersList(new Gson().toJson(groupIdList));
                    } else {
                        if (addMemberList.size() > 0) {
                            addGroupMember(new GsonBuilder().create().toJson(addMemberList));
                        }
                    }
                    break;
                case GET_RELATIONSHIP:
                    if (data != null) {
                        setRelationship(data.getStringExtra("relationship"));
                    }
                    break;
            }
        }
    }

    public void setRelationship(String response_relationship) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("member_id", memberTypeId);
        params.put("action_type", response_type);
        params.put("user_relationship_name", response_relationship);

        new HttpTools(this).post(Constant.API_SET_RELATIONSHIP, params, Tag, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        MessageUtil.getInstance(mContext).showShortToast(R.string.text_success_set_relationship);//成功
                        getMembersList();
                    } else {
                        MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
                    }

                } catch (JSONException e) {
                    MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
                    e.printStackTrace();
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

    public void addGroupMember(String strGroupMembers) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("group_id", groupId);
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("query_on", "addGroupMember");
        params.put("group_members", strGroupMembers);

        new HttpTools(mContext).post(Constant.API_GROUP_ADD_MEMBERS, params, Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                MessageUtil.getInstance(mContext).showShortToast(R.string.text_success);
                getMembersList();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance(mContext).showShortToast(R.string.text_error);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    public void getSelectMembersList(String groupIdList) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("group_list", groupIdList);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_GROUP_MEMBERS, params);
        new HttpTools(mContext).get(url, null, Tag, new HttpCallback() {
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
                List<UserEntity> addUserList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                if (addUserList != null && addUserList.size() > 0) {
                    Message.obtain(handler, GET_DATA, addUserList).sendToTarget();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance(mContext).showShortToast(R.string.text_error_try_again);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    public void getMembersList() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", groupId);
        jsonParams.put("viewer_id", MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_GROUP_MEMBERS, params);
        new HttpTools(mContext).get(url, null, Tag, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                List<UserEntity> userList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                if (userList != null && userList.size() > 0) {
                    Message.obtain(handler, GET_MEMBERS_DATA, userList).sendToTarget();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance(mContext).showShortToast(R.string.text_error_try_again);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    List<UserEntity> addUserList = (List<UserEntity>) msg.obj;
                    List<UserEntity> userEntityList = new ArrayList<>();
                    userEntityList.addAll(addUserList);
                    if (addMemberList.size() > 0) {
                        for (UserEntity userEntity : addUserList) {
                            for (String userId : addMemberList) {
                                if (userEntity.getUser_id().equals(userId)) {
                                    userEntityList.remove(userEntity);
                                    break;
                                }
                            }
                        }
                    }
                    for (UserEntity user : userEntityList) {
                        addMemberList.add(user.getUser_id());
                    }
                    if (addMemberList.size() > 0) {
                        addGroupMember(new GsonBuilder().create().toJson(addMemberList));
                    }
                    break;
                case GET_MEMBERS_DATA:
                    userList = (List<UserEntity>) msg.obj;
                    if (userList == null) {
                        break;
                    }
                    for (UserEntity user : userList) {
                        addMemberList.add(user.getUser_id());
                    }
                    if (MainActivity.getUser().getUser_id().equals(userList.get(0).getGroup_owner_id())) {
                        isGroupAdmin = true;
                        rightButton.setVisibility(View.VISIBLE);
                    } else {
                        rightButton.setVisibility(View.INVISIBLE);
                    }
                    groupSettingAdapter.setData(userList);
                    tvNumMembers.setText(userList.size() + " " + getResources().getString(R.string.text_members));
                    break;
            }
            return false;
        }
    });

    public class GroupInfoAdapter extends ArrayAdapter {
        private int resourceId;
        private Context mContext;
        private List<UserEntity> mUserList;

        public GroupInfoAdapter(Context context, int listViewResourceId, List userList) {
            super(context, listViewResourceId, userList);
            resourceId = listViewResourceId;
            mContext = context;
            mUserList = userList;
        }

        public void setData(List<UserEntity> list) {
            if (mUserList != null && mUserList.size() > 0) {
                mUserList.clear();
            }
            mUserList.addAll(list);
            notifyDataSetChanged();
        }

        public List<UserEntity> getmUserList() {
            if (mUserList == null) {
                return new ArrayList<>();
            }
            return mUserList;
        }

        public void removeData(UserEntity userEntity) {
            mUserList.remove(userEntity);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder.cniMain = (CircularNetworkImage) convertView.findViewById(R.id.cni_main);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tvAdmin = (TextView) convertView.findViewById(R.id.tv_admin);
                viewHolder.ivWaitting = (ImageView) convertView.findViewById(R.id.iv_waitting);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserEntity userEntity = mUserList.get(position);
            BitmapTools.getInstance(mContext).display(viewHolder.cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            viewHolder.tvName.setText(userEntity.getUser_given_name());
            if (userEntity.getUser_id().equals(userEntity.getGroup_owner_id())) {
                if (isGroupDefault) {
                    viewHolder.tvAdmin.setVisibility(View.GONE);
                } else {
                    viewHolder.tvAdmin.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.tvName.setText(userEntity.getUser_given_name());
            }
            viewHolder.ivWaitting.setVisibility(View.GONE);
            if ("1".equals(userEntity.getJoin_group())) {
                viewHolder.ivWaitting.setImageResource(R.drawable.existing_user);
            } else {
                viewHolder.ivWaitting.setImageResource(R.drawable.pending_user);
            }
            return convertView;
        }

        class ViewHolder {
            CircularNetworkImage cniMain;
            TextView tvName;
            TextView tvAdmin;
            ImageView ivWaitting;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Intent intent = new Intent();
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupid", groupId);
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void getIsGroupDefault() {
        String url = String.format(Constant.API_GET_GROUP_DEFAULT, groupId);
        new HttpTools(mContext).get(url, null, Tag, new HttpCallback() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                try {//[{"group_name":"MAD","group_owner_id":"31","group_individual":"0","group_type":"0","group_photo":"","group_default":"1"}]
                    JSONObject json = new JSONObject(string);
                    String group_default = json.optString("group_default");
                    if ("1".equalsIgnoreCase(group_default)) {
                        isGroupDefault = true;
                        btnLeaveGroup.setVisibility(View.GONE);
                        rightButton.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
