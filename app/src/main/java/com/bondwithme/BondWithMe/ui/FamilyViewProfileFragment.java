package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by liangzemian on 15/12/8.
 */
public class FamilyViewProfileFragment extends BaseFragment<FamilyViewProfileActivity>{
    private final static String TAG = "FamilyViewProfileFragment";
    private String useId;//本人Id，这个将来是全局变量
    private String memberId;//本人的memberId
    private String bwmId;//本人的memberId
    private String memberFlag;

    CircularNetworkImage cniMain;
    NetworkImageView networkImageView;
    ImageView ivBottomLeft;
    ImageView ivBottomRight;
    TextView tvName1;
    TextView tvId1;

    TextView tvPhone;
    TextView tvFirstName;
    TextView tvLastName;
    //    TextView tvAge;
    TextView tvBirthday;
    TextView tvYearBirthday;
    TextView tvGender;
    TextView tvEmail;
    TextView tvRegion;
    private View vProgress;

    private View rlBirthday;
    private View rlYearBirthday;
    private View rlGender;
    private View rlEmail;
    private View rlRegion;
    private View rlPhone;

    private static final int GET_USER_ENTITY = 0X11;


    UserEntity userEntity = new UserEntity();

    public static FamilyViewProfileFragment newInstance(){
        return createInstance(new FamilyViewProfileFragment());
    }

    public FamilyViewProfileFragment(){
        super();
    }
    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_family_view_profile;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_USER_ENTITY:
                    userEntity = (UserEntity) msg.obj;
                    if(userEntity == null)return;

                    VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
                    VolleyUtil.initNetworkImageView(getActivity(), networkImageView, String.format(Constant.API_GET_PIC_PROFILE,  userEntity.getUser_id()), 0, 0);

                    setDatePrivacy(userEntity.getDob_date_flag(),rlBirthday);
                    setDatePrivacy(userEntity.getDob_year_flag(),rlYearBirthday);
                    setDatePrivacy(userEntity.getGender_flag(),rlGender);
                    setDatePrivacy(userEntity.getEmail_flag(),rlEmail);
                    setDatePrivacy(userEntity.getLocation_flag(),rlRegion);
                    setDatePrivacy(userEntity.getMember_flag(),rlPhone);

                    tvFirstName.setText(userEntity.getUser_given_name());
                    tvLastName.setText(userEntity.getUser_surname());
                    String strDOB = userEntity.getUser_dob();
                    LogUtil.d("TAG", "strDOB===" + strDOB);
                    setTvBirthday(strDOB);
                    if ("F".equals(userEntity.getUser_gender())) {
                        /**
                         * begin QK
                         */
                        tvGender.setText(getResources().getString(R.string.text_female));
                    } else if ("M".equals(userEntity.getUser_gender())) {
                        tvGender.setText(getResources().getString(R.string.text_male));
                        /**
                         * end
                         */
                    }
                    tvEmail.setText(userEntity.getUser_email());
                    tvRegion.setText(userEntity.getUser_location_name());
                    String dofeel_code = userEntity.getDofeel_code();
                    if (!TextUtils.isEmpty(dofeel_code)) {
                        try {
                            String filePath = "";
                            if (dofeel_code.indexOf("_") != -1) {
                                filePath = dofeel_code.replaceAll("_", File.separator);
                            }
                            InputStream is = getParentActivity().getAssets().open(filePath);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            ivBottomLeft.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public void initView() {
        userEntity = (UserEntity) getParentActivity().getIntent().getExtras().getSerializable("userEntity");
        useId = MainActivity.getUser().getUser_id();//MainActivity.
//        memberId = getParentActivity().getIntent().getStringExtra("member_id");
        bwmId = getParentActivity().getIntent().getStringExtra("bwm_id");
        cniMain = getViewById(R.id.cni_main);
        networkImageView = getViewById(R.id.iv_profile_images);
        ivBottomLeft = getViewById(R.id.civ_left);
        ivBottomRight = getViewById(R.id.civ_right);
        tvName1 = getViewById(R.id.tv_name1);
        tvId1 = getViewById(R.id.tv_id1);

        tvPhone = getViewById(R.id.tv_phone);
        tvFirstName = getViewById(R.id.tv_first_name);
        tvLastName = getViewById(R.id.tv_last_name);
//        tvAge = getViewById(R.id.tv_age);
        tvBirthday = getViewById(R.id.tv_birthday);
        tvYearBirthday = getViewById(R.id.tv_year_birthday);
        tvGender = getViewById(R.id.tv_gender);
        tvEmail = getViewById(R.id.tv_email);
        tvRegion = getViewById(R.id.tv_region);
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.GONE);

        rlBirthday = getViewById(R.id.rl_birthday);
        rlYearBirthday = getViewById(R.id.rl_year_birthday);
        rlGender = getViewById(R.id.rl_gender);
        rlEmail = getViewById(R.id.rl_email);
        rlRegion = getViewById(R.id.rl_region);
        rlPhone = getViewById(R.id.rl_phone);

        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userEntity == null)return;

                Intent intent = new Intent(getParentActivity(), ViewOriginalPicesActivity.class);

                ArrayList<PhotoEntity> datas = new ArrayList();
                PhotoEntity peData = new PhotoEntity();
                peData.setUser_id(userEntity.getUser_id());
                peData.setFile_id("profile");
                peData.setPhoto_caption(Constant.Module_profile);
                peData.setPhoto_multipe("false");
                datas.add(peData);

                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);

