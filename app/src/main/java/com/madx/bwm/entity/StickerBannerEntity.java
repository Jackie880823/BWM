package com.madx.bwm.entity;

import java.io.Serializable;import java.lang.String;

/**
 * Created by heweidong on 15/6/19.
 */
public class StickerBannerEntity implements Serializable {
    private String banner_photo;        // 广告图
    private String sticker_group_path;  // 对应的表情包，若是“”空就没有对应的表情包

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
