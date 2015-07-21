package com.bondwithme.BondWithMe.ui.more.sticker;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MyStickerAdapter;
import com.bondwithme.BondWithMe.db.SQLiteHelperOrm;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.widget.FullyLinearLayoutManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

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
        yearButton.setText(getResources().getString(R.string.text_sort));
    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent(this,StickerSortActivity.class);
        startActivity(intent);
    }
    @Override
    public void initView() {
        mhandler.sendEmptyMessageDelayed(QUERY_STICKER, 0);
    }
    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_STICKER:
                    try {
                        Dao<LocalStickerInfo, String> stickerInfoDao = SQLiteHelperOrm.getHelper(MyStickerActivity.this).getDao(LocalStickerInfo.class);
                        QueryBuilder qb = stickerInfoDao.queryBuilder();
                        qb.orderBy("order", false).where().eq("loginUserId", MainActivity.getUser().getUser_id());
                        data = qb.query();
                    } catch (Exception e) {
                        LogUtil.e(TAG, "", e);

                    }
                    if (data != null && data.size() > 0) {
                        rvList = (RecyclerView) getViewById(R.id.rv_my_sticker);
                        llm = new FullyLinearLayoutManager(MyStickerActivity.this);
                        rvList.setLayoutManager(llm);
                        rvList.setHasFixedSize(true);
                        rvList.setItemAnimator(new DefaultItemAnimator());

                        MyStickerAdapter adapter = new MyStickerAdapter(MyStickerActivity.this, data);
                        rvList.setAdapter(adapter);

                    }

                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mhandler.sendEmptyMessageDelayed(QUERY_STICKER, 0);
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
