package com.madxstudio.co8.ui.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.NewsHolder;
import com.madxstudio.co8.http.VolleyUtil;
import com.madxstudio.co8.util.LogUtil;

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
