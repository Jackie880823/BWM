package com.madxstudio.co8.ui.company;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.ProfileAdapter;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.PickAndCropPictureActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.widget.MyDialog;

/**
 * 公司简介和管理页，在这里公司可以修改
 * Created 16/3/21.
 *
 * @author Jackie
 * @version 1.0
 */
public class CompanyActivity extends BaseActivity implements View.OnClickListener, ProfileAdapterListener {
    private static final String TAG = "CompanyActivity";
    public static final int REQUEST_PICTURE = 1;

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
     * TitleBar 右边事件
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
        adapter.setListener(this);
        rvProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                if (lastPosition == adapter.getItemCount() - 1) {
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
        ibTop.setOnClickListener(this);
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

            case R.id.ib_top:
                rvProfile.scrollToPosition(0);
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClickProfileImage(View view) {
        showCameraAlbum(view.getWidth(), view.getHeight());
    }

    private void showCameraAlbum(final int photoWidth, final int photoHeight) {
        LogUtil.i(TAG, photoWidth + "*" + photoHeight);
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_camera_album, null);
        myDialog = new MyDialog(this, null, selectIntention);

        TextView tvCamera = (TextView) selectIntention.findViewById(R.id.tv_camera);
        TextView tvAlbum = (TextView) selectIntention.findViewById(R.id.tv_album);
        TextView tv_cancel = (TextView) selectIntention.findViewById(R.id.tv_cancel);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                Intent intent = new Intent(CompanyActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, photoWidth);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, photoHeight);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                intent.putExtra(PickAndCropPictureActivity.CACHE_PIC_NAME, "_background");
                startActivityForResult(intent, REQUEST_PICTURE);
                myDialog = null;
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                Intent intent = new Intent(CompanyActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_PHOTO);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, photoWidth);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, photoHeight);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                intent.putExtra(PickAndCropPictureActivity.CACHE_PIC_NAME, "_background");
                startActivityForResult(intent, REQUEST_PICTURE);
                myDialog = null;
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                myDialog = null;
            }
        });
        myDialog.show();
    }
}
