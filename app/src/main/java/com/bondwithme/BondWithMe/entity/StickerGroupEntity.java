package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by heweidong on 15/6/17.
 */
public class StickerGroupEntity implements Serializable {
    private String first_sticker;          //first sticker name 第一张的表情名字
    private String path;                   // sticker group path 表情包途径
    private String name;                   // sticker group name 表情包名称
    private String type;                   // sticker type 图片格式
    private String total;                  // sticker total表情总数
    private String price;                  // sticker price表情包价格
    private String version;                // sticker version表情包版本
    private String download_condition;     // download condition表情包下载条件
    private String condition_type;         // 表情包下载条件分类 （以后用）
    private String downloadable;           // 表情包下载权限 （0-没权限，1-可以下载） ＊

    public String getSticker_new() {
        return sticker_new;
    }

    public void setSticker_new(String sticker_new) {
        this.sticker_new = sticker_new;
    }

    private String sticker_new;            // 判断是否为新的sticker

    public String getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(String downloadable) {
        this.downloadable = downloadable;
    }

    public StickerGroupEntity() {
        super();
    }

    public String getFirst_sticker() {
        return first_sticker;
    }

    public void setFirst_sticker(String first_sticker) {
        this.first_sticker = first_sticker;
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


}
