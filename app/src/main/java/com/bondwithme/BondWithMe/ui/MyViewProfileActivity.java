package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.DatePicker;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MyViewProfileActivity extends BaseActivity {
    private CircularNetworkImage cniMain;
    private ImageView ivBottomLeft;
    private LinearLayout llResetPassword;
    private TextView etFirstName;
    private TextView etLastName;
    private TextView tvBirthday;
    private TextView tvYearBirthday;
    private TextView rlBirthday;
    private TextView tvGender;
    private TextView rlGender;
    private TextView etEmail;
    private EditText etPhone;
    private EditText etRegion;
    private TextView tvChange;
    private NetworkImageView imProfileImages;
    private NetworkImageView imQrImages;
    private View vProgress;
    private RelativeLayout RlView;
    private Dialog showSelectDialog;
    private Dialog showCameraAlbum;
    private Boolean isUploadName = false;
    private Boolean isEit = false;
    private int headOrBackdropImage;

    private Boolean isUploadNameSuccess = true;
    private Boolean isUploadImageSuccess = false;

    private String userGender;

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_PROFILE_PHOTO = 3;

    Uri mCropImagedUri;
    Uri mBackdropImagedUri;
    private String headImagePath;
    private String backgroundImagePath;
    private Context mContext;
    private String TAG;

    String strDOB;
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
        rightButton.setImageResource(R.drawable.edit_profile);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_my_profile));
    }

    @Override
    protected void titleRightEvent() {
        if(!isEit){
            rightButton.setImageResource(R.drawable.ok_normal);
            tvChange.setVisibility(View.VISIBLE);
            ableEdit(true,RlView);
            isEit = true;
        }else {
            update();
        }
    }

    @Override
    protected void titleLeftEvent() {
        if (isProfileChanged()) {
            showNoFriendDialog();
        } else {
            super.titleLeftEvent();
        }
    }

    private void update(){

        if (mCropImagedUri != null) {
            uploadImage();

        }
        if(mBackdropImagedUri != null){
            uploadBackdropImage();
        }

        //如果没有跟新图片
        if(mBackdropImagedUri == null && mCropImagedUri == null){
            if (isProfileChanged()) {
                updateProfile();

            } else {
                finish();
            }
        }

    }

    private void ableEdit(boolean isEit,ViewGroup viewGroup){
        if(viewGroup == null)return;

        for(int i = 0; i < viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if(view instanceof EditText){
                view.setFocusable(isEit);
                view.setFocusableInTouchMode(isEit);
            }else if(view instanceof ViewGroup){
                this.ableEdit(isEit, (ViewGroup) view);
            }
        }

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

    private int isHeaderChanged() {
        if (mCropImagedUri != null) {
            return 1;
        }else if(mBackdropImagedUri != null){
            return 2;
        }
        return 0;
    }


    private boolean isProfileChanged() {
        if (!TextUtils.isEmpty(etFirstName.getText().toString().trim()) && !etFirstName.getText().toString().trim().equals(MainActivity.getUser().getUser_given_name())) {
            return true;
        } else {
            if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
                return true;
            }
        }
        if (!TextUtils.isEmpty(etLastName.getText().toString().trim()) && !etLastName.getText().toString().trim().equals(MainActivity.getUser().getUser_surname())) {
            return true;
        } else {
            if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
                return true;
            }
        }
        if (!TextUtils.isEmpty(MainActivity.getUser().getUser_dob()) && !strDOB.equals(MainActivity.getUser().getUser_dob())) {
            return true;
        } else {
            if (!TextUtils.isEmpty(strDOB) && !strDOB.equals(MainActivity.getUser().getUser_dob())) {
                return true;
            }
        }
        //性别简写
        String simpleSex = MainActivity.getUser().getUser_gender();
        String tempSex = tvGender.getText().toString().trim();
        String simpleRealitySex = null;
        //实际显示性别转换成简写
        if (tempSex.equals(getResources().getString(R.string.text_female))) {
            simpleRealitySex = "F";
        } else if (tempSex.equals(getResources().getString(R.string.text_male))) {
            simpleRealitySex = "M";
        }
        if (!simpleSex.equals(simpleRealitySex)) {
            return true;
        }
        if (MainActivity.getUser()!=null&&!etEmail.getText().toString().trim().equals(MainActivity.getUser().getUser_email())) {
            return true;
        }
        if (!etRegion.getText().toString().trim().equals(MainActivity.getUser().getUser_location_name())) {
            return true;
        }
        return false;
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    String headUrl;
    String backdropHeadUrl;
    String qrUrl;
    BitmapTools mBitmapTools;
    BitmapTools mBackdropBitmapTools;
    BitmapTools mQrBitmapTools;

    @Override
    public void initView() {

//        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        RlView = (RelativeLayout) findViewById(R.id.rl_view);

        llResetPassword = (LinearLayout)getViewById(R.id.ll_reset_password);



        etFirstName = getViewById(R.id.et_first_name);
        etLastName = getViewById(R.id.et_last_name);
//        tvAge = getViewById(R.id.tv_age);
//        rlAge = getViewById(R.id.rl_age);
        tvBirthday = getViewById(R.id.tv_birthday);
        tvYearBirthday = getViewById(R.id.tv_year_birthday);
        rlBirthday = getViewById(R.id.rl_birthday);
        tvGender = getViewById(R.id.tv_gender);
        rlGender = getViewById(R.id.rl_gender);
        etEmail = getViewById(R.id.et_email);
        etPhone = getViewById(R.id.et_phone);
        etRegion = getViewById(R.id.et_region);
        tvChange = getViewById(R.id.tv_change_text);
        imProfileImages = getViewById(R.id.iv_profile_images);
        imQrImages = getViewById(R.id.iv_profile_qr);
        vProgress = getViewById(R.id.rl_progress);

        headUrl = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id());
        backdropHeadUrl = String.format(Constant.API_GET_PIC_PROFILE, MainActivity.getUser().getUser_id());
        qrUrl = String.format(Constant.API_GET_PROFILE_QR, MainActivity.getUser().getUser_id());

        mBitmapTools = BitmapTools.getInstance(this);
        mBackdropBitmapTools = BitmapTools.getInstance(this);
        mQrBitmapTools = BitmapTools.getInstance(this);
