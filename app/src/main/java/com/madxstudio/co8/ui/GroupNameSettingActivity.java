package com.madxstudio.co8.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.MyDialog;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


public class GroupNameSettingActivity extends BaseActivity {

    private final static int REQUEST_PHOTO = 1;

    Uri mCropImagedUri;
    private String imagePath;
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    CircularNetworkImage civGroupPic;
    EditText etGroupName;

    String groupId;
    String groupName;

    Boolean isUploadName = false;
    Boolean isUploadImage = false;

    Boolean isUploadNameSuccess = false;
    Boolean isUploadImageSuccess = false;

//    ProgressDialog progressDialog;
    private String TAG;

    String headUrl;
    BitmapTools mBitmapTools;

    @Override
    public int getLayout() {
        return R.layout.activity_group_name_setting;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_group_setting));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
    }

    @Override
    protected void titleRightEvent() {
        if (mCropImagedUri == null && groupName.equals(etGroupName.getText().toString())) {
            finish();
            return;
        }
        uploadImage();
        uploadName();
    }

    @Override
    protected void titleLeftEvent() {
        if(isChange()){
            showNoFriendDialog();
        }else {
            super.titleLeftEvent();
        }
    }

    private void showNoFriendDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
        final Dialog showSelectDialog = new MyDialog(this, null, selectIntention);
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
                showSelectDialog.dismiss();
                finish();
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

//        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        TAG = this.getClass().getSimpleName();
        civGroupPic = getViewById(R.id.iv_pic);
        etGroupName = getViewById(R.id.et_group_name);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        etGroupName.setText(groupName);

        mBitmapTools = BitmapTools.getInstance(this);
        headUrl = String.format(Constant.API_GET_GROUP_PHOTO, groupId);
        mBitmapTools.display(civGroupPic,headUrl, R.drawable.network_image_default, R.drawable.network_image_default);
//        VolleyUtil.initNetworkImageView(this, civGroupPic, String.format(Constant.API_GET_GROUP_PHOTO, groupId), R.drawable.network_image_default, R.drawable.network_image_default);

        civGroupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** christopher begin */
                showCameraAlbum();
                /** christopher end */
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {

            switch (requestCode) {
                case REQUEST_PHOTO:
                    if (data != null) {
                        Bitmap photo;
                        mCropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        try {
                            imagePath = mCropImagedUri.getPath();
                            if (!TextUtils.isEmpty(imagePath)) {
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(GroupNameSettingActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;

                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isChange(){
        if(!TextUtils.isEmpty(etGroupName.getText().toString().trim()) && !groupName.equals(etGroupName.getText().toString().trim())){
            return true;
        }else {
            if(TextUtils.isEmpty(etGroupName.getText().toString().trim())){
                return true;
            }
        }
        if(mCropImagedUri != null){
            return true;
        }
        return false;
    }
    /**
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private void setPicToView(Bitmap photo) {
        if (photo != null) {

            civGroupPic.setImageBitmap(photo);
        }
    }

    private void uploadImage() {

        if (mCropImagedUri == null) {
            return;
        }

        isUploadImage = true;
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
        File file = new File(path);

        if (!file.exists()) {
            return;
        }

//        progressDialog.show();
        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadGroupPicture" + MainActivity.getUser().getUser_id() + groupId);
        params.put("mimeType", "image/png");
        params.put("group_id", groupId);
        params.put("file", file);

        new HttpTools(this).upload(Constant.API_UPLOAD_GROUP_PHOTO, params, TAG, new HttpCallback() {
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
                        //清除缓存
                        mBitmapTools.clearMemoryCache();
                        mBitmapTools.clearDiskCache(null);
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_success_upload_group_profile_phone), Toast.LENGTH_SHORT).show();
                        isUploadImageSuccess = true;
                        Intent intent = new Intent();
                        intent.putExtra("groupName", etGroupName.getText().toString());
                        intent.putExtra("groupid",groupId);
                        setResult(RESULT_OK, intent);
//                        progressDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_fail_upload_group_profile_phone), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
//                progressDialog.dismiss();
                Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }


    private void uploadName() {

        if (groupName.equals(etGroupName.getText().toString())) {
            return;
        }

        isUploadName = true;

        RequestInfo requestInfo = new RequestInfo();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", groupId);
        jsonParams.put("group_name", etGroupName.getText().toString());
        jsonParams.put("query_on", "editGroupTitle");
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        requestInfo.url = String.format(Constant.API_UPDATE_GROUP_NAME, groupId);
        requestInfo.jsonParam = jsonParamsString;

        new HttpTools(GroupNameSettingActivity.this).put(requestInfo, TAG, new HttpCallback() {
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
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_success_update_group_name), Toast.LENGTH_SHORT).show();//成功
                        isUploadNameSuccess = true;
                        if (!isUploadImage) {
                            Intent intent = new Intent();
                            intent.putExtra("groupName", etGroupName.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_fail_update_group_name), Toast.LENGTH_SHORT).show();//失败
                    }
                } catch (JSONException e) {
                    Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(GroupNameSettingActivity.this,PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM,PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH,civGroupPic.getWidth());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, civGroupPic.getHeight());
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(GroupNameSettingActivity.this,PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM,PickAndCropPictureActivity.REQUEST_FROM_PHOTO);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH,civGroupPic.getWidth());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, civGroupPic.getHeight());
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
            }
        });


        showCameraAlbum.show();
    }

}
