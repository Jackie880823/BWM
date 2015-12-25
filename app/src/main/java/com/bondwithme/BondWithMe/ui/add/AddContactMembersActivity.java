package com.bondwithme.BondWithMe.ui.add;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.AddContactMemberAdapter;
import com.bondwithme.BondWithMe.entity.ContactDetailEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.AddMemberWorkFlow;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.FamilyViewProfileActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.ContactUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactMembersActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    private final int REQUEST_CONTACT_DETAIL_SUCCESSFUL = 0;
    private final int GET_CONTACT = 1;

    private final int ADD_MEMBER = 1;
    private final int PENDING_MEMBER = 2;
    private final int INVITE_MEMBER = 3;

    private RecyclerView rv;
    private View vProgress;
    private EditText etSearch;
    private ImageView ivSearch;
    private List<ContactDetailEntity> contactDetailEntities;
    private List<ContactDetailEntity> serverContactDetailEntities;

    private List<ContactDetailEntity> searchContactDetailEntities = new ArrayList<>();

    //成功完成请求，本地自己更新数据
    private List<ContactDetailEntity> localContactDetailEntities;

    private AddContactMemberAdapter adapter;

    private MyDialog pendingDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case REQUEST_CONTACT_DETAIL_SUCCESSFUL:
                    adapter = new AddContactMemberAdapter(AddContactMembersActivity.this, serverContactDetailEntities);
                    rv.setAdapter(adapter);

                    adapter.setOnIconClickListener(new AddContactMemberAdapter.OnIconClickListener() {
                        @Override
                        public void onIconClick(View v, int position, ContactDetailEntity contactDetailEntity) {
                            switch (v.getId()) {
                                case R.id.tv_added:
                                    doAdded(position, contactDetailEntity);
                                    break;
                                case R.id.ib_add:
                                    doAdd(position, contactDetailEntity);
                                    break;
                                case R.id.ib_pending:
                                    doPending(position, contactDetailEntity);
                                    break;
                                case R.id.ib_invite:
                                    doInvite(position, contactDetailEntity);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    break;

                case GET_CONTACT:
                    getData();
                    break;
            }
        }
    };

    private void doInvite(int position, ContactDetailEntity contactDetailEntity) {
        Intent intent = new Intent(this, AddInviteMembersActivity.class);
        intent.putExtra("contact_detail",contactDetailEntity);
        startActivityForResult(intent, INVITE_MEMBER);
    }

    private void doPending(int position, ContactDetailEntity contactDetailEntity) {
        showPendingDialog(contactDetailEntity.getUser_id());
    }

    private void showPendingDialog(final String userId)
    {
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_bond_alert_member, null);
        pendingDialog = new MyDialog(this, R.string.text_tips_title, view);
        pendingDialog.setCanceledOnTouchOutside(false);

        pendingDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pendingDialog.isShowing())
                    pendingDialog.dismiss();
            }
        });

        view.findViewById(R.id.subject_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPendingResend(userId);
                if (pendingDialog.isShowing())
                    pendingDialog.dismiss();
            }
        });

        view.findViewById(R.id.subject_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPendingDelete(userId);
                if (pendingDialog.isShowing())
                    pendingDialog.dismiss();
            }
        });

        if (!pendingDialog.isShowing())
        {
            pendingDialog.show();
        }

    }

    private void doPendingDelete(String userId) {

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_BONDALERT_MEMEBER_REMOVE + MainActivity.getUser().getUser_id();
        Map<String, String> params = new HashMap<>();
        params.put("member_id", userId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(this).put(requestInfo, TAG, new HttpCallback() {
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
                LogUtil.d(TAG, "doPendingDelete======" + string);
                getData();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void doPendingResend(String userId) {
        vProgress.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AddMemberWorkFlow.class);
        intent.putExtra("from", MainActivity.getUser().getUser_id());
        intent.putExtra("to", userId);
        startActivityForResult(intent, PENDING_MEMBER);
    }

    private void doAdd(int position, ContactDetailEntity contactDetailEntity) {
        Intent intent = new Intent(this, FamilyViewProfileActivity.class);
        if (!TextUtils.isEmpty(contactDetailEntity.getUser_id()))
        {
            intent.putExtra("member_id", contactDetailEntity.getUser_id());

            startActivityForResult(intent, ADD_MEMBER);
        }
    }

    private void doAdded(int position, ContactDetailEntity contactDetailEntity) {
        Intent intent = new Intent(this, FamilyViewProfileActivity.class);
        if (!TextUtils.isEmpty(contactDetailEntity.getUser_id()))
        {
            intent.putExtra("member_id", contactDetailEntity.getUser_id());
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ADD_MEMBER:
                if (resultCode == RESULT_OK)
                {
                    getData();
                }
                break;

            case PENDING_MEMBER:
                vProgress.setVisibility(View.GONE);
                if (resultCode == RESULT_OK)
                {
                    getData();
                    MessageUtil.showMessage(this, R.string.text_success_resend);
                }
                break;

            case INVITE_MEMBER:
                if (resultCode == RESULT_OK)
                {
                }
                break;

            default:
                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_add_contact_members;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_contact);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initView() {
        rv = getViewById(R.id.rv);
        vProgress = getViewById(R.id.rl_progress);
        etSearch = getViewById(R.id.et_search);
        ivSearch = getViewById(R.id.iv_search);

        rv.setLayoutManager(new LinearLayoutManager(this));
        vProgress.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                contactDetailEntities = ContactUtil.getContactDetailEntities(AddContactMembersActivity.this, ContactUtil.getContacts(AddContactMembersActivity.this, null, null, null, null));
                handler.sendEmptyMessage(GET_CONTACT);
            }
        }).start();
//        获取联系人手机和邮箱


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etSearch.getText().toString())) {
                    if (adapter != null) {
                        adapter.changeData(serverContactDetailEntities);
                    }
                } else {
                    if (adapter != null) {
                        searchContactDetailEntities.clear();
                        for (int i = 0; i < serverContactDetailEntities.size(); i++) {
                            if (serverContactDetailEntities.get(i).getDisplayName().toLowerCase().startsWith((etSearch.getText().toString().toLowerCase()))) {
                                searchContactDetailEntities.add(serverContactDetailEntities.get(i));
                            }
                        }
                        adapter.changeData(searchContactDetailEntities);
                    }
                }
            }
        });



    }

    /**
     * 上传联系人与服务器匹配
     */
    @Override
    public void requestData() {

    }

    private void getData()
    {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("user_country_code", MainActivity.getUser().getUser_country_code());
        params.put("contact_list", new Gson().toJson(contactDetailEntities));

//        List<ContactListEntity> list = new ArrayList<>();
//        for (int i = 0; i < contactDetailEntities.size(); i++) {
//            ContactListEntity cle = new ContactListEntity();
//            cle.setDisplayName(contactDetailEntities.get(i).getDisplayName());
//            cle.setPhoneNumbers(contactDetailEntities.get(i).getPhoneNumbers());
//            cle.setEmails(contactDetailEntities.get(i).getEmails());
//            list.add(cle);
//        }
//        params.put("contact_list", new Gson().toJson(list));
//        Log.d(TAG, "requestData: =list====" + new Gson().toJson(list));


        LogUtil.d(TAG, "requestData: ======" + UrlUtil.mapToJsonstring(params));



        new HttpTools(this).post(Constant.API_MATCH_CONTACT_LIST, params, TAG, new HttpCallback() {
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
                LogUtil.d(TAG, "onResult: =========" + string);
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    LogUtil.d(TAG, "onResult: =jsonObject=" + jsonObject.toString());
                    serverContactDetailEntities = gson.fromJson(jsonObject.getString("contactDetails"), new TypeToken<List<ContactDetailEntity>>() {
                    }.getType());

                    handler.sendEmptyMessage(REQUEST_CONTACT_DETAIL_SUCCESSFUL);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
    public void onFragmentInteraction(Uri uri) {

    }
}
