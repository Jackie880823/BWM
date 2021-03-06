package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.company.CompanyActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LocalImageLoader;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.DatePicker;
import com.madxstudio.co8.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MyViewProfileActivity extends BaseActivity {
    private CircularNetworkImage cniMain;
    private ImageView ivBottomLeft;
    private LinearLayout llResetPassword;
    private EditText etFirstName;
    private EditText etLastName;
    private TextView tvBirthday;
    private EditText tvYearBirthday;
    private TextView rlBirthday;
    private TextView tvGender;
    private TextView rlGender;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etRegion;
    private EditText et_position;
    private EditText et_department;
    private EditText et_internal_phone;
    private UserEntity userEntity;
    private TextView et_organisation;
    private View rl_organisation;

    private TextView tvChange;
    private NetworkImageView imProfileImages;
    private NetworkImageView imQrImages;
    private View vProgress;
    private RelativeLayout RlView;
    private MyDialog showSelectDialog;
    private MyDialog showCameraAlbum;
    private Boolean isUploadName = false;
    private Boolean isEit = false;
    private int headOrBackdropImage;
    private ImageView iv_org_pend;

    private Boolean isUploadNameSuccess = true;
    private Boolean isUploadImageSuccess = false;

    private String userGender;

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_PROFILE_PHOTO = 3;

    Uri mCropImagedUri;
    Uri mBackdropImagedUri;
    private String headImagePath;
    private String backgroundImagePath;
    private Context mContext;
    private String TAG;
    private int profileBackgroundId;

    String strDOB;
    private static final int CANCEL_JOIN_ORG_SUCCESS = 0X10;
    private static final int RESEND_JOIN_ORG_SUCCESS = 0X11;
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    @Override
    public int getLayout() {
        return R.layout.activity_my_view_profile;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.edit_profile);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.title_my_profile));
    }

    private boolean banBack;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RESEND_JOIN_ORG_SUCCESS:
                    showJoinDialog(getString(R.string.text_resend_successful));
                    break;
                case CANCEL_JOIN_ORG_SUCCESS:
                    showJoinDialog(getString(R.string.text_cancel_successful));
                    userEntity = App.getLoginedUser();
                    showOrgPic();
                    break;
            }
            return false;
        }
    });

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                banBack = backCheck();
            }
            return banBack ? banBack : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean backCheck() {
        if (isProfileChanged()) {
            showNoFriendDialog();
            return true;
        } else {
            super.titleLeftEvent();
            return false;
        }
    }

    @Override
    protected void titleRightEvent() {
        if (!isEit) {
            rightButton.setImageResource(R.drawable.ok_normal);
            tvChange.setVisibility(View.VISIBLE);
            ableEdit(true, RlView);
            isEit = true;
        } else {
            if (TextUtils.isEmpty(etFirstName.getText()) && TextUtils.isEmpty(etLastName.getText())) {
                MessageUtil.getInstance().showLongToast(getString(R.string.text_insert_first_last_name));
            } else if (TextUtils.isEmpty(etFirstName.getText())) {
                MessageUtil.getInstance().showLongToast(getString(R.string.text_insert_first_name));
            } else if (TextUtils.isEmpty(etLastName.getText())) {
                MessageUtil.getInstance().showLongToast(getString(R.string.text_insert_last_name));
            } else {
                update();
            }
        }
    }

    @Override
    protected void titleLeftEvent() {
        boolean isChanged = isProfileChanged();
        if (isChanged) {
            showNoFriendDialog();
        } else {
            super.titleLeftEvent();
        }
    }

    private void update() {

        if (mCropImagedUri != null) {
            uploadImage();

        }
        if (mBackdropImagedUri != null) {
            uploadBackdropImage();
        }

        //如果没有跟新图片
        if (mBackdropImagedUri == null && mCropImagedUri == null) {
            if (isProfileChanged()) {
                updateProfile();

            } else {
                finish();
            }
        }

    }

    private void ableEdit(boolean isEit, ViewGroup viewGroup) {
        if (viewGroup == null) return;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof EditText) {
                view.setFocusable(isEit);
                view.setFocusableInTouchMode(isEit);
                if (isEit && (view.getId() == R.id.et_phone || view.getId() == R.id.tv_year_birthday)) {
                    view.setFocusable(false);
                    view.setFocusableInTouchMode(false);
                }
            } else if (view instanceof ViewGroup) {
                this.ableEdit(isEit, (ViewGroup) view);
            }
        }

    }

    private void showJoinDialog(String showContent) {
        View selectIntention = LayoutInflater.from(mContext).inflate(R.layout.dialog_message_delete, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView copyText = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        copyText.setText(showContent);
        cancelTv.setText(R.string.ok);
        selectIntention.findViewById(R.id.tv_create_new_group).setVisibility(View.GONE);
        selectIntention.findViewById(R.id.message_copy_view).setVisibility(View.GONE);
        selectIntention.findViewById(R.id.message_cancel_linear).setVisibility(View.VISIBLE);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    private void showNoFriendDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_some_empty, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView tv_no_member = (TextView) selectIntention.findViewById(R.id.tv_no_member);
        tv_no_member.setText(getString(R.string.text_create_group_not_save));
        TextView okTv = (TextView) selectIntention.findViewById(R.id.tv_ok);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        cancelTv.setVisibility(View.VISIBLE);
        ((View) selectIntention.findViewById(R.id.line_view)).setVisibility(View.VISIBLE);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                finish();
            }
        });
        showSelectDialog.show();
    }

    private int isHeaderChanged() {
        if (mCropImagedUri != null) {
            return 1;
        } else if (mBackdropImagedUri != null) {
            return 2;
        }
        return 0;
    }

    private void getData() {
//        if (TextUtils.isEmpty(userEntity.getOrganisation())) {
//            return;
//        }
//        Map<String, String> params = new Hashtable<>();
//        params.put(Constant.USER_ID, userEntity.getUser_id());
//        new HttpTools(mContext).get(String.format(OrganisationConstants.API_GET_ORGANISATION_DETAILS, userEntity.getOrg_id()), params, TAG, new HttpCallback() {
//            @Override
//            public void onStart() {
//                vProgress.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFinish() {
//                vProgress.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onResult(String string) {
//                Gson gson = new Gson();
//                OrganisationDetail detail = gson.fromJson(string, OrganisationDetail.class);
//                if (detail != null) {
//                    Profile profile = detail.getProfile();
//                    if (profile != null) {
//                        userEntity.setOrganisation(profile.getName());
//                        App.changeLoginedUser(userEntity);
//                        et_organisation.setText(userEntity.getOrganisation());
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                LogUtil.e(TAG, "onError: ", e);
//                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
//            }
//
//            @Override
//            public void onCancelled() {
//                LogUtil.d(TAG, "onCancelled: ");
//            }
//
//            @Override
//            public void onLoading(long count, long current) {
//                LogUtil.d(TAG, "onLoading() called with: " + "count = [" + count + "], current = [" + current + "]");
//            }
//        });

        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put("user_id", userEntity.getUser_id());
        jsonParams.put("member_id", userEntity.getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
        HashMap<String, String> params = new HashMap<>();
        params.put("condition", jsonParamsString);
        String url = UrlUtil.generateUrl(Constant.API_MEMBER_PROFILE_DETAIL, params);
        new HttpTools(this).get(url, params, TAG, new HttpCallback() {
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
                    App.changeLoginedUser(userEntity);
                    showOrgPic();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }


    private boolean isProfileChanged() {
        if (TextUtils.isEmpty(etFirstName.getText()) && TextUtils.isEmpty(etLastName.getText())) {
//            MessageUtil.getInstance().showLongToast(getString(R.string.text_insert_first_last_name));
            return false;
        }
        if (!TextUtils.isEmpty(etFirstName.getText()) && !etFirstName.getText().toString().trim().equals(userEntity.getUser_given_name())) {
            return true;
        } else {
            if (TextUtils.isEmpty(etFirstName.getText())) {
//                MessageUtil.getInstance().showLongToast(getString(R.string.text_insert_first_name));
                return false;
            }
        }
        if (!TextUtils.isEmpty(etLastName.getText()) && !etLastName.getText().toString().trim().equals(userEntity.getUser_surname())) {
            return true;
        } else {
            if (TextUtils.isEmpty(etLastName.getText())) {
//                MessageUtil.getInstance().showLongToast(getString(R.string.text_insert_last_name));
                return false;
            }
        }
        if (!TextUtils.isEmpty(strDOB) && !strDOB.equals(userEntity.getUser_dob())) {
            return true;
        }
        //性别简写
        String simpleSex = userEntity.getUser_gender();
        String tempSex = tvGender.getText().toString().trim();
        String simpleRealitySex = null;
        //实际显示性别转换成简写
        if (tempSex.equals(getResources().getString(R.string.text_female))) {
            simpleRealitySex = "F";
        } else if (tempSex.equals(getResources().getString(R.string.text_male))) {
            simpleRealitySex = "M";
        }
        if (simpleRealitySex != null && !simpleRealitySex.equals(simpleSex)) {
            return true;
        }
        if (!TextUtils.isEmpty(etEmail.getText()) && !etEmail.getText().toString().trim().equals(userEntity.getUser_email())) {
            return true;
        }

        if (!TextUtils.isEmpty(etRegion.getText()) && !etRegion.getText().toString().trim().equals(userEntity.getUser_location_name())) {
            return true;
        }
        if (!TextUtils.isEmpty(et_department.getText()) && !et_department.getText().toString().trim().equals(userEntity.getDepartment())) {
            return true;
        }
        if (!TextUtils.isEmpty(et_position.getText()) && !et_position.getText().toString().trim().equals(userEntity.getPosition())) {
            return true;
        }
        if (!TextUtils.isEmpty(et_internal_phone.getText()) && !et_internal_phone.getText().toString().trim().equals(userEntity.getInt_phone_ext().get(0))) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        userEntity = MainActivity.getUser();
        getData();
        showOrgPic();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    private String headUrl;
    private String backdropHeadUrl;
    private String qrUrl;
    private BitmapTools mBitmapTools;
    private BitmapTools mBackdropBitmapTools;
    private BitmapTools mQrBitmapTools;

    @Override
    public void initView() {
//        progressDialog = new ProgressDialog(this, getResources().getString(R.string.text_dialog_loading));
        mContext = this;
        TAG = mContext.getClass().getSimpleName();
        profileBackgroundId = getIntent().getIntExtra("profile_image_id", 0);
        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        RlView = (RelativeLayout) findViewById(R.id.rl_view);
        llResetPassword = (LinearLayout) getViewById(R.id.ll_reset_password);

        etFirstName = getViewById(R.id.et_first_name);
        etLastName = getViewById(R.id.et_last_name);
        tvBirthday = getViewById(R.id.tv_birthday);
        tvYearBirthday = getViewById(R.id.tv_year_birthday);
        rlBirthday = getViewById(R.id.rl_birthday);
        tvGender = getViewById(R.id.tv_gender);
        rlGender = getViewById(R.id.rl_gender);
        etEmail = getViewById(R.id.et_email);
        etPhone = getViewById(R.id.et_phone);
        etRegion = getViewById(R.id.et_region);
        et_position = getViewById(R.id.et_position);
        et_department = getViewById(R.id.et_department);
        et_internal_phone = getViewById(R.id.et_internal_phone);
        tvChange = getViewById(R.id.tv_change_text);
        imProfileImages = getViewById(R.id.iv_profile_images);
        imQrImages = getViewById(R.id.iv_profile_qr);
        vProgress = getViewById(R.id.rl_progress);
        et_organisation = getViewById(R.id.et_organisation);
        rl_organisation = getViewById(R.id.rl_organisation);
        iv_org_pend = getViewById(R.id.iv_org_pend);

        userEntity = MainActivity.getUser();

        rl_organisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(userEntity.getDemo()) && "0".equals(userEntity.getPending_org())) {
                    Intent companyIntent = new Intent(mContext, CompanyActivity.class);
                    startActivity(companyIntent);
                } else if ("1".equals(userEntity.getDemo()) && "1".equals(userEntity.getPending_org())) {
                    showPendDialog();
                } else {
                    startActivity(new Intent(mContext, OrganisationActivity.class));
                }
            }
        });

        headUrl = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id());
        backdropHeadUrl = String.format(Constant.API_GET_PIC_PROFILE, userEntity.getUser_id());
        qrUrl = String.format(Constant.API_GET_PROFILE_QR, userEntity.getUser_id());

        mBitmapTools = BitmapTools.getInstance(this);
        mBackdropBitmapTools = BitmapTools.getInstance(this);
        mQrBitmapTools = BitmapTools.getInstance(this);
