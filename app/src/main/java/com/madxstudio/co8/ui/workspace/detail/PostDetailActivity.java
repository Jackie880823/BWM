/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.ui.workspace.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.WorkspaceDetailAdapter;
import com.madxstudio.co8.entity.WorkspaceDetail;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.ui.BaseToolbarActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.widget.SendComment;

public class PostDetailActivity extends BaseToolbarActivity implements PostDetailContracts
        .ViewLayer {
    private static final String TAG = "PostDetailActivity";

    private SendComment sendComment;
    private RecyclerView recyclerView;
    private WorkspaceDetailAdapter detailAdapter;
    private PostDetailContracts.Presenter presenter;
    private WorkspaceEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 检测启动本Activity的Intent是否带有Key值
     *
     * @param key 从Intent中取出数据，需要的Key
     * @param <O> 数据的类型
     * @return 返回有的数据
     */
    private <O extends Object> O checkExtras(String key) {
        LogUtil.d(TAG, "checkExtras() called with: " + "key = [" + key + "]");
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            // intent中没有绑定数据，抛出空指针，提示必需要绑定数据才能启动本Activity
            throw new NullPointerException("Intent of start this Activity must have Extras");
        } else {
            O extra = (O) bundle.get(key);
            if (extra == null || (extra instanceof String && TextUtils.isEmpty((String) extra))) {
                // 为空说明没有这个key的数据，抛出参数错误异常，必需带有对应key值才能启动本Activity
                String msg = "Intent must have extra key is  kind of " + key;
                throw new IllegalArgumentException(msg);
            } else {
                return extra;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    public void initView() {
        super.initView();
        if (actionBar != null) {
            actionBar.setTitle("Headline Here");
        }

        sendComment = getViewById(R.id.send_comment);
        recyclerView = getViewById(R.id.recycler_view);
        sendComment.initViewPager(this, null);

        detailAdapter = new WorkspaceDetailAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(detailAdapter);

        entity = checkExtras(Constant.EXTRA_ENTITY);

        new PostDetailPresenter(this, entity);
    }

    @Override
    public void requestData() {
        LogUtil.d(TAG, "requestData: ");
        presenter.onStart();
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        String authorTag = entity.getIs_author();
        rightMenu.setVisible("1".equals(authorTag));
        rightMenu.setTitle(R.string.text_edit);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sendComment != null) {
            sendComment.commitAllowingStateLoss();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void bindWorkspaceEntity(WorkspaceDetail data) {
        LogUtil.d(TAG, "bindWorkspaceEntity: ");
        detailAdapter.setData(data);
    }

    @Override
    public void loadComplete(WorkspaceDetail data) {
        LogUtil.d(TAG, "loadComplete: ");
        detailAdapter.setData(data);
    }

    @Override
    public void setPresenter(@NonNull PostDetailContracts.Presenter presenter) {
        this.presenter = presenter;
    }
}
