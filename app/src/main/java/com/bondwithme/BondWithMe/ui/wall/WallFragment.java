package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.WallAdapter;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
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
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall;
    }

    private FrameLayout flWallStartUp;
    private TextView tvNoData;

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

    @Override
    public void initView() {

        if(getArguments() != null) {
            member_id = getArguments().getString(ARG_PARAM_PREFIX + 0);
        }

        flWallStartUp = getViewById(R.id.wall_start_up);
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
                if(data.size() >= offset && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
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
        adapter = new WallAdapter(getParentActivity(), data);
        adapter.setPicClickListener(this);
        rvList.setAdapter(adapter);
    }


    @Override
    public void requestData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("limit", offset + "");
        params.put("start", startIndex + "");
        if(!TextUtils.isEmpty(member_id)) {
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
     * 显示Wall详情包括评论
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    @Override
    public void showComments(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallCommentActivity.class);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivityForResult(intent, Constant.ACTION_COMMENT_WALL);
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
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
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
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivity(intent);
    }

    MyDialog removeAlertDialog;

    /**
     * 删除Wall
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     */
    @Override
    public void remove(View view,final String content_group_id) {
        /**wing add begin*/

        initItemMeau(view);
//        showDeleteDialog(content_group_id);
        /**wing add end*/

    }

    private void showDeleteDialog(final String content_group_id) {
        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_diary_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, content_group_id), null);
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
                        refresh();
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
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if(!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }
    }

    private MyDialog itemMenu;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initItemMeau(View v){

//        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
//        popupMenu.inflate(R.menu.wall_item_menu);
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.menu_item_add_photo:
//                        break;
//                    case R.id.menu_edit_this_post:
//                        break;
//                    case R.id.menu_save_all_photos:
//                        break;
//                    case R.id.menu_delete_this_post:
//                        break;
//                }
//                return true;
//            }
//        });
//
//        //使用反射，强制显示菜单图标
//        try {
//            Field field = popupMenu.getClass().getDeclaredField("mPopup");
//            field.setAccessible(true);
//            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
//            mHelper.setForceShowIcon(true);
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        popupMenu.show();

        PopupWindow popupMenu = new PopupWindow(getActivity());
        popupMenu.setAnimationStyle(R.style.PopupAnimation);
//
//        popupMenu = new PopupWindow(getActivity().getLayoutInflater().inflate(R.layout.wall_item_menu,null),
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        popupMenu.setOutsideTouchable(true);
//        popupMenu.showAsDropDown(v,0,0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i("WallFragment", "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case Constant.ACTION_CREATE_WALL:
                    //wait a mement for the pic handle on server
                    try {
                        Thread.sleep(2000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                case Constant.ACTION_COMMENT_WALL:
                    refresh();
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
}
