package com.madxstudio.co8.ui;

import android.view.View;

import com.madxstudio.co8.entity.FamilyMemberEntity;

/**
 *
 * 点击家谱中了项用户监听，返回被点击的{@link View}和用户实例{@link FamilyMemberEntity}
 * Created by Jackie on 8/20/15.
 *
 * @author Jackie
 * @version 1.0
 */
public interface OnFamilyItemClickListener {

    /**
     * 被点击
     * @param view 被点击的{@link View}
     * @param entity 当前点击的用户实例{@link FamilyMemberEntity}
     */
    void onClick(View view, FamilyMemberEntity entity);
}
