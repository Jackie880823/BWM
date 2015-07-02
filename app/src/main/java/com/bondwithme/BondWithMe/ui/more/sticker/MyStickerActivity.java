package com.bondwithme.BondWithMe.ui.more.sticker;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MyStickerAdapter;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.widget.FullyLinearLayoutManager;

import java.sql.SQLException;
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
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {

    }
    @Override
    public void initView() {

        try {
            Dao<LocalStickerInfo,Integer> stickerDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
            data = stickerDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(data!=null && data.size() > 0){
            rvList = (RecyclerView) getViewById(R.id.rv_my_sticker);
            llm = new FullyLinearLayoutManager(this);
            rvList.setLayoutManager(llm);
            rvList.setHasFixedSize(true);
            rvList.setItemAnimator(new DefaultItemAnimator());

            MyStickerAdapter adapter = new MyStickerAdapter(this,data);
            rvList.setAdapter(adapter);

        }

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
