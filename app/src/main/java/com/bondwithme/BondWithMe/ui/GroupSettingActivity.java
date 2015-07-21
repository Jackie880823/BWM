package com.bondwithme.BondWithMe.ui;

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
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.FamilyGroupEntity;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupSettingActivity extends BaseActivity {

    private static final String Tag = GroupSettingActivity.class.getSimpleName();
    private CardView llSetting;
    private CircularNetworkImage cniMain;
    private TextView tvName;
    private TextView tvNumMembers;

    private ListView lvMembers;

    private Button btnLeaveGroup;

    //private GroupEntity groupEntity = new GroupEntity();//传进来的

    private List<UserEntity> userList;

    private Dialog showAdminDialog1;
    private Dialog showAdminDialog0;

    private Dialog showNonAdminDialog1;
    private Dialog showNonAdminDialog0;

    private boolean isAdmin;

    private String groupId;//当前群Id
    private String groupName;//当前群名字
    private String groupOwnerId;
    private Gson gson;
    private final static int GET_MEMBERS = 2;
    private final static int SET_GROUP_PIC_NAME = 3;
    private final static int GET_RELATIONSHIP = 4;
    private List<String> memberList;
    private List<String> addMemberList;
    private Context mContext;
    private static final int GET_DATA = 0X11;
    private String groupData = null;
    private List<FamilyGroupEntity> familyGroupEntityList;

    @Override
    public int getLayout() {
        return R.layout.activity_group_setting;
    }

    @Override
    protected void initBottomBar() {

    }
//    @Override
//    public void finish() {
//        Intent intent = new Intent();
//        intent.putExtra("groupName", tvName.getText().toString());
//        setResult(RESULT_OK, intent);
//        finish();
//    }
    @Override
    protected void titleLeftEvent() {
        Intent intent = new Intent();
        intent.putExtra("groupName", tvName.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }



    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_group_info));
    }

    @Override
    protected void titleRightEvent() {
        //是否会出现list数据还没下载完就点击进去了。此时不是最新数据。这里需要怎么处理呢？
        Intent intent = new Intent(GroupSettingActivity.this, InviteMemberActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("members_data", new Gson().toJson(userList));
        intent.putExtra("groups_data", "");
        startActivityForResult(intent, GET_MEMBERS);
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
        llSetting = getViewById(R.id.ll_setting);
        cniMain = getViewById(R.id.cni_main);
        tvName = getViewById(R.id.tv_group_name);
        tvNumMembers = getViewById(R.id.tv_num_members);
        lvMembers = getViewById(R.id.lv_members);
        btnLeaveGroup = getViewById(R.id.btn_leave_group);
        memberList = new ArrayList();
        addMemberList = new ArrayList();
        familyGroupEntityList = new ArrayList<>();
        FamilyGroupEntity groupEntity = new FamilyGroupEntity();
        groupEntity.setGroup_id(groupId);
        groupEntity.setGroup_name(groupName);
        familyGroupEntityList.add(groupEntity);
        gson = new Gson();
        VolleyUtil.initNetworkImageView(this, cniMain, String.format(Constant.API_GET_GROUP_PHOTO, groupId), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName.setText(groupName);
        //groupName = groupEntity.getGroup_name();

        getMembersList();


        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettingActivity.this, ViewOriginalPicesActivity.class);

                ArrayList<PhotoEntity> datas = new ArrayList();

                PhotoEntity peData = new PhotoEntity();

                peData.setUser_id(groupId);
                peData.setFile_id("group_profile");
                peData.setPhoto_caption("photo_profile");
                peData.setPhoto_multipe("false");

                datas.add(peData);

                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);

                startActivity(intent);
            }
        });

        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    Intent intent = new Intent(GroupSettingActivity.this, GroupNameSettingActivity.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("groupName", tvName.getText().toString());
                    startActivityForResult(intent, SET_GROUP_PIC_NAME);
                } else {

                }
            }
        });


        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isAdmin) {
                    if (position == 0) {
                        //群主点击群主
                    } else {
                        //群主点击群成员
                        //判断AddedFlag值
                        if ("0".equals(userList.get(position).getAdded_flag())) {
                            showAdminDialog0(position);
                        } else if ("1".equals(userList.get(position).getAdded_flag())) {
                            showAdminDialog1(position);
                        }

                    }
                } else {
                    if (position == 0) {
                        //用户成员点击群主
                        if ("0".equals(userList.get(position).getAdded_flag())) {
                            showNonAdminDialog0(position);
                        } else if ("1".equals(userList.get(position).getAdded_flag())) {
                            showNonAdminDialog1(position);
                        }
                    } else {
                        //用户成员点击群成员
                        //判断AddedFlag值
                        //还要判断是不是自己
                        if (MainActivity.getUser().getUser_id().equals(userList.get(position).getUser_id())) {
                            //表示是自己
                        } else {
                            if ("0".equals(userList.get(position).getAdded_flag())) {
                                showNonAdminDialog0(position);
                            } else if ("1".equals(userList.get(position).getAdded_flag())) {
                                showNonAdminDialog1(position);
                            }
                        }

                    }

                }
            }
        });

        btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(groupOwnerId)) {
                    leaveGroup();
                }

            }
        });
    }

    /**
     * 移除重复的好友
     *
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

    @Override
    public void requestData() {

    }

    public void getMembersList() {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", groupId);
        jsonParams.put("viewer_id", MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_GROUP_MEMBERS, params);

        new HttpTools(GroupSettingActivity.this).get(url, null,Tag, new HttpCallback() {
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

                userList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());

                if (userList != null && userList.size() > 0) {
                    for (UserEntity user : userList) {
                        addMemberList.add(user.getUser_id());
                    }
                   // removeDuplicate(userList);
                    groupSettingAdapter = new GroupSettingAdapter(GroupSettingActivity.this, R.layout.item_group_setting_members, userList);
                    lvMembers.setAdapter(groupSettingAdapter);

                    tvNumMembers.setText(userList.size() + getResources().getString(R.string.text_members));
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    GroupSettingAdapter groupSettingAdapter;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class GroupSettingAdapter extends ArrayAdapter {

        private int resourceId;
        private Context mContext;
        List<UserEntity> mUserList;

        public GroupSettingAdapter(Context context, int listViewResourceId, List userList) {
            super(context, listViewResourceId, userList);

            resourceId = listViewResourceId;
            mContext = context;
            mUserList = userList;
        }

        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            UserEntity userEntity = mUserList.get(position);

            ViewHolder viewHolder;
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder.cniMain = (CircularNetworkImage) convertView.findViewById(R.id.cni_main);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvAdmin = (TextView) convertView.findViewById(R.id.tv_admin);
            viewHolder.ivWaitting = (ImageView) convertView.findViewById(R.id.iv_right);

            VolleyUtil.initNetworkImageView(mContext, viewHolder.cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

            if (userEntity.getUser_id().equals(userEntity.getGroup_owner_id())) {
                viewHolder.tvName.setText(userEntity.getUser_given_name());
                viewHolder.tvAdmin.setVisibility(View.VISIBLE);
                viewHolder.ivWaitting.setVisibility(View.GONE);
                groupOwnerId = userEntity.getGroup_owner_id();
                if (MainActivity.getUser().getUser_id().equals(userEntity.getGroup_owner_id())) {
                    isAdmin = true;
                    rightButton.setVisibility(View.VISIBLE);
                } else {
                }
            } else {
                viewHolder.tvName.setText(userEntity.getUser_given_name());
            }

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

    //admin and addedflag = 1
    private void showAdminDialog1(final int position) {
        if (showAdminDialog1 != null && showAdminDialog1.isShowing()) {
            return;
        }

        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_group_info_options_admin1, null);

        showAdminDialog1 = new MyDialog(this, null, selectIntention);
        TextView tvRemoveUser = (TextView) selectIntention.findViewById(R.id.tv_remove_user);
        TextView tvFamilyProfile = (TextView) selectIntention.findViewById(R.id.tv_family_profile);
        TextView tvMessage = (TextView) selectIntention.findViewById(R.id.tv_message);

        tvRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo();

                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("user_id", userList.get(position).getUser_id());//MainActivity
                final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

                requestInfo.url = String.format(Constant.API_GROUP_REMOVE_MEMBERS, groupId);
                requestInfo.jsonParam = jsonParamsString;

                new HttpTools(GroupSettingActivity.this).put(requestInfo,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResult(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (("200").equals(jsonObject.getString("response_status_code"))) {
                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_remove), Toast.LENGTH_SHORT).show();//成功
                                showAdminDialog1.dismiss();
                                getMembersList();

                            } else {
                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_fail_remove), Toast.LENGTH_SHORT).show();//成功
                                showAdminDialog1.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        showAdminDialog1.dismiss();
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


        tvFamilyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(GroupSettingActivity.this, FamilyProfileActivity.class);
                intent1.putExtra("member_id", userList.get(position).getUser_id());
                startActivity(intent1);
                showAdminDialog1.dismiss();
            }
        });

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(GroupSettingActivity.this, MessageChatActivity.class);
                //intent2.putExtra("userEntity", userList.get(position));
                intent2.putExtra("groupId", userList.get(position).getGroup_id());
                intent2.putExtra("titleName", userList.get(position).getUser_given_name());
                intent2.putExtra("type", 0);
                startActivity(intent2);
                showAdminDialog1.dismiss();
            }
        });
        if (!showAdminDialog1.isShowing())
            showAdminDialog1.show();
    }

    //admin and addedflag = 0

    /**
     * 删除会员
     *
     * @param position
     */
    private void showAdminDialog0(final int position) {
        if (showAdminDialog0 != null && showAdminDialog0.isShowing()) {
            return;
        }
        if (position > userList.size()) {
            return;
        }
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_group_info_options_admin0, null);

        showAdminDialog0 = new MyDialog(this, null, selectIntention);

        TextView tvRemoveUser = (TextView) selectIntention.findViewById(R.id.tv_remove_user);
        TextView tvAdd = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);

        tvRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo();
                if (position > userList.size()) {
                    showAdminDialog0.dismiss();
                }
                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("user_id", userList.get(position).getUser_id());//MainActivity
                final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                requestInfo.url = String.format(Constant.API_GROUP_REMOVE_MEMBERS, groupId);
                requestInfo.jsonParam = jsonParamsString;
                new HttpTools(GroupSettingActivity.this).put(requestInfo,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResult(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (("200").equals(jsonObject.getString("response_status_code"))) {
                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_remove), Toast.LENGTH_SHORT).show();
                                showAdminDialog0.dismiss();
                                getMembersList();
                            } else {
                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_fail_remove), Toast.LENGTH_SHORT).show();
                                showAdminDialog0.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        showAdminDialog0.dismiss();
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

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminDialog0.dismiss();
                getMemberType(position);
            }
        });
        if (!showAdminDialog0.isShowing())
            showAdminDialog0.show();
    }

    private void showNonAdminDialog1(final int position) {
        if (showNonAdminDialog1 != null && showNonAdminDialog1.isShowing()) {
            return;
        }
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_group_info_options_non_admin1, null);

        showNonAdminDialog1 = new MyDialog(this, null, selectIntention);

        TextView tvFamilyProfile = (TextView) selectIntention.findViewById(R.id.tv_family_profile);
        TextView tvMessage = (TextView) selectIntention.findViewById(R.id.tv_message);

        tvFamilyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(GroupSettingActivity.this, FamilyProfileActivity.class);
                intent1.putExtra("member_id", userList.get(position).getUser_id());
                startActivity(intent1);
                showNonAdminDialog1.dismiss();
            }
        });

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(GroupSettingActivity.this, MessageChatActivity.class);
                //intent2.putExtra("userEntity", userList.get(position));
                intent2.putExtra("type", 0);
                intent2.putExtra("groupId", userList.get(position).getGroup_id());
                intent2.putExtra("titleName", userList.get(position).getUser_given_name());
                startActivity(intent2);
                showNonAdminDialog1.dismiss();
            }
        });
        if (!showNonAdminDialog1.isShowing())
            showNonAdminDialog1.show();
    }

    //non admin and addedflag = 0
    private void showNonAdminDialog0(final int position) {
        if (showNonAdminDialog0 != null && showNonAdminDialog0.isShowing()) {
            return;
        }
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_group_info_options_non_admin0, null);
        showNonAdminDialog0 = new MyDialog(this, null, selectIntention);
        TextView tvAdd = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNonAdminDialog0.dismiss();
                getMemberType(position);
            }
        });
        if (!showNonAdminDialog0.isShowing())
            showNonAdminDialog0.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                        addGroupMember(gson.toJson(addMemberList));
                    }

                    break;
            }
        }
    };

    public void getSelectMembersList(String groupIdList) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("group_list", groupIdList);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_GROUP_MEMBERS, params);
        new HttpTools(mContext).get(url, null,Tag, new HttpCallback() {
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
                Toast.makeText(mContext, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case GET_MEMBERS:
                    if (addMemberList != null && addMemberList.size() > 0) {
                        addMemberList.clear();
                    }
                    String members = data.getStringExtra("members_data");
                    groupData = data.getStringExtra("groups_data");
                    List<UserEntity> userEntityList = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    if (null != userEntityList && userEntityList.size() > 0) {
                        for (UserEntity user : userEntityList) {
                            String userId = user.getUser_id();
                            if (!memberList.contains(userId)) {
                                addMemberList.add(user.getUser_id());
                            }
                        }
                    }

                    List<FamilyGroupEntity> groupEntityList = null;
                    if (null != groupData) {
                        groupEntityList = new GsonBuilder().create().fromJson(groupData, new TypeToken<ArrayList<FamilyGroupEntity>>() {
                        }.getType());
                    }
                    if (groupEntityList != null && groupEntityList.size() > 0) {
                        familyGroupEntityList.addAll(groupEntityList);
                        List<String> groupIdList = new ArrayList<>();
                        for (FamilyGroupEntity familyGroupEntity : groupEntityList) {
                            groupIdList.add(familyGroupEntity.getGroup_id());
                        }
//                        if (addMemberList.size() > 0) {
//                            memberList.addAll(addMemberList);
//                        }
                        getSelectMembersList(new Gson().toJson(groupIdList));
                    } else {

                        if (addMemberList.size() > 0) {
//                        removeDuplicate(userList);
                            addGroupMember(gson.toJson(addMemberList));
//                            getMembersList();
                        }
                    }

                    break;

                /** christopher begin */
                case SET_GROUP_PIC_NAME:
                    String newGroupName = data.getStringExtra("groupName");
                    Intent intent = new Intent();
                    intent.putExtra("groupName",newGroupName);
                    setResult(RESULT_OK, intent);
                    finish();
//                    tvName.setText(data.getStringExtra("groupName"));
//                    VolleyUtil.initNetworkImageView(GroupSettingActivity.this, cniMain, String.format(Constant.API_GET_GROUP_PHOTO,  groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(3*1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                    break;
                /** christopher end */


                case GET_RELATIONSHIP:
                    response_relationship = data.getStringExtra("relationship");
                    setRelationship(positionEnd);
                    break;
            }
        }
    }

    public void addGroupMember(final String strGroupMembers) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("group_id", groupId);
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("query_on", "addGroupMember");
        params.put("group_members", strGroupMembers);

        new HttpTools(GroupSettingActivity.this).post(Constant.API_GROUP_ADD_MEMBERS, params,Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success), Toast.LENGTH_SHORT).show();
                getMembersList();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            Intent intent = new Intent();
            intent.putExtra("group_name", tvName.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.dispatchKeyEvent(event);
    }


    MyDialog leaveGroupAlertDialog;

    private void leaveGroup() {
        leaveGroupAlertDialog = new MyDialog(this, null, getResources().getString(R.string.text_leave_group_sure));

        leaveGroupAlertDialog.setButtonAccept(getResources().getString(R.string.text_dialog_yes), new View.OnClickListener() {
            /**
             * end
             */
            @Override
            public void onClick(View v) {
                leaveGroupAlertDialog.dismiss();

                RequestInfo requestInfo = new RequestInfo();
                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("group_id", groupId);
                jsonParams.put("group_owner_id", groupOwnerId);
                jsonParams.put("group_user_default", "0");
                jsonParams.put("query_on", "exitGroup");
                jsonParams.put("user_id", MainActivity.getUser().getUser_id());
                final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                requestInfo.url = String.format(Constant.API_LEAVE_GROUP, groupId);
                requestInfo.jsonParam = jsonParamsString;

                new HttpTools(GroupSettingActivity.this).put(requestInfo,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResult(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (("200").equals(jsonObject.getString("response_status_code"))) {
                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_leave_group), Toast.LENGTH_SHORT).show();//成功
                                Intent intent = new Intent(GroupSettingActivity.this, MainActivity.class);
                                intent.putExtra("jumpIndex", 1);
                                startActivity(intent);
                                //finish();
                            } else {
                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_fail_leave_group), Toast.LENGTH_SHORT).show();//失败
                            }
                        } catch (JSONException e) {
                            Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
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
        leaveGroupAlertDialog.setButtonCancel(this.getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroupAlertDialog.dismiss();

            }
        });
        if (!leaveGroupAlertDialog.isShowing()) {
            leaveGroupAlertDialog.show();
        }

    }


    String response_type;
    String response_relationship;
    int positionEnd;

    public void getMemberType(final int position) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.params = null;
        requestInfo.url = String.format(Constant.API_GET_MEMBER_TYPE, MainActivity.getUser().getUser_id(), userList.get(position).getUser_id());
        new HttpTools(this).get(requestInfo,Tag, new HttpCallback() {
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
                        response_relationship = jsonObject.getString("response_relationship");
                        if ("Resend".equals(response_type)) {
                            Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_resend), Toast.LENGTH_SHORT).show();
                        } else if ("Request".equals(response_type)) {
                            Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_request), Toast.LENGTH_SHORT).show();
                        } else if ("Accept".equals(response_type)) {
                            Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_accept), Toast.LENGTH_SHORT).show();
                        }
                        if (TextUtils.isEmpty(response_relationship))//关系为空时, 跳转到选择界面
                        {
                            positionEnd = position;
                            startActivityForResult(new Intent(GroupSettingActivity.this, RelationshipActivity.class), GET_RELATIONSHIP);

                        }
                    } else {
                        //失败
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

    public void setRelationship(int position) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("member_id", userList.get(position).getUser_id());
        params.put("action_type", response_type);
        params.put("user_relationship_name", response_relationship);

        new HttpTools(this).post(Constant.API_SET_RELATIONSHIP, params,Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_set_relationship), Toast.LENGTH_SHORT).show();//成功
                        getMembersList();

                    } else {

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

}
