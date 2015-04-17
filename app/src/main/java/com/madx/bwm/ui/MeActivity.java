package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.madx.bwm.R;

public class MeActivity extends BaseActivity {

//    private final static int UPDATE_PROFILE = 1;
//
//    private LinearLayout llViewProfile;
//
//    private CircularNetworkImage cniMain;
//    private ImageView ivBottomLeft;
//    private TextView tvName1;
//    private TextView tvId1;
//
//    private RelativeLayout rlAlbumGallery;
//    private RelativeLayout rlWallPosting;


//    @Override
//    public int getLayout() {
//        return R.layout.activity_me;
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
        tvTitle.setText(getResources().getString(R.string.title_me));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return MeFragment.newInstance();
    }

    @Override
    public void initView() {

//        cniMain = getViewById(R.id.cni_main);
//        ivBottomLeft = getViewById(R.id.civ_left);
//        tvName1 = getViewById(R.id.tv_name1);
//        tvId1 = getViewById(R.id.tv_id1);
//
//        llViewProfile = getViewById(R.id.ll_view_profile);//跳转到My Profile
//        rlAlbumGallery = getViewById(R.id.rl_album_gallery);
//        rlWallPosting = getViewById(R.id.rl_wall_posting);
//
//        VolleyUtil.initNetworkImageView(MeActivity.this, cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//        tvName1.setText(MainActivity.getUser().getUser_given_name());
//        tvTitle.setText(MainActivity.getUser().getUser_given_name());
//        tvId1.setText("ID:" + MainActivity.getUser().getDis_bondwithme_id());
//        try {
//            InputStream is = MeActivity.this.getAssets().open(MainActivity.getUser().getUser_emoticon()+".png");
//            Bitmap bitmap= BitmapFactory.decodeStream(is);
//            ivBottomLeft.setImageBitmap(bitmap);
//
////                    Drawable da = Drawable.createFromStream(is, null);
////                    ivBottomLeft.setImageDrawable(da);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        llViewProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MeActivity.this, MyViewProfileActivity.class);
//                startActivityForResult(intent, UPDATE_PROFILE);
//            }
//        });
//
//        rlAlbumGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO
//                Intent intent = new Intent(MeActivity.this, AlbumGalleryActivity.class);
//                intent.putExtra("member_id", MainActivity.getUser().getUser_id());
//                startActivity(intent);
//            }
//        });
//
//        rlWallPosting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO
//            }
//        });

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode)
//        {
//            case 1:
//                if (resultCode == RESULT_OK)
//                {
//                    tvName1.setText(data.getStringExtra("name"));
//                    VolleyUtil.initNetworkImageView(MeActivity.this, cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//                }
//            break;
//
//            default:
//        }
//    }


    @Override
    protected void titleLeftEvent() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Log.i("", "titleLeftEvent============");
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
