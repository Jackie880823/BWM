package com.madxstudio.co8.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.WallGroupAdapter;
import com.madxstudio.co8.adapter.WallMemberAdapter;
import com.madxstudio.co8.entity.GroupEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.WallUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class WallMembersOrGroupsFragment extends BaseFragment<WallMembersOrGroupsActivity> implements AbsListView.OnItemClickListener {

    private final static String TAG = WallMembersOrGroupsFragment.class.getSimpleName();

    private final static String GET_DETAIL = TAG + "_GET_DETAIL";

    private String content_group_id;
//    private String user_id;
//    private String group_id;

    private String viewer_id;
    private String refer_id;
    private String type;

    private RecyclerView rvList;
    private RecyclerView.Adapter adapter;
    private List<UserEntity> userEntities;
    private List<GroupEntity> groupEntities;

    private View vProgress;

    public WallMembersOrGroupsFragment() {
        super();
    }

    public static WallMembersOrGroupsFragment newInstance(String... params) {
        LogUtil.i(TAG, "createInstance");
        return createInstance(new WallMembersOrGroupsFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall_members_or_groups;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {

        try {
            String action = getActivity().getIntent().getAction();
            if(!Constant.ACTION_SHOW_LOVED_USER.equals(action)) {
                content_group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
//                group_id = getArguments().getString(ARG_PARAM_PREFIX + "2");
            } else {
                viewer_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
                refer_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
                type = getArguments().getString(ARG_PARAM_PREFIX + "2");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        rvList = getViewById(R.id.rv_wall_member_or_group_list);
        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
    }

    @Override
    public void requestData() {
        HashMap<String, String> pparams = new HashMap<>();
        String url;
        if(!Constant.ACTION_SHOW_LOVED_USER.equals(getActivity().getIntent().getAction())) {
            url = Constant.API_WALL_DETAIL;
            pparams.put(Constant.CONTENT_GROUP_ID, content_group_id);
            pparams.put(Constant.USER_ID, MainActivity.getUser().getUser_id());
        } else {
            url = Constant.API_WALL_GET_LOVE_MEMBER_LIST;
            pparams.put(WallUtil.GET_LOVE_LIST_VIEWER_ID, viewer_id);
            pparams.put(WallUtil.GET_LOVE_LIST_REFER_ID, refer_id);
            pparams.put(WallUtil.GET_LOVE_LIST_TYPE, type);
        }

        new HttpTools(getActivity()).get(url, pparams, GET_DETAIL, new HttpCallback() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {

                if (vProgress != null) {
                    vProgress.setVisibility(View.GONE);
                }

                if(Constant.ACTION_SHOW_LOVED_USER.equals(getActivity().getIntent().getAction())){
                    userEntities = new Gson().fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());
                    adapter = new WallMemberAdapter(getActivity(), userEntities, WallMembersOrGroupsFragment.this);
                } else {
                    WallEntity wall = new Gson().fromJson(response, WallEntity.class);
                    if(Constant.ACTION_SHOW_NOTIFY_GROUP.equals(getActivity().getIntent().getAction())) {
                        groupEntities = wall.getTag_group();
                        adapter = new WallGroupAdapter(getActivity(), groupEntities);
                    } else {
                        userEntities = wall.getTag_member();
                        adapter = new WallMemberAdapter(getActivity(), userEntities, WallMembersOrGroupsFragment.this);
                    }
                }
                rvList.setAdapter(adapter);
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
        LogUtil.i(TAG, "onActivityResult& requestCode = " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
//            MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
            //                    startIndex = 0;
            //                    isRefresh = true;
            requestData();//这样直接请求???
        } else {
            MessageUtil.getInstance().showShortToast(R.string.msg_action_canceled);
        }
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();

        // 取消获取群组或者用户列表的网络请求
        new HttpTools(getActivity()).cancelRequestByTag(GET_DETAIL);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
