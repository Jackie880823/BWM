package com.madxstudio.co8.ui.add;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.AddInviteMembersAdapter;
import com.madxstudio.co8.entity.ContactDetailEntity;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.RelationshipActivity;
import com.madxstudio.co8.util.RelationshipUtil;
import com.google.gson.Gson;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddInviteMembersActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    private final int GET_RELATIONSHIP = 0;

    private ContactDetailEntity data;

    private TextView tvName;
    private TextView tvRelationship;
    private EditText etInfo;
    private TextView tvNum;
    private RecyclerView rv;
    private PaperButton pbBtn;
    private View vPrgress;

    private String relationshipForServer;
    private String relationshipForLocal;

    private AddInviteMembersAdapter adapter;

    Gson gson = new Gson();

    @Override
    public int getLayout() {
        return R.layout.activity_add_invite_members;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_add_member);
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
        data = (ContactDetailEntity) getIntent().getSerializableExtra("contact_detail");
        if (data == null)
        {
            finish();
        }
        tvName = getViewById(R.id.tv_name);
        tvRelationship = getViewById(R.id.tv_relationship);
        etInfo = getViewById(R.id.et_info);
        tvNum = getViewById(R.id.tv_num);
        pbBtn = getViewById(R.id.pb_send);
        rv = getViewById(R.id.rv);
        vPrgress = getViewById(R.id.rl_progress);

        tvName.setText(data.getDisplayName());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(null);
        adapter = new AddInviteMembersAdapter(data, this);
        rv.setAdapter(adapter);
        etInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNum.setText(etInfo.getText().toString().length() + "/30");
            }
        });

        tvRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddInviteMembersActivity.this, RelationshipActivity.class), GET_RELATIONSHIP);
            }
        });

        etInfo.setText(R.string.text_invite_members_info);

        pbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null)
                {
                    doSend();
                }
            }
        });
    }

    private void doSend() {
        if (checkedInput())
        {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("user_fullname", tvName.getText().toString());
            params.put("user_relationship_name", relationshipForServer);
            params.put("user_status", "invite");
            params.put("personal_Msg", etInfo.getText().toString());
            params.put("creator_id", MainActivity.getUser().getUser_id());
            params.put("creator_given_name", MainActivity.getUser().getUser_given_name());
            params.put("creator_country_code", MainActivity.getUser().getUser_country_code());
            params.put("userEmailList", gson.toJson(adapter.getEmails()));
            params.put("userPhoneList", gson.toJson(adapter.getPhoneNumbers()));

            new HttpTools(AddInviteMembersActivity.this).post(Constant.API_ADD_MEMBER_THROUGH_CONTACT, params, TAG, new HttpCallback() {
                @Override
                public void onStart() {
                    vPrgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    vPrgress.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("Success".equals(jsonObject.getString("response_status"))) {
                            Toast.makeText(AddInviteMembersActivity.this, getString(R.string.text_lwait_for_respons), Toast.LENGTH_SHORT).show();
                            finish();
                        } else if ("Fail".equals(jsonObject.getString("response_status"))) {
                            Toast.makeText(AddInviteMembersActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AddInviteMembersActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddInviteMembersActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
        else
        {
            Toast.makeText(AddInviteMembersActivity.this, getString(R.string.text_all_field_compulsory), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkedInput() {
        return !TextUtils.isEmpty(tvRelationship.getText().toString()) && !TextUtils.isEmpty(etInfo.getText().toString()) && (adapter.getChecked().size() > 0);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case GET_RELATIONSHIP:
                if (resultCode == RESULT_OK)
                {
                    relationshipForServer = data.getStringExtra("relationship");
                    relationshipForLocal = RelationshipUtil.getRelationshipName(this, relationshipForServer);
                    tvRelationship.setText(relationshipForLocal);
                }
                break;
        }
    }
}
