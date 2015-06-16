package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.madx.bwm.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsInfoActivity extends BaseActivity {

    WebView webView;
    private String newUrl;
    private ProgressDialog mProgressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_terms;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press4);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getResources().getString(R.string.text_news));
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
        webView = getViewById(R.id.webView);
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.string.text_loading);
        }
        mProgressDialog.show();
        //begin
        newUrl = getIntent().getStringExtra("newUrl");
        //end
    }

    @Override
    public void requestData() {
        new HttpTools(this).get(String.format(newUrl, MainActivity.getUser().getUser_id()), null,this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String content = jsonObject.optString("content", "");
                    webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
                    webView.getSettings().setJavaScriptEnabled(true);//修复url不能跳转bug,add by wing
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
