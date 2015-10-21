package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.FeelingAdapter;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.InviteMemberActivity;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.WallEditView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created 10/20/15.
 *
 * @author Jackie
 * @version 1.0
 *          <p/>
 *          日志编辑
 */
public class EditDiaryFragment extends BaseFragment<NewDiaryActivity> implements View.OnClickListener, FeelingAdapter.ItemCheckListener {

    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private static final String TAG = EditDiaryFragment.class.getSimpleName();

    private static final String POST_WALL = TAG + "_POST_WALL";
    private static final String UPLOAD_PIC = TAG + "_UPLOAD_PIC";

    public static final String PREFERENCE_NAME = "SAVE_DRAFT";
    public static final String PREFERENCE_KEY_IS_SAVE = "IS_SAVE";

    private static final String PREFERENCE_KEY_PIC_CONTENT = "PIC_CONTENT";
    private static final String PREFERENCE_KEY_PIC_COUNT = "PIC_COUNT";
    private static final String PREFERENCE_KEY_LOC_NAME = "LOC_NAME";
    private static final String PREFERENCE_KEY_LOC_LONGITUDE = "LOC_LONGITUDE";
    private static final String PREFERENCE_KEY_LOC_LATITUDE = "LOC_LATITUDE";
    private static final String PREFERENCE_KEY_DO_FEEL_CODE = "DO_FEEL_CODE";
    private static final String PREFERENCE_KEY_CHECK_ITEM_INDEX = "CHECK_ITEM_INDEX";
    private static final String PREFERENCE_KEY_CONTENT_GROUP_PUBLIC = "CONTENT_GROUP_PUBLIC";
    private static final String PREFERENCE_KEY_TAG_MEMBERS = "TAG_MEMBERS";
    private static final String PREFERENCE_KEY_TAG_GROUPS = "TAG_GROUPS";
    private static final String PREFERENCE_KEY_OLD_MEMBER_TEXT = "OLD_MEMBER_TEXT";
    private static final String PREFERENCE_KEY_OLD_GROUP_TEXT = "OLD_GROUP_TEXT";
    private static final String PREFERENCE_KEY_TEXT_CONTENT = "TEXT_CONTENT";
    private static final String PREFERENCE_KEY_VIDEO_PATH = "VIDEO_PATH";
    private static final String PREFERENCE_KEY_VIDEO_DURATION = "VIDEO_DURATION";

    private final static int GET_LOCATION = 1;
    private final static int GET_MEMBERS = 2;
    private final static int OPEN_GPS = 3;
    private final static int REQUEST_HEAD_PHOTO = 4;

    public final static String PATH_PREFIX = "feeling";
    private final static String FEEL_ICON_NAME = PATH_PREFIX + "/%s";

    /**
     * 保存草稿的首选项
     */
    public static SharedPreferences draftPreferences;

    public static EditDiaryFragment newInstance(String... params) {

        return createInstance(new EditDiaryFragment());
    }

    private double latitude;
    private double longitude;
    private Gson gson;

    private boolean allRange = false;

    /**
     * TAG的用户列表
     */
    public List<UserEntity> at_members_data = new ArrayList<>();
    /**
     * TAG的组群列表
     */
    public List<GroupEntity> at_groups_data = new ArrayList<>();

    private String loc_type;

    /**
     * 存放图片Uri列表
     */
    private List<Uri> imageUris = new ArrayList<>();

    /**
     * 视频Uri
     */
    private Uri videoUri;

    /**
     * 视频时长
     */
    private String duration;

    /**
     * 心情列表适配器
     */
    private FeelingAdapter feelingAdapter;

    /**
     * private CircularProgress progressBar;
     */
    private RecyclerView feeling_icons;

    /**
     * 心情选择弹出框
     */
    private PopupWindow popupwindow;

    /**
     * 心情布局
     */
    private LinearLayoutManager llmFeeling;

    /**
     * 心情图标名称列表
     */
    private List<String> fileNames = new ArrayList<>();

    /**
     * 选择的心情在列表中的下标
     */
    private int checkItemIndex = -1;

    /**
     * 选择心情的目录
     */
    private String selectFeelingPath;

    /**
     * Diary详情
     */
    private WallEditView wevContent;

