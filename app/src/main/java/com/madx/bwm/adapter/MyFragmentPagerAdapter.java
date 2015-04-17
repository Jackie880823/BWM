package com.madx.bwm.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.List;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

	private final ViewPager pager;
	private List<Fragment> fragments;
	private FragmentManager fm;

	public MyFragmentPagerAdapter(FragmentManager fm, Context context,
			ViewPager pager,List<Fragment> fragments) {
		super(fm);
        this.fm = fm;
		this.pager = pager;
		this.fragments = fragments;
		this.pager.setAdapter(this);
		this.pager.setOnPageChangeListener(this);
	}
	

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}
	
	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	public void onPageScrollStateChanged(int state) {
	}

	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixesl) {
	}
	
	
	public void goNext(){
		int nextItem = pager.getCurrentItem()+1;
		if(nextItem<getCount())
			pager.setCurrentItem(nextItem);
	}
	public void goPrevious(){
		int nextItem = pager.getCurrentItem()-1;
		if(nextItem>-1)
			pager.setCurrentItem(nextItem);
	}

	Fragment currentFragment;
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
	    super.setPrimaryItem(container, position, object);
	}

	@Override
	public void onPageSelected(int position) {
		if(onPageChangeListenner!=null)
			onPageChangeListenner.onPageChange(position);
	}


	OnPageChangeListenner onPageChangeListenner;
	public void setOnMyPageChangeListenner(OnPageChangeListenner pcl){
		onPageChangeListenner = pcl;
	}
	
	public interface OnPageChangeListenner{
		void onPageChange(int position);
	}

    public Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return  fm.findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

	
}