//        VolleyUtil.initNetworkImageView(this, imProfileImages, String.format(Constant.API_GET_PIC_PROFILE, MainActivity.getUser().getUser_id()), 0, 0);
        mBitmapTools.display(cniMain, headUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        mBackdropBitmapTools.display(imProfileImages,backdropHeadUrl,R.drawable.my_profile_bunny,R.drawable.my_profile_bunny);
        mQrBitmapTools.display(imQrImages,qrUrl,R.drawable.qrcode_button,R.drawable.qrcode_button);
        etFirstName.setText(MainActivity.getUser().getUser_given_name());
        etLastName.setText(MainActivity.getUser().getUser_surname());
        tvTitle.setText(MainActivity.getUser().getUser_given_name());

//        tvAge.setText(MainActivity.getUser().getUser_dob());//需要做处理，年转为岁数
        //1990-09-10   1990年
        strDOB = MainActivity.getUser().getUser_dob();
        LogUtil.d(TAG,"strDOB==="+strDOB);
        setTvBirthday(strDOB);

        if ("F".equals(MainActivity.getUser().getUser_gender())) {
            tvGender.setText(getResources().getString(R.string.text_female));
        } else if ("M".equals(MainActivity.getUser().getUser_gender())) {
            tvGender.setText(getResources().getString(R.string.text_male));
        } else {
            tvGender.setText(null);
        }

        etEmail.setText(MainActivity.getUser().getUser_email());
        if (MainActivity.getUser().getUser_phone().length() > 0){
            etPhone.setText("+" + MainActivity.getUser().getUser_country_code() + " " + MainActivity.getUser().getUser_phone());
        }
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
        etRegion.setText(MainActivity.getUser().getUser_location_name());
//        etRegion.setKeyListener(null);
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

            } catch (IOException e) {
            }
        }


        rlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEit)return;

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
                if(!isEit)return;

                headOrBackdropImage = 1;
                showCameraAlbum(REQUEST_HEAD_PHOTO,cniMain.getWidth(),cniMain.getHeight());

            }
        });

        rlBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEit)return;

                showDateTimePicker();
            }
        });
        imProfileImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEit)return;

                headOrBackdropImage = 2;
                showCameraAlbum(REQUEST_PROFILE_PHOTO,imProfileImages.getWidth(),imProfileImages.getHeight());

            }
        });

        ableEdit(false,RlView);
    }

    //设置生日
    private void setTvBirthday(String strDOB) {
        if (strDOB != null && !strDOB.equals("0000-00-00")){
            Date date = null;
            try {
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //不同语言设置不同日期显示
            if (Locale.getDefault().toString().equals("zh_CN")){
//            tvBirthday.setText(date.getYear()+"年" + date.getMonth() + "月" + date.getDay() + "日");
//                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                String time = dateFormat.format(date).toString();
                LogUtil.i("Profile_time",time);
                String birthday = time.substring(5,time.length());
                String YearBirthday = time.substring(0,5);
                tvBirthday.setText(birthday);
                tvYearBirthday.setText(YearBirthday);
            }else {
                int year = date.getYear() + 1900;
//                tvBirthday.setText(date.getDate() + " " + this.getResources().getStringArray(R.array.months)[date.getMonth()]);
                tvBirthday.setText(date.getDate() + " " + MyDateUtils.getMonthNameArray(false)[date.getMonth()]);
                tvYearBirthday.setText(String.valueOf(year));
            }
        }
    }


    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateProfile() {
        if (!isProfileChanged()) {
//            finish();
            return;
        }

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
        jsonParams.put("user_dob", strDOB);
        jsonParams.put("user_email", etEmail.getText().toString());
        jsonParams.put("user_location_name", etRegion.getText().toString());

        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        requestInfo.url = String.format(Constant.API_UPDATE_MY_PROFILE, MainActivity.getUser().getUser_id());
        requestInfo.jsonParam = jsonParamsString;

        new HttpTools(MyViewProfileActivity.this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                isUploadName = true;
                isUploadNameSuccess = false;
                if(vProgress.getVisibility() == View.GONE){
                    vProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                isUploadNameSuccess = true;
                laterFinish();
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
                        App.changeLoginedUser(gson.fromJson(response, UserEntity.class));
                        List userList = new ArrayList<UserEntity>();
                        userList.add(gson.fromJson(response, UserEntity.class));
                        if (intent == null) {
                            intent = new Intent();
                        }

                        intent.putExtra("name", etFirstName.getText().toString());
                        setResult(RESULT_OK, intent);
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

    Intent intent;

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

    private  void showCameraAlbum(final int requestMember,final int photoWidth, final int photoHeight) {
        LogUtil.i("photoWidth*photoHeight_1",photoWidth+"*"+photoHeight);
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
                Intent intent = new Intent(MyViewProfileActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, photoWidth);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, photoHeight);
                if(headOrBackdropImage == 2){
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                    intent.putExtra(PickAndCropPictureActivity.CACHE_PIC_NAME, "_background");
                }
                startActivityForResult(intent, requestMember);
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(MyViewProfileActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_PHOTO);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, photoWidth);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, photoHeight);
                if(headOrBackdropImage == 2){
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                    intent.putExtra(PickAndCropPictureActivity.CACHE_PIC_NAME, "_background");
                }
                startActivityForResult(intent, requestMember);
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
//        Uri imagedUri = null;
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                // 取得裁剪后的图片
                case REQUEST_HEAD_PHOTO:
                    if (data != null) {
                        mCropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        Bitmap photo;
                        try {
                            headImagePath = mCropImagedUri.getPath();
                            if (!TextUtils.isEmpty(headImagePath)) {
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(MyViewProfileActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(cniMain,photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_PROFILE_PHOTO:
                    if (data != null) {
                        mBackdropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        Bitmap photo;
                        try {
                            backgroundImagePath = mBackdropImagedUri.getPath();
                            if (!TextUtils.isEmpty(backgroundImagePath)) {
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(MyViewProfileActivity.this.getContentResolver().openInputStream(mBackdropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(imProfileImages,photo);
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
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private <T extends ImageView>void setPicToView(T view,Bitmap photo) {
        if (photo != null) {
            view.setImageBitmap(photo);
        }
    }

    private void uploadImage() {
        if (mCropImagedUri == null) {
            return;
        }
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
        final File file = new File(path);
//        isUploadImage = true;
//        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!file.exists()) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadPersonalPicture" + MainActivity.getUser().getUser_id());
        params.put("mimeType", "image/png");
        params.put("file", file);
        params.put("user_id", MainActivity.getUser().getUser_id());


        new HttpTools(this).upload(Constant.API_UPLOAD_PROFILE_PICTURE, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                mCropImagedUri = null;
                laterFinish();
            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");
                    if (responseStatus.equals("Fail")) {
//                        Toast.makeText(this, getResources().getString(R.string.text_update_proPicFail), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();

                    } else {
                        /**wing modify 20150625 begin*/
                        //清除缓存
                        mBitmapTools.clearMemoryCache();
                        mBitmapTools.clearDiskCache(null);
//                        mBitmapTools.clearMemoryCache(headUrl);
                        /**wing modify 20150625 end*/

//                        progressDialog.dismiss();
//                        Toast.makeText(this, getResources().getString(R.string.text_updateProPicSuccess), Toast.LENGTH_SHORT).show();
                        isUploadImageSuccess = true;
                        intent = new Intent();
                        intent.putExtra("head_pic", Uri.fromFile(file));
                        setResult(RESULT_OK, intent);
                        if(!isUploadName){
                            updateProfile();
                        }
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
//                Toast.makeText(this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void uploadBackdropImage(){
        if (mBackdropImagedUri == null) {
            return;
        }
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mBackdropImagedUri), 1080, 608, false);
        final File file = new File(path);
//        isUploadImage = true;
//        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!file.exists()) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadProfilePicture" + MainActivity.getUser().getUser_id());
        params.put("mimeType", "image/png");
        params.put("file", file);
        params.put("user_id", MainActivity.getUser().getUser_id());
        new HttpTools(this).upload(Constant.API_POST_PIC_PROFILE, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                if(vProgress.getVisibility() == View.GONE){
                    vProgress.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFinish() {
                mBackdropImagedUri = null;
                laterFinish();
            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");
                    if (responseStatus.equals("Fail")) {
//                        Toast.makeText(this, getResources().getString(R.string.text_update_proPicFail), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();

                    } else {
                        /**wing modify 20150625 begin*/
                        //清除缓存
                        mBackdropBitmapTools.clearMemoryCache();
                        mBackdropBitmapTools.clearDiskCache(null);
//                        mBitmapTools.clearMemoryCache(headUrl);
                        /**wing modify 20150625 end*/

//                        progressDialog.dismiss();
//                        Toast.makeText(this, getResources().getString(R.string.text_updateProPicSuccess), Toast.LENGTH_SHORT).show();
                        isUploadImageSuccess = true;
                        intent = new Intent();
                        intent.putExtra("background_pic", Uri.fromFile(file));
                        setResult(RESULT_OK, intent);
                        if(!isUploadName){
                            updateProfile();
                        }
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
//                Toast.makeText(this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void laterFinish(){
        Boolean uploadFinish;
        if(mCropImagedUri == null && mBackdropImagedUri == null){
            uploadFinish = true;
        }else {
            uploadFinish = false;
        }

        if(uploadFinish == true && isUploadNameSuccess){
            vProgress.setVisibility(View.GONE);
            finish();
        }
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
                ts = Timestamp.valueOf(strDOB + " 00:00:00");
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
                strDOB = defaultDateFormat.format(mCalendar.getTime());
                setTvBirthday(strDOB);
                int age = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())) - Integer.parseInt(new SimpleDateFormat("yyy").format(mCalendar.getTime()));
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
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
        super.onDestroy();
    }
}
