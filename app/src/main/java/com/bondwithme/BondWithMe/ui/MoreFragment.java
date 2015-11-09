package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.more.AboutActivity;
import com.bondwithme.BondWithMe.ui.more.ArchiveActivity;
import com.bondwithme.BondWithMe.ui.more.MoreSettingActivity;
import com.bondwithme.BondWithMe.ui.more.RewardsActivity;
import com.bondwithme.BondWithMe.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.widget.InteractivePopupWindow;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bondwithme.BondWithMe.ui.MoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.bondwithme.BondWithMe.ui.MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends BaseFragment<MainActivity> implements View.OnClickListener {


    private TextView tv_num;
    private TextView tvVersion;

    private TextView news_alert_num;
    private TextView member_alert_num;
    private TextView recommend_alert_num;
    private TextView rewards_num;
    private static final int GET_DELAY_ADD_PHOTO = 0x30;
    private InteractivePopupWindow popupWindowAddPhoto;

    private static final String TAG = "MoreFragment";

    public static MoreFragment newInstance(String... params) {
        return createInstance(new MoreFragment(), params);
    }

    public MoreFragment() {
        super();
        // Required empty public constructor
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_DELAY_ADD_PHOTO:
                    if(MainActivity.interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)){
                        popupWindowAddPhoto = MainActivity.interactivePopupWindowMap.get(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO);
                        popupWindowAddPhoto.showPopupWindowUp();
                    }
                    break;
            }

        }
    };




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
        getViewById(R.id.btn_sticker_store).setOnClickListener(this);
        getViewById(R.id.btn_about_us).setOnClickListener(this);
//        getViewById(R.id.btn_contact_us).setOnClickListener(this);
//        getViewById(R.id.btn_terms).setOnClickListener(this);
        getViewById(R.id.btn_archive).setOnClickListener(this);
        getViewById(R.id.btn_alert_member).setOnClickListener(this);
        getViewById(R.id.btn_alert_news).setOnClickListener(this);
        getViewById(R.id.btn_alert_recommend).setOnClickListener(this);
        getViewById(R.id.btn_rewards).setOnClickListener(this);

        news_alert_num = getViewById(R.id.news_alert_num);
        member_alert_num = getViewById(R.id.member_alert_num);
        recommend_alert_num = getViewById(R.id.recommend_alert_num);
        rewards_num = getViewById(R.id.rewards_alert_num);

        tvVersion = getViewById(R.id.tv_version);
        tvVersion.setText("V " + AppInfoUtil.getAppVersionName(getActivity()));
        tv_num = getViewById(R.id.tv_num);

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void newPopAddPhoto(){
        if(MainActivity.interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)){
            popupWindowAddPhoto = MainActivity.interactivePopupWindowMap.get(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO);
            popupWindowAddPhoto.showPopupWindowUp();
        }else {
            handler.sendEmptyMessageDelayed(GET_DELAY_ADD_PHOTO, 500);
        }

    }

    private void bondDatas(JSONObject jsonObject) throws JSONException {
        checkDataAndBond2View(news_alert_num,jsonObject.getString("news"));
        checkDataAndBond2View(member_alert_num,jsonObject.getString("member"));
        checkDataAndBond2View(recommend_alert_num,jsonObject.getString("recommended"));
        checkDataAndBond2View(rewards_num,jsonObject.getString("reward"));
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

    public void getNum()
    {


        new HttpTools(getActivity()).get(String.format(Constant.API_BONDALERT_MODULES_COUNT, MainActivity.getUser().getUser_id()), null, this, new HttpCallback() {
            @Override
            public void onStart() {
//                mProgressDialog.show();
                LogUtil.d(TAG, "======url_API_BONDALERT_MODULES_COUNT=======" + (String.format(Constant.API_BONDALERT_MODULES_COUNT, MainActivity.getUser().getUser_id())));
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
    public void requestData() {
        new HttpTools(getActivity()).get(String.format(Constant.API_BONDALERT_ALL_COUNT, MainActivity.getUser().getUser_id()), null,this, new HttpCallback() {
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
                    int countOfTotal = Integer.valueOf(countString);
                    int countOfNews = Integer.valueOf(jsonObject.getString("news"));
                    int countOfMember = Integer.valueOf(jsonObject.getString("member"));
                    int countOfRecommended = Integer.valueOf(jsonObject.getString("recommended"));
                    int countOfRewards = Integer.valueOf(jsonObject.getString("reward"));
                    LogUtil.d(TAG,"countOfNews======"+countOfNews+"countOfRewards========="+countOfRewards);
                    int count = countOfTotal - countOfNews - countOfMember - countOfRecommended;
                    if (count > 0) {
                        Log.d("","mmmmmmmmm" + "mmmmmmm>0");
                        tv_num.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("","mmmmmmmmm" + "mmmmmmm<0");
                        tv_num.setVisibility(View.GONE);
                    }

                    Log.d("", "mmmmmmmmm" + count + "=====" + jsonObject.toString());

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
                v.findViewById(R.id.tv_num).setVisibility(View.GONE);
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
            case R.id.btn_sticker_store:
                goStickerStore();
                break;
            case R.id.btn_archive:
                goArchive();
                break;
            case R.id.btn_alert_member:
                v.findViewById(R.id.member_alert_num).setVisibility(View.GONE);
                goMember();
                break;
            case R.id.btn_alert_news:
                v.findViewById(R.id.news_alert_num).setVisibility(View.GONE);
                goNews();
                break;
            case R.id.btn_alert_recommend:
                v.findViewById(R.id.recommend_alert_num).setVisibility(View.GONE);
                goRecommendAlert();
                break;
            case R.id.btn_about_us:
                goAbout();
                break;
            case R.id.btn_rewards:
                goRewards();
                break;
        }
    }

    private void goRewards() {
        Intent intent = new Intent(getActivity(), RewardsActivity.class);
        startActivity(intent);
    }

    private void goMember() {
        Intent intent = new Intent(getActivity(), MemberActivity.class);
        startActivity(intent);
    }



    private void goNews() {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        startActivity(intent);
    }

    private void goRecommendAlert() {
        Intent intent = new Intent(getActivity(), RecommendActivity.class);
        startActivity(intent);
    }



    private void shareApp() {
        share2Friend();
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
    private void goStickerStore() {
        Intent intent = new Intent(getActivity(),StickerStoreActivity.class);
        startActivity(intent);
    }

    private void goArchive(){
        Intent intent = new Intent(getActivity(), ArchiveActivity.class);
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

    private void goAbout() {
        Intent intent = new Intent(getActivity(),AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
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


    @Override
    public void onResume() {
        super.onResume();
        requestData();
        getNum();
    }
}
