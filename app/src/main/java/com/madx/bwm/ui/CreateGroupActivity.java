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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
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
import java.util.List;


public class CreateGroupActivity extends BaseActivity {

    List<UserEntity> userList  = new ArrayList();
    List<GroupEntity> groupList = new ArrayList();

    List<UserEntity> searchUserList = new ArrayList<>();//好友
    List<GroupEntity> searchGroupList = new ArrayList<>();//群组

    ImageButton mTop;

    Gson gson = new Gson();
    List memberList = new ArrayList();//要添加的成员user_id，需要变成json作为一个接口变量，传到CreateGroupDialogActivity创建群组

    GridView mGridView;
    Boolean isSearch = false;
    List data = new ArrayList<>();

    EditText etSearch;

    private List<String> checkItem = new ArrayList();

    ProgressBarCircularIndeterminate progressBar;

    public static CreateGroupActivity instance;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
        rightButton.setImageResource(R.drawable.btn_done);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_message_creategroup);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_create_group;
    }

    @Override
    protected void titleRightEvent() {
        if ((data.size() > 0) && data !=null)
        {
            Intent intent = new Intent(CreateGroupActivity.this, CreateGroupDialogActivity.class);
            intent.putExtra("members_json", gson.toJson(data));

            startActivity(intent);
        }
        else
        {
            Toast.makeText(CreateGroupActivity.this, "You didn't choose any members.", Toast.LENGTH_SHORT).show();//成功
        }

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }



    @Override
    public void initView() {
        instance = this;

        mGridView = (GridView)findViewById(R.id.creategroup_gridView);

        mTop = getViewById(R.id.ib_top);

        etSearch = getViewById(R.id.et_search);

        progressBar = getViewById(R.id.gv_progress_bar);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSearch)
                {
                    if ("0".equals(searchUserList.get(position).getFam_accept_flag()))
                    {
                        //不是好友,不做任何处理
                        Toast.makeText(CreateGroupActivity.this, "Awaiting for approval from member.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i("","check------" +position);
                    ((CheckBox)view.findViewById(R.id.creategroup_image_right)).toggle();
                    if ( ((CheckBox)view.findViewById(R.id.creategroup_image_right)).isChecked())
                    {
                        data.add(searchUserList.get(position).getUser_id().toString());
                        checkItem.add(searchUserList.get(position).getUser_id());
                    }
                    else
                    {
                        data.remove(searchUserList.get(position).getUser_id().toString());
                        checkItem.remove(searchUserList.get(position).getUser_id());
                    }
                }
                else
                {
                    if ("0".equals(userList.get(position).getFam_accept_flag()))
                    {
                        //不是好友,不做任何处理
                        Toast.makeText(CreateGroupActivity.this, "Awaiting for approval from member.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i("","check------" +position);
                    ((CheckBox)view.findViewById(R.id.creategroup_image_right)).toggle();
                    if ( ((CheckBox)view.findViewById(R.id.creategroup_image_right)).isChecked())
                    {
                        data.add(userList.get(position).getUser_id().toString());
                        checkItem.add(userList.get(position).getUser_id());
                    }
                    else
                    {
                        data.remove(userList.get(position).getUser_id().toString());
                        checkItem.remove(userList.get(position).getUser_id());
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
                    CreateGroupAdapter createGroupAdapter1 = new CreateGroupAdapter(CreateGroupActivity.this, R.layout.gridview_item_for_creategroup, userList, groupList);
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

                    for(int j = 0; j < userList.size(); j++)
                    {
                        if (userList.get(j).getUser_given_name().toLowerCase().contains(etSearch.getText().toString().toLowerCase()))
                        {
                            searchUserList.add(userList.get(j));
                        }

                    }

                    CreateGroupAdapter createGroupAdapter2 = new CreateGroupAdapter(CreateGroupActivity.this, R.layout.gridview_item_for_creategroup, searchUserList, searchGroupList);
                    mGridView.setAdapter(createGroupAdapter2);
                    isSearch = true;
                }

            }
        });
    }

    @Override
    public void requestData() {

        new HttpTools(CreateGroupActivity.this).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null, new HttpCallback() {
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

                    userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {}.getType());
                    groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {}.getType());

                    CreateGroupAdapter createGroupAdapter = new CreateGroupAdapter(CreateGroupActivity.this, R.layout.gridview_item_for_creategroup, userList, groupList);

                    mGridView.setAdapter(createGroupAdapter);

                    createGroupAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e)
                {
                    MessageUtil.showMessage(CreateGroupActivity.this, getResources().getString(R.string.text_error));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(CreateGroupActivity.this, getResources().getString(R.string.text_error));
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
//                    userList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<UserEntity>>() {}.getType());
//                    groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<GroupEntity>>() {}.getType());
//
//                    CreateGroupAdapter createGroupAdapter = new CreateGroupAdapter(CreateGroupActivity.this, R.layout.gridview_item_for_creategroup, userList, groupList);
//
//                    mGridView.setAdapter(createGroupAdapter);
//
//                    createGroupAdapter.notifyDataSetChanged();
//
//                    progressBar.setVisibility(View.GONE);
//
//                } catch (JSONException e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("", "error=====" + error.getMessage());
//            }
//        });
//
//        VolleyUtil.addRequest2Queue(this.getApplicationContext(), stringRequest, "");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    public class CreateGroupAdapter extends ArrayAdapter<UserEntity>
    {
        private int resourceId;
        private Context mContext;
        List<UserEntity> mUserList = new ArrayList<>();;
        List<GroupEntity> mGroupList;

        public CreateGroupAdapter(Context context, int gridViewResourceId, List<UserEntity> userList, List<GroupEntity> groupList) {
            super(context, gridViewResourceId);
            resourceId = gridViewResourceId;
            mContext = context;
            mUserList.addAll(userList);
            mGroupList = groupList;
        }


        @Override
        public int getCount() {
            return mUserList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

//            if(convertView == null)
//            {
//                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
//
//                viewHolder = new ViewHolder();
//
//                viewHolder.imageMain = (CircularNetworkImage)convertView.findViewById(R.id.creategroup_image_main);
//                viewHolder.imageRight = (CheckBox)convertView.findViewById(R.id.creategroup_image_right);
//                viewHolder.textName = (TextView)convertView.findViewById(R.id.creategroup_name);
//
//                convertView.setTag(viewHolder);
//            }
//            else
//            {
//                viewHolder = (ViewHolder)convertView.getTag();
//            }
//
//            UserEntity userEntity = mUserList.get(position);
//            Log.i("","----------" + position);
//            viewHolder.textName.setText(userEntity.getUser_given_name());
//            VolleyUtil.initNetworkImageView(mContext,viewHolder.imageMain,String.format(Constant.API_GET_PIC,Constant.Module_profile, userEntity.getUser_id(),"profile"),R.drawable.network_image_default, R.drawable.network_image_default);

            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();

            viewHolder.imageMain = (CircularNetworkImage)convertView.findViewById(R.id.creategroup_image_main);
            viewHolder.imageRight = (CheckBox)convertView.findViewById(R.id.creategroup_image_right);
            viewHolder.textName = (TextView)convertView.findViewById(R.id.creategroup_name);

            UserEntity userEntity = mUserList.get(position);

            //
            if ("0".equals(userEntity.getFam_accept_flag()))
            {
                viewHolder.imageTop = (ImageView)convertView.findViewById(R.id.creategroup_image_top);
                viewHolder.imageTop.setVisibility(View.VISIBLE);
                viewHolder.imageRight.setVisibility(View.GONE);
            }

            if (checkItem.contains(userEntity.getUser_id()))
            {
                viewHolder.textName.setText(userEntity.getUser_given_name());
                viewHolder.imageRight.setChecked(true);
                VolleyUtil.initNetworkImageView(mContext,viewHolder.imageMain,String.format(Constant.API_GET_PIC,Constant.Module_profile, userEntity.getUser_id(),"profile"),R.drawable.network_image_default, R.drawable.network_image_default);

            }
            else
            {
                viewHolder.textName.setText(userEntity.getUser_given_name());
                viewHolder.imageRight.setChecked(false);
                VolleyUtil.initNetworkImageView(mContext,viewHolder.imageMain,String.format(Constant.API_GET_PIC,Constant.Module_profile, userEntity.getUser_id(),"profile"),R.drawable.network_image_default, R.drawable.network_image_default);
            }

            return convertView;
        }

        class ViewHolder
        {
            CircularNetworkImage imageMain;
            CheckBox imageRight;
            ImageView imageTop;
            TextView textName;
        }


    }

}
