package com.madx.bwm.entity;

import java.io.Serializable;

/**
 * Created by heweidong on 15/6/10.
 */
public class StickerItemEntity implements Serializable{
    private String sticker_name;        // sticker name表情名称
    private String sticker_tag;         // sticker tag表情标签 （以后如果需要用文字来找出表情 ）

    public StickerItemEntity() {
        super();
    }

    public String getSticker_name() {
        return sticker_name;
    }

    public void setSticker_name(String sticker_name) {
        this.sticker_name = sticker_name;
    }

    public String getSticker_tag() {
        return sticker_tag;
    }

    public void setSticker_tag(String sticker_tag) {
        this.sticker_tag = sticker_tag;
    }

    @Override
    public String toString() {
        return "StickerItemEntity{" +
                "sticker_name='" + sticker_name + '\'' +
                ", sticker_tag='" + sticker_tag + '\'' +
                '}';
    }
}
