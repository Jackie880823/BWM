package com.madxstudio.co8.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.madxstudio.co8.R;

import java.util.Locale;

public class TermsActivity extends BaseActivity {

    WebView webView;

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
        tvTitle.setText(getString(R.string.titlte_terms_and_privacy));
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
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        webView.loadUrl("http://bondwithme.com/term.php?lang="+ Locale.getDefault().getLanguage());
//        webView.loadUrl("http://bondwithme.com/term.php?lang="+ HttpTools.getHeaders().get("X_BWM_APPLANG"));
//        webView.loadUrl("http://bondwithme.com/bonding-terms.htm");
        webView.getSettings().setJavaScriptEnabled(true);//修复url不能跳转bug,add by wing
        webView.setWebViewClient(new WebViewClient());

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
