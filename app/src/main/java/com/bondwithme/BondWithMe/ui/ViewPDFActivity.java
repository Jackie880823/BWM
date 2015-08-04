package com.bondwithme.BondWithMe.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.SDKUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.co.senab.photoview.PhotoView;

public class ViewPDFActivity extends BaseActivity {

    WebView webView;
    public static final String PARAM_PDF_URL = "pdf_url";
    private String mFilePath;
    private int currentPage;
    private View vProgress;

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

        String url = getIntent().getStringExtra(PARAM_PDF_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        vProgress = getViewById(R.id.rl_progress);
        getPdfUrl();

    }

    /**
     * 一个SB的方法因为服务把pdf分开两个接口
     */
    private void getPdfUrl(){
        vProgress.setVisibility(View.VISIBLE);
//        mProgressDialog.show();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
        new HttpTools(ViewPDFActivity.this).get(String.format(Constant.API_FAMILY_TREE, MainActivity.getUser().getUser_id()), null, "load_pdf", new HttpCallback() {
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
                        String urlString = jsonObject.getString("filePath");
                        if (!TextUtils.isEmpty(urlString)) {
                            getPdf(urlString);
                        }
                    } else {
                        MessageUtil.showMessage(ViewPDFActivity.this, R.string.msg_action_failed);
                    }
                } catch (Exception e) {
                    MessageUtil.showMessage(ViewPDFActivity.this, R.string.msg_action_failed);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(ViewPDFActivity.this, R.string.msg_action_failed);
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
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

        if (SDKUtil.IS_L) {
            getViewById(R.id.pdflayout1).setVisibility(View.VISIBLE);
            getViewById(R.id.pdflayout2).setVisibility(View.GONE);
            mImageView = getViewById(R.id.image);
//            mButtonPrevious = getViewById(R.id.previous);
//            mButtonNext = getViewById(R.id.next);
            // Bind events.
//            mButtonPrevious.setOnClickListener(this);
//            mButtonNext.setOnClickListener(this);
            // Show the first page by default.
            int index = 0;
            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
            if (null != mSavedInstanceState) {
                index = mSavedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
            }

            try {
                openRenderer(this, path);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                this.finish();
            }

            showPage(index);

        } else {
            getViewById(R.id.pdflayout1).setVisibility(View.GONE);
            mainLayout = getViewById(R.id.pdflayout2);
            mainLayout.setVisibility(View.VISIBLE);
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
                vProgress.setVisibility(View.GONE);
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
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled() {
                vProgress.setVisibility(View.GONE);
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


    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;

    /**
     * {@link PdfRenderer} to render the PDF.
     */
    private PdfRenderer mPdfRenderer;

    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;

    /**
     * {@link ImageView} that shows a PDF page as a {@link Bitmap}
     */
    private PhotoView mImageView;

    /**
     * {@link Button} to move to the previous page.
     */
    private Button mButtonPrevious;

    /**
     * {@link Button} to move to the next page.
     */
    private Button mButtonNext;

    @Override
    protected void onDestroy() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (SDKUtil.IS_L) {
            if (null != mCurrentPage) {
                outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
            }
        }
    }

    /**
     * Sets up a {@link PdfRenderer} and related resources.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context, String path) throws IOException {

        if (SDKUtil.IS_L) {

            File file = new File(path);
            mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }

    }

    /**
     * Closes the {@link PdfRenderer} and related resources.
     *
     * @throws IOException When the PDF file cannot be closed.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (SDKUtil.IS_L) {
            if (null != mCurrentPage) {
                mCurrentPage.close();
            }
            mPdfRenderer.close();
            mFileDescriptor.close();
        }
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (SDKUtil.IS_L) {
            if (mPdfRenderer.getPageCount() <= index) {
                return;
            }
            // Make sure to close the current page before opening another one.
            if (null != mCurrentPage) {
                mCurrentPage.close();
            }
            // Use `openPage` to open a specific page in PDF.
            mCurrentPage = mPdfRenderer.openPage(index);


            int width = mCurrentPage.getWidth();
            int height =mCurrentPage.getHeight();

            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // We are ready to show the Bitmap to user.
            mImageView.setImageBitmap(bitmap);
//            updateUi();
        }
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        if (SDKUtil.IS_L) {
            int index = mCurrentPage.getIndex();
            int pageCount = mPdfRenderer.getPageCount();
            mButtonPrevious.setEnabled(0 != index);
            mButtonNext.setEnabled(index + 1 < pageCount);
//        setTitle(getString(R.string.app_name_with_index, index + 1, pageCount));
        }
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.previous: {
//                // Move to the previous page
//                showPage(mCurrentPage.getIndex() - 1);
//                break;
//            }
//            case R.id.next: {
//                // Move to the next page
//                showPage(mCurrentPage.getIndex() + 1);
//                break;
//            }
//        }
    }



}
