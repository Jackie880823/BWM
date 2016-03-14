package com.madxstudio.co8.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.WallAdapter;
import com.madxstudio.co8.adapter.WallHolder;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.interfaces.WallViewClickListener;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.PreferencesUtil;
import com.madxstudio.co8.util.WallUtil;
import com.madxstudio.co8.widget.InteractivePopupWindow;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WallFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallFragment extends BaseFragment<MainActivity> implements WallViewClickListener {

    private static final String TAG = WallFragment.class.getSimpleName();

    private static final String GET_WALL = TAG + "_GET_WALL";
    private static final String PUT_REMOVE = TAG + "_PUT_REMOVE";
    private WallHolder holder;

    public static WallFragment newInstance(String... params) {
        return createInstance(new WallFragment(), params);
    }

    public WallFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected void setParentTitle() {

        setTitle(getString(R.string.title_tab_diary));
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall;
    }

    private View flWallStartUp;
    private View tvNoData;

    private RecyclerView rvList;
    private WallAdapter adapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private View vProgress;
    private boolean isRefresh = true;
    public List<WallEntity> data = new ArrayList<>();
    private int startIndex = 0;
    private int offset = 10;
    private boolean loading;
    LinearLayoutManager llm;
    private String member_id;//根据member查看的wall
    public InteractivePopupWindow popupWindow;
    private static final int GET_DELAY = 0x28;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DELAY:
                    String popText = getParentActivity().getResources().getString(R.string.text_tip_add_diary);
                    if (TextUtils.isEmpty(popText)) return;

                    popupWindow = new InteractivePopupWindow(getParentActivity(), getParentActivity().rightButton, popText, 0);
                    popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                        @Override
                        public void popDismiss() {
                            //存储本地
                            PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_DIARY, true);

                        }
                    });
                    popupWindow.showPopupWindow(true);
                    break;
            }
        }
    };

    @Override
    public void initView() {

        if (getArguments() != null) {
            member_id = getArguments().getString(ARG_PARAM_PREFIX + 0);
        }

        flWallStartUp = getViewById(R.id.tv_no_data);
        tvNoData = getViewById(R.id.tv_no_data);

        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        rvList = getViewById(R.id.rv_wall_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        initAdapter();
        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if (data.size() >= offset && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    requestData();//再请求数据
                }
            }
        });


        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });
    }

    /**
     * 初始化Wlla列表适配器
     */
    private void initAdapter() {
        adapter = new WallAdapter(this, data);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);
    }


    @Override
    public void requestData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("limit", offset + "");
        params.put("start", startIndex + "");
        if (!TextUtils.isEmpty(member_id)) {
            params.put("member_id", member_id + "");
        }

        LogUtil.i(TAG, "requestData& startIndex: " + startIndex);

        String url = UrlUtil.generateUrl(Constant.API_WALL_MAIN, params);
        new HttpTools(getActivity()).get(url, params, GET_WALL, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                //DateDeserializer ds = new DateDeserializer();
                //给GsonBuilder方法单独指定Date类型的反序列化方法
                //gsonb.registerTypeAdapter(Date.class, ds);
                Gson gson = gsonb.create();
                try {
                    boolean hasData = data != null && data.size() > 0;
                    data = gson.fromJson(response, new TypeToken<ArrayList<WallEntity>>() {
                    }.getType());
                    LogUtil.i(TAG, "requestData& isRefresh: " + isRefresh);
                    if (isRefresh) {
                        startIndex = data.size();
                        finishReFresh();
                        initAdapter();
                    } else {
                        startIndex += data.size();
                        adapter.add(data);
                    }
                    LogUtil.i(TAG, "requestData& adapter size: " + adapter.getItemCount());
                    if (data.size() <= 0 && !hasData) {
                        if (TextUtils.isEmpty(member_id)) {
                            tvNoData.setVisibility(View.GONE);
                            flWallStartUp.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.VISIBLE);
                            flWallStartUp.setVisibility(View.GONE);
                        }
                        swipeRefreshLayout.setVisibility(View.GONE);
                    } else {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        flWallStartUp.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.GONE);
                    }
                    loading = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    reInitDataStatus();
                } finally {
                    vProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                if (isRefresh) {
                    finishReFresh();
                }
                loading = false;
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (MainActivity.IS_INTERACTIVE_USE && !PreferencesUtil.getValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_DIARY, false)) {
                handler.sendEmptyMessage(GET_DELAY);
            }

        }

    }

    private void reInitDataStatus() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        startIndex = 0;
        loading = false;
    }


    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }

    /**
     * 显示Wall详情
     */
    @Override
    public void showDiaryInformation(WallEntity wallEntity) {
        Intent intent;
        intent = new Intent(getActivity(), DiaryInformationActivity.class);
        intent.putExtra(Constant.WALL_ENTITY, wallEntity);
        intent.putExtra(Constant.CONTENT_GROUP_ID, wallEntity.getContent_group_id());
        intent.putExtra(Constant.GROUP_ID, wallEntity.getGroup_id());
        int position = adapter.getData().indexOf(wallEntity);
        intent.putExtra(Constant.POSITION, position);
        startActivityForResult(intent, Constant.INTENT_UPDATE_DIARY);
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

    MyDialog removeAlertDialog;

    /**
     * 删除Wall
     *
     * @param wallEntity {@link WallEntity}
     */
    @Override
    public void remove(WallEntity wallEntity) {
        showDeleteDialog(wallEntity);
    }

    private void showDeleteDialog(final WallEntity wallEntity) {
        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_diary_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, wallEntity.getContent_group_id()), null);
                new HttpTools(getActivity()).put(requestInfo, PUT_REMOVE, new HttpCallback() {
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
                        List<WallEntity> data = adapter.getData();
                        int index = data.indexOf(wallEntity);
                        data.remove(wallEntity);
                        adapter.notifyItemRemoved(index);
                    }

                    @Override
                    public void onError(Exception e) {
                        vProgress.setVisibility(View.GONE);
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
     * 显示点赞的用户列表
     *
     * @param viewer_id {@link WallEntity#user_id}
     * @param refer_id  {@link WallEntity#content_id}
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
            refresh(adapter.getData().indexOf(wallEntity));
        } else {
            if (vProgress != null) {
                vProgress.setVisibility(View.GONE);
            }
            LogUtil.e(TAG, "addPhoto Fail");
        }
    }

    @Override
    public void saved(WallEntity wallEntity, boolean succeed) {
        if (vProgress != null) {
            vProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSave() {
        if (vProgress != null) {
            vProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i("WallFragment", "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.INTENT_UPDATE_DIARY: { // 更新了评论
                    if (data == null) {
                        return;
                    }
                    List<WallEntity> wallEntities = adapter.getData();

                    int position = data.getIntExtra(Constant.POSITION, -1);
                    LogUtil.d(TAG, "onActivityResult& position: " + position);

                    if (position >= 0 && position < wallEntities.size()) {
                        WallEntity wallEntity;
                        wallEntity = (WallEntity) data.getSerializableExtra(Constant.WALL_ENTITY);
                        if (wallEntity != null) {
                            wallEntities.set(position, wallEntity);
                            adapter.notifyItemChanged(position);
                        }

                        String commentCount = data.getStringExtra(Constant.COMMENT_COUNT);
                        if (!TextUtils.isEmpty(commentCount)) {
                            wallEntity = wallEntities.get(position);
                            if (!wallEntity.getComment_count().equals(commentCount)) {
                                wallEntity.setComment_count(commentCount);
                                adapter.notifyItemChanged(position);
                            }
                        }
                    }
                }
                break;

                case Constant.INTENT_REQUEST_CREATE_WALL:
                    //wait a mement for the pic handle on server
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    refresh();
                    break;

                case Constant.INTENT_REQUEST_UPDATE_WALL: // 更新了日志
                case Constant.INTENT_REQUEST_UPDATE_PHOTOS:// 更新了图片的日志
                {
                    if (data == null) {
                        LogUtil.w(TAG, "onActivityResult& result data is null");
                        return;
                    }

                    int position = data.getIntExtra(Constant.POSITION, -1);
                    refresh(position);
                }
                break;

                case Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO:
                    if (data != null && holder != null) {
                        ArrayList<Uri> pickUris;
                        pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.EXTRA_IMAGES_STR);
                        if (pickUris != null && !pickUris.isEmpty()) {
                            holder.setLocalPhotos(pickUris);
                        }
                    }
                    break;
            }
        }

    }

    /**
     * 刷新，重新获取所有数据
     */
    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        startIndex = 0;
        requestData();
    }

    /**
     * 刷新，重新获取指定项数据
     */
    private void refresh(final int position) {

        final List<WallEntity> wallEntities = adapter.getData();
        if (position < 0 || position >= wallEntities.size()) {
            return;
        }

        WallEntity wallEntity = wallEntities.get(position);
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.CONTENT_GROUP_ID, wallEntity.getContent_group_id());
        params.put(Constant.USER_ID, MainActivity.getUser().getUser_id());

        String GET_DETAIL = "GET_DELAY";
        new HttpTools(getContext()).get(Constant.API_WALL_DETAIL, params, GET_DETAIL, new HttpCallback() {
            WallEntity wall;

            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                if (wall == null) {
                    getParentActivity().finish();
                } else {
                    wallEntities.set(position, wall);
                    adapter.notifyItemChanged(position);
                    vProgress.setVisibility(View.GONE);
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
}
