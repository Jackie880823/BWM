package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heweidong on 16/1/25.
 */
public class RewardResultEntity implements Serializable {
    private List<GeneralRewardEntity> generalReward;
    private List<RewardPointEntity> rewardPoint;
    private List<RewardEntity> reward;

    public List<GeneralRewardEntity> getGeneralReward() {
        return generalReward;
    }

    public void setGeneralReward(List<GeneralRewardEntity> generalReward) {
        this.generalReward = generalReward;
    }

    public List<RewardPointEntity> getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(List<RewardPointEntity> rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public List<RewardEntity> getReward() {
        return reward;
    }

    public void setReward(List<RewardEntity> reward) {
        this.reward = reward;
    }
}
