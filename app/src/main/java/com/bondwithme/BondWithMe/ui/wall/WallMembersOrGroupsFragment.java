package com.bondwithme.BondWithMe.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.WallGroupAdapter;
import com.bondwithme.BondWithMe.adapter.WallMemberAdapter;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.MessageUtil;

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
    private String user_id;
    private String group_id;

    private RecyclerView rvList;
    private RecyclerView.Adapter adapter;
    private List<UserEntity> userEntities;
    private List<GroupEntity> groupEntities;


    public static WallMembersOrGroupsFragment newInstance(String... params) {
        Log.i(TAG, "createInstance");
        return createInstance(new WallMembersOrGroupsFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall_members_or_groups;
    }

    @Override
    public void initView() {

        try {
            getActivity().getIntent().getAction();
            content_group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "2");
        } catch (Exception e) {
            e.printStackTrace();
        }

        rvList = getViewById(R.id.rv_wall_member_or_group_list);
        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
    }

    @Override
    public void requestData() {
        HashMap<String, String> pparams = new HashMap<String, String>();
        pparams.put("content_group_id", content_group_id);
        pparams.put("user_id", MainActivity.getUser().getUser_id());

        new HttpTools(getActivity()).get(Constant.API_WALL_DETAIL, pparams, GET_DETAIL, new HttpCallback() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                WallEntity wall = new Gson().fromJson(response, WallEntity.class);
                if(Constant.ACTION_SHOW_NOTIFY_GROUP.equals(getActivity().getIntent().getAction())){
                    groupEntities = wall.getTag_group();
                    adapter = new WallGroupAdapter(getActivity(), groupEntities);
                } else {
                    userEntities = wall.getTag_member();
                    adapter = new WallMemberAdapter(getActivity(), userEntities, WallMembersOrGroupsFragment.this);
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
        Log.i(TAG, "onActivityResult& requestCode = " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
            //                    startIndex = 0;
            //                    isRefresh = true;
            requestData();//这样直接请求???
        } else {
            MessageUtil.showMessage(getActivity(),R.string.msg_action_canceled);
        }
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
