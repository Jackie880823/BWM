package com.bondwithme.BondCorp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by christepherzhang on 15/1/23.
 */
public class MyGridViewForScroolView extends GridView {
    public MyGridViewForScroolView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridViewForScroolView(Context context) {
        super(context);
    }

    public MyGridViewForScroolView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
