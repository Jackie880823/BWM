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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.MediaData;
import com.madxstudio.co8.http.PicturesCacheUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.CountryCodeActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.MyTextUtil;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.CircularImageView;
import com.madxstudio.co8.widget.MyDialog;
import com.material.widget.Dialog;
import com.material.widget.PaperButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CreateNewOrgActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = CreateNewOrgActivity.class.getSimpleName();
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
    private static final int GET_COUNTRY_CODE = 0x11;

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

    private Dialog showCameraAlbum;
    private Context mContext;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_COMPLETE_PROFILE_SUCCESS:
                    //
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
        leftButton.setVisibility(View.INVISIBLE);
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
                showCameraAlbum();
                break;

            case R.id.civ_pic:
                showCameraAlbum();
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
                        .fromFile(PicturesCacheUtil.getCachePicFileByName(CreateNewOrgActivity.this,
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
                Intent intent = new Intent(CreateNewOrgActivity.this, SelectPhotosActivity.class);
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
     * @throws IOException
     */
    public void startPhotoZoom(Uri uri) throws IOException {
        if (uri == null || uri.getPath() == null) {
            return;
        }
//        String path = LocalImageLoader.compressBitmap(this, uri, 400, 200, false);
//        Uri source = Uri.fromFile(new File(path));
//        File f = PicturesCacheUtil.getCachePicFileByName(CreateNewOrgActivity.this, CACHE_PIC_NAME, true);
//        ImageLoader.getInstance().displayImage(uri.toString(), rlPic, UniversalImageLoaderUtil.options);
        Log.i("aaa111", uri.toString());
        String path = LocalImageLoader.compressBitmap(mContext, FileUtil.getRealPathFromURI(mContext, uri), 480, 800, false);
        File file = new File(path);

        mCropImagedUri = Uri.fromFile(file);
        Log.i("aaa222", mCropImagedUri.toString());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap photo = BitmapFactory.decodeStream(CreateNewOrgActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
        if (photo != null) {
            rlPic.setImageBitmap(photo);
        }
//        Crop.of(source, mCropImagedUri).asSquare().start(this, REQUEST_HEAD_FINAL);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private void setPicToView(Bitmap photo) {
        if (photo != null) {
            rlPic.setImageBitmap(photo);
//            civPic.setVisibility(View.VISIBLE);
//            rlPic.setVisibility(View.INVISIBLE);
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
                            startPhotoZoom(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(CreateNewOrgActivity.this,
                            CACHE_PIC_NAME_TEMP, true));
//                    uri = Uri.parse(ImageDownloader.Scheme.FILE.wrap(uri.getPath()));
                    Log.i("aaaaa", uri.toString());
                    if (new File(uri.getPath()).exists()) {
                        try {
                            startPhotoZoom(uri);
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
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(CreateNewOrgActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case GET_COUNTRY_CODE:
                    if (resultCode == RESULT_OK) {
                        etLast.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
//                        data.getStringExtra(CountryCodeActivity.CODE));
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
            Intent intent = new Intent();
            intent.putExtra(Constant.CREATE_COUNTRY_NAME, strFirstName);
            setResult(RESULT_OK, intent);
            finish();
//            Map<String, Object> params = new HashMap<>();
//            params.put("user_id", "");
//            params.put("user_given_name", strFirstName);
//            params.put("user_surname", strLastName);
//
//            if (mCropImagedUri != null) {
//                String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
//                File file = new File(path);
//                if (!file.exists()) {
//                    return;
//                }
//                params.put("fileKey", "file");
//                params.put("fileName", "");
//                params.put("mimeType", "image/png");
//                params.put("file", file);
//            }
//
//            new HttpTools(this).upload(Constant.API_START_COMPLETE_PROFILE, params, COMPLETE_USER, new HttpCallback() {
//                @Override
//                public void onStart() {
//                    doHttpChangeUI();
//                }
//
//                @Override
//                public void onFinish() {
//                    finishHttpChangeUI();
//                }
//
//                @Override
//                public void onResult(String response) {
//                    GsonBuilder gsonb = new GsonBuilder();
//                    Gson gson = gsonb.create();
//
//                    userEntity = gson.fromJson(response, UserEntity.class);
//                    if (!TextUtils.isEmpty(userEntity.getUser_id())) {
//                        handler.sendEmptyMessage(HANDLER_COMPLETE_PROFILE_SUCCESS);
//                    }
//                }
//
//                @Override
//                public void onError(Exception e) {
//
//                }
//
//                @Override
//                public void onCancelled() {
//
//                }
//
//                @Override
//                public void onLoading(long count, long current) {
//
//                }
//            });

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
