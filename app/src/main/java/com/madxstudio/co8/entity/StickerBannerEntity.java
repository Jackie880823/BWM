package com.madxstudio.co8.entity;

import java.io.Serializable;
import java.lang.String;

/**
 * Created by heweidong on 15/6/19.
 */
public class StickerBannerEntity implements Serializable {
    private String banner_photo;// 广告图
    private String sticker_group_path;//对应的表情包，若是“”空就没有对应的表情包
    private String first_sticker_code; // 第一张的表情代码（小）
    private String path; // sticker group path 表情途径
    private String name; // sticker group name 表情包名称
    private String type; // sticker type 图片格式
    private String total;// sticker total表情总数
    private String price; // sticker price表情包价格
    private String version;// sticker version表情包版本
    private String description; // 表情包描述
    private String download_condition; // download condition表情包下载条件
    private String condition_type;// 表情包下载条件分类 （以后用）
    private String sticker_new; // 表情包新的状态
    private String expiry_time; // 表情包截止日期
    private String downloadable; // 表情包下载权限 （0-没权限，1-可以下载）

    public String getFirst_sticker_code() {
        return first_sticker_code;
    }

    public void setFirst_sticker_code(String first_sticker_code) {
        this.first_sticker_code = first_sticker_code;
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
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getSticker_new() {
        return sticker_new;
    }

    public void setSticker_new(String sticker_new) {
        this.sticker_new = sticker_new;
    }

    public String getExpiry_time() {
        return expiry_time;
    }

    public void setExpiry_time(String expiry_time) {
        this.expiry_time = expiry_time;
    }

    public String getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(String downloadable) {
        this.downloadable = downloadable;
    }
    public String getBanner_photo() {
        return banner_photo;
    }

    public void setBanner_photo(String banner_photo) {
        this.banner_photo = banner_photo;
    }

    public String getSticker_group_path() {
        return sticker_group_path;
    }

    public void setSticker_group_path(String sticker_group_path) {
        this.sticker_group_path = sticker_group_path;
    }
}
