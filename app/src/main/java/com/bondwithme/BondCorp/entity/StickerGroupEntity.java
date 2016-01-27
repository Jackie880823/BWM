package com.bondwithme.BondCorp.entity;

import java.io.Serializable;

/**
 * Created by heweidong on 15/6/17.
 */
public class StickerGroupEntity implements Serializable {
    private boolean downloading;           // 标识正在下载
    private String first_sticker_code;//FamilyWishes_S_1.gif 第一张表情的代码（小）
    private String path;// sticker group path 表情包途径
    private String name;//Family Wishes ", // sticker group name 表情包名称
    private String type;//.gif", // sticker type 图片格式
    private String total;//12", // sticker total表情总数
    private String price;//0", // sticker price表情包价格
    private String sticker_version;//1.0", // sticker version表情包版本
    private String download_condition;//normal", // download condition表情包下载条件 （normal： 普通，default：默认app自带， event：活动）现在只有normal 和default
    private String condition_type;// 表情包下载条件分类 （以后用）
    private String sticker_new;//表情包新的状态
    private String expiry_time;// 表情包截止时间
    private String downloadable;// 表情包下载权限 （0-没权限，1-可以下载） ＊
    private String description;// 表情包描述

    public String getFirst_sticker_code() {
        return first_sticker_code;
    }

    public void setFirst_sticker_code(String first_sticker_code) {
        this.first_sticker_code = first_sticker_code;
    }

    public String getExpiry_time() {
        return expiry_time;
    }

    public void setExpiry_time(String expiry_time) {
        this.expiry_time = expiry_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSticker_new() {
        return sticker_new;
    }

    public void setSticker_new(String sticker_new) {
        this.sticker_new = sticker_new;
    }

    public String getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(String downloadable) {
        this.downloadable = downloadable;
    }

    public StickerGroupEntity() {
        super();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVersion() {
        return sticker_version;
    }

    public void setVersion(String version) {
        this.sticker_version = version;
    }

    public String getDownload_condition() {
        return download_condition;
    }

    public void setDownload_condition(String download_condition) {
        this.download_condition = download_condition;
    }

    public String getCondition_type() {
        return condition_type;
    }

    public void setCondition_type(String condition_type) {
        this.condition_type = condition_type;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }
}
