package com.madxstudio.co8.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.TextView;

import com.madxstudio.co8.util.LogUtil;

/**
 * Created by Jackie Zhu on 7/17/15.
 * 长按自由复制的TextView,此TextView解决了双击弹出复制的问题
 * @author Jackie Zhu
 */
public class FreedomSelectionTextView extends TextView {
    private static final String TAG = FreedomSelectionTextView.class.getSimpleName();

    public FreedomSelectionTextView(Context context) {
        super(context);
    }

    public FreedomSelectionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreedomSelectionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreedomSelectionTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * <br>记录最后一次点击是否为长按，部分手机长按后还会调用{@link #performClick()},设置此标识位为在
     * <br>长按当前View后值设置为<code>true</code>, 调用{@link #performClick()}时不禁用选择复制功能
     */
    boolean lastClickIsLong = false;

    @Override
    public boolean performLongClick() {
        LogUtil.i(TAG, "performLongClick");
        lastClickIsLong = true;
        boolean handled = super.performLongClick();
        return handled;
    }

    @Override
    public boolean performClick() {
        LogUtil.i(TAG, "performClick");
        if(!lastClickIsLong) {
            setTextIsSelectable(false);
        }
        lastClickIsLong = false;
        boolean result = super.performClick();
        setTextIsSelectable(true);
        return result;
    }

    /**
     * Views should implement this if the view itself is going to add items to
     * the context menu.
     *
     * @param menu the context menu to populate
     */
    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }
}
