package com.madx.bwm.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.AlbumGalleryAdapter;
import com.madx.bwm.entity.PhotoEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumGalleryActivity extends BaseActivity {

    String memberId;
    private String TAG;
    public List<PhotoEntity> data = new ArrayList<>();//所有照片数据

    public List<PhotoEntity> dataCache = new ArrayList<>();//每个月临时存储的list

    public List<List<PhotoEntity>> listData = new ArrayList<List<PhotoEntity>>();//经过处理后: 每个List = 每个月份所有照片

    ListView lv;

    @Override
    public int getLayout() {
        return R.layout.activity_album_gallery;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_album_gallery));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        memberId = getIntent().getStringExtra("member_id");
        TAG = this.getClass().getSimpleName();
        lv = getViewById(R.id.lv);
    }

    @Override
    public void requestData() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("viewer_id", MainActivity.getUser().getUser_id());
        params.put("member_id", memberId);
        params.put("limit", "6");
        params.put("start", "0");
        String url = UrlUtil.generateUrl(Constant.API_ALBUM_GALLERY, params);

        new HttpTools(AlbumGalleryActivity.this).get(url, null, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                if ("[]".equals(response)) {
                    MessageUtil.showMessage(AlbumGalleryActivity.this, getResources().getString(R.string.text_no_album_gallery));
                    return;
                }

                data = gson.fromJson(response, new TypeToken<ArrayList<PhotoEntity>>() {
                }.getType());

                String creationDate = data.get(0).getCreation_date();//初始化，日期，用作分组

                for (int i = 0; i < data.size(); i++) {
                    if (creationDate.equals(data.get(i).getCreation_date()))//月份相等时
                    {
                        dataCache.add(data.get(i));//相同月份加入缓存中
                    }
                    if (i == data.size() - 1) {
                        listData.add(dataCache);//判断到最后一直是同月份。list加入到list中
                        break;
                    }
                }

                if (listData.size() > 0 && listData != null) {
                    AlbumGalleryAdapter aga = new AlbumGalleryAdapter(AlbumGalleryActivity.this, R.layout.item_album_gallery, listData, memberId);
                    lv.setAdapter(aga);
                }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(AlbumGalleryActivity.this, ViewOriginalPicesActivity.class);

                        List<PhotoEntity> mData = listData.get(position);

                        for (int i = 0; i < mData.size(); i++) {
                            mData.get(i).setUser_id(memberId);
                            mData.get(i).setPhoto_caption(Constant.Module_Original);
                        }
                        intent.putExtra("is_data", true);
                        intent.putExtra("datas", (java.io.Serializable) mData);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(AlbumGalleryActivity.this, getResources().getString(R.string.text_error));
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
    public void onFragmentInteraction(Uri uri) {

    }
}
