package com.madx.bwm.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.ContactCursorAdapter;
import com.madx.bwm.entity.ContactDetailEntity;
import com.madx.bwm.entity.ContactMessageEntity;
import com.madx.bwm.util.ContactUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.widget.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class TellAFriendsActivity extends BaseActivity {


    private MyDialog showSelectDialog;
    private RecyclerView contact_list;
    private ContentResolver reslover;
    private Cursor cursor;
    private ContactCursorAdapter adapter;
    private SearchView search_view;

    public int getLayout() {
        return R.layout.activity_tell_a_friend;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_tell_friend);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setImageResource(R.drawable.btn_done);
    }

    @Override
    protected void titleRightEvent() {
        sendContact();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        reslover = getContentResolver();
        cursor = ContactUtil.getContacts(this, null, null, null, null);
        contact_list = getViewById(R.id.contact_list);
        adapter = new ContactCursorAdapter(this, cursor);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        contact_list.setLayoutManager(llm);
        contact_list.setAdapter(adapter);

        search_view = getViewById(R.id.search_view);
//        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                if (search_view != null) {
                    // 得到输入管理对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                        imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                    }
                    search_view.clearFocus(); // 不获取焦点
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                String selection = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " LIKE '%" + queryText + "%' " + " OR "
                        + ContactsContract.RawContacts.SORT_KEY_PRIMARY + " LIKE '%" + queryText + "%' ";
                cursor = ContactUtil.getContacts(TellAFriendsActivity.this, null, selection, null, null);
                adapter.swapCursor(cursor);
                return true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (showSelectDialog != null) {
            showSelectDialog.dismiss();
            showSelectDialog = null;
        }
        super.onDestroy();
    }


    private boolean validateForm() {
        if (adapter==null||adapter.getSelectContactIds().size()==0) {
            return false;
        }

        return true;
    }

    ProgressDialog mProgressDialog;

    private void sendContact() {
        if (validateForm()) {
            mProgressDialog = new ProgressDialog(this, R.string.text_waiting);
            mProgressDialog.show();

            ContactMessageEntity messageEntity = generateMessage("");
//            params.put("messageEntity", new Gson().toJson(messageEntity));

            Map<String, String> params = new HashMap<>();
            params.put("user_id",MainActivity.getUser().getUser_id());
            params.put("user_given_name", messageEntity.getUser_given_name());
            params.put("user_country_code", messageEntity.getUser_country_code());
            params.put("personal_msg", messageEntity.getPersonal_msg());
            params.put("contact_list", new Gson().toJson(messageEntity.getContact_list()));

            RequestInfo requestInfo = new RequestInfo();
            requestInfo.params = params;
            requestInfo.url = Constant.API_SHARE2FRIEND;

            new HttpTools(this).post(requestInfo, this,new HttpCallback() {
//            new HttpTools(this).post(Constant.API_SHARE2FRIEND, params, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResult(String string) {
                    MessageUtil.showMessage(TellAFriendsActivity.this, R.string.msg_action_successed);
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
        } else {
            MessageUtil.showMessage(this, R.string.msg_no_contact_select);
        }
    }

    private ContactMessageEntity generateMessage(String msg) {

        ContactMessageEntity contactMessageEntity = new ContactMessageEntity();
        contactMessageEntity.setPersonal_msg(msg);

        List<ContactDetailEntity> contactDetailEntities = new ArrayList<>();
        List<Integer> contactIds = adapter.getSelectContactIds();

        if (contactIds != null) {
            for (Integer contactId : contactIds) {
                ContactDetailEntity contactDetailEntity = new ContactDetailEntity();
                cursor.moveToPosition(contactId);
                contactDetailEntity.setDisplayName(cursor.getString(0));
                contactDetailEntity.setPhoneNumbers(ContactUtil.getContactPhones(this, contactId));
                contactDetailEntity.setEmails(ContactUtil.getContactEmails(this, contactId));
                contactDetailEntities.add(contactDetailEntity);
            }
        }

        contactMessageEntity.setContact_list(contactDetailEntities);
        contactMessageEntity.setUser_country_code(MainActivity.getUser().getUser_country_code());
        contactMessageEntity.setUser_given_name(MainActivity.getUser().getUser_given_name());
        contactMessageEntity.setUser_id(MainActivity.getUser().getUser_id());
        return contactMessageEntity;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.subject_1:
//                subject_text = getString(R.string.mail_subject_1);
//                showSelectDialog.dismiss();
//                break;
            default:
                super.onClick(v);
                break;
        }
    }


    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
