package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.widget.CircularNetworkImage;

import java.util.ArrayList;
import java.util.List;

public class FamilyProfileActivity extends BaseActivity {

    String useId;//本人Id，这个将来是全局变量
    String memberId;//本人的memberId

    CircularNetworkImage cniMain;
    ImageView ivBottomLeft;
    ImageView ivBottomRight;
    TextView tvName1;
    TextView tvId1;
    ImageButton ibMiss;

    LinearLayout llViewProfile;

    RelativeLayout rlPathRelationship;
    RelativeLayout rlAlbumGallery;
    RelativeLayout rlWallPosting;

    Button btnSendMessage;

    public List<UserEntity> data = new ArrayList<>();
    UserEntity userEntity = new UserEntity();

//    @Override
//    public int getLayout() {
//
//        return R.layout.activity_family_profile;
//    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
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
        tvTitle.setText(getResources().getString(R.string.title_family_profile));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return FamilyProfileFragment.newInstance();
    }

    @Override
    public void initView() {


    }


    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void titleLeftEvent() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Log.i("","titleLeftEvent============");
            super.titleLeftEvent();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                titleLeftEvent();
            }
            return true;
        } else {
//            Log.i("","2titleLeftEvent============");
            return super.dispatchKeyEvent(event);
        }
    }

}
