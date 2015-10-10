package com.bondwithme.BondWithMe.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created on 9/16/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class CradleBall extends View {

    private int width;
    private int height;

    private Paint paint;

    public CradleBall(Context context) {
        super(context);
        initView();
    }

    public CradleBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CradleBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
    }
}
