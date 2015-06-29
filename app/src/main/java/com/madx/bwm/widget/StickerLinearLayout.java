package com.madx.bwm.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.madx.bwm.R;

public class StickerLinearLayout extends LinearLayout{
    private Context mContext;
    private View rootView;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private LinearLayout layout;

    public StickerLinearLayout(Context context) {
        super(context);
    }
    public StickerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        rootView= LayoutInflater.from(context).inflate(R.layout.activity_new_sticker,null);
        viewPager=(ViewPager)rootView.findViewById(R.id.viewpager);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.sticker_recyclerView);
        layout=(LinearLayout)rootView.findViewById(R.id.sticker_setting_linear);

    }

}
