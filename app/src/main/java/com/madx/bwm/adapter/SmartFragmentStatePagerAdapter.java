package com.madx.bwm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/* 
Extension of FragmentStatePagerAdapter which intelligently caches 
all active fragments and manages the fragment lifecycles. 
Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
*/
public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
//
//public class PageAdapter extends SmartFragmentStatePagerAdapter {
//
//    final String TAG = "PageAdapter";
//
//    ArrayList<Fragment> cards = new ArrayList<Fragment>();
//
//    private final String[] TITLES = { getResources().getString(R.string.home) , getResources().getString(R.string.second_home) , getResources().getString(R.string.third_home) , getResources().getString(R.string.fourth_home) };
//
//    FragmentManager fm = null;
//
//    public PageAdapter(FragmentManager fm) {
//        super(fm);
//        this.fm = fm;
//        Log.d(TAG, "Tiles are being created");
//    }
//
//    @Override
//    public int getCount() {
//        return TITLES.length;
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position){
//        return TITLES[position];
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        Fragment card = CardFragment.newInstance(position);
//        Log.d(TAG, "getItem has been called");
//        cards.add(card);
//        return card;
//    }
//}
//My CardFragment.class:
//
//        package com.package.name;
//
//        import java.util.List;
//
//        import android.content.Context;
//        import android.os.Bundle;
//        import android.support.v4.app.Fragment;
//        import android.support.v4.app.FragmentManager;
//        import android.util.Log;
//        import android.util.TypedValue;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.FrameLayout;
//        import android.widget.FrameLayout.LayoutParams;
//
//public class CardFragment extends Fragment{
//
//    final String TAG = "CardFragment";
//
//    ViewGroup cards = null;
//    FrameLayout fl = null;
//
//    static Context context;
//
//    static FragmentManager fragmentManager = null;
//
//    private static final String ARG_POSITION = "position";
//
//    private int position;
//
//    public static CardFragment newInstance(int position) {
//        CardFragment f = new CardFragment();
//        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
//        f.setArguments(b);
//        return f;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        fragmentManager = this.getFragmentManager();
//        position = getArguments().getInt(ARG_POSITION);
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        cards = container;
//        View v = null;
//        if(fragmentManager == null){
//            fragmentManager = this.getFragmentManager();
//        }
//
//        switch (position){
//            case 0:
//                v = inflater.inflate(R.layout.tab_home,null);
//                Log.d(TAG, "Layout for position " + position + " inflated");
//                break;
//            case 1:
//                v = inflater.inflate(R.layout.tab_second_home,null);
//                Log.d(TAG, "Layout for position " + position + " inflated");
//                break;
//            case 2:
//                v = inflater.inflate(R.layout.tab_third_home,null);
//                Log.d(TAG, "Layout for position " + position + " inflated");
//                break;
//            case 3:
//                v = inflater.inflate(R.layout.tab_fourth_home,null);
//                Log.d(TAG, "Layout for position " + position + " inflated");
//                break;
//        }
//
//
//
//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//
//        fl = new FrameLayout(getActivity());
//        fl.setLayoutParams(params);
//
//        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
//                .getDisplayMetrics());
//
//
//        params.setMargins(margin, margin, margin, margin);
//
//
//        fl.addView(v);
//        return fl;
//    }