package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.InvitedUserEditAdapter;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.InvitedTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events
 * Use the {@link InvitedTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitedEditFragment extends BaseFragment<InvitedEditActivity> {

    private RecyclerView rvList;
    private InvitedUserEditAdapter adapter;
    private String group_id;
    private String owner_id;
    public List<UserEntity> members_data ;
    private final static int GET_MEMBERS = 1;
    private Gson gson = new Gson();

    public static InvitedEditFragment newInstance(String... params) {

        return createInstance(new InvitedEditFragment(), params);
    }

    public InvitedEditFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_invited_edit;
    }




    @Override
    public void initView() {

        rvList = getViewById(R.id.rv_users);

        LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);


        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                switch (v.getId()) {
                    case R.id.ib_top_button_right:
                        goMore();
                        break;
                    case R.id.ib_top_button_left:
                        setResult();
                        break;
                }
                return false;
            }
        });

        group_id = getArguments().getString(ARG_PARAM_PREFIX + 1);
        owner_id = getArguments().getString(ARG_PARAM_PREFIX + 2);
        String members = getArguments()==null?null:getArguments().getString(ARG_PARAM_PREFIX + 0);
        members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
        }.getType());

        changeData();

    }

    private void goMore() {
        Intent intent = new Intent(getParentActivity(), SelectPeopleActivity.class);
        intent.putExtra("members_data", gson.toJson(adapter.getData()));
        intent.putExtra("type", 1);
        startActivityForResult(intent, GET_MEMBERS);
    }

    private void setResult() {
        Intent intent = new Intent(getParentActivity(), EventEditActivity.class);
        intent.putExtra("members_data", gson.toJson(adapter.getData()));
        getParentActivity().setResult(Activity.RESULT_OK, intent);
//        getParentActivity().finish();
    }

    @Override
    public void requestData() {


//        if (TextUtils.isEmpty(group_id)) {
//            if (!getParentActivity().isFinishing()) {
//                getParentActivity().finish();
//
//            }
//            return;
//        }
//        HashMap<String, String> jsonParams = new HashMap<String, String>();
//        jsonParams.put("group_id", group_id);
//        jsonParams.put("response", "all");
//        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("condition", jsonParamsString);
//
//        String url = UrlUtil.generateUrl(Constant.API_EVENT_INVITED, params);
//
//        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                GsonBuilder gsonb = new GsonBuilder();
//                Gson gson = gsonb.create();
//                members_data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
//                }.getType());
//                //刷新
//                changeData();
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                MessageUtil.showMessage(getActivity(), R.string.msg_unknow_err);
//            }
//        });
//        stringRequest.setShouldCache(false);
//        VolleyUtil.addRequest2Queue(getActivity().getApplicationContext(), stringRequest, "");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK){
            switch (requestCode){
                case GET_MEMBERS:
                    if(data!=null) {
                        String members = data.getStringExtra("members_data");
                        //TODO show waitting
                        submitAddMember(members);
                    }
                    break;

            }
        }
    }

    private void changeData(){
        if(members_data==null){
            members_data = new ArrayList<>();
        }

        //排队创建者
        if(MainActivity.getUser().getUser_id().equals(owner_id)) {
            for (UserEntity user :  members_data) {
                if(MainActivity.getUser().getUser_id().equals(user.getUser_id())) {
                    members_data.remove(user);
                    break;
                }
            }
        }

        adapter = new InvitedUserEditAdapter(getParentActivity(), members_data);
        adapter.setMemberDeleteListenere(new InvitedUserEditAdapter.MemberDeleteListenere() {
            @Override
            public boolean remove(String userId) {
                submitDeleteMember(userId);
                return false;
            }
        });
        rvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void submitDeleteMember(final String userId) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userId);//MainActivity
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        StringRequest srRemoveMember = new StringRequest(Request.Method.PUT, String.format(Constant.API_EVENT_REMOVE_MEMBER, group_id), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (("200").equals(jsonObject.getString("response_status_code"))) {
                        MessageUtil.showMessage(getActivity(),R.string.msg_action_successed);
//                        getMembersList();
                        for(UserEntity user:members_data){
                            if(user.getUser_id().equals(userId)){
                                members_data.remove(user);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        MessageUtil.showMessage(getActivity(),R.string.msg_action_failed);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MessageUtil.showMessage(getActivity(),R.string.msg_action_failed);
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonParamsString.getBytes();
            }

        };

        srRemoveMember.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), srRemoveMember, "");
    }
    private void submitAddMember(final String members){

        final List<UserEntity> new_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {}.getType());
        if(new_members_data==null||new_members_data.isEmpty()){
            return;
        }

        StringRequest stringRequestPost = new StringRequest(Request.Method.POST, Constant.API_EVENT_ADD_MEMBERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                members_data.addAll(new_members_data);
                changeData();
//                getMembersList();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("group_id", group_id);
                params.put("group_owner_id", MainActivity.getUser().getUser_id());
                params.put("query_on", "addEventMember");
                params.put("group_members", new Gson().toJson(setGetMembersIds(new_members_data)));
                return params;
            }
        };
        stringRequestPost.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequestPost, "");
    }

    private List<String> setGetMembersIds(List<UserEntity> users) {
        List<String> ids = new ArrayList<>();
        if (users != null) {
            int count = users.size();
            for (int i = 0; i < count; i++) {
                ids.add(users.get(i).getUser_id());
            }
        }
        return ids;
    }
}
