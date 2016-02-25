package com.bondwithme.BondWithMe.ui.more.rewards;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.GeneralRewardEntity;
import com.bondwithme.BondWithMe.entity.MyRewardEntity;
import com.bondwithme.BondWithMe.entity.RewardEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;

/**
 * 查看reward_code页面；
 * Created by heweidong on 16/1/25.
 */
public class RewardCodeActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private GeneralRewardEntity generalRewardEntity;
    private RewardEntity rewardEntity;
    private MyRewardEntity myRewardEntity;
    private NetworkImageView iv;
    private TextView tvCode;
    private TextView tvDesc;
    private TextView tvRewardDate;
    private TextView tvRewardPoint;
    private TextView tvRewardTitle;
    private TextView tvRedemption;
    private LinearLayout llCode;
    private String titleName;
    private String strCode;

    @Override
    public int getLayout() {
        return R.layout.activity_reward_code;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
        changeTitleColor(R.color.tab_color_press4);
        tvTitle.setText(titleName);
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

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
        generalRewardEntity = (GeneralRewardEntity) intent.getSerializableExtra(RewardsActivity.GENERAL_REWARD_CODE);
        rewardEntity = (RewardEntity) intent.getSerializableExtra(RewardsActivity.REWARD);
        myRewardEntity = (MyRewardEntity) intent.getSerializableExtra(MyRewardsActivity.MY_REWARD_CODE);

        iv = getViewById(R.id.iv_reward);
        tvCode = getViewById(R.id.tv_reward_code);
        tvDesc = getViewById(R.id.tv_description);
        llCode = getViewById(R.id.ll_code);
        tvRewardDate = getViewById(R.id.tv_reward_date);
        tvRewardPoint = getViewById(R.id.tv_reward_pts);
        tvRewardTitle = getViewById(R.id.tv_reward_title);
        tvRedemption = getViewById(R.id.tv_redemption);

        if (generalRewardEntity != null ){
            LogUtil.d(TAG,"generalRewardEntity");
            titleName = generalRewardEntity.getMerchant_name();
            BitmapTools.getInstance(this).display(iv,generalRewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
            tvCode.setText(generalRewardEntity.getCode());
            tvRewardTitle.setText(generalRewardEntity.getDescription());
            tvDesc.setText(generalRewardEntity.getDescription_1());
            tvRewardDate.setText(getString(R.string.valid_till) + " " +generalRewardEntity.getVoucher_due());
//            tvRewardPoint.setText(generalRewardEntity.getPoint() + " " +getString(R.string.text_capital_points));
            tvRewardPoint.setVisibility(View.INVISIBLE);
            tvRedemption.setVisibility(View.INVISIBLE);
        }

        if (rewardEntity != null ){
            LogUtil.d(TAG,"rewardEntity");
            titleName = rewardEntity.getMerchant_name();
            BitmapTools.getInstance(this).display(iv,rewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
            tvCode.setText(intent.getStringExtra(RewardDetailActivity.REWARD_CODE));
            tvRewardTitle.setText(rewardEntity.getDescription());
            tvDesc.setText(rewardEntity.getDescription_1());
            tvRewardDate.setText(getString(R.string.valid_till) + " " +rewardEntity.getVoucher_due());
            tvRewardPoint.setText(rewardEntity.getPoint() + " " +getString(R.string.text_capital_points));
        }

        if (myRewardEntity != null ){
            LogUtil.d(TAG,"myRewardEntity");
            titleName = myRewardEntity.getMerchant_name();
            BitmapTools.getInstance(this).display(iv,myRewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
            tvCode.setText(myRewardEntity.getCode());
            tvRewardTitle.setText(myRewardEntity.getDescription());
            tvDesc.setText(myRewardEntity.getDescription_1());
            tvRewardDate.setText(getString(R.string.valid_till) + " " +myRewardEntity.getVoucher_due());
            tvRewardPoint.setText(myRewardEntity.getPoint() + " " +getString(R.string.text_capital_points));
        }

        llCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager mClipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                mClipboardManager.setPrimaryClip(ClipData.newPlainText(null,tvCode.getText()));
                Toast.makeText(RewardCodeActivity.this,getString(R.string.copy_success),Toast.LENGTH_SHORT).show();
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
