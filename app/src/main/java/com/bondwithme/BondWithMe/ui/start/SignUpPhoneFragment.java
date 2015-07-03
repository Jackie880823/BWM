package com.bondwithme.BondWithMe.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.TermsActivity;
import com.gc.materialdesign.views.Button;


public class SignUpPhoneFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = SignUpPhoneFragment.class.getSimpleName();
    private final static String GET_CODE = "_GET_CODE";
    private final static String RESPONSE_MESSAGE = "Server.LoginIdExist";

    private RelativeLayout rlCountryCode;
    private TextView tvCountry;
    private TextView tvCountryCode;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private TextView tvLogIn;
    private Button brLogIn;
    private TextView tvTerms;



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
                break;

//            case R.id.tv_btn_log_in:
//                break;

            case R.id.br_log_in:
                break;

            case R.id.tv_terms:
                goTermsActivity();
                break;
        }
    }


    private void initView(View view)
    {


        rlCountryCode = (RelativeLayout)view.findViewById(R.id.rl_country_code);
        tvCountry = (TextView)view.findViewById(R.id.tv_country);
        tvCountryCode = (TextView)view.findViewById(R.id.tv_country_code);
        etPhoneNumber = (EditText)view.findViewById(R.id.et_phone_number);
        etPassword = (EditText)view.findViewById(R.id.et_password);
//        tvLogIn = (TextView)view.findViewById(R.id.tv_btn_log_in);
        brLogIn = (Button)view.findViewById(R.id.br_sign_up);
        tvTerms = (TextView)view.findViewById(R.id.tv_terms);

        rlCountryCode.setOnClickListener(this);
//        tvLogIn.setOnClickListener(this);
        brLogIn.setOnClickListener(this);
        tvTerms.setOnClickListener(this);

        tvTerms.setText(Html.fromHtml(getResources().getString(R.string.text_start_terms2)));
    }

    private void goTermsActivity() {
        startActivity(new Intent(getActivity(), TermsActivity.class));
    }





}
