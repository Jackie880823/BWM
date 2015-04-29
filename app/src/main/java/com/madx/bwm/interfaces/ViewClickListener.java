package com.madx.bwm.interfaces;

/**
 * Created by zhuweiping on 4/28/15.
 */
public interface ViewClickListener {
    /**
     * 显示Wall图片
     *
     * @param content_id
     */
    public void showOriginalPic(final String content_id);

    /**
     * @param content_group_id
     * @param group_id
     */
    public void showComments(final String content_group_id, final String group_id);

    /**
     * 显示被@的用户列表
     * @param content_group_id
     * @param group_id
     */
    public void showMembers(final String content_group_id, final String group_id);

    /**
     * 显示被@的群组列表
     * @param content_group_id
     * @param group_id
     */
    public void showGroups(final String content_group_id, final String group_id);

    public void remove(final String content_group_id);
}
