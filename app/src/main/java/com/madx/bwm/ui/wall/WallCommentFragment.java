package com.madx.bwm.ui.wall;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.App;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.WallCommentAdapter;
import com.madx.bwm.entity.WallCommentEntity;
import com.madx.bwm.entity.WallEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.interfaces.ViewClickListener;
import com.madx.bwm.ui.BaseFragment;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.ui.ViewOriginalPicesActivity;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.UIUtil;
import com.madx.bwm.widget.MyDialog;

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
public class WallCommentFragment extends BaseFragment<WallCommentActivity> implements View.OnClickListener, ViewClickListener {

    private ProgressDialog mProgressDialog;
    private String content_group_id;
    private String user_id;
    private String group_id;
    private EditText et_comment;
    private boolean isRefresh;
    private int startIndex = 0;
    private int currentPage = 1;
    private final static int offset = 20;
    private boolean loading;
    private RecyclerView rvList;

    private WallCommentAdapter adapter;

    public List<WallCommentEntity> data = new ArrayList<WallCommentEntity>();

    private WallEntity wall;

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
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getParentActivity(), R.string.text_loading);
        }


        try {
            content_group_id = getArguments().getString(ARG_PARAM_PREFIX + "0");
            group_id = getArguments().getString(ARG_PARAM_PREFIX + "2");
        } catch (Exception e) {
            e.printStackTrace();
        }

        rvList = getViewById(R.id.rv_wall_comment_list);
        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
//        rvList.setHasFixedSize(true);
//        initAdapter();

        getViewById(R.id.btn_submit).setOnClickListener(this);
        et_comment = getViewById(R.id.et_comment);


        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if ((data.size() == (currentPage * offset)) && !loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    currentPage++;
                    loading = true;
                    getComments();//再请求数据
                }
            }
        });


    }


    @Override
    public void requestData() {
        HashMap<String, String> pparams = new HashMap<String, String>();
        pparams.put("content_group_id", content_group_id);
        pparams.put("user_id", MainActivity.getUser().getUser_id());

        new HttpTools(getActivity()).get(Constant.API_WALL_DETAIL, pparams, new HttpCallback() {
            @Override
            public void onStart() {
                if (mProgressDialog != null) {
                    mProgressDialog.setTitle(R.string.text_loading);
                    mProgressDialog.show();
                }
            }

            @Override
            public void onFinish() {
                getComments();
                mProgressDialog.dismiss();
                if(wall==null){
                    getParentActivity().finish();
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

    private void getComments() {
        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put("content_group_id", content_group_id);
        jsonParams.put("group_id", group_id);
        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<>();
        params.put("condition", jsonParamsString);
        params.put("start", startIndex + "");
        params.put("limit", offset + "");

        String url = UrlUtil.generateUrl(Constant.API_WALL_COMMENT_LIST, params);

        new HttpTools(App.getContextInstance()).get(url, params, new HttpCallback() {
            @Override
            public void onStart() {

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

                if (isRefresh) {
                    isRefresh = false;
                    currentPage = 1;//还原为第一页
                    initAdapter();
                } else {
                    startIndex += data.size();
                    if (adapter == null) {
                        initAdapter();
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.addData(data);
                    }
                }
                wall.setComment_count(adapter.getItemCount()-1+"");
                loading = false;
            }

            @Override
            public void onError(Exception e) {
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
        adapter = new WallCommentAdapter(getParentActivity(), data, wall);
        adapter.setPicClickListener(this);
        adapter.setCommentActionListener(new WallCommentAdapter.CommentActionListener() {
            @Override
            public void doLove(WallCommentEntity commentEntity, boolean love) {
                doLoveComment(commentEntity, love);
            }

            @Override
            public void doDelete(String commentId) {
                removeComment(commentId);
            }
        });
        rvList.setAdapter(adapter);
//        RecyclerView.ItemAnimator animator = rvList.getItemAnimator();
//        animator.setAddDuration(2000);
//        animator.setRemoveDuration(1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (!TextUtils.isEmpty(et_comment.getText())) {
//                    MessageUtil.showMessage(getActivity(), R.string.alert_comment_null);
//                } else {
                    sendComment();
                }
                break;
        }
    }

    private void sendComment() {

        String commentText =et_comment.getText().toString();
        et_comment.setText(null);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content_group_id", content_group_id);
        params.put("comment_owner_id", MainActivity.getUser().getUser_id());
        params.put("content_type", "comment");
        params.put("comment_content", commentText);
        params.put("sticker_group_path", "");
        params.put("sticker_name", "");
        params.put("sticker_type", "post");

        new HttpTools(getActivity()).post(Constant.API_WALL_COMMENT_TEXT_POST, params, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                startIndex = 0;
                isRefresh = true;
                getComments();
                et_comment.setText("");
                getParentActivity().setResult(Activity.RESULT_OK);
                UIUtil.hideKeyboard(getActivity(), et_comment);
            }

            @Override
            public void onError(Exception e) {
                UIUtil.hideKeyboard(getActivity(), et_comment);
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

    private void doLoveComment(final WallCommentEntity commentEntity, final boolean love) {
        Log.i("WallCommentFragment", "doLoveComment& love = " + love);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("comment_id", commentEntity.getComment_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());
        new HttpTools(getActivity()).post(Constant.API_WALL_COMMENT_LOVE, params, new HttpCallback() {
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

    private void removeComment(final String commentId) {
        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_comment_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_COMMENT_DELETE, commentId), null);
                new HttpTools(getActivity()).delete(requestInfo, new HttpCallback() {
                    @Override
                    public void onStart() {
                        mProgressDialog.setTitle(R.string.text_waiting);
                        mProgressDialog.show();
                    }

                    @Override
                    public void onFinish() {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResult(String string) {
                        getParentActivity().setResult(Activity.RESULT_OK);

                        startIndex = 0;
                        isRefresh = true;
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
        if (!removeAlertDialog.isShowing()) {
            removeAlertDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
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

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_wall_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInfo requestInfo = new RequestInfo(String.format(Constant.API_WALL_DELETE, content_group_id), null);
                new HttpTools(getActivity()).put(requestInfo, new HttpCallback() {
                    @Override
                    public void onStart() {
                        mProgressDialog.setTitle(R.string.text_waiting);
                        mProgressDialog.show();
                    }

                    @Override
                    public void onFinish() {
                        mProgressDialog.dismiss();
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
        if (!removeAlertDialog.isShowing()) {
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
        startActivityForResult(intent, Constant.ACTION_COMMENT_MEMBERS);
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
        startActivityForResult(intent, Constant.ACTION_COMMENT_GROUPS);
    }
}
