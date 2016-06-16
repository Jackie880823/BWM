package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.OrgSearchEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.interfaces.NoFoundDataListener;
import com.madxstudio.co8.util.NetworkUtil;
import com.madxstudio.co8.widget.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 16/3/8.
 */
public class OrgSearchActivity extends BaseActivity {
    private Context mContext;
    private View vProgress;
    private List<OrgSearchEntity> searchEntityList;
    private static final int GET_DATA = 0x11;
    private static final int JOIN_ORG_SUCCESS = 0x12;
    private DetailAdapter adapter;
    private ListView listView;
    private ImageButton userIb;
    private String Tag = OrgSearchActivity.class.getName();
    private EditText etSearch;
    private TextView searchTv;
    private boolean isGetData = false;
    private MyDialog myDialog;

    @Override
    protected void initBottomBar() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_org_search;
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.text_new_family));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    private void showSearchNoDataView(String string) {
        listView.setVisibility(View.GONE);
        searchTv.setVisibility(View.VISIBLE);
        searchTv.setText(String.format(mContext.getString(R.string.text_search_no_data), string));
    }

    private void hideSearchNoDataView() {
        listView.setVisibility(View.VISIBLE);
        searchTv.setVisibility(View.GONE);
    }

    @Override
    public void initView() {
        mContext = this;
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.GONE);
        listView = getViewById(R.id.org_list_view);
        userIb = getViewById(R.id.ib_top);
        etSearch = getViewById(R.id.et_search);
        searchTv = getViewById(R.id.message_search);
        adapter = new DetailAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        listView.setAdapter(adapter);

        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(0);
            }
        });

        adapter.showNoData(new NoFoundDataListener() {
            @Override
            public void showFoundData(String string) {
                showSearchNoDataView(string);
            }

            @Override
            public void showRefreshLayout() {
                hideSearchNoDataView();
            }
        });

        getViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etImport = etSearch.getText().toString();
                getData(etImport);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                final String name = (String) adapter.getItem(arg2);
                boolean isFromOrg = getIntent().getBooleanExtra(Constant.IS_FROM_ORG, false);
                if (isFromOrg) {
                    if (myDialog == null) {
                        Spanned content = Html.fromHtml(String.format(getString(R.string.text_org_sure_join), name));
                        myDialog = new MyDialog(mContext, R.string.text_tips_title, content);
                        myDialog.setCanceledOnTouchOutside(false);
                        myDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (myDialog != null) {
                                    myDialog.dismiss();
                                }
                            }
                        });
                        myDialog.setButtonAccept(R.string.text_dialog_accept, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                                if (searchEntityList != null) {
                                    for (OrgSearchEntity searchEntity : searchEntityList) {
                                        if (name.equals(searchEntity.getName())) {
                                            joinOrg(searchEntity.getId());
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                    if (!myDialog.isShowing()) {
                        myDialog.show();
                    }

                } else {
                    Intent intent = new Intent();
                    if (searchEntityList != null) {
                        for (OrgSearchEntity searchEntity : searchEntityList) {
                            if (name.equals(searchEntity.getName())) {
                                intent.putExtra(Constant.CREATE_COUNTRY_NAME, searchEntity);
                                break;
                            }
                        }
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            String beforeText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!TextUtils.isEmpty(s)) {
                    beforeText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(beforeText) && s.toString().toLowerCase().indexOf(beforeText.toLowerCase()) != -1) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
                if (s.length() < 3 && adapter.getCount() == 0) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    hideSearchNoDataView();
                }
                if (s.length() >= 3 && !isGetData) {
                    getData(s.toString());
                }
            }
        });
    }

    private void joinOrg(String orgId) {
        Map<String, String> params = new HashMap<>();
        UserEntity userEntity = MainActivity.getUser();
        params.put("user_id", userEntity.getUser_id());
        params.put("user_given_name", userEntity.getUser_given_name());
        params.put("org_id", orgId);
        new HttpTools(this).post(Constant.API_ORG_JOIN, params, Tag, new HttpCallback() {
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
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    UserEntity userEntity = new GsonBuilder().create().fromJson(jsonObject.optString(Constant.LOGIN_USER), UserEntity.class);
                    if (userEntity != null) {
                        App.changeLoginedUser(userEntity);
                    }
                    handler.sendEmptyMessage(JOIN_ORG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public class DetailAdapter<T> extends ArrayAdapter<String> implements Filterable {
        public DetailAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        private Filter filter = new Filter() {
            List<String> mList = new ArrayList<>();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || searchEntityList == null) {
                    filterResults.values = new ArrayList<>();
                    filterResults.count = 0;
                    return filterResults;
                }
                List<OrgSearchEntity> list = new ArrayList<>();
                list.addAll(searchEntityList);
                mList.clear();
                searchEntityList.clear();
                for (OrgSearchEntity orgSearchEntity : list) {
                    String name = orgSearchEntity.getName();
                    if (-1 != name.toLowerCase().toLowerCase().indexOf(constraint.toString().trim().toLowerCase())) {
                        mList.add(name);
                        searchEntityList.add(orgSearchEntity);
                    }
                }

                filterResults.values = mList;
                filterResults.count = mList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapter.clear();
                adapter.addAll(mList);
                notifyDataSetChanged();
                if (results.count == 0) {
                    if (showData != null && !TextUtils.isEmpty(constraint))
                        showData.showFoundData(constraint.toString());
                } else {
                    if (showData != null)
                        showData.showRefreshLayout();
                }
            }
        };
        private NoFoundDataListener showData;

        public void showNoData(NoFoundDataListener showData) {
            this.showData = showData;
        }
    }

    @Override
    public void requestData() {
    }

    private void getData(final String orgName) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("name", orgName);
        new HttpTools(this).get(Constant.API_ORG_SEARCH, params, Tag, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
                isGetData = true;
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
                isGetData = false;
            }

            @Override
            public void onResult(String response) {
                if (TextUtils.isEmpty(response) || "[]".equals(response)) {
                    showSearchNoDataView(orgName);
                } else {
                    hideSearchNoDataView();
                    List<OrgSearchEntity> list = new GsonBuilder().create().fromJson(response, new TypeToken<ArrayList<OrgSearchEntity>>() {
                    }.getType());
                    Message.obtain(handler, GET_DATA, list).sendToTarget();
                }
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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA:
                    if (searchEntityList != null) {
                        searchEntityList.clear();
                    }
                    searchEntityList = (List<OrgSearchEntity>) msg.obj;
                    if (searchEntityList != null && searchEntityList.size() > 0) {
                        adapter.clear();
                        for (OrgSearchEntity orgEntity : searchEntityList) {
                            adapter.add(orgEntity.getName());
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case JOIN_ORG_SUCCESS:
                    setResult(RESULT_OK, new Intent());
                    finish();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
