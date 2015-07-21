package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.wall.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.NetworkUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalPictureActivity extends BaseActivity {

    ImageView ivPhone;

    LinearLayout llCamera;
    LinearLayout llAlbum;

    TextView tvSkip;
    Button btnStartingBonding;

    RelativeLayout rlProgress;

    UserEntity userEntity = new UserEntity();
    AppTokenEntity appTokenEntity = new AppTokenEntity();

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

    @Override
    public int getLayout() {
        return R.layout.activity_personal_picture;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_login);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.text_personal_information));
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
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(PersonalPictureActivity.this, CACHE_PIC_NAME_TEMP));
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
                                photo = BitmapFactory.decodeStream(PersonalPictureActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
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
        int width = ivPhone.getWidth();
        int height = ivPhone.getHeight();

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
        List<ResolveInfo> list = PersonalPictureActivity.this.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(PersonalPictureActivity.this, getString(R.string.text_no_found_reduce), Toast.LENGTH_SHORT).show();
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
            File f = PicturesCacheUtil.getCachePicFileByName(PersonalPictureActivity.this, CACHE_PIC_NAME);
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
            ivPhone.setImageBitmap(photo);
        }
    }

    @Override
    protected void titleLeftEvent() {
        super.titleLeftEvent();
    }

    @Override
    public void initView() {

        rlProgress = getViewById(R.id.rl_progress);

        userEntity = (UserEntity) getIntent().getExtras().getSerializable("user");
        appTokenEntity = (AppTokenEntity) getIntent().getExtras().getSerializable("token");

        App.initToken(userEntity.getUser_login_id(), appTokenEntity);

        ivPhone = getViewById(R.id.iv_personal_picture);

        llCamera = getViewById(R.id.ll_from_camera);
        llAlbum = getViewById(R.id.ll_from_album);

        tvSkip = getViewById(R.id.tv_skip);
        btnStartingBonding = getViewById(R.id.btn_starting_bonding);

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);

                //调用前置摄像头
                intent2.putExtra("camerasensortype", 2);

                // 下面这句指定调用相机拍照后的照片存储的路径
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(PersonalPictureActivity.this, CACHE_PIC_NAME_TEMP)));

                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                intent2.putExtra("return-data", false);

                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
            }
        });

        llAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectPhotosActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
            }
        });

        //跳过上传个人头像，直接登录
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(PersonalPictureActivity.this, MainActivity.class);
                /**wing modified for clear activity stacks*/
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                ComponentName cn = intent.getComponent();
//                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                App.changeLoginedUser(userEntity, appTokenEntity);
                startActivity(intent);
                /**wing modified for clear activity stacks*/
                finish();
            }
        });

        //上传个人头像成功后登录
        btnStartingBonding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkConnected(PersonalPictureActivity.this)) {
                    Toast.makeText(PersonalPictureActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImage();//上传照片
            }
        });
    }



    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void uploadImage() {

        if (mCropImagedUri == null) {
            return;
        }

        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
        File file = new File(path);

//        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!file.exists()) {
            return;
        }

        rlProgress.setVisibility(View.VISIBLE);
        btnStartingBonding.setClickable(false);

        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "PersonalPicture" + userEntity.getUser_id());
        params.put("mimeType", "image/png");
        params.put("file", file);
        params.put("user_id", userEntity.getUser_id());

        new HttpTools(this).upload(Constant.API_UPLOAD_PROFILE_PICTURE, params, this,new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                btnStartingBonding.setClickable(true);
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");
                    if ("Fail".equals(responseStatus)) {
                        Toast.makeText(PersonalPictureActivity.this, getString(R.string.text_updateProPicFail), Toast.LENGTH_SHORT).show();
                    } else if ("Success".equals(responseStatus)) {
                        Toast.makeText(PersonalPictureActivity.this, getString(R.string.text_updateProPicSuccess), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PersonalPictureActivity.this, MainActivity.class);
                        /**wing modified for clear activity stacks*/
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        ComponentName cn = intent.getComponent();
//                        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                        App.changeLoginedUser(userEntity, appTokenEntity);//可能会传入没有数据的???
                        startActivity(intent);
                        /**wing modified for clear activity stacks*/
                    } else {
                        Toast.makeText(PersonalPictureActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(PersonalPictureActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PersonalPictureActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                /**wing modified for clear activity stacks*/
                Intent intent = new Intent(PersonalPictureActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                ComponentName cn = intent.getComponent();
//                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                App.changeLoginedUser(userEntity, appTokenEntity);
                startActivity(intent);

//                Intent intentToBeNewRoot = new Intent(this, MainActivity.class);
//                ComponentName cn = intentToBeNewRoot.getComponent();
//                App.changeLoginedUser(userEntity, appTokenEntity);
//                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
//
//                startActivity(mainIntent);
//                finish();
                /**wing modified for clear activity stacks*/
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
