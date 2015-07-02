package com.madx.bwm.ui;

import android.app.Activity;
import android.view.View;

import com.madx.bwm.R;
import com.madx.bwm.adapter.ArchiveCommentAdapter;
import com.madx.bwm.entity.ArchiveCommentEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzemian on 15/7/1.
 */
public class ArchiveCommentFragment extends BaseFragment<Activity> implements View.OnClickListener{

    private List<ArchiveCommentEntity> data = new ArrayList<>();
    private ArchiveCommentAdapter adapter;

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_archive_comment;
    }

    @Override
    public void initView() {

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View v) {

    }
}
