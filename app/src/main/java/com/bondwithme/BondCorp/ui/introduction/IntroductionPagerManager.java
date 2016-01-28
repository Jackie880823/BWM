package com.bondwithme.BondCorp.ui.introduction;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 11/9/15.
 * 应用介绍子片段管理者
 *
 * @author Jackie
 * @version 1.0
 */
public class IntroductionPagerManager {
    public static final String DATA = "data";
    private List<CharSequence> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    public IntroductionPagerManager() {
    }

    public Fragment getItem(int position) {
        return this.fragmentList.get(position);
    }

    public int getFragmentCount() {
        return this.fragmentList.size();
    }

    public boolean hasTitles() {
        return this.titleList.size() != 0;
    }

    public CharSequence getTitle(int position) {
        return this.titleList.get(position);
    }

    public IntroductionPagerManager addFragment(Fragment fragment, CharSequence title) {
        this.titleList.add(title);
        this.addFragment(fragment);
        return this;
    }

    public IntroductionPagerManager addFragment(Fragment fragment) {
        this.fragmentList.add(fragment);
        return this;
    }

    /**
     * @param c
     * @param list
     * @param titleList 每个片段设置的标题
     * @return
     * @see #addCommonFragment(Class, List)
     */
    public IntroductionPagerManager addCommonFragment(Class<? extends Fragment> c, List<? extends Serializable> list, List<String> titleList) {
        this.titleList.addAll(titleList);
        this.addCommonFragment(c, list);
        return this;
    }

    /**
     * 添加了子片段
     *
     * @param c    了片段类型限制只能继承{@link Fragment}的子类，所有的了片段视图都将使用该类型实例
     * @param list 每个片段的数据{@link List<? extends Serializable>},保存了每个片段的数据如果有的话
     * @return 返回该管理者实例
     */
    public IntroductionPagerManager addCommonFragment(Class<? extends Fragment> c, List<? extends Serializable> list) {
        try {
            for (int e = 0; e < list.size(); ++e) {
                Fragment fragment = c.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable(DATA, list.get(e));
                fragment.setArguments(bundle);
                this.fragmentList.add(fragment);
            }
        } catch (InstantiationException var6) {
            var6.printStackTrace();
        } catch (IllegalAccessException var7) {
            var7.printStackTrace();
        }

        return this;
    }

    /**
     * @param list 添加一组子片段
     * @return 返回该管理者实例
     */
    public IntroductionPagerManager addCommonFragment(List<? extends Fragment> list) {
        this.fragmentList.addAll(list);
        return this;
    }

    /**
     * @param list
     * @param titleList 每个子片段的标题
     * @return 返回该管理者实例
     * @see #addCommonFragment(List)
     */
    public IntroductionPagerManager addCommonFragment(List<? extends Fragment> list, List<String> titleList) {
        this.titleList.addAll(titleList);
        this.addCommonFragment(list);
        return this;
    }

}
