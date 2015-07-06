package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.bondwithme.BondWithMe.R;

public class AddNewMembersActivity extends BaseActivity {

    private RelativeLayout rlViaContact;
    private RelativeLayout rlViaIdName;
    private RelativeLayout rlViaQRCode;

    @Override
    public int getLayout() {
        return R.layout.activity_add_new_members;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_add_new_members));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected void titleLeftEvent() {
//        if(commandlistener!=null)
//            commandlistener.execute(leftButton);
        setResult(Activity.RESULT_OK);
        finish();

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        rlViaContact = getViewById(R.id.rl_1);
        rlViaIdName = getViewById(R.id.rl_2);
        rlViaQRCode = getViewById(R.id.rl_3);

        rlViaContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(AddNewMembersActivity.this, ViaContactMainActivity.class);
                startActivity(intent1);
            }
        });

        rlViaIdName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent(AddNewMembersActivity.this, ViaIdNameActivity.class);
                startActivity(intent2);
            }
        });
    }



    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
