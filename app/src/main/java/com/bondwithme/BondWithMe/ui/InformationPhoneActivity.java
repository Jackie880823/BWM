package com.bondwithme.BondWithMe.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.AppTokenEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.util.NetworkUtil;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

public class InformationPhoneActivity extends BaseActivity {

    EditText etFirstName;
    EditText etLastName;

    RadioGroup mRadioGroup;
    boolean blnFM = false;

    CheckBox cbAgree;
    TextView terms;

    Button btnNext;

    //以下参数是post到服务器注册账号的
    String user_surname;                      // last name
    String user_given_name;                   // first name
    String user_login_id;                     // username/phonenumber   username / (区号+手机号)
    String user_login_type;                   // phone/username
    String user_password;                     // need to encode to md5hash format
    String user_uuid ;                        // device unique id - UUID
    String user_gender;                       // M/F – M is male, F is female
    String user_country_code;                 // country code
    String user_phone;                        // phone number
    String user_tnc_read = "1";               // default to 1, user agreed to term and condition
    String user_app_version = "1.8.0";        // app version
    String user_app_os = "android";           // device os (ios/android)

    UserEntity userEntity = new UserEntity();
    private List<UserEntity> userList;

    @Override
    public int getLayout() {
        return R.layout.activity_personal_information;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_login);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void setTitle() {
        tvTitle.setText("Personal Information");
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");

        etFirstName = getViewById(R.id.et_first_name);
        etLastName = getViewById(R.id.et_last_name);

        mRadioGroup = getViewById(R.id.rg_main);

        cbAgree = getViewById(R.id.cb);
        terms = getViewById(R.id.terms);

        btnNext = getViewById(R.id.btn_next);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationPhoneActivity.this, TermsActivity.class));
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_femal:
                        user_gender = "F";
                        blnFM = true;
                        break;
                    case R.id.rb_male:
                        user_gender = "M";
                        blnFM = true;
                        break;
                    default:
                        blnFM = false;
                        break;
                }
            }
        });

        etFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (etFirstName.getText().toString().length() != 0) {

                    } else {
                        Toast.makeText(InformationPhoneActivity.this, "Please input your First Name", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        etLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (etLastName.getText().toString().length() != 0) {

                    } else {
                        Toast.makeText(InformationPhoneActivity.this, "Please input your Last Name", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkConnected(InformationPhoneActivity.this)) {
                    Toast.makeText(InformationPhoneActivity.this, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkall())
                {
                    btnNext.setClickable(false);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user_surname", etLastName.getText().toString());
                    params.put("user_given_name", etFirstName.getText().toString());
                    params.put("user_login_id", userEntity.getUser_login_id());
                    params.put("user_login_type", userEntity.getUser_login_type());
                    params.put("user_password", userEntity.getUser_password());
                    params.put("user_uuid", Settings.Secure.getString(InformationPhoneActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                    params.put("user_gender", user_gender);
                    params.put("user_country_code", userEntity.getUser_country_code());
                    params.put("user_phone", userEntity.getUser_phone());
                    params.put("user_tnc_read", "1");
                    params.put("user_app_version", "1.8.0");
                    params.put("user_app_os", "android");

                    new HttpTools(InformationPhoneActivity.this).post(Constant.API_CREATE_NEW_USER, params,this, new HttpCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {
                            btnNext.setClickable(true);
                        }

                        @Override
                        public void onResult(String response) {
                            GsonBuilder gsonb = new GsonBuilder();
                            Gson gson = gsonb.create();

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                userEntity = gson.fromJson(jsonObject.getString("user"), new TypeToken<UserEntity>() {}.getType());

                                AppTokenEntity tokenEntity = gson.fromJson(jsonObject.getString("token"), AppTokenEntity.class);

                                if (userEntity != null ) {
//                                    Intent intent = new Intent(InformationPhoneActivity.this, PersonalPictureActivity.class);
//                                    /**wing modified for clear activity stack*/
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.putExtra("user", userEntity);
//                                    intent.putExtra("token", tokenEntity);
//                                    startActivity(intent);

                                    Intent intentToBeNewRoot = new Intent(InformationPhoneActivity.this, PersonalPictureActivity.class);
                                    ComponentName cn = intentToBeNewRoot.getComponent();
                                    Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                                    mainIntent.putExtra("user", userEntity);
                                    mainIntent.putExtra("token", tokenEntity);
                                    startActivity(mainIntent);
                                }
                                else
                                {
                                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });
                }
                else if (etFirstName.getText().toString().length() == 0)
                {
                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_input_first_name), Toast.LENGTH_SHORT).show();
                }
                else if (etLastName.getText().toString().length() == 0)
                {
                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_input_last_name), Toast.LENGTH_SHORT).show();
                }
                else if (!blnFM)
                {
                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_choose_gender), Toast.LENGTH_SHORT).show();
                }
                else if (!cbAgree.isChecked())
                {
                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_agree_with_terms), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
                }




