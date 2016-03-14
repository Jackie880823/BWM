package com.madxstudio.co8.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.SelectAccountAdapter;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.BaseActivity;

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
        changeTitleColor(R.color.btn_bg_color_login_normal);
        tvTitle.setTextColor(getResources().getColor(R.color.login_text_bg_color));
        leftButton.setImageResource(R.drawable.co8_back_button);
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
        rvList.setItemAnimator(null);
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
