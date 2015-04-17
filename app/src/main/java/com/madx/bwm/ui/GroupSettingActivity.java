package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.PhotoEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.widget.CircularNetworkImage;
import com.madx.bwm.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupSettingActivity extends BaseActivity {


    private CardView llSetting;
    private CircularNetworkImage cniMain;
    private TextView tvName;
    private TextView tvNumMembers;

    private ListView lvMembers;

    private Button btnLeaveGroup;

    private GroupEntity groupEntity = new GroupEntity();//传进来的

    private List<UserEntity> userList;

    private Dialog showAdminDialog1;
    private Dialog showAdminDialog0;

    private Dialog showNonAdminDialog1;
    private Dialog showNonAdminDialog0;

    private boolean isAdmin;

    private String groupId;//当前群Id
    private String groupName;//当前群名字
    private String groupOwnerId;

    Gson gson = new Gson();

    public List<UserEntity> members_data = new ArrayList<UserEntity>();//传到选人的界面上去的

    private final static int GET_MEMBERS = 2;
    private final static int SET_GROUP_PIC_NAME = 3;
    private final static int GET_RELATIONSHIP = 4;

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
        intent.putExtra("group_name", tvName.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
        rightButton.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void setTitle() {
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_group_info));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {
        //是否会出现list数据还没下载完就点击进去了。此时不是最新数据。这里需要怎么处理呢？
        Intent intent = new Intent(GroupSettingActivity.this, SelectPeopleActivity.class);

        intent.putExtra("type", 1);
        intent.putExtra("members_data", new Gson().toJson(userList));

        startActivityForResult(intent, GET_MEMBERS);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        groupEntity = (GroupEntity) getIntent().getExtras().getSerializable("groupEntity");
        groupId = groupEntity.getGroup_id();

        llSetting = getViewById(R.id.ll_setting);

        cniMain = getViewById(R.id.cni_main);
        tvName = getViewById(R.id.tv_group_name);
        tvNumMembers = getViewById(R.id.tv_num_members);

        lvMembers = getViewById(R.id.lv_members);

        btnLeaveGroup = getViewById(R.id.btn_leave_group);

        VolleyUtil.initNetworkImageView(this, cniMain, String.format(Constant.API_GET_GROUP_PHOTO,  groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName.setText(groupEntity.getGroup_name());
        groupName = groupEntity.getGroup_name();

        getMembersList();


        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettingActivity.this, ViewOriginalPicesActivity.class);

                ArrayList<PhotoEntity> datas = new ArrayList();

                PhotoEntity peData = new PhotoEntity();

                peData.setUser_id(groupEntity.getGroup_id());
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
                if (isAdmin)
                {
                    Intent intent = new Intent(GroupSettingActivity.this, GroupNameSettingActivity.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("groupName", tvName.getText().toString());
                    startActivityForResult(intent, SET_GROUP_PIC_NAME);
                }
                else
                {

                }
            }
        });




        lvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isAdmin)
                {
                    if (position == 0)
                    {
                        //群主点击群主
                    }
                    else
                    {
                        //群主点击群成员
                        //判断AddedFlag值
                        if ("0".equals(userList.get(position).getAdded_flag()))
                        {
                            showAdminDialog0(position);
                        }
                        else if ("1".equals(userList.get(position).getAdded_flag()))
                        {
                            showAdminDialog1(position);
                        }

                    }
                }
                else
                {
                    if (position == 0)
                    {
                        //用户成员点击群主
                        if ("0".equals(userList.get(position).getAdded_flag()))
                        {
                            showNonAdminDialog0(position);
                        }
                        else if ("1".equals(userList.get(position).getAdded_flag()))
                        {
                            showNonAdminDialog1(position);
                        }
                    }
                    else
                    {
                        //用户成员点击群成员
                        //判断AddedFlag值
                        //还要判断是不是自己
                        if (MainActivity.getUser().getUser_id().equals(userList.get(position).getUser_id()))
                        {
                            //表示是自己
                        }
                        else
                        {
                            if ("0".equals(userList.get(position).getAdded_flag()))
                            {
                                showNonAdminDialog0(position);
                            }
                            else if ("1".equals(userList.get(position).getAdded_flag()))
                            {
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
                if (!TextUtils.isEmpty(groupOwnerId))
                {
                    leaveGroup();
                }

            }
        });
    }

    @Override
    public void requestData() {

    }

    public void getMembersList()
    {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", groupEntity.getGroup_id());
        jsonParams.put("viewer_id", MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_GROUP_MEMBERS, params);

        new HttpTools(GroupSettingActivity.this).get(url, null, new HttpCallback() {
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

                userList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());

                if (userList != null)
                {
                    GroupSettingAdapter groupSettingAdapter = new GroupSettingAdapter(GroupSettingActivity.this, R.layout.item_group_setting_members, userList);

                    lvMembers.setAdapter(groupSettingAdapter);

                    tvNumMembers.setText(userList.size() + getResources().getString(R.string.text_members));

                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

















//        StringRequest srMembers = new StringRequest(url, new Response.Listener<String>() {
//
//            GsonBuilder gsonb = new GsonBuilder();
//
//            Gson gson = gsonb.create();
//
//            @Override
//            public void onResponse(String response) {
//
//                Log.d("","response" + response);
//
//                userList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());
//
//                if (userList != null)
//                {
//                    GroupSettingAdapter groupSettingAdapter = new GroupSettingAdapter(GroupSettingActivity.this, R.layout.item_group_setting_members, userList);
//
//                    lvMembers.setAdapter(groupSettingAdapter);
//                    /**
//                     * begin QK
//                     */
//                    tvNumMembers.setText(userList.size() + getResources().getString(R.string.text_members));
//                    /**
//                     * end
//                     */
//
//                }
//            }
//        },new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        VolleyUtil.addRequest2Queue(GroupSettingActivity.this, srMembers, "");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class GroupSettingAdapter extends ArrayAdapter
    {

        private int resourceId;
        private Context mContext;
        List<UserEntity> mUserList ;

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
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.tvAdmin = (TextView)convertView.findViewById(R.id.tv_admin);

            VolleyUtil.initNetworkImageView(mContext, viewHolder.cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

            if (userEntity.getUser_id().equals(userEntity.getGroup_owner_id()))
            {
                viewHolder.tvName.setText(userEntity.getUser_given_name());
                viewHolder.tvAdmin.setVisibility(View.VISIBLE);
                groupOwnerId = userEntity.getGroup_owner_id();
                if (MainActivity.getUser().getUser_id().equals(userEntity.getGroup_owner_id()))
                {
                    isAdmin = true;
                    rightButton.setVisibility(View.VISIBLE);
                }
                else
                {

                }
            }
            else
            {
                viewHolder.tvName.setText(userEntity.getUser_given_name());
            }

            return convertView;

        }

        class ViewHolder
        {
            CircularNetworkImage cniMain;
            TextView tvName;
            TextView tvAdmin;
        }
    }

    //admin and addedflag = 1
    private void showAdminDialog1(final int position)
    {
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

                new HttpTools(GroupSettingActivity.this).put(requestInfo, new HttpCallback() {
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
                            Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        showAdminDialog1.dismiss();
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });











//                StringRequest srRemoveMember = new StringRequest(Request.Method.PUT, String.format(Constant.API_GROUP_REMOVE_MEMBERS, groupId), new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if (("200").equals(jsonObject.getString("response_status_code"))) {
//                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_remove), Toast.LENGTH_SHORT).show();//成功
//                                showAdminDialog1.dismiss();
//                                getMembersList();
//
//                            } else {
//                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_fail_remove), Toast.LENGTH_SHORT).show();//成功
//                                showAdminDialog1.dismiss();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        showAdminDialog1.dismiss();
//                    }
//                }) {
//
//                    @Override
//                    public byte[] getBody() throws AuthFailureError {
//                        return jsonParamsString.getBytes();
//                    }
//
//                };
//                VolleyUtil.addRequest2Queue(GroupSettingActivity.this, srRemoveMember, "");
            }
        });


        tvFamilyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(GroupSettingActivity.this, FamilyProfileActivity.class);
                intent1.putExtra("member_id", userList.get(position).getUser_id());
                Log.d("", "------@@@-----" + userList.get(position).getUser_id());
                startActivity(intent1);
                showAdminDialog1.dismiss();
            }
        });

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(GroupSettingActivity.this, ChatActivity.class);
                intent2.putExtra("userEntity", userList.get(position));
                intent2.putExtra("type", 0);
                startActivity(intent2);
                showAdminDialog1.dismiss();
            }
        });
        showAdminDialog1.show();
    }




    //admin and addedflag = 0
    private void showAdminDialog0(final int position)
    {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_group_info_options_admin0, null);

        showAdminDialog0 = new MyDialog(this, null, selectIntention);

        TextView tvRemoveUser = (TextView) selectIntention.findViewById(R.id.tv_remove_user);
        TextView tvAdd = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);

        tvRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo();

                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("user_id", userList.get(position).getUser_id());//MainActivity
                final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
                requestInfo.url = String.format(Constant.API_GROUP_REMOVE_MEMBERS, groupId);
                requestInfo.jsonParam = jsonParamsString;


                new HttpTools(GroupSettingActivity.this).put(requestInfo, new HttpCallback() {
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
                                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_success_remove), Toast.LENGTH_SHORT).show();
                                showAdminDialog0.dismiss();
                                getMembersList();
                            } else {
                                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_fail_remove), Toast.LENGTH_SHORT).show();
                                showAdminDialog0.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        showAdminDialog0.dismiss();
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });



