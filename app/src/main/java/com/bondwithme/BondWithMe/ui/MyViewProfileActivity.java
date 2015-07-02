package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.DatePicker;
import com.bondwithme.BondWithMe.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MyViewProfileActivity extends BaseActivity {
    private CircularNetworkImage cniMain;
    private ImageView ivBottomLeft;
    private TextView tvName1;
    private TextView tvId1;
    private LinearLayout llResetPassword;
    private TextView tvId2;
    private TextView etFirstName;
    private TextView etLastName;
    //    private TextView tvAge;
//    private RelativeLayout rlAge;
    private TextView tvBirthday;
    private RelativeLayout rlBirthday;
    private TextView tvGender;
    private RelativeLayout rlGender;
    private TextView etEmail;
    private TextView etRegion;
    private Dialog showSelectDialog;
    private Dialog showCameraAlbum;
    private Boolean isUploadName = false;
    private Boolean isUploadImage = false;

    private Boolean isUploadNameSuccess = false;
    private Boolean isUploadImageSuccess = false;


    private String userGender;

    private ProgressDialog progressDialog;

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

    Uri mCropImagedUri;
    private String imagePath;
    private Context mContext;
    private String TAG;
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
        return R.layout.activity_my_view_profile;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.btn_done);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_my_profile));
    }

    @Override
    protected void titleRightEvent() {
        uploadImage();
        updateProfile();
    }

    @Override
    protected void titleLeftEvent() {
        // super.titleLeftEvent();
        showNoFriendDialog();
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
                showSelectDialog.dismiss();
                finish();
            }
        });
        showSelectDialog.show();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            showNoFriendDialog();
//            return true;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        tvName1 = getViewById(R.id.tv_name1);
        tvId1 = getViewById(R.id.tv_id1);

        llResetPassword = getViewById(R.id.ll_reset_password);

        tvId2 = getViewById(R.id.tv_id2);
        etFirstName = getViewById(R.id.et_first_name);
        etLastName = getViewById(R.id.et_last_name);
