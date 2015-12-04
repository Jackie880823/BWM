package com.bondwithme.BondWithMe.ui.add;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.AddMembersAdapter;
import com.bondwithme.BondWithMe.entity.RecommendEntity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AddMembersActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    private RecyclerView rv;
    private List<RecommendEntity> data = new ArrayList<>();
    private View vProgress;

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
    public void initView() {
        rv = getViewById(R.id.rv);
        vProgress = getViewById(R.id.rl_progress);
        rv.setLayoutManager(new LinearLayoutManager(this));

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
                rv.setAdapter(new AddMembersAdapter(AddMembersActivity.this, data));
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
}
