package com.madxstudio.co8.ui.workspace;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseToolbarActivity;

import java.util.Date;

public class PostDetailActivity extends BaseToolbarActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar.setSubtitle(new Date().toString());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = findView(R.id.rec_icon_list);
    }

    @Override
    protected boolean canBack() {
        return true;
    }
}
