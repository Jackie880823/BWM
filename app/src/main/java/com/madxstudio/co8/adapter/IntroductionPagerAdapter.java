package com.madxstudio.co8.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.madxstudio.co8.ui.introduction.IntroductionPagerManager;

/**
 * Created 11/9/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class IntroductionPagerAdapter extends FragmentStatePagerAdapter {
    protected IntroductionPagerManager introductionPagerManager;

    public IntroductionPagerAdapter(FragmentManager fm, IntroductionPagerManager introductionPagerManager) {
        super(fm);
        this.introductionPagerManager = introductionPagerManager;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return this.introductionPagerManager.getItem(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return this.introductionPagerManager.getFragmentCount();
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return this.introductionPagerManager.hasTitles() ? this.introductionPagerManager.getTitle(position) : super.getPageTitle(position);
    }
}