                startActivity(intent);
            }
        });

        if(userEntity != null){
            VolleyUtil.initNetworkImageView(getParentActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            VolleyUtil.initNetworkImageView(getActivity(), networkImageView, String.format(Constant.API_GET_PIC_PROFILE,  userEntity.getUser_id()), 0, 0);
            memberFlag = userEntity.getMember_flag();

            setDatePrivacy(userEntity.getDob_date_flag(),rlBirthday);
            setDatePrivacy(userEntity.getDob_year_flag(),rlYearBirthday);
            setDatePrivacy(userEntity.getGender_flag(),rlGender);
            setDatePrivacy(userEntity.getEmail_flag(),rlEmail);
            setDatePrivacy(userEntity.getLocation_flag(),rlRegion);
            setDatePrivacy(userEntity.getMember_flag(),rlPhone);

            tvName1.setText(userEntity.getUser_given_name());
            tvId1.setText(getResources().getString(R.string.app_name) + " ID: "+ userEntity.getDis_bondwithme_id());
            if (userEntity.getUser_phone().length() > 0){
                tvPhone.setText("+" + userEntity.getUser_country_code() + " " + userEntity.getUser_phone());
            }
            tvFirstName.setText(userEntity.getUser_given_name());
            tvLastName.setText(userEntity.getUser_surname());
//        tvAge.setText(userEntity.getUser_dob());
            String strDOB = userEntity.getUser_dob();
            LogUtil.d("TAG", "strDOB===" + strDOB);
            setTvBirthday(strDOB);

            if ("F".equals(userEntity.getUser_gender())) {
                /**
                 * begin QK
                 */
                tvGender.setText(getResources().getString(R.string.text_female));
            } else if ("M".equals(userEntity.getUser_gender())) {
                tvGender.setText(getResources().getString(R.string.text_male));
                /**
                 * end
                 */
            }

            tvEmail.setText(userEntity.getUser_email());
            tvRegion.setText(userEntity.getUser_location_name());
            String dofeel_code = userEntity.getDofeel_code();
            if (!TextUtils.isEmpty(dofeel_code)) {
                try {
                    String filePath = "";
                    if (dofeel_code.indexOf("_") != -1) {
                        filePath = dofeel_code.replaceAll("_", File.separator);
                    }
                    InputStream is = getParentActivity().getAssets().open(filePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ivBottomLeft.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


//        tvBirthday.setText(strDOB);



    }
    private void setTvBirthday(String strDOB) {
        if (strDOB != null && !strDOB.equals("0000-00-00")){
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //不同语言设置不同日期显示
            if (Locale.getDefault().toString().equals("zh_CN")){
//                tvBirthday.setText(date.getMonth() + "月" + date.getDay() + "日");
//                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
//                tvBirthday.setText(dateFormat.format(date));
                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                String time = dateFormat.format(date).toString();
                LogUtil.i("Profile_time",time);
                String birthday = time.substring(5,time.length());
                String YearBirthday = time.substring(0,5);
                tvBirthday.setText(birthday);
                tvYearBirthday.setText(YearBirthday);
            }else {
//                tvBirthday.setText(this.getResources().getStringArray(R.array.months)[date.getMonth()] + " " + date.getDate());
                int year = date.getYear() + 1900;
                tvBirthday.setText(date.getDate() + " " + this.getResources().getStringArray(R.array.months)[date.getMonth()]);
                tvYearBirthday.setText(String.valueOf(year));
            }
        }
    }

    private void setDatePrivacy(String dateFlag,View view){
        if(memberFlag.equals("-0")){
            //不是好友
            switch (dateFlag){
                case "0":
                    view.setVisibility(View.GONE);
                    break;
                case "1":
                    view.setVisibility(View.GONE);
                    break;
                case "2":
                    view.setVisibility(View.VISIBLE);
                    break;
            }
        }else {
            //是好友
            switch (dateFlag){
                case "0":
                    view.setVisibility(View.GONE);
                    break;
                case "1":
                    view.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    view.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
    @Override
    public void requestData() {
        if(userEntity == null){
            HashMap<String, String> jsonParams = new HashMap<>();
            jsonParams.put("user_id", useId);
            if(memberId != null){
                jsonParams.put("member_id", memberId);
            }else {
                jsonParams.put("bwm_id", bwmId);
            }

            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<>();
            params.put("condition", jsonParamsString);
            vProgress.setVisibility(View.VISIBLE);
            String url = UrlUtil.generateUrl(Constant.API_MEMBER_PROFILE_DETAIL, params);

            new HttpTools(getActivity()).get(url, params, TAG, new HttpCallback() {
                @Override
                public void onStart() {
                    vProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    vProgress.setVisibility(View.GONE);
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
                        if (vProgress.getVisibility() == View.VISIBLE) {
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

    }
}
