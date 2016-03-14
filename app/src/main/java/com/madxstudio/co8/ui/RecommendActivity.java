package com.madxstudio.co8.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.RecommendAdapter;
import com.madxstudio.co8.entity.RecommendEntity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class RecommendActivity extends BaseActivity {

    public static String TAG = "RecommendActivity";
    View mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private int startIndex = 0;
    private int offSet = 20;
    private boolean loading;
    private List<RecommendEntity> data = new ArrayList<>();
    private RecommendAdapter adapter;
    private RecyclerView rvList;
    private LinearLayoutManager llm;
    private TextView tvNoDate;
    protected LinearLayout llSelect;
    private boolean isEditingMode;
    private boolean isAllSelected;
    private Button btnAllSelect;
    private Button btnInverseSelect;
    private Button btnRemove;

    private final int ALL_SELECT = 1;
    private final int CANCEL_ALL = 2;
    private final int INVERSE_SELECT = 3;
    private final int DELETE_RECOMMEND = 4;
    private int selectedRecommendCount;
    

    public int getLayout() {
        return R.layout.activity_recommend;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_recommended_alert);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setImageResource(R.drawable.icon_delete);

    }

    @Override
    protected void titleRightEvent() {
        if (!isEditingMode){
            isEditingMode = true;
            adapter.setEditingMode(isEditingMode);
            adapter.notifyDataSetChanged();
            llSelect.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.GONE);
            yearButton.setVisibility(View.VISIBLE);
            yearButton.setTextSize(14);
            yearButton.setText(R.string.text_cancel);
            btnRemove.setText(R.string.text_delete);
        }else {
            isEditingMode = false;
            adapter.setEditingMode(isEditingMode);
            adapter.notifyDataSetChanged();
            llSelect.setVisibility(View.GONE);
            rightButton.setVisibility(View.VISIBLE);
            yearButton.setVisibility(View.GONE);
            setAllUnselected();


        }
    }


    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        mProgressDialog = getViewById(R.id.rl_progress);
        mProgressDialog.setVisibility(View.VISIBLE);
        tvNoDate = getViewById(R.id.tv_no_data_display);
        llSelect = getViewById(R.id.ll_select);
        btnAllSelect = getViewById(R.id.btn_all_select);
        btnInverseSelect = getViewById(R.id.btn_inverse_select);
        btnRemove = getViewById(R.id.btn_delete);

        btnAllSelect.setOnClickListener(this);
        btnInverseSelect.setOnClickListener(this);
        btnRemove.setOnClickListener(this);


        rvList = getViewById(R.id.rvList);
        llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        rvList.setItemAnimator(null);
        initAdapter();

//        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                isRefresh = true;
//                startIndex = 0;
//                requestData();
//            }
//
//        });

//        rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastVisibleItem = ((LinearLayoutManager) llm).findLastVisibleItemPosition();
//                int totalItemCount = llm.getItemCount();
//                //lastVisibleItem >= totalItemCount - 5 表示剩下5个item自动加载
//                // dy>0 表示向下滑动
//                if (!loading && lastVisibleItem >= totalItemCount - 5 && dy > 0) {
//                    loading = true;
//                    requestData();//再请求数据
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void finishReFresh() {
//        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
        mProgressDialog.setVisibility(View.INVISIBLE);
    }

