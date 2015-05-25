package com.madx.bwm.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;

import com.madx.bwm.R;

/**
 * Created by wing on 15/3/23.
 */
public class ViewOriginalPicesActivity extends BaseFragmentActivity {


    private String request_url;
    private String memberId;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_original_pices);
        ViewOriginalPicesMainFragment fragment;
        Bundle bundle = new Bundle();
        if (getIntent().getBooleanExtra("is_data", false)) {
            fragment = ViewOriginalPicesMainFragment.newInstance((java.util.List<com.madx.bwm.entity.PhotoEntity>) getIntent().getSerializableExtra("datas"));
        } else {
            fragment = new ViewOriginalPicesMainFragment();
            request_url = getIntent().getStringExtra("request_url");
            memberId = getIntent().getStringExtra("memberId");
            bundle.putString("request_url", request_url);
            bundle.putString("memberId", memberId);
        }
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.images_witcher, fragment).commit();

//        changeFragment(ViewOriginalPicesMainFragment.createInstance(new ViewOriginalPicesMainFragment(), url), true);
        initView();
        requestData();
    }


    private void initView() {

    }

    public void requestData() {

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
