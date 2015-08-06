package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViaIdNameActivity extends BaseActivity {
    public static final String EXTRA_USER_NAME = "dis_bond_with_me_id";
    private static final String Tag = ViaIdNameActivity.class.getSimpleName();
    EditText etInfo;
    TextView tvWarning;
    TextView tvRelationship;
    Button btnAdd;

    private List<UserEntity> userList;
    private UserEntity userEntity = new UserEntity();

    List<String> data_Zh = new ArrayList<String>();
    List<String> data_Us = new ArrayList<String>();

    @Override
    public int getLayout() {
        return R.layout.activity_via_id_name;
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
        tvTitle.setText(getString(R.string.title_via_id_username));
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
        etInfo = getViewById(R.id.et_info);
        tvWarning = getViewById(R.id.tv_warning);
        tvRelationship = getViewById(R.id.tv_relationship);
        btnAdd = getViewById(R.id.btn_add);

        String name = getIntent().getStringExtra(EXTRA_USER_NAME);
        if(!TextUtils.isEmpty(name)) {
            etInfo.setText(name);
        }

        tvRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ViaIdNameActivity.this, RelationshipActivity.class), 1);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(etInfo.getText()) && !TextUtils.isEmpty(tvRelationship.getText())) {
                    srAddMember();
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private String relationship;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
//                    tvRelationship.setText(data.getStringExtra("relationship"));
                    int selectMemeber = data.getExtras().getInt("selectMemeber");
                    relationship = data.getStringExtra("relationship");
                    if (isZh) {
                        tvRelationship.setText(data_Zh.get(selectMemeber));
                    } else {
                        tvRelationship.setText(data_Us.get(selectMemeber));
                    }
                }
        }
    }

    public void srAddMember() {
        tvWarning.setVisibility(View.GONE);
        //        StringRequest srAddMember = new StringRequest(Request.Method.POST, Constant.API_SEARCH_MEMBER, new Response.Listener<String>() {
        //
        //            @Override
        //            public void onResponse(String response) {
        //
        //                Log.i("","response" + response);
        //
        //                try {
        //                        JSONObject jsonObject = new JSONObject(response);
        //                        if ("Success".equals(jsonObject.getString("response_status")))
        //                        {
        //                            Toast.makeText(ViaIdNameActivity.this, "Success to add member.", Toast.LENGTH_SHORT).show();
        //                            finish();
        //                        }
        //                        else
        //                        {
        //                            tvWarning.setText("Fail to add member. try again.");
        //                            tvWarning.setVisibility(View.VISIBLE);
        //                        }
        //
        //                } catch (JSONException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }, new Response.ErrorListener() {
        //
        //            @Override
        //            public void onErrorResponse(VolleyError error) {
        //                //TODO
        //                error.printStackTrace();
        //                Toast.makeText(ViaIdNameActivity.this, "error.", Toast.LENGTH_SHORT).show();
        //            }
        //        }) {
        //            @Override
        //            protected Map<String, String> getParams() throws AuthFailureError {
        //                HashMap<String, String> params = new HashMap<String, String>();
        //                params.put("user_id", MainActivity.getUser().getUser_id());
        //                params.put("user_relationship_name", tvRelationship.getText().toString());
        //                params.put("search_detail", etInfo.getText().toString());
        //
        //                return params;
        //            }
        //        };
        //        VolleyUtil.addRequest2Queue(ViaIdNameActivity.this, srAddMember, "");


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("user_relationship_name", relationship);
        params.put("search_detail", etInfo.getText().toString());

        new HttpTools(this).post(Constant.API_ADD_SEARCH_MEMBER, params,Tag, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {

                Log.i("", "response" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if("Success".equals(jsonObject.getString("response_status"))) {
                        Toast.makeText(ViaIdNameActivity.this, getString(R.string.text_success_add_member), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        tvWarning.setText(getString(R.string.text_fail_add_member));
                        tvWarning.setVisibility(View.VISIBLE);
                    }

                } catch(JSONException e) {
                    Toast.makeText(ViaIdNameActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ViaIdNameActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
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
