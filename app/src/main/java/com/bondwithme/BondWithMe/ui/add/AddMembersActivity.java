package com.bondwithme.BondWithMe.ui.add;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.db.table.TableUtils;
import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.AddMembersAdapter;
import com.bondwithme.BondWithMe.entity.RecommendEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.RelationshipActivity;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddMembersActivity extends BaseActivity{

    public String getTAG() {
        return TAG;
    }

    private final String TAG = getClass().getSimpleName();
    private final int ADD_MEMBER = 1;


    private RecyclerView rv;
    private List<RecommendEntity> data = new ArrayList<>();
    private View vProgress;
    private EditText etSearch;
    private ImageView ivSearch;

    private AddMembersAdapter adapter;

    public View getvProgress() {
        return vProgress;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_add_members;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_add_member);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setImageResource(R.drawable.qrcode_button_icon);
    }

    @Override
    public void initView() {
        rv = getViewById(R.id.rv);
        vProgress = getViewById(R.id.rl_progress);
        etSearch = getViewById(R.id.et_search);
        ivSearch = getViewById(R.id.iv_search);

        rv.setLayoutManager(new LinearLayoutManager(this));

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etSearch.getText().toString())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", MainActivity.getUser().getUser_id());
                    params.put("search_detail", etSearch.getText().toString());

                    new HttpTools(AddMembersActivity.this).get(Constant.API_SEARCH_BWM_USER, params, TAG, new HttpCallback() {
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
                                if (Constant.SUCCESS.equals(jsonObject.get("response_status"))) {
                                    Intent intent = new Intent(AddMembersActivity.this, RelationshipActivity.class);
                                    intent.putExtra("member_id", jsonObject.getString("member_id"));
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddMembersActivity.this);
                                    dialog.setMessage(R.string.text_search_bwm_user_no_found);
                                    dialog.setCancelable(false);
                                    dialog.setPositiveButton(R.string.text_dialog_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    if (!isFinishing())
                                    {
                                        dialog.show();
                                    }
                                }
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
            }
        });


    }

    @Override
    public void requestData() {
        new HttpTools(this).get(String.format(Constant.API_BONDALERT_RECOMMEND, MainActivity.getUser().getUser_id()), null, TAG, new HttpCallback() {
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
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                data = gson.fromJson(string, new TypeToken<ArrayList<RecommendEntity>>() {
                }.getType());

                adapter = new AddMembersAdapter(AddMembersActivity.this, data);
                rv.setAdapter(adapter);

                adapter.setOnAddIconClickListener(new AddMembersAdapter.OnAddIconClickListener() {
                    @Override
                    public void onAddIconClick(RecommendEntity recommendEntity) {
                        Intent intent = new Intent(AddMembersActivity.this, RelationshipActivity.class);
                        intent.putExtra("member_id", recommendEntity.getUser_id());
                        startActivityForResult(intent, ADD_MEMBER);
                    }
                });


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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void finish() {
        super.finish();
        HttpTools.getHttpRequestQueue().cancelAll(TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ADD_MEMBER:
                if (resultCode == RESULT_OK)
                {
                    requestData();
                }
                break;
        }
    }
}