//        tvAge = getViewById(R.id.tv_age);
//        rlAge = getViewById(R.id.rl_age);
        tvBirthday = getViewById(R.id.tv_birthday);
        rlBirthday = getViewById(R.id.rl_birthday);
        tvGender = getViewById(R.id.tv_gender);
        rlGender = getViewById(R.id.rl_gender);
        etEmail = getViewById(R.id.et_email);
        etRegion = getViewById(R.id.et_region);

        VolleyUtil.initNetworkImageView(MyViewProfileActivity.this, cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile,
                MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName1.setText(MainActivity.getUser().getUser_given_name());
        etFirstName.setText(MainActivity.getUser().getUser_given_name());
        etLastName.setText(MainActivity.getUser().getUser_surname());
        tvTitle.setText(MainActivity.getUser().getUser_given_name());
        tvId1.setText("ID:" + MainActivity.getUser().getDis_bondwithme_id());
        tvId2.setText(MainActivity.getUser().getDis_bondwithme_id());

//        tvAge.setText(MainActivity.getUser().getUser_dob());//需要做处理，年转为岁数
        tvBirthday.setText(MainActivity.getUser().getUser_dob());
//
//        if (!TextUtils.isEmpty(MainActivity.getUser().getUser_dob()))
//        {
//            Timestamp ts;
//            ts = Timestamp.valueOf(tvBirthday.getText().toString() + " 00:00:00");
//            Calendar mCalendar = Calendar.getContextInstance(TimeZone.getDefault());
//            mCalendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
//            int age = Integer.parseInt(new SimpleDateFormat("yyyy").format(new java.util.Date())) -
//              Integer.parseInt(new SimpleDateFormat("yyy").format(mCalendar.getTime()));
//            tvAge.setText(String.valueOf(age));
//        }


        if ("F".equals(MainActivity.getUser().getUser_gender())) {
            tvGender.setText(getResources().getString(R.string.text_female));
        } else if ("M".equals(MainActivity.getUser().getUser_gender())) {
            tvGender.setText(getResources().getString(R.string.text_male));
        } else {
            tvGender.setText(getResources().getString(R.string.text_null));
        }

        etEmail.setText(MainActivity.getUser().getUser_email());
        String[] countryArray = getResources().getStringArray(R.array.country_code);
        String userCountryCode = MainActivity.getUser().getUser_country_code().trim();
        if (countryArray != null) {
            for (String string : countryArray) {
                if (string.indexOf(" ") != -1) {
                    String countryCode = string.substring(string.lastIndexOf(" ") + 1).trim();
                    String countryName = string.substring(0, string.lastIndexOf(" ")).trim();
                    if (null != countryCode && countryCode.equals(userCountryCode)) {
                        etRegion.setText(countryName);
                    }
                }
            }
        }
        //etRegion.setText(MainActivity.getUser().getUser_location_name());
        etRegion.setKeyListener(null);
        String dofeel_code = MainActivity.getUser().getDofeel_code();
        if (!TextUtils.isEmpty(dofeel_code)) {
            try {
                String filePath = "";
                if (dofeel_code.indexOf("_") != -1) {
                    filePath = dofeel_code.replaceAll("_", File.separator);
                }
                InputStream is = MyViewProfileActivity.this.getAssets().open(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivBottomLeft.setImageBitmap(bitmap);

//                    Drawable da = Drawable.createFromStream(is, null);
//                    ivBottomLeft.setImageDrawable(da);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        rlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });

        llResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyViewProfileActivity.this, ProfileResetPasswordActivity.class));
            }
        });

        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum();
            }
        });

        rlBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateProfile() {

        if (TextUtils.isEmpty(etLastName.getText()) || TextUtils.isEmpty(etFirstName.getText()) || TextUtils.isEmpty(tvGender.getText())) {
            Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_input_name_gender), Toast.LENGTH_SHORT).show();
            return;
        }

        isUploadName = true;

        if ("Male".equals(tvGender.getText().toString())) {
            userGender = "M";
        } else {
            userGender = "F";
        }
        RequestInfo requestInfo = new RequestInfo();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_surname", etLastName.getText().toString());
        jsonParams.put("user_given_name", etFirstName.getText().toString());
        jsonParams.put("user_gender", userGender);
        jsonParams.put("user_dob", tvBirthday.getText().toString());
        jsonParams.put("user_email", etEmail.getText().toString());
        jsonParams.put("user_location_name", etRegion.getText().toString());

        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        requestInfo.url = String.format(Constant.API_UPDATE_MY_PROFILE, MainActivity.getUser().getUser_id());
        requestInfo.jsonParam = jsonParamsString;

        new HttpTools(MyViewProfileActivity.this).put(requestInfo, TAG, new HttpCallback() {
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

                if (!TextUtils.isEmpty(response)) {
                    if (response.contains("changedFlag")) {
                    } else if (response.contains("response_status")) {
                        Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_fail_update_information), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_success_update_information), Toast.LENGTH_SHORT).show();
                        isUploadNameSuccess = true;
                        App.changeLoginedUser(gson.fromJson(response, UserEntity.class));
                        List userList = new ArrayList<UserEntity>();
                        userList.add(gson.fromJson(response, UserEntity.class));
                        if (!isUploadImage) {
                            Intent intent = new Intent();
                            intent.putExtra("name", etFirstName.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_male_female, null);
        showSelectDialog = new MyDialog(this, null, selectIntention);
        TextView tvMale = (TextView) selectIntention.findViewById(R.id.tv_male);
        TextView tvFemale = (TextView) selectIntention.findViewById(R.id.tv_female);

        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGender.setText("Male");
                showSelectDialog.dismiss();
            }
        });

        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGender.setText("Female");
                showSelectDialog.dismiss();
            }
        });

        showSelectDialog.show();
    }

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
                        .fromFile(PicturesCacheUtil.getCachePicFileByName(MyViewProfileActivity.this,
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
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
            }
        });
        showCameraAlbum.show();
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
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(MyViewProfileActivity.this, CACHE_PIC_NAME_TEMP));
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
                                photo = BitmapFactory.decodeStream(MyViewProfileActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
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
        int width = cniMain.getWidth();
        int height = cniMain.getHeight();

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
        List<ResolveInfo> list = MyViewProfileActivity.this.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_no_found_reduce), Toast.LENGTH_SHORT).show();
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
            File f = PicturesCacheUtil.getCachePicFileByName(MyViewProfileActivity.this, CACHE_PIC_NAME);
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
            cniMain.setImageBitmap(photo);
        }
    }

    private void uploadImage() {

        if (mCropImagedUri == null) {
            return;
        }
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
        File file = new File(path);
        isUploadImage = true;
//        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!file.exists()) {
            return;
        }
        progressDialog.show();

        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadPersonalPicture" + MainActivity.getUser().getUser_id());
        params.put("mimeType", "image/png");
        params.put("file", file);
        params.put("user_id", MainActivity.getUser().getUser_id());


        new HttpTools(this).upload(Constant.API_UPLOAD_PROFILE_PICTURE, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");
                    if (responseStatus.equals("Fail")) {
                        Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_update_proPicFail), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_updateProPicSuccess), Toast.LENGTH_SHORT).show();
                        isUploadImageSuccess = true;
                        Intent intent = new Intent();
                        intent.putExtra("name", etFirstName.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private MyDialog pickDateTimeDialog;
    String strData = null;

    private void showDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        Timestamp ts;
        if (strData == null) {
            if (TextUtils.isEmpty(tvBirthday.getText().toString())) {
                ts = new Timestamp(System.currentTimeMillis());
            } else {
                ts = Timestamp.valueOf(tvBirthday.getText().toString() + " 00:00:00");
            }
        } else {
            ts = Timestamp.valueOf(strData);
        }

        Calendar mCalendar = Calendar.getInstance(TimeZone.getDefault());
        mCalendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
        datePicker.setCalendar(mCalendar);

        pickDateTimeDialog = new MyDialog(this, null, dateTimePicker);
        pickDateTimeDialog.setButtonAccept(getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, datePicker.getYear());
                mCalendar.set(Calendar.MONTH, datePicker.getMonth());
                mCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDay());
                if (!MyDateUtils.isBeforeDate(mCalendar.getTimeInMillis())) {
                    MessageUtil.showMessage(MyViewProfileActivity.this, R.string.text_wrong_data);
                    return;
                }
                String dateDesc = MyDateUtils.getLocalDateStringFromLocal(MyViewProfileActivity.this, mCalendar.getTimeInMillis());
                strData = MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis());
                SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyy-MM-dd");
                tvBirthday.setText(defaultDateFormat.format(mCalendar.getTime()));
                int age = Integer.parseInt(new SimpleDateFormat("yyyy").format(new java.util.Date())) - Integer.parseInt(new SimpleDateFormat("yyy").format(mCalendar.getTime()));
//                tvAge.setText(String.valueOf(age));
            }
        });
        pickDateTimeDialog.setButtonCancel(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
            }
        });

        pickDateTimeDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
