package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.EditDiaryAdapter;
import com.bondwithme.BondWithMe.adapter.HeadHolder;
import com.bondwithme.BondWithMe.adapter.VideoHolder;
import com.bondwithme.BondWithMe.entity.DiaryPhotoEntity;
import com.bondwithme.BondWithMe.entity.GroupEntity;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.entity.PushedPhotoEntity;
import com.bondwithme.BondWithMe.entity.PutWallEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.ImagesRecyclerListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.InviteMemberActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.share.PreviewVideoActivity;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.SortComparator;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.WallEditView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created 10/20/15.
 *
 * @author Jackie
 * @version 1.0
 *          <p/>
 *          日志编辑
 */
public class EditDiaryFragment extends BaseFragment<NewDiaryActivity> implements View.OnClickListener {

    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private static final String TAG = EditDiaryFragment.class.getSimpleName();

    private static final String POST_WALL = TAG + "_POST_WALL";
    private static final String PUT_WALL = TAG + "_PUT_WALL";
    private static final String UPLOAD_PIC = TAG + "_UPLOAD_PIC";
    private static final String UPLOAD_VIDEO = TAG + "_UPLOAD_VIDEO";
    private static final String GET_DETAIL = TAG + "_GET_DETAIL";

    public static final String PREFERENCE_NAME = "SAVE_DRAFT";
    public static final String PREFERENCE_KEY_IS_SAVE = "IS_SAVE";

    private static final String PREFERENCE_KEY_PIC_CONTENT = "PIC_CONTENT";
    private static final String PREFERENCE_KEY_PIC_CAPTION = "PIC_CAPTION";
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

    /**
     * 保存草稿的首选项
     */
    private static SharedPreferences draftPreferences;
    private HttpTools mHttpTools;
    private WallEntity wall;
    private int lastPic = 0;

    public static EditDiaryFragment newInstance(String... params) {

        return createInstance(new EditDiaryFragment(), params);
    }

    private CallBack callBack = new CallBack();
    private boolean isEdit = false;

    private String contentGroupId;

    private double latitude;
    private double longitude;
    private Gson gson;

    private boolean allRange = false;

    /**
     * TAG的用户列表
     */
    private List<UserEntity> at_members_data = new ArrayList<>();
    /**
     * TAG的组群列表
     */
    private List<GroupEntity> at_groups_data = new ArrayList<>();

    private String loc_type;

    /**
     * 存放图片Uri列表
     */
    private List<Uri> imageUris = new ArrayList<>();
    private ArrayList<PushedPhotoEntity> photoEntities = new ArrayList<>();
    private ArrayList<DiaryPhotoEntity> localEntities = new ArrayList<>();
    private List<String> deletePhoto = new ArrayList<>();
    private List<String> deleteVideo = new ArrayList<>();

    private EditDiaryAdapter mAdapter;

    /**
     * 视频Uri
     */
    private Uri videoUri = Uri.EMPTY;

    /**
     * 视频时长
     */
    private String duration;

    /**
     * private CircularProgress progressBar;
     */
    private RecyclerView feeling_icons;

    /**
     * 心情选择弹出框
     */
    private PopupWindow popupwindow;

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

    private LinearLayout llLocation;

    /**
     * 地址描述
     */
    private TextView tvLocationDesc;

    /**
     * 公开性提示控件：private or ALL
     */
    private TextView tvPrivacy;

    private View headView;

    /**
     * 得到的视频显示在这个控件上
     */
    private View previewVideoView;
    private ImageView ivDisplay;
    private TextView tvDuration;

    private RecyclerView rvImages;
    /**
     * 加载框
     */
    private RelativeLayout rlProgress;

    MyDialog myDialog;

    private List<CompressBitmapTask> tasks;
    private static final int SHOW_PROGRESS = 11;
    private static final int HIDE_PROGRESS = 12;
    private static final int ACTION_FAILED = 13;
    private static final int ACTION_SUCCEED = 14;
    private static final int GET_WALL_SUCCEED = 15;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_FAILED:
                    MessageUtil.showMessage(App.getContextInstance(), R.string.msg_action_failed);
                    sendEmptyMessage(HIDE_PROGRESS);
                    break;
                case ACTION_SUCCEED:
                    if (!isEdit) {
                        SharedPreferences.Editor editor = draftPreferences.edit();
                        editor.clear().apply();
                    }

                    getActivity().setResult(Activity.RESULT_OK);
//                    MessageUtil.showMessage(App.getContextInstance(), R.string.msg_action_successed);
                    if (getActivity() != null) {
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
                case GET_WALL_SUCCEED:
                    // 得到纬度
                    String strLatitude = wall.getLoc_latitude();
                    if (!TextUtils.isEmpty(strLatitude)) {
                        latitude = Double.valueOf(strLatitude);
                    }
                    // 得到经度
                    String strLongitude = wall.getLoc_longitude();
                    if (!TextUtils.isEmpty(strLongitude)) {
                        longitude = Double.valueOf(strLatitude);
                    }
                    loc_type = wall.getLoc_type();
                    // 地名
                    String locName = wall.getLoc_name();
                    if (!TextUtils.isEmpty(locName)) {
                        llLocation.setVisibility(View.VISIBLE);
                    } else {
                        llLocation.setVisibility(View.GONE);
                    }
                    tvLocationDesc.setText(locName);

                    // AT 用户列表
                    at_members_data = wall.getTag_member();
                    if (at_members_data != null && at_members_data.size() > 0) {
                        wevContent.setOldMemberText(String.format(WallUtil.AT_MEMBER, at_members_data.size()));
                    }
                    // AT 群组列表
                    at_groups_data = wall.getTag_group();
                    if (at_groups_data != null && at_groups_data.size() > 0) {
                        wevContent.setOldGroupText(String.format(WallUtil.AT_GROUPS, at_groups_data.size()));
                    }
                    // 设置描述显示
                    wevContent.setText(wall.getText_description());
                    changeAtDesc(true);

                    String feel = wall.getDofeel_code();
                    if (!TextUtils.isEmpty(feel)) {
                        if (fileNames.isEmpty()) {
                            fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), Constant.PATH_PREFIX);
                            // 对文件进行按首字的大小排序
                            SortComparator comparator = new SortComparator();
                            Collections.sort(fileNames, comparator);
                        }

