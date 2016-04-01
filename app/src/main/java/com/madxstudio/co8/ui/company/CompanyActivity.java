package com.madxstudio.co8.ui.company;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.madxstudio.co8.App;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.ProfileAdapter;
import com.madxstudio.co8.entity.OrganisationDetail;
import com.madxstudio.co8.entity.Profile;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.PickAndCropPictureActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.MyDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 公司简介和管理页，在这里公司可以修改
 * Created 16/3/21.
 *
 * @author Jackie
 * @version 1.0
 */
public class CompanyActivity extends BaseActivity implements View.OnClickListener, ProfileAdapter.ProfileAdapterListener {
    private static final String TAG = "CompanyActivity";
    private static final String PUT_UPDATE_ORGANISATION_TAG = "put update organisation";
    public static final String GET_ORGANISATION_TAG = "get organisation";
    public static final String POST_COVER_PHOTO_TAG = "get organisation";

    public static final int REQUEST_PICTURE = 1;

    private ImageButton ibTop;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvProfile;

    private Button btnLeaveGroup;

    private ProfileAdapter adapter;

    private MyDialog myDialog;

    private boolean write;

    private HttpTools mHttpTools;

    private Uri postImageUri;
    private OrganisationDetail detail;

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
            rvProfile.scrollToPosition(0);
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
        adapter = new ProfileAdapter(this, OrganisationConstants.TEST_COMPANY_ID);
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
        if (mHttpTools == null) {
            mHttpTools = new HttpTools(App.getContextInstance());
        }

        mHttpTools.get(String.format(OrganisationConstants.API_GET_ORGANISATION_DETAILS, OrganisationConstants.TEST_COMPANY_ID), null, GET_ORGANISATION_TAG, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "onStart: ");
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "onFinish: ");
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: " + "string = [" + string + "]");
                parseDetail(string);
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "onError: ", e);
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "onCancelled: ");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading() called with: " + "count = [" + count + "], current = [" + current + "]");
            }
        });
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICTURE:
                    rvProfile.scrollToPosition(0);
                    postImageUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                    LogUtil.d(TAG, "onActivityResult: mBackDropImagedUri: " + postImageUri);
                    RecyclerView.ViewHolder hodler = rvProfile.findViewHolderForAdapterPosition(0);
                    adapter.displayProfileImage(hodler, postImageUri.toString());
                    break;
            }
        }
    }

    private void parseDetail(String string) {
        Gson gson = new Gson();
        detail = gson.fromJson(string, OrganisationDetail.class);
        if (detail == null) {
            LogUtil.w(TAG, "parseDetail: get Organisation Detail fail");
        } else {
            adapter.setData(detail);
            LogUtil.d(TAG, "get Organisation not null");
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_leave_group:
                myDialog = new MyDialog(this, R.string.attention_leave_group_title, R.string.attention_leave_organisation_description);
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

    @Override
    public void confirmWrite(Profile profile) {
        LogUtil.d(TAG, "confirmWrite: ");
        if (mHttpTools == null) {
            mHttpTools = new HttpTools(App.getContextInstance());
        }

        postCoverPhoto();

        if (!profile.equals(detail.getProfile())) {
            putProfile(profile);
        }
    }

    /**
     * 上传公司资料
     *
     * @param profile
     */
    private void putProfile(Profile profile) {
        Map<String, String> map = new HashMap<>();
        map.put(OrganisationConstants.NAME, profile.getName());
        map.put(OrganisationConstants.DESCRIPTION, profile.getDescription());
        map.put(OrganisationConstants.ADDRESS, profile.getAddress());
        map.put(OrganisationConstants.PHONE, profile.getPhone());
        map.put(OrganisationConstants.EMAIL, profile.getEmail());

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(OrganisationConstants.API_PUT_ORGANISATION_DETAILS, OrganisationConstants.TEST_COMPANY_ID);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(map);
        Log.d(TAG, "confirmWrite: json param = " + requestInfo.jsonParam);

        mHttpTools.put(requestInfo, PUT_UPDATE_ORGANISATION_TAG, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "onStart: ");
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "onFinish: ");
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: " + "string = [" + string + "]");
                parseDetail(string);
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "onError: ", e);
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "onCancelled: ");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading() called with: " + "count = [" + count + "], current = [" + current + "]");
            }
        });
    }

    /**
     * 上传更新公司图片
     *
     * @return - {@code true}:     需要上传图片 <br/>
     * - {@code false}:    不用上传图片 <br/>
     */
    private boolean postCoverPhoto() {
        LogUtil.d(TAG, "postCoverPhoto: ");
        if (postImageUri == null || Uri.EMPTY == postImageUri) {
            // 不需要上传图片
            return false;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(OrganisationConstants.FILE_KEY, "file");
        map.put(OrganisationConstants.FILE_NAME, "uploadCover" + OrganisationConstants.TEST_COMPANY_ID);
        map.put(OrganisationConstants.MIME_TYPE, "image/png");
        map.put(OrganisationConstants.ORG_ID, OrganisationConstants.TEST_COMPANY_ID);
        map.put(OrganisationConstants.FILE, new File(postImageUri.getPath()));

        mHttpTools.upload(OrganisationConstants.API_POST_ORGANISATION_COVER, map, POST_COVER_PHOTO_TAG, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "onStart: ");
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "onFinish: ");
                // 上传完成需要清除缓存，否则无法加载网络图片
                UniversalImageLoaderUtil.clearCache();
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: " + "string = [" + string + "]");
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "onError: ", e);
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "onCancelled: ");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading() called with: " + "count = [" + count + "], current = [" + current + "]");
            }
        });

        // 需要上传图片
        return true;
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
