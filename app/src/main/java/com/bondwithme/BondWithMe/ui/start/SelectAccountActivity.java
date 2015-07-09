package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.SelectAccountAdapter;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;

import java.util.ArrayList;

public class SelectAccountActivity extends BaseActivity {

    private ArrayList<UserEntity> forgotAccountList;
    private String strCountryCode;
    private String strPhoneNumber;

    private RecyclerView rvList;
    private SelectAccountAdapter selectAccountAdapter ;

    @Override
    public int getLayout() {
        return R.layout.activity_select_account;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_start_select_account));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.btn_gradient_color_green_normal);
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        getData();
        rvList = getViewById(R.id.rv);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private void initAdapter() {
        selectAccountAdapter = new SelectAccountAdapter(this, forgotAccountList, strCountryCode, strPhoneNumber);
        rvList.setAdapter(selectAccountAdapter);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void getData() {
        Intent intent = getIntent();
        strCountryCode = intent.getStringExtra("user_country_code");
        strPhoneNumber = intent.getStringExtra("user_phone");
        forgotAccountList = (ArrayList<UserEntity>) intent.getExtras().getSerializable(Constant.LOGIN_USER);
    }
}
