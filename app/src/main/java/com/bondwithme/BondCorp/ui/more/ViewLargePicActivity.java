package com.bondwithme.BondCorp.ui.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.NewsHolder;
import com.bondwithme.BondCorp.http.VolleyUtil;
import com.bondwithme.BondCorp.util.LogUtil;

/**
 * Created by heweidong on 15/10/20.
 */
public class ViewLargePicActivity extends Activity {

    NetworkImageView ivLargeImage;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_large_pic);
        Intent intent = getIntent();
        url = intent.getStringExtra(NewsHolder.PIC_URL);

        ivLargeImage = (NetworkImageView) findViewById(R.id.iv_pic);
        ivLargeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        VolleyUtil.initNetworkImageView(this,ivLargeImage,url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("ViewLargeImageActivity","onDestroy");
    }
}