                        int charIndex = feel.lastIndexOf("_");
                        String name = feel.substring(charIndex + 1);
                        checkItemIndex = fileNames.indexOf(name);
                        selectFeelingPath = String.format(Constant.FEEL_ICON_NAME, name);
                        LogUtil.d(TAG, "path: " + selectFeelingPath);
                        try {
                            iv_feeling.setVisibility(View.VISIBLE);
                            iv_feeling.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(selectFeelingPath)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    allRange = String.valueOf(1).equals(wall.getContent_group_public());
                    switchPrivacy(allRange);

                    String videoName = wall.getVideo_filename();
                    if (TextUtils.isEmpty(videoName)) {
                        mAdapter.setIsPhoto(true);
                        // 检测网络上的图片
                        int photoCount = Integer.valueOf(wall.getPhoto_count());
                        LogUtil.d(TAG, "GET_WALL_SUCCEED photoCount = " + photoCount);
                        if (photoCount > 0) {
                            Map<String, String> condition = new HashMap<>();
                            condition.put("content_id", wall.getContent_id());
                            Map<String, String> params = new HashMap<>();
                            params.put("condition", UrlUtil.mapToJsonstring(condition));
                            String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
                            mAdapter.setRequest_url(url);
                        }
                    } else {
                        mAdapter.setIsPhoto(false);
                    }
                    break;
            }
        }
    };


    /**
     * 更新日志成功结果处理
     */
    private void resultByPutWall() {
        if (!localEntities.isEmpty()) {

            if (wall != null) {
                submitLocalPhotos(wall.getContent_id());
            }
        } else if (!Uri.EMPTY.equals(videoUri)) {
            Map<String, Object> params = new HashMap<>();

            File f = new File(videoUri.getPath());
            params.put("file", f);
            //            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(videoUri.toString(), new ImageSize(640, 480));
            //            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //            String strThumbnail = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            String strThumbnail = LocalImageLoader.getVideoThumbnail(getActivity(), videoUri);
            strThumbnail = String.format("data:image/png;base64,%s", strThumbnail);
            params.put("video_thumbnail", strThumbnail);
            params.put("video_duration", duration);
            params.put("content_creator_id", MainActivity.getUser().getUser_id());
            params.put("content_id", wall.getContent_id());
            LogUtil.i(TAG, "submitPic$ strThumbnail: " + strThumbnail);
            callBack.setLinkType(CallBack.LINK_TYPE_UPLOAD_VOID);
            mHttpTools.upload(Constant.API_UPLOAD_VIDEO, params, UPLOAD_VIDEO, callBack);
        } else {
            mHandler.sendEmptyMessage(ACTION_SUCCEED);
        }
    }

    /**
     * 发送日志成功返回结果处理
     *
     * @param response
     */
    private void resultBySubmitWall(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            if ("1".equals(obj.getString("resultStatus")) && !TextUtils.isEmpty(obj.getString("contentID"))) {
                String contentId = obj.getString("contentID");
                submitLocalPhotos(contentId);
            } else {
                mHandler.sendEmptyMessage(ACTION_FAILED);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            mHandler.sendEmptyMessage(ACTION_FAILED);
        }
    }

    private void submitLocalPhotos(String contentId) {
        LogUtil.d(TAG, "submitLocalPhotos& contentId: " + contentId);
        if (localEntities.isEmpty()) {
            mHandler.sendEmptyMessage(ACTION_SUCCEED);
        } else {
            int count = photoEntities.size();
            int pushedCount = count - localEntities.size();
            boolean multiple = (pushedCount < count - 1);
            tasks = new ArrayList<>();
            for (int index = pushedCount; index < count; index++) {
                DiaryPhotoEntity photoEntity = (DiaryPhotoEntity) photoEntities.get(index);
                photoEntity.setPhoto_caption(getPhotoCaptionByPosition(index + 1));
                CompressBitmapTask task = new CompressBitmapTask(contentId, index, multiple);
                tasks.add(task);
                //for not work in down 11
                if (SDKUtil.IS_HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photoEntity);
                } else {
                    task.execute(photoEntity);
                }
            }
        }
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_edit_diary;
    }

    @Override
    public void initView() {
        try {
            contentGroupId = getArguments().getString(ARG_PARAM_PREFIX + "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHttpTools = new HttpTools(getContext());
        isEdit = !TextUtils.isEmpty(contentGroupId);
        if (!isEdit) {
            recoverList();
        }

        gson = new Gson();

        rlProgress = getViewById(R.id.rl_progress);


        rvImages = getViewById(R.id.rcv_post_photos);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvImages.setLayoutManager(llm);
        mAdapter = new EditDiaryAdapter(getContext(), photoEntities);
        mAdapter.setListener(new ImagesRecyclerListener() {
            @Override
            public void loadHeadView(HeadHolder headHolder) {
                LogUtil.d(TAG, "loadHeadView");
                headView = headHolder.itemView;

                // 显示的列表
                iv_feeling = (ImageView) headView.findViewById(R.id.iv_feeling);

                llLocation = (LinearLayout) headView.findViewById(R.id.ll_location);
                llLocation.setOnClickListener(EditDiaryFragment.this);
                tvLocationDesc = (TextView) headView.findViewById(R.id.location_desc);
                wevContent = (WallEditView) headView.findViewById(R.id.diary_edit_content);
                wevContent.setTextChangeListener(new WallEditView.TextChangeListener() {
                    int lastChange = CHANGE_MODE_NORMAL;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s, int change) {
                        switch (change) {
                            case CHANGE_MODE_NORMAL:
                                if (lastChange == CHANGE_MODE_BLACK_CHANGE) {
                                    lastChange = change;
                                    changeAtDesc(true);
                                    return;
                                }
                                break;
                            case CHANGE_MODE_DELETE_AT_ALL:
                                if (at_members_data != null) {
                                    at_members_data.clear();
                                }
                                if (at_groups_data != null) {
                                    at_groups_data.clear();
                                }
                                break;

                            case CHANGE_MODE_DELETE_AT_GROUPS:
                                if (at_groups_data != null) {
                                    at_groups_data.clear();
                                }
                                break;

                            case CHANGE_MODE_DELETE_AT_MEMBER:
                                if (at_members_data != null) {
                                    at_members_data.clear();
                                }
                                break;
                        }
                        lastChange = change;
                    }
                });
            }

            @Override
            public void loadVideoView(VideoHolder videoHolder) {
                LogUtil.d(TAG, "loadVideoView");
                previewVideoView = videoHolder.itemView;
                initVideoView();
                if (!Uri.EMPTY.equals(videoUri)) {
                    ImageLoader.getInstance().displayImage(videoUri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
                } else {
                    if (!TextUtils.isEmpty(wall.getVideo_filename())) {
                        String url = String.format(Constant.API_GET_VIDEO_THUMBNAIL, wall.getContent_creator_id(), wall.getVideo_thumbnail());
                        ImageLoader.getInstance().displayImage(url, ivDisplay, UniversalImageLoaderUtil.options);
//                        VolleyUtil.initNetworkImageView(getContext(), nivVideo, url, R.drawable.network_image_default, R.drawable.network_image_default);
                        duration = wall.getVideo_duration();
                    }
                }
                tvDuration.setText(MyDateUtils.formatDuration(duration));
            }

            @Override
            public void deletePhoto(PushedPhotoEntity photo) {
                LogUtil.d(TAG, "deletePhoto");
                photoEntities.remove(photo);
                if (photo instanceof DiaryPhotoEntity) {
                    localEntities.remove(photo);
                    imageUris.remove(((DiaryPhotoEntity) photo).getUri());
                } else {
                    deletePhoto.add(photo.getPhoto_id());
                }

            }

            @Override
            public void loadFinish() {
                LogUtil.d(TAG, "loadFinish");
                if (!isEdit) {
                    try {
                        recoverDraft();
                    } catch (Exception e) {
                        draftPreferences.edit().clear().apply();
                        e.printStackTrace();
                    }
                }
            }
        });
        if (!Uri.EMPTY.equals(videoUri)) {
            clearPhotos();
        } else if (!photoEntities.isEmpty()) {
            clearVideo();
        }
        rvImages.setAdapter(mAdapter);

        tvPrivacy = getViewById(R.id.tv_privacy);

        // 选择图片的点击监听
        getViewById(R.id.tv_camera).setOnClickListener(this);
        // 选择用户的点击监听
        getViewById(R.id.tv_tag).setOnClickListener(this);
        // 选择性情的点击监听
        getViewById(R.id.tv_feeling).setOnClickListener(this);
        // 选择位置的点击监听
        getViewById(R.id.tv_location).setOnClickListener(this);
        // 切换锁的点击监听
        getViewById(R.id.tv_privacy).setOnClickListener(this);
    }

    private void initVideoView() {
        tvDuration = (TextView) previewVideoView.findViewById(R.id.duration_tv);
        ivDisplay = (ImageView) previewVideoView.findViewById(R.id.video_view_iv);
        // 删除视频
        previewVideoView.findViewById(R.id.delete_video_view).setOnClickListener(this);
        previewVideoView.findViewById(R.id.go_to_preview_video_iv).setOnClickListener(this);
    }


    /**
     * 清空选择的视频和显示的UI
     */
    private void clearVideo() {
        LogUtil.i(TAG, "clearVideo&");
        videoUri = Uri.EMPTY;
        if (rvImages.getChildCount() > 1 && !mAdapter.isPhoto()) {
            rvImages.removeView(previewVideoView);
        }
        if (isEdit) {
            if (!TextUtils.isEmpty(wall.getVideo_filename())) {
                deleteVideo.add(wall.getVideo_id());
                wall.setVideo_filename("");
            }
        }
        mAdapter.setIsPhoto(true);
    }

    /**
     * 清除选择的图片数据和UI
     */
    private void clearPhotos() {
        LogUtil.i(TAG, "clearPhotos&");
        // 清册选择的图片
        imageUris.clear();
        photoEntities.clear();
        localEntities.clear();
        if (photoEntities.size() > 0 && mAdapter.isPhoto()) {
            rvImages.removeViews(1, photoEntities.size());
        }
        mAdapter.setIsPhoto(false);
        // datas.clear();
    }

    /**
     * 添加Activity请求返回的视频数据
     *
     * @param data 请求返回的{@link Intent}
     */
    private void addVideoFromActivityResult(Intent data) {
        clearPhotos();
        mAdapter.notifyDataSetChanged();
        videoUri = data.getData();
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(getActivity(), videoUri);
        duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        metadataRetriever.release();
        if (!Uri.EMPTY.equals(videoUri) && ivDisplay != null) {
            ImageLoader.getInstance().displayImage(videoUri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
            tvDuration.setText(MyDateUtils.formatDuration(duration));
        }
        LogUtil.i(TAG, "addVideoFromActivityResult& videoUri: " + videoUri);
        LogUtil.i(TAG, "addVideoFromActivityResult& videoDuration: " + duration);
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
    }

    /**
     * 当运行到{@link #onResume()}时恢复草稿
     */
    private void recoverDraft() throws Exception {
        LogUtil.i(TAG, "recoverDraft");
        if (draftPreferences == null) {
            draftPreferences = getParentActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        if (!draftPreferences.getBoolean(PREFERENCE_KEY_IS_SAVE, false)) {
            return;
        }
        String locationDesc = draftPreferences.getString(PREFERENCE_KEY_LOC_NAME, "");
        if (!TextUtils.isEmpty(locationDesc)) {
            llLocation.setVisibility(View.VISIBLE);
        } else {
            llLocation.setVisibility(View.GONE);
        }
        tvLocationDesc.setText(locationDesc);
        longitude = draftPreferences.getFloat(PREFERENCE_KEY_LOC_LONGITUDE, (float) longitude);
        latitude = draftPreferences.getFloat(PREFERENCE_KEY_LOC_LATITUDE, (float) latitude);

        selectFeelingPath = draftPreferences.getString(PREFERENCE_KEY_DO_FEEL_CODE, selectFeelingPath);
        if (!TextUtils.isEmpty(selectFeelingPath)) {
            checkItemIndex = draftPreferences.getInt(PREFERENCE_KEY_CHECK_ITEM_INDEX, checkItemIndex);
            iv_feeling.setVisibility(View.VISIBLE);
            try {
                iv_feeling.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(selectFeelingPath)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        allRange = draftPreferences.getBoolean(PREFERENCE_KEY_CONTENT_GROUP_PUBLIC, allRange);
        switchPrivacy(allRange);

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
        // 恢复了草稿，清除保存
        draftPreferences.edit().clear().apply();
    }

    /**
     * 恢复保存的图片列表或者视频，一定要在setAdapter之前恢复否则无法显示
     */
    private void recoverList() {
        if (draftPreferences == null) {
            draftPreferences = getParentActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        if (!draftPreferences.getBoolean(PREFERENCE_KEY_IS_SAVE, false)) {
            return;
        }
        int picCount = draftPreferences.getInt(PREFERENCE_KEY_PIC_COUNT, 0);
        if (picCount > 0) {
            for (int i = 0; i < picCount; i++) {
                String strUri = draftPreferences.getString(PREFERENCE_KEY_PIC_CONTENT + i, "");
                String strCaption = draftPreferences.getString(PREFERENCE_KEY_PIC_CAPTION + i, "");
                LogUtil.i(TAG, "recoverDraft& uri: " + strUri);
                if (!TextUtils.isEmpty(strUri)) {
                    Uri uri = Uri.parse(strUri);
                    imageUris.add(uri);
                    DiaryPhotoEntity diaryPhotoEntity = new DiaryPhotoEntity();
                    diaryPhotoEntity.setUri(uri);
                    diaryPhotoEntity.setPhoto_caption(strCaption);
                    localEntities.add(diaryPhotoEntity);
                }
            }
            photoEntities.addAll(localEntities);
        } else {
            String videoUriStr = draftPreferences.getString(PREFERENCE_KEY_VIDEO_PATH, "");

            Log.i(TAG, "recoverDraft& videoUri: " + videoUriStr);
            if (!TextUtils.isEmpty(videoUriStr)) {
                videoUri = Uri.parse(videoUriStr);
                duration = draftPreferences.getString(PREFERENCE_KEY_VIDEO_DURATION, "");
            }
        }
    }

    private void switchPrivacy(boolean allRange) {
        Resources resources = getResources();
        Drawable drawable;

        if (allRange) {
            drawable = resources.getDrawable(R.drawable.privacy_open);
            tvPrivacy.setText(R.string.text_all);
        } else {
            drawable = resources.getDrawable(R.drawable.privacy_lock);
            tvPrivacy.setText(R.string.text_private);
        }
        Drawable[] drawables = tvPrivacy.getCompoundDrawables();
        if (drawable != null) {
            drawable.setBounds(drawables[1].getBounds());
        }
        tvPrivacy.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public void requestData() {
        if (isEdit) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constant.CONTENT_GROUP_ID, contentGroupId);
            params.put(Constant.USER_ID, MainActivity.getUser().getUser_id());
            callBack.setLinkType(CallBack.LINK_TYPE_GET_WALL);
            mHttpTools.get(Constant.API_WALL_DETAIL, params, GET_DETAIL, callBack);
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
        // 隐藏键盘
        UIUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        }
    }

    /**
     * 保存草稿
     */
    private void saveDraft() {
        LogUtil.i(TAG, "saveDraft");
        SharedPreferences.Editor editor = draftPreferences.edit();
        if (!photoEntities.isEmpty()) {
            int i = 0;
            for (PushedPhotoEntity entity : photoEntities) {
                if (entity instanceof DiaryPhotoEntity) {
                    editor.putString(PREFERENCE_KEY_PIC_CONTENT + i, ((DiaryPhotoEntity) entity).getUri().toString());
                    editor.putString(PREFERENCE_KEY_PIC_CAPTION + i, getPhotoCaptionByPosition(i + 1));
                    LogUtil.i(TAG, "saveDraft& " + PREFERENCE_KEY_PIC_CONTENT + i + ": " + ((DiaryPhotoEntity) entity).getUri().toString());
                    LogUtil.i(TAG, "saveDraft& " + PREFERENCE_KEY_PIC_CAPTION + i + ": " + entity.getPhoto_caption());
                    i++;
                }
            }
            editor.putInt(PREFERENCE_KEY_PIC_COUNT, i);
        } else if (!Uri.EMPTY.equals(videoUri)) {
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
        if (draftPreferences != null) {
            draftPreferences.edit().putBoolean(PREFERENCE_KEY_IS_SAVE, false).apply();
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.INTENT_REQUEST_GET_LOCATION:
                    if (data != null) {
                        String locationName = data.getStringExtra(Constant.EXTRA_LOCATION_NAME);
                        if (!TextUtils.isEmpty(locationName)) {
                            llLocation.setVisibility(View.VISIBLE);
                            tvLocationDesc.setText(locationName);
                            latitude = data.getDoubleExtra(Constant.EXTRA_LATITUDE, 0);
                            longitude = data.getDoubleExtra(Constant.EXTRA_LONGITUDE, 0);
                        } else {
                            llLocation.setVisibility(View.GONE);
                            tvLocationDesc.setText(null);
                            latitude = -1000;
                            longitude = -1000;
                        }
                        //                        }
                        //坐标数据类型
                        loc_type = data.getStringExtra("loc_type");
                    }
                    break;
                case Constant.INTENT_REQUEST_GET_MEMBERS:
                    String members = data.getStringExtra("members_data");
                    at_members_data = gson.fromJson(members, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    LogUtil.i(TAG, "onActivityResult: size = " + at_members_data.size());
                    String groups = data.getStringExtra("groups_data");
                    at_groups_data = gson.fromJson(groups, new TypeToken<ArrayList<GroupEntity>>() {
                    }.getType());
                    changeAtDesc(true);
                    break;

                case Constant.INTENT_REQUEST_OPEN_GPS:
                    openMap();
                    break;
                case Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO:
                    if (data != null) {
                        String type = data.getStringExtra(MediaData.EXTRA_MEDIA_TYPE);
                        if (MediaData.TYPE_VIDEO.equals(type)) {
                            LogUtil.i(TAG, "onActivityResult& play video uri: " + data.getData());
                            addVideoFromActivityResult(data);
                        } else {
                            clearVideo();
                            ArrayList<Uri> pickUris;
                            pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.EXTRA_IMAGES_STR);
                            imageUris.clear();
                            imageUris.addAll(pickUris);
                            addDataAndNotify(pickUris);
                        }
                    }
                    break;

                case Constant.INTENT_REQUEST_FEELING:
                    checkItemIndex = data.getIntExtra(Constant.EXTRA_CHECK_ITEM_INDEX, 0);
                    if (fileNames.isEmpty()) {
                        fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), Constant.PATH_PREFIX);
                        // 对文件进行按首字的大小排序
                        SortComparator comparator = new SortComparator();
                        Collections.sort(fileNames, comparator);
                    }

                    selectFeelingPath = String.format(Constant.FEEL_ICON_NAME, fileNames.get(checkItemIndex));
                    iv_feeling.setVisibility(View.VISIBLE);
                    try {
                        iv_feeling.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(selectFeelingPath)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        if (requestCode == Constant.INTENT_REQUEST_OPEN_GPS && LocationUtil.isOPen(getActivity())) {
            openMap();
        }
    }

    private void addDataAndNotify(ArrayList<Uri> pickUris) {
        photoEntities.removeAll(localEntities);
        localEntities.clear();
        for (Uri uri : pickUris) {
            LogUtil.i(TAG, "addDataAndNotify& add uri: " + uri.toString());
            DiaryPhotoEntity entity = new DiaryPhotoEntity();
            entity.setUri(uri);
            localEntities.add(entity);
        }
        photoEntities.addAll(localEntities);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 修改描述
     *
     * @param checkVisible
     */
    public void changeAtDesc(boolean checkVisible) {

        String memberText;
        String groupText;
        if (at_members_data != null && at_members_data.size() > 0) {
            memberText = String.format(getParentActivity().getString(R.string.text_diary_content_at_member_desc), at_members_data.size());
            Log.i(TAG, "changeAtDesc& member of at description is " + memberText);
        } else {
            Log.i(TAG, "changeAtDesc& no member of at description");
            memberText = "";
        }
        if (at_groups_data != null && at_groups_data.size() > 0) {
            groupText = String.format(getParentActivity().getString(R.string.text_diary_content_at_group_desc), at_groups_data.size());
            Log.i(TAG, "changeAtDesc& group of at description is " + groupText);
        } else {
            Log.i(TAG, "changeAtDesc& no group of at description");
            groupText = "";
        }
        if (!checkVisible) {
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
            case R.id.tv_camera:// 打开相册
                boolean needAlert;
                if (isEdit) {
                    needAlert = wall != null && !TextUtils.isEmpty(wall.getVideo_filename());
                } else {
                    needAlert = !Uri.EMPTY.equals(videoUri);
                }

                if (needAlert) {
                    // 已经选择了视频需要弹出提示
                    myDialog = new MyDialog(getContext(), "", getContext().getString(R.string.will_remove_selected_video));
                    myDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                            openPhotos();
                        }
                    });

                    // 不选择视频
                    myDialog.setButtonCancel(R.string.text_dialog_no, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                } else {
                    openPhotos();
                }
                break;

            case R.id.tv_tag:
                goChooseMembers();
                break;
            case R.id.tv_feeling:
                UIUtil.hideKeyboard(getParentActivity(), wevContent);
                InputMethodManager imm = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
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
            case R.id.ll_location:
            case R.id.tv_location:
                checkGPS();
                break;
            case R.id.tv_privacy:
                allRange = !allRange;
                switchPrivacy(allRange);
                break;

            case R.id.delete_video_view:
                clearVideo();
                mAdapter.notifyItemRemoved(1);
                break;

            case R.id.go_to_preview_video_iv:
                if (!Uri.EMPTY.equals(videoUri)) {
                    Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
                    intent.putExtra(PreviewVideoActivity.EXTRA_VIDEO_URI, videoUri.toString());
                    startActivity(intent);
                } else if (wall != null && !TextUtils.isEmpty(wall.getVideo_filename())) {
                    showPreviewVideo();
                }
                break;
        }
    }

    /**
     * 启动网络播放视频
     */
    private void showPreviewVideo() {
        // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
        Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
        // 传的值对应视频的content_creator_id
        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, wall.getContent_creator_id());
        // 传的值对应video_filename
        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, wall.getVideo_filename());
        startActivity(intent);
    }

    /**
     * 打开相册，选择图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPhotos() {
        LogUtil.d("", "openPhotos========");
        Intent intent = new Intent(getParentActivity(), SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        /**
         * 使用了Universal Image Loader库来处理图片需要返回的Uri与传统有差异，传此值用于区分
         */
        intent.putExtra(MediaData.EXTRA_USE_UNIVERSAL, true);
        intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, true);
        intent.putParcelableArrayListExtra(SelectPhotosActivity.EXTRA_SELECTED_PHOTOS, (ArrayList<? extends Parcelable>) imageUris);
        int residue = SelectPhotosActivity.MAX_SELECT - photoEntities.size();
        intent.putExtra(SelectPhotosActivity.EXTRA_RESIDUE, residue);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO);
    }

    /**
     * 跳至选择TAG用户
     */
    private void goChooseMembers() {
        Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
        if (at_groups_data == null) {
            at_groups_data = new ArrayList<>();
        }

        if (at_members_data == null) {
            at_members_data = new ArrayList<>();
        }
        intent.putExtra("members_data", gson.toJson(at_members_data));
        intent.putExtra("groups_data", gson.toJson(at_groups_data));
        intent.putExtra("type", 0);
        startActivityForResult(intent, Constant.INTENT_REQUEST_GET_MEMBERS);
    }

    private void showChooseFeeling() {
        Intent intent = new Intent(getContext(), FeelingActivity.class);
        intent.putExtra(Constant.EXTRA_CHECK_ITEM_INDEX, checkItemIndex);
        startActivityForResult(intent, Constant.INTENT_REQUEST_FEELING);
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
                    startActivityForResult(intent, Constant.INTENT_REQUEST_OPEN_GPS);
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
        if (intent != null) {
            startActivityForResult(intent, Constant.INTENT_REQUEST_GET_LOCATION);
        }
    }


    /**
     * 上传日记
     */
    public void submitWall() {
        Log.i(TAG, "submitWall&");
        if (isEdit) {
            putWall();
            return;
        }

        String text_content = wevContent.getRelText();

        if (TextUtils.isEmpty(text_content) && photoEntities.isEmpty() && Uri.EMPTY.equals(videoUri)) {
            // 没文字、没图片、没视频不能上传日记
            MessageUtil.showMessage(getActivity(), R.string.msg_no_content);
            return;
        }

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        String locationDesc = tvLocationDesc.getText().toString();
        String latitudeDesc;
        String longitudeDesc;

        if (!TextUtils.isEmpty(locationDesc)) {
            locationDesc = tvLocationDesc.getText().toString();
            latitudeDesc = "" + latitude;
            longitudeDesc = "" + longitude;
        } else {
            locationDesc = "";
            latitudeDesc = "";
            longitudeDesc = "";
        }


        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PARAM_CONTENT_CREATOR_ID, MainActivity.getUser().getUser_id());
        params.put(Constant.PARAM_CONTENT_TYPE, "post");
        params.put(Constant.PARAM_TEXT_DESCRIPTION, text_content);
        params.put(Constant.PARAM_LOC_LATITUDE, latitudeDesc);
        params.put(Constant.PARAM_LOC_LONGITUDE, longitudeDesc);
        params.put(Constant.PARAM_LOC_NAME, locationDesc);
        params.put(Constant.PARAM_LOC_CAPTION, "");
        params.put(Constant.PARAM_STICKER_GROUP_PATH, "");
        params.put(Constant.PARAM_LOC_TYPE, loc_type);


        if (!Uri.EMPTY.equals(videoUri)) {
            params.put("video", "1");
            /**wing modifi for pic too big begin*/
            /**wing modifi for pic too big begin*/
            File f = new File(videoUri.getPath());
            params.put("file", f);
            //            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(videoUri.toString(), new ImageSize(640, 480));
            //            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //            String strThumbnail = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            String strThumbnail = LocalImageLoader.getVideoThumbnail(getActivity(), videoUri);
            strThumbnail = String.format("data:image/png;base64,%s", strThumbnail);
            params.put("video_thumbnail", strThumbnail);
            params.put("video_duration", duration);
            LogUtil.i(TAG, "submitPic$ strThumbnail: " + strThumbnail);
        }


        try {
            if (!TextUtils.isEmpty(selectFeelingPath)) {
                StringBuilder b = new StringBuilder(selectFeelingPath);
                int charIndex = selectFeelingPath.lastIndexOf("/");
                b.replace(charIndex, charIndex + 1, "_");
                params.put("dofeel_code", b.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("sticker_type", "");
        params.put("group_ind_type", "2");
        params.put("content_group_public", (allRange ? "1" : "0"));

        if (photoEntities.isEmpty()) {
            params.put("upload_photo", "0");
        } else {
            params.put("upload_photo", "1");
        }

        params.put("tag_group", gson.toJson(setGetGroupIds(at_groups_data)));
        params.put("tag_member", gson.toJson(setGetMembersIds(at_members_data)));
        String url;
        url = Constant.API_WALL_TEXT_POST;
        callBack.setLinkType(CallBack.LINK_TYPE_SUBMIT_WALL);
        mHttpTools.upload(url, params, POST_WALL, callBack);

    }

    /**
     * 上传日记
     */
    public void putWall() {
        String text_content = wevContent.getRelText();

        if (TextUtils.isEmpty(text_content) && photoEntities.isEmpty() && Uri.EMPTY.equals(videoUri)) {
            // 没文字、没图片、没视频不能上传日记
            MessageUtil.showMessage(getActivity(), R.string.msg_no_content);
            return;
        }

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        String locationDesc = tvLocationDesc.getText().toString();
        String latitudeDesc;
        String longitudeDesc;

        if (!TextUtils.isEmpty(locationDesc)) {
            locationDesc = tvLocationDesc.getText().toString();
            latitudeDesc = "" + latitude;
            longitudeDesc = "" + longitude;
        } else {
            locationDesc = "";
            latitudeDesc = "";
            longitudeDesc = "";
        }


        PutWallEntity entity = new PutWallEntity();
        entity.setNew_photo("0");
        entity.setUser_id(MainActivity.getUser().getUser_id());
        entity.setText_description(text_content);
        entity.setLoc_latitude(latitudeDesc);
        entity.setLoc_longitude(longitudeDesc);
        entity.setLoc_name(locationDesc);
        entity.setLoc_type(loc_type);
        entity.setDelete_photo(deletePhoto);
        entity.setDelete_video(deleteVideo);
        entity.setTag_group(setGetGroupIds(at_groups_data));
        entity.setTag_member(setGetMembersIds(at_members_data));
        entity.setContent_group_public(allRange ? String.valueOf(1) : String.valueOf(0));
        entity.setPhoto_max(String.valueOf(photoEntities.size() - 1));

        if (!Uri.EMPTY.equals(videoUri)) {
            entity.setNew_video("1");
        } else {
            ArrayList<PushedPhotoEntity> pushedPhotoEntities = new ArrayList<>();
            for (int i = 0; i < photoEntities.size() - localEntities.size(); i++) {
                String caption = getPhotoCaptionByPosition(i + 1);
                LogUtil.d(TAG, "putWall& caption: " + caption);
                PushedPhotoEntity photoEntity = photoEntities.get(i);
                photoEntity.setPhoto_caption(caption);
                pushedPhotoEntities.add(photoEntity);
            }
            entity.setEdit_photo(pushedPhotoEntities);
        }

        if (!localEntities.isEmpty()) {
            entity.setNew_photo("1");
        }


        try {
            if (!TextUtils.isEmpty(selectFeelingPath)) {
                StringBuilder b = new StringBuilder(selectFeelingPath);
                int charIndex = selectFeelingPath.lastIndexOf("/");
                b.replace(charIndex, charIndex + 1, "_");
                entity.setDofeel_code(b.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_PUT_WALL, wall.getContent_id());
        requestInfo.jsonParam = gson.toJson(entity);
        LogUtil.d(TAG, "params: " + requestInfo.jsonParam);
        callBack.setLinkType(CallBack.LINK_TYPE_PUT_WALL);
        mHttpTools.put(requestInfo, PUT_WALL, callBack);

    }

    private String getPhotoCaptionByPosition(int position) {
        LogUtil.d(TAG, "getPhotoCaptionByPosition& position = " + position);
        if (position < mAdapter.getItemCount()) {
            RecyclerView.ViewHolder holder = rvImages.findViewHolderForLayoutPosition(position);
            if (holder != null && holder instanceof EditDiaryAdapter.ImageHolder) {
                return ((EditDiaryAdapter.ImageHolder) holder).wevContent.getRelText();
            } else {
                if (holder == null) {
                    LogUtil.e(TAG, "getPhotoCaptionByPosition& get holder is null");
                }
                LogUtil.e(TAG, "getPhotoCaptionByPosition& Class Cast Exception");
            }

        } else {
            LogUtil.e(TAG, "getPhotoCaptionByPosition& position " + position + ", count is " + rvImages.getChildCount());
        }
        String result = photoEntities.get(position - 1).getPhoto_caption();
        LogUtil.e(TAG, "getPhotoCaptionByPosition& result: " + result);
        return result;
    }

    /**
     * 上传图片
     *
     * @param path      - 图片路径
     * @param contentId
     * @param index
     * @param multiple
     */
    private void submitPic(String path, String contentId, int index, boolean multiple) throws FileNotFoundException {
        File f = new File(path);
        if (!f.exists()) {
            throw new FileNotFoundException("path: " + path + "; not found");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("content_id", contentId);
        params.put("photo_index", String.valueOf(index));
        params.put("photo_caption", getPhotoCaptionByPosition(index + 1));
        params.put("file", f);
        params.put("multiple", multiple ? "1" : "0");
        LogUtil.d(TAG, "submitPic: params: " + params);
        callBack.setLinkType(CallBack.LINK_TYPE_SUBMIT_PICTURE);
        mHttpTools.upload(Constant.API_WALL_PIC_POST, params, UPLOAD_PIC, callBack);

    }


    private List<String> setGetMembersIds(List<UserEntity> users) {
        List<String> ids = new ArrayList<>();
        if (users != null) {
            int count = users.size();
            for (int i = 0; i < count; i++) {
                ids.add(users.get(i).getUser_id());
            }
        }
        return ids;
    }

    private List<String> setGetGroupIds(List<GroupEntity> groups) {
        List<String> ids = new ArrayList<>();
        if (groups != null) {
            int count = groups.size();
            for (int i = 0; i < count; i++) {
                ids.add(groups.get(i).getGroup_id());
            }
        }
        return ids;
    }

    /**
     * 返回检测，如正在上传或有数据返回为true，表示不能禁止返回
     *
     * @return
     */
    public boolean backCheck() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            return true;
        } else if (rlProgress != null && rlProgress.getVisibility() == View.VISIBLE) {
            MessageUtil.showMessage(App.getContextInstance(), R.string.waiting_upload);
            return true;
        }
        if (tasks != null && tasks.size() > 0) {
            // 图片上传务正在执行
            LogUtil.i(TAG, "backCheck& tasks size: " + tasks.size());
            return true;
        } else if (isEdit) {
            // 是修改正已经发表的日志，不需要保存草稿
            LogUtil.i(TAG, "backCheck& tasks size: ");
            return false;
        } else {
            String text_content = wevContent.getRelText();
            if (draftPreferences == null) {
                draftPreferences = getParentActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            }
            SharedPreferences.Editor editor = draftPreferences.edit();
            if (TextUtils.isEmpty(text_content) && photoEntities.isEmpty() && Uri.EMPTY.equals(videoUri)) {
                // 没有需要上传的内容
                editor.clear().apply();
                return false;
            }

            // 提示是否将内容保存到草稿
            if (myDialog == null) {
                myDialog = new MyDialog(getActivity(), "", getActivity().getString(R.string.text_dialog_save_draft));
                myDialog.setButtonAccept(R.string.text_dialog_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        saveDraft();
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
            if (!myDialog.isShowing()) {
                myDialog.show();
            }
            return true;
        }
    }

    class CompressBitmapTask extends AsyncTask<DiaryPhotoEntity, Void, String> {

        String contentId;
        int index;
        boolean multiple;

        public CompressBitmapTask(String contentId, int index, boolean multiple) {
            this.contentId = contentId;
            this.index = index;
            this.multiple = multiple;
        }

        @Override
        protected String doInBackground(DiaryPhotoEntity... params) {
            if (params == null) {
                return null;
            }
            return LocalImageLoader.compressBitmap(App.getContextInstance(), params[0].getUri(), 480, 800, false);
        }

        @Override
        protected void onPostExecute(String path) {
            try {
                submitPic(path, contentId, index, multiple);
            } catch (FileNotFoundException e) {
                mHandler.sendEmptyMessage(ACTION_FAILED);
                e.printStackTrace();
                getActivity().finish();
            }
            tasks.remove(this);
        }
    }

    class CallBack implements HttpCallback {
        /**
         * 获取日志
         */
        public static final int LINK_TYPE_GET_WALL = 0;
        /**
         * 发送新的日志
         */
        public static final int LINK_TYPE_SUBMIT_WALL = 1;
        /**
         * 上传图片
         */
        public static final int LINK_TYPE_SUBMIT_PICTURE = 2;
        /**
         * put修改的日志
         */
        public static final int LINK_TYPE_PUT_WALL = 3;
        /**
         * 上传视频
         */
        public static final int LINK_TYPE_UPLOAD_VOID = 4;

        /**
         * 当前回调标识，用于识别当前同调用类别
         */
        private int linkType = LINK_TYPE_GET_WALL;

        public void setLinkType(int linkType) {
            this.linkType = linkType;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onFinish() {
            switch (linkType) {
                case LINK_TYPE_GET_WALL:
                    if (wall == null) {
                        getParentActivity().finish();
                    } else {
                        rlProgress.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        @Override
        public void onResult(String string) {
            LogUtil.i(TAG, "onResult# typy: " + linkType + " response: " + string);
            switch (linkType) {
                case LINK_TYPE_GET_WALL:
                    wall = new Gson().fromJson(string, WallEntity.class);
                    mHandler.sendEmptyMessage(GET_WALL_SUCCEED);
                    break;
                case LINK_TYPE_SUBMIT_WALL:
                    resultBySubmitWall(string);
                    break;
                case LINK_TYPE_UPLOAD_VOID:
                    mHandler.sendEmptyMessage(ACTION_SUCCEED);
                    break;
                case LINK_TYPE_SUBMIT_PICTURE:
                    lastPic++;
                    if (lastPic == localEntities.size()) {
                        mHandler.sendEmptyMessage(ACTION_SUCCEED);
                    }
                    break;
                case LINK_TYPE_PUT_WALL:
                    resultByPutWall();
                    break;
            }
        }

        @Override
        public void onError(Exception e) {
            LogUtil.e(TAG, "onError& linkType = " + linkType);
            rlProgress.setVisibility(View.GONE);
            e.printStackTrace();
            mHandler.sendEmptyMessage(ACTION_FAILED);
            switch (linkType) {
                case LINK_TYPE_GET_WALL:
                    getParentActivity().finish();
                    break;
                case LINK_TYPE_SUBMIT_PICTURE:
                    getParentActivity().finish();
                    break;
            }
        }

        @Override
        public void onCancelled() {

        }

        @Override
        public void onLoading(long count, long current) {

        }
    }
}
