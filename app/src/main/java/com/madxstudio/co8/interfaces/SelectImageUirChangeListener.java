package com.madxstudio.co8.interfaces;

/**
 * Created by Jackie Zhu on 7/14/15.
 */

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.madxstudio.co8.adapter.LocalMediaAdapter;
import com.madxstudio.co8.entity.MediaData;

import java.util.List;


/**
 * 选中图片改变监听
 */
public interface SelectImageUirChangeListener {
    /**
     * 添加图片{@code mediaData}到选择列表
     *
     * @param mediaData -   需要添加的图片uri数据
     * @return -   true:   添加成功；
     * -   false:  添加失败；
     */
    boolean addUri(MediaData mediaData);

    /**
     * 从列表中删除图片{@code mediaData}
     *
     * @param mediaData -   需要删除的图片uri数据
     * @return -   true:   删除成功；
     * -   false:  删除失败；
     */
    boolean removeUri(MediaData mediaData);

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

    void onLoadedMedia(List<MediaData> data, @NonNull LocalMediaAdapter adapter);

    /**
     * @param mediaData
     */
    void preview(MediaData mediaData);
}