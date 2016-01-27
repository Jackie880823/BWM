package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by heweidong on 16/1/24.
 */
public class RewardPointEntity implements Serializable {
//            "id": "1", // reward id
//            "title": "referral program",
//            "description": "           Hi [user_name]!\r\nInvite members to earn Reward Point and use your Reward Points to redeem voucher!\r\n\r\n1 member = 1 Reward Point\r\n\r\n", // reward point description  获取分数活动详情
//            "user_point": "30", // current user total point 用户的总分数
//            "invited": 0, // total invited member 邀约总数
//            "accepted": 0, // total accepted member 已同意总数
//            "pending": 0 // total pending member 待成员同意加为好友总数

    private String id;
    private String title;
    private String description;
    private String user_point;
    private String invited;
    private String accepted;
    private String pending;
    private String description_1;
    private String description_2;
    private String description_3;
    private String user_given_name;

    public String getDescription_1() {
        return description_1;
    }

    public void setDescription_1(String description_1) {
        this.description_1 = description_1;
    }

    public String getDescription_2() {
        return description_2;
    }

    public void setDescription_2(String description_2) {
        this.description_2 = description_2;
    }

    public String getDescription_3() {
        return description_3;
    }

    public void setDescription_3(String description_3) {
        this.description_3 = description_3;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_point() {
        return user_point;
    }

    public void setUser_point(String user_point) {
        this.user_point = user_point;
    }

    public String getInvited() {
        return invited;
    }

    public void setInvited(String invited) {
        this.invited = invited;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }
}
