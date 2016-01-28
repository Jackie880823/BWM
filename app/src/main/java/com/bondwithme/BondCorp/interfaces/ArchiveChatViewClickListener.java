package com.bondwithme.BondCorp.interfaces;

import com.bondwithme.BondCorp.entity.ArchiveChatEntity;

/**
 * Created by zhuweiping on 4/28/15.
 */
public interface ArchiveChatViewClickListener {
    /**
     * 显示图片
     *
     * @param entity
     */
    void showOriginalPic(final ArchiveChatEntity entity);

    /**
     * @param content_group_id
     * @param group_id
     */
    void showComments(final String content_group_id, final String group_id);

}
