package com.bondwithme.BondWithMe.ui.more.BondAlert;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.BondAlertBigDayAdapter;
import com.bondwithme.BondWithMe.entity.BigDayEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class BigDayActivity extends BaseActivity {

    private RecyclerView rvList;
    private BondAlertBigDayAdapter bondAlertBigDayAdapter;
    private List<BigDayEntity> data = new ArrayList<>();


    public int getLayout() {
        return R.layout.activity_big_day;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_big_day_alert);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
//        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
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
        rvList = getViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    @Override
    public void requestData() {

        new HttpTools(this).get(String.format(Constant.API_BONDALERT_DIG_DAY, MainActivity.getUser().getUser_id()), null, this, new HttpCallback() {
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
                data = gson.fromJson(response, new TypeToken<ArrayList<BigDayEntity>>() {}.getType());



                if (data != null)
                {
                    initAdapter();
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
        bondAlertBigDayAdapter = new BondAlertBigDayAdapter(this, data);

        rvList.setAdapter(bondAlertBigDayAdapter);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
