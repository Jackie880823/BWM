package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateGroupDialogActivity extends BaseActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    private ImageView ivGroupPic;
    private EditText etGroupName;
    private Button btnDone;
    private String groupMembers;//上一个界面传来的成员数据(JSON格式)
    private ProgressDialog progressDialog;
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;
    private Uri mCropImagedUri;//裁剪后的uri
    private String imagePath;
    private TextView member_num;
    private Context mContext;
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
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

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
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(CreateGroupDialogActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
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
            /**
             * begin QK
             */
            Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_no_found_reduce), Toast.LENGTH_SHORT).show();
            /**
             * end
             */
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
            ivGroupPic.setImageBitmap(photo);
        }
    }

    private void uploadImage() {

        if (mCropImagedUri == null) {
            Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_choose_your_group_picture), Toast.LENGTH_SHORT).show();
            btnDone.setEnabled(true);
            return;
        }
        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!f.exists()) {
            btnDone.setEnabled(true);
            return;
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "GroupPicture" + MainActivity.getUser().getUser_id() + groupMembers);
        params.put("mimeType", "image/png");
        params.put("file", f);
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("group_name", etGroupName.getText().toString());
        params.put("query_on", "createGroup");
        params.put("group_members", groupMembers);

        new HttpTools(this).upload(Constant.API_CREATE_GROUP, params, new HttpCallback() {
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
                        btnDone.setEnabled(true);
                        Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_fail_to_create_group), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_success_to_create_group), Toast.LENGTH_SHORT).show();

                        GroupEntity groupEntity = new GroupEntity();
                        groupEntity.setGroup_id(groupId);
                        groupEntity.setGroup_name(etGroupName.getText().toString());
                        Intent intent = new Intent(CreateGroupDialogActivity.this, MessageChatActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("groupId", groupEntity.getGroup_id());
                        intent.putExtra("titleName", groupEntity.getGroup_name());
                        //intent.putExtra("groupEntity",groupEntity);
                        startActivity(intent);//创建完群组直接跳转到聊天界面，那么前面的CreateGroupActivity和CreateGroupDialogActivity界面如何处理???
                        CreateGroupActivity.instance.finish();
                        progressDialog.dismiss();
                        finish();

                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    btnDone.setEnabled(true);
                    Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                btnDone.setEnabled(true);
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
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
            }
        });

        showCameraAlbum.show();
    }

    @Override
    public void initView() {
        mContext = this;
        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        groupMembers = getIntent().getStringExtra("members_json");//上一个界面传来的成员数据(JSON格式)
        member_num = getViewById(R.id.member_num);
        ivGroupPic = getViewById(R.id.creategroup_imageview);
        etGroupName = getViewById(R.id.creategroup_editText);
        btnDone = getViewById(R.id.creategroup_button);
        String memberLength = getIntent().getIntExtra("memberLength", 0) + "";
        String memberFormat = String.format(getString(R.string.text_members_num), memberLength);
        member_num.setText(memberFormat);
        ivGroupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etGroupName.getText())) {
                    Toast.makeText(CreateGroupDialogActivity.this, getResources().getString(R.string.text_input_your_group_name), Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(groupMembers) || "[]".equals(groupMembers)) {
                    } else {
                        btnDone.setEnabled(false);
                        uploadImage();
                    }

                }
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
                finish();
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
                startActivity(intent);
            }
        });
        showSelectDialog.show();
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
