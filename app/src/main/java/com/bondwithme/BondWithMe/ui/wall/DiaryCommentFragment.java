package com.bondwithme.BondWithMe.ui.wall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.DiaryCommentAdapter;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.SendComment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiaryCommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryCommentFragment extends BaseFragment<DiaryCommentActivity> {
    private final static String TAG = DiaryCommentFragment.class.getSimpleName();

    private static final String GET_COMMENTS = TAG + "_GET_COMMENTS";
    private static final String POST_COMMENTS = TAG + "_POST_COMMENTS";
    private static final String POST＿LOVE_COMMENTS = TAG + "_POST_LOVE_COMMENT";
    private static final String UPLOAD_PIC = TAG + "_POST_PIC";
    private static final String DELETE_COMMENT = TAG + "_DELETE_COMMENT";

    private View vProgress;

    private String content_group_id;
    private String group_id;
    private String content_id;
    private boolean isRefresh;
    private int startIndex = 0;
    private final static int offset = 10;
    private boolean loading;
    private LinearLayoutManager llm;
    private RecyclerView rvList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SendComment sendCommentView;

    private DiaryCommentAdapter adapter;

    public List<WallCommentEntity> data = new ArrayList<>();

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

    public static DiaryCommentFragment newInstance(String... params) {
        return createInstance(new DiaryCommentFragment(), params);
    }


    public DiaryCommentFragment() {
        super();

        // Required empty public constructor
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_diary_comment;
    }


    @Override
    public void initView() {
        mHttpTools = new HttpTools(getActivity());
        String agreeCount = null;
        try {
            content_group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
            content_id = getArguments().getString(ARG_PARAM_PREFIX + "1");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "2");
            agreeCount = getArguments().getString(ARG_PARAM_PREFIX + "3");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView tvAgreeCount = getViewById(R.id.tv_agree_count);
        int count = 0;
        if (!TextUtils.isEmpty(agreeCount)) {
            count = Integer.valueOf(agreeCount);
        }
        tvAgreeCount.setText(String.format(getContext().getString(R.string.loves_count), count));
        getViewById(R.id.rl_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLovedMember(MainActivity.getUser().getUser_id(), content_id, WallUtil.LOVE_MEMBER_WALL_TYPE);
            }
        });

        // initView
        vProgress = getViewById(R.id.rl_progress);
        rvList = getViewById(R.id.rv_wall_comment_list);
        llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        //        initAdapter();

        rvList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (sendCommentView != null) {
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
             * @param uri 收到评论图片的URI
             */
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onReceiveBitmapUri(Uri uri) {
                if (uri != null) { // 传输图片
                    CompressBitmapTask task = new CompressBitmapTask();
                    vProgress.setVisibility(View.VISIBLE);
                    //for not work in down 11
                    if (SDKUtil.IS_HONEYCOMB) {
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
                if (data.size() >= offset && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    LogUtil.i(TAG, "onScrolled& getComments");
                    loading = true;
                    getComments();//再请求数据
                }
            }
        });
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
        if (Activity.RESULT_OK == resultCode) {
            sendCommentView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void requestData() {
        startIndex = 0;
        isRefresh = true;
        swipeRefreshLayout.setRefreshing(true);

        getComments();
    }

    private void getComments() {
        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put(Constant.CONTENT_GROUP_ID, content_group_id);
        jsonParams.put(Constant.GROUP_ID, group_id);
        jsonParams.put(Constant.USER_ID, MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<>();
        params.put("condition", jsonParamsString);
        params.put("start", startIndex + "");
        params.put(WallCommentEntity.LIMIT, offset + "");
        LogUtil.i(TAG, "getComments& startIndex: " + startIndex);

        String url = UrlUtil.generateUrl(Constant.API_WALL_COMMENT_LIST, params);

        mHttpTools.get(url, params, GET_COMMENTS, new HttpCallback() {
            @Override
            public void onStart() {}

            @Override
            public void onFinish() {}

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                //DateDeserializer ds = new DateDeserializer();
                //给GsonBuilder方法单独指定Date类型的反序列化方法
                //gsonb.registerTypeAdapter(Date.class, ds);
                Gson gson = gsonb.create();
                data = gson.fromJson(response, new TypeToken<ArrayList<WallCommentEntity>>() {
                }.getType());

                LogUtil.i(TAG, "getComments& isRefresh: " + isRefresh);
                if (isRefresh) {
                    startIndex = data.size();
                    isRefresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                    initAdapter();
                } else {
                    startIndex += data.size();
                    if (adapter != null) {
                        adapter.addData(data);
                    } else {
                        initAdapter();
                    }
                }
                loading = false;

                LogUtil.i(TAG, "getComments& size: " + adapter.getItemCount());
            }

            @Override
            public void onError(Exception e) {
                vProgress.setVisibility(View.GONE);
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

    private void setResultOK(int commentCount) {
        Intent intent = getParentActivity().getIntent();
        intent.putExtra(Constant.COMMENT_COUNT, String.valueOf(commentCount));
        getParentActivity().setResult(Activity.RESULT_OK, intent);
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new DiaryCommentAdapter(getParentActivity(), data);
            adapter.setCommentActionListener(new DiaryCommentAdapter.CommentActionListener() {
                @Override
                public void doLove(WallCommentEntity commentEntity, boolean love) {
                    doLoveComment(commentEntity, love);
                }

                @Override
                public void doDelete(String commentId) {
                    deleteComment(commentId);
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
        if (et != null) {
            commentText = et.getText().toString();
            et.setText(null);
        }
        if (TextUtils.isEmpty(commentText) && TextUtils.isEmpty(stickerGroupPath)) {
            // 如果没有输入字符且没有添加表情，不发送评论
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.CONTENT_GROUP_ID, content_group_id);
        params.put(Constant.COMMENT_OWNER_ID, MainActivity.getUser().getUser_id());
        params.put(Constant.CONTENT_TYPE, "comment");
        params.put(Constant.COMMENT_CONTENT, commentText);
        params.put(Constant.STICKER_GROUP_PATH, stickerGroupPath);
        params.put(Constant.STICKER_NAME, stickerName);
        params.put(Constant.STICKER_TYPE, stickerType);

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

                // 获取评论列表
                getComments();

                // 清除上传的表情信息
                stickerName = "";
                stickerType = "";
                stickerGroupPath = "";

                setResultOK(data.size() + 1);
                if (getActivity() != null && !getActivity().isFinishing()) {
                    UIUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
                }
            }

            @Override
            public void onError(Exception e) {
                vProgress.setVisibility(View.GONE);
                if (getActivity() != null && !getActivity().isFinishing()) {
                    UIUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
                }
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

    class CompressBitmapTask extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... params) {
            if (params == null) {
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
            if (!f.exists()) {
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put(Constant.CONTENT_GROUP_ID, content_group_id);
            params.put(Constant.COMMENT_OWNER_ID, MainActivity.getUser().getUser_id());
            params.put(Constant.CONTENT_TYPE, "comment");
            params.put(Constant.FILE, f);
            params.put(Constant.PHOTO_FULLSIZE, "1");


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

                    getComments();
                    setResultOK(data.size() + 1);
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
                vProgress.setVisibility(View.VISIBLE);
                setResultOK(data.size());
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
            }

            @Override
            public void onError(Exception e) {
                vProgress.setVisibility(View.VISIBLE);
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

                        startIndex = 0;
                        isRefresh = true;
                        swipeRefreshLayout.setRefreshing(true);

                        getComments();
                        setResultOK(data.size() - 1);
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
        if (!removeAlertDialog.isShowing()) {
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

        if (sendCommentView != null) {
            sendCommentView.commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        vProgress.setVisibility(View.GONE);
        super.onDestroy();
    }

    MyDialog removeAlertDialog;

    /**
     * 显示点赞的用户列表
     *
     * @param viewer_id {@link WallEntity#user_id}
     * @param refer_id  {@link WallEntity#content_id} or {@link WallCommentEntity#comment_id}
     * @param type      {@link WallUtil#LOVE_MEMBER_COMMENT_TYPE} or {@link WallUtil#LOVE_MEMBER_WALL_TYPE}
     */
    public void showLovedMember(String viewer_id, String refer_id, String type) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_LOVED_USER);
        intent.putExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID, viewer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_REFER_ID, refer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_TYPE, type);
        startActivity(intent);
    }

}
