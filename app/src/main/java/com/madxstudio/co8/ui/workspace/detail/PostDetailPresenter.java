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

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.entity.WorkspaceCommentEntity;
import com.madxstudio.co8.entity.WorkspaceDetail;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created 16/9/7.
 *
 * @author Jackie
 * @version 1.0
 */
public class PostDetailPresenter implements PostDetailContracts.Presenter {
    private static final String TAG = "PostDetailPresenter";
    private static final Object GET_COMMENT_TAG = TAG + "getComment";
    private WorkspaceDetail detail;
    private PostDetailContracts.ViewLayer viewLayer;
    private Parameter parameter;

    public PostDetailPresenter(PostDetailContracts.ViewLayer viewLayer, WorkspaceEntity entity) {
        this.viewLayer = viewLayer;
        this.parameter = new Parameter();

        viewLayer.setPresenter(this);

        parameter.user_id = entity.getUser_id();
        parameter.content_group_id = entity.getContent_group_id();
        parameter.group_id = entity.getGroup_id();
        parameter.start = String.valueOf(0);
        parameter.limit = String.valueOf(10);

        detail = new WorkspaceDetail();
        detail.setEntity(entity);
        viewLayer.bindWorkspaceEntity(detail);
    }

    @Override
    public void onStart() {
        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put(Constant.CONTENT_GROUP_ID, parameter.content_group_id);
        jsonParams.put(Constant.GROUP_ID, parameter.group_id);
        jsonParams.put(Constant.USER_ID, MainActivity.getUser().getUser_id());
        String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.CONDITION, jsonParamsString);
        params.put(Constant.START, parameter.start);
        params.put(Constant.LIMIT, parameter.limit);
        LogUtil.i(TAG, "onStart() called with: start " + parameter.start);

        String url = UrlUtil.generateUrl(Constant.API_WALL_COMMENT_LIST, params);
        new HttpTools(viewLayer.getContext()).get(url, params, GET_COMMENT_TAG, new HttpCallback() {
            @Override
            public void onStart() {
                LogUtil.d(TAG, "get comment list onStart: ");
            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "get comment list onFinish: ");
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "get comment list onResult() called with: " + "string = [" + string + "]");

                Gson gson = new GsonBuilder().create();
                ArrayList<WorkspaceCommentEntity> commentList = gson.fromJson(string, new
                        TypeToken<ArrayList<WorkspaceEntity>>() {
                        }.getType());
                detail.setCommentList(commentList);
                viewLayer.loadComplete(detail);
            }

            @Override
            public void onError(Exception e) {
                LogUtil.e(TAG, "get comment list onError: ", e);
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {
                LogUtil.d(TAG, "get comment list onCancelled: ");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "get comment list onLoading() called with: " + "count = [" + count + "], current = " +
                        "[" + current + "]");
            }
        });
    }

    protected static class Parameter {
        /**
         * condition:{
         * "content_group_id":1410,
         * "user_id":175,
         * "group_id":142}
         * user_id : 当前用户
         * start: "0"
         * limit: "10"
         */
        protected String content_group_id;
        protected String user_id;
        protected String group_id;

        protected String start;
        protected String limit;
    }
}
