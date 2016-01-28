package com.bondwithme.BondWithMe.ui.more.rewards;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.ui.BaseActivity;
import com.bondwithme.BondCorp.util.LogUtil;

import java.util.Locale;

/**
 * 获取Rewards Point详细规则；
 * Created by heweidong on 16/1/24.
 */
public class RewardInfoActivity extends BaseActivity {

    private final String TAG = "RewardInfoActivity";
    private WebView wv;
    @Override
    public int getLayout() {
        return R.layout.activity_reward_info;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.reward_info));
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press4);
        rightButton.setVisibility(View.INVISIBLE);
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
        wv = getViewById(R.id.wv_reward_info);
        LogUtil.d(TAG,"current_Language========="+Locale.getDefault().getLanguage());
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        LogUtil.d(TAG,"RewardINfo_url========"+"http://bondwithme.com/reward_info.php?lang="+ HttpTools.getHeaders().get("X_BWM_APPLANG"));
        //http://bondwithme.com/reward_info.php?lang=en
//        wv.loadUrl("http://bondwithme.com/reward_info.php?lang="+ Locale.getDefault().getLanguage());
        wv.loadUrl("http://bondwithme.com/reward_info.php?lang="+ HttpTools.getHeaders().get("X_BWM_APPLANG"));
//        webView.loadUrl("http://bondwithme.com/term.php?lang="+ HttpTools.getHeaders().get("X_BWM_APPLANG"));
        wv.getSettings().setJavaScriptEnabled(true);//修复url不能跳转bug,add by wing
        wv.setWebViewClient(new WebViewClient());

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
