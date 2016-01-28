package com.bondwithme.BondCorp.entity;

/**
 * check sticker update
 * Created by heweidong on 15/6/23.
 */
public class StickerUpdateEntity {
    private String path;              //  "Dino_Milo", sticker group path 表情途径
    private String name;              //  "Dino Milo",  sticker group name 表情包名称
    private String type;              // ".png",  sticker type 图片格式
    private String total;             // sticker total表情总数
    private String version;           // sticker version表情包版本
    private String disable;           // disable status表情包无效状态 （0－有效，1-无效［删除］）
    private String trial_expired;     // trial expired status表情包试用期状态 （0－有效，1-无效）

    public StickerUpdateEntity(String path, String name, String type, String total, String version, String disable, String trial_expired) {
        this.path = path;
        this.name = name;
        this.type = type;
        this.total = total;
        this.version = version;
        this.disable = disable;
        this.trial_expired = trial_expired;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDisable() {
        return disable;
    }

    public void setDisable(String disable) {
        this.disable = disable;
    }

    public String getTrial_expired() {
        return trial_expired;
    }

    public void setTrial_expired(String trial_expired) {
        this.trial_expired = trial_expired;
    }

    @Override
    public String toString() {
        return "StickerUpdateEntity{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", total='" + total + '\'' +
                ", version='" + version + '\'' +
                ", disable='" + disable + '\'' +
                ", trial_expired='" + trial_expired + '\'' +
                '}';
    }
}
