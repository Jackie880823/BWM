package com.bondwithme.BondWithMe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by christepherzhang on 15/1/23.
 */
public class MyListViewForScroolView extends ListView {
    public MyListViewForScroolView(Context context) {
        // TODO Auto-generated method stub
        super(context);
    }

    public MyListViewForScroolView(Context context, AttributeSet attrs) {
        // TODO Auto-generated method stub
        super(context, attrs);
    }

    public MyListViewForScroolView(Context context, AttributeSet attrs, int defStyle) {
        // TODO Auto-generated method stub
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
