package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ViewPDFActivity extends BaseActivity {

    WebView webView;
    public static final String PARAM_PDF_URL = "pdf_url";
    private String mFilePath;

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

    private RelativeLayout mainLayout;
    private MuPDFCore core;
    private MuPDFReaderView mDocView;

    @Override
    public void initView() {
        mainLayout = getViewById(R.id.pdflayout);
        String url = getIntent().getStringExtra(PARAM_PDF_URL);
        if(TextUtils.isEmpty(url)){
            finish();
            return;
        }
        getPdf(url);

//        showPDF(url);
    }

    private void showPDF(String path) {
//    private void showPDF(String url) {
//        webView = getViewById(R.id.webView);
//        WebSettings settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) //required for running javascript on android 4.1 or later
//        {
//            settings.setAllowFileAccessFromFileURLs(true);
//            settings.setAllowUniversalAccessFromFileURLs(true);
//        }
//        settings.setBuiltInZoomControls(true);
//        webView.setWebChromeClient(new
//                        WebChromeClient()
//        );
//        Uri path = Uri.parse(url);
////        Uri path = Uri.parse("http://sc.bondwith.me/bondwithme/download/7984627910071.pdf");
////
//        FileOutputStream fileOutputStream = null;
//        try {
//            InputStream ims = getAssets().open("pdfviewer/index.html");
//            String line = getStringFromInputStream(ims);
//            if (line.contains("THE_FILE")) {
//                line = line.replace("THE_FILE", path.toString());
//                fileOutputStream = openFileOutput("index.html", Context.MODE_PRIVATE);
//                fileOutputStream.write(line.getBytes());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        webView.loadUrl("file://" + getFilesDir() + "/index.html");


        core = openFile(Uri.decode(path));

        if (core != null && core.countPages() == 0) {
            core = null;
        }
        if (core == null || core.countPages() == 0 || core.countPages() == -1) {
            Log.e("", "Document Not Opening");
        }
        if (core != null) {
            mDocView = new MuPDFReaderView(this) {
                @Override
                protected void onMoveToChild(int i) {
                    if (core == null)
                        return;
                    super.onMoveToChild(i);
                }

            };

            mDocView.setAdapter(new MuPDFPageAdapter(this, core));
            mainLayout.addView(mDocView);
        }
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

    public void getPdf(String urlString) {

        final String target = FileUtil.getCacheFilePath(this) + String.format("/cache_%s.pdf", "" + System.currentTimeMillis());

        new HttpTools(this).download(urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
//                vProgress.setVisibility(View.GONE);
                File file = new File(target);
                if (file.exists()) {
                    showPDF(target);
                }
            }

            @Override
            public void onResult(String string) {
//                File file = new File(target);
//                if (file.exists()) {
////                    mProgressDialog.dismiss();
//                    Uri path1 = Uri.fromFile(file);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(path1, "application/pdf");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    try {
//                        startActivity(intent);
//                    } catch (ActivityNotFoundException e) {
//                        MessageUtil.showMessage(ViewPDFActivity.this, R.string.msg_action_failed);
//                    }
//                }
            }

            @Override
            public void onError(Exception e) {
//                mProgressDialog.dismiss();
                MessageUtil.showMessage(ViewPDFActivity.this, R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private MuPDFCore openFile(String path) {
        int lastSlashPos = path.lastIndexOf('/');
        mFilePath = new String(lastSlashPos == -1
                ? path
                : path.substring(lastSlashPos + 1));
        try {
            core = new MuPDFCore(this, path);
            // New file: drop the old outline data
        } catch (Exception e) {
            return null;
        }
        return core;
    }
}
