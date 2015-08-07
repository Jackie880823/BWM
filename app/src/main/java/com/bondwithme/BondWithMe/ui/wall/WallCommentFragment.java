package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.WallCommentAdapter;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.ViewOriginalPicesActivity;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.SendComment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.CircularProgress;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WallCommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WallCommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallCommentFragment extends BaseFragment<WallCommentActivity> implements WallViewClickListener, View.OnClickListener {
    private final static String TAG = WallCommentFragment.class.getSimpleName();

    private static final String GET_DETAIL = TAG + "_GET_DETAIL";
    private static final String GET_COMMENTS = TAG + "_GET_COMMENTS";
    private static final String POST_COMMENTS = TAG + "_POST_COMMENTS";
    private static final String POST_LOVE = TAG + "_POST_LOVE";
    private static final String POST＿LOVE_COMMENTS = TAG + "_POST_LOVE_COMMENT";
    private static final String UPLOAD_PIC = TAG + "_POST_PIC";
    private static final String DELETE_COMMENT = TAG + "_DELETE_COMMENT";
    private static final String POST_DELETE = TAG + "_POST_DELETE";

    private CircularNetworkImage nivHead;
    private TextView tvContent;
    /**
     * 时间
     */
    private TextView tvDate;
    /**
     * 用户名
     */
    private TextView tvUserName;
    View llWallsImage;
    /**
     * 分享的网络图片显示控件
     */
    private NetworkImageView imWallsImages;
    /**
     * 图片数量统计显示
     */
    private TextView tvPhotoCount;
    /**
     *
     */
    private TextView tvAgreeCount;
    /**
     * 评论总数显示
     */
    private TextView tvCommentCount;
    private ImageButton ibAgree;
    private ImageButton ibComment;
    private ImageButton btn_del;
    private ImageView iv_mood;
    // location tag
    private LinearLayout llLocation;
    private ImageView ivLocation;
    private TextView tvLocation;
    private View vProgress;

    boolean loving = false;

    private String content_group_id;
    private String group_id;
    private boolean isRefresh;
    private int startIndex = 0;
    private final static int offset = 10;
    private boolean loading;
    private View split;
    LinearLayoutManager llm;
    private RecyclerView rvList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircularProgress progressBar;
    private SendComment sendCommentView;

    private WallCommentAdapter adapter;

    public List<WallCommentEntity> data = new ArrayList<>();

    private WallEntity wall;
    /**
     * 上传表情时的表情格式{@link Constant#Sticker_Png} 或 {@link Constant#Sticker_Gif}
     */
    private String stickerType = "";
    /**
     * 上传表情的表情名称
     */
    private String stickerName = "";
    /**
     * 上传表情的包路径
     */
    private String stickerGroupPath = "";
    /**
     * 网络请求工具实例
     */
    private HttpTools mHttpTools;

    public static WallCommentFragment newInstance(String... params) {
        return createInstance(new WallCommentFragment(), params);
    }


    public WallCommentFragment() {
        super();

        // Required empty public constructor
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_wall_comment;
    }


    @Override
    public void initView() {
        mHttpTools = new HttpTools(getActivity());
        try {
            content_group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "2");
        } catch(Exception e) {
            e.printStackTrace();
        }

        // initView
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);
        rvList = getViewById(R.id.rv_wall_comment_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        //        initAdapter();

        rvList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(sendCommentView != null) {
                    sendCommentView.hideAllViewState(true);
                }
                return false;
            }
        });

        sendCommentView = getViewById(R.id.send_comment);
        sendCommentView.initViewPager(getParentActivity(), this);
        sendCommentView.setCommentListener(new SendComment.CommentListener() {
            @Override
            public void onStickerItemClick(String type, String folderName, String filName) {
                stickerType = type;
                stickerGroupPath = folderName;
                stickerName = filName;
                sendComment(null);
            }

            /**
             * 得到图片uri
             *
             * @param uri
             */
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onReceiveBitmapUri(Uri uri) {
                if(uri != null) { // 传输图片
                    CompressBitmapTask task = new CompressBitmapTask();

                    //for not work in down 11
                    if(SDKUtil.IS_HONEYCOMB) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
                    } else {
                        task.execute(uri);
                    }
                }
            }

            @Override
            public void onSendCommentClick(EditText et) {
                sendComment(et);
            }

            @Override
            public void onRemoveClick() {
                stickerType = "";
                stickerGroupPath = "";
                stickerName = "";
            }
        });
        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.i(TAG, "onRefresh&");
                isRefresh = true;
                startIndex = 0;
                requestData();
            }

        });

        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = llm.findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if(data.size() >= offset && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    LogUtil.i(TAG, "onScrolled& getComments");
                    loading = true;
                    getComments();//再请求数据
                }
            }
        });
    }

    private void initWallView(View wallView) {
        if(wallView != null) {
            split = wallView.findViewById(R.id.comment_split_line);
            nivHead = (CircularNetworkImage) wallView.findViewById(R.id.owner_head);
            tvUserName = (TextView) wallView.findViewById(R.id.owner_name);
            tvContent = (TextView) wallView.findViewById(R.id.tv_wall_content);
            tvDate = (TextView) wallView.findViewById(R.id.push_date);
            llWallsImage = wallView.findViewById(R.id.ll_walls_image);
            imWallsImages = (NetworkImageView) wallView.findViewById(R.id.iv_walls_images);
            tvPhotoCount = (TextView) wallView.findViewById(R.id.tv_wall_photo_count);
            tvAgreeCount = (TextView) wallView.findViewById(R.id.tv_wall_agree_count);
            tvCommentCount = (TextView) wallView.findViewById(R.id.tv_wall_relay_count);
            ibAgree = (ImageButton) wallView.findViewById(R.id.iv_love);
            ibComment = (ImageButton) wallView.findViewById(R.id.iv_comment);
            btn_del = (ImageButton) wallView.findViewById(R.id.btn_del);
            iv_mood = (ImageView) wallView.findViewById(R.id.iv_mood);
            llLocation = (LinearLayout) wallView.findViewById(R.id.ll_location);
            ivLocation = (ImageView) wallView.findViewById(R.id.iv_location);
            tvLocation = (TextView) wallView.findViewById(R.id.tv_location);

            wallView.findViewById(R.id.ll_love).setOnClickListener(this);
            llLocation.setOnClickListener(this);
            ivLocation.setOnClickListener(this);
            tvLocation.setOnClickListener(this);

            ibAgree.setOnClickListener(this);
            //            ibComment.setOnClickListener(this);
            btn_del.setOnClickListener(this);
            imWallsImages.setOnClickListener(this);
        }
    }

    private void initListHeadView(View listHeadView) {

        if(listHeadView != null) {
            progressBar = (CircularProgress) listHeadView.findViewById(R.id.wall_comment_progress_bar);
            if(loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
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
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);
        sendCommentView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("content_group_id", content_group_id);
        params.put("user_id", MainActivity.getUser().getUser_id());

        mHttpTools.get(Constant.API_WALL_DETAIL, params, GET_DETAIL, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                if(wall == null) {
                    getParentActivity().finish();
                } else {
                    vProgress.setVisibility(View.GONE);
                    getComments();
                }
            }

            @Override
            public void onResult(String string) {
                wall = new Gson().fromJson(string, WallEntity.class);
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                getParentActivity().finish();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private void setWallContext() {
        VolleyUtil.initNetworkImageView(getParentActivity(), nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wall.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        String atDescription = TextUtils.isEmpty(wall.getText_description()) ? "" : wall.getText_description();
        tvContent.setText(atDescription);
        // 设置文字可点击，实现特殊文字点击跳转必需添加些设置
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        int tagMemberCount = wall.getTag_member().size();
        int tagGroupCount = wall.getTag_group().size();
        if(tagMemberCount > 0 || tagGroupCount > 0) {
            WallUtil wallUtil = new WallUtil(getActivity(), this);
            wallUtil.setSpanContent(tvContent, wall, atDescription, tagMemberCount, tagGroupCount);
        }

        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(getParentActivity(), wall.getContent_creation_date()));
        //            tvTime.setText(wall.getTime());
        tvUserName.setText(wall.getUser_given_name());
        if(TextUtils.isEmpty(wall.getFile_id())) {
            llWallsImage.setVisibility(View.GONE);
        } else {
            llWallsImage.setVisibility(View.VISIBLE);

            VolleyUtil.initNetworkImageView(getParentActivity(), imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, wall.getUser_id(), wall.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);

            // 有图片显示图片总数
            int count = Integer.valueOf(wall.getPhoto_count());
            if(count > 1) {
                String photoCountStr;
                photoCountStr = count + " " + getString(R.string.text_photos);
                tvPhotoCount.setText(photoCountStr);
                tvPhotoCount.setVisibility(View.VISIBLE);
            } else {
                tvPhotoCount.setVisibility(View.GONE);
            }
        }

         /*is owner wall*/
        //        if (!TextUtils.isEmpty(wall.getUser_id())&&wall.getUser_id().equals("49")) {
        //            ibDelete.setVisibility(View.VISIBLE);
        //        } else {
        //            ibDelete.setVisibility(View.GONE);
        //        }

        try {
            if(wall.getDofeel_code() != null) {
                StringBuilder b = new StringBuilder(wall.getDofeel_code());
                int charIndex = wall.getDofeel_code().lastIndexOf("_");
                b.replace(charIndex, charIndex + 1, "/");

                InputStream is = getParentActivity().getAssets().open(b.toString());
                iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
            } else {
                iv_mood.setVisibility(View.GONE);
            }
        } catch(Exception e) {
            iv_mood.setVisibility(View.GONE);
        }

        /*location*/
        //        if (TextUtils.isEmpty(wall.getLoc_name())) {
        //            at.setVisibility(View.GONE);
        //        } else {
        //            at.setVisibility(View.VISIBLE);
        //            tvLocation.setText(wall.getLoc_name());
        //        }

        tvAgreeCount.setText(wall.getLove_count());
        tvCommentCount.setText(wall.getComment_count());


        if(MainActivity.getUser().getUser_id().equals(wall.getUser_id())) {
            btn_del.setVisibility(View.VISIBLE);
        } else {
            btn_del.setVisibility(View.GONE);
        }

        if(TextUtils.isEmpty(wall.getLove_id())) {
            ibAgree.setImageResource(R.drawable.love_normal);
        } else {
            ibAgree.setImageResource(R.drawable.love_press);
        }

        if(TextUtils.isEmpty(wall.getLoc_name()) || TextUtils.isEmpty(wall.getLoc_latitude()) || TextUtils.isEmpty(wall.getLoc_longitude())) {
            llLocation.setVisibility(View.GONE);
        } else {
            llLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(wall.getLoc_name());
        }
    }

    private void getComments() {
        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put(WallCommentEntity.CONTENT_GROUP_ID, content_group_id);
        jsonParams.put(WallCommentEntity.GROUP_ID, group_id);
        jsonParams.put(WallCommentEntity.USER_ID, MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<>();
        params.put("condition", jsonParamsString);
        params.put("start", startIndex + "");
        params.put(WallCommentEntity.LIMIT, offset + "");

        String url = UrlUtil.generateUrl(Constant.API_WALL_COMMENT_LIST, params);

        mHttpTools.get(url, params, GET_COMMENTS, new HttpCallback() {
            @Override
            public void onStart() {
                if(progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                //DateDeserializer ds = new DateDeserializer();
                //给GsonBuilder方法单独指定Date类型的反序列化方法
                //gsonb.registerTypeAdapter(Date.class, ds);
                Gson gson = gsonb.create();
                data = gson.fromJson(response, new TypeToken<ArrayList<WallCommentEntity>>() {}.getType());

                if(isRefresh) {
                    isRefresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                    initAdapter();
                } else {
                    startIndex += data.size();
                    if(adapter != null) {
                        adapter.addData(data);
                    } else {
                        initAdapter();
                    }
                }

                if(split != null) {
                    if(adapter != null && adapter.getItemCount() > 0) {
                        split.setVisibility(View.VISIBLE);
                    } else {
                        split.setVisibility(View.GONE);
                    }
                }
                if(progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                loading = false;
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                loading = false;
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


    }

    private void initAdapter() {
        if(adapter == null) {
            adapter = new WallCommentAdapter(getParentActivity(), data);
            adapter.setPicClickListener(this);
            adapter.setCommentActionListener(new WallCommentAdapter.CommentActionListener() {
                @Override
                public void doLove(WallCommentEntity commentEntity, boolean love) {
                    doLoveComment(commentEntity, love);
                }

                @Override
                public void doDelete(String commentId) {
                    deleteComment(commentId);
                }
            });
            adapter.setUpdateListener(new WallCommentAdapter.ListViewItemViewUpdateListener() {
                @Override
                public void updateWallView(View wallView) {
                    initWallView(wallView);
                    if(wall != null) {
                        setWallContext();
                    }
                }

                @Override
                public void updateListHeadView(View listHeadView) {
                    initListHeadView(listHeadView);
                }
            });
            rvList.setAdapter(adapter);
        } else {
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }
        //        RecyclerView.ItemAnimator animator = rvList.getItemAnimator();
        //        animator.setAddDuration(2000);
        //        animator.setRemoveDuration(1000);
    }

    private void sendComment(EditText et) {
        String commentText = "";
        if(et != null) {
            commentText = et.getText().toString();
            et.setText(null);
        }
        if(TextUtils.isEmpty(commentText) && TextUtils.isEmpty(stickerGroupPath)) {
            // 如果没有输入字符且没有添加表情，不发送评论
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("content_group_id", content_group_id);
        params.put("comment_owner_id", MainActivity.getUser().getUser_id());
        params.put("content_type", "comment");
        params.put("comment_content", commentText);
        params.put("sticker_group_path", stickerGroupPath);
        params.put("sticker_name", stickerName);
        params.put("sticker_type", stickerType);

        mHttpTools.post(Constant.API_WALL_COMMENT_TEXT_POST, params, POST_COMMENTS, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {

                startIndex = 0;
                isRefresh = true;
                swipeRefreshLayout.setRefreshing(true);

                // 更新评论总数
                int commentCount = Integer.valueOf((String) tvCommentCount.getText()) + 1;
                wall.setComment_count(String.valueOf(commentCount));
                tvCommentCount.setText(String.valueOf(commentCount));

                // 获取评论列表
                getComments();

                // 清除上传的表情信息
                stickerName = "";
                stickerType = "";
                stickerGroupPath = "";

                getParentActivity().setResult(Activity.RESULT_OK);
                UIUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                UIUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ll_love:
            case R.id.iv_love:
                int count = Integer.valueOf(tvAgreeCount.getText().toString());
                if(TextUtils.isEmpty(wall.getLove_id())) {
                    tvAgreeCount.setText(count + 1 + "");
                    ibAgree.setImageResource(R.drawable.love_press);
                    wall.setLove_id(MainActivity.getUser().getUser_id());
                } else {
                    ibAgree.setImageResource(R.drawable.love_normal);
                    wall.setLove_id(null);
                    tvAgreeCount.setText(count - 1 + "");
                }
                LogUtil.i(TAG, "love count = " + count);
                //判断是否已经有进行中的判断
                if(!loving) {
                    LogUtil.i(TAG, "prepare love");
                    loving = true;
                    check();
                } else {
                    LogUtil.i(TAG, "not love");
                }
                break;
            case R.id.iv_walls_images:
                showOriginalPic(wall.getContent_id());
                break;
            case R.id.btn_del:
                remove(wall.getContent_group_id());
                break;
            case R.id.iv_location:
            case R.id.tv_location:
            case R.id.ll_location:
                gotoLocationSetting(wall);
                break;
        }
    }

    private void gotoLocationSetting(WallEntity wall) {
        if(TextUtils.isEmpty(wall.getLoc_latitude()) || TextUtils.isEmpty(wall.getLoc_longitude())) {
            return;
        }
        LocationUtil.goNavigation(getActivity(), Double.valueOf(wall.getLoc_latitude()), Double.valueOf(wall.getLoc_longitude()), wall.getLoc_type());
    }

    private void check() {

        // 数据有修改设置result 为 Activity.RESULT_OK
        getParentActivity().setResult(Activity.RESULT_OK);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //缓冲时间为100
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                loving = false;

                if(TextUtils.isEmpty(wall.getLove_id())) {
                    postLove(wall, false);
                } else {
                    postLove(wall, true);
                }
            }
        }).start();
    }

    private void postLove(WallEntity wallEntity, boolean love) {

        HashMap<String, String> params = new HashMap<>();
        params.put("content_id", wallEntity.getContent_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());

        RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE, params);

        mHttpTools.post(requestInfo, POST_LOVE, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.i(TAG, "onStart");
            }

            @Override
            public void onFinish() {
                LogUtil.i(TAG, "onFinish");
            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "onResult");
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    class CompressBitmapTask extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... params) {
            if(params == null) {
                return null;
            }
            return LocalImageLoader.compressBitmap(getActivity(), params[0], 480, 800, false);
        }

        @Override
        protected void onPostExecute(String path) {
            submitPic(path);
        }

        private void submitPic(String path) {
            File f = new File(path);
            if(!f.exists()) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> params = new HashMap<>();
            params.put("content_group_id", content_group_id);
            params.put("comment_owner_id", MainActivity.getUser().getUser_id());
            params.put("content_type", "comment");
            params.put("file", f);
            params.put("photo_fullsize", "1");


            mHttpTools.upload(Constant.API_WALL_COMMENT_PIC_POST, params, UPLOAD_PIC, new HttpCallback() {
                @Override
                public void onStart() {
                    vProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    vProgress.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String string) {
                    startIndex = 0;
                    isRefresh = true;
                    swipeRefreshLayout.setRefreshing(true);

                    // 更新评论总数
                    int commentCount = Integer.valueOf((String) tvCommentCount.getText()) + 1;
                    wall.setComment_count(String.valueOf(commentCount));
                    tvCommentCount.setText(String.valueOf(commentCount));

                    getComments();
                    getParentActivity().setResult(Activity.RESULT_OK);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onCancelled() {
                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
    }

    private void doLoveComment(final WallCommentEntity commentEntity, final boolean love) {
        LogUtil.i("WallCommentFragment", "doLoveComment& love = " + love);
        HashMap<String, String> params = new HashMap<>();
        params.put("comment_id", commentEntity.getComment_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());
        mHttpTools.post(Constant.API_WALL_COMMENT_LOVE, params, POST＿LOVE_COMMENTS, new HttpCallback() {
            @Override
            public void onStart() {
                getParentActivity().setResult(Activity.RESULT_OK);
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
            }

            @Override
            public void onError(Exception e) {
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {
            }
        });
    }

    private void deleteComment(final String commentId) {
        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_comment_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_COMMENT_DELETE, commentId), null);
                mHttpTools.delete(requestInfo, DELETE_COMMENT, new HttpCallback() {
                    @Override
                    public void onStart() {
                        vProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        vProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String string) {
                        getParentActivity().setResult(Activity.RESULT_OK);

                        startIndex = 0;
                        isRefresh = true;
                        swipeRefreshLayout.setRefreshing(true);

                        int commentCount = Integer.valueOf((String) tvCommentCount.getText()) - 1;
                        if(commentCount >= 0) {
                            wall.setComment_count(String.valueOf(commentCount));
                            tvCommentCount.setText(String.valueOf(commentCount));
                        }

                        getComments();
                    }

                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
                removeAlertDialog.dismiss();

            }
        });
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if(!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
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

        if(sendCommentView != null) {
            sendCommentView.commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        vProgress.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void showOriginalPic(String content_id) {
        Intent intent = new Intent(getActivity(), ViewOriginalPicesActivity.class);
        Map<String, String> condition = new HashMap<>();
        condition.put("content_id", content_id);
        Map<String, String> params = new HashMap<>();
        params.put("condition", UrlUtil.mapToJsonstring(condition));
        String url = UrlUtil.generateUrl(Constant.GET_MULTI_ORIGINALPHOTO, params);
        intent.putExtra("request_url", url);
        startActivity(intent);
    }

    /**
     * @param content_group_id
     * @param group_id
     */
    @Override
    public void showComments(String content_group_id, String group_id) {

    }


    MyDialog removeAlertDialog;

    @Override
    public void remove(final String content_group_id) {

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_diary_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, content_group_id), null);
                mHttpTools.put(requestInfo, POST_DELETE, new HttpCallback() {
                    @Override
                    public void onStart() {
                        vProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        vProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResult(String string) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                        getParentActivity().setResult(Activity.RESULT_OK);
                        getParentActivity().finish();
                    }

                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
                removeAlertDialog.dismiss();
            }
        });
        removeAlertDialog.setButtonCancel(getActivity().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAlertDialog.dismiss();
            }
        });
        if(!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }

    }

    /**
     * 显示被@的用户列表
     *
     * @param content_group_id
     * @param group_id
     */
    @Override
    public void showMembers(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_NOTIFY_USER);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivity(intent);
    }

    /**
     * 显示被@的群组列表
     *
     * @param content_group_id
     * @param group_id
     */
    @Override
    public void showGroups(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_NOTIFY_GROUP);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivity(intent);
    }

}
