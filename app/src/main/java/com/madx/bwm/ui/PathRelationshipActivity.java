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

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
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
    List<String> data_En = new ArrayList<String>();
    private int selectMemeber;
    String[] relationships =
            {"Mother","Father","Wife","Husband","Sister","Brother","Daughter","Son","Grandma","Grandpa",
            "Granddaughter","Grandson","Granduncle","Grandaunt","Grandniece","Grandnephew","Great Grandma","Great Grandpa","Great Grandniece","Great Grandnephew",
                    "Uncle","First Cousin","Second Cousin","Cousin","Niece","Nephew","Mother-in-law","Father-in-law","Aunty-in-law","Uncle-in-law",
                    "Sister-in-law","Brother-in-law","Daughter-in-law","Son-in-law","Cousin-in-law","Niece-in-Law","Nephew-in-Law","Fiancee","Fiance","Ex-wife",
                    "Ex-husband","Godmother","Godfather","Godsister","Godbrother","Goddaughter","Godson","Stepmother","NieStepfatherce","Stepsister",
                    "Stepbrother","Stepdaughter","Stepson","Other Relative","Partners","Friend"};

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
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        if(Locale.getDefault().toString().equals("zh_CN")){
            isZh = true;
            data_En = Arrays.asList(relationships);
            getData();
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
//        if(isZh){
//             int postion = data_En.indexOf(relationship);
//            tvRelationship.setText(data_Zh.get(postion));
//        }else {
//            tvRelationship.setText(relationship);
//        }

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

        new HttpTools(PathRelationshipActivity.this).get(url, null,Tag, new HttpCallback() {
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

                Log.d("", "!!!!!!!!" + response);
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
                    if(isZh){
                        int positon = data_En.indexOf(pathList.get(i).getRelationship());
                        tvRelationships[i].setText(data_Zh.get(positon));
                        tvRelationship.setText(tvRelationships[0].getText());
                    } else {
                        tvRelationships[i].setText(pathList.get(i).getRelationship());
                        tvRelationship.setText(tvRelationships[0].getText());
                    }
                    tvNames[i].setText(pathList.get(i).getMember_fullname());
                }

                for (int j = pathList.size(); j < 4; j++)//其他空位
                {
                    ll[j].setVisibility(View.INVISIBLE);
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
                    //获取选择关系界面传来的关系
                    tvRelationship.setText(data.getStringExtra("relationship"));
                    selectMemeber = data.getExtras().getInt("selectMemeber");
//                    setResult(RESULT_OK);
                    updateRelationship();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                titleLeftEvent();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }

    }


    private List<String> getData() {
        String [] ralationArray=getResources().getStringArray(R.array.relationship_item);
        for (int i=0;i<ralationArray.length;i++){
            data_Zh.add(ralationArray[i]);
        }
        return data_Zh;
    }
    public void updateRelationship() {
        RequestInfo requestInfo = new RequestInfo();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("member_id", memberId);
//        jsonParams.put("user_relationship_name", tvRelationship.getText().toString());
        jsonParams.put("user_relationship_name", relationships[selectMemeber]);
        jsonParams.put("fam_nickname", fam_nickname);
        jsonParams.put("member_status", member_status);
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        requestInfo.url = String.format(Constant.API_UPDATE_RELATIONSHIP_NICKNAME, MainActivity.getUser().getUser_id());
        requestInfo.jsonParam = jsonParamsString;

        new HttpTools(PathRelationshipActivity.this).put(requestInfo,Tag, new HttpCallback() {
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

//    private String selectRelationship(String relationship){
//        switch (relationship){
//            case "妈妈":
//                break;
//            case "爸爸":
//                break;
//            case "老婆":
//                break;
//            case "老公":
//                break;
//            case "姐妹":
//                break;
//            case "兄弟":
//                break;
//            case "女儿":
//                break;
//            case "儿子":
//                break;
//            case "婆婆":
//                break;
//            case "公公":
//                break;
//            case "孙女":
//                break;
//            case "孙子":
//                break;
//            case "叔祖":
//                break;
//            case "叔祖母":
//                break;
//            case "侄孙女":
//                break;
//            case "侄孙子":
//                break;
//            case "太公":
//                break;
//            case "太婆":
//                break;
//
//            case "太侄孙女":
//                break;
//            case "太侄孙子":
//                break;
//            case "叔叔":
//                break;
//            case "第一代堂表兄妹":
//                break;
//            case "第二代堂表兄妹":
//                break;
//            case "堂表兄弟姊妹":
//                break;
//            case "外甥女":
//                break;
//            case "外甥":
//                break;
//            case "岳母":
//                break;
//            case "岳父":
//                break;
//            case "阿姨":
//                break;
//            case "弟媳":
//                break;
//            case "姐夫，妹夫":
//                break;
//            case "儿媳妇":
//                break;
//            case "女婿":
//                break;
//            case "姻堂表兄妹":
//                break;
//            case "姻外甥女":
//                break;
//            case "姻外甥":
//                break;
//
//
//            case "未婚妻":
//                break;
//            case "未婚夫":
//                break;
//            case "前妻":
//                break;
//            case "前夫":
//                break;
//            case "干妈":
//                break;
//            case "干爹":
//                break;
//            case "干姐妹":
//                break;
//            case "干哥弟":
//                break;
//            case "干女儿":
//                break;
//            case "干儿子":
//                break;
//            case "后母":
//                break;
//            case "后父":
//                break;
//            case "后姐妹":
//                break;
//            case "后哥弟":
//                break;
//            case "后女儿":
//                break;
//            case "后儿子":
//                break;
//            case "其他亲戚":
//                break;
//            case "伴侣":
//                break;
//            case "朋友":
//                break;
//        }
//        return null;
//    }
}
