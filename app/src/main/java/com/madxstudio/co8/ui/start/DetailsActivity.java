package com.madxstudio.co8.ui.start;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.OrgSearchEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.PicturesCacheUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.OrgSearchActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.widget.CircularImageView;
import com.madxstudio.co8.widget.MyDialog;
import com.material.widget.Dialog;
import com.material.widget.PaperButton;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends BaseActivity implements View.OnClickListener {

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
//    private String strGender;

    private CircularImageView civPic;
    private RelativeLayout rlPic;
    private EditText etFirst;
    private TextView tvFirstNameError;
    private EditText etLast;
    //    private RelativeLayout rlRB;
//    private RadioGroup rgMain;
    private PaperButton brNext;
    private RelativeLayout rlProgress;
    private TextView detail_new_org;

    private TextView et_organisation_name;
    private OrgSearchEntity searchEntity;

    private Dialog showCameraAlbum;
    private boolean isCreateNewOrg = false;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_COMPLETE_PROFILE_SUCCESS:
                    if (isCreateNewOrg) {
                        Intent intent = new Intent(DetailsActivity.this, CreateNewOrgActivity.class);
                        intent.putExtra(Constant.LOGIN_USER, userEntity);
                        intent.putExtra(Constant.HTTP_TOKEN, tokenEntity);
                        intent.putExtra(Constant.IS_SIGN_UP, true);
                        intent.putExtra(Constant.PARAM_USER_ID, userEntity.getUser_id());
                        startActivity(intent);
                    } else {
                        userEntity.setShow_tip(true);//从登陆流程进入的必须显示tip，此值作为判断依据。
                        userEntity.setShow_add_member(true);
                        App.userLoginSuccessed(DetailsActivity.this, userEntity, tokenEntity);
                    }
                    break;

                case ERROR:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

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
        changeTitleColor(R.color.btn_bg_color_login_normal);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.login_text_bg_color));
        leftButton.setImageResource(R.drawable.co8_back_button);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        if (mSavedInstanceState != null) {
            userEntity = (UserEntity) mSavedInstanceState.getSerializable(Constant.LOGIN_USER);
            tokenEntity = (AppTokenEntity) mSavedInstanceState.getSerializable(Constant.HTTP_TOKEN);
            if (!TextUtils.isEmpty(mSavedInstanceState.getString("uri"))) {
                mCropImagedUri = Uri.parse(mSavedInstanceState.getString("uri"));
            }
        }
        getData();

        civPic = getViewById(R.id.civ_pic);
        rlPic = getViewById(R.id.rl_pic);
        etFirst = getViewById(R.id.et_first_name);
        tvFirstNameError = getViewById(R.id.tv_first_name_error);
        etLast = getViewById(R.id.et_last_name);
//        rlRB = getViewById(R.id.rl_rb);
//        rgMain = getViewById(R.id.rg_main);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);
        et_organisation_name = getViewById(R.id.et_organisation_name);
        detail_new_org = getViewById(R.id.detail_new_org);

        detail_new_org.setText(Html.fromHtml(getResources().getString(R.string.text_sign_up_detail_org)));
        rlPic.setOnClickListener(this);
        civPic.setOnClickListener(this);
        brNext.setOnClickListener(this);
        detail_new_org.setOnClickListener(this);
        et_organisation_name.setOnClickListener(this);

        etFirst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etFirst.getText().toString())) {
                    etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                    tvFirstNameError.setVisibility(View.VISIBLE);
                } else {
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
                if (TextUtils.isEmpty(etLast.getText().toString())) {
                    etLast.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                } else {
                    etLast.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_pic:
                showCameraAlbum();
                break;

            case R.id.civ_pic:
                showCameraAlbum();
                break;

            case R.id.br_next:
                doHttpCompleteProfile();
                break;

            case R.id.detail_new_org:
//                Intent intent = new Intent(DetailsActivity.this, CreateNewOrgActivity.class);
//                intent.putExtra(Constant.PARAM_USER_ID, userEntity.getUser_id());
//                startActivityForResult(intent, Constant.CREATE_NEW_ORG);
                isCreateNewOrg = true;
                doHttpCompleteProfile();
                break;
            case R.id.et_organisation_name:
                startActivityForResult(new Intent(DetailsActivity.this, OrgSearchActivity.class), Constant.SEARCH_ORG_DATA);
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

    private void doHttpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
    }

    private void finishHttpChangeUI() {
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
                                CACHE_PIC_NAME_TEMP, true)));
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
        File f = PicturesCacheUtil.getCachePicFileByName(DetailsActivity.this, CACHE_PIC_NAME, true);
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
        if (RESULT_OK == resultCode) {

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
                            CACHE_PIC_NAME_TEMP, true));
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
                            e.printStackTrace();
                        }
                    }
                    break;
                case Constant.CREATE_NEW_ORG:
                    if (data != null) {
                        searchEntity = (OrgSearchEntity) data.getSerializableExtra(Constant.CREATE_COUNTRY_NAME);
                        if (searchEntity != null) {
                            et_organisation_name.setText(searchEntity.getName());
                        }
                    }
                    break;
                case Constant.SEARCH_ORG_DATA:
                    if (data != null) {
                        searchEntity = (OrgSearchEntity) data.getSerializableExtra(Constant.CREATE_COUNTRY_NAME);
                        if (searchEntity != null) {
                            et_organisation_name.setText(searchEntity.getName());
                        }
                    }
                    break;
                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCreateNewOrg = false;
    }

    private void doHttpCompleteProfile() {
        strFirstName = etFirst.getText().toString();
        strLastName = etLast.getText().toString();

        if (checkInput()) {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userEntity.getUser_id());
            params.put("user_given_name", strFirstName);
            params.put("user_surname", strLastName);
            if (searchEntity != null) {
                params.put("org_id", searchEntity.getId());
            }

            if (mCropImagedUri != null) {
                String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
                File file = new File(path);
                if (!file.exists()) {
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
                    if (!TextUtils.isEmpty(userEntity.getUser_id())) {
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

        } else {
            if (TextUtils.isEmpty(strFirstName)) {
                etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_red);
                tvFirstNameError.setVisibility(View.VISIBLE);
            } else {
                etFirst.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
                tvFirstNameError.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(strLastName)) {
                etLast.setBackgroundResource(R.drawable.bg_stroke_corners_red);
            } else {
                etLast.setBackgroundResource(R.drawable.bg_stroke_corners_gray);
            }

//            if (TextUtils.isEmpty(strGender)) {
//                rlRB.setBackgroundResource(R.drawable.bg_stroke_corners_red);
//            } else {
//                rlRB.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//            }
        }
    }

    private boolean checkInput() {
        return !MyTextUtil.isHasEmpty(strFirstName, strLastName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constant.LOGIN_USER, userEntity);
        outState.putSerializable(Constant.HTTP_TOKEN, tokenEntity);
        if (mCropImagedUri != null) {
            outState.putString("uri", mCropImagedUri.toString());
        }
    }
}
