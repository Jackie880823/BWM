package com.bondwithme.BondCorp.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.App;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.EditDiaryAdapter;
import com.bondwithme.BondCorp.adapter.HeadHolder;
import com.bondwithme.BondCorp.adapter.NewsFragmentAdapter;
import com.bondwithme.BondCorp.adapter.VideoHolder;
import com.bondwithme.BondCorp.entity.DiaryPhotoEntity;
import com.bondwithme.BondCorp.entity.MediaData;
import com.bondwithme.BondCorp.entity.NewsEntity;
import com.bondwithme.BondCorp.entity.PushedPhotoEntity;
import com.bondwithme.BondCorp.entity.PutNewsEntity;
import com.bondwithme.BondCorp.entity.UserEntity;
import com.bondwithme.BondCorp.http.PicturesCacheUtil;
import com.bondwithme.BondCorp.http.UrlUtil;
import com.bondwithme.BondCorp.interfaces.ImagesRecyclerListener;
import com.bondwithme.BondCorp.ui.share.PreviewVideoActivity;
import com.bondwithme.BondCorp.ui.share.SelectPhotosActivity;
import com.bondwithme.BondCorp.util.LocalImageLoader;
import com.bondwithme.BondCorp.util.LocationUtil;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.MessageUtil;
import com.bondwithme.BondCorp.util.MyDateUtils;
import com.bondwithme.BondCorp.util.SDKUtil;
import com.bondwithme.BondCorp.util.UIUtil;
import com.bondwithme.BondCorp.util.UniversalImageLoaderUtil;
import com.bondwithme.BondCorp.widget.MyDialog;
import com.bondwithme.BondCorp.widget.WallEditView;
import com.google.gson.Gson;
import com.material.widget.Dialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 16/1/25.
 */
public class WriteNewsFragment extends BaseFragment<WriteNewsActivity> implements View.OnClickListener {
    private static final String TAG = "WriteNewsFragment";
    private View rl_category;
    private MyDialog categoryDialog;
    private TextView category_tv;
    private RecyclerView rvImages;
    private Context mContext;
    MyDialog myDialog;
    private boolean isEdit = false;
    private boolean isUpate = false;

    private NewsFragmentAdapter mAdapter;
    private RelativeLayout rlProgress;

    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    /**
     * 保存草稿的首选项
     */
    private static SharedPreferences draftPreferences;
    private HttpTools mHttpTools;
    private NewsEntity entity;
    private int lastPic = 0;

    private List<Uri> imageUris = new ArrayList<>();
    private List<PushedPhotoEntity> photoEntities = new ArrayList<>();
    private List<DiaryPhotoEntity> localEntities = new ArrayList<>();
    private List<String> deletePhoto = new ArrayList<>();
    private List<String> deleteVideo = new ArrayList<>();

    private String contentGroupId;
    private String[] reminderArrayUs;
    private List<String> list;

    /**
     * 视频Uri
     */
    private Uri videoUri = Uri.EMPTY;
    /**
     * 视频时长
     */
    private String duration;
    /**
     * 得到的视频显示在这个控件上
     */
    private View previewVideoView;
    private ImageView ivDisplay;
    private TextView tvDuration;

    /**
     * 新闻分类id
     */
    private String categoryId;
    /**
     * 新闻标题
     */
    private EditText titleDesc;

    private WallEditView wevContent;
    private View headView;
    private LinearLayout llLocation;

    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_PROFILE_PHOTO = 3;

    private double latitude;
    private double longitude;
    private Gson gson;
    private CallBack callBack = new CallBack();

    private int headOrBackdropImage;

    /**
     * 地址描述
     */
    private TextView tvLocationDesc;
    /**
     * 地图类型
     */
    private String loc_type;

    public static WriteNewsFragment newInstance(String... params) {

        return createInstance(new WriteNewsFragment());
    }

