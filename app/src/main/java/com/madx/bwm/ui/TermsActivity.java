package com.madx.bwm.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

import com.madx.bwm.R;

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
        webView.loadUrl("http://bondwithme.com/bonding-terms.htm");


//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.loadUrl("http://docs.google.com/gview?embedded=true&url="+"http://sc.bondwith.me/bondwithme/download/94539275123.pdf");
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf");

//        webView.getSettings().setJavaScriptEnabled(true);
//        String pdf = "http://sc.bondwith.me/bondwithme/download/94539275123.pdf";
//        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdf);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
