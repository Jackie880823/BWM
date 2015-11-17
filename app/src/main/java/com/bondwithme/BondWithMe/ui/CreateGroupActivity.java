package com.bondwithme.BondWithMe.ui;

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
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.CircularProgress;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CreateGroupActivity extends BaseActivity {
    private static final String Tag = CreateGroupActivity.class.getSimpleName();
    private List<UserEntity> userList = new ArrayList();
    private List<GroupEntity> groupList = new ArrayList();
    private List<UserEntity> searchUserList = new ArrayList<>();//好友
    private List<GroupEntity> searchGroupList = new ArrayList<>();//群组
    private ImageButton mTop;
    private Gson gson = new Gson();
    private List memberList = new ArrayList();//要添加的成员user_id，需要变成json作为一个接口变量，传到CreateGroupDialogActivity创建群组

    private GridView mGridView;
    private Boolean isSearch = false;
    private List data = new ArrayList<>();
    private EditText etSearch;
    private List<String> checkItem = new ArrayList();

    private CircularProgress progressBar;

    public static CreateGroupActivity instance;
    private Context mContext;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
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
        if (data != null && data.size() > 0) {
            Intent intent = new Intent(CreateGroupActivity.this, CreateGroupDialogActivity.class);
            intent.putExtra("members_json", gson.toJson(data));
            intent.putExtra("memberLength", data.size());
            startActivity(intent);
        } else {
            showNoFriendDialog();
        }

    }

    private void showNoFriendDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
        final Dialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
        tv_no_member.setText(getString(R.string.text_create_group_members_least_two));
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }


    @Override
    public void initView() {
        mContext = this;
        instance = this;
        mGridView = (GridView) findViewById(R.id.creategroup_gridView);
        mTop = getViewById(R.id.ib_top);
        etSearch = getViewById(R.id.et_search);
        progressBar = getViewById(R.id.gv_progress_bar);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSearch) {
                    if ("0".equals(searchUserList.get(position).getFam_accept_flag())) {
                        //不是好友,不做任何处理
                        Toast.makeText(CreateGroupActivity.this, "Awaiting for approval from member.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i("", "check------" + position);
                    ((CheckBox) view.findViewById(R.id.creategroup_image_right)).toggle();
                    if (((CheckBox) view.findViewById(R.id.creategroup_image_right)).isChecked()) {
                        data.add(searchUserList.get(position).getUser_id().toString());
                        checkItem.add(searchUserList.get(position).getUser_id());
                    } else {
                        data.remove(searchUserList.get(position).getUser_id().toString());
                        checkItem.remove(searchUserList.get(position).getUser_id());
                    }
                } else {
                    if ("0".equals(userList.get(position).getFam_accept_flag())) {
                        //不是好友,不做任何处理
                        Toast.makeText(CreateGroupActivity.this, "Awaiting for approval from member.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i("", "check------" + position);
                    ((CheckBox) view.findViewById(R.id.creategroup_image_right)).toggle();
                    if (((CheckBox) view.findViewById(R.id.creategroup_image_right)).isChecked()) {
                        data.add(userList.get(position).getUser_id().toString());
                        checkItem.add(userList.get(position).getUser_id());
                    } else {
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


                if (TextUtils.isEmpty(etSearch.getText())) {
                    CreateGroupAdapter createGroupAdapter1 = new CreateGroupAdapter(
                            CreateGroupActivity.this, R.layout.gridview_item_for_creategroup, userList, groupList);
                    mGridView.setAdapter(createGroupAdapter1);
                    isSearch = false;
                } else {
                    searchGroupList.clear();
                    searchUserList.clear();

                    for (int i = 0; i < groupList.size(); i++) {
                        if (groupList.get(i).getGroup_name().toLowerCase().contains(etSearch.getText().toString().toLowerCase())) {
                            searchGroupList.add(groupList.get(i));
                        }
                    }

                    for (int j = 0; j < userList.size(); j++) {
                        if (userList.get(j).getUser_given_name().toLowerCase().contains(etSearch.getText().toString().toLowerCase())) {
                            searchUserList.add(userList.get(j));
                        }

                    }

                    CreateGroupAdapter createGroupAdapter2 = new CreateGroupAdapter(
                            CreateGroupActivity.this, R.layout.gridview_item_for_creategroup, searchUserList, searchGroupList);
                    mGridView.setAdapter(createGroupAdapter2);
                    isSearch = true;
                }

            }
        });
    }

    @Override
    public void requestData() {

        new HttpTools(CreateGroupActivity.this).get(String.format(Constant.API_GET_EVERYONE,
                MainActivity.getUser().getUser_id()), null,Tag, new HttpCallback() {
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

                    CreateGroupAdapter createGroupAdapter = new CreateGroupAdapter(CreateGroupActivity.this,
                            R.layout.gridview_item_for_creategroup, userList, groupList);

                    mGridView.setAdapter(createGroupAdapter);

                    createGroupAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class CreateGroupAdapter extends ArrayAdapter<UserEntity> {
        private int resourceId;
        private Context mContext;
        List<UserEntity> mUserList = new ArrayList<>();
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

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.creategroup_image_main);
                viewHolder.imageRight = (CheckBox) convertView.findViewById(R.id.creategroup_image_right);
                viewHolder.textName = (TextView) convertView.findViewById(R.id.creategroup_name);
                viewHolder.imageTop = (ImageView) convertView.findViewById(R.id.creategroup_image_top);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserEntity userEntity = mUserList.get(position);
            viewHolder.textName.setText(userEntity.getUser_given_name());
            BitmapTools.getInstance(mContext).display(viewHolder.imageMain,
                    String.format(Constant.API_GET_PIC, Constant.Module_profile, userEntity.getUser_id(), "profile"),
                    R.drawable.network_image_default, R.drawable.network_image_default);
            if ("0".equals(userEntity.getFam_accept_flag())) {
                viewHolder.imageTop.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageTop.setVisibility(View.GONE);
            }

            if (checkItem.contains(userEntity.getUser_id())) {
                viewHolder.imageRight.setChecked(true);
            } else {
                viewHolder.imageRight.setChecked(false);
            }
            return convertView;
        }

        class ViewHolder {
            CircularNetworkImage imageMain;
            CheckBox imageRight;
            ImageView imageTop;
            TextView textName;
        }
    }

}
