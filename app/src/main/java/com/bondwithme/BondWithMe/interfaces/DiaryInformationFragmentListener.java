package com.bondwithme.BondWithMe.interfaces;

import android.support.annotation.Nullable;

import com.bondwithme.BondWithMe.entity.WallEntity;

/**
 * Created 15/12/16.
 * 日志详情监听
 * @author Jackie
 * @version 1.0
 */
public interface DiaryInformationFragmentListener {
    /**
     * 日志加载完成
     * @param wallEntity
     */
    void onLoadedEntity(@Nullable WallEntity wallEntity);
}
