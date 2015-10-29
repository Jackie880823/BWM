package com.bondwithme.BondWithMe.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.FeelingAdapter;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.util.FileUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created 10/29/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class FeelingFragment extends BaseFragment<FeelingActivity> implements FeelingAdapter.ItemCheckListener {

    /**
     * 选择的心情在列表中的下标
     */
    private int checkItemIndex = -1;

    public static FeelingFragment createInstance(String... params) {
        return createInstance(new FeelingFragment(params[0]), params);
    }

    public FeelingFragment(String checkItemIndexStr){
        super();
        checkItemIndex = Integer.valueOf(checkItemIndexStr);
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.feeling_list;
    }

    @Override
    public void initView() {
        RecyclerView feeling_icons = getViewById(R.id.feeling_icons);
        LinearLayoutManager llmFeeling = new LinearLayoutManager(getParentActivity());
        llmFeeling.setOrientation(LinearLayoutManager.VERTICAL);
        feeling_icons.setLayoutManager(llmFeeling);
        List<String> fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), Constant.PATH_PREFIX);
        // 对文件进行按首字的大小排序
        SortComparator comparator = new SortComparator();
        Collections.sort(fileNames, comparator);
        List<String> filePaths = new ArrayList<>();
        for (String name : fileNames) {
            filePaths.add(Constant.PATH_PREFIX + "/" + name);
        }

        /**
         * 心情列表适配器
         */
        FeelingAdapter feelingAdapter = new FeelingAdapter(getActivity(), filePaths);

        feelingAdapter.setCheckIndex(checkItemIndex);
        feeling_icons.setAdapter(feelingAdapter);

        feelingAdapter.setItemCheckListener(this);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onItemCheckedChange(int position) {
        checkItemIndex = position;
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_CHECK_ITEM_INDEX, checkItemIndex);
        getParentActivity().setResult(Activity.RESULT_OK, intent);
        getParentActivity().finish();
    }

    /**
     * 对字符串按首字母排序按首字母排序，并忽略大小字的排序规则
     */
    class SortComparator implements Comparator<String> {

        /**
         * Compares the two specified objects to determine their relative ordering. The ordering
         * implied by the return value of this method for all possible pairs of
         * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
         * This means that
         * <ul>
         * <li>{@code compare(a,a)} returns zero for all {@code a}</li>
         * <li>the sign of {@code compare(a,b)} must be the opposite of the sign of {@code
         * compare(b,a)} for all pairs of (a,b)</li>
         * <li>From {@code compare(a,b) > 0} and {@code compare(b,c) > 0} it must
         * follow {@code compare(a,c) > 0} for all possible combinations of {@code
         * (a,b,c)}</li>
         * </ul>
         *
         * @param lhs an {@code Object}.
         * @param rhs a second {@code Object} to compare with {@code lhs}.
         * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
         * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
         * @throws ClassCastException if objects are not of the correct type.
         */
        @Override
        public int compare(String lhs, String rhs) {
            String str1 = lhs.substring(0, 1).toUpperCase();
            String str2 = rhs.substring(0, 1).toUpperCase();
            return str1.compareTo(str2);
        }
    }
}
