package com.bondwithme.BondWithMe.ui;

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

import com.android.volley.ext.tools.BitmapTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.ui.wall.WallFragment;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    String headUrl;
    @Override
    public void initView() {
        cniMain = getViewById(R.id.cni_main);
        ivBottomLeft = getViewById(R.id.civ_left);
        tvName1 = getViewById(R.id.tv_name1);
        tvId1 = getViewById(R.id.tv_id1);

        llViewProfile = getViewById(R.id.ll_view_profile);//跳转到My Profile
        rlAlbumGallery = getViewById(R.id.rl_album_gallery);
        rlWallPosting = getViewById(R.id.rl_wall_posting);

        headUrl = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id());
        new BitmapTools(getActivity()).display(cniMain, headUrl, R.drawable.network_image_default, R.drawable.network_image_default);
//        VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName1.setText(MainActivity.getUser().getUser_given_name());
//        tvTitle.setText(MainActivity.getUser().getUser_given_name());
        tvId1.setText("ID:" + MainActivity.getUser().getDis_bondwithme_id());
        String dofeel_code = MainActivity.getUser().getDofeel_code();
        if (!TextUtils.isEmpty((dofeel_code))) {
            try {
                String filePath = "";
                if (dofeel_code.indexOf("_") != -1) {
                    filePath = dofeel_code.replaceAll("_", File.separator);
                }
                InputStream is = getActivity().getAssets().open(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivBottomLeft.setImageBitmap(bitmap);
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
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra("member_id", MainActivity.getUser().getUser_id());
                startActivity(intent);
            }
        });

        rlWallPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = WallFragment.newInstance(MainActivity.getUser().getUser_id());
                ft.replace(R.id.container, f);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        cniMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);
                ArrayList<PhotoEntity> datas = new ArrayList();
                PhotoEntity peData = new PhotoEntity();
                peData.setUser_id(MainActivity.getUser().getUser_id());
                peData.setFile_id("profile");
                peData.setPhoto_caption(Constant.Module_profile);
                peData.setPhoto_multipe("false");
                datas.add(peData);
                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);
                startActivity(intent);
            }
        });
    }

    @Override
    public void requestData() {

    }

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

//                    initView();
                }
                break;

            default:
        }
    }
}
