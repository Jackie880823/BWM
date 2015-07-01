package com.bondwithme.BondWithMe.ui.wall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.FeelingAdapter;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.InviteMemberActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.util.animation.ViewHelper;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.WallEditView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WallNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WallNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallNewFragment extends BaseFragment<WallNewActivity> implements View.OnClickListener, FeelingAdapter.ItemCheckListener {

    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private static final String TAG = WallNewFragment.class.getSimpleName();

    private static final String POST_WALL = TAG + "_POST_WALL";
    private static final String UPLOAD_PIC = TAG + "_UPLOAD_PIC";

    public static final String PREFERENCE_NAME = "SAVE_DRAFT";
    public static final String PREFERENCE_KEY_IS_SAVE = "IS_SAVE";

    private static final String PREFERENCE_KEY_PIC_CONTENT = "PIC_CONTENT";
    private static final String PREFERENCE_KEY_PIC_COUNT = "PIC_COUNT";
    private static final String PREFERENCE_KEY_PIC_VIEW_WIDTH = "PIC_VIEW_WIDTH";
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

    /**
     * 输入文字的TAB
     */
    private final static int WALL_TAB_WORD = 0;
    /**
     * 插入图片的TAB
     */
    private final static int WALL_TAB_PICTURE = 1;

    private final static int GET_LOCATION = 1;
    private final static int GET_MEMBERS = 2;

    public final static String PATH_PREFIX = "feeling";
    private final static String FEEL_ICON_NAME = PATH_PREFIX + "/%s";

    private ImageView ivCursor;
    private ImageView iv_feeling;
    private ImageButton btn_feeling;
    private ImageButton btn_notify;
    //    private TextView btn_share_option;
    private Button btn_share_option;
    private LinearLayout btn_submit;
    private LinearLayout btn_location;
    private TextView location_desc;
    // 加载框
    private RelativeLayout rlProgress;

    private List<String> fileNames = new ArrayList<>();

    public List<UserEntity> at_members_data = new ArrayList();
    public List<GroupEntity> at_groups_data = new ArrayList();
    private String text_content;
    private String locationName;
    private List<Uri> pic_content;
    private List<CompressBitmapTask> tasks;

    private double latitude;
    private double longitude;
    private Gson gson;

    public static SharedPreferences draftPreferences;

    /**
     * private ProgressBarCircularIndeterminate progressBar;
     */
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
        rlProgress = getViewById(R.id.rl_progress);
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

        iv_feeling.setOnClickListener(this);
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

    MyDialog myDialog;

    /**
     * 返回检测，如正在上传或有数据返回为true，表示不能禁止返回
     *
     * @return
     */
    public boolean backCheck() {
        if(popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            return true;
        } else if(rlProgress != null && rlProgress.getVisibility() == View.VISIBLE) {
            MessageUtil.showMessage(App.getContextInstance(), R.string.waiting_upload);
            return true;
        }
        if(tasks != null && tasks.size() > 0) {
            // 图片上传务正在执行
            Log.i(TAG, "backCheck& tasks size: " + tasks.size());
            return true;
        } else {
            hasTextContent = false;
            hasPicContent = false;
            if(fragment1 != null) {
                WallEditView editText = fragment1.getEditText4Content();
                text_content = editText.getRelText();
                if(TextUtils.isEmpty(text_content.trim())) {
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
            Log.i(TAG, "backCheck& hasTextContent: " + hasTextContent + "; hasPicContent: " + hasPicContent);
            SharedPreferences.Editor editor = draftPreferences.edit();
            if(!hasTextContent && !hasPicContent) {
                // 没有需要上传的内容
                editor.clear().commit();
                return false;
            }

            // 提示是否将内容保存到草稿
            if(myDialog == null) {
                myDialog = new MyDialog(getActivity(), "", getActivity().getString(R.string.text_dialog_save_draft));
                myDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveDraft();
                        myDialog.dismiss();
                        getActivity().finish();
                    }
                });
                myDialog.setButtonCancel(R.string.text_dialog_no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        getActivity().finish();
                    }
                });
            }
            if(!myDialog.isShowing()) {
                myDialog.show();
            }
            return true;
        }
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
        Log.i(TAG, "onResume");

        // 监听地址更新
        // LocationUtil.setRequestLocationUpdates(getActivity());

        try {
            recoverDraft();
        } catch(Exception e) {
            draftPreferences.edit().clear().commit();
            e.printStackTrace();
        }
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();

        // 取消地址监听
        // LocationUtil.removerLocationListener();

        if(myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }

        if(popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        }
    }

    /**
     * 保存草稿
     */
    private void saveDraft() {
        Log.i(TAG, "saveDraft");
        SharedPreferences.Editor editor = draftPreferences.edit();
        if(hasPicContent) {
            int i = 0;
            for(Uri uri : pic_content) {
                editor.putString(PREFERENCE_KEY_PIC_CONTENT + i++, uri.toString());
                Log.i(TAG, "saveDraft& " + PREFERENCE_KEY_PIC_CONTENT + ": " + uri.toString());
            }
            editor.putInt(PREFERENCE_KEY_PIC_COUNT, i);
            editor.putInt(PREFERENCE_KEY_PIC_VIEW_WIDTH, fragment2.getColumnWidthHeight());
        }

        editor.putString(PREFERENCE_KEY_LOC_NAME, location_desc.getText().toString());
        editor.putFloat(PREFERENCE_KEY_LOC_LONGITUDE, (float) longitude);
        editor.putFloat(PREFERENCE_KEY_LOC_LATITUDE, (float) latitude);

        editor.putString(PREFERENCE_KEY_DO_FEEL_CODE, selectFeelingPath);
        editor.putInt(PREFERENCE_KEY_CHECK_ITEM_INDEX, checkItemIndex);
        editor.putBoolean(PREFERENCE_KEY_CONTENT_GROUP_PUBLIC, allRange);

        editor.putString(PREFERENCE_KEY_TAG_MEMBERS, gson.toJson(at_members_data));
        editor.putString(PREFERENCE_KEY_TAG_GROUPS, gson.toJson(at_groups_data));

        WallEditView editView = fragment1.getEditText4Content();
        editor.putString(PREFERENCE_KEY_TEXT_CONTENT, editView.getRelText());
        editor.putString(PREFERENCE_KEY_OLD_MEMBER_TEXT, editView.getOldMemberText());
        editor.putString(PREFERENCE_KEY_OLD_GROUP_TEXT, editView.getOldGroupText());

        editor.putBoolean(PREFERENCE_KEY_IS_SAVE, true);

        editor.commit();
    }

    /**
     * 恢复草稿
     */
    private void recoverDraft() throws Exception {
        Log.i(TAG, "recoverDraft");
        if(draftPreferences == null) {
            draftPreferences = getParentActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        if(!draftPreferences.getBoolean(PREFERENCE_KEY_IS_SAVE, false)) {
            return;
        }
        location_desc.setText(draftPreferences.getString(PREFERENCE_KEY_LOC_NAME, ""));
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
        if(allRange) {
            btn_share_option.setBackgroundResource(R.drawable.post_rang_all);
            btn_share_option.setText(R.string.text_all);
        } else {
            btn_share_option.setBackgroundResource(R.drawable.post_rang_private);
            btn_share_option.setText(R.string.text_private);
        }

        String members = draftPreferences.getString(PREFERENCE_KEY_TAG_MEMBERS, "");
        at_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {}.getType());
        String groups = draftPreferences.getString(PREFERENCE_KEY_TAG_GROUPS, "");
        at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {}.getType());

        WallEditView editView = fragment1.getEditText4Content();
        editView.setOldMemberText(draftPreferences.getString(PREFERENCE_KEY_OLD_MEMBER_TEXT, editView.getOldMemberText()));
        editView.setOldGroupText(draftPreferences.getString(PREFERENCE_KEY_OLD_GROUP_TEXT, editView.getOldGroupText()));
        editView.setText(draftPreferences.getString(PREFERENCE_KEY_TEXT_CONTENT, editView.getRelText()));
        changeAtDesc(false);

        int picCount = draftPreferences.getInt(PREFERENCE_KEY_PIC_COUNT, 0);
        if(picCount > 0) {
            pic_content = new ArrayList<>();
            for(int i = 0; i < picCount; i++) {
                String strUri = draftPreferences.getString(PREFERENCE_KEY_PIC_CONTENT + i, "");
                Log.i(TAG, "recoverDraft& uri: " + strUri);
                if(!TextUtils.isEmpty(strUri)) {
                    Uri uri = Uri.parse(strUri);
                    pic_content.add(uri);
                }
            }
            fragment2.setColumnWidthHeight(draftPreferences.getInt(PREFERENCE_KEY_PIC_VIEW_WIDTH, 0));
            fragment2.setEditPicContent(pic_content);
            pic_content = new ArrayList<>();
        }

        draftPreferences.edit().putBoolean(PREFERENCE_KEY_IS_SAVE, false).commit();
    }

    private boolean allRange = false;

    @Override
    public void onClick(View v) {


        switch(v.getId()) {
            case R.id.tv_tab_word:
                changeTab(WALL_TAB_WORD);
                break;
            case R.id.tv_tab_picture:
                changeTab(WALL_TAB_PICTURE);
                break;
            case R.id.btn_feeling:
            case R.id.iv_feeling:
                UIUtil.hideKeyboard(getParentActivity(), fragment1.getEditText4Content());
                InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()) {
                    imm.hideSoftInputFromWindow(fragment1.getEditText4Content().getWindowToken(), 0);
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
                UIUtil.hideKeyboard(getParentActivity(), fragment1.getEditText4Content());
                submitWall();
                break;
        }

    }

    boolean hasPicContent;
    boolean hasTextContent;
    String loc_type;

    private void submitWall() {
        hasTextContent = false;
        hasPicContent = false;
        if(fragment1 != null) {
            WallEditView editText = fragment1.getEditText4Content();
            text_content = editText.getRelText();
            if(TextUtils.isEmpty(text_content.trim())) {
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

        String locationDesc = location_desc.getText().toString();
        String latitudeDesc;
        String longitudeDesc;

        if(!TextUtils.isEmpty(locationDesc)) {
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
        params.put("loc_name", locationDesc);
        params.put("loc_caption", "");
        params.put("sticker_group_path", "");
        params.put("loc_type", loc_type);


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

        params.put("tag_group", gson.toJson(setGetGroupIds(at_groups_data)));
        params.put("tag_member", gson.toJson(setGetMembersIds(at_members_data)));

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.params = params;
        requestInfo.url = Constant.API_WALL_TEXT_POST;
        new HttpTools(App.getContextInstance()).post(requestInfo, POST_WALL, new HttpCallback() {
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
                            mHandler.sendEmptyMessage(ACTION_SUCCEED);
                        } else {
                            int count = pic_content.size();
                            boolean multiple = (count > 0 ? false : true);
                            tasks = new ArrayList<>();
                            for(int index = 0; index < count; index++) {

                                if(index == count - 1) {
                                    CompressBitmapTask task = new CompressBitmapTask(contentId, index, multiple, true);
                                    tasks.add(task);
                                    task.execute(pic_content.get(index));
                                } else {
                                    CompressBitmapTask task = new CompressBitmapTask(contentId, index, multiple, false);
                                    tasks.add(task);
                                    task.execute(pic_content.get(index));
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
    private static final int ACTION_SUCCEED = 14;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ACTION_FAILED:
                    MessageUtil.showMessage(App.getContextInstance(), R.string.msg_action_failed);
                    sendEmptyMessage(HIDE_PROGRESS);
                    break;
                case ACTION_SUCCEED:
                    SharedPreferences.Editor editor = draftPreferences.edit();
                    editor.clear().commit();
                    getParentActivity().setResult(Activity.RESULT_OK);
                    MessageUtil.showMessage(App.getContextInstance(), R.string.msg_action_successed);
                    if(getActivity() != null) {
                        getActivity().finish();
                    }
                    sendEmptyMessage(HIDE_PROGRESS);
                    break;
                case SHOW_PROGRESS:
                    rlProgress.setVisibility(View.VISIBLE);
                    break;
                case HIDE_PROGRESS:
                    rlProgress.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        rlProgress.setVisibility(View.GONE);
        super.onDestroy();
    }

    private void changeTab(final int tabIndex) {
        if(currentTabIndex == tabIndex) {
            Log.i(TAG, "changeTab& return;");
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BaseFragment fragment = null;
        if(tabIndex == WALL_TAB_WORD) {
            fragment = fragment1;
            if(translateAnimation1 == null) {
                translateAnimation1 = new TranslateAnimation(ivCursor.getWidth(), 0, ViewHelper.getY(ivCursor), ViewHelper.getY(ivCursor));
                translateAnimation1.setFillAfter(true);
                translateAnimation1.setDuration(300);
            }
            ivCursor.startAnimation(translateAnimation1);
        } else {
            UIUtil.hideKeyboard(getParentActivity(), fragment1.getEditText4Content());
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

        Log.i(TAG, "changeTab& tabIndex = " + tabIndex);

        if(tabIndex == WALL_TAB_WORD) {
            Log.i(TAG, "changeTab& tabIndex = WALL_TAB_WORD");
            // 切换至输入文字的Tab，输入框获取焦点并弹出键盘
            fragment1.getEditText4Content().requestFocus();
            InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
            if(params == null) {
                return null;
            }
            return LocalImageLoader.compressBitmap(App.getContextInstance(), FileUtil.getRealPathFromURI(App.getContextInstance(), params[0]), 480, 800, false);
        }

        @Override
        protected void onPostExecute(String path) {
            submitPic(path, contentId, index, multiple, lastPic);
            tasks.remove(this);
        }
    }

    private void submitPic(String path, String contentId, int index, boolean multiple, final boolean lastPic) {
        File f = new File(path);
        if(!f.exists()) {
            if(lastPic) {
                mHandler.sendEmptyMessage(ACTION_FAILED);
            }
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("content_id", contentId);
        params.put("photo_index", "" + index);
        params.put("photo_caption", "");
        params.put("file", f);
        params.put("multiple", multiple ? "1" : "0");


        new HttpTools(App.getContextInstance()).upload(Constant.API_WALL_PIC_POST, params, UPLOAD_PIC, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                if(lastPic) {
                    mHandler.sendEmptyMessage(ACTION_SUCCEED);
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
        Intent intent = LocationUtil.getPlacePickerIntent(getActivity(), latitude, longitude, location_desc.getText().toString());
        if(intent != null)
            startActivityForResult(intent, GET_LOCATION);
    }

    private void goChooseMembers() {
        Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
        intent.putExtra("members_data", gson.toJson(at_members_data));
        intent.putExtra("groups_data", gson.toJson(at_groups_data));
        intent.putExtra("type", 0);
        startActivityForResult(intent, GET_MEMBERS);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");

        // 没有退出编辑不用保存蓝草稿
        draftPreferences.edit().putBoolean(PREFERENCE_KEY_IS_SAVE, false).commit();

        if(resultCode == getActivity().RESULT_OK) {
            switch(requestCode) {
                case GET_LOCATION:
                    if(data != null) {
                        //        intent.putExtra("has_location", position_name.getText().toString());
                        //                        if (SystemUtil.checkPlayServices(getActivity())) {
                        //                            final Place place = PlacePicker.getPlace(data, getActivity());
                        //                            if(place!=null) {
                        //                                String locationName = place.getAddress().toString();
                        //                                location_desc.setText(locationName);
                        //                                latitude = place.getLatLng().latitude;
                        //                                longitude = place.getLatLng().longitude;
                        //                            }
                        //
                        //                        }else {
                        String locationName = data.getStringExtra(Constant.EXTRA_LOCATION_NAME);
                        if(!TextUtils.isEmpty(locationName)) {
                            location_desc.setText(locationName);
                            latitude = data.getDoubleExtra(Constant.EXTRA_LATITUDE, 0);
                            longitude = data.getDoubleExtra(Constant.EXTRA_LONGITUDE, 0);
                        } else {
                            location_desc.setText(null);
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
                    Log.i(TAG, "onActivityResult: size = " + at_members_data.size());
                    String groups = data.getStringExtra("groups_data");
                    at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {}.getType());
                    changeAtDesc(true);
                    break;
            }
        }
    }

    void changeAtDesc(boolean checkVisible) {
        if(fragment1 != null) {
            fragment1.changeAtDesc(at_members_data, at_groups_data, checkVisible);
        } else {
            Log.w(TAG, "changeAtDesc fragment1 is null, can't change at description");
        }
    }

    private void showChooseFeeling() {
        if(popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            return;
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
        llm = new LinearLayoutManager(getParentActivity());
        feeling_icons.setLayoutManager(llm);
        fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), PATH_PREFIX);
        // 对文件进行按首字的大小排序
        SortComparator compartor = new SortComparator();
        Collections.sort(fileNames, compartor);
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

    private List<String> setGetGroupIds(List<GroupEntity> groups) {
        List<String> ids = new ArrayList<>();
        if(groups != null) {
            int count = groups.size();
            for(int i = 0; i < count; i++) {
                ids.add(groups.get(i).getGroup_id());
            }
        }
        return ids;
    }

    /**
     * 对字符串按首字母排序按首字母排序，并忽略大小字的排序规则
     */
    class SortComparator implements Comparator<String>{

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