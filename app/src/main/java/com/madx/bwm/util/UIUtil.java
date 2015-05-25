package com.madx.bwm.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.madx.bwm.util.animation.ViewHelper;


public class UIUtil {

    private static int screenWidth;
    private static int screenHeight;
    private static int screenDpi;

    public static int getScreenDip(Context context) {
        if (screenDpi == 0) {
            screenDpi = context.getResources().getDisplayMetrics().densityDpi;
        }
        return screenDpi;
    }

    public static int getScreenWidth(Context context) {
        if (screenWidth == 0) {
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (screenHeight == 0) {
            screenHeight = context.getResources().getDisplayMetrics().heightPixels
                    + getNavigationHeight(context);
        }
        return screenHeight;
    }

    /**
     * @param context
     * @return
     * @Description 获取导航栏高度
     * @date 2014-10-28 上午9:13:30
     */
    private static int getNavigationHeight(Context context) {
        //是否有物理键
        if (!ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(context))) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    /**
     * @param view
     * @param background
     * @Description 设置控件背景
     * @date 2014-10-28 上午9:12:44
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setViewBackground(View view, Drawable background) {
        if (SDKUtil.IS_JB) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setViewBackground(Context context, View view, int background) {
        if (SDKUtil.IS_JB) {
            view.setBackground(context.getResources().getDrawable(background));
        } else {
            view.setBackgroundDrawable(context.getResources().getDrawable(background));
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (SDKUtil.IS_JB) {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    public static void setViewAlpha(View view, float alpha) {
        ViewHelper.setAlpha(view, alpha);
    }

    public static void setViewScale(View view, float scale) {
        ViewHelper.setScaleX(view, scale);
        ViewHelper.setScaleY(view, scale);
    }

    public static void hideKeyboard(Context context, EditText et) {
        if (context != null && et != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context context, EditText et) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }

}
