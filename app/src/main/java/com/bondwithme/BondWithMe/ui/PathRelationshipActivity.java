package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MemberPathEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PathRelationshipActivity extends BaseActivity {
    private static final String Tag = PathRelationshipActivity.class.getSimpleName();
    private boolean isZh;
    String memberId;
    String relationship;
    String fam_nickname;
    String member_status;
    List<MemberPathEntity> pathList = new ArrayList<MemberPathEntity>();
    List<String> data_Zh = new ArrayList<String>();
    List<String> data_Us = new ArrayList<String>();
    private int selectMemeber;
    private int RESULT = 0;

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
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_path_relationship));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void titleLeftEvent() {
//        setResult(RESULT_OK);
        super.titleLeftEvent();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        if (isZh) {
//            Log.i("finish====",tvRelationship.getText().toString()+"");
            intent.putExtra("relationship", data_Us.get(data_Zh.indexOf(tvRelationship.getText().toString())));
        } else {
            intent.putExtra("relationship", tvRelationship.getText().toString());
        }
        setResult(RESULT, intent);
        super.finish();
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
        memberId = getIntent().getStringExtra("member_id");
        relationship = getIntent().getStringExtra("relationship");
        fam_nickname = getIntent().getStringExtra("fam_nickname");
        member_status = getIntent().getStringExtra("member_status");


        if (TextUtils.isEmpty(memberId) && TextUtils.isEmpty(relationship)) {
            finish();
        }

        llRelationship = getViewById(R.id.ll_relationship);

        tvRelationship = getViewById(R.id.tv_relationship);//头部右边显示的关系
        cniMain = getViewById(R.id.cni_main);
        tvName = getViewById(R.id.tv_name);//放名字还是放Me????

        int length=getIntent().getIntExtra("selectMemeber", -1);
        if(length==-1||length>=data_Zh.size()){
            tvRelationship.setText("");
        }else{
        if (isZh) {
            tvRelationship.setText(data_Zh.get(getIntent().getIntExtra("selectMemeber", -1)));
        } else {
            tvRelationship.setText(data_Us.get(getIntent().getIntExtra("selectMemeber", data_Us.size()-1)));
        }
        }


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
                //选择关系
                Intent intent = new Intent(PathRelationshipActivity.this, RelationshipActivity.class);
                startActivityForResult(intent, 1);
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

        new HttpTools(PathRelationshipActivity.this).get(url, null, Tag, new HttpCallback() {
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

                pathList = gson.fromJson(response, new TypeToken<ArrayList<MemberPathEntity>>() {
                }.getType());

                if (pathList != null) {
                    for (int i = 0; i < pathList.size(); i++) {//关系列表

                        ll[i].setVisibility(View.VISIBLE);
                        VolleyUtil.initNetworkImageView(PathRelationshipActivity.this, circularNetworkImages[i], String.format(Constant.API_GET_PHOTO, Constant.Module_profile, pathList.get(i).getMember_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//                        if(i == 0){

                            if(TextUtils.isEmpty(tvRelationship.getText().toString())){
                                String mrelationship4En = pathList.get(0).getRelationship();
                                int mPosition = data_Us.indexOf(mrelationship4En);
                                if(isZh){
                                    tvRelationship.setText(data_Zh.get(mPosition));
                                }else {
                                    tvRelationship.setText(mrelationship4En);
                                }

                            }
//                        }
                        String relationship4En = pathList.get(i).getRelationship();
                        int position = data_Us.indexOf(relationship4En);
                        /**wing modify for no relationship begin*/
                        if (TextUtils.isEmpty(relationship4En)) {
                            continue;
                        }
//                        if(TextUtils.isEmpty(tvRelationship.getText().toString())){
//                            String mRelationship = pathList.get(0).getRelationship();
//                            if (isZh) {
////                                tvRelationship.setText(data_Us.indexOf(mRelationship));
//                            }else {
////                                tvRelationship.setText(mRelationship);
//                            }
//                        }
                        if (isZh) {
//                            int position = data_Us.indexOf(relationship4En);
                            if (position != -1) {
                                tvRelationships[i].setText(data_Zh.get(position));
                            }
                        } else {
                            tvRelationships[i].setText(relationship4En);
                        }
                        /**wing modify end*/
                        tvNames[i].setText(pathList.get(i).getMember_fullname());
                    }

                    for (int j = pathList.size(); j < 4; j++)//其他空位
                    {
                        ll[j].setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PathRelationshipActivity.this, getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //获取选择关系界面传来的关系 根据index。
                    selectMemeber = data.getExtras().getInt("selectMemeber");
                    RESULT = RESULT_OK;
                    if (isZh) {
                        tvRelationship.setText(data_Zh.get(selectMemeber));
                    } else {
                        tvRelationship.setText(data_Us.get(selectMemeber));
                    }
                    updateRelationship();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                titleLeftEvent();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
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

    public void updateRelationship() {
        RequestInfo requestInfo = new RequestInfo();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("member_id", memberId);
//        jsonParams.put("user_relationship_name", tvRelationship.getText().toString());
//        jsonParams.put("user_relationship_name", relationships[selectMemeber]);
        jsonParams.put("user_relationship_name", data_Us.get(selectMemeber));
        jsonParams.put("fam_nickname", fam_nickname);
        jsonParams.put("member_status", member_status);
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        requestInfo.url = String.format(Constant.API_UPDATE_RELATIONSHIP_NICKNAME, MainActivity.getUser().getUser_id());
        requestInfo.jsonParam = jsonParamsString;

        new HttpTools(PathRelationshipActivity.this).put(requestInfo, Tag, new HttpCallback() {
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

                    if (("200").equals(jsonObject.getString("response_status_code"))) {
                        Toast.makeText(PathRelationshipActivity.this, getString(R.string.successfully_update_relationship), Toast.LENGTH_SHORT).show();//成功
                        requestData();
                    } else {
                        Toast.makeText(PathRelationshipActivity.this, getString(R.string.fail_update_relationship), Toast.LENGTH_SHORT).show();//失败
                    }
                } catch (JSONException e) {
                    Toast.makeText(PathRelationshipActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PathRelationshipActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
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
