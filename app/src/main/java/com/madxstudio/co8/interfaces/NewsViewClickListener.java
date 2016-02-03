package com.madxstudio.co8.interfaces;

import com.madxstudio.co8.entity.NewsEntity;
import com.madxstudio.co8.entity.WallEntity;

/**
 * Wall内容各点击执行动作接口
 * Created by Jackie on 4/28/15.
 *
 * @author Jackie
 * @version 1.0
 */
public interface NewsViewClickListener {

    /**
     * 删除News
     *
     * @param newsEntity {@link WallEntity}
     */
    void remove(NewsEntity newsEntity);

    /**
     * 显示点赞的用户列表
     * @param viewer_id
     * @param refer_id
     * @param type
     */
    void showLovedMember(String viewer_id, String refer_id, String type);

}
