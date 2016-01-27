package com.bondwithme.BondCorp.ui.wall;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.http.UrlUtil;
import com.bondwithme.BondCorp.ui.BaseActivity;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.UIUtil;

import java.util.HashMap;

public class WallEditCaptionActivity extends BaseActivity{

    private static final String TAG = WallEditCaptionActivity.class.getSimpleName();

    private EditText editText;
    private String strEditBefore;
    private String strPhotoId;

    private View vProgress;
    @Override
    public int getLayout() {
        return R.layout.activity_wall_edit_caption;
    }



    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(Color.BLACK);
        leftButton.setImageResource(R.drawable.back_press);
        rightButton.setImageResource(R.drawable.ok_press);
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

    }

    @Override
    protected void titleRightEvent() {
        //更新成功后执行以下代码

            updateCaption();

    }

    private void updateCaption() {


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("photo_caption", editText.getText().toString());
        final String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = String.format(Constant.API_WALL_UPDATE_CAPTION,strPhotoId);
        requestInfo.jsonParam = jsonParamsString;


        new HttpTools(this).put(requestInfo, TAG, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                //成功失败判断？？
                LogUtil.d(TAG, "-----" + string);
                Intent intent = new Intent();
                intent.putExtra("update_caption", editText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(Exception e) {
e.printStackTrace();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        strEditBefore = getIntent().getStringExtra("caption");
        strPhotoId = getIntent().getStringExtra("photo_id");

        LogUtil.d(TAG, "--------" + "caption" + strEditBefore + "####" + "photo_id" + strPhotoId);
        if (TextUtils.isEmpty(strPhotoId))
        {
            finish();
        }

        editText = getViewById(R.id.et_caption);
        vProgress = getViewById(R.id.rl_progress);

        editText.setText(strEditBefore);
        editText.requestFocus();
        UIUtil.showKeyboard(this, editText);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
