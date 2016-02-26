package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * add member flow part 1, part 2 is RelationshipActivity
 */
public class AddMemberWorkFlow extends Activity {

    public final static String FLAG_FROM = "from";
    public final static String FLAG_TO = "to";
    private String to;
    private String from;
    private final static int GET_RELATIONSHIP = 10;
    private String add_flag;
    private String response_relationship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        from = getIntent().getStringExtra(FLAG_FROM);
        to = getIntent().getStringExtra(AddMemberWorkFlow.FLAG_TO);

        if (TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
            finish();
        }

        getAddFlag();
    }

    void getAddFlag() {
        RequestInfo requestInfo = new RequestInfo();
//        requestInfo.params = null;
        requestInfo.url = String.format(Constant.API_GET_MEMBER_TYPE, from, to);

        new HttpTools(this).get(requestInfo, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {


                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        add_flag = jsonObject.getString("response_type");
                        response_relationship = jsonObject.getString("response_relationship");

//                        Toast.makeText(AddMemberWorkFlow.this, "Success to get member type.", Toast.LENGTH_SHORT).show();//成功 需要不提示

//                        if ("Resend".equals(add_flag)) {
//                            Toast.makeText(AddMemberWorkFlow.this, "Success to resend.", Toast.LENGTH_SHORT).show();
//                        } else if ("Request".equals(add_flag)) {
//                            Toast.makeText(AddMemberWorkFlow.this, "Success to request.", Toast.LENGTH_SHORT).show();
//                        } else if ("Accept".equals(add_flag)) {
//                            Toast.makeText(AddMemberWorkFlow.this, "Success to accept.", Toast.LENGTH_SHORT).show();
//                        }

                        if (TextUtils.isEmpty(response_relationship))//关系为空时, 跳转到选择界面
                        {
                            startActivityForResult(new Intent(AddMemberWorkFlow.this, RelationshipActivity.class), GET_RELATIONSHIP);

                        } else {
                            addMember();
                        }
                    } else {
                        //失败
                        MessageUtil.showMessage(AddMemberWorkFlow.this, R.string.msg_action_failed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MessageUtil.showMessage(AddMemberWorkFlow.this, R.string.msg_action_failed);
                }

            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(AddMemberWorkFlow.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }


    private void addMember() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", from);
        params.put("member_id", to);
        params.put("action_type", add_flag);
        params.put("user_relationship_name", response_relationship);

        new HttpTools(this).post(Constant.API_SET_RELATIONSHIP,params, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                finish();
            }

            @Override
            public void onResult(String response) {

//                Log.i("", "response---setRelationship" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
//                        Toast.makeText(AddMemberWorkFlow.this, "Success to set relationship.", Toast.LENGTH_SHORT).show();//成功
                        setResult(RESULT_OK);
                    } else {
                        cancle();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    cancle();
                }
            }

            @Override
            public void onError(Exception e) {

                cancle();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GET_RELATIONSHIP:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        response_relationship = data.getStringExtra("relationship");

                        if (!TextUtils.isEmpty(response_relationship)) {
                            addMember();
                        }else{
                            cancle();
                        }
                    }else{
                        cancle();
                    }
                    break;
                }else{
                    cancle();
                }
        }
    }

    private void cancle() {
        if("Accept".equals(add_flag)){
            setResult(RESULT_OK);
            finish();
        }else{
            setResult(RESULT_CANCELED);
            finish();
        }

    }
}
