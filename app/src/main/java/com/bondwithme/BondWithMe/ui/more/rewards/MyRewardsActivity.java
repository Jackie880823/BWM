package com.bondwithme.BondWithMe.ui.more.rewards;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MyRewardAdapter;
import com.bondwithme.BondWithMe.entity.MyRewardEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 已经兑换的优惠券列表页面；
 * Created by heweidong on 16/1/21.
 */
public class MyRewardsActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    public final static String MY_REWARD_CODE = "my_reward_code";
    private LinearLayoutManager llm;
    private TextView tvNoData;
    private RecyclerView rvList;
    private View vProgress;
    private MyRewardAdapter adapter;
    private List<MyRewardEntity> data = new ArrayList<>();



    @Override
    public int getLayout() {
        return R.layout.activity_my_rewards;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press4);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.my_rewards));
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
        vProgress = getViewById(R.id.rl_progress);
        llm = new LinearLayoutManager(this);
        rvList = getViewById(R.id.rvList);
        tvNoData = getViewById(R.id.tv_no_reward);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(llm);

    }

    private void initAdapter() {
        adapter = new MyRewardAdapter(data,this);
        rvList.setAdapter(adapter);
        adapter.setItemClickListener(new MyRewardAdapter.ItemClickListener() {
            @Override
            public void itemClick(MyRewardEntity myRewardEntity, int position) {
                if (myRewardEntity.getExpired_flag().equals("0")){
                    Intent intent = new Intent(MyRewardsActivity.this,RewardCodeActivity.class);
                    intent.putExtra(MY_REWARD_CODE,myRewardEntity);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void requestData() {
        new HttpTools(this).get(String.format(Constant.API_GET_My_REWARD, MainActivity.getUser().getUser_id()), null, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                Gson gson = new GsonBuilder().create();
                data = gson.fromJson(string,new TypeToken<ArrayList<MyRewardEntity>>(){}.getType());
                initAdapter();
                //no my reward;
                if (!data.isEmpty()){
                    tvNoData.setVisibility(View.GONE);
                }else {
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(getResources().getString(R.string.text_no_rewards));
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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
