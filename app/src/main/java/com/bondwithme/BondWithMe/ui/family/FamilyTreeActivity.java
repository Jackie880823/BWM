package com.bondwithme.BondWithMe.ui.family;

import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.NumberProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Jackie on 8/20/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class FamilyTreeActivity extends BaseActivity {
    private static final String TAG = FamilyTreeActivity.class.getSimpleName();
    private static final String FAMILY_TREE_FILE_PATH_PARENT = Environment.getExternalStorageDirectory() + "/Download";

    private static int count = 0;

    private DownloadRequest request;

    private NumberProgressBar progressBar;

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {

    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.text_new_family_tree));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();

        rightButton.setVisibility(View.VISIBLE);
        rightButton.setImageResource(R.drawable.pdf);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeUrl();
            }
        });
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return FamilyTreeFragment.newInstance();
    }

    @Override
    public void initView() {
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * 下载家谱
     */
    private void getTreeUrl() {

        String url = String.format(Constant.API_FAMILY_TREE, ((FamilyTreeFragment)getFragment()).getSelectUseId());
        new HttpTools(this).get(url, null, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "getTreeUrl&onResult$ response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if("Success".equals(jsonObject.getString("response_status"))) {
                        String urlString = jsonObject.getString("filePath");
                        if(!TextUtils.isEmpty(urlString)) {
                            downloadTree(urlString);
                        }
                    } else {
                        MessageUtil.showMessage(FamilyTreeActivity.this, R.string.msg_action_failed);
                    }
                } catch(JSONException e) {
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

    private void downloadTree(String url) {
        File treeDir = new File(FAMILY_TREE_FILE_PATH_PARENT);
        boolean exists = treeDir.exists() || treeDir.mkdir();
        LogUtil.i(TAG, "getTreeUrl& file path exists: " + exists);

        final String target;
        target = exists ? (FAMILY_TREE_FILE_PATH_PARENT + url.substring(url.lastIndexOf("/"))) : (Environment.getDataDirectory() + url.substring(url.lastIndexOf("/")));

        LogUtil.i(TAG, "getTreeUrl& target: " + target);
        LogUtil.i(TAG, "getTreeUrl& url: " + url);

        request = new HttpTools(this).download(url, target, true, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                MessageUtil.showMessage(FamilyTreeActivity.this, target);
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
