package com.madxstudio.co8.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.GsonBuilder;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.ProfilePrivacyEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.widget.MyDialog;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class GroupPrivacyActivity extends BaseActivity {

    View mProgressDialog;
    private CheckBox cbTO;
    private CheckBox cbTM;
    private CheckBox cbGC;
    private LinearLayout llGroupPrivacy;

    private TextView tvBirthday;
    private TextView tvYearOfBirth;
    private TextView tvGender;
    private TextView tvEmail;
    private TextView tvRegion;
    private TextView tvPhone;
    private TextView tvExtPhone;

    private LinearLayout llBirthday;
    private LinearLayout llYearOfBirthday;
    private LinearLayout llGender;
    private LinearLayout llEmail;
    private LinearLayout llRegion;
    private LinearLayout llPhone;
    private LinearLayout llExtPhone;

    private ProfilePrivacyEntity profilePrivacyEntity;
    private MyDialog changePrivacyDialog;

    private int intLevel;

    @Override
    public int getLayout() {
        return R.layout.activity_group_privacy;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_group_privacy);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
//        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setImageResource(R.drawable.btn_done);
//        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {
        updateConfig();
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);

        llGroupPrivacy = getViewById(R.id.ll_group_privacy);

        cbTO = getViewById(R.id.cb_to_other);
        cbTM = getViewById(R.id.cb_to_me);
        cbGC = getViewById(R.id.cb_group_chat);

        tvBirthday = getViewById(R.id.tv_birthday_privacy);
        tvYearOfBirth = getViewById(R.id.tv_yearOfBirth_privacy);
        tvGender = getViewById(R.id.tv_gender_privacy);
        tvEmail = getViewById(R.id.tv_email_privacy);
        tvRegion = getViewById(R.id.tv_region_privacy);
        tvPhone = getViewById(R.id.tv_phone_privacy);
        tvExtPhone= getViewById(R.id.tv_internal_phone_privacy);

        llBirthday = getViewById(R.id.ll_birthday_privacy);
        llYearOfBirthday = getViewById(R.id.ll_yearOfBirth_privacy);
        llGender = getViewById(R.id.ll_gender_privacy);
        llEmail = getViewById(R.id.ll_region_privacy);
        llRegion = getViewById(R.id.ll_email_privacy);
        llPhone = getViewById(R.id.ll_phone_privacy);
        llExtPhone= getViewById(R.id.ll_internal_phone_privacy);

        llBirthday.setOnClickListener(this);
        llYearOfBirthday.setOnClickListener(this);
        llGender.setOnClickListener(this);
        llEmail.setOnClickListener(this);
        llRegion.setOnClickListener(this);
        llPhone.setOnClickListener(this);
        llExtPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (profilePrivacyEntity != null) {
            switch (v.getId()) {
                case R.id.ll_birthday_privacy:
                    setProfilePrivacy(getString(R.string.text_birthday), profilePrivacyEntity.getDob_date(), tvBirthday);
                    break;
                case R.id.ll_yearOfBirth_privacy:
                    setProfilePrivacy(getString(R.string.year_of_birth), profilePrivacyEntity.getDob_year(), tvYearOfBirth);
                    break;
                case R.id.ll_gender_privacy:
                    setProfilePrivacy(getString(R.string.text_gender), profilePrivacyEntity.getGender(), tvGender);
                    break;
                case R.id.ll_email_privacy:
                    setProfilePrivacy(getString(R.string.text_email), profilePrivacyEntity.getEmail(), tvEmail);
                    break;
                case R.id.ll_region_privacy:
                    setProfilePrivacy(getString(R.string.text_region), profilePrivacyEntity.getLocation(), tvRegion);
                    break;
                case R.id.ll_phone_privacy:
                    setProfilePrivacy(getString(R.string.text_phone), profilePrivacyEntity.getPhone(), tvPhone);
                    break;
                case R.id.ll_internal_phone_privacy:
                    setProfilePrivacy(getString(R.string.text_privacy_internal_phone), profilePrivacyEntity.getInt_phone_ext(), tvExtPhone);
                    break;
            }
        }
    }

    RadioButton rbOnlyMe;
    RadioButton rbAllMember;
    RadioButton rbPublic;

    private void setProfilePrivacy(String dialogTitle, final String strLevel, final TextView tv) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.dialog_profile_privacy, null);
        changePrivacyDialog = new MyDialog(this, dialogTitle, dialogView);
        rbOnlyMe = (RadioButton) dialogView.findViewById(R.id.rb_only_me);
        rbAllMember = (RadioButton) dialogView.findViewById(R.id.rb_all_member);
        rbPublic = (RadioButton) dialogView.findViewById(R.id.rb_public);
        if ("0".equals(strLevel)) {
            rbOnlyMe.setChecked(true);
        } else if ("1".equals(strLevel)) {
            rbAllMember.setChecked(true);
        } else if ("2".equals(strLevel)) {
            rbPublic.setChecked(true);
        }

        intLevel = Integer.parseInt(strLevel);
        changePrivacyDialog.setCanceledOnTouchOutside(false);
        changePrivacyDialog.setButtonCancel(R.string.text_confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrivacyDialog.dismiss();
                String str = Integer.toString(intLevel);
                switch (tv.getId()) {
                    case R.id.tv_birthday_privacy:
                        profilePrivacyEntity.setDob_date(str);
                        displayPrivacyLevel(str, tvBirthday);
                        break;
                    case R.id.tv_yearOfBirth_privacy:
                        profilePrivacyEntity.setDob_year(str);
                        displayPrivacyLevel(str, tvYearOfBirth);
                        break;
                    case R.id.tv_gender_privacy:
                        profilePrivacyEntity.setGender(str);
                        displayPrivacyLevel(str, tvGender);
                        break;
                    case R.id.tv_email_privacy:
                        profilePrivacyEntity.setEmail(str);
                        displayPrivacyLevel(str, tvEmail);
                        break;
                    case R.id.tv_region_privacy:
                        profilePrivacyEntity.setLocation(str);
                        displayPrivacyLevel(str, tvRegion);
                        break;
                    case R.id.tv_phone_privacy:
                        profilePrivacyEntity.setPhone(str);
                        displayPrivacyLevel(str, tvPhone);
                        break;
                    case R.id.tv_internal_phone_privacy:
                        profilePrivacyEntity.setInt_phone_ext(str);
                        displayPrivacyLevel(str, tvExtPhone);
                        break;

                }

            }
        });

        dialogView.findViewById(R.id.rl_only_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbOnlyMe.setChecked(true);
                rbAllMember.setChecked(false);
                rbPublic.setChecked(false);
                intLevel = 0;
            }
        });
        dialogView.findViewById(R.id.re_all_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbOnlyMe.setChecked(false);
                rbAllMember.setChecked(true);
                rbPublic.setChecked(false);
                intLevel = 1;

            }
        });
        dialogView.findViewById(R.id.rl_public).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbOnlyMe.setChecked(false);
                rbAllMember.setChecked(false);
                rbPublic.setChecked(true);
                intLevel = 2;

            }
        });
        changePrivacyDialog.show();

    }

    private void updateConfig() {
        if (profilePrivacyEntity == null) {
            return;
        }
        mProgressDialog.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("recommend_me", (cbTO.isChecked() ? "1" : "0"));
        params.put("recommend_others", (cbTM.isChecked() ? "1" : "0"));
        params.put("group_add", (cbGC.isChecked() ? "1" : "0"));
        params.put("dob_date", profilePrivacyEntity.getDob_date());
        params.put("dob_year", profilePrivacyEntity.getDob_year());
        params.put("gender", profilePrivacyEntity.getGender());
        params.put("email", profilePrivacyEntity.getEmail());
        params.put("phone", profilePrivacyEntity.getPhone());
        params.put("location", profilePrivacyEntity.getLocation());
        params.put("int_phone_ext", profilePrivacyEntity.getInt_phone_ext());

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id());

        new HttpTools(this).put(requestInfo, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResult(String string) {
//                MessageUtil.getInstance().showShortToast(R.string.msg_action_successed);
                GroupPrivacyActivity.this.finish();
            }

            @Override
            public void onError(Exception e) {
//                MessageUtil.getInstance().showShortToast(R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    boolean result;

    @Override
    public void requestData() {
        new HttpTools(this).get(String.format(Constant.API_SETTING_CONFIG, MainActivity.getUser().getUser_id()), null, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
                if (result) {
                    llGroupPrivacy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResult(String string) {
                try {
                    if (string != null) {
                        bindConfig2Group(string);
                        result = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                MessageUtil.getInstance().showShortToast(R.string.msg_load_config_successed);
            }

            @Override
            public void onError(Exception e) {
//                MessageUtil.getInstance().showShortToast(R.string.msg_load_config_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void bindConfig2Group(String config) throws JSONException {
        profilePrivacyEntity = (new GsonBuilder()).create().fromJson(config, ProfilePrivacyEntity.class);
        if (profilePrivacyEntity == null) {
            return;
        }
//        if ("1".equals(config.get("recommend_me"))) {
//            cbTO.setChecked(true);
//        } else {
//            cbTO.setChecked(false);
//        }
//        if ("1".equals(config.get("recommend_others"))) {
//            cbTM.setChecked(true);
//        } else {
//            cbTM.setChecked(false);
//        }
        if ("1".equals(profilePrivacyEntity.getGroup_add())) {
            cbGC.setChecked(true);
        } else {
            cbGC.setChecked(false);
        }

        displayPrivacyLevel(profilePrivacyEntity.getDob_date(), tvBirthday);
        displayPrivacyLevel(profilePrivacyEntity.getDob_year(), tvYearOfBirth);
        displayPrivacyLevel(profilePrivacyEntity.getGender(), tvGender);
        displayPrivacyLevel(profilePrivacyEntity.getEmail(), tvEmail);
        displayPrivacyLevel(profilePrivacyEntity.getPhone(), tvPhone);
        displayPrivacyLevel(profilePrivacyEntity.getLocation(), tvRegion);
        displayPrivacyLevel(profilePrivacyEntity.getInt_phone_ext(), tvExtPhone);
    }

    private void displayPrivacyLevel(Object level, TextView tv) {
        LogUtil.d("level====" + level, "TextView====" + tv.getId());

        if ("0".equals(level)) {
            tv.setText(getString(R.string.text_only_me));       //0 代表仅自己可以看 (Only Me)
        } else if ("1".equals(level)) {
            tv.setText(getString(R.string.text_new_everyone));    //1 代表只有成员可以看 (All Members)
        } else if ("2".equals(level)) {
            tv.setText(getString(R.string.text_public));        //2代表公开资料 (Public)
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * add by wing
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        finish();
        startActivity(intent);
    }
}
