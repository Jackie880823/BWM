package com.madxstudio.co8.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.AppTokenEntity;
import com.madxstudio.co8.entity.UserEntity;

public class SignUpSuccessfulActivity extends Activity {

    private UserEntity userEntity;
    private AppTokenEntity tokenEntity;

    TextView tvStartUsing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_successful);

        tvStartUsing = (TextView)findViewById(R.id.tv_start_using);

        Intent intent = getIntent();
        userEntity = (UserEntity) intent.getExtras().getSerializable(Constant.LOGIN_USER);
        tokenEntity = (AppTokenEntity) intent.getExtras().getSerializable(Constant.HTTP_TOKEN);

        tvStartUsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDetails();
            }
        });
    }

    private void goDetails() {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Constant.LOGIN_USER, userEntity);
        intent.putExtra(Constant.HTTP_TOKEN, tokenEntity);
        startActivity(intent);
    }

}
