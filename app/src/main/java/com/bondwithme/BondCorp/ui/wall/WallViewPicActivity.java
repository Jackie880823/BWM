package com.bondwithme.BondCorp.ui.wall;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.App;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.WallViewPicPagerAdapter;
import com.bondwithme.BondCorp.entity.PhotoEntity;
import com.bondwithme.BondCorp.http.PicturesCacheUtil;
import com.bondwithme.BondCorp.ui.BaseActivity;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.MessageUtil;
import com.bondwithme.BondCorp.widget.MyDialog;
import com.bondwithme.BondCorp.widget.ViewPagerFixed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.Dialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WallViewPicActivity extends BaseActivity {

    private static final String TAG = WallViewPicActivity.class.getSimpleName();

    private ViewPagerFixed viewPager;
    private String request_url;
    private LayoutInflater layoutInflater;
    private List<PhotoEntity> data;
    private WallViewPicPagerAdapter wallViewPicPagerAdapter;

    private static final int EDIT_CAPTION = 1;

    private Dialog dialog;

    private View vProgress;

    private String user_id;

    private int position;

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
        rightButton.setImageResource(R.drawable.option_dots_view);
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {
//        showDialog();
        popUpMenu();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }


    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        request_url = bundle.getString(Constant.REQUEST_URL);
        user_id = bundle.getString(Constant.USER_ID);

        /**add by Jackie*/
        position = bundle.getInt(Constant.PHOTO_POSITION, -1);
        /**add by Jackie*/

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
                LogUtil.d(TAG, "------------###" + response);
                try {
                    GsonBuilder gsonb = new GsonBuilder();

                    Gson gson = gsonb.create();
                    //这个接口返回的数据和ViewOriginalPicesMainFragment（其他查看大图的接口）返回的数据不同。
                    data = gson.fromJson(response, new TypeToken<ArrayList<PhotoEntity>>() {
                        }.getType());

                    if (data.size() > 0)
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

        /** add by Jackie **/
        if (position >= 0 && position < data.size()) {
            viewPager.setCurrentItem(position);
        }
        /** add by Jackie **/
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
                    String path = PicturesCacheUtil.getPicPath(this,"wall");
                    PicturesCacheUtil.saveToFile(path, bitmap);
                    PicturesCacheUtil.saveImageToGallery(this, path, "wall");
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

        deleteDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
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
                    /** setResult务必添加getIntent()的参数，这里封装了返回上一Activity的必要数据 */
                    setResult(RESULT_OK, getIntent());
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


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void popUpMenu()
    {
        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(this, rightButton);
        popupMenu.getMenuInflater().inflate(R.menu.wall_view_pic_menu, popupMenu.getMenu());

        if (!App.getLoginedUser().getUser_id().equals(user_id))
        {
            popupMenu.getMenu().findItem(R.id.menu_edit_this_post).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_delete_this_post).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_save_photos:
                        savePhoto();
                        break;
                    case R.id.menu_edit_this_post:
                        if (data.size() != 0)
                        {
                            TextView tvCaption = (TextView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.tv_content);
                            TextView tvCaptionAll = (TextView)wallViewPicPagerAdapter.getmCurrentView().findViewById(R.id.tv_content_all);

                            Intent intent = new Intent(WallViewPicActivity.this, WallEditCaptionActivity.class);
                            intent.putExtra("caption", tvCaptionAll.getText().toString());
                            intent.putExtra("photo_id", data.get(viewPager.getCurrentItem()).getPhoto_id());
                            startActivityForResult(intent, EDIT_CAPTION);
                        }
                        break;
                    case R.id.menu_delete_this_post:
                        deletePhoto();
                        break;
                }
                return true;
            }
        });

        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        popupMenu.show();
    }
}
