package com.bondwithme.BondWithMe.interfaces;

/**
 * Created by Jackie Zhu on 7/14/15.
 */

import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * 选中图片改变监听
 */
public interface SelectImageUirChangeListener {
    /**
     * 添加图片{@code imageUri}到选择列表
     *
     * @param imageUri -   需要添加的图片uri
     * @return -   true:   添加成功；
     * -   false:  添加失败；
     */
    boolean addUri(Uri imageUri);

    /**
     * 从列表中删除图片{@code imageUri}
     *
     * @param imageUri -   需要删除的图片uri
     * @return -   true:   删除成功；
     * -   false:  删除失败；
     */
    boolean removeUri(Uri imageUri);

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

    void preview(Uri uri);
}