package com.madxstudio.co8.ui.workspace;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseToolbarActivity;

import java.util.Date;

public class PostDetailActivity extends BaseToolbarActivity {
    private static final String TAG = "PostDetailActivity";

    private AppCompatTextView txtDate;
    private RecyclerView recIcons;
    private RecyclerView recDiscussions;
    private PostIconsAdapter iconsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtDate.setText(new Date().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_right:
                switchLayoutManager();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        txtDate = findView(R.id.txt_workspace_date);
        recIcons = findView(R.id.rec_icons);
        recDiscussions = (RecyclerView) findViewById(R.id.rec_discussions);

        iconsAdapter = new PostIconsAdapter(this);
        switchLayoutManager();
        recIcons.setAdapter(iconsAdapter);

        recDiscussions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false));
        PostIconsAdapter adapter = new PostIconsAdapter(this);
        adapter.setItemCount(20);
        adapter.setGrid(false);
        recDiscussions.setAdapter(adapter);
    }

    /**
     * 切换Image
     */
    private void switchLayoutManager() {
        Log.d(TAG, "switchLayoutManager() called with: " + "");

        RecyclerView.LayoutManager manager;
        boolean isGridLayout;

        if (iconsAdapter == null || iconsAdapter.isGrid()) {
            isGridLayout = false;
            manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        } else {
            isGridLayout = true;
            manager = new GridLayoutManager(this, 4);
            recIcons.setLayoutParams(new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        // 高度自适应。必须是在这23.2.1版本之后才能调用
        manager.setAutoMeasureEnabled(true);
        recIcons.setLayoutManager(manager);

        if (iconsAdapter != null) {
            iconsAdapter.setGrid(isGridLayout);
            iconsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected boolean canBack() {
        return true;
    }
}
