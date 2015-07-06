package com.bondwithme.BondWithMe.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.AlbumAdapter;
import com.bondwithme.BondWithMe.entity.AlbumEntity;
import com.bondwithme.BondWithMe.entity.AlbumPhotoEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.PickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quankun on 15/5/19.
 */
public class AlbumActivity extends BaseActivity {
    private static final String Tag = AlbumActivity.class.getSimpleName();
    private ListView albumListView;
    private List<AlbumEntity> albumEntityList;
    private Context mContext;
    private String memberId;
    private String selectYear;
    private AlbumAdapter albumAdapter;
    private ProgressDialog mProgressDialog;
    private static final int GET_DATA = 0X11;
    private LinearLayout no_image_linear;
    private TextView textView;

    public AlbumActivity() {
    }

    @Override
    public int getLayout() {
        return R.layout.activity_album_gallery;
    }

    @Override
    protected void initBottomBar() {

    }
    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.GONE);
        yearButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(selectYear);
    }

    @Override
    protected void titleRightEvent() {
        showSelectDialog();
    }
    //选择年dialog
    private void showSelectDialog() {
        final View selectIntention = LayoutInflater.from(mContext).inflate(R.layout.dialog_album_select_year, null);
        final PickerView year_pv = (PickerView) selectIntention.findViewById(R.id.select_year_picker_view);
        List<String> data = new ArrayList<String>();
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        int maxYear = nowYear + 20;
        for (int i = nowYear - 5; i < maxYear; i++) {
            data.add(i + "");
        }
        year_pv.setData(data);

        new AlertDialog.Builder(mContext).setView(selectIntention)
                .setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        selectYear = year_pv.getSelectData();
                        requestData();
                        mProgressDialog.show();
                        tvTitle.setText(selectYear);
                    }
                })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        })
                .show();
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        tvTitle.setText(selectYear);
//                    }
//                }).show();
//        year_pv.setOnSelectListener(new PickerView.onSelectListener() {
//
//            @Override
//            public void onSelect(String text) {
//                selectYear = text;
//                requestData();
//                mProgressDialog.show();
//                showSelectDialog.dismiss();
//            }
//        });
        year_pv.setSelected(selectYear);
//        showSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                tvTitle.setText(selectYear);
//            }
//        });
//        showSelectDialog.show();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA:
                    List<AlbumEntity> albumList = (List<AlbumEntity>) msg.obj;
                    albumAdapter.addNewData(albumList);
                    break;
            }
        }
    };

    @Override
    public void initView() {
        mContext = this;
        memberId = getIntent().getStringExtra("member_id");
        albumListView = getViewById(R.id.lv);
        no_image_linear = getViewById(R.id.no_image_linear);
        albumEntityList = new ArrayList<>();
        selectYear = Calendar.getInstance().get(Calendar.YEAR) + "";
        albumAdapter = new AlbumAdapter(mContext, albumEntityList, memberId);
        albumListView.setAdapter(albumAdapter);
        mProgressDialog = new ProgressDialog(mContext, getString(R.string.text_loading));
        mProgressDialog.show();

    }

    @Override
    public void requestData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> params = new HashMap<>();
                params.put("viewer_id", MainActivity.getUser().getUser_id());
                params.put("member_id", memberId);
                params.put("year", selectYear);
                String url = UrlUtil.generateUrl(Constant.API_GET_YEAR_ALBUM_LIST, params);
                new HttpTools(mContext).get(url, null,Tag, new HttpCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResult(String response) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        List<AlbumEntity> albumList = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            AlbumEntity albumEntity;
                            JSONObject jsonObject;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                albumEntity = new AlbumEntity();
                                albumEntity.setMonth(jsonObject.optString("month"));
                                albumEntity.setYear(jsonObject.optString("year"));
                                List<AlbumPhotoEntity> photoList = new ArrayList<>();
                                JSONObject json = jsonObject.optJSONObject("photo");
                                albumEntity.setTotal(json.optString("total"));
                                JSONArray photoJSONArray = json.getJSONArray("data");
                                if (photoJSONArray == null || photoJSONArray.length() == 0) {
                                    continue;
                                }
                                AlbumPhotoEntity albumPhotoEntity;
                                JSONObject photoJsonObject;
                                for (int j = 0; j < photoJSONArray.length(); j++) {
                                    photoJsonObject = photoJSONArray.getJSONObject(j);
                                    albumPhotoEntity = new AlbumPhotoEntity();
                                    albumPhotoEntity.setContent_creator_id(photoJsonObject.optString("content_creator_id"));
                                    albumPhotoEntity.setContent_group_flag(photoJsonObject.optString("content_group_flag"));
                                    albumPhotoEntity.setContent_group_id(photoJsonObject.optString("content_group_id"));
                                    albumPhotoEntity.setContent_id(photoJsonObject.optString("content_id"));
                                    albumPhotoEntity.setContent_type(photoJsonObject.optString("content_type"));
                                    albumPhotoEntity.setCreation_date(photoJsonObject.optString("creation_date"));
                                    albumPhotoEntity.setCreation_month(photoJsonObject.optString("creation_month"));
                                    albumPhotoEntity.setFile_id(photoJsonObject.optString("file_id"));
                                    albumPhotoEntity.setGroup_id(photoJsonObject.optString("group_id"));
                                    albumPhotoEntity.setPhoto_caption(photoJsonObject.optString("photo_caption"));
                                    albumPhotoEntity.setPhoto_id(photoJsonObject.optString("photo_id"));
                                    albumPhotoEntity.setPhoto_thumbsize(photoJsonObject.optString("photo_thumbsize"));
                                    photoList.add(albumPhotoEntity);
                                }
                                albumEntity.setPhotoList(photoList);
                                albumList.add(albumEntity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        Gson gson = new GsonBuilder().create();
//                        albumEntityList = gson.fromJson(response, new TypeToken<ArrayList<AlbumEntity>>() {
//                        }.getType());
                        Message.obtain(handler, GET_DATA, albumList).sendToTarget();
                        if (null == albumList || albumList.size() == 0) {
                            no_image_linear.setVisibility(View.VISIBLE);
                        } else {
                            no_image_linear.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        no_image_linear.setVisibility(View.VISIBLE);
                        MessageUtil.showMessage(mContext, getResources().getString(R.string.text_error));
                    }

                    @Override
                    public void onCancelled() {
                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
            }
        }.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                titleLeftEvent();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
