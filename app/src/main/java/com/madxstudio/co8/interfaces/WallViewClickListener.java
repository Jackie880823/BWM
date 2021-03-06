package com.madxstudio.co8.interfaces;

import com.madxstudio.co8.adapter.WallHolder;
import com.madxstudio.co8.entity.WallCommentEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.util.WallUtil;

/**
 * Wall内容各点击执行动作接口
 * Created by Jackie on 4/28/15.
 *
 * @author Jackie
 * @version 1.0
 */
public interface WallViewClickListener {

    /**
     * 显示Wall详情
     */
    void showDiaryInformation(WallEntity wallEntity);

    /**
     * 显示被@的用户列表
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    void showMembers(final String content_group_id, final String group_id);

    /**
     * 显示被@的群组列表
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    void showGroups(final String content_group_id, final String group_id);

    /**
     * 删除Wall
     *
     * @param view
     * @param wallEntity {@link WallEntity}
     */
    void remove(WallEntity wallEntity);

    /**
     * 显示点赞的用户列表
     *
     * @param viewer_id {@link WallEntity#user_id}
     * @param refer_id {@link WallEntity#content_id} or {@link WallCommentEntity#comment_id}
     * @param type  {@link WallUtil#LOVE_MEMBER_COMMENT_TYPE} or {@link WallUtil#LOVE_MEMBER_WALL_TYPE}
     */
    void showLovedMember(String viewer_id, String refer_id, String type);

    void showPopClick(WallHolder holder);

    void addPhotoed(WallEntity wallEntity, boolean succeed);

    void saved(WallEntity wallEntity, boolean succeed);

    void onSave();
}
