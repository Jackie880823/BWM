package com.bondwithme.BondWithMe.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.zxing.activity.CaptureActivity;


/**
 * Created by heweidong on 15/12/7.
 */
public class ViewQRCodeActivity extends BaseActivity {

    private NetworkImageView ivQRCode;
    private TextView tvScanHint;
    private Button btnScanQRCode;
    private UserEntity userEntity;
    private String userId;
    private String userName;

    @Override
    public int getLayout() {
        return R.layout.activity_view_qrcode;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        rightButton.setVisibility(View.INVISIBLE);
        if (userEntity != null){
            tvTitle.setText(userName + getResources().getString(R.string.text_qr));
        }else {
            tvTitle.setText(R.string.my_qr_code);
        }
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
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            userEntity = (UserEntity)bundle.getSerializable("userEntity");
        }
        ivQRCode = getViewById(R.id.iv_qr);
        tvScanHint = getViewById(R.id.tv_scan_hint);
        btnScanQRCode = getViewById(R.id.btn_scan_qr);

        if (userEntity != null){
            userId = userEntity.getUser_id();
            userName = userEntity.getUser_given_name();
            LogUtil.d("ViewQRCodeActivity","userId=========="+userId);
        }else {
            userId = MainActivity.getUser().getUser_id();
        }

        VolleyUtil.initNetworkImageView(this, ivQRCode, String.format(Constant.API_QRCode,userId),R.drawable.network_image_default, R.drawable.network_image_default);

        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(ViewQRCodeActivity.this, CaptureActivity.class);
                startActivity(scanIntent);
            }
        });


    }


    @Override
    public void requestData() {


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