    /**
     * 显示已经选择了的心情视图
     */
    private ImageView iv_feeling;

    /**
     * 地址描述
     */
    private TextView tvLocationDesc;

    private TextView tvPrivacy;

    /**
     * 视频或图片的布局
     */
    private RelativeLayout rlMediaDisplay;
    /**
     * 得到的视频显示在这个控件上
     */
    private ImageView ivDisplay;
    private View previewVideoView;
    private TextView tvDuration;

    private RecyclerView rvImages;
    /**
     * 加载框
     */
    private RelativeLayout rlProgress;

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_edit_diary;
    }

    @Override
    public void initView() {
        rlProgress = getViewById(R.id.rl_progress);
        wevContent = getViewById(R.id.diary_edit_content);

        iv_feeling = getViewById(R.id.iv_feeling);
        tvLocationDesc = getViewById(R.id.location_desc);
        tvPrivacy = getViewById(R.id.tv_privacy);
        rvImages = getViewById(R.id.rcv_post_photos);

        rlMediaDisplay = getViewById(R.id.media_display_rl);
        previewVideoView = LayoutInflater.from(getActivity()).inflate(R.layout.tab_picture_voide_view, null);
        tvDuration = (TextView) previewVideoView.findViewById(R.id.duration_tv);
        ivDisplay = (ImageView) previewVideoView.findViewById(R.id.video_view_iv);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        previewVideoView.setLayoutParams(params);

        // 删除视频
        previewVideoView.findViewById(R.id.delete_video_view).setOnClickListener(this);
        previewVideoView.findViewById(R.id.go_to_preview_video_iv).setOnClickListener(this);

        // 选择图片的点击监听
        getViewById(R.id.tv_camera).setOnClickListener(this);
        // 选择用户的点击监听
        getViewById(R.id.tv_tag).setOnClickListener(this);
        // 选择性情的点击监听
        getViewById(R.id.tv_feeling).setOnClickListener(this);
        // 选择位置的点击监听
        getViewById(R.id.tv_location).setOnClickListener(this);
        // 切换锁的点击监听
        getViewById(R.id.tv_private).setOnClickListener(this);

        if(!imageUris.isEmpty()) {
            clearVideo();
//            addDataAndNotify(imageUris);
        } else {
            if(!Uri.EMPTY.equals(videoUri)) {
                clearPhotos();
                ImageLoader.getInstance().displayImage(videoUri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
                tvDuration.setText(MyDateUtils.formatDuration(duration));
            }
        }
    }

    /**
     * 清空选择的视频和显示的UI
     */
    private void clearVideo() {
        videoUri = Uri.EMPTY;

        // 删除视频View
        if(rlMediaDisplay.indexOfChild(previewVideoView) >= 0) {
            rlMediaDisplay.removeView(previewVideoView);
        }

        // 添加图片显示View
        if(rlMediaDisplay.indexOfChild(rvImages) < 0) {
            rlMediaDisplay.addView(rvImages, 0);
        }
    }

    /**
     * 清除选择的图片数据和UI
     */
    private void clearPhotos() {
        // 删除图片显示View
        if(rlMediaDisplay.indexOfChild(rvImages) >= 0) {
            rlMediaDisplay.removeView(rvImages);
        }

        // 显示视频View
        if(rlMediaDisplay.indexOfChild(previewVideoView) < 0) {
            rlMediaDisplay.addView(previewVideoView, 0);
        }

        // 清册选择的图片
        imageUris.clear();
        // datas.clear();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        try {
            recoverDraft();
        } catch(Exception e) {
            draftPreferences.edit().clear().apply();
            e.printStackTrace();
        }
    }

    /**
     * 恢复草稿
     */
    private void recoverDraft() throws Exception {
        LogUtil.i(TAG, "recoverDraft");
        if(draftPreferences == null) {
            draftPreferences = getParentActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        if(!draftPreferences.getBoolean(PREFERENCE_KEY_IS_SAVE, false)) {
            return;
        }
        tvLocationDesc.setText(draftPreferences.getString(PREFERENCE_KEY_LOC_NAME, ""));
        longitude = draftPreferences.getFloat(PREFERENCE_KEY_LOC_LONGITUDE, (float) longitude);
        latitude = draftPreferences.getFloat(PREFERENCE_KEY_LOC_LATITUDE, (float) latitude);

        selectFeelingPath = draftPreferences.getString(PREFERENCE_KEY_DO_FEEL_CODE, selectFeelingPath);
        if(!TextUtils.isEmpty(selectFeelingPath)) {
            checkItemIndex = draftPreferences.getInt(PREFERENCE_KEY_CHECK_ITEM_INDEX, checkItemIndex);
            iv_feeling.setVisibility(View.VISIBLE);
            try {
                iv_feeling.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(selectFeelingPath)));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        allRange = draftPreferences.getBoolean(PREFERENCE_KEY_CONTENT_GROUP_PUBLIC, allRange);
        Resources resources = getResources();
        Drawable[] drawables = tvPrivacy.getCompoundDrawables();
        if(allRange) {
            tvPrivacy.setCompoundDrawables(drawables[0], resources.getDrawable(R.drawable.privacy_open), drawables[2], drawables[3]);
            tvPrivacy.setText(R.string.text_all);
        } else {
            tvPrivacy.setCompoundDrawables(drawables[0], resources.getDrawable(R.drawable.privacy_lock), drawables[2], drawables[3]);
            tvPrivacy.setText(R.string.text_private);
        }

        String members = draftPreferences.getString(PREFERENCE_KEY_TAG_MEMBERS, "");
        at_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
        }.getType());
        String groups = draftPreferences.getString(PREFERENCE_KEY_TAG_GROUPS, "");
        at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {
        }.getType());

        wevContent.setOldMemberText(draftPreferences.getString(PREFERENCE_KEY_OLD_MEMBER_TEXT, wevContent.getOldMemberText()));
        wevContent.setOldGroupText(draftPreferences.getString(PREFERENCE_KEY_OLD_GROUP_TEXT, wevContent.getOldGroupText()));
        wevContent.setText(draftPreferences.getString(PREFERENCE_KEY_TEXT_CONTENT, wevContent.getRelText()));
        changeAtDesc(false);

        int picCount = draftPreferences.getInt(PREFERENCE_KEY_PIC_COUNT, 0);
        if(picCount > 0) {
            imageUris = new ArrayList<>();
            for(int i = 0; i < picCount; i++) {
                String strUri = draftPreferences.getString(PREFERENCE_KEY_PIC_CONTENT + i, "");
                LogUtil.i(TAG, "recoverDraft& uri: " + strUri);
                if(!TextUtils.isEmpty(strUri)) {
                    Uri uri = Uri.parse(strUri);
                    imageUris.add(uri);
                }
            }
        } else {
            String videoUriStr = draftPreferences.getString(PREFERENCE_KEY_VIDEO_PATH, "");

            Log.i(TAG, "recoverDraft& videoUri: " + videoUriStr);
            if(!TextUtils.isEmpty(videoUriStr)) {
                videoUri = Uri.parse(videoUriStr);
                duration = draftPreferences.getString(PREFERENCE_KEY_VIDEO_DURATION, "");
//                fragment2.setVideoUri(videoUri);
//                fragment2.setVideoDuration(duration);
            }
        }

        // 恢复了草稿，清除保存
        draftPreferences.edit().clear().apply();
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onItemCheckedChange(int position) {
        checkItemIndex = position;
        selectFeelingPath = String.format(FEEL_ICON_NAME, fileNames.get(checkItemIndex));
        iv_feeling.setVisibility(View.VISIBLE);
        try {
            iv_feeling.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(selectFeelingPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        popupwindow.dismiss();
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();

        if(popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        }
    }

    /**
     * 保存草稿
     */
    private void saveDraft() {
        LogUtil.i(TAG, "saveDraft");
        SharedPreferences.Editor editor = draftPreferences.edit();
        if(!imageUris.isEmpty()) {
            int i = 0;
            for(Uri uri : imageUris) {
                editor.putString(PREFERENCE_KEY_PIC_CONTENT + i++, uri.toString());
                LogUtil.i(TAG, "saveDraft& " + PREFERENCE_KEY_PIC_CONTENT + ": " + uri.toString());
            }
            editor.putInt(PREFERENCE_KEY_PIC_COUNT, i);
        } else if(!Uri.EMPTY.equals(videoUri)) {
            editor.putString(PREFERENCE_KEY_VIDEO_PATH, videoUri.toString());
            editor.putString(PREFERENCE_KEY_VIDEO_DURATION, duration);
        }

        editor.putString(PREFERENCE_KEY_LOC_NAME, tvLocationDesc.getText().toString());
        editor.putFloat(PREFERENCE_KEY_LOC_LONGITUDE, (float) longitude);
        editor.putFloat(PREFERENCE_KEY_LOC_LATITUDE, (float) latitude);

        editor.putString(PREFERENCE_KEY_DO_FEEL_CODE, selectFeelingPath);
        editor.putInt(PREFERENCE_KEY_CHECK_ITEM_INDEX, checkItemIndex);
        editor.putBoolean(PREFERENCE_KEY_CONTENT_GROUP_PUBLIC, allRange);

        editor.putString(PREFERENCE_KEY_TAG_MEMBERS, gson.toJson(at_members_data));
        editor.putString(PREFERENCE_KEY_TAG_GROUPS, gson.toJson(at_groups_data));

        editor.putString(PREFERENCE_KEY_TEXT_CONTENT, wevContent.getText().toString());
        editor.putString(PREFERENCE_KEY_OLD_MEMBER_TEXT, wevContent.getOldMemberText());
        editor.putString(PREFERENCE_KEY_OLD_GROUP_TEXT, wevContent.getOldGroupText());

        editor.putBoolean(PREFERENCE_KEY_IS_SAVE, true);

        editor.apply();
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult");

        // 没有退出编辑不用保存蓝草稿
        if(draftPreferences != null) {
            draftPreferences.edit().putBoolean(PREFERENCE_KEY_IS_SAVE, false).apply();
        }

        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case GET_LOCATION:
                    if(data != null) {
                        String locationName = data.getStringExtra(Constant.EXTRA_LOCATION_NAME);
                        if(!TextUtils.isEmpty(locationName)) {
                            tvLocationDesc.setText(locationName);
                            latitude = data.getDoubleExtra(Constant.EXTRA_LATITUDE, 0);
                            longitude = data.getDoubleExtra(Constant.EXTRA_LONGITUDE, 0);
                        } else {
                            tvLocationDesc.setText(null);
                            latitude = -1000;
                            longitude = -1000;
                        }
                        //                        }
                        //坐标数据类型
                        loc_type = data.getStringExtra("loc_type");
                    }
                    break;
                case GET_MEMBERS:
                    String members = data.getStringExtra("members_data");
                    at_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {}.getType());
                    LogUtil.i(TAG, "onActivityResult: size = " + at_members_data.size());
                    String groups = data.getStringExtra("groups_data");
                    at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {}.getType());
                    changeAtDesc(true);
                    break;

                case OPEN_GPS:
                    openMap();
                    break;
            }
        }

        if(requestCode == OPEN_GPS && LocationUtil.isOPen(getActivity())) {
            openMap();
        }
    }


    public void changeAtDesc(boolean checkVisible) {

        String memberText;
        String groupText;
        if(at_members_data != null && at_members_data.size() > 0) {
            memberText = String.format(getParentActivity().getString(R.string.text_diary_content_at_member_desc), at_members_data.size());
            Log.i(TAG, "changeAtDesc& member of at description is " + memberText);
        } else {
            Log.i(TAG, "changeAtDesc& no member of at description");
            memberText = "";
        }
        if(at_groups_data != null && at_groups_data.size() > 0) {
            groupText = String.format(getParentActivity().getString(R.string.text_diary_content_at_group_desc), at_groups_data.size());
            Log.i(TAG, "changeAtDesc& group of at description is " + groupText);
        } else {
            Log.i(TAG, "changeAtDesc& no group of at description");
            groupText = "";
        }
        if(!checkVisible) {
            wevContent.addAtDesc(memberText, groupText, true);
        } else {
            wevContent.addAtDesc(memberText, groupText, isVisible());
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                openPhotos();
                break;
            case R.id.tv_tag:
                goChooseMembers();
                break;
            case R.id.tv_feeling:
                UIUtil.hideKeyboard(getParentActivity(), wevContent);
                InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(wevContent.getWindowToken(), 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showChooseFeeling();
                        }
                    }, 100);
                } else {
                    showChooseFeeling();
                }
                break;
            case R.id.tv_location:
                checkGPS();
                break;
            case R.id.tv_private:
                break;
        }
    }

    /**
     * 打开相册，选择图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPhotos() {

        Intent intent = new Intent(getParentActivity(), SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        /**
         * 使用了Universal Image Loader库来处理图片需要返回的Uri与传统有差异，传此值用于区分
         */
        intent.putExtra(MediaData.EXTRA_USE_UNIVERSAL, true);
        intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, true);
        intent.putParcelableArrayListExtra(SelectPhotosActivity.EXTRA_SELECTED_PHOTOS, (ArrayList<? extends Parcelable>) imageUris);
