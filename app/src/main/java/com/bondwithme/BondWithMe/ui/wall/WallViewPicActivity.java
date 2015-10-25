package com.bondwithme.BondWithMe.ui.wall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.WallViewPicPagerAdapter;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.Dialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WallViewPicActivity extends BaseActivity {

    private static final String TAG = WallViewPicActivity.class.getSimpleName();

    private ViewPager viewPager;
    private String request_url;
    private LayoutInflater layoutInflater;
    private List<PhotoEntity> data;
    private WallViewPicPagerAdapter wallViewPicPagerAdapter;

    private static final int EDIT_CAPTION = 1;

    private Dialog dialog;

    private View vProgress;

    private String user_id;

    private static final int REQUEST_DATA_SUCCESS = 1;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case REQUEST_DATA_SUCCESS:
                    initAdapter();
                break;
            }
        }
    };
    ;

    @Override
    public int getLayout() {
        return R.layout.activity_wall_view_pic;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        //TODO
        // 需要新的图
        titleBar.setBackgroundColor(Color.BLACK);
        leftButton.setImageResource(R.drawable.back_press);
        rightButton.setImageResource(R.drawable.more);
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {
            showDialog();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }


    @Override
    public void initView() {

        request_url = getIntent().getExtras().getString("request_url");
        user_id = getIntent().getExtras().getString("user_id");
        if (TextUtils.isEmpty(request_url))
        {
            finish();
        }

        layoutInflater = getLayoutInflater();

        viewPager = getViewById(R.id.pager);
        vProgress = getViewById(R.id.rl_progress);

    }

    @Override
    public void requestData() {
        new HttpTools(this).get(request_url, null, this, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                LogUtil.d(getClass().getSimpleName(), response);
                try {
                    GsonBuilder gsonb = new GsonBuilder();
                    //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                    //DateDeserializer ds = new DateDeserializer();
                    //给GsonBuilder方法单独指定Date类型的反序列化方法
                    //gsonb.registerTypeAdapter(Date.class, ds);


                    //这解析。。。。。。。。。什么鬼？？？
                    Gson gson = gsonb.create();
                    if (response.startsWith("{\"data\":")) {
                        JSONObject jsonObject = new JSONObject(response);
                        String dataString = jsonObject.optString("data");
                        data = gson.fromJson(dataString, new TypeToken<ArrayList<PhotoEntity>>() {
                        }.getType());
                    } else {
                        data = gson.fromJson(response, new TypeToken<ArrayList<PhotoEntity>>() {
                        }.getType());
                    }
                    handler.sendEmptyMessage(REQUEST_DATA_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void initAdapter() {
        wallViewPicPagerAdapter = new WallViewPicPagerAdapter(this, layoutInflater, data);
        viewPager.setAdapter(wallViewPicPagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     *
     */
    private void showDialog()
    {
        if (data == null)
            return;

        View selectIntention = layoutInflater.inflate(R.layout.dialog_wall_view_pic, null);

        dialog = new MyDialog(this, null, selectIntention);

        LinearLayout llSave = (LinearLayout) selectIntention.findViewById(R.id.tv_save);
        LinearLayout llEdit = (LinearLayout) selectIntention.findViewById(R.id.tv_edit);
        LinearLayout llDelete = (LinearLayout) selectIntention.findViewById(R.id.tv_delete);

        if (!App.getLoginedUser().getUser_id().equals(user_id))
        {
            llEdit.setVisibility(View.GONE);
            llDelete.setVisibility(View.GONE);
        }

        //保存当前图片
        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }
                savePhoto();
            }
        });

        //编辑描述
        llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }

                if (data.size() != 0)
                {
                    TextView tvCaption = (TextView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.tv_content);
                    TextView tvCaptionAll = (TextView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.tv_content_all);

                    Intent intent = new Intent(WallViewPicActivity.this, WallEditCaptionActivity.class);
                    intent.putExtra("caption", tvCaptionAll.getText().toString());
                    intent.putExtra("photo_id", data.get(viewPager.getCurrentItem()).getPhoto_id());
                    startActivityForResult(intent, EDIT_CAPTION);
                }

            }
        });

        //删除照片
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }
                deletePhoto();
            }
        });

        if (!dialog.isShowing())
        {
            dialog.show();
        }

    }


    /**
     * 照片保存到本地相册
     */
    private void savePhoto() {
        //此方式不好。效率低。缓存文件已经存在 应该直接添加到相册中。
        if (data.size() != 0 )
        {
            ImageView imageView = (ImageView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.iv_pic);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                try {
                    String path = PicturesCacheUtil.saveImageToGallery(this, bitmap, "wall");
                    MessageUtil.showMessage(this, this.getString(R.string.saved_to_path) + path);
                } catch (Exception e) {
                    MessageUtil.showMessage(this, R.string.msg_action_failed);
                }
            }
        }
    }

    /**
     * 上一个界面执行请求，成功更新描述后，更新本地视图
     *
     * @param data
     */
    private void updateCaption(Intent data) {
        if (this.data.size() != 0)
        {
            TextView textView = (TextView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.tv_content);
            TextView textViewAll = (TextView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.tv_content_all);
            textView.setText(data.getStringExtra("update_caption"));
            textViewAll.setText(data.getStringExtra("update_caption"));
            this.data.get(viewPager.getCurrentItem()).setPhoto_caption(data.getStringExtra("update_caption"));
        }
    }

    /**
     * 请求服务器删除 成功
     */
    private void deletePhoto() {

        final Dialog deleteDialog = new MyDialog(WallViewPicActivity.this, R.string.text_wall_view_pic_delete, R.string.text_wall_view_pic_delete_warning);

        deleteDialog.setButtonAccept(R.string.accept, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //请求服务器删除照片
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }

                deletePhotoFromServer();

            }
        });

        deleteDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }
            }
        });
        deleteDialog.show();
    }

    private void deletePhotoFromServer() {

        RequestInfo requestInfo = new RequestInfo();

        if (data.size() != 0) {
            String strId = data.get(viewPager.getCurrentItem()).getPhoto_id();
            if (TextUtils.isEmpty(strId))
            {
                return;
            }
            requestInfo.url = String.format(Constant.API_WALL_DELETE_PHOTO,strId);
        }

        new HttpTools(this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                if (data.size() != 0) {
                    data.remove(viewPager.getCurrentItem());
                    wallViewPicPagerAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case EDIT_CAPTION:
                    updateCaption(data);
                break;
            }
        }
    }
}
