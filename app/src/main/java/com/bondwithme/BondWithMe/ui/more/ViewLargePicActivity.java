package com.bondwithme.BondWithMe.ui.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.NewsHolder;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.LogUtil;

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
