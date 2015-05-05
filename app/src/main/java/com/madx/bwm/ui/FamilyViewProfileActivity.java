package com.madx.bwm.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.PhotoEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FamilyViewProfileActivity extends BaseActivity {

    CircularNetworkImage cniMain;
    ImageView ivBottomLeft;
    ImageView ivBottomRight;
    TextView tvName1;
    TextView tvId1;

    TextView tvID2;
    TextView tvFirstName;
    TextView tvLastName;
//    TextView tvAge;
    TextView tvBirthday;
    TextView tvGender;
    TextView tvEmail;
    TextView tvRegion;


    UserEntity userEntity = new UserEntity();


    @Override
    public int getLayout() {
        return R.layout.activity_family_view_profile;
    }

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
        return null;
    }

    @Override
    public void initView() {

        userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");

        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        ivBottomRight = getViewById(R.id.civ_right);
        tvName1 = getViewById(R.id.tv_name1);
        tvId1 = getViewById(R.id.tv_id1);

        tvID2 = getViewById(R.id.tv_id2);
        tvFirstName = getViewById(R.id.tv_first_name);
        tvLastName = getViewById(R.id.tv_last_name);
//        tvAge = getViewById(R.id.tv_age);
        tvBirthday = getViewById(R.id.tv_birthday);
        tvGender = getViewById(R.id.tv_gender);
        tvEmail = getViewById(R.id.tv_email);
        tvRegion = getViewById(R.id.tv_region);

        VolleyUtil.initNetworkImageView(FamilyViewProfileActivity.this, cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, userEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName1.setText(userEntity.getUser_given_name());
        tvId1.setText("ID:" + userEntity.getDis_bondwithme_id());

        tvID2.setText(userEntity.getDis_bondwithme_id());
        tvFirstName.setText(userEntity.getUser_given_name());
        tvLastName.setText(userEntity.getUser_surname());
//        tvAge.setText(userEntity.getUser_dob());
        tvBirthday.setText(userEntity.getUser_dob());

//        if (!TextUtils.isEmpty(userEntity.getUser_dob()))
//        {
//            Timestamp ts;
//            ts = Timestamp.valueOf(tvBirthday.getText().toString() + " 00:00:00");
//            Calendar mCalendar = Calendar.getContextInstance(TimeZone.getDefault());
//            mCalendar.setTimeInMillis(ts.getTime() + TimeZone.getDefault().getRawOffset());
//            int age = Integer.parseInt(new SimpleDateFormat("yyyy").format(new java.util.Date())) - Integer.parseInt(new SimpleDateFormat("yyy").format(mCalendar.getTime()));
//            tvAge.setText(String.valueOf(age));
//        }

        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FamilyViewProfileActivity.this, ViewOriginalPicesActivity.class);

                ArrayList<PhotoEntity> datas = new ArrayList();
                PhotoEntity peData = new PhotoEntity();
                peData.setUser_id(userEntity.getUser_id());
                peData.setFile_id("profile");
                peData.setPhoto_caption(Constant.Module_profile);
                peData.setPhoto_multipe("false");
                datas.add(peData);

                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);

                startActivity(intent);
            }
        });

        if ("F".equals(userEntity.getUser_gender()))
        {
            /**
             * begin QK
             */
            tvGender.setText(getResources().getString(R.string.text_female));
        }
        else if ("M".equals(userEntity.getUser_gender()))
        {
            tvGender.setText(getResources().getString(R.string.text_male));
            /**
             * end
             */
        }

        tvEmail.setText(userEntity.getUser_email());
        tvRegion.setText(userEntity.getUser_location_name());

        if (!TextUtils.isEmpty(userEntity.getUser_emoticon()))
        {
            try {
                InputStream is = FamilyViewProfileActivity.this.getAssets().open(userEntity.getUser_emoticon()+".png");
                Bitmap bitmap= BitmapFactory.decodeStream(is);
                ivBottomLeft.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