//                if (checkall())
//                {
//                    StringRequest stringRequestPost = new StringRequest(Request.Method.POST, Constant.API_CREATE_NEW_USER, new Response.Listener<String>() {
//
//                        GsonBuilder gsonb = new GsonBuilder();
//
//                        Gson gson = gsonb.create();
//
//                        @Override
//                        public void onResponse(String response) {
//
//                            Toast.makeText(InformationPhoneActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
//
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//
//                                userEntity = gson.fromJson(jsonObject.getString("user"), new TypeToken<UserEntity>() {}.getType());
//
//                                AppTokenEntity tokenEntity = gson.fromJson(jsonObject.getString("token"), AppTokenEntity.class);
//
//                                if (userEntity != null ) {
//                                    Intent intent = new Intent(InformationPhoneActivity.this, PersonalPictureActivity.class);
//                                    intent.putExtra("user", userEntity);
//                                    intent.putExtra("token", tokenEntity);
//                                    startActivity(intent);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            //TODO
//                            error.printStackTrace();
//                            Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            HashMap<String, String> params = new HashMap<String, String>();
//                            params.put("user_surname", etLastName.getText().toString());
//                            params.put("user_given_name", etFirstName.getText().toString());
//                            params.put("user_login_id", userEntity.getUser_login_id());
//                            params.put("user_login_type", userEntity.getUser_login_type());
//                            params.put("user_password", userEntity.getUser_password());
//                            params.put("user_uuid", Settings.Secure.getString(InformationPhoneActivity.this.getContentResolver(),
//                                    Settings.Secure.ANDROID_ID));
//                            params.put("user_gender", user_gender);
//                            params.put("user_country_code", userEntity.getUser_country_code());
//                            params.put("user_phone", userEntity.getUser_phone());
//                            params.put("user_tnc_read", "1");
//                            params.put("user_app_version", "1.8.0");
//                            params.put("user_app_os", "android");
//
//                            return params;
//                        }
//                    };
//                    VolleyUtil.addRequest2Queue(InformationPhoneActivity.this, stringRequestPost, "");
//                }
//                else if (etFirstName.getText().toString().length() == 0)
//                {
//                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_input_first_name), Toast.LENGTH_SHORT).show();
//                }
//                else if (etLastName.getText().toString().length() == 0)
//                {
//                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_input_last_name), Toast.LENGTH_SHORT).show();
//                }
//                else if (!blnFM)
//                {
//                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_choose_gender), Toast.LENGTH_SHORT).show();
//                }
//                else if (!cbAgree.isChecked())
//                {
//                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_agree_with_terms), Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(InformationPhoneActivity.this, getString(R.string.text_full_all_fields), Toast.LENGTH_SHORT).show();
//                }


            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //string -> MD5
    public static String MD5(String str)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for(int i = 0; i < charArray.length; i++)
        {
            byteArray[i] = (byte)charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for( int i = 0; i < md5Bytes.length; i++)
        {
            int val = ((int)md5Bytes[i])&0xff;
            if(val < 16)
            {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    boolean checkall()
    {
        if ( (etLastName.getText().toString().length() != 0) && (etFirstName.getText().toString().length() != 0) && blnFM && cbAgree.isChecked())
        {
            return true;
        }
        else
            return false;
    }
}