//        VolleyUtil.initNetworkImageView(this, imProfileImages, String.format(Constant.API_GET_PIC_PROFILE, userEntity.getUser_id()), 0, 0);
        mBitmapTools.display(cniMain, headUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        mBackdropBitmapTools.display(imProfileImages, backdropHeadUrl, profileBackgroundId, profileBackgroundId);
        mQrBitmapTools.display(imQrImages, qrUrl, R.drawable.qrcode_button, R.drawable.qrcode_button);
        etFirstName.setText(userEntity.getUser_given_name());
        etLastName.setText(userEntity.getUser_surname());
        tvTitle.setText(userEntity.getUser_given_name());

        if (Constant.TYPE_FACEBOOK.equalsIgnoreCase(userEntity.getUser_login_type())) {
            getViewById(R.id.edit_pwd_linear).setVisibility(View.GONE);
        }

//        tvAge.setText(userEntity.getUser_dob());//需要做处理，年转为岁数
        //1990-09-10   1990年
        strDOB = userEntity.getUser_dob();
        setTvBirthday(strDOB);

        if ("F".equals(userEntity.getUser_gender())) {
            tvGender.setText(getResources().getString(R.string.text_female));
        } else if ("M".equals(userEntity.getUser_gender())) {
            tvGender.setText(getResources().getString(R.string.text_male));
        } else {
            tvGender.setText(null);
        }

        etEmail.setText(userEntity.getUser_email());
        if (null != userEntity.getUser_phone() && userEntity.getUser_phone().length() > 0) {
            etPhone.setText("+" + userEntity.getUser_country_code() + " " + userEntity.getUser_phone());
        }
        String[] countryArray = getResources().getStringArray(R.array.country_code);
        String userCountryCode = userEntity.getUser_country_code();
        if (countryArray != null) {
            for (String string : countryArray) {
                if (string.indexOf(" ") != -1) {
                    String countryCode = string.substring(string.lastIndexOf(" ") + 1).trim();
                    String countryName = string.substring(0, string.lastIndexOf(" ")).trim();
                    if (null != countryCode && countryCode.equals(userCountryCode)) {
                        etRegion.setText(countryName);
                    }
                }
            }
        }
        etRegion.setText(userEntity.getUser_location_name());
        et_position.setText(userEntity.getPosition());
        et_department.setText(userEntity.getDepartment());
        List<String> phoneExtList = userEntity.getInt_phone_ext();
        if (phoneExtList != null && phoneExtList.size() > 0) {
            et_internal_phone.setText(phoneExtList.get(0));
        }

        rlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEit) return;

                showSelectDialog();
            }
        });

        llResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyViewProfileActivity.this, ProfileResetPasswordActivity.class));
            }
        });

        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEit) return;

                headOrBackdropImage = 1;
                showCameraAlbum(REQUEST_HEAD_PHOTO, cniMain.getWidth(), cniMain.getHeight());

            }
        });

        rlBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEit) return;

                showDateTimePicker();
            }
        });
        imProfileImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEit) return;

                headOrBackdropImage = 2;
                showCameraAlbum(REQUEST_PROFILE_PHOTO, imProfileImages.getWidth(), imProfileImages.getHeight());

            }
        });

        ableEdit(false, RlView);
    }

    private void showOrgPic() {
        et_organisation.setText(userEntity.getOrganisation());
        if ("0".equals(userEntity.getDemo()) && "0".equals(userEntity.getPending_org())) {
            iv_org_pend.setVisibility(View.INVISIBLE);
        } else if ("1".equals(userEntity.getDemo()) && "1".equals(userEntity.getPending_org())) {
            iv_org_pend.setImageResource(R.drawable.time);
            iv_org_pend.setVisibility(View.VISIBLE);
        } else {
            iv_org_pend.setImageResource(R.drawable.org_is_pend);
            iv_org_pend.setVisibility(View.VISIBLE);
        }
    }

    //设置生日
    private void setTvBirthday(String strDOB) {
        if (strDOB != null && !strDOB.equals("0000-00-00")) {
            Date date = null;
            try {
//                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //不同语言设置不同日期显示
            if (Locale.getDefault().toString().equals("zh_CN")) {
//            tvBirthday.setText(date.getYear()+"年" + date.getMonth() + "月" + date.getDay() + "日");
//                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                String time = dateFormat.format(date).toString();
                LogUtil.i("Profile_time", time);
                String birthday = time.substring(5, time.length());
                String YearBirthday = time.substring(0, 5);
                tvBirthday.setText(birthday);
                tvYearBirthday.setText(YearBirthday);
            } else {
                int year = date.getYear() + 1900;
//                tvBirthday.setText(date.getDate() + " " + this.getResources().getStringArray(R.array.months)[date.getMonth()]);
                tvBirthday.setText(date.getDate() + " " + MyDateUtils.getMonthNameArray(false)[date.getMonth()]);
                tvYearBirthday.setText(String.valueOf(year));
            }
        }
    }

    private void showPendDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_org_detail, null);
        final MyDialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView profileView = (TextView) selectIntention.findViewById(R.id.tv_view_profile);
        TextView messageView = (TextView) selectIntention.findViewById(R.id.tv_to_message);
        TextView leaveView = (TextView) selectIntention.findViewById(R.id.tv_leave_or_delete);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        profileView.setText(R.string.text_org_request_pend);
        messageView.setText(R.string.text_org_resend_request);
        leaveView.setText(R.string.text_org_cancel_request);

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                resendJoinOrg();
            }
        });
        leaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
                cancelJoinOrg();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    private void cancelJoinOrg() {
        RequestInfo info = new RequestInfo();
        info.url = String.format(Constant.API_ORG_CANCEL_JOIN, userEntity.getUser_id());
        new HttpTools(this).put(info, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    UserEntity userEntity = new GsonBuilder().create().fromJson(jsonObject.optString(Constant.LOGIN_USER), UserEntity.class);
                    if (userEntity != null) {
                        App.changeLoginedUser(userEntity);
                    }
                    handler.sendEmptyMessage(CANCEL_JOIN_ORG_SUCCESS);
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

    private void resendJoinOrg() {
        RequestInfo info = new RequestInfo();
        info.url = String.format(Constant.API_ORG_RESEND_JOIN, userEntity.getUser_id());
        new HttpTools(this).put(info, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    UserEntity userEntity = new GsonBuilder().create().fromJson(jsonObject.optString(Constant.LOGIN_USER), UserEntity.class);
                    if (userEntity != null) {
                        App.changeLoginedUser(userEntity);
                    }
                    handler.sendEmptyMessage(RESEND_JOIN_ORG_SUCCESS);
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
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateProfile() {
        if (!isProfileChanged()) {
//            finish();
            return;
        }
        String email = etEmail.getText().toString();
        if (!TextUtils.isEmpty(email) && email.indexOf("@") <= 0) {
            MessageUtil.getInstance().showShortToast(getString(R.string.text_erro_email));
            return;
        }
        if ("Male".equals(tvGender.getText().toString())) {
            userGender = "M";
        } else {
            userGender = "F";
        }
        RequestInfo requestInfo = new RequestInfo();

        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put("user_surname", etLastName.getText().toString());
        jsonParams.put("user_given_name", etFirstName.getText().toString());
        jsonParams.put("user_gender", userGender);
        jsonParams.put("user_dob", strDOB);
        jsonParams.put("user_email", email);
        jsonParams.put("user_location_name", etRegion.getText().toString());

        jsonParams.put("position", et_position.getText().toString());
        jsonParams.put("department", et_department.getText().toString());
//            jsonParams.put("int_phone_ext", "[\"" + et_internal_phone.getText().toString() + "\"]");

        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        jsonParamsString = jsonParamsString.substring(0, jsonParamsString.length() - 1);
        jsonParamsString = jsonParamsString + ",\"int_phone_ext\":[\"" + et_internal_phone.getText().toString() + "\"]}";

        requestInfo.url = String.format(Constant.API_UPDATE_MY_PROFILE, userEntity.getUser_id());
        requestInfo.jsonParam = jsonParamsString;

        new HttpTools(MyViewProfileActivity.this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                isUploadName = true;
                isUploadNameSuccess = false;
                if (vProgress.getVisibility() == View.GONE) {
                    vProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                isUploadNameSuccess = true;
                laterFinish();
            }

            @Override
            public void onResult(String response) {

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                if (!TextUtils.isEmpty(response)) {
                    if (response.contains("changedFlag")) {
                    } else if (response.contains("response_status")) {
                        Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_fail_update_information), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_success_update_information), Toast.LENGTH_SHORT).show();
                        App.changeLoginedUser(gson.fromJson(response, UserEntity.class));
                        List userList = new ArrayList<UserEntity>();
                        userList.add(gson.fromJson(response, UserEntity.class));
                        if (intent == null) {
                            intent = new Intent();
                        }

                        intent.putExtra("name", etFirstName.getText().toString());
                        setResult(RESULT_OK, intent);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MyViewProfileActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    Intent intent;

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_male_female, null);
        showSelectDialog = new MyDialog(this, null, selectIntention);
        TextView tvMale = (TextView) selectIntention.findViewById(R.id.tv_male);
        TextView tvFemale = (TextView) selectIntention.findViewById(R.id.tv_female);

        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGender.setText("Male");
                showSelectDialog.dismiss();
            }
        });

        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGender.setText("Female");
                showSelectDialog.dismiss();
            }
        });

        showSelectDialog.show();
    }

    private void showCameraAlbum(final int requestMember, final int photoWidth, final int photoHeight) {
        LogUtil.i("photoWidth*photoHeight_1", photoWidth + "*" + photoHeight);
        LayoutInflater factory = LayoutInflater.from(this);
        final View selectIntention = factory.inflate(R.layout.dialog_camera_album, null);
        showCameraAlbum = new MyDialog(this, null, selectIntention);

        TextView tvCamera = (TextView) selectIntention.findViewById(R.id.tv_camera);
        TextView tvAlbum = (TextView) selectIntention.findViewById(R.id.tv_album);
        TextView tv_cancel = (TextView) selectIntention.findViewById(R.id.tv_cancel);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(MyViewProfileActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_CAMERA);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, photoWidth);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, photoHeight);
                if (headOrBackdropImage == 2) {
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                    intent.putExtra(PickAndCropPictureActivity.CACHE_PIC_NAME, "_background");
                }
                startActivityForResult(intent, requestMember);
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
                Intent intent = new Intent(MyViewProfileActivity.this, PickAndCropPictureActivity.class);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FROM, PickAndCropPictureActivity.REQUEST_FROM_PHOTO);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_WIDTH, photoWidth);
                intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_FINAL_HEIGHT, photoHeight);
                if (headOrBackdropImage == 2) {
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_WIDTH, 16);
                    intent.putExtra(PickAndCropPictureActivity.FLAG_PIC_ASPECT_HEIGHT, 9);
                    intent.putExtra(PickAndCropPictureActivity.CACHE_PIC_NAME, "_background");
                }
                startActivityForResult(intent, requestMember);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAlbum.dismiss();
            }
        });
        showCameraAlbum.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Uri imagedUri = null;
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                // 取得裁剪后的图片
                case REQUEST_HEAD_PHOTO:
                    if (data != null) {
                        mCropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        Bitmap photo;
                        try {
                            headImagePath = mCropImagedUri.getPath();
                            if (!TextUtils.isEmpty(headImagePath)) {
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(MyViewProfileActivity.this.getContentResolver().openInputStream(mCropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(cniMain, photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_PROFILE_PHOTO:
                    if (data != null) {
                        mBackdropImagedUri = data.getParcelableExtra(PickAndCropPictureActivity.FINAL_PIC_URI);
                        Bitmap photo;
                        try {
                            backgroundImagePath = mBackdropImagedUri.getPath();
                            if (!TextUtils.isEmpty(backgroundImagePath)) {
//								photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImagedUri);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.RGB_565;
                                photo = BitmapFactory.decodeStream(MyViewProfileActivity.this.getContentResolver().openInputStream(mBackdropImagedUri), null, options);
                                if (photo != null) {
                                    setPicToView(imProfileImages, photo);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param photo
     */
    private <T extends ImageView> void setPicToView(T view, Bitmap photo) {
        if (photo != null) {
            view.setImageBitmap(photo);
        }
    }

    private void uploadImage() {
        if (mCropImagedUri == null) {
            return;
        }
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mCropImagedUri), 480, 800, false);
        final File file = new File(path);
//        isUploadImage = true;
//        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!file.exists()) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadPersonalPicture" + userEntity.getUser_id());
        params.put("mimeType", "image/png");
        params.put("file", file);
        params.put("user_id", userEntity.getUser_id());


        new HttpTools(this).upload(Constant.API_UPLOAD_PROFILE_PICTURE, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                mCropImagedUri = null;
                laterFinish();
            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");
                    if (responseStatus.equals("Fail")) {
//                        Toast.makeText(this, getResources().getString(R.string.text_update_proPicFail), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();

                    } else {
                        /**wing modify 20150625 begin*/
                        //清除缓存
                        mBitmapTools.clearMemoryCache();
                        mBitmapTools.clearDiskCache(null);
//                        mBitmapTools.clearMemoryCache(headUrl);
                        /**wing modify 20150625 end*/

//                        progressDialog.dismiss();
//                        Toast.makeText(this, getResources().getString(R.string.text_updateProPicSuccess), Toast.LENGTH_SHORT).show();
                        isUploadImageSuccess = true;
                        intent = new Intent();
                        intent.putExtra("head_pic", Uri.fromFile(file));
                        setResult(RESULT_OK, intent);
                        if (!isUploadName) {
                            updateProfile();
                        }
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
//                Toast.makeText(this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void uploadBackdropImage() {
        if (mBackdropImagedUri == null) {
            return;
        }
        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, mBackdropImagedUri), 1080, 608, false);
        final File file = new File(path);
//        isUploadImage = true;
//        File f = new File(FileUtil.getRealPathFromURI(this, mCropImagedUri));
        if (!file.exists()) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fileKey", "file");
        params.put("fileName", "UploadProfilePicture" + userEntity.getUser_id());
        params.put("mimeType", "image/png");
        params.put("file", file);
        params.put("user_id", userEntity.getUser_id());
        new HttpTools(this).upload(Constant.API_POST_PIC_PROFILE, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                if (vProgress.getVisibility() == View.GONE) {
                    vProgress.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFinish() {
                mBackdropImagedUri = null;
                laterFinish();
            }

            @Override
            public void onResult(String response) {
                try {
                    String responseStatus;
                    JSONObject jsonObject = new JSONObject(response);
                    responseStatus = jsonObject.getString("response_status");
                    if (responseStatus.equals("Fail")) {
//                        Toast.makeText(this, getResources().getString(R.string.text_update_proPicFail), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();

                    } else {
                        /**wing modify 20150625 begin*/
                        //清除缓存
                        mBackdropBitmapTools.clearMemoryCache();
                        mBackdropBitmapTools.clearDiskCache(null);
//                        mBitmapTools.clearMemoryCache(headUrl);
                        /**wing modify 20150625 end*/

//                        progressDialog.dismiss();
//                        Toast.makeText(this, getResources().getString(R.string.text_updateProPicSuccess), Toast.LENGTH_SHORT).show();
                        isUploadImageSuccess = true;
                        intent = new Intent();
                        intent.putExtra("background_pic", Uri.fromFile(file));
                        setResult(RESULT_OK, intent);
                        if (!isUploadName) {
                            updateProfile();
                        }
                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
//                Toast.makeText(this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void laterFinish() {
        Boolean uploadFinish;
        if (mCropImagedUri == null && mBackdropImagedUri == null) {
            uploadFinish = true;
        } else {
            uploadFinish = false;
        }

        if (uploadFinish == true && isUploadNameSuccess) {
            vProgress.setVisibility(View.GONE);
            finish();
        }
    }

    private MyDialog pickDateTimeDialog;
    String strData = null;

    private void showDateTimePicker() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dateTimePicker = factory.inflate(R.layout.dialog_date_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        Timestamp ts;
        if (strData == null) {
            if (TextUtils.isEmpty(tvBirthday.getText().toString())) {
                ts = new Timestamp(System.currentTimeMillis());
            } else {
                ts = Timestamp.valueOf(strDOB + " 00:00:00");
            }
        } else {
            ts = Timestamp.valueOf(strData);
        }

        Calendar mCalendar = Calendar.getInstance(TimeZone.getDefault());
        mCalendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
        datePicker.setCalendar(mCalendar);

        pickDateTimeDialog = new MyDialog(this, null, dateTimePicker);
        pickDateTimeDialog.setButtonAccept(getString(R.string.text_dialog_accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, datePicker.getYear());
                mCalendar.set(Calendar.MONTH, datePicker.getMonth());
                mCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDay());
                if (!MyDateUtils.isBeforeDate(mCalendar.getTimeInMillis() - (1000 * 86400))) {
                    MessageUtil.getInstance().showShortToast(R.string.text_wrong_data);
                    return;
                }
                String dateDesc = MyDateUtils.getLocalDateStringFromLocal(MyViewProfileActivity.this, mCalendar.getTimeInMillis());
                strData = MyDateUtils.getUTCDateString4DefaultFromLocal(mCalendar.getTimeInMillis());
                SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyy-MM-dd");
                strDOB = defaultDateFormat.format(mCalendar.getTime());
                setTvBirthday(strDOB);
                int age = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())) - Integer.parseInt(new SimpleDateFormat("yyy").format(mCalendar.getTime()));
//                tvAge.setText(String.valueOf(age));
            }
        });
        pickDateTimeDialog.setButtonCancel(getString(R.string.text_dialog_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateTimeDialog.dismiss();
            }
        });

        pickDateTimeDialog.show();
    }

    @Override
    protected void onDestroy() {
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
        super.onDestroy();
    }
}
