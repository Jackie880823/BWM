package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.CountryCodeActivity;
import com.bondwithme.BondWithMe.ui.TermsActivity;
import com.bondwithme.BondWithMe.util.CountryCodeUtil;
import com.gc.materialdesign.views.Button;


public class SignUpPhoneFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener{

    private final static String TAG = SignUpPhoneFragment.class.getSimpleName();
    private final static String GET_CODE = "_GET_CODE";
    private final static String RESPONSE_MESSAGE = "Server.LoginIdExist";

    private static final int GET_COUNTRY_CODE = 0;

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private TextView tvStartCountryCode;
    private EditText etPhoneNumber;
    private EditText etPassword;
//    private TextView tvLogIn;
    private Button brSignUp;
    private TextView tvTerms;
    private ImageView ivUsername;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_country_code:
                Intent intent = new Intent(getActivity(), CountryCodeActivity.class);
                startActivityForResult(intent, GET_COUNTRY_CODE);
                break;

//            case R.id.tv_btn_log_in:
//                break;

            case R.id.iv_username:
                goSignUpUsernameActivity();
                break;

            case R.id.br_sign_up:
                break;

            case R.id.tv_terms:
                goTermsActivity();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case GET_COUNTRY_CODE:
                if (resultCode == getActivity().RESULT_OK)
                {
                    tvCountry.setText(data.getStringExtra(CountryCodeActivity.COUNTRY));
                    tvCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                    tvStartCountryCode.setText(data.getStringExtra(CountryCodeActivity.CODE));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            return true;
        }
        return false;
    }

    private void initView(View view)
    {
        rlCountryCode = (RelativeLayout)view.findViewById(R.id.rl_country_code);
        tvCountry = (TextView)view.findViewById(R.id.tv_country);
        tvCountryCode = (TextView)view.findViewById(R.id.tv_country_code);
        tvStartCountryCode = (TextView)view.findViewById(R.id.tv_start_country_code);
        etPhoneNumber = (EditText)view.findViewById(R.id.et_phone_number);
        etPassword = (EditText)view.findViewById(R.id.et_password);
//        tvLogIn = (TextView)view.findViewById(R.id.tv_btn_log_in);
        brSignUp = (Button)view.findViewById(R.id.br_sign_up);
        tvTerms = (TextView)view.findViewById(R.id.tv_terms);
        ivUsername = (ImageView)view.findViewById(R.id.iv_username);

        rlCountryCode.setOnClickListener(this);
//        tvLogIn.setOnClickListener(this);
        brSignUp.setOnClickListener(this);
        tvTerms.setOnClickListener(this);
        ivUsername.setOnClickListener(this);

        etPhoneNumber.setOnEditorActionListener(this);

        tvTerms.setText(Html.fromHtml(getResources().getString(R.string.text_start_terms)));
        tvCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));
        tvStartCountryCode.setText(CountryCodeUtil.GetCountryZipCode(getActivity()));
    }

    private void goTermsActivity() {
        startActivity(new Intent(getActivity(), TermsActivity.class));
    }

    private void goSignUpUsernameActivity() {
        startActivity(new Intent(getActivity(), SignUpUsernameActivity.class));
    }


}
