package com.madxstudio.co8.ui.workspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.WorkSpaceRecyclerViewAdapter;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.workspace.detail.PostDetailActivity;
import com.madxstudio.co8.util.LogUtil;

import java.util.ArrayList;


/**
 * @author Jackie
 */
public class WorkSpaceFragment extends BaseFragment<MainActivity> implements WorkspaceContracts
        .View, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "WorkSpaceFragment";

    private WorkspaceContracts.Presenter presenter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private WorkSpaceRecyclerViewAdapter adapter;

    public static BaseFragment newInstance(String... params) {

        return createInstance(new WorkSpaceFragment(), params);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkSpaceFragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 列表数据有更改刷新数据
            case Constant.INTENT_REQUEST_CREATE_WALL:
            case Constant.ENTITY_REQUEST_CODE:
                onRefresh();
                break;
        }
    }


    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_workspace;
    }

    /**
     * 强制定义title,请在重写时直接调用setTitle方法,如果不想设置title比如Activity只有一个fragment时重写不做任何事就OK
     */
    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = getViewById(R.id.workspace_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkSpaceRecyclerViewAdapter(getParentActivity(),
                new OnListFragmentInteractionListener() {
                    @Override
                    public void onListFragmentInteraction(WorkspaceEntity item) {
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(Constant.EXTRA_ENTITY, item);
                        startActivityForResult(intent, Constant.ENTITY_REQUEST_CODE);
                    }
                });
        recyclerView.setAdapter(adapter);

        WorkSpacePresenter.Parameter parameter = new WorkSpacePresenter.Parameter();
        Bundle arguments = getArguments();
        if (arguments != null) {
            parameter.member_id = arguments.getString(ARG_PARAM_PREFIX + 0);
        }
        parameter.user_id = MainActivity.getUser().getUser_id();
        parameter.start = String.valueOf(0);
        parameter.limit = String.valueOf(10);
        new WorkSpacePresenter(this, parameter);
    }

    @Override
    public void requestData() {
        presenter.onStart();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void setPresenter(@NonNull WorkspaceContracts.Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * 刷新加载完成
     *
     * @param data
     */
    @Override
    public void loadComplete(ArrayList<WorkspaceEntity> data) {
        adapter.setData(data);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        LogUtil.d(TAG, "onRefresh: ");
        presenter.onStart();
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(WorkspaceEntity item);
    }
}
