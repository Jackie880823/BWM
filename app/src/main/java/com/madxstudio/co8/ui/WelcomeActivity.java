package com.madxstudio.co8.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.madxstudio.co8.App;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.br_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEntity userEntity;
                userEntity = App.getLoginedUser();
                userEntity.setShow_add_member(false);
                App.changeLoginedUser(userEntity);
                App.goMain(WelcomeActivity.this);
            }
        });
    }
}