package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SelectPeopleActivity extends BaseActivity {

    List<UserEntity> userList;//最原始用户数据
    List<GroupEntity> groupList;//群组数据 这里没用到

    List<UserEntity> searchUserList = new ArrayList<>();//好友
    List<GroupEntity> searchGroupList = new ArrayList<>();;//群组

    LinkedList<UserEntity> ll = new LinkedList<>();//全部数据+排序的

    Gson gson = new Gson();
    List<UserEntity> inList;//传进进来的date，转换为List后的

    ImageButton mTop;
    ScrollView mSv;

    String data;//传进来的
    int type;//传进来的

    EditText etSearch;
    Boolean isSearch = false;

    ProgressDialog progressDialog;


    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
        rightButton.setImageResource(R.drawable.btn_done);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_select_members));
    }

    @Override
    public int getLayout() {
        return R.layout.activity_select_people;
    }

//    @Override
//    protected void titleLeftEvent() {
//        Intent intent = new Intent();
//        Gson gson = new Gson();
//        intent.putExtra("members_data", gson.toJson(inList));
//        setResult(RESULT_OK, intent);
//        super.titleLeftEvent();
//    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent();
        Gson gson = new Gson();

        intent.putExtra("members_data", gson.toJson(inList));

        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    GridView mGridView;
    private List<String> checkItem = new ArrayList();

    @Override
    public void initView() {

        progressDialog = new ProgressDialog(this,getResources().getString(R.string.text_dialog_loading));
        Intent intent = getIntent();

        data = intent.getStringExtra("members_data");
        type = intent.getIntExtra("type", -1);

        inList = gson.fromJson(data, new TypeToken<ArrayList<UserEntity>>() {
        }.getType());

        if (inList == null) {
            inList = new ArrayList<>();
        }

        mGridView = (GridView) findViewById(R.id.creategroup_gridView);

        etSearch = getViewById(R.id.et_search);

        mTop = getViewById(R.id.ib_top);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSearch)
                {
                    if ("0".equals(searchUserList.get(position).getFam_accept_flag()))
                    {
                        //不是好友,不做任何处理
                        Toast.makeText(SelectPeopleActivity.this, getString(R.string.text_awaiting_approval_member), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i("", "check------" + position);
                    ((CheckBox) view.findViewById(R.id.creategroup_image_right)).toggle();
                    if (((CheckBox) view.findViewById(R.id.creategroup_image_right)).isChecked()) {
                        inList.add(searchUserList.get(position));
                        checkItem.add(searchUserList.get(position).getUser_id());//保存每个item唯一标示，记录那个被选中

                    } else {
                        for (int i = 0; i < inList.size(); i++) {
                            if (inList.get(i).getUser_id().equals(searchUserList.get(position).getUser_id())) {
                                checkItem.remove(searchUserList.get(position).getUser_id());
                                inList.remove(i);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if ("0".equals(ll.get(position).getFam_accept_flag()))
                    {
                        //不是好友,不做任何处理
                        Toast.makeText(SelectPeopleActivity.this,getString(R.string.text_awaiting_approval_member), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i("", "check------" + position);
                    ((CheckBox) view.findViewById(R.id.creategroup_image_right)).toggle();
                    if (((CheckBox) view.findViewById(R.id.creategroup_image_right)).isChecked()) {
                        inList.add(ll.get(position));
                        checkItem.add(ll.get(position).getUser_id());//保存每个item唯一标示，记录那个被选中

                    } else {
                        for (int i = 0; i < inList.size(); i++) {
                            if (inList.get(i).getUser_id().equals(ll.get(position).getUser_id())) {
                                checkItem.remove(ll.get(position).getUser_id());
                                inList.remove(i);
                                break;
                            }
                        }
                    }
                }



            }
        });

        mTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGridView.setSelection(0);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(etSearch.getText()))
                {
                    CreateGroupAdapter createGroupAdapter1 = new CreateGroupAdapter(SelectPeopleActivity.this, R.layout.gridview_item_for_creategroup, ll);
                    mGridView.setAdapter(createGroupAdapter1);
                    isSearch = false;
                }
                else
                {
                    searchGroupList.clear();
                    searchUserList.clear();

                    for (int i = 0; i < groupList.size(); i++) {
                        if (groupList.get(i).getGroup_name().toLowerCase().contains(etSearch.getText().toString().toLowerCase()))
                        {
                            searchGroupList.add(groupList.get(i));
                        }
                    }

                    for(int j = 0; j < ll.size(); j++)
                    {
                        if (ll.get(j).getUser_given_name().toLowerCase().contains(etSearch.getText().toString().toLowerCase()))
                        {
                            searchUserList.add(ll.get(j));
                        }
                    }

                    CreateGroupAdapter createGroupAdapter2 = new CreateGroupAdapter(SelectPeopleActivity.this, R.layout.gridview_item_for_creategroup, searchUserList);
                    mGridView.setAdapter(createGroupAdapter2);
                    isSearch = true;
                }

            }
        });


    }



    @Override
    public void requestData() {

        progressDialog.show();

        new HttpTools(SelectPeopleActivity.this).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()),null,new HttpCallback() {
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

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {
                    }.getType());

                    if (type == 0) { //创建 已选上的人还会显示
                        ll = initList();//排序
                        for(int i = 0;i<inList.size();i++){
                            checkItem.add(inList.get(i).getUser_id());
                        }

                    } else if (type == 1) { //update, 已选上的人不显示 隐藏
                        for (int i = 0; i < inList.size(); i++) {
                            for (int j = 0; j < userList.size(); j++) {
                                if (!TextUtils.isEmpty(userList.get(j).getUser_id()))
                                {
                                    if (userList.get(j).getUser_id().equals(inList.get(i).getUser_id())) {
                                        userList.remove(j);
                                        break;
                                    }
                                }
                            }
                        }
                        inList.clear();
                        ll.addAll(userList);
                    } else {
                        finish();
                    }

                    CreateGroupAdapter createGroupAdapter = new CreateGroupAdapter(SelectPeopleActivity.this, R.layout.gridview_item_for_creategroup, ll);

                    mGridView.setAdapter(createGroupAdapter);

                    createGroupAdapter.notifyDataSetChanged();

                    rightButton.setVisibility(View.VISIBLE);

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    MessageUtil.showMessage(SelectPeopleActivity.this, R.string.msg_action_failed);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(SelectPeopleActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });






