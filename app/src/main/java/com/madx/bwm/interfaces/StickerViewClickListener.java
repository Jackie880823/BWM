package com.madx.bwm.interfaces;

/**
 * Created by quankun on 15/5/12.
 */
public interface StickerViewClickListener {
    /**
     * @param type       sticker的后缀类型(.gif)
     * @param folderName 放置sticker的文件夹名称
     * @param filName    sticker的文件名
     */
    public void showComments(final String type, final String folderName, final String filName);
}
