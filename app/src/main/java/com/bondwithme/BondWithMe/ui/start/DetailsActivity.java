package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.PersonalPictureActivity;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.widget.CircularImageView;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.material.widget.Dialog;
import com.material.widget.PaperButton;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = DetailsActivity.class.getSimpleName();
    private final static String COMPLETE_USER = TAG + "_COMPLETE_USER";

    private final static int HANDLER_COMPLETE_PROFILE_SUCCESS = 100;
    private final static int ERROR = -1;


    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

    private Uri mCropImagedUri;
    private String imagePath;

    private UserEntity userEntity;
    private AppTokenEntity tokenEntity;

    private String strFirstName;
    private String strLastName;
    private String strGender;

    private FrameLayout flPic;
    private CircularImageView civPic;
    private RelativeLayout rlPic;
    private EditText etFirst;
    private TextView tvFirstNameError;
    private EditText etLast;
    private RelativeLayout rlRB;
    private RadioGroup rgMain;
    private PaperButton brNext;
    private RelativeLayout rlProgress;

    private Dialog showCameraAlbum;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case HANDLER_COMPLETE_PROFILE_SUCCESS:
                    App.userLoginSuccessed(DetailsActivity.this,userEntity, tokenEntity);
                    break;

                case ERROR:
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_details;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_start_details));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_gradient_color_green_normal);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        if (mSavedInstanceState != null)
        {
            userEntity = (UserEntity) mSavedInstanceState.getSerializable(Constant.LOGIN_USER);
            tokenEntity = (AppTokenEntity) mSavedInstanceState.getSerializable(Constant.HTTP_TOKEN);
            mCropImagedUri = Uri.parse(mSavedInstanceState.getString("uri"));
        }
        getData();

        flPic = getViewById(R.id.fl_pic);
        civPic = getViewById(R.id.civ_pic);
        rlPic = getViewById(R.id.rl_pic);
        etFirst = getViewById(R.id.et_first_name);
        tvFirstNameError = getViewById(R.id.tv_first_name_error);
        etLast = getViewById(R.id.et_last_name);
        rlRB = getViewById(R.id.rl_rb);
        rgMain = getViewById(R.id.rg_main);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);

        rlPic.setOnClickListener(this);
        civPic.setOnClickListener(this);
        brNext.setOnClickListener(this);

        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
                        strGender = "M";
                        rlRB.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                        break;
                    case R.id.rb_femal:
                        rlRB.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                        strGender = "F";
                        break;
                }
            }
        });

        etFirst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etFirst.getText().toString()))
                {
                    etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvFirstNameError.setVisibility(View.VISIBLE);
                }
                else
                {
                    etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                    tvFirstNameError.setVisibility(View.GONE);
                }
            }
        });

        etLast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etLast.getText().toString()))
                {
                    etLast.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                }
                else
                {
                    etLast.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                }
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.rl_pic:
                showCameraAlbum();
                break;

            case R.id.civ_pic:
                showCameraAlbum();
                break;

            case R.id.br_next:
                doHttpCompleteProfile();
                break;

            default:
                break;
        }
    }



    private void getData() {
        Intent intent = getIntent();
        userEntity = (UserEntity) intent.getExtras().getSerializable(Constant.LOGIN_USER);
        tokenEntity = (AppTokenEntity) intent.getExtras().getSerializable(Constant.HTTP_TOKEN);
        App.initToken(userEntity.getUser_login_id(), tokenEntity);
    }

    private void doHttpChangeUI()
    {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
    }

    private void finishHttpChangeUI()
    {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
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
                        .fromFile(PicturesCacheUtil.getCachePicFileByName(DetailsActivity.this,
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
                Intent intent = new Intent(DetailsActivity.this, SelectPhotosActivity.class);
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
        Log.i(TAG, "startPhotoZoom& uri: " + uri);
        String path = LocalImageLoader.compressBitmap(this, uri, 400, 480, false);
        Uri source = Uri.fromFile(new File(path));
        File f = PicturesCacheUtil.getCachePicFileByName(DetailsActivity.this, CACHE_PIC_NAME);
        mCropImagedUri = Uri.fromFile(f);
        Log.i(TAG, "startPhotoZoom& cropImageUri: " + mCropImagedUri);
        Crop.of(source, mCropImagedUri).asSquare().start(this, REQUEST_HEAD_FINAL);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private void setPicToView(Bitmap photo) {
        if (photo != null) {
            civPic.setImageBitmap(photo);
            civPic.setVisibility(View.VISIBLE);
            rlPic.setVisibility(View.INVISIBLE);
        }
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
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(DetailsActivity.this,
                            CACHE_PIC_NAME_TEMP));
                    uri = Uri.parse(ImageDownloader.Scheme.FILE.wrap(uri.getPath()));
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
                                photo = BitmapFactory.decodeStream(DetailsActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
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

    private void doHttpCompleteProfile()
    {
        strFirstName = etFirst.getText().toString();
        strLastName = etLast.getText().toString();

        if (checkInput())
        {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userEntity.getUser_id());
            params.put("user_given_name", strFirstName);
            params.put("user_surname", strLastName);
            params.put("user_gender", strGender);

            if (mCropImagedUri != null)
            {
                String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
                File file = new File(path);
                if (!file.exists())
                {
                    return;
                }
                params.put("fileKey", "file");
                params.put("fileName", "PersonalPicture" + userEntity.getUser_id());
                params.put("mimeType", "image/png");
                params.put("file", file);
            }

            new HttpTools(this).upload(Constant.API_START_COMPLETE_PROFILE, params, COMPLETE_USER, new HttpCallback() {
                @Override
                public void onStart() {
                    doHttpChangeUI();
                }

                @Override
                public void onFinish() {
                    finishHttpChangeUI();
                }

                @Override
                public void onResult(String response) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();

                    userEntity = gson.fromJson(response, UserEntity.class);
                    if (!TextUtils.isEmpty(userEntity.getUser_id()))
                    {
                        handler.sendEmptyMessage(HANDLER_COMPLETE_PROFILE_SUCCESS);
                    }
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });

        }
        else
        {
            if (TextUtils.isEmpty(strFirstName))
            {
                etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                tvFirstNameError.setVisibility(View.VISIBLE);
            }
            else
            {
                etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvFirstNameError.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(strLastName))
            {
                etLast.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                etLast.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

            if (TextUtils.isEmpty(strGender))
            {
                rlRB.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            }
            else
            {
                rlRB.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
            }
        }
    }

    private boolean checkInput() {
        return !MyTextUtil.isHasEmpty(strFirstName, strLastName, strGender);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constant.LOGIN_USER, userEntity);
        outState.putSerializable(Constant.HTTP_TOKEN, tokenEntity);
        if (mCropImagedUri != null)
        {
            outState.putString("uri", mCropImagedUri.toString());
        }
    }
}
