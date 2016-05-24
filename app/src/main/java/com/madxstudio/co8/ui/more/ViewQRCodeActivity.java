package com.madxstudio.co8.ui.more;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.qr_code.zxing.co8.CaptureActivity;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.OrganisationActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.widget.MyDialog;


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
        if (bundle != null) {
            userEntity = (UserEntity) bundle.getSerializable("userEntity");
        }

        btnScanQRCode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!("0".equals(App.getLoginedUser().getDemo()) && "0".equals(App.getLoginedUser().getPending_org()))) {
                            final LayoutInflater factory = LayoutInflater.from(ViewQRCodeActivity.this);
                            View selectIntention = factory.inflate(R.layout.dialog_group_nofriend, null);
                            final MyDialog dialog = new MyDialog(ViewQRCodeActivity.this, null, selectIntention);
                            TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
                            tv_no_member.setText(R.string.text_before_jion_org);
                            TextView acceptTv = (TextView) selectIntention.findViewById(R.id.tv_cal);//确定
                            TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_ok);//取消
                            selectIntention.findViewById(R.id.line_v).setVisibility(View.VISIBLE);
                            acceptTv.setText(R.string.text_org_join_now);
                            cancelTv.setText(R.string.text_later);
                            cancelTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                            dialog.setCanceledOnTouchOutside(false);
                            cancelTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            acceptTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    startActivity(new Intent(ViewQRCodeActivity.this, OrganisationActivity.class));
                                }
                            });
                            dialog.show();
                        } else {
                            Intent scanIntent = new Intent(ViewQRCodeActivity.this, CaptureActivity.class);
                            startActivity(scanIntent);
                        }
                    }
                }
        );
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("ViewQRCodeActivity_onResume()", "=============");

        if (userEntity != null) {
            userId = userEntity.getUser_id();
            userName = userEntity.getUser_given_name();
            tvTitle.setText(userName + getResources().getString(R.string.text_qr));
            LogUtil.d("ViewQRCodeActivity_onResume()", "userId==========" + userId + "userName==========" + userName);
        } else {
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
        if (bundle != null) {
            viewMyQR = (String) bundle.get("from_scan");
            LogUtil.d("ViewQRCodeActivity_onNewIntent()", "=====from_scan=====" + viewMyQR);
            if ("view_my_qr".equals(viewMyQR)) {
                if (userEntity != null) {
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
