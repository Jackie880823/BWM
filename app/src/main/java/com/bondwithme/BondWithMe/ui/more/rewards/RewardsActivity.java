package com.bondwithme.BondWithMe.ui.more.rewards;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.RewardAdapter;
import com.bondwithme.BondWithMe.entity.GeneralRewardEntity;
import com.bondwithme.BondWithMe.entity.RewardEntity;
import com.bondwithme.BondWithMe.entity.RewardPointEntity;
import com.bondwithme.BondWithMe.entity.RewardResultEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.add.AddMembersActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.widget.FullyLinearLayoutManager;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by heweidong on 16/1/20.
 */
public class RewardsActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    private RewardAdapter adapter;
    private List<GeneralRewardEntity> dataGeneralReward = new ArrayList<>();
    private List<RewardPointEntity> dataRewardPoint = new ArrayList<>();
    private List<RewardEntity> dataReward = new ArrayList<>();
    private GeneralRewardEntity generalRewardEntity = null;
    private RewardPointEntity rewardPointEntity = null;
    private RewardResultEntity rewardResultEntity = null;
    private RecyclerView reList;
    private FullyLinearLayoutManager fll;
    private TextView tvGeneralRewardData;
    private TextView tvAddMemberForPoint;
    private TextView tvPointAcc;
    private TextView tvGeneralRewardTitle;
    private TextView tvRewardPointDesc;
    private TextView tvUserPoint;
    private TextView tvCountInvited;
    private TextView tvCountPending;
    private NetworkImageView ivGeneralReward;
    private View progress;

    @Override
    public int getLayout() {
        return R.layout.activity_rewards_main;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_rewards);
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setImageResource(R.drawable.myreward_icon);
    }

    @Override
    protected void titleRightEvent() {
        Intent intent = new Intent(this,MyRewardsActivity.class);
        startActivity(intent);

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        progress = getViewById(R.id.rl_progress);
        ivGeneralReward = getViewById(R.id.iv_free_reward);
        tvAddMemberForPoint = getViewById(R.id.tv_add_member_for_point);
        tvPointAcc = getViewById(R.id.tv_point_acc);
        tvGeneralRewardTitle = getViewById(R.id.tv_general_reward_title);
        tvGeneralRewardData = getViewById(R.id.tv_general_reward_date);
        tvRewardPointDesc = getViewById(R.id.tv_reward_point_description);
        tvUserPoint = getViewById(R.id.tv_reward_pts);
        tvCountInvited = getViewById(R.id.tv_count_invited);
        tvCountPending = getViewById(R.id.tv_count_pending);


        reList = getViewById(R.id.rvList);
        fll = new FullyLinearLayoutManager(this);
        reList.setLayoutManager(fll);
        reList.setHasFixedSize(true);
        reList.setItemAnimator(new DefaultItemAnimator());

        reList.post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) RewardsActivity.this.getViewById(R.id.sc_rewards)).scrollTo(0, 0);
            }
        });
    }

    private void initAdapter() {
        adapter = new RewardAdapter(this,dataReward,dataRewardPoint.get(0).getUser_point());
        reList.setAdapter(adapter);
    }


    public static Long getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        long l = 0;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    @Override
    public void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("country_code", "60");
//        MainActivity.getUser().getUser_country_code()
        new HttpTools(this).get(String.format(Constant.API_GET_REWARD_LIST,MainActivity.getUser().getUser_id()), params,TAG, new HttpCallback() {
            @Override
            public void onStart() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(String string) {
                Gson gson =new Gson();
                LogUtil.d(TAG,"onResult====="+string);
                rewardResultEntity = gson.fromJson(string, RewardResultEntity.class);
                dataGeneralReward = rewardResultEntity.getGeneralReward();
                dataRewardPoint = rewardResultEntity.getRewardPoint();
                dataReward = rewardResultEntity.getReward();
                initGeneralRewardView();
                initRewardPointView();
                initAdapter();
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

    private void initRewardPointView() {
        tvAddMemberForPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RewardsActivity.this, AddMembersActivity.class);
                startActivity(intent);
            }
        });
        tvPointAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRewardInfo = new Intent(RewardsActivity.this, RewardInfoActivity.class);
                startActivity(toRewardInfo);
            }
        });
        if (!dataRewardPoint.isEmpty()){
            rewardPointEntity = dataRewardPoint.get(0);
            tvRewardPointDesc.setText(rewardPointEntity.getDescription());
            tvUserPoint.setText(rewardPointEntity.getUser_point());
            tvCountInvited.setText(rewardPointEntity.getInvited());
            tvCountPending.setText(rewardPointEntity.getPending());
        }
    }

    private void initGeneralRewardView() {
        if (!dataGeneralReward.isEmpty()){
            generalRewardEntity = dataGeneralReward.get(0);
            BitmapTools.getInstance(this).display(ivGeneralReward,generalRewardEntity.getImage(),R.drawable.network_image_default, R.drawable.network_image_default);
            tvGeneralRewardTitle.setText(generalRewardEntity.getTitle());
            tvGeneralRewardData.setText(getString(R.string.valid_till) + generalRewardEntity.getVoucher_due());

        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}