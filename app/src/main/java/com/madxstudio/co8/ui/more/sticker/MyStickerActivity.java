package com.madxstudio.co8.ui.more.sticker;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.MyStickerAdapter;
import com.madxstudio.co8.db.SQLiteHelperOrm;
import com.madxstudio.co8.entity.LocalStickerInfo;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heweidong on 15/6/11.
 */
public class MyStickerActivity extends BaseActivity {

    private final String TAG = "MyStickerActivity";
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private List<LocalStickerInfo> data = new ArrayList<>();
    public static final String ACTION_UPDATE = "ACTION_UPDATE_FROM_MY_STICKER";
    private final int QUERY_STICKER = 1;

    @Override
    public int getLayout() {
        return R.layout.activity_my_sticker;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        tvTitle.setText(getResources().getString(R.string.my_sticker));
        rightButton.setVisibility(View.GONE);
        yearButton.setVisibility(View.VISIBLE);
        yearButton.setTextSize(15);
        yearButton.setText(getResources().getString(R.string.text_sort));

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent(this, StickerSortActivity.class);
        startActivity(intent);
    }

    @Override
    public void initView() {
//        TextView textView = getViewById(R.id.tv_download_stickers);
//        textView.requestFocus();


        try {
            Dao<LocalStickerInfo, String> stickerInfoDao = SQLiteHelperOrm.getHelper(MyStickerActivity.this).getDao(LocalStickerInfo.class);
            QueryBuilder qb = stickerInfoDao.queryBuilder();
            qb.orderBy("order", false).where().eq("loginUserId", MainActivity.getUser().getUser_id());
            data = qb.query();
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);

        }
        if (data != null && data.size() > 0) {
            rvList = getViewById(R.id.rv_my_sticker);
            llm = new LinearLayoutManager(MyStickerActivity.this);
            rvList.setLayoutManager(llm);
            rvList.setHasFixedSize(false);
            rvList.setItemAnimator(null);

            MyStickerAdapter adapter = new MyStickerAdapter(MyStickerActivity.this, data);
            rvList.setAdapter(adapter);

            rvList.post(new Runnable() {
                @Override
                public void run() {
                    (MyStickerActivity.this.getViewById(R.id.sc_my_sticker)).scrollTo(0, 0);
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void requestData() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
