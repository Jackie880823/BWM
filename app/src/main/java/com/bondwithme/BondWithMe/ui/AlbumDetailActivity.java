package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.AlbumDetailAdapter;
import com.bondwithme.BondWithMe.entity.AlbumPhotoEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/5/21.
 */
public class AlbumDetailActivity extends BaseActivity {
    private NetworkImageView imageView;
    private RecyclerView recyclerView;
    private TextView pic_num;
    private Context mContext;
    private String year;
    private String month;
    private String memberId;
    private String fileId;
    private int startIndex = 0;
//    private ProgressDialog mProgressDialog;
    private static final int GET_DATA = 0X11;
    private AlbumDetailAdapter albumDetailAdapter;
    private String total;
    private boolean isPullData = false;
    private boolean isFirstData = true;
    private boolean isLeftMoreData = false;
    private final int RIGHT_SCROLL = 0;
    private final int LEFT_SCROLL = 1;
    private int nowSelectIndex = 0;
    private LinearLayoutManager linearLayoutManager;
    private String TAG;

    @Override
    public int getLayout() {
        return R.layout.activity_aibum_detail;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_album);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
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
                    Map<String, Object> map = (Map) msg.obj;
                    total = (String) map.get("total");
                    List<AlbumPhotoEntity> albumList = (List<AlbumPhotoEntity>) map.get("data");
                    if (null != albumList && albumList.size() > 0) {
                        if (isFirstData) {
                            for (int i = 0; i < albumList.size(); i++) {
                                if (fileId != null && fileId.equals(albumList.get(i).getFile_id())) {
                                    albumDetailAdapter.setSelectPosition(i);
                                    break;
                                }
                            }
                            startIndex++;
                            isFirstData = false;
                            setImageData(fileId);
                        }
                        if (isLeftMoreData) {
                            albumDetailAdapter.setSelectPosition(albumDetailAdapter.getItemCount() + 1);
                            setImageData(albumList.get(0).getFile_id());
                            isLeftMoreData = false;
                        }
                    }

                    albumDetailAdapter.addData(albumList);
                    break;
            }
        }
    };

    @Override
    public void initView() {
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        Intent intent = getIntent();
        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        memberId = intent.getStringExtra("memberId");
        fileId = intent.getStringExtra("fileId");
        imageView = getViewById(R.id.album_image_view);
        recyclerView = getViewById(R.id.album_recycler_view);
        pic_num = getViewById(R.id.pic_num);
//        mProgressDialog = new ProgressDialog(mContext, getString(R.string.text_loading));
//        mProgressDialog.show();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        albumDetailAdapter = new AlbumDetailAdapter(mContext, memberId, new ArrayList<AlbumPhotoEntity>(), linearLayoutManager);
        recyclerView.setAdapter(albumDetailAdapter);
        albumDetailAdapter.setPicClickListener(new AlbumDetailAdapter.SelectViewClickListener() {
            @Override
            public void showOriginalPic(String fileId) {
                setImageData(fileId);
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (albumDetailAdapter.getItemCount() > 0 && (linearLayoutManager.findLastVisibleItemPosition() > (linearLayoutManager.getItemCount() - 5)) && !isPullData) {
                    isPullData = true;
                    requestData();
                }
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float upX = moveX - pressX;
                        if (upX > 20) {
                            doResult(0);
                        } else if (upX < -20) {
                            doResult(1);
                        }
                        break;
                }
                return true;
            }
        });
    }

    float pressX = 0;
    float moveX = 0;

    private void setImageData(String albumFileId) {
        nowSelectIndex = albumDetailAdapter.getSelectPosition();
        pic_num.setText(String.format("<%s/%s>", albumDetailAdapter.getSelectPosition() + 1 + "", total));
        VolleyUtil.initNetworkImageView(mContext, imageView, String.format(Constant.API_GET_PIC, Constant.Module_preview, memberId, albumFileId),
                R.drawable.network_image_default, R.drawable.network_image_default);
    }

    public void doResult(int action) {
        switch (action) {
            case RIGHT_SCROLL:
                if (nowSelectIndex == 0) {

                } else {
                    int index = --nowSelectIndex;
                    albumDetailAdapter.setSelectPosition(index);
                    setImageData(albumDetailAdapter.getAlbumList().get(index).getFile_id());
                }
                break;

            case LEFT_SCROLL:
                if (nowSelectIndex == albumDetailAdapter.getItemCount()) {
                    isLeftMoreData = true;
                    requestData();
                } else {
                    int index = ++nowSelectIndex;
                    albumDetailAdapter.setSelectPosition(index);
                    setImageData(albumDetailAdapter.getAlbumList().get(index).getFile_id());
                }
                break;

        }
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
                params.put("year", year);
                params.put("month", month);
                params.put("start", 20 * startIndex + "");
                params.put("limit", "20");
                String url = UrlUtil.generateUrl(Constant.API_GET_MONTH_ALBUM_LIST, params);
                new HttpTools(mContext).get(url, null, TAG, new HttpCallback() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResult(String response) {
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                        if (isPullData) {
                            startIndex++;
                            isPullData = false;
                        }
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        Map<String, Object> map = new HashMap<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<AlbumPhotoEntity> albumList = gson.fromJson(jsonObject.optString("data"), new TypeToken<ArrayList<AlbumPhotoEntity>>() {
                            }.getType());
                            String total = jsonObject.optString("total");
                            map.put("data", albumList);
                            map.put("total", total);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message.obtain(handler, GET_DATA, map).sendToTarget();
                    }

                    @Override
                    public void onError(Exception e) {
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                    }

                    @Override
                    public void onCancelled() {
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
            }
        }.start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
