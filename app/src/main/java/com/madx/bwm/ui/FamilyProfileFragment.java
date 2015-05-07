package com.madx.bwm.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.PhotoEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.ui.wall.WallFragment;
import com.madx.bwm.widget.CircularNetworkImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.FamilyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.FamilyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamilyProfileFragment extends BaseFragment<FamilyProfileActivity> {

    String useId;//本人Id，这个将来是全局变量
    String memberId;//本人的memberId

    CircularNetworkImage cniMain;
    ImageView ivBottomLeft;
    ImageView ivBottomRight;
    TextView tvName1;
    TextView tvId1;
    ImageButton ibMiss;

    LinearLayout llViewProfile;

    RelativeLayout rlPathRelationship;
    RelativeLayout rlAlbumGallery;
    RelativeLayout rlWallPosting;

    Button btnSendMessage;

    public List<UserEntity> data = new ArrayList<>();
    UserEntity userEntity = new UserEntity();

    public static FamilyProfileFragment newInstance(String... params) {
        return createInstance(new FamilyProfileFragment());
    }

    public FamilyProfileFragment() {
        super();
        // Required empty public constructor
    }

//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event, container, false);
//    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.family_profile_fragment;
    }

    @Override
    public void initView() {
        Intent intent = getActivity().getIntent();
        useId = MainActivity.getUser().getUser_id();//MainActivity.
        memberId = intent.getStringExtra("member_id");

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

        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);

                ArrayList<PhotoEntity> datas = new ArrayList();
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
                if (data != null && data.size()>0) {
                    Intent intent1 = new Intent(getActivity(), FamilyViewProfileActivity.class);
                    intent1.putExtra("userEntity", userEntity);
                    startActivity(intent1);
                }
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null && data.size()>0) {
                    Intent intent2 = new Intent(getActivity(), MessageChatActivity.class);
                    intent2.putExtra("type", 0);
                    intent2.putExtra("userEntity", userEntity);
                    startActivity(intent2);
                }
            }
        });

        ibMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((data != null) && (data.size() > 0)) {

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("from_user_id", MainActivity.getUser().getUser_id());
                    params.put("to_user_id", memberId);
                    params.put("to_user_fullname", userEntity.getUser_given_name());

                    new HttpTools(getActivity()).post(Constant.API_MISS_MEMBER, params, new HttpCallback() {
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

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
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





//                if ((data != null) && (data.size() > 0)) {
//                    StringRequest stringRequestPost = new StringRequest(Request.Method.POST, Constant.API_MISS_MEMBER, new Response.Listener<String>() {
//
//                        GsonBuilder gsonb = new GsonBuilder();
//
//                        Gson gson = gsonb.create();
//
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//
//                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                            Toast.makeText(getActivity(), getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            HashMap<String, String> params = new HashMap<String, String>();
//                            params.put("from_user_id", MainActivity.getUser().getUser_id());
//                            params.put("to_user_id", memberId);
//                            params.put("to_user_fullname", userEntity.getUser_given_name());
//                            return params;
//                        }
//                    };
//                    VolleyUtil.addRequest2Queue(getActivity(), stringRequestPost, "");
//                }
            }
        });

        rlPathRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null && data.size()>0) {
                    Intent intent = new Intent(getActivity(), PathRelationshipActivity.class);
                    intent.putExtra("member_id", memberId);
                    intent.putExtra("relationship", userEntity.getTree_type_name());
                    intent.putExtra("fam_nickname",userEntity.getFam_nickname());
                    intent.putExtra("member_status",userEntity.getUser_status());
                    startActivityForResult(intent, 0);
                }
            }
        });

        rlAlbumGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null && data.size()>0) {
                    Intent intent = new Intent(getActivity(), AlbumGalleryActivity.class);
                    intent.putExtra("member_id", memberId);
                    startActivity(intent);
                }
            }
        });

        rlWallPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data != null && data.size()>0) {
                    FragmentManager fm = getParentActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment f = WallFragment.newInstance(memberId);
                    Log.d("", "memberId============" + memberId);
                    ft.replace(R.id.container, f);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
    }

    @Override
    public void requestData() {
        data.clear();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", useId);
        jsonParams.put("member_id", memberId);
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_MEMBER_PROFILE_DETAIL, params);

        new HttpTools(getActivity()).get(url, params, new HttpCallback() {
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

                data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());

                if( (data != null) && (data.size()>0) )
                {
                    userEntity = data.get(0);

                    VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                    tvName1.setText(userEntity.getUser_given_name());
                    getParentActivity().tvTitle.setText(userEntity.getUser_given_name());
                    tvId1.setText("ID:" + userEntity.getDis_bondwithme_id());

                    if (!TextUtils.isEmpty(userEntity.getUser_emoticon()))
                    {
                        try {
                            InputStream is = App.getContextInstance().getAssets().open(userEntity.getUser_emoticon()+".png");
                            Bitmap bitmap= BitmapFactory.decodeStream(is);
                            ivBottomLeft.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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







//        StringRequest srMemberProfile = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response)
//            {
//
//                Log.d("", "@@@@@@@@@@@@!!!!" + response);
//
//                GsonBuilder gsonb = new GsonBuilder();
//
//                Gson gson = gsonb.create();
//
//                data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {}.getType());
//
//                if( (data != null) && (data.size()>0) )
//                {
//                    userEntity = data.get(0);
//
//                    VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//                    tvName1.setText(userEntity.getUser_given_name());
//                    getParentActivity().tvTitle.setText(userEntity.getUser_given_name());
//                    tvId1.setText("ID:" + userEntity.getDis_bondwithme_id());
//
//                    if (!TextUtils.isEmpty(userEntity.getUser_emoticon()))
//                    {
//                        try {
//                            InputStream is = App.getContextInstance().getAssets().open(userEntity.getUser_emoticon()+".png");
//                            Bitmap bitmap= BitmapFactory.decodeStream(is);
//                            ivBottomLeft.setImageBitmap(bitmap);
//
////                    Drawable da = Drawable.createFromStream(is, null);
////                    ivBottomLeft.setImageDrawable(da);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        VolleyUtil.addRequest2Queue(getActivity(), srMemberProfile, "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 0:
                if (resultCode == getActivity().RESULT_OK)
                {
                    requestData();
                }
        }
    }


}