    public WriteNewsFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_write_new;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        try {
            contentGroupId = getArguments().getString(ARG_PARAM_PREFIX + "0");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContext=getActivity();
        isEdit = !TextUtils.isEmpty(contentGroupId);
        rl_category = getViewById(R.id.rl_category);
        category_tv = getViewById(R.id.category_tv);
        rvImages = getViewById(R.id.rcv_post_photos);
        titleDesc = getViewById(R.id.title_desc);

        mHttpTools = new HttpTools(getContext());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvImages.setLayoutManager(llm);
        mAdapter = new NewsFragmentAdapter(getContext(),photoEntities);
        rl_category.setOnClickListener(this);

        rlProgress = getViewById(R.id.rl_progress);
        rlProgress.setVisibility(View.GONE);
        getViewById(R.id.tv_camera).setOnClickListener(this);
        getViewById(R.id.tv_album).setOnClickListener(this);
        getViewById(R.id.tv_location).setOnClickListener(this);
        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener(){

            @Override
            public boolean execute(View v) {
                if(v.getId() == getParentActivity().rightButton.getId()){
                    submitWall();
                }else if(v.getId() == getParentActivity().leftButton.getId()){

                }
                return true;
            }
        });

        reminderArrayUs = getActivity().getResources().getStringArray(R.array.category_item);
        list = Arrays.asList(reminderArrayUs);
        mAdapter.setListener(new ImagesRecyclerListener() {
            @Override
            public void loadHeadView(HeadHolder headHolder) {
                headView = headHolder.itemView;
                llLocation = (LinearLayout) headView.findViewById(R.id.ll_location);
                llLocation.setOnClickListener(WriteNewsFragment.this);
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
                        switch (change){
                            case CHANGE_MODE_NORMAL:
                                break;
                            case CHANGE_MODE_DELETE_AT_ALL:
                                break;
                            case CHANGE_MODE_DELETE_AT_GROUPS:
                                break;
                            case CHANGE_MODE_DELETE_AT_MEMBER:
                                break;
                        }
                        lastChange = change;

                    }
                });

