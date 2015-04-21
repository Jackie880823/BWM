package com.madx.bwm.ui;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.widget.CircularNetworkImage;
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

public class GroupNameSettingActivity extends BaseActivity {

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

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

    ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_group_name_setting;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
    }

    @Override
    protected void setTitle()     {
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_group_setting));
        /**
         * end
         */
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
    }

    @Override
    protected void titleRightEvent() {

        if (mCropImagedUri == null && groupName.equals(etGroupName.getText().toString()))
        {
            /**
             * begin QK
             */
            Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_not_change_anything), Toast.LENGTH_SHORT).show();
            /**
             * end
             */
            return;
        }
        uploadImage();
        uploadName();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        progressDialog = new ProgressDialog(this,getResources().getString(R.string.text_dialog_loading));

        civGroupPic = getViewById(R.id.iv_pic);
        etGroupName = getViewById(R.id.et_group_name);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        etGroupName.setText(groupName);

        VolleyUtil.initNetworkImageView(this, civGroupPic, String.format(Constant.API_GET_GROUP_PHOTO,  groupId), R.drawable.network_image_default, R.drawable.network_image_default);

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
        if (PersonalPictureActivity.RESULT_OK == resultCode) {

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
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(GroupNameSettingActivity.this, CACHE_PIC_NAME_TEMP));
                    if (new File(uri.getPath()).exists()) {
                        try {
                            startPhotoZoom(uri,false);
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
        int width = civGroupPic.getWidth();
        int height = civGroupPic.getHeight();

        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
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
        List<ResolveInfo> list = GroupNameSettingActivity.this.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            /**
             * begin QK
             */
            Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_no_found_reduce), Toast.LENGTH_SHORT).show();
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

            // //防止毛边
            // intent.putExtra("scale", true);//黑边
            // intent.putExtra("scaleUpIfNeeded", true);//黑边

            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);

            //		if(fromPhoto){
            File f = PicturesCacheUtil.getCachePicFileByName(GroupNameSettingActivity.this, CACHE_PIC_NAME);
            mCropImagedUri = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
            //		}else{
            //			mCropImagedUri = uri;
            //		}
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

            civGroupPic.setImageBitmap(photo);
        }
    }


    private void uploadImage() {

        if (mCropImagedUri==null)
        {
            return;
        }

        isUploadImage = true;


        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
        File file = new File(path);

        if (!file.exists()) {
            return;
        }

        progressDialog.show();
        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadGroupPicture" + MainActivity.getUser().getUser_id() + groupId);
        params.put("mimeType", "image/png");
        params.put("group_id", groupId);
        params.put("file", file);

        new HttpTools(this).upload(Constant.API_UPLOAD_GROUP_PHOTO, params, new HttpCallback() {
            @Override
            public void onStart() {
                Log.i("", "11response==========");
            }

            @Override
            public void onFinish() {
                Log.i("", "222response==========");
            }

            @Override
            public void onResult(String response) {
                Log.i("", "333response==========" + response);
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if ("Success".equals(jsonObject.getString("response_status")))
                    {
                        /**
                         * begin QK
                         */
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_success_upload_group_profile_phone), Toast.LENGTH_SHORT).show();
                        /**
                         * end
                         */
                        isUploadImageSuccess = true;
                        Intent intent = new Intent();
                        intent.putExtra("groupName",etGroupName.getText().toString());
                        setResult(RESULT_OK, intent);
                        progressDialog.dismiss();
                        finish();
                    }
                    else
                    {
                        /**
                         * begin QK
                         */
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_fail_upload_group_profile_phone), Toast.LENGTH_SHORT).show();
                        /**
                         * end
                         */
                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                Log.i("", "444response==========");
                /**
                 * begin QK
                 */
                Toast.makeText(GroupNameSettingActivity.this,getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                /**
                 * end
                 */
            }

            @Override
            public void onCancelled() {
                Log.i("", "555response==========");
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }



    private void uploadName() {

        if (groupName.equals(etGroupName.getText().toString()))
        {
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

        new HttpTools(GroupNameSettingActivity.this).put(requestInfo, new HttpCallback() {
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
                        if (!isUploadImage)
                        {
                            Intent intent = new Intent();
                            intent.putExtra("groupName",etGroupName.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_fail_update_group_name), Toast.LENGTH_SHORT).show();//失败
                    }
                } catch (JSONException e) {
                    Toast.makeText(GroupNameSettingActivity.this,getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupNameSettingActivity.this,getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });







//        StringRequest srUpdateGroupName = new StringRequest(Request.Method.PUT, String.format(Constant.API_UPDATE_GROUP_NAME, groupId), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    if (("200").equals(jsonObject.getString("response_status_code"))) {
//                        /**
//                         * begin QK
//                         */
//                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_success_update_group_name), Toast.LENGTH_SHORT).show();//成功
//                        /**
//                         * end
//                         */
//                        isUploadNameSuccess = true;
//                        if (!isUploadImage)
//                        {
//                            Intent intent = new Intent();
//                            intent.putExtra("groupName",etGroupName.getText().toString());
//                            setResult(RESULT_OK, intent);
//                            finish();
//                        }
//                    } else {
//                        /**
//                         * begin QK
//                         */
//                        Toast.makeText(GroupNameSettingActivity.this, getResources().getString(R.string.text_fail_update_group_name), Toast.LENGTH_SHORT).show();//失败
//                        /**
//                         * end
//                         */
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                /**
//                 * begin QK
//                 */
//                Toast.makeText(GroupNameSettingActivity.this,getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                /**
//                 * end
//                 */
//            }
//        }) {
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return jsonParamsString.getBytes();
//            }
//        };
//        VolleyUtil.addRequest2Queue(GroupNameSettingActivity.this, srUpdateGroupName, "");
    }


    /** christopher begin */

    private Dialog showCameraAlbum;

    private void showCameraAlbum()
    {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_camera_album, null);

        showCameraAlbum = new MyDialog(this, null, selectIntention);

        TextView tvCamera = (TextView) selectIntention.findViewById(R.id.tv_camera);
        TextView tvAlbum = (TextView) selectIntention.findViewById(R.id.tv_album);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent2.putExtra("camerasensortype", 2);

                // 下面这句指定调用相机拍照后的照片存储的路径
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(PicturesCacheUtil.getCachePicFileByName(GroupNameSettingActivity.this,
                                CACHE_PIC_NAME_TEMP)));
                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent2.putExtra("return-data", false);
                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
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
    /** christopher end */

}
