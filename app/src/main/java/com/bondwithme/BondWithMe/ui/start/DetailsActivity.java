package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.widget.CircularImageView;
import com.gc.materialdesign.views.Button;

public class DetailsActivity extends BaseActivity implements EditText.OnEditorActionListener, View.OnClickListener{

    private final static String TAG = DetailsActivity.class.getSimpleName();
    private final static String COMPLETE_USER = TAG + "_COMPLETE_USER";

    private UserEntity userEntity;
    private AppTokenEntity tokenEntity;

    private String strFirstName;
    private String strLastName;
    private String strGender = "";

    private FrameLayout flPic;
    private CircularImageView civPic;
    private RelativeLayout rlPic;
    private EditText etFirst;
    private EditText etLast;
    private RadioGroup rgMain;
    private Button brNext;

    @Override
    public int getLayout() {
        return R.layout.activity_details;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_start_details));
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

        flPic = getViewById(R.id.fl_pic);
        civPic = getViewById(R.id.civ_pic);
        rlPic = getViewById(R.id.rl_pic);
        etFirst = getViewById(R.id.et_first_name);
        etLast = getViewById(R.id.et_last_name);
        rgMain = getViewById(R.id.rg_main);
        brNext = getViewById(R.id.br_next);

        flPic.setOnClickListener(this);
        brNext.setOnClickListener(this);

        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
                        strGender = "M";
                        break;
                    case R.id.rb_femal:
                        strGender = "F";
                        break;
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.br_next:
                break;

            case R.id.fl_pic:
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {

            return true;
        }
        return false;
    }

    private void getData() {
        Intent intent = getIntent();
        userEntity = (UserEntity) intent.getExtras().getSerializable(Constant.LOGIN_USER);
        tokenEntity = (AppTokenEntity) intent.getExtras().getSerializable(Constant.HTTP_TOKEN);
    }

}
