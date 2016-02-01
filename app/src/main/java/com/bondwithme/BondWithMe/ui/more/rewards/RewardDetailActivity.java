package com.bondwithme.BondWithMe.ui.more.rewards;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.entity.RewardEntity;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 查看Reward详细信息的页面；
 * Created by heweidong on 16/1/21.
 */
public class RewardDetailActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    public final static String REWARD_CODE = "voucher_code";
    private RewardEntity rewardEntity;
    private NetworkImageView ivReward;
    private TextView tvRewardDate;
    private TextView tvRewardPoint;
    private TextView tvRewardDesc;
    private Button btnRedeem;
    @Override
    public int getLayout() {
        return R.layout.activity_reward_detail;

    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        tvTitle.setText(rewardEntity.getMerchant_name());
        changeTitleColor(R.color.tab_color_press4);
        rightButton.setVisibility(View.INVISIBLE);
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
        rewardEntity = (RewardEntity) getIntent().getSerializableExtra(RewardsActivity.REWARD);
        ivReward = getViewById(R.id.iv_reward);
        tvRewardDate = getViewById(R.id.tv_reward_date);
        tvRewardPoint = getViewById(R.id.tv_reward_pts);
        tvRewardDesc = getViewById(R.id.tv_description);
        btnRedeem = getViewById(R.id.tv_redeem);
        BitmapTools.getInstance(this).display(ivReward,rewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
        tvRewardDate.setText(getString(R.string.valid_till) + " " +rewardEntity.getVoucher_due());
        tvRewardPoint.setText(rewardEntity.getPoint() + " " +getString(R.string.text_capital_points));
        tvRewardDesc.setText(rewardEntity.getDescription());
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();//弹出确定获取code的dialog；
            }
        });



    }

    private void dialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(getString(R.string.text_tips_title)); //设置标题
        builder.setMessage(getString(R.string.sure_redeem) + " "+ rewardEntity.getPoint() + " " + getString(R.string.reward_point)); //设置内容
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getRewardCode();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void getRewardCode() {
        HashMap<String,String> param = new HashMap<>();
        param.put("user_id", MainActivity.getUser().getUser_id());
        param.put("reward_id",rewardEntity.getId());
        new HttpTools(this).post(Constant.API_POST_REWARD_CODE, param, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if (Constant.SUCCESS.equals(jsonObject.get("status"))){
                        Intent intent = new Intent(RewardDetailActivity.this,RewardCodeActivity.class);
                        intent.putExtra(REWARD_CODE, (String) jsonObject.get("voucher_code"));
                        intent.putExtra(RewardsActivity.REWARD,rewardEntity);
                        startActivity(intent);
                        RewardDetailActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
}