//        StringRequest stringRequest = new StringRequest(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                GsonBuilder gsonb = new GsonBuilder();
//
//                Gson gson = gsonb.create();
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {
//                    }.getType());
//                    groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {
//                    }.getType());
//
//                    if (type == 0) { //创建 已选上的人还会显示
//                        ll = initList();//排序
//                        for(int i = 0;i<inList.size();i++){
//                            checkItem.add(inList.get(i).getUser_id());
//                        }
//
//                    } else if (type == 1) { //update, 已选上的人不显示 隐藏
//                        for (int i = 0; i < inList.size(); i++) {
//                            for (int j = 0; j < userList.size(); j++) {
//                                if (!TextUtils.isEmpty(userList.get(j).getUser_id()))
//                                {
//                                    if (userList.get(j).getUser_id().equals(inList.get(i).getUser_id())) {
//                                        userList.remove(j);
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        inList.clear();
//                        ll.addAll(userList);
//                    } else {
//                        finish();
//                    }
//
//                    CreateGroupAdapter createGroupAdapter = new CreateGroupAdapter(SelectPeopleActivity.this, R.layout.gridview_item_for_creategroup, ll);
//
//                    mGridView.setAdapter(createGroupAdapter);
//
//                    createGroupAdapter.notifyDataSetChanged();
//
//                    rightButton.setVisibility(View.VISIBLE);
//
//                    progressDialog.dismiss();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//                Log.i("", "error=====" + error.getMessage());
//            }
//        });
//
//        VolleyUtil.addRequest2Queue(this.getApplicationContext(), stringRequest, "");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class CreateGroupAdapter extends ArrayAdapter<UserEntity> {
        private int resourceId;
        private Context mContext;
        List<UserEntity> mUserList;
        List<UserEntity> mInList = new ArrayList<>();


        public CreateGroupAdapter(Context context, int gridViewResourceId, List<UserEntity> userList) {
            super(context, gridViewResourceId);
            resourceId = gridViewResourceId;
            mContext = context;
            mUserList = userList;
            mInList.addAll(inList);

        }


        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
//
//                viewHolder = new ViewHolder();
//
//                viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.creategroup_image_main);
//                viewHolder.imageRight = (CheckBox) convertView.findViewById(R.id.creategroup_image_right);
//                viewHolder.textName = (TextView) convertView.findViewById(R.id.creategroup_name);
//
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            UserEntity userEntity = mUserList.get(position);
//
//            if (position < mInList.size()) {
//                Log.i("", "----------" + position);
//                viewHolder.textName.setText(userEntity.getUser_given_name());
//                viewHolder.imageRight.setChecked(true);
//                VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PIC, Constant.Module_profile, userEntity.getUser_id(), "profile"), R.drawable.network_image_default, R.drawable.network_image_default);
//            } else {
//                viewHolder.textName.setText(userEntity.getUser_given_name());
//                viewHolder.imageRight.setChecked(false);
//                VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PIC, Constant.Module_profile, userEntity.getUser_id(), "profile"), R.drawable.network_image_default, R.drawable.network_image_default);
//            }


            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();

            viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.creategroup_image_main);
            viewHolder.imageRight = (CheckBox) convertView.findViewById(R.id.creategroup_image_right);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.creategroup_name);

            UserEntity userEntity = mUserList.get(position);

            if ("0".equals(userEntity.getFam_accept_flag()))
            {
                viewHolder.imageTop = (ImageView)convertView.findViewById(R.id.creategroup_image_top);
                viewHolder.imageTop.setVisibility(View.VISIBLE);
                viewHolder.imageRight.setVisibility(View.GONE);
            }


            if (checkItem.contains(userEntity.getUser_id())) {
                viewHolder.textName.setText(userEntity.getUser_given_name());
                viewHolder.imageRight.setChecked(true);
                VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PIC, Constant.Module_profile, userEntity.getUser_id(), "profile"), R.drawable.network_image_default, R.drawable.network_image_default);
            }else {
                viewHolder.textName.setText(userEntity.getUser_given_name());
                viewHolder.imageRight.setChecked(false);
                VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PIC, Constant.Module_profile, userEntity.getUser_id(), "profile"), R.drawable.network_image_default, R.drawable.network_image_default);
            }


            return convertView;

            /*MyFamilyUserEntity myFamilyUserEntity = mUserList.get(position);

            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            Log.i("","----------" + position);
            CircularNetworkImage circularNetworkImage = (CircularNetworkImage)view.findViewById(R.id.creategroup_image_main);
            CheckBox checkBox = (CheckBox)view.findViewById(R.id.creategroup_image_right);
            TextView textView = (TextView)view.findViewById(R.id.creategroup_name);
            VolleyUtil.initNetworkImageView(mContext,circularNetworkImage,String.format(Constant.API_GET_PIC,Constant.Module_profile, myFamilyUserEntity.getUser_id(),"profile"),R.drawable.network_image_default, R.drawable.network_image_default);
            textView.setText(myFamilyUserEntity.getUser_given_name());

            return view;*/


        }

        class ViewHolder {
            CircularNetworkImage imageMain;
            CheckBox imageRight;
            ImageView imageTop;
            TextView textName;
        }
    }


    LinkedList<UserEntity> initList() {

        LinkedList<UserEntity> list3 = new LinkedList<>();//排好的，要传入adapter

        if (userList != null) {
            for (int i = 0; i < userList.size(); i++) {
                boolean add2first = false;
                if (inList != null) {
                    for (int j = 0; j < inList.size(); j++) {
                        if (userList.get(i).getUser_id().equals(inList.get(j).getUser_id())) {
                            add2first = true;
                            break;
                        } else {
                            add2first = false;
                        }
                    }
                }
                if (add2first) {
                    list3.addFirst(userList.get(i));
                } else {
                    list3.addLast(userList.get(i));
                }
            }
        }


        return list3;
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    Intent intent = new Intent();
//                    Gson gson = new Gson();
//
//                    intent.putExtra("members_data", gson.toJson(inList));
//
//                    setResult(RESULT_OK, intent);
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }

}
