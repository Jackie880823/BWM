package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.FamilyGroupEntity;
import com.madxstudio.co8.entity.GroupEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.MD5Util;
import com.madxstudio.co8.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateGroupDialogActivity extends BaseActivity {
    private ImageView ivGroupPic;
    private EditText etGroupName;
    private String groupMembers;//上一个界面传来的成员数据(JSON格式)
    private final static int REQUEST_PHOTO = 1;
    private Uri mCropImagedUri;//裁剪后的uri
    private String imagePath;
    private TextView member_num;
    private Context mContext;
    private List<String> selectUserList;
    private List<String> flagUserList;
    private List<FamilyGroupEntity> selectGroupEntityList;
    private static final int GET_DATA = 0X11;
    private static final int JUMP_SELECT_MEMBER = 0X12;
    private int jumpIndex = 0;
    private List<UserEntity> selectUserEntityList;
    List<UserEntity> flagEntityList;//双方不是好友
    List<UserEntity> addedEntityList;//双方是好友
    private String TAG;
    private RelativeLayout defaultHead;
    private ImageView default_imag;
    private TextView add_photo_text;
    private View vProgress;
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    @Override
    public int getLayout() {
        return R.layout.activity_create_new_group;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_message_creategroup);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        leftButton.setVisibility(View.INVISIBLE);
//        rightButton.setVisibility(View.INVISIBLE);
        rightButton.setImageResource(R.drawable.btn_done);
        rightButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void titleRightEvent() {
        if (TextUtils.isEmpty(etGroupName.getText())) {
            Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_input_your_group_name), Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(groupMembers) || "[]".equals(groupMembers)) {
            } else {
                rightButton.setEnabled(false);
                uploadImage();
            }

        }
    }

    @Override
    protected void titleLeftEvent() {
//        super.titleLeftEvent();
//        backToLastPage();
        finish();
    }

    private void backToLastPage() {
        for (UserEntity eventEntity : selectUserEntityList) {
            Log.i("selectUserEntityList===", eventEntity.getUser_id());
        }
        Intent intent = new Intent(mContext, SelectMemberActivity.class);
        String selectMemberData = new Gson().toJson(selectUserEntityList);
//        Log.i("selectUserEntityList====",new Gson().toJson(selectUserEntityList));

        intent.putExtra("members_data", selectMemberData);
        intent.putExtra("type", 0);
//        if (selectGroupEntityList.size() > 0) {
//            intent.putExtra("groups_data", new Gson().toJson(selectGroupEntityList));
//            intent.putExtra("groupType", 1);
//        }
        startActivityForResult(intent, JUMP_SELECT_MEMBER);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CreateGroupDialogActivity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_PHOTO:
                    if (data != null) {
                        Bitmap photo;
                        mCropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        try {
                            imagePath = mCropImagedUri.getPath();
                            if (!TextUtils.isEmpty(imagePath)) {
//                      photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(CreateGroupDialogActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case JUMP_SELECT_MEMBER:
                    if (data != null) {
                        initData(data);
                    }
                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initData(Intent data) {
        selectUserList.clear();
        selectUserEntityList.clear();
        String members_data = data.getStringExtra("members_data");
        Log.i("members_data====", members_data + "");
        if (members_data != null) {
            List<UserEntity> userEntityList = new GsonBuilder().create().fromJson(members_data, new TypeToken<ArrayList<UserEntity>>() {
            }.getType());
            if (userEntityList != null && userEntityList.size() > 0) {
                List<UserEntity> userList = new ArrayList<>();
                userList.addAll(userEntityList);
//                if (selectUserList.size() > 0) {
//                    for (UserEntity userEntity : userEntityList) {
//                        if (selectUserList.contains(userEntity.getUser_id())) {
//                            userList.remove(userEntity);
//                        }
//                    }
//                }
                for (UserEntity userEntity : userList) {
                    selectUserList.add(userEntity.getUser_id());
                }
                selectUserEntityList.addAll(userList);
            }
            Log.i("selectUserList====2", selectUserList.size() + "");
        }

        if (flagEntityList != null && flagEntityList.size() > 0) {
            Log.i("flagEntityList====2", flagEntityList.size() + "");
            for (UserEntity user : flagEntityList) {
                selectUserList.add(user.getUser_id());
                selectUserEntityList.add(user);
            }
        }
        Log.i("selectUserList====3", selectUserList.size() + "");
        String groups_data = data.getStringExtra("groups_data");
        List<FamilyGroupEntity> groupEntityList = null;
        if (null != groups_data) {
            groupEntityList = new GsonBuilder().create().fromJson(groups_data, new TypeToken<ArrayList<FamilyGroupEntity>>() {
            }.getType());
        }
        if (groupEntityList != null && groupEntityList.size() > 0) {
            List<String> groupIdList = new ArrayList<>();
            List<FamilyGroupEntity> groupList = new ArrayList<>();
            groupList.addAll(groupEntityList);
//            if (selectGroupEntityList.size() > 0) {
//                for (FamilyGroupEntity familyGroupEntity : groupEntityList) {
//                    for (FamilyGroupEntity groupEntity : selectGroupEntityList) {
//                        if (familyGroupEntity.getGroup_id().equals(groupEntity.getGroup_id())) {
//                            groupList.remove(familyGroupEntity);
//                            break;
//                        }
//                    }
//                }
//            }
            if (groupList.size() > 0) {
                selectGroupEntityList.addAll(groupList);
                for (FamilyGroupEntity familyGroupEntity : groupList) {
                    groupIdList.add(familyGroupEntity.getGroup_id());
                }
            }
            if (groupIdList.size() > 0) {
                getMembersList(new Gson().toJson(groupIdList));
            }
        } else {
            changeData();
        }
    }

    private void changeData() {
        String memberFormat = String.format(getString(R.string.text_members_num), selectUserList.size() + "");
        member_num.setText(memberFormat);
        groupMembers = new Gson().toJson(selectUserList);
    }


    /**
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private void setPicToView(Bitmap photo) {
        if (photo != null) {
            default_imag.setVisibility(View.GONE);
            add_photo_text.setVisibility(View.GONE);
            defaultHead.setBackgroundResource(0);
            ivGroupPic.setImageBitmap(photo);
        }
    }

    private void uploadImage() {
//        if (mCropImagedUri == null) {
//            Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_choose_your_group_picture), Toast.LENGTH_SHORT).show();
//            rightButton.setEnabled(true);
//            return;
//        }
        File f = null;
        if (mCropImagedUri != null) {
            f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        String fileName = MD5Util.string2MD5(System.currentTimeMillis() + "");
        params.put("fileName", fileName);
        params.put("mimeType", "image/png");
        if (f != null && f.exists()) {
            params.put("file", f);
        }
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("group_name", etGroupName.getText().toString());
        params.put("query_on", "createGroup");
        params.put("group_members", groupMembers);

        new HttpTools(this).upload(Constant.API_CREATE_GROUP, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                if (vProgress != null) {
                    vProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                if (vProgress != null) {
                    vProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResult(String response) {
                try {
                    String groupId;
                    JSONObject jsonObject = new JSONObject(response);
                    groupId = jsonObject.getString("group_id");
                    if (TextUtils.isEmpty(groupId)) {
//                        progressDialog.dismiss();
                        rightButton.setEnabled(true);
                        Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_fail_to_create_group), Toast.LENGTH_SHORT).show();
                    } else {
                        //                      Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_success_to_create_group), Toast.LENGTH_SHORT).show();
                        GroupEntity groupEntity = new GroupEntity();
                        groupEntity.setGroup_id(groupId);
                        groupEntity.setGroup_name(etGroupName.getText().toString());
                        Intent intent = new Intent(CreateGroupDialogActivity.this, MessageChatActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("isNewGroup", 1);
                        intent.putExtra("groupId", groupEntity.getGroup_id());
                        intent.putExtra("titleName", groupEntity.getGroup_name());
                        //intent.putExtra("groupEntity",groupEntity);
                        startActivity(intent);//创建完群组直接跳转到聊天界面，那么前面的CreateGroupActivity和CreateGroupDialogActivity界面如何处理???
//                        progressDialog.dismiss();

                        finish();
                    }

                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    rightButton.setEnabled(true);
                    Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
//                progressDialog.dismiss();
                rightButton.setEnabled(true);
                Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


    }

    private MyDialog showCameraAlbum;

    private void showCameraAlbum() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_camera_album, null);

        showCameraAlbum = new MyDialog(this, null, selectIntention);

        TextView tvCamera = (TextView) selectIntention.findViewById(R.id.tv_camera);
        TextView tvAlbum = (TextView) selectIntention.findViewById(R.id.tv_album);
        TextView tv_cancel = (TextView) selectIntention.findViewById(R.id.tv_cancel);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(CreateGroupDialogActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, ivGroupPic.getWidth());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, ivGroupPic.getHeight());
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
            }
        });
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(CreateGroupDialogActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_PHOTO);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, ivGroupPic.getWidth());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, ivGroupPic.getHeight());
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });
        if (!showCameraAlbum.isShowing())
            showCameraAlbum.show();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    List<UserEntity> userList = (List<UserEntity>) msg.obj;
                    List<UserEntity> userEntityList = new ArrayList<>();
                    userEntityList.addAll(userList);
                    if (selectUserList.size() > 0) {
                        for (UserEntity userEntity : userList) {
                            for (String userId : selectUserList) {
                                if (userEntity.getUser_id().equals(userId)) {
                                    userEntityList.remove(userEntity);
                                    break;
                                }
                            }
                        }
                    }
                    for (UserEntity user : userEntityList) {
//                        selectUserList.add(user.getUser_id());
                        //如果双方不是好友
                        if (user.getAdded_flag().equals("0")) {
                            flagEntityList.add(user);
                            selectUserList.add(user.getUser_id());
                        }
                        if (user.getAdded_flag().equals("1")) {
                            addedEntityList.add(user);
                            selectUserList.add(user.getUser_id());
                        }
                    }
//                    selectUserEntityList.addAll(flagEntityList);
                    selectUserEntityList.addAll(addedEntityList);
                    Log.i("flagEntityList====1", flagEntityList.size() + "");
                    Log.i("selectUserList====1", selectUserList.size() + "");
                    changeData();
                    break;
            }
            return false;
        }
    });

    @Override
    public void initView() {
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.GONE);
        selectUserList = new ArrayList<>();
        flagEntityList = new ArrayList<>();
        addedEntityList = new ArrayList<>();
        selectUserEntityList = new ArrayList<>();
        selectGroupEntityList = new ArrayList<>();
        flagUserList = new ArrayList<>();
//        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        member_num = getViewById(R.id.member_num);
        ivGroupPic = getViewById(R.id.creategroup_imageview);
        etGroupName = getViewById(R.id.creategroup_editText);
        add_photo_text = getViewById(R.id.add_photo_text);
        defaultHead = getViewById(R.id.default_imagview);
        add_photo_text = getViewById(R.id.add_photo_text);
        default_imag = getViewById(R.id.default_imag);
        //groupMembers = getIntent().getStringExtra("members_json");//上一个界面传来的成员数据(JSON格式)
        jumpIndex = getIntent().getIntExtra("jumpIndex", 0);
        initData(getIntent());
//        ivGroupPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCameraAlbum();
//            }
//        });
        defaultHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum();
            }
        });

        getViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ask4Confirm();
            }
        });
        member_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLastPage();
            }
        });
    }

    private MyDialog myDialog = null;

    private void ask4Confirm() {
//
//        if (myDialog == null) {
        myDialog = new MyDialog(CreateGroupDialogActivity.this, R.string.text_tips_title, R.string.text_create_group_not_save);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                myDialog = null;
            }
        });
        myDialog.setButtonAccept(R.string.text_dialog_accept, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                myDialog = null;
                finish();
            }
        });
//        }
        if (myDialog != null && !myDialog.isShowing())
            myDialog.show();

    }

    @Override
    public void requestData() {

    }

    public void getMembersList(String groupIdList) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("group_list", groupIdList);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_GROUP_MEMBERS, params);
        new HttpTools(mContext).get(url, null, TAG, new HttpCallback() {
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
                List<UserEntity> userList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                Log.i("getMembersList", userList.size() + "");
                if (userList != null && userList.size() > 0) {
                    Message.obtain(handler, GET_DATA, userList).sendToTarget();
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
            myDialog = null;
        }
        if (showCameraAlbum != null)
            showCameraAlbum.dismiss();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                ask4Confirm();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}