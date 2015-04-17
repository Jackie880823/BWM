package com.madx.bwm.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.EventCommentAdapter;
import com.madx.bwm.entity.EventCommentEntity;
import com.madx.bwm.entity.EventEntity;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.http.VolleyUtil;
import com.madx.bwm.util.MessageUtil;
import com.madx.bwm.util.MyDateUtils;
import com.madx.bwm.util.UIUtil;
import com.madx.bwm.widget.CircularNetworkImage;
import com.madx.bwm.widget.FullyLinearLayoutManager;
import com.madx.bwm.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.EventDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment<EventDetailActivity> implements View.OnClickListener {

    private List<EventCommentEntity> data = new ArrayList<EventCommentEntity>();
    private TextView push_date;
    private TextView owner_name;
    private CircularNetworkImage owner_head;
    private TextView event_title;
    private NetworkImageView event_picture_4_location;
    private TextView event_desc;
    private TextView event_date;
    private TextView location_desc;
    private RelativeLayout btn_intent_all;
    private LinearLayout iv_intent_agree;
    private LinearLayout iv_intent_maybe;
    private LinearLayout iv_intent_no;
    private CardView btn_location;
    private TextView going_count;
    private TextView maybe_count;
    private TextView not_going_count;

    private boolean isRefresh;
    private int startIndex = 0;
    private int currentPage = 1;
    private final static int offset = 20;
    private boolean loading;
    MyDialog removeAlertDialog;

    private RecyclerView rvList;
    private EventEntity event;
    private EditText et_comment;
    private LinearLayout btn_submit;
    //    private TextView btn_submit;
    int colorIntentSelected;
    private ProgressBarCircularIndeterminate progressBar;

    public static EventDetailFragment newInstance( String... params) {
//        event = eventEntity;
        return createInstance(new EventDetailFragment(), params);
    }

    public EventDetailFragment() {
        super();

    }

    @Override
    public void onDestroy() {
        event = null;
        super.onDestroy();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_event_detail;
    }



    @Override
    public void initView() {

        progressBar = getViewById(R.id.progressBar);
        rvList = getViewById(R.id.rv_event_comment_list);
        final FullyLinearLayoutManager llm = new FullyLinearLayoutManager(getParentActivity());
//        final LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
//        rvList.setHasFixedSize(true);
        initAdapter();


        et_comment = getViewById(R.id.et_comment);
        btn_submit = getViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_comment.getText())) {
                    MessageUtil.showMessage(getActivity(), R.string.alert_comment_null);
                } else {
                    sendComment();
                }
            }
        });

        push_date = getViewById(R.id.push_date);
        owner_name = getViewById(R.id.owner_name);
        owner_head = getViewById(R.id.owner_head);
        event_title = getViewById(R.id.event_title);
        event_picture_4_location = getViewById(R.id.event_picture_4_location);

        event_desc = getViewById(R.id.event_desc);
        event_date = getViewById(R.id.event_date);
        location_desc = getViewById(R.id.location_desc);
        btn_intent_all = getViewById(R.id.btn_intent_all);
        iv_intent_agree = getViewById(R.id.iv_intent_agree);
        iv_intent_maybe = getViewById(R.id.iv_intent_maybe);
        iv_intent_no = getViewById(R.id.iv_intent_no);
        going_count = getViewById(R.id.going_count);
        maybe_count = getViewById(R.id.maybe_count);
        not_going_count = getViewById(R.id.not_going_count);
//        comment_container = getViewById(R.id.comment_container);


        btn_intent_all.setOnClickListener(this);
        iv_intent_agree.setOnClickListener(this);
        iv_intent_maybe.setOnClickListener(this);
        iv_intent_no.setOnClickListener(this);
        option_status = getViewById(R.id.option_status);
        option_cancel = getViewById(R.id.option_cancel);
        event_options = getViewById(R.id.event_options);
        option_no_going = getViewById(R.id.image_no_going);
        option_maybe = getViewById(R.id.image_maybe);
        option_going = getViewById(R.id.image_going);
//        option_no_going = getViewById(R.id.option_no_going);
//        option_maybe = getViewById(R.id.option_maybe);
//        option_going = getViewById(R.id.option_going);
        colorIntentSelected = getResources().getColor(R.color.btn_bg_color_green_press);
//        colorIntentSelected = getResources().getColor(R.color.default_text_color_while);

        //bind data
//        bindData();

        option_cancel.setOnClickListener(this);
        option_no_going.setOnClickListener(this);
        option_maybe.setOnClickListener(this);
        option_going.setOnClickListener(this);


