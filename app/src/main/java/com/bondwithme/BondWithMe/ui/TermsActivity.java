package com.bondwithme.BondWithMe.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.R;

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
        webView.loadUrl("http://bondwithme.com/term.php?lang="+ HttpTools.getHeaders().get("X_BWM_APPLANG"));
//        webView.loadUrl("http://bondwithme.com/bonding-terms.htm");
        webView.getSettings().setJavaScriptEnabled(true);//修复url不能跳转bug,add by wing

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
