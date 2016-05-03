package com.madxstudio.co8.ui.start;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.GsonBuilder;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.OrgSearchEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.PicturesCacheUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.CountryCodeActivity;
import com.madxstudio.co8.ui.PickAndCropPictureActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.material.widget.Dialog;
import com.material.widget.PaperButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateNewOrgActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = CreateNewOrgActivity.class.getSimpleName();
    private final static String COMPLETE_USER = TAG + "_COMPLETE_USER";
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
    private static final int GET_COUNTRY_CODE = 0x11;
    private final static int HANDLER_COMPLETE_PROFILE_SUCCESS = 0x12;
    private final static int GET_CREATE_ORG_EXIST = 0x13;
    private final static int GET_DATA_ERROR = 0x10;

    private Uri mCropImagedUri;
    private String imagePath;

    private String strFirstName;
    private String strLastName;

    //    private NetworkImageView civPic;
    private ImageView rlPic;
    private EditText etFirst;
    private TextView tvFirstNameError;
    private TextView etLast;
    private PaperButton brNext;
    private RelativeLayout rlProgress;

    private MyDialog showCameraAlbum;
    private Context mContext;
    private String userId;
    private UserEntity userEntity;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_COMPLETE_PROFILE_SUCCESS:
                    Intent intent = getIntent();
                    boolean isSignUp = intent.getBooleanExtra(Constant.IS_SIGN_UP, false);
                    if (isSignUp) {
                        if (userEntity == null) {
                            userEntity = (UserEntity) intent.getExtras().getSerializable(Constant.LOGIN_USER);
                        }
                        AppTokenEntity tokenEntity = (AppTokenEntity) intent.getExtras().getSerializable(Constant.HTTP_TOKEN);
                        userEntity.setShow_tip(true);//从登陆流程进入的必须显示tip，此值作为判断依据。
                        userEntity.setShow_add_member(true);
                        App.userLoginSuccessed(CreateNewOrgActivity.this, userEntity, tokenEntity);
                    } else {
                        OrgSearchEntity searchEntity = (OrgSearchEntity) msg.obj;
                        Intent intent1 = new Intent();
                        intent1.putExtra(Constant.CREATE_COUNTRY_NAME, searchEntity);
                        setResult(RESULT_OK, intent1);
                        if (userEntity != null) {
                            App.changeLoginedUser(userEntity);
                        }
                        finish();
                    }
                    break;

                case GET_DATA_ERROR:
                    break;
                case GET_CREATE_ORG_EXIST:
                    String message = (String) msg.obj;
                    if (!TextUtils.isEmpty(message)) {
                        MessageUtil.getInstance().showShortToast(message);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public int getLayout() {
        return R.layout.activity_create_new_org;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText("Create New");
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_bg_color_login_normal);
        tvTitle.setTextColor(getResources().getColor(R.color.login_text_bg_color));
        leftButton.setImageResource(R.drawable.co8_back_button);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        mContext = this;
//        civPic = getViewById(R.id.civ_pic);
        rlPic = getViewById(R.id.rl_pic);
        etFirst = getViewById(R.id.et_first_name);
        tvFirstNameError = getViewById(R.id.tv_first_name_error);
        etLast = getViewById(R.id.et_last_name);
        brNext = getViewById(R.id.br_next);
        rlProgress = getViewById(R.id.rl_progress);
        userId = getIntent().getStringExtra(Constant.PARAM_USER_ID);
        rlPic.setOnClickListener(this);
//        civPic.setOnClickListener(this);
        brNext.setOnClickListener(this);
        etLast.setOnClickListener(this);

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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_pic:
                showCameraAlbum(v);
                break;

            case R.id.civ_pic:
                showCameraAlbum(v);
                break;

            case R.id.br_next:
                doHttpCompleteProfile();
                break;
            case R.id.et_last_name:
                Intent intent = new Intent(mContext, CountryCodeActivity.class);
                startActivityForResult(intent, GET_COUNTRY_CODE);
                break;
            default:
                break;
        }
    }

    private void doHttpChangeUI() {
        rlProgress.setVisibility(View.VISIBLE);
        brNext.setClickable(false);
    }

    private void finishHttpChangeUI() {
        rlProgress.setVisibility(View.GONE);
        brNext.setClickable(true);
    }

    private void showCameraAlbum(final View view) {
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
                Intent intent = new Intent(mContext, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, view.getWidth());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, view.getHeight());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                startActivityForResult(intent, REQUEST_HEAD_CAMERA);
                showCameraAlbum = null;
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(mContext, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_PHOTO);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, view.getWidth());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, view.getHeight());
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                startActivityForResult(intent, REQUEST_HEAD_CAMERA);
                showCameraAlbum = null;
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
     */
    public void startPhotoZoom() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cloneFrom(UniversalImageLoaderUtil.options);
        builder.showImageOnFail(R.drawable.ic_profile_title_bg);
        builder.showImageOnLoading(R.drawable.ic_profile_title_bg);
        builder.showImageForEmptyUri(R.drawable.ic_profile_title_bg);
        DisplayImageOptions options = builder.build();
        ImageLoader.getInstance().displayImage(mCropImagedUri.toString(), rlPic, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {

            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    if (data != null) {
                        mCropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        startPhotoZoom();
                    }
                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
                    if (data != null) {
                        mCropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        startPhotoZoom();
                    }
                    break;

                case GET_COUNTRY_CODE:
                    if (resultCode == RESULT_OK) {
                        etLast.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
                    }
                    break;

                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doHttpCompleteProfile() {
        strFirstName = etFirst.getText().toString();
        strLastName = etLast.getText().toString();
        if (checkInput()) {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("name", strFirstName);
            params.put("country", strLastName);

            if (mCropImagedUri != null) {
                File file = new File(mCropImagedUri.getPath());
                if (!file.exists()) {
                    return;
                }
                params.put("fileKey", "file");
                params.put("fileName", "");
                params.put("mimeType", "image/png");
                params.put("file", file);
            }

            new HttpTools(this).upload(Constant.API_ORG_CREATE, params, COMPLETE_USER, new HttpCallback() {
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
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String profile = jsonObject.optString("profile");
                        if (!TextUtils.isEmpty(profile)) {
                            JSONObject json = new JSONObject(profile);
                            OrgSearchEntity searchEntity = new OrgSearchEntity();
                            searchEntity.setName(json.optString("name", ""));
                            searchEntity.setId(json.optString("id", ""));
                            String user = jsonObject.optString("user");
                            if (!TextUtils.isEmpty(user)) {
                                userEntity = new GsonBuilder().create().fromJson(user, UserEntity.class);
                            }
                            Message.obtain(handler, HANDLER_COMPLETE_PROFILE_SUCCESS, searchEntity).sendToTarget();
                            return;
                        }
//                        String status_code = jsonObject.optString("response_status_code");
                        String response_status = jsonObject.optString("response_status");
                        String response_message = jsonObject.optString("response_message");
                        if ("Fail".equalsIgnoreCase(response_status)) {
                            Message.obtain(handler, GET_CREATE_ORG_EXIST, response_message).sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    handler.sendEmptyMessage(GET_DATA_ERROR);
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
        }
    }

    private boolean checkInput() {
        return !MyTextUtil.isHasEmpty(strFirstName, strLastName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
