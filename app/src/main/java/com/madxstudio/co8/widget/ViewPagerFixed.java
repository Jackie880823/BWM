package com.madxstudio.co8.widget;

/**
 * Created by wing on 15/4/1.
 * fixed viewpaper pointerIndex out of range bug
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ViewPagerFixed extends android.support.v4.view.ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}