//    void sdf()
//    {
//        findViewById(R.id.iv_move);
//
//    }

    @Override
    public void requestData() {


//        Map<String, String> params = new HashMap<>();
//        params.put("start", "" + startIndex);
//        params.put("limit", "" + offSet);


        new HttpTools(this).get(String.format(Constant.API_BONDALERT_RECOMMEND, MainActivity.getUser().getUser_id()), null, this,new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.setVisibility(View.INVISIBLE);
                if (isRefresh) {
                    finishReFresh();
                }
            }

            @Override
            public void onResult(String string) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<RecommendEntity>>() {
                }.getType());

                if (!data.isEmpty()){
                    tvNoDate.setVisibility(View.GONE);
                }else if (data.isEmpty() && !RecommendActivity.this.isFinishing()){
                    tvNoDate.setText(getResources().getString(R.string.text_no_date_recommended));
                }

                finishReFresh();
                initAdapter();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                MessageUtil.showMessage(RecommendActivity.this, R.string.msg_action_failed);
                if (isRefresh) {
                    finishReFresh();
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

    //begin
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecommendAdapter.CHOOSE_RELATION_CODE:
                if (resultCode == RESULT_OK) {
                    String relationShip = data.getStringExtra("relationship");
                    if (!TextUtils.isEmpty(relationShip)) {
                        addUser(relationShip);
                    }
                }
        }
    }

    private void addUser(final String relationShip) {


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("user_relationship_name", relationShip);
        params.put("member_id", data.get(adapter.getPositionId()).getUser_id());

        new HttpTools(this).post(Constant.API_ADD_MEMBER, params, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("Success".equals(jsonObject.getString("response_status"))) {
                        Toast.makeText(RecommendActivity.this, getResources().getString(R.string.text_success_add_member), Toast.LENGTH_SHORT).show();
                        adapter.setAdapterNotifyDataSetChanged(adapter.getPositionId());
                        // finish();
                    } else {
                        Toast.makeText(RecommendActivity.this, getResources().getString(R.string.text_fail_add_member), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(RecommendActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    //end
    private void initAdapter() {
        adapter = new RecommendAdapter(this, data);
        adapter.setEditingMode(isEditingMode);

        adapter.setItemClickListener(new RecommendAdapter.ItemClickListener() {
            @Override
            public void itemClick(RecommendEntity recommendEntity, int position) {
                if (isEditingMode){
                    boolean isSelected = recommendEntity.isSelected();
                    recommendEntity.setIsSelected(!isSelected);
                    adapter.notifyItemChanged(position);
                    if (!isSelected) {
                        selectedRecommendCount += 1;
                    } else {
                        selectedRecommendCount -= 1;
                    }
                    changeSelectCount();
                }
            }
        });
        rvList.setAdapter(adapter);
    }

    //改变已选数量
    private void changeSelectCount() {
        LogUtil.d(TAG,"=changeSelectCount()===");
        if (selectedRecommendCount == 0){
            LogUtil.d(TAG,"======changeSelectCount==0");
            btnRemove.setTextColor(getResources().getColor(R.color.default_text_color_light));
            btnRemove.setText(R.string.text_delete);
            btnRemove.setEnabled(false);
            return;
        }
        btnRemove.setEnabled(true);
        btnRemove.setTextColor(getResources().getColor(R.color.btn_bg_color_red_normal));
        btnRemove.setText(getString(R.string.text_delete) + " (" + selectedRecommendCount + ")");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * add by wing
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        finish();
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_all_select:
                selectAllOrCancelAll();
                break;
            case R.id.btn_inverse_select:
                inverseSelect();
                break;
            case R.id.btn_delete:
                deleteRecommend();
                break;

        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ALL_SELECT:
                    adapter.notifyDataSetChanged();
                    btnAllSelect.setText(R.string.text_cancel_all);
                    selectedRecommendCount = data.size();
                    changeSelectCount();
                    break;
                case CANCEL_ALL:
                    adapter.notifyDataSetChanged();
                    btnAllSelect.setText(R.string.text_all_select);
                    selectedRecommendCount = 0;
                    changeSelectCount();
                    break;
                case INVERSE_SELECT:
                    adapter.notifyDataSetChanged();
                    selectedRecommendCount = data.size() - selectedRecommendCount;
                    changeSelectCount();
                    break;
                case DELETE_RECOMMEND:
                    selectedRecommendCount = 0;
                    changeSelectCount();
                    requestData();
                    break;

            }
        }
    };


    //全选或者取消全选
    private void selectAllOrCancelAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                if (!isAllSelected){
                    isAllSelected = true;
                    while (i < data.size()) {
                        data.get(i).setIsSelected(isAllSelected);
                        i++;
                    }
                    handler.sendEmptyMessage(ALL_SELECT);

                }else {
                    isAllSelected = false;
                    while (i < data.size()) {
                        data.get(i).setIsSelected(isAllSelected);
                        i++;
                    }
                    handler.sendEmptyMessage(CANCEL_ALL);
                }
            }
        }).start();

    }

    //RecommendEntity 全部 setIsSelect 为 false
    private void setAllUnselected() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                isAllSelected = false;
                while (i < data.size()) {
                    data.get(i).setIsSelected(isAllSelected);
                    i++;
                }
                handler.sendEmptyMessage(CANCEL_ALL);
            }
        }).start();
    }

    //反选
    private void inverseSelect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < data.size()){
                    data.get(i).setIsSelected(!data.get(i).isSelected());
                    i++;
                }
                handler.sendEmptyMessage(INVERSE_SELECT);
            }
        }).start();
    }

    //联网删除recommend
    private void deleteRecommend() {
        mProgressDialog.setVisibility(View.VISIBLE);
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        List<String> strList = setGetMembersIds(data);
        RequestInfo requestInfo = new RequestInfo();
        params.put("member_id",strList);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("member_id",strList);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        requestInfo.url = Constant.API_BONDALERT_REMOVE_RECOMMEND + "" + MainActivity.getUser().getUser_id();
        String fixJson = jsonObject.toString();
        fixJson = fixJson.substring(0,fixJson.lastIndexOf("\"")) + fixJson.substring(fixJson.lastIndexOf("\"")+1,fixJson.length());
        fixJson = fixJson.substring(0,fixJson.lastIndexOf("\"")) + fixJson.substring(fixJson.lastIndexOf("\"")+1,fixJson.length());
//        fixJson = fixJson.substring(0,fixJson.lastIndexOf("\"")-1) + fixJson.substring(fixJson.lastIndexOf("\""),fixJson.length());
        requestInfo.jsonParam = fixJson;
        //        requestInfo.jsonParam = "{\"member_id\":[1341, 1344, 6, 1033, 914]}";
        LogUtil.d("", "2sssss==============" + requestInfo.url);
        LogUtil.d("", "sssss==============" + fixJson);
        new HttpTools(this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                MessageUtil.showMessage(RecommendActivity.this, R.string.msg_action_successed);
                mProgressDialog.setVisibility(View.INVISIBLE);
                handler.sendEmptyMessage(DELETE_RECOMMEND);


            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG,"",e);
                MessageUtil.showMessage(RecommendActivity.this, R.string.msg_action_failed);

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

    }

    private List<String> setGetMembersIds(List<RecommendEntity> recommendEntityList) {
        List<String> ids = new ArrayList<>();
        if (recommendEntityList != null) {
            int count = recommendEntityList.size();
            for (int i = 0; i < count; i++) {
                if (recommendEntityList.get(i).isSelected()){
                    ids.add(recommendEntityList.get(i).getUser_id());
                }
            }
        }
        return ids;
    }


}
























