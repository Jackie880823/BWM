package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.InvitedUserAdapter;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bondwithme.BondWithMe.ui.InvitedTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events
 * Use the {@link com.bondwithme.BondWithMe.ui.InvitedTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitedTabFragment extends BaseFragment<InvitedStatusActivity> implements InvitedUserAdapter.OperaListener {

    private ProgressDialog progressDialog;

    public static InvitedTabFragment newInstance(String... params) {

        return createInstance(new InvitedTabFragment(), params);
    }

    public InvitedTabFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_invited_tab;
    }


    private RecyclerView rvList;
    private InvitedUserAdapter adapter;
    private InvitedStatusFragment.StatusType statusType;
    private String typeName;
    private String group_id;
    private String owner_id;
    private static final int ADD_MEMBER = 10;
    private String TAG;

    @Override
    public void initView() {

        progressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));

        rvList = getViewById(R.id.rv_users);

        LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        TAG = this.getClass().getSimpleName();
    }

    public List<UserEntity> data = new ArrayList<UserEntity>();

    @Override
    public void requestData() {

        typeName = getArguments().getString(ARG_PARAM_PREFIX + 0);
        group_id = getArguments().getString(ARG_PARAM_PREFIX + 1);
        owner_id = getArguments().getString(ARG_PARAM_PREFIX + 2);
        if (TextUtils.isEmpty(typeName) || TextUtils.isEmpty(group_id)) {
            if (!getParentActivity().isFinishing()) {
                getParentActivity().finish();
            }
            return;
        }
        statusType = InvitedStatusFragment.StatusType.valueOf(typeName);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", group_id);
        jsonParams.put("response", typeName);
        jsonParams.put("viewer_id", MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);

        String url = UrlUtil.generateUrl(Constant.API_EVENT_INVITED, params);

        new HttpTools(getActivity()).get(url, null, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                //刷新
                if (data == null) {
                    data = new ArrayList<>();
                }
                //排队创建者
                if (MainActivity.getUser().getUser_id().equals(owner_id)) {
                    for (UserEntity user : data) {
                        if (MainActivity.getUser().getUser_id().equals(user.getUser_id())) {
                            data.remove(user);
                            break;
                        }
                    }
                }
                adapter = new InvitedUserAdapter(getParentActivity(), data);
                rvList.setAdapter(adapter);
                adapter.setOperaListener(InvitedTabFragment.this);
                adapter.notifyDataSetChanged();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ADD_MEMBER:
                if (resultCode == getActivity().RESULT_OK) {
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    requestData();
                } else {
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_canceled);
                }
                break;
        }

    }

    private String operaUserId;


    @Override
    public void addMember(UserEntity user) {
        operaUserId = user.getUser_id();
        Intent intent = new Intent(getActivity(), AddMemberWorkFlow.class);
        intent.putExtra("from", MainActivity.getUser().getUser_id());
        intent.putExtra("to", operaUserId);
        startActivityForResult(intent, ADD_MEMBER);
    }

    @Override
    public void goChat(UserEntity user) {
        operaUserId = user.getUser_id();
        Intent intent = new Intent(getActivity(), MessageChatActivity.class);
        //intent.putExtra("userEntity", user);
        intent.putExtra("groupId", user.getGroup_id());
        intent.putExtra("titleName", user.getUser_given_name());
        intent.putExtra("type", 0);
        getActivity().startActivity(intent);
    }
}
