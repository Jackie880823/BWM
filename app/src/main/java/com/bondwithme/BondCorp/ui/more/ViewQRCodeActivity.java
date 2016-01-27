package com.bondwithme.BondCorp.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.UserEntity;
import com.bondwithme.BondCorp.http.VolleyUtil;
import com.bondwithme.BondCorp.ui.BaseActivity;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.zxing.activity.CaptureActivity;


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
    private String viewMyQR;

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
        ivQRCode = getViewById(R.id.iv_qr);
        tvScanHint = getViewById(R.id.tv_scan_hint);
        btnScanQRCode = getViewById(R.id.btn_scan_qr);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        LogUtil.d("ViewQRCodeActivity_initView()", "=====");
        if (bundle != null){
            userEntity = (UserEntity)bundle.getSerializable("userEntity");
        }

        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(ViewQRCodeActivity.this, CaptureActivity.class);
                startActivity(scanIntent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("ViewQRCodeActivity_onResume()","=============");

        if (userEntity != null){
            userId = userEntity.getUser_id();
            userName = userEntity.getUser_given_name();
            tvTitle.setText(userName + getResources().getString(R.string.text_qr));
            LogUtil.d("ViewQRCodeActivity_onResume()", "userId==========" + userId + "userName==========" + userName);
        }else {
            userId = MainActivity.getUser().getUser_id();
            tvTitle.setText(R.string.my_qr_code);
        }

        VolleyUtil.initNetworkImageView(this, ivQRCode, String.format(Constant.API_QRCode, userId), R.drawable.network_image_default, R.drawable.network_image_default);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        LogUtil.d("ViewQRCodeActivity_onNewIntent", "=====");
        if (bundle != null){
            viewMyQR = (String)bundle.get("from_scan");
            LogUtil.d("ViewQRCodeActivity_onNewIntent()", "=====from_scan=====" + viewMyQR);
            if ("view_my_qr".equals(viewMyQR)){
                if (userEntity != null){
                    userEntity = null;
                }
            }
        }
    }

    @Override
    public void requestData() {


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
