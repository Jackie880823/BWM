package com.madxstudio.co8.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.DiaryInformationAdapter;
import com.madxstudio.co8.adapter.WallHolder;
import com.madxstudio.co8.entity.WallCommentEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.interfaces.DiaryInformationFragmentListener;
import com.madxstudio.co8.interfaces.WallViewClickListener;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.WallUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiaryInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryInformationFragment extends BaseFragment<DiaryInformationActivity> implements WallViewClickListener {
    private final static String TAG = DiaryInformationFragment.class.getSimpleName();

    private static final String GET_DETAIL = TAG + "_GET_DETAIL";
    private static final String POST_DELETE = TAG + "_POST_DELETE";

    private DiaryInformationFragmentListener mListener;

    private WallHolder holder;

    private View vProgress;

    /**
     * 更新标识，如果当前日志有更新设置为true用于获取到最新数据后返回数据给上一层Activity
     */
    private boolean isUpdate;

    private SwipeRefreshLayout swipeRefreshLayout;
    private String content_group_id;

    private DiaryInformationAdapter mAdapter;

    private WallEntity wall;
    /**
     * 网络请求工具实例
     */
    private HttpTools mHttpTools;

    public static DiaryInformationFragment newInstance(String... params) {
        return createInstance(new DiaryInformationFragment(), params);
    }


    public DiaryInformationFragment() {
        super();

        // Required empty public constructor
    }

    public void setListener(DiaryInformationFragmentListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_diary_information;
    }

    @Override
    protected void setParentTitle() {

    }


    @Override
    public void initView() {
        mHttpTools = new HttpTools(getActivity());
        try {
            content_group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // initView
        vProgress = getViewById(R.id.rl_progress);
        RecyclerView rvList = getViewById(R.id.rv_wall_comment_list);
        LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
        rvList.setHasFixedSize(true);

        mAdapter = new DiaryInformationAdapter(DiaryInformationFragment.this);
        mAdapter.setListener(new DiaryInformationAdapter.DiaryInformationListener() {
            @Override
            public void loadHeadView(WallHolder wallHolder) {
                holder = wallHolder;
                if (wall == null) {
                    wall = (WallEntity) getActivity().getIntent().getSerializableExtra(Constant.WALL_ENTITY);
                    if (wall != null) {
                        setWallContext();
                        updatePhotoList();
                    }

                    if (mListener != null) {
                        mListener.onLoadedEntity(wall);
                    }
                } else {
                    setWallContext();
                }
            }
        });

        rvList.setAdapter(mAdapter);


        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }

        });
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);

        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO:
                    if (data != null) {
                        ArrayList<Uri> pickUris;
                        pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.EXTRA_IMAGES_STR);
                        if (pickUris != null && !pickUris.isEmpty()) {
                            holder.setLocalPhotos(pickUris);
                        }
                    }
                    break;
                case Constant.INTENT_REQUEST_UPDATE_PHOTOS:
                case Constant.INTENT_REQUEST_UPDATE_WALL:
                    isUpdate = true;
                    requestData();
                    break;
                case Constant.INTENT_UPDATE_DIARY:
                    String commentCount = data.getStringExtra(Constant.COMMENT_COUNT);
                    if (!wall.getComment_count().equals(commentCount)) {
                        wall.setComment_count(commentCount);
                        setWallContext();
                        setResultOK(false);
                    }
                    break;
            }
        }
    }

    public void setResultOK(boolean isDelete) {
        Intent intent = getParentActivity().getIntent();
        intent.putExtra(Constant.WALL_ENTITY, wall);
        intent.putExtra(Constant.IS_DELETE, isDelete);
        getParentActivity().setResult(Activity.RESULT_OK, intent);
    }

    public void setProgressVisibility(int visibility) {
        if (vProgress != null) {
            vProgress = getViewById(R.id.rl_progress);
        }
        vProgress.setVisibility(visibility);
    }

    @Override
    public void requestData() {

        if (wall == null) {
            LogUtil.i(TAG, "request holder null null========1");
            WallEntity wallEntity = (WallEntity) getActivity().getIntent().getSerializableExtra(Constant.WALL_ENTITY);
            if (wallEntity != null) {
                return;
            }
        }
        LogUtil.i(TAG, "request holder null null========5");
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.CONTENT_GROUP_ID, content_group_id);
        params.put(Constant.USER_ID, MainActivity.getUser().getUser_id());

        mHttpTools.get(Constant.API_WALL_DETAIL, params, GET_DETAIL, new HttpCallback() {
            @Override
            public void onStart() {
                setProgressVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                if (wall == null) {
                    getParentActivity().finish();
                } else {
                    setProgressVisibility(View.GONE);
                    if (holder != null) {
                        setWallContext();
                        updatePhotoList();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "request& onResult# response: " + response);
                wall = new Gson().fromJson(response, WallEntity.class);
                if (isUpdate) {
                    setResultOK(false);
                }

                if (mListener != null) {
                    mListener.onLoadedEntity(wall);
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                getParentActivity().finish();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    /**
     * 更新图片列表
     */
    private void updatePhotoList() {
        if (holder != null) {
            LogUtil.i(TAG, "request holder null null========7");
            String videoName = wall.getVideo_filename();
            if (TextUtils.isEmpty(videoName) && !TextUtils.isEmpty(wall.getPhoto_count())) {
                // 检测网络上的图片
                int photoCount = Integer.valueOf(wall.getPhoto_count());
                LogUtil.d(TAG, "GET_WALL_SUCCEED photoCount = " + photoCount);
                if (photoCount > 0) {
                    Map<String, String> condition = new HashMap<>();
                    condition.put("content_id", wall.getContent_id());
                    Map<String, String> params = new HashMap<>();
                    params.put("condition", UrlUtil.mapToJsonstring(condition));
                    String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
                    mAdapter.setRequest_url(url, wall.getUser_id());
                } else {
                    mAdapter.clearData();
                }
            } else {
                mAdapter.clearData();
            }
        }
    }

    private void setWallContext() {
        holder.setViewClickListener(this);
        holder.setContent(wall, getParentActivity().getIntent().getIntExtra(Constant.POSITION, -1), getActivity());
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mHttpTools != null) {
            mHttpTools.cancelRequestByTag(GET_DETAIL);
        }
        setProgressVisibility(View.GONE);
        super.onDestroy();
    }

    /**
     * 显示Wall详情
     */
    @Override
    public void showDiaryInformation(WallEntity wallEntity) {
    }


    MyDialog removeAlertDialog;

    /**
     * 删除Wall
     *
     * @param wallEntity {@link WallEntity}
     */
    @Override
    public void remove(WallEntity wallEntity) {

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_diary_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, content_group_id), null);
                mHttpTools.put(requestInfo, POST_DELETE, new HttpCallback() {
                    @Override
                    public void onStart() {
                        setProgressVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        setProgressVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String string) {
//                        MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
                        setResultOK(true);
                        getParentActivity().finish();
                    }

                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
                removeAlertDialog.dismiss();
            }
        });
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.text_dialog_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if (!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }

    }

    /**
     * 显示被@的用户列表
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    @Override
    public void showMembers(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_NOTIFY_USER);
        intent.putExtra(Constant.CONTENT_GROUP_ID, content_group_id);
        intent.putExtra(Constant.GROUP_ID, group_id);
        startActivity(intent);
    }

    /**
     * 显示被@的群组列表
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    @Override
    public void showGroups(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_NOTIFY_GROUP);
        intent.putExtra(Constant.CONTENT_GROUP_ID, content_group_id);
        intent.putExtra(Constant.GROUP_ID, group_id);
        startActivity(intent);
    }

    /**
     * 显示点赞的用户列表
     *
     * @param viewer_id {@link WallEntity#user_id}
     * @param refer_id  {@link WallEntity#content_id} or {@link WallCommentEntity#comment_id}
     * @param type      {@link WallUtil#LOVE_MEMBER_COMMENT_TYPE} or {@link WallUtil#LOVE_MEMBER_WALL_TYPE}
     */
    @Override
    public void showLovedMember(String viewer_id, String refer_id, String type) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_LOVED_USER);
        intent.putExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID, viewer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_REFER_ID, refer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_TYPE, type);
        startActivity(intent);
    }

    @Override
    public void showPopClick(WallHolder holder) {
        this.holder = holder;
    }

    public WallHolder getHolder() {
        return holder;
    }

    @Override
    public void addPhotoed(WallEntity wallEntity, boolean succeed) {
        if (succeed) {
            isUpdate = true;
            requestData();
        } else {
            setProgressVisibility(View.GONE);
            LogUtil.e(TAG, "add Photo Fail");
            getParentActivity().finish();
        }
    }

    @Override
    public void saved(WallEntity wallEntity, boolean succeed) {
        setProgressVisibility(View.GONE);
        if (!succeed) {
            LogUtil.e(TAG, "save Fail");
        }
    }

    @Override
    public void onSave() {
        setProgressVisibility(View.VISIBLE);
    }

}
