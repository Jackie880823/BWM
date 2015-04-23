package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.FeelingAdapter;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.animation.ViewHelper;
import com.madx.bwm.widget.WallEditView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.WallNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.WallNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallNewFragment extends BaseFragment<WallNewActivity> implements View.OnClickListener, FeelingAdapter.ItemCheckListener {

    private ImageView ivCursor;
    private ImageView iv_feeling;
    private ImageButton btn_feeling;
    private ImageButton btn_notify;
    //    private TextView btn_share_option;
    private Button btn_share_option;
    private LinearLayout btn_submit;
    private LinearLayout btn_location;
    private TextView location_desc;

    private final static int GET_LOCATION = 1;
    private final static int GET_MEMBERS = 2;
    private List<String> fileNames = new ArrayList<>();
    private final static String PATH_PREFIX = "feeling";
    private final static String FEEL_ICON_NAME = PATH_PREFIX + "/%s";


    public List<UserEntity> at_members_data = new ArrayList();
    public List<GroupEntity> at_gourps_data = new ArrayList();
    private String text_content;
    private List<Uri> pic_content;
    private String locationName;

    private double latitude;
    private double longitude;
    private Gson gson;
    //    private ProgressBarCircularIndeterminate progressBar;
    private RecyclerView feeling_icons;

    PopupWindow popupwindow;
    private LinearLayoutManager llm;

    public static WallNewFragment newInstance(String... params) {

        return createInstance(new WallNewFragment());
    }

    public WallNewFragment() {
        super();
        // Required empty public constructor
    }

    private FeelingAdapter feelingAdapter;

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall_new;
    }

    @Override
    public void initView() {
        //        progressBar = getViewById(R.id.progressBar);

        gson = new Gson();
        getViewById(R.id.tv_tab_word).setOnClickListener(this);
        getViewById(R.id.tv_tab_picture).setOnClickListener(this);
        ivCursor = getViewById(R.id.cursor);

        fragment1 = new TabWordFragment();

        fragment2 = new TabPictureFragment();

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wall_new_container, fragment1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        iv_feeling = getViewById(R.id.iv_feeling);
        btn_feeling = getViewById(R.id.btn_feeling);
        btn_notify = getViewById(R.id.btn_notify);
        btn_share_option = getViewById(R.id.btn_share_option);
        btn_location = getViewById(R.id.btn_location);
        btn_submit = getViewById(R.id.btn_submit);
        location_desc = getViewById(R.id.location_desc);

        btn_feeling.setOnClickListener(this);
        btn_notify.setOnClickListener(this);
        btn_share_option.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_location.setOnClickListener(this);

    }


    FragmentManager fragmentManager;
    TabWordFragment fragment1;
    TabPictureFragment fragment2;
    int currentTabIndex;
    TranslateAnimation translateAnimation1;
    TranslateAnimation translateAnimation2;

    @Override
    public void requestData() {

        //        uploadImages();
    }


    private boolean allRange = true;

    @Override
    public void onClick(View v) {


        switch(v.getId()) {
            case R.id.tv_tab_word:
                changeTab(0);
                break;
            case R.id.tv_tab_picture:
                changeTab(1);
                break;
            case R.id.btn_feeling:
                showChooseFeeling();
                break;
            case R.id.btn_location:
                goLocationSetting();
                break;
            case R.id.btn_notify:
                goChooseMembers();
                break;
            case R.id.btn_share_option:
                if(allRange) {
                    allRange = false;
                    // btn_share_option.setImageResource(R.drawable.post_rang_private);
                    btn_share_option.setBackgroundResource(R.drawable.post_rang_private);
                    btn_share_option.setText(R.string.text_private);
                } else {
                    allRange = true;
                    // btn_share_option.setImageResource(R.drawable.post_rang_all);
                    btn_share_option.setBackgroundResource(R.drawable.post_rang_all);
                    btn_share_option.setText(R.string.text_all);
                }

                break;
            case R.id.btn_submit:
                submitWall();
                break;
        }

    }

    boolean hasPicContent;
    boolean hasTextContent;
    ProgressDialog progressDialog;

    String locationDesc;
    String latitudeDesc;
    String longitudeDesc;

    private void submitWall() {
        hasTextContent = false;
        hasPicContent = false;
        if(fragment1 != null) {
            WallEditView editText = fragment1.getEditText4Content();
            text_content = editText.getRelText();
            if(TextUtils.isEmpty(text_content)) {
                hasTextContent = false;
            } else {
                hasTextContent = true;
            }
        }
        if(fragment2 != null) {
            pic_content = fragment2.getEditPic4Content();
            if(pic_content == null || pic_content.size() == 0) {
                hasPicContent = false;
            } else {
                hasPicContent = true;
            }
        }

        if(!hasTextContent && !hasPicContent) {
            MessageUtil.showMessage(getActivity(), R.string.msg_no_content);
            return;
        }

        if(!TextUtils.isEmpty(location_desc.getText())) {
            locationDesc = location_desc.getText().toString();
            latitudeDesc = "" + latitude;
            longitudeDesc = "" + longitude;
        } else {
            locationDesc = "";
            latitudeDesc = "";
            longitudeDesc = "";
        }

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("content_type", "post");
        params.put("text_description", text_content);
        params.put("loc_latitude", latitudeDesc);
        params.put("loc_longitude", longitudeDesc);
        params.put("locationName", location_desc.getText().toString());
        params.put("loc_caption", "");
        params.put("sticker_group_path", "");


        try {
            StringBuilder b = new StringBuilder(selectFeelingPath);
            int charIndex = selectFeelingPath.lastIndexOf("/");
            b.replace(charIndex, charIndex + 1, "_");
            params.put("dofeel_code", b.toString());
        } catch(Exception e) {
        }
        params.put("sticker_type", "");
        params.put("group_ind_type", "2");
        params.put("content_group_public", (allRange ? "1" : "0"));

        if(pic_content == null || pic_content.size() == 0) {
            params.put("upload_photo", "0");
        } else {
            params.put("upload_photo", "1");
        }
        params.put("tag_group", null);
        //        params.put("tag_group", gson.toJson(setGetMembersIds(at_gourps_data)));
        params.put("tag_member", gson.toJson(setGetMembersIds(at_members_data)));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.params = params;
        requestInfo.url = Constant.API_WALL_TEXT_POST;
        new HttpTools(getActivity()).post(requestInfo, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if("1".equals(obj.getString("resultStatus")) && !TextUtils.isEmpty(obj.getString("contentID"))) {
                        String contentId = obj.getString("contentID");
                        if(!hasPicContent) {
                            mHandler.sendEmptyMessage(ACTION_SUCCESSED);
                        } else {
                            int count = pic_content.size();
                            boolean multiple = (count > 0 ? false : true);
                            for(int index = 0; index < count; index++) {
                                if(index == count - 1) {
                                    new CompressBitmapTask(contentId, index, multiple, true).execute(pic_content.get(index));
                                } else {
                                    new CompressBitmapTask(contentId, index, multiple, false).execute(pic_content.get(index));
                                }
                            }
                        }

                    } else {
                        mHandler.sendEmptyMessage(ACTION_FAILED);
                    }
                } catch(Throwable t) {
                    t.printStackTrace();
                    mHandler.sendEmptyMessage(ACTION_FAILED);
                }
            }

            @Override
            public void onError(Exception e) {
                mHandler.sendEmptyMessage(ACTION_FAILED);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private static final int SHOW_PROGRESS = 11;
    private static final int HIDE_PROGRESS = 12;
    private static final int ACTION_FAILED = 13;
    private static final int ACTION_SUCCESSED = 14;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ACTION_FAILED:
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    sendEmptyMessage(HIDE_PROGRESS);
                    break;
                case ACTION_SUCCESSED:
                    getParentActivity().setResult(Activity.RESULT_OK);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                    sendEmptyMessage(HIDE_PROGRESS);
                    getActivity().finish();
                    break;
                case SHOW_PROGRESS:
                    if(progressDialog == null) {
                        progressDialog = new ProgressDialog(getActivity(), R.string.text_uploading);
                    }
                    progressDialog.show();
                    break;
                case HIDE_PROGRESS:
                    if(progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    private void changeTab(final int tabIndex) {
        if(currentTabIndex == tabIndex) {
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment fragment = null;
        if(tabIndex == 0) {
            fragment = fragment1;

            if(translateAnimation1 == null) {
                translateAnimation1 = new TranslateAnimation(ivCursor.getWidth(), 0, ViewHelper.getY(ivCursor), ViewHelper.getY(ivCursor));
                translateAnimation1.setFillAfter(true);
                translateAnimation1.setDuration(300);
            }
            ivCursor.startAnimation(translateAnimation1);
        } else {
            fragment = fragment2;
            if(translateAnimation2 == null) {
                translateAnimation2 = new TranslateAnimation(0, ivCursor.getWidth(), ViewHelper.getY(ivCursor), ViewHelper.getY(ivCursor));
                translateAnimation2.setFillAfter(true);
                translateAnimation2.setDuration(300);
            }
            ivCursor.startAnimation(translateAnimation2);
        }
        fragmentTransaction.replace(R.id.wall_new_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
        currentTabIndex = tabIndex;
    }

    private int checkItemIndex = -1;
    private String selectFeelingPath;

    @Override
    public void onItemCheckedChange(int position) {
        checkItemIndex = position;
        selectFeelingPath = String.format(FEEL_ICON_NAME, fileNames.get(checkItemIndex));
        iv_feeling.setVisibility(View.VISIBLE);
        try {
            iv_feeling.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(selectFeelingPath)));
        } catch(IOException e) {
            e.printStackTrace();
        }
        popupwindow.dismiss();

    }

    class CompressBitmapTask extends AsyncTask<Uri, Void, String> {

        String contentId;
        int index;
        boolean multiple;
        boolean lastPic;

        public CompressBitmapTask(String contentId, int index, boolean multiple, final boolean lastPic) {
            this.contentId = contentId;
            this.index = index;
            this.multiple = multiple;
            this.lastPic = lastPic;
        }

        @Override
        protected String doInBackground(Uri... params) {
            if(params == null)
                return null;
            return LocalImageLoader.compressBitmap(getActivity(), FileUtil.getRealPathFromURI(getActivity(), params[0]), 480, 800, false);
        }

        @Override
        protected void onPostExecute(String path) {
            submitPic(path, contentId, index, multiple, lastPic);
        }
    }

    private void submitPic(String path, String contentId, int index, boolean multiple, final boolean lastPic) {
        File f = new File(path);
        if(!f.exists()) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("content_id", contentId);
        params.put("photo_index", "" + index);
        params.put("photo_caption", "");
        params.put("file", f);
        params.put("multiple", multiple ? "1" : "0");


        new HttpTools(getActivity()).upload(Constant.API_WALL_PIC_POST, params, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                if(lastPic) {
                    mHandler.sendEmptyMessage(ACTION_SUCCESSED);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                if(lastPic) {
                    mHandler.sendEmptyMessage(ACTION_FAILED);
                }
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    public void setLocation(String locatonName, LatLng latLng) {

    }

    private void goLocationSetting() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        //        intent.putExtra("has_location", position_name.getText().toString());
        if(!TextUtils.isEmpty(location_desc.getText())) {
            intent.putExtra("location_name", location_desc.getText().toString());
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }
        startActivityForResult(intent, GET_LOCATION);
    }

    private void goChooseMembers() {
        Intent intent = new Intent(getActivity(), SelectPeopleActivity.class);
        intent.putExtra("members_data", gson.toJson(at_members_data));
        intent.putExtra("type", 0);
        startActivityForResult(intent, GET_MEMBERS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK) {
            switch(requestCode) {
                case GET_LOCATION:
                    if(data != null) {
                        //        intent.putExtra("has_location", position_name.getText().toString());
                        locationName = data.getStringExtra("location_name");
                        if(!TextUtils.isEmpty(locationName)) {
                            location_desc.setText(locationName);
                            latitude = data.getDoubleExtra("latitude", 0);
                            longitude = data.getDoubleExtra("longitude", 0);
                        }
                    }
                    break;
                case GET_MEMBERS:
                    String members = data.getStringExtra("members_data");
                    at_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {}.getType());
                    changeAtDesc();
                    break;
            }
        }
    }

    void changeAtDesc() {
        if(fragment1 != null && at_members_data != null && at_members_data.size() > 0) {
            WallEditView editText = fragment1.getEditText4Content();
            editText.addAtDesc("@ " + at_members_data.size() + "members");
        }
    }

    private void showChooseFeeling() {
        if(popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            return;
        } else {
            initmPopupWindowView();
            popupwindow.showAsDropDown(getViewById(R.id.option_bar), 0, 5);
        }
    }

    public void initmPopupWindowView() {
        // // 获取自定义布局文件pop.xml的视图
        View customView = getActivity().getLayoutInflater().inflate(R.layout.feeling_list, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]

        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //                if (popupwindow != null && popupwindow.isShowing()) {
                //                    popupwindow.dismiss();
                //                    popupwindow = null;
                //                }

                return false;
            }
        });

        RecyclerView feeling_icons = (RecyclerView) customView.findViewById(R.id.feeling_icons);
        llm = new LinearLayoutManager(getParentActivity());
        feeling_icons.setLayoutManager(llm);
        fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), PATH_PREFIX);
        List<String> filePaths = new ArrayList<>();
        if(fileNames != null) {
            for(String name : fileNames) {
                filePaths.add(PATH_PREFIX + "/" + name);
            }
        }
        feelingAdapter = new FeelingAdapter(getActivity(), filePaths);
        feelingAdapter.setCheckIndex(checkItemIndex);
        feeling_icons.setAdapter(feelingAdapter);

        feelingAdapter.setItemCheckListener(this);

    }

    private List<String> setGetMembersIds(List<UserEntity> users) {
        List<String> ids = new ArrayList<>();
        if(users != null) {
            int count = users.size();
            for(int i = 0; i < count; i++) {
                ids.add(users.get(i).getUser_id());
            }
        }
        return ids;
    }


}
