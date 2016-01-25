package com.bondwithme.BondWithMe.ui.more.rewards;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.util.LogUtil;

import java.util.Locale;

/**
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
        if("zh".equals(Locale.getDefault().getLanguage())){
            wv.loadUrl("http://bondwithme.com/reward/cn/reward_info.htm");
        }else {
            wv.loadUrl("http://bondwithme.com/reward/en/reward_info.htm");
        }

//        webView.loadUrl("http://bondwithme.com/term.php?lang="+ HttpTools.getHeaders().get("X_BWM_APPLANG"));
//        webView.loadUrl("http://bondwithme.com/bonding-terms.htm");
//        中文：
//        http://bondwithme.com/reward/cn/reward_info.htm
//
//        英文:
//        http://bondwithme.com/reward/en/reward_info.htm

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
