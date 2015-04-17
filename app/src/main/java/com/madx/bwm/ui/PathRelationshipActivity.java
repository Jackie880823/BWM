package com.madx.bwm.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.MemberPathEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathRelationshipActivity extends BaseActivity {

    String memberId ;
    String relationship;
    String fam_nickname;
    String member_status;


    List<MemberPathEntity> pathList = new ArrayList<MemberPathEntity>();

    LinearLayout llRelationship;

    TextView tvRelationship;
    CircularNetworkImage cniMain;
    TextView tvName;

    LinearLayout ll[] = new LinearLayout[4];
    CircularNetworkImage circularNetworkImages[] = new CircularNetworkImage[4];
    TextView tvNames[] = new TextView[4];
    TextView tvRelationships[] = new TextView[4];


    @Override
    public int getLayout() {
        return R.layout.activity_path_relationship;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        /**
         * begin QK
         */
        tvTitle.setText(getString(R.string.title_path_relationship));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void titleLeftEvent() {
        setResult(RESULT_OK);
        super.titleLeftEvent();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        memberId = getIntent().getStringExtra("member_id");
        relationship = getIntent().getStringExtra("relationship");
        fam_nickname = getIntent().getStringExtra("fam_nickname");
        member_status = getIntent().getStringExtra("member_status");

        if (TextUtils.isEmpty(memberId) && TextUtils.isEmpty(relationship))
        {
            finish();
        }

        llRelationship = getViewById(R.id.ll_relationship);

        tvRelationship = getViewById(R.id.tv_relationship);
        cniMain = getViewById(R.id.cni_main);
        tvName = getViewById(R.id.tv_name);//放名字还是放Me????
        tvRelationship.setText(relationship);

        VolleyUtil.initNetworkImageView(PathRelationshipActivity.this, cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        ll[0] = getViewById(R.id.ll_1);
        ll[1] = getViewById(R.id.ll_2);
        ll[2] = getViewById(R.id.ll_3);
        ll[3] = getViewById(R.id.ll_4);

        circularNetworkImages[0] = getViewById(R.id.cni1);
        circularNetworkImages[1] = getViewById(R.id.cni2);
        circularNetworkImages[2] = getViewById(R.id.cni3);
        circularNetworkImages[3] = getViewById(R.id.cni4);

        tvNames[0] = getViewById(R.id.tv_name1);
        tvNames[1] = getViewById(R.id.tv_name2);
        tvNames[2] = getViewById(R.id.tv_name3);
        tvNames[3] = getViewById(R.id.tv_name4);

        tvRelationships[0] = getViewById(R.id.tv_relationship1);
        tvRelationships[1] = getViewById(R.id.tv_relationship2);
        tvRelationships[2] = getViewById(R.id.tv_relationship3);
        tvRelationships[3] = getViewById(R.id.tv_relationship4);

        llRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PathRelationshipActivity.this, RelationshipActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    public void requestData() {
        pathList.clear();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("member_id", getIntent().getStringExtra("member_id"));
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_PATH_RELATIONSHIP, params);

        StringRequest srPhoto = new StringRequest(url, new Response.Listener<String>() {

            GsonBuilder gsonb = new GsonBuilder();

            Gson gson = gsonb.create();

            @Override
            public void onResponse(String response) {

                Log.d("","!!!!!!!!" + response);
//                if ("[]".equals(response))
//                {
//                    Toast.makeText(PathRelationshipActivity.this, "", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
                pathList = gson.fromJson(response, new TypeToken<ArrayList<MemberPathEntity>>() {
                }.getType());

                for (int i = 0; i < pathList.size(); i++) {//关系列表
                    ll[i].setVisibility(View.VISIBLE);
                    VolleyUtil.initNetworkImageView(PathRelationshipActivity.this, circularNetworkImages[i], String.format(Constant.API_GET_PHOTO, Constant.Module_profile, pathList.get(i).getMember_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                    tvRelationships[i].setText(pathList.get(i).getRelationship());
                    tvNames[i].setText(pathList.get(i).getMember_fullname());
                }

                for (int j = pathList.size(); j < 4 ; j++)//其他空位
                {
                    ll[j].setVisibility(View.INVISIBLE);
                }



            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleyUtil.addRequest2Queue(PathRelationshipActivity.this, srPhoto, "");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 1:
                if (resultCode == RESULT_OK)
                {
                    tvRelationship.setText(data.getStringExtra("relationship"));
                    updateRelationship();
                }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            setResult(RESULT_OK);
            finish();
        }
        return super.dispatchKeyEvent(event);
    }


    public void updateRelationship()
    {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("member_id", memberId);
        jsonParams.put("user_relationship_name", tvRelationship.getText().toString());
        jsonParams.put("fam_nickname", fam_nickname);
        jsonParams.put("member_status", member_status);
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        StringRequest srUpdateRelationship = new StringRequest(Request.Method.PUT, String.format(Constant.API_UPDATE_RELATIONSHIP_NICKNAME, MainActivity.getUser().getUser_id()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (("200").equals(jsonObject.getString("response_status_code"))) {
                        Toast.makeText(PathRelationshipActivity.this, getString(R.string.successfully_update_relationship), Toast.LENGTH_SHORT).show();//成功
                        requestData();
                    } else {
                        Toast.makeText(PathRelationshipActivity.this, getString(R.string.fail_update_relationship), Toast.LENGTH_SHORT).show();//失败
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO
                error.printStackTrace();
                Toast.makeText(PathRelationshipActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonParamsString.getBytes();
            }
        };
        VolleyUtil.addRequest2Queue(PathRelationshipActivity.this, srUpdateRelationship, "");
    }
}
