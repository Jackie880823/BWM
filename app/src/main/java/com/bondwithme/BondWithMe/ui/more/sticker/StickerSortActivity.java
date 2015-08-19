package com.bondwithme.BondWithMe.ui.more.sticker;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.DragListAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.db.SQLiteHelperOrm;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.SystemUtil;
import com.bondwithme.BondWithMe.widget.DragListView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heweidong on 15/7/16.
 */
public class StickerSortActivity extends BaseActivity {
    private final int QUERY_STICKER = 1;
    private List<LocalStickerInfo> data = new ArrayList<>();
    private final String TAG = "StickerSortActivity";
    private DragListView dvList;
    View mProgressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_sticker_sort;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.my_sticker));
        rightButton.setVisibility(View.GONE);
        yearButton.setVisibility(View.VISIBLE);
        yearButton.setTextSize(15);
        yearButton.setText(getResources().getString(R.string.text_done));
    }

    @Override
    protected void titleRightEvent() {
        changeOrder();
    }

    private void changeOrder() {
        mProgressDialog.setVisibility(View.VISIBLE);
        LocalStickerInfoDao dao = LocalStickerInfoDao.getInstance(this);
        int count = data.size()-1;
        for (int i = count; i >= 0; i--){
            LocalStickerInfo stickerInfo = (LocalStickerInfo) dvList.getAdapter().getItem(i);
            LogUtil.i(TAG, "========stickerInfo=========" + i + stickerInfo.toString());
            stickerInfo.setOrder(System.currentTimeMillis()+1);
            LogUtil.i(TAG, "========stickerInfo=========" + i + stickerInfo.toString());
            dao.addOrUpdate(stickerInfo);
        }
        if (mProgressDialog != null){
            mProgressDialog.setVisibility(View.INVISIBLE);
        }
        finish();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        mProgressDialog = getViewById(R.id.rl_progress);
        mhandler.sendEmptyMessage(QUERY_STICKER);
    }

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QUERY_STICKER:
                    try {
                        Dao<LocalStickerInfo, String> stickerInfoDao = SQLiteHelperOrm.getHelper(StickerSortActivity.this).getDao(LocalStickerInfo.class);
                        QueryBuilder qb = stickerInfoDao.queryBuilder();
                        qb.orderBy("order",false).where().eq("loginUserId", MainActivity.getUser().getUser_id());
                        data = qb.query();
                    } catch (Exception e) {
                        LogUtil.e(TAG, "", e);
                    }
                    if(data!=null && data.size() > 0){
                        dvList = getViewById(R.id.dlv_sticker_sort);

                        DragListAdapter adapter = new DragListAdapter(StickerSortActivity.this,data);
                        dvList.setAdapter(adapter);

                    }

                    break;
            }
        }
    };

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
