package com.madxstudio.co8.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.ui.more.ViewQRCodeActivity;
import com.madxstudio.co8.ui.wall.WallFragment;
import com.madxstudio.co8.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MeFragment extends BaseFragment<MeActivity> {

    private final static int UPDATE_PROFILE = 1;

    private RelativeLayout llViewProfile;

    private CircularNetworkImage cniMain;
    private NetworkImageView imProfileImages;
    private NetworkImageView imQrImages;
    private ImageView ivBottomLeft;
    private TextView tvName1;
    private TextView tvId1;
    private TextView tvLoginId;

    private RelativeLayout rlAlbumGallery;
    private RelativeLayout rlWallPosting;
    private int[] array;
    private int profileBackgroundId;
    private RelativeLayout rlViewQRCode;

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
    protected void setParentTitle() {
        setTitle(getString(R.string.title_me));
    }

    String headUrl;

    @Override
    public void initView() {
        cniMain = getViewById(R.id.cni_main);
        imProfileImages = getViewById(R.id.iv_profile_images);
        imQrImages = getViewById(R.id.iv_profile_qr);
        ivBottomLeft = getViewById(R.id.civ_left);
        tvName1 = getViewById(R.id.tv_name1);
        tvId1 = getViewById(R.id.tv_id1);
        tvLoginId = getViewById(R.id.tv_login_id1);

        llViewProfile = getViewById(R.id.ll_view_profile);//跳转到My Profile
        rlAlbumGallery = getViewById(R.id.rl_album_gallery);
        rlWallPosting = getViewById(R.id.rl_wall_posting);
        rlViewQRCode = getViewById(R.id.rl_view_qr);

//        array = new int[]{R.drawable.profile_background_0,R.drawable.profile_background_1,R.drawable.profile_background_2,
//                R.drawable.profile_background_3,R.drawable.profile_background_4,R.drawable.profile_background_5};
//        profileBackgroundId = randomImageId(array);
        profileBackgroundId = R.drawable.profile_background_0;

        headUrl = String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id());
        BitmapTools.getInstance(getActivity()).display(cniMain, headUrl, R.drawable.default_head_icon, R.drawable.default_head_icon);
        BitmapTools.getInstance(getActivity()).display(imProfileImages, String.format(Constant.API_GET_PIC_PROFILE, MainActivity.getUser().getUser_id()), profileBackgroundId, profileBackgroundId);
        BitmapTools.getInstance(getActivity()).display(imQrImages, String.format(Constant.API_GET_PROFILE_QR, MainActivity.getUser().getUser_id()), R.drawable.qrcode_button, R.drawable.qrcode_button);


//        VolleyUtil.initNetworkImageView(getActivity(), cniMain, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, MainActivity.getUser().getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        tvName1.setText(MainActivity.getUser().getUser_given_name());
//        tvTitle.setText(MainActivity.getUser().getUser_given_name());
        tvId1.setText(MainActivity.getUser().getDis_bondwithme_id());
        if(Constant.TYPE_FACEBOOK.equals(MainActivity.getUser().getUser_login_type()))
        {
            tvLoginId.setText(getResources().getString(R.string.login_via_facebook));
        }else {
            tvLoginId.setText(MainActivity.getUser().getUser_login_id());
        }

        String dofeel_code = MainActivity.getUser().getDofeel_code();
//        if (!TextUtils.isEmpty((dofeel_code))) {
//            try {
//                String filePath = "";
//                if (dofeel_code.indexOf("_") != -1) {
//                    filePath = dofeel_code.replaceAll("_", File.separator);
//                }
//                InputStream is = getActivity().getAssets().open(filePath);
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//                ivBottomLeft.setImageBitmap(bitmap);
//            } catch (IOException e) {
//            }
//        }

        //跳转到view QRCode 页面
        rlViewQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentQRCode = new Intent(getActivity(), ViewQRCodeActivity.class);
                startActivity(intentQRCode);

                if (listener != null) {
                    listener.clickedOther(v.getId());
                }
            }
        });

        llViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyViewProfileActivity.class);
                intent.putExtra("profile_image_id",profileBackgroundId);
                startActivityForResult(intent, UPDATE_PROFILE);

                if (listener != null) {
                    listener.clickedOther(v.getId());
                }
            }
        });

        rlAlbumGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra("member_id", MainActivity.getUser().getUser_id());
                startActivity(intent);

                if (listener != null) {
                    listener.clickedOther(v.getId());
                }
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
                if (listener != null) {
                    listener.clickedDiary();
                }

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
                peData.setPhoto_caption(Constant.Module_Original);
                peData.setPhoto_multipe("false");
                datas.add(peData);
                intent.putExtra("is_data", true);
                intent.putExtra("datas", datas);
                startActivity(intent);

                if (listener != null) {
                    listener.clickedOther(v.getId());
                }
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
                    if (!TextUtils.isEmpty(data.getStringExtra("name"))) {
                        tvName1.setText(data.getStringExtra("name"));
                    }
                    Uri head_pic = data.getParcelableExtra("head_pic");
                    Uri background_pic = data.getParcelableExtra("background_pic");
                    if (head_pic != null) {
                        cniMain.setImageURI(head_pic);
                    }
                    if(background_pic != null){
                        String background_url = background_pic.toString();
                        if(background_url.contains("file://")){
                            imProfileImages.setImageBitmap(BitmapFactory.decodeFile(background_url.substring(background_url.indexOf("file://") + 7)));
                        }
                    }
                }

            default:
        }

    }

    //获取一个背景图的随机id
    public int randomImageId(int[] array){
        int result = new Random().nextInt(5);
        return array[result];
    }

    /**
     * 当前Fragment变化监听
     */
    public interface MeFragmentListener{
        /**
         * 点击了日记
         */
        void clickedDiary();

        /**
         * 点击了除日记以外的控件
         * @param resID 控件ID
         */
        void clickedOther(int resID);
    }

    private MeFragmentListener listener;

    public void setListener(MeFragmentListener listener) {
        this.listener = listener;
    }
}
