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

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.WallCommentAdapter;
import com.bondwithme.BondWithMe.adapter.WallHolder;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
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
import com.material.widget.CircularProgress;

import java.io.File;
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
public class WallCommentFragment extends BaseFragment<WallCommentActivity> implements WallViewClickListener {
    private final static String TAG = WallCommentFragment.class.getSimpleName();

    private static final String GET_DETAIL = TAG + "_GET_DETAIL";
    private static final String GET_COMMENTS = TAG + "_GET_COMMENTS";
    private static final String POST_COMMENTS = TAG + "_POST_COMMENTS";
    private static final String POST＿LOVE_COMMENTS = TAG + "_POST_LOVE_COMMENT";
    private static final String UPLOAD_PIC = TAG + "_POST_PIC";
    private static final String DELETE_COMMENT = TAG + "_DELETE_COMMENT";
    private static final String POST_DELETE = TAG + "_POST_DELETE";

    private WallHolder holder;

    private View vProgress;

    private String content_group_id;
    private String group_id;
    private boolean isRefresh;
    private int startIndex = 0;
    private final static int offset = 10;
    private boolean loading;
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
             * @param uri 收到评论图片的URI
             */
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onReceiveBitmapUri(Uri uri) {
                if(uri != null) { // 传输图片
                    CompressBitmapTask task = new CompressBitmapTask();
                    vProgress.setVisibility(View.VISIBLE);
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

        holder = new WallHolder(getParentActivity(), wallView, mHttpTools, true);
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
                if (wall == null) {
                    getParentActivity().finish();
                } else {
                    vProgress.setVisibility(View.GONE);
                    getComments();
                }
            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "request& onResult# response: " + response);
                wall = new Gson().fromJson(response, WallEntity.class);
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
        holder.setViewClickListener(this);
        holder.setWallEntity(wall);
        holder.setContent(wall, getActivity());
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
        LogUtil.i(TAG, "getComments& startIndex: " + startIndex);

        String url = UrlUtil.generateUrl(Constant.API_WALL_COMMENT_LIST, params);

        mHttpTools.get(url, params, GET_COMMENTS, new HttpCallback() {
            @Override
            public void onStart() {
                if (progressBar != null) {
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

                LogUtil.i(TAG, "getComments& size: " + adapter.getItemCount());
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                loading = false;
            }

            @Override
            public void onError(Exception e) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
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

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

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
                int commentCount = Integer.valueOf((String) holder.getTvCommentCount().getText()) + 1;
                wall.setComment_count(String.valueOf(commentCount));
                holder.getTvCommentCount().setText(String.valueOf(commentCount));

                // 获取评论列表
                getComments();

                // 清除上传的表情信息
                stickerName = "";
                stickerType = "";
                stickerGroupPath = "";

                getParentActivity().setResult(Activity.RESULT_OK);
                if(getActivity()!=null&&!getActivity().isFinishing()) {
                    UIUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
                }
            }

            @Override
            public void onError(Exception e) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if(getActivity()!=null&&!getActivity().isFinishing()) {
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
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("content_group_id", content_group_id);
            params.put("comment_owner_id", MainActivity.getUser().getUser_id());
            params.put("content_type", "comment");
            params.put("file", f);
            params.put("photo_fullsize", "1");


            mHttpTools.upload(Constant.API_WALL_COMMENT_PIC_POST, params, UPLOAD_PIC, new HttpCallback() {
                @Override
                public void onStart() {

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
                    int commentCount = Integer.valueOf((String) holder.getTvCommentCount().getText()) + 1;
                    wall.setComment_count(String.valueOf(commentCount));
                    holder.getTvCommentCount().setText(String.valueOf(commentCount));

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
                vProgress.setVisibility(View.VISIBLE);
                getParentActivity().setResult(Activity.RESULT_OK);
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
                        getParentActivity().setResult(Activity.RESULT_OK);

                        startIndex = 0;
                        isRefresh = true;
                        swipeRefreshLayout.setRefreshing(true);

                        int commentCount = Integer.valueOf((String) holder.getTvCommentCount().getText()) - 1;
                        if(commentCount >= 0) {
                            wall.setComment_count(String.valueOf(commentCount));
                            holder.getTvCommentCount().setText(String.valueOf(commentCount));
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

    /**
     * 显示Wall详情包括评论
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    @Override
    public void showComments(String content_group_id, String group_id) {}


    MyDialog removeAlertDialog;

    /**
     * 删除Wall
     *
     * @param content_group_id {@link WallEntity#content_group_id}
     */
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
//                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
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
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
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
     * @param content_group_id {@link WallEntity#content_group_id}
     * @param group_id         {@link WallEntity#group_id}
     */
    @Override
    public void showGroups(String content_group_id, String group_id) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_NOTIFY_GROUP);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        startActivity(intent);
    }

    /**
     * 显示点赞的用户列表
     *
     * @param viewer_id {@link WallEntity#user_id}
     * @param refer_id  {@link WallEntity#content_id} or {@link WallCommentEntity#comment_id}
     * @param type      {@link WallUtil#LOVE_MEMBER_COMMENT_TYPE} or {@link WallUtil#LOVE_MEMBER_WALL_TYPE}
     */
    @Override
    public void showLovedMember(String viewer_id, String refer_id, String type) {
        Intent intent = new Intent(getActivity(), WallMembersOrGroupsActivity.class);
        intent.setAction(Constant.ACTION_SHOW_LOVED_USER);
        intent.putExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID, viewer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_REFER_ID, refer_id);
        intent.putExtra(WallUtil.GET_LOVE_LIST_TYPE, type);
        startActivity(intent);
    }

}
