package com.madx.bwm.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

/**
 * Created by Weidong on 15/6/7.
 */
public class StickerPagerAdapter extends PagerAdapter {

    private List<View> viewList;

    public StickerPagerAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
