package com.madx.bwm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.ui.more.MoreSettingActivity;
import com.madx.bwm.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.MoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends BaseFragment<MainActivity> implements View.OnClickListener {

    private MyDialog myDialog;
    private TextView tv_num;

    public static MoreFragment newInstance(String... params) {
        return createInstance(new MoreFragment(), params);
    }

    public MoreFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_more;
    }

    @Override
    public void initView() {
        getViewById(R.id.btn_bond_alert).setOnClickListener(this);
        getViewById(R.id.btn_me).setOnClickListener(this);
//        getViewById(R.id.btn_family).setOnClickListener(this);
        getViewById(R.id.btn_share).setOnClickListener(this);
        getViewById(R.id.btn_setting).setOnClickListener(this);
//        getViewById(R.id.btn_sticker_store).setOnClickListener(this);
        getViewById(R.id.btn_about_us).setOnClickListener(this);
        getViewById(R.id.btn_contact_us).setOnClickListener(this);
        getViewById(R.id.btn_terms).setOnClickListener(this);
        getViewById(R.id.btn_sign_out).setOnClickListener(this);


        tv_num = getViewById(R.id.tv_num);

    }


    @Override
    public void requestData() {
        new HttpTools(getActivity()).get(String.format(Constant.API_BONDALERT_ALL_COUNT, MainActivity.getUser().getUser_id()), null, new HttpCallback() {
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
                    String countString = jsonObject.getString("total");
                    int count = Integer.valueOf(countString);

                    if (count > 0) {
                        tv_num.setVisibility(View.VISIBLE);
                    } else {
                        tv_num.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    tv_num.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bond_alert:
                goBondAlert();
                break;
            case R.id.btn_me:
                goMe();
                break;
//            case R.id.btn_family:
//                goFamily();
//                break;
            case R.id.btn_share:
                shareApp();
                break;
            case R.id.btn_setting:
                goSetting();
                break;
//            case R.id.btn_sticker_store:
//                break;
            case R.id.btn_about_us:
                goAbout();
                break;
            case R.id.btn_contact_us:
                contactUs();
                break;
            case R.id.btn_terms:
                showTerms();
                break;
            case R.id.btn_sign_out:
                if (myDialog == null) {
                    myDialog = new MyDialog(getActivity(), R.string.alert_exit, R.string.msg_ask_exit_app);
                    myDialog.setCanceledOnTouchOutside(false);
                    myDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (myDialog != null) {
                                myDialog.dismiss();
                            }
                        }
                    });
                    myDialog.setButtonAccept(R.string.accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            myDialog.dismiss();
                            App.logout(getActivity());
                        }
                    });
                }
                if (!myDialog.isShowing())
                    myDialog.show();

                break;
        }
    }

    private void showTerms() {
        Intent intent = new Intent(getActivity(), TermsActivity.class);
        startActivity(intent);
    }

    private void shareApp() {
        share2Friend();
    }

    private void contactUs() {
        Intent intent = new Intent(getActivity(), ContactUsActivity.class);
        startActivity(intent);
    }

    private void goAbout() {
        Intent intent = new Intent(getActivity(), AboutUsActivity.class);
        startActivity(intent);
    }

    private void goBondAlert() {

        Intent intent = new Intent(getActivity(), BondAlertActivity.class);
        startActivityForResult(intent, 0);

//        FragmentManager fm = getChildFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.fragment_container, new BondAlertFragment());
//        ft.addToBackStack(null);
//        ft.commit();
    }

    private void goMe() {
        Intent intent = new Intent(getActivity(), MeActivity.class);
        startActivity(intent);
    }

    private void goSetting() {
        Intent intent = new Intent(getActivity(), MoreSettingActivity.class);
        startActivity(intent);
    }

    private void goFamily() {
        Intent intent = new Intent(getActivity(), FamilyFragment.class);
        startActivity(intent);
    }

    private void share2Friend() {
        Intent intent = new Intent(getActivity(), TellAFriendsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (myDialog != null) {
            myDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 0:
                    requestData();
                break;
            }
        }

    }
}
