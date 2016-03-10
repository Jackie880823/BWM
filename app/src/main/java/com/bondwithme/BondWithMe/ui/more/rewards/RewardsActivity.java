package com.bondwithme.BondWithMe.ui.more.rewards;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Rewards主页；
 * Created by heweidong on 16/1/20.
 */
public class RewardsActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    public final static String GENERAL_REWARD_CODE = "general_reward";
    public final static String REWARD = "reward";
    private RewardAdapter adapter;
    private List<GeneralRewardEntity> dataGeneralReward = new ArrayList<>();
    private List<RewardPointEntity> dataRewardPoint = new ArrayList<>();
    private List<RewardEntity> dataReward = new ArrayList<>();
    private GeneralRewardEntity generalRewardEntity = null;
    private RewardPointEntity rewardPointEntity = null;
    private RewardResultEntity rewardResultEntity = null;
    private RecyclerView reList;
    private LinearLayoutManager fll;
    private RelativeLayout rlGeneralRewardInfo;
    private NestedScrollView sc_rewards;
    private TextView tvGeneralRewardData;
    private TextView tvAddMemberForPoint;
    private TextView tvPointAcc;
    private TextView tvGeneralRewardTitle;
    private TextView tvRewardPointDesc1;
    private TextView tvRewardPointDesc2;
    private TextView tvRewardPointDesc3;
    private TextView tvUserPoint;
    private TextView tvCountInvited;
    private TextView tvCountPending;
    private NetworkImageView ivGeneralReward;
    private View progress;
    private ImageView ivNoGeneralReward;
    private ImageView ivNoReward;

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
        Intent intent = new Intent(this, MyRewardsActivity.class);
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
        tvRewardPointDesc1 = getViewById(R.id.tv_reward_point_description_1);
        tvRewardPointDesc2 = getViewById(R.id.tv_reward_point_description_2);
        tvRewardPointDesc3 = getViewById(R.id.tv_reward_point_description_3);
        tvUserPoint = getViewById(R.id.tv_reward_pts);
        tvCountInvited = getViewById(R.id.tv_count_invited);
        tvCountPending = getViewById(R.id.tv_count_pending);
        ivNoGeneralReward = getViewById(R.id.iv_no_general_reward);
        ivNoReward = getViewById(R.id.iv_no_reward);
        rlGeneralRewardInfo = getViewById(R.id.rl_reward_info);
        sc_rewards = getViewById(R.id.sc_rewards);

    }

    int userPoint;

    private void initAdapter() {
        if (!dataReward.isEmpty() && dataReward.size() > 0){
            reList = getViewById(R.id.rvList);
            fll = new LinearLayoutManager(this);
//        fll = new FullyLinearLayoutManager(this);
            reList.setLayoutManager(fll);
            reList.setHasFixedSize(true);
            reList.setItemAnimator(null);
            reList.setNestedScrollingEnabled(false);
            userPoint = Integer.parseInt(dataRewardPoint.get(0).getUser_point());
            adapter = new RewardAdapter(this, dataReward, String.valueOf(userPoint));
            reList.setAdapter(adapter);
            adapter.setItemClickListener(new RewardAdapter.ItemClickListener() {
                @Override
                public void itemClick(RewardEntity rewardEntity, int position) {
                    int rewardPoint = Integer.parseInt(rewardEntity.getPoint());
                    if (!rewardEntity.getTotal_voucher().equals("0") && rewardPoint <= userPoint) {
                        Intent intent = new Intent(RewardsActivity.this, RewardDetailActivity.class);
                        intent.putExtra(REWARD, rewardEntity);
                        startActivity(intent);
                    }

                }
            });
        }
//        else {
//            ivNoReward.setVisibility(View.VISIBLE);
//        }

    }


    @Override
    public void requestData() {

    }

    private void refreshData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("country_code", MainActivity.getUser().getUser_country_code());
        new HttpTools(this).get(String.format(Constant.API_GET_REWARD_LIST, MainActivity.getUser().getUser_id()), params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(final String string) {
                sc_rewards.setVisibility(View.VISIBLE);
                final Gson gson = new Gson();
                rewardResultEntity = gson.fromJson(string, RewardResultEntity.class);
                dataGeneralReward = rewardResultEntity.getGeneralReward();
                dataRewardPoint = rewardResultEntity.getRewardPoint();
                dataReward = rewardResultEntity.getReward();
                initGeneralRewardView();
                initRewardPointView();
                initAdapter();
                //wing mofified
                sc_rewards.fullScroll(ScrollView.FOCUS_UP);

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
        if (!dataRewardPoint.isEmpty() && dataRewardPoint.size() > 0) {
            rewardPointEntity = dataRewardPoint.get(0);
            tvRewardPointDesc1.setText(rewardPointEntity.getDescription_1());
            tvRewardPointDesc2.setText(rewardPointEntity.getDescription_2());
            tvRewardPointDesc3.setText(rewardPointEntity.getDescription_3());
            tvUserPoint.setText(rewardPointEntity.getUser_point());
            tvCountInvited.setText(Html.fromHtml(String.format(getString(R.string.members_invited), rewardPointEntity.getInvited())));
            tvCountPending.setText(Html.fromHtml(String.format(getString(R.string.members_pending), rewardPointEntity.getPending())));
        }
    }

    private void initGeneralRewardView() {
        if (!dataGeneralReward.isEmpty() && dataGeneralReward.size() > 0) {
            generalRewardEntity = dataGeneralReward.get(0);
            BitmapTools.getInstance(this).display(ivGeneralReward, generalRewardEntity.getImage(), R.drawable.network_image_default, R.drawable.network_image_default);
            tvGeneralRewardTitle.setText(generalRewardEntity.getTitle());
            tvGeneralRewardData.setText(getString(R.string.valid_till) + " " + generalRewardEntity.getVoucher_due());
            ivGeneralReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RewardsActivity.this, RewardCodeActivity.class);
                    intent.putExtra(GENERAL_REWARD_CODE, generalRewardEntity);
                    startActivity(intent);
                }
            });

        } else {
            ivNoGeneralReward.setVisibility(View.VISIBLE);
            ivGeneralReward.setVisibility(View.INVISIBLE);
            rlGeneralRewardInfo.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
//        reList.post(new Runnable() {
//            @Override
//            public void run() {
//                sc_rewards.scrollTo(0, 0);
//            }
//        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
