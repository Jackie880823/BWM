package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by heweidong on 16/1/24.
 */
public class GeneralRewardEntity implements Serializable {

//            "id": "1",
//            "title": "Welcome Reward", // reward title 奖励标题
//            "description": "Additional 15% off storewide\r\n\r\nLondn kkdnksd is simply dummy test to printing and just for jun.\r\n\r\nPromotion Period: 1 Dec 2015- 30 Jun 2016\r\n\r\nHOW TO REDEEM:\r\n1. Londn kkdnksd is simply dummy test to printing and just for jun.\r\n\r\n2. Londn kkdnksd is simply dummy test to printing and just for jun.\r\n\r\n3. Londn kkdnksd is simply dummy test to printing and just for jun.", // reward description 奖励详情
//            "image": "http://dev.bondwith.me/bondwithme/images/month_reward.jpg", // reward image 奖励图片
//            "type": "general",
//            "point": "0",
//            "voucher_due": "2016-06-30", // voucher expire date 优惠券截止日期
//            "voucher_due_day": "30", // voucher expire day 优惠券截止日
//            "voucher_due_month": "06", // voucher expire month 优惠券截止月
//            "voucher_due_year": "2016", // voucher expire year 优惠券截止年
//            "code": "LAZ15DISC", // reward code 优惠券编码
//            "end_date": "2016-02-17 16:00:00", // redemption campaign expire date 兑换优惠券活动截止0时区日期
//            "end_date_timestamp": "1455724800" // redemption campaign expire datetime stamp兑换优惠券活动截止0时区时间戳

    private String id;
    private String title;
    private String description;
    private String image;
    private String type;
    private String point;
    private String voucher_due;
    private String voucher_due_day;
    private String voucher_due_month;
    private String voucher_due_year;
    private String code;
    private String end_date;
    private String end_date_timestamp;

    public GeneralRewardEntity() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getVoucher_due() {
        return voucher_due;
    }

    public void setVoucher_due(String voucher_due) {
        this.voucher_due = voucher_due;
    }

    public String getVoucher_due_day() {
        return voucher_due_day;
    }

    public void setVoucher_due_day(String voucher_due_day) {
        this.voucher_due_day = voucher_due_day;
    }

    public String getVoucher_due_month() {
        return voucher_due_month;
    }

    public void setVoucher_due_month(String voucher_due_month) {
        this.voucher_due_month = voucher_due_month;
    }

    public String getVoucher_due_year() {
        return voucher_due_year;
    }

    public void setVoucher_due_year(String voucher_due_year) {
        this.voucher_due_year = voucher_due_year;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_date_timestamp() {
        return end_date_timestamp;
    }

    public void setEnd_date_timestamp(String end_date_timestamp) {
        this.end_date_timestamp = end_date_timestamp;
    }
}
