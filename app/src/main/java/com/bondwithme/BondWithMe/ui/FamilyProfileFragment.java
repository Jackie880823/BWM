package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.ui.wall.WallFragment;
import com.bondwithme.BondWithMe.util.MslToast;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bondwithme.BondWithMe.ui.FamilyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.bondwithme.BondWithMe.ui.FamilyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamilyProfileFragment extends BaseFragment<FamilyProfileActivity> {
    private static final String Tag = FamilyProfileFragment.class.getSimpleName();
    private String useId;//本人Id，这个将来是全局变量
    private String memberId;//本人的memberId
    private String groupId;
    private String groupName;
//    private String relationship;
//    private String famNickname;
//    private String memberStatus;
    private String getDofeelCode;
    private View vProgress;

    private CircularNetworkImage cniMain;
    private ImageView ivBottomLeft;
    private ImageView ivBottomRight;
    private TextView tvName1;
    private TextView tvId1;
    private ImageButton ibMiss;
    private RelativeLayout llViewProfile;

    private RelativeLayout rlPathRelationship;
    private RelativeLayout rlAlbumGallery;
    private RelativeLayout rlWallPosting;

    private Button btnSendMessage;
    private UserEntity userEntity;

    private static final int GET_USER_ENTITY = 0X11;

    List<String> data_Us = new ArrayList<>();

    public static FamilyProfileFragment newInstance(String... params) {
        return createInstance(new FamilyProfileFragment());
    }

    public FamilyProfileFragment() {
        super();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.family_profile_fragment;
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 1:
//             if (resultCode == getActivity().RESULT_OK) {
//                userEntity.setTree_type_name(data.getStringExtra("relationship"));
//             }
//        }
//    }
    List<String> data_Zh = new ArrayList<>();
    private List<String> getDataZh() {
        Configuration configuration = new Configuration();
        //设置应用为简体中文
        configuration.locale = Locale.SIMPLIFIED_CHINESE;
        getResources().updateConfiguration(configuration, null);
        String[] ralationArrayZh = getResources().getStringArray(R.array.relationship_item);
        data_Zh = Arrays.asList(ralationArrayZh);
        return data_Zh;
    }

    @Override
    public void initView() {
        getDataEn();
        if (Locale.getDefault().toString().equals("zh_CN")) {
//            data_Us = Arrays.asList(relationships);
            getDataZh();

        }
        Intent intent = getActivity().getIntent();
        useId = MainActivity.getUser().getUser_id();//MainActivity.
        memberId = intent.getStringExtra("member_id");
        groupId = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");
        vProgress = getViewById(R.id.rl_progress);
        if(TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupName)){
            //如果上个页面没传进groupId或者groupName，显示进度条
            vProgress.setVisibility(View.VISIBLE);
        }
//        relationship = intent.getStringExtra("relationship");
//        famNickname = intent.getStringExtra("fam_nickname");
//        memberStatus = intent.getStringExtra("member_status");
        getDofeelCode = intent.getStringExtra("getDofeel_code");

        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        ivBottomRight = getViewById(R.id.civ_right);
        tvName1 = getViewById(R.id.tv_name);
        tvId1 = getViewById(R.id.tv_id1);
        ibMiss = getViewById(R.id.iv_miss);
        llViewProfile = getViewById(R.id.ll_view_profile);
        rlPathRelationship = getViewById(R.id.rl_path_relationship);
        rlAlbumGallery = getViewById(R.id.rl_album_gallery);
        rlWallPosting = getViewById(R.id.rl_wall_posting);
        btnSendMessage = getViewById(R.id.btn_message);

        tvName1.setText(groupName);
        getParentActivity().tvTitle.setText(groupName);
        VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, memberId), R.drawable.network_image_default, R.drawable.network_image_default);
        if (!TextUtils.isEmpty(getDofeelCode)) {
            try {
                String filePath = "";
                if (getDofeelCode.contains("_")) {
                    filePath = getDofeelCode.replaceAll("_", File.separator);
                }
                InputStream is = App.getContextInstance().getAssets().open(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivBottomLeft.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);
                ArrayList<PhotoEntity> datas = new ArrayList<>();
                PhotoEntity peData = new PhotoEntity();
                peData.setUser_id(memberId);
                peData.setFile_id("profile");
                peData.setPhoto_caption(Constant.Module_profile);
                peData.setPhoto_multipe("false");
                datas.add(peData);
                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);
                startActivity(intent);
            }
        });

        llViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity != null) {
                    //用户详情界面
                    Intent intent1 = new Intent(getActivity(), FamilyViewProfileActivity.class);
                    intent1.putExtra("userEntity", userEntity);
                    startActivity(intent1);
                }
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //聊天界面
                Intent intent2 = new Intent(getActivity(), MessageChatActivity.class);
                intent2.putExtra("type", 0);
                if((TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupName)) && userEntity != null){
                    //如果上个页面没有groupId或者groupName
                    intent2.putExtra("groupId", userEntity.getGroup_id());
                    intent2.putExtra("titleName", userEntity.getUser_given_name());
                    startActivity(intent2);
                }else {
                    intent2.putExtra("groupId", groupId);
                    intent2.putExtra("titleName", groupName);
                    startActivity(intent2);
                }

            }
        });

        ibMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity != null) {
                    getHasMiss();
                }
            }
        });

        rlPathRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity != null ) {
                    //关系界面
                    Intent intent = new Intent(getActivity(), PathRelationshipActivity.class);
                    intent.putExtra("member_id", memberId);
//                    if(!TextUtils.isEmpty(relationship)){
//                        intent.putExtra("relationship", relationship);
//                        intent.putExtra("fam_nickname", famNickname);
//                        intent.putExtra("member_status", memberStatus);
////                        intent.putExtra("selectMemeber", data_Us.indexOf(userEntity.getTree_type_name()));
//                        intent.putExtra("selectMemeber", "");
//                    }
                    intent.putExtra("relationship", userEntity.getTree_type_name());
                    intent.putExtra("fam_nickname", userEntity.getFam_nickname());
                    intent.putExtra("member_status", userEntity.getUser_status());
                    //传index进下个界面
                    intent.putExtra("selectMemeber", data_Us.indexOf(userEntity.getTree_type_name()));
//                    startActivityForResult(intent, 0);
                    startActivityForResult(intent, 1);
                }
            }
        });

        rlAlbumGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相册界面
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra("member_id", memberId);
                startActivity(intent);
            }
        });

        rlWallPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //wall fragment
                FragmentManager fm = getParentActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = WallFragment.newInstance(memberId);
                ft.replace(R.id.container, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_USER_ENTITY:
                    userEntity = (UserEntity) msg.obj;
                    if(TextUtils.isEmpty(groupId)){
                        VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                    }
                    if(TextUtils.isEmpty(groupName)){
                        tvName1.setText(userEntity.getUser_given_name());
                        getParentActivity().tvTitle.setText(userEntity.getUser_given_name());
                    }
                    tvId1.setText(getResources().getString(R.string.app_name) + " ID: "+ userEntity.getDis_bondwithme_id());
                    if(TextUtils.isEmpty(getDofeelCode)){
                        String dofeel_code = userEntity.getDofeel_code();
                        if (!TextUtils.isEmpty(dofeel_code)) {
                            try {
                                String filePath = "";
                                if (dofeel_code.contains("_")) {
                                    filePath = dofeel_code.replaceAll("_", File.separator);
                                }
                                InputStream is = App.getContextInstance().getAssets().open(filePath);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                ivBottomLeft.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }
        }
    };


    private void getHasMiss() {
        HashMap<String, String> params = new HashMap<>();
        params.put("from_user_id", MainActivity.getUser().getUser_id());
        params.put("to_user_id", memberId);
        params.put("to_user_fullname", userEntity.getUser_given_name());
        new HttpTools(getActivity()).post(Constant.API_MISS_MEMBER, params,Tag, new HttpCallback() {
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
//                    String missMessage = jsonObject.getString("message");
//                    if (missMessage.startsWith("You already sent miss")) {
//
//                    } else if (missMessage.startsWith("You successfully sent miss")) {
//
//                    }
                    MslToast.getInstance(getActivity()).showShortToast(jsonObject.getString("message"));
                } catch (JSONException e) {
                    MslToast.getInstance(getActivity()).showShortToast(getResources().getString(R.string.text_error));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MslToast.getInstance(getActivity()).showShortToast(getResources().getString(R.string.text_error));
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
    public void requestData() {
        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put("user_id", useId);
        jsonParams.put("member_id", memberId);
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_MEMBER_PROFILE_DETAIL, params);

        new HttpTools(getActivity()).get(url, params, Tag, new HttpCallback() {
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
                List<UserEntity> data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                if ((data != null) && (data.size() > 0)) {
                    userEntity = data.get(0);
                    Message.obtain(handler, GET_USER_ENTITY, userEntity).sendToTarget();
                    if(vProgress.getVisibility() == View.VISIBLE){
                        vProgress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    getActivity().setResult(Activity.RESULT_OK);
                    userEntity.setTree_type_name(data.getStringExtra("relationship"));
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

}
