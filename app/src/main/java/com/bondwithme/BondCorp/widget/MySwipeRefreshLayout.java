package com.bondwithme.BondCorp.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.bondwithme.BondCorp.R;

/**
 * Created by wing on 15/3/21.
 * fix the SwipeRefreshLayout indicator does not shown
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {


    private boolean mMeasured = false;
    private boolean mPreMeasureRefreshing = false;
    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setColorSchemeResources(R.color.progress_color_1,R.color.progress_color_2,R.color.progress_color_3,R.color.progress_color_4);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mMeasured) {
            mMeasured = true;
            setRefreshing(mPreMeasureRefreshing);
        }
    }


    @Override
    public void setRefreshing(boolean refreshing) {
        if (mMeasured) {
            super.setRefreshing(refreshing);
        } else {
            mPreMeasureRefreshing = refreshing;
        }
    }


}
