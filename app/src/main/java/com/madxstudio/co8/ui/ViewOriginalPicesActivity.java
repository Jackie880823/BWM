package com.madxstudio.co8.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;

/**
 * Created by wing on 15/3/23.
 */
public class ViewOriginalPicesActivity extends BaseFragmentActivity {


    private String request_url;
    private String memberId;

    /**
     * 标识是否传入数据
     */
    public static final String IS_DATA = "is_data";
    /**
     * 当IS_DATA为false时下面两参数生效,两个参数是请求图片的必须条件
     */
    public static final String MEMBER_ID = "memberId";

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
        //浏览单张图片的情况
        if (getIntent().getBooleanExtra(IS_DATA, false)) {
            fragment = ViewOriginalPicesMainFragment.newInstance((java.util.List<com.madxstudio.co8.entity.PhotoEntity>) getIntent().getSerializableExtra("datas"));
        } else {
            //多张图片的时候
            fragment = new ViewOriginalPicesMainFragment();
            request_url = getIntent().getStringExtra(Constant.REQUEST_URL);
            memberId = getIntent().getStringExtra(MEMBER_ID);
            bundle.putString(Constant.REQUEST_URL, request_url);
            bundle.putString(MEMBER_ID, memberId);
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
