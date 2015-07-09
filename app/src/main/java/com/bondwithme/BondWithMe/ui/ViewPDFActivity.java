package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bondwithme.BondWithMe.R;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ViewPDFActivity extends BaseActivity {

    WebView webView;
    public static final String PARAM_PDF_URL = "pdf_url";

    @Override
    public int getLayout() {
        return R.layout.activity_family_tree;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.text_new_family_tree));
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
        String url = getIntent().getStringExtra(PARAM_PDF_URL);
        if(TextUtils.isEmpty(url)){
            finish();
            return;
        }
        showPDF(url);
    }

    private void showPDF(String url) {
        webView = getViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) //required for running javascript on android 4.1 or later
        {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        settings.setBuiltInZoomControls(true);
        webView.setWebChromeClient(new
                        WebChromeClient()
        );
        Uri path = Uri.parse(url);
//        Uri path = Uri.parse("http://sc.bondwith.me/bondwithme/download/7984627910071.pdf");

        FileOutputStream fileOutputStream = null;
        try {
            InputStream ims = getAssets().open("pdfviewer/index.html");
            String line = getStringFromInputStream(ims);
            if (line.contains("THE_FILE")) {
                line = line.replace("THE_FILE", path.toString());
                fileOutputStream = openFileOutput("index.html", Context.MODE_PRIVATE);
                fileOutputStream.write(line.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        webView.loadUrl("file://" + getFilesDir() + "/index.html");
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
