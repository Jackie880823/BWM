package com.madxstudio.co8.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.FeelingAdapter;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.SortComparator;

import java.util.ArrayList;
import java.util.Collections;
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
        return createInstance(new FeelingFragment(), params);
    }

    public FeelingFragment() {
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.feeling_list;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {

        String checkItemIndexStr = getArguments().getString(ARG_PARAM_PREFIX + 0, "-1");
        checkItemIndex = Integer.valueOf(checkItemIndexStr);

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
}
