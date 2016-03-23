package com.madxstudio.co8.ui.company;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.ProfileAdapter;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.widget.MyDialog;

/**
 * Created 16/3/21.
 *
 * @author Jackie
 * @version 1.0
 */
public class CompanyActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibTop;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvProfile;

    private Button btnLeaveGroup;

    private ProfileAdapter adapter;

    private MyDialog myDialog;

    private boolean write;

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_company;
    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_org_company_profile);
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        write = !write;
        if (write) {
            rightButton.setImageResource(R.drawable.btn_done);
        } else {
            rightButton.setImageResource(R.drawable.edit_profile);
        }

        if (adapter != null) {
            adapter.changeEdit(write);
        }
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        ibTop = getViewById(R.id.ib_top);
        btnLeaveGroup = getViewById(R.id.btn_leave_group);
        rvProfile = getViewById(R.id.rv_profile);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProfile.setLayoutManager(linearLayoutManager);
        adapter = new ProfileAdapter(this);
        rvProfile.setAdapter(adapter);
        rvProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * Callback method to be invoked when RecyclerView's scroll state changes.
             *
             * @param recyclerView The RecyclerView whose scroll state has changed.
             * @param newState     The updated scroll state. One of {@link #SCROLL_STATE_IDLE},
             *                     {@link #SCROLL_STATE_DRAGGING} or {@link #SCROLL_STATE_SETTLING}.
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**
             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
             * called after the scroll has completed.
             * <p/>
             * This callback will also be called if visible item range changes after a layout
             * calculation. In that case, dx and dy will be 0.
             *
             * @param recyclerView The RecyclerView which scrolled.
             * @param dx           The amount of horizontal scroll.
             * @param dy           The amount of vertical scroll.
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastPosition == adapter.getItemCount() -1) {
                    ibTop.setVisibility(View.GONE);
                    btnLeaveGroup.setVisibility(View.VISIBLE);
                    ibTop.setVisibility(View.VISIBLE);
                } else {
                    btnLeaveGroup.setVisibility(View.GONE);
                }

                int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (firstPosition == 0) {
                    ibTop.setVisibility(View.GONE);
                } else {
                    ibTop.setVisibility(View.VISIBLE);
                }
            }
        });
        btnLeaveGroup.setOnClickListener(this);

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();

        rightButton.setVisibility(View.VISIBLE);
        rightButton.setImageResource(R.drawable.edit_profile);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_leave_group:
                myDialog = new MyDialog(this, R.string.attention_leave_group_title, R.string.attention_leave_group_description);
                myDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 16/3/22 leave group
                        myDialog.dismiss();
                        myDialog = null;
                    }
                });
                myDialog.setButtonCancel(R.string.text_dialog_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        myDialog = null;
                    }
                });
                myDialog.show();
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
