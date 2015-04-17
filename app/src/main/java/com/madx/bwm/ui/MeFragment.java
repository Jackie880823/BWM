package com.madx.bwm.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.widget.CircularNetworkImage;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.MeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends BaseFragment<MeActivity> {

    private final static int UPDATE_PROFILE = 1;

    private LinearLayout llViewProfile;

    private CircularNetworkImage cniMain;
    private ImageView ivBottomLeft;
    private TextView tvName1;
    private TextView tvId1;

    private RelativeLayout rlAlbumGallery;
    private RelativeLayout rlWallPosting;

    public static MeFragment newInstance(String... params) {
        return createInstance(new MeFragment());
    }

    public MeFragment() {
        super();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.me_fragment;
    }

    @Override
    public void initView() {
        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        tvName1 = getViewById(R.id.tv_name1);
        tvId1 = getViewById(R.id.tv_id1);

        llViewProfile = getViewById(R.id.ll_view_profile);//跳转到My Profile
        rlAlbumGallery = getViewById(R.id.rl_album_gallery);
        rlWallPosting = getViewById(R.id.rl_wall_posting);

        VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName1.setText(MainActivity.getUser().getUser_given_name());
//        tvTitle.setText(MainActivity.getUser().getUser_given_name());
        tvId1.setText("ID:" + MainActivity.getUser().getDis_bondwithme_id());

        if (!TextUtils.isEmpty((MainActivity.getUser().getUser_emoticon()))) {
            try {
                InputStream is = getActivity().getAssets().open(MainActivity.getUser().getUser_emoticon() + ".png");
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivBottomLeft.setImageBitmap(bitmap);

//                    Drawable da = Drawable.createFromStream(is, null);
//                    ivBottomLeft.setImageDrawable(da);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        llViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyViewProfileActivity.class);
                startActivityForResult(intent, UPDATE_PROFILE);
            }
        });

        rlAlbumGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(getActivity(), AlbumGalleryActivity.class);
                intent.putExtra("member_id", MainActivity.getUser().getUser_id());
                startActivity(intent);
            }
        });

        rlWallPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO

                FragmentManager fm = getParentActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = WallFragment.newInstance(MainActivity.getUser().getUser_id());
                ft.replace(R.id.container, f);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
    }

    @Override
    public void requestData() {

    }


    /** christopher begin */
    //TODO
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UPDATE_PROFILE:
                if (resultCode == getActivity().RESULT_OK) {

                    getActivity().finish();
//                    tvName1.setText(data.getStringExtra("name"));
//                    VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(3 * 1000);
//                                VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();

                    initView();
                }
                break;

            default:
        }
    }
    /** christopher end */


}
