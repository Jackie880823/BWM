package com.bondwithme.BondCorp.entity;

import android.support.annotation.IdRes;

import java.io.Serializable;

/**
 * Created 11/10/15.
 * 介绍页面的实体，封装介绍页的描述和图片资源ID
 * @author Jackie
 * @version 1.0
 */
public class IntroductionEntity implements Serializable {
    /**
     * 图片资源ID
     */
    private int imagesResId;
    private String description;

    public int getImagesResId() {
        return imagesResId;
    }

    public void setImagesResId(@IdRes int imagesResId) {
        this.imagesResId = imagesResId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
