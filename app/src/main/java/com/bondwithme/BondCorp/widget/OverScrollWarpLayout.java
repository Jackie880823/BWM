package com.bondwithme.BondCorp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;
import android.widget.*;

/**
 * Created by liangzemian on 15/6/25.
 */
public class OverScrollWarpLayout extends LinearLayout {
    /**
     * OvershootInterpolator的弹性系数
     */
    private static final float OVERSHOOT_TENSION = 0.75f;

    /**
     * 平滑滚动器
     */
    private android.widget.Scroller mScroller;

    /**
     * 初始化平滑滚动器
     * @param context
     * @param attr
     */
    public OverScrollWarpLayout(Context context, AttributeSet attr) {
        super(context, attr);
        this.setOrientation(LinearLayout.VERTICAL);
        // 初始化平滑滚动器
        mScroller = new android.widget.Scroller(getContext(), new OvershootInterpolator(OVERSHOOT_TENSION));
    }

    /**
     * 初始化平滑滚动器
     * @param context
     */
    public OverScrollWarpLayout(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        // 初始化平滑滚动器
        mScroller = new android.widget.Scroller(getContext(), new OvershootInterpolator(OVERSHOOT_TENSION));
    }

    /**
     * 滚动到目标位置
     * @param fx
     * @param fy
     */
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    /**
     * 设置滚动的相对偏移
     * @param dx
     * @param dy
     */
    public void smoothScrollBy(int dx, int dy) {

        // 设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        // 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
        invalidate();
    }

    @Override
    public void computeScroll() {

        // 先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    public final void smoothScrollToNormal() {
        smoothScrollTo(0, 0);
    }

    public final int getScrollerCurrY() {
        return mScroller.getCurrY();
    }
}
