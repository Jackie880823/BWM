package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.FamilyGroupEntity;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.MD5Util;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateGroupDialogActivity extends BaseActivity {
    private ImageView ivGroupPic;
    private EditText etGroupName;
    private String groupMembers;//上一个界面传来的成员数据(JSON格式)
    private ProgressDialog progressDialog;
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;
    private Uri mCropImagedUri;//裁剪后的uri
    private String imagePath;
    private TextView member_num;
    private Context mContext;
    private List<String> selectUserList;
    private List<FamilyGroupEntity> selectGroupEntityList;
    private static final int GET_DATA = 0X11;
    private static final int JUMP_SELECT_MEMBER = 0X12;
    private int jumpIndex = 0;
    private List<UserEntity> selectUserEntityList;
    private String TAG;
    private RelativeLayout defaultHead;
    private ImageView default_imag;
    private TextView add_photo_text;
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
        Intent intent = new Intent(mContext, InviteMemberActivity.class);
        String selectMemberData = new Gson().toJson(selectUserEntityList);
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
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    if (data != null) {

                        Uri uri;
                        uri = data.getData();
                        try {
                            startPhotoZoom(uri, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(CreateGroupDialogActivity.this, CACHE_PIC_NAME_TEMP));
                    if (new File(uri.getPath()).exists()) {
                        try {
                            startPhotoZoom(uri, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;


                // 取得裁剪后的图片
                case REQUEST_HEAD_FINAL:
                    if (data != null) {
                        Bitmap photo;
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
        }
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
     * 裁剪图片方法实现
     *
     * @param uri
     * @throws java.io.IOException
     */
    public void startPhotoZoom(Uri uri, boolean fromPhoto) throws IOException {

        if (uri == null || uri.getPath() == null) {
            return;
        }

        //TODO换为view 的宽度
        int width = ivGroupPic.getWidth();
        int height = ivGroupPic.getHeight();

        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转?????
         */
        int degree = LocalImageLoader.readPictureDegree(uri.getPath());

        if (degree != 0) {
            /**
             * 把图片旋转为正的方向
             */
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(
                    new FileInputStream(uri.getPath()), null, options);
            options.inSampleSize = 4;
            options.outWidth = width;
            options.outHeight = height;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(
                    new FileInputStream(uri.getPath()), null, options);
            bitmap = LocalImageLoader.rotaingImageView(degree, bitmap);
            byte[] newBytes = LocalImageLoader.bitmap2bytes(bitmap);
            File file = new File(uri.getPath());
            file.delete();
            FileOutputStream fos = new FileOutputStream(uri.getPath());
            fos.write(newBytes);
            fos.flush();
            fos.close();
            bitmap.recycle();
            bitmap = null;
        }


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        List<ResolveInfo> list = CreateGroupDialogActivity.this.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_no_found_reduce), Toast.LENGTH_SHORT).show();
            return;
        } else {
            // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            File f = PicturesCacheUtil.getCachePicFileByName(CreateGroupDialogActivity.this, CACHE_PIC_NAME);
            mCropImagedUri = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
            startActivityForResult(intent, REQUEST_HEAD_FINAL);
        }
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
        if (mCropImagedUri == null) {
            Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_choose_your_group_picture), Toast.LENGTH_SHORT).show();
            rightButton.setEnabled(true);
            return;
        }
        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!f.exists()) {
            rightButton.setEnabled(true);
            return;
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        String fileName = MD5Util.string2MD5(System.currentTimeMillis() + "");
        params.put("fileName", fileName);
        params.put("mimeType", "image/png");
        params.put("file", f);
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("group_name", etGroupName.getText().toString());
        params.put("query_on", "createGroup");
        params.put("group_members", groupMembers);

        new HttpTools(this).upload(Constant.API_CREATE_GROUP, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                try {
                    String groupId;
                    JSONObject jsonObject = new JSONObject(response);
                    groupId = jsonObject.getString("group_id");
                    if (TextUtils.isEmpty(groupId)) {
                        progressDialog.dismiss();
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
                        progressDialog.dismiss();

                        finish();
                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    rightButton.setEnabled(true);
                    Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
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

    private Dialog showCameraAlbum;

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
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent2.putExtra("camerasensortype", 2);

                // 下面这句指定调用相机拍照后的照片存储的路径
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(PicturesCacheUtil.getCachePicFileByName(CreateGroupDialogActivity.this,
                                CACHE_PIC_NAME_TEMP)));
                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent2.putExtra("return-data", false);
                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
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
                Intent intent = new Intent(CreateGroupDialogActivity.this, SelectPhotosActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
            }
        });

        showCameraAlbum.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                        selectUserList.add(user.getUser_id());
                    }
                    selectUserEntityList.addAll(userEntityList);
                    changeData();
                    break;
            }
        }
    };

    @Override
    public void initView() {
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        selectUserList = new ArrayList<>();
        selectUserEntityList = new ArrayList<>();
        selectGroupEntityList = new ArrayList<>();
        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        member_num = getViewById(R.id.member_num);
        ivGroupPic = getViewById(R.id.creategroup_imageview);
        etGroupName = getViewById(R.id.creategroup_editText);
        add_photo_text = getViewById(R.id.add_photo_text);
        defaultHead = getViewById(R.id.default_imagview);
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
                showNoFriendDialog();
            }
        });
        member_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLastPage();
            }
        });
    }

    private void showNoFriendDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
        final Dialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
        tv_no_member.setText(getString(R.string.text_create_group_not_save));
        TextView okTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        cancelTv.setVisibility(View.VISIBLE);
        ((View) selectIntention.findViewById(R.id.line_view)).setVisibility(View.VISIBLE);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("jumpIndex", jumpIndex);
                startActivity(intent);
            }
        });
        showSelectDialog.show();
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
}