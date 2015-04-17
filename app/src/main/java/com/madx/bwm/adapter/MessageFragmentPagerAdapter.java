package com.madx.bwm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by christepherzhang on 15/1/22.
 */
public class MessageFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentsList;

    public MessageFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MessageFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

}
