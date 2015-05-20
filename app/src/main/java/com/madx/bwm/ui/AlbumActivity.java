package com.madx.bwm.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ListView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.AlbumAdapter;
import com.madx.bwm.entity.AlbumEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by quankun on 15/5/19.
 */
public class AlbumActivity extends BaseActivity {
    private ListView albumListView;
    private List<AlbumEntity> albumEntityList;
    private Context mContext;
    private String memberId;
    private String selectYear;
    private AlbumAdapter albumAdapter;
    private ProgressDialog mProgressDialog;

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
    protected void setTitle() {

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
        mContext = this;
        memberId = getIntent().getStringExtra("member_id");
        albumListView = getViewById(R.id.lv);
        albumEntityList = new ArrayList<>();
        selectYear = Calendar.getInstance().get(Calendar.YEAR) + "";
        albumAdapter = new AlbumAdapter(mContext, albumEntityList, memberId);
        mProgressDialog = new ProgressDialog(mContext, getString(R.string.text_loading));
        mProgressDialog.show();
    }

    @Override
    public void requestData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("viewer_id", MainActivity.getUser().getUser_id());
                params.put("member_id", memberId);
                params.put("year", selectYear);
                String url = UrlUtil.generateUrl(Constant.API_GET_YEAR_ALBUM_LIST, params);

                new HttpTools(mContext).get(url, null, new HttpCallback() {
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
                        if (TextUtils.isEmpty(response) || "[]".equals(response)) {
                            MessageUtil.showMessage(mContext, getResources().getString(R.string.text_no_album_gallery));
                            return;
                        }
                        Gson gson = new GsonBuilder().create();
                        albumEntityList = gson.fromJson(response, new TypeToken<ArrayList<AlbumEntity>>() {
                        }.getType());

                    }

                    @Override
                    public void onError(Exception e) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
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
    public void onFragmentInteraction(Uri uri) {

    }
}
