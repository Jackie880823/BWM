package com.madxstudio.co8.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViaContactMainActivity extends BaseActivity {
    private static final String Tag = ViaContactMainActivity.class.getSimpleName();
    TextView tvSelectContact;
    TextView tvRelationship;
    EditText etMessage;
    ListView lvInfo;
    Button btnAdd;

    List dataNumber = new ArrayList<>();
    List dataEmail = new ArrayList<>();

    List<String> data_Zh = new ArrayList<String>();
    List<String> data_Us = new ArrayList<String>();

    Gson gson = new Gson();

    @Override
    public int getLayout() {
        return R.layout.activity_via_contact_main;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_via_contact));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        getDataEn();
        //系统环境是中文
        if (Locale.getDefault().toString().equals("zh_CN")) {
            isZh = true;
            getDataZh();
        }
        tvSelectContact = getViewById(R.id.tv_select_contact);
        tvRelationship = getViewById(R.id.tv_relationship);
        etMessage = getViewById(R.id.et_message);
        lvInfo = getViewById(R.id.lv);
        btnAdd = getViewById(R.id.btn_add);


        tvSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 2);
            }
        });

        tvRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ViaContactMainActivity.this, RelationshipActivity.class), 1);
            }
        });

        lvInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((CheckBox) view.findViewById(R.id.cb)).toggle();
                //获得选中的手机号
                if ((position < userNumber.size()) && ((CheckBox) view.findViewById(R.id.cb)).isChecked()) {
                    dataNumber.add(userNumber.get(position).toString());
                    Log.d("", "------->" + userNumber.get(position).toString());
                } else if (position < userNumber.size()) {
                    dataNumber.remove(userNumber.get(position).toString());
                    Log.d("", "------->" + userNumber.get(position).toString());
                }

                //获得选中的邮箱
                if ((position > userNumber.size() - 1) && (((CheckBox) view.findViewById(R.id.cb)).isChecked())) {
                    dataEmail.add(userEmail.get(position - userNumber.size()).toString());

                    Log.d("", "------->" + userEmail.get(position - userNumber.size()).toString());
                } else if (position > userNumber.size() - 1) {
                    dataEmail.remove(userEmail.get(position - userNumber.size()).toString());
                    Log.d("", "------->" + userEmail.get(position - userNumber.size()).toString());
                }

                Log.d("", "!!!!!!---->" + gson.toJson(dataNumber));
                Log.d("", "!!!!!!---->" + gson.toJson(dataEmail));


            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(tvSelectContact.getText()) && !TextUtils.isEmpty(tvRelationship.getText()) && !TextUtils.isEmpty(etMessage.getText()) && ((!"[]".equals(gson.toJson(dataNumber))) || (!"[]".equals(gson.toJson(dataEmail))))) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user_fullname", tvSelectContact.getText().toString());
                    params.put("user_relationship_name", relationship);
                    params.put("user_status", "invite");
                    params.put("personal_Msg", etMessage.getText().toString());
                    params.put("creator_id", MainActivity.getUser().getUser_id());
                    params.put("creator_given_name", MainActivity.getUser().getUser_given_name());
                    params.put("creator_country_code", MainActivity.getUser().getUser_country_code());
                    params.put("userEmailList", gson.toJson(dataEmail));
                    params.put("userPhoneList", gson.toJson(dataNumber));


                    new HttpTools(ViaContactMainActivity.this).post(Constant.API_ADD_MEMBER_THROUGH_CONTACT, params,Tag, new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResult(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if ("Success".equals(jsonObject.getString("response_status"))) {
                                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_lwait_for_respons), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if ("Fail".equals(jsonObject.getString("response_status"))) {
                                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });
                } else {
                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_all_field_compulsory), Toast.LENGTH_SHORT).show();
                }
//                else if(TextUtils.isEmpty(tvSelectContact.getText()))
//                {
//                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_select_contact), Toast.LENGTH_SHORT).show();
//                }
//                else if(TextUtils.isEmpty(tvRelationship.getText()))
//                {
//                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_select_relationship), Toast.LENGTH_SHORT).show();
//                }
//                else if(TextUtils.isEmpty(etMessage.getText()))
//                {
//                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_input_personal_message), Toast.LENGTH_SHORT).show();
//                }
//                else if ("[]".equals(gson.toJson(dataNumber)) && "[]".equals(gson.toJson(dataEmail)) )
//                {
//                    Toast.makeText(ViaContactMainActivity.this, getString(R.string.text_choose_contact_information), Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private String username;
    private List userNumber;
    private List userEmail;

    private String relationship;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    tvRelationship.setText(data.getStringExtra("relationship"));
                    int selectMemeber = data.getExtras().getInt("selectMemeber");
                    relationship = data.getStringExtra("relationship");
                    if (isZh) {
                        tvRelationship.setText(data_Zh.get(selectMemeber));
                    } else {
                        tvRelationship.setText(data_Us.get(selectMemeber));
                    }
                }
                break;

            case 2: {

                if (resultCode == Activity.RESULT_OK) {

                    userNumber = new ArrayList<String>();
                    userEmail = new ArrayList<String>();

                    dataNumber.clear();
                    dataEmail.clear();

                    //ContentProvider展示数据类似一个单个数据库表
                    //ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
                    ContentResolver reContentResolver = getContentResolver();
                    //URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
                    Uri contactData = data.getData();
                    //查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
                    Cursor cursor = this.getContentResolver().query(contactData, null, null, null, null);
                    if(cursor.moveToFirst()) {
                        //获得DATA表中的名字
                        username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        //条件为联系人ID
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
                        Cursor phone = reContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null);

                        Cursor email = reContentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null);

                        while (phone.moveToNext()) {
                            userNumber.add(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        }

                        while (email.moveToNext()) {
                            userEmail.add(email.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                        }

                        tvSelectContact.setText(username);
                        Adapter adapter = new Adapter(this, R.layout.item_phone_email, userNumber, userEmail);
                        lvInfo.setAdapter(adapter);
                    }
                }

            }
            break;

        }
    }

    public class Adapter extends ArrayAdapter {

        private Context mContext;
        private int resourceId;
        private List mUserNumber = new ArrayList<String>();
        private List mUserEmail = new ArrayList<String>();

        public Adapter(Context context, int resource, List userNumber, List userEmail) {
            super(context, resource);

            mContext = context;
            resourceId = resource;
            mUserNumber = userNumber;
            mUserEmail = userEmail;
        }

        @Override
        public int getCount() {
            return userNumber.size() + userEmail.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();

            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);

            if (position < mUserNumber.size()) {
                viewHolder.tvInfo.setText(mUserNumber.get(position).toString());
            } else {
                viewHolder.tvInfo.setText(mUserEmail.get(position - mUserNumber.size()).toString());
            }

            return convertView;

        }

        class ViewHolder {
            TextView tvInfo;
        }
    }

    private List<String> getDataZh() {
        Configuration configuration = new Configuration();
        //设置应用为简体中文
        configuration.locale = Locale.SIMPLIFIED_CHINESE;
        getResources().updateConfiguration(configuration, null);
        String[] ralationArrayZh = getResources().getStringArray(R.array.relationship_item);
        data_Zh = Arrays.asList(ralationArrayZh);
        return data_Zh;
    }

    private List<String> getDataEn() {
        Configuration configuration = new Configuration();
        //设置应用为英文
        configuration.locale = Locale.US;
        getResources().updateConfiguration(configuration, null);
        String[] ralationArrayUs = getResources().getStringArray(R.array.relationship_item);
        data_Us = Arrays.asList(ralationArrayUs);
        return data_Us;
    }

    private boolean isZh;


}
