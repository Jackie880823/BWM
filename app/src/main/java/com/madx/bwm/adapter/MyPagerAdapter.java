package com.madx.bwm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

/**
 * Created by wing on 15/2/10.
 *
 */
public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;

    private final ViewPager pager;
    public MyPagerAdapter(FragmentManager fragmentManager,ViewPager pager) {
        super(fragmentManager);
        this.pager = pager;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
//            case 0: // Fragment # 0 - This will show FirstFragment
//                return FirstFragment.newInstance(0, "Page # 1");
//            case 1: // Fragment # 0 - This will show FirstFragment different title
//                return FirstFragment.newInstance(1, "Page # 2");
//            case 2: // Fragment # 1 - This will show SecondFragment
//                return SecondFragment.newInstance(2, "Page # 3");
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}