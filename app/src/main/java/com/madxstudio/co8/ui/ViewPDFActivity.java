package com.madxstudio.co8.ui;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.SDKUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.co.senab.photoview.PhotoView;

public class ViewPDFActivity extends BaseActivity {
    private static final String TAG = ViewPDFActivity.class.getSimpleName();
    private static final String FAMILY_TREE_FILE_PATH_PARENT = Environment.getExternalStorageDirectory() + "/Download";

    WebView webView;
    private String mFilePath;
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

        vProgress = getViewById(R.id.rl_progress);
        getPdfUrl();

    }

    /**
     * 一个SB的方法因为服务把pdf分开两个接口
     */
    private void getPdfUrl() {
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
                Message msg = mHandler.obtainMessage();
                msg.what = RECEIVE_PDF_INFO;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
                mHandler.sendEmptyMessage(REQUEST_ERROR);
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

        if (SDKUtil.IS_L) {

            //调用第三方实现
            Uri path1 = Uri.fromFile(new File(path));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path1, "application/pdf");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                MessageUtil.showMessage(this, R.string.msg_action_failed);
            }
            finish();
            //调用api实现
//            getViewById(R.id.pdflayout1).setVisibility(View.VISIBLE);
//            getViewById(R.id.pdflayout2).setVisibility(View.GONE);
//            mImageView = getViewById(R.id.image);
////            mButtonPrevious = getViewById(R.id.previous);
////            mButtonNext = getViewById(R.id.next);
//            // Bind events.
////            mButtonPrevious.setOnClickListener(this);
////            mButtonNext.setOnClickListener(this);
//            // Show the first page by default.
//            int index = 0;
//            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
//            if (null != mSavedInstanceState) {
//                index = mSavedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
//            }
//
//            try {
//                openRenderer(this, path);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                this.finish();
//            }
//
//            showPage(index);

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

    private static final int REQUEST_ERROR = 9;
    private static final int SHOW_PDF = 10;
    private static final int RECEIVE_PDF_INFO = 11;
    private int currentPage;
    PdfRenderer renderer = null;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PDF:
                    String target = (String) msg.obj;
                    vProgress.setVisibility(View.GONE);
                    File file = new File(target);
                    if (file.exists()) {
//                        if (SDKUtil.IS_L) {
//                            mImageView = getViewById(R.id.image);
//                            int REQ_WIDTH = mImageView.getWidth() * 2;
//                            int REQ_HEIGHT = mImageView.getHeight() * 2;
//                            Bitmap mBitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_4444);
//                            try {
//                                renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
//                                if (currentPage < 0) {
//                                    currentPage = 0;
//                                } else if (currentPage > renderer.getPageCount()) {
//                                    currentPage = renderer.getPageCount() - 1;
//                                }
//                                Matrix m = mImageView.getImageMatrix();
//                                Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);
//                                renderer.openPage(currentPage).render(mBitmap, rect, m, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                                mImageView.setImageMatrix(m);
//                                mImageView.setImageBitmap(mBitmap);
//                                mImageView.invalidate();
//
////                                // let us just render all pages
////                                final int pageCount = renderer.getPageCount();
////                                for (int i = 0; i < pageCount; i++) {
////                                    PdfRenderer.Page page = renderer.openPage(i);
////                                    // say we render for showing on the screen
////                                    page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
////                                    // do stuff with the bitmap
////
////                                    // close the page
////                                    page.close();
////                                }
////                                // close the renderer
////                                renderer.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
                        showPDF(target);
//                        }
                    }
                    break;
                case RECEIVE_PDF_INFO:
                    String response = (String) msg.obj;
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
                    break;
                case REQUEST_ERROR:
                    MessageUtil.showMessage(ViewPDFActivity.this, R.string.msg_action_failed);
                    vProgress.setVisibility(View.GONE);
                    break;

            }
            return false;
        }
    });

    public void getPdf(String urlString) {

//        final String target = FileUtil.getCacheFilePath(this) + String.format("/cache_%s.pdf", "" + System.currentTimeMillis());

        File treeDir = new File(FAMILY_TREE_FILE_PATH_PARENT);
        boolean exists = treeDir.exists() || treeDir.mkdir();
        LogUtil.i(TAG, "getTreeUrl& file path exists: " + exists);
        final String target;
        target = exists ? (FAMILY_TREE_FILE_PATH_PARENT + urlString.substring(urlString.lastIndexOf("/"))) : (Environment.getDataDirectory() + urlString.substring(urlString.lastIndexOf("/")));


        new HttpTools(this).download(App.getContextInstance(), urlString, target, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                Message msg = mHandler.obtainMessage();
                msg.what = SHOW_PDF;
                msg.obj = target;
                mHandler.sendMessage(msg);

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
                mHandler.sendEmptyMessage(REQUEST_ERROR);

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
        File file = new File(path);
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        mPdfRenderer = new PdfRenderer(mFileDescriptor);

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
            if (mPdfRenderer != null) {
                mPdfRenderer.close();
            }
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
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
            int height = mCurrentPage.getHeight();

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


}
