package com.bondwithme.BondCorp.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.TextView;

import com.bondwithme.BondCorp.util.LogUtil;

/**
 * Created by quankun on 15/7/27.
 */
public class MessageTextView extends TextView {
    private String text;
    private float textSize;
    private float paddingLeft;
    private float paddingRight;
    private int textColor;
    private Paint paint1 = new Paint();
    private float textShowWidth;
    private static final String TAG = MessageTextView.class.getSimpleName();

    public MessageTextView(Context context) {
        super(context);
    }

    public MessageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        text = this.getText().toString();
        textSize = this.getTextSize();
        textColor = this.getTextColors().getDefaultColor();
        paddingLeft = this.getPaddingLeft();
        paddingRight = this.getPaddingRight();
        paint1.setTextSize(textSize);
        paint1.setColor(textColor);
        paint1.setAntiAlias(true);
    }

    public MessageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MessageTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        if (!lastClickIsLong) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        textShowWidth = this.getMeasuredWidth() - paddingLeft - paddingRight;
        int lineCount = 0;
        text = this.getText().toString();
        if (text == null)
            return;
        char[] textCharArray = text.toCharArray();
        float drawedWidth = 0;
        float charWidth;
        for (int i = 0; i < textCharArray.length; i++) {
            charWidth = paint1.measureText(textCharArray, i, 1);
            if (textCharArray[i] == '\n') {
                lineCount++;
                drawedWidth = 0;
                continue;
            }
            if (textShowWidth - drawedWidth < charWidth) {
                lineCount++;
                drawedWidth = 0;
            }
            canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth, (lineCount + 1) * (textSize * 1.4f), paint1);
            drawedWidth += charWidth;
        }
    }
}