//        ((ScrollView)getViewById(R.id.content)).On


        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((FullyLinearLayoutManager) llm).findLastVisibleItemPosition();
                int totalItemCount = llm.getItemCount();
                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
                // dy>0 表示向下滑动
                if ((data.size()==(currentPage*offset))&&!loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
                    loading = true;
                    requestComment();
                }
            }
        });


        getParentActivity().setCommandlistener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {
                if (R.id.tv_title == v.getId()) {
                    if (MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                        option_cancel.setVisibility(View.VISIBLE);
                        option_status.setVisibility(View.GONE);
                    } else {
                        option_cancel.setVisibility(View.GONE);
                        option_status.setVisibility(View.VISIBLE);

                    }
                    if(event_options.getVisibility() == View.VISIBLE){
                        event_options.setVisibility(View.GONE);
                        getParentActivity().title_icon.setImageResource(R.drawable.arrow_down);
                    }else{
                        event_options.setVisibility(View.VISIBLE);
                        getParentActivity().title_icon.setImageResource(R.drawable.arrow_up);
                    }

                } else if (R.id.ib_top_button_right == v.getId()) {
                    Intent intent = new Intent(getParentActivity(), EventEditActivity.class);
                    intent.putExtra("event", event);
                    startActivityForResult(intent, Constant.ACTION_EVENT_UPDATE);
                }
                return false;
            }
        });


    }

    private void initAdapter() {
        adapter = new EventCommentAdapter(getParentActivity(), data);
        adapter.setCommentActionListener(new EventCommentAdapter.CommentActionListener() {
            @Override
            public void doLove(EventCommentEntity commentEntity, boolean love) {
                doLoveComment(commentEntity, love);
            }

            @Override
            public void doDelete(String commentId) {
                removeComment(commentId);
            }
        });
        rvList.setAdapter(adapter);
        RecyclerView.ItemAnimator animator = rvList.getItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(1000);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(event!=null){
            bindData();
            requestComment();
        }else{
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (true){
                        if(getParentActivity()!=null&&getParentActivity().getDataDone){
                            break;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    bindData();
                    requestComment();
                }
            }.execute();
        }

    }

    public void bindData() {

        if(getParentActivity().getEventEntity()!=null) {
            event = getParentActivity().getEventEntity();
            push_date.setText(MyDateUtils.getLocalDateStringFromUTC(getActivity(), event.getGroup_creation_date()));
            owner_name.setText(event.getUser_given_name());
            VolleyUtil.initNetworkImageView(getActivity(), owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, event.getGroup_owner_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            event_title.setText(event.getGroup_name());//暂用Group_name

            event_desc.setText(event.getText_description());

            event_date.setText(MyDateUtils.getLocalDateStringFromUTC(getActivity(), event.getGroup_event_date()));
            location_desc.setText(event.getLoc_name());

            if (MainActivity.getUser().getUser_id().equals(event.getGroup_owner_id())) {
                try {
                    going_count.setText((Integer.valueOf(event.getTotal_yes()) - 1) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;
            } else {
                going_count.setText(event.getTotal_yes());
            }
            maybe_count.setText(event.getTotal_maybe());
            not_going_count.setText(event.getTotal_no());

            ResponseStatus[] statuses = ResponseStatus.values();

            for (ResponseStatus status : statuses) {
                if (status.getServerCode().equals(event.getGroup_member_response())) {
                    currentStatus = status;
                    break;
                }


            }
            changeIntentUI(currentStatus);

            if(!TextUtils.isEmpty(event.getLoc_latitude())&&!TextUtils.isEmpty(event.getLoc_longitude())) {
                event_picture_4_location.setVisibility(View.VISIBLE);
                VolleyUtil.initNetworkImageView(getActivity(), event_picture_4_location
                        , String.format(Constant.MAP_API_GET_LOCATION_PIC, event.getLoc_latitude() + "," + event.getLoc_longitude(), getActivity().getString(R.string.google_map_pic_size), event.getLoc_latitude() + "," + event.getLoc_longitude())
                        , R.drawable.network_image_default, R.drawable.network_image_default);

                event_picture_4_location.setOnClickListener(this);
//                btn_location.setOnClickListener(this);
            }else{
                event_picture_4_location.setVisibility(View.GONE);
            }
        }
    }

    private void doChangeResponse(ResponseStatus status) {
        changeIntentUI(status);
        updateIntentStatus(status);
    }

    private void changeIntentUI(ResponseStatus status) {
        if (status == null) {
            return;
        }
        switch (status) {
            case go:
                currentStatus = status;
                option_going.setImageResource(R.drawable.status_going_press);
                option_maybe.setImageResource(R.drawable.status_maybe_normal);
                option_no_going.setImageResource(R.drawable.status_not_going_normal);
                break;
            case maybe:
                currentStatus = status;
                option_going.setImageResource(R.drawable.status_going_normal);
                option_maybe.setImageResource(R.drawable.status_maybe_press);
                option_no_going.setImageResource(R.drawable.status_not_going_normal);
                break;
            case not_go:
                currentStatus = status;
                option_going.setImageResource(R.drawable.status_going_normal);
                option_maybe.setImageResource(R.drawable.status_maybe_normal);
                option_no_going.setImageResource(R.drawable.status_not_going_press);
                break;
        }
    }

    RelativeLayout event_options;
    RelativeLayout option_status;
    LinearLayout option_cancel;
    //    TextView option_cancel;
    ImageView option_no_going;
    ImageView option_maybe;
    ImageView option_going;
//    LinearLayout option_no_going;
//    LinearLayout option_maybe;
//    LinearLayout option_going;


    public EventCommentAdapter adapter;

    private void sendComment() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.API_EVENT_POST_COMMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                startIndex = 0;
                isRefresh = true;
                requestComment();
                MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                et_comment.setText("");
                UIUtil.hideKeyboard(getActivity(), et_comment);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                UIUtil.hideKeyboard(getActivity(), et_comment);
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X_BWM_DEVID", "anonymous");
                headers.put("X_BWM_TOKEN", "ccebaf1e71d606204ad1c31e86696d3e");
                headers.put("X_BWM_USEREMAIL", "kengwai.chiah@madxstudio.com");
                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("content_group_id", event.getContent_group_id());
                params.put("comment_owner_id", MainActivity.getUser().getUser_id());
                params.put("content_type", "comment");
                params.put("comment_content", et_comment.getText().toString());
                params.put("sticker_group_path", "");
                params.put("sticker_name", "");
                params.put("sticker_type", "");

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "event_comment");

    }

    @Override
    public void requestData() {
//        String group_id = (String) getArguments().get(ARG_PARAM_PREFIX+0);
//
//        if (TextUtils.isEmpty(group_id))
//            return;
//
//        HashMap<String, String> jsonParams = new HashMap<String, String>();
//
//        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
//        jsonParams.put("group_id", group_id);
//        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("condition", jsonParamsString);
//
//        String url = UrlUtil.generateUrl(Constant.API_GET_EVENT_DETAIL, params);
//        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                mProgressDialog.dismiss();
//                GsonBuilder gsonb = new GsonBuilder();
//                event = new Gson().fromJson(response,EventEntity.class);
//                bindData();
//                requestComment();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
//            }
//        });
//        stringRequest.setShouldCache(false);
//        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "request_comment");

    }


    public void requestComment(){

        if (event == null || MainActivity.getUser() == null)
            return;

        HashMap<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("user_id", MainActivity.getUser().getUser_id());
        jsonParams.put("group_id", event.getGroup_id());
        jsonParams.put("content_group_id", event.getContent_group_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("condition", jsonParamsString);
        params.put("start", startIndex + "");
        params.put("limit", offset+"");

        String url = UrlUtil.generateUrl(Constant.API_EVENT_COMMENT, params);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(response, new TypeToken<ArrayList<EventCommentEntity>>() {
                }.getType());
                if (isRefresh) {
                    isRefresh = false;
                    currentPage = 1;
                    initAdapter();
                    adapter.notifyDataSetChanged();
                } else {
                    startIndex += data.size();
                    adapter.addData(data);
                }
                if (adapter != null && adapter.getItemCount() > 0) {
                    getViewById(R.id.comment_split_line).setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "error=====" + error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "request_comment");
    }

    private void goInvitedStutus() {
        Intent intent = new Intent(getActivity(), InvitedStatusActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void goNavigation() {
        if (TextUtils.isEmpty(event.getLoc_latitude()) || TextUtils.isEmpty(event.getLoc_longitude())) {
            return;
        }
        try {
            //14为缩放比例
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", Double.valueOf(event.getLoc_latitude()), Double.valueOf(event.getLoc_longitude()), Double.valueOf(event.getLoc_latitude()), Double.valueOf(event.getLoc_longitude()));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            getActivity().startActivity(intent);
        }catch (Exception e){
            MessageUtil.showMessage(getActivity(),R.string.msg_no_map_app);
        }
    }

    private void cancelEvent() {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, String.format(Constant.API_EVENT_CANCEL, event.getGroup_id()), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X_BWM_DEVID", "anonymous");
                headers.put("X_BWM_TOKEN", "ccebaf1e71d606204ad1c31e86696d3e");
                headers.put("X_BWM_USEREMAIL", "kengwai.chiah@madxstudio.com");
                return headers;

            }
        };
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "event_cancel");
    }

    /**
     * update the user intent
     *
     * @param status 1- yes, 2- no, 3- maybe
     */
    private void updateIntentStatus(ResponseStatus status) {
        String serverCode = status.getServerCode();
        if (TextUtils.isEmpty(serverCode))
            return;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("group_id", event.getGroup_id());
        jsonParams.put("group_owner_id", event.getGroup_owner_id());
        jsonParams.put("group_member_response", serverCode);
        event.setGroup_event_status(serverCode);
        jsonParams.put("member_id", MainActivity.getUser().getUser_id());
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, String.format(Constant.API_EVENT_INTENT, event.getGroup_id()), new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    going_count.setText(result.getString("total_yes"));
                    maybe_count.setText(result.getString("total_maybe"));
                    not_going_count.setText(result.getString("total_no"));
                    getParentActivity().setResult(Activity.RESULT_OK);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X_BWM_DEVID", "anonymous");
                headers.put("X_BWM_TOKEN", "ccebaf1e71d606204ad1c31e86696d3e");
                headers.put("X_BWM_USEREMAIL", "kengwai.chiah@madxstudio.com");
                return headers;

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonParamsString.getBytes();
            }
        };
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "event_intent");
    }

    private ResponseStatus currentStatus;

    enum ResponseStatus {
        go("1"), maybe("3"), not_go("2");

        private String mServerCode;

        private ResponseStatus(String serverCode) {
            mServerCode = serverCode;
        }

        ResponseStatus getStatusByCode(String serverCode) {

            if (go.getServerCode().equals(serverCode)) {
                return go;
            } else if (maybe.getServerCode().equals(serverCode)) {
                return maybe;
            } else if (not_go.getServerCode().equals(serverCode)) {
                return not_go;
            }
            return null;
        }

        String getServerCode() {
            return mServerCode;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_intent_all:
                goInvitedStutus();
                break;
            case R.id.btn_location:
            case R.id.event_picture_4_location:
                goNavigation();
                break;
            case R.id.option_cancel:
                event_options.setVisibility(View.GONE);
                cancelEvent();
                getParentActivity().setResult(Activity.RESULT_OK);
                getParentActivity().finish();
                break;
            case R.id.image_going:
//            case R.id.option_going:
                event_options.setVisibility(View.GONE);
                if (ResponseStatus.go != currentStatus) {
                    doChangeResponse(ResponseStatus.go);
                }
                break;
            case R.id.image_maybe:
//            case R.id.option_maybe:
                event_options.setVisibility(View.GONE);
                if (ResponseStatus.maybe != currentStatus) {
                    doChangeResponse(ResponseStatus.maybe);
                }
                break;
            case R.id.image_no_going:
//            case R.id.option_no_going:

                event_options.setVisibility(View.GONE);
                if (ResponseStatus.not_go != currentStatus) {
                    doChangeResponse(ResponseStatus.not_go);
                }
                break;
        }
    }

    private void getEventResponseInfos() {
        if (event == null)
            return;


        StringRequest stringRequest = new StringRequest(String.format(Constant.API_EVENT_RESPONSE_INFOS, event.getGroup_id()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result;
                    result = new JSONObject(response);
                    going_count.setText(result.getString("total_yes"));
                    maybe_count.setText(result.getString("total_maybe"));
                    not_going_count.setText(result.getString("total_no"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "error=====" + error.getMessage());
            }
        });
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "request_comment");

    }

    private void removeComment(final String commentId) {

        removeAlertDialog = new MyDialog(getActivity(), getActivity().getString(R.string.text_tips_title), getActivity().getString(R.string.alert_comment_del));
        removeAlertDialog.setButtonAccept(getActivity().getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, String.format(Constant.API_EVENT_COMMENT_DELETE, commentId), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                        startIndex = 0;
                        isRefresh = true;
                        requestComment();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("", "error=====" + error.getMessage());
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);

                    }
                });
                stringRequest.setShouldCache(false);
                VolleyUtil.addRequest2Queue(getActivity().getApplicationContext(), stringRequest, "");
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

    private void doLoveComment(final EventCommentEntity commentEntity, final boolean love) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.API_EVENT_COMMENT_LOVE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X_BWM_DEVID", "anonymous");
                headers.put("X_BWM_TOKEN", "ccebaf1e71d606204ad1c31e86696d3e");
                headers.put("X_BWM_USEREMAIL", "kengwai.chiah@madxstudio.com");
                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("comment_id", commentEntity.getComment_id());
                params.put("love", love ? "1" : "0");// 0-取消，1-赞
                params.put("user_id", "" + MainActivity.getUser().getUser_id());

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        VolleyUtil.addRequest2Queue(getActivity(), stringRequest, "event_comment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTION_EVENT_UPDATE:
                    if (data != null && data.getSerializableExtra("event") != null) {
                        event = (EventEntity) data.getSerializableExtra("event");
                        if (event != null) {
                            bindData();
                        }
                    }
                    getParentActivity().setResult(Activity.RESULT_OK);
                    getEventResponseInfos();
                    break;
            }
        }
    }


}
