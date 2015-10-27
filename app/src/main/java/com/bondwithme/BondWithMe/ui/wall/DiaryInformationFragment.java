package com.bondwithme.BondWithMe.ui.wall;

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
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.DiaryInformationAdapter;
import com.bondwithme.BondWithMe.adapter.WallHolder;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
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

    private WallHolder holder;

    private View vProgress;

    private SwipeRefreshLayout swipeRefreshLayout;
    private String content_group_id;
    private LinearLayoutManager llm;
    private RecyclerView rvList;

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

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_diary_information;
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
        rvList = getViewById(R.id.rv_wall_comment_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        mAdapter = new DiaryInformationAdapter(DiaryInformationFragment.this);
        mAdapter.setListener(new DiaryInformationAdapter.DiaryInformationListener() {
            @Override
            public void loadHeadView(WallHolder wallHolder) {
                holder = wallHolder;
                if (wall != null) {
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
                        if (pickUris != null && pickUris.isEmpty()) {
                            holder.setLocalPhotos(pickUris);
                        }
                        getParentActivity().setResult(Activity.RESULT_OK);
                    }
                    break;
                case Constant.INTENT_REQUEST_UPDATE_PHOTOS:
                case Constant.INTENT_REQUEST_UPDATE_WALL:
                    getParentActivity().setResult(Activity.RESULT_OK);
                    requestData();
                    break;
            }
        }
    }

    @Override
    public void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.CONTENT_GROUP_ID, content_group_id);
        params.put(Constant.USER_ID, MainActivity.getUser().getUser_id());

        mHttpTools.get(Constant.API_WALL_DETAIL, params, GET_DETAIL, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                if (wall == null) {
                    getParentActivity().finish();
                } else {
                    vProgress.setVisibility(View.GONE);
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
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
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
            // 更新评论总数
            int commentCount = Integer.valueOf((String) holder.getTvCommentCount().getText()) + 1;
            wall.setComment_count(String.valueOf(commentCount));
            holder.getTvCommentCount().setText(String.valueOf(commentCount));

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
                }
                holder.llWallsImage.setVisibility(View.GONE);
            } else {
                holder.llWallsImage.setVisibility(View.VISIBLE);
                mAdapter.clearData();
            }
        }
    }

    private void setWallContext() {
        holder.setViewClickListener(this);
        holder.setWallEntity(wall);
        holder.setContent(wall, getActivity());
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
        vProgress.setVisibility(View.GONE);
        super.onDestroy();
    }

    /**
     * 显示Wall详情包括评论
     */
    @Override
    public void showComments(WallEntity wallEntity) {
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
                        vProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        vProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String string) {
//                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                        getParentActivity().setResult(Activity.RESULT_OK);
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
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.cancel), new View.OnClickListener() {
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

    @Override
    public void addPhotoed(WallEntity wallEntity, boolean succeed) {
        if (succeed) {
            getParentActivity().setResult(Activity.RESULT_OK);
            requestData();
        } else {
            if (vProgress != null) {
                vProgress.setVisibility(View.GONE);
            }
            LogUtil.e(TAG, "add Photo Fail");
            getParentActivity().finish();
        }
    }

    @Override
    public void savePhotoed(WallEntity wallEntity, boolean succeed) {
        if (vProgress != null) {
            vProgress.setVisibility(View.GONE);
        }
        if (!succeed) {
            LogUtil.e(TAG, "save Photo Fail");
        }
    }

    @Override
    public void onSavePhoto() {
        if (vProgress != null) {
            vProgress.setVisibility(View.VISIBLE);
        }
    }

}