//                StringRequest srRemoveMember = new StringRequest(Request.Method.PUT, String.format(Constant.API_GROUP_REMOVE_MEMBERS, groupId), new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if (("200").equals(jsonObject.getString("response_status_code"))) {
//                                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_success_remove), Toast.LENGTH_SHORT).show();
//                                showAdminDialog0.dismiss();
//                                getMembersList();
//                            } else {
//                                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_fail_remove), Toast.LENGTH_SHORT).show();
//                                showAdminDialog0.dismiss();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        showAdminDialog0.dismiss();
//                    }
//                }) {
//
//                    @Override
//                    public byte[] getBody() throws AuthFailureError {
//                        return jsonParamsString.getBytes();
//                    }
//                };
//                VolleyUtil.addRequest2Queue(GroupSettingActivity.this, srRemoveMember, "");
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminDialog0.dismiss();
                getMemberType(position);
            }
        });

        showAdminDialog0.show();
    }




    //non admin and addedflag = 1
    private void showNonAdminDialog1(final int position)
    {
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
                Log.d("", "------@@@-----" + userList.get(position).getUser_id());
                startActivity(intent1);
                showNonAdminDialog1.dismiss();
            }
        });

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(GroupSettingActivity.this, ChatActivity.class);
                intent2.putExtra("userEntity", userList.get(position));
                intent2.putExtra("type", 0);
                startActivity(intent2);
                showNonAdminDialog1.dismiss();
            }
        });
        showNonAdminDialog1.show();
    }


    //non admin and addedflag = 0
    private void showNonAdminDialog0(final int position)
    {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_group_info_options_non_admin0, null);

        showNonAdminDialog0 = new MyDialog(this, null, selectIntention);

        TextView tvAdd = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                showNonAdminDialog0.dismiss();
                getMemberType(position);
            }
        });

        showNonAdminDialog0.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {

                case GET_MEMBERS:
                    String members = data.getStringExtra("members_data");
                    members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());

                    Log.d("","#######" + members);
                    Log.d("","#######------" + members_data.size());

                    List memberList = new ArrayList();//要添加的成员user_id，需要变成json作为一个接口变量

                    for (int i = 0; i < members_data.size(); i++)
                    {
                        memberList.add(members_data.get(i).getUser_id());
                    }

                    if (memberList.size() != 0)
                    {
                        Log.d("", "@@@@@@@@@" + gson.toJson(memberList));

                        addGroupMember(gson.toJson(memberList));
                    }
                    else
                    {

                    }

                    break;

                /** christopher begin */
                //TODO
                case SET_GROUP_PIC_NAME:
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

    public void addGroupMember(final String strGroupMembers)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("group_id", groupId);
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("query_on", "addGroupMember");
        params.put("group_members", strGroupMembers);

        new HttpTools(GroupSettingActivity.this).post(Constant.API_GROUP_ADD_MEMBERS, params, new HttpCallback() {
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


//
//        StringRequest stringRequestPost = new StringRequest(Request.Method.POST, Constant.API_GROUP_ADD_MEMBERS, new Response.Listener<String>() {
//
//            GsonBuilder gsonb = new GsonBuilder();
//
//            Gson gson = gsonb.create();
//
//            @Override
//            public void onResponse(String response) {
//
//                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success), Toast.LENGTH_SHORT).show();
//
//                getMembersList();
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("group_id", groupId);
//                params.put("group_owner_id", MainActivity.getUser().getUser_id());
//                params.put("query_on", "addGroupMember");
//                params.put("group_members", strGroupMembers);
//
//                return params;
//            }
//        };
//        VolleyUtil.addRequest2Queue(GroupSettingActivity.this, stringRequestPost, "");
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
        /**
         * begin QK
         */
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

                new HttpTools(GroupSettingActivity.this).put(requestInfo, new HttpCallback() {
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
                                finish();
                            } else {
                                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_fail_leave_group), Toast.LENGTH_SHORT).show();//失败
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







//                StringRequest srUpdateGroupName = new StringRequest(Request.Method.PUT, String.format(Constant.API_LEAVE_GROUP, groupId), new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("","#########" + response);
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if (("200").equals(jsonObject.getString("response_status_code"))) {
//                                /**
//                                 * begin QK
//                                 */
//                                Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_leave_group), Toast.LENGTH_SHORT).show();//成功
//                                finish();
//                            } else {
//                                Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_fail_leave_group), Toast.LENGTH_SHORT).show();//失败
//                                /**
//                                 * end
//                                 */
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        /**
//                         * begin QK
//                         */
//                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        /**
//                         * end
//                         */
//                    }
//                }) {
//
//                    @Override
//                    public byte[] getBody() throws AuthFailureError {
//                        return jsonParamsString.getBytes();
//                    }
//                };
//                VolleyUtil.addRequest2Queue(GroupSettingActivity.this, srUpdateGroupName, "");
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

    public void getMemberType(final int position)
    {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.params=null;
        requestInfo.url = String.format(Constant.API_GET_MEMBER_TYPE, MainActivity.getUser().getUser_id(),userList.get(position).getUser_id());

        Log.d("","uuuuurl" + requestInfo.url);

        new HttpTools(this).get(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {

                Log.i("", "response" + string);

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("Success".equals(jsonObject.getString("response_status")))
                    {
                        response_type = jsonObject.getString("response_type");
                        response_relationship = jsonObject.getString("response_relationship");
                        Log.d("","type" + response_type);
                        /**
                         * begin QK
                         */
//                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_get_member_type), Toast.LENGTH_SHORT).show();//成功 需要不提示

                        if ("Resend".equals(response_type))
                        {
                            Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_success_resend) , Toast.LENGTH_SHORT).show();
                        }
                        else if ("Request".equals(response_type))
                        {
                            Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_success_request), Toast.LENGTH_SHORT).show();
                        }
                        else if ("Accept".equals(response_type))
                        {
                            Toast.makeText(GroupSettingActivity.this,getResources().getString(R.string.text_success_accept), Toast.LENGTH_SHORT).show();
                        }
                        /**
                         * end
                         */
                        if (TextUtils.isEmpty(response_relationship))//关系为空时, 跳转到选择界面
                        {
                            Log.d("","response_relationship----1" + response_relationship);
                            positionEnd = position;
                            startActivityForResult(new Intent(GroupSettingActivity.this, RelationshipActivity.class), GET_RELATIONSHIP);
                            Log.d("","response_relationship----2" + response_relationship);

                        }
                    }
                    else
                    {
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

    public void setRelationship(int position)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("member_id", userList.get(position).getUser_id());
        params.put("action_type", response_type);
        params.put("user_relationship_name", response_relationship);

        new HttpTools(this).post(Constant.API_SET_RELATIONSHIP, params, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {

                Log.i("", "response---setRelationship" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status")))
                    {
                        /**
                         * begin QK
                         */
                        Toast.makeText(GroupSettingActivity.this, getResources().getString(R.string.text_success_set_relationship), Toast.LENGTH_SHORT).show();//成功
                        /**
                         * end
                         */
                        getMembersList();

                    }
                    else
                    {

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
