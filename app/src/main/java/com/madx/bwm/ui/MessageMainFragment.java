package com.madx.bwm.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.madx.bwm.R;
import com.madx.bwm.widget.MyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/4/17.
 */
public class MessageMainFragment extends BaseFragment<MainActivity> implements View.OnClickListener {
    private ViewPager pager;
    private MyFragmentPageAdapter mAdapter;
    private TextView message_member_tv;
    private TextView message_group_tv;
    private Dialog showSelectDialog;
    private List<Fragment> fragmentList;

    public static MessageMainFragment newInstance(String... params) {

        return createInstance(new MessageMainFragment());
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_message_list;
    }

    @Override
    public void initView() {
        pager = getViewById(R.id.message_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        message_group_tv = getViewById(R.id.message_group_tv);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragmentList = new ArrayList<>();
        fragmentList.add(MemberMessageFragment.newInstance());
        fragmentList.add(GroupMessageFragment.newInstance());
        //初始化自定义适配器
        mAdapter = new MyFragmentPageAdapter(fm, fragmentList);
        //绑定自定义适配器
        pager.setAdapter(mAdapter);
        pager.setOnPageChangeListener(new MyOnPageChanger());
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                showSelectDialog();
                return false;
            }
        });
        message_member_tv.setOnClickListener(this);
        message_group_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_member_tv:
                pager.setCurrentItem(0);
                break;
            case R.id.message_group_tv:
                pager.setCurrentItem(1);
                break;
        }
    }

    class MyOnPageChanger implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                message_member_tv.setBackgroundResource(R.drawable.message_member_selected_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_normal_shap);
                message_group_tv.setTextColor(Color.parseColor("#666666"));
                message_member_tv.setTextColor(Color.parseColor("#ffffff"));
            } else {
                message_member_tv.setBackgroundResource(R.drawable.message_member_normal_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_selected_shap);
                message_group_tv.setTextColor(Color.parseColor("#ffffff"));
                message_member_tv.setTextColor(Color.parseColor("#666666"));
            }
        }

    }

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);

        showSelectDialog = new MyDialog(getParentActivity(), null, selectIntention);
        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNewMembersActivity.class));
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                showSelectDialog.dismiss();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    @Override
    public void requestData() {

    }

    class MyFragmentPageAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;

        public MyFragmentPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}
