package com.madxstudio.co8.ui.company;

import android.view.View;

/**
 * Company Profile 中适配器的自定义监听，监听适配器中相应的动作或需要的回调
 * Created 16/3/23.
 *
 * @author Jackie
 * @version 1.0
 */
public interface ProfileAdapterListener {
    /**
     * 公司简介图片被点
     * @param view 被点击的控件
     */
    void onClickProfileImage(View view);
}
