package com.bondwithme.BondWithMe.ui;

import com.bondwithme.BondWithMe.R;

/**
 * Created by liangzemian on 15/5/4.
 */
public class EventStartupFragment extends BaseFragment<MainActivity>  {
    public static EventStartupFragment newInstance(String... params) {

        return createInstance(new EventStartupFragment());
    }
    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_startup;
    }

    @Override
    public void initView() {
    }

    @Override
    public void requestData() {

    }

}