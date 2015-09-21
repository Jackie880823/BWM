package com.bondwithme.BondWithMe.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MyFragmentPagerAdapter;
import com.bondwithme.BondWithMe.util.animation.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvitedStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitedStatusFragment extends BaseFragment<InvitedStatusActivity> implements View.OnClickListener {

    private ViewPager mViewPager;
    private MyFragmentPagerAdapter tabPagerAdapter;
    private ImageView ivCursor;
    private ImageView ivCursorGo;
    private ImageView ivCursorMb;
    private ImageView ivCursorNot;

    private LinearLayout all;
    private LinearLayout going;
    private LinearLayout maybe;
    private LinearLayout not_going;

    private TextView tab_text_all;
    private TextView tab_text_going;
    private TextView tab_text_maybe;
    private TextView tab_text_not_going;

    private ImageView tab_icon_all;
    private ImageView tab_icon_going;
    private ImageView tab_icon_maybe;
    private ImageView tab_icon_not_going;



    TranslateAnimation translateAnimation1;
    TranslateAnimation translateAnimation2;
    int currentTabIndex;
    FragmentManager fragmentManager;

    private int tabIndex = 4;//默认值

    public static InvitedStatusFragment newInstance(String... params) {
        return createInstance(new InvitedStatusFragment(), params);
    }

    public InvitedStatusFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_invited_status;
    }

    @Override
    public void initView() {

        fragmentManager = getFragmentManager();
        mViewPager = getViewById(R.id.mPaper);
        List<Fragment> fragments = new ArrayList<Fragment>();
        if (getParentActivity().eventEntity == null) {
            return;
        }
        String group_id = getParentActivity().eventEntity.getGroup_id();
        String owner_id = getParentActivity().eventEntity.getGroup_owner_id();
        tabIndex = getParentActivity().tabIndex;
        fragments.add(InvitedTabFragment.newInstance(StatusType.all.name(), group_id, owner_id));
        fragments.add(InvitedTabFragment.newInstance(StatusType.yes.name(), group_id, owner_id));
        fragments.add(InvitedTabFragment.newInstance(StatusType.maybe.name(), group_id, owner_id));
        fragments.add(InvitedTabFragment.newInstance(StatusType.no.name(), group_id, owner_id));

//        startClick(tabIndex);
        tabPagerAdapter = new MyFragmentPagerAdapter(getParentActivity().getSupportFragmentManager(), getParentActivity(), mViewPager, fragments);
        tabPagerAdapter.setOnMyPageChangeListenner(new MyFragmentPagerAdapter.OnPageChangeListenner() {
            @Override
            public void onPageChange(int position) {
                changOrdinal(position);
            }


        });

        mViewPager.setAdapter(tabPagerAdapter);

        ivCursor = getViewById(R.id.cursor_all);
        ivCursorGo = getViewById(R.id.cursor_go);
        ivCursorMb = getViewById(R.id.cursor_mb);
        ivCursorNot  = getViewById(R.id.cursor_not);
        all = getViewById(R.id.all);
        going = getViewById(R.id.going);
        maybe = getViewById(R.id.maybe);
        not_going = getViewById(R.id.not_going);


        tab_text_all = getViewById(R.id.tab_text_all);
        tab_text_going = getViewById(R.id.tab_text_going);
        tab_text_maybe = getViewById(R.id.tab_text_maybe);
        tab_text_not_going = getViewById(R.id.tab_text_not_going);

        tab_icon_all = getViewById(R.id.tab_icon_all);
        tab_icon_going = getViewById(R.id.tab_icon_going);
        tab_icon_maybe = getViewById(R.id.tab_icon_maybe);
        tab_icon_not_going = getViewById(R.id.tab_icon_not_going);

        all.setOnClickListener(this);
        going.setOnClickListener(this);
        maybe.setOnClickListener(this);
        not_going.setOnClickListener(this);

        startClick(tabIndex);

    }


    @Override
    public void requestData() {

    }

    enum StatusType {
        all, yes, maybe, no;
    }

    @Override
    public void onClick(View v) {
        int tabIndex = 0;
        switch (v.getId()) {
            case R.id.all:
                tabIndex = 0;
                break;
            case R.id.going:
                tabIndex = 1;
                break;
            case R.id.maybe:
                tabIndex = 2;
                break;
            case R.id.not_going:
                tabIndex = 3;
                break;
            default:
                return;
        }
        mViewPager.setCurrentItem(tabIndex);

    }
    private void startClick(int tabIndex){
        startOrdianl(tabIndex);
        mViewPager.setCurrentItem(tabIndex);
    }
    private void changeTabTitle(int tabIndex) {
        int blackColor = getResources().getColor(R.color.default_text_color_dark);
        int grayColor = getResources().getColor(R.color.default_text_color_light);
        switch (tabIndex) {
            case 0:
                tab_icon_all.setImageResource(R.drawable.members_large_press);
                tab_icon_going.setImageResource(R.drawable.status_going_normal);
                tab_icon_maybe.setImageResource(R.drawable.status_maybe_normal);
                tab_icon_not_going.setImageResource(R.drawable.status_not_going_normal);

                tab_text_all.setTextColor(blackColor);
                tab_text_going.setTextColor(grayColor);
                tab_text_maybe.setTextColor(grayColor);
                tab_text_not_going.setTextColor(grayColor);
                break;
            case 1:
                tab_icon_all.setImageResource(R.drawable.members_large_normal);
                tab_icon_going.setImageResource(R.drawable.status_going_press);
                tab_icon_maybe.setImageResource(R.drawable.status_maybe_normal);
                tab_icon_not_going.setImageResource(R.drawable.status_not_going_normal);

                tab_text_all.setTextColor(grayColor);
                tab_text_going.setTextColor(blackColor);
                tab_text_maybe.setTextColor(grayColor);
                tab_text_not_going.setTextColor(grayColor);
                break;
            case 2:
                tab_icon_all.setImageResource(R.drawable.members_large_normal);
                tab_icon_going.setImageResource(R.drawable.status_going_normal);
                tab_icon_maybe.setImageResource(R.drawable.status_maybe_press);
                tab_icon_not_going.setImageResource(R.drawable.status_not_going_normal);

                tab_text_all.setTextColor(grayColor);
                tab_text_going.setTextColor(grayColor);
                tab_text_maybe.setTextColor(blackColor);
                tab_text_not_going.setTextColor(grayColor);
                break;
            case 3:
                tab_icon_all.setImageResource(R.drawable.members_large_normal);
                tab_icon_going.setImageResource(R.drawable.status_going_normal);
                tab_icon_maybe.setImageResource(R.drawable.status_maybe_normal);
                tab_icon_not_going.setImageResource(R.drawable.status_not_going_press);

                tab_text_all.setTextColor(grayColor);
                tab_text_going.setTextColor(grayColor);
                tab_text_maybe.setTextColor(grayColor);
                tab_text_not_going.setTextColor(blackColor);
                break;
            default:
                return;
        }
    }

    private void startOrdianl(final int tabIndex) {
        if (currentTabIndex == tabIndex  && tabIndex == 5) {
            return;
        }
        changeTabTitle(tabIndex);
        switch (tabIndex) {
            case 0:
                ivCursor.setVisibility(View.VISIBLE);
                ivCursorGo.setVisibility(View.INVISIBLE);
                ivCursorMb.setVisibility(View.INVISIBLE);
                ivCursorNot.setVisibility(View.INVISIBLE);
                break;
            case 1:
                ivCursorGo.setVisibility(View.VISIBLE);
                ivCursor.setVisibility(View.INVISIBLE);
                ivCursorMb.setVisibility(View.INVISIBLE);
                ivCursorNot.setVisibility(View.INVISIBLE);
                break;
            case 2:
                ivCursorMb.setVisibility(View.VISIBLE);
                ivCursor.setVisibility(View.INVISIBLE);
                ivCursorGo.setVisibility(View.INVISIBLE);
                ivCursorNot.setVisibility(View.INVISIBLE);
                break;
            case 3:
                ivCursorNot.setVisibility(View.VISIBLE);
                ivCursor.setVisibility(View.INVISIBLE);
                ivCursorGo.setVisibility(View.INVISIBLE);
                ivCursorMb.setVisibility(View.INVISIBLE);
                break;
        }

        currentTabIndex = tabIndex;
    }

    private void changOrdinal(final int tabIndex){
        if (currentTabIndex == tabIndex) {
            return;
        }
        ivCursorGo.setVisibility(View.INVISIBLE);
        ivCursorMb.setVisibility(View.INVISIBLE);
        ivCursorNot.setVisibility(View.INVISIBLE);
        if (tabIndex > currentTabIndex) {
            int beginX = (tabIndex - 1) * ivCursor.getWidth();
            int endX = beginX + ivCursor.getWidth();
            translateAnimation1 = new TranslateAnimation(beginX, endX, ViewHelper.getY(ivCursor), ViewHelper.getY(ivCursor));
            translateAnimation1.setFillAfter(true);
            translateAnimation1.setDuration(300);
            translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    changeTabTitle(tabIndex);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ivCursor.startAnimation(translateAnimation1);

        } else {
            int beginX = currentTabIndex * ivCursor.getWidth();
            int endX = tabIndex * ivCursor.getWidth();
            translateAnimation2 = new TranslateAnimation(beginX, endX, ViewHelper.getY(ivCursor), ViewHelper.getY(ivCursor));
            translateAnimation2.setFillAfter(true);
            translateAnimation2.setDuration(300);
            ivCursor.startAnimation(translateAnimation2);
            translateAnimation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    changeTabTitle(tabIndex);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        currentTabIndex = tabIndex;
    }

    @Override
    public void onDestroy() {
        if (translateAnimation1 != null) {
            translateAnimation1.cancel();
            translateAnimation1 = null;
        }
        if (translateAnimation2 != null) {
            translateAnimation2.cancel();
            translateAnimation2 = null;
        }
        super.onDestroy();
    }
}