//        intent.putExtra(SelectPhotosActivity.EXTRA_RESIDUE, residue);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_HEAD_PHOTO);
    }

    /**
     * 跳至选择TAG用户
     */
    private void goChooseMembers() {
        Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
        intent.putExtra("members_data", gson.toJson(at_members_data));
        intent.putExtra("groups_data", gson.toJson(at_groups_data));
        intent.putExtra("type", 0);
        startActivityForResult(intent, GET_MEMBERS);
    }

    private void showChooseFeeling() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        } else {
            initPopupWindowView();
            popupwindow.showAsDropDown(getViewById(R.id.option_bar), 0, 5);
        }
    }

    public void initPopupWindowView() {
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
        llmFeeling = new LinearLayoutManager(getParentActivity());
        feeling_icons.setLayoutManager(llmFeeling);
        fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), PATH_PREFIX);
        // 对文件进行按首字的大小排序
        SortComparator compartor = new SortComparator();
        Collections.sort(fileNames, compartor);
        List<String> filePaths = new ArrayList<>();
        if (fileNames != null) {
            for (String name : fileNames) {
                filePaths.add(PATH_PREFIX + "/" + name);
            }
        }
        feelingAdapter = new FeelingAdapter(getActivity(), filePaths);
        feelingAdapter.setCheckIndex(checkItemIndex);
        feeling_icons.setAdapter(feelingAdapter);

        feelingAdapter.setItemCheckListener(this);

    }

    /**
     * 打開地圖获取地理位置
     */
    private void checkGPS() {
        if (!LocationUtil.isOPen(getActivity())) {
            LogUtil.i(TAG, "onClick& need open GPS");
            final MyDialog myDialog = new MyDialog(getActivity(), R.string.open_gps_title, R.string.use_gps_hint);
            myDialog.setButtonAccept(R.string.text_dialog_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, OPEN_GPS);
                    myDialog.dismiss();
                }
            });
            myDialog.setButtonCancel(R.string.text_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.show();
        } else {
            LogUtil.i(TAG, "onClick& GPS opened");
            openMap();
        }
    }

    private void openMap() {
        Intent intent = LocationUtil.getPlacePickerIntent(getActivity(), latitude, longitude, tvLocationDesc.getText().toString());
        if(intent != null) {
            startActivityForResult(intent, GET_LOCATION);
        }
    }

    /**
     * 对字符串按首字母排序按首字母排序，并忽略大小字的排序规则
     */
    class SortComparator implements Comparator<String> {

        /**
         * Compares the two specified objects to determine their relative ordering. The ordering
         * implied by the return value of this method for all possible pairs of
         * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
         * This means that
         * <ul>
         * <li>{@code compare(a,a)} returns zero for all {@code a}</li>
         * <li>the sign of {@code compare(a,b)} must be the opposite of the sign of {@code
         * compare(b,a)} for all pairs of (a,b)</li>
         * <li>From {@code compare(a,b) > 0} and {@code compare(b,c) > 0} it must
         * follow {@code compare(a,c) > 0} for all possible combinations of {@code
         * (a,b,c)}</li>
         * </ul>
         *
         * @param lhs an {@code Object}.
         * @param rhs a second {@code Object} to compare with {@code lhs}.
         * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
         * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
         * @throws ClassCastException if objects are not of the correct type.
         */
        @Override
        public int compare(String lhs, String rhs) {
            String str1 = lhs.substring(0, 1).toUpperCase();
            String str2 = rhs.substring(0, 1).toUpperCase();
            return str1.compareTo(str2);
        }
    }
}