                if (entity == null) {
                    entity = (NewsEntity) getActivity().getIntent().getSerializableExtra(Constant.WALL_ENTITY);
                    if (entity != null) {
                        mHandler.sendEmptyMessage(GET_WALL_SUCCEED);
                    }
                }
            }

            @Override
            public void loadVideoView(VideoHolder videoHolder) {
                previewVideoView = videoHolder.itemView;
                initVideoView();
                //如果有视频
                if (!Uri.EMPTY.equals(videoUri)) {
                    ImageLoader.getInstance().displayImage(videoUri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
                } else {
                    if (!TextUtils.isEmpty(entity.getVideo_filename())) {
                        String url = String.format(Constant.API_GET_VIDEO_THUMBNAIL, entity.getContent_creator_id(), entity.getVideo_thumbnail());
                        ImageLoader.getInstance().displayImage(url, ivDisplay, UniversalImageLoaderUtil.options);
//                        VolleyUtil.initNetworkImageView(getContext(), nivVideo, url, R.drawable.network_image_default, R.drawable.network_image_default);
                        duration = entity.getVideo_duration();
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
//                        recoverDraft();//恢复草稿
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

    }

    private List<CompressBitmapTask> tasks;
    private static final int SHOW_PROGRESS = 11;
    private static final int HIDE_PROGRESS = 12;
    private static final int ACTION_FAILED = 13;
    private static final int ACTION_SUCCEED = 14;
    private static final int GET_WALL_SUCCEED = 15;
    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case GET_WALL_SUCCEED:
                    rlProgress.setVisibility(View.GONE);
                    // 得到纬度
                    String strLatitude = entity.getLoc_latitude();
                    if (!TextUtils.isEmpty(strLatitude) && MyDateUtils.isDouble(strLatitude)) {
                        latitude = Double.valueOf(strLatitude);
                    }
                    // 得到经度
                    String strLongitude = entity.getLoc_longitude();
                    if (!TextUtils.isEmpty(strLongitude) && MyDateUtils.isDouble(strLongitude)) {
                        longitude = Double.valueOf(strLongitude);
                    }
                    loc_type = entity.getLoc_type();
                    // 地名
                    String locName = entity.getLoc_name();
                    if (!TextUtils.isEmpty(locName)) {
                        llLocation.setVisibility(View.VISIBLE);
                    } else {
                        llLocation.setVisibility(View.GONE);
                    }
                    tvLocationDesc.setText(locName);
                    wevContent.setText(entity.getText_description());
                    categoryId = entity.getCategory_id();
                    String categoryText = list.get(Integer.valueOf(categoryId).intValue() - 1);
                    if(!TextUtils.isEmpty(categoryText)){
                        category_tv.setText(categoryText);
                    }else {
                        category_tv.setText(list.get(0));
                    }
                    String s = entity.getContent_title();
                    titleDesc.setText(s);

                    String videoName = entity.getVideo_filename();
                    if (TextUtils.isEmpty(videoName)) {
                        mAdapter.setIsPhoto(true);
                        // 检测网络上的图片
                        int photoCount = Integer.valueOf(entity.getPhoto_count());
                        LogUtil.d(TAG, "GET_WALL_SUCCEED photoCount = " + photoCount);
                        if (photoCount > 0) {
                            Map<String, String> condition = new HashMap<>();
                            condition.put("content_id", entity.getContent_id());
                            Map<String, String> params = new HashMap<>();
                            params.put("condition", UrlUtil.mapToJsonstring(condition));
                            String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
                            mAdapter.setRequest_url(url);
                        }
                    } else {
                        mAdapter.setIsPhoto(false);
                    }
                    break;
                case SHOW_PROGRESS:
                    rlProgress.setVisibility(View.VISIBLE);
                    break;
                case ACTION_SUCCEED:
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        Intent intent = getParentActivity().getIntent();
                        intent.putExtra(Constant.WALL_ENTITY, entity);
                        intent.putExtra(Constant.IS_DELETE, false);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                    mHandler.sendEmptyMessage(HIDE_PROGRESS);
                    break;
                case HIDE_PROGRESS:
                    rlProgress.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    });

    /**
     * 返回键监听
     *
     * @return
     */
    public boolean backCheck() {
//        if(isEventDate()) {
//            showSaveAlert();
//            return true;
//        } else {
//            getParentActivity().finish();
//            return false;
//        }
        return false;
    }

    @Override
    public void requestData() {
        if (isEdit) {
            if (entity == null) {
                UserEntity userEntity = (UserEntity) getActivity().getIntent().getSerializableExtra(Constant.WALL_ENTITY);
                if (userEntity != null) {
                    return;
                }
            }

            HashMap<String, String> params = new HashMap<>();
            params.put(Constant.CONTENT_GROUP_ID, contentGroupId);
            params.put(Constant.USER_ID, MainActivity.getUser().getUser_id());
            callBack.setLinkType(CallBack.LINK_TYPE_GET_WALL);
            mHttpTools.get(Constant.API_WALL_DETAIL, params, TAG, callBack);
        } else {
            rlProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())) {
            case R.id.rl_category:
                showCateGoryDialog();
                break;
            case R.id.tv_camera:
//                openPhotos();
                openChooseDialog();
                break;
            case R.id.tv_album:
                openPhotos();
                break;
            case R.id.tv_location:
                checkGPS();
                break;
            case R.id.delete_video_view:
                isUpate = isEdit;
                clearVideo();
                mAdapter.notifyItemRemoved(1);
                break;
            case R.id.go_to_preview_video_iv:
                if (!Uri.EMPTY.equals(videoUri)) {
                    Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
                    intent.putExtra(PreviewVideoActivity.EXTRA_VIDEO_URI, videoUri.toString());
                    startActivity(intent);
                } else if (entity != null && !TextUtils.isEmpty(entity.getVideo_filename())) {
                    showPreviewVideo();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if (isEdit) {
                // 进入编辑确定Wall是更新了
                isUpate = true;
            }
        }
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case Constant.INTENT_REQUEST_HEAD_CAMERA:
                    clearVideo();
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mContext, CACHE_PIC_NAME_TEMP,true));
                    imageUris.add(uri);
                    ArrayList<Uri> pickUris2 = new ArrayList<>();
                    pickUris2.add(uri);
                    addDataAndNotify(pickUris2);
                    break;
                case Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO:
                    if (data != null) {
                        String type = data.getStringExtra(MediaData.EXTRA_MEDIA_TYPE);
                        if (MediaData.TYPE_VIDEO.equals(type)) {
                            //视频
                            LogUtil.i(TAG, "onActivityResult& play video uri: " + data.getData());
                            addVideoFromActivityResult(data);//添加视频
                        } else {
                            //图片
                            clearVideo();
                            ArrayList<Uri> pickUris;
                            pickUris = data.getParcelableArrayListExtra(SelectPhotosActivity.EXTRA_IMAGES_STR);
                            imageUris.clear();
                            imageUris.addAll(pickUris);
                            addDataAndNotify(pickUris);
                        }
                    }
                    break;
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
                    break;
            }
        }
        if (requestCode == Constant.INTENT_REQUEST_OPEN_GPS && LocationUtil.isOPen(getActivity())) {
            openMap();
        }

    }}

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

    private void addVideoFromActivityResult(Intent data) {
        clearPhotos();//清除图片
//
        mAdapter.notifyDataSetChanged();
        videoUri = data.getData();
//
//        // 如果videoUri不是空的Uir则取得视频相关信息并显示
        if (!Uri.EMPTY.equals(videoUri)) {
            // 获取视频的元数据信息
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(getActivity(), videoUri);
            duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            // 使用之后需要释放资源
            metadataRetriever.release();

            // 数据上传需要使用以秒为单位，取出的时长已精确到毫秒，所以需要转换为秒。
            if (duration != null && !TextUtils.isEmpty(duration)) {
                duration = String.valueOf(Long.valueOf(duration) / 1000L);
            }

            if (ivDisplay != null) {
                // 取出视频的一帧画面显示在ivDisplay控件上
                ImageLoader.getInstance().displayImage(videoUri.toString(), ivDisplay, UniversalImageLoaderUtil.options);
                // 显示视频的时长
                tvDuration.setText(MyDateUtils.formatDuration(duration));
            }

            LogUtil.i(TAG, "addVideoFromActivityResult& videoUri: " + videoUri);
            LogUtil.i(TAG, "addVideoFromActivityResult& videoDuration: " + duration);
        }
    }

    private void initVideoView() {
        tvDuration = (TextView) previewVideoView.findViewById(R.id.duration_tv);
        ivDisplay = (ImageView) previewVideoView.findViewById(R.id.video_view_iv);
        // 删除视频
        previewVideoView.findViewById(R.id.delete_video_view).setOnClickListener(this);
        previewVideoView.findViewById(R.id.go_to_preview_video_iv).setOnClickListener(this);
    }

    /**
     * 清空图片
     */
    private void clearPhotos() {
//        LogUtil.i(TAG, "clearPhotos&");
//        // 清册选择的图片
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
     * 清空视频
     */
    private void clearVideo() {
//        LogUtil.i(TAG, "clearVideo&");
        videoUri = Uri.EMPTY;
        if (rvImages.getChildCount() > 1 && !mAdapter.isPhoto()) {
            rvImages.removeView(previewVideoView);
        }
        if (isEdit) {
            if (!TextUtils.isEmpty(entity.getVideo_filename())) {
                deleteVideo.add(entity.getVideo_id());
                entity.setVideo_filename("");
            }
        }
        mAdapter.setIsPhoto(true);
    }

    private void showCateGoryDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View categoryView = factory.inflate(R.layout.meeting_reminder_list, null);
        ListView listView = (ListView) categoryView.findViewById(R.id.reminder_list_view);
        ArrayAdapter reminderAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(reminderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryDialog.dismiss();
                categoryId = String.valueOf(i + 1);
                category_tv.setText(list.get(i));
            }
        });
        categoryDialog = new MyDialog(getParentActivity(), "", categoryView);
        if (!categoryDialog.isShowing()) {
            categoryDialog.show();
        }

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
     * 启动网络播放视频
     */
    private void showPreviewVideo() {
        // 启动网络视频预览Activity的隐式意图，也可选择显示启动PreviewVideoActivity
        Intent intent = new Intent(PreviewVideoActivity.ACTION_PREVIEW_VIDEO_ACTIVITY);
        // 传的值对应视频的content_creator_id
        intent.putExtra(PreviewVideoActivity.CONTENT_CREATOR_ID, entity.getContent_creator_id());
        // 传的值对应video_filename
        intent.putExtra(PreviewVideoActivity.VIDEO_FILENAME, entity.getVideo_filename());
        startActivity(intent);
    }

    /**
     * 打开相册，选择图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPhotos() {
//        LogUtil.d("", "openPhotos========");
        Intent intent = new Intent(getParentActivity(), SelectPhotosActivity.class);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        /**
         * 使用了Universal Image Loader库来处理图片需要返回的Uri与传统有差异，传此值用于区分
         */
        intent.putExtra(MediaData.EXTRA_USE_UNIVERSAL, true);
        intent.putExtra(MediaData.USE_VIDEO_AVAILABLE, true);
        String photoCount = entity == null ? null : entity.getPhoto_count();
        boolean hadPhotos = !imageUris.isEmpty() || (!TextUtils.isEmpty(photoCount) && Integer.valueOf(photoCount) > 0);
        LogUtil.d(TAG, "openPhotos& hadPhotos: " + hadPhotos);
        intent.putExtra(SelectPhotosActivity.EXTRA_HAD_PHOTOS, hadPhotos);
        intent.putParcelableArrayListExtra(SelectPhotosActivity.EXTRA_SELECTED_PHOTOS, (ArrayList<? extends Parcelable>) imageUris);
        int residue = SelectPhotosActivity.MAX_SELECT - photoEntities.size();
        intent.putExtra(SelectPhotosActivity.EXTRA_RESIDUE, residue);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Constant.INTENT_REQUEST_HEAD_MULTI_PHOTO);
    }

    private void openChooseDialog(){
        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);
        final Dialog showSelectDialog = new MyDialog(mContext, null, selectIntention);
        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        tvAddNewMember.setText(R.string.text_recording_video);
        tvCreateNewGroup.setText(R.string.text_take_photos);
        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Modify start by Jackie, Use custom recorder
                Intent mIntent = new Intent(MediaData.ACTION_RECORDER_VIDEO);
