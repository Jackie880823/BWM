package com.bondwithme.BondWithMe.interfaces;

/**
 * Created by Jackie Zhu on 7/14/15.
 */

import android.graphics.drawable.Drawable;

import com.bondwithme.BondWithMe.entity.ImageData;


/**
 * 选中图片改变监听
 */
public interface SelectImageUirChangeListener {
    /**
     * 添加图片{@code imageData}到选择列表
     *
     * @param imageData -   需要添加的图片uri数据
     * @return -   true:   添加成功；
     * -   false:  添加失败；
     */
    boolean addUri(ImageData imageData);

    /**
     * 从列表中删除图片{@code imageData}
     *
     * @param imageData -   需要删除的图片uri数据
     * @return -   true:   删除成功；
     * -   false:  删除失败；
     */
    boolean removeUri(ImageData imageData);

    /**
     * 打开了左侧的目录列表并设置标题栏左侧图标为{@code drawable}
     *
     * @param drawable
     */
    void onDrawerOpened(Drawable drawable);

    /**
     * 关闭了左侧的目录列表并设置标题栏左侧图标为{@code drawable}
     *
     * @param drawable
     */
    void onDrawerClose(Drawable drawable);

    /**
     * @param imageData
     */
    void preview(ImageData imageData);
}