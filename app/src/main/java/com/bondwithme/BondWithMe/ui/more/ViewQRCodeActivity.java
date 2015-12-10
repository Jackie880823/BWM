package com.bondwithme.BondWithMe.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.zxing.activity.CaptureActivity;


/**
 * Created by heweidong on 15/12/7.
 */
public class ViewQRCodeActivity extends BaseActivity {

    private NetworkImageView ivQRCode;
    private TextView tvScanHint;
    private Button btnScanQRCode;

    @Override
    public int getLayout() {
        return R.layout.activity_view_qrcode;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.my_qr_code);
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

        VolleyUtil.initNetworkImageView(this, ivQRCode, String.format(Constant.API_QRCode,MainActivity.getUser().getUser_id()));

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
