

package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.InvitedUserEditAdapter;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private boolean isRefresh;

    public List<GroupEntity> at_groups_data = new ArrayList<>();//群组
    public List<UserEntity>  tempuserList = new ArrayList();
    public List<UserEntity>  userList ;

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
//        members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
//        }.getType());
        userList = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
        }.getType());
        changeData();

    }

    private void goMore() {
        tempuserList.clear();
//        userList.clear();
//        Intent intent = new Intent(getParentActivity(), SelectPeopleActivity.class);
//        intent.putExtra("members_data", gson.toJson(adapter.getData()));
//        intent.putExtra("type", 1);
//        startActivityForResult(intent, GET_MEMBERS);
        Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
        intent.putExtra("members_data", gson.toJson(userList));
        intent.putExtra("groups_data", "");
        intent.putExtra("type", 0);
//        Log.i("startActivity===",userList.size()+"");
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


    }
    //移除重复的好友
    private static void removeDuplicate(List<UserEntity> userList) {
        for(int i = userList.size()-1;i>0;i--){
            String item = userList.get(i).getUser_given_name().trim();
            for (int j=i-1;j>=0; j--){
                if (userList.get(j).getUser_given_name().trim().equals(item)){
                    userList.remove(i);
                    break;
                }
            }
        }
    }
    private void  getMembersList(final String strGroupsid){

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("user_id",MainActivity.getUser().getUser_id());
        params.put("group_list",strGroupsid);
        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_GROUP_MEMBERS, params);
        new HttpTools(getActivity()).get(url, null, new HttpCallback() {
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
//                Log.i("at_groups_data===", at_groups_data.get(temp).getGroup_id());
                tempuserList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());

//                Log.i("onResult===",response);
//                Log.i("tempuserList_size===", tempuserList.size()+"");
                submitAddMember(response);
//                userList.addAll(tempuserList);


            }

            @Override
            public void onError(Exception e) {
//                Log.i("onError===",e.getMessage());
                Toast.makeText(getActivity(), getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });



        /*
        if(at_groups_data.size()>0){

            for (int i=0; i < at_groups_data.size(); i++){
                final int temp = i;
                HashMap<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("group_id", at_groups_data.get(i).getGroup_id());
                jsonParams.put("viewer_id", MainActivity.getUser().getUser_id());
                String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);//??
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("condition", jsonParamsString);
                String url = UrlUtil.generateUrl(Constant.API_GROUP_MEMBERS, params);

                new HttpTools(getActivity()).get(url, null, new HttpCallback() {
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
                        Log.i("at_groups_data===", at_groups_data.get(temp).getGroup_id());
                        tempuserList = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());
                        userList.addAll(tempuserList);
                        if(temp == (at_groups_data.size()-1)){
                            removeDuplicate(userList);
                            changeData();
//                                users_date ＝
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
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
        */

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK){
            switch (requestCode){
                case GET_MEMBERS:
                    if(data!=null) {
                        String members = data.getStringExtra("members_data");//获取好友选择页面传来到好友数据
                        members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                        }.getType());
//                        Log.i("member_userList===",members_data.size()+"");
//                        Log.i("Result_userList===",userList.size()+"");
//                    Log.i("members===",members);
                        String groups = data.getStringExtra("groups_data");//获取好友选择页面的群组数据
                        at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {
                        }.getType());
                        //TODO show waitting
//                        submitAddMember(members);
                        List memberList = new ArrayList();
                        for (int i = 0; i < at_groups_data.size(); i++){
                            memberList.add(at_groups_data.get(i).getGroup_id());
                        }
                        if(memberList.size() != 0){
//                            Log.i("groupsid====", gson.toJson(memberList));
                            getMembersList(gson.toJson(memberList));
                        }
                        else {
                            userList.addAll(members_data);
                        }
                        submitAddMember(members);

                    }
                    break;

            }
        }
    }

    private void changeData(){
        /*
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
        */
        if(userList==null){
            userList = new ArrayList<>();
        }
//        Log.i("changeData_userList===",userList.size()+"");

        //排队创建者
        if(MainActivity.getUser().getUser_id().equals(owner_id)) {
            for (UserEntity user :  userList) {
                if(MainActivity.getUser().getUser_id().equals(user.getUser_id())) {
                    userList.remove(user);
                    break;
                }
            }
        }
        //适配器
        adapter = new InvitedUserEditAdapter(getParentActivity(), userList);
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

    //删除好友
    private void submitDeleteMember(final String userId) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userId);//MainActivity
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = jsonParamsString;
        requestInfo.url = String.format(Constant.API_EVENT_REMOVE_MEMBER, group_id);

        new HttpTools(getActivity()).put(requestInfo,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                isRefresh = true;
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (("200").equals(jsonObject.getString("response_status_code"))) {
                        MessageUtil.showMessage(getActivity(),R.string.msg_action_successed);
//                        getMembersList();
                        for (UserEntity user:userList){
                            if(user.getUser_id().equals(userId)){
                                userList.remove(user);
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

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {
                MessageUtil.showMessage(getActivity(),R.string.msg_action_failed);
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }
    private void submitAddMember(final String members){

        final List<UserEntity> new_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {}.getType());
        if(new_members_data==null||new_members_data.isEmpty()){
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("group_id", group_id);
        params.put("group_owner_id", MainActivity.getUser().getUser_id());
        params.put("query_on", "addEventMember");
        params.put("group_members", new Gson().toJson(setGetMembersIds(new_members_data)));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_EVENT_ADD_MEMBERS;
        requestInfo.params = params;
        new HttpTools(getActivity()).post(requestInfo,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
//                Log.i("AddMembe_userList===1", userList.size() + "");
                userList.addAll(new_members_data);
//                Log.i("AddMembe_userList===2", userList.size() + "");
                removeDuplicate(userList);
                if(isRefresh){
                    isRefresh = false;
                }
                changeData();
//                Log.i("AddMembe_userList===3", userList.size() + "");

//                getMembersList();
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

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
