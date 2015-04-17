package com.madx.bwm.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.NetworkUtil;
import com.madx.bwm.widget.CircularNetworkImage;
import com.madx.bwm.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyFamilyActivity extends BaseActivity {


    List<UserEntity> userList = new ArrayList<>();//好友
    List<GroupEntity> groupList = new ArrayList<>();//群组

    List<UserEntity> searchUserList = new ArrayList<>();//好友
    List<GroupEntity> searchGroupList = new ArrayList<>();;//群组
    EditText etSearch;//搜索
    Boolean isSearch = false;

    GridView mGridView;

    ImageButton mTop;

    ProgressDialog progressDialog;

    private Dialog showSelectDialog;

    ProgressBarCircularIndeterminate ProgressBar;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    @Override
    public int getLayout() {
        return R.layout.activity_my_family;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_message_myfamily);
    }

    @Override
    protected void titleRightEvent() {
        showSelectDialog();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
    }

    @Override
    public void initView() {

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        etSearch = getViewById(R.id.et_search);

        mGridView = getViewById(R.id.myfamily_gridView);

        mTop = getViewById(R.id.ib_top);

        ProgressBar = getViewById(R.id.gv_progress_bar);

        progressDialog = new ProgressDialog(this,getResources().getString(R.string.text_download));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!NetworkUtil.isNetworkConnected(MyFamilyActivity.this)) {
                    /**
                     * begin QK
                     */
                    Toast.makeText(MyFamilyActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    finishReFresh();
                    /**
                     * end
                     */
                    return;
                }

                isRefresh = true;
                requestData();

            }

        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSearch)
                {
                    if (position == 0)
                    {
                        //下载PDF,并查看
                        getUrl();
                    }
                    else
                    {

                        if ((position > 0) && (position < searchGroupList.size() + 1)) {
                            //Group,跳转到对话界面
                            Intent intent = new Intent(MyFamilyActivity.this, ChatActivity.class);
                            intent.putExtra("type",1);
                            intent.putExtra("groupEntity", searchGroupList.get(position-1));
                            startActivity(intent);
                        }
                        else
                        {
                            //
                            if ("0".equals(searchUserList.get(position - searchGroupList.size() - 1).getFam_accept_flag()))
                            {
                                //不是好友,提示等待接收
                                MessageUtil.showMessage(MyFamilyActivity.this, getResources().getString(R.string.text_awaiting_for_approval), 3000);
                                return;
                            }
                            else
                            {
                                //member, 跳转到个人资料页面需要
                                //put请求消除爱心
                                if ("".equals(searchUserList.get(position - searchGroupList.size() - 1).getMiss()))
                                {
                                    updateMiss(searchUserList.get(position - searchGroupList.size() - 1).getUser_id());
                                    view.findViewById(R.id.myfamily_image_right).setVisibility(View.GONE);
                                    searchUserList.get(position - searchGroupList.size() - 1).setMiss(null);
                                }
                                else
                                {

                                }

                                Intent intent = new Intent(MyFamilyActivity.this, FamilyProfileActivity.class);
                                intent.putExtra("member_id",searchUserList.get(position - searchGroupList.size() - 1).getUser_id());
                                startActivity(intent);
                            }
                        }
                    }
                }
                else
                {
                    if (position == 0)
                    {
                        //下载PDF,并查看
                        progressDialog.show();
                        getUrl();
                    }
                    else
                    {

                        if ((position > 0) && (position < groupList.size() + 1)) {
                            //Group,跳转到对话界面
                            Intent intent = new Intent(MyFamilyActivity.this, ChatActivity.class);
                            intent.putExtra("type",1);
                            intent.putExtra("groupEntity", groupList.get(position-1));
                            startActivity(intent);
                        }
                        else
                        {
                            //
                            if ("0".equals(userList.get(position - groupList.size() - 1).getFam_accept_flag()))
                            {
                                //不是好友,提示等待接收
                                MessageUtil.showMessage(MyFamilyActivity.this, getResources().getString(R.string.text_awaiting_for_approval), 3000);
                                return;
                            }
                            else
                            {
                                if ("".equals(userList.get(position - groupList.size() - 1).getMiss()))
                                {
                                    updateMiss(userList.get(position - groupList.size() - 1).getUser_id());
                                    view.findViewById(R.id.myfamily_image_right).setVisibility(View.GONE);
                                    userList.get(position - groupList.size() - 1).setMiss(null);
                                }
                                else
                                {

                                }
                                //member, 跳转到个人资料页面需要
                                //put请求消除爱心
                                Intent intent = new Intent(MyFamilyActivity.this, FamilyProfileActivity.class);
                                intent.putExtra("member_id",userList.get(position - groupList.size() - 1).getUser_id());
                                startActivity(intent);
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
                    MyFamilyAdapter myFamilyAdapter1 = new MyFamilyAdapter(MyFamilyActivity.this, R.layout.gridview_item_for_myfamily, userList, groupList);
                    mGridView.setAdapter(myFamilyAdapter1);
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

                    MyFamilyAdapter myFamilyAdapter2 = new MyFamilyAdapter(MyFamilyActivity.this, R.layout.gridview_item_for_myfamily, searchUserList, searchGroupList);
                    mGridView.setAdapter(myFamilyAdapter2);
                    isSearch = true;
                }


            }
        });
    }



    @Override
    public void requestData() {

        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }


        new HttpTools(this).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null, new HttpCallback() {
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
                    finishReFresh();
                    MyFamilyAdapter myFamilyAdapter = new MyFamilyAdapter(MyFamilyActivity.this, R.layout.gridview_item_for_myfamily, userList, groupList);
                    mGridView.setAdapter(myFamilyAdapter);
                    ProgressBar.setVisibility(View.GONE);
                } catch (JSONException e)
                {
                    MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                    if (isRefresh) {
                        finishReFresh();
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                if (isRefresh) {
                    finishReFresh();
                }
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
//                    MyFamilyAdapter myFamilyAdapter = new MyFamilyAdapter(MyFamilyActivity.this, R.layout.gridview_item_for_myfamily, userList, groupList);
//                    mGridView.setAdapter(myFamilyAdapter);
//                    ProgressBar.setVisibility(View.GONE);
//                } catch (JSONException e)
//                {
//                    ProgressBar.setVisibility(View.GONE);
//                    e.printStackTrace();
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                ProgressBar.setVisibility(View.GONE);
//                Log.i("", "error=====" + error.getMessage());
//            }
//        });
//        VolleyUtil.addRequest2Queue(this.getApplicationContext(), stringRequest, "");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyFamilyAdapter extends ArrayAdapter
    {
        private int resourceId;
        private Context mContext;
        List<UserEntity> mUserList ;
        List<GroupEntity> mGroupList;

        public MyFamilyAdapter(Context context, int gridViewResourceId, List<UserEntity> userList, List<GroupEntity> groupList) {
            super(context, gridViewResourceId);
            resourceId = gridViewResourceId;
            mContext = context;
            mUserList = userList;
            mGroupList = groupList;
        }


        @Override
        public int getCount() {
            return ( mUserList.size() + mGroupList.size() + 1 );
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
//                viewHolder.imageMain = (CircularNetworkImage)convertView.findViewById(R.id.myfamily_image_main);
//                viewHolder.imageLeft = (ImageView)convertView.findViewById(R.id.myfamily_image_left);
//                viewHolder.imageRight = (ImageView)convertView.findViewById(R.id.myfamily_image_right);
//                viewHolder.textName = (TextView)convertView.findViewById(R.id.myfamily_name);
//
//                convertView.setTag(viewHolder);
//            }
//            else
//            {
//                viewHolder = (ViewHolder)convertView.getTag();
//            }
//
//            if (position == 0)
//            {
//                viewHolder.textName.setText("Family Tree");
//                viewHolder.imageMain.setDefaultImageResId(R.drawable.family_tree);
//                viewHolder.imageMain.setImageResource(R.drawable.family_tree);
//            }
//            else {
//                if ((position > 0) && (position < mGroupList.size() + 1)) {
//                    GroupEntity groupEntity = mGroupList.get(position - 1);
//                    viewHolder.textName.setText(groupEntity.getGroup_name());
//                    VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO,  groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//                } else {
//                    UserEntity userEntity = mUserList.get(position - mGroupList.size() - 1);
//                    viewHolder.textName.setText(userEntity.getUser_given_name());
//
//                    VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//
//                    InputStream is = null;
//                    try {
//                        is = mContext.getAssets().open(userEntity.getUser_emoticon()+".png");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Drawable da = Drawable.createFromStream(is, null);
//
//                    viewHolder.imageLeft.setImageDrawable(da);
//
//                }
//            }


            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();

            viewHolder.imageMain = (CircularNetworkImage)convertView.findViewById(R.id.myfamily_image_main);
            viewHolder.textName = (TextView)convertView.findViewById(R.id.myfamily_name);

            if (position == 0)
            {
                viewHolder.textName.setText(getResources().getString(R.string.text_family_tree));
                viewHolder.imageMain.setDefaultImageResId(R.drawable.family_tree);
                viewHolder.imageMain.setImageResource(R.drawable.family_tree);
            }
            else {
                if ((position > 0) && (position < mGroupList.size() + 1)) {
                    GroupEntity groupEntity = mGroupList.get(position - 1);
                    viewHolder.textName.setText(groupEntity.getGroup_name());
                    VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO,  groupEntity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                } else {
                    UserEntity userEntity = mUserList.get(position - mGroupList.size() - 1);

                    viewHolder.textName.setText(userEntity.getUser_given_name());
                    VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

                    //右上角指示是否为好友
                    if ("0".equals(userEntity.getFam_accept_flag()))
                    {
                        viewHolder.imageTop = (ImageView)convertView.findViewById(R.id.myfamily_image_top);
                        viewHolder.imageTop.setVisibility(View.VISIBLE);
                    }

                    //右下角miss,滚动后会重新构建出来
                    if ("".equals(userEntity.getMiss()))
                    {
                        viewHolder.imageRight = (ImageView)convertView.findViewById(R.id.myfamily_image_right);
                        viewHolder.imageRight.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolder.imageRight = (ImageView)convertView.findViewById(R.id.myfamily_image_right);
                        viewHolder.imageRight.setVisibility(View.GONE);
                    }

                    //左下角心情图标
                    if (!TextUtils.isEmpty(userEntity.getUser_emoticon()))
                    {
                        viewHolder.imageLeft = (ImageView)convertView.findViewById(R.id.myfamily_image_left);

                        try {
                            InputStream is = mContext.getAssets().open(userEntity.getUser_emoticon()+".png");
                            Drawable da = Drawable.createFromStream(is, null);
                            viewHolder.imageLeft.setImageDrawable(da);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            return convertView;
        }


        class ViewHolder
        {
            CircularNetworkImage imageMain;
            ImageView imageLeft;
            ImageView imageRight;
            ImageView imageTop;
            TextView textName;
        }
    }


    private void showSelectDialog()
    {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);

        showSelectDialog = new MyDialog(this, null, selectIntention);

        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);

        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                startActivity(new Intent(MyFamilyActivity.this, AddNewMembersActivity.class));
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyFamilyActivity.this, CreateGroupActivity.class));
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }



    private void getUrl()
    {


        if (!NetworkUtil.isNetworkConnected(MyFamilyActivity.this)) {
            Toast.makeText(MyFamilyActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        new HttpTools(this).get(String.format(Constant.API_FAMILY_TREE, MainActivity.getUser().getUser_id()),null, new HttpCallback() {
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
                    if ("Success".equals(jsonObject.getString("response_status")))
                    {
                        urlString = jsonObject.getString("filePath");
                        if (!TextUtils.isEmpty(urlString))
                        {
                            getPdf();
                        }
                    }
                    else
                    {
                        MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

//
//        StringRequest srUrl = new StringRequest(String.format(Constant.API_FAMILY_TREE, MainActivity.getUser().getUser_id()), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if ("Success".equals(jsonObject.getString("response_status")))
//                    {
//                        urlString = jsonObject.getString("filePath");
//                        /**
//                         * begin QK
//                         */
////                        Toast.makeText(MyFamilyActivity.this, getResources().getString(R.string.text_success_get_pdf), Toast.LENGTH_SHORT).show();
//                        if (!TextUtils.isEmpty(urlString))
//                        {
//                            getPdf();
//                        }
//                    }
//                    else
//                    {
////                        Toast.makeText(MyFamilyActivity.this, getResources().getString(R.string.text_fail_get_pdf), Toast.LENGTH_SHORT).show();
//                        /**
//                         * end
//                         */
//                        progressDialog.dismiss();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//            }
//        });
//        VolleyUtil.addRequest2Queue(MyFamilyActivity.this, srUrl, "");
    }

    private final static String CACHE_FILE_NAME="/cache_%s.pdf";
    String urlString ;

    public void getPdf(){

        final String target = FileUtil.getCacheFilePath(this)+String.format(CACHE_FILE_NAME,""+System.currentTimeMillis());

        new HttpTools(this).download(urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                File file = new File(target);
                if (file.exists()) {
                    progressDialog.dismiss();
                    System.out.println("打开");
                    Uri path1 = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path1, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                        System.out.println("打开失败");
                    }
                }

            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
//
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection connection = (HttpURLConnection)
//                    url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setUseCaches(false);
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//            //实现连接
//            connection.connect();
//
//            System.out.println("connection.getResponseCode()="+connection.getResponseCode());
//            if (connection.getResponseCode() == 200) {
//                InputStream is = connection.getInputStream();
//                //以下为下载操作
//                byte[] arr = new byte[1];
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                BufferedOutputStream bos = new BufferedOutputStream(baos);
//                int n = is.read(arr);
//                while (n > 0) {
//                    bos.write(arr);
//                    n = is.read(arr);
//                }
//                bos.close();
//                String path = Environment.getExternalStorageDirectory()
//                        + "/download/";
//                String[] name = urlString.split("/");
//                path = path + name[name.length - 1];
//                System.out.println("name="+name);
//                System.out.println("path="+path);
//                File file = new File(path);
//                FileOutputStream fos = new FileOutputStream(file);
//                fos.write(baos.toByteArray());
//                fos.close();
//                //关闭网络连接
//                connection.disconnect();
//                System.out.println("下载完成");
//                if (file.exists()) {
//                    System.out.println("打开");
//                    Uri path1 = Uri.fromFile(file);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(path1, "application/pdf");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                    try {
//                        startActivity(intent);
//                    }
//                    catch (ActivityNotFoundException e) {
//                        System.out.println("打开失败");
//                    }
//                }
//            }
//        } catch (IOException e) {
//            // TODO: handle exception
//            System.out.println(e.getMessage());
//        }
    }

    public void updateMiss(String member_id)
    {
        RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", member_id);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_UPDATE_MISS, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                Log.d("","update----" + string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("200".equals(jsonObject.getString("response_status_code")))
                    {
                        Toast.makeText(MyFamilyActivity.this, getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(MyFamilyActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }
}
