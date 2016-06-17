package com.madxstudio.co8.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.ui.company.CompanyActivity;
import com.madxstudio.co8.util.DensityUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.NoScrollListView;
import com.material.widget.PaperButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by liangzemian on 15/12/8.
 */
public class FamilyViewProfileFragment extends BaseFragment<FamilyViewProfileActivity> {
    private final static String TAG = "FamilyViewProfileFragment";
    private String useId;//本人Id，这个将来是全局变量
    private String memberId;//本人的memberId
    private String bwmId;//本人的bwmId

    public static final int CHOOSE_RELATION_CODE = 1;

    private CircularNetworkImage cniMain;
    private NetworkImageView networkImageView;
    private ImageView ivBottomLeft;
    private ImageView ivBottomRight;
    private TextView tvName1;
    private TextView tvId1;

    private NoScrollListView tvPhone;
    private TextView tvFirstName;
    private TextView tvLastName;
    //    TextView tvAge;
    private TextView tvBirthday;
    private TextView tvYearBirthday;
    private TextView tvGender;
    private TextView tvEmail;
    private TextView tvRegion;
    private TextView tvOrganisationName;
    private View vProgress;

    private View rlFirstName;
    private View rlListName;
    private View rlBirthday;
    private View rlYearBirthday;
    private View rlGender;
    private View rlEmail;
    private View rlRegion;
    private View rlPhone;
    private View flMember;
    private View rl_position;
    private View rl_department;
    private View rl_organisation;
    private View rl_et_internal_phone;
    private TextView et_position;
    private TextView et_department;
    private NoScrollListView et_internal_phone;
    private PaperButton btAddMember;
    private PaperButton btMessage;
    private ImageView iv_org_pend;
    private int[] array;
    private int profileBackgroundId;
    private String stFemale;
    private String stMale;

    private static final int GET_USER_ENTITY = 0X11;
    private final static int ADD_MEMBER = 0X12;
    private boolean isFromMember = false;


    UserEntity userEntity = new UserEntity();

    public static FamilyViewProfileFragment newInstance() {
        return createInstance(new FamilyViewProfileFragment());
    }

    public FamilyViewProfileFragment() {
        super();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_family_view_profile;
    }

    @Override
    protected void setParentTitle() {
        if (userEntity != null) {
            getParentActivity().tvTitle.setText(userEntity.getUser_given_name() + " " + userEntity.getUser_surname());
        }
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case GET_USER_ENTITY:
                    userEntity = (UserEntity) message.obj;
                    if (userEntity != null) {
                        setData();
                    }
                    break;
            }