//                        Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                // Modify end by Jackie
                mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.9);//画质0.5
                //mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60000);//60s
//                        mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 45 * 1024 * 1024l);
                startActivityForResult(mIntent, Constant.INTENT_REQUEST_HEAD_CAMERA);//CAMERA_ACTIVITY = 1
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                showSelectDialog.dismiss();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    /**
     * 打开相机
     */
    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 2);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                .fromFile(PicturesCacheUtil.getCachePicFileByName(mContext,
                        CACHE_PIC_NAME_TEMP,true)));
        // 图片质量为高
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, Constant.INTENT_REQUEST_HEAD_CAMERA);
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
                    if (entity == null) {
                        getParentActivity().finish();
                    } else {
                        rlProgress.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        @Override
        public void onResult(String string) {
            switch (linkType) {
                case LINK_TYPE_SUBMIT_WALL:
                    resultBySubmitWall(string);
                    break;
                case LINK_TYPE_SUBMIT_PICTURE:
                    lastPic++;
                    if (lastPic == localEntities.size()) {
                        mHandler.sendEmptyMessage(ACTION_SUCCEED);
                    }
                    break;
                case LINK_TYPE_UPLOAD_VOID:
                    mHandler.sendEmptyMessage(ACTION_SUCCEED);
                    break;
                case LINK_TYPE_PUT_WALL:
                    resultByPutWall();
            }
        }

        @Override
        public void onError(Exception e) {
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

    /**
     * 新建news
     */
    private void submitWall(){
        if (rlProgress.getVisibility() == View.VISIBLE) {
            return;
        }
        //隐藏键盘
        UIUtil.hideKeyboard(getContext(), getActivity().getCurrentFocus());
        if (isEdit) {
            putWall();
            return;
        }

        String text_content = wevContent.getRelText();
        if (TextUtils.isEmpty(text_content) && photoEntities.isEmpty() && Uri.EMPTY.equals(videoUri)
                || TextUtils.isEmpty(categoryId) || TextUtils.isEmpty(titleDesc.getText().toString())) {
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

        params.put("dofeel_code", "");
        params.put("category_id",categoryId);//新闻分类id
        params.put("content_title",titleDesc.getText().toString());//新闻主题
        params.put("group_ind_type","3");//
        params.put("content_group_public","2");



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
            params.put("video_thumbnail", strThumbnail);//视频小图片
            params.put("video_duration", duration);//视频时长
            LogUtil.i(TAG, "submitPic$ strThumbnail: " + strThumbnail);
        }

        if (photoEntities.isEmpty()) {
            params.put("upload_photo", "0");
        } else {
            params.put("upload_photo", "1");
        }
        String url = Constant.API_POST_NEWS;
        callBack.setLinkType(CallBack.LINK_TYPE_SUBMIT_WALL);
        mHttpTools.upload(url, params, TAG, callBack);

    }

    /**
     * 上传视频
     */
    private void resultByPutWall(){
        if (!localEntities.isEmpty()) {

            if (entity != null) {
                submitLocalPhotos(entity.getContent_id());
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
            params.put("content_id", entity.getContent_id());
            LogUtil.i(TAG, "submitPic$ strThumbnail: " + strThumbnail);
            callBack.setLinkType(CallBack.LINK_TYPE_UPLOAD_VOID);
            mHttpTools.upload(Constant.API_UPLOAD_VIDEO, params, TAG, callBack);
        } else {
            mHandler.sendEmptyMessage(ACTION_SUCCEED);
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
                submitLocalPhotos(contentId);//上传图片
            } else {
                mHandler.sendEmptyMessage(ACTION_FAILED);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            mHandler.sendEmptyMessage(ACTION_FAILED);
        }
    }

    /**
     * 上传图片
     */
    private void submitLocalPhotos(String contentId){
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
        mHttpTools.upload(Constant.API_WALL_PIC_POST, params, TAG, callBack);

    }

    /**
     * 修改news
     */
    public void putWall() {
        String text_content = wevContent.getRelText();
        if (!isUpate && (TextUtils.isEmpty(text_content) && photoEntities.isEmpty() && Uri.EMPTY.equals(videoUri))
                || TextUtils.isEmpty(categoryId) || TextUtils.isEmpty(titleDesc.getText().toString())) {
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

        /**
         *
         */

        PutNewsEntity putNewsEntity = new PutNewsEntity();
        putNewsEntity.setNew_photo("0");
        putNewsEntity.setUser_id(MainActivity.getUser().getUser_id());
        putNewsEntity.setText_description(text_content);
        putNewsEntity.setLoc_latitude(latitudeDesc);
        putNewsEntity.setLoc_longitude(longitudeDesc);
        putNewsEntity.setLoc_name(locationDesc);
        putNewsEntity.setLoc_type(loc_type);
        putNewsEntity.setDelete_photo(deletePhoto);
        putNewsEntity.setDelete_video(deleteVideo);
        putNewsEntity.setPhoto_max(String.valueOf(photoEntities.size() - 1));
        putNewsEntity.setCategory_id(categoryId);
        putNewsEntity.setContent_title(titleDesc.getText().toString());
        if (!Uri.EMPTY.equals(videoUri)) {
            putNewsEntity.setNew_video("1");
        } else {
            ArrayList<PushedPhotoEntity> pushedPhotoEntities = new ArrayList<>();
            for (int i = 0; i < photoEntities.size() - localEntities.size(); i++) {
                String caption = getPhotoCaptionByPosition(i + 1);
                LogUtil.d(TAG, "putWall& caption: " + caption);
                PushedPhotoEntity photoEntity = photoEntities.get(i);
                photoEntity.setPhoto_caption(caption);
                pushedPhotoEntities.add(photoEntity);
            }
            putNewsEntity.setEdit_photo(pushedPhotoEntities);
        }

        if (!localEntities.isEmpty()) {
            putNewsEntity.setNew_photo("1");
        }

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_PUT_NEWS, entity.getContent_id());
        requestInfo.jsonParam = gson.toJson(putNewsEntity);
        LogUtil.d(TAG, "params: " + requestInfo.jsonParam);
        callBack.setLinkType(CallBack.LINK_TYPE_PUT_WALL);
        mHttpTools.put(requestInfo, TAG, callBack);
    }



}
