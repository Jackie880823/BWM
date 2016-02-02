package com.madxstudio.co8.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madxstudio.co8.ui.BondAlertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BondAlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BondAlertFragment extends BaseFragment<BondAlertActivity> implements View.OnClickListener {


    private TextView wall_alert_num;
    private TextView event_alert_num;
    private TextView bigday_alert_num;
    private TextView miss_alert_num;
    private TextView member_alert_num;

    private TextView group_alert_num;

    public static BondAlertFragment newInstance(String... params) {
        return createInstance(new BondAlertFragment(), params);
    }

    public BondAlertFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_bond_alert;
    }

    @Override
    public void initView() {

//        mProgressDialog = new ProgressDialog(getActivity(), R.string.text_loading);
        getViewById(R.id.btn_alert_miss).setOnClickListener(this);
        getViewById(R.id.btn_alert_bigday).setOnClickListener(this);
        getViewById(R.id.btn_alert_wall).setOnClickListener(this);
        getViewById(R.id.btn_alert_event).setOnClickListener(this);
        getViewById(R.id.btn_alert_member).setOnClickListener(this);

        getViewById(R.id.btn_alert_group).setOnClickListener(this);

        wall_alert_num = getViewById(R.id.wall_alert_num);
        event_alert_num = getViewById(R.id.event_alert_num);
        bigday_alert_num = getViewById(R.id.bigday_alert_num);
        miss_alert_num = getViewById(R.id.miss_alert_num);
        member_alert_num = getViewById(R.id.member_alert_num);
        group_alert_num = getViewById(R.id.group_alert_num);

    }

    private void bondDatas(JSONObject jsonObject) throws JSONException {
        checkDataAndBond2View(wall_alert_num, jsonObject.getString("wall"));
        checkDataAndBond2View(event_alert_num, jsonObject.getString("event"));
        checkDataAndBond2View(bigday_alert_num, jsonObject.getString("bigDay"));
        checkDataAndBond2View(miss_alert_num,jsonObject.getString("miss"));
        checkDataAndBond2View(group_alert_num,jsonObject.getString("group"));
        checkDataAndBond2View(member_alert_num,jsonObject.getString("member"));

    }

    private void checkDataAndBond2View(TextView view, String countString){
        int count = Integer.valueOf(countString);

        if (count > 99) {
            count = 99;
        }

        if (count == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText("" + count);
        }
    }

    @Override
    public void requestData() {


    }

    @Override
    public void onResume() {
        super.onResume();
        getNum();
    }

    public void getNum()
    {


        new HttpTools(getActivity()).get(String.format(Constant.API_BONDALERT_MODULES_COUNT, MainActivity.getUser().getUser_id()), null, this, new HttpCallback() {
            @Override
            public void onStart() {
//                mProgressDialog.show();
            }

            @Override
            public void onFinish() {
//                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    bondDatas(jsonObject);
                } catch (JSONException e) {
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
            case R.id.btn_alert_miss:
                v.findViewById(R.id.miss_alert_num).setVisibility(View.GONE);
                goMiss();
                break;
            case R.id.btn_alert_bigday:
                v.findViewById(R.id.bigday_alert_num).setVisibility(View.GONE);
                goBigDay();
                break;
            case R.id.btn_alert_wall:
                v.findViewById(R.id.wall_alert_num).setVisibility(View.GONE);
                goWallAlert();
                break;
            case R.id.btn_alert_event:
                v.findViewById(R.id.event_alert_num).setVisibility(View.GONE);
                goEventAlert();
                break;
            case R.id.btn_alert_group:
                v.findViewById(R.id.group_alert_num).setVisibility(View.GONE);
                goGroupAlert();
                break;
            case R.id.btn_alert_member:
                v.findViewById(R.id.member_alert_num).setVisibility(View.GONE);
                goMemberAlert();
                break;
        }
    }

    private void goMemberAlert() {
        Intent intent = new Intent(getActivity(), MemberActivity.class);
        startActivity(intent);
    }

    private void goBigDay() {
        Intent intent = new Intent(getActivity(), com.madxstudio.co8.ui.more.BondAlert.BigDayActivity.class);
        startActivity(intent);
    }

    private void goWallAlert() {
        Intent intent = new Intent(getActivity(), AlertWallActivity.class);
        startActivity(intent);
    }


    private void goEventAlert() {
        Intent intent = new Intent(getActivity(), AlertEventActivity.class);
        startActivity(intent);
    }


    private void goMiss() {
        Intent intent = new Intent(getActivity(), MissListActivity.class);
        startActivity(intent);
    }



    private void goGroupAlert(){
        Intent intent = new Intent(getActivity(), AlertGroupActivity.class);
        startActivity(intent);
    }




}