            return false;
        }
    });

    private void setData() {
        VolleyUtil.initNetworkImageView(getParentActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
        VolleyUtil.initNetworkImageView(getActivity(), networkImageView, String.format(Constant.API_GET_PIC_PROFILE, userEntity.getUser_id()), profileBackgroundId, profileBackgroundId);

        showMessageButton();
        rlFirstName.setVisibility(View.VISIBLE);
        rlListName.setVisibility(View.VISIBLE);
        setDatePrivacy(userEntity.getDob_date_flag(), rlBirthday);
        setDatePrivacy(userEntity.getDob_year_flag(), rlYearBirthday);
        setDatePrivacy(userEntity.getGender_flag(), rlGender);
        setDatePrivacy(userEntity.getEmail_flag(), rlEmail);
        setDatePrivacy(userEntity.getLocation_flag(), rlRegion);
        setDatePrivacy(userEntity.getMember_flag(), rlPhone);
        setDatePrivacy(userEntity.getInt_phone_ext_flag(), rl_et_internal_phone);
        tvOrganisationName.setText(userEntity.getOrganisation());
        et_position.setText(userEntity.getPosition());
        et_department.setText(userEntity.getDepartment());
        if (userEntity.getInt_phone_ext() != null && userEntity.getInt_phone_ext().size() > 0) {
            for (int i = 0; i < userEntity.getInt_phone_ext().size(); i++) {
                if (!TextUtils.isEmpty(userEntity.getInt_phone_ext().get(i))) {
                    userEntity.getInt_phone_ext().set(i, "+" + userEntity.getInt_phone_ext().get(i));
                }
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.array_list_item, R.id.tv_phone, userEntity.getInt_phone_ext());
            et_internal_phone.setAdapter(adapter);
        }
        tvName1.setText(userEntity.getUser_given_name());
        tvId1.setText(getResources().getString(R.string.co8_id) + userEntity.getDis_bondwithme_id());
        if (userEntity.getUser_phone_number() != null && userEntity.getUser_phone_number().size() > 0) {
            for (int i = 0; i < userEntity.getUser_phone_number().size(); i++) {
                userEntity.getUser_phone_number().set(i, "+" + userEntity.getUser_phone_number().get(i));
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.array_list_item, R.id.tv_phone, userEntity.getUser_phone_number());
            tvPhone.setAdapter(adapter);
        }
        tvFirstName.setText(userEntity.getUser_given_name());
        tvLastName.setText(userEntity.getUser_surname());
        String strDOB = userEntity.getUser_dob();
        setTvBirthday(strDOB);
        if ("F".equals(userEntity.getUser_gender())) {
            tvGender.setText(stFemale);
        } else if ("M".equals(userEntity.getUser_gender())) {
            tvGender.setText(stMale);
        }
        tvEmail.setText(userEntity.getUser_email());
        tvRegion.setText(userEntity.getUser_location_name());

        if ("0".equals(userEntity.getDemo()) && "0".equals(userEntity.getPending_org())) {
            iv_org_pend.setVisibility(View.INVISIBLE);
        } else if ("1".equals(userEntity.getDemo()) && "1".equals(userEntity.getPending_org())) {
            iv_org_pend.setImageResource(R.drawable.time);
            iv_org_pend.setVisibility(View.VISIBLE);
        } else {
            iv_org_pend.setImageResource(R.drawable.org_is_pend);
            iv_org_pend.setVisibility(View.VISIBLE);
        }

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

    @Override
    public void initView() {
        userEntity = (UserEntity) getParentActivity().getIntent().getExtras().getSerializable("userEntity");
        useId = MainActivity.getUser().getUser_id();//MainActivity.
        memberId = getParentActivity().getIntent().getStringExtra("member_id");
        bwmId = getParentActivity().getIntent().getStringExtra("bwm_id");

//        profileBackgroundId = getParentActivity().getIntent().getIntExtra("profile_image_id", 6);
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

        rlFirstName = getViewById(R.id.rl_first_name);
        rlListName = getViewById(R.id.rl_last_name);
        rlBirthday = getViewById(R.id.rl_birthday);
        rlYearBirthday = getViewById(R.id.rl_year_birthday);
        rlGender = getViewById(R.id.rl_gender);
        rlEmail = getViewById(R.id.rl_email);
        rlRegion = getViewById(R.id.rl_region);
        rlPhone = getViewById(R.id.rl_phone);
        tvOrganisationName = getViewById(R.id.tv_organisation_name);

        flMember = getViewById(R.id.fl_member);
        btAddMember = getViewById(R.id.btn_add_member);
        btMessage = getViewById(R.id.btn_message);
        rl_position = getViewById(R.id.rl_position);
        rl_department = getViewById(R.id.rl_department);
        rl_et_internal_phone = getViewById(R.id.rl_et_internal_phone);
        et_position = getViewById(R.id.et_position);
        et_department = getViewById(R.id.et_department);
        et_internal_phone = getViewById(R.id.et_internal_phone);
        rl_organisation = getViewById(R.id.rl_organisation);
        iv_org_pend = getViewById(R.id.iv_org_pend);

        stFemale = getResources().getString(R.string.text_female);
        stMale = getResources().getString(R.string.text_male);

        isFromMember = getParentActivity().getIntent().getBooleanExtra(Constant.LOOK_USER_PROFILE, false);

        rl_position.setVisibility(View.VISIBLE);
        rl_department.setVisibility(View.VISIBLE);
//        if (profileBackgroundId == 6) {
//            array = new int[]{R.drawable.profile_background_0, R.drawable.profile_background_1, R.drawable.profile_background_2,
//                    R.drawable.profile_background_3, R.drawable.profile_background_4, R.drawable.profile_background_5};
//            profileBackgroundId = randomImageId(array);
//        }
        profileBackgroundId = R.drawable.profile_background_0;

        btAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromMember) {
                    doAdd();
                } else {
                    Intent intent = new Intent(getParentActivity(), RelationshipActivity.class);
                    intent.putExtra("member_id", userEntity.getUser_id());
                    startActivityForResult(intent, CHOOSE_RELATION_CODE);
                }
            }
        });

        rl_organisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(userEntity.getDemo()) && "0".equals(userEntity.getPending_org())) {
                    Intent intent = new Intent(getActivity(), CompanyActivity.class);
                    intent.putExtra(Constant.LOGIN_USER, userEntity);
                    startActivity(intent);
                }
            }
        });

        btMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromMember) {
                    addRemove();
                } else {
                    Intent intent2 = new Intent(getActivity(), MessageChatActivity.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (userEntity != null) {
                        intent2.putExtra("type", 0);
                        //如果上个页面没有groupId或者groupName
                        intent2.putExtra("groupId", userEntity.getGroup_id());
                        intent2.putExtra("titleName", userEntity.getUser_given_name());
                        startActivity(intent2);
                    }
                }
            }
        });

        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity == null) return;
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

        if (isFromMember) {
            btAddMember.setVisibility(View.VISIBLE);
            btAddMember.setText(getString(R.string.text_dialog_accept));
            btMessage.setText(getString(R.string.text_item_reject));
            btMessage.setVisibility(View.VISIBLE);
        }

        if (userEntity != null) {
            setData();
        } else {
            networkImageView.setDefaultImageResId(profileBackgroundId);
        }
    }

    private void addRemove() {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_REJECT_PENDING_MEMBER, MainActivity.getUser().getUser_id());
        Map<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(memberId) && userEntity != null) {
            memberId = userEntity.getUser_id();
        }
        params.put("requestor_id", memberId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(getActivity()).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {//{"response_status_code":"200","response_status":"Success","message":"Successfully reject member."}
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String response_status = jsonObject.optString("response_status");
                    if ("Success".equals(response_status)) {
                        getActivity().setResult(getActivity().RESULT_OK);
                        getActivity().finish();
                    } else {
                        MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void doAdd() {
        vProgress.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), AddMemberWorkFlow.class);
        intent.putExtra(AddMemberWorkFlow.FLAG_FROM, MainActivity.getUser().getUser_id());
        if (TextUtils.isEmpty(memberId) && userEntity != null) {
            memberId = userEntity.getUser_id();
        }
        intent.putExtra(AddMemberWorkFlow.FLAG_TO, memberId);
        startActivityForResult(intent, ADD_MEMBER);
    }

    private void setTvBirthday(String strDOB) {
        if (strDOB != null && !strDOB.equals("0000-00-00")) {
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //不同语言设置不同日期显示
//            if (Locale.getDefault().toString().equals("zh_CN")) {
//                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
//                String time = dateFormat.format(date).toString();
//                LogUtil.i("Profile_time", time);
//                String birthday = time.substring(5, time.length());
//                String YearBirthday = time.substring(0, 5);
//                tvBirthday.setText(birthday);
//                tvYearBirthday.setText(YearBirthday);
//            } else {
            int year = date.getYear() + 1900;
            tvBirthday.setText(date.getDate() + " " + MyDateUtils.getMonthNameArray(false)[date.getMonth()]);
            tvYearBirthday.setText(String.valueOf(year));
//            }
        }
    }

    private void setDatePrivacy(String dateFlag, View view) {
        switch (dateFlag) {
            case "0":
                view.setVisibility(View.GONE);
                break;
            case "1":
                view.setVisibility(View.VISIBLE);
                break;
            default:
                view.setVisibility(View.GONE);
                break;
        }
    }


    private void showMessageButton() {
        if ((bwmId != null || memberId != null) && !useId.equals(userEntity.getUser_id())) {
            flMember.setVisibility(View.VISIBLE);
            if ("0".equals(userEntity.getMember_flag())) {
                //如果不是好友
                btAddMember.setVisibility(View.VISIBLE);
            } else if ("1".equals(userEntity.getMember_flag())) {
                //如果是好友
//                            btMessage.setVisibility(View.VISIBLE);
            }
            LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            layoutParam.setMargins(0, 0, 0, DensityUtil.dip2px(getParentActivity(), 50));
            rlPhone.setLayoutParams(layoutParam);
        }
        if (isFromMember) {
            btAddMember.setVisibility(View.VISIBLE);
            btMessage.setVisibility(View.VISIBLE);
        }
    }

    private void addUser(final String relationShip) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("user_relationship_name", relationShip);
        params.put("member_id", userEntity.getUser_id());

        new HttpTools(getParentActivity()).post(Constant.API_ADD_MEMBER, params, this, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
                getParentActivity().finish();
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        getParentActivity().setResult(getActivity().RESULT_OK);
                        MessageUtil.getInstance().showShortToast(R.string.text_success_add_member);
                        // finish();
                    } else {
                        MessageUtil.getInstance().showShortToast(R.string.text_fail_add_member);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_RELATION_CODE:
                    userEntity.setTree_type_name(data.getStringExtra("relationship"));
                    addUser(userEntity.getTree_type_name());
                case ADD_MEMBER:
                    if (resultCode == getActivity().RESULT_OK) {
                        getActivity().setResult(getActivity().RESULT_OK);
                        getActivity().finish();
                    } else {
                        MessageUtil.getInstance().showShortToast(R.string.msg_action_canceled);
                    }
                    vProgress.setVisibility(View.GONE);
                default:
                    break;
            }

        }
    }

    @Override
    public void requestData() {
        if (userEntity == null) {
            HashMap<String, String> jsonParams = new HashMap<>();
            jsonParams.put("user_id", useId);
            if (memberId != null) {
                jsonParams.put("member_id", memberId);
            } else {
                jsonParams.put("bwm_id", bwmId);
            }

            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<>();
            params.put("condition", jsonParamsString);
//            vProgress.setVisibility(View.VISIBLE);
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
                    MessageUtil.getInstance().showShortToast(getResources().getString(R.string.text_error));
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

    //获取一个背景图的随机id
    public int randomImageId(int[] array) {
        int result = new Random().nextInt(5);
        return array[result];
    }
}
